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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.websvc.registry.jaxrpc;


public class WsdlType {
    private java.net.URL location;
    private java.lang.String packageName;

    /**
     * Normal starting point constructor.
     */
    public WsdlType() {
        packageName = "";
    }

    /**
     * Required parameters constructor
     */
    public WsdlType(java.net.URL location, java.lang.String packageName) {
        location = location;
        packageName = packageName;
    }


    public void setLocation(java.net.URL value) {
        location = value;
    }
    
    public java.net.URL getLocation() {
        return location;
    }
    
    public void setPackageName(java.lang.String value) {
        packageName = value;
    }
    
    public java.lang.String getPackageName() {
        return packageName;
    }
    
    
    
    public void writeNode(java.io.Writer out, String nodeName, String indent) throws java.io.IOException {
        out.write(indent);
        out.write("<");
        out.write(nodeName);
        // location is an attribute
        if (location != null) {
            out.write(" location");	// NOI18N
            out.write("='");	// NOI18N
            Configuration.writeXML(out, location.toString(), true);
            out.write("'");	// NOI18N
        }
        // packageName is an attribute
        if (packageName != null && packageName.length() > 0) {
            out.write(" packageName");	// NOI18N
            out.write("='");	// NOI18N
            Configuration.writeXML(out, packageName, true);
            out.write("'");	// NOI18N
        }
        out.write(">\n");
        String nextIndent = indent + "	";
        out.write(indent);
        out.write("</"+nodeName+">\n");
    }
    
    public void readNode(org.w3c.dom.Node node) {
        if (node.hasAttributes()) {
            org.w3c.dom.NamedNodeMap attrs = node.getAttributes();
            org.w3c.dom.Attr attr;
            java.lang.String attrValue;
            attr = (org.w3c.dom.Attr) attrs.getNamedItem("location");
            try {
                if (attr != null) {
                    attrValue = attr.getValue();
                } else {
                    attrValue = null;
                }
                location = new java.net.URL(attrValue);
            }
            catch (java.net.MalformedURLException e) {
                throw new java.lang.RuntimeException(e);
            }
            attr = (org.w3c.dom.Attr) attrs.getNamedItem("packageName");
            if (attr != null) {
                attrValue = attr.getValue();
            } else {
                attrValue = null;
            }
            packageName = attrValue;
        }
        org.w3c.dom.NodeList children = node.getChildNodes();
        for (int i = 0, size = children.getLength(); i < size; ++i) {
            org.w3c.dom.Node childNode = children.item(i);
            String childNodeName = (childNode.getLocalName() == null ? childNode.getNodeName().intern() : childNode.getLocalName().intern());
            String childNodeValue = "";
            if (childNode.getFirstChild() != null) {
                childNodeValue = childNode.getFirstChild().getNodeValue();
            }
            else {
                // Found extra unrecognized childNode
            }
        }
    }
}


