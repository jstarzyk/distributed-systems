package bookstore.remote.worker;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import message.BookInfo;
import message.response.BookUnavailable;

import java.util.*;

public class AggregatingActor extends AbstractActor {

//    private final static int WORKERS = 2;
//
//    private int workerCounter = 0;
//    private Map<BookPrice, Integer> priceResults = new HashMap<>();
//    private Map<BookUnavailable, Integer> unavailableResults = new HashMap<>();
//
//    private ActorRef originalSender;
//
//    public AggregatingActor(ActorRef originalSender) {
//        this.originalSender = originalSender;
//    }
//
//    @Override
//    public Receive createReceive() {
//        return receiveBuilder()
//                .match(Optional.class, splitLine -> {
////                    if (splitLine.isPresent()) {
////                        double price = Double.parseDouble(splitLine.get()[1]);
////                        BookPrice bookPrice = new BookPrice(name, price);
////                        getSender().tell(bookPrice, null);
////                    } else {
////                        BookUnavailable bookUnavailable = new BookUnavailable(name, "book '" + name + "' is unavailable");
////                        getSender().tell(bookUnavailable, null);
////                    }
//                })
//                .match(BookPrice.class, bookPrice -> {
//                    priceResults.merge(bookPrice, 1, Integer::sum);
//                    aggregate();
//                })
//                .match(BookUnavailable.class, bookUnavailable -> {
//                    unavailableResults.merge(bookUnavailable, 1, Integer::sum);
//                    aggregate();
//                })
//                .build();
//    }
//
//    private void aggregate() {
//        workerCounter++;
//        if (workerCounter == WORKERS) {
//            if (priceResults.isEmpty()) {
//                for (BookUnavailable bookUnavailable : unavailableResults.keySet()) {
//                    originalSender.tell(bookUnavailable, null);
//                }
//            } else {
//                for (BookPrice bookPrice : priceResults.keySet()) {
//                    originalSender.tell(bookPrice, null);
//                }
//            }
//        }
//    }

    private final static int WORKERS = 2;

    private int workerCounter = 0;
    private Map<BookInfo, Integer> searchResults = new HashMap<>();
    private BookUnavailable bookUnavailable = null;
//    private Map<BookUnavailable, Integer> unavailableResults = Collections.singletonMap();

    private ActorRef originalSender;

    public AggregatingActor(ActorRef originalSender) {
        this.originalSender = originalSender;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookUnavailable.class, bookUnavailable -> {
                    this.bookUnavailable = bookUnavailable;
                    aggregate();
                })
                .match(BookInfo.class, bookInfo -> {
                    searchResults.merge(bookInfo, 1, Integer::sum);
                    aggregate();
                })
//                .match(BookPrice.class, bookPrice -> {
//                    priceResults.merge(bookPrice, 1, Integer::sum);
//                    aggregate();
//                })
//                .match(BookUnavailable.class, bookUnavailable -> {
//                    unavailableResults.merge(bookUnavailable, 1, Integer::sum);
//                    aggregate();
//                })
                .build();
    }

    private void aggregate() {
        workerCounter++;
        if (workerCounter == WORKERS) {
            if (searchResults.isEmpty()) {
                if (bookUnavailable != null) {
                    originalSender.tell(bookUnavailable, null);
                }
            } else {
                for (BookInfo bookInfo : searchResults.keySet()) {
                    originalSender.tell(bookInfo, null);
                }
            }
//            if (priceResults.isEmpty()) {
//                for (BookUnavailable bookUnavailable : unavailableResults.keySet()) {
//                    originalSender.tell(bookUnavailable, null);
//                }
//            } else {
//                for (BookPrice bookPrice : priceResults.keySet()) {
//                    originalSender.tell(bookPrice, null);
//                }
//            }
        }
    }
}
