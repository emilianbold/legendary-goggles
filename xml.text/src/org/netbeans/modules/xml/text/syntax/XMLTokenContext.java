/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.xml.text.syntax;

import org.netbeans.editor.BaseTokenCategory;
import org.netbeans.editor.BaseTokenID;
import org.netbeans.editor.TokenContext;
import org.netbeans.editor.TokenContextPath;

import org.netbeans.modules.xml.text.syntax.javacc.lib.*;

/**
 * Token-ids and token-categories for XML
 *
 * @author  Miloslav Metelka
 * @author  Petr Kuzel
 * @version 1.0
 */

public class XMLTokenContext extends TokenContext {

    public static final int ATT_ID = 1;
    public static final int CDATA_ID = 2;
    public static final int COMMENT_ID = 3;
    public static final int EOL_ID = 4;
    public static final int ERROR_ID = 5;
    public static final int KW_ID = 6;
    public static final int PLAIN_ID = 7;
    public static final int REF_ID = 8;
    public static final int STRING_ID = 9;
    public static final int SYMBOL_ID = 10;
    public static final int TAG_ID = 11;
    public static final int TARGET_ID = 12;
    public static final int CDATA_MARKUP_ID = 13;



    // <home attname="..."> // NOI18N
    public static final JJTokenID ATT = new JJTokenID("attribute", ATT_ID); // NOI18N
    // <![CDATA[ dtatasection ]]>
    public static final JJTokenID CDATA = new JJTokenID("cdata", CDATA_ID); // NOI18N

    public static final JJTokenID COMMENT = new JJTokenID("comment", COMMENT_ID); // NOI18N
    public static final JJTokenID EOL = new JJTokenID("EOL", EOL_ID); // NOI18N
    public static final JJTokenID ERROR = new JJTokenID("error", ERROR_ID, true); // NOI18N

    // <!declatarion + "SYSTEM"/"PUBLIC" // NOI18N
    public static final JJTokenID KW = new JJTokenID("keyword", KW_ID); // NOI18N
    public static final JJTokenID PLAIN = new JJTokenID("plain", PLAIN_ID); // NOI18N
    //  &aref; &#x000;
    public static final JJTokenID REF = new JJTokenID("ref", REF_ID); // NOI18N
    // <home id="attrvalue" > // NOI18N
    public static final JJTokenID STRING = new JJTokenID("string", STRING_ID); // NOI18N
    // <>!
    public static final JJTokenID SYMBOL = new JJTokenID("symbol", SYMBOL_ID); // NOI18N
    // <atagname ....>
    public static final JJTokenID TAG = new JJTokenID("tag", TAG_ID); // NOI18N
    // <? target ...>
    public static final JJTokenID TARGET = new JJTokenID("target", TARGET_ID); // NOI18N

    public static final JJTokenID CDATA_MARKUP = new JJTokenID("markup-in-CDATA", CDATA_MARKUP_ID); // NOI18N
    
    // Context instance declaration
    public static final XMLTokenContext context = new XMLTokenContext();

    public static final TokenContextPath contextPath = context.getContextPath();


    protected XMLTokenContext() {
        super("xml-"); // NOI18N

        try {
            addDeclaredTokenIDs();
        } catch (Exception e) {
            if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
                e.printStackTrace();
            }
        }

    }

}
