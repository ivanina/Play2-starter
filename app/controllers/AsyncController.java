package controllers;

import akka.NotUsed;
import akka.actor.ActorSystem;

import javax.inject.*;

import akka.actor.Scheduler;
import akka.actor.Status;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.*;
import play.libs.Comet;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import scala.concurrent.ExecutionContextExecutor;

/**
 * This controller contains an action that demonstrates how to write
 * simple asynchronous code in a controller. It uses a timer to
 * asynchronously delay sending a response for 1 second.
 */
@Singleton
public class AsyncController extends Controller {

    private final ActorSystem actorSystem;
    private final ExecutionContextExecutor exec;

    @Inject
    HttpExecutionContext ec;

    /**
     * @param actorSystem We need the {@link ActorSystem}'s
     *                    {@link Scheduler} to run code after a delay.
     * @param exec        We need a Java {@link Executor} to apply the result
     *                    of the {@link CompletableFuture} and a Scala
     *                    {@link ExecutionContext} so we can use the Akka {@link Scheduler}.
     *                    An {@link ExecutionContextExecutor} implements both interfaces.
     */
    @Inject
    public AsyncController(ActorSystem actorSystem, ExecutionContextExecutor exec) {
        this.actorSystem = actorSystem;
        this.exec = exec;
    }

    /**
     * An action that returns a plain text message after a delay
     * of 1 second.
     * <p>
     * The configuration in the <code>routes</code> file means that this method
     * will be called when the application receives a <code>GET</code> request with
     * a path of <code>/message</code>.
     */
    public CompletionStage<Result> message() {
        return getFutureMessage(1, TimeUnit.SECONDS).thenApplyAsync(Results::ok, exec);
    }

    private CompletionStage<String> getFutureMessage(long time, TimeUnit timeUnit) {
        CompletableFuture<String> future = new CompletableFuture<>();

        actorSystem.scheduler().scheduleOnce(
                Duration.create(time, timeUnit),
                () -> future.complete("Hi!"),
                exec
        );
        return future;
    }


    public CompletionStage<Result> testAction() {
        return CompletableFuture
                .supplyAsync(() -> {
                    //TODO: do something
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return new HashMap<String, String>() {{
                        put("timeout", "4 sec");
                        put("k1", "value1");
                        put("k2", "value2");
                    }};
                })
                .thenApply(i -> ok(Json.toJson(i)));
    }

    public CompletionStage<Result> testCompletionStage() {
        String cookeName = "test-cookie-counter";
        return CompletableFuture.supplyAsync(() -> {
            Http.Cookie cookie = request().cookie(cookeName);
            int count = 0;
            if (cookie != null) {
                Logger.debug("cookie exists");
                count += Integer.parseInt(cookie.value()) + 1;
            }
            Logger.debug("count: " + count);
            cookie = new Http.Cookie(cookeName, count + "", 100, "/", "localhost", false, false);

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Logger.debug("set cookie: " + cookie.name() + " with value: " + cookie.value());
            response().setCookie(cookie);
            return count;
        }, ec.current())
                .thenApply(i -> ok("count: " + i));
    }


    public Result chunkedTest() {
        // Prepare a chunked text stream
        Source<ByteString, ?> source = Source.<ByteString>actorRef(256, OverflowStrategy.dropNew())
                .mapMaterializedValue(sourceActor -> {
                    sourceActor.tell(ByteString.fromString(" ->kiki"), null);
                    sourceActor.tell(ByteString.fromString(" ->foo"), null);
                    sourceActor.tell(ByteString.fromString(" ->bar"), null);

                    // request not finished without next line
                    sourceActor.tell(new Status.Success(NotUsed.getInstance()), null);
                    return null;
                });
        // Serves this stream with 200 OK
        return ok().chunked(source);
    }


    /**
     * Do not work correctly (as need)
     * https://www.playframework.com/documentation/2.5.x/JavaComet
     */
    public Result cometTest() {
        final ObjectNode objectNode = Json.newObject();
        objectNode.put("foo", "bar-1");
        objectNode.put("foo-2", "bar-2");
        final Source source = Source.from(Arrays.asList(objectNode));

        return ok().chunked(
                source.via(
                        Comet.json("console.log")
                ))
                .as(Http.MimeTypes.HTML);
    }


    //---------------------------------------------


    /**
     * Handling WebSockets using callbacks
     *
     * for test use: https://www.websocket.org/echo.html
     * and ws://localhost:9000/socket-test
     */
    public LegacyWebSocket<String> socketTest() {
        return WebSocket.whenReady((in, out) -> {
            // For each event received on the socket,
            in.onMessage(System.out::println);

            in.onMessage(msg -> {
                out.write("-=("+msg+")=-");
            });

            // When the socket is closed.
            in.onClose(() -> System.out.println("Disconnected"));

            // Send a single 'Hello!' message
            out.write("Hello!");
        });
    }


}
