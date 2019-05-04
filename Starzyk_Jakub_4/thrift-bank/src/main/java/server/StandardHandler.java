package server;

import auth.AuthToken;
import auth.Unauthenticated;
import bank.StandardService;
import money.Money;
import org.apache.thrift.TException;

public class StandardHandler implements StandardService.Iface {
    @Override
    public Money balance(AuthToken authToken) throws Unauthenticated, TException {
        return null;
    }
}
