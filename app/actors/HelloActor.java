package actors;

import akka.actor.Props;
import akka.actor.UntypedActor;

public class HelloActor extends UntypedActor {

    public static Props props = Props.create(HelloActor.class);

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof HelloActorProtocol.SayHello) {
            sender().tell("Hi, " + ((HelloActorProtocol.SayHello) message).name, self());
        }
    }
}
