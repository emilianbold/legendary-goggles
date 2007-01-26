/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.xml.schema.ui.basic;

import org.netbeans.modules.xml.schema.model.SchemaComponent;
import org.netbeans.modules.xml.schema.model.SchemaModel;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentComponent;

/**
 * Utility class for generating names for elements within a schema model.
 *
 * @author Nathan Fiedler
 */
public class NameGenerator {
    /** Default starting value for the uniqueness counter (e.g. 0 or 1). */
//    private static final int COUNTER_START = 1;
    /** Prefix for the namespace prefix values (e.g. "ns"). */
    private static final String PREFIX_PREFIX = "ns"; // NOI18N
    /** The singleton instance of this class. */
    private static NameGenerator theInstance;

    /**
     * Creates a new instance of NameGenerator.
     */
    private NameGenerator() {
    }

    /**
     * Return the singleton instance of this class.
     *
     * @return  instance of this class.
     */
    public static synchronized NameGenerator getInstance() {
        if (theInstance == null) {
            theInstance = new NameGenerator();
        }
        return theInstance;
    }

    /**
     * Generate a unique namespace prefix for the given model.
     *
     * @param  prefix  the desired prefix for the namespace prefix;
     *                 if null, a default of "ns" will be used.
     * @param  model   model in which to find unique prefix.
     * @return  the unique namespace prefix.
     */
    public String generateNamespacePrefix(String prefix,
            SchemaModel model) {
// WSDL uses zero for the namespace prefix, so let's do the same.
//        int prefixCounter = COUNTER_START;
        int prefixCounter = 0;
        String prefixStr = prefix == null ? PREFIX_PREFIX : prefix;
        String generated = prefixStr;
        while (isPrefixExist(generated, model)) {
            generated = prefixStr + prefixCounter++;
        }
        return generated;
    }

    /**
     * Determine if the given namespace prefix is used in the model.
     *
     * @param  prefix  namespace prefix to look up.
     * @param  model   the model in which to look.
     * @return  true if exists, false otherwise.
     */
    public static boolean isPrefixExist(String prefix, SchemaModel model) {
        return getNamespaceURI(prefix, model) != null ? true : false;
    }

    /**
     * Get the prefix for the given namespace, for this given element.
     *
     * @param  namespace  the namespace to lookup.
     * @param  element    the element to look at.
     * @return  the prefix, or null if none.
     */
    public static String getNamespacePrefix(String namespace,
            SchemaComponent element) {
        if (element != null && namespace != null) {
            return ((AbstractDocumentComponent) element).lookupPrefix(namespace);
        }
        return null;
    }

    /**
     * Retrieve the namespace for the given prefix, if any.
     *
     * @param  prefix  namespace prefix to look up.
     * @param  model   the model in which to look.
     * @return  the namespace for the prefix, or null if none.
     */
    public static String getNamespaceURI(String prefix, SchemaModel model) {
        if (model != null && prefix != null) {
            return ((AbstractDocumentComponent) model.getSchema()).
                    lookupNamespaceURI(prefix, true);
        }
        return null;
    }
}
