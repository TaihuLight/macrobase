package edu.stanford.futuredata.macrobase.rest;

import com.google.gson.Gson;
import edu.stanford.futuredata.pipeline.BasicBatchPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class RestServer {
    private static Logger log = LoggerFactory.getLogger(RestServer.class);

    private static Gson gson = new Gson();

    public static void main(String[] args) {
        post("/query", (req, res) -> {
            res.type("application/json");
            RestQuery q = gson.fromJson(req.body(), RestQuery.class);
            return new BasicBatchPipeline(q.getBatchQuery()).results();
        }, gson::toJson);

        exception(Exception.class, (exception, request, response) -> {
            log.error("An exception occurred: ", exception);
        });
    }
}
