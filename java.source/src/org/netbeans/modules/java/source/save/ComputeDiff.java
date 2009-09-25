/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.java.source.save;

import java.util.*;


/**
 * Compares two collections, returning a list of the additions, changes, and
 * deletions between them. A <code>Comparator</code> may be passed as an
 * argument to the constructor, and will thus be used. If not provided, the
 * initial value in the <code>a</code> ("from") collection will be looked at to
 * see if it supports the <code>Comparable</code> interface. If so, its
 * <code>equals</code> and <code>compareTo</code> methods will be invoked on the
 * instances in the "from" and "to" collections; otherwise, for speed, hash
 * codes from the objects will be used instead for comparison.
 *
 * <p>The file FileDiff.java shows an example usage of this class, in an
 * application similar to the Unix "diff" program.</p>
 */
public class ComputeDiff<E> {
    /**
     * The source array, AKA the "from" values.
     */
    private E[] a;
    
    /**
     * The target array, AKA the "to" values.
     */
    private E[] b;
    
    /**
     * The list of differences, as <code>Difference</code> instances.
     */
    private List<Difference> diffs = new ArrayList<Difference>();
    
    /**
     * The pending, uncommitted difference.
     */
    private Difference pending;
    
    /**
     * The comparator used, if any.
     */
    private Comparator<E> comparator;
    
    /**
     * The thresholds.
     */
    private TreeMap<Integer, Integer> thresh;
    
    /**
     * Constructs the Diff object for the two arrays, using the given comparator.
     */
    public ComputeDiff(E[] a, E[] b, Comparator<E> comp) {
        this.a = a;
        this.b = b;
        this.comparator = comp;
        this.thresh = null;     // created in getLongestCommonSubsequences
    }
    
    /**
     * Constructs the Diff object for the two arrays, using the default
     * comparison mechanism between the objects, such as <code>equals</code> and
     * <code>compareTo</code>.
     */
    public ComputeDiff(E[] a, E[] b) {
        this(a, b, null);
    }
    
    /**
     * Constructs the Diff object for the two collections, using the given
     * comparator.
     */
    @SuppressWarnings("unchecked")
    public ComputeDiff(Collection<E> a, Collection<E> b, Comparator<E> comp) {
        this((E[]) a.toArray(), (E[]) b.toArray(), comp);
    }
    
    /**
     * Constructs the Diff object for the two collections, using the default
     * comparison mechanism between the objects, such as <code>equals</code> and
     * <code>compareTo</code>.
     */
    @SuppressWarnings("unchecked")
    public ComputeDiff(Collection<E> a, Collection<E> b) {
        this((E[]) a.toArray(), (E[]) b.toArray(), null);
    }
    
    /**
     * Runs diff and returns the results.
     */
    List<Difference> diff() {
        traverseSequences();
        
        // add the last difference, if pending:
        if (pending != null) {
            diffs.add(pending);
        }
        
        return diffs;
    }
    
    /**
     * Traverses the sequences, seeking the longest common subsequences,
     * invoking the methods <code>finishedA</code>, <code>finishedB</code>,
     * <code>onANotB</code>, and <code>onBNotA</code>.
     */
    protected void traverseSequences() {
        Integer[] matches = getLongestCommonSubsequences();
        
        int lastA = a.length - 1;
        int lastB = b.length - 1;
        int bi = 0;
        int ai;
        
        int lastMatch = matches.length - 1;
        
        for (ai = 0; ai <= lastMatch; ++ai) {
            Integer bLine = matches[ai];
            
            if (bLine == null) {
                onANotB(ai, bi);
            } else {
                while (bi < bLine.intValue()) {
                    onBNotA(ai, bi++);
                }
                
                onMatch(ai, bi++);
            }
        }
        
        boolean calledFinishA = false;
        boolean calledFinishB = false;
        
        while (ai <= lastA || bi <= lastB) {
            
            // last A?
            if (ai == lastA + 1 && bi <= lastB) {
                if (!calledFinishA && callFinishedA()) {
                    finishedA(lastA);
                    calledFinishA = true;
                } else {
                    while (bi <= lastB) {
                        onBNotA(ai, bi++);
                    }
                }
            }
            
            // last B?
            if (bi == lastB + 1 && ai <= lastA) {
                if (!calledFinishB && callFinishedB()) {
                    finishedB(lastB);
                    calledFinishB = true;
                } else {
                    while (ai <= lastA) {
                        onANotB(ai++, bi);
                    }
                }
            }
            
            if (ai <= lastA) {
                onANotB(ai++, bi);
            }
            
            if (bi <= lastB) {
                onBNotA(ai, bi++);
            }
        }
    }
    
