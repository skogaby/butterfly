package com.buttongames.butterfly.http.handlers.impl;

import com.buttongames.butterfly.http.exception.UnsupportedRequestException;
import com.buttongames.butterfly.http.handlers.BaseRequestHandler;
import com.buttongames.butterfly.xml.KXmlBuilder;
import com.buttongames.butterfly.xml.XmlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import spark.Request;
import spark.Response;

/**
 * Handler for any requests that come to the <code>playerdata</code> module.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class PlayerDataRequestHandler extends BaseRequestHandler {

    private final Logger LOG = LogManager.getLogger(PcbEventRequestHandler.class);

    /**
     * Handles an incoming request for the <code>playerdata</code> module.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    @Override
    public Object handleRequest(final Element requestBody, final Request request, final Response response) {
        final String requestMethod = request.attribute("method");

        if (requestMethod.equals("usergamedata_advanced")) {
            // figure out which kind of usergamedata_advanced request this is
            final String mode = XmlUtils.strValueAtPath(requestBody, "/playerdata/data/mode");
            final String refid = XmlUtils.strValueAtPath(requestBody, "/playerdata/data/refid");
            final String dataid = XmlUtils.strValueAtPath(requestBody, "/playerdata/data/dataid");

            if (mode.equals("userload")) {
                LOG.info("Handling a userload request");

                if (refid.equals("X0000000000000000000000000000000") &&
                        dataid.equals("X0000000000000000000000000000000")) {
                    return handleEventsRequest(request, response);
                }
            } else if (mode.equals("rivalload")) {
                LOG.info("Handling a rivalload request");

                int loadFlag = XmlUtils.intValueAtPath(requestBody, "/playerdata/data/loadflag");

                if (loadFlag == 1) {
                    return handleRivalLoad1Request(request, response);
                } else if (loadFlag == 2) {
                    return  handleRivalLoad2Request(request, response);
                } else if (loadFlag == 4) {
                    return handleGlobalScoresRequest(request, response);
                }
            }
        }

        throw new UnsupportedRequestException();
    }
    /**
     * Handles an incoming request for the events.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleEventsRequest(final Request request, final Response response) {
        final String requestModel = request.attribute("model");

        // TODO: This is almost *definitely* not supposed to be a static response...
        if (this.getSanitizedModel(requestModel).equals("mdx_2018042300")) {
            return this.sendStaticResponse(request, response, "static_responses/mdx_2018042300/events.xml");
        } else {
            throw new UnsupportedRequestException();
        }
    }

    /**
     * Handles a <code>rivalload</code> request with a loadflag of 1.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleRivalLoad1Request(final Request request, final Response response) {
        // TODO: Implement this properly and load rival data...
        final KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0).up()
                    .e("data")
                        .s32("recordtype", 0);

        return this.sendResponse(request, response, respBuilder);
    }

    /**
     * Handles a <code>rivalload</code> request with a loadflag of 2.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleRivalLoad2Request(final Request request, final Response response) {
        // TODO: Implement this properly and load rival data...
        final KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("playerdata")
                .s32("result", 0).up()
                .e("data")
                .s32("recordtype", 0);

        return this.sendResponse(request, response, respBuilder);
    }

    /**
     * Handles a request for the global server scores.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleGlobalScoresRequest(final Request request, final Response response) {
        // TODO: Implement this properly and load/save scores...
        final KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("playerdata")
                .s32("result", 0).up()
                .e("data")
                .s32("recordtype", 0);

        return this.sendResponse(request, response, respBuilder);
    }
}
