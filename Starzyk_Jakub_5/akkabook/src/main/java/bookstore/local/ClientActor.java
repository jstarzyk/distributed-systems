package bookstore.local;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import message.request.BookRequest;
import message.response.*;

public class ClientActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private final static String SERVER_PATH = "akka.tcp://bookstore_server@127.0.0.1:2550/user/server";

//    private void received() {
//        System.out.print("RECEIVED: ");
//    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookPrice.class, System.out::println)
                .match(BookOrder.class, System.out::println)
                .match(BookText.class, bookText -> {
                    System.out.print("(" + bookText.getName() + ") ");
                    String text = bookText.getText();
//                    switch (bookText.getType()) {
//                        case SENTENCE:
//                            System.out.print(text);
//                        case LINE:
//                            System.out.println(text);
//                    }
                    System.out.println(text);
                })
                .match(BookRequest.class, bookRequest -> getContext().actorSelection(SERVER_PATH)
                        .tell(bookRequest, getSelf()))
                .match(BookUnavailable.class, bookUnavailable -> System.out.println(bookUnavailable.getMessage()))
                .match(SearchCompleted.class, searchCompleted -> {})
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
