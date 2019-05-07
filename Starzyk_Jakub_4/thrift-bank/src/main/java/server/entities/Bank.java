package server.entities;

import account.AccountType;
import org.javamoney.moneta.Money;
import server.operations.Operation;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.NumberValue;
import java.math.BigDecimal;
import java.util.*;

public abstract class Bank {

    public static final CurrencyUnit BANK_CURRENCY = Monetary.getCurrency("PLN");

    private static List<Account> accounts = new ArrayList<>();
    private static List<Operation> executed = new LinkedList<>();
    private static Map<CurrencyUnit, BigDecimal> currencyRates = new HashMap<>();

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

    public static Money convert(Money fromMoney, CurrencyUnit toUnit) {
        NumberValue nv;

        if (fromMoney.getCurrency().equals(BANK_CURRENCY)) {
            if (currencyRates.containsKey(toUnit)) {
                nv = fromMoney.divide(currencyRates.get(toUnit))
                        .getNumber();
            } else {
                return null;
            }
        } else if (currencyRates.containsKey(fromMoney.getCurrency())) {
            if (currencyRates.containsKey(toUnit)) {
                nv = fromMoney.multiply(currencyRates.get(fromMoney.getCurrency()))
                        .divide(currencyRates.get(toUnit))
                        .getNumber();
            } else if (BANK_CURRENCY.equals(toUnit)) {
                nv = fromMoney.multiply(currencyRates.get(fromMoney.getCurrency()))
                        .getNumber();
            } else {
                return null;
            }
        } else {
            return null;
        }

        return Money.of(nv, toUnit);
//        return money.with(MonetaryConversions.getConversion(unit));
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

    public static void addCurrencyRate(CurrencyUnit code, BigDecimal rate) {
        currencyRates.put(code, rate);
    }

}
