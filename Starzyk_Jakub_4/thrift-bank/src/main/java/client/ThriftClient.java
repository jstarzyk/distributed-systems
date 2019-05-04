package client;

import account.*;
import bank.*;
import errors.ArgumentError;
import money.Money;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import premium.CreditSummary;
import premium.PremiumService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThriftClient {

    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//    private static MessageDigest digest = MessageDigest.getInstance(SHA_2);

    private static boolean isAccount(String line) {
        return line.equals("a") || line.equals("account");
    }

    private static boolean isBalance(String line) {
        return line.equals("b") || line.equals("balance");
    }

    private static boolean isCredit(String line) {
        return line.equals("c") || line.equals("credit");
    }

//    private static String readLowerCase() throws IOException {
//        return br.readLine().toLowerCase();
//    }

    private static String readArgument(String parameterName) throws IOException {
        System.out.println("Enter " + parameterName + ": ");
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
            TTransport transport;

            transport = new TSocket("localhost", 9090);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            perform(protocol);

            transport.close();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    private static void perform(TProtocol protocol) throws TException, IOException {
//    private static void perform(AccountService.Client accountClient) throws TException, IOException {
//    private static void perform() throws TException, IOException {
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        var accountClient = new AccountService.Client(protocol);
//        var standardClient = new StandardService.Client(protocol);
        var premiumClient = new PremiumService.Client(protocol);
        var bankClient = new BankService.Client(protocol);
//        BankService.Client bankClient = null;
        String line = null;
        boolean repeatOperation = false;

        while (true) {
            if (!repeatOperation) {
                line = br.readLine().toLowerCase();
            } else {
                repeatOperation = false;
            }

            try {
                if (isAccount(line)) {
                    String firstName = readArgument("first name");
                    String lastName = readArgument("last name");
                    String id = readArgument("PESEL");
                    String limit = readArgument("income limit");
//                try {
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
                    Money balance = bankClient.balance(authToken);
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
