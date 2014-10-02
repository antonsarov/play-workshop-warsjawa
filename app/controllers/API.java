package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Anton Sarov on 21.9.2014 г..
 */
public class API extends Controller {

    public static Result getUsers()
    {
        List<User> users = User.find.all();
        List<ObjectNode> usernames = users.stream().
                map((User user) ->
                        JsonNodeFactory.instance.
                                objectNode().
                                put("username", user.getUsername())).
                collect(Collectors.toList());

        return ok(usernames.toString());
    }

    public static Result createUser() {
        JsonNode jsonNode = request().body().asJson();
        String username = jsonNode.get("username").asText();
        User user = User.create(username);
        if (user!=null) {
            return created();
        } else {
            return badRequest();
        }
    }
}
