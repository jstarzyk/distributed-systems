package message;

public abstract class BookInfo {

    private final String name;

    public BookInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
