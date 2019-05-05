package client;

import account.AccountInfo;
import account.AccountService;
import auth.AuthToken;
import auth.Unauthenticated;
import auth.Unauthorized;
import enums.ServiceMethod;
import errors.ArgumentError;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import premium.CreditInfo;
import premium.PremiumService;
import standard.StandardService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static server.ThriftServer.*;

public class ThriftClient {

    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static String operations = String.join(", ",
            Stream.of(ServiceMethod.values())
                    .map(Enum::toString)
                    .collect(Collectors.toSet()));

    private static ServiceMethod parseServiceMethod(String line) {
        ServiceMethod account = ServiceMethod.ACCOUNT;
        ServiceMethod balance = ServiceMethod.BALANCE;
        ServiceMethod credit = ServiceMethod.CREDIT;
        var stream = Stream.of(account, balance, credit);
        return stream.filter(
                s -> s.toString().substring(0, 1).equals(line.substring(0, 1).toUpperCase())
                || s.toString().equals(line.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

//    private static boolean isAccount(String line) {
//        return line.equals("a") || line.equals("account");
//    }
//
//    private static boolean isBalance(String line) {
//        return line.equals("b") || line.equals("balance");
//    }
//
//    private static boolean isCredit(String line) {
//        return line.equals("c") || line.equals("credit");
//    }

    private static void printAvailableOperations() {
        System.out.println("Available operations: " + operations);
        System.out.println();
    }

    private static String readArgument(String parameterName) throws IOException {
        System.out.print("Enter " + parameterName + ": ");
        return br.readLine();
    }

    private static void received(Object o) {
        System.out.println("Received:");
        System.out.println(o.toString());
    }

    private static void nextOperation() {
        System.out.println();
    }

    private static void printErrorMessage(String errorMessage) {
        System.out.println(errorMessage);
    }

    private static boolean tryAgain() throws IOException {
        System.out.println("Try again? [Y/n]: ");
        String line = br.readLine().toLowerCase();
        return line.equals("y") || line.equals("");
    }

    private static AuthToken authToken() throws IOException {
        String id = readArgument("PESEL");
        String password = readArgument("password");
        String passwordHash = DigestUtils.sha3_256Hex(password);
        return new AuthToken(id, passwordHash);
    }

    public static void main(String [] args) throws IOException {
        try {
            accountTransport.open();
            bankTransport.open();

            perform();

            accountTransport.close();
            bankTransport.close();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    private static TTransport accountTransport = new TSocket(LOCALHOST, ACCOUNT_PORT);
    private static TTransport bankTransport = new TSocket(LOCALHOST, BANK_PORT);

    private static void perform() throws TException, IOException {
        var bankProtocol = new TBinaryProtocol(bankTransport);

        var accountClient = new AccountService.Client(new TBinaryProtocol(accountTransport));
        var standardClient = new StandardService.Client(new TMultiplexedProtocol(bankProtocol, STANDARD_NAME));
        var premiumClient = new PremiumService.Client(new TMultiplexedProtocol(bankProtocol, PREMIUM_NAME));

        String line = "";
        boolean repeatOperation = false;

        printAvailableOperations();

        while (true) {
            if (!repeatOperation) {
                while (true) {
                    line = br.readLine();
                    if (line.isEmpty()) {
                        printAvailableOperations();
                    } else {
                        break;
                    }
                }
            } else {
                repeatOperation = false;
            }

            try {
                ServiceMethod serviceMethod = parseServiceMethod(line);

                if (serviceMethod == null) {
                    printAvailableOperations();
                } else if (serviceMethod == ServiceMethod.ACCOUNT) {
                    String firstName = readArgument("first name");
                    String lastName = readArgument("last name");
                    String id = readArgument("PESEL");
                    String monthlyLimit = readArgument("monthly income limit");
                    String currencyCode = readArgument("currency code");

                    AccountInfo accountInfo = accountClient.account(firstName, lastName, id, monthlyLimit, currencyCode);
                    received(accountInfo);
                } else if (serviceMethod == ServiceMethod.BALANCE) {
                    AuthToken authToken = authToken();

                    String balance = standardClient.balance(authToken);
                    received(balance);
                } else if (serviceMethod == ServiceMethod.CREDIT) {
                    String amount = readArgument("amount");
                    String currencyCode = readArgument("currency code");
                    String dueDate = readArgument("due date");

                    AuthToken authToken = authToken();

                    CreditInfo creditInfo = premiumClient.credit(amount, currencyCode, dueDate, authToken);
                    received(creditInfo);
                }
            } catch (ArgumentError argumentError) {
                printErrorMessage(argumentError.message);
                repeatOperation = tryAgain();
            } catch (Unauthenticated unauthenticated) {
                printErrorMessage(unauthenticated.message);
                repeatOperation = tryAgain();
            } catch (Unauthorized unauthorized) {
                printErrorMessage(unauthorized.message);
            } finally {
                nextOperation();
            }
        }
    }

}
