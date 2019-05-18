package bookstore.remote;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import bookstore.remote.worker.OrderWorker;
import bookstore.remote.worker.PriceWorker;
import bookstore.remote.worker.TextWorker;
import message.request.BookRequest;

public class ServerActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private static final String PRICE_WORKER = "priceWorker";
    private static final String ORDER_WORKER = "orderWorker";
    private static final String TEXT_WORKER = "textWorker";

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookRequest.class, bookRequest -> {
                    String worker = null;
                    switch (bookRequest.getType()) {
                        case PRICE:
                            worker = PRICE_WORKER;
                            break;
                        case ORDER:
                            worker = ORDER_WORKER;
                            break;
                        case TEXT:
                            worker = TEXT_WORKER;
                            break;
                    }
                    context().child(worker).get().tell(bookRequest, getSender());
                })
                .match(String.class, s -> {
                    if (s.equals(ServerApp.TEST_PATH)) {
                        System.out.println(getSelf().path().toString());
                    }
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    @Override
    public void preStart() {
        context().actorOf(Props.create(PriceWorker.class), PRICE_WORKER);
        context().actorOf(Props.create(OrderWorker.class), ORDER_WORKER);
        context().actorOf(Props.create(TextWorker.class), TEXT_WORKER);
    }
}
