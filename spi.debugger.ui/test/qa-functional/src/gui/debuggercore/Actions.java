/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * The Original Software is NetBeans.
 * The Initial Developer of the Original Software is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2005
 * All Rights Reserved.
 *
 * Contributor(s): Sun Microsystems, Inc.
 */

package gui.debuggercore;

import java.awt.event.KeyEvent;
import junit.textui.TestRunner;
import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.MainWindowOperator;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jellytools.actions.DebugProjectAction;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.modules.debugger.actions.ContinueAction;
import org.netbeans.jellytools.modules.debugger.actions.RunToCursorAction;
import org.netbeans.jellytools.modules.debugger.actions.StepIntoAction;
import org.netbeans.jellytools.modules.debugger.actions.StepOutAction;
import org.netbeans.jellytools.modules.debugger.actions.StepOverAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.nodes.SourcePackagesNode;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.junit.NbTestSuite;

/**
 *
 * @author cincura, ehucka
 */
public class Actions extends JellyTestCase {

    public Actions(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTest(new Actions("testCheckEnabledActions"));
        suite.addTest(new Actions("testCheckEnabledActionsDebugging"));
        suite.addTest(new Actions("testToggleBreakpoints"));
        suite.addTest(new Actions("testStartDebugging"));
        suite.addTest(new Actions("testRemoveBreakpoint"));
        suite.addTest(new Actions("testStepInto"));
        suite.addTest(new Actions("testStepOver"));
        suite.addTest(new Actions("testRunToCursor"));
        suite.addTest(new Actions("testStepOut"));
        suite.addTest(new Actions("testContinue"));
        suite.addTest(new Actions("testStepOverExpression"));
        suite.addTest(new Actions("testPause"));
        return suite;
    }

    /** setUp method  */
    public void setUp() {
        System.out.println("########  " + getName() + "  #######");
    }

    public void tearDown() {
        JemmyProperties.getCurrentOutput().printTrace("\nteardown\n");
        if (getName().equals("testPause")) {
            Utilities.endAllSessions();
            Utilities.deleteAllBreakpoints();
        }
    }

