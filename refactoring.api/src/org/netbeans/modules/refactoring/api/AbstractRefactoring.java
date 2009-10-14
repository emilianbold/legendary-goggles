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
package org.netbeans.modules.refactoring.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.modules.refactoring.api.impl.APIAccessor;
import org.netbeans.modules.refactoring.api.impl.ProgressSupport;
import org.netbeans.modules.refactoring.api.impl.SPIAccessor;
import org.netbeans.modules.refactoring.spi.GuardedBlockHandler;
import org.netbeans.modules.refactoring.spi.GuardedBlockHandlerFactory;
import org.netbeans.modules.refactoring.spi.ProgressProvider;
import org.netbeans.modules.refactoring.spi.ReadOnlyFilesHandler;
import org.netbeans.modules.refactoring.spi.RefactoringPlugin;
import org.netbeans.modules.refactoring.spi.RefactoringPluginFactory;
import org.netbeans.modules.refactoring.spi.impl.Util;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;
import org.netbeans.modules.refactoring.spi.impl.RefactoringPanel;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Parameters;
import org.openide.util.lookup.InstanceContent;


/**
 * Abstract superclass for particular refactorings.
 * Methods should be typically called in following order:
 * <ul>
 *  <li>preCheck
 *  <li>setParameter1(..), setParameter2(..) (this methods will typically be added by the subclass
 *  to allow parametrization of the implemented refactoring)
 *  <li>fastCheckParameters() (performs only fast check - useful for online error checking)
 *  <li>checkParameters() (full check of parameters)
 *  <li>prepare() (collects usages)
 * </ul>
 * @see RefactoringSession
 * @author Martin Matula, Jan Becicka
 */
public abstract class AbstractRefactoring {

    static {
        APIAccessor.DEFAULT = new AccessorImpl();
    }
    
    /**
     * Initial state
     */
    public static final int INIT = 0;
    /** Pre-check state. */
    public static final int PRE_CHECK = 1;
    /** Parameters check state. */
    public static final int PARAMETERS_CHECK = 2;
    /** Prepare state. */
    public static final int PREPARE = 3;
    
    private int currentState = INIT;
    
    private static final int PLUGIN_STEPS = 30;
    
    private ArrayList plugins;
    
    ArrayList pluginsWithProgress;
    
    private ArrayList gbHandlers;
    
    private ProgressL progressListener = new ProgressL();
    
    private ProgressSupport progressSupport;
    
    Lookup refactoringSource;
    
    protected AbstractRefactoring(Lookup refactoringSource) {
        Parameters.notNull("refactoringSource", refactoringSource); // NOI18N
        this.refactoringSource = refactoringSource;
    }
    
    private Collection getPlugins() {
        if (plugins == null) {
            plugins = new ArrayList();
            // get plugins from the lookup
            Lookup.Result result = Lookup.getDefault().lookup(new Lookup.Template(RefactoringPluginFactory.class));
            for (Iterator it = result.allInstances().iterator(); it.hasNext();) {
                RefactoringPluginFactory factory = (RefactoringPluginFactory) it.next();
                RefactoringPlugin plugin = factory.createInstance(this);
                if (plugin != null)  {
                    RefactoringPlugin callerPlugin = getContext().lookup(RefactoringPlugin.class);
                    AbstractRefactoring caller = getContext().lookup(AbstractRefactoring.class);
                    if (caller == null || factory.getClass().getClassLoader().equals(callerPlugin.getClass().getClassLoader()) || factory.createInstance(caller)==null) {
                        //caller is internal non-api field. Plugin is always added for API calls.
                        //For non-api internal calls: 
                        //  Plugins from different modules are ignored, 
                        //  if factory for the caller return non-null plugin.
                        //  This quite hacky method is used for SafeDeleteRefactoring:
                        //  SafeDeleteRefactorinPlugin uses WhereUsedQuery internally.
                        //  If some module implements both plugins (SafeDelete and WhereUsed),
                        //  WhereUsedRefactoringPlugin should be ignored, because whole process will be handled by
                        //  SafeDeleteRefactoringPlugin.
                        //  #65980
                        plugins.add(plugin);
                    }
                }
            }
        }
        return plugins;
    }
    
    Collection getGBHandlers() {
        if (gbHandlers == null) {
            gbHandlers = new ArrayList();
            // get plugins from the lookup
            Lookup.Result result = Lookup.getDefault().lookup(new Lookup.Template(GuardedBlockHandlerFactory.class));
            for (Iterator it = result.allInstances().iterator(); it.hasNext();) {
                GuardedBlockHandler handler = ((GuardedBlockHandlerFactory) it.next()).createInstance(this);
                if (handler != null) gbHandlers.add(handler);
            }
        }
        return gbHandlers;
    }
    
