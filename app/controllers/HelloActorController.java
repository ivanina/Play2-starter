package controllers;

import akka.actor.*;
import play.mvc.*;
import play.mvc.Result;
import scala.compat.java8.FutureConverters;
import actors.*;
import javax.inject.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static akka.pattern.Patterns.ask;



public class HelloActorController  extends Controller{

    final ActorRef helloActor;

    @Inject
    public HelloActorController(ActorSystem system) {
        this.helloActor = system.actorOf(HelloActor.props);
    }

    public CompletionStage<Result> sayHello(String name){
        return FutureConverters.toJava(
                ask(helloActor, new HelloActorProtocol.SayHello(name),1000)
        ).thenApply(response -> ok(response.toString()));
    }



}
