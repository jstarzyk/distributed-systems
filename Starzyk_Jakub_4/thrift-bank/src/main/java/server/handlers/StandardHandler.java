package server.handlers;

import auth.AuthToken;
import auth.Unauthenticated;
import org.apache.thrift.TException;
import server.entities.Bank;
import server.entities.SecurityManager;
import standard.StandardService;

public class StandardHandler implements StandardService.Iface {

    @Override
    public String balance(AuthToken token) throws TException {
        if (SecurityManager.authenticate(token.id, token.passwordHash)) {
            return Bank.viewAccountBalance(token.id).toString();
        } else {
            throw new Unauthenticated(SecurityManager.UNAUTHENTICATED);
        }
//        return new Money(Currency.EUR, new Amount(10, 2));
    }
}
