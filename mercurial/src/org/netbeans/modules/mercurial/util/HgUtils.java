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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2009 Sun
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

package org.netbeans.modules.mercurial.util;

import java.awt.EventQueue;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.jdesktop.layout.LayoutStyle;
import org.netbeans.modules.mercurial.FileInformation;
import org.netbeans.modules.mercurial.FileStatusCache;
import org.netbeans.modules.mercurial.Mercurial;
import org.netbeans.modules.mercurial.HgModuleConfig;
import org.netbeans.modules.mercurial.ui.status.SyncFileNode;
import org.openide.util.NbBundle;
import org.netbeans.modules.versioning.util.Utils;

import org.openide.loaders.DataObject;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;
import org.netbeans.modules.versioning.spi.VCSContext;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileLock;
import org.openide.loaders.DataObjectNotFoundException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.modules.mercurial.HgFileNode;
import org.netbeans.modules.mercurial.OutputLogger;
import org.netbeans.modules.mercurial.ui.commit.CommitOptions;
import org.netbeans.modules.mercurial.ui.log.HgLogMessage;
import org.netbeans.modules.versioning.util.FileSelector;
import org.openide.util.HelpCtx;
import org.openide.util.Utilities;

/**
 *
 * @author jrice
 */
public class HgUtils {    
    private static final Pattern httpPasswordPattern = Pattern.compile("(https*://)(\\w+\\b):(\\b\\S*)@"); //NOI18N
    private static final String httpPasswordReplacementStr = "$1$2:\\*\\*\\*\\*@"; //NOI18N
    private static final Pattern httpCredentialsPattern = Pattern.compile("(.*://)(\\w+\\b):(\\b\\S*)@"); //NOI18N
    private static final String httpCredentialsReplacementStr = "$1"; //NOI18N
    
    private static final Pattern metadataPattern = Pattern.compile(".*\\" + File.separatorChar + "(\\.)hg(\\" + File.separatorChar + ".*|$)"); // NOI18N
    
    // IGNORE SUPPORT HG: following file patterns are added to {Hg repos}/.hgignore and Hg will ignore any files
    // that match these patterns, reporting "I"status for them // NOI18N
    private static final String [] HG_IGNORE_FILES = { "\\.orig$", "\\.orig\\..*$", "\\.chg\\..*$", "\\.rej$", "\\.conflict\\~$"}; // NOI18N
    private static final String HG_IGNORE_ORIG_FILES = "\\.orig$"; // NOI18N
    private static final String HG_IGNORE_ORIG_ANY_FILES = "\\.orig\\..*$"; // NOI18N
    private static final String HG_IGNORE_CHG_ANY_FILES = "\\.chg\\..*$"; // NOI18N
    private static final String HG_IGNORE_REJ_ANY_FILES = "\\.rej$"; // NOI18N
    private static final String HG_IGNORE_CONFLICT_ANY_FILES = "\\.conflict\\~$"; // NOI18N
    
    private static final String FILENAME_HGIGNORE = ".hgignore"; // NOI18N
    private static final String IGNORE_SYNTAX_PREFIX = "syntax:";       //NOI18N
    private static final String IGNORE_SYNTAX_GLOB = "glob";            //NOI18N
    private static final String IGNORE_SYNTAX_REGEXP = "regexp";        //NOI18N

    private static HashMap<String, Set<Pattern>> ignorePatterns;


    /**
     * Timeout for remote repository check in seconds, after expires the repository will be considered valid.
     */
    public static final String HG_CHECK_REPOSITORY_TIMEOUT_SWITCH = "mercurial.checkRepositoryTimeout"; //NOI18N
    public static final String HG_CHECK_REPOSITORY_DEFAULT_TIMEOUT = "5";
    public static final int HG_CHECK_REPOSITORY_DEFAULT_ROUNDS = 50;
    public static final String HG_FOLDER_NAME = ".hg";                 //NOI18N
    private static int repositoryValidityCheckRounds = 0;
    public static String PREFIX_VERSIONING_MERCURIAL_URL = "versioning.mercurial.url."; //NOI18N

