/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.j2ee.weblogic9;


import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

import javax.enterprise.deploy.model.*;
import javax.enterprise.deploy.shared.*;
import javax.enterprise.deploy.spi.*;
import javax.enterprise.deploy.spi.exceptions.*;
import javax.enterprise.deploy.spi.factories.*;
import javax.enterprise.deploy.spi.status.*;
import org.netbeans.modules.j2ee.weblogic9.config.DepConfig;

import org.openide.*;
import org.openide.util.*;
import org.netbeans.modules.j2ee.deployment.plugins.api.*;

import org.netbeans.modules.j2ee.weblogic9.util.WLDebug;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Main class of the deployment process. This serves a a wrapper for the 
 * server's DeploymentManager implementation, all calls are delegated to the
 * server's implementation, with the thread's context classloader updated
 * if necessary.
 * 
 * @author Kirill Sorokin
 */
public class WLDeploymentManager implements DeploymentManager {
    
    private DeploymentManager dm;
    private InstanceProperties instanceProperties;
    private String uri;
    private String username;
    private String password;
    private boolean isConnected;
    private String host;
    private String port;
    
    /** Create connected DM */
    public WLDeploymentManager(DeploymentManager dm, String uri, String username, String password, String host, String port) {
        this.dm = dm;
        this.uri = uri;
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.isConnected = true;
    }
    
