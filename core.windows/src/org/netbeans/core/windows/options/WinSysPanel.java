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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.core.windows.options;

import java.util.prefs.Preferences;
import javax.swing.JPanel;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.core.windows.FloatingWindowTransparencyManager;
import org.netbeans.core.windows.nativeaccess.NativeWindowSystem;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;

@OptionsPanelController.Keywords(keywords={"#KW_WindowOptions"}, location="Appearance", tabTitle="#AdvancedOption_DisplayName_WinSys")
public class WinSysPanel extends javax.swing.JPanel {

    protected final WinSysOptionsPanelController controller;
    
    private final Preferences prefs = NbPreferences.forModule(WinSysPanel.class);
    
    protected WinSysPanel(final WinSysOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        // TODO listen to changes in form fields and call controller.changed()
        boolean isMacJDK17 = isMacJDK7();
        this.isAlphaFloating.setEnabled(!isMacJDK17);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        isDragImage = new javax.swing.JCheckBox();
        isAlphaFloating = new javax.swing.JCheckBox();
        isSnapping = new javax.swing.JCheckBox();
        isDragImageAlpha = new javax.swing.JCheckBox();
        isSnapScreenEdges = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(isDragImage, org.openide.util.NbBundle.getMessage(WinSysPanel.class, "LBL_DragWindowImage")); // NOI18N
        isDragImage.setToolTipText(org.openide.util.NbBundle.getMessage(WinSysPanel.class, "IsDragWindowTooltip")); // NOI18N
        isDragImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isDragImageActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(isDragImage, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(isAlphaFloating, org.openide.util.NbBundle.getMessage(WinSysPanel.class, "LBL_TransparentFloatingWindows")); // NOI18N
        isAlphaFloating.setToolTipText(org.openide.util.NbBundle.getMessage(WinSysPanel.class, "IsAlphaFloatingTooltip")); // NOI18N
        isAlphaFloating.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isAlphaFloatingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        add(isAlphaFloating, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(isSnapping, org.openide.util.NbBundle.getMessage(WinSysPanel.class, "LBL_SnapFloatingWindows")); // NOI18N
        isSnapping.setToolTipText(org.openide.util.NbBundle.getMessage(WinSysPanel.class, "IsSnappingTooltip")); // NOI18N
        isSnapping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isSnappingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        add(isSnapping, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(isDragImageAlpha, org.openide.util.NbBundle.getMessage(WinSysPanel.class, "LBL_TransparentDragWindow")); // NOI18N
        isDragImageAlpha.setToolTipText(org.openide.util.NbBundle.getMessage(WinSysPanel.class, "IsAlphaDragTooltip")); // NOI18N
        isDragImageAlpha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isDragImageAlphaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(isDragImageAlpha, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(isSnapScreenEdges, org.openide.util.NbBundle.getMessage(WinSysPanel.class, "LBL_SnapToScreenEdges")); // NOI18N
        isSnapScreenEdges.setToolTipText(org.openide.util.NbBundle.getMessage(WinSysPanel.class, "IsSnapScreenEdgesTooltip")); // NOI18N
        isSnapScreenEdges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isSnapScreenEdgesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        add(isSnapScreenEdges, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void isDragImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isDragImageActionPerformed
        updateDragSection();
        fireChanged();
}//GEN-LAST:event_isDragImageActionPerformed

private void isAlphaFloatingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isAlphaFloatingActionPerformed
    fireChanged();
}//GEN-LAST:event_isAlphaFloatingActionPerformed

private void isSnappingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isSnappingActionPerformed
    updateSnapSection();
    fireChanged();
}//GEN-LAST:event_isSnappingActionPerformed

    private void isDragImageAlphaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isDragImageAlphaActionPerformed
        fireChanged();
    }//GEN-LAST:event_isDragImageAlphaActionPerformed

    private void isSnapScreenEdgesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isSnapScreenEdgesActionPerformed
        fireChanged();
    }//GEN-LAST:event_isSnapScreenEdgesActionPerformed

    private void fireChanged() {
        boolean isChanged = false;
        boolean isNotSolaris = Utilities.getOperatingSystem() != Utilities.OS_SOLARIS;
        boolean isMacJDK17 = isMacJDK7();
        if (isDragImage.isSelected() != prefs.getBoolean(WinSysPrefs.DND_DRAGIMAGE, isNotSolaris && !isMacJDK17)
                || isDragImageAlpha.isSelected() != prefs.getBoolean(WinSysPrefs.TRANSPARENCY_DRAGIMAGE, isNotSolaris && !isMacJDK17)
                || isAlphaFloating.isSelected() != prefs.getBoolean(WinSysPrefs.TRANSPARENCY_FLOATING, false)
                || isSnapping.isSelected() != prefs.getBoolean(WinSysPrefs.SNAPPING, true)
                || isSnapScreenEdges.isSelected() != prefs.getBoolean(WinSysPrefs.SNAPPING_SCREENEDGES, true)) {
            isChanged = true;
        }
        controller.changed(isChanged);
    }

    protected void load() {
        boolean isNotSolaris = Utilities.getOperatingSystem() != Utilities.OS_SOLARIS;
        boolean isMacJDK17 = isMacJDK7();
        isDragImage.setSelected(prefs.getBoolean(WinSysPrefs.DND_DRAGIMAGE, isNotSolaris && !isMacJDK17));
        isDragImageAlpha.setSelected(prefs.getBoolean(WinSysPrefs.TRANSPARENCY_DRAGIMAGE, isNotSolaris && !isMacJDK17));

        isAlphaFloating.setSelected(prefs.getBoolean(WinSysPrefs.TRANSPARENCY_FLOATING,false));
        
        isSnapping.setSelected(prefs.getBoolean(WinSysPrefs.SNAPPING, true));
        isSnapScreenEdges.setSelected(prefs.getBoolean(WinSysPrefs.SNAPPING_SCREENEDGES, true));
    }

    protected boolean store() {
        prefs.putBoolean(WinSysPrefs.DND_DRAGIMAGE, isDragImage.isSelected());
        prefs.putBoolean(WinSysPrefs.TRANSPARENCY_DRAGIMAGE, isDragImageAlpha.isSelected());
        
        prefs.putBoolean(WinSysPrefs.TRANSPARENCY_FLOATING, isAlphaFloating.isSelected());
        FloatingWindowTransparencyManager.getDefault().update();
        
        prefs.putBoolean(WinSysPrefs.SNAPPING, isSnapping.isSelected());
        prefs.putBoolean(WinSysPrefs.SNAPPING_SCREENEDGES, isSnapScreenEdges.isSelected());

        return false;
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    protected void initTabsPanel( JPanel panel ) {

    }
    
    private void updateDragSection () {
        boolean isAlpha = NativeWindowSystem.getDefault().isUndecoratedWindowAlphaSupported();
        boolean isDrag = isDragImage.isSelected();

        isDragImageAlpha.setEnabled(isAlpha && isDrag);

        if (isAlpha) {
            isDragImageAlpha.setToolTipText(
                    NbBundle.getMessage(WinSysPanel.class, "IsAlphaDragTooltip")); // NOI18N
        } else {
            isDragImageAlpha.setToolTipText(
                    NbBundle.getMessage(WinSysPanel.class, "NoAlphaSupport")); // NOI18N
        }
    }
    
    private void updateSnapSection () {
        isSnapScreenEdges.setEnabled(isSnapping.isSelected());
    }
    
    private void updateFloatingSection () {
        boolean isAlpha = NativeWindowSystem.getDefault().isWindowAlphaSupported();

        isAlphaFloating.setEnabled(isAlpha);

        if (isAlpha) {
            isAlphaFloating.setToolTipText(
                    NbBundle.getMessage(WinSysPanel.class, "IsAlphaFloatingTooltip")); // NOI18N
        } else {
            isAlphaFloating.setToolTipText(
                    NbBundle.getMessage(WinSysPanel.class, "NoAlphaSupport")); // NOI18N
        }
    }
    
    private static boolean isMacJDK7() {
        if( Utilities.isMac() ) {
            String version = System.getProperty("java.version"); //NOI18N
            if( null != version && version.startsWith("1.7" ) ) //NOI18N
                return true;
        }
        return false;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox isAlphaFloating;
    private javax.swing.JCheckBox isDragImage;
    private javax.swing.JCheckBox isDragImageAlpha;
    private javax.swing.JCheckBox isSnapScreenEdges;
    private javax.swing.JCheckBox isSnapping;
    // End of variables declaration//GEN-END:variables
}
