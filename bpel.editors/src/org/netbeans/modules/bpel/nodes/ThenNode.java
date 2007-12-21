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
package org.netbeans.modules.bpel.nodes;

import org.netbeans.modules.bpel.editors.api.nodes.NodeType;
import org.netbeans.modules.bpel.model.api.If;
import org.netbeans.modules.bpel.nodes.actions.ActionType;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import static org.netbeans.modules.bpel.properties.PropertyType.*;

/**
 *
 * @author Vitaly Bychkov
 */
public class ThenNode extends BpelNode<If> {

    public ThenNode(If reference, Children children, Lookup lookup) {
        super(reference, children, lookup);
    }

    public ThenNode(If reference, Lookup lookup) {
        super(reference, lookup);
    }
    
    public NodeType getNodeType() {
        return NodeType.THEN;
    }

    protected ActionType[] getActionsArray() {
        return new ActionType[0];
    }     
}
