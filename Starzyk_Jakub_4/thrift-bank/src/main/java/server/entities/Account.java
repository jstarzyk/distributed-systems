package server.entities;

import account.AccountType;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;

public class Account {

    public final String id;
    String firstName;
    String lastName;
    AccountType category;
    CurrencyUnit currency;
    Money monthlyLimit;
    public Money balance;

    public Account(String id, String firstName, String lastName, AccountType category, CurrencyUnit currencyUnit, Money monthlyLimit) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.category = category;
        this.currency = currencyUnit;
        this.monthlyLimit = monthlyLimit;
    }

}
