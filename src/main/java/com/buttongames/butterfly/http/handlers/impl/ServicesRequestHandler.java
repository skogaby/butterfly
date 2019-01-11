package com.buttongames.butterfly.http.handlers.impl;

import com.buttongames.butterfly.http.exception.InvalidRequestMethodException;
import com.buttongames.butterfly.http.handlers.BaseRequestHandler;
import com.buttongames.butterfly.xml.KXmlBuilder;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    /**
     * URL to return in the <code>services.get</code> request.
     */
    private static final String HOST_URL = "http://localhost";

    /**
     * Mapping of services to their URLs.
     */
    private static final ImmutableMap<String, String> SERVICES_URLS;

    static {
        SERVICES_URLS = ImmutableMap.<String, String> builder()
                .put("cardmng", HOST_URL)
                .put("facility", HOST_URL)
                .put("message", HOST_URL)
                .put("numbering", HOST_URL)
                .put("package", HOST_URL)
                .put("pcbevent", HOST_URL)
                .put("pcbtracker", HOST_URL)
                .put("pkglist", HOST_URL)
                .put("posevent", HOST_URL)
                .put("userdata", HOST_URL)
                .put("userid", HOST_URL)
                .put("eacoin", HOST_URL)
                .put("local", HOST_URL)
                .put("local2", HOST_URL)
                .put("lobby", HOST_URL)
                .put("lobby2", HOST_URL)
                .put("ntp", "ntp://pool.ntp.org/")
                .put("keepalive", "http://127.0.0.1/keepalive?pa=127.0.0.1&amp;ia=127.0.0.1&amp;ga=127.0.0.1&amp;ma=127.0.0.1&amp;t1=2&amp;t2=10")
                .build();
    }

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
        } else {
            throw new InvalidRequestMethodException();
        }
    }

    /**
     * Handles an incoming request for <code>services.get</code>
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleGetRequest(final Request request, final Response response) {
        // TODO: Remove all the hardcoded stuff
        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("services").a("expire", "600").a("method", "get").a("mode", "operation").a("status", "0");

        for (Map.Entry<String, String> entry : SERVICES_URLS.entrySet()) {
            respBuilder = respBuilder.e("item").a("name", entry.getKey()).a("url", entry.getValue()).up();
        }

        return this.sendResponse(request, response, respBuilder);
    }
}
