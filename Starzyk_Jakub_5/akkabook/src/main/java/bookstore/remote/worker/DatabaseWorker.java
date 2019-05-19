package bookstore.remote.worker;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import message.BookInfo;
import message.request.BookRequest;
import message.request.FileRequest;
import message.response.BookOrder;
import message.response.BookPrice;
import message.response.BookUnavailable;

import java.util.List;
import java.util.Optional;

public class DatabaseWorker extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

//    private Path dbFilePath;
//    private List<String[]> splitLines;
//    private final Object lock;
    private String dbFileName;

    public DatabaseWorker(String dbFileName) {
        this.dbFileName = dbFileName;
//        this.dbFilePath = getPathFromFileName(dbFileName);
//        this.lock = new Object();
//        init();
    }

//    private static Path getPathFromFileName(String fileName) {
//        return Paths.get(".", fileName);
//    }
////
//    private void init() {
//        try {
//            readLines();
////            watchDatabase();
//        } catch (IOException e) {
//            log.error("unable to read database file: '" + dbFilePath.getFileName() + "'");
//        }
//    }
//
//    private void readLines() throws IOException {
//        synchronized (lock) {
//            splitLines = Files.lines(dbFilePath)
//                    .map(line -> line.split("\\s+\\|\\s+", 2))
//                    .collect(Collectors.toList());
//        }
//    }
//
//    private Optional<String[]> findLine(String name) {
//        synchronized (lock) {
//            return splitLines.stream()
//                    .filter(pair -> pair[0].equals(name))
//                    .findAny();
//        }
//    }

    private Optional<String[]> findLine(List<String> lines, String name) {
        return lines.stream()
                .map(line -> line.split("\\s+\\|\\s+", 2))
                .filter(pair -> pair[0].equals(name))
                .findAny();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookRequest.class, bookRequest -> {
                    FileRequest fileRequest = new FileRequest();
                    getContext().child("fileWorker").get().tell(fileRequest, getSelf());
                    getContext().become(waitForLines(bookRequest));
                })
//                .match(BookRequest.class, bookRequest -> {
//                    String name = bookRequest.getName();
//                    Optional<String[]> splitLine = findLine(name);
//                    getSender().tell(splitLine, null);
//                    if (splitLine.isPresent()) {
//                        getSender().tell(splitLine.get(), null);
//                    } else {
//                        getSender().tell(null, null);
//                    }
//                })
                .build();
    }

    private Receive waitForLines(BookRequest bookRequest) {
        return receiveBuilder()
                .match(List.class, lines -> {
                    String requestName = bookRequest.getName();
                    Optional<String[]> optionalLine = findLine((List<String>) lines, requestName);

                    BookInfo bookInfo = null;
                    if (optionalLine.isPresent()) {
                        String[] splitLine = optionalLine.get();
                        String foundName = splitLine[0];

                        switch (bookRequest.getType()) {
                            case PRICE:
                                double foundPrice = Double.parseDouble(splitLine[1]);
                                bookInfo = new BookPrice(foundName, foundPrice);
                                break;
                            case ORDER:
                                bookInfo = new BookOrder(foundName);
                                break;
                        }
                    } else {
                        bookInfo = new BookUnavailable(requestName, "book '" + requestName + "' is unavailable");
                    }

                    getSender().tell(bookInfo, null);
                })
                .build();
    }

    @Override
    public void preStart() {
        getContext().actorOf(Props.create(FileWorker.class, dbFileName), "fileWorker");
    }
}
