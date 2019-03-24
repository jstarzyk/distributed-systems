package pl.edu.agh.student.jastarzyk.network;

import org.jgroups.*;
import org.jgroups.util.Util;
import pl.edu.agh.student.jastarzyk.command.MapCommand;
import pl.edu.agh.student.jastarzyk.map.DistributedMap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class Receiver extends ReceiverAdapter {

    private final DistributedMap state;
    private final JChannel channel;

    public Receiver(JChannel channel, DistributedMap map) {
        this.channel = channel;
        this.state = map;
    }

    @Override
    public void receive(Message message) {
        if (message.getSrc().equals(channel.getAddress())) {
            return;
        }

        String s = (String) message.getObject();

        Printer.printEvent(
                "MESSAGE RECEIVED (FROM " + message.getSrc() + ")", s, Printer.ANSI_BRIGHT_YELLOW,
                true, true);

        MapCommand command = MapCommand.parse(state, s);
        if (command != null) {
            System.out.println(command.execute());
            System.out.println();
        }
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized (state) {
            Util.objectToStream(state.getLocalCopy(), new DataOutputStream(output));
        }
    }

    @Override
    public void setState(InputStream input) throws Exception {
        Map<String, Integer> map = (HashMap<String, Integer>) Util.objectFromStream(new DataInputStream(input));
        synchronized (state) {
            state.setState(map);
        }
    }

    @Override
    public void viewAccepted(View view) {
        Printer.printEvent(
                "VIEW RECEIVED", view.toString(), Printer.ANSI_BRIGHT_BLUE,
                true, true);

        if (view instanceof MergeView) {
            MergeHandler handler = new MergeHandler(channel, (MergeView) view);
            handler.start();
        }
    }

    private static class MergeHandler extends Thread {

        JChannel channel;
        MergeView view;

        private MergeHandler(JChannel channel, MergeView view) {
            this.channel = channel;
            this.view = view;
        }

        public void run() {
            View partition = view.getSubgroups().get(0);
            Address localAddress = channel.getAddress();

            if (partition.getMembers().contains(localAddress)) {
                Printer.printEvent(
                        "INFO",
                        "Already a member of the new primary partition ("
                                + partition + "), will do nothing",
                        Printer.ANSI_BRIGHT_GREEN,
                        true,
                        true
                );
            } else {
                Printer.printEvent(
                        "INFO",
                        "Not a member of the new primary partition ("
                                + partition + "), will reacquire the state",
                        Printer.ANSI_BRIGHT_GREEN,
                        true,
                        true
                );

                try {
                    channel.getState(null, 30000);
                } catch (Exception ignored) {

                }
            }
        }
    }

}
