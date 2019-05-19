package message.response;

import message.BookInfo;

public class BookPrice extends BookInfo {

    private final double price;

    public BookPrice(String name, double price) {
        super(name);
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

//    @Override
//    public BookInfo parseBookInfo(String[] lineTokens) {
//        String name = lineTokens[0];
//        double price = Double.parseDouble(lineTokens[1]);
//        return new BookPrice(name, price);
//    }
}
