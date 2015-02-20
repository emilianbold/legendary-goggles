/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * https://java.net/projects/gf-tooling/pages/License or LICENSE.TXT.
 * See the License for the specific language governing permissions
 * and limitations under the License.  When distributing the software,
 * include this License Header Notice in each file and include the License
 * file at LICENSE.TXT. Oracle designates this particular file as subject
 * to the "Classpath" exception as provided by Oracle in the GPL Version 2
 * section of the License file that accompanied this code. If applicable,
 * add the following below the License Header, with the fields enclosed
 * by brackets [] replaced by your own identifying information:
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.glassfish.tooling.admin;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.netbeans.modules.glassfish.tooling.data.GlassFishConfig;
import org.netbeans.modules.glassfish.tooling.data.GlassFishServer;
import org.netbeans.modules.glassfish.tooling.logging.Logger;
import org.netbeans.modules.glassfish.tooling.server.config.ConfigBuilderProvider;
import org.netbeans.modules.glassfish.tooling.server.config.GlassFishConfigManager;
import org.netbeans.modules.glassfish.tooling.server.config.JavaSEPlatform;
import org.netbeans.modules.glassfish.tooling.server.config.JavaSESet;
import org.netbeans.modules.glassfish.tooling.utils.JavaUtils;
import org.netbeans.modules.glassfish.tooling.utils.ServerUtils;

/**
 * GlassFish server administration command execution using local Java VM.
 * <p/>
 * @author Tomas Kraus
 */
abstract class RunnerJava extends Runner {

    ////////////////////////////////////////////////////////////////////////////
    // Class attributes                                                       //
    ////////////////////////////////////////////////////////////////////////////

    /** Logger instance for this class. */
    private static final Logger LOGGER = new Logger(RunnerJava.class);

    /** Specifies program encapsulated in a JAR file to execute. */
    static final String JAR_PARAM = "-jar";

    /** Character used to separate query string from list of parameters. */
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    static final char QUERY_SEPARATOR = ' ';

    /** Character used to separate individual parameters. */
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    static final char PARAM_SEPARATOR = ' ';

    /** Character used to assign value to parameter. */
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    static final char PARAM_ASSIGN_VALUE = ' ';

    ////////////////////////////////////////////////////////////////////////////
    // Static methods                                                         //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Get {@link GlassFishConfig} instance for provided GlassFish server which
     * shall not be <code>null</code>.
     * <p/>
     * {@link CommandException} is thrown when configuration object value
     * is <code>null</code>.
     * <p/>
     * @param server GlassFish server entity object.
     * @return GlassFish server features configuration which
     *         is not <code>null</code>.
     */
    static GlassFishConfig getServerConfig(final GlassFishServer server) {
        final String METHOD = "getServerConfig";
        GlassFishConfig config = GlassFishConfigManager.getConfig(
                ConfigBuilderProvider.getBuilderConfig(
                server.getVersion()));
        if (config == null) {
            throw new CommandException(LOGGER.excMsg(METHOD, "noConfig"),
                    server.getVersion());
        }
        return config;
    }

    /**
     * Get {@link JavaSESet} instance for provided GlassFish server
     * features configuration.
     * <p/>
     * @param config GlassFish server features configuration.
     * @return GlassFish JavaSE configuration which is not <code>null</code>.
     */
    static JavaSESet getJavaSEConfig(final GlassFishConfig config) {
        final String METHOD = "getJavaSEConfig";
        JavaSESet javaSEConfig = config.getJavaSE();
        if (javaSEConfig == null) {
            throw new CommandException(LOGGER.excMsg(METHOD, "noJavaSEConfig"));
        }
        return javaSEConfig;
    }

