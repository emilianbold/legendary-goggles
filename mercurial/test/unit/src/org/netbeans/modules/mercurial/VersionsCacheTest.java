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

package org.netbeans.modules.mercurial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.netbeans.modules.versioning.historystore.Storage;
import org.netbeans.modules.versioning.historystore.StorageManager;
import org.netbeans.modules.mercurial.ui.log.HgLogMessage;
import org.netbeans.modules.mercurial.ui.log.HgLogMessage.HgRevision;
import org.netbeans.modules.mercurial.util.HgCommand;
import org.netbeans.modules.mercurial.util.HgUtils;
import org.netbeans.modules.versioning.util.Utils;

/**
 *
 * @author ondra
 */
public class VersionsCacheTest extends AbstractHgTest {

    private File workdir;

    public VersionsCacheTest (String arg0) {
        super(arg0);
    }
    
    @Override
    protected void setUp() throws Exception {
        System.setProperty("netbeans.user", getWorkDir().getParentFile().getAbsolutePath());
        super.setUp();
        // create
        workdir = getWorkTreeDir();
        Mercurial.STATUS_LOG.setLevel(Level.FINE);
    }
    
    public void testCache () throws Exception {
        File folder = new File(workdir, "folder");
        folder.mkdirs();
        File file = new File(folder, "file");
        List<File> revisions = prepareVersions(file);
        testContents(revisions, file, false);
    }
    
    public void testCacheAfterRollback () throws Exception {
        File folder = new File(workdir, "folder");
        folder.mkdirs();
        File file = new File(folder, "file");
        List<File> revisions = prepareVersions(file);
        testContents(revisions, file, false);
        HgCommand.doRollback(workdir, NULL_LOGGER);
        revisions.remove(0);
        File newRevision = new File(new File(getDataDir(), "versionscache"), "rollback");
        revisions.add(0, newRevision);
        Utils.copyStreamsCloseAll(new FileOutputStream(file), new FileInputStream(newRevision));
        commit(new File[]{file});
        testContents(revisions, file, true);
    }

    private List<File> prepareVersions (File file) throws Exception {
        List<File> revisionList = new LinkedList<File>();
        File dataDir = new File(getDataDir(), "versionscache");
        File[] revisions = dataDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("rev");
            }
        });
        for (File rev : revisions) {
            if (rev.isFile()) {
                revisionList.add(0, rev);
                Utils.copyStreamsCloseAll(new FileOutputStream(file), new FileInputStream(rev));
                commit(new File[] {file});
            }
        }
        return revisionList;
    }

    private void testContents (List<File> revisions, File file, boolean cacheFilled) throws Exception {
        HgLogMessage tip = HgCommand.doTip(workdir, NULL_LOGGER);
        long lastRev = tip.getRevisionAsLong();
        VersionsCache cache = VersionsCache.getInstance();
        Storage storage = StorageManager.getInstance().getStorage(workdir.getAbsolutePath());
        for (File golden : revisions) {
            File content;
            HgRevision hgRev = HgCommand.getLogMessages(workdir, Collections.singleton(file), String.valueOf(lastRev), String.valueOf(lastRev), false, false, 1, Collections.<String>emptyList(), NULL_LOGGER, true)[0].getHgRevision();
            if (!cacheFilled) {
                content = storage.getContent(HgUtils.getRelativePath(file), file.getName(), hgRev.getChangesetId());
                assertEquals(0, content.length());
            }
            content = cache.getFileRevision(file, hgRev);
            assertFile(content, golden, null);
            content = storage.getContent(HgUtils.getRelativePath(file), file.getName(), hgRev.getChangesetId());
            assertFile(content, golden, null);
            --lastRev;
        }
    }
}
