/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.j2ee.deployment.impl;

import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.*;
import javax.enterprise.deploy.shared.*;
import javax.enterprise.deploy.spi.status.*;

import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.deployment.plugins.spi.*;
import org.netbeans.modules.j2ee.deployment.impl.ui.DeployProgressUI;
import org.netbeans.modules.j2ee.deployment.impl.ui.DeployProgressMonitor;
import org.openide.filesystems.*;
import org.openide.cookies.InstanceCookie;
import java.util.*;
import java.io.*;
import org.openide.nodes.Node;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;
import javax.management.j2ee.Management;
import org.openide.util.RequestProcessor;

//import org.netbeans.modules.j2ee.deployment.impl.ui.actions.ShowEventWindowsAction;

public class ServerInstance implements Node.Cookie {
    
    private final String url;
    private final Server server;
    private DeploymentManager manager;
    private Management management;
    private HashMap managerWrappers;  // keyed by classnames valued by instances
    private final Set targetsStartedByIde = new HashSet(); // valued by target name
    private Map targets; // keyed by target name, valued by ServerTarget
    private boolean managerStartedByIde = false;
    
    // PENDING how to manage connected/disconnected servers with the same manager?
    // maybe concept of 'default unconnected instance' is broken?
    public ServerInstance(Server server, String url, DeploymentManager manager) {
        this.server = server; this.url = url; this.manager = manager;
    }
    
    private InstanceProperties props = null;
    private String getName() {
        if (props == null)
            props = InstanceProperties.getInstanceProperties(getUrl());
        if (props != null)
            return props.getProperty(InstanceProperties.NAME_ATTR);
        else
            return null;
    }
    
    public String getDisplayName() {
        if (getName() != null)
            return getName();
        return server.getDisplayName() + "(" + url + ")";
    }
    
    public Server getServer() {
        return server;
    }
    
    public String getUrl() {
        return url;
    }
    
    public DeploymentManager getDeploymentManager() {
        if (manager != null) return manager;
        
        try {
            FileObject fo = ServerRegistry.getInstanceFileObject(url);
            if (fo == null) {
                String msg = NbBundle.getMessage(ServerInstance.class, "MSG_NullInstanceFileObject", url);
                throw new IllegalStateException(msg);
            }
            String username = (String) fo.getAttribute(ServerRegistry.USERNAME_ATTR);
            String password = (String) fo.getAttribute(ServerRegistry.PASSWORD_ATTR);
            manager = server.getDeploymentManager(url, username, password);
        }
        catch(javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException e) {
            throw new RuntimeException(e);
        }
        return manager;
    }
    
    public void refresh() {
        if (manager != null) {
            manager.release();
            manager = null;
        }
        management = null;
        managerWrappers = null;
        targets = null;
        fireInstanceRefreshed();
    }
    
    public void remove() {
        String title = NbBundle.getMessage(ServerInstance.class, "LBL_StopServerProgressMonitor", getUrl());
        DeployProgressUI ui = new DeployProgressMonitor(title, false, true);  // modeless with stop/cancel buttons
        
        for (Iterator i=targetsStartedByIde.iterator(); i.hasNext(); ){
            String targetName = (String) i.next();
            getServerTarget(targetName).stop(ui);
        }
        stop();
        ServerRegistry.getInstance().removeServerInstance(getUrl());
    }
    
    public ServerTarget[] getTargets() {
        getTargetMap();
        return (ServerTarget[]) targets.values().toArray(new ServerTarget[targets.size()]);
    }
    
    public Collection getTargetList() {
        getTargetMap();
        return  targets.values();
    }
    
