/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.project;

import java.net.URI;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Lookup;

/**
 * Test functionality of FileOwnerQuery.
 * @author Jesse Glick
 */
public class FileOwnerQueryTest extends NbTestCase {
    
    public FileOwnerQueryTest(String name) {
        super(name);
    }
    
    private FileObject scratch;
    private FileObject projdir;
    private FileObject randomfile;
    private FileObject projfile;
    private FileObject projfile2;
    private FileObject subprojdir;
    private FileObject subprojfile;
    private Project p;
    
    protected void setUp() throws Exception {
        TestUtil.setLookup(new Object[] {
            TestUtil.testProjectFactory(),
        }, FileOwnerQueryTest.class.getClassLoader());
        ProjectManager.getDefault().reset();
        FileOwnerQuery.reset();
        scratch = TestUtil.makeScratchDir(this);
        projdir = scratch.createFolder("my-project");
        projdir.createFolder("testproject");
        randomfile = scratch.createData("randomfile");
        projfile = projdir.createData("projfile");
        FileObject projsubdir = projdir.createFolder("projsubdir");
        projfile2 = projsubdir.createData("projfile2");
        subprojdir = projdir.createFolder("subproject");
        subprojdir.createFolder("testproject");
        subprojfile = subprojdir.createData("subprojfile");
        scratch.createFolder("external1").createFolder("subdir").createData("file");
        scratch.createFolder("external2").createFolder("subdir").createData("file");
        scratch.createFolder("external3").createFolder("subproject").createFolder("testproject");
        p = ProjectManager.getDefault().findProject(projdir);
        assertNotNull("found a project successfully", p);
    }
    
    protected void tearDown() throws Exception {
        scratch = null;
        projdir = null;
        randomfile = null;
        projfile = null;
        p = null;
        TestUtil.setLookup(Lookup.EMPTY);
    }
    
    public void testFileOwner() throws Exception {
        assertEquals("correct project from projfile FileObject", p, FileOwnerQuery.getOwner(projfile));
        URI u = FileUtil.toFile(projfile).toURI();
        assertEquals("correct project from projfile URI " + u, p, FileOwnerQuery.getOwner(u));
        assertEquals("correct project from projfile2 FileObject", p, FileOwnerQuery.getOwner(projfile2));
        assertEquals("correct project from projfile2 URI", p, FileOwnerQuery.getOwner(FileUtil.toFile(projfile2).toURI()));
        assertEquals("correct project from projdir FileObject", p, FileOwnerQuery.getOwner(projdir));
        assertEquals("correct project from projdir URI", p, FileOwnerQuery.getOwner(FileUtil.toFile(projdir).toURI()));
        // Check that it loads the project even though we have not touched it yet:
        Project p2 = FileOwnerQuery.getOwner(subprojfile);
        Project subproj = ProjectManager.getDefault().findProject(subprojdir);
        assertEquals("correct project from subprojdir FileObject", subproj, p2);
        assertEquals("correct project from subprojdir URI", subproj, FileOwnerQuery.getOwner(FileUtil.toFile(subprojdir).toURI()));
        assertEquals("correct project from subprojfile FileObject", subproj, FileOwnerQuery.getOwner(subprojfile));
        assertEquals("correct project from subprojfile URI", subproj, FileOwnerQuery.getOwner(FileUtil.toFile(subprojfile).toURI()));
        assertEquals("no project from randomfile FileObject", null, FileOwnerQuery.getOwner(randomfile));
        assertEquals("no project from randomfile URI", null, FileOwnerQuery.getOwner(FileUtil.toFile(randomfile).toURI()));
        assertEquals("no project in C:\\", null, FileOwnerQuery.getOwner(URI.create("file:/C:/")));
    }
    
    // XXX test jar: URIs
    
    public void testExternalOwner() throws Exception {
        FileObject ext1 = scratch.getFileObject("external1");
        FileObject extfile1 = ext1.getFileObject("subdir/file");
        assertEquals("no owner yet", null, FileOwnerQuery.getOwner(extfile1));
        FileOwnerQuery.markExternalOwner(ext1, p, FileOwnerQuery.EXTERNAL_ALGORITHM_TRANSIENT);
        assertEquals("now have an owner", p, FileOwnerQuery.getOwner(extfile1));
        assertEquals("even for the projdir", p, FileOwnerQuery.getOwner(ext1));
        assertEquals("but not for something else", null, FileOwnerQuery.getOwner(scratch));
        FileObject ext2 = scratch.getFileObject("external2");
        FileObject extfile2 = ext2.getFileObject("subdir/file");
        assertEquals("no owner yet", null, FileOwnerQuery.getOwner(extfile2));
        Project p2 = ProjectManager.getDefault().findProject(subprojdir);
        FileOwnerQuery.markExternalOwner(ext2, p2, FileOwnerQuery.EXTERNAL_ALGORITHM_TRANSIENT);
        assertEquals("now have an owner", p2, FileOwnerQuery.getOwner(extfile2));
        assertEquals("even for the projdir", p2, FileOwnerQuery.getOwner(ext2));
        assertEquals("but not for something else", null, FileOwnerQuery.getOwner(scratch));
        assertEquals("still correct for first proj", p, FileOwnerQuery.getOwner(extfile1));
        FileObject ext3 = scratch.getFileObject("external3");
        assertEquals("no owner yet", null, FileOwnerQuery.getOwner(ext3));
        FileOwnerQuery.markExternalOwner(ext3, p, FileOwnerQuery.EXTERNAL_ALGORITHM_TRANSIENT);
        assertEquals("now have an owner", p, FileOwnerQuery.getOwner(ext3));
        FileObject ext3subproj = ext3.getFileObject("subproject");
        Project p3 = FileOwnerQuery.getOwner(ext3subproj);
        assertNotSame("different project", p, p3);
        assertEquals("but subprojects are not part of it", ProjectManager.getDefault().findProject(ext3subproj), p3);
        FileOwnerQuery.markExternalOwner(ext3, null, FileOwnerQuery.EXTERNAL_ALGORITHM_TRANSIENT);
        assertEquals("unmarking an owner works", null, FileOwnerQuery.getOwner(ext3));
    }
    
    // XXX test URI usage of external owner
    // XXX test GC of roots and projects used in external ownership:
    // - the owning Project is not held strongly (just PM's soft cache)
    // - the root is not held strongly (note - FOQ won't be accurate after it is collected)
    // XXX test IAE from illegal calls to FOQ.markExternalOwner
    // XXX test an owner which is above the project directory
    
}
