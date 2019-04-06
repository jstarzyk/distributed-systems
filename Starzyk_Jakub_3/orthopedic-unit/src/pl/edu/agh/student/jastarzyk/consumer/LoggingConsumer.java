package pl.edu.agh.student.jastarzyk.consumer;

import com.rabbitmq.client.*;
import pl.edu.agh.student.jastarzyk.examination.Examination;
import pl.edu.agh.student.jastarzyk.examination.Exchange;

import java.io.IOException;

public class LoggingConsumer extends DefaultConsumer {
    public LoggingConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
        try {
            Examination message = (Examination) Exchange.deserialize(bytes);
            Exchange.received(message.toString());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
