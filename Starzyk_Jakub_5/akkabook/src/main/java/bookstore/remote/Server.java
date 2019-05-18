package bookstore.remote;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import bookstore.local.ClientActor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Server {

    public static void main(String[] args) throws IOException {
        final ActorSystem system = ActorSystem.create("remote_system");
        final ActorRef actor = system.actorOf(Props.create(ServerActor.class), "server");
        System.out.println("Bookstore server started.\n" +
                "Awaiting requests.");

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
