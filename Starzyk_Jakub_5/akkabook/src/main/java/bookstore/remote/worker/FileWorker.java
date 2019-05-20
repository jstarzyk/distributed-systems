package bookstore.remote.worker;

import akka.Done;
import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.javadsl.*;
import akka.util.ByteString;
import message.request.ReadLinesRequest;
import message.request.WriteLineRequest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class FileWorker extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private Path filePath;
    private List<String> lines;

    public FileWorker(String fileName) {
        this.filePath = getPathFromFileName(fileName);
    }

    private static Path getPathFromFileName(String fileName) {
        return Paths.get(fileName);
    }

    private void readLines() throws ExecutionException, InterruptedException {
        lines = new LinkedList<>();
        Source<String, CompletionStage<IOResult>> source = FileIO.fromPath(filePath)
                .via(Framing.delimiter(ByteString.fromString("\n"), 1024, FramingTruncation.ALLOW))
                .map(ByteString::utf8String);
        ActorMaterializer mat = ActorMaterializer.create(getContext().getSystem());
        Sink<String, CompletionStage<Done>> sink = Sink.foreach(line -> lines.add(line));
        source.runWith(sink, mat).toCompletableFuture().get();
    }

    private void writeLine(String line) throws ExecutionException, InterruptedException {
        Sink<ByteString, CompletionStage<IOResult>> sink = FileIO.toPath(filePath,
                Collections.singleton(StandardOpenOption.APPEND));
        Source<ByteString, NotUsed> source = Source.single(ByteString.fromString(line + "\n"));
        ActorMaterializer mat = ActorMaterializer.create(getContext().getSystem());
        source.runWith(sink, mat).toCompletableFuture().get();
    }

//    private void watchFile() throws IOException {
//        WatchService watcher = FileSystems.getDefault().newWatchService();
//        filePath.register(watcher, ENTRY_MODIFY);
//
//        Runnable r = () -> {
//            while (true) {
//                try {
//                    WatchKey key = watcher.take();
//                    for (WatchEvent<?> event : key.pollEvents()) {
//                        WatchEvent.Kind<?> kind = event.kind();
//                        if (kind.equals(ENTRY_MODIFY)) {
//                            log.info("database file modified, indexing...");
//                            readLines();
//                        }
//                    }
//                    key.reset();
//                } catch (InterruptedException | ExecutionException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        new Thread(r).start();
//    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ReadLinesRequest.class, readLinesRequest -> {
                    // TODO file watcher
                    readLines();
                    getContext().getParent().tell(lines, getSender());
                })
                .match(WriteLineRequest.class, writeLineRequest -> {
                    String line = writeLineRequest.getLine();
                    writeLine(line);
                })
                .build();
    }
}
