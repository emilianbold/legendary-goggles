/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.editor;

import java.io.IOException;
import javax.swing.JEditorPane;

import org.netbeans.editor.BaseKit;
import org.netbeans.editor.Settings;
import org.netbeans.editor.LocaleSupport;
import org.netbeans.modules.editor.java.JavaKit;
import org.netbeans.modules.editor.html.HTMLKit;
import org.netbeans.modules.editor.plain.PlainKit;
import org.netbeans.modules.editor.java.JCStorage;
import org.netbeans.modules.editor.java.JCUpdateAction;
import org.netbeans.modules.editor.options.AllOptions;
import org.netbeans.modules.editor.options.JavaOptions;
import org.netbeans.modules.editor.options.HTMLOptions;
import org.netbeans.modules.editor.options.PlainOptions;
import org.netbeans.modules.editor.options.JavaPrintOptions;
import org.netbeans.modules.editor.options.HTMLPrintOptions;
import org.netbeans.modules.editor.options.PlainPrintOptions;

import org.openide.modules.ModuleInstall;
import org.openide.TopManager;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.LocalFileSystem;
import org.openide.loaders.DataFolder;
import org.openide.util.SharedClassObject;
import org.openide.options.SystemOption;
import org.openide.text.PrintSettings;

import org.openidex.util.Utilities2;

/**
* Module installation class for editor
*
* @author Miloslav Metelka
*/
public class EditorModule extends ModuleInstall {

    /** Kit replacements that will be installed into JEditorPane */
    KitInfo[] replacements = new KitInfo[] {
        new KitInfo(PlainKit.PLAIN_MIME_TYPE, PlainKit.class.getName(),
            PlainOptions.class, PlainPrintOptions.class),
        new KitInfo(JavaKit.JAVA_MIME_TYPE, JavaKit.class.getName(),
            JavaOptions.class, JavaPrintOptions.class),
        new KitInfo(HTMLKit.HTML_MIME_TYPE, HTMLKit.class.getName(),
            HTMLOptions.class, HTMLPrintOptions.class)
    };

    static final long serialVersionUID =-929863607593944237L;

    public void installed () {
        try {
            Utilities2.createAction (JCUpdateAction.class, DataFolder.create (TopManager.getDefault ().getPlaces ().folders ().actions (), "Tools")); // NOI18N
        } catch (IOException ioe) {
            if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
                ioe.printStackTrace ();
        }

        restored ();
    }

    /** Module installed again. */
    public void restored () {

        LocaleSupport.addLocalizer(new NbLocalizer(NbEditorSettingsInitializer.class));
        LocaleSupport.addLocalizer(new NbLocalizer(AllOptions.class));

        // Initializations

        // Settings
        NbEditorSettingsInitializer.init();

        // Options
        AllOptions ao = (AllOptions) SharedClassObject.findObject(AllOptions.class, true);
        PrintSettings ps = (PrintSettings) SharedClassObject.findObject(PrintSettings.class, true);
        
        // Start listening on addition/removal of options
        ao.init();

        for (int i = 0; i < replacements.length; i++) {
            ao.addOption((SystemOption)SharedClassObject.findObject(replacements[i].optionsClass, true));
            ps.addOption((SystemOption)SharedClassObject.findObject(replacements[i].printOptionsClass, true));
        }
        
        // Java completion storage init
        FileSystem rfs = TopManager.getDefault().getRepository().getDefaultFileSystem();
        JCStorage.init(rfs.getRoot());

        // Preloading of some classes for faster editor opening
        BaseKit.getKit(JavaKit.class).createDefaultDocument();

        // Registration of the editor kits to JEditorPane
        for (int i = 0; i < replacements.length; i++) {
            JEditorPane.registerEditorKitForContentType(
                replacements[i].contentType,
                replacements[i].newKitClassName,
                getClass().getClassLoader()
            );
        }

        org.netbeans.modules.editor.options.ProjectHack.restored();

    }

