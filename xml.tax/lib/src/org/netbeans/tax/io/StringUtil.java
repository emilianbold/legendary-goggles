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
package org.netbeans.tax.io;

/**
 *
 * @author  Libor Kramolis
 * @version 0.1
 */
public final class StringUtil {
    
    public static boolean isWS (char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
    }
    
    /**
     * @param nest list of characters that symetrically delimit some inner token that
     *  can contain stop delimiter. e.g. &lt;!ENTITY sdsd "sd>">
     */
    public static int skipDelimited (String text, int pos, char del1, char del2, String nest) {
        char ch = text.charAt (pos);
        if ( ch != del1) return -1;
        do {
            pos++;
            ch = text.charAt (pos);
            if (nest.indexOf (ch) >= 0) {
                pos = skipDelimited (text, pos, ch, ch, "");
                ch = text.charAt (pos);
            }
        } while (ch != del2);
        return pos + 1;
    }
    
    public static int skipDelimited (String text, int pos, String del1, String del2) {
        if (text.startsWith (del1, pos)) {
            int match = text.indexOf (del2, pos + del1.length ());
            if (match == -1) return -1;
            return match + del2.length ();
        } else {
            return -1;
        }
    }
    
    public static int skipWS (String text, int pos) {
        if (isWS (text.charAt (pos))) {
            return pos + 1;
        } else {
            return -1;
        }
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main (String args[]) {
        
        String idtd = "   <!-- klfh -->  <!hjk \"fdsf\"  ''>]>";
        int pos = 0;
        int now = 0;
        int last = -1;
        
        System.err.println ("SkipWs" + skipWS (" k", pos));
        
        System.err.println ("SkipDelinitedchar" + skipDelimited ("<  ' > '>", 0, '<', '>' ,"\"'"));
        
        System.err.println ("SkipDelinitedchar" + skipDelimited ("<!--  ' > '-->", 0, "<!--", "-->"));
        
        while (idtd.substring (pos).startsWith ("]>") == false && last != pos) {
            
            last = pos;
            
            for (now = 0; now != -1; now = skipWS (idtd, pos)) pos = now;
            
            for (now = 0; now != -1; now = skipDelimited (idtd, pos, "<!--", "-->")) {
                pos = now;
                for (now = 0; now != -1; now = skipWS (idtd, pos)) pos = now;
            }
            
            for (now = 0; now != -1; now = skipDelimited (idtd, pos, '<', '>' , "\"'")) pos = now;
            
            //            while(skipWS(idtd, pos));
            //            while(skipDelimited(idtd, pos, "<!--", "-->")) { while(skipWS(idtd, pos));};
            
            //            skipDelimited(idtd, pos, '%', ';' , "");
        }
        
    }
    
}
