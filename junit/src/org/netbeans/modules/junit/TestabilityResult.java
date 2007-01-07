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
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.junit;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 * Helper class representing reasons for skipping a class in the test 
 * generation process. The class enumerates known reasons, why a class may 
 * not be considered testable, allows to combine the reasons and provide 
 * human-readable representation  of them.
 */
final class TestabilityResult {
    // bitfield of reasons for skipping a class
    private long reason;

    // reason constants
    public static final TestabilityResult OK = new TestabilityResult(0);
    public static final TestabilityResult PACKAGE_PRIVATE_CLASS = new TestabilityResult(1);
    public static final TestabilityResult NO_TESTEABLE_METHODS = new TestabilityResult(2);
    public static final TestabilityResult TEST_CLASS = new TestabilityResult(4);
    public static final TestabilityResult ABSTRACT_CLASS = new TestabilityResult(8);
    public static final TestabilityResult NONSTATIC_INNER_CLASS = new TestabilityResult(16);
    public static final TestabilityResult EXCEPTION_CLASS = new TestabilityResult(32);
    public static final TestabilityResult PRIVATE_CLASS = new TestabilityResult(64);


    // bundle keys for reason descriptions
    private static final String [] reasonBundleKeys = {
        "TestabilityResult_PkgPrivate", 
        "TestabilityResult_NoTestableMethods",
        "TestabilityResult_TestClass",
        "TestabilityResult_AbstractClass",
        "TestabilityResult_NonstaticInnerClass",
        "TestabilityResult_ExceptionClass",
        "TestabilityResult_Private"};

    private TestabilityResult(long reason) {
        this.reason = reason;
    }

    /**
     * Combine two result reasons into a new one.
     *
     * The combination is the union
     * of the failure reasons represented by the two results. Thus,
     * if both are success (no failure), the combination is a success. If 
     * some of them is failed, the result is failed.
     *
     * @param lhs the first TestabilityResult
     * @param rhs the second TestabilityResult
     * @return a new TestabilityResult representing the combination of the two 
     *         results
     **/
    public static TestabilityResult combine(TestabilityResult lhs, TestabilityResult rhs) {
        return new TestabilityResult(lhs.reason | rhs.reason);
    }

    /**
     * Returns true if the result is for a testable class.
     * @return true or false
     */
    public boolean isTestable() {
        return reason == 0;
    }

    /**
     * Returns true if the result is for a non-testable class.
     * @return true if the result is for a non-testable class.
     */
    public boolean isFailed() {
        return reason != 0;
    }

    /**
     * Returns a human-readable representation of the reason. If the reason 
     * is a combination of multiple reasons, they are separated with ",".
     * @return String
     */
    public String getReason() {
        return getReason(", ", ", ");                                   //NOI18N
    }

    /**
     * Returns {@link #getReason()}.
     * @return String
     */
    public String toString() { 
        return getReason(", ", ", ");                                   //NOI18N
    }

    /** 
     * Returns a human-readable representation of the reason. If the reason 
     * is a combination of multiple reasons, they are separated with 
     * {@code separ} except for the last reason, which is separated 
     * with {@code terminalSepar}
     * <p>
     * For example: getReason(", ", " or ") might return 
     * "abstract, package private or without testable methods".
     *
     * @return String
     */
    public String getReason(String separ, String terminalSepar) {
        try {
            ResourceBundle bundle = NbBundle.getBundle(TestCreator.class);
            if (reason == 0) {
                return bundle.getString("TestabilityResult_OK");          //NOI18N
            } else {
                String str = "";                                        //NOI18N
                boolean lastPrep = true;
                for (long i = 0, r = reason; r > 0; r >>= 1, i++) {
                    if ((r & 1) != 0) {
                        if (str.length() > 0) {
                            if (lastPrep) {
                                str = terminalSepar + str;
                                lastPrep = false;
                            } else {
                                str = separ + str;
                            }
                        }
                        str = bundle.getString(reasonBundleKeys[(int)i]) + str;
                    }
                } 
                return str;
            }
        } catch (MissingResourceException ex) {
            ErrorManager.getDefault().notify(ex);
            return "";
        }
    }
    
    /**
     * Class for holding name of a skipped java class
     * together with the reason why it was skipped.
     */
    static final class SkippedClass {
        final String clsName;
        final TestabilityResult reason;
        SkippedClass(String clsName,
                     TestabilityResult reason) {
            this.clsName = clsName;
            this.reason = reason;
        }
    }

}
