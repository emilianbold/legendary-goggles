/*
 * TestGenerateJavadoc.java
 *
 * Created on February 3, 2003, 3:56 PM
 */

package org.netbeans.test.gui.javadoc;

import java.io.PrintStream;
import java.io.File;

import junit.framework.TestSuite;

import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.OptionsOperator;
import org.netbeans.jellytools.RepositoryTabOperator;

import org.netbeans.jellytools.actions.Action;

import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.properties.ComboBoxProperty;
import org.netbeans.jellytools.properties.PropertySheetTabOperator;

import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.operators.JTextFieldOperator;

import org.netbeans.junit.NbTestSuite;

/** JUnit test suite with Jemmy support
 *
 * @author mk97936
 * @version 1.0
 */
public class TestGenerateJavadoc extends JavadocTestCase {
    
    public static final String sep = File.separator;
    
    /** constructor required by JUnit
     * @param testName method name to be used as testcase
     */
    public TestGenerateJavadoc(String testName) {
        super(testName);
    }
    
    /** method used for explicit testsuite definition
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite(TestGenerateJavadoc.class);
        return suite;
    }
    
    /** method called before each testcase
     */
    protected void setUp() {
    }
    
    /** method called after each testcase
     */
    protected void tearDown() {
    }
    
    // -------------------------------------------------------------------------
    
    public void testGenerate() {
        
        String userHome = System.getProperty("netbeans.user"); // NOI18N
        RepositoryTabOperator repoTabOper = RepositoryTabOperator.invoke();
        Node topNode = new Node(repoTabOper.getRootNode(), 0);
        
        String sampledirPath = topNode.getPath() + sep + "org" + sep + "netbeans" + sep + "test" + sep + // NOI18N
                               "gui" + sep + "javadoc" + sep + "data" + sep + "sampledir"; // NOI18N
        
        repoTabOper.mountLocalDirectoryAPI(sampledirPath); // NOI18N
        
        JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 120000); // NOI18N
        Action generateJDoc = new Action(toolsMainMenuItem + "|" + generateMenuItem, // NOI18N
                                         toolsPopupMenuItem + "|" + generateMenuItem); // NOI18N
        
        // javadoc is generated to default location ${netbeans.user}/javadoc
        
        // generate for MemoryView.java
        Node memoryViewNode = new Node(new Node(repoTabOper.getRootNode(), sampledirPath), 
                                       "examples|advanced|MemoryView"); // NOI18N
        generateJDoc.perform(memoryViewNode);
        
        // question about showing javadoc in browser
        NbDialogOperator questionDialogOper = new NbDialogOperator(questionWinTitle);
        questionDialogOper.no();
        
        verifyCommonJdocFiles(userHome, "javadoc");
        assertTrue("MemoryView doesn't exist!", new File(userHome + sep + "javadoc" + sep + "examples" + sep + // NOI18N
                   "advanced" + sep + "MemoryView.html").exists()); // NOI18N
        new EventTool().waitNoEvent(1000);
        
        // generate for colorpicker package
        Node colorPickerNode = new Node(new Node(repoTabOper.getRootNode(), sampledirPath), "examples|colorpicker"); // NOI18N        
        generateJDoc.perform(colorPickerNode);
        
        // question about showing javadoc in browser
        NbDialogOperator questionDialogOper_2 = new NbDialogOperator(questionWinTitle);
        questionDialogOper_2.no();
        
        verifyCommonJdocFiles(userHome, "javadoc");
        assertTrue("ColorPicker doesn't exist!", new File(userHome + sep + "javadoc" + sep + "examples" + sep + // NOI18N
                   "colorpicker" + sep + "ColorPicker.html").exists()); // NOI18N
        new EventTool().waitNoEvent(1000);
        
        // generate for examples package
        Node examplesNode = new Node(new Node(repoTabOper.getRootNode(), sampledirPath), "examples"); // NOI18N        
        generateJDoc.perform(examplesNode);
        
        // question about showing javadoc in browser
        NbDialogOperator questionDialogOper_3 = new NbDialogOperator(questionWinTitle);
        questionDialogOper_3.no();
        
