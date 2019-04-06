package pl.edu.agh.student.jastarzyk.orthopedicunit.message;

import com.rabbitmq.client.Channel;
import pl.edu.agh.student.jastarzyk.orthopedicunit.Exchange;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Message implements Serializable {

    String date;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");

    public Message() {
        this.date = dateFormat.format(new Date());
    }

    private static void received(String message) {
        System.out.println("Received: " + message);
    }

    private static void sent(String message) {
        System.out.println("Sent: " + message);
    }

    private byte[] serialize() throws IOException {
        var b = new ByteArrayOutputStream();
        var o = new ObjectOutputStream(b);
        o.writeObject(this);
        return b.toByteArray();
    }

    private static Message deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        var b = new ByteArrayInputStream(bytes);
        var o = new ObjectInputStream(b);
        return (Message) o.readObject();
    }

    public void send(Channel channel, String routingKey) throws IOException {
        byte[] bytes = this.serialize();
        channel.basicPublish(Exchange.NAME, routingKey, null, bytes);
        sent(this.toString());
    }

    public static Message receive(byte[] bytes) throws IOException {
        Message message = null;
        try {
            message = deserialize(bytes);
            received(message.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return message;
    }

}
