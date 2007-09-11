/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package gui.actions;

import java.io.File;

import org.netbeans.jellytools.actions.CloseAllDocumentsAction;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.OutputOperator;
import org.netbeans.jellytools.OutputTabOperator;

import org.netbeans.junit.ide.ProjectSupport;

import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.operators.JRadioButtonOperator;

/**
 * Test Reverse Engineering 
 *
 * @author  rashid@netbeans.org
 */
public class ReverseEngineering extends org.netbeans.performance.test.utilities.PerformanceTestCase {

            
    private static String testProjectName = "jEdit";  
    private static long suffix;
    /**
     * Creates a new instance of ReverseEngineering
     * @param testName the name of the test
     */
    public ReverseEngineering(String testName) {
        super(testName);
        expectedTime = 300000;
        WAIT_AFTER_OPEN=4000;
    }
    
    /**
     * Creates a new instance of ReverseEngineering
     * @param testName the name of the test
     * @param performanceDataName measured values will be saved under this name
     */
    public ReverseEngineering(String testName, String performanceDataName) {
        super(testName, performanceDataName);
        expectedTime = 300000;
        WAIT_AFTER_OPEN=4000;
    }
    
    public void initialize(){
        ProjectSupport.openProject(System.getProperty("xtest.tmpdir")+File.separator+testProjectName);
//        new CloseAllDocumentsAction().performAPI();
    }
    
    public void prepare(){
  
        Node pNode = new ProjectsTabOperator().getProjectRootNode(testProjectName);
        pNode.select();
        pNode.performPopupAction("Reverse Engineer...");
               
        new EventTool().waitNoEvent(1000);
        NbDialogOperator reng = new NbDialogOperator("Reverse Engineer");
        JRadioButtonOperator newUMLradio = new JRadioButtonOperator(reng,"Create New UML Project");
        newUMLradio.push();
        new EventTool().waitNoEvent(1000);
        suffix = System.currentTimeMillis(); 
        JTextFieldOperator textProject = new JTextFieldOperator(reng,2);
        textProject.clearText();
        textProject.typeText("jEdit-Model_"+ suffix); 
        reng.ok();
    
    }
    
    public ComponentOperator open(){
            OutputOperator oot = new OutputOperator();
            oot.getTimeouts().setTimeout("ComponentOperator.WaitStateTimeout",300000);

          OutputTabOperator asot = oot.getOutputTab("Reverse Engineering");
          asot.getTimeouts().setTimeout("ComponentOperator.WaitStateTimeout",300000);
          asot.waitText("Task Successful");  

       return null;
    }
    
    public void close(){
//        new CloseAllDocumentsAction().performAPI(); //avoid issue 68671 - editors are not closed after closing project by ProjectSupport
        ProjectSupport.closeProject("jEdit-Model_"+ suffix);
 }

    protected void shutdown() {

//    new CloseAllDocumentsAction().performAPI(); //avoid issue 68671 - editors are not closed after closing project by ProjectSupport      
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(new ReverseEngineering("measureTime"));
    }
}