        verifyCommonJdocFiles(userHome, "javadoc");
        assertTrue("ClockFrame doesn't exist!", new File(userHome + sep + "javadoc" + sep + "examples" + sep + // NOI18N
                   "clock" + sep + "ClockFrame.html").exists()); // NOI18N
        assertTrue("ImageFrame doesn't exist!", new File(userHome + sep + "javadoc" + sep + "examples" + sep + // NOI18N
                   "imageviewer" + sep + "ImageFrame.html").exists()); // NOI18N
        assertTrue("ImageViewer doesn't exist!", new File(userHome + sep + "javadoc" + sep + "examples" + sep + // NOI18N
                   "imageviewer" + sep + "ImageViewer.html").exists()); // NOI18N
        assertTrue("MemoryView doesn't exist!", new File(userHome + sep + "javadoc" + sep + "examples" + sep + // NOI18N
                   "advanced" + sep + "MemoryView.html").exists()); // NOI18N
        assertTrue("Ted doesn't exist!", new File(userHome + sep + "javadoc" + sep + "examples" + sep + // NOI18N
                   "texteditor" + sep + "Ted.html").exists()); // NOI18N
        assertTrue("About doesn't exist!", new File(userHome + sep + "javadoc" + sep + "examples" + sep + // NOI18N
                   "texteditor" + sep + "About.html").exists()); // NOI18N
        assertTrue("Finder doesn't exist!", new File(userHome + sep + "javadoc" + sep + "examples" + sep + // NOI18N
                   "texteditor" + sep + "Finder.html").exists()); // NOI18N
        assertTrue("ColorPicker doesn't exist!", new File(userHome + sep + "javadoc" + sep + "examples" + sep + // NOI18N
                   "colorpicker" + sep + "ColorPicker.html").exists()); // NOI18N
        new EventTool().waitNoEvent(1000);
        
    }
    
    private void verifyCommonJdocFiles(String base, String folder) {
        assertTrue("index.html doesn't exist!", new File(base + sep + folder + sep + "index.html").exists()); // NOI18N
        assertTrue("index-all.html doesn't exist!", new File(base + sep + folder + sep + "index-all.html").exists()); // NOI18N
        assertTrue("allclasses-frame.html doesn't exist!", new File(base + sep + folder + sep + "allclasses-frame.html").exists()); // NOI18N
        assertTrue("allclasses-noframe.html doesn't exist!", new File(base + sep + folder + sep + "allclasses-noframe.html").exists()); // NOI18N
        assertTrue("packages.html doesn't exist!", new File(base + sep + folder + sep + "packages.html").exists()); // NOI18N
        assertTrue("stylesheet.css doesn't exist!", new File(base + sep + folder + sep + "stylesheet.css").exists()); // NOI18N
        assertTrue("package-list doesn't exist!", new File(base + sep + folder + sep + "package-list").exists()); // NOI18N
        assertTrue("help-doc.html doesn't exist!", new File(base + sep + folder + sep + "help-doc.html").exists()); // NOI18N
        assertTrue("overview-tree.html doesn't exist!", new File(base + sep + folder + sep + "overview-tree.html").exists()); // NOI18N
    }
    
    private void verifyCommonJdocFilesInFolder(String fodler) {
        
    }
    
    public void testGenerateToFolder() {
        
        String userHome = System.getProperty("netbeans.user"); // NOI18N
        RepositoryTabOperator repoTabOper = RepositoryTabOperator.invoke();
        Node topNode = new Node(repoTabOper.getRootNode(), 0);
        
        String sampledirPath = topNode.getPath() + sep + "org" + sep + "netbeans" + sep + "test" + sep + // NOI18N
                               "gui" + sep + "javadoc" + sep + "data" + sep + "sampledir"; // NOI18N
        
        repoTabOper.mountLocalDirectoryAPI(sampledirPath); // NOI18N
        Action generateJDoc = new Action(toolsMainMenuItem + "|" + generateMenuItem, // NOI18N
                                         toolsPopupMenuItem + "|" + generateMenuItem); // NOI18N
        
        // set property "Ask for Destination Directory" to true
        OptionsOperator optionsOper = OptionsOperator.invoke();
        optionsOper.selectOption("Code Documentation|Documentation");
        PropertySheetTabOperator propertiesTab = new PropertySheetTabOperator(optionsOper);
        ComboBoxProperty askForDestProp = new ComboBoxProperty(propertiesTab, "Ask for Destination Directory");
        askForDestProp.setValue("True");
        optionsOper.close();
        // -----
        
        Node imageviewerNode = new Node(new Node(repoTabOper.getRootNode(), sampledirPath), "examples|imageviewer"); // NOI18N        
        generateJDoc.perform(imageviewerNode);
        
        NbDialogOperator dialogOper = new NbDialogOperator("Javadoc Destination Directory");
        JTextFieldOperator textOper = new JTextFieldOperator(dialogOper);
        textOper.clearText();
        textOper.typeText(userHome + sep + "javadoc_1");
        dialogOper.ok();
        
        // question about creating non existing folder
        NbDialogOperator questionDialogOper = new NbDialogOperator(questionWinTitle);
        questionDialogOper.ok();
        
        // question about showing javadoc in browser
        NbDialogOperator questionDialogOper_2 = new NbDialogOperator(questionWinTitle);
        questionDialogOper_2.no();
        
        verifyCommonJdocFiles(userHome, "javadoc_1");
        assertTrue("ImageViewer doesn't exist!", new File(userHome + sep + "javadoc_1" + sep + "examples" + sep + // NOI18N
                   "imageviewer" + sep + "ImageViewer.html").exists()); // NOI18N
        new EventTool().waitNoEvent(1000);
        
    }
    
    /** Use for internal test execution inside IDE
     * @param args command line arguments
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    private static final String toolsPopupMenuItem = Bundle.getStringTrimmed("org.openide.actions.Bundle", "CTL_Tools");
    private static final String toolsMainMenuItem = Bundle.getStringTrimmed("org.netbeans.core.Bundle", "Menu/Tools");
    private static final String generateMenuItem = Bundle.getStringTrimmed("org.netbeans.modules.javadoc.Bundle", "CTL_ActionGenerate");
    private static final String questionWinTitle = Bundle.getStringTrimmed("org.openide.Bundle", "NTF_QuestionTitle");
    
}
