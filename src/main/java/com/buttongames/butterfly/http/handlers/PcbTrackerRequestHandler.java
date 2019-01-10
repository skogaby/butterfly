package com.buttongames.butterfly.http.handlers;

import com.buttongames.butterfly.http.exception.InvalidRequestMethodException;
import com.buttongames.butterfly.xml.KXmlBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import spark.Request;
import spark.Response;

/**
 * Handler for any requests that come to the <code>pcbtracker</code> module.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class PcbTrackerRequestHandler extends BaseRequestHandler {

    private final Logger LOG = LogManager.getLogger(PcbTrackerRequestHandler.class);

    /**
     * Handles an incoming request for the <code>pcbtracker</code> module.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    @Override
    public Object handleRequest(final Element requestBody, final Request request, final Response response) {
        final String requestMethod = request.queryParams("method");

        if (requestMethod.equals("alive")) {
            return handleAliveRequest(request, response);
        } else {
            throw new InvalidRequestMethodException();
        }
    }

    /**
     * Handles an incoming request for <code>pcbtracker.alive</code>
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleAliveRequest(final Request request, final Response response) {
        // TODO: Remove all the hardcoded stuff and actually do something with the input
        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("pcbtracker").a("ecenable", "1").a("eclimit", "0").a("expire", "0").a("limit", "0").a("status", "0");

        return this.sendResponse(request, response, respBuilder);
    }
}
