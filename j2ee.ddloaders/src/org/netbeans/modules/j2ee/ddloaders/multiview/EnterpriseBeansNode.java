/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ddloaders.multiview;

import org.netbeans.modules.j2ee.dd.api.ejb.Ejb;
import org.netbeans.modules.j2ee.dd.api.ejb.EnterpriseBeans;
import org.netbeans.modules.j2ee.dd.api.ejb.Entity;
import org.netbeans.modules.j2ee.dd.api.ejb.MessageDriven;
import org.netbeans.modules.j2ee.dd.api.ejb.Session;
import org.netbeans.modules.xml.multiview.SectionNode;
import org.netbeans.modules.xml.multiview.ui.SectionNodeView;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author pfiala
 */
public class EnterpriseBeansNode extends SectionNode {

    public EnterpriseBeansNode(SectionNodeView sectionNodeView, EnterpriseBeans enterpriseBeans) {
        super(sectionNodeView, enterpriseBeans, Utils.getBundleMessage("LBL_EnterpriseBeans"),
                Utils.ICON_BASE_ENTERPRISE_JAVA_BEANS_NODE);
        setExpanded(true);

        final Ejb[] ejbs = enterpriseBeans.getEjbs();

        // sort beans according to their display name
        Arrays.sort(ejbs, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Ejb) o1).getDefaultDisplayName().compareTo(((Ejb) o2).getDefaultDisplayName());
            }
        });

        for (int i = 0; i < ejbs.length; i++) {
            Ejb ejb = ejbs[i];
            if (ejb instanceof Session) {
                addChild(new SessionNode(sectionNodeView, (Session) ejb));
            } else if (ejb instanceof Entity) {
                addChild(new EntityNode(sectionNodeView, (Entity) ejb));
            } else if (ejb instanceof MessageDriven) {
                addChild(new MessageDrivenNode(sectionNodeView, (MessageDriven) ejb));
            }
        }
    }
}
