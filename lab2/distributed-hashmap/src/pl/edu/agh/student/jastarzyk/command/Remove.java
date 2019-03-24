package pl.edu.agh.student.jastarzyk.command;

import pl.edu.agh.student.jastarzyk.map.DistributedMap;

public class Remove implements MapCommand {

    private final DistributedMap state;
    private final String key;

    public Remove(DistributedMap state, String key) {
        this.state = state;
        this.key = key;
    }

    @Override
    public Object executeAndNotify() {
        return state.remove(key);
    }

    @Override
    public Object execute() {
        return state.removeLocal(key);
    }

}
