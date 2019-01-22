package com.buttongames.butterfly.http;

import com.buttongames.butterfly.compression.Lz77;
import com.buttongames.butterfly.encryption.Rc4;
import com.buttongames.butterfly.hibernate.dao.impl.ButterflyUserDao;
import com.buttongames.butterfly.hibernate.dao.impl.MachineDao;
import com.buttongames.butterfly.http.exception.CardCipherException;
import com.buttongames.butterfly.http.exception.InvalidPcbIdException;
import com.buttongames.butterfly.http.exception.InvalidRequestException;
import com.buttongames.butterfly.http.exception.InvalidRequestMethodException;
import com.buttongames.butterfly.http.exception.InvalidRequestModelException;
import com.buttongames.butterfly.http.exception.InvalidRequestModuleException;
import com.buttongames.butterfly.http.exception.MismatchedRequestUriException;
import com.buttongames.butterfly.http.exception.UnsupportedRequestException;
import com.buttongames.butterfly.http.handlers.impl.CardManageRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.EacoinRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.EventLogRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.FacilityRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.MessageRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.PackageRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.PcbEventRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.PcbTrackerRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.PlayerDataRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.ServicesRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.SystemRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.TaxRequestHandler;
import com.buttongames.butterfly.model.ButterflyUser;
import com.buttongames.butterfly.model.Machine;
import com.buttongames.butterfly.util.PropertyNames;
import com.buttongames.butterfly.xml.XmlUtils;
import com.buttongames.butterfly.xml.kbinxml.PublicKt;
import com.google.common.collect.ImmutableSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import spark.Request;
import spark.utils.StringUtils;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalDateTime;

import static com.buttongames.butterfly.util.Constants.COMPRESSION_HEADER;
import static com.buttongames.butterfly.util.Constants.CRYPT_KEY_HEADER;
import static com.buttongames.butterfly.util.Constants.LZ77_COMPRESSION;
import static spark.Spark.exception;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.stop;
import static spark.Spark.threadPool;

