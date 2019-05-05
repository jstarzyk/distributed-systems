package server.entities;

import account.AccountType;
import org.apache.commons.codec.digest.DigestUtils;
import server.operations.AuthorizedOperation;
import server.operations.Credit;

import java.util.*;

public abstract class SecurityManager {

    public static final String UNAUTHENTICATED = "Invalid ID/password";
    public static final String UNAUTHORIZED = "Your account type does not permit this operation";

    private static Map<String, String> authentication = new HashMap<>();
    private static Map<String, Set<AccountType>> authorization = new HashMap<>();

    public static void configure() {
//        Reflections reflections = new Reflections("server");
//        var c = reflections.getSubTypesOf(Operation.class);
//        new Credit().getClass().getName()
        authorization.put(Credit.class.getName(), Set.of(AccountType.PREMIUM));
    }

    public static boolean authenticate(String id, String passwordHash) {
        String dbHash = authentication.get(id);
        return dbHash != null && dbHash.equals(passwordHash);
    }
//
    public static boolean authorize(String id, Class<? extends AuthorizedOperation> c) {
        Set<AccountType> dbSet = authorization.get(c.getName());
        return dbSet != null && dbSet.contains(Bank.findAccount(id).category);
    }

    public static void addCredentials(String id, String passwordHash) {
        authentication.put(id, passwordHash);
    }

    public static String createPassword() {
        // TODO
        return "pass1";
    }

    public static String hashPassword(String password) {
        return DigestUtils.sha3_256Hex(password);
    }

}
