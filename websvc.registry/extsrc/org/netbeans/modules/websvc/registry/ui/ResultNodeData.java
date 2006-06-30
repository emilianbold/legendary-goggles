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

package org.netbeans.modules.websvc.registry.ui;

import com.sun.xml.rpc.processor.model.java.JavaType;

/**
 * This class represents the data for each node in the Results TreeTable.
 * @author  David Botterill
 */
public class ResultNodeData implements NodeData {



    private Object resultValue;
    private JavaType resultType;

    public ResultNodeData() {

    }


    /** Creates a new instance of TypeNodeData */
    public ResultNodeData(JavaType inType, Object inValue) {
        resultType=inType;
        resultValue=inValue;
    }
    
    public void setResultType(JavaType inType) {
        resultType=inType;
    }
    
    public JavaType getResultType() {
        return resultType;
    }
    
    public void setResultValue(Object inValue) {
        resultValue=inValue;
    }
    
    public Object getResultValue() {
        return resultValue;
    }
    
    public JavaType getNodeType() {
        return getResultType();
    }    
    
    public Object getNodeValue() {
        return getResultValue();
    }    
    
}