    /** Perform checks to ensure that the preconditions are met for the implemented
     * refactoring.
     * @return Chain of problems encountered or <code>null</code> if no problems
     * were found.
     */
    @CheckForNull
    public final Problem preCheck() {
//        //workaround for #68803
//        if (!(this instanceof WhereUsedQuery)) {
//            if (progressSupport != null)
//                progressSupport.fireProgressListenerStart(this, ProgressEvent.START, -1);
//            setCP();
//            if (progressSupport != null)
//                progressSupport.fireProgressListenerStop(this);
//        }
        currentState = PRE_CHECK;
        return pluginsPreCheck(null);
    }
    
    /** Collects and returns a set of refactoring elements - objects that
     * will be affected by the refactoring.
     * @param session RefactoringSession that the operation will use to return
     * instances of {@link org.netbeans.modules.refactoring.api.RefactoringElement} class representing objects that
     * will be affected by the refactoring.
     * @return Chain of problems encountered or <code>null</code> in no problems
     * were found.
     */
    @CheckForNull
    public final Problem prepare(@NonNull RefactoringSession session) {
        Parameters.notNull("session", session); // NOI18N
        long time = System.currentTimeMillis();

        Problem p = null;
        boolean checkCalled = false;
        if (currentState < PARAMETERS_CHECK) {
            p = checkParameters();
            checkCalled = true;
        }
        if (p != null && p.isFatal())
            return p;

        p =  pluginsPrepare(checkCalled?p:null, session);
        Logger timer = Logger.getLogger("TIMER.RefactoringPrepare");
        if (timer.isLoggable(Level.FINE)) {
            time = System.currentTimeMillis() - time;
            timer.log(Level.FINE, "refactoring.prepare", new Object[] { this, time } );
        }
        return p;
    }
    
    /**
     * Checks if this refactoring has correctly set all parameters.
     * @return Returns instance of Problem or null
     */
    @CheckForNull
    public final Problem checkParameters() {
//        //workaround for #68803
//        if (this instanceof WhereUsedQuery) {
//            if (progressSupport != null)
//                progressSupport.fireProgressListenerStart(this, ProgressEvent.START, -1);
//            setCP();
//            if (progressSupport != null)
//                progressSupport.fireProgressListenerStop(this);
//        } else {
//            setCP();
//        }
        Problem p = fastCheckParameters();
        if (p != null && p.isFatal())
            return p;
        currentState = PARAMETERS_CHECK;
        return pluginsCheckParams(p);
    }
    
    /**
     * This method checks parameters. Its implementation is fast and allows on-line checking of errors.
     * If you want complete check of parameters, use #checkParameters()
     * @return Returns instance of Problem or null
     */
    @CheckForNull
    public final Problem fastCheckParameters() {
        // Do not set classpath - use default merged class path
        // #57558
        // setCP();
        Problem p = null;
        if (currentState < PRE_CHECK) {
            p = preCheck();
        }
        if (p != null && p.isFatal())
            return p;
        return pluginsFastCheckParams(p);
    }
    
    /** Registers ProgressListener to receive events.
     * @param listener The listener to register.
     *
     */
    public final synchronized void addProgressListener(@NonNull ProgressListener listener) {
        Parameters.notNull("listener", listener); // NOI18N
        if (progressSupport == null ) {
            progressSupport = new ProgressSupport();
        }
        progressSupport.addProgressListener(listener);
        
        if (pluginsWithProgress == null) {
            pluginsWithProgress = new ArrayList();
            Iterator pIt=getPlugins().iterator();
            while(pIt.hasNext()) {
                RefactoringPlugin plugin=(RefactoringPlugin)pIt.next();
                if (plugin instanceof ProgressProvider) {
                    ((ProgressProvider) plugin).addProgressListener(progressListener);
                    pluginsWithProgress.add(plugin);
                }
            }
        }
    }
    
    /** Removes ProgressListener from the list of listeners.
     * @param listener The listener to remove.
     *
     */
    public final synchronized void removeProgressListener(@NonNull ProgressListener listener) {
        Parameters.notNull("listener", listener); // NOI18N
        if (progressSupport != null ) {
            progressSupport.removeProgressListener(listener); 
        }

        if (pluginsWithProgress != null) {
            Iterator pIt=pluginsWithProgress.iterator();
            
            while(pIt.hasNext()) {
                ProgressProvider plugin=(ProgressProvider)pIt.next();
                plugin.removeProgressListener(progressListener);
            }
            pluginsWithProgress.clear();
            pluginsWithProgress = null;
       }
    }
    
