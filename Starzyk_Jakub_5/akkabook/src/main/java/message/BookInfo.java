package message;

import scala.Serializable;

public abstract class BookInfo implements Serializable {

    private final String name;

    public BookInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
