/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.websvc.wsstack.jaxrs.glassfish.v3;

import java.net.URL;
import org.netbeans.modules.websvc.wsstack.api.WSStack.Feature;
import org.netbeans.modules.websvc.wsstack.api.WSStack.Tool;
import org.netbeans.modules.websvc.wsstack.api.WSTool;
import org.netbeans.modules.websvc.wsstack.jaxrs.JaxRs;
import org.netbeans.modules.websvc.wsstack.spi.WSStackFactory;

/**
 *
 * @author ayubkhan
 */
public class GlassFishV31EE6JaxRsStack extends GlassFishV3EE6JaxRsStack {

    private static final String[] GFV31_JAXRS_LIBRARIES =
        new String[] {"jackson", "jersey-client", "jersey-core", "jersey-gf-server", "jersey-json", "jersey-multipart", "jettison", "mimepull"}; //NOI18N

    public GlassFishV31EE6JaxRsStack(String gfRootStr) {
        super(gfRootStr);
    }

    @Override
    public WSTool getWSTool(Tool toolId) {
        if (toolId == JaxRs.Tool.JAXRS) {
            return WSStackFactory.createWSTool(new JaxRsTool(JaxRs.Tool.JAXRS, GFV31_JAXRS_LIBRARIES));
        }
        return null;
    }

    @Override
    public boolean isFeatureSupported(Feature feature) {
        boolean isFeatureSupported = false;
        if (feature == JaxRs.Feature.JAXRS) {
            WSTool wsTool = getWSTool(JaxRs.Tool.JAXRS);
            if (wsTool != null) {
                URL[] libs = wsTool.getLibraries();
                if(libs != null && libs.length == GFV31_JAXRS_LIBRARIES.length) {
                    isFeatureSupported = true;
                }
            }
        }
        return isFeatureSupported;
    }

}
