package ren.gavin.export.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ren.gavin.export.util.parser.GenericTokenParser;
import ren.gavin.export.util.parser.PropertyTokenHandler;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

public class XMLDomParser {

    private Document document;

    private XPath xPath;

    private GenericTokenParser tokenParser;


    public String evaluateString(String expression) {
        return evaluateString(document, expression);
    }

    public String evaluateString(Object node, String expression) {
        String evaluate = ((String) evaluate(node, expression, XPathConstants.STRING)).trim();
        return tokenParser == null ? evaluate : tokenParser.parser(evaluate);
    }

    public Node evaluateNode(String expression) {
        return evaluateNode(document, expression);
    }

    public Node evaluateNode(Object node, String expression) {
        return (Node) evaluate(node, expression, XPathConstants.NODE);
    }

    public NodeList evaluateNodes(String expression) {
        return evaluateNodes(document, expression);
    }

    public NodeList evaluateNodes(Object node, String expression) {
        return (NodeList) evaluate(node, expression, XPathConstants.NODESET);
    }

    public Object evaluate( Object node, String expression, QName qName) {
        try {
            return xPath.evaluate(expression, node, qName);
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Error evaluating XPath. Cause: " + e, e);
        }
    }


    private XMLDomParser(Builder builder) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(builder.validation);
            factory.setIgnoringElementContentWhitespace(builder.ignoringElementContentWhitespace);
            factory.setCoalescing(builder.coalescing);
            factory.setExpandEntityReferences(builder.expandEntityReferences);
            factory.setIgnoringComments(builder.ignoringComments);
            factory.setNamespaceAware(builder.namespaceAware);

            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            if (null != builder.entityResolver) {
                documentBuilder.setEntityResolver(builder.entityResolver);
            }
            if (null != builder.errorHandler) {
                documentBuilder.setErrorHandler(builder.errorHandler);
            }

            document = documentBuilder.parse(builder.resource);
            xPath = XPathFactory.newInstance().newXPath();
            if (null != builder.properties) {
                tokenParser = new GenericTokenParser("${", "}",
                        new PropertyTokenHandler(builder.properties));
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException("Error Initializing XMLDomParser. Cause: " + e, e);
        }
    }

    public static class Builder {

        private InputSource resource;

        private Properties properties;

        private boolean validation;

        private boolean ignoringElementContentWhitespace;

        private EntityResolver entityResolver;

        private ErrorHandler errorHandler;

        private boolean expandEntityReferences;

        private boolean ignoringComments;

        private boolean coalescing;

        private boolean namespaceAware;

        public Builder setResource(String xml) {
            this.resource = new InputSource(new StringReader(xml));
            return this;
        }

        public Builder setResource(InputStream resource) {
            this.resource = new InputSource(resource);
            return this;
        }

        public Builder setResource(Reader resource) {
            this.resource = new InputSource(resource);
            return this;
        }

        public Builder setProperties(Properties properties) {
            this.properties = properties;
            return this;
        }

        public Builder setValidation(boolean validation) {
            this.validation = validation;
            return this;
        }

        public Builder setEntityResolver(EntityResolver entityResolver) {
            this.entityResolver = entityResolver;
            return this;
        }

        public Builder setErrorHandler(ErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        public Builder setIgnoringElementContentWhitespace(boolean ignoringElementContentWhitespace) {
            this.ignoringElementContentWhitespace = ignoringElementContentWhitespace;
            return this;
        }

        public Builder setExpandEntityReferences(boolean expandEntityReferences) {
            this.expandEntityReferences = expandEntityReferences;
            return this;
        }

        public Builder setIgnoringComments(boolean ignoringComments) {
            this.ignoringComments = ignoringComments;
            return this;
        }

        public Builder setCoalescing(boolean coalescing) {
            this.coalescing = coalescing;
            return this;
        }

        public Builder setNamespaceAware(boolean namespaceAware) {
            this.namespaceAware = namespaceAware;
            return this;
        }

        public XMLDomParser builder() {
            return new XMLDomParser(this);
        }
    }
}
