package bookstore.remote.worker.db;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import bookstore.remote.worker.db.books.BookWorker;
import message.request.BookRequest;
import message.request.SearchArgumentsRequest;
import message.request.TextTypeRequest;
import message.response.BookText;
import message.response.BookUnavailable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BooksWorker extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private static final String BOOKS_DIR = "db/books/";
    private List<Path> books;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookRequest.class, bookRequest -> {
                    getContext().become(waitForSearchArguments(bookRequest));
                })
                .build();
    }

    private Receive waitForSearchArguments(BookRequest bookRequest) {
        return receiveBuilder()
                .match(SearchArgumentsRequest.class, searchArguments -> {
                    final String requestName = bookRequest.getName();
                    List<String> books = findBooks(requestName, searchArguments);

                    if (!books.isEmpty()) {
                        final String foundBook = books.get(0);
//                        System.out.println(foundBook);
                        ActorRef bookWorker = getContext().child(foundBook).get();

//                        System.out.println(bookWorker.toString());
                        bookWorker.tell(foundBook, getSender());
                        bookWorker.tell(new TextTypeRequest(BookText.Type.LINE), getSender());
                    } else {
                        BookUnavailable bookUnavailable = new BookUnavailable(requestName,
                                "book '" + requestName + "' is unavailable for streaming");

                        getSender().tell(bookUnavailable, null);
                    }

                    getContext().unbecome();
                })
                .build();
    }

    private List<String> findBooks(String bookName, SearchArgumentsRequest searchArguments) {
        SearchArgumentsRequest.SearchType searchType = searchArguments.getSearchType();
        boolean matchCase = searchArguments.isMatchCase();

        Stream<String> stream = books.stream()
                .map(Path::getFileName)
                .map(Path::toString);

        Predicate<String> predicate = null;
        switch (searchType) {
            case EQUALS:
                if (matchCase) {
                    predicate = book -> book.equals(bookName);
                } else {
                    predicate = book -> book.toLowerCase().equals(bookName.toLowerCase());
                }
                break;
            case CONTAINS:
                if (matchCase) {
                    predicate = book -> book.contains(bookName);
                } else {
                    predicate = book -> book.toLowerCase().contains(bookName.toLowerCase());
                }
                break;
        }

        return stream.filter(predicate).collect(Collectors.toList());
    }

    private void createDatabaseWorkers() {
        try (Stream<Path> walk = Files.walk(Paths.get(BOOKS_DIR))) {
            final List<Path> paths = walk.filter(Files::isRegularFile).collect(Collectors.toList());
            books = new ArrayList<>(paths);
            paths.forEach(p -> getContext().actorOf(Props.create(BookWorker.class, p.toString()), p.getFileName().toString()));
//            System.out.println(books);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preStart() {
        createDatabaseWorkers();
    }
}
