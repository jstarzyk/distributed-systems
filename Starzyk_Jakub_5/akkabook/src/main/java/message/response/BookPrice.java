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

    @Override
    public String toString() {
        return "BookPrice{" +
                "price=" + price +
                "} " + super.toString();
    }
}
