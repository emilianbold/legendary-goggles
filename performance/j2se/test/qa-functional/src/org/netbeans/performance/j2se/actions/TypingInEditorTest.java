/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.performance.j2se.actions;

import org.netbeans.modules.performance.utilities.PerformanceTestCase;
import org.netbeans.performance.j2se.setup.J2SESetup;

import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.EditorWindowOperator;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.nodes.SourcePackagesNode;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JEditorPaneOperator;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.junit.NbTestSuite;

import java.awt.event.KeyEvent;
import java.awt.Robot;
import java.awt.AWTException;

/**
 * Test of typing in opened source editor.
 *
 * @author  anebuzelsky@netbeans.org, mmirilovic@netbeans.org
 */
public class TypingInEditorTest extends PerformanceTestCase {
    
    private EditorOperator editorOperator;
    protected String fileName;
    protected int caretPositionX, caretPositionY;
    Node fileToBeOpened;
    JEditorPaneOperator epo;
    Robot r;
    private int keyCode = KeyEvent.VK_SPACE;
    private int repeatTimes = 1;
    
    /** Creates a new instance of TypingInEditor */
    public TypingInEditorTest(String testName) {
        super(testName);
        WAIT_AFTER_OPEN = 200;
    }
    
    /** Creates a new instance of TypingInEditor */
    public TypingInEditorTest(String testName, String performanceDataName) {
        super(testName, performanceDataName);
        WAIT_AFTER_OPEN = 200;
    }

    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTest(NbModuleSuite.create(NbModuleSuite.createConfiguration(J2SESetup.class)
             .addTest(TypingInEditorTest.class)
             .enableModules(".*").clusters(".*")));
        return suite;
    }

    public void testTxtEditor() {
        fileName = "textfile.txt";
        caretPositionX = 9;
        caretPositionY = 1;
        fileToBeOpened = new Node(new SourcePackagesNode("PerformanceTestData"), "org.netbeans.test.performance|" + fileName);
        doMeasurement();
    }
    
    public void testTxtEditor10() {
        fileName = "textfile.txt";
        repeatTimes = 10;
        caretPositionX = 9;
        caretPositionY = 1;
        fileToBeOpened = new Node(new SourcePackagesNode("PerformanceTestData"), "org.netbeans.test.performance|" + fileName);
        doMeasurement();
        repeatTimes = 1;
    }

    public void testJavaEditor() {
        fileName = "Main.java";
        caretPositionX = 9;
        caretPositionY = 1;
        fileToBeOpened = new Node(new SourcePackagesNode("PerformanceTestData"), "org.netbeans.test.performance|" + fileName);
        doMeasurement();
    }

    public void testJavaEditor10() {
        repeatTimes = 10;
        fileName = "Main.java";
        caretPositionX = 9;
        caretPositionY = 1;
        fileToBeOpened = new Node(new SourcePackagesNode("PerformanceTestData"), "org.netbeans.test.performance|" + fileName);
        doMeasurement();
        repeatTimes = 1;
    }

    public void testJavaEditor10Enter() {
        keyCode = KeyEvent.VK_ENTER;
        repeatTimes = 10;
        fileName = "Main.java";
        caretPositionX = 9;
        caretPositionY = 1;
        fileToBeOpened = new Node(new SourcePackagesNode("PerformanceTestData"), "org.netbeans.test.performance|" + fileName);
        doMeasurement();
        keyCode = KeyEvent.VK_SPACE;
        repeatTimes = 1;
    }

    @Override
    public void initialize() {
        repaintManager().addRegionFilter(repaintManager().EDITOR_FILTER);
        new OpenAction().performAPI(fileToBeOpened);
        editorOperator = EditorWindowOperator.getEditor(fileName);
        editorOperator.setCaretPosition(caretPositionX,caretPositionY);
    }
    
    public void prepare() {
        epo=new JEditorPaneOperator(editorOperator);     
        try {
             r = new Robot();
//             r.setAutoDelay(10);
        } catch (AWTException e) {};
    }
    
    public ComponentOperator open(){
        //epo.pressKey(KeyEvent.VK_A);//typeKey('a'/*KeyEvent.VK_A*/);
        for (int i = 0; i < repeatTimes; i++) {
            r.keyPress(keyCode);
            r.keyRelease(keyCode);
        }
        return null;
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void shutdown() {
        repaintManager().resetRegionFilters();
        editorOperator.closeDiscard();
    }

}
