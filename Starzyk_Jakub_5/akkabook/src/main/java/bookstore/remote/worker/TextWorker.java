package bookstore.remote.worker;

import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import message.request.BookRequest;
import message.response.BookText;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextWorker extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookRequest.class, bookRequest -> {
                    String name = bookRequest.getName();
//                    var sink = textSink(name);
                    Sink<BookText, NotUsed> sink = textSink(name);
//                    var source = textSource(name);
                    Source<BookText, NotUsed> source = textSource(name);
//                    var mat = ActorMaterializer.create(getContext().getSystem());
                    ActorMaterializer mat = ActorMaterializer.create(getContext().getSystem());
//                    var runnable = source.toMat(sink, Keep.right());
                    RunnableGraph<NotUsed> runnable = source.toMat(sink, Keep.right());
                    runnable.run(mat);
                })
                .build();
    }

    private Source<BookText, NotUsed> textSource(String name) {
        // TODO
        BookText.Type type = BookText.Type.LINE;
        Stream<BookText> s = Stream.of("text1", "text2", "text3", "text4")
                .map(text -> new BookText(name, type, text));
        return Source.from(s.collect(Collectors.toList()))
                .throttle(1, Duration.create(1, TimeUnit.SECONDS), 1, ThrottleMode.shaping());
    }

    private Sink<BookText, NotUsed> textSink(String name) {
        String textType = BookText.Type.LINE.toString().toLowerCase();
        String message = "streaming '" + name + "' completed (" + textType + "(s))";
        return Sink.actorRef(getSender(), message);
    }
}
