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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import org.netbeans.libs.git.GitClient;
import org.netbeans.libs.git.GitException;
import org.netbeans.libs.git.GitStatus;
import org.netbeans.libs.git.jgit.AbstractGitTestCase;
import org.netbeans.libs.git.progress.ProgressMonitor;

/**
 *
 * @author ondra
 */
public class RenameTest extends AbstractGitTestCase {
    private File workDir;

    public RenameTest (String testName) throws IOException {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        workDir = getWorkingDirectory();
    }

    public void testRenameUnderItself () throws Exception {
        File folder = new File(workDir, "folder");
        folder.mkdirs();
        File target = new File(folder, "subFolder");
        target.mkdirs();

        Monitor m = new Monitor();
        GitClient client = getClient(workDir);
        try {
            client.rename(folder, target, false, m);
            fail();
        } catch (GitException ex) {
            assertEquals("folder/subFolder lies under folder", ex.getMessage());
        }
        try {
            client.rename(target, folder, false, m);
            fail();
        } catch (GitException ex) {
            assertEquals("folder/subFolder lies under folder", ex.getMessage());
        }
    }

    public void testRenameFailSourceDoesNotExist () throws Exception {
        File folder = new File(workDir, "folder");
        folder.mkdirs();
        File target = new File(workDir, "target");
        target.mkdirs();

        Monitor m = new Monitor();
        folder.renameTo(target);
        GitClient client = getClient(workDir);
        try {
            client.rename(folder, target, false, m);
            fail();
        } catch (GitException ex) {
            assertEquals("Source does not exist: " + folder.getAbsolutePath(), ex.getMessage());
        }
    }

    public void testRenameFailTargetExists () throws Exception {
        File folder = new File(workDir, "folder");
        folder.mkdirs();
        File target = new File(workDir, "target");
        target.mkdirs();

        Monitor m = new Monitor();
        GitClient client = getClient(workDir);
        try {
            client.rename(folder, target, false, m);
            fail();
        } catch (GitException ex) {
            assertEquals("Target already exists: " + target.getAbsolutePath(), ex.getMessage());
        }
    }

    public void testRenameFailTargetOutsideWorkingTree () throws Exception {
        File folder = new File(workDir, "folder");
        folder.mkdirs();
        File target = new File(workDir.getParentFile(), "target");

        Monitor m = new Monitor();
        GitClient client = getClient(workDir);
        try {
            client.rename(folder, target, true, m);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(target.getAbsolutePath() + " is not under " + workDir.getAbsolutePath(), ex.getMessage());
        }
    }

    public void testRenameFailTargetDoesNotExist () throws Exception {
        File folder = new File(workDir, "folder");
        folder.mkdirs();
        File target = new File(workDir, "target");

        Monitor m = new Monitor();
        GitClient client = getClient(workDir);
        try {
            client.rename(folder, target, true, m);
            fail();
        } catch (GitException ex) {
            assertEquals("Target does not exist: " + target.getAbsolutePath(), ex.getMessage());
        }
    }

    public void testRenameFile () throws Exception {
        File file = new File(workDir, "file");
        write(file, "hello");
        File target = new File(workDir, "fileRenamed");

        add(file);
        commit(file);
        GitClient client = getClient(workDir);
        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.rename(file, target, false, m);
        assertTrue(target.exists());
        assertFalse(file.exists());
        assertEquals("hello", read(target));
        assertEquals(Collections.singleton(target), m.notifiedFiles);
        Map<File, GitStatus> statuses = client.getStatus(new File[] { workDir }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_REMOVED, false);
        assertStatus(statuses, workDir, target, true, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, false);
        assertTrue(statuses.get(target).isRenamed());
    }

    public void testRenameFileAfter () throws Exception {
        File file = new File(workDir, "file");
        write(file, "hello");
        File target = new File(workDir, "fileRenamed");

        add(file);
        commit(file);
        file.renameTo(target);
        GitClient client = getClient(workDir);
        Monitor m = new Monitor();
        assertTrue(target.exists());
        assertFalse(file.exists());
        assertEquals("hello", read(target));
        client.addNotificationListener(m);
        client.rename(file, target, true, m);
        assertEquals(Collections.singleton(target), m.notifiedFiles);
        Map<File, GitStatus> statuses = client.getStatus(new File[] { workDir }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_REMOVED, false);
        assertStatus(statuses, workDir, target, true, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, false);
        assertTrue(statuses.get(target).isRenamed());
    }

