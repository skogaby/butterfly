package com.buttongames.butterflycore.xml.kbinxml;

/**
 * CONTEXT: I (skogaby) wanted to extend XMLBuilder2 from java-xmlbuilder to provide convenience
 * methods for writing typed nodes to the document (i.e. with a __type attribute) to help with
 * converting to binary XML. However, XMLBuilder2 is a final class, so, here we are.
 */

/*
 * Copyright 2008-2017 James Murty (www.jamesmurty.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * This code is available from the GitHub code repository at:
 * https://github.com/jmurty/java-xmlbuilder
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.Properties;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import com.jamesmurty.utils.BaseXMLBuilder;
import com.jamesmurty.utils.XMLBuilderRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML Builder is a utility that creates simple XML documents using relatively
 * sparse Java code. It is intended to allow for quick and painless creation of
 * XML documents where you might otherwise be tempted to use concatenated
 * strings, rather than face the tedium and verbosity of coding with
 * JAXP (http://jaxp.dev.java.net/).
 * <p>
 * Internally, XML Builder uses JAXP to build a standard W3C
 * {@link org.w3c.dom.Document} model (DOM) that you can easily export as a
 * string, or access and manipulate further if you have special requirements.
 * </p>
 * <p>
 * The KXmlBuilder class serves as a wrapper of {@link org.w3c.dom.Element} nodes,
 * and provides a number of utility methods that make it simple to
 * manipulate the underlying element and the document to which it belongs.
 * In essence, this class performs dual roles: it represents a specific XML
 * node, and also allows manipulation of the entire underlying XML document.
 * The platform's default {@link DocumentBuilderFactory} and
 * {@link DocumentBuilder} classes are used to build the document.
 * </p>
 * <p>
 * KXmlBuilder has an feature set to the original XMLBuilder, but only ever
 * throws runtime exceptions (as opposed to checked exceptions). Any internal
 * checked exceptions are caught and wrapped in an
 * {@link XMLBuilderRuntimeException} object.
 * </p>
 *
 * @author James Murty
 */
public final class KXmlBuilder extends BaseXMLBuilder {

    /**
     * Construct a new builder object that wraps the given XML document.
     * This constructor is for internal use only.
     *
     * @param xmlDocument
     * an XML document that the builder will manage and manipulate.
     */
    protected KXmlBuilder(Document xmlDocument) {
        super(xmlDocument);
    }

    /**
     * Construct a new builder object that wraps the given XML document and node.
     * This constructor is for internal use only.
     *
     * @param myNode
     * the XML node that this builder node will wrap. This node may
     * be part of the XML document, or it may be a new element that is to be
     * added to the document.
     * @param parentNode
     * If not null, the given myElement will be appended as child node of the
     * parentNode node.
     */
    protected KXmlBuilder(Node myNode, Node parentNode) {
        super(myNode, parentNode);
    }

