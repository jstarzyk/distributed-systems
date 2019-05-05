package client;

import account.Account;
import account.AccountService;
import auth.AuthToken;
import auth.Unauthenticated;
import auth.Unauthorized;
import errors.ArgumentError;
import money.Money;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import premium.CreditSummary;
import premium.PremiumService;
import standard.StandardService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;

import static server.ThriftServer.*;

public class ThriftClient {

    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static Set<String> operations = new LinkedHashSet<>();

    private static boolean isAccount(String line) {
        return line.equals("a") || line.equals("account");
    }

    private static boolean isBalance(String line) {
        return line.equals("b") || line.equals("balance");
    }

    private static boolean isCredit(String line) {
        return line.equals("c") || line.equals("credit");
    }

    private static void printAvailableOperations() {
        System.out.println("Available operations: " + String.join(", ", operations));
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

        operations.add("ACCOUNT");
        operations.add("BALANCE");
        operations.add("CREDIT");
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
                if (isAccount(line)) {
                    String firstName = readArgument("first name");
                    String lastName = readArgument("last name");
                    String id = readArgument("PESEL");
                    String limit = readArgument("income limit");
                    Account account = accountClient.account(firstName, lastName, id, limit);
                    received(account);
//                } catch (InvalidFirstName eifn) {
//                    printErrorMessage(eifn.message);
//                } catch (InvalidLastName eiln) {
//                    printErrorMessage(eiln.message);
//                } catch (InvalidID eid) {
//                    printErrorMessage(eid.message);
//                } catch (InvalidAmount eia) {
//                    printErrorMessage(eia.message);
//                } catch (ArgumentError argumentError) {
//                    printErrorMessage(argumentError.message);
//                    repeatOperation = tryAgain();
//                } finally {
//                    nextOperation();
//                }
                } else if (isBalance(line)) {
//                try {
                    AuthToken authToken = authToken();
                    Money balance = standardClient.balance(authToken);
                    received(balance);
//                } catch (Unauthenticated unauthenticated) {
//                    printErrorMessage(unauthenticated.message);
//                    repeatOperation = tryAgain();
//                } finally {
//                    nextOperation();
//                }
                } else if (isCredit(line)) {
//                try {
                    String currency = readArgument("currency");
                    String amount = readArgument("amount");
                    String dueDate = readArgument("due date");
                    AuthToken authToken = authToken();
                    CreditSummary creditSummary = premiumClient.credit(currency, amount, dueDate, authToken);
                    received(creditSummary);
//                } catch (Unauthenticated unauthenticated) {
//                    printErrorMessage(unauthenticated.message);
//                     try again authentication
//                } catch (Unauthorized unauthorized) {
//                    printErrorMessage(unauthorized.message);
//                } catch (InvalidCurrency eic) {
//                    printErrorMessage(eic.message);
//                } catch (InvalidAmount eia) {
//                    printErrorMessage(eia.message);
//                } catch (InvalidDueDate eidd) {
//                    printErrorMessage(eidd.message);
//                } catch (ArgumentError argumentError) {
//                    printErrorMessage(argumentError.message);
//                } finally {
//                    nextOperation();
//                }
                } else {
                    printAvailableOperations();
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
