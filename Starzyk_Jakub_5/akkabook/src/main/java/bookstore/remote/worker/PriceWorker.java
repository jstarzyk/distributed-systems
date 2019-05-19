package bookstore.remote.worker;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import message.request.BookRequest;

public class PriceWorker extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private static final String DB1 = "db/book_db_1.txt";
    private static final String DB2 = "db/book_db_2.txt";

    private static final String DATABASE_WORKER = "databaseWorker";

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookRequest.class, bookRequest -> {
                    ActorRef actor = getContext().actorOf(Props.create(AggregatingActor.class, getSender()));
                    getContext().child(DATABASE_WORKER + "1").get().tell(bookRequest, actor);
                    getContext().child(DATABASE_WORKER + "2").get().tell(bookRequest, actor);
                })
                .build();
    }

    @Override
    public void preStart() {
        getContext().actorOf(Props.create(DatabaseWorker.class, DB1), "databaseWorker1");
        getContext().actorOf(Props.create(DatabaseWorker.class, DB2), "databaseWorker2");
    }
}
