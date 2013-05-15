/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2011 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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

package org.netbeans.modules.glassfish.javaee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.model.DeployableObject;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.DConfigBeanVersionType;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.DeploymentConfiguration;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.DConfigBeanVersionUnsupportedException;
import javax.enterprise.deploy.spi.exceptions.InvalidModuleException;
import javax.enterprise.deploy.spi.exceptions.TargetException;
import javax.enterprise.deploy.spi.status.ProgressEvent;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.api.server.ServerInstance;
import org.netbeans.modules.glassfish.eecommon.api.HttpMonitorHelper;
import org.netbeans.modules.glassfish.javaee.ide.*;
import org.netbeans.modules.glassfish.spi.*;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.deployment.plugins.spi.DeploymentContext;
import org.netbeans.modules.j2ee.deployment.plugins.spi.DeploymentManager2;
import org.openide.util.NbBundle;
import org.xml.sax.SAXException;

/**
 *
 * @author Ludovic Champenois
 * @author Peter Williams
 */
public class Hk2DeploymentManager implements DeploymentManager2 {

    private volatile ServerInstance serverInstance;
    private final InstanceProperties instanceProperties;
    private final Hk2PluginProperties pluginProperties;
    private final String uri;
    private final ServerUtilities su;
    
    /**
     * 
     * @param uri 
     * @param uname 
     * @param passwd 
     */
    public Hk2DeploymentManager(String uri, String uname, String passwd, ServerUtilities su) {
        this.uri = uri;
        this.su = su;
        pluginProperties = new Hk2PluginProperties(this,su);
        instanceProperties = InstanceProperties.getInstanceProperties(getUri());
    }
        
    
    /**
     * 
     * @param targetList
     * @param moduleArchive
     * @param deploymentPlan
     * @return
     * @throws java.lang.IllegalStateException
     */
    @Override
    public ProgressObject distribute(Target[] targetList, final File moduleArchive, File deploymentPlan)
            throws IllegalStateException {
        return distribute(targetList, moduleArchive, deploymentPlan, new File[0], null);
    }

    @Override
    public ProgressObject distribute(Target[] targets, DeploymentContext context) {
        String cr = null;
        String moduleFilePath = context.getModuleFile().getAbsolutePath();
        if (moduleFilePath.endsWith(".war")) {
            // compute cr 
            ModuleConfigurationImpl mci = ModuleConfigurationImpl.get(context.getModule());
            if (null != mci) {
                try {
                    cr = mci.getContextRoot();
                } catch (ConfigurationException ex) {
                    Logger.getLogger("glassfish").log(Level.WARNING, "could not getContextRoot() for {0}",moduleFilePath);
                }
            }
        }
        return distribute(targets, context.getModuleFile(), context.getDeploymentPlan(), context.getRequiredLibraries(), cr);
    }
    
