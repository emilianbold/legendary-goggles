/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.subversion.client;

import java.text.DateFormat;
import java.util.Date;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.subversion.Diagnostics;
import org.netbeans.modules.subversion.OutputLogger;
import org.netbeans.modules.subversion.Subversion;
import org.openide.util.Cancellable;
import org.openide.util.RequestProcessor;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 *
 * @author Tomas Stupka
 */
public abstract class SvnProgressSupport implements Runnable, Cancellable {

    private Cancellable delegate; 
    private boolean canceled;
    private Thread interruptibleThread;
    
    private ProgressHandle progressHandle = null;    
    private String displayName = ""; // NOI18N
    private String originalDisplayName = ""; // NOI18N
    private OutputLogger logger;
    private SVNUrl repositoryRoot;

    public RequestProcessor.Task start(RequestProcessor rp, SVNUrl repositoryRoot, String displayName) {
        setDisplayName(displayName);
        this.repositoryRoot = repositoryRoot;
        return rp.post(this);        
    }

    public void setRepositoryRoot(SVNUrl repositoryRoot) {
        this.repositoryRoot = repositoryRoot;
        logger = null;
    }

    public void run() {        
        startProgress();
        performIntern();
    }

    protected void performIntern() {
        try {
            interruptibleThread = Thread.currentThread();
            Diagnostics.println("Start - " + displayName); // NOI18N
            perform();
            Diagnostics.println("End - " + displayName); // NOI18N
        } finally {            
            finnishProgress();
            getLogger().closeLog();
        }
    }

    protected abstract void perform();

    public synchronized boolean isCanceled() {
        return canceled;
    }

    public synchronized boolean cancel() {
        if (canceled) {
            return false;
        }        
        if(delegate != null) {
            delegate.cancel();
        }
        if (interruptibleThread != null) {
            interruptibleThread.interrupt();  
        }
        canceled = true;
        return true;
    }

    void setCancellableDelegate(Cancellable cancellable) {
        this.delegate = cancellable;
    }

    public void setDisplayName(String displayName) {
        if(originalDisplayName.equals("")) { // NOI18N
            originalDisplayName = displayName;
        }
        this.displayName = displayName;
        if(progressHandle!=null) {
            progressHandle.progress(displayName);
        }
    }

    protected String getDisplayName() {
        return displayName;
    }

    protected ProgressHandle getProgressHandle() {
        if(progressHandle==null) {
            progressHandle = ProgressHandleFactory.createHandle(displayName, this);
        }
        return progressHandle;
    }

    protected void startProgress() {
        getProgressHandle().start();
        getLogger().logCommandLine("==[IDE]== " + DateFormat.getDateTimeInstance().format(new Date()) + " " + originalDisplayName); // NOI18N
    }

    protected void finnishProgress() {
        getProgressHandle().finish();
        if (isCanceled() == false) {
            getLogger().logCommandLine("==[IDE]== " + DateFormat.getDateTimeInstance().format(new Date()) + " " + originalDisplayName + " " + org.openide.util.NbBundle.getMessage(SvnProgressSupport.class, "MSG_Progress_Finished")); // NOI18N
        } else {
            getLogger().logCommandLine("==[IDE]== " + DateFormat.getDateTimeInstance().format(new Date()) + " " +originalDisplayName + " " + org.openide.util.NbBundle.getMessage(SvnProgressSupport.class, "MSG_Progress_Canceled")); // NOI18N
        }
    }
    
    protected OutputLogger getLogger() {
        if (logger == null) {
            logger = Subversion.getInstance().getLogger(repositoryRoot);
        }
        return logger;
    }
}
