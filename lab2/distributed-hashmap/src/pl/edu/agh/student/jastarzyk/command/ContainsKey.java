package pl.edu.agh.student.jastarzyk.command;

import pl.edu.agh.student.jastarzyk.map.DistributedMap;

public class ContainsKey implements MapCommand {

    private final DistributedMap state;
    private final String key;

    public ContainsKey(DistributedMap state, String key) {
        this.state = state;
        this.key = key;
    }

    @Override
    public Object executeAndNotify() {
        return state.containsKey(key);
    }

    @Override
    public Object execute() {
        return state.containsKeyLocal(key);
    }

}
