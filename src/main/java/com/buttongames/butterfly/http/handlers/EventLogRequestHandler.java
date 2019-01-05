package com.buttongames.butterfly.http.handlers;

import com.buttongames.butterfly.http.exception.InvalidRequestMethodException;
import com.buttongames.butterfly.xml.KXmlBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import spark.Request;
import spark.Response;

/**
 * Handler for any requests that come to the <code>eventlog</code> module.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class EventLogRequestHandler extends BaseRequestHandler {

    private final Logger LOG = LogManager.getLogger(EventLogRequestHandler.class);

    /**
     * Handles an incoming request for the <code>eventlog</code> module.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    @Override
    public Object handleRequest(final Element requestBody, final Request request, final Response response) {
        final String requestMethod = request.queryParams("method");

        if (requestMethod.equals("write")) {
            return handleWriteRequest(request, response);
        } else {
            throw new InvalidRequestMethodException();
        }
    }

    /**
     * Handles an incoming request for <code>eventlog.write</code>
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleWriteRequest(final Request request, final Response response) {
        LOG.debug("Handling the eventlog.write request");

        // TODO: actually store the events coming in from the client
        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("eventlog")
                    .writeS64("gamesession", 0).up()
                    .writeS32("logsendflg", 0).up()
                    .writeS32("logerrlevel", 0).up()
                    .writeS32("evtidnosendflg", 0);

        return this.sendResponse(request, response, respBuilder);
    }
}
