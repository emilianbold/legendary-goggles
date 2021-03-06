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
package org.netbeans.modules.cnd.api.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import org.netbeans.modules.cnd.api.remote.HostInfoProvider;
import org.netbeans.modules.cnd.api.toolchain.PlatformTypes;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironmentFactory;
import org.netbeans.modules.nativeexecution.api.HostInfo;
import org.netbeans.modules.nativeexecution.api.util.ConnectionManager.CancellationException;
import org.netbeans.modules.nativeexecution.api.util.HostInfoUtils;
import org.netbeans.modules.nativeexecution.api.util.ProcessUtils;
import org.netbeans.modules.nativeexecution.api.util.ProcessUtils.ExitStatus;
import org.openide.util.Exceptions;

/**
 * This class evolutioned from cnd.api.utils.Path
 * Now it handles all development host related information
 *
 * @author gordonp
 * @author Sergey Grinev
 */
public final class PlatformInfo {

    private ArrayList<String> list = new ArrayList<String>();
    private String pathName;
    private final ExecutionEnvironment executionEnvironment;
    private final int platform;
    private HostInfo hostinfo;

    private PlatformInfo(ExecutionEnvironment execEnv) {
        this.executionEnvironment = execEnv;
        try {
            hostinfo = HostInfoUtils.getHostInfo(execEnv);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
//            Exceptions.printStackTrace(ex);
        } catch (CancellationException ex) {
            // doesn't make sense to log - user cancelled operation (most likely, login) explicitely
        }
        if (hostinfo != null) {
            switch (hostinfo.getOSFamily()){
                case SUNOS:
                    switch (hostinfo.getCpuFamily()){
                        case SPARC:
                            platform = PlatformTypes.PLATFORM_SOLARIS_SPARC;
                            break;
                        case X86:
                            platform = PlatformTypes.PLATFORM_SOLARIS_INTEL;
                            break;
                        default:
                            platform = PlatformTypes.PLATFORM_GENERIC;
                            break;
                    }
                    break;
                case LINUX:
                    platform = PlatformTypes.PLATFORM_LINUX;
                    break;
                case WINDOWS:
                    platform = PlatformTypes.PLATFORM_WINDOWS;
                    break;
                case MACOSX:
                    platform = PlatformTypes.PLATFORM_MACOSX;
                    break;
                case FREEBSD:
                    platform = PlatformTypes.PLATFORM_GENERIC;
                    break;
                default:
                    platform = PlatformTypes.PLATFORM_GENERIC;
                    break;
            }
        } else {
            platform = PlatformTypes.PLATFORM_NONE;
        }

        String path = getEnv().get(getPathName());
        if (Boolean.getBoolean("cnd.debug.use_altpath")) { // NOI18N
            // Its very hard to debug path problems on Windows because changing PATH is so hard. So these
            // properties let me do it without changing my real path
            path = System.getProperty("cnd.debug.altpath", path); // NOI18N
        }
        if (path != null) {
            StringTokenizer st = new StringTokenizer(path, File.pathSeparator); // NOI18N

            while (st.hasMoreTokens()) {
                String dir = st.nextToken();
                list.add(dir);
            }
        } else {
            if (isUnix()) {
                list.add("/bin"); // NOI18N
                list.add("/usr/bin"); // NOI18N
                list.add("/sbin"); // NOI18N
                list.add("/usr/sbin"); // NOI18N
            } else if (isWindows()) {
                list.add("C:/WINDOWS/System32"); // NOI18N
                list.add("C:/WINDOWS"); // NOI18N
                list.add("C:/WINDOWS/System32/WBem"); // NOI18N
            } else {
                System.err.println("PlatformInfo: Path is empty for host " + executionEnvironment);
            }
        }
    }

    public ExecutionEnvironment getExecutionEnvironment() {
        return executionEnvironment;
    }

    /**
     * Replace the current path with this new one. We should validate but currently aren't.
     *
     * @param newPath A list of directories to use as a replacement path
     */
    public void setPath(ArrayList<String> newPath) {
        list = newPath;
    }

    /**
     * Read the PATH from the environment and make an array from it.
     *
     * @return A list of all path directories
     */
    public ArrayList<String> getPath() {
        return list;
    }

    /**
     * Return the path with the correct path separator character.
     * This would be named toString() if it weren't a method.
     *
     * @return Path as a string (with OS specific directory separators)
     */
    public String getPathAsString() {
        if (list.isEmpty()) {
            return "";
        }
        StringBuilder buf = new StringBuilder();

        for (String dir : list) {
            buf.append(dir);
            buf.append(pathSeparator());
        }
        return buf.substring(0, buf.length() - 1); // remove the trailing pathSeparator...
    }

    public String getPathAsStringWith(String newDir) {
        if (newDir == null || newDir.length() == 0) {
            return getPathName() + '=' + getPathAsString();
        } else {
            return getPathName() + '=' + newDir + pathSeparator() + getPathAsString();
        }
    }

    /**
     * Add a directory to the path.
     *
     * @param pos Position where dir should be added
     * @param dir New directory to add to path
     * @throws IndexOutOfBoundsException
     */
    public void add(int pos, String dir) throws IndexOutOfBoundsException {
        list.add(pos, dir);
    }

    /**
     * Remove a directory (by index) from the path.
     *
     * @param pos Position where dir should be added
     * @throws IndexOutOfBoundsException
     */
    public void remove(int pos) throws IndexOutOfBoundsException {
        list.remove(pos);
    }

