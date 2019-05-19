package bookstore.remote.worker;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import message.request.BookRequest;
import message.response.BookPrice;
import message.response.BookUnavailable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class DatabaseWorker extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    //    private File dbFile;
    private Path dbFilePath;
//    private String dbFileName;
    private List<String[]> splitLines;
//    private final Object lock = new Object();
    private final Object lock;

    public DatabaseWorker(String dbFileName) {
//        this.dbFile = new File(dbFileName);
//        this.dbFileName = dbFileName;

        this.dbFilePath = getPathFromFileName(dbFileName);
        this.lock = new Object();
//        System.out.println(dbFilePath.getRoot());
        init();
//        try {
//            readLines();
//            watchDatabase();
//        } catch (IOException e) {
//            log.error("unable to read database file: '" + dbFileName + "'");
//        }
    }

    private static Path getPathFromFileName(String fileName) {
//        URL url = getClass().getResource(fileName);
//        return Paths.get(url.getPath());

//        File file = new File(fileName);
//        return file.toPath();

        return Paths.get(".", fileName);
    }

    private void init() {
        try {
            readLines();
//            watchDatabase();
        } catch (IOException e) {
            log.error("unable to read database file: '" + dbFilePath.getFileName() + "'");
        }
    }

    private void readLines() throws IOException {
        synchronized (lock) {
            splitLines = Files.lines(dbFilePath)
                    .map(line -> line.split("\\s+\\|\\s+", 2))
                    .collect(Collectors.toList());
        }
    }

    private Optional<String[]> findLine(String name) {
        synchronized (lock) {
            return splitLines.stream()
                    .filter(pair -> pair[0].equals(name))
                    .findAny();
        }
    }

    private void watchDatabase() throws IOException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        dbFilePath.register(watcher, ENTRY_MODIFY);

        Runnable r = () -> {
            while (true) {
                try {
                    WatchKey key = watcher.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        if (kind.equals(ENTRY_MODIFY)) {
                            log.info("database file modified, indexing...");
                            readLines();
                        }
                    }
                    key.reset();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(r).start();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookRequest.class, bookRequest -> {
                    String name = bookRequest.getName();
                    Optional<String[]> splitLine = findLine(name);
                    if (splitLine.isPresent()) {
                        double price = Double.parseDouble(splitLine.get()[1]);
                        BookPrice bookPrice = new BookPrice(name, price);
                        getSender().tell(bookPrice, null);
                    } else {
                        BookUnavailable bookUnavailable = new BookUnavailable(name, "book '" + name + "' is unavailable");
                        getSender().tell(bookUnavailable, null);
                    }
                })
                .build();
    }
}
