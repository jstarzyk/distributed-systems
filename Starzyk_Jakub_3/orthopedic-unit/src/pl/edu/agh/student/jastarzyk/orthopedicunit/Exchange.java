package pl.edu.agh.student.jastarzyk.orthopedicunit;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import pl.edu.agh.student.jastarzyk.orthopedicunit.message.Examination;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class Exchange {

    public static final String NAME = "orthopedic-unit";
    public static final String INFO = "info";
    public static final String REQUEST = "req";
    public static final String RESULT = "res";

    private static String wrap(String s, String c) {
        return c + s + c;
    }

    private static void createQueues(Channel channel) throws IOException {
        String queueName;

        for (Examination.Type type : Examination.Type.values()) {
            queueName = type.toString();
            String routingPattern = makeKey(REQUEST, queueName);
            channel.queueDeclare(queueName, false, false, true, null);
            channel.queueBind(queueName, NAME, routingPattern);
            queueCreated(queueName, routingPattern);
        }
    }

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

    public static String makeKey(String... tokens) {
        return String.join(".", tokens);
    }

    public static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        return factory.newConnection();
    }

    public static void main(String[] args) {
        try {
            Connection connection = getConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(NAME, BuiltinExchangeType.TOPIC);
            createQueues(channel);
//            channel.close();
//            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
