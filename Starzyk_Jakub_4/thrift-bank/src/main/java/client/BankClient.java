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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static server.BankServer.*;

public class BankClient {

    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static String availableOperations = "[" + Stream.of(
            ServiceMethod.ACCOUNT, ServiceMethod.BALANCE, ServiceMethod.CREDIT)
            .map(Enum::toString)
            .collect(Collectors.joining(", ")) + "]";

    private static ServiceMethod parseServiceMethod(String line) {
        ServiceMethod account = ServiceMethod.ACCOUNT;
        ServiceMethod balance = ServiceMethod.BALANCE;
        ServiceMethod credit = ServiceMethod.CREDIT;

        var stream = Stream.of(account, balance, credit);
        return stream.filter(
                s -> s.toString().substring(0, 1).equals(line.toUpperCase())
                || s.toString().equals(line.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

    private static void printAvailableOperations() {
        System.out.println("Available operations: " + availableOperations);
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
        System.out.print("Try again? [Y/n]: ");
        String line = br.readLine().toLowerCase();
        return line.equals("y") || line.equals("");
    }

    private static boolean confirm() throws IOException {
        System.out.print("Confirm? [Y/n]: ");
        String line = br.readLine().toLowerCase();
        return line.equals("y") || line.equals("");
    }

    private static AuthToken authToken() throws IOException {
        String id = readArgument("PESEL");
        String password = readArgument("password");
        String passwordHash = DigestUtils.sha3_256Hex(password);
        return new AuthToken(id, passwordHash);
    }

//    private static class AccountArguments {
//        String firstName;
//        String lastName;
//        String id;
//        String monthlyLimit;
//        String currencyCode;
//    }
//
//    private static class BalanceArguments {
//    }
//
//    private static class CreditArguments {
//        String amount;
//        String currencyCode;
//        String dueDate;
//    }


    public static void main(String [] args) throws IOException {
        try {
            if (args.length < 1) {
                return;
            }

            final int offset = Integer.valueOf(args[0]);
            accountPort += offset;
            bankPort += offset;

            accountTransport = new TSocket(LOCALHOST, accountPort);
            bankTransport = new TSocket(LOCALHOST, bankPort);


            accountTransport.open();
            bankTransport.open();

            perform();

            accountTransport.close();
            bankTransport.close();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    private static int accountPort = ACCOUNT_PORT;
    private static int bankPort = BANK_PORT;

//    private static TTransport accountTransport = new TSocket(LOCALHOST, accountPort);
    private static TTransport accountTransport;
//    private static TTransport bankTransport = new TSocket(LOCALHOST, bankPort);
    private static TTransport bankTransport;

    private static void perform() throws TException, IOException {
        var bankProtocol = new TBinaryProtocol(bankTransport);

        var accountClient = new AccountService.Client(new TBinaryProtocol(accountTransport));
        var standardClient = new StandardService.Client(new TMultiplexedProtocol(bankProtocol, STANDARD_NAME));
        var premiumClient = new PremiumService.Client(new TMultiplexedProtocol(bankProtocol, PREMIUM_NAME));

        String line = "";
        boolean repeatOperation = false;

        printAvailableOperations();

//        AccountArguments accountArguments = new AccountArguments();
//        BalanceArguments balanceArguments = new BalanceArguments();
//        CreditArguments creditArguments = new CreditArguments();
//
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

//                    accountArguments = new AccountArguments();
                } else if (serviceMethod == ServiceMethod.BALANCE) {
                    AuthToken authToken = authToken();

                    String balance = standardClient.balance(authToken);
                    received(balance);

//                    balanceArguments = new BalanceArguments();
                } else if (serviceMethod == ServiceMethod.CREDIT) {
                    String amount = readArgument("amount");
                    String currencyCode = readArgument("currency code");
                    String dueDate = readArgument("due date");

                    AuthToken authToken = authToken();

                    CreditInfo creditInfo = premiumClient.credit(amount, currencyCode, dueDate, authToken);
                    received(creditInfo);

                    if (confirm()) {
                        String result = standardClient.confirm(authToken);
                        received(result);
                    }

//                    creditArguments = new CreditArguments();
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
