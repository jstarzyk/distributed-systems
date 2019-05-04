package server;

import bank.AuthToken;
import bank.Unauthenticated;
import bank.Unauthorized;
import errors.ArgumentError;
import org.apache.thrift.TException;
import premium.CreditSummary;
import premium.PremiumService;

public class PremiumHandler extends BankHandler implements PremiumService.Iface {
    @Override
    public CreditSummary credit(String currency, String amount, String dueDate, AuthToken authToken) throws ArgumentError, Unauthenticated, Unauthorized, TException {
        return null;
    }
}