    public void testMoveFileToFolder () throws Exception {
        File file = new File(workDir, "file");
        write(file, "aaa");
        File target = new File(new File(workDir, "folder"), "file");

        add(file);
        commit(file);
        GitClient client = getClient(workDir);
        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.rename(file, target, false, m);
        assertTrue(target.exists());
        assertFalse(file.exists());
        assertEquals("aaa", read(target));
        assertEquals(Collections.singleton(target), m.notifiedFiles);
        Map<File, GitStatus> statuses = client.getStatus(new File[] { workDir }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_REMOVED, false);
        assertStatus(statuses, workDir, target, true, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, false);
        assertTrue(statuses.get(target).isRenamed());

        write(file, "aaa");
        add(file);
        statuses = client.getStatus(new File[] { file }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, false);
        File target2 = new File(target.getParentFile(), "moved");
        m = new Monitor();
        client.addNotificationListener(m);
        client.rename(file, target2, false, m);
        assertTrue(target2.exists());
        assertFalse(file.exists());
        assertEquals("aaa", read(target2));
        assertEquals(Collections.singleton(target2), m.notifiedFiles);
        statuses = client.getStatus(new File[] { workDir }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_REMOVED, false);
        assertStatus(statuses, workDir, target, true, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, false);
        assertStatus(statuses, workDir, target2, true, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, false);
        assertTrue(statuses.get(target).isRenamed() && statuses.get(target2).isCopied()
                || statuses.get(target2).isRenamed() && statuses.get(target).isCopied());
    }

    public void testMoveFileToFolderAfter () throws Exception {
        File file = new File(workDir, "file");
        write(file, "aaa");
        File target = new File(new File(workDir, "folder"), "file");
        target.getParentFile().mkdirs();

        add(file);
        commit(file);
        GitClient client = getClient(workDir);
        Monitor m = new Monitor();
        assertTrue(file.renameTo(target));
        assertTrue(target.exists());
        assertFalse(file.exists());
        assertEquals("aaa", read(target));
        client.addNotificationListener(m);
        client.rename(file, target, true, m);
        assertEquals(Collections.singleton(target), m.notifiedFiles);
        Map<File, GitStatus> statuses = client.getStatus(new File[] { workDir }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_REMOVED, false);
        assertStatus(statuses, workDir, target, true, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, false);
        assertTrue(statuses.get(target).isRenamed());

        write(file, "aaa");
        add(file);
        statuses = client.getStatus(new File[] { file }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_NORMAL, false);
        File target2 = new File(target.getParentFile(), "moved");
        m = new Monitor();
        assertTrue(file.renameTo(target2));
        assertTrue(target2.exists());
        assertFalse(file.exists());
        assertEquals("aaa", read(target2));
        client.addNotificationListener(m);
        client.rename(file, target2, true, m);
        assertEquals(Collections.singleton(target2), m.notifiedFiles);
        statuses = client.getStatus(new File[] { workDir }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_REMOVED, false);
        assertStatus(statuses, workDir, target, true, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, false);
        assertStatus(statuses, workDir, target2, true, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, false);
        assertTrue(statuses.get(target).isRenamed() && statuses.get(target2).isCopied()
                || statuses.get(target2).isRenamed() && statuses.get(target).isCopied());
    }

