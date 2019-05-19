package bookstore.remote.worker;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import message.request.FileRequest;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class FileWorker extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private Path filePath;
    private List<String> lines;
    private final Object lock;

    public FileWorker(String fileName) {
        this.filePath = getPathFromFileName(fileName);
        this.lock = new Object();
        init();
    }

    private static Path getPathFromFileName(String fileName) {
        return Paths.get(".", fileName);
    }

    private void init() {
        try {
            readLines();
//            watchFile();
        } catch (IOException e) {
            log.error("unable to read file: '" + filePath.getFileName() + "'");
        }
    }

    private void readLines() throws IOException {
        synchronized (lock) {
            lines = Files.lines(filePath)
                    .collect(Collectors.toList());
        }
    }

    private void watchFile() throws IOException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        filePath.register(watcher, ENTRY_MODIFY);

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
                .match(FileRequest.class, fileRequest -> {
                    // TODO watcher
                    readLines();
                    getSender().tell(lines, null);
                })
                .build();
    }
}
