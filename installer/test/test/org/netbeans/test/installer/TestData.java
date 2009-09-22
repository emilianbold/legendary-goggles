/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html or
 * http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file and
 * include the License file at http://www.netbeans.org/cddl.txt. If applicable, add
 * the following below the CDDL Header, with the fields enclosed by brackets []
 * replaced by your own identifying information:
 *
 *     "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 *
 * $Id$
 *
 */

package org.netbeans.test.installer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mikhail Vaysman
 */
public class TestData implements Serializable {
    
    //private static final String NB_DOWNLOAD_PAGE = "http://bits.netbeans.org/netbeans/6.0/nightly/latest/";

    private File installerFile = null;
    private File uninstallerFile = null;
    private File workDir = null;
    private File bundleFile = null;
    private File uninstallerBundleFile = null;
    private Logger logger = null;
    private ClassLoader loader = null;
    private ClassLoader engineLoader = null;
    private Class installerMainClass = null;
    private Class uninstallerMainClass = null;
    private String workDirCanonicalPath = null;
    private String platformName = null;
    private String platformExt = null;
    private String installerType = null;
    private String buildNumber = null;
    private String installerURL = null;

    private String sExecutableName = null;

    private String sTestPackage = null;

    private String m_sNetBeansInstallPath;
    private String m_sApplicationServerInstallPath;
    private String m_sTomcatInstallPath;

    public TestData(Logger logger) {
        assert logger != null;
        this.logger = logger;
        initPlatformVar();
    }

    public String getInstallerFileName() {
        return "E:/pub/Netbeans/6.0/installer.exe";
//        return "C:/work/test/TestInstaller/netbeans-6.0-nightly-200707100000-basic-windows.exe";
    }
    
    public String getNetbeansDownloadPage() {
        return System.getProperty("test.installer.url.prefix");
    }

    public String getInstallerURL() {
        return installerURL;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public void setInstallerURL(String URL) {
        installerURL = URL;
    }
    
    private void initPlatformVar() {

        if (System.getProperty("os.name").contains("Windows")) {
            platformName = "windows";
            platformExt = "exe";
        }

        if (System.getProperty("os.name").contains("Linux")) {
            platformName = "linux";
            platformExt = "sh";
        }

        if (System.getProperty("os.name").contains("Mac OS X")) {
            throw new Error("Mac OS not supported");
        }

        if (System.getProperty("os.name").contains("SunOS") && System.getProperty("os.arch").contains("sparc")) {
            platformName = "solaris-sparc";
            platformExt = "sh";
        }
        if (System.getProperty("os.name").contains("SunOS") && System.getProperty("os.arch").contains("x86")) {
            platformName = "solaris-x86";
            platformExt = "sh";
        }
    }

    public String getPlatformExt() {
        return platformExt;
    }

    public String getPlatformName() {
        return platformName;
    }

    public String getInstallerMainClassName() {
        return "org.netbeans.installer.Installer";
    }

    public String getUninstallerMainClassName() {
        return "org.netbeans.installer.Installer";
    }

    public Logger getLogger() {
        return logger;
    }

    public void setWorkDir(File workDir) throws IOException {
        assert workDir != null;
        this.workDir = workDir;
        workDirCanonicalPath = workDir.getCanonicalPath();
    }

    public File getTestWorkDir() {
        assert workDir != null;
        return workDir;
    }

    public String getWorkDirCanonicalPath() {
        return workDirCanonicalPath;
    }

    public File getInstallerFile() {
        return installerFile;
    }

    public void setInstallerFile(File installerFile) {
        if (canRead(installerFile)) {
            this.installerFile = installerFile;
        }
    }
    
    public File getUninstallerFile( )
    {
        return uninstallerFile;
    }

    public void setUninstallerFile( File uninstallerFile )
    {
        if (canRead(uninstallerFile)) {
            this.uninstallerFile = uninstallerFile;
        }
    }

    public void setBundleFile(File bundleFile) {
        if (canRead(bundleFile)) {
            this.bundleFile = bundleFile;
        }
    }

    public File getBundleFile() {
        return bundleFile;
    }
    
    public void setUninstallerBundleFile(File bundleFile) {
        if (canRead(bundleFile)) {
            this.bundleFile = bundleFile;
        }
    }

    public File getUninstallerBundleFile() {
        return bundleFile;
    }

    public void setClassLoader(ClassLoader loader) {
        assert loader != null;
        this.loader = loader;
    }

    public ClassLoader getClassLoader() {
        return loader;
    }

    public void setEngineClassLoader(ClassLoader loader) {
        assert loader != null;
        engineLoader = loader;
    }

    public ClassLoader getEngineClassLoader() {
        return engineLoader;
    }

    public void setInstallerMainClass(Class clazz) {
        assert clazz != null;
        this.installerMainClass = clazz;
    }

    public Class getInstallerMainClass() {
        return installerMainClass;
    }

    public void setUninstallerMainClass(Class clazz) {
        assert clazz != null;
        this.uninstallerMainClass = clazz;
    }

    public Class getUninstallerMainClass() {
        return uninstallerMainClass;
    }

    private boolean canRead(File file) {
        if (file != null) {
            if (!file.canRead()) {
                java.lang.String fileName = null;
                try {
                    fileName = file.getCanonicalPath();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "Can't get cannonical path");
                }
                logger.log(Level.SEVERE, "Can't read file: " + fileName);
                return false;
            }
        } else {
            logger.log(Level.SEVERE, "Bundle file name can be null");
            return false;
        }
        return true;
    }

