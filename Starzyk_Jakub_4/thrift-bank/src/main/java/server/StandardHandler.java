package server;

import auth.AuthToken;
import auth.Unauthenticated;
//import bank.StandardService;
import money.Currency;
import money.Money;
import org.apache.thrift.TException;
import standard.StandardService;

public class StandardHandler implements StandardService.Iface {
    @Override
    public Money balance(AuthToken authToken) throws Unauthenticated, TException {
        return new Money(Currency.EUR, 10.2);
    }
}
