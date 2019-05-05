package server.entities;

import account.AccountType;
import org.javamoney.moneta.Money;
import server.operations.Credit;
import server.operations.Operation;

import java.util.*;

public abstract class Bank {

//    public static final String UNAUTHENTICATED = "Invalid ID/password";
//    public static final String UNAUTHORIZED = "Your account type does not permit this operation";

//    private static Map<String, String> authentication = new HashMap<>();
//    private static Map<String, Set<AccountType>> authorization = new HashMap<>();
    private static List<Account> accounts = new ArrayList<>();
    private static List<Operation> operations = new LinkedList<>();

//    public static void configure() {
////        Reflections reflections = new Reflections("server");
////        var c = reflections.getSubTypesOf(Operation.class);
////        new Credit().getClass().getName()
//        authorization.put(Credit.class.getName(), Set.of(AccountType.PREMIUM));
//    }

//    public static boolean authenticate(String id, String passwordHash) {
//        String dbHash = authentication.get(id);
//        return dbHash != null && dbHash.equals(passwordHash);
//    }

//    public static boolean authorize(String id, Class<? extends AuthorizedOperation> c) {
//        Set<AccountType> dbSet = authorization.get(c.getName());
//        return dbSet != null && dbSet.contains(findAccount(id).category);
//    }

    static Account findAccount(String id) {
        return accounts.stream().filter(a -> a.id.equals(id)).findAny().orElse(null);
    }

    public static boolean accountExists(String id) {
        return findAccount(id) != null;
    }

    public static String openAccount(Account account) {
        String password = SecurityManager.createPassword();
        String passwordHash = SecurityManager.hashPassword(password);
        SecurityManager.addCredentials(account.id, passwordHash);
        accounts.add(account);
        return password;
    }

    public static Money viewAccountBalance(String id) {
        return findAccount(id).balance;
    }

    public static void takeCredit(String id, Money value, Date dueDate) {
        Account account = findAccount(id);
        Credit credit = new Credit(account, value, dueDate);
        operations.add(credit);
        credit.execute();
    }

    public static AccountType determineCategory(Money limit) {
//         TODO
        return AccountType.STANDARD;
    }


}
