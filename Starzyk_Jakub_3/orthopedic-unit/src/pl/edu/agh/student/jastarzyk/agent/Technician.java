package pl.edu.agh.student.jastarzyk.agent;

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

        Technician technician = new Technician(types);

        String queue = technician.createQueue();

        technician.bindQueue(queue, Exchange.INFO);

        technician.listen(queue, new InfoConsumer(technician.getChannel()));

        Consumer requestConsumer = new RequestConsumer(technician.getChannel());
        for (Examination.Type type : technician.types) {
            technician.listen(type.toString(), requestConsumer);
        }

        System.out.println("Waiting for requests...");
    }

}
