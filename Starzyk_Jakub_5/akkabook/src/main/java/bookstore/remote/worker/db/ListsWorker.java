package bookstore.remote.worker.db;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import bookstore.remote.worker.AggregatingActor;
import bookstore.remote.worker.db.lists.BooksWorker;
import bookstore.remote.worker.db.lists.OrdersWorker;
import message.BookInfo;
import message.request.BookRequest;
import message.request.SearchArgumentsRequest;
import message.response.BookOrder;

public class ListsWorker extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private static final String LISTS_DIR = "db/lists/";
    private static final String ORDERS_FILE = LISTS_DIR + "orders.txt";
    private static final String BOOKS_WORKER = "booksWorker";
    private static final String ORDERS_WORKER = "ordersWorker";
    private static final int BOOKS_WORKERS = 2;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                // from client
                .match(BookRequest.class, bookRequest -> {
                    ActorRef aggregatingActor;
                    switch (bookRequest.getType()) {
                        case PRICE:
                            aggregatingActor = getContext().actorOf(Props.create(AggregatingActor.class,
                                    getSender(),
                                    getSelf(),
                                    AggregatingActor.ReturnMode.ALL,
                                    BOOKS_WORKERS));

                            for (int i = 1; i <= BOOKS_WORKERS; i++) {
                                ActorRef booksWorker = getContext().child(BOOKS_WORKER + i).get();
                                booksWorker.tell(bookRequest, aggregatingActor);
                                SearchArgumentsRequest searchArguments = new SearchArgumentsRequest(
                                        SearchArgumentsRequest.SearchType.CONTAINS,
                                        false);
                                booksWorker.tell(searchArguments, aggregatingActor);
                            }

                            break;
                        case ORDER:
                            aggregatingActor = getContext().actorOf(Props.create(AggregatingActor.class,
                                    getSender(),
                                    getSelf(),
                                    AggregatingActor.ReturnMode.ONE,
                                    BOOKS_WORKERS));

                            for (int i = 1; i <= BOOKS_WORKERS; i++) {
                                ActorRef booksWorker = getContext().child(BOOKS_WORKER + i).get();
                                booksWorker.tell(bookRequest, aggregatingActor);
                                SearchArgumentsRequest searchArguments = new SearchArgumentsRequest(
                                        SearchArgumentsRequest.SearchType.EQUALS,
                                        true);
                                booksWorker.tell(searchArguments, aggregatingActor);
                            }

                            break;
                    }

                })
                // aggregated
                .match(BookOrder.class, bookOrder -> {
                    ActorRef ordersWorker = getContext().child(ORDERS_WORKER).get();
                    ordersWorker.tell(bookOrder, getSender());
                })
                // aggregated
                .match(BookInfo.class, bookInfo -> getSender().tell(bookInfo, null))
                .build();
    }

    private static String booksFile(Integer i) {
        return LISTS_DIR + "books" + i.toString() + ".txt";
    }

    private void createDatabaseWorkers() {
        for (Integer i = 1; i <= BOOKS_WORKERS; i++) {
            String fileName = booksFile(i);
            getContext().actorOf(Props.create(BooksWorker.class, fileName), BOOKS_WORKER + i.toString());

        }

        getContext().actorOf(Props.create(OrdersWorker.class, ORDERS_FILE), ORDERS_WORKER);
    }

    @Override
    public void preStart() {
        createDatabaseWorkers();
    }
}
