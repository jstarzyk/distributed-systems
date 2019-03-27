package pl.edu.agh.student.jastarzyk.command;

import pl.edu.agh.student.jastarzyk.map.DistributedMap;

public class Put implements MapCommand {

    private final DistributedMap state;
    private final String key;
    private final Integer value;

    public Put(DistributedMap state, String key, Integer value) {
        this.state = state;
        this.key = key;
        this.value = value;
    }

    @Override
    public Object executeAndNotify() {
        return state.put(key, value);
    }

    @Override
    public Object execute() {
        return state.putLocal(key, value);
    }

}
