package com.buttongames.butterfly.http.handlers;

import com.buttongames.butterfly.http.exception.InvalidRequestMethodException;
import com.buttongames.butterfly.xml.KXmlBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import spark.Request;
import spark.Response;

/**
 * Handler for any requests that come to the <code>facility</code> module.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class FacilityRequestHandler extends BaseRequestHandler {

    private final Logger LOG = LogManager.getLogger(FacilityRequestHandler.class);

    /**
     * Handles an incoming request for the <code>facility</code> module.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    @Override
    public Object handleRequest(final Element requestBody, final Request request, final Response response) {
        final String requestMethod = request.queryParams("method");

        if (requestMethod.equals("get")) {
            return handleGetRequest(request, response);
        } else {
            throw new InvalidRequestMethodException();
        }
    }

    /**
     * Handles an incoming request for <code>facility.get</code>
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleGetRequest(final Request request, final Response response) {
        LOG.debug("Handling the facility.get request");

        // TODO: Remove all the hardcoded stuff
        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("facility")
                    .e("location")
                        .writeStr("id", "US-01").up()
                        .writeStr("country", "US").up()
                        .writeStr("region", "TX").up()
                        .writeStr("name", "BUTTERFLY").up()
                        .writeU8("type", 0).up().up()
                    .e("line")
                        .writeStr("id", "3").up()
                        .writeU8("class", 8).up()
                        .writeU8("upclass", 8).up()
                        .writeU16("rtt", 40).up().up()
                    .e("public")
                        .writeU8("flag", 1).up()
                        .writeStr("name", "BUTTERFLY").up()
                        .writeStr("lattitude", "0").up()
                        .writeStr("longitude", "0").up().up()
                    .e("share")
                        .e("eacoin")
                            .writeS32("notchamount", 0).up()
                            .writeS32("notchcount", 0).up()
                            .writeS32("supplylimit", 1000000).up().up()
                        .e("url")
                            .writeStr("eapass", "http://eagate.573.jp/").up()
                            .writeStr("arcadefan", "http://eagate.573.jp/").up()
                            .writeStr("konaminetdx", "http://eagate.573.jp/").up()
                            .writeStr("konamiid", "http://eagate.573.jp/").up()
                            .writeStr("eagate", "http://eagate.573.jp/").up().up().up()
                    .e("portfw")
                        .writeIp("globalip", "1.0.0.127").up()
                        .writeU16("globalport", 8888).up()
                        .writeU16("privateport", 8888);

        return this.sendResponse(request, response, respBuilder);
    }
}
