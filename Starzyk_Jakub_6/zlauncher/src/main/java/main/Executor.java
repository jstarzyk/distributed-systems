package main;

import java.io.*;

import org.apache.zookeeper.*;

public class Executor extends Thread implements Watcher, NodeMonitor.NodeMonitorListener, NodeController.NodeControllerListener {

    private static NodeApplication na;
    private static NodeController nc;
    private NodeMonitor nm;
    private String[] exec;
    private Process child;

    private Executor(String connectString, String znode, String[] exec) throws IOException {
        this.exec = exec;
        this.child = null;
        ZooKeeper zk = new ZooKeeper(connectString, 3000, this);
        na = NodeApplication.getInstance();
        nm = new NodeMonitor(zk, znode, this);
        nc = new NodeController(zk, znode, this);
    }

    public static void main(String[] args) {
        try {
            if (args.length < 3) {
                System.err.println("USAGE: Executor host:port path program [args ...]");
                System.exit(2);
            }

            String connectString = args[0];
            String znode = args[1];
            String[] exec = new String[args.length - 2];
            System.arraycopy(args, 2, exec, 0, exec.length);

            new Executor(connectString, znode, exec).start();
            nc.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process(WatchedEvent event) {
        nm.process(event);
    }

    public void run() {
        try {
            synchronized (this) {
                while (!nm.dead && !nc.exited) {
                    wait();
                }
            }
        } catch (InterruptedException ignored) {
        }
    }

    public void quit() {
        synchronized (this) {
            notifyAll();
        }
    }

    public void closing(int rc) {
        synchronized (this) {
            notifyAll();
        }
    }

    public void nodeCreated() {
        try {
            if (child == null) {
                System.out.println("STARTING '" + String.join(" ", exec) + "'");
                child = Runtime.getRuntime().exec(exec);
            }
            na.update("0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nodeDeleted() {
        try {
            if (child != null) {
                if (child.isAlive()) {
                    System.out.println("KILLING process");
                    child.destroy();
                    child.waitFor();
                }
                child = null;
            }
            na.update("");
        } catch (InterruptedException ignored) {
        }
    }

    public void nodeChildrenChanged(Integer i) {
        na.update(i.toString());
    }
}
