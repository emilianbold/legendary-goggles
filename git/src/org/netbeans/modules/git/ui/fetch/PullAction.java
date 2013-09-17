/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.modules.git.ui.fetch;

import java.awt.EventQueue;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import org.netbeans.libs.git.GitBranch;
import org.netbeans.libs.git.GitClient.RebaseOperationType;
import org.netbeans.modules.git.client.GitClient;
import org.netbeans.libs.git.GitException;
import org.netbeans.libs.git.GitMergeResult;
import org.netbeans.libs.git.GitRebaseResult;
import org.netbeans.libs.git.GitRemoteConfig;
import org.netbeans.libs.git.GitRevisionInfo;
import org.netbeans.libs.git.GitTransportUpdate;
import org.netbeans.modules.git.Git;
import org.netbeans.modules.git.client.GitClientExceptionHandler;
import org.netbeans.modules.git.client.GitProgressSupport;
import org.netbeans.modules.git.ui.actions.GitAction;
import org.netbeans.modules.git.ui.actions.SingleRepositoryAction;
import org.netbeans.modules.git.ui.merge.MergeRevisionAction;
import org.netbeans.modules.git.ui.rebase.RebaseAction;
import org.netbeans.modules.git.ui.repository.RepositoryInfo;
import org.netbeans.modules.git.utils.GitUtils;
import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.modules.versioning.util.Utils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor.Task;

/**
 *
 * @author ondra
 */
@ActionID(id = "org.netbeans.modules.git.ui.fetch.PullAction", category = "Git")
@ActionRegistration(displayName = "#LBL_PullAction_Name")
@NbBundle.Messages({"#PullAction", "LBL_PullAction_Name=P&ull..."})
public class PullAction extends SingleRepositoryAction {
    
    private static final String ICON_RESOURCE = "org/netbeans/modules/git/resources/icons/pull-setting.png"; //NOI18N
    
    public PullAction () {
        super(ICON_RESOURCE);
    }

    @Override
    protected String iconResource () {
        return ICON_RESOURCE;
    }
    
    private static final Logger LOG = Logger.getLogger(PullAction.class.getName());

    @Override
    protected void performAction (File repository, File[] roots, VCSContext context) {
        pull(repository);
    }
    
