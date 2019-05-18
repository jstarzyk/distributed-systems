package bookstore.remote.worker;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import message.request.BookRequest;
import message.response.BookOrder;

public class OrderWorker extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookRequest.class, bookRequest -> {
                    String name = bookRequest.getName();
                    BookOrder confirmation = order(name);
                    getSender().tell(confirmation, null);
                })
                .build();
    }

    private BookOrder order(String name) {
        // TODO
        return new BookOrder(name);
    }
}
