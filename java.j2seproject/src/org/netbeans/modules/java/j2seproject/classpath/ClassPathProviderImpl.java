/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.java.j2seproject.classpath;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.spi.java.classpath.ClassPathFactory;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.AntProjectListener;
import org.netbeans.spi.project.support.ant.AntProjectEvent;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.WeakListeners;

/**
 * Defines the various class paths for a J2SE project.
 */
public final class ClassPathProviderImpl implements ClassPathProvider, PropertyChangeListener {
    
    private static final String SRC_DIR = "src.dir"; // NOI18N
    private static final String TEST_SRC_DIR = "test.src.dir"; // NOI18N
    private static final String BUILD_CLASSES_DIR = "build.classes.dir"; // NOI18N
    private static final String DIST_JAR = "dist.jar"; // NOI18N
    private static final String BUILD_TEST_CLASSES_DIR = "build.test.classes.dir"; // NOI18N
    
    private final AntProjectHelper helper;
    private final PropertyEvaluator evaluator;
    private final Reference[] cache = new SoftReference[7];

    private final Map/*<String,FileObject>*/ dirCache = new HashMap();

    public ClassPathProviderImpl(AntProjectHelper helper, PropertyEvaluator evaluator) {
        this.helper = helper;
        this.evaluator = evaluator;
        evaluator.addPropertyChangeListener(WeakListeners.propertyChange(this, evaluator));
    }

    private synchronized FileObject getDir(String propname) {
        FileObject fo = (FileObject) this.dirCache.get (propname);
        if (fo == null ||  !fo.isValid()) {
            String prop = evaluator.getProperty(propname);
            if (prop != null) {
                fo = helper.resolveFileObject(prop);
                this.dirCache.put (propname, fo);
            }
        }
        return fo;
    }
    
    private FileObject getPrimarySrcDir() {
        return getDir(SRC_DIR);
    }
    
    private FileObject getTestSrcDir() {
        return getDir(TEST_SRC_DIR);
    }
    
    private FileObject getBuildClassesDir() {
        return getDir(BUILD_CLASSES_DIR);
    }
    
    private FileObject getDistJar() {
        return getDir(DIST_JAR);
    }
    
    private FileObject getBuildTestClassesDir() {
        return getDir(BUILD_TEST_CLASSES_DIR);
    }
    
    private int getType(FileObject file) {
        FileObject dir = getPrimarySrcDir();
        if (dir != null && (dir.equals(file) || FileUtil.isParentOf(dir, file))) {
            return 0;
        }
        dir = getTestSrcDir();
        if (dir != null && (dir.equals(file) || FileUtil.isParentOf(dir, file))) {
            return 1;
        }
        dir = getBuildClassesDir();
        if (dir != null && (dir.equals(file) || FileUtil.isParentOf(dir, file))) {
            return 2;
        }
        dir = getDistJar();
        if (dir != null && (dir.equals(file))) {     //TODO: When MasterFs check also isParentOf
            return 2;
        }
        dir = getBuildTestClassesDir();
        if (dir != null && (dir.equals(file) || FileUtil.isParentOf(dir,file))) {
            return 3;
        }
        return -1;
    }
    
    private ClassPath getCompileTimeClasspath(FileObject file) {
        int type = getType(file);
        if (type < 0 || type > 1) {
            return null;
        }
        ClassPath cp = null;
        if (cache[2+type] == null || (cp = (ClassPath)cache[2+type].get()) == null) {
            if (type == 0) {
                cp = ClassPathFactory.createClassPath(
                new ProjectClassPathImplementation(helper, "javac.classpath", evaluator)); // NOI18N
            }
            else {
                cp = ClassPathFactory.createClassPath(
                new ProjectClassPathImplementation(helper, "javac.test.classpath", evaluator)); // NOI18N
            }
            cache[2+type] = new SoftReference(cp);
        }
        return cp;
    }
    
    private ClassPath getRunTimeClasspath(FileObject file) {
        int type = getType(file);
        if (type < 0 || type > 3) {
            return null;
        } else if (type > 1) {
            type-=2;            //Compiled source transform into source
        }
        ClassPath cp = null;
        if (cache[4+type] == null || (cp = (ClassPath)cache[4+type].get())== null) {
            if (type == 0) {
                cp = ClassPathFactory.createClassPath(
                new ProjectClassPathImplementation(helper, "run.classpath", evaluator)); // NOI18N
            }
            else if (type == 1) {
                cp = ClassPathFactory.createClassPath(
                new ProjectClassPathImplementation(helper, "run.test.classpath", evaluator)); // NOI18N
            }
            cache[4+type] = new SoftReference(cp);
        }
        return cp;
    }
    
    private ClassPath getSourcepath(FileObject file) {
        int type = getType(file);
        if (type < 0 || type > 1) {
            return null;
        }
        ClassPath cp = null;
        if (cache[type] == null || (cp = (ClassPath)cache[type].get()) == null) {
            if (type == 0) {
                cp = ClassPathFactory.createClassPath(
                new ProjectClassPathImplementation(helper, SRC_DIR, evaluator)); // NOI18N
            }
            else {
                cp = ClassPathFactory.createClassPath(
                new ProjectClassPathImplementation(helper, TEST_SRC_DIR, evaluator)); // NOI18N
            }
            cache[type] = new SoftReference(cp);
        }
        return cp;
    }
    
    private ClassPath getBootClassPath() {
        ClassPath cp = null;
        if (cache[6] == null || (cp = (ClassPath)cache[6].get()) == null) {
            cp = ClassPathFactory.createClassPath(new BootClassPathImplementation(helper, evaluator));
            cache[6] = new SoftReference(cp);
        }
        return cp;
    }
    
    public ClassPath findClassPath(FileObject file, String type) {
        if (type.equals(ClassPath.COMPILE)) {
            return getCompileTimeClasspath(file);
        } else if (type.equals(ClassPath.EXECUTE)) {
            return getRunTimeClasspath(file);
        } else if (type.equals(ClassPath.SOURCE)) {
            return getSourcepath(file);
        } else if (type.equals(ClassPath.BOOT)) {
            return getBootClassPath();
        } else {
            return null;
        }
    }
    
    /**
     * Returns array of all classpaths of the given type in the project.
     * The result is used for example for GlobalPathRegistry registrations.
     */
    public ClassPath[] getProjectClassPaths(String type) {
        if (ClassPath.BOOT.equals(type)) {
            return new ClassPath[]{getBootClassPath()};
        }
        if (ClassPath.COMPILE.equals(type)) {
            List/*<ClassPath>*/ l = new ArrayList(2);
            FileObject d = getPrimarySrcDir();
            if (d != null) {
                l.add(getCompileTimeClasspath(d));
            }
            d = getTestSrcDir();
            if (d != null) {
                l.add(getCompileTimeClasspath(d));
            }
            return (ClassPath[])l.toArray(new ClassPath[l.size()]);
        }
        if (ClassPath.SOURCE.equals(type)) {
            List/*<ClassPath>*/ l = new ArrayList(2);
            FileObject d = getPrimarySrcDir();
            if (d != null) {
                l.add(getSourcepath(d));
            }
            d = getTestSrcDir();
            if (d != null) {
                l.add(getSourcepath(d));
            }
            return (ClassPath[])l.toArray(new ClassPath[l.size()]);
        }
        assert false;
        return null;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        dirCache.remove(evt.getPropertyName());
    }
    
}

