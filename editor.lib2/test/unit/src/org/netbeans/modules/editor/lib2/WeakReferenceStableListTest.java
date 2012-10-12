/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.editor.lib2;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.netbeans.junit.Filter;
import org.netbeans.junit.NbTestCase;

/**
 *
 * @author Miloslav Metelka
 */
public class WeakReferenceStableListTest extends NbTestCase {
    
    private List<Object> expected;
    
    private WeakReferenceStableList<Object> tested;
    
    public WeakReferenceStableListTest(String testName) {
        super(testName);
        List<String> includes = new ArrayList<String>();
//        includes.add("testSimple1");
//        filterTests(includes);
    }

    private void filterTests(List<String> includeTestNames) {
        List<Filter.IncludeExclude> includeTests = new ArrayList<Filter.IncludeExclude>();
        for (String testName : includeTestNames) {
            includeTests.add(new Filter.IncludeExclude(testName, ""));
        }
        Filter filter = new Filter();
        filter.setIncludes(includeTests.toArray(new Filter.IncludeExclude[includeTests.size()]));
        setFilter(filter);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp(); //To change body of generated methods, choose Tools | Templates.
        expected = new ArrayList<Object>();
        tested= new WeakReferenceStableList<Object>();
    }

    @Override
    protected Level logLevel() {
        return Level.INFO; // null;
//        return Level.FINEST;
    }

    public void testAddNull() throws Exception {
        try {
            tested.add(null);
            fail("Not expected to accept nulls");
        } catch (AssertionError ex) {
            // Expected
        }
    }

    public void testAddRemove() throws Exception {
        Integer i1 = new Integer(1001);
        Integer i2 = new Integer(1002);
        Integer i3 = new Integer(1003);
        Integer i4 = new Integer(1004);
        Integer i5 = new Integer(1005);
        Integer i6 = new Integer(1006);
        add(i1);
        check();
        add(i2);
        check();
        add(i3);
        check();
        add(i4);
        check();

        assertTrue(expected.remove(i4));
        Reference<Integer> refI4 = new WeakReference<Integer>(i4);
        i4 = null;
        assertGC("i4 not GCable", refI4);
        gc();
        check();
        
        add(i5);
        check();
        add(i6);
        check();
        
        assertTrue(expected.remove(i3));
        i3 = null;
        assertTrue(expected.remove(i1));
        i1 = null;
        gc(); // should remove i3 from tested
        check();
        assertTrue(expected.remove(i5));
        i5 = null;
        assertTrue(expected.remove(i6));
        i6 = null;
        gc(); // should remove i3 from tested
        check();
    }
    
    private void add(Object o) {
        expected.add(o);
        tested.add(o);
    }
    
    private void check() {
        List<Object> testedList = tested.getList();
        int j = 0;
        for (int i = 0; i < expected.size(); i++) {
            Object testedValue;
            while ((testedValue = testedList.get(j++)) == null) { }
            assertSame("Index=" + i, expected.get(i), testedValue);
        }
        while (j < testedList.size()) {
            Object testedValue = testedList.get(j++);
            assertNull("Expected null", testedValue);
        }
        
        int i = 0;
        for (Object testedValue : testedList) { // Iterator skips null values
            assertSame("Index=" + i, expected.get(i), testedValue);
            i++;
        }
        assertEquals("Wrong size", expected.size(), i);
    }
    
    private static void gc() {
        System.gc();
        Runtime.getRuntime().runFinalization();
        System.gc();
        System.gc();
    }

}
