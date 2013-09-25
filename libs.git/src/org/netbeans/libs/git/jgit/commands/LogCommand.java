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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.libs.git.jgit.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.FollowFilter;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevFlag;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.AndRevFilter;
import org.eclipse.jgit.revwalk.filter.CommitTimeRevFilter;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.netbeans.libs.git.GitBranch;
import org.netbeans.libs.git.GitException;
import org.netbeans.libs.git.GitObjectType;
import org.netbeans.libs.git.GitRevisionInfo;
import org.netbeans.libs.git.GitUser;
import org.netbeans.libs.git.SearchCriteria;
import org.netbeans.libs.git.jgit.DelegatingGitProgressMonitor;
import org.netbeans.libs.git.jgit.GitClassFactory;
import org.netbeans.libs.git.jgit.Utils;
import org.netbeans.libs.git.progress.ProgressMonitor;
import org.netbeans.libs.git.progress.RevisionInfoListener;

/**
 *
 * @author ondra
 */
public class LogCommand extends GitCommand {
    private final ProgressMonitor monitor;
    private final RevisionInfoListener listener;
    private final List<GitRevisionInfo> revisions;
    private final String revision;
    private final SearchCriteria criteria;
    private final boolean fetchBranchInfo;
    private static final Logger LOG = Logger.getLogger(LogCommand.class.getName());

    public LogCommand (Repository repository, GitClassFactory gitFactory, SearchCriteria criteria,
            boolean fetchBranchInfo, ProgressMonitor monitor, RevisionInfoListener listener) {
        super(repository, gitFactory, monitor);
        this.monitor = monitor;
        this.listener = listener;
        this.criteria = criteria;
        this.fetchBranchInfo = fetchBranchInfo;
        this.revision = null;
        this.revisions = new LinkedList<>();
    }
    
    public LogCommand (Repository repository, GitClassFactory gitFactory, String revision, ProgressMonitor monitor, RevisionInfoListener listener) {
        super(repository, gitFactory, monitor);
        this.monitor = monitor;
        this.listener = listener;
        this.criteria = null;
        this.fetchBranchInfo = false;
        this.revision = revision;
        this.revisions = new LinkedList<>();
    }

    @Override
    protected void run () throws GitException {
        Repository repository = getRepository();
        if (revision != null) {
            RevCommit commit = Utils.findCommit(repository, revision);
            addRevision(getClassFactory().createRevisionInfo(commit, repository));
        } else {
            RevWalk walk = new RevWalk(repository);
            RevWalk fullWalk = new RevWalk(repository);
            Map<String, GitBranch> allBranches;
            Map<String, RevCommit> branchCommits;
            List<RevFlag> branchFlags;
            if (fetchBranchInfo) {
                allBranches = Utils.getAllBranches(repository, getClassFactory(), new DelegatingGitProgressMonitor(monitor));
                branchFlags = new ArrayList<>(allBranches.size());
                branchCommits = new LinkedHashMap<>(allBranches.size());
                for (Map.Entry<String, GitBranch> e : allBranches.entrySet()) {
                    if (e.getKey() != GitBranch.NO_BRANCH) {
                        RevFlag flag = walk.newFlag(e.getKey());
                        branchFlags.add(flag);
                        try {
                            RevCommit branchHeadCommit = walk.parseCommit(repository.resolve(e.getValue().getId()));
                            branchHeadCommit.add(flag);
                            branchHeadCommit.carry(flag);
                            walk.markStart(branchHeadCommit);
                        } catch (IOException ex) {
                            LOG.log(Level.INFO, null, ex);
                        }
                    }
                }
                walk.carry(branchFlags);
            } else {
                allBranches = Collections.<String, GitBranch>emptyMap();
                branchCommits = Collections.<String, RevCommit>emptyMap();
                branchFlags = Collections.<RevFlag>emptyList();
            }
            try {
                RevFlag interestingFlag = walk.newFlag("RESULT_FLAG"); //NOI18N
                walk.carry(interestingFlag);
                String revisionFrom = criteria.getRevisionFrom();
                String revisionTo = criteria.getRevisionTo();
                if (revisionTo != null && revisionFrom != null) {
                    for (RevCommit uninteresting : Utils.findCommit(repository, revisionFrom).getParents()) {
                        walk.markUninteresting(walk.parseCommit(uninteresting));
                    }
                    walk.markStart(markStartCommit(walk.lookupCommit(Utils.findCommit(repository, revisionTo)), interestingFlag));
                } else if (revisionTo != null) {
                    walk.markStart(markStartCommit(walk.lookupCommit(Utils.findCommit(repository, revisionTo)), interestingFlag));
                } else if (revisionFrom != null) {
                    for (RevCommit uninteresting : Utils.findCommit(repository, revisionFrom).getParents()) {
                        walk.markUninteresting(walk.parseCommit(uninteresting));
                    }
                    walk.markStart(markStartCommit(walk.lookupCommit(Utils.findCommit(repository, Constants.HEAD)), interestingFlag));
                } else {
                    ListBranchCommand branchCommand = new ListBranchCommand(repository, getClassFactory(), false, new DelegatingGitProgressMonitor(monitor));
                    branchCommand.execute();
                    if (monitor.isCanceled()) {
                        return;
                    }
                    for (Map.Entry<String, GitBranch> e : branchCommand.getBranches().entrySet()) {
                        walk.markStart(markStartCommit(walk.lookupCommit(Utils.findCommit(repository, e.getValue().getId())), interestingFlag));
                    }
                }
                applyCriteria(walk, criteria, interestingFlag);
                walk.sort(RevSort.TOPO);
                walk.sort(RevSort.COMMIT_TIME_DESC, true);
                int remaining = criteria.getLimit();
                for (Iterator<RevCommit> it = walk.iterator(); it.hasNext() && !monitor.isCanceled() && remaining != 0;) {
                    RevCommit commit = it.next();
                    if (!applyCriteriaAfter(criteria, commit)) {
                        continue;
                    }
                    addRevision(getClassFactory().createRevisionInfo(fullWalk.parseCommit(commit),
                            getAffectedBranches(commit, allBranches, branchFlags), repository));
                    --remaining;
                }
            } catch (MissingObjectException ex) {
                throw new GitException.MissingObjectException(ex.getObjectId().toString(), GitObjectType.COMMIT);
            } catch (IOException ex) {
                throw new GitException(ex);
            } finally {
                walk.release();
                fullWalk.release();
            }
        } 
    }

