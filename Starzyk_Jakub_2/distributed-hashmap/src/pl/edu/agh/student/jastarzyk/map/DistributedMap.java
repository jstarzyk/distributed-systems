package pl.edu.agh.student.jastarzyk.map;

import pl.edu.agh.student.jastarzyk.network.Channel;

import java.util.HashMap;
import java.util.Map;

public class DistributedMap implements SimpleStringMap {

    private HashMap<String, Integer> localCopy = new HashMap<>();
    private Channel channel;

    public DistributedMap(String clusterName) throws Exception {
        this.channel = new Channel();
        channel.init(clusterName, this);
    }

    public HashMap<String, Integer> getLocalCopy() {
        return localCopy;
    }

    public void setState(Map<? extends String, ? extends Integer> remoteCopy) {
        localCopy.clear();
        localCopy.putAll(remoteCopy);
    }

    @Override
    public boolean containsKey(String key) {
        return containsKeyLocal(key);
    }

    public boolean containsKeyLocal(String key) {
        return localCopy.containsKey(key);
    }

    @Override
    public Integer get(String key) {
        return getLocal(key);
    }

    public Integer getLocal(String key) {
        return localCopy.get(key);
    }

    @Override
    public Integer put(String key, Integer value) {
        Integer result = putLocal(key, value);
        try {
            channel.send(String.format("put %s %d", key, value));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Integer putLocal(String key, Integer value) {
        return localCopy.put(key, value);
    }

    @Override
    public Integer remove(String key) {
        Integer result = removeLocal(key);
        try {
            channel.send(String.format("remove %s", key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Integer removeLocal(String key) {
        return localCopy.remove(key);
    }

}