    // PENDING use targets final variable?
    private Map getTargetMap() {
        if (targets == null) {
            Target[] targs = null;
            StartServer startServer = getStartServer();
            try {
                if (! isRunning() && startServer != null && startServer.needsStartForTargetList()) {
                    start();
                }
                
                targs = getDeploymentManager().getTargets();
            } catch(IllegalStateException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
            if(targs == null)
                targs = new Target[0];
            
            targets = new HashMap();
            for (int i = 0; i < targs.length; i++) {
                //System.out.println("getTargetMap: targ["+i+"]="+targs[i]);
                targets.put(targs[i].getName(), new ServerTarget(this, targs[i]));
            }
        }
        return targets;
    }
    
    public ServerTarget getServerTarget(String targetName) {
        return (ServerTarget) getTargetMap().get(targetName);
    }
    
    public StartServer getStartServer() {
        return (StartServer) getManagerWrapper(StartServer.class);
    }
    
    public IncrementalDeployment getIncrementalDeployment() {
        return (IncrementalDeployment) getManagerWrapper(IncrementalDeployment.class);
    }
    
    private ManagementMapper mgmtMapper = null;
    public ManagementMapper getManagementMapper() {
        if (mgmtMapper == null)
            mgmtMapper = server.getManagementMapper();
        return mgmtMapper;
    }
    
    public FileDeploymentLayout getFileDeploymentLayout() {
        return (FileDeploymentLayout) getManagerWrapper(FileDeploymentLayout.class);
    }
    
    public ModuleUrlResolver getModuleUrlResolver() {
        return (ModuleUrlResolver) getManagerWrapper(ModuleUrlResolver.class);
    }
    
    public FindJSPServlet getFindJSPServlet() {
        return (FindJSPServlet) getManagerWrapper(FindJSPServlet.class);
    }

    private DeploymentManagerWrapper getManagerWrapper(Class wrapperClass) {
        return (DeploymentManagerWrapper) getManagerWrappers().get(wrapperClass.getName());
    }
    
    static Class[] wrapperClasses = new Class[] {
        StartServer.class, 
        IncrementalDeployment.class, 
        FileDeploymentLayout.class, 
        ModuleUrlResolver.class,
        FindJSPServlet.class
    };
    private Map getManagerWrappers() {
        if (getDeploymentManager() == null)
            throw new IllegalStateException("DeployementManager is not ready"); //NOI18N
        
        if (managerWrappers != null)
            return managerWrappers;
        
        //initialize cache
        managerWrappers = new HashMap(5);
        for (int i=0; i<wrapperClasses.length; i++) {
            DeploymentManagerWrapper wrapper = server.getDeploymentManagerWrapper(wrapperClasses[i]);
            if (wrapper != null) {
                wrapper.setDeploymentManager(getDeploymentManager());
                managerWrappers.put(wrapperClasses[i].getName(), wrapper);
            }
        }
        return managerWrappers;
    }
    
    //---------- State API's:  running, debuggable, startedByIDE -----------
    public boolean isRunning() {
        StartServer ss = getStartServer();
        return ss != null && ss.isRunning();
    }
    public boolean isStartedByIde() {
        return managerStartedByIde;
    }
    public boolean isDebuggable() {
        StartServer ss = getStartServer();
        Target target = getDeploymentManager().getTargets()[0];
        return ss != null && ss.isAlsoTargetServer(target) && ss.isDebuggable(target);
    }
    public boolean isDebuggable(Target target) {
        StartServer ss = getStartServer();
        return ss != null && ss.isDebuggable(target);
    }
    //PENDING: StopOnExit implementation, loosend for now
    public boolean startedByIde() {
        return managerStartedByIde;
    }
    /**
     * Return set of ServerTarget's that have been started from inside IDE.
     * @return set of ServerTarget objects.
     */
    public Set getTargetsStartedByIde() {
        Set ret = new HashSet();
        for (Iterator i=targetsStartedByIde.iterator(); i.hasNext(); ) {
            String targetName = (String) i.next();
            ret.add(getServerTarget(targetName));
        }
        return ret;
    }
    
    //----------- State Transistion API's: ----------------------
    // Note: configuration needs
    /**
     * Return a connected DeploymentManager if needed by server platform for configuration
     * @return DeploymentManager object for configuration.
     */
    public DeploymentManager getDeploymentManagerForConfiguration() {
        StartServer ss = getStartServer();
        if (ss != null && ss.needsStartForConfigure())
            start();
        
        return getDeploymentManager();
    }
    
    // Note: execution only need these 3 state transition APIs
    
    /**
     * Start the admin server.
     * Note: for debug mode, always use startDebugTarget() calls because
     * it is sure then the target need to be started.
     * @return true if successful.
     */
    public boolean start(DeployProgressUI ui) {
        if (isRunning())
            return true;
        
        return _start(ui);
    }
    /**
     * Start specified target server.  If it is also admin server only make sure
     * admin server is running.
     * @param target target server to be started
     * @param ui DeployProgressUI to display start progress
     * @return true when server is started successfully; else return false.
     */
    public boolean startTarget(Target target, DeployProgressUI ui) {
        return startTarget(target, ui, false);
    }
    
    /**
     * Start specified target server in debug mode.  If target is also admin
     * server only make sure admin server is running.
     * @param target target server to be started
     * @param ui DeployProgressUI to display start progress
     * @return true when server is started successfully; else return false.
     */
    public boolean startDebugTarget(Target target, DeployProgressUI ui) {
        return startTarget(target, ui, true);
    }
    
    // Note: these 2 state transiont APIs are mainly for registry actions with no existing progress UI
    
    /**
     * Start admin server.
     */
    public boolean start() {
        if (isRunning())
            return true;
        
        if (! RequestProcessor.getDefault().isRequestProcessorThread()) {
            //PENDING maybe a modal dialog instead of async is needed here
            RequestProcessor.Task t = RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    _start();
                }
            }, 0, Thread.MAX_PRIORITY);
            return false;
        } else {
            String title = NbBundle.getMessage(ServerInstance.class, "LBL_StartServerProgressMonitor", getUrl());
            final DeployProgressUI ui = new DeployProgressMonitor(title, false, true);  // modeless with stop/cancel buttons
            ui.startProgressUI(10);
            if (_start(ui)) {
                ui.recordWork(10);
                return true;
            }
            return false;
        }
    }
    
    /**
     * Stop admin server.
     */
    public void stop(DeployProgressUI ui) {
        _stop(ui);
    }
    public void stop() {
        String title = NbBundle.getMessage(ServerInstance.class, "LBL_StopServerProgressMonitor", getUrl());
        DeployProgressUI ui = new DeployProgressMonitor(title, false, true);  // modeless with stop/cancel buttons
        ui.startProgressUI(10);
        _stop(ui);
        //TESTING
        //_test_stop(getCoTarget().getTarget(), ui);
        ui.recordWork(10);
    }
    
    //------------------------------------------------------------
    // state-machine core
    private boolean startTarget(Target target, DeployProgressUI ui, boolean debugMode) {
        StartServer ss = getStartServer();
        String error = checkStartServer(ss);
        if (error != null) {
            handleStartServerError(ui, error);
            return false;
        }
        
        if (ss.isAlsoTargetServer(target)) {
            if (debugMode) {
                if (ss.isDebuggable(target)) { // imply ss.isRunning() true in
                    return true;
                } else if (ss.isRunning()) {
                    if (! _stop(ui))
                        return false;
                }
                return _startDebug(target, ui);
            }
            else if (isRunning()) {
                //System.out.println("INFO: Server "+getUrl()+" is already running!");
                return true;
            } else {
                return _start(ui);
            }
        } else { // not also target server
            if (! ss.isRunning()) {
                if (! _start(ui))
                    return false;
            }
            if (debugMode) {
                if (ss.isDebuggable(target))
                    return true;
                else {
                    return _startDebug(target, ui);
                }
            } else {
                return _start(target, ui);
            }
        }
    }
    
    //------------------------------------------------------------
    // state-transition atomic operations
    //------------------------------------------------------------
    // startDeploymentManager
    private synchronized boolean _start(DeployProgressUI ui) {
        
        DeployProgressUI.CancelHandler ch = getCancelHandler();
        ui.addCancelHandler(ch);
        
        try {
            StartServer ss = getStartServer();
            String errorMessage = checkStartDM(ss);
            if (errorMessage != null) {
                handleStartServerError(ui, errorMessage);
                return false;
            }
            
            ProgressObject po = ss.startDeploymentManager();
            
            ui.setProgressObject(po);
            po.addProgressListener(new StartProgressHandler());
            
            // wait until done or cancelled
            boolean done = sleep();
            if (! done)
                showStatusText(NbBundle.getMessage(ServerInstance.class, "MSG_StartServerTimeout", url));
            
            if (ui.checkCancelled() || ! done)
                return false;
            
            managerStartedByIde = true;
            refresh();
            return true;
            
        } finally {
            ui.removeCancelHandler(ch);
        }
    }


    private synchronized void _start() {
        StartServer ss = getStartServer();
        String errorMessage = checkStartDM(ss);
        if (errorMessage != null) {
            handleStartServerError(null, errorMessage);
            return;
        }
        
        showStatusText(NbBundle.getMessage(ServerInstance.class, "MSG_StartingServer", url));
        ProgressObject po = ss.startDeploymentManager();
        po.addProgressListener(new StartProgressHandler());
        
        // wait until done or cancelled
        boolean done = sleep();
        if (! done) {
            showStatusText(NbBundle.getMessage(ServerInstance.class, "MSG_StartServerTimeout", url));
        } else {
            showStatusText(NbBundle.getMessage(ServerInstance.class, "MSG_StartedServer", url));
            managerStartedByIde = true;
            refresh();
        }
    }
    
    // startDebugging
    private synchronized boolean _startDebug(Target target, DeployProgressUI ui) {
        DeployProgressUI.CancelHandler ch = getCancelHandler();
        ui.addCancelHandler(ch);
        
        try {
            StartServer ss = getStartServer();
            ProgressObject po = ss.startDebugging(target);
            ui.setProgressObject(po);
            po.addProgressListener(new StartProgressHandler());
            
            // wait until done or cancelled
            boolean done = sleep();
            if (! done)
                showStatusText(NbBundle.getMessage(ServerInstance.class, "MSG_StartDebugTimeout", url));
            
            if (ui.checkCancelled() || ! done)
                return false;
            
            managerStartedByIde = true;
            return true;
            
        } finally {
            ui.removeCancelHandler(ch);
        }
    }
    // stopDeploymentManager
    private synchronized boolean _stop(DeployProgressUI ui) {
        DeployProgressUI.CancelHandler ch = getCancelHandler();
        ui.addCancelHandler(ch);
        
        try {
            StartServer ss = getStartServer();
            ProgressObject po = ss.stopDeploymentManager();
            ui.setProgressObject(po);
            po.addProgressListener(new StartProgressHandler());
            
            // wait until done or cancelled
            boolean done = sleep();
            if (! done)
                showStatusText(NbBundle.getMessage(ServerInstance.class, "MSG_StopServerTimeout", url));
            
            if (ui.checkCancelled() || ! done)
                return false;
            
            managerStartedByIde = false;
            return true;
            
        } finally {
            ui.removeCancelHandler(ch);
        }
    }
    
    // start target server using jsr77
    private boolean _start(Target target, DeployProgressUI ui) {
        ServerTarget serverTarget = getServerTarget(target.getName());
        if (serverTarget.isRunning())
            return true;
        if (! serverTarget.start(ui))
            return false;
        
        targetsStartedByIde.add(serverTarget.getName());
        return true;
    }
    
    // stop target server using jsr77
    private boolean _stop(Target target, DeployProgressUI ui) {
        ServerTarget serverTarget = getServerTarget(target.getName());
        if (! serverTarget.isRunning())
            return true;
        if (! serverTarget.stop(ui))
            return false;
        
        targetsStartedByIde.remove(serverTarget.getName());
        return true;
    }
    
    //-------------- End state-machine operations -------------------
    public boolean _test_stop(Target target, DeployProgressUI ui) {
        return _stop(target, ui);
    }
    public void reportError(String errorText) {
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(errorText));
    }
    
    private boolean handleStartServerError(DeployProgressUI ui, String errorMessage) {
        // Precondition: ui needs to have cancel handler added
        ui.addError(errorMessage);
        
        //sleepTillCancel();
        return false;
    }
    
    private String checkStartServer(StartServer ss) {
        if (ss == null)
            return NbBundle.getMessage(ServerInstance.class, "MSG_PluginHasNoStartServerClass", getServer());
        return null;
    }
    
    private String checkStartDM(StartServer ss) {
        if (! ss.supportsStartDeploymentManager()) {
            return NbBundle.getMessage(ServerInstance.class, "MSG_StartingThisServerNotSupported", getUrl());
        }
        return null;
    }
    
    private class StartProgressHandler implements ProgressListener {
        public StartProgressHandler() {
        }
        public void handleProgressEvent(ProgressEvent progressEvent) {
            DeploymentStatus status = progressEvent.getDeploymentStatus();
            StateType state = status.getState();
            if (state != StateType.RUNNING)
                ServerInstance.this.wakeUp();
        }
    }
    private DeployProgressUI.CancelHandler getCancelHandler() {
        DeployProgressUI.CancelHandler ch = new DeployProgressUI.CancelHandler() {
            public void handle() {
                ServerInstance.this.wakeUp();
            }
        };
        return ch;
    }
    private synchronized void wakeUp() {
        notify();
    }
    private synchronized void sleepTillCancel() {
        try {
            wait();
        } catch (Exception e) { }
    }
    private static long TIMEOUT = 180000;
    //return false when timeout or interrupted
    private synchronized boolean sleep() {
        try {
            long t0 = System.currentTimeMillis();
            wait(TIMEOUT);
            return (System.currentTimeMillis() - t0) < TIMEOUT;
        } catch (Exception e) { return false; }
    }
    
    public boolean isManagerOf(Target target) {
        return getTargetMap().keySet().contains(target.getName());
    }
    
    public static interface RefreshListener {
        public void handleRefresh() ;
    }
    
    Vector rListeners = new Vector();
    public void addRefreshListener(RefreshListener rl) {
        rListeners.add(rl);
    }
    public void removeRefreshListener(RefreshListener rl) {
        rListeners.remove(rl);
    }
    private void fireInstanceRefreshed() {
        for (Iterator i=rListeners.iterator(); i.hasNext();) {
            RefreshListener rl = (RefreshListener) i.next();
            rl.handleRefresh();
        }
    }
    private void showStatusText(String msg) {
        org.openide.awt.StatusDisplayer.getDefault().setStatusText(msg);
    }
    
    public ServerTarget getCoTarget() {
        /*if (! isRunning() && getStartServer().needsStartForTargetList())
            return null;*/
        
        ServerTarget[] childs = getTargets();
        for (int i=0; i<childs.length; i++) {
            if (getStartServer().isAlsoTargetServer(childs[i].getTarget()))
                return childs[i];
        }
        return null;
    }
    
    /*private ShowEventWindowsAction.EventLogProvider getEventLogProviderCookie() {
        
        return new ShowEventWindowsAction.EventLogProvider() {
            
            public void showEventWindows() {
                org.openide.windows.InputOutput ios[] = getStartServer().getServerOutput(null);
                if (ios != null) {
                    for (int i=0; i<ios.length; i++) {
                        ios[i].select();
                    }
                }
            }
        };
    }*/

    public Management getManagement() {
        if (management != null) return management;
        
        if (getManagementMapper() == null)
            return null;
        management = getManagementMapper().getManagement(getDeploymentManager());
        return management;
    }
    
    public javax.management.j2ee.ListenerRegistration getListenerRegistry() {
        if (getManagement() == null)
            return null;
        try {
            return getManagement().getListenerRegistry();
        } catch(Exception ex) {
            ErrorManager.getDefault().log(ex.getMessage());
        }
        return null;
    }
    
    
}