    private ProgressObject distribute(Target[] targetList, final File moduleArchive, File deploymentPlan, File[] requiredLibraries, String cr)
            throws IllegalStateException {
        String t = moduleArchive.getName();
        final GlassfishModule commonSupport = getCommonServerSupport();
        String url = commonSupport.getInstanceProperties().get(GlassfishModule.URL_ATTR);
        String targ = getTargetFromUri(url);
        String nameSuffix = ""; // NOI18N
        if (null != targ)
            nameSuffix = "_"+targ; // NOI18N
        final String moduleName = org.netbeans.modules.glassfish.spi.Utils.sanitizeName(t.substring(0, t.length() - 4)) +
                nameSuffix;
        // 
        Hk2TargetModuleID moduleId = Hk2TargetModuleID.get((Hk2Target) targetList[0], moduleName,
                null, moduleArchive.getAbsolutePath());
        final MonitorProgressObject deployProgress = new MonitorProgressObject(this, moduleId);
        final MonitorProgressObject updateCRProgress = new MonitorProgressObject(this, moduleId);
        deployProgress.addProgressListener(new UpdateContextRoot(updateCRProgress, moduleId, getServerInstance(), true));
        MonitorProgressObject restartProgress = new MonitorProgressObject(this, moduleId);

        final GlassfishModule2 commonSupport2 = (commonSupport instanceof GlassfishModule2 ?
            (GlassfishModule2)commonSupport : null);
        boolean restart = false;
        try {
            restart = HttpMonitorHelper.synchronizeMonitor( commonSupport.getInstanceProperties().get(GlassfishModule.DOMAINS_FOLDER_ATTR),
                    commonSupport.getInstanceProperties().get(GlassfishModule.DOMAIN_NAME_ATTR),
                    Boolean.parseBoolean(commonSupport.getInstanceProperties().get(GlassfishModule.HTTP_MONITOR_FLAG)),
                    "modules/org-netbeans-modules-schema2beans.jar");
        } catch (IOException ex) {
            Logger.getLogger("glassfish-javaee").log(Level.WARNING, "http monitor state", ex);
        } catch (SAXException ex) {
            Logger.getLogger("glassfish-javaee").log(Level.WARNING, "http monitor state", ex);
        }
        ResourceRegistrationHelper.deployResources(moduleArchive,this);
        if (restart) {
            restartProgress.addProgressListener(new ProgressListener() {
                @Override
                public void handleProgressEvent(ProgressEvent event) {
                    if (event.getDeploymentStatus().isCompleted()) {
                        commonSupport.deploy(deployProgress, moduleArchive, moduleName);
                    } else {
                        deployProgress.fireHandleProgressEvent(event.getDeploymentStatus());
                    }
                }
            });
            commonSupport.restartServer(restartProgress);
            return updateCRProgress;
        } else {
            if (commonSupport2 != null && requiredLibraries.length > 0) {
                commonSupport2.deploy(deployProgress, moduleArchive, moduleName, cr, Collections.<String, String>emptyMap(), requiredLibraries);
            } else {
                commonSupport.deploy(deployProgress, moduleArchive, moduleName, cr);
            }
            return updateCRProgress;
        }
    }

    /**
     * 
     * @param moduleIDList
     * @param moduleArchive
     * @param deploymentPlan
     * @return
     * @throws java.lang.UnsupportedOperationException
     * @throws java.lang.IllegalStateException
     */
    @Override
    public ProgressObject redeploy(TargetModuleID [] moduleIDList, final File moduleArchive, File deploymentPlan)
            throws UnsupportedOperationException, IllegalStateException {
        return redeploy(moduleIDList, moduleArchive, deploymentPlan, new File[0]);
    }

    @Override
    public ProgressObject redeploy(TargetModuleID[] moduleIDList, DeploymentContext context) {
        return redeploy(moduleIDList, context.getModuleFile(), context.getDeploymentPlan(), context.getRequiredLibraries());
    }

    private ProgressObject redeploy(TargetModuleID [] moduleIDList, final File moduleArchive, File deploymentPlan, File[] requiredLibraries)
            throws UnsupportedOperationException, IllegalStateException {
        final Hk2TargetModuleID moduleId = (Hk2TargetModuleID) moduleIDList[0];
        final String moduleName = moduleId.getModuleID();
        final MonitorProgressObject progressObject = new MonitorProgressObject(this,
                moduleId, CommandType.REDEPLOY);
       MonitorProgressObject restartObject = new MonitorProgressObject(this,moduleId,
                CommandType.REDEPLOY);
        final MonitorProgressObject updateCRObject = new MonitorProgressObject(this, 
                moduleId, CommandType.REDEPLOY);
        final GlassfishModule commonSupport = this.getCommonServerSupport();
        final GlassfishModule2 commonSupport2 = (commonSupport instanceof GlassfishModule2 ?
            (GlassfishModule2)commonSupport : null);
        // FIXME -- broken for remote deploy of web apps
        progressObject.addProgressListener(new UpdateContextRoot(updateCRObject,moduleId,getServerInstance(), true));
        boolean restart = false;
        try {
            restart = HttpMonitorHelper.synchronizeMonitor(
                    commonSupport.getInstanceProperties().get(GlassfishModule.DOMAINS_FOLDER_ATTR),
                    commonSupport.getInstanceProperties().get(GlassfishModule.DOMAIN_NAME_ATTR),
                    Boolean.parseBoolean(commonSupport.getInstanceProperties().get(GlassfishModule.HTTP_MONITOR_FLAG)),
                    "modules/org-netbeans-modules-schema2beans.jar");
        } catch (IOException ex) {
            Logger.getLogger("glassfish-javaee").log(Level.WARNING, "http monitor state",
                    ex);
        } catch (SAXException ex) {
            Logger.getLogger("glassfish-javaee").log(Level.WARNING, "http monitor state",
                    ex);
        }
        ResourceRegistrationHelper.deployResources(moduleArchive,this);
        if (restart) {
            restartObject.addProgressListener(new ProgressListener() {

                @Override
                public void handleProgressEvent(ProgressEvent event) {
                    if (event.getDeploymentStatus().isCompleted()) {
                            commonSupport.deploy(progressObject, moduleArchive, moduleName);
                    } else {
                        progressObject.fireHandleProgressEvent(event.getDeploymentStatus());
                    }
                }
            });
            commonSupport.restartServer(restartObject);
            return updateCRObject;
        } else {
            if (commonSupport2 != null && requiredLibraries.length > 0) {
                commonSupport2.deploy(progressObject, moduleArchive, moduleName, null, Collections.<String, String>emptyMap(), requiredLibraries);
            } else {
                commonSupport.deploy(progressObject, moduleArchive, moduleName);
            }
            return updateCRObject;
        }
    }
    
