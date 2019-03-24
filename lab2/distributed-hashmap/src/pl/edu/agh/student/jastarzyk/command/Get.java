package pl.edu.agh.student.jastarzyk.command;

import pl.edu.agh.student.jastarzyk.map.DistributedMap;

public class Get implements MapCommand {

    private final DistributedMap state;
    private final String key;

    public Get(DistributedMap state, String key) {
        this.state = state;
        this.key = key;
    }

    @Override
    public Object executeAndNotify() {
        return state.get(key);
    }

    @Override
    public Object execute() {
        return state.getLocal(key);
    }

}
