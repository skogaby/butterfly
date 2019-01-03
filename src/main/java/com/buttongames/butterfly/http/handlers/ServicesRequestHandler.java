package com.buttongames.butterfly.http.handlers;

import com.buttongames.butterfly.http.exception.InvalidRequestMethodException;
import spark.Response;

/**
 * Handler for any requests that come to the <code>services</code> module.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class ServicesRequestHandler extends BaseRequestHandler {

    /**
     * Handles all requests to the <code>services/code> module.
     * @param request
     * @param requestMethod
     * @param response
     * @return
     */
    public static Object handleRequest(String request, String requestMethod, Response response) {
        if (requestMethod.equals("get")) {
            return  handleGetRequest(request, response);
        } else {
            throw new InvalidRequestMethodException();
        }
    }

    /**
     * Handles <code>services.get</code> requests.
     * @param request
     * @param response
     * @return
     */
    private static Object handleGetRequest(String request, Response response) {
        // TODO: Implement
        return "Womp womp";
    }
}
