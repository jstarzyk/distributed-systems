package bookstore.local;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import message.request.BookRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ClientApp {

    private static String requests = String.join(", ",
            Arrays.stream(BookRequest.Type.values())
                    .map(Enum::toString)
                    .collect(Collectors.toSet()));

    public static void main(String[] args) throws IOException {
        File configFile = new File("config/client_app.conf");
        Config config = ConfigFactory.parseFile(configFile);

        final ActorSystem system = ActorSystem.create("bookstore_client", config);
        final ActorRef actor = system.actorOf(Props.create(ClientActor.class), "client");
        System.out.println("Bookstore client started.\n" +
                "Available request types: " + requests);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();

            if (line.equals("q")) {
                break;
            }

            BookRequest bookRequest = parseBookRequest(line);
            if (bookRequest != null) {
                actor.tell(bookRequest, null);
            }
        }

        system.terminate();
    }

    private static BookRequest parseBookRequest(String line) {
        try {
            String[] tokens = line.split("\\s+", 2);
            BookRequest.Type requestType = BookRequest.Type.valueOf(tokens[0].toUpperCase());
            String bookName = tokens[1];
            return new BookRequest(bookName, requestType);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("not enough arguments");
        } catch (IllegalArgumentException e) {
            System.out.println("invalid request type");
        }
        return null;
    }
}
