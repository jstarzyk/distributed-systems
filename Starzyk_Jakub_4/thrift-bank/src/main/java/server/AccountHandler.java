package server;

import account.Account;
import bank.AccountService;
import errors.ArgumentError;
import org.apache.thrift.TException;

public class AccountHandler implements AccountService.Iface {

//    private Map<String, >

    @Override
    public Account account(String firstName, String lastName, String id, String limit) throws ArgumentError, TException {
        return null;
    }
}
