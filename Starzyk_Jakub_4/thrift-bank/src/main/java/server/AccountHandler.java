package server;

import account.AccountService;
import account.InvalidArguments;
import org.apache.thrift.TException;

public class AccountHandler implements AccountService.Iface {


    @Override
    public String createAccount(String firstName, String lastName, String id, int limit) throws InvalidArguments, TException {
        System.out.println("account created");
        return firstName.substring(0, 1) + lastName.substring(0, 1);
    }
}
