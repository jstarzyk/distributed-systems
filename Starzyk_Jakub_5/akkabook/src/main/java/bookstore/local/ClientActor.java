package bookstore.local;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import message.request.BookRequest;
import message.response.*;

public class ClientActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookPrice.class, bookPrice -> {
                    System.out.println(bookPrice.getPrice());
                })
                .match(BookOrder.class, bookOrder -> {
                    System.out.println(bookOrder.toString());
                })
                .match(BookText.class, bookText -> {
                    String text = bookText.getText();
                    switch (bookText.getType()) {
                        case PARAGRAPH:
                            System.out.print(text);
                        case LINE:
                            System.out.println(text);
                    }
                })
                .match(String.class, line -> {
                    try {
                        String[] tokens = line.split("\\s+", 2);
                        BookRequest.Type requestType = BookRequest.Type.valueOf(tokens[0].toUpperCase());
                        String bookName = tokens[1];
                        send(bookName, requestType);
                    } catch (IndexOutOfBoundsException e) {
                        log.error("not enough arguments");
                    } catch (IllegalArgumentException e) {
                        log.error("invalid request type");
                    }
                })
                .match(BookUnavailable.class, bookUnavailable -> {
                    log.warning(bookUnavailable.getMessage());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    private void send(String bookName, BookRequest.Type requestType) {
        String path = "akka.tcp://bookstore_server@127.0.0.1:2552/user/server";
        getContext().actorSelection(path).tell(new BookRequest(bookName, requestType), getSelf());
    }

//    @Override
//    public void preStart() throws Exception {
//        context().actorOf(Props.create())
//    }
}
