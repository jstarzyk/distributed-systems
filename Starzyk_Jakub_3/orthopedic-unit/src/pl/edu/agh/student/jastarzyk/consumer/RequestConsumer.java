package pl.edu.agh.student.jastarzyk.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import pl.edu.agh.student.jastarzyk.examination.Exchange;
import pl.edu.agh.student.jastarzyk.examination.Request;
import pl.edu.agh.student.jastarzyk.examination.Result;

import java.io.IOException;

public class RequestConsumer extends DefaultConsumer {

    public RequestConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            Request request = (Request) Exchange.deserialize(body);
            Exchange.received(request.toString());
            String routingKey = request.getRoutingKey();
            Result result = new Result(request);
            byte[] bytes = Exchange.serialize(result);
            this.getChannel().basicPublish(Exchange.EXCHANGE_NAME, routingKey, null, bytes);
            Exchange.sent(result.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
