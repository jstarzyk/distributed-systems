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
        final ActorSystem system = ActorSystem.create("bookstore_local");
        final ActorRef actor = system.actorOf(Props.create(ClientActor.class), "client");
        System.out.println("Bookstore client started.\n" +
                "Available request types: " + requests);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.equals("q")) {
                break;
            }
            actor.tell(line, null);
        }

        system.terminate();
    }
}
