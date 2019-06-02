package main;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException.Code;

import java.util.List;

class NodeMonitor implements Watcher {

    private ZooKeeper zk;
    private String znode;
    private NodeMonitorListener listener;
    private List<String> children;

    boolean dead;

    NodeMonitor(ZooKeeper zk, String znode, NodeMonitorListener listener) {
        this.zk = zk;
        this.znode = znode;
        this.listener = listener;
        setDataWatches();
        setChildWatches();
        checkExistingChildren();
    }

    interface NodeMonitorListener {
        void closing(int rc);
        void nodeCreated();
        void nodeDeleted();
        void nodeChildrenChanged(Integer i);
    }

    private void setDataWatches() {
        try {
            zk.exists(znode, this);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setChildWatches() {
        try {
            children = zk.getChildren(znode, this);
        } catch (KeeperException.NoNodeException e) {
            children = null;
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkExistingChildren() {
        if (children != null) {
            listener.nodeCreated();
            listener.nodeChildrenChanged(children.size());
        } else {
            listener.nodeDeleted();
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
                setDataWatches();
                setChildWatches();
                break;
            case NodeDeleted:
                listener.nodeDeleted();
                setDataWatches();
                break;
            case NodeChildrenChanged:
                setChildWatches();
                checkExistingChildren();
                break;
        }
    }
}
