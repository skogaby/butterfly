package com.buttongames.butterflyserver.http.handlers.impl.mdx.ddr16;

import com.buttongames.butterflyserver.http.exception.InvalidRequestModuleException;
import com.buttongames.butterflyserver.http.handlers.impl.CardManageRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.EacoinRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.EventLogRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.FacilityRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.MessageRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.PackageRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.PcbEventRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.PcbTrackerRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.ServicesRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.SystemRequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import spark.Request;
import spark.Response;

/**
 * The main handler class for all MDX (Dance Dance Revolution) requests.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class BaseDdr16RequestHandler {

    private static final Logger LOG = LogManager.getLogger(BaseDdr16RequestHandler.class);

    /** Handler for requests for the <code>services</code> module. */
    private final ServicesRequestHandler servicesRequestHandler;

    /** Handler for requests for the <code>pcbevent</code> module. */
    private final PcbEventRequestHandler pcbEventRequestHandler;

    /** Handler for requests for the <code>pcbtracker</code> module. */
    private final PcbTrackerRequestHandler pcbTrackerRequestHandler;

    /** Handler for requests for the <code>message</code> module. */
    private final MessageRequestHandler messageRequestHandler;

    /** Handler for requests for the <code>facility</code> module. */
    private final FacilityRequestHandler facilityRequestHandler;

    /** Handler for requests for the <code>package</code> module. */
    private final PackageRequestHandler packageRequestHandler;

    /** Handler for requests for the <code>eventlog</code> module. */
    private final EventLogRequestHandler eventLogRequestHandler;

    /** Handler for requests for the <code>tax</code> module. */
    private final TaxRequestHandler taxRequestHandler;

    /** Handler for requests for the <code>playerdata</code> module. */
    private final PlayerDataRequestHandler playerDataRequestHandler;

    /** Handler for requests for the <code>cardmng</code> module. */
    private final CardManageRequestHandler cardManageRequestHandler;

    /** Handler for requests for the <code>system</code> module. */
    private final SystemRequestHandler systemRequestHandler;

    /** Handler for requests for the <code>eacoin</code> module. */
    private final EacoinRequestHandler eacoinRequestHandler;

    /**
     * Constructor.
     */
    @Autowired
    public BaseDdr16RequestHandler(final ServicesRequestHandler servicesRequestHandler,
                                   final PcbEventRequestHandler pcbEventRequestHandler,
                                   final PcbTrackerRequestHandler pcbTrackerRequestHandler,
                                   final MessageRequestHandler messageRequestHandler,
                                   final FacilityRequestHandler facilityRequestHandler,
                                   final PackageRequestHandler packageRequestHandler,
                                   final EventLogRequestHandler eventLogRequestHandler,
                                   final TaxRequestHandler taxRequestHandler,
                                   final PlayerDataRequestHandler playerDataRequestHandler,
                                   final CardManageRequestHandler cardManageRequestHandler,
                                   final SystemRequestHandler systemRequestHandler,
                                   final EacoinRequestHandler eacoinRequestHandler) {
        this.servicesRequestHandler = servicesRequestHandler;
        this.pcbEventRequestHandler = pcbEventRequestHandler;
        this.pcbTrackerRequestHandler = pcbTrackerRequestHandler;
        this.messageRequestHandler = messageRequestHandler;
        this.facilityRequestHandler = facilityRequestHandler;
        this.packageRequestHandler = packageRequestHandler;
        this.eventLogRequestHandler = eventLogRequestHandler;
        this.taxRequestHandler = taxRequestHandler;
        this.playerDataRequestHandler = playerDataRequestHandler;
        this.cardManageRequestHandler = cardManageRequestHandler;
        this.systemRequestHandler = systemRequestHandler;
        this.eacoinRequestHandler = eacoinRequestHandler;
    }

    /**
     * Handles the incoming request for MDX.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    public Object handleRequest(final Element requestBody, final Request request, final Response response) {
        // send the request to the right module handler
        final String requestModule = request.attribute("module");

        if (requestModule.equals("services")) {
            return this.servicesRequestHandler.handleRequest(requestBody, request, response);
        } else if (requestModule.equals("pcbevent")) {
            return this.pcbEventRequestHandler.handleRequest(requestBody, request, response);
        } else if (requestModule.equals("pcbtracker")) {
            return this.pcbTrackerRequestHandler.handleRequest(requestBody, request, response);
        } else if (requestModule.equals("message")) {
            return this.messageRequestHandler.handleRequest(requestBody, request, response);
        } else if (requestModule.equals("facility")) {
            return this.facilityRequestHandler.handleRequest(requestBody, request, response);
        } else if (requestModule.equals("package")) {
            return this.packageRequestHandler.handleRequest(requestBody, request, response);
        } else if (requestModule.equals("eventlog")) {
            return this.eventLogRequestHandler.handleRequest(requestBody, request, response);
        } else if (requestModule.equals("tax")) {
            return this.taxRequestHandler.handleRequest(requestBody, request, response);
        } else if (requestModule.equals("playerdata")) {
            return this.playerDataRequestHandler.handleRequest(requestBody, request, response);
        } else if (requestModule.equals("cardmng")) {
            return this.cardManageRequestHandler.handleRequest(requestBody, request, response);
        } else if (requestModule.equals("system")) {
            return this.systemRequestHandler.handleRequest(requestBody, request, response);
        } else if (requestModule.equals("eacoin")) {
            return this.eacoinRequestHandler.handleRequest(requestBody, request, response);
        } else {
            throw new InvalidRequestModuleException();
        }
    }
}