    /**
     *
     * @param deployableObject
     * @return
     * @throws javax.enterprise.deploy.spi.exceptions.InvalidModuleException
     */
    @Override
    public DeploymentConfiguration createConfiguration(DeployableObject deployableObject)
            throws InvalidModuleException {
        return new Hk2Configuration(deployableObject);
    }


    /**
     *
     * @param targetList
     * @param moduleArchive
     * @param deploymentPlan
     * @return
     * @throws java.lang.IllegalStateException
     */
    @Override
    public ProgressObject distribute(Target [] targetList, InputStream moduleArchive, InputStream deploymentPlan)
            throws IllegalStateException {
        throw new UnsupportedOperationException(
                "Hk2DeploymentManager.distribute(target [], stream, stream) not supported yet.");
    }

    /**
     *
     * @param targetList
     * @param type
     * @param moduleArchive
     * @param deploymentPlan
     * @return
     * @throws java.lang.IllegalStateException
     */
    @Override
    public ProgressObject distribute(Target [] targetList, ModuleType type, InputStream moduleArchive, InputStream deploymentPlan)
            throws IllegalStateException {
        throw new UnsupportedOperationException(
                "Hk2DeploymentManager.distribute(target [], module_type, stream, stream) not supported yet.");
    }

    /**
     * 
     * @param moduleIDList
     * @param moduleArchive
     * @param deploymentPlan
     * @return
     * @throws java.lang.UnsupportedOperationException
     * @throws java.lang.IllegalStateException
     */
    @Override
    public ProgressObject redeploy(TargetModuleID [] moduleIDList, InputStream moduleArchive, InputStream deploymentPlan) 
            throws UnsupportedOperationException, IllegalStateException {
        throw new UnsupportedOperationException(
                "Hk2DeploymentManager.redeploy(target_module [], stream, stream) not supported yet.");
    }

    /**
     * 
     * @param targetModuleID 
     * @return 
     * @throws java.lang.IllegalStateException 
     */
    @Override
    public ProgressObject undeploy(TargetModuleID [] targetModuleIDs) 
            throws IllegalStateException {
        // !PW FIXME handle arrays with length > 1 (EARs?)
        if(targetModuleIDs != null && targetModuleIDs.length > 0) {
            GlassfishModule commonSupport = getCommonServerSupport();
            MonitorProgressObject progressObject = new MonitorProgressObject(
                    this, (Hk2TargetModuleID) targetModuleIDs[0], CommandType.UNDEPLOY);
            commonSupport.undeploy(progressObject, targetModuleIDs[0].getModuleID());
            return progressObject;
        } else {
            throw new IllegalArgumentException("No TargetModuleID's specified.");
        }
    }

