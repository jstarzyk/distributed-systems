package server.handlers;

import auth.AuthToken;
import auth.Unauthenticated;
import auth.Unauthorized;
import enums.ServiceMethod;
import errors.*;
import org.apache.thrift.TException;
import org.javamoney.moneta.Money;
import premium.CreditInfo;
import premium.PremiumService;
import server.entities.Account;
import server.entities.Bank;
import server.entities.Parser;
import server.entities.SecurityManager;
import server.operations.Credit;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;
import java.util.Date;

public class PremiumHandler extends StandardHandler implements PremiumService.Iface {

    @Override
    public CreditInfo credit(String amount, String currencyCode, String dueDate, AuthToken token) throws TException {
        Handlers.log(ServiceMethod.CREDIT);

        if (SecurityManager.authenticate(token.id, token.passwordHash)) {
            if (SecurityManager.authorize(token.id, Credit.class)) {
                ArgumentError argumentError = new ArgumentError();

                var bigDecimal = Parser.parseBigDecimal(amount);
                if (bigDecimal == null || bigDecimal.compareTo(BigDecimal.ZERO) < 0) {
                    argumentError.error = UArgumentError.ia(new InvalidAmount());
                    if (bigDecimal == null) {
                        argumentError.message = "Invalid amount";
                    } else {
                        argumentError.message = "Amount must be a positive number";
                    }
                    throw argumentError;
                }

                var currencyUnit = Parser.parseCurrencyUnit(currencyCode);
                if (currencyUnit == null) {
                    argumentError.error = UArgumentError.ic(new InvalidCurrency());
                    argumentError.message = "Invalid currency code";
                    throw argumentError;
                }

                var date = Parser.parseDate(dueDate);
                if (date == null || date.before(new Date())) {
                    argumentError.error = UArgumentError.idd(new InvalidDueDate());
                    if (date == null) {
                        argumentError.message = "Invalid date, format is " + Parser.DATE_FORMAT;
                    } else {
                        argumentError.message = "Date must be in the future";
                    }
                    throw argumentError;
                }

                String id = token.id;
                Account account = Bank.findAccount(id);

                Money baseValue = Money.of(bigDecimal, currencyUnit);
                Money totalValue = Bank.calculateTotalCreditValue(id, baseValue, date);
                CurrencyUnit accountCurrency = account.getCurrency();
                CurrencyUnit creditCurrency = totalValue.getCurrency();
                CreditInfo creditInfo = new CreditInfo();

                // can't be null, because authenticated
                if (!creditCurrency.equals(accountCurrency)) {
                    // TODO conversions
                    Money totalInAccountCurrency = Bank.convert(totalValue, accountCurrency);
//                    var domesticTotal = totalValue.with(MonetaryConversions.getConversion(accountCurrency));
                    creditInfo.setDomesticTotal(totalInAccountCurrency.toString());
                    creditInfo.setForeignTotal(totalValue.toString());
                } else {
                    creditInfo.setDomesticTotal(totalValue.toString());
                }

                new Credit(account, baseValue, totalValue, date);

                return creditInfo;
            } else {
                throw new Unauthorized(SecurityManager.UNAUTHORIZED);
            }
        } else {
            throw new Unauthenticated(SecurityManager.UNAUTHENTICATED);
        }
    }

}