    @Override
    protected String getCommandDescription () {
        StringBuilder sb = new StringBuilder("git log --name-status "); //NOI18N
        if (criteria != null && criteria.isFollow() && criteria.getFiles() != null && criteria.getFiles().length == 1) {
            sb.append("--follow "); //NOI18N
        }
        if (revision != null) {
            sb.append("--no-walk ").append(revision);
        } else if (criteria.getRevisionTo() != null && criteria.getRevisionFrom() != null) {
            sb.append(criteria.getRevisionFrom()).append("..").append(criteria.getRevisionTo()); //NOI18N
        } else if (criteria.getRevisionTo() != null) {
            sb.append(criteria.getRevisionTo());
        } else if (criteria.getRevisionFrom() != null) {
            sb.append(criteria.getRevisionFrom()).append(".."); //NOI18N
        }
        return sb.toString();
    }

    public GitRevisionInfo[] getRevisions () {
        return revisions.toArray(new GitRevisionInfo[revisions.size()]);
    }

    private void addRevision (GitRevisionInfo info) {
        revisions.add(info);
        listener.notifyRevisionInfo(info);
    }

    private void applyCriteria (RevWalk walk, SearchCriteria criteria, final RevFlag partOfResultFlag) {
        File[] files = criteria.getFiles();
        if (files.length > 0) {
            Collection<PathFilter> pathFilters = Utils.getPathFilters(getRepository().getWorkTree(), files);
            if (!pathFilters.isEmpty()) {
                if (criteria.isFollow() && pathFilters.size() == 1) {
                    walk.setTreeFilter(FollowFilter.create(pathFilters.iterator().next().getPath()));
                } else {
                    walk.setTreeFilter(AndTreeFilter.create(TreeFilter.ANY_DIFF, PathFilterGroup.create(pathFilters)));
                }
            }
        }
        RevFilter filter;
        if (criteria.isIncludeMerges()) {
            filter = RevFilter.ALL;
        } else {
            filter = RevFilter.NO_MERGES;
        }
        filter = AndRevFilter.create(filter, new RevFilter() {

            @Override
            public boolean include (RevWalk walker, RevCommit cmit) {
                return cmit.has(partOfResultFlag);
            }

            @Override
            public RevFilter clone () {
                return this;
            }

            @Override
            public boolean requiresCommitBody () {
                return false;
            }            
            
        });
        
//        String username = criteria.getUsername();
//        if (username != null && !(username = username.trim()).isEmpty()) {
//            filter = AndRevFilter.create(filter, OrRevFilter.create(CommitterRevFilter.create(username), AuthorRevFilter.create(username)));
//        }
//        String message = criteria.getMessage();
//        if (message != null && !(message = message.trim()).isEmpty()) {
//            filter = AndRevFilter.create(filter, MessageRevFilter.create(message));
//        }
        Date from  = criteria.getFrom();
        Date to  = criteria.getTo();
        if (from != null && to != null) {
            filter = AndRevFilter.create(filter, CommitTimeRevFilter.between(from, to));
        } else if (from != null) {
            filter = AndRevFilter.create(filter, CommitTimeRevFilter.after(from));
        } else if (to != null) {
            filter = AndRevFilter.create(filter, CommitTimeRevFilter.before(to));
        }
        walk.setRevFilter(filter);
    }

    private boolean applyCriteriaAfter (SearchCriteria criteria, RevCommit commit) {
        boolean apply = true;
        String username = criteria.getUsername();
        if (username != null && !(username = username.trim()).isEmpty()) {
            apply = applyTo(commit.getAuthorIdent(), username) || applyTo(commit.getCommitterIdent(), username);
        }
        String message = criteria.getMessage();
        if (message != null && !(message = message.trim()).isEmpty()) {
            apply &= commit.getFullMessage().contains(message);
        }
        return apply;
    }

    private boolean applyTo (PersonIdent ident, String pattern) {
        return ident != null && new GitUser(ident.getName(), ident.getEmailAddress()).toString().contains(pattern);
    }

    private RevCommit markStartCommit (RevCommit commit, RevFlag interestingFlag) {
        commit.add(interestingFlag);
        return commit;
    }

    private Map<String, GitBranch> getAffectedBranches (RevCommit commit, Map<String, GitBranch> allBranches,
            List<RevFlag> branchFlags) {
        Map<String, GitBranch> affected = new LinkedHashMap<>(allBranches.size());
        for (RevFlag flag : branchFlags) {
            GitBranch b;
            if (commit.has(flag) && (b = allBranches.get(flag.toString())) != null) {
                affected.put(b.getName(), b);
            }
        }
        return affected;
    }
}
