package server;

import exchange.ExchangeGrpc;
import exchange.ExchangeOuterClass;
import exchange.ExchangeOuterClass.CurrencyCode;
import exchange.ExchangeOuterClass.CurrencyRate;
import exchange.ExchangeOuterClass.ExchangeRateRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import server.entities.Bank;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ExchangeClient {
    private static final Logger logger = Logger.getLogger(ExchangeClient.class.getName());

    private final ManagedChannel channel;
    private final ExchangeGrpc.ExchangeBlockingStub blockingStub;

    private ExchangeClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build());
    }

    private ExchangeClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = ExchangeGrpc.newBlockingStub(channel);
    }

    private void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    private static Set<CurrencyCode> currencyCodes(Set<CurrencyUnit> currencyUnits) {
        return currencyUnits.stream()
                .map(cu -> CurrencyCode.valueOf(cu.getCurrencyCode()))
                .collect(Collectors.toSet());
    }

//    private void received(CurrencyRate rate) {
//        System.out.println("Received:");
//    }

    private void send(Set<CurrencyCode> currencyCodes) {
        ExchangeRateRequest request = ExchangeRateRequest.newBuilder()
                .addAllCodes(currencyCodes)
                .build();
        Iterator<CurrencyRate> response;
        try {
            response = blockingStub.streamRates(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }

        response.forEachRemaining(r -> {
//            System.out.println(r.toString());
            Bank.addCurrencyRate(Monetary.getCurrency(r.getCode().toString()), new BigDecimal(r.getRate()));
        });
//        logger.info("Greeting: " + response..getMessage());
    }

//    public static void start(int offset, Set<CurrencyUnit> currencies) throws InterruptedException {
    public static void start(Set<CurrencyUnit> currencies) throws InterruptedException {
//        ExchangeClient client = new ExchangeClient("localhost", 50051 + offset);
        ExchangeClient client = new ExchangeClient("localhost", 50051);
        try {
            client.send(currencyCodes(currencies));
        } finally {
            client.shutdown();
        }
    }
}