    private static RuntimeException wrapExceptionAsRuntimeException(Exception e) {
        // Don't wrap (or re-wrap) runtime exceptions.
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new XMLBuilderRuntimeException(e);
        }
    }

    /**
     * Construct a builder for new XML document with a default namespace.
     * The document will be created with the given root element, and the builder
     * returned by this method will serve as the starting-point for any further
     * document additions.
     *
     * @param name
     * the name of the document's root element.
     * @param namespaceURI
     * default namespace URI for document, ignored if null or empty.
     * @param enableExternalEntities
     * enable external entities; beware of XML External Entity (XXE) injection.
     * @param isNamespaceAware
     * enable or disable namespace awareness in the underlying
     * {@link DocumentBuilderFactory}
     * @return
     * a builder node that can be used to add more nodes to the XML document.
     * @throws XMLBuilderRuntimeException
     * to wrap {@link ParserConfigurationException}
     */
    public static KXmlBuilder create(
            String name, String namespaceURI, boolean enableExternalEntities,
            boolean isNamespaceAware)
    {
        try {
            return new KXmlBuilder(
                    createDocumentImpl(
                            name, namespaceURI, enableExternalEntities, isNamespaceAware));
        } catch (ParserConfigurationException e) {
            throw wrapExceptionAsRuntimeException(e);
        }
    }

    /**
     * Construct a builder for new XML document. The document will be created
     * with the given root element, and the builder returned by this method
     * will serve as the starting-point for any further document additions.
     *
     * @param name
     * the name of the document's root element.
     * @param enableExternalEntities
     * enable external entities; beware of XML External Entity (XXE) injection.
     * @param isNamespaceAware
     * enable or disable namespace awareness in the underlying
     * {@link DocumentBuilderFactory}
     * @return
     * a builder node that can be used to add more nodes to the XML document.
     * @throws XMLBuilderRuntimeException
     * to wrap {@link ParserConfigurationException}
     */
    public static KXmlBuilder create(String name,
                                     boolean enableExternalEntities, boolean isNamespaceAware)
    {
        return KXmlBuilder.create(
                name, null, enableExternalEntities, isNamespaceAware);
    }

    /**
     * Construct a builder for new XML document with a default namespace.
     * The document will be created with the given root element, and the builder
     * returned by this method will serve as the starting-point for any further
     * document additions.
     *
     * @param name
     * the name of the document's root element.
     * @param namespaceURI
     * default namespace URI for document, ignored if null or empty.
     * @return
     * a builder node that can be used to add more nodes to the XML document.
     * @throws XMLBuilderRuntimeException
     * to wrap {@link ParserConfigurationException}
     */
    public static KXmlBuilder create(String name, String namespaceURI)
    {
        return KXmlBuilder.create(name, namespaceURI, false, true);
    }

    /**
     * Construct a builder for new XML document. The document will be created
     * with the given root element, and the builder returned by this method
     * will serve as the starting-point for any further document additions.
     *
     * @param name
     * the name of the document's root element.
     * @return
     * a builder node that can be used to add more nodes to the XML document.
     * @throws XMLBuilderRuntimeException
     * to wrap {@link ParserConfigurationException}
     */
    public static KXmlBuilder create(String name)
    {
        return KXmlBuilder.create(name, null, false, true);
    }

    /**
     * Construct a builder from an existing XML document. The provided XML
     * document will be parsed and an KXmlBuilder object referencing the
     * document's root element will be returned.
     *
     * @param inputSource
     * an XML document input source that will be parsed into a DOM.
     * @param enableExternalEntities
     * enable external entities; beware of XML External Entity (XXE) injection.
     * @param isNamespaceAware
     * enable or disable namespace awareness in the underlying
     * {@link DocumentBuilderFactory}
     * @return
     * a builder node that can be used to add more nodes to the XML document.
     * @throws XMLBuilderRuntimeException
     * to wrap {@link ParserConfigurationException}, {@link SAXException},
     * {@link IOException}
     */
    public static KXmlBuilder parse(
            InputSource inputSource, boolean enableExternalEntities,
            boolean isNamespaceAware)
    {
        try {
            return new KXmlBuilder(
                    parseDocumentImpl(
                            inputSource, enableExternalEntities, isNamespaceAware));
        } catch (ParserConfigurationException e) {
            throw wrapExceptionAsRuntimeException(e);
        } catch (SAXException e) {
            throw wrapExceptionAsRuntimeException(e);
        } catch (IOException e) {
            throw wrapExceptionAsRuntimeException(e);
        }
    }

    /**
     * Construct a builder from an existing XML document string.
     * The provided XML document will be parsed and an KXmlBuilder
     * object referencing the document's root element will be returned.
     *
     * @param xmlString
     * an XML document string that will be parsed into a DOM.
     * @param enableExternalEntities
     * enable external entities; beware of XML External Entity (XXE) injection.
     * @param isNamespaceAware
     * enable or disable namespace awareness in the underlying
     * {@link DocumentBuilderFactory}
     * @return
     * a builder node that can be used to add more nodes to the XML document.
     */
    public static KXmlBuilder parse(
            String xmlString, boolean enableExternalEntities, boolean isNamespaceAware)
    {
        return KXmlBuilder.parse(
                new InputSource(new StringReader(xmlString)),
                enableExternalEntities,
                isNamespaceAware);
    }

    /**
     * Construct a builder from an existing XML document file.
     * The provided XML document will be parsed and an KXmlBuilder
     * object referencing the document's root element will be returned.
     *
     * @param xmlFile
     * an XML document file that will be parsed into a DOM.
     * @param enableExternalEntities
     * enable external entities; beware of XML External Entity (XXE) injection.
     * @param isNamespaceAware
     * enable or disable namespace awareness in the underlying
     * {@link DocumentBuilderFactory}
     * @return
     * a builder node that can be used to add more nodes to the XML document.
     * @throws XMLBuilderRuntimeException
     * to wrap {@link ParserConfigurationException}, {@link SAXException},
     * {@link IOException}, {@link FileNotFoundException}
     */
    public static KXmlBuilder parse(File xmlFile, boolean enableExternalEntities,
                                    boolean isNamespaceAware)
    {
        try {
            return KXmlBuilder.parse(
                    new InputSource(new FileReader(xmlFile)),
                    enableExternalEntities,
                    isNamespaceAware);
        } catch (FileNotFoundException e) {
            throw wrapExceptionAsRuntimeException(e);
        }
    }

    /**
     * Construct a builder from an existing XML document. The provided XML
     * document will be parsed and an KXmlBuilder object referencing the
     * document's root element will be returned.
     *
     * @param inputSource
     * an XML document input source that will be parsed into a DOM.
     * @return
     * a builder node that can be used to add more nodes to the XML document.
     * @throws XMLBuilderRuntimeException
     * to wrap {@link ParserConfigurationException}, {@link SAXException},
     * {@link IOException}
     */
    public static KXmlBuilder parse(InputSource inputSource)
    {
        return KXmlBuilder.parse(inputSource, false, true);
    }

    /**
     * Construct a builder from an existing XML document string.
     * The provided XML document will be parsed and an KXmlBuilder
     * object referencing the document's root element will be returned.
     *
     * @param xmlString
     * an XML document string that will be parsed into a DOM.
     * @return
     * a builder node that can be used to add more nodes to the XML document.
     */
    public static KXmlBuilder parse(String xmlString)
    {
        return KXmlBuilder.parse(xmlString, false, true);
    }

    /**
     * Construct a builder from an existing XML document file.
     * The provided XML document will be parsed and an KXmlBuilder
     * object referencing the document's root element will be returned.
     *
     * @param xmlFile
     * an XML document file that will be parsed into a DOM.
     * @return
     * a builder node that can be used to add more nodes to the XML document.
     * @throws XMLBuilderRuntimeException
     * to wrap {@link ParserConfigurationException}, {@link SAXException},
     * {@link IOException}, {@link FileNotFoundException}
     */
    public static KXmlBuilder parse(File xmlFile)
    {
        return KXmlBuilder.parse(xmlFile, false, true);
    }

    /**
     * @throws XMLBuilderRuntimeException
     * to wrap {@link XPathExpressionException}
     */
    @Override
    public KXmlBuilder stripWhitespaceOnlyTextNodes()
    {
        try {
            super.stripWhitespaceOnlyTextNodesImpl();
            return this;
        } catch (XPathExpressionException e) {
            throw wrapExceptionAsRuntimeException(e);
        }
    }

    @Override
    public KXmlBuilder importXMLBuilder(BaseXMLBuilder builder) {
        super.importXMLBuilderImpl(builder);
        return this;
    }

    @Override
    public KXmlBuilder root() {
        return new KXmlBuilder(getDocument());
    }

    /**
     * @throws XMLBuilderRuntimeException
     * to wrap {@link XPathExpressionException}
     */
    @Override
    public KXmlBuilder xpathFind(String xpath, NamespaceContext nsContext)
    {
        try {
            Node foundNode = super.xpathFindImpl(xpath, nsContext);
            return new KXmlBuilder(foundNode, null);
        } catch (XPathExpressionException e) {
            throw wrapExceptionAsRuntimeException(e);
        }
    }

    @Override
    public KXmlBuilder xpathFind(String xpath) {
        return xpathFind(xpath, null);
    }

    @Override
    public KXmlBuilder element(String name) {
        String namespaceURI = super.lookupNamespaceURIImpl(name);
        return element(name, namespaceURI);
    }

    @Override
    public KXmlBuilder elem(String name) {
        return element(name);
    }

    @Override
    public KXmlBuilder e(String name) {
        return element(name);
    }

    @Override
    public KXmlBuilder element(String name, String namespaceURI) {
        Element elem = super.elementImpl(name, namespaceURI);
        return new KXmlBuilder(elem, this.getElement());
    }

    @Override
    public KXmlBuilder elementBefore(String name) {
        Element newElement = super.elementBeforeImpl(name);
        return new KXmlBuilder(newElement, null);
    }

    @Override
    public KXmlBuilder elementBefore(String name, String namespaceURI) {
        Element newElement = super.elementBeforeImpl(name, namespaceURI);
        return new KXmlBuilder(newElement, null);
    }

    @Override
    public KXmlBuilder attribute(String name, String value) {
        super.attributeImpl(name, value);
        return this;
    }

    @Override
    public KXmlBuilder attr(String name, String value) {
        return attribute(name, value);
    }

    @Override
    public KXmlBuilder a(String name, String value) {
        return attribute(name, value);
    }


    @Override
    public KXmlBuilder text(String value, boolean replaceText) {
        super.textImpl(value, replaceText);
        return this;
    }

    @Override
    public KXmlBuilder text(String value) {
        return this.text(value, false);
    }

    @Override
    public KXmlBuilder t(String value) {
        return text(value);
    }

    @Override
    public KXmlBuilder cdata(String data) {
        super.cdataImpl(data);
        return this;
    }

    @Override
    public KXmlBuilder data(String data) {
        return cdata(data);
    }

    @Override
    public KXmlBuilder d(String data) {
        return cdata(data);
    }

    @Override
    public KXmlBuilder cdata(byte[] data) {
        super.cdataImpl(data);
        return this;
    }

    @Override
    public KXmlBuilder data(byte[] data) {
        return cdata(data);
    }

    @Override
    public KXmlBuilder d(byte[] data) {
        return cdata(data);
    }

    @Override
    public KXmlBuilder comment(String comment) {
        super.commentImpl(comment);
        return this;
    }

    @Override
    public KXmlBuilder cmnt(String comment) {
        return comment(comment);
    }

    @Override
    public KXmlBuilder c(String comment) {
        return comment(comment);
    }

    @Override
    public KXmlBuilder instruction(String target, String data) {
        super.instructionImpl(target, data);
        return this;
    }

    @Override
    public KXmlBuilder inst(String target, String data) {
        return instruction(target, data);
    }

    @Override
    public KXmlBuilder i(String target, String data) {
        return instruction(target, data);
    }

    @Override
    public KXmlBuilder insertInstruction(String target, String data) {
        super.insertInstructionImpl(target, data);
        return this;
    }

    @Override
    public KXmlBuilder reference(String name) {
        super.referenceImpl(name);
        return this;
    }

    @Override
    public KXmlBuilder ref(String name) {
        return reference(name);
    }

    @Override
    public KXmlBuilder r(String name) {
        return reference(name);
    }

    @Override
    public KXmlBuilder namespace(String prefix, String namespaceURI) {
        super.namespaceImpl(prefix, namespaceURI);
        return this;
    }

    @Override
    public KXmlBuilder ns(String prefix, String namespaceURI) {
        return namespace(prefix, namespaceURI);
    }

    @Override
    public KXmlBuilder namespace(String namespaceURI) {
        this.namespace(null, namespaceURI);
        return this;
    }

    @Override
    public KXmlBuilder ns(String namespaceURI) {
        return namespace(namespaceURI);
    }

    @Override
    public KXmlBuilder up(int steps) {
        Node currNode = super.upImpl(steps);
        if (currNode instanceof Document) {
            return new KXmlBuilder((Document) currNode);
        } else {
            return new KXmlBuilder(currNode, null);
        }
    }

    @Override
    public KXmlBuilder up() {
        return up(1);
    }

    @Override
    public KXmlBuilder document() {
        return new KXmlBuilder(getDocument(), null);
    }

    /**
     * @throws XMLBuilderRuntimeException
     * to wrap {@link TransformerException}
     *
     */
    @Override
    public String asString() {
        try {
            return super.asString();
        } catch (TransformerException e) {
            throw wrapExceptionAsRuntimeException(e);
        }
    }

    /**
     * @throws XMLBuilderRuntimeException
     * to wrap {@link TransformerException}
     *
     */
    @Override
    public String asString(Properties properties) {
        try {
            return super.asString(properties);
        } catch (TransformerException e) {
            throw wrapExceptionAsRuntimeException(e);
        }
    }

    /**
     * @throws XMLBuilderRuntimeException
     * to wrap {@link TransformerException}
     *
     */
    @Override
    public String elementAsString() {
        try {
            return super.elementAsString();
        } catch (TransformerException e) {
            throw wrapExceptionAsRuntimeException(e);
        }
    }

    /**
     * @throws XMLBuilderRuntimeException
     * to wrap {@link TransformerException}
     *
     */
    @Override
    public String elementAsString(Properties outputProperties) {
        try {
            return super.elementAsString(outputProperties);
        } catch (TransformerException e) {
            throw wrapExceptionAsRuntimeException(e);
        }
    }

    /**
     * @throws XMLBuilderRuntimeException
     * to wrap {@link TransformerException}
     *
     */
    @Override
    public void toWriter(boolean wholeDocument, Writer writer, Properties outputProperties)
    {
        try {
            super.toWriter(wholeDocument, writer, outputProperties);
        } catch (TransformerException e) {
            throw wrapExceptionAsRuntimeException(e);
        }
    }

    /**
     * @throws XMLBuilderRuntimeException
     * to wrap {@link TransformerException}
     *
     */
    @Override
    public void toWriter(Writer writer, Properties outputProperties)
    {
        try {
            super.toWriter(writer, outputProperties);
        } catch (TransformerException e) {
            throw wrapExceptionAsRuntimeException(e);
        }
    }

    /**
     * @throws XMLBuilderRuntimeException
     * to wrap {@link XPathExpressionException}
     *
     */
    @Override
    public Object xpathQuery(String xpath, QName type, NamespaceContext nsContext)
    {
        try {
            return super.xpathQuery(xpath, type, nsContext);
        } catch (XPathExpressionException e) {
            throw wrapExceptionAsRuntimeException(e);
        }
    }

    /**
     * @throws XMLBuilderRuntimeException
     * to wrap {@link XPathExpressionException}
     *
     */
    @Override
    public Object xpathQuery(String xpath, QName type)
    {
        try {
            return super.xpathQuery(xpath, type);
        } catch (XPathExpressionException e) {
            throw wrapExceptionAsRuntimeException(e);
        }
    }

    public KXmlBuilder str(final String name, final String value) {
        return e(name).a("__type", "str").t(value);
    }

    public KXmlBuilder u8(final String name, final int value) {
        return e(name).a("__type", "u8").t(String.valueOf(value));
    }

    public KXmlBuilder u16(final String name, final int value) {
        return e(name).a("__type", "u16").t(String.valueOf(value));
    }

    public KXmlBuilder u32(final String name, final long value) {
        return e(name).a("__type", "u32").t(String.valueOf(value));
    }

    public KXmlBuilder u64(final String name, final long value) {
        return e(name).a("__type", "u64").t(String.valueOf(value));
    }

    public KXmlBuilder s8(final String name, final int value) {
        return e(name).a("__type", "s8").t(String.valueOf(value));
    }

    public KXmlBuilder s16(final String name, final int value) {
        return e(name).a("__type", "s16").t(String.valueOf(value));
    }

    public KXmlBuilder s32(final String name, final int value) {
        return e(name).a("__type", "s32").t(String.valueOf(value));
    }

    public KXmlBuilder s64(final String name, final long value) {
        return e(name).a("__type", "s64").t(String.valueOf(value));
    }

    public KXmlBuilder bool(final String name, final boolean value) {
        return e(name).a("__type", "bool").t(value ? "1" : "0");
    }

    public KXmlBuilder ip(final String name, final String value) {
        return e(name).a("__type", "ip4").t(value);
    }
}

