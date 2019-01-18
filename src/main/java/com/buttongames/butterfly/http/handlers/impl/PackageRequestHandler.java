package com.buttongames.butterfly.http.handlers.impl;

import com.buttongames.butterfly.http.exception.InvalidRequestMethodException;
import com.buttongames.butterfly.http.handlers.BaseRequestHandler;
import com.buttongames.butterfly.xml.kbinxml.KXmlBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import spark.Request;
import spark.Response;

/**
 * Handler for any requests that come to the <code>package</code> module.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class PackageRequestHandler extends BaseRequestHandler {

    private final Logger LOG = LogManager.getLogger(PackageRequestHandler.class);

    /**
     * Handles an incoming request for the <code>package</code> module.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    @Override
    public Object handleRequest(final Element requestBody, final Request request, final Response response) {
        final String requestMethod = request.attribute("method");

        if (requestMethod.equals("list")) {
            return handleListRequest(request, response);
        } else {
            throw new InvalidRequestMethodException();
        }
    }

    /**
     * Handles an incoming request for <code>package.list</code>
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleListRequest(final Request request, final Response response) {
        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("package");

        return this.sendResponse(request, response, respBuilder);
    }
}
