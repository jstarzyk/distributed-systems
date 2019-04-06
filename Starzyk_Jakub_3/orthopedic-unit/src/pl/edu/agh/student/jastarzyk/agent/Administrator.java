package pl.edu.agh.student.jastarzyk.agent;

import pl.edu.agh.student.jastarzyk.consumer.LoggingConsumer;
import pl.edu.agh.student.jastarzyk.Exchange;
import pl.edu.agh.student.jastarzyk.message.Info;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class Administrator extends Agent {

    private Administrator() throws IOException, TimeoutException {
        super();
    }

//    @Override
//    void listen() throws IOException {
//        Consumer loggingConsumer = new LoggingConsumer(channel);
//        channel.basicConsume(localQueue, true, loggingConsumer);
//    }

    public static void main(String[] args) throws Exception {
        System.out.println("ADMINISTRATOR");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

//        Administrator administrator = new Administrator(Exchange.makeRoutingKey("*", "*"));
        Administrator administrator = new Administrator();
//        Connection connection = Exchange.getConnection();
//        Channel channel = connection.createChannel();

//        String localQueue = channel.queueDeclare().getQueue();
//        String routingPattern = Exchange.makeRoutingKey("*", "*");
//        channel.queueBind(localQueue, Exchange.EXCHANGE_NAME, routingPattern);
//        Exchange.queueCreated(localQueue, routingPattern);
//        System.out.println("Queue '" + localQueue + "' created with routing pattern '" + routingPattern + "'");

        String queue = administrator.createQueue();
        administrator.bindQueue(queue, Exchange.makeRoutingKey("*", "*"));
        administrator.listen(queue, new LoggingConsumer(administrator.getChannel()));
//        administrator.channel.basicConsume(queue, true, new LoggingConsumer(administrator.channel));
//        administrator.channel.basicConsume(administrator.localQueue, new LoggingConsumer(administrator.channel));
//        Consumer loggingConsumer = new LoggingConsumer(administrator.channel);
//        channel.basicConsume(localQueue, true, loggingConsumer);
//        Exchange.consumeString(channel, localQueue);

        System.out.println("Waiting for messages...");

        while (true) {
            String cmd = br.readLine();
            if (!cmd.equals("")) {
                continue;
            }
            System.out.print("Send info: ");
            String line = br.readLine();

            Info info = new Info(line);
            info.send(administrator.getChannel(), Exchange.INFO);
//            byte[] bytes = Exchange.serialize(info);
//            administrator.channel.basicPublish(Exchange.EXCHANGE_NAME, Exchange.INFO, null, bytes);
//            Exchange.sent(info.toString());
        }
    }

}
