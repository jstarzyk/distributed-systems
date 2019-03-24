package pl.edu.agh.student.jastarzyk.map;

public interface SimpleStringMap {

    boolean containsKey(String key);

    Integer get(String key);

    Integer put(String key, Integer value);

    Integer remove(String key);

}
