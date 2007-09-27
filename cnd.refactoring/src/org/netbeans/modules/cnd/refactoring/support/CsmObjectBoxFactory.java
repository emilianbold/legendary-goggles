/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.cnd.refactoring.support;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.openide.filesystems.FileObject;

/**
 * factory caching intervals with csm objects
 *
 * @author Vladimir Voskresensky
 */
public class CsmObjectBoxFactory {

    private static CsmObjectBoxFactory instance;
    private Map<FileObject, Interval> map = new WeakHashMap<FileObject, Interval>();

    /**
     * Creates a new instance of ElementGripFactory
     */
    private CsmObjectBoxFactory() {
    }

    public static CsmObjectBoxFactory getDefault() {
        if (instance == null) {
            instance = new CsmObjectBoxFactory();
        }
        return instance;
    }

    public void cleanUp() {
        map.clear();
    }

    public CsmObjectBox get(FileObject fileObject, int position) {
        Interval start = map.get(fileObject);
        if (start == null) {
            return null;
        }
        try {
            return start.get(position).item;
        } catch (RuntimeException e) {
            return start.item;
        }
    }

    public CsmObjectBox getParent(CsmObjectBox el) {
        Interval start = map.get(el.getFileObject());
        return start.getParent(el);
    }

    public void put(FileObject parentFile) {
//        Interval root = map.get(parentFile);
//        Interval i = Interval.createInterval(tp, info, root, null, parentFile);
//        if (i != null) {
//            map.put(parentFile, i);
//        }
    }

    private static class Interval {

        long from = -1;
        long to = -1;
        Set<Interval> subintervals = new HashSet<Interval>();
        CsmObjectBox item = null;

        Interval get(long position) {
            if (from <= position && to >= position) {
                for (Interval o : subintervals) {
                    Interval ob = o.get(position);
                    if (ob != null) {
                        return ob;
                    }
                }
                return this;
            }
            return null;
        }

        CsmObjectBox getParent(CsmObjectBox eh) {
            for (Interval i : subintervals) {
                if (i.item.equals(eh)) {
                    return this.item;
                } else {
                    CsmObjectBox e = i.getParent(eh);
                    if (e != null) {
                        return e;
                    }
                }
            }
            return null;
        }

//        public static Interval createInterval(TreePath tp, CompilationInfo info, Interval root, Interval p, FileObject parentFile) {
//            Tree t = tp.getLeaf();
//            long start = info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), t);
//            long end = info.getTrees().getSourcePositions().getEndPosition(info.getCompilationUnit(), t);
//            Element current = info.getTrees().getElement(tp);
//            Tree.Kind kind = tp.getLeaf().getKind();
//            if (kind != Tree.Kind.CLASS && kind != Tree.Kind.METHOD) {
//                if (tp.getParentPath() == null || tp.getParentPath().getLeaf().getKind() == Tree.Kind.COMPILATION_UNIT) {
//                    //xxx: rather workaround. should be fixed better.
//                    return null;
//                } else {
//                    return createInterval(tp.getParentPath(), info, root, p, parentFile);
//                }
//            }
//            Interval i = null;
//            if (root != null) {
//                Interval o = root.get(start);
//                if (o != null && current != null && current.equals(o.item.resolveElement(info))) {
//                    if (p != null) {
//                        o.subintervals.add(p);
//                    }
//                    return null;
//                }
//            }
//            if (i == null) {
//                i = new Interval();
//            }
//            if (i.from != start) {
//                i.from = start;
//                i.to = end;
//                CsmObjectBox currentHandle2 = new CsmObjectBox(tp, info);
//                i.item = currentHandle2;
//            }
//            if (p != null) {
//                i.subintervals.add(p);
//            }
//            if (tp.getParentPath().getLeaf().getKind() == Tree.Kind.COMPILATION_UNIT) {
//                return i;
//            }
//            return createInterval(tp.getParentPath(), info, root, i, parentFile);
//        }
    }
}