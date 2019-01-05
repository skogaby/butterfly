package com.buttongames.butterfly.http.handlers;

import com.buttongames.butterfly.compression.Lz77;
import com.buttongames.butterfly.encryption.Rc4;
import com.buttongames.butterfly.xml.BinaryXmlUtils;
import com.google.common.net.MediaType;
import com.jamesmurty.utils.XMLBuilder2;
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

import static com.buttongames.butterfly.util.Constants.COMPRESSION_HEADER;
import static com.buttongames.butterfly.util.Constants.CRYPT_KEY_HEADER;
import static com.buttongames.butterfly.util.Constants.LZ77_COMPRESSION;

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

    protected Object sendResponse(final Request request, final Response response, final XMLBuilder2 respBody) {
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

        // convert them to binary XML
        if (!BinaryXmlUtils.isBinaryXML(respBytes)) {
            respBytes = BinaryXmlUtils.xmlToBinary(respBytes);
        }

        // compress if needed
        final String compressionScheme = request.headers(COMPRESSION_HEADER);

        if (!StringUtils.isBlank(compressionScheme) &&
                compressionScheme.equals(LZ77_COMPRESSION)) {
            respBytes = Lz77.compress(respBytes);
            response.header(COMPRESSION_HEADER, LZ77_COMPRESSION);
        } else {
            response.header(COMPRESSION_HEADER, "none");
        }

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
            rawResponse.setContentLength(respBytes.length);

            rawResponse.getOutputStream().write(respBytes);
            rawResponse.getOutputStream().flush();
            rawResponse.getOutputStream().close();

            LOG.info("Response sent: '" + request.queryParams("model") + "::" +
                    request.queryParams("module") + "." + request.queryParams("method") + "'");

            return 200;
        } catch (IOException e) {
            e.printStackTrace();
            return 500;
        }
    }
}
