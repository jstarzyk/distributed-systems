package bookstore.remote;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerApp {

//    static final String TEST_PATH = "testPath";

    public static void main(String[] args) throws IOException {
        File configFile = new File("config/server_app.conf");
        Config config = ConfigFactory.parseFile(configFile);

        final ActorSystem system = ActorSystem.create("bookstore_server", config);
        system.actorOf(Props.create(ServerActor.class), "server");
        System.out.println("Bookstore server started.\n" +
                "Awaiting requests...");

//        actor.tell(TEST_PATH, null);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.equals("q")) {
                break;
            }
        }

        system.terminate();
    }
}