    /**
     * Constructs path to Java VM executable and verifies if it exists.
     * <p/>
     * @param server GlassFish server entity object.
     * @param command lassFish server administration command with local Java VM.
     * @return Path to Java VM executable
     */
    private static String getJavaVM(final GlassFishServer server,
            final CommandJava command) {
        final String METHOD = "getJavaVM";
        String javaVmExe = JavaUtils.javaVmExecutableFullPath(command.javaHome);
        File javaVmFile = new File(javaVmExe);
        // Java VM executable should exist and should be executable.
        if (!javaVmFile.canExecute()) {
            LOGGER.log(Level.INFO, METHOD, "noJavaVMExe", javaVmExe);
            return null;
        }
        return javaVmExe;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Instance attributes                                                    //
    ////////////////////////////////////////////////////////////////////////////

    /** Holding data for command execution. */
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    final CommandJava command;

    /** Java VM executable. */
    final String javaVMExe;

    ////////////////////////////////////////////////////////////////////////////
    // Constructors                                                           //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs an instance of administration command executor using
     * command line asadmin interface.
     * <p/>
     * @param server  GlassFish server entity object.
     * @param command GlassFish server administration command entity.
     * @param query   Query string for this command.
     */
    public RunnerJava(final GlassFishServer server, final Command command,
            final String query) {
        super(server, command, null, query);
        final String METHOD = "init";
        if (command instanceof CommandJava) {
            this.command = (CommandJava)command;
        } else {
            throw new CommandException(LOGGER.excMsg(METHOD, "noCommandJava"));
        }
        javaVMExe = getJavaVM(server, this.command);
        if (javaVMExe == null) {
            throw new CommandException(LOGGER.excMsg(METHOD, "noJavaVMExe"),
                    new Object[] {this.command.javaHome, server.getName()});
        }
    }

    /**
     * Constructs an instance of administration command executor using
     * command line asadmin interface.
     * <p/>
     * @param server  GlassFish server entity object.
     * @param command GlassFish server administration command entity.
     */
    public RunnerJava(final GlassFishServer server, final Command command) {
        this(server, command, null);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Implemented Abstract Methods                                           //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Do not send information to the server via HTTP POST by default.
     * <p/>
     * @return <code>true</code> if using HTTP POST to send to server
     *         or <code>false</code> otherwise
     */
    /**
     * Do not send information to the server via HTTP POST by default.
     * <p/>
     * This method makes no sense for this runner.
     * <p/>
     * @return Always returns <code>false</code>.
     */
    @Override
    public boolean getDoOutput() {
        return false;
    }

    /**
     * Inform whether this runner implementation accepts gzip format.
     * <p/>
     * This method makes no sense for this runner.
     * <p/>
     * @return Always returns <code>false</code>.
     */
    @Override
    public boolean acceptsGzip() {
        return false;
    }

    /**
     * Build GlassFish administration interface command URL.
     * <p/>
     * This method makes no sense for this runner.
     * <p/>
     * @return Always returns <code>null</code>.
     * @throws <code>CommandException</code> if there is a problem with building
     *         command URL.
     */
    @Override
    protected String constructCommandUrl() throws CommandException {
        return null;
    }

    /**
     * The type of HTTP method used to access administration interface command.
     * <p/>
     * This method makes no sense for this runner.
     * <p/>
     * @return Always returns <code>null</code>.
     */
    @Override
    protected String getRequestMethod() {
        return null;
    }

    /**
     * Handle sending data to server using HTTP administration command interface.
     * <p/>
     * Does nothing. This method makes no sense for this runner.
     */
    @Override
    protected void handleSend(HttpURLConnection hconn) throws IOException {
    }

    ////////////////////////////////////////////////////////////////////////////
    // ExecutorService call() method helpers                                  //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Verifies if Java VM version is supported by provided GlassFish server.
     * <p/>
     * @return Value of <code>true</code> when Java VM executable version
     *         is known and supported by provided GlassFish server
     *         or <code>false</code> otherwise.
     */
    boolean verifyJavaVM() {
        final String METHOD = "verifyJavaVM";
        // Java VM executable version must be known.
        JavaUtils.JavaVersion javaVersion
                = JavaUtils.javaVmVersion(new File(javaVMExe));
        if (javaVersion == null) {
            LOGGER.log(Level.INFO, METHOD, "unknown", javaVMExe);
            return false;
        } else {
            LOGGER.log(Level.FINEST, METHOD, "info",
                    new Object[] {javaVMExe, javaVersion.toString()});
        }
        // Java VM executable version must be supported by provided server.
        Set<JavaSEPlatform> platforms =
                getJavaSEConfig(getServerConfig(server)).platforms();
        if (!platforms.contains(javaVersion.toPlatform())) {
            LOGGER.log(Level.INFO, METHOD, "unsupported",
                    new Object[] {javaVMExe, server.getName()});
            return false;
        }
        return true;
    }

    /**
     * Prepare Java VM environment for Glassfish server execution.
     * <p/>
     * @param env     Process builder environment <code>Map</code>.
     * @param command GlassFish Server Administration Command Entity.
     */
    static void setJavaEnvironment(Map<String,String> env,
            CommandJava command) {
        // Java VM home stored in AS environment variables JAVA_HOME and AS_JAVA
        env.put(JavaUtils.JAVA_HOME_ENV, command.javaHome);
        env.put(ServerUtils.AS_JAVA_ENV, command.javaHome);
    }

    /**
     * Set server process current directory to domain directory if exists.
     * <p/>
     * No current directory will be set when domain directory does not exist.
     * <p/>
     * @param pb Process builder object where to set current directory.
     */
    void setProcessCurrentDir(ProcessBuilder pb) {
        final String METHOD = "setProcessCurrentDir";
        String domainsFolder = server.getDomainsFolder();
        if (domainsFolder != null && domainsFolder.length() > 0) {
            File currentDir = new File(
                    ServerUtils.getDomainConfigPath(domainsFolder));
            if (currentDir.exists()) {
                LOGGER.log(Level.FINEST, METHOD, "dir",
                        new Object[] {server.getName(), currentDir});
                pb.directory(currentDir);
            }
        }
    }

}
