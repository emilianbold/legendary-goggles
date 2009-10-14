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

/*
 * Created on Jan 26, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.netbeans.modules.xml.wsdl.ui.fastmodel;

import java.io.File;
import java.io.InputStream;
import org.openide.util.NbBundle;


/**
 * @author radval
 *
 * A factory which parses wsdl fast.
 * Just parse some attributes from wsdl and ignore rests.
 */
public abstract class FastSchemaFactory {

    private static FastSchemaFactory factory;

    public FastSchemaFactory() {

    }

    /**
     * Gets the Fast WSDL Definitions factory singleton.
     * @return  a Fast WSDL Definitions factory.
     * @throws  EInsightModelException  When implementing factory class not found.
     */
    public static synchronized FastSchemaFactory getInstance() throws Exception {
        if (null == factory) {
            String fac = System.getProperty(FastSchemaFactory.class.getName(),
            "org.netbeans.modules.xml.wsdl.ui.fastmodel.impl.FastSchemaFactoryImpl");//NOI18N
            try {
                factory = (FastSchemaFactory) Class.forName(fac).newInstance();
            } catch (Exception e) {
                throw new Exception (
                        NbBundle.getMessage(FastSchemaFactory.class, "ERR_MSG_CLASS_NOT_FOUND", fac), e);
            }
        }
        return factory;
    }    
    
    public abstract FastSchema newFastSchema(InputStream in, boolean parseImports);
    
    
    public abstract FastSchema newFastSchema (File file);
    public abstract FastSchema newFastSchema (File file, boolean parseImports);
    
    public abstract FastSchema newFastSchema(String defFileUrl);
    
    public abstract FastSchema newFastSchema(String defFileUrl, 
            boolean parseImports);
    
}
