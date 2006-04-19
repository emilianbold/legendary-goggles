/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.projectimport.eclipse;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import org.openide.ErrorManager;
import org.openide.xml.XMLUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.netbeans.modules.projectimport.ProjectImporterException;

/**
 * Parses user library xml document.
 *
 * @author mkrauskopf
 */
final class UserLibraryParser extends DefaultHandler {
    
    // elements names
    private static final String USER_LIBRARY = "userlibrary"; // NOI18N
    private static final String ARCHIVE = "archive"; // NOI18N
    private static final String ATTRIBUTES = "attributes"; // NOI18N
    private static final String ATTRIBUTE = "attribute"; // NOI18N
    
    // attributes names
    private static final String PATH_ATTR = "path"; // NOI18N
    
    // indicates current position in a xml document
    private static final int POSITION_NONE = 0;
    private static final int POSITION_USER_LIBRARY = 1;
    private static final int POSITION_ARCHIVE = 2;
    private static final int POSITION_ATTRIBUTES = 3;
    private static final int POSITION_ATTRIBUTE = 4;
    
    private int position = POSITION_NONE;
    private StringBuffer chars;
    
    private Collection jars;
    
    private UserLibraryParser() {/* emtpy constructor */}
    
    /** Returns jars contained in the given user library. */
    static Collection getJars(String xmlDoc) throws ProjectImporterException {
        UserLibraryParser parser = new UserLibraryParser();
        parser.load(new InputSource(new StringReader(xmlDoc)));
        return parser.jars;
    }
    
    /** Parses a given InputSource and fills up jars collection */
    private void load(InputSource projectIS) throws ProjectImporterException{
        try {
            /* parser creation */
            XMLReader reader = XMLUtil.createXMLReader(false, true);
            reader.setContentHandler(this);
            reader.setErrorHandler(this);
            chars = new StringBuffer(); // initialization
            reader.parse(projectIS); // start parsing
        } catch (IOException e) {
            throw new ProjectImporterException(e);
        } catch (SAXException e) {
            throw new ProjectImporterException(e);
        }
    }
    
    public void characters(char ch[], int offset, int length) throws SAXException {
        chars.append(ch, offset, length);
    }
    
    public void startElement(String uri, String localName,
            String qName, Attributes attributes) throws SAXException {
        
        chars.setLength(0);
        switch (position) {
            case POSITION_NONE:
                if (localName.equals(USER_LIBRARY)) {
                    position = POSITION_USER_LIBRARY;
                    jars = new HashSet();
                } else {
                    throw (new SAXException("First element has to be " // NOI18N
                            + USER_LIBRARY + ", but is " + localName)); // NOI18N
                }
                break;
            case POSITION_USER_LIBRARY:
                if (localName.equals(ARCHIVE)) {
                    jars.add(attributes.getValue(PATH_ATTR));
                    position = POSITION_ARCHIVE;
                }
                break;
            case POSITION_ARCHIVE:
                if (localName.equals(ATTRIBUTES)) {
                    // ignored in the meantime - prepared for future (see #75112)
                    position = POSITION_ATTRIBUTES;
                }
                break;
            case POSITION_ATTRIBUTES:
                if (localName.equals(ATTRIBUTE)) {
                    // ignored in the meantime - prepared for future (see #75112)
                    position = POSITION_ATTRIBUTE;
                }
                break;
            default:
                throw (new SAXException("Unknown element reached: " // NOI18N
                        + localName));
        }
    }
    
    public void endElement(String uri, String localName, String qName) throws
            SAXException {
        switch (position) {
            case POSITION_USER_LIBRARY:
                // parsing ends
                position = POSITION_NONE;
                break;
            case POSITION_ARCHIVE:
                position = POSITION_USER_LIBRARY;
                break;
            case POSITION_ATTRIBUTES:
                position = POSITION_ARCHIVE;
                break;
            case POSITION_ATTRIBUTE:
                position = POSITION_ATTRIBUTES;
                break;
            default:
                ErrorManager.getDefault().log(ErrorManager.WARNING,
                        "Unknown state reached in UserLibraryParser, " + // NOI18N
                        "position: " + position); // NOI18N
        }
        chars.setLength(0);
    }
    
    public void warning(SAXParseException e) throws SAXException {
        ErrorManager.getDefault().log(ErrorManager.WARNING, "Warning occurred: " + e);
    }
    
    public void error(SAXParseException e) throws SAXException {
        ErrorManager.getDefault().log(ErrorManager.WARNING, "Error occurres: " + e);
        throw e;
    }
    
    public void fatalError(SAXParseException e) throws SAXException {
        ErrorManager.getDefault().log(ErrorManager.WARNING, "Fatal error occurres: " + e);
        throw e;
    }
}
