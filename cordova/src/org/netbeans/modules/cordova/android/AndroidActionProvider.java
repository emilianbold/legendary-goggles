/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cordova.android;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.modules.cordova.CordovaPerformer;
import org.netbeans.modules.cordova.project.ClientProjectConfigurationImpl;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ProjectConfigurationProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Becicka
 */
public class AndroidActionProvider implements ActionProvider {

    private final Project p;

    public AndroidActionProvider(Project p) {
        this.p = p;
    }
    
    @Override
    public String[] getSupportedActions() {
        return new String[]{
                    COMMAND_BUILD,
                    COMMAND_CLEAN,
                    COMMAND_RUN
                };
    }

    @Override
    public void invokeAction(String command, Lookup context) throws IllegalArgumentException {
        if (COMMAND_BUILD.equals(command)) {
            new CordovaPerformer(CordovaPerformer.BUILD_ANDROID).perform(p);
        } else if (COMMAND_CLEAN.equals(command)) {
            new CordovaPerformer(CordovaPerformer.CLEAN_ANDROID).perform(p);
        } else if (COMMAND_RUN.equals(command)) {
            String checkDevices = checkDevices(p);
            while (checkDevices !=null) {
                NotifyDescriptor not = new NotifyDescriptor(
                        checkDevices, 
                        "Error", 
                        NotifyDescriptor.DEFAULT_OPTION, 
                        NotifyDescriptor.ERROR_MESSAGE,
                        null, 
                        null);
                Object value = DialogDisplayer.getDefault().notify(not);
                if (NotifyDescriptor.CANCEL_OPTION == value) {
                    return;
                } else {
                    checkDevices = checkDevices(p);
                }
            } 

            new CordovaPerformer(CordovaPerformer.RUN_ANDROID).perform(p);
        }
    }

    @Override
    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        return true;
    }

    private String checkDevices(Project p) {
        ClientProjectConfigurationImpl config = (ClientProjectConfigurationImpl) p.getLookup().lookup(ProjectConfigurationProvider.class).getActiveConfiguration();
        try {
            if ("device".equals(config.getProperty("device"))) {
                for (Device dev : AndroidPlatform.getDefault().getDevices()) {
                    if (!dev.isEmulator()) {
                        return null;
                    }
                }
                return "Please connect Android device and enable USB debugging.";
            } else {
                for (Device dev : AndroidPlatform.getDefault().getDevices()) {
                    if (dev.isEmulator()) {
                        return null;
                    }
                }
                return "Please run Android Emulator";
            }
        } catch (IOException iOException) {
            Exceptions.printStackTrace(iOException);
        }
        return "Unknown Error";
    }
    
}
