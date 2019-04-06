package pl.edu.agh.student.jastarzyk.examination;

import com.rabbitmq.client.*;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class Exchange {

    public static final String EXCHANGE_NAME = "orthopedic-unit";
    public static final String INFO = "info";
    public static final String REQUEST = "req";
    public static final String RESULT = "res";

    public static void received(String message) {
        System.out.println("Received: " + message);
    }

    public static void sent(String message) {
        System.out.println("Sent: " + message);
    }

    private static String wrap(String s, String c) {
        return c + s + c;
    }

//    public static void queueCreated(String name, List<String> routingPatterns) {
    public static void queueCreated(String name, String... routingPatterns) {
        String result = String.join(" ",
                "Queue",
                wrap(name, "'"),
                "created with routing pattern(s)",
                Arrays.stream(routingPatterns)
                        .map(p -> wrap(p, "'"))
                        .collect(Collectors.joining(", ")));
        System.out.println(result);
    }

    public static String makeRoutingKey(String... tokens) {
        return String.join(".", tokens);
    }

    public static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        return factory.newConnection();
    }

    private static void declareQueues(Channel channel) throws IOException {
        String queueName;

        for (Type type : Type.values()) {
            queueName = type.toString();
            String routingPattern = makeRoutingKey(REQUEST, queueName);
            channel.queueDeclare(queueName, false, false, true, null);
            channel.queueBind(queueName, EXCHANGE_NAME, routingPattern);
            queueCreated(queueName, routingPattern);
        }
    }



    public static void main(String[] args) {
        try {
            Connection connection = getConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            declareQueues(channel);

//            channel.close();
//            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] serialize(Object object) throws IOException {
        var b = new ByteArrayOutputStream();
        var o = new ObjectOutputStream(b);
        o.writeObject(object);
        return b.toByteArray();
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        var b = new ByteArrayInputStream(bytes);
        var o = new ObjectInputStream(b);
        return o.readObject();
    }

//    public static void consumeString(Channel channel, String queueName) throws IOException {
//        Consumer infoConsumer = new DefaultConsumer(channel) {
//            @Override
//            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//                try {
//                    Examination e = (Examination) Examination.deserialize(body);
//                    received(e.toString());
//                } catch (ClassNotFoundException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        };
//
//        channel.basicConsume(queueName, true, infoConsumer);
//    }
}
