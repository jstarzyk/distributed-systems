package message.response;

import message.BookInfo;

public class BookUnavailable extends BookInfo {

    private String message;

    public BookUnavailable(String name, String message) {
        super(name);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
