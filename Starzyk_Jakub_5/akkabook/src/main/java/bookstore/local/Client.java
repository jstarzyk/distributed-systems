package bookstore.local;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import message.request.BookRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Client {

    private static String requests = String.join(", ",
            Arrays.stream(BookRequest.Type.values())
                    .map(Enum::toString)
                    .collect(Collectors.toSet()));

    public static void main(String[] args) throws IOException {
        final ActorSystem system = ActorSystem.create("local_system");
        final ActorRef actor = system.actorOf(Props.create(ClientActor.class), "client");
        System.out.println("Bookstore client started.\n" +
                "Available request types:\n" +
                requests);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
//            try {
                String line = br.readLine();
                if (line.equals("q")) {
                    break;
                }
                actor.tell(line, null);
//                Type bookRequest = Type.valueOf(line);
//            } catch (IllegalArgumentException e) {
//                error("Invalid request type");
//            }

//            if (line.equals(Type.PRICE.toString())) {
//
//            } else if (line.equals(Type.PRICE.toString())) {
//
//            } else if (line.equals(Type.PRICE.toString())) {
//
//            }
        }

        system.terminate();

    }
}
