/*
 * $Id$
 *
 * ---------------------------------------------------------------------------
 *
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
 * Contributor(s): Manfred Riem (mriem@netbeans.org).
 *
 * The Original Software is the Jemmy library. The Initial Developer of the
 * Original Software is Alexandre Iline. All Rights Reserved.
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
package org.netbeans.jemmy.operators;



import javax.swing.JFrame;

import javax.swing.JToggleButton;

import junit.framework.Test;

import junit.framework.TestCase;

import junit.framework.TestSuite;

import org.netbeans.jemmy.util.NameComponentChooser;



/**

 * A JUnit test for JToggleButtonOperator.

 *

 * @author Manfred Riem (mriem@netbeans.org)

 * @version $Revision$

 */

public class JToggleButtonOperatorTest extends TestCase {

    /**

     * Stores the frame we use for testing.

     */

    private JFrame frame;

    

    /**

     * Stores the toggle button we use for testing.

     */

    private JToggleButton toggleButton;

    

    /**

     * Constructor.

     *

     * @param testName the name of the test.

     */

    public JToggleButtonOperatorTest(String testName) {

        super(testName);

    }

    

    /**

     * Setup for testing.

     */

    protected void setUp() throws Exception {

        frame = new JFrame();

        toggleButton = new JToggleButton("JToggleButtonOperatorTest");

        toggleButton.setName("JToggleButtonOperatorTest");

        frame.getContentPane().add(toggleButton);

        frame.pack();

        frame.setLocationRelativeTo(null);

    }

    

    /**

     * Cleanup after testing.

     */

    protected void tearDown() throws Exception {

        frame.setVisible(false);

        frame.dispose();

        frame = null;

    }

    

    /**

     * Suite method.

     */

    public static Test suite() {

        TestSuite suite = new TestSuite(JToggleButtonOperatorTest.class);

        

        return suite;

    }



    /**

     * Test constructor.

     */

    public void testConstructor() {

        frame.setVisible(true);

        

        JFrameOperator operator1 = new JFrameOperator();

        assertNotNull(operator1);

        

        JToggleButtonOperator operator2 = new JToggleButtonOperator(operator1);

        assertNotNull(operator2);

        

        JToggleButtonOperator operator3 = new JToggleButtonOperator(operator1, new NameComponentChooser("JToggleButtonOperatorTest"));

        assertNotNull(operator3);



        JToggleButtonOperator operator4 = new JToggleButtonOperator(operator1, "JToggleButtonOperatorTest");

        assertNotNull(operator4);

    }

    

    /**

     * Test findJToggleButton method.

     */

    public void testFindJToggleButton() {

        frame.setVisible(true);

        

        JToggleButton toggleButton1 = JToggleButtonOperator.findJToggleButton(frame, "JToggleButtonOperatorTest", false, false);

        assertNotNull(toggleButton1);



        JToggleButton toggleButton2 = JToggleButtonOperator.findJToggleButton(frame, new NameComponentChooser("JToggleButtonOperatorTest"));

        assertNotNull(toggleButton2);

    }

    

    /**

     * Test waitJToggleButton method.

     */

    public void testWaitJToggleButton() {

        frame.setVisible(true);

        

        JToggleButton toggleButton1 = JToggleButtonOperator.waitJToggleButton(frame, "JToggleButtonOperatorTest", false, false);

        assertNotNull(toggleButton1);



        JToggleButton toggleButton2 = JToggleButtonOperator.waitJToggleButton(frame, new NameComponentChooser("JToggleButtonOperatorTest"));

        assertNotNull(toggleButton2);

    }

    

    /**

     * Test prepareToClick method.

     */

    public void testPrepareToClick() {

        frame.setVisible(true);

        

        JFrameOperator operator1 = new JFrameOperator();

        assertNotNull(operator1);

        

        JToggleButtonOperator operator2 = new JToggleButtonOperator(operator1);

        assertNotNull(operator2);

        

        operator2.prepareToClick();

        assertTrue(toggleButton.isVisible());

    }

}

