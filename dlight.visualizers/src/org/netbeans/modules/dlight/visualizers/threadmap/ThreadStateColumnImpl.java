/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.dlight.visualizers.threadmap;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import org.netbeans.modules.dlight.api.storage.threadmap.ThreadStateColumn;
import org.netbeans.modules.dlight.api.storage.threadmap.ThreadState;
import org.netbeans.modules.dlight.visualizers.threadmap.ThreadsDataManager.MergedThreadInfo;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexander Simon (adapted for CND)
 */
public class ThreadStateColumnImpl implements ThreadStateColumn {
    /** Thread status is unknown. */
    public static final Color THREAD_STATUS_UNKNOWN_COLOR = Color.LIGHT_GRAY;
    /** Thread is waiting to die. Also used for "doesn't exist yet" and "dead" */
    public static final Color THREAD_STATUS_ZOMBIE_COLOR = Color.BLACK;
    public static final Color THREAD_STATUS_RUNNING_COLOR = new Color(84, 185, 72);//new Color(58, 228, 103);
    public static final Color THREAD_STATUS_SLEEPING_COLOR = new Color(255, 199, 38);//new Color(155, 134, 221);
    public static final Color THREAD_STATUS_BLOCKED_COLOR = new Color(238, 29, 37);//new Color(255, 114, 102);
    public static final Color THREAD_STATUS_WAITING_COLOR = new Color(83, 130, 161);//new Color(255, 228, 90);

    // I18N String constants
    static final ResourceBundle messages = NbBundle.getBundle(ThreadStateColumnImpl.class);
    public static final String THREAD_STATUS_UNKNOWN_STRING = messages.getString("CommonConstants_ThreadStatusUnknownString"); // NOI18N 
    public static final String THREAD_STATUS_ZOMBIE_STRING = messages.getString("CommonConstants_ThreadStatusZombieString"); // NOI18N
    public static final String THREAD_STATUS_RUNNING_STRING = messages.getString("CommonConstants_ThreadStatusRunningString"); // NOI18N
    public static final String THREAD_STATUS_SLEEPING_STRING = messages.getString("CommonConstants_ThreadStatusSleepingString"); // NOI18N;
    public static final String THREAD_STATUS_BLOCKED_STRING = messages.getString("CommonConstants_ThreadStatusBlockedString"); // NOI18N
    public static final String THREAD_STATUS_WAITING_STRING = messages.getString("CommonConstants_ThreadStatusWaitingString"); // NOI18N

    static Color getThreadStateColor(ThreadState.MSAState threadState) {
        switch(threadState) {
            case ThreadFinished: return THREAD_STATUS_ZOMBIE_COLOR;
            case Running: return THREAD_STATUS_RUNNING_COLOR;
            case Blocked: return THREAD_STATUS_BLOCKED_COLOR;
            case Waiting:  return THREAD_STATUS_WAITING_COLOR;
            case Sleeping: return THREAD_STATUS_SLEEPING_COLOR;
            case Stopped: return THREAD_STATUS_SLEEPING_COLOR;
        }
        return THREAD_STATUS_UNKNOWN_COLOR;
    }

    static Color getThreadStateColor(ThreadState threadStateColor, int msa) {
        return getThreadStateColor(threadStateColor.getMSAState(msa, false));
    }

    private final MergedThreadInfo info;
    private final List<ThreadState> list = new ArrayList<ThreadState>();
    private final AtomicInteger comparable = new AtomicInteger();

    ThreadStateColumnImpl(MergedThreadInfo info) {
        this.info = info;
    }

    public void setSummary(int sum) {
        comparable.set(sum);
    }

    public int getSummary() {
        return comparable.get();
    }

    public String getName(){
        return info.getThreadName();
    }

    public int size() {
        return list.size();
    }

    public boolean isAlive(int index) {
        return !list.get(index).getMSAState(0, false).equals(ThreadState.MSAState.ThreadFinished);
    }
    
    public ThreadState getThreadStateAt(int index){
        return list.get(index);
    }

    public boolean isAlive() {
        return !list.get(list.size()-1).getMSAState(0, false).equals(ThreadState.MSAState.ThreadFinished);
    }

    void add(ThreadState state) {
        list.add(state);
    }

    void clearStates() {
        list.clear();
    }

    int getThreadID() {
        return info.getThreadId();
    }
    long getThreadStartTimeStamp() {
        return info.getStartTimeStamp();
    }
}