    /**
     * Override and return true in order to have <code>finishedA</code> invoked
     * at the last element in the <code>a</code> array.
     */
    protected boolean callFinishedA() {
        return false;
    }
    
    /**
     * Override and return true in order to have <code>finishedB</code> invoked
     * at the last element in the <code>b</code> array.
     */
    protected boolean callFinishedB() {
        return false;
    }
    
    /**
     * Invoked at the last element in <code>a</code>, if
     * <code>callFinishedA</code> returns true.
     */
    protected void finishedA(int lastA) {
    }
    
    /**
     * Invoked at the last element in <code>b</code>, if
     * <code>callFinishedB</code> returns true.
     */
    protected void finishedB(int lastB) {
    }
    
    /**
     * Invoked for elements in <code>a</code> and not in <code>b</code>.
     */
    protected void onANotB(int ai, int bi) {
        if (pending == null) {
            pending = new Difference(ai, ai, bi, -1);
        } else {
            pending.setDeleted(ai);
        }
    }
    
    /**
     * Invoked for elements in <code>b</code> and not in <code>a</code>.
     */
    protected void onBNotA(int ai, int bi) {
        if (pending == null) {
            pending = new Difference(ai, -1, bi, bi);
        } else {
            pending.setAdded(bi);
        }
    }
    
    /**
     * Invoked for elements matching in <code>a</code> and <code>b</code>.
     */
    protected void onMatch(int ai, int bi) {
        if (pending == null) {
            // no current pending
        } else {
            diffs.add(pending);
            pending = null;
        }
    }
    
    /**
     * Compares the two objects, using the comparator provided with the
     * constructor, if any.
     */
    protected boolean equals(E x, E y) {
        return comparator == null ? x.equals(y) : comparator.compare(x, y) == 0;
    }
    
    /**
     * Returns an array of the longest common subsequences.
     */
    public Integer[] getLongestCommonSubsequences() {
        int aStart = 0;
        int aEnd = a.length - 1;
        
        int bStart = 0;
        int bEnd = b.length - 1;
        
        TreeMap<Integer, Integer> matches = new TreeMap<Integer, Integer>();
        
        while (aStart <= aEnd && bStart <= bEnd && equals(a[aStart], b[bStart])) {
            matches.put(new Integer(aStart++), new Integer(bStart++));
        }
        
        while (aStart <= aEnd && bStart <= bEnd && equals(a[aEnd], b[bEnd])) {
            matches.put(new Integer(aEnd--), new Integer(bEnd--));
        }
        
        Map<E, List<Integer>> bMatches = null;
        if (comparator == null) {
            if (a.length > 0 && a[0] instanceof Comparable) {
                // this uses the Comparable interface
                bMatches = new TreeMap<E, List<Integer>>();
            } else {
                // this just uses hashCode()
                bMatches = new HashMap<E, List<Integer>>();
            }
        } else {
            // we don't really want them sorted, but this is the only Map
            // implementation (as of JDK 1.4) that takes a comparator.
            bMatches = new TreeMap<E, List<Integer>>(comparator);
        }
        
        for (int bi = bStart; bi <= bEnd; ++bi) {
            E element = b[bi];
            E key = element;
            List<Integer> positions = bMatches.get(key);
            if (positions == null) {
                positions = new ArrayList<Integer>();
                bMatches.put(key, positions);
            }
            positions.add(new Integer(bi));
        }
        
        thresh = new TreeMap<Integer, Integer>();
        Map<Integer, Object[]> links = new HashMap<Integer, Object[]>();
        
        for (int i = aStart; i <= aEnd; ++i) {
            E aElement = a[i]; // keygen here.
            List<Integer> positions = bMatches.get(aElement);
            
            if (positions != null) {
                Integer  k   = new Integer(0);
                ListIterator<Integer> pit = positions.listIterator(positions.size());
                while (pit.hasPrevious()) {
                    Integer j = pit.previous();
                    
                    k = insert(j, k);
                    
                    if (k == null) {
                        // nothing
                    } else {
                        Object value = k.intValue() > 0 ? links.get(new Integer(k.intValue() - 1)) : null;
                        links.put(k, new Object[] { value, new Integer(i), j });
                    }
                }
            }
        }
        
        if (thresh.size() > 0) {
            Integer  ti   = thresh.lastKey();
            Object[] link = links.get(ti);
            while (link != null) {
                Integer x = (Integer)link[1];
                Integer y = (Integer)link[2];
                matches.put(x, y);
                link = (Object[])link[0];
            }
        }
        
        return toArray(matches);
    }
    
