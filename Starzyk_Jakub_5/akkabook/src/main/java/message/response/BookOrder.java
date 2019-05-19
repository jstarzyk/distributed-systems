package message.response;

import message.BookInfo;

public class BookOrder extends BookInfo {

    public BookOrder(String name) {
        super(name);
    }

//    @Override
//    public BookInfo parseBookInfo(String[] lineTokens) {
//        String name = lineTokens[0];
//        return new BookOrder(name);
//    }
}