    /**
     * This utility method makes it easier (on Windows) to replace PATH with one with
     * the same case. IZ 103016 updated PATH but it wasn't foud because Path wasn't
     * replaced. This will let us add a path using the exact same name.
     */
    public String getPathName() {
        if (pathName == null) {
            if (isWindows()) {
                for (String key : getEnv().keySet()) {
                    if (key.toLowerCase(Locale.getDefault()).equals("path")) { // NOI18N
                        pathName = key.substring(0, 4);
                        return pathName;
                    }
                }
            }
            pathName = "PATH"; // NOI18N
        }
        return pathName;
    }

    public String findCommand(String cmd) {
        if (cmd != null && cmd.length() > 0) {
            int i = cmd.replace('\\', '/').lastIndexOf('/');
            if (i >= 0) {
                return null;
            }
            ArrayList<String> dirlist = getPath();

            for (String dir : dirlist) {
                String path = findCommand(dir, cmd);
                if (path != null) {
                    return path;
                }
            }
        }
        return null;
    }

    public String findCommand(String dir, String cmd) {
        String path = dir + separator() + cmd;
        if (fileExists(path)) {
            return path;
        } else {
            if (isWindows() && cmd.endsWith(".exe")){ // NOI18N
                String path2 = dir + separator() + cmd + ".lnk"; // NOI18N
                if (fileExists(path2)) {
                    return path;
                }
            }
        }
        if (isWindows() && !cmd.endsWith(".exe")) { // NOI18N
            String cmd2 = cmd + ".exe"; // NOI18N
            path = dir + separator() + cmd2;
            if (fileExists(path)) {
                return path;
            }
            String path2 = dir + separator() + cmd + ".lnk"; // NOI18N
            if (fileExists(path2)) {
                return path;
            }
        }
        return null;
    }

    public String separator() {
        return isWindows() ? "\\" : "/"; // NOI18N
    }

    public String pathSeparator() {
        return isWindows() ? ";" : ":"; // NOI18N
    }

    public int getPlatform() {
        return platform;
    }

    // utility
    public boolean isWindows() {
        return platform == PlatformTypes.PLATFORM_WINDOWS;
    }

    public boolean isUnix() {
        return platform == PlatformTypes.PLATFORM_SOLARIS_INTEL || platform == PlatformTypes.PLATFORM_SOLARIS_SPARC || platform == PlatformTypes.PLATFORM_LINUX || platform == PlatformTypes.PLATFORM_MACOSX;
    }

    public boolean isLinux() {
        return platform == PlatformTypes.PLATFORM_LINUX;
    }

    public boolean isMac() {
        return platform == PlatformTypes.PLATFORM_MACOSX;
    }

    public boolean isSolaris() {
        return platform == PlatformTypes.PLATFORM_SOLARIS_INTEL || platform == PlatformTypes.PLATFORM_SOLARIS_SPARC;
    }

    public boolean isLocalhost() {
        return executionEnvironment.isLocal();
    }

    public Map<String, String> getEnv() {
        return HostInfoProvider.getEnv(executionEnvironment);
    }

    public boolean fileExists(String path) {
        return HostInfoProvider.fileExists(executionEnvironment, path);
    }

    public File[] listFiles(File file) {
        //TODO: till API review
        if (executionEnvironment.isLocal()) {
            return file.listFiles();
        } else {
            final ExitStatus res = ProcessUtils.execute(executionEnvironment, "ls", "-A1"); //NOI18N
            if (res.isOK()) {
                String files = res.getOutputString();
                if (files != null) {
                    BufferedReader bufferedReader = new BufferedReader(new StringReader(files));
                    String line;
                    ArrayList<File> lines = new ArrayList<File>();
                    try {
                        while ((line = bufferedReader.readLine()) != null) {
                            lines.add(new File(line));
                        }
                        bufferedReader.close();
                    } catch (IOException ex) {
                        //hardly can happen during reading string
                        Exceptions.printStackTrace(ex);
                        return null;
                    }
                    return lines.toArray(new File[lines.size()]);
                }
            }
        }
        return null;
    }

    public boolean fileIsDirectory(File file) {
        //TODO: till API review
        if (executionEnvironment.isLocal()) {
            return file.isDirectory();
        } else {
            return ProcessUtils.execute(executionEnvironment, "test",  "-d", "\"" + file.getPath() + "\"").isOK(); //NOI18N
        }
    }

    public boolean fileIsFile(File file) {
        //TODO: till API review
        if (executionEnvironment.isLocal()) {
            return file.isFile();
        } else {
            return ProcessUtils.execute(executionEnvironment, "test", "-f", "\"" + file.getPath() + "\"").isOK(); //NOI18N
        }
    }

    public boolean fileCanRead(File file) {
        //TODO: till API review
        if (executionEnvironment.isLocal()) {
            return file.canRead();
        } else {
            return ProcessUtils.execute(executionEnvironment, "test", "-r" ,"\"" + file.getPath() + "\"").isOK(); //NOI18N
        }
    }

    private static final Map<ExecutionEnvironment, PlatformInfo> map =
            new HashMap<ExecutionEnvironment, PlatformInfo>();

    public static synchronized PlatformInfo getDefault(ExecutionEnvironment execEnv) {
        PlatformInfo pi = map.get(execEnv);
        if (pi == null) {
            pi = new PlatformInfo(execEnv);
            map.put(execEnv, pi);
        }
        return pi;
    }

    public static PlatformInfo localhost() {
        return getDefault(ExecutionEnvironmentFactory.getLocal());
    }
}