    public void testCheckEnabledActions() throws Throwable {
        try {
            new Action(null, Utilities.setMainProjectAction).perform(new ProjectsTabOperator().getProjectRootNode(Utilities.testProjectName));

            Node projectNode = ProjectsTabOperator.invoke().getProjectRootNode(Utilities.testProjectName);
            Utilities.verifyPopup(projectNode, new String[]{Bundle.getString("org.netbeans.modules.java.j2seproject.ui.Bundle", "LBL_BuildAction_Name"), Bundle.getString("org.netbeans.modules.java.j2seproject.ui.Bundle", "LBL_RunAction_Name"), Bundle.getString("org.netbeans.modules.java.j2seproject.ui.Bundle", "LBL_DebugAction_Name")});

            //main menu actions
            //check main menu debug main project action
            assertTrue(Utilities.runMenu + "|" + Utilities.debugMainProjectItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.debugMainProjectItem, true));
            //Step into
            assertTrue(Utilities.runMenu + "|" + Utilities.stepIntoItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.stepIntoItem, true));
            //new breakpoint
            assertTrue(Utilities.runMenu + "|" + Utilities.newBreakpointItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.newBreakpointItem, true));
            //new watch
            assertTrue(Utilities.runMenu + "|" + Utilities.newWatchItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.newWatchItem, true));
            //main menu actions disabled
            //check finish debugger
            assertFalse(Utilities.runMenu + "|" + Utilities.finishSessionsItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.finishSessionsItem, false));
            //pause
            assertFalse(Utilities.runMenu + "|" + Utilities.pauseItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.pauseItem, false));
            //continue
            assertFalse(Utilities.runMenu + "|" + Utilities.continueItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.continueItem, false));
            //step over
            assertFalse(Utilities.runMenu + "|" + Utilities.stepOverItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.stepOverItem, false));
            //step over expression
            assertFalse(Utilities.runMenu + "|" + Utilities.stepOverExpresItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.stepOverExpresItem, false));
            //step out
            assertFalse(Utilities.runMenu + "|" + Utilities.stepOutItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.stepOutItem, false));
            //run to cursor
            assertFalse(Utilities.runMenu + "|" + Utilities.runToCursorItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.runToCursorItem, false));
            //run into method
            assertFalse(Utilities.runMenu + "|" + Utilities.runIntoMethodItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.runIntoMethodItem, false));
            //apply code changes
            assertFalse(Utilities.runMenu + "|" + Utilities.applyCodeChangesItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.applyCodeChangesItem, false));
            //toggle breakpoint
            assertFalse(Utilities.runMenu + "|" + Utilities.toggleBreakpointItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.toggleBreakpointItem, false));
            //evaluate expression
            assertFalse(Utilities.runMenu + "|" + Utilities.evaluateExpressionItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.evaluateExpressionItem, false));
            MainWindowOperator.getDefault().pushKey(KeyEvent.VK_ESCAPE);

            //open source file
            Node beanNode = new Node(new SourcePackagesNode(Utilities.testProjectName), "examples.advanced|MemoryView.java"); //NOI18N
            new OpenAction().performAPI(beanNode); // NOI18N
            EditorOperator eo = new EditorOperator("MemoryView.java");
            Utilities.setCaret(eo, 80);
            new EventTool().waitNoEvent(1000); //because of issue 70731
            //main menu file actions
            //check run file action
            String actionName = Bundle.getStringTrimmed("org.netbeans.modules.project.ui.actions.Bundle", "LBL_RunSingleAction_Name", new Object[]{new Integer(1), "MemoryView.java"});
            assertTrue(Utilities.runMenu + "|" + Utilities.runFileMenu + "|" + actionName + " is not enabled", Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.runFileMenu + "|" + actionName, true));
            //check debug file action
            actionName = Bundle.getStringTrimmed("org.netbeans.modules.debugger.ui.actions.Bundle", "LBL_DebugSingleAction_Name", new Object[]{new Integer(1), "MemoryView.java"});
            assertTrue(Utilities.runMenu + "|" + Utilities.runFileMenu + "|" + actionName + " is not enabled", Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.runFileMenu + "|" + actionName, true));
            //run to cursor
            assertTrue(Utilities.runMenu + "|" + Utilities.runToCursorItem + " is not enabled", Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.runToCursorItem, true));
            //toggle breakpoint
            assertTrue(Utilities.runMenu + "|" + Utilities.toggleBreakpointItem + " is not enabled", Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.toggleBreakpointItem, true));
            MainWindowOperator.getDefault().pushKey(KeyEvent.VK_ESCAPE);
            MainWindowOperator.getDefault().pushKey(KeyEvent.VK_ESCAPE);

            //source popup menu actions
            JPopupMenuOperator operator = new JPopupMenuOperator(JPopupMenuOperator.callPopup(eo, 50, 50));
            Utilities.verifyPopup(operator, new String[]{Bundle.getStringTrimmed("org.netbeans.modules.java.project.Bundle", "LBL_DebugFile_Action"), Bundle.getStringTrimmed("org.netbeans.modules.debugger.ui.actions.Bundle", "CTL_New_Watch"), Bundle.getStringTrimmed("org.netbeans.modules.debugger.ui.actions.Bundle", "CTL_Toggle_breakpoint")});

            //tools menu
            //debug is not visible
            for (int i = 0; i < MainWindowOperator.getDefault().getToolbarCount(); i++) {
                assertFalse("Debug toolbar is visible", MainWindowOperator.getDefault().getToolbarName(i).equals(Utilities.debugToolbarLabel));
            }
            //run
            ContainerOperator tbrop = MainWindowOperator.getDefault().getToolbar(Bundle.getString("org.netbeans.modules.project.ui.Bundle", "Toolbars/Build"));
            assertTrue("Debug Main Project toolbar action is not enabled", MainWindowOperator.getDefault().getToolbarButton(tbrop, Bundle.getStringTrimmed("org.netbeans.modules.debugger.ui.actions.Bundle", "LBL_DebugMainProjectAction_Name")).isEnabled());

            eo.close();
        } catch (Throwable th) {
            Utilities.captureScreen(this);
            throw th;
        }
    }

    public void testCheckEnabledActionsDebugging() throws Throwable {
        try {
            //open source
            Node projectNode = ProjectsTabOperator.invoke().getProjectRootNode(Utilities.testProjectName);
            Node beanNode = new Node(new SourcePackagesNode(Utilities.testProjectName), "examples.advanced|MemoryView.java"); //NOI18N
            new OpenAction().performAPI(beanNode); // NOI18N
            EditorOperator eo = new EditorOperator("MemoryView.java");
            //place breakpoint
            Utilities.toggleBreakpoint(eo, 104);
            //start debugging
            new DebugProjectAction().perform(projectNode);
            Utilities.getDebugToolbar().waitComponentVisible(true);
            //wait for breakpoint
            Utilities.waitStatusText("Thread main stopped at MemoryView.java:104");
            //check actions
            //main menu actions
            //check main menu debug main project action
            assertTrue(Utilities.runMenu + "|" + Utilities.debugMainProjectItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.debugMainProjectItem, true));
            //Step into
            assertTrue(Utilities.runMenu + "|" + Utilities.stepIntoItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.stepIntoItem, true));
            //new breakpoint
            assertTrue(Utilities.runMenu + "|" + Utilities.newBreakpointItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.newBreakpointItem, true));
            //new watch
            assertTrue(Utilities.runMenu + "|" + Utilities.newWatchItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.newWatchItem, true));
            //check finish debugger
            assertTrue(Utilities.runMenu + "|" + Utilities.finishSessionsItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.finishSessionsItem, true));
            //pause
            assertFalse(Utilities.runMenu + "|" + Utilities.pauseItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.pauseItem, false));
            //continue
            assertTrue(Utilities.runMenu + "|" + Utilities.continueItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.continueItem, true));
            //step over
            assertTrue(Utilities.runMenu + "|" + Utilities.stepOverItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.stepOverItem, true));
            //step over expression
            assertTrue(Utilities.runMenu + "|" + Utilities.stepOverExpresItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.stepOverExpresItem, true));
            //step out
            assertTrue(Utilities.runMenu + "|" + Utilities.stepOutItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.stepOutItem, true));
            //run to cursor
            assertTrue(Utilities.runMenu + "|" + Utilities.runToCursorItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.runToCursorItem, true));
            //run into method
            assertTrue(Utilities.runMenu + "|" + Utilities.runIntoMethodItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.runIntoMethodItem, true));
            //apply code changes
            assertTrue(Utilities.runMenu + "|" + Utilities.applyCodeChangesItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.applyCodeChangesItem, true));
            //toggle breakpoint
            assertTrue(Utilities.runMenu + "|" + Utilities.toggleBreakpointItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.toggleBreakpointItem, true));
            //evaluate expression
            assertTrue(Utilities.runMenu + "|" + Utilities.evaluateExpressionItem, Utilities.verifyMainMenu(Utilities.runMenu + "|" + Utilities.evaluateExpressionItem, true));
            MainWindowOperator.getDefault().pushKey(KeyEvent.VK_ESCAPE);

            //debug toolbar
            ContainerOperator debugToolbarOper = Utilities.getDebugToolbar();
            assertTrue("Toolbar action Finish is not enabled", MainWindowOperator.getDefault().getToolbarButton(debugToolbarOper, Bundle.getStringTrimmed("org.netbeans.modules.debugger.ui.actions.Bundle", "CTL_KillAction_name")).isEnabled());
            assertFalse("Toolbar action Pause is not disabled", MainWindowOperator.getDefault().getToolbarButton(debugToolbarOper, Bundle.getStringTrimmed("org.netbeans.modules.debugger.ui.actions.Bundle", "CTL_Pause_action_name")).isEnabled());
            assertTrue("Toolbar action Continue is not enabled", MainWindowOperator.getDefault().getToolbarButton(debugToolbarOper, Bundle.getStringTrimmed("org.netbeans.modules.debugger.ui.actions.Bundle", "CTL_Continue_action_name")).isEnabled());
            //step
            assertTrue("Toolbar action Step ovet is not enabled", MainWindowOperator.getDefault().getToolbarButton(debugToolbarOper, Bundle.getStringTrimmed("org.netbeans.modules.debugger.ui.actions.Bundle", "CTL_Step_over_action_name")).isEnabled());
            assertTrue("Toolbar action Step over expression is not enabled", MainWindowOperator.getDefault().getToolbarButton(debugToolbarOper, Bundle.getStringTrimmed("org.netbeans.modules.debugger.ui.actions.Bundle", "CTL_Step_operation_action_name")).isEnabled());
            assertTrue("Toolbar action Step into is not enabled", MainWindowOperator.getDefault().getToolbarButton(debugToolbarOper, Bundle.getStringTrimmed("org.netbeans.modules.debugger.ui.actions.Bundle", "CTL_Step_into_action_name")).isEnabled());
            assertTrue("Toolbar action Step out is not enabled", MainWindowOperator.getDefault().getToolbarButton(debugToolbarOper, Bundle.getStringTrimmed("org.netbeans.modules.debugger.ui.actions.Bundle", "CTL_Step_out_action_name")).isEnabled());
            //run to cursor
            assertTrue("Toolbar action Run to cursor is not enabled", MainWindowOperator.getDefault().getToolbarButton(debugToolbarOper, Bundle.getStringTrimmed("org.netbeans.modules.debugger.ui.actions.Bundle", "CTL_Run_to_cursor_action_name")).isEnabled());
            assertTrue("Toolbar action Apply code changes is not enabled", MainWindowOperator.getDefault().getToolbarButton(debugToolbarOper, Bundle.getStringTrimmed("org.netbeans.modules.debugger.ui.actions.Bundle", "CTL_Fix_action_name")).isEnabled());

            //remove breakpoint
            Utilities.deleteAllBreakpoints();
            //finish debugging
            Utilities.endAllSessions();
            //close sources
            eo.close();
        } catch (Throwable th) {
            Utilities.captureScreen(this);
            throw th;
        }
    }

    public void testToggleBreakpoints() throws Throwable {
        try {
            //open source
            Node projectNode = ProjectsTabOperator.invoke().getProjectRootNode(Utilities.testProjectName);
            Node beanNode = new Node(new SourcePackagesNode(Utilities.testProjectName), "examples.advanced|MemoryView.java"); //NOI18N
            new OpenAction().performAPI(beanNode); // NOI18N
            EditorOperator eo = new EditorOperator("MemoryView.java");
            //place breakpoint
            Utilities.toggleBreakpoint(eo, 80);
            assertTrue("Breakpoint annotation is not displayed", Utilities.checkAnnotation(eo, 80, "Breakpoint"));
        } catch (Throwable th) {
            Utilities.captureScreen(this);
            throw th;
        }
    }

    static int lastLineNumber = 0;

    public void testStartDebugging() throws Throwable {
        try {
            //start debugging
            Node projectNode = ProjectsTabOperator.invoke().getProjectRootNode(Utilities.testProjectName);
            new DebugProjectAction().perform(projectNode);
            Utilities.getDebugToolbar().waitComponentVisible(true);
            //wait for breakpoint
            Utilities.waitStatusText("Thread main stopped at MemoryView.java:80");
            EditorOperator eo = new EditorOperator("MemoryView.java");
            assertTrue("Breakpoint annotation is not on line 80", Utilities.checkAnnotation(eo, 80, "Breakpoint"));
            assertTrue("Current PC annotation is not on line 80", Utilities.checkAnnotation(eo, 80, "CurrentPC"));
        } catch (Throwable th) {
            Utilities.captureScreen(this);
            throw th;
        }
    }

    public void testRemoveBreakpoint() throws Throwable {
        try {
            EditorOperator eo = new EditorOperator("MemoryView.java");
            //remove breakpoint
            Utilities.toggleBreakpoint(eo, 80, false);
            assertFalse("Breakpoint annotation is not removed from line 80", Utilities.checkAnnotation(eo, 80, "Breakpoint"));
        } catch (Throwable th) {
            Utilities.captureScreen(this);
            throw th;
        }
    }

    public void testStepInto() throws Throwable {
        try {
            new StepIntoAction().performShortcut();
            lastLineNumber = Utilities.waitDebuggerConsole("Thread main stopped at MemoryView.java:92", lastLineNumber + 1);
            //check 80, 92
            EditorOperator eo = new EditorOperator("MemoryView.java");
            assertTrue("CurrentPC annotation is not on line 92", Utilities.checkAnnotation(eo, 92, "CurrentPC"));
            assertTrue("Call Site annotation is not on line 80", Utilities.checkAnnotation(eo, 80, "CallSite"));
        } catch (Throwable th) {
            Utilities.captureScreen(this);
            throw th;
        }
    }

    public void testStepOver() throws Throwable {
        try {
            new StepOverAction().performShortcut();
            lastLineNumber = Utilities.waitDebuggerConsole("Thread main stopped at MemoryView.java:93", lastLineNumber + 1);
            //check 80, 82
            EditorOperator eo = new EditorOperator("MemoryView.java");
            assertFalse("CurrentPC annotation remains on line 92", Utilities.checkAnnotation(eo, 92, "CurrentPC"));
            assertTrue("CurrentPC annotation is not on line 93", Utilities.checkAnnotation(eo, 93, "CurrentPC"));
        } catch (Throwable th) {
            Utilities.captureScreen(this);
            throw th;
        }
    }

    public void testRunToCursor() throws Throwable {
        try {
            EditorOperator eo = new EditorOperator("MemoryView.java");
            Utilities.setCaret(eo, 109);
            //run to cursor
            new RunToCursorAction().perform();
            lastLineNumber = Utilities.waitDebuggerConsole("Thread main stopped at MemoryView.java:109", lastLineNumber + 1);
            //check line
            assertFalse("Current PC annotation remains on line 93", Utilities.checkAnnotation(eo, 93, "CurrentPC"));
            assertTrue("Current PC annotation is not on line 109", Utilities.checkAnnotation(eo, 109, "CurrentPC"));
        } catch (Throwable th) {
            Utilities.captureScreen(this);
            throw th;
        }
    }

    public void testStepOut() throws Throwable {
        try {
            new StepOutAction().performShortcut();
            lastLineNumber = Utilities.waitDebuggerConsole("Thread main stopped at MemoryView.java:80", lastLineNumber + 1);
            //check 82, 92
            EditorOperator eo = new EditorOperator("MemoryView.java");
            assertFalse("Current PC annotation remains on line 109", Utilities.checkAnnotation(eo, 109, "CurrentPC"));
            assertTrue("Current PC annotation is not on line 80", Utilities.checkAnnotation(eo, 80, "CurrentExpressionLine"));
        } catch (Throwable th) {
            Utilities.captureScreen(this);
            throw th;
        }
    }

    public void testContinue() throws Throwable {
        try {
            EditorOperator eo = new EditorOperator("MemoryView.java");
            //remove breakpoint
            Utilities.toggleBreakpoint(eo, 104, true);
            new ContinueAction().perform();
            lastLineNumber = Utilities.waitDebuggerConsole("Thread main stopped at MemoryView.java:104", lastLineNumber + 1);
            assertFalse("Current PC annotation remains on line 80", Utilities.checkAnnotation(eo, 80, "CurrentPC"));
            assertTrue("Current PC annotation is not on line 104", Utilities.checkAnnotation(eo, 104, "CurrentPC"));
            Utilities.toggleBreakpoint(eo, 104, false);
        } catch (Throwable th) {
            Utilities.captureScreen(this);
            throw th;
        }
    }

    public void testStepOverExpression() throws Throwable {
        try {
            EditorOperator eo = new EditorOperator("MemoryView.java");

            new Action(Utilities.runMenu + "|" + Utilities.stepOverExpresItem, null).perform();
            lastLineNumber = Utilities.waitDebuggerConsole("Thread main stopped at MemoryView.java:104", lastLineNumber + 1);
            assertTrue("CurrentExpressionLine annotation is not on line 105", Utilities.checkAnnotation(eo, 105, "CurrentExpressionLine"));
            new Action(Utilities.runMenu + "|" + Utilities.stepOverExpresItem, null).perform();
            lastLineNumber = Utilities.waitDebuggerConsole("Thread main stopped at MemoryView.java:104", lastLineNumber + 1);
            assertTrue("CurrentExpressionLine annotation is not on line 106", Utilities.checkAnnotation(eo, 106, "CurrentExpressionLine"));
            new Action(Utilities.runMenu + "|" + Utilities.stepOverExpresItem, null).perform();
            lastLineNumber = Utilities.waitDebuggerConsole("Thread main stopped at MemoryView.java:104", lastLineNumber + 1);
            assertTrue("CurrentExpressionLine annotation is not on line 107", Utilities.checkAnnotation(eo, 107, "CurrentExpressionLine"));
            new Action(Utilities.runMenu + "|" + Utilities.stepOverExpresItem, null).perform();
            lastLineNumber = Utilities.waitDebuggerConsole("Thread main stopped at MemoryView.java:104", lastLineNumber + 1);
            assertTrue("CurrentExpressionLine annotation is not on line 104", Utilities.checkAnnotation(eo, 104, "CurrentExpressionLine"));
            new Action(Utilities.runMenu + "|" + Utilities.stepOverExpresItem, null).perform();
            lastLineNumber = Utilities.waitDebuggerConsole("Thread main stopped at MemoryView.java:104", lastLineNumber + 1);
            assertTrue("CurrentExpressionLine annotation is not on line 104", Utilities.checkAnnotation(eo, 104, "CurrentExpressionLine"));
            new Action(Utilities.runMenu + "|" + Utilities.stepOverExpresItem, null).perform();
            lastLineNumber = Utilities.waitDebuggerConsole("Thread main stopped at MemoryView.java:109", lastLineNumber + 1);
            assertTrue("Current PC annotation is not on line 109", Utilities.checkAnnotation(eo, 109, "CurrentPC"));
        } catch (Throwable th) {
            Utilities.captureScreen(this);
            throw th;
        }
    }

    public void testPause() throws Throwable {
        try {
            EditorOperator eo = new EditorOperator("MemoryView.java");
            //place breakpoint
            Utilities.toggleBreakpoint(eo, 80);
            //continue
            new ContinueAction().perform();
            lastLineNumber = Utilities.waitDebuggerConsole("Thread main stopped at MemoryView.java:80", lastLineNumber + 1);
            //remove breakpoint
            Utilities.toggleBreakpoint(eo, 80, false);
            //continue
            new ContinueAction().perform();
            String pausePath = Utilities.runMenu + "|" + Utilities.pauseItem;
            for (int i = 0; i < 10; i++) {
                if (MainWindowOperator.getDefault().menuBar().showMenuItem(pausePath).isEnabled()) {
                    new Action(pausePath, null).perform();
                }
                MainWindowOperator.getDefault().menuBar().closeSubmenus();
                new EventTool().waitNoEvent(500);
            }
            Utilities.waitDebuggerConsole("Thread main stopped at ", lastLineNumber + 1);
            eo = new EditorOperator("MemoryView.java");
            boolean found = false;
            for (int i = 79; i < 87; i++) {
                if (Utilities.checkAnnotation(eo, i, "CallSite")) {
                    found = true;
                    break;
                }
            }
            assertTrue("Call Site annotation is not in for cycle", found);
            //there should not be any other opened classes - issue 83704
            eo.closeAllDocuments();
        } catch (Throwable th) {
            Utilities.captureScreen(this);
            throw th;
        }
    }
}
