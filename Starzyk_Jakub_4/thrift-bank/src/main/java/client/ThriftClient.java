package client;

import account.Account;
import account.AccountService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class ThriftClient {

    public static void main(String [] args) {
        try {
            TTransport transport;

            transport = new TSocket("localhost", 9090);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            AccountService.Client client = new AccountService.Client(protocol);

            perform(client);

            transport.close();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    private static void perform(AccountService.Client client) throws TException
    {
        var a = client.createAccount("Jakub", "Starzyk", "110012102", 100);
        System.out.println(a);
//        int product = client.multiply(3,5);
//        System.out.println("3*5=" + product);
    }
}