    /**
     * Converts the map (indexed by java.lang.Integers) into an array.
     */
    protected static Integer[] toArray(TreeMap map) {
        int       size = map.size() == 0 ? 0 : 1 + ((Integer)map.lastKey()).intValue();
        Integer[] ary  = new Integer[size];
        Iterator  it   = map.keySet().iterator();
        
        while (it.hasNext()) {
            Integer idx = (Integer)it.next();
            Integer val = (Integer)map.get(idx);
            ary[idx.intValue()] = val;
        }
        return ary;
    }
    
    /**
     * Returns whether the integer is not zero (including if it is not null).
     */
    protected static boolean isNonzero(Integer i) {
        return i != null && i.intValue() != 0;
    }
    
    /**
     * Returns whether the value in the map for the given index is greater than
     * the given value.
     */
    protected boolean isGreaterThan(Integer index, Integer val) {
        Integer lhs = thresh.get(index);
        return lhs != null && val != null && lhs.compareTo(val) > 0;
    }
    
    /**
     * Returns whether the value in the map for the given index is less than
     * the given value.
     */
    protected boolean isLessThan(Integer index, Integer val) {
        Integer lhs = thresh.get(index);
        return lhs != null && (val == null || lhs.compareTo(val) < 0);
    }
    
    /**
     * Returns the value for the greatest key in the map.
     */
    protected Integer getLastValue() {
        return thresh.get(thresh.lastKey());
    }
    
    /**
     * Adds the given value to the "end" of the threshold map, that is, with the
     * greatest index/key.
     */
    protected void append(Integer value) {
        Integer addIdx = null;
        if (thresh.size() == 0) {
            addIdx = new Integer(0);
        } else {
            Integer lastKey = thresh.lastKey();
            addIdx = new Integer(lastKey.intValue() + 1);
        }
        thresh.put(addIdx, value);
    }
    
    /**
     * Inserts the given values into the threshold map.
     */
    protected Integer insert(Integer j, Integer k) {
        if (isNonzero(k) && isGreaterThan(k, j) && isLessThan(new Integer(k.intValue() - 1), j)) {
            thresh.put(k, j);
        } else {
            int hi = -1;
            
            if (isNonzero(k)) {
                hi = k.intValue();
            } else if (thresh.size() > 0) {
                hi = (thresh.lastKey()).intValue();
            }
            
            // off the end?
            if (hi == -1 || j.compareTo(getLastValue()) > 0) {
                append(j);
                k = new Integer(hi + 1);
            } else {
                // binary search for insertion point:
                int lo = 0;
                
                while (lo <= hi) {
                    int     index = (hi + lo) / 2;
                    Integer val   = thresh.get(new Integer(index));
                    int     cmp   = j.compareTo(val);
                    
                    if (cmp == 0) {
                        return null;
                    } else if (cmp > 0) {
                        lo = index + 1;
                    } else {
                        hi = index - 1;
                    }
                }
                
                thresh.put(new Integer(lo), j);
                k = new Integer(lo);
            }
        }
        return k;
    }
    
}
