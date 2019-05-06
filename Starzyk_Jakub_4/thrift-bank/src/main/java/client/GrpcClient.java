package client;

import exchange.ExchangeGrpc;
import exchange.ExchangeOuterClass;
import exchange.ExchangeOuterClass.ExchangeRateRequest;
import exchange.ExchangeOuterClass.ExchangeRateResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GrpcClient {
    private static final Logger logger = Logger.getLogger(GrpcClient.class.getName());

    private final ManagedChannel channel;
    private final ExchangeGrpc.ExchangeBlockingStub blockingStub;

    /** Construct client connecting to HelloWorld server at {@code host:port}. */
    public GrpcClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build());
    }

    /** Construct client for accessing HelloWorld server using the existing channel. */
    GrpcClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = ExchangeGrpc.newBlockingStub(channel);
//        ExchangeGrpc.newFutureStub()
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /** Say hello to server. */
    public void send() {
//        logger.info();
//        logger.info("Will try to greet " + name + " ...");
        //todo
        ExchangeRateRequest request = ExchangeRateRequest.newBuilder()
                .addCodes(ExchangeOuterClass.CurrencyCode.CHF)
                .addCodes(ExchangeOuterClass.CurrencyCode.EUR)
                .build();
        Iterator<ExchangeOuterClass.CurrencyRate> response;
        try {
            response = blockingStub.streamRates(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        response.forEachRemaining(r -> System.out.println(r.toString()));
//        for (var r : response) {
//
//        }
//        logger.info("Greeting: " + response..getMessage());
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting.
     */

//    public static void main(String[] args) throws Exception {
    public static void start() throws InterruptedException {
        GrpcClient client = new GrpcClient("localhost", 50051);
        try {
            /* Access a service running on the local machine on port 50051 */
//            String user = "world";
//            if (args.length > 0) {
//                user = args[0]; /* Use the arg as the name to greet if provided */
//            }
            client.send();
        } finally {
            client.shutdown();
        }
    }
}
