package server.entities;

import account.AccountType;
import org.javamoney.moneta.Money;
import server.operations.Operation;

import javax.money.CurrencyUnit;
import java.util.LinkedList;
import java.util.List;

public class Account {

    private final String id;

    private String firstName;
    private String lastName;

    private AccountType category;

    private CurrencyUnit currency;

    private Money monthlyLimit;
    private Money balance;
    private Money debt;

    private List<Operation> submitted;
    private List<Operation> executed;

    public Account(String id, String firstName, String lastName, AccountType category, CurrencyUnit currencyUnit, Money monthlyLimit) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.category = category;
        this.currency = currencyUnit;
        this.monthlyLimit = monthlyLimit;
        this.balance = Money.zero(this.currency);
        this.debt = Money.zero(this.currency);
        this.submitted = new LinkedList<>();
        this.executed = new LinkedList<>();
    }

    public List<Operation> getExecuted() {
        return executed;
    }

    public String getId() {
        return id;
    }

    public Money getDebt() {
        return debt;
    }

    public void setDebt(Money debt) {
        this.debt = debt;
    }

    public CurrencyUnit getCurrency() {
        return currency;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public AccountType getCategory() {
        return category;
    }

    public void setCategory(AccountType category) {
        this.category = category;
    }

    public Money getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(Money monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public Money getBalance() {
        return balance;
    }

    public List<Operation> getSubmitted() {
        return submitted;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }

}
