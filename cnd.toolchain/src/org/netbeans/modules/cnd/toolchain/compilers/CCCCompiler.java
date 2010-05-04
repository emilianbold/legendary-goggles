/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.cnd.toolchain.compilers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;
import org.netbeans.modules.cnd.api.toolchain.CompilerFlavor;
import org.netbeans.modules.cnd.api.toolchain.ToolchainManager.CompilerDescriptor;
import org.netbeans.modules.nativeexecution.api.util.LinkSupport;
import org.netbeans.modules.cnd.api.toolchain.AbstractCompiler;
import org.netbeans.modules.cnd.api.toolchain.ToolKind;
import org.netbeans.modules.cnd.toolchain.compilerset.ToolUtils;
import org.netbeans.modules.cnd.utils.CndUtils;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironmentFactory;
import org.netbeans.modules.nativeexecution.api.NativeProcess;
import org.netbeans.modules.nativeexecution.api.NativeProcess.State;
import org.netbeans.modules.nativeexecution.api.NativeProcessBuilder;
import org.netbeans.modules.nativeexecution.api.util.HostInfoUtils;
import org.openide.util.Utilities;

/*package*/ abstract class CCCCompiler extends AbstractCompiler {

    private static final String DEV_NULL = "/dev/null"; // NOI18N

    private volatile Pair compilerDefinitions;
    private static File emptyFile = null;

    protected CCCCompiler(ExecutionEnvironment env, CompilerFlavor flavor, ToolKind kind, String name, String displayName, String path) {
        super(env, flavor, kind, name, displayName, path);
    }

    @Override
    public boolean setSystemIncludeDirectories(List<String> values) {
        assert values != null;
        if (compilerDefinitions == null) {
            compilerDefinitions = new Pair();
        }
        if (values.equals(compilerDefinitions.systemIncludeDirectoriesList)) {
            return false;
        }
        List<String> systemIncludeDirectoriesList = new ArrayList<String>(values);
        normalizePaths(systemIncludeDirectoriesList);
        compilerDefinitions.systemIncludeDirectoriesList = systemIncludeDirectoriesList;
        return true;
    }

    @Override
    public boolean setSystemPreprocessorSymbols(List<String> values) {
        assert values != null;
        if (compilerDefinitions == null) {
            compilerDefinitions = new Pair();
        }
        if (values.equals(compilerDefinitions.systemPreprocessorSymbolsList)) {
            return false;
        }
        compilerDefinitions.systemPreprocessorSymbolsList = new ArrayList<String>(values);
        return true;
    }

    @Override
    public List<String> getSystemPreprocessorSymbols() {
        if (compilerDefinitions == null) {
            resetSystemProperties();
        }
        return compilerDefinitions.systemPreprocessorSymbolsList;
    }

    @Override
    public List<String> getSystemIncludeDirectories() {
        if (compilerDefinitions == null) {
            resetSystemProperties();
        }
        return compilerDefinitions.systemIncludeDirectoriesList;
    }

    @Override
    public boolean isReady() {
        return compilerDefinitions != null;
    }

    @Override
    public void waitReady(boolean reset) {
        if (reset || !isReady()) {
            resetSystemProperties();
        }
    }

    @Override
    public void resetSystemProperties(boolean lazy) {
        if (lazy) {
            compilerDefinitions = null;
        } else {
            CndUtils.assertNonUiThread();
            compilerDefinitions = getFreshSystemIncludesAndDefines();
        }
    }

    @Override
    public void loadSettings(Preferences prefs, String prefix) {
        List<String> includeDirList = new ArrayList<String>();
        String includeDirPrefix = prefix + ".systemIncludes"; // NOI18N
        int includeDirCount = prefs.getInt(includeDirPrefix + ".count", 0); // NOI18N
        for (int i = 0; i < includeDirCount; ++i) {
            String includeDir = prefs.get(includeDirPrefix + '.' + i, null); // NOI18N
            if (includeDir != null) {
                includeDirList.add(includeDir);
            }
        }
        if (includeDirList.isEmpty()) {
            // try to load using the old way;  this might be removed at some moment in future
            List<String> oldIncludeDirList = PersistentList.restoreList(getUniqueID() + "systemIncludeDirectoriesList"); // NOI18N
            if (oldIncludeDirList != null) {
                includeDirList.addAll(oldIncludeDirList);
            }
        }
        setSystemIncludeDirectories(includeDirList);

        List<String> preprocSymbolList = new ArrayList<String>();
        String preprocSymbolPrefix = prefix + ".systemMacros"; // NOI18N
        int preprocSymbolCount = prefs.getInt(preprocSymbolPrefix + ".count", 0); // NOI18N
        for (int i = 0; i < preprocSymbolCount; ++i) {
            String preprocSymbol = prefs.get(preprocSymbolPrefix + '.' + i, null); // NOI18N
            if (preprocSymbol != null) {
                preprocSymbolList.add(preprocSymbol);
            }
        }
        if (preprocSymbolList.isEmpty()) {
            // try to load using the old way;  this might be removed at some moment in future
            List<String> oldPreprocSymbolList = PersistentList.restoreList(getUniqueID() + "systemPreprocessorSymbolsList"); // NOI18N
            if (oldPreprocSymbolList != null) {
                preprocSymbolList.addAll(oldPreprocSymbolList);
            }
        }
        setSystemPreprocessorSymbols(preprocSymbolList);
    }

    @Override
    public void saveSettings(Preferences prefs, String prefix) {
        List<String> includeDirList = getSystemIncludeDirectories();
        String includeDirPrefix = prefix + ".systemIncludes"; // NOI18N
        prefs.putInt(includeDirPrefix + ".count", includeDirList.size()); // NOI18N
        for (int i = 0; i < includeDirList.size(); ++i) {
            prefs.put(includeDirPrefix + '.' + i, includeDirList.get(i)); // NOI18N
        }

        List<String> preprocSymbolList = getSystemPreprocessorSymbols();
        String preprocSymbolPrefix = prefix + ".systemMacros"; // NOI18N
        prefs.putInt(preprocSymbolPrefix + ".count", preprocSymbolList.size()); // NOI18N
        for (int i = 0; i < preprocSymbolList.size(); ++i) {
            prefs.put(preprocSymbolPrefix + '.' + i, preprocSymbolList.get(i)); // NOI18N
        }
    }

    protected final void getSystemIncludesAndDefines(String arguments, boolean stdout, Pair pair) throws IOException {
        String compilerPath = getPath();
        if (compilerPath == null || compilerPath.length() == 0) {
            return;
        }
        ExecutionEnvironment execEnv = getExecutionEnvironment();
        try {
            if (execEnv.isLocal() && Utilities.isWindows()) {
                compilerPath = LinkSupport.resolveWindowsLink(compilerPath);
            }
            if (!HostInfoUtils.fileExists(execEnv, compilerPath)) {
                compilerPath = getDefaultPath();
            }
            if (!HostInfoUtils.fileExists(execEnv, compilerPath)) {
                return;
            }

            List<String> argsList = new ArrayList<String>();
            argsList.addAll(Arrays.asList(arguments.trim().split(" +"))); // NOI18N
            argsList.add(getEmptyFile(execEnv));

            NativeProcessBuilder npb = NativeProcessBuilder.newProcessBuilder(execEnv);
            npb.setExecutable(compilerPath);
            npb.setArguments(argsList.toArray(new String[argsList.size()]));
            npb.getEnvironment().prependPathVariable("PATH", ToolUtils.getDirName(compilerPath)); // NOI18N

            NativeProcess process = npb.call();
            if (process.getState() != State.ERROR) {
                InputStream stream = stdout? process.getInputStream() : process.getErrorStream();
                if (stream != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    try {
                        parseCompilerOutput(reader, pair);
                    } finally {
                        reader.close();
                    }
                }
            }
        } catch (IOException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IOException(ex);
        }
    }

    // To be overridden
    protected abstract void parseCompilerOutput(BufferedReader reader, Pair pair);

    protected abstract Pair getFreshSystemIncludesAndDefines();

    protected String getDefaultPath() {
        CompilerDescriptor compiler = getDescriptor();
        if (compiler != null && compiler.getNames().length > 0){
            return compiler.getNames()[0];
        }
        return ""; // NOI18N
    }

    /**
     * Determines whether the given macro presents in the list
     * @param macrosList list of macros strings (in the form "macro=value" or just "macro")
     * @param macroToFind the name of the macro to search for
     * @return true if macro with the given name is found, otherwise false
     */
    protected boolean containsMacro(List<String> macrosList, String macroToFind) {
	int len = macroToFind.length();
	for (Iterator<String> it = macrosList.iterator(); it.hasNext();) {
	    String macro = it.next();
	    if (macro.startsWith(macroToFind) ) {
		if( macro.length() == len ) {
		    return true; // they are just equal
		}
		if( macro.charAt(len) == '=' ) {
		    return true; // it presents in the form macro=value
		}
	    }
	}
	return false;
    }

    protected void parseUserMacros(final String line, final List<String> preprocessorList) {
        int defineIndex = line.indexOf("-D"); // NOI18N
        while (defineIndex >= 0) {
            String token;
            int spaceIndex = line.indexOf(' ', defineIndex + 1); // NOI18N
            if (spaceIndex > 0) {
                token = line.substring(defineIndex+2, spaceIndex);
                if (defineIndex > 0 && line.charAt(defineIndex-1)=='"') {
                    if (token.length() > 0 && token.charAt(token.length()-1)=='"') {
                        token = token.substring(0,token.length()-1);
                    }
                }
                addUnique(preprocessorList, token);
                defineIndex = line.indexOf("-D", spaceIndex); // NOI18N
            } else {
                token = line.substring(defineIndex+2);
                if (defineIndex > 0 && line.charAt(defineIndex-1)=='"') {
                    if (token.length() > 0 && token.charAt(token.length()-1)=='"') {
                        token = token.substring(0,token.length()-1);
                    }
                }
                addUnique(preprocessorList, token);
                break;
            }
        }
    }

    private String getEmptyFile(ExecutionEnvironment execEnv) {
        if (execEnv.isLocal() && Utilities.isWindows()) {
            // no /dev/null on Windows, so we need a real file
            if (emptyFile == null) {
                try {
                    File tmpFile = File.createTempFile("xyz", ".c"); // NOI18N
                    tmpFile.deleteOnExit();
                    emptyFile = tmpFile;
                } catch (IOException ioe) {
                }
            }
            return emptyFile == null? DEV_NULL : emptyFile.getAbsolutePath();
        } else {
            return DEV_NULL;
        }
    }

    protected String getUniqueID() {
        if (getCompilerSet() == null || getCompilerSet().isAutoGenerated()) {
            return getClass().getName() +
                    ExecutionEnvironmentFactory.toUniqueID(getExecutionEnvironment()).hashCode() + getPath().hashCode() + "."; // NOI18N
        } else {
            return getClass().getName() + getCompilerSet().getName() +
                    ExecutionEnvironmentFactory.toUniqueID(getExecutionEnvironment()).hashCode() + getPath().hashCode() + "."; // NOI18N
        }
    }

//    private void dumpLists() {
//        System.out.println("==================================" + getDisplayName()); // NOI18N
//        for (int i = 0; i < compilerDefinitions.systemIncludeDirectoriesList.size(); i++) {
//            System.out.println("-I" + compilerDefinitions.systemIncludeDirectoriesList.get(i)); // NOI18N
//        }
//        for (int i = 0; i < compilerDefinitions.systemPreprocessorSymbolsList.size(); i++) {
//            System.out.println("-D" + compilerDefinitions.systemPreprocessorSymbolsList.get(i)); // NOI18N
//        }
//    }

    protected static final <T> void addUnique(List<? super T> list, T element) {
        if (!list.contains(element)) {
            list.add(element);
        }
    }

    protected static final class Pair {
        public List<String> systemIncludeDirectoriesList;
        public List<String> systemPreprocessorSymbolsList;
        public Pair(){
            systemIncludeDirectoriesList = new ArrayList<String>(0);
            systemPreprocessorSymbolsList = new ArrayList<String>(0);
        }
    }
}
