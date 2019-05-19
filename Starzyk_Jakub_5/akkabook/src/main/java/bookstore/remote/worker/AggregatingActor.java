package bookstore.remote.worker;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import message.response.BookPrice;
import message.response.BookUnavailable;

import java.util.*;

public class AggregatingActor extends AbstractActor {

    private final static int WORKERS = 2;

    private int workerCounter = 0;
    private Map<BookPrice, Integer> priceResults = new HashMap<>();
    private Map<BookUnavailable, Integer> unavailableResults = new HashMap<>();

    private ActorRef originalSender;

    public AggregatingActor(ActorRef originalSender) {
        this.originalSender = originalSender;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookPrice.class, bookPrice -> {
                    priceResults.merge(bookPrice, 1, Integer::sum);
                    aggregate();
                })
                .match(BookUnavailable.class, bookUnavailable -> {
                    unavailableResults.merge(bookUnavailable, 1, Integer::sum);
                    aggregate();
                })
                .build();
    }

    private void aggregate() {
        workerCounter++;
        if (workerCounter == WORKERS) {
            if (priceResults.isEmpty()) {
                for (BookUnavailable bookUnavailable : unavailableResults.keySet()) {
                    originalSender.tell(bookUnavailable, null);
                }
            } else {
                for (BookPrice bookPrice : priceResults.keySet()) {
                    originalSender.tell(bookPrice, null);
                }
            }
        }
    }
}
