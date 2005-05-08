/*
 * Copyright (c) 2005 Sun Microsystems, Inc.  All rights reserved.  U.S.
 * Government Rights - Commercial software.  Government users are subject
 * to the Sun Microsystems, Inc. standard license agreement and
 * applicable provisions of the FAR and its supplements.  Use is subject
 * to license terms.
 *
 * This distribution may include materials developed by third parties.
 * Sun, Sun Microsystems, the Sun logo, Java and J2EE are trademarks
 * or registered trademarks of Sun Microsystems, Inc. in the U.S. and
 * other countries.
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. Tous droits reserves.
 *
 * Droits du gouvernement americain, utilisateurs gouvernementaux - logiciel
 * commercial. Les utilisateurs gouvernementaux sont soumis au contrat de
 * licence standard de Sun Microsystems, Inc., ainsi qu'aux dispositions
 * en vigueur de la FAR (Federal Acquisition Regulations) et des
 * supplements a celles-ci.  Distribue par des licences qui en
 * restreignent l'utilisation.
 *
 * Cette distribution peut comprendre des composants developpes par des
 * tierces parties. Sun, Sun Microsystems, le logo Sun, Java et J2EE
 * sont des marques de fabrique ou des marques deposees de Sun
 * Microsystems, Inc. aux Etats-Unis et dans d'autres pays.
 */
package domexample;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;
import javax.xml.transform.dom.DOMSource;


public class DOMSrcExample {
    static DOMSource domSource;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Argument required: " + "-Dxml-file=<filename>");
            System.exit(1);
        }

        DOMSrcExample dse = new DOMSrcExample();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(args[0]));
            domSource = new DOMSource(document);
        } catch (SAXParseException spe) {
            // Error generated by the parser
            System.out.println("\n** Parsing error" + ", line " +
                spe.getLineNumber() + ", uri " + spe.getSystemId());
            System.out.println("   " + spe.getMessage());

            // Use the contained exception, if any
            Exception x = spe;

            if (spe.getException() != null) {
                x = spe.getException();
            }

            x.printStackTrace();
        } catch (SAXException sxe) {
            // Error generated during parsing)
            System.out.println("\n** SAXException:");

            Exception x = sxe;

            if (sxe.getException() != null) {
                x = sxe.getException();
            }

            x.printStackTrace();
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            System.out.println("\n** ParserConfigurationException:");
            pce.printStackTrace();
        } catch (IOException ioe) {
            // I/O error
            System.out.println("\n** IOException:");
            ioe.printStackTrace();
        }

        try {
            // Create message factory
            MessageFactory messageFactory = MessageFactory.newInstance();

            // Create a message
            SOAPMessage message = messageFactory.createMessage();

            // Get the SOAP part and set its content to domSource
            SOAPPart soapPart = message.getSOAPPart();
            soapPart.setContent(domSource);

            message.saveChanges();

            // Get contents using SAAJ APIs.
            // Header is optional.
            SOAPHeader header = message.getSOAPHeader();

            if (header != null) {
                Iterator iter1 = header.getChildElements();
                System.out.println("Header contents:");
                dse.getContents(iter1, "");
            }

            SOAPBody body = message.getSOAPBody();
            Iterator iter2 = body.getChildElements();
            System.out.println("Body contents:");
            dse.getContents(iter2, "");
        } catch (Exception ex) {
            System.out.println("\n** Exception:");
            ex.printStackTrace();
        }
    }
     // main

    /*
     * Retrieves the contents of the elements recursively and
     * displays them.
     *
     * @param iterator        Iterator returned by getChildElements
     * @param indent        indentation to nest element display
     */
    public void getContents(Iterator iterator, String indent) {
        while (iterator.hasNext()) {
            Node node = (Node) iterator.next();
            SOAPElement element = null;
            Text text = null;

            if (node instanceof SOAPElement) {
                element = (SOAPElement) node;

                Name name = element.getElementName();
                System.out.println(indent + "Name is " +
                    name.getQualifiedName());

                Iterator attrs = element.getAllAttributes();

                while (attrs.hasNext()) {
                    Name attrName = (Name) attrs.next();
                    System.out.println(indent + " Attribute name is " +
                        attrName.getQualifiedName());
                    System.out.println(indent + " Attribute value is " +
                        element.getAttributeValue(attrName));
                }

                Iterator iter2 = element.getChildElements();
                getContents(iter2, indent + " ");
            } else {
                text = (Text) node;

                String content = text.getValue();
                System.out.println(indent + "Content is: " + content);
            }
        }
    }
}
