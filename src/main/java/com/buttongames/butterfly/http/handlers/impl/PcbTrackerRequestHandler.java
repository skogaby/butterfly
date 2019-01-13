package com.buttongames.butterfly.http.handlers.impl;

import com.buttongames.butterfly.http.exception.InvalidRequestMethodException;
import com.buttongames.butterfly.http.handlers.BaseRequestHandler;
import com.buttongames.butterfly.util.PropertyNames;
import com.buttongames.butterfly.xml.KXmlBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import spark.Request;
import spark.Response;

/**
 * Handler for any requests that come to the <code>pcbtracker</code> module.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class PcbTrackerRequestHandler extends BaseRequestHandler {

    private final Logger LOG = LogManager.getLogger(PcbTrackerRequestHandler.class);

    /**
     * Says whether or not this server is running in maintenance mode.
     */
    @Value(PropertyNames.MAINT_MODE)
    private String isMaintenance;

    /**
     * Handles an incoming request for the <code>pcbtracker</code> module.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    @Override
    public Object handleRequest(final Element requestBody, final Request request, final Response response) {
        final String requestMethod = request.attribute("method");

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
        final boolean isMaint = Boolean.parseBoolean(this.isMaintenance);

        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("pcbtracker").a("ecenable", "1");

        if (isMaint) {
            respBuilder = respBuilder.a("eclimit", "0").a("expire", "0").a("limit", "0").a("status", "0");
        }

        return this.sendResponse(request, response, respBuilder);
    }
}
