package pl.edu.agh.student.jastarzyk.orthopedicunit.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import pl.edu.agh.student.jastarzyk.orthopedicunit.message.Message;
import pl.edu.agh.student.jastarzyk.orthopedicunit.message.Request;
import pl.edu.agh.student.jastarzyk.orthopedicunit.message.Result;

import java.io.IOException;

public class RequestConsumer extends DefaultConsumer {

    public RequestConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] bytes) throws IOException {
        Request request = (Request) Message.receive(bytes);
        String routingKey = request.getRoutingKey();
        Result result = new Result(request, null);
        result.send(this.getChannel(), routingKey);
    }

}
