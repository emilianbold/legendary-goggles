<?xml version="1.0" encoding="UTF-8"?>
<!--
                Sun Public License Notice

The contents of this file are subject to the Sun Public License
Version 1.0 (the "License"). You may not use this file except in
compliance with the License. A copy of the License is available at
http://www.sun.com/

The Original Code is NetBeans. The Initial Developer of the Original
Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
Microsystems, Inc. All Rights Reserved.
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:p="http://www.netbeans.org/ns/project"
                xmlns:xalan="http://xml.apache.org/xslt"
                xmlns:nbm="http://www.netbeans.org/ns/nb-module-project"
                exclude-result-prefixes="xalan p nbm">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="4"/>
    
    <xsl:variable name="modules" select="document('modules.xml')/modules"/>
    
    <xsl:template match="/">

<xsl:comment> *** GENERATED FROM project.xml - DO NOT EDIT *** </xsl:comment>

<project name="{/p:project/p:configuration/nbm:data/nbm:path}/impl" default="netbeans" basedir="..">

    <target name="init">
        <property file="nbproject/private/private.properties"/>
        <!--
        <property name="user.properties.file" location="${{netbeans.user}}/build.properties"/>
        <property file="${{user.properties.file}}"/>
        -->
        <property file="nbproject/project.properties"/>
        <property name="code.name.base.dashes" value="{translate(/p:project/p:name, '.', '-')}"/>
        <property name="domain" value="{substring-before(/p:project/p:configuration/nbm:data/nbm:path, '/')}"/>
        <property name="module.jar.dir" value="modules"/>
        <property name="module.jar" location="${{module.jar.dir}}/${{code.name.base.dashes}}.jar"/>
        <property name="nbm" value="${{code.name.base.dashes}}.nbm"/>
        <property name="homepage.base" value="netbeans.org"/>
        <property name="dist.base" value="www.netbeans.org/download/nbms/40"/>
        <fail unless="nbroot">Must set nbroot</fail>
        <property name="license.file" location="${{nbroot}}/nbbuild/standard-nbm-license.txt"/>
        <property name="nbm_alias" value="nb_ide"/>
        <property name="build.compiler.debug" value="true"/>
        <property name="build.compiler.deprecation" value="true"/>
        <property name="build.sysclasspath" value="ignore"/>
        <property name="manifest.mf" location="manifest.mf"/>
        <property name="src.dir" location="src"/>
        <property name="build.classes.dir" location="build/classes"/>
        <path id="cp">
            <xsl:for-each select="/p:project/p:configuration/nbm:data/nbm:module-dependencies/nbm:dependency[count(nbm:compile-dependency) = 1]">
                <xsl:variable name="cnb" select="nbm:code-name-base"/>
                <xsl:variable name="match" select="$modules/module[cnb = $cnb]"/>
                <!--
                <xsl:message>Found match: <xsl:value-of select="$match"/> for '<xsl:value-of select="$cnb"/>'</xsl:message>
                -->
                <xsl:choose>
                    <xsl:when test="count($match) = 0">
                        <xsl:message>Warning: could not find module named <xsl:value-of select="$cnb"/>!</xsl:message>
                    </xsl:when>
                    <xsl:when test="count($match) > 1">
                        <xsl:message>Warning: more than one match for module <xsl:value-of select="$cnb"/>!</xsl:message>
                    </xsl:when>
                    <xsl:otherwise>
                        <pathelement location="${{nbroot}}/{$match/path}/netbeans/{$match/jar}"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </path>
    </target>

    <target name="compile" depends="init">
        <mkdir dir="${{build.classes.dir}}"/>
        <javac srcdir="${{src.dir}}" destdir="${{build.classes.dir}}" debug="${{build.compiler.debug}}" deprecation="${{build.compiler.deprecation}}" source="1.4" includeantruntime="false">
            <classpath refid="cp"/>
        </javac>
        <copy todir="${{build.classes.dir}}">
            <fileset dir="${{src.dir}}" excludesfile="${{nbroot}}/nbbuild/standard-jar-excludes.txt"/>
        </copy>
    </target>

    <!--
    <target name="compile-single" depends="init">
        <fail unless="selected.files">Must select some files in the IDE or set selected.files</fail>
        <property name="src.dir.absolute" location="${{src.dir}}"/>
        <pathconvert property="javac.includes" pathsep=",">
            <path path="${{selected.files}}"/>
            <map from="${{src.dir.absolute}}${{file.separator}}" to=""/>
        </pathconvert>
        <mkdir dir="${{build.classes.dir}}"/>
        <javac srcdir="${{src.dir}}" destdir="${{build.classes.dir}}"
               debug="${{javac.debug}}" optimize="${{javac.optimize}}" deprecation="${{javac.deprecation}}"
               source="${{javac.source}}" includes="${{javac.includes}}" includeantruntime="false">
            <classpath>
                <path path="${{javac.classpath}}"/>
            </classpath>
        </javac>
    </target>
    -->

    <target name="jar" depends="init,compile">
        <mkdir dir="netbeans/${{module.jar.dir}}"/>
        <tstamp>
            <format property="buildnumber" pattern="manual-yyMMdd" timezone="UTC"/>
        </tstamp>
        <jar jarfile="netbeans/${{module.jar}}" compress="${{jar.compress}}" manifest="${{manifest.mf}}">
            <manifest>
                <attribute name="OpenIDE-Module-Public-Packages">
                    <xsl:attribute name="value">
                        <!-- XXX read from project.xml#packages -->
                    </xsl:attribute>
                </attribute>
                <!-- XXX OpenIDE-Module-IDE-Dependencies and/or OpenIDE-Module-Module-Dependencies -->
                <!-- XXX make this conditional so can use OIDE-M-B-V instead -->
                <attribute name="OpenIDE-Module-Implementation-Version" value="${{buildnumber}}"/>
            </manifest>
        </jar>
    </target>
    
    <target name="netbeans" depends="init,jar">
        <taskdef name="genlist" classname="org.netbeans.nbbuild.MakeListOfNBM" classpath="${{nbroot}}/nbbuild/nbantext.jar"/>
        <genlist targetname="nbm" outputfiledir="netbeans"/>
    </target>
    
    <target name="nbm" depends="init,netbeans">
        <mkdir dir="build"/>
        <taskdef name="makenbm" classname="org.netbeans.nbbuild.MakeNBM" classpath="${{nbroot}}/nbbuild/nbantext.jar"/>
        <makenbm file="build/${{nbm}}"
                 topdir="."
                 module="netbeans/${{module.jar}}"
                 homepage="http://${{domain}}.${{homepage.base}}/"
                 distribution="http://${{dist.base}}/${{nbm}}">
            <license file="${{license.file}}"/>
            <signature keystore="${{keystore}}" storepass="${{storepass}}" alias="${{nbm_alias}}"/>
        </makenbm>
    </target>

    <!--
    <target name="javadoc" depends="init">
    </target>

    <target name="javadoc-nb" depends="init,javadoc" if="netbeans.home">
        <nbbrowse file="${{dist.javadoc.dir}}/index.html"/>
    </target>
    -->

    <!--
    <target name="test-build" depends="init,compile" if="have.tests">
        <mkdir dir="${{build.test.classes.dir}}"/>
        <javac srcdir="test" destdir="${{build.test.classes.dir}}"
               debug="true" optimize="false" deprecation="${{javac.deprecation}}"
               source="${{javac.source}}" includeantruntime="false">
            <classpath>
                <path path="${{javac.test.classpath}}"/>
            </classpath>
        </javac>
        <copy todir="${{build.test.classes.dir}}">
            <fileset dir="${{test.src.dir}}">
                <exclude name="**/*.java"/>
            </fileset>
        </copy>
    </target>

    <target name="test" depends="init,test-build" if="have.tests">
        <mkdir dir="${{build.test.results.dir}}"/>
        <junit showoutput="true" fork="true" failureproperty="tests.failed" errorproperty="tests.failed">
            <xsl:call-template name="test-junit-body"/>
        </junit>
        <fail if="tests.failed">Some tests failed; see details above.</fail>
    </target>

    <target name="test-single" depends="init,test-build" if="have.tests">
        <fail unless="selected.files">Must select some files in the IDE or set selected.files</fail>
        <property name="test.src.dir.absolute" location="${{test.src.dir}}"/>
        <pathconvert property="test.includes" pathsep=",">
            <path path="${{selected.files}}"/>
            <map from="${{test.src.dir.absolute}}${{file.separator}}" to=""/>
        </pathconvert>
        <mkdir dir="${{build.test.results.dir}}"/>
        <junit showoutput="true" fork="true" failureproperty="tests.failed" errorproperty="tests.failed">
        <xsl:call-template name="test-single-junit-body"/>
        </junit>
        <fail if="tests.failed">Some tests failed; see details above.</fail>
    </target>

    <target name="init-test-class" unless="test.class">
        <fail unless="selected.files">Must select one file in the IDE or set selected.files</fail>
        <property name="test.src.dir.absolute" location="${{test.src.dir}}"/>
        <pathconvert property="test.class.tmp" dirsep=".">
            <path path="${{selected.files}}"/>
            <map from="${{test.src.dir.absolute}}${{file.separator}}" to=""/>
        </pathconvert>
        <basename file="${{test.class.tmp}}" property="test.class" suffix=".java"/>
    </target>
    
    <target name="do-debug-test-single" depends="init,init-test-class" if="have.tests">
        <java fork="true" classname="junit.textui.TestRunner">
        <xsl:call-template name="debug-test-single-java-body"/>
        </java>
    </target>

    <target name="debug-test-single" depends="init,init-test-class,test-build,do-debug-test-single" if="have.tests">
    </target>

    <target name="debug-test-single-nb" depends="init,init-test-class,test-build" if="netbeans.home+have.tests">
        <nbjpdastart transport="dt_socket" addressproperty="jpda.address" name="${{test.class}}"/>
        <antcall target="do-debug-test-single"/>
    </target>

    <target name="clean" depends="init">
        <delete dir="${{build.dir}}"/>
        <delete dir="${{netbeans.dir}}"/>
    </target>
    -->