    /**
     * 
     * @param targetModuleID 
     * @return 
     * @throws java.lang.IllegalStateException 
     */
    @Override
    public ProgressObject stop(TargetModuleID[] moduleIDList) throws IllegalStateException {
        return new DummyProgressObject(moduleIDList[0]);
    }

    /**
     * 
     * @param targetModuleID 
     * @return 
     * @throws java.lang.IllegalStateException 
     */
    @Override
    public ProgressObject start(TargetModuleID [] moduleIDList) throws IllegalStateException {
        return new DummyProgressObject(moduleIDList[0]);
    }

    /**
     * 
     * @param locale 
     * @throws java.lang.UnsupportedOperationException 
     */
    @Override
    public void setLocale(java.util.Locale locale) throws UnsupportedOperationException {
    }

    /**
     * 
     * @param locale 
     * @return 
     */
    @Override
    public boolean isLocaleSupported(java.util.Locale locale) {
        return false;
    }

    /**
     * 
     * @param moduleType 
     * @param target 
     * @return 
     * @throws javax.enterprise.deploy.spi.exceptions.TargetException 
     * @throws java.lang.IllegalStateException 
     */
    @Override
    public TargetModuleID [] getAvailableModules(ModuleType moduleType, Target [] targetList) 
            throws TargetException, IllegalStateException {
        return getDeployedModules(moduleType, targetList);
    }
        

    /**
     * 
     * @param moduleType 
     * @param target 
     * @return 
     * @throws javax.enterprise.deploy.spi.exceptions.TargetException 
     * @throws java.lang.IllegalStateException 
     */
    @Override
    public TargetModuleID [] getNonRunningModules(ModuleType moduleType, Target [] targetList) 
            throws TargetException, IllegalStateException {
        Logger.getLogger("glassfish-javaee").log(Level.WARNING,
                "Hk2DeploymentManager.getNonRunningModules() not supported yet.");
        return new TargetModuleID[0];
    }

    /**
     * 
     * @param moduleType 
     * @param target 
     * @return 
     * @throws javax.enterprise.deploy.spi.exceptions.TargetException 
     * @throws java.lang.IllegalStateException 
     */
    @Override
    public TargetModuleID [] getRunningModules(ModuleType moduleType, Target [] targetList) 
            throws TargetException, IllegalStateException {
        return getDeployedModules(moduleType, targetList);
    }
    
    private TargetModuleID [] getDeployedModules(ModuleType moduleType, Target [] targetList) 
            throws TargetException, IllegalStateException {
        List<TargetModuleID> moduleList = new ArrayList<TargetModuleID>();
        GlassfishModule commonSupport = getCommonServerSupport();
        if(commonSupport != null) {
            AppDesc [] appList = commonSupport.getModuleList(GlassfishModule.WEB_CONTAINER);
            if(appList != null && appList.length > 0) {
                if(targetList[0] instanceof Hk2Target) {
                    Hk2Target target = (Hk2Target) targetList[0];
                    for(AppDesc app: appList) {
                        moduleList.add(Hk2TargetModuleID.get(target, app.getName(),
                                "".equals(app.getContextRoot()) ? null : app.getContextRoot(),
                                app.getPath()));
                    }
                } else {
                    String targetDesc = targetList[0] != null ? targetList[0].toString() : "(null)";
                    throw new TargetException(NbBundle.getMessage(
                            Hk2DeploymentManager.class, "ERR_WrongTarget", targetDesc));
                }
            }
        }
        return moduleList.size() > 0 ? moduleList.toArray(new TargetModuleID[moduleList.size()]) :
            new TargetModuleID[0];
    }

    /**
     * 
     * @param dConfigBeanVersionType 
     * @throws javax.enterprise.deploy.spi.exceptions.DConfigBeanVersionUnsupportedException 
     */
    @Override
    public void setDConfigBeanVersion(DConfigBeanVersionType version) throws DConfigBeanVersionUnsupportedException {
    }

    /**
     * 
     * @param dConfigBeanVersionType 
     * @return 
     */
    @Override
    public boolean isDConfigBeanVersionSupported(DConfigBeanVersionType version) {
        return false;
    }

