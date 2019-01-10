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
        // TODO: Remove all the hardcoded stuff
        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("facility")
                    .e("location")
                        .str("id", "US-01").up()
                        .str("country", "US").up()
                        .str("region", "TX").up()
                        .str("name", "BUTTERFLY").up()
                        .u8("type", 0).up().up()
                    .e("line")
                        .str("id", "3").up()
                        .u8("class", 8).up()
                        .u8("upclass", 8).up()
                        .u16("rtt", 40).up().up()
                    .e("public")
                        .u8("flag", 1).up()
                        .str("name", "BUTTERFLY").up()
                        .str("lattitude", "0").up()
                        .str("longitude", "0").up().up()
                    .e("share")
                        .e("eacoin")
                            .s32("notchamount", 0).up()
                            .s32("notchcount", 0).up()
                            .s32("supplylimit", 1000000).up().up()
                        .e("url")
                            .str("eapass", "http://eagate.573.jp/").up()
                            .str("arcadefan", "http://eagate.573.jp/").up()
                            .str("konaminetdx", "http://eagate.573.jp/").up()
                            .str("konamiid", "http://eagate.573.jp/").up()
                            .str("eagate", "http://eagate.573.jp/").up().up().up()
                    .e("portfw")
                        .ip("globalip", "1.0.0.127").up()
                        .u16("globalport", 8888).up()
                        .u16("privateport", 8888);

        return this.sendResponse(request, response, respBuilder);
    }
}