    public void uninstalled() {

        org.netbeans.modules.editor.options.ProjectHack.uninstalled();
        
        // Options
        AllOptions ao = (AllOptions) SharedClassObject.findObject(AllOptions.class, true);
        PrintSettings ps = (PrintSettings) SharedClassObject.findObject(PrintSettings.class, true);

        for (int i = 0; i < replacements.length; i++) {
            ao.removeOption((SystemOption)SharedClassObject.findObject(replacements[i].optionsClass, true));
            ps.removeOption((SystemOption)SharedClassObject.findObject(replacements[i].printOptionsClass, true));
        }

        try {
            Utilities2.removeAction (JCUpdateAction.class, DataFolder.create (TopManager.getDefault ().getPlaces ().folders ().actions (), "Tools")); // NOI18N
        } catch (IOException ioe) {
            if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
                ioe.printStackTrace ();
        }

        if (Boolean.getBoolean("netbeans.module.test")) { // NOI18N
            /* Reset the hashtable holding the editor kits, so the editor kit
            * can be refreshed. As the JEditorPane.kitRegistryKey is private
            * it must be accessed through the reflection.
            */
            try {
                java.lang.reflect.Field kitRegistryKeyField = JEditorPane.class.getDeclaredField("kitRegistryKey");  // NOI18N
                if (kitRegistryKeyField != null) {
                    kitRegistryKeyField.setAccessible(true);
                    Object kitRegistryKey = kitRegistryKeyField.get(JEditorPane.class);
                    if (kitRegistryKey != null) {
                        // Set a fresh hashtable. It can't be null as there is a hashtable in AppContext
                        sun.awt.AppContext.getAppContext().put(kitRegistryKey, new java.util.Hashtable());
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }


    static class KitInfo {

        /** Content type for which the kits will be switched */
        String contentType;

        /** Class name of the kit that will be registered */
        String newKitClassName;
        
        /** Class holding the options for the kit */
        Class optionsClass;
        
        /** Class holding the print options for the kit */
        Class printOptionsClass;

        KitInfo(String contentType, String newKitClassName, Class optionsClass, Class printOptionsClass) {
            this.contentType = contentType;
            this.newKitClassName = newKitClassName;
            this.optionsClass = optionsClass;
            this.printOptionsClass = printOptionsClass;
        }

    }

}

/*
 * Log
 *  42   Jaga      1.38.1.0.1.13/17/00  Miloslav Metelka 
 *  41   Jaga      1.38.1.0.1.03/15/00  Miloslav Metelka Structural change
 *  40   Gandalf-post-FCS1.38.1.0    3/8/00   Miloslav Metelka 
 *  39   Gandalf   1.38        1/16/00  Jesse Glick     Actions pool.
 *  38   Gandalf   1.37        1/13/00  Miloslav Metelka Localization
 *  37   Gandalf   1.36        1/4/00   Miloslav Metelka 
 *  36   Gandalf   1.35        11/27/99 Patrik Knakal   
 *  35   Gandalf   1.34        11/9/99  Miloslav Metelka 
 *  34   Gandalf   1.33        11/8/99  Miloslav Metelka 
 *  33   Gandalf   1.32        10/29/99 Jaroslav Tulach Does not cast to 
 *       LocalFileSystem
 *  32   Gandalf   1.31        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  31   Gandalf   1.30        10/10/99 Miloslav Metelka 
 *  30   Gandalf   1.29        10/1/99  Petr Hrebejk    org.openide.modules.ModuleInstall
 *        changed to class + some methods added
 *  29   Gandalf   1.28        9/30/99  Miloslav Metelka 
 *  28   Gandalf   1.27        9/13/99  Petr Jiricka    JSP and properties 
 *       kitinfo removed.
 *  27   Gandalf   1.26        9/2/99   Libor Kramolis  Remove XML and DTD 
 *       settings from editor module
 *  26   Gandalf   1.25        8/17/99  Miloslav Metelka 
 *  25   Gandalf   1.24        8/4/99   Petr Jiricka    Added editor kits for 
 *       text/x-jsp and text/x-properties
 *  24   Gandalf   1.23        7/31/99  Ian Formanek    removed debug messages
 *  23   Gandalf   1.22        7/28/99  Libor Kramolis  
 *  22   Gandalf   1.21        7/26/99  Miloslav Metelka 
 *  21   Gandalf   1.20        7/21/99  Miloslav Metelka 
 *  20   Gandalf   1.19        7/21/99  Miloslav Metelka 
 *  19   Gandalf   1.18        7/20/99  Miloslav Metelka Creation of ParserDB dir
 *       if necessary
 *  18   Gandalf   1.17        7/20/99  Miloslav Metelka 
 *  17   Gandalf   1.16        7/9/99   Miloslav Metelka 
 *  16   Gandalf   1.15        6/9/99   Miloslav Metelka 
 *  15   Gandalf   1.14        6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  14   Gandalf   1.13        6/8/99   Miloslav Metelka 
 *  13   Gandalf   1.12        6/1/99   Miloslav Metelka 
 *  12   Gandalf   1.11        6/1/99   Miloslav Metelka 
 *  11   Gandalf   1.10        5/5/99   Miloslav Metelka 
 *  10   Gandalf   1.9         4/23/99  Miloslav Metelka Differrent document 
 *       constructor
 *  9    Gandalf   1.8         4/13/99  Ian Formanek    Fixed bug #1518 - 
 *       java.lang.NoClassDefFoundError: 
 *       org/netbeans/modules/editor/NbEditorBaseKit thrown on startup.
 *  8    Gandalf   1.7         4/8/99   Miloslav Metelka 
 *  7    Gandalf   1.6         3/18/99  Miloslav Metelka 
 *  6    Gandalf   1.5         3/11/99  Jaroslav Tulach Works with plain 
 *       document.
 *  5    Gandalf   1.4         3/10/99  Jaroslav Tulach body of install moved to
 *       restored.
 *  4    Gandalf   1.3         3/9/99   Ian Formanek    Fixed last change
 *  3    Gandalf   1.2         3/9/99   Ian Formanek    Removed obsoleted import
 *  2    Gandalf   1.1         3/8/99   Jesse Glick     For clarity: Module -> 
 *       ModuleInstall; NetBeans-Module-Main -> NetBeans-Module-Install.
 *  1    Gandalf   1.0         2/4/99   Miloslav Metelka 
 * $
 */
