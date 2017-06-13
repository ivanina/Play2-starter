package controllers;



import com.fasterxml.jackson.databind.JsonNode;
import controllers.actions.PassArgAction;
import models.Client;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

public class Clients  extends Controller{

    public Result getFile(String name) {
        response().discardCookie("theme");

        session().remove("connected");
        session().clear(); //or

        return ok("File path: "+name);
    }

    public Result client(Long id){
        Client client = new Client(id, "Test Joh");
        JsonNode json = Json.toJson(client);
        response().setHeader(ETAG, "xxx");

        response().setCookie(
                new Http.Cookie(
                "theme",
                "red",
                60,
                "/",
                "localhost",
                false,
                false
        ));

        session("connected", "user@gmail.com");

        return ok( json ).as("application/json");
    }

    public Result getTestText(String msg){
        return ok("I received text: "+msg).as("text/html");
    }

    @With(PassArgAction.class)
    public Result passArgIndex(){
        return ok( Json.toJson(  ctx().args.get("HashMap-object")) ).as("application/json");
    }
}
