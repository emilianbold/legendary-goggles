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

import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.netbeans.libs.git.GitBranch;
import org.netbeans.libs.git.GitException;
import org.netbeans.libs.git.jgit.GitClassFactory;
import org.netbeans.libs.git.jgit.Utils;
import org.netbeans.libs.git.progress.ProgressMonitor;

/**
 *
 * @author ondra
 */
public class CreateBranchCommand extends GitCommand {
    private final String revision;
    private final String branchName;
    private GitBranch branch;

    public CreateBranchCommand (Repository repository, GitClassFactory gitFactory, String branchName, String revision, ProgressMonitor monitor) {
        super(repository, gitFactory, monitor);
        this.branchName = branchName;
        this.revision = revision;
    }

    @Override
    protected void run () throws GitException {
        Repository repository = getRepository();
        org.eclipse.jgit.api.CreateBranchCommand cmd = new Git(repository).branchCreate();
        cmd.setName(branchName);
        Ref ref;
        if (revision.startsWith(Constants.R_HEADS) || revision.startsWith(Constants.R_REMOTES)) {
            cmd.setUpstreamMode(SetupUpstreamMode.TRACK);
        } else {
            cmd.setStartPoint(Utils.findCommit(repository, revision));
        }
        cmd.setStartPoint(revision);
        try {
            ref = cmd.call();
        } catch (GitAPIException ex) {
            throw new GitException(ex);
        }
        branch = getBranch(false, ref);
    }

    @Override
    protected String getCommandDescription () {
        return new StringBuilder("git branch --track ").append(branchName).append(' ').append(revision).toString(); //NOI18N
    }
    
    public GitBranch getBranch () {
        return branch;
    }
    
    private GitBranch getBranch (boolean isRemote, Ref ref) {
        String refName = ref.getLeaf().getName();
        String name = refName.substring(refName.indexOf('/', 5) + 1);
        return getClassFactory().createBranch(name, isRemote, false, ref.getLeaf().getObjectId());
    }
}