    public void testMoveFileToExisting () throws Exception {
        File file = new File(workDir, "file");
        write(file, "aaa");
        File target = new File(new File(workDir, "folder"), "file");

        add(file);
        commit(file);
        GitClient client = getClient(workDir);
        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.rename(file, target, false, m);
        assertTrue(target.exists());
        assertFalse(file.exists());
        assertEquals("aaa", read(target));
        assertEquals(Collections.singleton(target), m.notifiedFiles);
        Map<File, GitStatus> statuses = client.getStatus(new File[] { workDir }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_REMOVED, false);
        assertStatus(statuses, workDir, target, true, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, false);
        assertTrue(statuses.get(target).isRenamed());

        write(file, "aaa");
        write(target, "bbb");
        add(target, file);
        commit(target);

        File target2 = target;
        assertTrue(target2.exists());
        m = new Monitor();
        file.renameTo(target2);
        client.addNotificationListener(m);
        client.rename(file, target2, true, m);
        assertTrue(m.notifiedWarnings.contains("Index already contains an entry for folder/file"));
        assertEquals(Collections.singleton(target2), m.notifiedFiles);
        statuses = client.getStatus(new File[] { workDir }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertStatus(statuses, workDir, target, true, GitStatus.Status.STATUS_MODIFIED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_MODIFIED, false);
        assertStatus(statuses, workDir, target2, true, GitStatus.Status.STATUS_MODIFIED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_MODIFIED, false);
        assertStatus(statuses, workDir, file, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_REMOVED, false);
        // aaa -> bbb is a 0% match
        assertFalse(statuses.get(target).isRenamed());
        assertFalse(statuses.get(target).isCopied());
    }

    public void testMoveTree () throws Exception {
        File folder = new File(workDir, "folder");
        folder.mkdirs();
        File file1 = new File(folder, "file1");
        write(file1, "file1 content");
        File file2 = new File(folder, "file2");
        write(file2, "file2 content");
        File subFolder1 = new File(folder, "folder1");
        subFolder1.mkdirs();
        File file11 = new File(subFolder1, "file");
        write(file11, "file11 content");
        File subFolder2 = new File(folder, "folder2");
        subFolder2.mkdirs();
        File file21 = new File(subFolder2, "file");
        write(file21, "file21 content");

        File target = new File(workDir, "target");
        File moved1 = new File(target, file1.getName());
        File moved2 = new File(target, file2.getName());
        File moved11 = new File(new File(target, file11.getParentFile().getName()), file11.getName());
        File moved21 = new File(new File(target, file21.getParentFile().getName()), file21.getName());

        add(file1, file11, file21);
        commit(file1, file11);

        GitClient client = getClient(workDir);
        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.rename(folder, target, false, m);
        assertTrue(moved1.exists());
        assertTrue(moved2.exists());
        assertTrue(moved11.exists());
        assertTrue(moved21.exists());
        assertEquals(new HashSet<File>(Arrays.asList(moved1, moved11, moved21)), m.notifiedFiles);
        Map<File, GitStatus> statuses = client.getStatus(new File[] { workDir }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertStatus(statuses, workDir, file1, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_REMOVED, false);
        assertNull(statuses.get(file2));
        assertStatus(statuses, workDir, file11, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_REMOVED, false);
        assertNull(statuses.get(file21));
        assertStatus(statuses, workDir, moved1, true, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, false);
        assertStatus(statuses, workDir, moved2, false, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_ADDED, false);
        assertStatus(statuses, workDir, moved11, true, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, false);
        assertStatus(statuses, workDir, moved21, true, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, false);
        assertTrue(statuses.get(moved1).isRenamed());
        assertTrue(statuses.get(moved11).isRenamed());
        // file21 was not committed
        assertFalse(statuses.get(moved21).isRenamed());
    }

    public void testMoveTreeAfter () throws Exception {
        File folder = new File(workDir, "folder");
        folder.mkdirs();
        File file1 = new File(folder, "file1");
        write(file1, "file1 content");
        File file2 = new File(folder, "file2");
        write(file2, "file2 content");
        File subFolder1 = new File(folder, "folder1");
        subFolder1.mkdirs();
        File file11 = new File(subFolder1, "file");
        write(file11, "file11 content");
        File subFolder2 = new File(folder, "folder2");
        subFolder2.mkdirs();
        File file21 = new File(subFolder2, "file");
        write(file21, "file21 content");

        File target = new File(workDir, "target");
        File moved1 = new File(target, file1.getName());
        File moved2 = new File(target, file2.getName());
        File moved11 = new File(new File(target, file11.getParentFile().getName()), file11.getName());
        File moved21 = new File(new File(target, file21.getParentFile().getName()), file21.getName());

        add(file1, file11, file21);
        commit(file1, file11);

        folder.renameTo(target);
        GitClient client = getClient(workDir);
        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.rename(folder, target, true, m);
        assertTrue(moved1.exists());
        assertTrue(moved2.exists());
        assertTrue(moved11.exists());
        assertTrue(moved21.exists());
        assertEquals(new HashSet<File>(Arrays.asList(moved1, moved11, moved21)), m.notifiedFiles);
        Map<File, GitStatus> statuses = client.getStatus(new File[] { workDir }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertStatus(statuses, workDir, file1, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_REMOVED, false);
        assertNull(statuses.get(file2));
        assertStatus(statuses, workDir, file11, true, GitStatus.Status.STATUS_REMOVED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_REMOVED, false);
        assertNull(statuses.get(file21));
        assertStatus(statuses, workDir, moved1, true, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, false);
        assertStatus(statuses, workDir, moved2, false, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_ADDED, false);
        assertStatus(statuses, workDir, moved11, true, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, false);
        assertStatus(statuses, workDir, moved21, true, GitStatus.Status.STATUS_ADDED, GitStatus.Status.STATUS_NORMAL, GitStatus.Status.STATUS_ADDED, false);
        assertTrue(statuses.get(moved1).isRenamed());
        assertTrue(statuses.get(moved11).isRenamed());
        // file21 was not committed
        assertFalse(statuses.get(moved21).isRenamed());
    }

    public void testCancel () throws Exception {
        final File folder = new File(workDir, "folder");
        folder.mkdirs();
        File file = new File(folder, "file");
        file.createNewFile();
        File file2 = new File(folder, "file2");
        file2.createNewFile();
        final Monitor m = new Monitor();
        final GitClient client = getClient(workDir);
        client.add(new File[] { }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        final Exception[] exs = new Exception[1];
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.addNotificationListener(m);
                    client.rename(folder, new File(folder.getParentFile(), "folder2"), false, m);
                } catch (GitException ex) {
                    exs[0] = ex;
                }
            }
        });
        m.cont = false;
        t1.start();
        m.waitAtBarrier();
        m.cancel();
        m.cont = true;
        t1.join();
        assertTrue(m.isCanceled());
        assertEquals(1, m.count);
        assertEquals(null, exs[0]);
    }
}
