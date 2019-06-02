package main;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZKUtil;
import org.apache.zookeeper.ZooKeeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class NodeController extends Thread {

    private ZooKeeper zk;
    private String znode;
    private NodeControllerListener listener;

    boolean exited;

    NodeController(ZooKeeper zk, String znode, NodeControllerListener listener) {
        this.zk = zk;
        this.znode = znode;
        this.listener = listener;
    }

    public interface NodeControllerListener {
        void quit();
    }

    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String line = br.readLine();
                if (line.equals("") || line.toLowerCase().equals("tree")) {
                    showTree();
                } else if (line.equals("q") || line.toLowerCase().equals("quit")) {
                    exited = true;
                    listener.quit();
                    break;
                } else {
                    System.err.println("AVAILABLE COMMANDS: tree");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showTree() {
        try {
            ZKUtil.visitSubTreeDFS(zk, znode, false, (rc, path, ctx, name) -> System.out.println(path));
        } catch (KeeperException.NoNodeException e) {
            System.out.println(e.getMessage());
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
