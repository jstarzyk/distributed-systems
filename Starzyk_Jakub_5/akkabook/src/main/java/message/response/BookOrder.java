package message.response;

import message.BookInfo;

public class BookOrder extends BookInfo {

    public BookOrder(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "BookOrder{} " + super.toString();
    }
}
