package com.buttongames.butterflyserver.http;

import com.buttongames.butterflycore.compression.Lz77;
import com.buttongames.butterflycore.encryption.Rc4;
import com.buttongames.butterflycore.xml.XmlUtils;
import com.buttongames.butterflycore.xml.kbinxml.PublicKt;
import com.buttongames.butterflydao.hibernate.dao.impl.ButterflyUserDao;
import com.buttongames.butterflydao.hibernate.dao.impl.MachineDao;
import com.buttongames.butterflymodel.model.ButterflyUser;
import com.buttongames.butterflymodel.model.Machine;
import com.buttongames.butterflymodel.model.SupportedGames;
import com.buttongames.butterflyserver.graphql.GraphQLRequest;
import com.buttongames.butterflyserver.graphql.types.ButterflyQuery;
import com.buttongames.butterflyserver.graphql.types.ddr16.Ddr16Query;
import com.buttongames.butterflyserver.http.exception.CardCipherException;
import com.buttongames.butterflyserver.http.exception.InvalidPcbIdException;
import com.buttongames.butterflyserver.http.exception.InvalidRequestException;
import com.buttongames.butterflyserver.http.exception.InvalidRequestMethodException;
import com.buttongames.butterflyserver.http.exception.InvalidRequestModelException;
import com.buttongames.butterflyserver.http.exception.InvalidRequestModuleException;
import com.buttongames.butterflyserver.http.exception.MismatchedRequestUriException;
import com.buttongames.butterflyserver.http.exception.UnsupportedRequestException;
import com.buttongames.butterflyserver.http.handlers.impl.mdx.ddr16.BaseDdr16RequestHandler;
import com.buttongames.butterflyserver.util.PropertyNames;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.InvalidSyntaxError;
import graphql.schema.GraphQLSchema;
import graphql.validation.ValidationError;
import io.leangen.graphql.GraphQLSchemaGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import spark.Request;
import spark.Spark;
import spark.utils.StringUtils;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.buttongames.butterflycore.util.Constants.COMPRESSION_HEADER;
import static com.buttongames.butterflycore.util.Constants.CRYPT_KEY_HEADER;
import static com.buttongames.butterflycore.util.Constants.LZ77_COMPRESSION;
import static spark.Spark.before;
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

    /** Handler for requests for DDR Ace. */
    private final BaseDdr16RequestHandler baseDdr16RequestHandler;

    /** DAO for interacting with <code>Machine</code> objects in the database. */
    private final MachineDao machineDao;

    /** DAO for interacting with <code>ButterflyUser</code> objects in the database. */
    private final ButterflyUserDao userDao;

    /** GraphQL query object for server-level entities. */
    private final ButterflyQuery butterflyQuery;

    /** GraphQL query object for game-level entities for DDR16. */
    private final Ddr16Query ddr16Query;

    /**
     * Constructor.
     */
    @Autowired
    public ButterflyHttpServer(final BaseDdr16RequestHandler baseDdr16RequestHandler,
                               final MachineDao machineDao,
                               final ButterflyUserDao userDao,
                               final ButterflyQuery butterflyQuery,
                               final Ddr16Query ddr16Query) {
        this.baseDdr16RequestHandler = baseDdr16RequestHandler;
        this.machineDao = machineDao;
        this.userDao = userDao;
        this.butterflyQuery = butterflyQuery;
        this.ddr16Query = ddr16Query;
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
        // enable a static file location, for graphiql
        Spark.staticFileLocation("/static");

        // CORS header, for the GraphQL APIs
        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        // configure our root route; its handler will parse the request and go from there
        post("/", ((request, response) -> {
            // send the request to the right module handler
            final Element requestBody = validateAndUnpackRequest(request);
            final SupportedGames gameModel = SupportedGames.fromModel(request.attribute("model"));

            switch (gameModel) {
                case DDR_A_A20:
                    return this.baseDdr16RequestHandler.handleRequest(requestBody, request, response);
                default:
                    throw new InvalidRequestModelException();
            }
        }));

        // configure the graphql handler
        final GraphQLSchema graphQLSchema = this.graphQLServerSetupSchema();
        final ObjectMapper mapper = new ObjectMapper();

        post("/graphql", (request, response) -> {
            final GraphQLRequest graphQLRequest = mapper.readValue(request.body(), GraphQLRequest.class);
            final ExecutionResult executionResult = new GraphQL.Builder(graphQLSchema)
                    .build()
                    .execute(graphQLRequest.getQuery(), graphQLRequest.getOperationName(), (Object) null);
            response.type("application/json");
            return mapper.writeValueAsString(this.createResultFromDataAndErrors(executionResult.getData(), executionResult.getErrors()));
        });

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
                    LOG.info(String.format("RECEIVED AN UNSUPPORTED REQUEST MODULE: %s.%s",
                            request.attribute("module"), request.attribute("method")));
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
        final String requestUriModule;
        final String requestUriMethod;

        if(request.queryParams("module") != null){
            requestUriModule = request.queryParams("module");
            requestUriMethod = request.queryParams("method");
        } else {
            String[] moduleMethod = request.queryParams("f").split("\\.");
            requestUriModule = moduleMethod[0];
            requestUriMethod = moduleMethod[1];
        }

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

    /**
     * Setups up the GraphQL schema.
     * @return
     */
    private GraphQLSchema graphQLServerSetupSchema() {
        return new GraphQLSchemaGenerator()
                .withOperationsFromSingletons(this.butterflyQuery, this.ddr16Query)
                .generate();
    }

    /**
     * Creates a GraphQL result from the data and the errors.
     * @param data
     * @param errors
     * @return
     */
    private Map<String, Object> createResultFromDataAndErrors(final Object data, final List<GraphQLError> errors) {
        final Map<String, Object> result = new HashMap<>();
        result.put("data", data);

        if (errors != null &&
                !errors.isEmpty()) {
            final List<GraphQLError> clientErrors = filterGraphQLErrors(errors);

            if (clientErrors.size() < errors.size()) {
                errors.stream()
                        .filter(error -> !isClientError(error))
                        .forEach(error -> LOG.error("Error executing query ({}): {}", error.getClass().getSimpleName(), error.getMessage()));
            }

            result.put("errors", clientErrors);
        }

        return result;
    }

    /**
     * Says whether or not a GraphQL error is a client error.
     * @param error
     * @return
     */
    private boolean isClientError(final GraphQLError error) {
        return ((error instanceof InvalidSyntaxError) ||
                (error instanceof ValidationError));
    }

    /**
     * Filter a list of errors for only the client errors.
     * @param errors
     * @return
     */
    private List<GraphQLError> filterGraphQLErrors(final List<GraphQLError> errors) {
        return errors.stream()
                .filter(this::isClientError)
                .collect(Collectors.toList());
    }
}
