package main;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException.Code;

class NodeMonitor implements Watcher {

    private ZooKeeper zk;
    private String znode;
    private NodeMonitorListener listener;

    boolean dead;

    NodeMonitor(ZooKeeper zk, String znode, NodeMonitorListener listener) {
        this.zk = zk;
        this.znode = znode;
        this.listener = listener;
        watch();
    }

    public interface NodeMonitorListener {
        void closing(int rc);
        void nodeCreated();
        void nodeDeleted();
        void nodeChildrenChanged();
    }

    private void watch() {
        try {
            zk.exists(znode, this);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                switch (event.getState()) {
                    case SyncConnected:
                        break;
                    case Expired:
                        dead = true;
                        listener.closing(Code.SESSIONEXPIRED.intValue());
                        break;
                }
                break;
            case NodeCreated:
                listener.nodeCreated();
                watch();
                break;
            case NodeDeleted:
                listener.nodeDeleted();
                watch();
                break;
            case NodeChildrenChanged:
                listener.nodeChildrenChanged();
                watch();
                break;
        }
    }
}
