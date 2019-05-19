package bookstore.local;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import message.request.BookRequest;
import message.response.*;

public class ClientActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private final static String SERVER_PATH = "akka.tcp://bookstore_server@127.0.0.1:2552/user/server";

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookPrice.class, bookPrice -> System.out.println(bookPrice.getPrice()))
                .match(BookOrder.class, bookOrder -> System.out.println(bookOrder.toString()))
                .match(BookText.class, bookText -> {
                    String text = bookText.getText();
                    switch (bookText.getType()) {
                        case PARAGRAPH:
                            System.out.print(text);
                        case LINE:
                            System.out.println(text);
                    }
                })
                .match(BookRequest.class, bookRequest -> getContext().actorSelection(SERVER_PATH)
                        .tell(bookRequest, getSelf()))
                .match(BookUnavailable.class, bookUnavailable -> System.out.println(bookUnavailable.getMessage()))
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
