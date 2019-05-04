package server;

import account.AccountService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class ThriftServer {

//    public static AccountHandler handler;
//    public static AccountService.Processor processor;

    public static void main(String[] args) {
        try {
//            handler = new AccountHandler();
//            processor = new AccountService.Processor(handler);

            Runnable simple = new Runnable() {
                @Override
                public void run() {
                    simple();
                }
            };

            new Thread(simple).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void simple()
    {
        try {
            var processor1 = new AccountService.Processor<>(new AccountHandler());
//            var processor2 = new AccountService.Processor<>(new AccountHandler(2));

            TServerTransport serverTransport = new TServerSocket(9090);

            TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
            //TProtocolFactory protocolFactory = new TJSONProtocol.Factory();
            //TProtocolFactory protocolFactory = new TCompactProtocol.Factory();

            TServer server = new TSimpleServer(new TServer.Args(serverTransport).protocolFactory(protocolFactory).processor(processor1));

            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
