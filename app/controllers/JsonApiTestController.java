package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Properties;

public class JsonApiTestController extends Controller {

    public Result testApi(){
        Properties result = new Properties();

        JsonNode json = request().body().asJson();
        String name = json.findPath("name").textValue();
        if(name != null && name.length() > 0){
            result.put("result","Hello "+ name);
            return ok(Json.toJson(result) ).as("application/json");
        }else {
            result.put("result","Name undefined");
            return badRequest(Json.toJson(result) ).as("application/json");
        }
    }
}