    /**
     * 
     */
    @Override
    public void release() {
    }

    /**
     * 
     * @return 
     */
    @Override
    public boolean isRedeploySupported() {
        return isLocal();
    }

    /**
     * 
     * @return 
     */
    @Override
    public java.util.Locale getCurrentLocale() {
        return null;
    }

    /**
     * 
     * @return 
     */
    @Override
    public DConfigBeanVersionType getDConfigBeanVersion() {
        return null;
    }

    /**
     * 
     * @return 
     */
    @Override
    public java.util.Locale getDefaultLocale() {
        return null;
    }

    /**
     * 
     * @return 
     */
    @Override
    public java.util.Locale[] getSupportedLocales() {
        return new java.util.Locale[] { java.util.Locale.getDefault() };
    }

    /**
     * 
     * @return 
     * @throws java.lang.IllegalStateException 
     */
    @Override
    public Target[] getTargets() throws IllegalStateException {
        InstanceProperties ip = getInstanceProperties();
        if (null == ip) {
            Logger.getLogger("glassfish-javaee").log(Level.INFO, "instance props are null for URI: "+getUri(), new Exception());
            return new Hk2Target[] {};
        }
        String url = ip.getProperty(GlassfishModule.URL_ATTR);
        String protocol = "http";
        String host = ip.getProperty(GlassfishModule.HOSTNAME_ATTR);
        String httpPort = getCommonServerSupport().getInstanceProperties().get(GlassfishModule.HTTPPORT_ATTR);
        if (url == null || !url.contains("ee6wc")) {
            protocol = Utils.getHttpListenerProtocol(host, httpPort);
        }

        String serverUri = constructServerUri(protocol, host, httpPort, null);
        String name = ip.getProperty(GlassfishModule.DISPLAY_NAME_ATTR);
        Hk2Target target = new Hk2Target(name, serverUri);
        Hk2Target targets[] = {target};
        return targets;
    }

    /**
     * 
     * @return 
     */
    public final String getUri() {
        return uri;
    }
    
    /**
     * 
     * @return 
     */
    public Hk2PluginProperties getProperties() {
        return pluginProperties;
    }
    
    /**
     * 
     * @return 
     */
    public InstanceProperties getInstanceProperties() {
        return instanceProperties;
    }
    
    /**
     * Get the GlassfishInstance associated with this deployment manager.
     * <p/>
     * @return GlassfishInstance associated with this deployment manager.
     */
    public ServerInstance getServerInstance() {
        if (serverInstance != null) {
            return serverInstance;
        }
        if ((serverInstance = su.getServerInstance(uri)) == null) {
            String warning = "Common server instance not found for " + uri;
            Logger.getLogger("glassfish-javaee").log(Level.WARNING, warning);
            throw new IllegalStateException(warning);
        }
        return serverInstance;
    }

    /**
     * Get a reference to the common support module for the server instance
     * associated with this deployment manager.
     * 
     * @return Reference to common server support impl.
     */
    public GlassfishModule getCommonServerSupport() {
        ServerInstance si = getServerInstance();
        return si.getBasicNode().getLookup().lookup(GlassfishModule.class);
    }
    
    private String constructServerUri(String protocol, String host, String port, String path) {
        StringBuilder builder = new StringBuilder(128);
        builder.append(protocol);
        builder.append("://"); // NOI18N
        builder.append(host);
        builder.append(":"); // NOI18N
        builder.append(port);
        if(path != null && path.length() > 0) {
            builder.append(path);
        }
        return builder.toString();
    }

    public boolean isLocal() {
        boolean result = true;
        GlassfishModule commonSupport = getCommonServerSupport();
        if(commonSupport != null && commonSupport.isRemote()) {
            result = false;
        }
        return result;
    }

    public static String getTargetFromUri(String uri) {
        String target = null;
            int lastColon = uri.lastIndexOf(':');
            if (lastColon != -1) {
                String candidate = uri.substring(lastColon+1);
                if (!Character.isDigit(candidate.charAt(0))) {
                    target = candidate;
                }
            }
        return target;
    }
}
