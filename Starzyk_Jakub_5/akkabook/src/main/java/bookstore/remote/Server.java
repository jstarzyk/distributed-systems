package bookstore.remote;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Server {

    static final String TEST_PATH = "testPath";

    public static void main(String[] args) throws IOException {
        final ActorSystem system = ActorSystem.create("bookstore_server");
        final ActorRef actor = system.actorOf(Props.create(ServerActor.class), "server");
        System.out.println("Bookstore server started.\n" +
                "Awaiting requests.");

        actor.tell(TEST_PATH, null);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.equals("q")) {
                break;
            }
//            actor.tell(line, null);
        }

        system.terminate();
    }
}
