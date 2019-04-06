package pl.edu.agh.student.jastarzyk.agent;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import pl.edu.agh.student.jastarzyk.Exchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public abstract class Agent {

    private Connection connection;
    private Channel channel;
    private List<String> localQueues;

    Agent() throws IOException, TimeoutException {
        this.connection = Exchange.getConnection();
        this.channel = connection.createChannel();
        this.localQueues = new ArrayList<>();
    }

    String createQueue() throws IOException {
        String queue = channel.queueDeclare().getQueue();
        localQueues.add(queue);
        return queue;
    }

    void bindQueue(String queue, String... patterns) throws IOException {
        for (String pattern : patterns) {
            channel.queueBind(queue, Exchange.NAME, pattern);
        }
        Exchange.queueCreated(queue, patterns);
    }

    void listen(String queue, Consumer consumer) throws IOException {
        channel.basicConsume(queue, true, consumer);
    }

    public Channel getChannel() {
        return channel;
    }

}
