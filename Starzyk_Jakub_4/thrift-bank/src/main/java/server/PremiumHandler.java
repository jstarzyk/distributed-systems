package server;

import auth.AuthToken;
import auth.Unauthenticated;
import auth.Unauthorized;
import bank.PremiumService;
import credit.CreditSummary;
import errors.ArgumentError;
import org.apache.thrift.TException;

public class PremiumHandler extends StandardHandler implements PremiumService.Iface {
    @Override
    public CreditSummary credit(String currency, String amount, String dueDate, AuthToken authToken) throws ArgumentError, Unauthenticated, Unauthorized, TException {
        return null;
    }
}
