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

package org.netbeans.modules.j2ee.weblogic9;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.j2ee.deployment.common.api.Version;


/**
 *
 * @author sherold
 */
public class WLPluginPropertiesTest extends NbTestCase {

    public WLPluginPropertiesTest(String testName) {
        super(testName);
    }

    public void testGetServerVersion() throws Exception {
        File baseFolder = getWorkDir();
        File libFolder = new File(baseFolder, "server/lib");
        libFolder.mkdirs();

        File file = new File(libFolder, "weblogic.jar");
        createJar(file, "Implementation-Version: 10.0.0.1");
        Version version = WLPluginProperties.getServerVersion(baseFolder);
        assertEquals("10.0.0.1", version.toString());
        assertEquals(10, version.getMajor().intValue());
        assertEquals(1, version.getUpdate().intValue());
        assertTrue(file.delete());
    }

    public void testIsSupportedVersion() throws Exception {
        File baseFolder = getWorkDir();
        File libFolder = new File(baseFolder, "server/lib");
        libFolder.mkdirs();
        File file = new File(libFolder, "weblogic.jar");
        createJar(file, "Implementation-Version: 10.0.0.0");
        assertTrue(WLPluginProperties.isSupportedVersion(WLPluginProperties.getServerVersion(baseFolder)));
        createJar(file, "Implementation-Version: 9.0.0.0");
        assertTrue(WLPluginProperties.isSupportedVersion(WLPluginProperties.getServerVersion(baseFolder)));
        createJar(file, "Implementation-Version: 8.0.0.0");
        assertFalse(WLPluginProperties.isSupportedVersion(WLPluginProperties.getServerVersion(baseFolder)));
        createJar(file, "Missing-Implementation-Version: 10.0.0.0");
        assertFalse(WLPluginProperties.isSupportedVersion(WLPluginProperties.getServerVersion(baseFolder)));
    }

    public void testGetWeblogicJar() throws Exception {
        File baseFolder = getWorkDir();
        File libFolder = new File(baseFolder, "server/lib");
        libFolder.mkdirs();

        File file = new File(libFolder, "weblogic.jar");

        File wlJar = WLPluginProperties.getWeblogicJar(baseFolder);
        assertNotNull(wlJar);
        assertEquals(file, wlJar);

        createJar(file, "Implementation-Version: 9.0.0.0");
        wlJar = WLPluginProperties.getWeblogicJar(baseFolder);
        assertNotNull(wlJar);
        assertEquals(file, wlJar);
    }

    public void testJvmVendor() {
        assertEquals(WLPluginProperties.JvmVendor.SUN,
                WLPluginProperties.JvmVendor.fromPropertiesString("Sun"));
        assertEquals(WLPluginProperties.JvmVendor.ORACLE,
                WLPluginProperties.JvmVendor.fromPropertiesString("Oracle"));
        assertEquals(WLPluginProperties.JvmVendor.DEFAULT,
                WLPluginProperties.JvmVendor.fromPropertiesString(""));
        assertEquals(WLPluginProperties.JvmVendor.DEFAULT,
                WLPluginProperties.JvmVendor.fromPropertiesString("  "));
        assertEquals("something",
                WLPluginProperties.JvmVendor.fromPropertiesString("something").toPropertiesString());

        WLPluginProperties.JvmVendor vendor1 = WLPluginProperties.JvmVendor.fromPropertiesString("something1");
        WLPluginProperties.JvmVendor vendor2 = WLPluginProperties.JvmVendor.fromPropertiesString("something2");
        WLPluginProperties.JvmVendor vendor3 = WLPluginProperties.JvmVendor.fromPropertiesString("something1");

        assertNotSame(vendor1, vendor2);
        assertNotSame(vendor1, vendor3);
        assertNotSame(vendor2, vendor3);
    }

    private void createJar(File file, String... manifestLines) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Manifest-Version: 1.0\n");
        for (String line : manifestLines) {
            stringBuilder.append(line).append("\n");
        }

        InputStream is = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        try {
            new JarOutputStream(new FileOutputStream(file), new Manifest(is)).close();
        } finally {
            is.close();
        }
    }

}
