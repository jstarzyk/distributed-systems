package bookstore.remote.worker.db.lists;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import bookstore.remote.worker.FileWorker;
import message.request.WriteLineRequest;
import message.response.BookOrder;

public class OrdersWorker extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private final String dbFileName;

    public OrdersWorker(String dbFileName) {
        this.dbFileName = dbFileName;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookOrder.class, bookOrder -> {
                    WriteLineRequest writeLineRequest = new WriteLineRequest(bookOrder.getName());
                    getContext().child("fileWorker").get().tell(writeLineRequest, null);
                    getSender().tell(bookOrder, null);
                })
                .build();
    }

    @Override
    public void preStart() {
        getContext().actorOf(Props.create(FileWorker.class, dbFileName), "fileWorker");
    }
}
