/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.glassfish.javaee;

import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.modules.glassfish.spi.ProfilerCookie;
import org.netbeans.modules.glassfish.spi.RemoveCookie;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.deployment.profiler.api.ProfilerServerSettings;
import org.netbeans.modules.j2ee.deployment.profiler.spi.Profiler;
import org.openide.util.Lookup;


/**
 *
 * @author Peter Williams
 */
public class JavaEEServerModule implements RemoveCookie, ProfilerCookie {

    private final Lookup lookup;
    private final InstanceProperties instanceProperties;
    
    JavaEEServerModule(Lookup instanceLookup, InstanceProperties ip) {
        lookup = instanceLookup;
        instanceProperties = ip;
    }

    public InstanceProperties getInstanceProperties() {
        return instanceProperties;
    }
    // ------------------------------------------------------------------------
    // RemoveCookie support
    // ------------------------------------------------------------------------
    public void removeInstance(String serverUri) {
        InstanceProperties.removeInstance(serverUri);
    }

    public Object[] getData() {
        Profiler profiler = (Profiler) Lookup.getDefault().lookup(Profiler.class);
        Object[] retVal = new Object[2];
        retVal[0] = JavaPlatform.getDefault().getInstallFolders().iterator().next();
        retVal[1] = new String[0];
        if (profiler == null) {
            return retVal;
        }
        final ProfilerServerSettings settings = profiler.getSettings(instanceProperties.getProperty(InstanceProperties.URL_ATTR));
        if (settings == null) {
            return retVal;
        }
        retVal[0] = settings.getJavaPlatform().getInstallFolders().iterator().next();
        retVal[1] = settings.getJvmArgs();
        return retVal;
    }

    // ------------------------------------------------------------------------
    // J2eeserver support
    // ------------------------------------------------------------------------
//    public boolean startServer() {
//        GlassfishModule commonModule = lookup.lookup(GlassfishModule.class);
//        if(commonModule != null) {
//            return commonModule.startServer(new OperationStateListener() {
//                public void operationStateChanged(OperationState newState, String message) {
//                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//            });
//        } else {
//            throw new UnsupportedOperationException("StartServer is not supported by JRubyServerModule");
//        }
//    }
//
//    public boolean stopServer() {
//        GlassfishModule commonModule = lookup.lookup(GlassfishModule.class);
//        if(commonModule != null) {
//            return commonModule.stopServer(new OperationStateListener() {
//                public void operationStateChanged(OperationState newState, String message) {
//                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//            });
//        } else {
//            throw new UnsupportedOperationException("StopServer is not supported by JRubyServerModule");
//        }
//    }

}
