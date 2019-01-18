package com.buttongames.butterfly.http.handlers.impl;

import com.buttongames.butterfly.http.handlers.BaseRequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import spark.Request;
import spark.Response;

/**
 * Handler for any requests that come to the <code>eacoin</code> module.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class EacoinRequestHandler extends BaseRequestHandler {

    private final Logger LOG = LogManager.getLogger(EacoinRequestHandler.class);

    /**
     * Handles an incoming request for the <code>eacoin</code> module.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    @Override
    public Object handleRequest(final Element requestBody, final Request request, final Response response) {
        final String requestMethod = request.attribute("method");
        return null;
    }
}
