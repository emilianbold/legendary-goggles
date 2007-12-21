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

package org.netbeans.modules.soa.mappercore;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import javax.swing.JComponent;
import org.netbeans.modules.soa.mappercore.dnd.*;

/**
 *
 * @author anjeleevich
 */
public abstract class AbstractMapperDnDTool extends MapperPropertyAccess {
    
    
    public AbstractMapperDnDTool(Mapper mapper) {
        super(mapper);
    }
    
    public abstract boolean drag(JComponent component, DropTargetDragEvent dtde);
    public abstract boolean drop(JComponent component, DropTargetDropEvent dtde);
    public abstract void dragDone();
}
