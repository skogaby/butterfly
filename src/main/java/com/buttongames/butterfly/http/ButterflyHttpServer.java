package com.buttongames.butterfly.http;

import com.buttongames.butterfly.compression.Lz77;
import com.buttongames.butterfly.encryption.Rc4;
import com.buttongames.butterfly.http.exception.InvalidRequestException;
import com.buttongames.butterfly.http.exception.InvalidRequestMethodException;
import com.buttongames.butterfly.http.exception.InvalidRequestModelException;
import com.buttongames.butterfly.http.exception.InvalidRequestModuleException;
import com.buttongames.butterfly.http.exception.MismatchedRequestUriException;
import com.buttongames.butterfly.http.handlers.ServicesRequestHandler;
import com.buttongames.butterfly.xml.BinaryXmlUtils;
import com.buttongames.butterfly.xml.XmlUtils;
import com.google.common.collect.ImmutableSet;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import spark.Request;
import spark.utils.StringUtils;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static com.buttongames.butterfly.util.Constants.COMPRESSION_HEADER;
import static com.buttongames.butterfly.util.Constants.CRYPT_KEY_HEADER;
import static com.buttongames.butterfly.util.Constants.LZ77_COMPRESSION;
import static spark.Spark.exception;
import static spark.Spark.halt;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.stop;
import static spark.Spark.threadPool;

/**
 * The main HTTP server. This class is responsible for the top-level handling of incoming
 * requests, then delegates the responsibility to the appropriate handler.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class ButterflyHttpServer {

    /**
     * Static set of all the models this server supports.
     */
    private static final ImmutableSet<String> SUPPORTED_MODELS;

    /**
     * Static set of all the modules this server supports.
     */
    private static final ImmutableSet<String> SUPPORTED_MODULES;

    // Do a static setup of our supported models, modules, etc.
    // TODO: Make this not hardcoded
    static {
        SUPPORTED_MODELS = ImmutableSet.of("MDX:J:A:A:2018042300");
        SUPPORTED_MODULES = ImmutableSet.of("services");
    }

    /**
     * Handler for requests for the <code>services<code> module.
     */
    private ServicesRequestHandler servicesRequestHandler;

    /**
     * Constructor.
     */
    public ButterflyHttpServer() {
        this.servicesRequestHandler = new ServicesRequestHandler();
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
        port(80);
        this.configureRoutesAndExceptions();
    }

    public void stopServer() {
        stop();
    }

    /**
     * Configures the routes on the server, and the exception handlers.
     * TODO: Remove all the hardcoded stuff.
     */
    private void configureRoutesAndExceptions() {
        // configure our root route; its handler will parse the request and go from there
        post("/", ((request, response) -> {
            // send the request to the right module handler
            final Element requestBody = validateAndUnpackRequest(request);
            final String requestModule = request.queryParams("module");

            if (requestModule.equals("services")) {
                return this.servicesRequestHandler.handleRequest(requestBody, request, response);
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
                    response.body("Request URI does not match request body");
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

        System.out.println("Got a request for " +
                requestUriModule + "." + requestUriMethod);

        // 1) validate the model is supported
        if (!SUPPORTED_MODELS.contains(requestUriModel)) {
            throw new InvalidRequestModelException();
        }

        // 2) validate the module is supported
        if (!SUPPORTED_MODULES.contains(requestUriModule)) {
            throw new InvalidRequestModuleException();
        }

        // 3) validate that the request URI matches the request body;
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
        if (BinaryXmlUtils.isBinaryXML(reqBody)) {
            reqBody = BinaryXmlUtils.binaryToXml(reqBody);
        }

        // read the request body into an XML document and check its properties
        // to verify it matches the request URI
        Element rootNode = XmlUtils.byteArrayToXmlFile(reqBody);

        if (rootNode == null ||
                !rootNode.getNodeName().equals("call")) {
            throw new InvalidRequestException();
        }

        final Element moduleNode = (Element) rootNode.getFirstChild();
        final String requestBodyModel = rootNode.getAttribute("model");
        final String requestBodyModule = moduleNode.getNodeName();
        final String requestBodyMethod = moduleNode.getAttribute("method");

        if (StringUtils.isBlank(requestBodyModel) ||
                StringUtils.isBlank(requestBodyModule) ||
                StringUtils.isBlank(requestBodyMethod) ||
                !requestBodyModel.equals(requestUriModel) ||
                !requestBodyModule.equals(requestUriModule) ||
                !requestBodyMethod.equals(requestUriMethod)) {
            throw new MismatchedRequestUriException();
        }

        // TODO: Verify the PCBID

        // 4) return the XML document
        return rootNode;
    }
}
