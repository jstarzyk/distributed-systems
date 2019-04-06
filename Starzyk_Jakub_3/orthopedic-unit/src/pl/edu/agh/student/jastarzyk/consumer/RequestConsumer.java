package pl.edu.agh.student.jastarzyk.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import pl.edu.agh.student.jastarzyk.message.Message;
import pl.edu.agh.student.jastarzyk.message.Request;
import pl.edu.agh.student.jastarzyk.message.Result;

import java.io.IOException;

public class RequestConsumer extends DefaultConsumer {

    public RequestConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] bytes) throws IOException {
//        try {
//            Request request = (Request) Exchange.deserialize(bytes);
//            Exchange.received(request.toString());
//            String routingKey = request.getRoutingKey();
//            Result result = new Result(request);
////            byte[] bytes = Exchange.serialize(result);
////            this.getChannel().basicPublish(Exchange.EXCHANGE_NAME, routingKey, null, bytes);
////            Exchange.sent(result.toString());
//            result.send(this.getChannel(), routingKey);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

        Request request = (Request) Message.receive(bytes);
        String routingKey = request.getRoutingKey();
        Result result = new Result(request);
        result.send(this.getChannel(), routingKey);
    }
}
