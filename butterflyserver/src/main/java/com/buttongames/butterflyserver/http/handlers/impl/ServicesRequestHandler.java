package com.buttongames.butterflyserver.http.handlers.impl;

import com.buttongames.butterflyserver.http.exception.UnsupportedRequestException;
import com.buttongames.butterflyserver.http.handlers.BaseRequestHandler;
import com.buttongames.butterflyserver.util.PropertyNames;
import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import spark.Request;
import spark.Response;

import java.util.Map;

/**
 * Handler for any requests that come to the <code>services</code> module.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class ServicesRequestHandler extends BaseRequestHandler {

    private final Logger LOG = LogManager.getLogger(ServicesRequestHandler.class);

    /** The port the server listens on */
    @Value(PropertyNames.PORT)
    private String port;

    /** URL to return in the <code>services.get</code> request */
    @Value(PropertyNames.URL)
    private String hostUrl;

    /**
     * Mapping of services to their URLs.
     */
    private ImmutableMap<String, String> servicesUrls;

    /**
     * Handles an incoming request for the <code>services</code> module.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    @Override
    public Object handleRequest(final Element requestBody, final Request request, final Response response) {
        final String requestMethod = request.attribute("method");

        if (requestMethod.equals("get")) {
            return handleGetRequest(request, response);
        }

        throw new UnsupportedRequestException();
    }

    /**
     * Handles an incoming request for <code>services.get</code>
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleGetRequest(final Request request, final Response response) {
        // init the URLs if they're null, check the port in the process
        // TODO: Remove all the hardcoded stuff
        if (servicesUrls == null) {
            if (!port.equals("80")) {
                hostUrl = hostUrl + ":" + port;
            }

            servicesUrls = ImmutableMap.<String, String> builder()
                    .put("cardmng", hostUrl)
                    .put("facility", hostUrl)
                    .put("message", hostUrl)
                    .put("numbering", hostUrl)
                    .put("package", hostUrl)
                    .put("pcbevent", hostUrl)
                    .put("pcbtracker", hostUrl)
                    .put("pkglist", hostUrl)
                    .put("posevent", hostUrl)
                    .put("userdata", hostUrl)
                    .put("userid", hostUrl)
                    .put("eacoin", hostUrl)
                    .put("local", hostUrl)
                    .put("local2", hostUrl)
                    .put("lobby", hostUrl)
                    .put("lobby2", hostUrl)
                    .put("ntp", "ntp://pool.ntp.org/")
                    .put("keepalive", "http://127.0.0.1/keepalive?pa=127.0.0.1&ia=127.0.0.1&ga=127.0.0.1&ma=127.0.0.1&t1=2&t2=10")
                    .build();
        }

        // send the response
        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("services").a("expire", "600").a("method", "get").a("mode", "operation").a("status", "0");

        for (Map.Entry<String, String> entry : servicesUrls.entrySet()) {
            respBuilder = respBuilder.e("item").a("name", entry.getKey()).a("url", entry.getValue()).up();
        }

        return this.sendResponse(request, response, respBuilder);
    }
}
