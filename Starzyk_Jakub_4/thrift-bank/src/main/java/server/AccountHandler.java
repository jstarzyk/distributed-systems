package server;

import account.Account;
import account.AccountService;
import account.AccountType;
//import bank.AccountService;
import errors.ArgumentError;
import org.apache.thrift.TException;

public class AccountHandler implements AccountService.Iface {

//    private Map<String, >

    @Override
    public Account account(String firstName, String lastName, String id, String limit) throws ArgumentError, TException {
        return new Account(AccountType.STANDARD, "pass1");
    }
}