    /**
     * addDaysToDate - add days (+days) or subtract (-days) from the given date
     *
     * @param int days to add or substract
     * @return Date new date that has been calculated
     */
    public static Date addDaysToDate(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    /**
     * Creates annotation format string.
     * @param format format specified by the user, e.g. [{status}]
     * @return modified format, e.g. [{0}]
     */
    public static String createAnnotationFormat(final String format) {
        String string = format;
        string = Utils.skipUnsupportedVariables(string, new String[] {"{status}", "{folder}"});     // NOI18N
        string = string.replaceAll("\\{status\\}", "\\{0\\}");                                      // NOI18N
        string = string.replaceAll("\\{folder\\}", "\\{1\\}");                                      // NOI18N
        return string;
    }

    /**
     * isSolaris - check you are running onthe Solaris OS
     *
     * @return boolean true - on Solaris, false - not on Solaris
     */
    public static boolean isSolaris(){
        return System.getProperty("os.name").equals("SunOS"); // NOI18N
    }

    /**
     * replaceHttpPassword - replace any http or https passwords in the string
     *
     * @return String modified string with **** instead of passwords
     */
    public static String removeHttpCredentials(String s){
        Matcher m = httpCredentialsPattern.matcher(s);
        return m.replaceAll(httpCredentialsReplacementStr);
    }

    /**
     * replaceHttpPassword - replace any http or https passwords in the string
     *
     * @return String modified string with **** instead of passwords
     */
    public static String replaceHttpPassword(String s){
        Matcher m = httpPasswordPattern.matcher(s);
        return m.replaceAll(httpPasswordReplacementStr); 
    }
    
    /**
     * replaceHttpPassword - replace any http or https passwords in the List<String>
     *
     * @return List<String> containing modified strings with **** instead of passwords
     */
    public static List<String> replaceHttpPassword(List<String> list){
        if(list == null) return null;

        List<String> out = new ArrayList<String>(list.size());
        for(String s: list){
            out.add(replaceHttpPassword(s));
        } 
        return out;
    }

    /**
     * isInUserPath - check if passed in name is on the Users PATH environment setting
     *
     * @param name to check
     * @return boolean true - on PATH, false - not on PATH
     */
    public static boolean isInUserPath(String name) {
        String path = findInUserPath(name);
        return (path == null || path.equals(""))? false: true;
    }

        /**
     * findInUserPath - check if passed in name is on the Users PATH environment setting and return the path
     *
     * @param name to check
     * @return String full path to name
     */
    public static String findInUserPath(String... names) {
        String pathEnv = System.getenv().get("PATH");// NOI18N
        // Work around issues on Windows fetching PATH
        if(pathEnv == null) pathEnv = System.getenv().get("Path");// NOI18N
        if(pathEnv == null) pathEnv = System.getenv().get("path");// NOI18N
        String pathSeparator = System.getProperty("path.separator");// NOI18N
        if (pathEnv == null || pathSeparator == null) return "";

        String[] paths = pathEnv.split(pathSeparator);
        for (String path : paths) {
            for (String name : names) {
                File f = new File(path, name);
                // On Windows isFile will fail on hgk.cmd use !isDirectory
                if (f.exists() && !f.isDirectory()) {
                    return path;
                }
            }
        }
        return "";
    }

    /**
     * confirmDialog - display a confirmation dialog
     *
     * @param bundleLocation location of string resources to display
     * @param title of dialog to display    
     * @param query ask user
     * @return boolean true - answered Yes, false - answered No
     */
    public static boolean confirmDialog(Class bundleLocation, String title, String query) {
        int response = JOptionPane.showOptionDialog(null, NbBundle.getMessage(bundleLocation, query), NbBundle.getMessage(bundleLocation, title), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

        if (response == JOptionPane.YES_OPTION) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * warningDialog - display a warning dialog
     *
     * @param bundleLocation location of string resources to display
     * @param title of dialog to display    
     * @param warning to display to the user
     */
     public static void warningDialog(Class bundleLocation, String title, String warning) {
        JOptionPane.showMessageDialog(null,
                NbBundle.getMessage(bundleLocation,warning),
                NbBundle.getMessage(bundleLocation,title),
                JOptionPane.WARNING_MESSAGE);
    }

    public static JComponent addContainerBorder(JComponent comp) {
        final LayoutStyle layoutStyle = LayoutStyle.getSharedInstance();

        JPanel panel = new JPanel();
        panel.add(comp);
        panel.setBorder(BorderFactory.createEmptyBorder(
                layoutStyle.getContainerGap(comp, SwingConstants.NORTH, null),
                layoutStyle.getContainerGap(comp, SwingConstants.WEST,  null),
                layoutStyle.getContainerGap(comp, SwingConstants.SOUTH, null),
                layoutStyle.getContainerGap(comp, SwingConstants.EAST,  null)));
        return panel;
    }

    /**
     * stripDoubleSlash - converts '\\' to '\' in path on Windows
     *
     * @param String path to convert
     * @return String converted path
     */
    public static String stripDoubleSlash(String path){
        if(Utilities.isWindows()){                       
            return path.replace("\\\\", "\\");
        }
        return path;
    }

    /**
     * Tells whether the given string is {@code null} or empty.
     * A string is considered empty if it consists only of spaces (and possibly
     * other spacing characters). The current implementation checks just for
     * spaces, future implementations may also check for other spacing
     * characters.
     *
     * @param  string to be verified or {@code null}
     * @return  {@code true} if the string is {@null} or empty,
     *          {@code false} otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        return (str == null) || (str.trim().length() == 0);
    }
    
    /**
     * fixIniFilePathsOnWindows - converts '\' to '\\' in paths in IniFile on Windows
     *
     * @param File iniFile to process
     * @return File processed tmpFile 
     */
    public static File fixPathsInIniFileOnWindows(File iniFile) {
        if(!Utilities.isWindows()) return null;
        
        File tmpFile = null;
        BufferedReader br = null;
        PrintWriter pw = null;

        try {
            if (iniFile == null || !iniFile.isFile() || !iniFile.canWrite()) {
                return null;
            }
            
            tmpFile = File.createTempFile(HgCommand.HG_COMMAND + "-", "tmp"); //NOI18N 

            if (tmpFile == null) {
                return null;
            }
            br = new BufferedReader(new FileReader(iniFile));
            pw = new PrintWriter(new FileWriter(tmpFile));

            String line = null;
            String stripLine = null;
            while ((line = br.readLine()) != null) {
                stripLine = line.replace("\\\\", "\\");
                pw.println(stripLine.replace("\\", "\\\\"));
                pw.flush();
            }
        } catch (IOException ex) {
            // Ignore
        } finally {
            try {
                if (pw != null) {
                    pw.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                // Ignore
            }
        }
        return tmpFile;
    }

    private static void resetIgnorePatterns(File file) {
        if (ignorePatterns == null) {
            return;
        }
        String key = file.getAbsolutePath();
        ignorePatterns.remove(key);
    }

    private static Set<Pattern> getIgnorePatterns(File file) {
        if (ignorePatterns == null) {
            ignorePatterns = new HashMap<String, Set<Pattern>>();
        }
        String key = file.getAbsolutePath();
        Set<Pattern> patterns = ignorePatterns.get(key);
        if (patterns == null) {
            patterns = new HashSet<Pattern>(5);
        }
        if (patterns.size() == 0) {
            addIgnorePatterns(patterns, file);
            ignorePatterns.put(key, patterns);
        }
	
        return patterns;
    }

    // cached not sharable files and folders
    private static final Map<File, Set<String>> notSharable = Collections.synchronizedMap(new HashMap<File, Set<String>>(5));
    private static void addNotSharable (File topFile, String ignoredPath) {
        synchronized (notSharable) {
            // get cached patterns
            Set ignores = notSharable.get(topFile);
            if (ignores == null) {
                ignores = new HashSet<String>();
            }
            String patternCandidate = ignoredPath;
            // test for duplicate patterns
            for (Iterator<String> it = ignores.iterator(); it.hasNext();) {
                String storedPattern = it.next();
                if (storedPattern.equals(ignoredPath) // already present
                        || ignoredPath.startsWith(storedPattern + '/')) { // path already ignored by its ancestor
                    patternCandidate = null;
                    break;
                } else if (storedPattern.startsWith(ignoredPath + '/')) { // stored pattern matches a subset of ignored path
                    // remove the stored pattern and add the ignored path
                    it.remove();
                }
            }
            if (patternCandidate != null) {
                ignores.add(patternCandidate);
            }
            notSharable.put(topFile, ignores);
        }
    }

    private static boolean isNotSharable (String path, File topFile) {
        boolean retval = false;
        Set<String> notSharablePaths = notSharable.get(topFile);
        if (notSharablePaths == null) {
            notSharablePaths = Collections.emptySet();
        }
        retval = notSharablePaths.contains(path);
        return retval;
    }

    /**
     * isIgnored - checks to see if this is a file Hg should ignore
     *
     * @param File file to check
     * @return boolean true - ignore, false - not ignored
     */
    public static boolean isIgnored(File file){
        return isIgnored(file, true);
    }

    public static boolean isIgnored(File file, boolean checkSharability){
        if (file == null) return false;
        String path = file.getPath();
        File topFile = Mercurial.getInstance().getRepositoryRoot(file);
        
        // We assume that the toplevel directory should not be ignored.
        if (topFile == null || topFile.equals(file)) {
            return false;
        }
        
        Set<Pattern> patterns = getIgnorePatterns(topFile);
        try {
        path = path.substring(topFile.getAbsolutePath().length() + 1);
        } catch(StringIndexOutOfBoundsException e) {
            throw e;
        }
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }

        for (Iterator i = patterns.iterator(); i.hasNext();) {
            Pattern pattern = (Pattern) i.next();
            if (pattern.matcher(path).find()) {
                return true;
            }
        }

        // check cached not sharable folders and files
        if (isNotSharable(path, topFile)) {
            return true;
        }

        // If a parent of the file matches a pattern ignore the file
        File parentFile = file.getParentFile();
        if (!parentFile.equals(topFile)) {
            if (isIgnored(parentFile, false)) return true;
        }

        if (FILENAME_HGIGNORE.equals(file.getName())) return false;
        if (checkSharability) {
            int sharability = SharabilityQuery.getSharability(FileUtil.normalizeFile(file));
            if (sharability == SharabilityQuery.NOT_SHARABLE) {
                addNotSharable(topFile, path);
                return true;
            }
        }
        return false;
    }

    /**
     * createIgnored - creates .hgignore file in the repository in which 
     * the given file belongs. This .hgignore file ensures Hg will ignore 
     * the files specified in HG_IGNORE_FILES list
     *
     * @param path to repository to place .hgignore file
     */
    public static void createIgnored(File path){
        if( path == null) return;
        BufferedWriter fileWriter = null;
        Mercurial hg = Mercurial.getInstance();
        File root = hg.getRepositoryRoot(path);
        if( root == null) return;
        File ignore = new File(root, FILENAME_HGIGNORE);
        
        try     {
            if (!ignore.exists()) {
                fileWriter = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(ignore)));
                for (String name : HG_IGNORE_FILES) {
                    fileWriter.write(name + "\n"); // NOI18N
                }
            }else{
                addToExistingIgnoredFile(ignore);
            }
        } catch (IOException ex) {
            Mercurial.LOG.log(Level.FINE, "createIgnored(): File {0} - {1}",  // NOI18N
                    new Object[] {ignore.getAbsolutePath(), ex.toString()});
        }finally {
            try {
                if(fileWriter != null) fileWriter.close();
                hg.getFileStatusCache().refresh(ignore);
            } catch (IOException ex) {
                Mercurial.LOG.log(Level.FINE, "createIgnored(): File {0} - {1}",  // NOI18N
                        new Object[] {ignore.getAbsolutePath(), ex.toString()});
            }
        }
    }
    
    private static int HG_NUM_PATTERNS_TO_CHECK = 5;
    private static void addToExistingIgnoredFile(File hgignoreFile) {
        if(hgignoreFile == null || !hgignoreFile.exists() || !hgignoreFile.canWrite()) return;
        File tempFile = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        boolean bOrigAnyPresent = false;
        boolean bOrigPresent = false;
        boolean bChgAnyPresent = false;
        boolean bRejAnyPresent = false;
        boolean bConflictAnyPresent = false;
        
        // If new patterns are added to HG_IGNORE_FILES, following code needs to
        // check for these new patterns
        assert( HG_IGNORE_FILES.length == HG_NUM_PATTERNS_TO_CHECK);
        
        try {
            tempFile = new File(hgignoreFile.getAbsolutePath() + ".tmp"); // NOI18N
            if (tempFile == null) return;
            
            br = new BufferedReader(new FileReader(hgignoreFile));
            pw = new PrintWriter(new FileWriter(tempFile));

            String line = null;            
            while ((line = br.readLine()) != null) {
                if(!bOrigAnyPresent && line.equals(HG_IGNORE_ORIG_ANY_FILES)){
                    bOrigAnyPresent = true;
                }else if (!bOrigPresent && line.equals(HG_IGNORE_ORIG_FILES)){
                    bOrigPresent = true;
                }else if (!bChgAnyPresent && line.equals(HG_IGNORE_CHG_ANY_FILES)){
                    bChgAnyPresent = true;
                }else if (!bRejAnyPresent && line.equals(HG_IGNORE_REJ_ANY_FILES)){
                    bRejAnyPresent = true;
                }else if (!bConflictAnyPresent && line.equals(HG_IGNORE_CONFLICT_ANY_FILES)){
                    bConflictAnyPresent = true;
                }
                pw.println(line);
                pw.flush();
            }
            // If not found add as required
            if (!bOrigAnyPresent) {
                pw.println(HG_IGNORE_ORIG_ANY_FILES );
                pw.flush();
            }
            if (!bOrigPresent) {
                pw.println(HG_IGNORE_ORIG_FILES );
                pw.flush();
            }
            if (!bChgAnyPresent) {
                pw.println(HG_IGNORE_CHG_ANY_FILES );
                pw.flush();
            }
            if (!bRejAnyPresent) {
                pw.println(HG_IGNORE_REJ_ANY_FILES );
                pw.flush();
            }     
            if (!bConflictAnyPresent) {
                pw.println(HG_IGNORE_CONFLICT_ANY_FILES );
                pw.flush();
            }     
            
        } catch (IOException ex) {
            // Ignore
        } finally {
            try {
                if(pw != null) pw.close();
                if(br != null) br.close();

                boolean bAnyAdditions = !bOrigAnyPresent || !bOrigPresent  || 
                        !bChgAnyPresent || !bRejAnyPresent || !bConflictAnyPresent;               
                if(bAnyAdditions){
                    if (!confirmDialog(HgUtils.class, "MSG_IGNORE_FILES_TITLE", "MSG_IGNORE_FILES")) { // NOI18N 
                        tempFile.delete();
                        return;
                    }
                    if(tempFile != null && tempFile.isFile() && tempFile.canWrite() && hgignoreFile != null){ 
                        hgignoreFile.delete();
                        tempFile.renameTo(hgignoreFile);
                    }
                }else{
                    tempFile.delete();
                }
            } catch (IOException ex) {
            // Ignore
            }
        }
    }

    private static void addIgnorePatterns(Set<Pattern> patterns, File file) {
        Set<String> shPatterns;
        try {
            shPatterns = readIgnoreEntries(file, true);
        } catch (IOException e) {
            // ignore invalid entries
            return;
        }
        for (Iterator i = shPatterns.iterator(); i.hasNext();) {
            String shPattern = (String) i.next();
            if ("!".equals(shPattern)) { // NOI18N
                patterns.clear();
            } else {
                try {
                    patterns.add(Pattern.compile(shPattern));
                } catch (Exception e) {
                    // unsupported pattern
                }
            }
        }
    }

    /**
     * Removes parts of the pattern denoting commentaries
     * @param line initial pattern
     * @return pattern with comments removed.
     */
    private static String removeCommentsInIgnore(String line) {
        int indexOfHash = -1;
        boolean cont;
        do {
            cont = false;
            indexOfHash = line.indexOf("#", indexOfHash);   // NOI18N
            // do not consider \# as a comment, skip that character and try to find the next comment
            if (indexOfHash > 0 && line.charAt(indexOfHash - 1) == '\\') {   // NOI18N
                ++indexOfHash;
                cont = true;
            }
        } while (cont);
        if (indexOfHash != -1) {
            if (indexOfHash == 0) {
                line = "";
            } else {
                line = line.substring(0, indexOfHash).trim();
            }
        }

        return line;
    }

    private static Boolean ignoreContainsSyntax(File directory) throws IOException {
        File hgIgnore = new File(directory, FILENAME_HGIGNORE);
        Boolean val = false;

        if (!hgIgnore.canRead()) return val;

        String s;
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(hgIgnore));
            while ((s = r.readLine()) != null) {
                String line = s.trim();
                line = removeCommentsInIgnore(line);
                if (line.length() == 0) continue;
                String [] array = line.split(" ");
                if (array[0].equals("syntax:")) {
                    val = true;
                    break;
                }
            }
        } finally {
            if (r != null) try { r.close(); } catch (IOException e) {}
        }
        return val;
    }

    /**
     * @param transformEntries if set to false, this method returns the exact .hgignore file's content as a set of lines. 
     * If set to true, the method will parse the output, remove all comments, process globa and regexp syntax, etc. So if you just want to 
     * add or remove a certain line in the file, set the parameter to false.
     */
    private static Set<String> readIgnoreEntries(File directory, boolean transformEntries) throws IOException {
        File hgIgnore = new File(directory, FILENAME_HGIGNORE);

        Set<String> entries = new LinkedHashSet<String>(5);
        if (!hgIgnore.canRead()) return entries;

        String s;
        BufferedReader r = null;
        boolean glob = false;
        try {
            r = new BufferedReader(new FileReader(hgIgnore));
            while ((s = r.readLine()) != null) {
                String line = s;
                if (transformEntries) {
                    line = line.trim();
                    line = removeCommentsInIgnore(line);
                    if (line.length() == 0) continue;
                    String [] array = line.split(" ");                  //NOI18N
                    if (array[0].equals(IGNORE_SYNTAX_PREFIX)) {
                        String syntax = line.substring(IGNORE_SYNTAX_PREFIX.length()).trim();
                        if (IGNORE_SYNTAX_GLOB.equals(syntax)) {
                            glob = true;
                        } else if (IGNORE_SYNTAX_REGEXP.equals(syntax)) {
                            glob = false;
                        }
                        continue;
                    }
                }
                entries.add(glob ? transformFromGlobPattern(line) : line);
            }
        } finally {
            if (r != null) try { r.close(); } catch (IOException e) {}
        }
        return entries;
    }

    private static String transformFromGlobPattern (String pattern) {
        // returned pattern consists of two patterns - one for a file/folder directly under
        pattern = pattern.replace("$", "\\$").replace("^", "\\^").replace(".", "\\.").replace("*", ".*") + '$'; //NOI18N
        return  "^" + pattern // a folder/file directly under the repository, does not start with '/' //NOI18N
                + "|"                                                   //NOI18N
                + ".*/" + pattern; // a folder in 2nd+ depth            //NOI18N
    }

    private static String computePatternToIgnore(File directory, File file) {
        String name = "^" + file.getAbsolutePath().substring(directory.getAbsolutePath().length()+1) + "$"; //NOI18N
        // # should be escaped, otherwise works as a comment
        // . should be escaped, otherwise works as a special char in regexp
        return name.replace(File.separatorChar, '/').replace("#", "\\#").replace(".", "\\."); //NOI18N
    }

    private static void writeIgnoreEntries(File directory, Set entries) throws IOException {
        File hgIgnore = new File(directory, FILENAME_HGIGNORE);
        FileObject fo = FileUtil.toFileObject(hgIgnore);

        if (entries.size() == 0) {
            if (fo != null) fo.delete();
            return;
        }

        if (fo == null || !fo.isValid()) {
            fo = FileUtil.toFileObject(directory);
            fo = fo.createData(FILENAME_HGIGNORE);
        }
        FileLock lock = fo.lock();
        PrintWriter w = null;
        try {
            w = new PrintWriter(fo.getOutputStream(lock));
            for (Iterator i = entries.iterator(); i.hasNext();) {
                w.println(i.next());
            }
        } finally {
            lock.releaseLock();
            if (w != null) w.close();
            resetIgnorePatterns(directory);
        }
    }

    /**
     * addIgnored - Add the specified files to the .hgignore file in the 
     * specified repository.
     *
     * @param directory for repository for .hgignore file
     * @param files an array of Files to be added
     */
    public static void addIgnored(File directory, File[] files) throws IOException {
        if (ignoreContainsSyntax(directory)) {
            warningDialog(HgUtils.class, "MSG_UNABLE_TO_IGNORE_TITLE", "MSG_UNABLE_TO_IGNORE");
            return;
        }
        Set<String> entries = readIgnoreEntries(directory, false);
        for (File file: files) {
            String patterntoIgnore = computePatternToIgnore(directory, file);
            entries.add(patterntoIgnore);
        }
        writeIgnoreEntries(directory, entries);
    }

    /**
     * removeIgnored - Remove the specified files from the .hgignore file in 
     * the specified repository.
     *
     * @param directory for repository for .hgignore file
     * @param files an array of Files to be removed
     */
    public static void removeIgnored(File directory, File[] files) throws IOException {
        if (ignoreContainsSyntax(directory)) {
            warningDialog(HgUtils.class, "MSG_UNABLE_TO_UNIGNORE_TITLE", "MSG_UNABLE_TO_UNIGNORE");
            return;
        }
        Set entries = readIgnoreEntries(directory, false);
        for (File file: files) {
            String patterntoIgnore = computePatternToIgnore(directory, file);
            entries.remove(patterntoIgnore);
        }
        writeIgnoreEntries(directory, entries);
    }

    /**
     * Semantics is similar to {@link org.openide.windows.TopComponent#getActivatedNodes()} except that this
     * method returns File objects instead of Nodes. Every node is examined for Files it represents. File and Folder
     * nodes represent their underlying files or folders. Project nodes are represented by their source groups. Other
     * logical nodes must provide FileObjects in their Lookup.
     *
     * @param nodes null (then taken from windowsystem, it may be wrong on editor tabs #66700).
     * @param includingFileStatus if any activated file does not have this CVS status, an empty array is returned
     * @param includingFolderStatus if any activated folder does not have this CVS status, an empty array is returned
     * @return File [] array of activated files, or an empty array if any of examined files/folders does not have given status
     */
    public static VCSContext getCurrentContext(Node[] nodes, int includingFileStatus, int includingFolderStatus) {
        VCSContext context = getCurrentContext(nodes);
        FileStatusCache cache = Mercurial.getInstance().getFileStatusCache();
        for (File file : context.getRootFiles()) {
            FileInformation fi = cache.getStatus(file);
            if (file.isDirectory()) {
                if ((fi.getStatus() & includingFolderStatus) == 0) return VCSContext.EMPTY;
            } else {
                if ((fi.getStatus() & includingFileStatus) == 0) return VCSContext.EMPTY;
            }
        }
        return context;
    }

    /**
     * Semantics is similar to {@link org.openide.windows.TopComponent#getActiva
tedNodes()} except that this
     * method returns File objects instead of Nodes. Every node is examined for
Files it represents. File and Folder
     * nodes represent their underlying files or folders. Project nodes are repr
esented by their source groups. Other
     * logical nodes must provide FileObjects in their Lookup.
     *
     * @return File [] array of activated files
     * @param nodes or null (then taken from windowsystem, it may be wrong on ed
itor tabs #66700).
     */

    public static VCSContext getCurrentContext(Node[] nodes) {
        if (nodes == null) {
            nodes = TopComponent.getRegistry().getActivatedNodes();
        }
        return VCSContext.forNodes(nodes);
    }

   /**
     * Returns path to repository root or null if not managed
     *
     * @param VCSContext
     * @return String of repository root path
     */
    public static String getRootPath(VCSContext context){
        File root = getRootFile(context);
        return (root == null) ? null: root.getAbsolutePath();
    }
    
   /**
     * Determines if the given context contains at least one root file from
     * a mercurial repository
     *
     * @param VCSContext
     * @return true if the given conetxt contains a root file from a hg repository
     */
    public static boolean isFromHgRepository(VCSContext context){
        return getRootFile(context) != null;
    }

   /**
     * Returns path to repository root or null if not managed
     *
     * @param VCSContext
     * @return String of repository root path
     */
    public static File getRootFile(VCSContext context){
        if (context == null) return null;
        Mercurial hg = Mercurial.getInstance();
        File [] files = context.getRootFiles().toArray(new File[context.getRootFiles().size()]);
        if (files == null || files.length == 0) return null;
        
        File root = hg.getRepositoryRoot(files[0]);
        return root;
    }

   /**
     * Returns repository roots for all root files from context
     *
     * @param VCSContext
     * @return repository roots
     */
    public static Set<File> getRepositoryRoots(VCSContext context) {
        Set<File> rootsSet = context.getRootFiles();
        return getRepositoryRoots(rootsSet);
    }

    /**
     * Returns repository roots for all root files from context
     *
     * @param roots root files
     * @return repository roots
     */
    public static Set<File> getRepositoryRoots (Set<File> roots) {
        Set<File> ret = new HashSet<File>();

        // filter managed roots
        for (File file : roots) {
            if(Mercurial.getInstance().isManaged(file)) {
                File repoRoot = Mercurial.getInstance().getRepositoryRoot(file);
                if(repoRoot != null) {
                    ret.add(repoRoot);
                }
            }
        }
        return ret;
    }

    /**
     *
     * @param ctx
     * @return
     */
    public static File[] getActionRoots(VCSContext ctx) {
        Set<File> rootsSet = ctx.getRootFiles();
        Map<File, List<File>> map = new HashMap<File, List<File>>();

        // filter managed roots
        for (File file : rootsSet) {
            if(Mercurial.getInstance().isManaged(file)) {
                File repoRoot = Mercurial.getInstance().getRepositoryRoot(file);
                if(repoRoot != null) {
                    List<File> l = map.get(repoRoot);
                    if(l == null) {
                        l = new LinkedList<File>();
                        map.put(repoRoot, l);
                    }
                    l.add(file);
                }
            }
        }

        Set<File> repoRoots = map.keySet();
        if(map.size() > 1) {
            // more than one managed root => need a dlg
            FileSelector fs = new FileSelector(
                    NbBundle.getMessage(HgUtils.class, "LBL_FileSelector_Title"),
                    NbBundle.getMessage(HgUtils.class, "FileSelector.jLabel1.text"),
                    new HelpCtx("org.netbeans.modules.mercurial.FileSelector"),
                    HgModuleConfig.getDefault().getPreferences());
            if(fs.show(repoRoots.toArray(new File[repoRoots.size()]))) {
                File selection = fs.getSelectedFile();
                List<File> l = map.get(selection);
                return l.toArray(new File[l.size()]);
            } else {
                return null;
            }
        } else {
            List<File> l = map.get(map.keySet().iterator().next());
            return l.toArray(new File[l.size()]);
        }
    }

    /**
     * Returns only those root files from the given context which belong to repository
     * @param ctx
     * @param repository
     * @param rootFiles
     * @return
     */
    public static File[] filterForRepository(final VCSContext ctx, final File repository, boolean rootFiles) {
        File[] files = null;
        if(ctx != null) {
            Set<File> s = rootFiles ? ctx.getRootFiles() : ctx.getFiles();
            files = s.toArray(new File[s.size()]);
        }
        if (files != null) {
            List<File> l = new LinkedList<File>();
            for (File file : files) {
                File r = Mercurial.getInstance().getRepositoryRoot(file);
                if (r != null && r.equals(repository)) {
                    l.add(file);
                }
            }
            files = l.toArray(new File[l.size()]);
        }
        return files;
    }

    /**
     * Returns root files sorted per their repository roots
     * @param ctx
     * @param rootFiles
     * @return
     */
    public static Map<File, Set<File>> sortUnderRepository (final VCSContext ctx, boolean rootFiles) {
        Set<File> files = null;
        if(ctx != null) {
            files = rootFiles ? ctx.getRootFiles() : ctx.getFiles();
        }
        Map<File, Set<File>> sortedRoots = null;
        if (files != null) {
            sortedRoots = new HashMap<File, Set<File>>();
            for (File file : files) {
                File r = Mercurial.getInstance().getRepositoryRoot(file);
                Set<File> repositoryRoots = sortedRoots.get(r);
                if (repositoryRoots == null) {
                    repositoryRoots = new HashSet<File>();
                    sortedRoots.put(r, repositoryRoots);
                }
                repositoryRoots.add(file);
            }
        }
        return sortedRoots == null ? Collections.<File, Set<File>>emptyMap() : sortedRoots;
    }

   /**
     * Returns File object for Project Directory
     *
     * @param VCSContext
     * @return File object of Project Directory
     */
    public static File getProjectFile(VCSContext context){
        return getProjectFile(getProject(context));
    }

    public static Project getProject(VCSContext context){
        if (context == null) return null;
        File [] files = context.getRootFiles().toArray(new File[context.getRootFiles().size()]);

        for (File file : files) {
            /* We may be committing a LocallyDeleted file */
            if (!file.exists()) file = file.getParentFile();
            FileObject fo = FileUtil.toFileObject(file);
            if(fo == null) {
                Mercurial.LOG.log(Level.FINE, "HgUtils.getProjectFile(): No FileObject for {0}", file); // NOI18N
            } else {
                Project p = FileOwnerQuery.getOwner(fo);
                if (p != null) {
                    return p;
                } else {
                    Mercurial.LOG.log(Level.FINE, "HgUtils.getProjectFile(): No project for {0}", file); // NOI18N
                }
            }
        }
        return null;
    }
    
    public static File getProjectFile(Project project){
        if (project == null) return null;

        FileObject fo = project.getProjectDirectory();
        return  FileUtil.toFile(fo);
    }

    public static File[] getProjectRootFiles(Project project){
        if (project == null) return null;
        Set<File> set = new HashSet<File>();

        Sources sources = ProjectUtils.getSources(project);
        SourceGroup [] sourceGroups = sources.getSourceGroups(Sources.TYPE_GENERIC);
        for (int j = 0; j < sourceGroups.length; j++) {
            SourceGroup sourceGroup = sourceGroups[j];
            FileObject srcRootFo = sourceGroup.getRootFolder();
            File rootFile = FileUtil.toFile(srcRootFo);
            set.add(rootFile);
        }
        return set.toArray(new File[set.size()]);
    }

    /**
     * Checks file location to see if it is part of mercurial metdata
     *
     * @param file file to check
     * @return true if the file or folder is a part of mercurial metadata, false otherwise
     */
    public static boolean isPartOfMercurialMetadata(File file) {
        return metadataPattern.matcher(file.getAbsolutePath()).matches();
    }
    

    /**
     * Forces refresh of Status for the given directory 
     * If a repository root is passed as the parameter, the cache will be refreshed only for already seen or open folders.
     * @param start file or dir to begin refresh from
     * @return void
     */
    public static void forceStatusRefresh(File file) {
        if (isAdministrative(file)) return;
        FileStatusCache cache = Mercurial.getInstance().getFileStatusCache();
        File repositoryRoot = Mercurial.getInstance().getRepositoryRoot(file);
        if (file.equals(repositoryRoot)) {
            // do not scan the whole repository, only open folders, projects etc. should be enough
            cache.refreshAllRoots(Collections.singletonMap(repositoryRoot, Mercurial.getInstance().getSeenRoots(repositoryRoot)));
        } else {
            cache.refresh(file);
        }
    }

    /**
     * Forces refresh of Status for the project of the specified context
     *
     * @param VCSContext ctx whose project is be updated.
     * @return void
     */
    public static void forceStatusRefreshProject(VCSContext context) {
        // XXX and what if there is more then one project in the ctx?!
        Project project = getProject(context);
        if (project == null) return;
        File[] files = getProjectRootFiles(project);
        for (int j = 0; j < files.length; j++) {
            forceStatusRefresh(files[j]);
        }
    }

    /**
     * Tests parent/child relationship of files.
     *
     * @param parent file to be parent of the second parameter
     * @param file file to be a child of the first parameter
     * @return true if the second parameter represents the same file as the first parameter OR is its descendant (child)
     */
    public static boolean isParentOrEqual(File parent, File file) {
        for (; file != null; file = file.getParentFile()) {
            if (file.equals(parent)) return true;
        }
        return false;
    }
    
    /**
     * Returns path of file relative to root repository or a warning message
     * if the file is not under the repository root.
     *
     * @param File to get relative path from the repository root
     * @return String of relative path of the file from teh repository root
     */
    public static String getRelativePath(File file) {
            if (file == null){
                return NbBundle.getMessage(SyncFileNode.class, "LBL_Location_NotInRepository"); // NOI18N
            }
            String shortPath = file.getAbsolutePath();
            if (shortPath == null){
                return NbBundle.getMessage(SyncFileNode.class, "LBL_Location_NotInRepository"); // NOI18N
            }
            
            Mercurial mercurial = Mercurial.getInstance();
            File rootManagedFolder = mercurial.getRepositoryRoot(file);
            if ( rootManagedFolder == null){
                return NbBundle.getMessage(SyncFileNode.class, "LBL_Location_NotInRepository"); // NOI18N
            }
            
            String root = rootManagedFolder.getAbsolutePath();
            if(shortPath.startsWith(root)) {
                return shortPath.substring(root.length()+1);
            }else{
                return NbBundle.getMessage(SyncFileNode.class, "LBL_Location_NotInRepository"); // NOI18N
            }
     }

    /**
     * Normalize flat files, Mercurial treats folder as normal file
     * so it's necessary explicitly list direct descendants to
     * get classical flat behaviour.
     * <strong>Does not return up-to-date files</strong>
     *
     * <p> E.g. revert on package node means:
     * <ul>
     *   <li>revert package folder properties AND
     *   <li>revert all modified (including deleted) files in the folder
     * </ul>
     *
     * @return files with given status and direct descendants with given status.
     */

    public static File[] flatten(File[] files, int status) {
        LinkedList<File> ret = new LinkedList<File>();

        FileStatusCache cache = Mercurial.getInstance().getFileStatusCache();
        for (int i = 0; i<files.length; i++) {
            File dir = files[i];
            FileInformation info = cache.getStatus(dir);
            if ((status & info.getStatus()) != 0) {
                ret.add(dir);
            }
            File[] entries = cache.listFiles(dir);  // comparing to dir.listFiles() lists already deleted too
            for (int e = 0; e<entries.length; e++) {
                File entry = entries[e];
                info = cache.getStatus(entry);
                if ((status & info.getStatus()) != 0) {
                    ret.add(entry);
                }
            }
        }

        return ret.toArray(new File[ret.size()]);
    }

    /**
     * Utility method that returns all non-excluded modified files that are
     * under given roots (folders) and have one of specified statuses.
     *
     * @param context context to search
     * @param includeStatus bit mask of file statuses to include in result
     * @param testCommitExclusions if set to true then returned files will not contain those excluded from commit
     * @return File [] array of Files having specified status
     */
    public static File [] getModifiedFiles(VCSContext context, int includeStatus, boolean testCommitExclusions) {
        File[] all = Mercurial.getInstance().getFileStatusCache().listFiles(context, includeStatus);
        List<File> files = new ArrayList<File>();
        for (int i = 0; i < all.length; i++) {
            File file = all[i];
            if (!testCommitExclusions || !HgModuleConfig.getDefault().isExcludedFromCommit(file.getAbsolutePath())) {
                files.add(file);
            }
        }

        // ensure that command roots (files that were explicitly selected by user) are included in Diff
        FileStatusCache cache = Mercurial.getInstance().getFileStatusCache();
        for (File file : context.getRootFiles()) {
            if (file.isFile() && (cache.getStatus(file).getStatus() & includeStatus) != 0 && !files.contains(file)) {
                files.add(file);
            }
        }
        return files.toArray(new File[files.size()]);
    }

    /**
     * Checks if the file is binary.
     *
     * @param file file to check
     * @return true if the file cannot be edited in NetBeans text editor, false otherwise
     */
    public static boolean isFileContentBinary(File file) {
        FileObject fo = FileUtil.toFileObject(file);
        if (fo == null) return false;
        try {
            DataObject dao = DataObject.find(fo);
            return dao.getCookie(EditorCookie.class) == null;
        } catch (DataObjectNotFoundException e) {
            // not found, continue
        }
        return false;
    }

    /**
     * @return true if the buffer is almost certainly binary.
     * Note: Non-ASCII based encoding encoded text is binary,
     * newlines cannot be reliably detected.
     */
    public static boolean isBinary(byte[] buffer) {
        for (int i = 0; i<buffer.length; i++) {
            int ch = buffer[i];
            if (ch < 32 && ch != '\t' && ch != '\n' && ch != '\r') {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a number of 100ms-lasting waiting loops in a repository validity check.
     * If a sysprop defined in HgUtils.HG_CHECK_REPOSITORY_TIMEOUT_SWITCH is set, this returns a number count from HG_CHECK_REPOSITORY_TIMEOUT_SWITCH, otherwise it uses
     * a defaut value HgUtils.HG_CHECK_REPOSITORY_DEFAULT_ROUNDS.
     * @return number of rounds
     */
    public static int getNumberOfRoundsForRepositoryValidityCheck() {
        if (repositoryValidityCheckRounds <= 0) {
            try {
                repositoryValidityCheckRounds = Integer.parseInt(System.getProperty(HG_CHECK_REPOSITORY_TIMEOUT_SWITCH, HG_CHECK_REPOSITORY_DEFAULT_TIMEOUT)) * 10; // number of 100ms lasting rounds
            } catch (NumberFormatException ex) {
                Mercurial.LOG.log(Level.INFO, "Parsing integer failed, default value will be used", ex);
            }
            if (repositoryValidityCheckRounds <= 0) {
                Mercurial.LOG.fine("Using default value for number of rounds in repository validity check: " + HG_CHECK_REPOSITORY_DEFAULT_ROUNDS);
                repositoryValidityCheckRounds = HG_CHECK_REPOSITORY_DEFAULT_ROUNDS;
            }
        }
        return repositoryValidityCheckRounds;
    }

    /**
     * Returns true if hg in a given version supports 'hg resolve' command
     * Resolve command was introduced in 1.1
     * @param version
     * @return
     */
    public static boolean hasResolveCommand(String version) {
        if (version != null && (!version.startsWith("0.")               //NOI18N
                || !version.startsWith("1.0"))) {                       //NOI18N
            return true;
        }

        return false;
    }

    /**
     * Returns true if hg in a given version supports '--topo' option
     * --topo available probably since 1.5
     * @param version
     * @return
     */
    public static boolean hasTopoOption (String version) {
        if (version != null && !version.startsWith("0.") //NOI18N
                && !version.startsWith("1.0") //NOI18N
                && !version.startsWith("1.1") //NOI18N
                && !version.startsWith("1.2") //NOI18N
                && !version.startsWith("1.3") //NOI18N
                && !version.startsWith("1.4")) { //NOI18N
            return true;
        }

        return false;
    }

    /**
     * Returns the remote repository url for the given file.</br>
     * It will be the pull url in the first case, otherwise push url or null
     * in case there is nothig set in .hg
     *
     * @param file
     * @return
     */
    public static String getRemoteRepository(File file) {
        if(file == null) return null;
        String remotePath = HgRepositoryContextCache.getInstance().getPullDefault(file);
        if (remotePath == null || remotePath.trim().isEmpty()) {
            Mercurial.LOG.log(Level.FINE, "No default pull available for managed file : [{0}]", file);
            remotePath = HgRepositoryContextCache.getInstance().getPushDefault(file);
            if (remotePath == null || remotePath.trim().isEmpty()) {
                Mercurial.LOG.log(Level.FINE, "No default pull or push available for managed file : [{0}]", file);
            }
        }
        if(remotePath != null) {
            remotePath = remotePath.trim();
            remotePath = HgUtils.removeHttpCredentials(remotePath);
            if (remotePath.isEmpty()) {
                // return null if empty
                remotePath = null;
            }
        }
        return remotePath;
    }

    public static void openInRevision (final File originalFile, final String revision, boolean showAnnotations) throws IOException {
        File file = org.netbeans.modules.mercurial.VersionsCache.getInstance().getFileRevision(originalFile, revision);

        if (file == null) { // can be null if the file does not exist or is empty in the given revision
            file = File.createTempFile("tmp", "-" + originalFile.getName()); //NOI18N
            file.deleteOnExit();
        }

        final FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(file));
        EditorCookie ec = null;
        org.openide.cookies.OpenCookie oc = null;
        try {
            DataObject dobj = DataObject.find(fo);
            ec = dobj.getCookie(EditorCookie.class);
            oc = dobj.getCookie(org.openide.cookies.OpenCookie.class);
        } catch (DataObjectNotFoundException ex) {
            Mercurial.LOG.log(Level.FINE, null, ex);
        }
        org.openide.text.CloneableEditorSupport ces = null;
        if (ec == null && oc != null) {
            oc.open();
        } else {
            ces = org.netbeans.modules.versioning.util.Utils.openFile(fo, revision);
        }
        if (showAnnotations) {
            if (ces == null) {
                return;
            } else {
                final org.openide.text.CloneableEditorSupport support = ces;
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        javax.swing.JEditorPane[] panes = support.getOpenedPanes();
                        if (panes != null) {
                            org.netbeans.modules.mercurial.ui.annotate.AnnotateAction.showAnnotations(panes[0], originalFile, revision);
                        }
                    }
                });
            }
        }
    }

    /**
     * Compares two {@link FileInformation} objects by importance of statuses they represent.
     */
    public static class ByImportanceComparator<T> implements Comparator<FileInformation> {
        public int compare(FileInformation i1, FileInformation i2) {
            return getComparableStatus(i1.getStatus()) - getComparableStatus(i2.getStatus());
        }
    }

    /**
     * Gets integer status that can be used in comparators. The more important the status is for the user,
     * the lower value it has. Conflict is 0, unknown status is 100.
     *
     * @return status constant suitable for 'by importance' comparators
     */
    public static int getComparableStatus(int status) {
        if (0 != (status & FileInformation.STATUS_VERSIONED_CONFLICT)) {
            return 0;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_MERGE)) {
            return 1;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_DELETEDLOCALLY)) {
            return 10;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY)) {
            return 11;
       } else if (0 != (status & FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY)) {
            return 12;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_ADDEDLOCALLY)) {
            return 13;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_MODIFIEDLOCALLY)) {
            return 14;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_REMOVEDINREPOSITORY)) {
            return 30;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_NEWINREPOSITORY)) {
            return 31;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_MODIFIEDINREPOSITORY)) {
            return 32;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_UPTODATE)) {
            return 50;
        } else if (0 != (status & FileInformation.STATUS_NOTVERSIONED_EXCLUDED)){
            return 100;
        } else if (0 != (status & FileInformation.STATUS_NOTVERSIONED_NOTMANAGED)) {
            return 101;
        } else if (status == FileInformation.STATUS_UNKNOWN) {
            return 102;
        } else {
            throw new IllegalArgumentException("Uncomparable status: " + status); // NOI18N
        }
    }

    /**
     * Rips an eventual username off - e.g. user@svn.host.org
     *
     * @param host - hostname with a userneame
     * @return host - hostname without the username
     */
    public static String ripUserFromHost(String host) {
        int idx = host.indexOf('@');
        if(idx < 0) {
            return host;
        } else {
            return host.substring(idx + 1);
        }
    }

    /**
     * Uses content analysis for unversioned files.
     *
     * @param file file to examine
     * @return String mime type of the file (or best guess)
     */
    public static String getMimeType(File file) {
        FileObject fo = FileUtil.toFileObject(file);
        String foMime;
        if (fo == null) {
            foMime = "content/unknown";
        } else {
            foMime = fo.getMIMEType();
        }
        if(foMime.startsWith("text")) {
            return foMime;
        }
        return Utils.isFileContentText(file) ? "text/plain" : "application/octet-stream";
    }

    public static void logHgLog(HgLogMessage log, OutputLogger logger) {
        String lbChangeset = NbBundle.getMessage(HgUtils.class, "LB_CHANGESET");   // NOI18N
        String lbUser =      NbBundle.getMessage(HgUtils.class, "LB_AUTHOR");      // NOI18N
        String lbDate =      NbBundle.getMessage(HgUtils.class, "LB_DATE");        // NOI18N
        String lbSummary =   NbBundle.getMessage(HgUtils.class, "LB_SUMMARY");     // NOI18N
        int l = 0;
        for (String s : new String[] {lbChangeset, lbUser, lbDate, lbSummary}) {
            if(l < s.length()) l = s.length();
        }
        StringBuffer sb = new StringBuffer();
        sb.append(formatlabel(lbChangeset, l));
        sb.append(log.getRevision());
        sb.append(":"); // NOI18N
        sb.append(log.getCSetShortID());
        sb.append('\n'); // NOI18N
        sb.append(formatlabel(lbUser, l));
        sb.append(log.getAuthor());
        sb.append('\n'); // NOI18N
        sb.append(formatlabel(lbDate, l));
        sb.append(log.getDate());
        sb.append('\n'); // NOI18N
        sb.append(formatlabel(lbSummary, l));
        sb.append(log.getMessage());
        sb.append('\n'); // NOI18N

        logger.output(sb.toString());
    }


    private static String formatlabel(String label, int l) {
        label = label + spaces(l - label.length()) + ": ";
        return label;
    }

    private static String spaces(int l) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < l + 3; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * This utility class should not be instantiated anywhere.
     */
    private HgUtils() {
    }

    private static Logger TY9_LOG = null;
    public static void logT9Y(String msg) {
        if(TY9_LOG == null) TY9_LOG = Logger.getLogger("org.netbeans.modules.mercurial.t9y");
        TY9_LOG.log(Level.FINEST, msg);
    }

    /**
     * Validates annotation format text
     * @param format format to be validatet
     * @return <code>true</code> if the format is correct, <code>false</code> otherwise.
     */
    public static boolean isAnnotationFormatValid(String format) {
        boolean retval = true;
        if (format != null) {
            try {
                new MessageFormat(format);
            } catch (IllegalArgumentException ex) {
                Mercurial.LOG.log(Level.FINER, "Bad user input - annotation format", ex);
                retval = false;
            }
        }
        return retval;
    }

    /**
     * Tests <tt>.hg</tt> directory itself.
     */
    public static boolean isAdministrative(File file) {
        String name = file.getName();
        return isAdministrative(name) && file.isDirectory();
    }

    public static boolean isAdministrative(String fileName) {
        return fileName.equals(".hg"); // NOI18N
    }

    public static boolean hgExistsFor(File file) {
        return new File(file, ".hg").exists();
    }

    public static CommitOptions[] createDefaultCommitOptions (HgFileNode[] nodes, boolean excludeNew) {
        CommitOptions[] commitOptions = new CommitOptions[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            HgFileNode node = nodes[i];
            File file = node.getFile();
            if (HgModuleConfig.getDefault().isExcludedFromCommit(file.getAbsolutePath())) {
                commitOptions[i] = CommitOptions.EXCLUDE;
            } else {
                switch (node.getInformation().getStatus()) {
                case FileInformation.STATUS_VERSIONED_DELETEDLOCALLY:
                case FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY:
                    commitOptions[i] = CommitOptions.COMMIT_REMOVE;
                    break;
                case FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY:
                    commitOptions[i] = excludeNew ? CommitOptions.EXCLUDE : CommitOptions.COMMIT;
                    break;
                default:
                    commitOptions[i] = CommitOptions.COMMIT;
                }
            }
        }
        return commitOptions;
    }

    /**
     * Returns the administrative hg folder for the given repository and normalizes the file
     * @param repositoryRoot root of the repository
     * @return administrative hg folder
     */
    public static File getHgFolderForRoot (File repositoryRoot) {
        return FileUtil.normalizeFile(new File(repositoryRoot, HG_FOLDER_NAME));
    }



    /**
     * Adds the given file into filesUnderRoot:
     * <ul>
     * <li>if the file was already in the set, does nothing and returns true</li>
     * <li>if the file lies under a folder already present in the set, does nothing and returns true</li>
     * <li>if the file and none of it's ancestors is not in the set yet, this adds the file into the set,
     * removes all it's children from the set and returns false</li>
     * @param repository repository root
     * @param filesUnderRoot set of repository roots
     * @param file file to add
     * @return true if the file or any of it's ancestors was originally in the set
     */
    public static boolean prepareRootFiles(File repository, Set<File> filesUnderRoot, File file) {
        boolean alreadyAdded = false;
        boolean added = false;
        for (File fileUnderRoot : filesUnderRoot) {
            // try to find a common parent for planned files
            File childCandidate = file;
            File ancestorCandidate = fileUnderRoot;
            added = true;
            if (childCandidate.equals(ancestorCandidate) || ancestorCandidate.equals(repository)) {
                // file has already been inserted or scan is planned for the whole repository root
                alreadyAdded = true;
                break;
            }
            if (childCandidate.equals(repository)) {
                // plan the scan for the whole repository root
                ancestorCandidate = childCandidate;
            } else {
                if (file.getAbsolutePath().length() < fileUnderRoot.getAbsolutePath().length()) {
                    // ancestor's path is too short to be the child's parent
                    ancestorCandidate = file;
                    childCandidate = fileUnderRoot;
                }
                if (!Utils.isAncestorOrEqual(ancestorCandidate, childCandidate)) {
                    ancestorCandidate = Utils.getCommonParent(childCandidate, ancestorCandidate);
                }
            }
            if (ancestorCandidate == fileUnderRoot) {
                // already added
                alreadyAdded = true;
                break;
            } else if (!FileStatusCache.FULL_REPO_SCAN_ENABLED && ancestorCandidate != childCandidate && ancestorCandidate.equals(repository)) {
                // common ancestor is the repo root and neither one of the candidates was originally the repo root
                // do not scan the whole clone, it might be a performance killer
                added = false;
            } else if (ancestorCandidate != null) {
                // file is under the repository root
                if (ancestorCandidate.equals(repository)) {
                    // adding the repository, there's no need to leave all other files
                    filesUnderRoot.clear();
                } else {
                    filesUnderRoot.remove(fileUnderRoot);
                }
                filesUnderRoot.add(ancestorCandidate);
                break;
            } else {
                added = false;
            }
        }
        if (!added) {
            // not added yet
            filesUnderRoot.add(file);
        }
        return alreadyAdded;
    }

    /**
     * Fires events for updated files. Thus diff sidebars are refreshed.
     * @param repo
     * @param list
     * @return true if any file triggered the notification
     */
    public static boolean notifyUpdatedFiles(File repo, List<String> list){
        boolean anyFileNotified = false;
        // When hg -v output, or hg -v unbundle or hg -v pull is called
        // the output contains line
        // getting <file>
        // for each file updated.
        //
        for (String line : list) {
            if (line.startsWith("getting ") || line.startsWith("merging ")) { //NOI18N
                String name = line.substring(8);
                File file = new File (repo, name);
                anyFileNotified = true;
                Mercurial.getInstance().notifyFileChanged(file);
            }
        }
        return anyFileNotified;
    }
}