/**
 * The main HTTP server. This class is responsible for the top-level handling of incoming
 * requests, then delegates the responsibility to the appropriate handler.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class ButterflyHttpServer {

    private static final Logger LOG = LogManager.getLogger(ButterflyHttpServer.class);

    /**
     * Static set of all the models this server supports.
     */
    private static final ImmutableSet<String> SUPPORTED_MODELS;

    /**
     * Static set of all the modules this server supports.
     */
    private static final ImmutableSet<String> SUPPORTED_MODULES;

    // Do a static setup of our supported models, modules, etc.
    static {
        SUPPORTED_MODELS = ImmutableSet.of("mdx_2018042300");
        SUPPORTED_MODULES = ImmutableSet.of("services", "pcbtracker", "message", "facility", "pcbevent",
                "package", "eventlog", "tax", "playerdata", "cardmng", "system", "eacoin");
    }

    /** The port the server listens on */
    @Value(PropertyNames.PORT)
    private String port;

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

    /** DAO for interacting with <code>Machine</code> objects in the database. */
    private final MachineDao machineDao;

    /** DAO for interacting with <code>ButterflyUser</code> objects in the database. */
    private final ButterflyUserDao userDao;

    /**
     * Constructor.
     */
    @Autowired
    public ButterflyHttpServer(final ServicesRequestHandler servicesRequestHandler,
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
                               final EacoinRequestHandler eacoinRequestHandler,
                               final MachineDao machineDao,
                               final ButterflyUserDao userDao) {
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
        this.machineDao = machineDao;
        this.userDao = userDao;
    }

    /**
     * Configures the routes on our server and begins listening.
     */
    public void startServer() {
        // configure the server properties
        int maxThreads = 20;
        int minThreads = 2;
        int timeOutMillis = 30000;

        // once routes are configured, the server automatically begins
        threadPool(maxThreads, minThreads, timeOutMillis);
        port(Integer.parseInt(this.port));
        this.configureRoutesAndExceptions();
    }

    /**
     * Stops the HTTP server.
     */
    public void stopServer() {
        stop();
    }

    /**
     * Configures the routes on the server, and the exception handlers.
     */
    private void configureRoutesAndExceptions() {
        // configure our root route; its handler will parse the request and go from there
        post("/", ((request, response) -> {
            // send the request to the right module handler
            final Element requestBody = validateAndUnpackRequest(request);
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
        }));

        // configure the exception handlers
        exception(InvalidRequestMethodException.class, ((exception, request, response) -> {
                    response.status(400);
                    response.body("Invalid request method.");
                }));
        exception(InvalidRequestModelException.class, ((exception, request, response) -> {
                    response.status(400);
                    response.body("Invalid request model.");
                }));
        exception(InvalidRequestModuleException.class, ((exception, request, response) -> {
                    response.status(400);
                    response.body("Invalid request module.");
                }));
        exception(MismatchedRequestUriException.class, (((exception, request, response) -> {
                    response.status(400);
                    response.body("Request URI does not match request body.");
                })));
        exception(InvalidPcbIdException.class, (((exception, request, response) -> {
                    response.status(403);
                    response.body("PCBID is not valid or nonexistent.");
                })));
        exception(UnsupportedRequestException.class, (((exception, request, response) -> {
                    response.status(400);
                    response.body("This request is probably valid, but currently unsupported.");

                    LOG.info(String.format("RECEIVED AN UNSUPPORTED REQUEST: %s.%s",
                            request.attribute("module"), request.attribute("method")));
                })));
        exception(CardCipherException.class, (((exception, request, response) -> {
                    response.status(403);
                    response.body("Issue with converting the requested card ID, likely invalid ID.");
                })));
    }

    /**
     * Validates incoming requests for basic sanity checks, and returns the request
     * as a plaintext XML document.
     * @param request The incoming request.
     * @return An <code>Element</code> representing the root of the request document
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    private Element validateAndUnpackRequest(Request request) {
        final String requestUriModel = request.queryParams("model");
        final String requestUriModule = request.queryParams("module");
        final String requestUriMethod = request.queryParams("method");

        LOG.info("Request received: " + requestUriModel + " (" + requestUriModule + "." + requestUriMethod + ")");

        // 1) validate the model is supported
        if (!SUPPORTED_MODELS.contains(com.buttongames.butterfly.util.StringUtils.getSanitizedModel(requestUriModel))) {
            LOG.warn("Invalid model requested: " + requestUriModel);
            throw new InvalidRequestModelException();
        }

        // 2) validate the module is supported
        if (!SUPPORTED_MODULES.contains(requestUriModule)) {
            LOG.warn("Invalid module requested: " + requestUriModule);
            throw new InvalidRequestModuleException();
        }

        // 3) validate that the PCBID exists in the database
        final String encryptionKey = request.headers(CRYPT_KEY_HEADER);
        final String compressionScheme = request.headers(COMPRESSION_HEADER);
        byte[] reqBody = request.bodyAsBytes();

        // decrypt the request if it's encrypted
        if (!StringUtils.isBlank(encryptionKey)) {
            reqBody = Rc4.decrypt(reqBody, encryptionKey);
        }

        // decompress the request if it's compressed
        if (!StringUtils.isBlank(compressionScheme) &&
                compressionScheme.equals(LZ77_COMPRESSION)) {
            reqBody = Lz77.decompress(reqBody);
        }

        // convert the body to plaintext XML if it's binary XML
        Element rootNode = null;

        if (XmlUtils.isBinaryXML(reqBody)) {
            rootNode = XmlUtils.stringToXmlFile(PublicKt.kbinDecodeToString(reqBody));
        } else {
            rootNode = XmlUtils.byteArrayToXmlFile(reqBody);
        }

        // read the request body into an XML document
        if (rootNode == null ||
                !rootNode.getNodeName().equals("call")) {
            throw new InvalidRequestException();
        }

        final Element moduleNode = (Element) rootNode.getFirstChild();
        final String requestBodyModel = rootNode.getAttribute("model");
        final String requestBodyPcbId = rootNode.getAttribute("srcid");
        final String requestBodyModule = moduleNode.getNodeName();
        final String requestBodyMethod = moduleNode.getAttribute("method");

        // check if the PCB exists and is unbanned in the database
        Machine machine = this.machineDao.findByPcbId(requestBodyPcbId);

        if (machine == null) {
            // create a machine for now, change this later to ban by default
            final LocalDateTime now = LocalDateTime.now();
            final ButterflyUser newUser = new ButterflyUser("0000", now, now, 10000);
            userDao.create(newUser);

            machine = new Machine(newUser, requestBodyPcbId, LocalDateTime.now(), true, 0);
            machineDao.create(machine);
        }

        // 4) validate that the request URI matches the request body
        if (StringUtils.isBlank(requestBodyModel) ||
                StringUtils.isBlank(requestBodyModule) ||
                StringUtils.isBlank(requestBodyMethod) ||
                !requestBodyModel.equals(requestUriModel) ||
                !requestBodyModule.equals(requestUriModule) ||
                !requestBodyMethod.equals(requestUriMethod)) {
            throw new MismatchedRequestUriException();
        }

        // set the model, pcbid, module, and method as request "attributes" so they can be
        // used by the request handlers if needed
        request.attribute("model", requestBodyModel);
        request.attribute("pcbid", requestBodyPcbId);
        request.attribute("module", requestBodyModule);
        request.attribute("method", requestBodyMethod);

        // 5) return the node corresponding to the actual call
        return moduleNode;
    }
}
