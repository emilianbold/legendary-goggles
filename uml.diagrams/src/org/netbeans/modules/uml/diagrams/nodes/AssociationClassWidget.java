/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.uml.diagrams.nodes;

import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.uml.core.metamodel.core.foundation.IPresentationElement;
import org.netbeans.modules.uml.drawingarea.actions.SceneSelectAction;
import org.netbeans.modules.uml.drawingarea.view.DesignerTools;
import org.netbeans.modules.uml.drawingarea.view.UMLEdgeWidget;


public class AssociationClassWidget extends UMLClassWidget
{
    // Since the Visual Library does not have edge to edge connections, the 
    // association class widget needs to be aware of the bridge connection between
    // the node and the association edge.
    private ConnectionWidget bridgeConnection = null;
    
    public AssociationClassWidget(Scene scene)
    {
        super(scene, "Class");
        
        WidgetAction.Chain actions = createActions(DesignerTools.SELECT);
        actions.addAction(new SceneSelectAction());
        
    }

    public AssociationClassWidget(Scene scene, IPresentationElement pe)
    {
        this(scene);
        initializeNode(pe);
    }

    public void setBridgeConnection(ConnectionWidget widget)
    {
        bridgeConnection = widget;
    }
    
    @Override
    public void remove()
    {
        super.remove();
        
        // Make sure that the bridge is still on the scene.  If the assocation
        // edge is already been removed then this will not be the case.
        if((bridgeConnection != null) && (bridgeConnection.getScene() != null))
        {
            Widget connection = bridgeConnection.getSourceAnchor().getRelatedWidget();
            connection.removeFromParent();
            bridgeConnection.removeFromParent();
            
            if (bridgeConnection.getScene() instanceof GraphScene)
            {
                GraphScene scene = (GraphScene) bridgeConnection.getScene();
                Object edge = scene.findObject(connection);
                
                if(edge != null)
                {
                    scene.removeEdge(edge);
                }
            }
        }
    }
}
