package com.oneandone.ejbcdiunit.cfganalyzer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.oneandone.ejbcdiunit.CdiTestConfig;
import com.oneandone.ejbcdiunit.internal.ApplicationExceptionDescription;

/**
 * @author aschoerk
 */
class EjbJarParser {
    private Logger log = LoggerFactory.getLogger("EjbJarParser");

    private final CdiTestConfig config;
    private final URL url;

    public EjbJarParser(final CdiTestConfig config, final URL url) {
        this.config = config;
        this.url = url;
    }

    public void invoke() throws IOException {
        try (URLClassLoader cl = new URLClassLoader(new URL[] { url }, null)) {
            URL resource = cl.getResource("WEB-INF/ejb-jar.xml");
            if (resource == null) {
                resource = cl.getResource("META-INF/ejb-jar.xml");
            }
            if (resource == null && url.getFile().endsWith("/classes/")) {
                resource = new URL(url, "../../src/main/webapp/WEB-INF/ejb-jar.xml");
            }

            if (resource == null) {
                log.error("expected ejb-jar to be found in {}", url);
                return;
            }

            try (InputStream inputStream = resource.openStream()) {
                final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                Document ejbjar = documentBuilderFactory.newDocumentBuilder().parse(inputStream);
                final Element documentElement = ejbjar.getDocumentElement();
                if ("ejb-jar".equals(documentElement.getNodeName())) {
                    documentElement.normalize();
                    NodeList children = documentElement.getChildNodes();
                    for (int i = 0; i < children.getLength(); i++) {
                        if ("assembly-descriptor".equals(children.item(i).getNodeName())) {
                            NodeList assemblyDescriptor = children.item(i).getChildNodes();
                            for (int j = 0; j < assemblyDescriptor.getLength(); j++) {
                                final Node description = assemblyDescriptor.item(j);
                                if ("application-exception".equals(description.getNodeName())) {
                                    ApplicationExceptionDescription applicationExceptionDescription = new ApplicationExceptionDescription();
                                    for (int k = 0; k < description.getChildNodes().getLength(); k++) {
                                        final Node attribute = description.getChildNodes().item(k);
                                        final String value = attribute.getTextContent();
                                        switch (attribute.getNodeName()) {
                                            case "exception-class":
                                                applicationExceptionDescription.setClassName(value.trim());
                                                break;
                                            case "rollback":
                                                applicationExceptionDescription.setRollback("true".equals(value.toLowerCase().trim()));
                                                break;
                                            case "inherited":
                                                applicationExceptionDescription.setInherited("true".equals(value.toLowerCase().trim()));
                                                break;
                                            case "#text":
                                                break;
                                            default:
                                                throw new RuntimeException("unexpected attribute in ejb-jar: " + attribute.getNodeName());
                                        }
                                    }
                                    config.getApplicationExceptionDescriptions().add(applicationExceptionDescription);
                                }
                            }
                            break;
                        }
                    }
                } else {
                    log.error("expected ejb-jar-element to be found in {}", resource);
                }
            } catch (ParserConfigurationException | SAXException e) {
                log.error("parsing ejb-jar", e);
            }
        }
    }

}