    /**
     * getter for refactoring Context
     * @see Context
     * @return context in which the refactoring was invoked.
     */
    @NonNull
    public final Context getContext() {
        if (this.scope == null) {
            this.scope=new Context(new InstanceContent());
        }
        return this.scope;
    }
    
    /**
     * Object being refactored
     * @return 
     */
    @NonNull
    public final Lookup getRefactoringSource() {
        return refactoringSource;
    }
    
    private Context scope;
    
    private volatile boolean cancel;
    /**
     * Asynchronous request to cancel ongoing long-term request (such as preCheck(), checkParameters() or prepare())
     */
    public final void cancelRequest() {
        cancel = true;
        Iterator pIt=getPlugins().iterator();
        
        while(pIt.hasNext()) {
            RefactoringPlugin plugin=(RefactoringPlugin)pIt.next();
            plugin.cancelRequest();
        }
    }
    
    private Problem pluginsPreCheck(Problem problem) {
        try {
            progressListener.start();
            Iterator pIt=getPlugins().iterator();

            while(pIt.hasNext()) {
                if (cancel)
                    return null;
                RefactoringPlugin plugin=(RefactoringPlugin)pIt.next();

                try {
                    problem=chainProblems(plugin.preCheck(),problem);
                } catch (Throwable t) {
                    problem =createProblemAndLog(problem, t, plugin.getClass());
                }
                if (problem!=null && problem.isFatal())
                    return problem;
            }
            return problem;
        } finally {
            progressListener.stop();
        }
    }
    
    private String getModuleName(Class c) {
        for (ModuleInfo info:Lookup.getDefault().lookupAll(ModuleInfo.class)) {
            if (info.owns(c)) {
                return info.getDisplayName();
            }
        }
        return "Unknown";//NOI18N
    }
    
    private String createMessage(Class c, Throwable t) {
        return NbBundle.getMessage(RefactoringPanel.class, "ERR_ExceptionInModule", getModuleName(c), t.toString());
    }
    
    private Problem createProblemAndLog(Problem p, Throwable t, Class source) {
        Throwable cause = t.getCause();
        Problem newProblem;
        if (cause != null && (cause.getClass().getName().equals("org.netbeans.api.java.source.JavaSource$InsufficientMemoryException") ||  //NOI18N
            (cause.getCause()!=null ) && cause.getCause().getClass().getName().equals("org.netbeans.api.java.source.JavaSource$InsufficientMemoryException"))) { //NOI18N
            newProblem = new Problem(true, NbBundle.getMessage(Util.class, "ERR_OutOfMemory"));
            Logger.global.log(Level.INFO, "There is not enough memory to complete this task.", t);
        } else {
            newProblem = new Problem(false, createMessage(source, t));
            Exceptions.printStackTrace(t);
        }
        return chainProblems(newProblem, p);
    }
    
    private Problem pluginsPrepare(Problem problem, RefactoringSession session) {
        try {
            progressListener.start();
            return pluginsPrepare2(problem, session);
        } finally {
            progressListener.stop();
        }
    }
    
    private Problem pluginsPrepare2(Problem problem, RefactoringSession session) {
        RefactoringElementsBag elements = session.getElementsBag();
        Iterator pIt=getPlugins().iterator();
        
        while(pIt.hasNext()) {
            if (cancel)
                return null;
            RefactoringPlugin plugin=(RefactoringPlugin)pIt.next();
            
            try {
                problem=chainProblems(plugin.prepare(elements),problem);
            } catch (Throwable t) {
                problem =createProblemAndLog(problem, t, plugin.getClass());
            }
            if (problem!=null && problem.isFatal())
                return problem;
        }
        
        //TODO: 
        //following condition "!(this instanceof WhereUsedQuery)" is hotfix of #65785
        //correct solution would probably be this condition: "!isQuery()"
        //unfortunately isQuery() is not in AbstractRefactoring class, but in RefactoringIU
        //we should consider moving this method to AbstractRefactoring class in future release
        if (!(this instanceof WhereUsedQuery)) {
            ReadOnlyFilesHandler handler = getROHandler();
            if (handler!=null) {
                Collection files = SPIAccessor.DEFAULT.getReadOnlyFiles(elements);
                Collection allFiles = new HashSet();
                for (Iterator i = files.iterator(); i.hasNext();) {
                    FileObject f = (FileObject) i.next();
                    DataObject dob;
                    try {
                        dob = DataObject.find(f);
                        for (Iterator j = dob.files().iterator(); j.hasNext();) {
                            FileObject file = (FileObject) j.next();
                            if (SharabilityQuery.getSharability(FileUtil.toFile(file)) == SharabilityQuery.SHARABLE) {
                                allFiles.add(file);
                            }
                        }
                    } catch (DataObjectNotFoundException e) {
                        allFiles.add(f);
                    }
                }
                problem = chainProblems(handler.createProblem(session, allFiles), problem);
            }
        }
        
        return problem;
    }
    
