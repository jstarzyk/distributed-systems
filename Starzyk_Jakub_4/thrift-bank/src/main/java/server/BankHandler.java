package server;

import bank.AuthToken;
import bank.BankService;
import bank.Unauthenticated;
import money.Money;
import org.apache.thrift.TException;

public class BankHandler implements BankService.Iface {
    @Override
    public Money balance(AuthToken authToken) throws Unauthenticated, TException {
        return null;
    }
}
