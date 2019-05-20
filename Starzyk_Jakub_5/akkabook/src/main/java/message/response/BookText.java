package message.response;

import message.BookInfo;

public class BookText extends BookInfo {

    public enum Type {

        SENTENCE,
        LINE

    }

    private final Type type;
    private final String text;

    public BookText(String name, Type type, String text) {
        super(name);
        this.type = type;
        this.text = text;
    }

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}
