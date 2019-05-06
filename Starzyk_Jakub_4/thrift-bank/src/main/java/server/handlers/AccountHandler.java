package server.handlers;

import account.AccountInfo;
import account.AccountService;
import account.AccountType;
import enums.ServiceMethod;
import errors.*;
import org.apache.thrift.TException;
import org.javamoney.moneta.Money;
import server.entities.Account;
import server.entities.Bank;
import server.entities.Parser;

import java.math.BigDecimal;

public class AccountHandler implements AccountService.Iface {

    @Override
    public AccountInfo account(String firstName, String lastName, String id, String monthlyLimit, String currencyCode) throws TException {
        Handlers.log(ServiceMethod.ACCOUNT);

        ArgumentError argumentError = new ArgumentError();

        if (!Parser.validateFirstName(firstName)) {
            argumentError.error = UArgumentError.ifn(new InvalidFirstName());
            argumentError.message = "Invalid first name";
            throw argumentError;
        }

        if (!Parser.validateLastName(lastName)) {
            argumentError.error = UArgumentError.iln(new InvalidLastName());
            argumentError.message = "Invalid last name";
            throw argumentError;
        }

        if (!Parser.validateID(id)) {
            argumentError.error = UArgumentError.iid(new InvalidID());
            argumentError.message = "Invalid ID";
            throw argumentError;
        }


        var bigDecimal = Parser.parseBigDecimal(monthlyLimit);
        if (bigDecimal == null || bigDecimal.compareTo(BigDecimal.ZERO) < 0) {
            argumentError.error = UArgumentError.ia(new InvalidAmount());
            if (bigDecimal == null) {
                argumentError.message = "Invalid monthly limit";
            } else {
                argumentError.message = "Monthly limit must be a positive number";
            }
            throw argumentError;
        }

        var currencyUnit = Parser.parseCurrencyUnit(currencyCode);
        if (currencyUnit == null) {
            argumentError.error = UArgumentError.ic(new InvalidCurrency());
            argumentError.message = "Invalid currency code";
            throw argumentError;
        }

        if (Bank.accountExists(id)) {
            argumentError.error = UArgumentError.iid(new InvalidID());
            argumentError.message = "Account with entered ID already exists";
            throw argumentError;
        }

        Money limit = Money.of(bigDecimal, currencyUnit);
        AccountType category = Bank.determineCategory(limit);
        Account account = new Account(id, firstName, lastName, category, currencyUnit, limit);
        String password = Bank.openAccount(account);

        return new AccountInfo(category, password);
    }

}
