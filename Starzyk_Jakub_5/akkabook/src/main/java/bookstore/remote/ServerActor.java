package bookstore.remote;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import bookstore.remote.worker.db.ListsWorker;
import bookstore.remote.worker.db.BooksWorker;
import message.request.BookRequest;
import message.request.SearchArgumentsRequest;

public class ServerActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private static final String LISTS_WORKER = "listsWorker";
    private static final String BOOKS_WORKER = "booksWorker";

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookRequest.class, bookRequest -> {
                    switch (bookRequest.getType()) {
                        case PRICE:
                        case ORDER:
                            context().child(LISTS_WORKER).get().tell(bookRequest, getSender());
                            break;
                        case TEXT:
                            final ActorRef booksWorker = context().child(BOOKS_WORKER).get();
                            booksWorker.tell(bookRequest, getSender());
                            SearchArgumentsRequest searchArguments = new SearchArgumentsRequest(
                                    SearchArgumentsRequest.SearchType.CONTAINS,
                                    false);
                            booksWorker.tell(searchArguments, getSender());
                            break;
                    }
                    System.out.println(bookRequest.toString());
                })
//                .match(String.class, s -> {
//                    if (s.equals(ServerApp.TEST_PATH)) {
//                        System.out.println(getSelf().path().toString());
//                    }
//                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    @Override
    public void preStart() {
        context().actorOf(Props.create(ListsWorker.class), LISTS_WORKER);
        context().actorOf(Props.create(BooksWorker.class), BOOKS_WORKER);
    }
}
