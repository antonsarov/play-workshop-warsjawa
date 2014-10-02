package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.User;
import play.*;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.*;

import views.html.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static Result login() {
        String username = request().body().
                asFormUrlEncoded().get("username")[0];
        User user = User.findByUsername(username);
        if (user!=null) {
            // user exists
            session("username", user.getUsername());
            return redirect(routes.Application.dashboard());
        } else {
            return badRequest("Wrong credentials");
        }
    }

    public static Result dashboard() {
        String username = session("username");
        if (username!=null) {

            return ok(dashboard.render(User.findByUsername(username)));
        } else {
            return badRequest();
        }
    }

    public static F.Promise<Result> jobs(String query) {
        F.Promise<WSResponse> responsePromise = WS.url("https://jobs.github.com/positions.json?description=" + query).get();
        F.Promise<Result> resultPromiseJsons = responsePromise.flatMap((WSResponse response) -> {
            JsonNode jobListings = response.asJson();
            Iterator<JsonNode> iterator = jobListings.iterator();
            // collect all jobs that aren't "remote"
            List<JsonNode> jobs = new LinkedList<>();
            while (iterator.hasNext()) {
                JsonNode jn = iterator.next();
                if (!jn.get("location").asText().contains("Remote")) {
                    Logger.debug(jn.get("location").asText());
                    jobs.add(jn);
                }
            }

            List<F.Promise<JsonNode>> promises = jobs.stream().map(job -> getLanLon(job)).collect(Collectors.toList());
            F.Promise<List<JsonNode>> jsonResult = F.Promise.sequence(promises);
            return jsonResult.map(r -> (Result) ok(toJson(r)));
        });
        return resultPromiseJsons;
    }

    private static F.Promise<JsonNode> getLanLon(JsonNode job) {
        String location = job.get("location").asText();
        F.Promise<JsonNode> resultPromise = WS.url("http://maps.googleapis.com/maps/api/geocode/json").
                setQueryParameter("sensor", "false").
                setQueryParameter("address", location).get().map(response -> {
            JsonNode locationAsJson = response.asJson().get("results");
            JsonNode geometry;
            if (locationAsJson.isArray()) {
                geometry = locationAsJson.get(0).get("geometry").get("location");
            } else {
                geometry = locationAsJson.get("geometry");
            }
            ((ObjectNode)job).put("coordinates", geometry);
            return job;
        });
        return resultPromise;
    }

    public static Result logout() {
        session().clear();
        return index();
    }
}
