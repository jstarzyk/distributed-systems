package server.handlers;

import auth.AuthToken;
import auth.Unauthenticated;
import enums.ServiceMethod;
import org.apache.thrift.TException;
import server.entities.Bank;
import server.entities.SecurityManager;
import server.operations.Operation;
import standard.StandardService;

public class StandardHandler implements StandardService.Iface {

    @Override
    public String balance(AuthToken token) throws TException {
        Handlers.log(ServiceMethod.BALANCE);

        if (SecurityManager.authenticate(token.id, token.passwordHash)) {
            return Bank.viewAccountBalance(token.id).toString();
        } else {
            throw new Unauthenticated(SecurityManager.UNAUTHENTICATED);
        }
    }

    @Override
    public String confirm(AuthToken token) throws TException {
        Handlers.log(ServiceMethod.CONFIRM);

        if (SecurityManager.authenticate(token.id, token.passwordHash)) {
            Operation operation = Bank.executeLastOperation(token.id);
            if (operation == null) {
                return "No operation to confirm";
            } else {
                return operation.toString();
            }
        } else {
            throw new Unauthenticated(SecurityManager.UNAUTHENTICATED);
        }
    }

}
