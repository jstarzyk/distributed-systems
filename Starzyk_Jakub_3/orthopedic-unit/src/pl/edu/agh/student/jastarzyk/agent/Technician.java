package pl.edu.agh.student.jastarzyk.agent;

//import com.rabbitmq.client.*;
import com.rabbitmq.client.Consumer;
import pl.edu.agh.student.jastarzyk.Exchange;
import pl.edu.agh.student.jastarzyk.consumer.InfoConsumer;
import pl.edu.agh.student.jastarzyk.consumer.RequestConsumer;
import pl.edu.agh.student.jastarzyk.message.*;

import java.io.*;
import java.util.concurrent.TimeoutException;

public class Technician extends Agent {

    private static final int NUMBER_OF_TYPES = 2;
    private Examination.Type[] types;

    private Technician(Examination.Type[] types) throws IOException, TimeoutException {
        super();
        this.types = types;
        this.getChannel().basicQos(1);
    }

//    @Override
//    void listen() throws IOException {
//        channel.basicQos(1);

//        Consumer infoConsumer = new InfoConsumer(channel);
//        channel.basicConsume(localQueue, true, infoConsumer);
//
//        Consumer requestConsumer = new RequestConsumer(channel);
//        for (Type type : types) {
//            channel.basicConsume(type.toString(), true, requestConsumer);
//        }
//    }

    public static void main(String[] args) throws Exception {
        System.out.println("TECHNICIAN");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int numberOfTypes = Math.min(NUMBER_OF_TYPES, Examination.Type.values().length);
        Examination.Type[] types = new Examination.Type[numberOfTypes];

        String line;
        for (int i = 0; i < numberOfTypes; i++) {
            System.out.print("Enter message type #" + (i + 1) + ": ");
            line = br.readLine();
            Examination.Type type = Examination.Type.valueOf(line.toUpperCase());
            types[i] = type;
        }

//        Connection connection = Exchange.getConnection();
//        Channel channel = connection.createChannel();
//        channel.basicQos(1);

//        String localQueue = channel.queueDeclare().getQueue();
//        String routingPattern = Exchange.makeRoutingKey(Exchange.INFO);
//        channel.queueBind(localQueue, Exchange.EXCHANGE_NAME, routingPattern);
//        Exchange.queueCreated(localQueue, routingPattern);

        Technician technician = new Technician(types);
        String queue = technician.createQueue();
        technician.bindQueue(queue, Exchange.INFO);
        technician.listen(queue, new InfoConsumer(technician.getChannel()));
//        technician.channel.basicConsume(queue, true, new InfoConsumer(technician.channel));
        Consumer requestConsumer = new RequestConsumer(technician.getChannel());
        for (Examination.Type type : technician.types) {
            technician.listen(type.toString(), requestConsumer);
//            technician.channel.basicConsume(type.toString(), true, requestConsumer);
        }
//
//        Consumer loggingConsumer = new LoggingConsumer(channel);
//        channel.basicConsume(localQueue, true, loggingConsumer);

//        Consumer requestConsumer = new DefaultConsumer(channel) {
//            @Override
//            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//                try {
//                    Request request = (Request) Examination.deserialize(body);
//                    Exchange.received(request.toString());
//                    String routingKey = request.getRoutingKey();
//                    Result result = new Result(request);
//                    byte[] bytes = Examination.serialize(result);
//                    channel.basicPublish(Exchange.EXCHANGE_NAME, routingKey, null, bytes);
//                    Exchange.sent(result.toString());
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        };

//        for (int i = 0; i < numberOfTypes; i++) {
//            channel.basicConsume(types[i].toString(), true, requestConsumer);
//        }

        System.out.println("Waiting for requests...");

    }

}
