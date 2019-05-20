package message;

public abstract class BookInfo implements scala.Serializable {

    private final String name;

    public BookInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "BookInfo{" +
                "name='" + name + '\'' +
                '}';
    }
}
