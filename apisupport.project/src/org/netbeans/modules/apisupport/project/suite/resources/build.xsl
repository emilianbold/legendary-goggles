<?xml version="1.0" encoding="UTF-8"?>
<!--
The contents of this file are subject to the terms of the Common Development
and Distribution License (the License). You may not use this file except in
compliance with the License.

You can obtain a copy of the License at http://www.netbeans.org/cddl.html
or http://www.netbeans.org/cddl.txt.

When distributing Covered Code, include this CDDL Header Notice in each file
and include the License file at http://www.netbeans.org/cddl.txt.
If applicable, add the following below the CDDL Header, with the fields
enclosed by brackets [] replaced by your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

The Original Software is NetBeans. The Initial Developer of the Original
Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
Microsystems, Inc. All Rights Reserved.
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:project="http://www.netbeans.org/ns/project/1"
                xmlns:sproject="http://www.netbeans.org/ns/nb-module-suite-project/1"
                xmlns:xalan="http://xml.apache.org/xslt"
                exclude-result-prefixes="xalan project sproject">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="4"/>
    <xsl:template match="/">
        <xsl:comment> You may freely edit this file. See harness/README in the NetBeans platform </xsl:comment>
        <xsl:comment> for some information on what you could do (e.g. targets to override). </xsl:comment>
        <xsl:comment> If you delete this file and reopen the project it will be recreated. </xsl:comment>
        <xsl:variable name="name" select="/project:project/project:configuration/sproject:data/sproject:name"/>
        <project name="{$name}"><!-- XXX consider sanitizing as per PropertyUtils.getUsablePropertyName -->
            <!-- XXX default attr? -->
            <xsl:attribute name="basedir">.</xsl:attribute>
            <description>Builds the module suite <xsl:value-of select="$name"/>.</description>
            <import file="nbproject/build-impl.xml"/>
        </project>
    </xsl:template>
</xsl:stylesheet>
