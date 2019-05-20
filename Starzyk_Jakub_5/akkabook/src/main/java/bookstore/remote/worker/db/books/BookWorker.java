package bookstore.remote.worker.db.books;

import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.stream.ActorMaterializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import bookstore.remote.worker.FileWorker;
import message.request.ReadLinesRequest;
import message.request.TextTypeRequest;
import message.response.BookText;
import message.response.SearchCompleted;
import scala.concurrent.duration.Duration;

import java.text.BreakIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BookWorker extends AbstractActor {

    private final String bookFileName;

    public BookWorker(String bookFileName) {
        this.bookFileName = bookFileName;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, foundBook -> {
//                    System.out.println(foundBook);
                    ReadLinesRequest readLinesRequest = new ReadLinesRequest();
                    getContext().child("fileWorker").get().tell(readLinesRequest, getSender());
                    getContext().become(waitForTextType());
                })
                .build();
    }

    private Receive waitForLines(TextTypeRequest textTypeRequest) {
        return receiveBuilder()
                .match(List.class, lines -> {
                    BookText.Type type = textTypeRequest.getTextType();

                    Sink<BookText, NotUsed> sink = sink();
                    Source<BookText, NotUsed> source = source((List<String>) lines, type)
                            .map(text -> new BookText(bookFileName, type, text));

                    ActorMaterializer mat = ActorMaterializer.create(getContext().getSystem());
                    RunnableGraph<NotUsed> runnable = source.toMat(sink, Keep.right());

                    runnable.run(mat);

                    getContext().unbecome();
                    getContext().unbecome();
                })
                .build();
    }

    private Receive waitForTextType() {
        return receiveBuilder()
                .match(TextTypeRequest.class, textTypeRequest -> {
                    getContext().become(waitForLines(textTypeRequest));
                })
                .build();

    }

    private Source<String, NotUsed> source(List<String> lines, BookText.Type textType) {
        Source<String, NotUsed> source = Source.from(lines);
        if (textType == BookText.Type.SENTENCE) {
            source = source.flatMapConcat(line -> Source.from(parseSentences(line)));
        }

        return source.throttle(1,
                Duration.create(1, TimeUnit.SECONDS),
                1,
                ThrottleMode.shaping());
    }

    private static List<String> parseSentences(String source) {
        List<String> result = new LinkedList<>();

        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        iterator.setText(source);

        int start = iterator.first();
        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {
            result.add(source.substring(start, end));
        }

        return result;
    }

    private Sink<BookText, NotUsed> sink() {
        return Sink.actorRef(getSender(), new SearchCompleted());
    }

    @Override
    public void preStart() {
        getContext().actorOf(Props.create(FileWorker.class, bookFileName), "fileWorker");
    }

}
