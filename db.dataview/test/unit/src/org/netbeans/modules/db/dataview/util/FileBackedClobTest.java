/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.db.dataview.util;

import java.io.CharArrayReader;
import java.io.IOException;
import org.openide.util.Exceptions;
import java.io.Reader;
import java.io.Writer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mblaesing
 */
public class FileBackedClobTest {
    private static char[] testCase1;
    private static char[] testCase2;
    private static char[] testCase3;

    private static void assertCharacterStreamEquals(Reader is1, Reader is2) {
        try {
            long position = 0;
            while (true) {
                int input1 = is1.read();
                int input2 = is2.read();
                if (input1 != input2) {
                    throw new AssertionError("Reader differ at position: " + Long.toString(position));
                }
                if (input1 == -1 || input2 == -1) {
                    return;
                }
                position++;
            }
        } catch (IOException ex) {
            throw new AssertionError(ex);
        } finally {
            try {
                is1.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            try {
                is2.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        // \u00E4\u00F6\u00FC\00DF\1E9E => Also include charactes outside ASCII plane
        // first three - the german umlauts: ae, oe, ue
        // last one is the small "eszett"
        char[] testPattern = "Test\u00E4\u00F6\u00FC\u00DF".toCharArray();
        int testLength = testPattern.length;
        testCase1 = new char[10];
        testCase2 = new char[1024];
        testCase3 = new char[1024 * 1024];
        for (int i = 0; i < testCase1.length; i++) {
            testCase1[i] = testPattern[ i % testLength];
        }
        for (int i = 0; i < testCase2.length; i++) {
            testCase2[i] = testPattern[ i % testLength];
        }
        for (int i = 0; i < testCase3.length; i++) {
            testCase3[i] = testPattern[ i % testLength];
        }
    }

    
    public FileBackedClobTest() {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        testCase1 = null;
        testCase2 = null;
        testCase3 = null;
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testLength() throws Exception {
        FileBackedClob c;
        c = new FileBackedClob(new CharArrayReader(testCase1));
        assertEquals(c.length(), testCase1.length);
        assertEquals(c.getBackingFile().length(), testCase1.length * 4);
        c.free();
        c = new FileBackedClob(new String(testCase2));
        assertEquals(c.length(), testCase2.length);
        assertEquals(c.getBackingFile().length(), testCase2.length * 4);
        c.free();
        c = new FileBackedClob(new CharArrayReader(testCase3));
        assertEquals(c.length(), testCase3.length);
        assertEquals(c.getBackingFile().length(), testCase3.length * 4);
        c.free();
    }

    @Test
    public void testTruncate() throws Exception {
        FileBackedClob c;
        c = new FileBackedClob(new CharArrayReader(testCase1));
        c.truncate(5);
        assertEquals(c.length(), 5);
        assertEquals(c.getBackingFile().length(), 5 * 4);
        c.free();
        c = new FileBackedClob(new String(testCase2));
        c.truncate(42);
        assertEquals(c.length(), 42);
        assertEquals(c.getBackingFile().length(), 42 * 4);
        c.free();
        c = new FileBackedClob(new CharArrayReader(testCase3));
        c.truncate(1024);
        assertEquals(c.length(), 1024);
        assertEquals(c.getBackingFile().length(), 1024 * 4);
        c.free();
    }

    @Test
    public void testGetSubString() throws Exception {
        FileBackedClob c;
        char[] referenceChars = new char[5];
        System.arraycopy(testCase1, 5, referenceChars, 0, 5);
        String reference = new String(referenceChars);
        c = new FileBackedClob(new CharArrayReader(testCase1));
        assertEquals(reference, c.getSubString(6, 5));
        c.free();
        c = new FileBackedClob(new String(testCase2));
        assertEquals( reference, c.getSubString(6, 5));
        c.free();
        c = new FileBackedClob(new CharArrayReader(testCase3));
        assertEquals(reference, c.getSubString(6, 5));
        c.free();
    }

    @Test
    public void testGetCharacterStream_0args() throws Exception {
        FileBackedClob c;
        c = new FileBackedClob(new CharArrayReader(testCase1));
        assertCharacterStreamEquals(c.getCharacterStream(), new CharArrayReader(testCase1));
        c.free();
        c = new FileBackedClob(new String(testCase2));
        assertCharacterStreamEquals(c.getCharacterStream(), new CharArrayReader(testCase2));
        c.free();
        c = new FileBackedClob(new CharArrayReader(testCase3));
        assertCharacterStreamEquals(c.getCharacterStream(), new CharArrayReader(testCase3));
        c.free();
    }

    @Test
    public void testSetString_long_String() throws Exception {
        FileBackedClob b;
        char[] test1 = "test".toCharArray();
        char[] test2 = "test0123456789".toCharArray();
        char[] firstPartReference = new char[testCase2.length - test1.length - 4];
        char[] secondPartReference = new char[4];
        System.arraycopy(testCase2, 0, firstPartReference, 0, testCase2.length - test1.length - 4);
        System.arraycopy(testCase2, testCase2.length - 4 - 1, secondPartReference, 0, 4);
        b = new FileBackedClob(new CharArrayReader(testCase2));
        b.setString(testCase2.length - test1.length - 4 + 1, new String(test1));
        assertEquals(new String(firstPartReference), b.getSubString(1, testCase2.length - test1.length - 4));
        assertEquals(new String(secondPartReference), b.getSubString(testCase2.length - 4, 4));
        assertEquals(new String(test1), b.getSubString(testCase2.length - 4 - test1.length + 1, test1.length));
        assertEquals(b.length(), 1024);
        b.setString(testCase2.length - test1.length - 4 + 1, new String(test2));
        assertEquals(new String(firstPartReference), b.getSubString(1, testCase2.length - test1.length - 4));
        assertEquals(b.length(), 1024 - test1.length - 4 + test2.length);
        assertEquals(new String(test2), b.getSubString(b.length() - test2.length + 1, test2.length));
        b.free();
    }

    @Test
    public void testSetString_4args() throws Exception {
        FileBackedClob b;
        char[] test1 = "test".toCharArray();
        char[] test2 = "01test23456789".toCharArray();
        char[] firstPartReference = new char[testCase2.length - test1.length - 4];
        char[] secondPartReference = new char[4];
        System.arraycopy(testCase2, 0, firstPartReference, 0, testCase2.length - test1.length - 4);
        System.arraycopy(testCase2, testCase2.length - 4 - 1, secondPartReference, 0, 4);
        b = new FileBackedClob(new CharArrayReader(testCase2));
        b.setString(testCase2.length - test1.length - 4 + 1, new String(test2), 2, 4);
        assertEquals(new String(firstPartReference), b.getSubString(1, testCase2.length - test1.length - 4));
        assertEquals(new String(secondPartReference), b.getSubString(testCase2.length - 4, 4));
        assertEquals(new String(test1), b.getSubString(testCase2.length - 4 - test1.length + 1, test1.length));
        assertEquals(b.length(), 1024);
        b.free();
    }

    @Test
    public void testSetCharacterStream() throws Exception {
        FileBackedClob b;
        char[] test1 = "test".toCharArray();
        char[] test2 = "test0123456789".toCharArray();
        char[] firstPartReference = new char[testCase2.length - test1.length - 4];
        char[] secondPartReference = new char[4];
        System.arraycopy(testCase2, 0, firstPartReference, 0, testCase2.length - test1.length - 4);
        System.arraycopy(testCase2, testCase2.length - 4 - 1, secondPartReference, 0, 4);
        b = new FileBackedClob(new CharArrayReader(testCase2));
        Writer os = b.setCharacterStream(testCase2.length - test1.length - 4 + 1);
        os.write(test1);
        os.close();
        assertEquals(new String(firstPartReference), b.getSubString(1, testCase2.length - test1.length - 4));
        assertEquals(new String(secondPartReference), b.getSubString(testCase2.length - 4, 4));
        assertEquals(new String(test1), b.getSubString(testCase2.length - 4 - test1.length + 1, test1.length));
        assertEquals(b.length(), 1024);
        os = b.setCharacterStream(testCase2.length - test1.length - 4 + 1);
        os.write(test2);
        os.close();
        assertEquals(new String(firstPartReference), b.getSubString(1, testCase2.length - test1.length - 4));
        assertEquals(b.length(), 1024 - test1.length - 4 + test2.length);
        assertEquals(new String(test2), b.getSubString(b.length() - test2.length + 1, test2.length));
        b.free();
    }

    @Test
    public void testFinalize() throws Exception {
        FileBackedClob b;
        b = new FileBackedClob(new CharArrayReader(testCase2));
        assertTrue(b.getBackingFile().exists());
        try {
            b.finalize();
        } catch (Throwable ex) {
            Exceptions.printStackTrace(ex);
        }
        assertFalse(b.getBackingFile().exists());
    }
    
    @Test
    public void testFree() throws Exception {
        FileBackedClob b;
        b = new FileBackedClob(new CharArrayReader(testCase2));
        assertTrue(b.getBackingFile().exists());
        b.free();
        assertFalse(b.getBackingFile().exists());
    }
}
