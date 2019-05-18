package bookstore.remote.worker;

import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import message.request.BookRequest;
import message.response.BookText;

import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextWorker extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookRequest.class, bookRequest -> {
                    String name = bookRequest.getName();
                    var sink = textSink(name);
                    var source = textSource(name);
                    var mat = ActorMaterializer.create(getContext().getSystem());
                    var runnable = source.toMat(sink, Keep.right());
                    runnable.run(mat);
                })
                .build();
    }

    private Source<BookText, NotUsed> textSource(String name) {
        // TODO
        BookText.Type type = BookText.Type.LINE;
        Stream<BookText> s = Stream.of("text1", "text2", "text3", "text4")
                .map(text -> new BookText(name, type, text));
        return Source.from(s.collect(Collectors.toUnmodifiableList()))
                .throttle(1, Duration.ofSeconds(1));
    }

    private Sink<BookText, NotUsed> textSink(String name) {
        String textType = BookText.Type.LINE.toString().toLowerCase();
        String message = "streaming '" + name + "' completed (" + textType + "(s))";
        return Sink.actorRef(getSender(), message);
    }
}
