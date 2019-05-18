package bookstore.remote.worker;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import message.request.BookRequest;
import message.response.BookPrice;

public class PriceWorker extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookRequest.class, bookRequest -> {
                    String name = bookRequest.getName();
                    BookPrice price = priceQuery(name);
                    getSender().tell(price, null);
                })
                .build();
    }

    private BookPrice priceQuery(String name) {
        // TODO
        double price = 25.0;
        return new BookPrice(name, price);
    }
}
