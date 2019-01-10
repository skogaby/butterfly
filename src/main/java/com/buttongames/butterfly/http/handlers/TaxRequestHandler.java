package com.buttongames.butterfly.http.handlers;

import com.buttongames.butterfly.http.exception.InvalidRequestMethodException;
import com.buttongames.butterfly.xml.KXmlBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import spark.Request;
import spark.Response;

/**
 * Handler for any requests that come to the <code>tax</code> module.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class TaxRequestHandler extends BaseRequestHandler {

    private final Logger LOG = LogManager.getLogger(TaxRequestHandler.class);

    /**
     * Handles an incoming request for the <code>tax</code> module.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    @Override
    public Object handleRequest(final Element requestBody, final Request request, final Response response) {
        final String requestMethod = request.queryParams("method");

        if (requestMethod.equals("get_phase")) {
            return handleGetPhaseRequest(request, response);
        } else {
            throw new InvalidRequestMethodException();
        }
    }

    /**
     * Handles an incoming request for <code>tax.get_phase</code>
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleGetPhaseRequest(final Request request, final Response response) {
        // TODO: remove the hardcoded value, actually store phase per PCBID
        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("tax")
                    .s32("phase", 0);

        return this.sendResponse(request, response, respBuilder);
    }
}
