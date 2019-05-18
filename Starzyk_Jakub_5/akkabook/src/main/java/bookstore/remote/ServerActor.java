package bookstore.remote;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ServerActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return null;
    }

    @Override
    public void preStart() throws Exception {
//        context().child("multiplyWorker").get().tell(s, getSelf()); // send task to child

//        context().actorOf(Props.create());
    }
}
