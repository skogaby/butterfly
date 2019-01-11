package com.buttongames.butterfly.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Class with helper methods for manipulating XML files.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class XmlUtils {

    private static final XPath XPATH = XPathFactory.newInstance().newXPath();

    /**
     * Scrubs empty nodes from a document so we don't accidentally read them.
     * @param node The root node of the document to clean.
     */
    public static void clean(final Node node) {
        final NodeList childrem = node.getChildNodes();

        for (int n = childrem.getLength() - 1; n >= 0; n--) {
            final Node child = childrem.item(n);
            final short nodeType = child.getNodeType();

            if (nodeType == Node.ELEMENT_NODE) {
                clean(child);
            } else if (nodeType == Node.TEXT_NODE) {
                final String trimmedNodeVal = child.getNodeValue().trim();

                if (trimmedNodeVal.length() == 0) {
                    node.removeChild(child);
                } else {
                    child.setNodeValue(trimmedNodeVal);
                }
            } else if (nodeType == Node.COMMENT_NODE) {
                node.removeChild(child);
            }
        }
    }
    /**
     * Reads the given byte[] into an Element that represents the root node of the XML body.
     * @param body
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Element byteArrayToXmlFile(final byte[] body) {
        try {
            final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = builderFactory.newDocumentBuilder();
            final Document reqDocument = builder.parse(new ByteArrayInputStream(body));
            XmlUtils.clean(reqDocument);

            return reqDocument.getDocumentElement();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads the String value at the given XPath expression from the given document.
     * @param doc
     * @param path
     * @return
     */
    public static String strValueAtPath(final Element doc, final String path) {
        try {
            return (String) XPATH.compile("/" + path).evaluate(doc, XPathConstants.STRING);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Reads the boolean value at the given XPath expression from the given document.
     * @param doc
     * @param path
     * @return
     */
    public static Boolean boolValueAtPath(final Element doc, final String path) {
        try {
            return (Boolean) XPATH.compile("/" + path).evaluate(doc, XPathConstants.BOOLEAN);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads the double value at the given XPath expression from the given document.
     * @param doc
     * @param path
     * @return
     */
    public static Double doubleValueAtPath(final Element doc, final String path) {
        try {
            return (Double) XPATH.compile("/" + path).evaluate(doc, XPathConstants.NUMBER);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads the long value at the given XPath expression from the given document.
     * @param doc
     * @param path
     * @return
     */
    public static Long longValueAtPath(final Element doc, final String path) {
        try {
            return Long.parseLong((String) XPATH.compile("/" + path).evaluate(doc, XPathConstants.STRING));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads the integer value at the given XPath expression from the given document.
     * @param doc
     * @param path
     * @return
     */
    public static Integer intValueAtPath(final Element doc, final String path) {
        try {
            return ((Double) XPATH.compile("/" + path).evaluate(doc, XPathConstants.NUMBER)).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
