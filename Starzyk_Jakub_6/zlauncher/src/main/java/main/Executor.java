package main;

import java.io.*;

import org.apache.zookeeper.*;

public class Executor implements Watcher, Runnable, NodeMonitor.NodeMonitorListener, NodeController.NodeControllerListener {

    private NodeController nc;
    private NodeMonitor dm;
    private String[] exec;
    private Process child;

    private Executor(String connectString, String znode, String[] exec) throws IOException {
        this.exec = exec;
        ZooKeeper zk = new ZooKeeper(connectString, 3000, this);
        dm = new NodeMonitor(zk, znode, this);
        nc = new NodeController(zk, znode, this);
        nc.start();
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

            new Executor(connectString, znode, exec).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process(WatchedEvent event) {
        dm.process(event);
    }

    public void run() {
        try {
            synchronized (this) {
                while (!dm.dead && !nc.exited) {
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
            System.out.println("Starting child");
            child = Runtime.getRuntime().exec(exec);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nodeDeleted() {
        if (child != null) {
            System.out.println("Killing process");
            child.destroy();
            try {
                child.waitFor();
            } catch (InterruptedException ignored) {
            }
        }
        child = null;
    }

    @Override
    public void nodeChildrenChanged() {

    }
}