    private void pull (final File repository) {
        RepositoryInfo info = RepositoryInfo.getInstance(repository);
        try {
            info.refreshRemotes();
        } catch (GitException ex) {
            GitClientExceptionHandler.notifyException(ex, true);
        }
        final Map<String, GitRemoteConfig> remotes = info.getRemotes();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run () {
                PullWizard wiz = new PullWizard(repository, remotes);
                if (wiz.show()) {
                    Utils.logVCSExternalRepository("GIT", wiz.getFetchUri()); //NOI18N
                    pull(repository, wiz.getFetchUri(), wiz.getFetchRefSpecs(), wiz.getBranchToMerge(), wiz.getRemoteToPersist());
                }
            }
        });
    }
    
    @NbBundle.Messages({
        "# {0} - repository name", "LBL_PullAction.progressName=Pulling - {0}",
        "MSG_PullAction.fetching=Fetching remote changes",
        "MSG_PullAction.merging=Merging remote changes",
        "MSG_PullAction.rebasing=Rebasing onto fetched head"
    })
    public Task pull (File repository, final String target, final List<String> fetchRefSpecs, final String branchToMerge, final String remoteNameToUpdate) {
        GitProgressSupport supp = new GitProgressSupportImpl(fetchRefSpecs, branchToMerge, target, remoteNameToUpdate);
        return supp.start(Git.getInstance().getRequestProcessor(repository), repository, Bundle.LBL_PullAction_progressName(repository.getName()));
    }

    private class GitProgressSupportImpl extends GitProgressSupport {

        private final List<String> fetchRefSpecs;
        private final String branchToMerge;
        private final String target;
        private final String remoteNameToUpdate;

        public GitProgressSupportImpl (List<String> fetchRefSpecs, String branchToMerge, String target, String remoteNameToUpdate) {
            this.fetchRefSpecs = fetchRefSpecs;
            this.branchToMerge = branchToMerge;
            this.target = target;
            this.remoteNameToUpdate = remoteNameToUpdate;
        }

        @Override
        @NbBundle.Messages({
            "# {0} - branch name", "MSG_PullAction.branchDeleted=Branch {0} deleted."
        })
        protected void perform () {
            final File repository = getRepositoryRoot();
            LOG.log(Level.FINE, "Pulling {0}/{1} from {2}", new Object[] { fetchRefSpecs, branchToMerge, target }); //NOI18N
            try {
                final GitClient client = getClient();
                final Set<String> toDelete = new HashSet<String>();
                for(ListIterator<String> it = fetchRefSpecs.listIterator(); it.hasNext(); ) {
                    String refSpec = it.next();
                    if (refSpec.startsWith(GitUtils.REF_SPEC_DEL_PREFIX)) {
                        // branches are deleted separately
                        it.remove();
                        toDelete.add(refSpec.substring(GitUtils.REF_SPEC_DEL_PREFIX.length()));
                    }
                }
                if (remoteNameToUpdate != null) {
                    GitRemoteConfig config = client.getRemote(remoteNameToUpdate, getProgressMonitor());
                    if (isCanceled()) {
                        return;
                    }
                    config = GitUtils.prepareConfig(config, remoteNameToUpdate, target, fetchRefSpecs);
                    client.setRemote(config, getProgressMonitor());
                    if (isCanceled()) {
                        return;
                    }
                }
                GitUtils.runWithoutIndexing(new Callable<Void>() {
                    @Override
                    public Void call () throws Exception {
                        for (String branch : toDelete) {
                            client.deleteBranch(branch, true, getProgressMonitor());
                            getLogger().outputLine(Bundle.MSG_PullAction_branchDeleted(branch));
                        }
                        setProgress(Bundle.MSG_PullAction_fetching());
                        Map<String, GitTransportUpdate> fetchResult = client.fetch(target, fetchRefSpecs, getProgressMonitor());
                        FetchUtils.log(repository, fetchResult, getLogger());
                        if (isCanceled() || branchToMerge == null) {
                            return null;
                        }
                        Callable<Void> nextAction = getNextAction();
                        if (nextAction != null) {
                            nextAction.call();
                        }
                        return null;
                    }
                }, repository);
            } catch (GitException ex) {
                GitClientExceptionHandler.notifyException(ex, true);
            } finally {
                setDisplayName(NbBundle.getMessage(GitAction.class, "LBL_Progress.RefreshingStatuses")); //NOI18N
                Git.getInstance().getFileStatusCache().refreshAllRoots(Collections.<File, Collection<File>>singletonMap(repository, Git.getInstance().getSeenRoots(repository)));
                GitUtils.headChanged(repository);
            }
        }
        
        private Callable<Void> getNextAction () {
            Callable<Void> nextAction = null;
            try {
                GitClient client = getClient();
                String currentHeadId = null;
                String branchId = null;
                Map<String, GitBranch> branches = client.getBranches(true, GitUtils.NULL_PROGRESS_MONITOR);
                for (Map.Entry<String, GitBranch> e : branches.entrySet()) {
                    if (e.getValue().isActive()) {
                        currentHeadId = e.getValue().getId();
                    }
                    if (e.getKey().equals(branchToMerge)) {
                        branchId = e.getValue().getId();
                    }
                }
                if (branchId == null || currentHeadId == null) {
                    nextAction = new Merge(); // just for sure
                } else if (!branchId.equals(currentHeadId)) {
                    GitRevisionInfo info = client.getCommonAncestor(new String[] { currentHeadId, branchId }, GitUtils.NULL_PROGRESS_MONITOR);
                    if (info == null || !(info.getRevision().equals(branchId) || info.getRevision().equals(currentHeadId))) {
                        // ask
                        return askForNextAction();
                    } else if (info.getRevision().equals(currentHeadId)) {
                        // FF merge
                        nextAction = new Merge();
                    }                    
                }
            } catch (GitException ex) {
                LOG.log(Level.INFO, null, ex);
            }
            return nextAction;
        }

        @NbBundle.Messages({
            "# {0} - branch to merge",
            "MSG_PullAction_mergeNeeded_text=A merge commit is needed to synchronize current branch with {0}.\n\n"
                + "Do you want to Merge the current branch with {0} or Rebase it onto {0}?",
            "LBL_PullAction_mergeNeeded_title=Merge Commit Needed",
            "CTL_PullAction_mergeButton_text=&Merge",
            "CTL_PullAction_mergeButton_TTtext=Merge the two created heads",
            "CTL_PullAction_rebaseButton_text=&Rebase",
            "CTL_PullAction_rebaseButton_TTtext=Rebase current branch on top of the fetched branch"
        })
        private Callable<Void> askForNextAction () {
            JButton btnMerge = new JButton();
            Mnemonics.setLocalizedText(btnMerge, Bundle.CTL_PullAction_mergeButton_text());
            btnMerge.setToolTipText(Bundle.CTL_PullAction_mergeButton_TTtext());
            JButton btnRebase = new JButton();
            Mnemonics.setLocalizedText(btnRebase, Bundle.CTL_PullAction_rebaseButton_text());
            btnRebase.setToolTipText(Bundle.CTL_PullAction_rebaseButton_TTtext());
            Object value = DialogDisplayer.getDefault().notify(new NotifyDescriptor(
                    Bundle.MSG_PullAction_mergeNeeded_text(branchToMerge),
                    Bundle.LBL_PullAction_mergeNeeded_title(),
                    NotifyDescriptor.DEFAULT_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE,
                    new Object[] { btnMerge, btnRebase, NotifyDescriptor.CANCEL_OPTION },
                    btnMerge));
            if (value == btnMerge) {
                return new Merge();
            } else if (value == btnRebase) {
                return new Rebase();
            }
            return null;
        }
        
        private class Merge implements Callable<Void> {

            @Override
            public Void call () throws GitException {
                boolean cont;
                GitClient client = getClient();
                File repository = getRepositoryRoot();
                setProgress(Bundle.MSG_PullAction_merging());
                do {
                    MergeRevisionAction.MergeResultProcessor mrp = new MergeRevisionAction.MergeResultProcessor(client, repository, branchToMerge, getLogger(), getProgressMonitor());
                    cont = false;
                    try {
                        GitMergeResult result = client.merge(branchToMerge, getProgressMonitor());
                        mrp.processResult(result);
                    } catch (GitException.CheckoutConflictException ex) {
                        if (LOG.isLoggable(Level.FINE)) {
                            LOG.log(Level.FINE, "Local modifications in WT during merge: {0} - {1}", new Object[] { repository, Arrays.asList(ex.getConflicts()) }); //NOI18N
                        }
                        cont = mrp.resolveLocalChanges(ex.getConflicts());
                    }
                } while (cont && !isCanceled());
                return null;
            }

        }

        private class Rebase implements Callable<Void> {

            @Override
            public Void call () throws GitException  {
                setProgress(Bundle.MSG_PullAction_rebasing());
                RebaseOperationType op = RebaseOperationType.BEGIN;
                GitClient client = getClient();
                File repository = getRepositoryRoot();
                String origHead = client.log(GitUtils.HEAD, getProgressMonitor()).getRevision();
                RebaseAction.RebaseResultProcessor rrp = new RebaseAction.RebaseResultProcessor(client, repository,
                        branchToMerge, branchToMerge, origHead, getProgressSupport());
                while (op != null && !isCanceled()) {
                    GitRebaseResult result = client.rebase(op, branchToMerge, getProgressMonitor());
                    rrp.processResult(result);
                    op = rrp.getNextAction();
                }
                return null;
            }
        }

        private GitProgressSupport getProgressSupport () {
            return this;
        }
    }
    
}
