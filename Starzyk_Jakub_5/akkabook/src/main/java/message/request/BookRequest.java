package message.request;

import message.BookInfo;

public class BookRequest extends BookInfo {

    public enum Type {

        PRICE,
        ORDER,
        TEXT

    }

    private final Type type;

    public BookRequest(String name, Type type) {
        super(name);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "BookRequest{" +
                "type=" + type +
                "} " + super.toString();
    }
}
