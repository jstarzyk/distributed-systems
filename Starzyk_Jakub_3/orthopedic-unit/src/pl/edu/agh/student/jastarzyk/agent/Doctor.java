package pl.edu.agh.student.jastarzyk.agent;

import pl.edu.agh.student.jastarzyk.consumer.InfoConsumer;
import pl.edu.agh.student.jastarzyk.consumer.ResultConsumer;
import pl.edu.agh.student.jastarzyk.examination.Exchange;
import pl.edu.agh.student.jastarzyk.examination.Request;
import pl.edu.agh.student.jastarzyk.examination.Type;

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

        String resultPattern = Exchange.makeRoutingKey(Exchange.RESULT, resultQueue.split("\\.")[1]);

        doctor.bindQueue(resultQueue, resultPattern);
        doctor.bindQueue(infoQueue, Exchange.INFO);

//        doctor.listen(resultQueue, new ResultConsumer(doctor.channel));
        doctor.channel.basicConsume(resultQueue, true, new ResultConsumer(doctor.channel));
//        doctor.listen(infoQueue, new InfoConsumer(doctor.channel));
        doctor.channel.basicConsume(infoQueue, true, new InfoConsumer(doctor.channel));

        System.out.println("Waiting for results...");

        while (true) {
            String cmd = br.readLine();
            if (!cmd.equals("")) {
                continue;
            }
            System.out.print("Enter examination type (knee, hip, elbow): ");
            String examinationType = br.readLine().strip().toUpperCase();
            System.out.print("Enter patient name: ");
            String patientName = br.readLine().strip();

            Request request = new Request(Type.valueOf(examinationType), patientName);
            request.setRoutingKey(resultPattern);

//            doctor.send(request, Exchange.makeRoutingKey(Exchange.REQUEST, examinationType));
            String routingKey = Exchange.makeRoutingKey(Exchange.REQUEST, examinationType);
            byte[] bytes = Exchange.serialize(request);
            doctor.channel.basicPublish(Exchange.EXCHANGE_NAME, routingKey, null, bytes);
            Exchange.sent(request.toString());
        }

    }

//    public static void main2(String[] args) throws Exception {
//        System.out.println("DOCTOR");
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//
//        Connection connection = Exchange.getConnection();
//        Channel channel = connection.createChannel();
//
//        String localQueue = channel.queueDeclare().getQueue();
////        String resultPattern = Exchange.RESULT + "." + localQueue.split("\\.")[1];
//        String localPattern = Exchange.makeRoutingKey(Exchange.RESULT, localQueue.split("\\.")[1]);
//        channel.queueBind(localQueue, Exchange.EXCHANGE_NAME, localPattern);
//        channel.queueBind(localQueue, Exchange.EXCHANGE_NAME, Exchange.INFO);
//        Exchange.queueCreated(localQueue, localPattern, Exchange.INFO);
//
//        Consumer loggingConsumer = new LoggingConsumer(channel);
//        channel.basicConsume(localQueue, true, loggingConsumer);
//
//        System.out.println("Waiting for results...");
//
//        while (true) {
//            String cmd = br.readLine();
//            if (!cmd.equals("")) {
//                continue;
//            }
//            System.out.print("Enter examination type (knee, hip, elbow): ");
//            String examinationType = br.readLine().strip().toUpperCase();
//            System.out.print("Enter patient name: ");
//            String patientName = br.readLine().strip();
//
//            Request request = new Request(Type.valueOf(examinationType), patientName);
//            request.setRoutingKey(localPattern);
//
////            String routingKey = Exchange.REQUEST + "." + examinationType;
//            String routingKey = Exchange.makeRoutingKey(Exchange.REQUEST, examinationType);
//            byte[] bytes = Examination.serialize(request);
//
//            channel.basicPublish(Exchange.EXCHANGE_NAME, routingKey, null, bytes);
////            Exchange.sent(bytes);
//            Exchange.sent(request.toString());
//        }
//
//    }

}
