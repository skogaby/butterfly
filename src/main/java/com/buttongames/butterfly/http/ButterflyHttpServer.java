package com.buttongames.butterfly.http;

import com.buttongames.butterfly.http.exception.InvalidRequestMethodException;
import com.buttongames.butterfly.http.exception.InvalidRequestModelException;
import com.buttongames.butterfly.http.exception.InvalidRequestModuleException;
import com.buttongames.butterfly.http.exception.MismatchedRequestUriException;
import com.buttongames.butterfly.http.handlers.ServicesRequestHandler;
import com.google.common.collect.ImmutableSet;
import spark.Request;

import static spark.Spark.exception;
import static spark.Spark.halt;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.stop;
import static spark.Spark.threadPool;

/**
 * The main HTTP server. This class is responsible for the top-level handling of incoming
 * requests, then delegates the responsbility to the appropriate handler.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class ButterflyHttpServer {

    private static final ImmutableSet<String> SUPPORTED_MODELS;
    private static final ImmutableSet<String> SUPPORTED_MODULES;

    // Do a static setup of our supported models, modules, etc.
    // TODO: Make this not hardcoded
    static {
        SUPPORTED_MODELS = ImmutableSet.of("MDX:J:A:A:2018042300");
        SUPPORTED_MODULES = ImmutableSet.of("services");
    }

    public ButterflyHttpServer() {

    }

    public void startServer() {
        // configure the server properties
        int maxThreads = 20;
        int minThreads = 2;
        int timeOutMillis = 30000;

        // once routes are configured, the server automatically begins
        threadPool(maxThreads, minThreads, timeOutMillis);
        port(80);
        this.configureRoutesAndExceptions();
    }

    public void stopServer() {
        stop();
    }

    /**
     * Configures the routes on the server, and the exception handlers.
     * TODO: Remove all the hardcoded stuff.
     */
    private void configureRoutesAndExceptions() {
        // configure our root route; its handler will parse the request and go from there
        post("/", ((request, response) -> {
            // send the request to the right module handler
            final String requestBody = validateAndUnpackRequest(request);
            final String requestModule = request.queryParams("module");
            final String requestMethod = request.queryParams("method");

            if (requestModule.equals("services")) {
                return ServicesRequestHandler.handleRequest(requestBody, requestMethod, response);
            } else {
                throw new InvalidRequestModuleException();
            }
        }));

        // configure the exception handlers
        exception(InvalidRequestMethodException.class,
                ((exception, request, response) -> halt(400, "Invalid request method.")));
        exception(InvalidRequestModelException.class,
                ((exception, request, response) -> halt(400, "Invalid request model.")));
        exception(InvalidRequestModuleException.class,
                ((exception, request, response) -> halt(400, "Invalid request module.")));
    }

    /**
     * Do some basic validation on the request before we handle it. Returns the request
     * body in plaintext form for handling, if it was a valid request.
     * TODO: Remove all the hardcoded stuff.
     * @param request The request to validate and unpack
     * @return A string representing the plaintext version of the packet, in XML format.
     */
    private String validateAndUnpackRequest(Request request) {
        final String requestUriModel = request.queryParams("model");
        final String requestUriModule = request.queryParams("module");
        final String requestUriMethod = request.queryParams("method");

        // validate the model is supported
        if (!SUPPORTED_MODELS.contains(requestUriModel)) {
            throw new InvalidRequestModelException();
        }

        // validate the module is supported
        if (!SUPPORTED_MODULES.contains(requestUriModule)) {
            throw new InvalidRequestModuleException();
        }

        // validate that the request URI matches the request body
        // TODO: Implement
        if (false) {
            throw new MismatchedRequestUriException();
        }

        // return the request body
        // TODO: Implement
        return "";
    }
}
