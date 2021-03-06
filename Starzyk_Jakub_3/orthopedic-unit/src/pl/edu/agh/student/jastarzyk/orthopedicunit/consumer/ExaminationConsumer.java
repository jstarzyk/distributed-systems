package pl.edu.agh.student.jastarzyk.orthopedicunit.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import pl.edu.agh.student.jastarzyk.orthopedicunit.message.Message;

import java.io.IOException;

public class ExaminationConsumer extends DefaultConsumer {

    public ExaminationConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
        Message.receive(bytes);
    }

}
