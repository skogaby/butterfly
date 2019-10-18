package com.buttongames.butterflyserver.http;

import com.buttongames.butterflycore.compression.Lz77;
import com.buttongames.butterflycore.encryption.Rc4;
import com.buttongames.butterflydao.hibernate.dao.impl.ButterflyUserDao;
import com.buttongames.butterflydao.hibernate.dao.impl.MachineDao;
import com.buttongames.butterflyserver.http.exception.CardCipherException;
import com.buttongames.butterflyserver.http.exception.InvalidPcbIdException;
import com.buttongames.butterflyserver.http.exception.InvalidRequestException;
import com.buttongames.butterflyserver.http.exception.InvalidRequestMethodException;
import com.buttongames.butterflyserver.http.exception.InvalidRequestModelException;
import com.buttongames.butterflyserver.http.exception.InvalidRequestModuleException;
import com.buttongames.butterflyserver.http.exception.MismatchedRequestUriException;
import com.buttongames.butterflyserver.http.exception.UnsupportedRequestException;
import com.buttongames.butterflyserver.http.handlers.impl.mdx.BaseMdxRequestHandler;
import com.buttongames.butterflymodel.model.ButterflyUser;
import com.buttongames.butterflymodel.model.Machine;
import com.buttongames.butterflyserver.util.PropertyNames;
import com.buttongames.butterflycore.xml.XmlUtils;
import com.buttongames.butterflycore.xml.kbinxml.PublicKt;
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

import static com.buttongames.butterflycore.util.Constants.COMPRESSION_HEADER;
import static com.buttongames.butterflycore.util.Constants.CRYPT_KEY_HEADER;
import static com.buttongames.butterflycore.util.Constants.LZ77_COMPRESSION;
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

    /** The port the server listens on */
    @Value(PropertyNames.PORT)
    private String port;

    /** Handler for requests for the <code>services</code> module. */
    private final BaseMdxRequestHandler baseMdxRequestHandler;

    /** DAO for interacting with <code>Machine</code> objects in the database. */
    private final MachineDao machineDao;

    /** DAO for interacting with <code>ButterflyUser</code> objects in the database. */
    private final ButterflyUserDao userDao;

    /**
     * Constructor.
     */
    @Autowired
    public ButterflyHttpServer(final BaseMdxRequestHandler baseMdxRequestHandler,
                               final MachineDao machineDao,
                               final ButterflyUserDao userDao) {
        this.baseMdxRequestHandler = baseMdxRequestHandler;
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
            final String requestModel = request.attribute("model");

            // handle requests for DDR
            if (requestModel.startsWith("MDX")) {
                return this.baseMdxRequestHandler.handleRequest(requestBody, request, response);
            } else {
                throw new InvalidRequestModelException();
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

        // validate that the PCBID exists in the database
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
            // create a machine for auditing purposes, and ban them
            final LocalDateTime now = LocalDateTime.now();
            final ButterflyUser newUser = new ButterflyUser("0000", now, now, 10000);
            userDao.create(newUser);

            machine = new Machine(newUser, requestBodyPcbId, LocalDateTime.now(), false, 0);
            machineDao.create(machine);

            throw new InvalidPcbIdException();
        } else if (!machine.isEnabled()) {
            throw new InvalidPcbIdException();
        }

        // validate that the request URI matches the request body
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

        // return the node corresponding to the actual call
        return moduleNode;
    }
}
