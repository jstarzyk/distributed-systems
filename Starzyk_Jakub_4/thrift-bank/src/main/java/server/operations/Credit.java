package server.operations;

import org.javamoney.moneta.Money;
import server.entities.Account;

import java.util.Date;

public class Credit extends AuthorizedOperation {

    private Account account;
    private Money value;
    private Date dueDate;

    public Credit(Account account, Money value, Date dueDate) {
        super();
        this.account = account;
        this.value = value;
        this.dueDate = dueDate;
    }

    @Override
    public void execute() {
        // TODO
        super.execute();
    }

}