    private ReadOnlyFilesHandler getROHandler() {
        Lookup.Result result = Lookup.getDefault().lookup(new Lookup.Template(ReadOnlyFilesHandler.class));
        List handlers = (List) result.allInstances();
        if (handlers.size() == 0) {
            return null;
        }
        if (handlers.size() > 1) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "Multiple instances of ReadOnlyFilesHandler found in Lookup; only using first one: " + handlers); //NOI18N
        }
        return (ReadOnlyFilesHandler) handlers.get(0);
    }
    
    private Problem pluginsCheckParams(Problem problem) {
        try {
            progressListener.start();
            Iterator pIt=getPlugins().iterator();

            while(pIt.hasNext()) {
                if (cancel)
                    return null;

                RefactoringPlugin plugin=(RefactoringPlugin)pIt.next();

                try {
                    problem=chainProblems(plugin.checkParameters(),problem);
                } catch (Throwable t) {
                    problem =createProblemAndLog(problem, t, plugin.getClass());
                }
                if (problem!=null && problem.isFatal())
                    return problem;
            }
            return problem;
        } finally {
            progressListener.stop();
        }
    }
    
    private Problem pluginsFastCheckParams(Problem problem) {
        try {
            progressListener.start();
            Iterator pIt=getPlugins().iterator();

            while(pIt.hasNext()) {
               if (cancel)
                    return null;

                RefactoringPlugin plugin=(RefactoringPlugin)pIt.next();

                try {
                    problem=chainProblems(plugin.fastCheckParameters(),problem);
                } catch (Throwable t) {
                    problem =createProblemAndLog(problem, t, plugin.getClass());
                }
                if (problem!=null && problem.isFatal())
                    return problem;
            }
            return problem;
        } finally {
            progressListener.stop();
        }
    }
    
    static Problem chainProblems(Problem p,Problem p1) {
        Problem problem;
        
        if (p==null) return p1;
        if (p1==null) return p;
        problem=p;
        while(problem.getNext()!=null) {
            problem=problem.getNext();
        }
        problem.setNext(p1);
        return p;
    }
    
    
    private class ProgressL implements ProgressListener {

        private float progressStep;
        private float current;
        private boolean startCalledByPlugin = false;
        public void start(ProgressEvent event) {
            progressStep = (float) PLUGIN_STEPS / event.getCount();
            startCalledByPlugin |= true;
            
            if (pluginsWithProgress.indexOf(event.getSource()) == 0) {
                //first plugin
                //let's start
                current = 0;
                if (event.getCount()==-1) {
                    fireProgressListenerStart(event.getOperationType(), -1);
                    progressStep = -1;
                } else {
                    fireProgressListenerStart(event.getOperationType(), PLUGIN_STEPS*pluginsWithProgress.size());
                }
            } else {
                current = pluginsWithProgress.indexOf(event.getSource())*PLUGIN_STEPS;
                fireProgressListenerStep((int) current);
            }
        }
        
        public void step(ProgressEvent event) {
            if (progressStep < 0) {
                int size = event.getCount();
                if (size > 0) {
                    progressStep = (float) PLUGIN_STEPS / size;
                    fireProgressListenerStep(PLUGIN_STEPS*pluginsWithProgress.size());
                } else {
                    fireProgressListenerStep();
                }
            } else {
                current = current + progressStep;
                fireProgressListenerStep((int) current) ;
            }
        }
        
        public void stop(ProgressEvent event) {
            // do not rely on plugins; see stop()
        }
        
        void start() {
            startCalledByPlugin = false;
        }
        
        void stop() {
            if (startCalledByPlugin) {
                fireProgressListenerStop();
            }
        }
        
        /** Notifies all registered listeners about the event.
         *
         * @param type Type of operation that is starting.
         * @param count Number of steps the operation consists of.
         *
         */
        private void fireProgressListenerStart(int type, int count) {
            if (progressSupport != null)
                progressSupport.fireProgressListenerStart(this, type, count);
        }
        
        /** Notifies all registered listeners about the event.
         */
        private void fireProgressListenerStep() {
            if (progressSupport != null)
                progressSupport.fireProgressListenerStep(this);
        }
        
        /**
         * Notifies all registered listeners about the event.
         * @param count
         */
        private void fireProgressListenerStep(int count) {
            if (progressSupport != null)
                progressSupport.fireProgressListenerStep(this, count);
        }
        
        /** Notifies all registered listeners about the event.
         */
        private void fireProgressListenerStop() {
            if (progressSupport != null)
                progressSupport.fireProgressListenerStop(this);
        }
    }
}
