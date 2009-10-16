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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.modules.java.j2seplatform.platformdefinition;

import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.BeanInfo;
import java.awt.*;

public class DefaultPlatformImplBeanInfo extends SimpleBeanInfo {

    public DefaultPlatformImplBeanInfo () {
    }

    public Image getIcon(int iconKind) {
        if ((iconKind == BeanInfo.ICON_COLOR_16x16) || (iconKind == BeanInfo.ICON_MONO_16x16)) {
            return ImageUtilities.loadImage("org/netbeans/modules/java/j2seplatform/resources/platform.gif"); // NOI18N
        } else {
            return null;
        }
    }


    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            PropertyDescriptor[] pds = new PropertyDescriptor[] {
                new PropertyDescriptor (DefaultPlatformImpl.PROP_DISPLAY_NAME, DefaultPlatformImpl.class),
                new PropertyDescriptor (DefaultPlatformImpl.PROP_SOURCE_FOLDER, DefaultPlatformImpl.class),                
                new PropertyDescriptor (DefaultPlatformImpl.PROP_JAVADOC_FOLDER, DefaultPlatformImpl.class),
            };
            pds[0].setDisplayName(NbBundle.getMessage(DefaultPlatformImplBeanInfo.class,"TXT_Name"));
            pds[0].setBound(true);
            pds[1].setDisplayName(NbBundle.getMessage(DefaultPlatformImplBeanInfo.class,"TXT_SourcesFolder"));
            pds[1].setPropertyEditorClass(FileObjectPropertyEditor.class);
            pds[1].setBound(true);
            pds[2].setDisplayName(NbBundle.getMessage(DefaultPlatformImplBeanInfo.class,"TXT_JavaDocFolder"));
            pds[2].setPropertyEditorClass(FileObjectPropertyEditor.class);
            pds[2].setBound(true);
            return pds;
        } catch (IntrospectionException ie) {
            return new PropertyDescriptor[0];
        }
    }

}
