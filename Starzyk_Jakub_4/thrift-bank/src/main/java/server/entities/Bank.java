package server.entities;

import account.AccountType;
import enums.Currency;
import org.javamoney.moneta.Money;
import server.operations.Operation;

import javax.money.CurrencyUnit;
import javax.money.convert.MonetaryConversions;
import java.util.*;

public abstract class Bank {

    private static List<Account> accounts = new ArrayList<>();
    private static List<Operation> executed = new LinkedList<>();

    public static Account findAccount(String id) {
        return accounts.stream().filter(a -> a.getId().equals(id)).findAny().orElse(null);
    }

    public static boolean accountExists(String id) {
        return findAccount(id) != null;
    }

    public static String openAccount(Account account) {
        String password = SecurityManager.createPassword();
        String passwordHash = SecurityManager.hashPassword(password);
        SecurityManager.addCredentials(account.getId(), passwordHash);
        accounts.add(account);
        return password;
    }

    public static Money viewAccountBalance(String id) {
        return findAccount(id).getBalance();
    }

    public static Money calculateTotalCreditValue(String id, Money value, Date dueDate) {
        return value.multiply(1.03);
    }

    public static AccountType determineCategory(Money limit) {
        return AccountType.PREMIUM;
    }

    public static Money convert(Money money, CurrencyUnit unit) {
        return money.with(MonetaryConversions.getConversion(unit));
    }

    public static Operation executeLastOperation(String id) {
        Account account = findAccount(id);
        Operation operation = account.getSubmitted().get(0);
        if (operation == null) {
            return null;
        }
        operation.execute();
        account.getExecuted().add(operation);
        executed.add(operation);
        return operation;
    }

}
