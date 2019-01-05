package com.buttongames.butterfly.http.handlers;

import com.buttongames.butterfly.http.exception.InvalidRequestMethodException;
import com.jamesmurty.utils.XMLBuilder2;
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
        XMLBuilder2 respBuilder = XMLBuilder2.create("response")
                .e("facility")
                    .e("location")
                        .e("id").a("__type", "str").t("US-01").up()
                        .e("country").a("__type", "str").t("US").up()
                        .e("region").a("__type", "str").t("TX").up()
                        .e("name").a("__type", "str").t("BUTTERFLY").up()
                        .e("type").a("__type", "u8").t("0").up().up()
                    .e("line")
                        .e("id").a("__type", "str").t("3").up()
                        .e("class").a("__type", "u8").t("8").up()
                        .e("upclass").a("__type", "u8").t("8").up()
                        .e("rtt").a("__type", "u16").t("40").up().up()
                    .e("public")
                        .e("flag").a("__type", "u8").t("1").up()
                        .e("name").a("__type", "str").t("BUTTERFLY").up()
                        .e("lattitude").a("__type", "str").t("0").up()
                        .e("longitude").a("__type", "str").t("0").up().up()
                    .e("share")
                        .e("eacoin")
                            .e("notchamount").a("__type", "s32").t("0").up()
                            .e("notchcount").a("__type", "s32").t("0").up()
                            .e("supplylimit").a("__type", "s32").t("1000000").up().up()
                        .e("url")
                            .e("eapass").a("__type", "str").t("http://eagate.573.jp/").up()
                            .e("arcadefan").a("__type", "str").t("http://eagate.573.jp/").up()
                            .e("konaminetdx").a("__type", "str").t("http://eagate.573.jp/").up()
                            .e("konamiid").a("__type", "str").t("http://eagate.573.jp/").up()
                            .e("eagate").a("__type", "str").t("http://eagate.573.jp/").up().up().up()
                    .e("portfw")
                        .e("globalip").a("__type", "ip4").t("1.0.0.127").up()
                        .e("globalport").a("__type", "u16").t("8888").up()
                        .e("privateport").a("__type", "u16").t("8888");

        return this.sendResponse(request, response, respBuilder);
    }
}
