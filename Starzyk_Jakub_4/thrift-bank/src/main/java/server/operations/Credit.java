package server.operations;

import org.javamoney.moneta.Money;
import server.entities.Account;
import server.entities.Bank;

import java.util.Date;

public class Credit extends Operation {

    private Account account;
    private Money baseValue;
    private Money totalValue;
    private Date dueDate;

    public Credit(Account account, Money baseValue, Money totalValue, Date dueDate) {
        super();
        this.account = account;
        this.baseValue = Bank.convert(baseValue, account.getCurrency());
        this.totalValue = Bank.convert(totalValue, account.getCurrency());
        this.dueDate = dueDate;
        account.getSubmitted().add(this);
    }

    @Override
    public void execute() {
        super.execute();
        account.setBalance(account.getBalance().add(baseValue));
        account.setDebt(account.getDebt().add(totalValue));
    }

}
