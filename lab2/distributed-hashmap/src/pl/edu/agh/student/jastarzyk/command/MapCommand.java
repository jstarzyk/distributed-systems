package pl.edu.agh.student.jastarzyk.command;

import pl.edu.agh.student.jastarzyk.map.DistributedMap;

public interface MapCommand {

    Object executeAndNotify();

    Object execute();

    static MapCommand parse(DistributedMap state, String s) {
        String[] split = s.split("\\s+");

        if (split.length < 2) {
            return null;
        }

        String cmd = split[0];
        String key = split[1];
        Integer value = null;

        try {
            if (split.length >= 3) value = Integer.parseInt(split[2]);
        } catch (NumberFormatException e) {
            return null;
        }

        switch (cmd) {
            case "put":
                return new Put(state, key, value);
            case "remove":
                return new Remove(state, key);
            case "get":
                return new Get(state, key);
            case "containsKey":
                return new ContainsKey(state, key);
        }

        return null;
    }

}
