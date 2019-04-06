package pl.edu.agh.student.jastarzyk.agent;

import pl.edu.agh.student.jastarzyk.consumer.InfoConsumer;
import pl.edu.agh.student.jastarzyk.consumer.ResultConsumer;
import pl.edu.agh.student.jastarzyk.Exchange;
import pl.edu.agh.student.jastarzyk.message.Examination;
import pl.edu.agh.student.jastarzyk.message.Request;

import java.io.*;
import java.util.concurrent.TimeoutException;

public class Doctor extends Agent {

    private Doctor() throws IOException, TimeoutException {
        super();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("DOCTOR");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        Doctor doctor = new Doctor();

        String infoQueue = doctor.createQueue();
        String resultQueue = doctor.createQueue();

        String bindingKey = Exchange.makeKey(Exchange.RESULT, resultQueue.split("\\.")[1]);

        doctor.bindQueue(resultQueue, bindingKey);
        doctor.bindQueue(infoQueue, Exchange.makeKey(Exchange.INFO));

        doctor.listen(resultQueue, new ResultConsumer(doctor.getChannel()));
        doctor.listen(infoQueue, new InfoConsumer(doctor.getChannel()));

        System.out.println("Waiting for results...");

        while (true) {
            String cmd = br.readLine();
            if (!cmd.equals("")) {
                continue;
            }

            System.out.print("Enter message type (knee, hip, elbow): ");
            String examinationType = br.readLine().strip().toUpperCase();
            System.out.print("Enter patient name: ");
            String patientName = br.readLine().strip();

            Request request = new Request(Examination.Type.valueOf(examinationType), patientName, bindingKey);
            request.send(doctor.getChannel(), Exchange.makeKey(Exchange.REQUEST, examinationType));
        }
    }

}
