package server;

import account.AccountService;
import enums.Currency;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import premium.PremiumService;
import server.entities.Parser;
import server.handlers.AccountHandler;
import server.handlers.PremiumHandler;
import server.handlers.StandardHandler;
import standard.StandardService;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThriftServer {

    public static final int ACCOUNT_PORT = 9090;
    public static final int BANK_PORT = 9091;

    public static final String LOCALHOST = "localhost";

    public static final String ACCOUNT_NAME = "account";
    public static final String STANDARD_NAME = "standard";
    public static final String PREMIUM_NAME = "premium";

//    private static final Stream<CurrencyUnit> CURRENCIES = Stream.of("PLN", "USD", "EUR", "GBP", "CHF")
    private static final Stream<CurrencyUnit> CURRENCIES = Stream.of(Currency.values()).map(Enum::toString)
//            .map(Monetary::getCurrency).collect(Collectors.toSet());
            .map(Monetary::getCurrency);

    // TODO
    public static final Stream<CurrencyUnit> currencies = Stream.of(Monetary.getCurrency("PLN"), Monetary.getCurrency("USD"));
    public static final Set<String> currencyCodes = currencies.map(CurrencyUnit::getCurrencyCode).collect(Collectors.toSet());


//    public static String join(Collection<String> serviceNames) {
//        return "[" + String.join(", ", serviceNames) + "]";
//    }

    private static void serverStarted(String processorType, Set<String> serviceNames) {
        System.out.println("Starting " + processorType + " server... " + Parser.join(serviceNames));
    }

    public static void main(String[] args) {
        try {
            Runnable account = () -> simple(ACCOUNT_NAME, new AccountService.Processor<>(
                    new AccountHandler()), ACCOUNT_PORT);

            var services = new HashMap<String, TProcessor>();
            services.put(STANDARD_NAME, new StandardService.Processor<>(new StandardHandler()));
            services.put(PREMIUM_NAME, new PremiumService.Processor<>(new PremiumHandler()));
            Runnable bank = () -> multiplexed(services, BANK_PORT);

            new Thread(account).start();
            new Thread(bank).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void simple(String serviceName, TProcessor processor, int port) {
        try {
            TServerTransport serverTransport = new TServerSocket(port);
            TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).protocolFactory(protocolFactory).processor(processor));

            serverStarted("simple", Collections.singleton(serviceName));

            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void multiplexed(Map<String, TProcessor> services, int port) {
        try {
            var processor = new TMultiplexedProcessor();

            for (var e : services.entrySet()) {
                processor.registerProcessor(e.getKey(), e.getValue());
            }

            TServerTransport serverTransport = new TServerSocket(port);
            TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).protocolFactory(protocolFactory).processor(processor));

            serverStarted("multiplexed", services.keySet());

            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
