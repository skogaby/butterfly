package com.buttongames.butterflyserver.http.handlers;

import com.buttongames.butterflycore.encryption.Rc4;
import com.buttongames.butterflycore.xml.XmlUtils;
import com.buttongames.butterflycore.xml.kbinxml.PublicKt;
import com.google.common.net.MediaType;
import com.jamesmurty.utils.BaseXMLBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.buttongames.butterflycore.util.Constants.COMPRESSION_HEADER;
import static com.buttongames.butterflycore.util.Constants.CRYPT_KEY_HEADER;

/**
 * Base request handler that the others inherit from.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public abstract class BaseRequestHandler {

    private final Logger LOG = LogManager.getLogger(BaseRequestHandler.class);

    /**
     * Handles an incoming request for the given module.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    public abstract Object handleRequest(final Element requestBody, final Request request, final Response response);

    /**
     * Sends the response to the client.
     * @param request The original request.
     * @param response The response object we can use to send the data.
     * @param respBody The XML document of the response.
     * @return A response object for Spark
     */
    protected Object sendResponse(final Request request, final Response response, final BaseXMLBuilder respBody) {
        // get the bytes of the XML document
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            final DOMSource source = new DOMSource(respBody.getDocument());
            final StreamResult result = new StreamResult(bos);
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
            return 500;
        }

        byte[] respBytes = bos.toByteArray();
        return this.sendBytesToClient(respBytes, request, response);
    }

    /**
     * Takes the raw, unencrypted and decompressed bytes, transforms them as necessary, and sends
     * them to the client.
     * @param respBytes The bytes to send.
     * @param request The original request.
     * @param response The response object we can use to send the data.
     * @return A response object for Spark
     */
    protected Object sendBytesToClient(byte[] respBytes, final Request request, final Response response) {
        response.header("Connection", "keep-alive");

        // convert them to binary XML
        if (!XmlUtils.isBinaryXML(respBytes)) {
            respBytes = PublicKt.kbinEncode(new String(respBytes), "UTF-8");
        }

        response.header(COMPRESSION_HEADER, "none");

        // encrypt if needed
        final String encryptionKey = request.headers(CRYPT_KEY_HEADER);

        if (!StringUtils.isBlank(encryptionKey)) {
            respBytes = Rc4.encrypt(respBytes, encryptionKey);
            response.header(CRYPT_KEY_HEADER, encryptionKey);
        }

        // send to the client
        try {
            final HttpServletResponse rawResponse = response.raw();
            response.type(MediaType.OCTET_STREAM.toString());

            rawResponse.getOutputStream().write(respBytes);
            rawResponse.getOutputStream().flush();
            rawResponse.getOutputStream().close();

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

            LOG.info("Response sent: " + requestUriModel + " (" + requestUriModule + "." + requestUriMethod + ")");

            return 200;
        } catch (IOException e) {
            e.printStackTrace();
            return 500;
        }
    }
}