    /** Create disconnected DM */
    public WLDeploymentManager(DeploymentManager dm, String uri, String host, String port) {
        this.dm = dm;
        this.uri = uri;
        this.host = host;
        this.port = port;
        this.isConnected = false;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Connection data methods
    ////////////////////////////////////////////////////////////////////////////
    
    public boolean isConnected() {
        return isConnected;
    }
    
    /**
     * Returns the stored server URI
     */
    public String getURI() {
        return this.uri;
    }
    
    /**
     * Returns the server host stored in the instance properties
     */
    public String getHost() {
        return host;
    }
    
    public String getUsername () {
        return getInstanceProperties().getProperty(InstanceProperties.USERNAME_ATTR);
    }
    
    public String getPassword () {
        return getInstanceProperties().getProperty(InstanceProperties.PASSWORD_ATTR);
    }
    
    /**
     * Returns the server port stored in the instance properties
     */
    public String getPort() {
        return port;
    }
    
    public boolean isLocal () {
        return Boolean.parseBoolean(getInstanceProperties().getProperty(WLDeploymentFactory.IS_LOCAL_ATTR));
    }
    /**
     * Returns the InstanceProperties object for the current server instance
     */
    public InstanceProperties getInstanceProperties() {
        if (instanceProperties == null) {
            this.instanceProperties = InstanceProperties.getInstanceProperties(uri);
            
        }
        return instanceProperties;
    }
    
    public ProgressObject distribute(Target[] target, File file, File file2) 
            throws IllegalStateException {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify(getClass(), "distribute(" + target + ", " + // NOI18N
                    file + ", " + file2 + ")");                        // NOI18N
        
        if (isLocal()) {
            //autodeployment version
            WLDeployer deployer = new WLDeployer(uri);

            org.w3c.dom.Document dom =null;


            WLTargetModuleID module_id = new WLTargetModuleID(target[0], file.getName() );

            try{
                String server_url = "http://" + getHost()+":"+getPort();
                if (file2 != null){
                    dom = org.openide.xml.XMLUtil.parse(new org.xml.sax.InputSource(new FileInputStream( file2 )), false, false,null, null);
                    String doctype = dom.getDocumentElement().getNodeName();
                    if (doctype.equals("weblogic-application")){

        //TODO  for application try using context root from application.xml
                        NodeList nlist = dom.getElementsByTagName("module");

                        for (int i = 0; i < nlist.getLength(); i++){
                            Element module_node = (Element)((Element)nlist.item(i)).getElementsByTagName("*").item(0);
                            WLTargetModuleID child_module = new WLTargetModuleID( target[0] );

                            if ( module_node.getTagName().equals("web")) {
                                //child_module.setJARName(module_node.getElementsByTagName("web-uri").item(0).getTextContent().trim()); // jdk 1.5
                                child_module.setJARName(module_node.getElementsByTagName("web-uri").item(0).getFirstChild().getNodeValue().trim()); // jdk 1.4
                                //child_module.setContextURL("http://" + getHost()+":"+getPort() + module_node.getElementsByTagName("context-root").item(0).getTextContent().trim()); // jdk 1.5
                                child_module.setContextURL("http://" + getHost()+":"+getPort() + module_node.getElementsByTagName("context-root").item(0).getFirstChild().getNodeValue().trim()); // jdk 1.4
                            } else if(module_node.getTagName().equals("ejb")){
        //                        child_module.setJARName(nlist.item(i).getTextContent().trim()); // jdk 1.5
                                child_module.setJARName(nlist.item(i).getFirstChild().getNodeValue().trim()); // jdk 1.4
                            }
                            module_id.addChild( child_module );
                        }

                    } else if (doctype.equals("weblogic-web-app")){
                       // module_id.setContextURL( server_url + dom.getElementsByTagName("context-root").item(0).getTextContent().trim()); // jdk 1.5
                        module_id.setContextURL( server_url + dom.getElementsByTagName("context-root").item(0).getFirstChild().getNodeValue().trim()); // jdk 1.4
                    } else if (doctype.equals("weblogic-ejb-jar")) {

                    }
                } else {
                    String fname = file.getName();
                    int dot = fname.lastIndexOf('.');
                    if (dot > 0)
                        fname = fname.substring(0, dot);
                    module_id.setContextURL(server_url+"/"+fname);
                }
            }catch(Exception e){
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
            deployer.deploy(target, file, file2, module_id);
            return deployer;
        } else {
            //weblogic jsr88 version
            modifiedLoader();
            try {
                return dm.distribute(target, file, file2);
            } finally {
                originalLoader();
            }
        }
    }
    
    private ClassLoader swapLoader;
    
    private void modifiedLoader() {
        swapLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(WLDeploymentFactory.getWLClassLoader());
    }
    private void originalLoader() {
        Thread.currentThread().setContextClassLoader(swapLoader);
    }
    
    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     * 
     * @return a wrapper for the server's DeploymentConfiguration implementation
     */
    public DeploymentConfiguration createConfiguration(
        DeployableObject deployableObject) throws InvalidModuleException {
        return new DepConfig(deployableObject,this);
    }
    
    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public ProgressObject redeploy(TargetModuleID[] targetModuleID, 
            InputStream inputStream, InputStream inputStream2) 
            throws UnsupportedOperationException, IllegalStateException {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("redeploy(" + targetModuleID + ", " +       // NOI18N
                    inputStream + ", " + inputStream2 + ")");          // NOI18N
        modifiedLoader();
        try {
            return dm.redeploy(targetModuleID, inputStream, inputStream2);
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public ProgressObject distribute(Target[] target, InputStream inputStream, 
            InputStream inputStream2) throws IllegalStateException {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("distribute(" + target + ", " +             // NOI18N
                    inputStream + ", " + inputStream2 + ")");          // NOI18N
        modifiedLoader();
        try {
            return dm.distribute(target, inputStream, inputStream2);
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public ProgressObject undeploy(TargetModuleID[] targetModuleID) 
            throws IllegalStateException {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("undeploy(" + targetModuleID + ")");        // NOI18N
        modifiedLoader();
        try {
            return dm.undeploy(targetModuleID);
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public ProgressObject stop(TargetModuleID[] targetModuleID) 
            throws IllegalStateException {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("stop(" + targetModuleID + ")");            // NOI18N
                
        modifiedLoader();
        try {
            return dm.stop(targetModuleID);
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public ProgressObject start(TargetModuleID[] targetModuleID) 
            throws IllegalStateException {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("start(" + targetModuleID + ")");           // NOI18N
        
        modifiedLoader();
        try {
            return dm.start(targetModuleID);
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public TargetModuleID[] getAvailableModules(ModuleType moduleType, 
            Target[] target) throws TargetException, IllegalStateException {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("getAvailableModules(" + moduleType +       // NOI18N
                    ", " + target + ")");                              // NOI18N
        
        modifiedLoader();
        try {
            TargetModuleID t[] = dm.getAvailableModules(moduleType, target);
            for (int i=0; i < t.length; i++) {
                System.out.println("available module:" + t[i]);
            }
            return t;
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public TargetModuleID[] getNonRunningModules(ModuleType moduleType, 
            Target[] target) throws TargetException, IllegalStateException {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("getNonRunningModules(" + moduleType +      // NOI18N
                    ", " + target + ")");                              // NOI18N
        
        modifiedLoader();
        try {
            TargetModuleID t[] = dm.getNonRunningModules(moduleType, target);
            for (int i=0; i < t.length; i++) {
                System.out.println("non running module:" + t[i]);
            }
            return t;
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public TargetModuleID[] getRunningModules(ModuleType moduleType, 
            Target[] target) throws TargetException, IllegalStateException {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("getRunningModules(" + moduleType +         // NOI18N
                    ", " + target + ")");                              // NOI18N
        
        modifiedLoader();
        try {
            TargetModuleID t[] = dm.getRunningModules(moduleType, target);
            for (int i=0; i < t.length; i++) {
                System.out.println("running module:" + t[i]);
            }
            return t;
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public ProgressObject redeploy(TargetModuleID[] targetModuleID, File file, 
            File file2) throws UnsupportedOperationException, 
            IllegalStateException {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("redeploy(" + targetModuleID + ", " +       // NOI18N
                    file + ", " + file2 + ")");                        // NOI18N
        
        modifiedLoader();
        try {
            return dm.redeploy(targetModuleID, file, file2);
        } finally {
            originalLoader();
        }
    }
    
    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public void setLocale(Locale locale) throws UnsupportedOperationException {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("setLocale(" + locale + ")");               // NOI18N
        
        modifiedLoader();
        try {
            dm.setLocale(locale);
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public boolean isLocaleSupported(Locale locale) {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("isLocaleSupported(" + locale + ")");       // NOI18N
        
        modifiedLoader();
        try {
            return dm.isLocaleSupported(locale);
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public void setDConfigBeanVersion(
            DConfigBeanVersionType dConfigBeanVersionType) 
            throws DConfigBeanVersionUnsupportedException {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("setDConfigBeanVersion(" +                  // NOI18N
                    dConfigBeanVersionType + ")");                     // NOI18N
        
        modifiedLoader();
        try {
            dm.setDConfigBeanVersion(dConfigBeanVersionType);
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public boolean isDConfigBeanVersionSupported(
            DConfigBeanVersionType dConfigBeanVersionType) {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("isDConfigBeanVersionSupported(" +          // NOI18N
                    dConfigBeanVersionType + ")");                     // NOI18N
        
        modifiedLoader();
        try {
            return dm.isDConfigBeanVersionSupported(dConfigBeanVersionType);
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public void release() {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("release()");                               // NOI18N
        
        modifiedLoader();
        try {
            if (dm != null) {
                // delegate the call and clear the stored deployment manager
                dm.release();
                dm = null;
            }
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public boolean isRedeploySupported() {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("isRedeploySupported()");                   // NOI18N
        
        modifiedLoader();
        try {
            return dm.isRedeploySupported();
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public Locale getCurrentLocale() {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("getCurrentLocale()");                      // NOI18N
        
        modifiedLoader();
        try {
            return dm.getCurrentLocale();
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public DConfigBeanVersionType getDConfigBeanVersion() {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("getDConfigBeanVersion()");                 // NOI18N

        modifiedLoader();
        try {
            return dm.getDConfigBeanVersion();
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public Locale getDefaultLocale() {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("getDefaultLocale()");                      // NOI18N
        
        modifiedLoader();
        try {
            return dm.getDefaultLocale();
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public Locale[] getSupportedLocales() {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("getSupportedLocales()");                   // NOI18N
        
        modifiedLoader();
        try {
            return dm.getSupportedLocales();
        } finally {
            originalLoader();
        }
    }

    /**
     * Delegates the call to the server's deployment manager, checking whether 
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public Target[] getTargets() throws IllegalStateException {
        if (WLDebug.isEnabled()) // debug output
            WLDebug.notify("getTargets()");                            // NOI18N
        
        modifiedLoader();
        try {
            return dm.getTargets();
        } finally {
            originalLoader();
        }
    }
    
}