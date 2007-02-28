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
 *
 */

package org.netbeans.modules.vmd.midp.serialization;

import org.netbeans.modules.vmd.api.model.ComponentDescriptor;
import org.netbeans.modules.vmd.api.model.Presenter;
import org.netbeans.modules.vmd.api.model.PresenterDeserializer;
import org.netbeans.modules.vmd.midp.codegen.MidpCodePresenterSupport;
import org.netbeans.modules.vmd.midp.components.MidpDocumentSupport;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.List;

/**
 * @author David Kaspar
 */
public class MidpAddImportPresenterDeserializer extends PresenterDeserializer {

    static final String PROPERTY_NODE = "MidpAddImport"; // NOI18N

    private static final PresenterFactory factory = new PresenterFactory () {
        public List<Presenter> createPresenters (ComponentDescriptor descriptor) {
            return Arrays.asList (MidpCodePresenterSupport.createAddImportPresenter ());
        }
    };

    public MidpAddImportPresenterDeserializer () {
        super (MidpDocumentSupport.PROJECT_TYPE_MIDP);
    }

    public PresenterFactory deserialize (Node node) {
        if (! PROPERTY_NODE.equalsIgnoreCase (node.getNodeName ()))
            return null;
        return factory;
    }

}
