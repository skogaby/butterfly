package com.buttongames.butterfly.http.handlers.impl;

import com.buttongames.butterfly.http.exception.InvalidRequestMethodException;
import com.buttongames.butterfly.http.handlers.BaseRequestHandler;
import com.buttongames.butterfly.xml.KXmlBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import spark.Request;
import spark.Response;

/**
 * Handler for any requests that come to the <code>pcbevent</code> module.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class PcbEventRequestHandler extends BaseRequestHandler {

    private final Logger LOG = LogManager.getLogger(PcbEventRequestHandler.class);

    /**
     * Handles an incoming request for the <code>pcbevent</code> module.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    @Override
    public Object handleRequest(final Element requestBody, final Request request, final Response response) {
        final String requestMethod = request.queryParams("method");

        if (requestMethod.equals("put")) {
            return handlePutRequest(request, response);
        } else {
            throw new InvalidRequestMethodException();
        }
    }

    /**
     * Handles an incoming request for <code>pcbevent.put</code>
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handlePutRequest(final Request request, final Response response) {
        // TODO: Remove all the hardcoded stuff and actually do something with the input
        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("pcbevent");

        return this.sendResponse(request, response, respBuilder);
    }
}