    public String getInstallerType() {
        return installerType;
    }

    public void setInstallerType(String installerType) {
        this.installerType = installerType;
    }

    public Proxy getProxy() {
        Proxy proxy = null;

        String proxyHost = System.getProperty("installer.proxy.host", null);
        String proxyPort = System.getProperty("installer.proxy.port", null);

        if (proxyHost != null && proxyPort != null) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.valueOf(proxyPort)));
        } else {
            proxy = Proxy.NO_PROXY;
        }

        return proxy;
    }

  public String GetExecutableName( )
  {
    return sExecutableName;
  }

  public void SetExecutableName( String s )
  {
    sExecutableName = s;
    return;
  }

  public void SetTestPackage( String s )
  {
    sTestPackage = s;
  }

  public String GetTestPackage( )
  {
    return sTestPackage;
  }

  public void CreateInstallPaths( )
  {
    // NetBeans
    String sInstallBase = System.getProperty( "test.installer.custom.path" );
    if( null == sInstallBase )
    {
      m_sNetBeansInstallPath = null;
      m_sApplicationServerInstallPath = null;
      m_sTomcatInstallPath = null;
    }
    else
    {
      m_sNetBeansInstallPath = sInstallBase + File.separator + Utils.NB_DIR_NAME;
      m_sApplicationServerInstallPath = sInstallBase + File.separator + Utils.GF2_DIR_NAME;
      m_sTomcatInstallPath = sInstallBase + File.separator + Utils.TOMCAT_DIR_NAME;
    }
  }

  public String GetNetBeansInstallPath( )
  {
    return m_sNetBeansInstallPath;
  }

  public String GetApplicationServerInstallPath( )
  {
    return m_sApplicationServerInstallPath;
  }

  public String GetTomcatInstallPath( )
  {
    return m_sTomcatInstallPath;
  }

  public void SetDefaultPath( String s )
  {
    if( null == m_sNetBeansInstallPath )
    {
      m_sNetBeansInstallPath = s;
      return;
    }
    if( null == m_sApplicationServerInstallPath )
    {
      m_sApplicationServerInstallPath = s;
      return;
    }
    if( null == m_sTomcatInstallPath )
    {
      m_sTomcatInstallPath = s;
      return;
    }
    return;
  }
}
