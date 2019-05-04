package server;

import account.Account;
import account.AccountService;
import account.InvalidArguments;
import errors.ArgumentError;
import org.apache.thrift.TException;

public class AccountHandler implements AccountService.Iface {

    @Override
    public Account account(String firstName, String lastName, String id, String limit) throws ArgumentError, TException {
        return null;
    }
}
