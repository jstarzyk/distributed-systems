package server;

import auth.AuthToken;
import auth.Unauthenticated;
import auth.Unauthorized;
//import bank.PremiumService;
//import credit.CreditSummary;
import errors.ArgumentError;
import org.apache.thrift.TException;
import premium.CreditSummary;
import premium.PremiumService;

public class PremiumHandler extends StandardHandler implements PremiumService.Iface {
    @Override
    public CreditSummary credit(String currency, String amount, String dueDate, AuthToken token) throws ArgumentError, Unauthenticated, Unauthorized, TException {
        return new CreditSummary();
    }
}
