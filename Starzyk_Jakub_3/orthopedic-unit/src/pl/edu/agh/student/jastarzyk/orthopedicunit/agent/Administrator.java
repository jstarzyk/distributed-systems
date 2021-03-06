package pl.edu.agh.student.jastarzyk.orthopedicunit.agent;

import pl.edu.agh.student.jastarzyk.orthopedicunit.consumer.ExaminationConsumer;
import pl.edu.agh.student.jastarzyk.orthopedicunit.Exchange;
import pl.edu.agh.student.jastarzyk.orthopedicunit.message.Info;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class Administrator extends Agent {

    private Administrator() throws IOException, TimeoutException {
        super();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("ADMINISTRATOR");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        Administrator administrator = new Administrator();
        String queue = administrator.createQueue();
        administrator.bindQueue(queue, Exchange.makeKey("*", "*"));
        administrator.listen(queue, new ExaminationConsumer(administrator.getChannel()));

        String routingKey = Exchange.makeKey(Exchange.INFO);

        System.out.println("Waiting for messages...");

        while (true) {
            String cmd = br.readLine();
            if (!cmd.equals("")) {
                continue;
            }

            System.out.print("Send info: ");
            String line = br.readLine();

            Info info = new Info(line);
            info.send(administrator.getChannel(), routingKey);
        }
    }

}
