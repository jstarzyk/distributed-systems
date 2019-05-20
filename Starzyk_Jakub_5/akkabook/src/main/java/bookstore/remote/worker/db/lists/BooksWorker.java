package bookstore.remote.worker.db.lists;

import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.*;
import bookstore.remote.worker.FileWorker;
import message.BookInfo;
import message.request.BookRequest;
import message.request.ReadLinesRequest;
import message.request.SearchArgumentsRequest;
import message.response.BookOrder;
import message.response.BookPrice;
import message.response.BookUnavailable;
import message.response.SearchCompleted;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BooksWorker extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private final String booksFileName;

    public BooksWorker(String booksFileName) {
        this.booksFileName = booksFileName;
    }

    private List<String[]> findBooks(List<String> lines, String bookName, SearchArgumentsRequest searchArguments) {
        SearchArgumentsRequest.SearchType searchType = searchArguments.getSearchType();
        boolean matchCase = searchArguments.isMatchCase();

        Stream<String[]> stream = lines.stream()
                .map(line -> line.split("\\s+\\|\\s+", 2));

        Predicate<String[]> predicate = null;
        switch (searchType) {
            case EQUALS:
                if (matchCase) {
                    predicate = pair -> pair[0].equals(bookName);
                } else {
                    predicate = pair -> pair[0].toLowerCase().equals(bookName.toLowerCase());
                }
                break;
            case CONTAINS:
                if (matchCase) {
                    predicate = pair -> pair[0].contains(bookName);
                } else {
                    predicate = pair -> pair[0].toLowerCase().contains(bookName.toLowerCase());
                }
                break;
        }

        return stream.filter(predicate).collect(Collectors.toList());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookRequest.class, bookRequest -> {
                    ReadLinesRequest readLinesRequest = new ReadLinesRequest();
                    getContext().child("fileWorker").get().tell(readLinesRequest, getSender());

                    getContext().become(waitForSearchArguments(bookRequest));
                })
                .build();
    }

    private Receive waitForSearchArguments(BookRequest bookRequest) {
        return receiveBuilder()
                .match(SearchArgumentsRequest.class, searchArguments -> {
                    getContext().become(waitForLines(bookRequest, searchArguments));
                })
                .build();
    }

    private Receive waitForLines(BookRequest bookRequest, SearchArgumentsRequest searchArguments) {
        return receiveBuilder()
                .match(List.class, lines -> {
                    String requestName = bookRequest.getName();
                    List<String[]> foundBooks = findBooks(lines, requestName, searchArguments);

                    if (!foundBooks.isEmpty()) {
                        Source<String[], NotUsed> source = Source.from(foundBooks);
                        Sink<BookInfo, NotUsed> sink = Sink.actorRef(getSender(), new SearchCompleted());
                        Flow<String[], BookInfo, NotUsed> flow = Flow.of(String[].class).map(bookTokens -> {
                            BookInfo bookInfo = null;
                            String foundName = bookTokens[0];

                            switch (bookRequest.getType()) {
                                case PRICE:
                                    double foundPrice = Double.parseDouble(bookTokens[1]);
                                    bookInfo = new BookPrice(foundName, foundPrice);
                                    break;
                                case ORDER:
                                    bookInfo = new BookOrder(foundName);
                                    break;
                            }

                            return bookInfo;
                        });

                        ActorMaterializer mat = ActorMaterializer.create(getContext().getSystem());
                        RunnableGraph<NotUsed> runnable = source.via(flow).toMat(sink, Keep.right());

                        runnable.run(mat);
                    } else {
                        BookInfo bookInfo = new BookUnavailable(requestName,
                                "book '" + requestName + "' is unavailable");
                        getSender().tell(bookInfo, null);
                        getSender().tell(new SearchCompleted(), null);
                    }

                    getContext().unbecome();
                    getContext().unbecome();
                })
                .build();
    }

    @Override
    public void preStart() {
        getContext().actorOf(Props.create(FileWorker.class, booksFileName), "fileWorker");
    }
}
