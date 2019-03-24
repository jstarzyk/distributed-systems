package pl.edu.agh.student.jastarzyk;

import pl.edu.agh.student.jastarzyk.command.MapCommand;
import pl.edu.agh.student.jastarzyk.map.DistributedMap;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");

        DistributedMap state = new DistributedMap("HashMap");
        InputStreamReader in = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(in);

        String line = reader.readLine();
        while (!line.equals("quit")) {
            MapCommand command = MapCommand.parse(state, line);
            if (command != null) {
                System.out.println(command.executeAndNotify());
                System.out.println();
            }
            line = reader.readLine();
        }

        reader.close();
    }

}
