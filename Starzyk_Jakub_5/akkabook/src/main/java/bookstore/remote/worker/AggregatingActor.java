package bookstore.remote.worker;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import message.BookInfo;
import message.response.BookUnavailable;
import message.response.SearchCompleted;

import java.util.*;

public class AggregatingActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public enum ReturnMode {

        ONE,
        ALL

    }

    private final ActorRef originalSender;
    private final ActorRef sender;
    private final ReturnMode returnMode;
    private final Integer workers;

    private int workerCounter;
    private Map<BookInfo, Integer> searchResults;
    private BookUnavailable bookUnavailable;

    public AggregatingActor(ActorRef originalSender, ActorRef sender, ReturnMode returnMode, Integer workers) {
        this.originalSender = originalSender;
        this.sender = sender;
        this.returnMode = returnMode;
        this.workers = workers;
        workerCounter = 0;
        searchResults = new HashMap<>();
        bookUnavailable = null;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookUnavailable.class, bookUnavailable -> this.bookUnavailable = bookUnavailable)
                .match(BookInfo.class, bookInfo -> searchResults.merge(bookInfo, 1, Integer::sum))
                .match(SearchCompleted.class, searchCompleted -> aggregate())
                .build();
    }

    private void aggregate() {
        workerCounter++;

        if (workerCounter == workers) {
            if (searchResults.isEmpty()) {
                if (bookUnavailable != null) {
                    sender.tell(bookUnavailable, originalSender);
                }
            } else {
                Iterator<BookInfo> iterator = searchResults.keySet().iterator();

                switch (returnMode) {
                    case ONE:
                        sender.tell(iterator.next(), originalSender);
                        break;
                    case ALL:
                        iterator.forEachRemaining(bookInfo -> sender.tell(bookInfo, originalSender));
                        break;
                }
            }
        }
    }
}