</project>

    </xsl:template>

    <!--
    <xsl:template name="test-junit-body">
            <batchtest todir="${{build.test.results.dir}}">
                <fileset dir="${{test.src.dir}}">
                    <!- - XXX could include only out-of-date tests... - ->
                    <include name="**/*Test.java"/>
                </fileset>
            </batchtest>
            <classpath>
                <path path="${{run.test.classpath}}"/>
            </classpath>
            <formatter type="brief" usefile="false"/>
    </xsl:template>

    <xsl:template name="test-single-junit-body">
            <batchtest todir="${{build.test.results.dir}}">
                <fileset dir="${{test.src.dir}}" includes="${{test.includes}}"/>
            </batchtest>
            <classpath>
                <path path="${{run.test.classpath}}"/>
            </classpath>
            <formatter type="brief" usefile="false"/>
    </xsl:template>

    <xsl:template name="debug-test-single-java-body">
            <jvmarg value="-Xdebug"/>
            <jvmarg value="-Xnoagent"/>
            <jvmarg value="-Djava.compiler=none"/>
            <jvmarg value="-Xrunjdwp:transport=dt_socket,address=${{jpda.address}}"/>
            <classpath>
                <path path="${{debug.test.classpath}}"/>
            </classpath>
            <arg line="${{test.class}}"/>
            <arg line="${{application.args}}"/>
    </xsl:template>
    -->

</xsl:stylesheet>
