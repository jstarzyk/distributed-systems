package pl.edu.agh.student.jastarzyk.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import pl.edu.agh.student.jastarzyk.message.Message;

import java.io.IOException;

public class ResultConsumer extends DefaultConsumer {
    public ResultConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
//        try {
//            Result e = (Result) Exchange.deserialize(bytes);
//            Exchange.received(e.toString());
//        } catch (ClassNotFoundException ex) {
//            ex.printStackTrace();
//        }
        Message.receive(bytes);
    }
}
