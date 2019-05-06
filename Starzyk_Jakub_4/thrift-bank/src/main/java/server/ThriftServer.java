package server;

import account.AccountService;
import client.GrpcClient;
import enums.Currency;
import exchange.ExchangeOuterClass;
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
import server.entities.SecurityManager;
import server.handlers.AccountHandler;
import server.handlers.PremiumHandler;
import server.handlers.StandardHandler;
import standard.StandardService;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.UnknownCurrencyException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThriftServer {

    public static final int ACCOUNT_PORT = 9090;
    public static final int BANK_PORT = 9070;

    public static final String LOCALHOST = "localhost";

    public static final String ACCOUNT_NAME = "account";
    public static final String STANDARD_NAME = "standard";
    public static final String PREMIUM_NAME = "premium";

    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    private static Set<CurrencyUnit> availableCurrencies = Stream.of(Currency.values()).map(Enum::toString)
        .map(Monetary::getCurrency)
        .collect(Collectors.toSet());

    public static Set<CurrencyUnit> currencies = null;

    private static void printError(String message) {
        System.out.println("ERROR: " + message);
    }

    private static Set<CurrencyUnit> inputCurrencies() throws IOException {
        try {
            System.out.print("Enter currency codes: ");

            var inputCurrencies = Stream.of(br.readLine().split("\\s+"))
                    .map(String::toUpperCase)
                    .map(Monetary::getCurrency)
                    .collect(Collectors.toSet());
            inputCurrencies.retainAll(availableCurrencies);

            if (availableCurrencies.isEmpty()) {
                printError("Currency codes not specified in interface");
                return null;
            } else if (inputCurrencies.isEmpty()) {
                printError("No currency codes / Currencies not specified in interface");
                return availableCurrencies;
            } else {
                return inputCurrencies;
            }
        } catch (UnknownCurrencyException e) {
            printError("One or more currency codes invalid");
            return availableCurrencies;
        }
    }

    private static void requestCurrencyRates(Set<CurrencyUnit> currencies) {
        try {
            GrpcClient.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void serverStarted(String processorType, Set<String> serviceNames, int port) {
        System.out.println("Starting " + processorType + " server... " + Parser.join(serviceNames) + " (" + port + ")");
    }

    public static void main(String[] args) {
        try {
            requestCurrencyRates(null);

            currencies = inputCurrencies();
            if (currencies == null) {
                return;
            }
            var tokens = currencies.stream().map(CurrencyUnit::getCurrencyCode).collect(Collectors.toSet());
            System.out.println("Registering currencies... " + Parser.join(tokens));



            SecurityManager.configure();

            if (args.length < 1) {
                return;
            }

            final int offset = Integer.valueOf(args[0]);
            int accountPort = ACCOUNT_PORT + offset;
            int bankPort = BANK_PORT + offset;

            Runnable account = () -> simple(ACCOUNT_NAME, new AccountService.Processor<>(
                    new AccountHandler()), accountPort);

            var services = new HashMap<String, TProcessor>();
            services.put(STANDARD_NAME, new StandardService.Processor<>(new StandardHandler()));
            services.put(PREMIUM_NAME, new PremiumService.Processor<>(new PremiumHandler()));
            Runnable bank = () -> multiplexed(services, bankPort);

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

            serverStarted("simple", Collections.singleton(serviceName), port);

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

            serverStarted("multiplexed", services.keySet(), port);

            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
