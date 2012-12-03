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

package org.netbeans.modules.java.ui;

import static org.netbeans.modules.java.ui.FmtOptions.*;
import static org.netbeans.modules.java.ui.FmtOptions.CategorySupport.OPTION_ID;
import org.netbeans.modules.java.ui.FmtOptions.CategorySupport;
import org.netbeans.modules.options.editor.spi.PreferencesCustomizer;

/**
 *
 * @author  phrebejk
 */
public class FmtBlankLines extends javax.swing.JPanel {
    
    /** Creates new form FmtBlankLines */
    public FmtBlankLines() {
        initComponents();
        
        bPackageField.putClientProperty(OPTION_ID, blankLinesBeforePackage );
        aPackageField.putClientProperty(OPTION_ID, blankLinesAfterPackage);
        bImportsField.putClientProperty(OPTION_ID, blankLinesBeforeImports);
        aImportsField.putClientProperty(OPTION_ID, blankLinesAfterImports);
        bClassField.putClientProperty(OPTION_ID, blankLinesBeforeClass);
        aClassField.putClientProperty(OPTION_ID, blankLinesAfterClass);
        aClassHeaderField.putClientProperty(OPTION_ID, blankLinesAfterClassHeader);
        anAnonymousClassHeaderField.putClientProperty(OPTION_ID, blankLinesAfterAnonymousClassHeader);
        aClassFooterField.putClientProperty(OPTION_ID, blankLinesBeforeClassClosingBrace);
        anAnonymousClassFooterField.putClientProperty(OPTION_ID, blankLinesBeforeAnonymousClosingBrace);
        bFieldsField.putClientProperty(OPTION_ID, blankLinesBeforeFields);
        aFieldsField.putClientProperty(OPTION_ID, blankLinesAfterFields);
        bMethodsField.putClientProperty(OPTION_ID, blankLinesBeforeMethods );
        aMethodsField.putClientProperty(OPTION_ID, blankLinesAfterMethods);
        
        bPackageField.addKeyListener(new NumericKeyListener());
        aPackageField.addKeyListener(new NumericKeyListener());
        bImportsField.addKeyListener(new NumericKeyListener());
        aImportsField.addKeyListener(new NumericKeyListener());
        bClassField.addKeyListener(new NumericKeyListener());
        aClassField.addKeyListener(new NumericKeyListener());
        aClassHeaderField.addKeyListener(new NumericKeyListener());
        anAnonymousClassHeaderField.addKeyListener(new NumericKeyListener());
        aClassFooterField.addKeyListener(new NumericKeyListener());
        anAnonymousClassFooterField.addKeyListener(new NumericKeyListener());
        bFieldsField.addKeyListener(new NumericKeyListener());
        aFieldsField.addKeyListener(new NumericKeyListener());
        bMethodsField.addKeyListener(new NumericKeyListener());
        aMethodsField.addKeyListener(new NumericKeyListener());
        
    }
    
    public static PreferencesCustomizer.Factory getController() {
        return new CategorySupport.Factory("blank-lines", FmtBlankLines.class, //NOI18N
                 org.openide.util.NbBundle.getMessage(FmtBlankLines.class, "SAMPLE_BlankLines")); // NOI18N
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bPackageLabel = new javax.swing.JLabel();
        bPackageField = new javax.swing.JTextField();
        aPackageLabel = new javax.swing.JLabel();
        aPackageField = new javax.swing.JTextField();
        bImportsLabel = new javax.swing.JLabel();
        bImportsField = new javax.swing.JTextField();
        aImports = new javax.swing.JLabel();
        aImportsField = new javax.swing.JTextField();
        bClassLabel = new javax.swing.JLabel();
        bClassField = new javax.swing.JTextField();
        aClassLabel = new javax.swing.JLabel();
        aClassField = new javax.swing.JTextField();
        aClassHeaderLabel = new javax.swing.JLabel();
        aClassHeaderField = new javax.swing.JTextField();
        anAnonymousClassHeaderLabel = new javax.swing.JLabel();
        anAnonymousClassHeaderField = new javax.swing.JTextField();
        aClassFooterLabel = new javax.swing.JLabel();
        aClassFooterField = new javax.swing.JTextField();
        anAnonymousClassFooterLabel = new javax.swing.JLabel();
        anAnonymousClassFooterField = new javax.swing.JTextField();
        bFieldsLabel = new javax.swing.JLabel();
        bFieldsField = new javax.swing.JTextField();
        aFieldsLabel = new javax.swing.JLabel();
        aFieldsField = new javax.swing.JTextField();
        bMethodsLabel = new javax.swing.JLabel();
        bMethodsField = new javax.swing.JTextField();
        aMethodsLabel = new javax.swing.JLabel();
        aMethodsField = new javax.swing.JTextField();

        setName(org.openide.util.NbBundle.getMessage(FmtBlankLines.class, "LBL_BlankLines")); // NOI18N
        setOpaque(false);

        bPackageLabel.setLabelFor(bPackageField);
        org.openide.awt.Mnemonics.setLocalizedText(bPackageLabel, org.openide.util.NbBundle.getMessage(FmtBlankLines.class, "LBL_blBeforePackage")); // NOI18N

        bPackageField.setColumns(2);

        aPackageLabel.setLabelFor(aPackageField);
        org.openide.awt.Mnemonics.setLocalizedText(aPackageLabel, org.openide.util.NbBundle.getMessage(FmtBlankLines.class, "LBL_blAfterPackage")); // NOI18N

        aPackageField.setColumns(5);

        bImportsLabel.setLabelFor(bImportsField);
        org.openide.awt.Mnemonics.setLocalizedText(bImportsLabel, org.openide.util.NbBundle.getMessage(FmtBlankLines.class, "LBL_blBeforeImports")); // NOI18N

        bImportsField.setColumns(5);

        aImports.setLabelFor(aImportsField);
        org.openide.awt.Mnemonics.setLocalizedText(aImports, org.openide.util.NbBundle.getMessage(FmtBlankLines.class, "LBL_blAfterImports")); // NOI18N

        aImportsField.setColumns(5);

        bClassLabel.setLabelFor(bClassField);
        org.openide.awt.Mnemonics.setLocalizedText(bClassLabel, org.openide.util.NbBundle.getMessage(FmtBlankLines.class, "LBL_blBeforeClass")); // NOI18N

        bClassField.setColumns(5);

        aClassLabel.setLabelFor(aClassField);
        org.openide.awt.Mnemonics.setLocalizedText(aClassLabel, org.openide.util.NbBundle.getMessage(FmtBlankLines.class, "LBL_blAfterClass")); // NOI18N

        aClassField.setColumns(5);

        aClassHeaderLabel.setLabelFor(aClassHeaderField);
        org.openide.awt.Mnemonics.setLocalizedText(aClassHeaderLabel, org.openide.util.NbBundle.getMessage(FmtBlankLines.class, "LBL_blAfterClassHeader")); // NOI18N

        aClassHeaderField.setColumns(5);

        anAnonymousClassHeaderLabel.setLabelFor(anAnonymousClassHeaderField);
        org.openide.awt.Mnemonics.setLocalizedText(anAnonymousClassHeaderLabel, org.openide.util.NbBundle.getMessage(FmtBlankLines.class, "LBL_blAfterAnonymousClassHeader")); // NOI18N

        aClassFooterLabel.setLabelFor(aClassFooterField);
        org.openide.awt.Mnemonics.setLocalizedText(aClassFooterLabel, org.openide.util.NbBundle.getMessage(FmtBlankLines.class, "LBL_blBeforeClassFooter")); // NOI18N

        aClassFooterField.setColumns(5);

        anAnonymousClassFooterLabel.setLabelFor(anAnonymousClassFooterField);
        org.openide.awt.Mnemonics.setLocalizedText(anAnonymousClassFooterLabel, org.openide.util.NbBundle.getMessage(FmtBlankLines.class, "LBL_blBeforeAnonymousClassFooter")); // NOI18N

        bFieldsLabel.setLabelFor(bFieldsField);
        org.openide.awt.Mnemonics.setLocalizedText(bFieldsLabel, org.openide.util.NbBundle.getMessage(FmtBlankLines.class, "LBL_blBeforeFields")); // NOI18N

        bFieldsField.setColumns(5);

        aFieldsLabel.setLabelFor(aFieldsField);
        org.openide.awt.Mnemonics.setLocalizedText(aFieldsLabel, org.openide.util.NbBundle.getMessage(FmtBlankLines.class, "LBL_blAfterFields")); // NOI18N

        aFieldsField.setColumns(5);

        bMethodsLabel.setLabelFor(bMethodsField);
        org.openide.awt.Mnemonics.setLocalizedText(bMethodsLabel, org.openide.util.NbBundle.getMessage(FmtBlankLines.class, "LBL_blBeforeMethods")); // NOI18N

        bMethodsField.setColumns(5);

        aMethodsLabel.setLabelFor(aMethodsField);
        org.openide.awt.Mnemonics.setLocalizedText(aMethodsLabel, org.openide.util.NbBundle.getMessage(FmtBlankLines.class, "LBL_blAfterMethods")); // NOI18N

        aMethodsField.setColumns(5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bPackageLabel)
                    .addComponent(aPackageLabel)
                    .addComponent(bImportsLabel)
                    .addComponent(aImports)
                    .addComponent(bClassLabel)
                    .addComponent(aClassLabel)
                    .addComponent(aClassHeaderLabel)
                    .addComponent(anAnonymousClassHeaderLabel)
                    .addComponent(aClassFooterLabel)
                    .addComponent(anAnonymousClassFooterLabel)
                    .addComponent(bFieldsLabel)
                    .addComponent(aFieldsLabel)
                    .addComponent(bMethodsLabel)
                    .addComponent(aMethodsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(bMethodsField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(aFieldsField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(bFieldsField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(anAnonymousClassFooterField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(aClassFooterField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(anAnonymousClassHeaderField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(aClassHeaderField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(aClassField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(bClassField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(aImportsField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(bImportsField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(aPackageField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(bPackageField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(aMethodsField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bPackageField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bPackageLabel))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aPackageField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aPackageLabel))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bImportsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bImportsLabel))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aImportsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aImports))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bClassField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bClassLabel))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aClassField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aClassLabel))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aClassHeaderField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aClassHeaderLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(anAnonymousClassHeaderLabel)
                    .addComponent(anAnonymousClassHeaderField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aClassFooterField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aClassFooterLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(anAnonymousClassFooterLabel)
                    .addComponent(anAnonymousClassFooterField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bFieldsLabel)
                    .addComponent(bFieldsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aFieldsLabel)
                    .addComponent(aFieldsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bMethodsLabel)
                    .addComponent(bMethodsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aMethodsLabel)
                    .addComponent(aMethodsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {aClassField, aClassHeaderField, aFieldsField, aImportsField, aMethodsField, aPackageField, bClassField, bFieldsField, bImportsField, bMethodsField, bPackageField});

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField aClassField;
    private javax.swing.JTextField aClassFooterField;
    private javax.swing.JLabel aClassFooterLabel;
    private javax.swing.JTextField aClassHeaderField;
    private javax.swing.JLabel aClassHeaderLabel;
    private javax.swing.JLabel aClassLabel;
    private javax.swing.JTextField aFieldsField;
    private javax.swing.JLabel aFieldsLabel;
    private javax.swing.JLabel aImports;
    private javax.swing.JTextField aImportsField;
    private javax.swing.JTextField aMethodsField;
    private javax.swing.JLabel aMethodsLabel;
    private javax.swing.JTextField aPackageField;
    private javax.swing.JLabel aPackageLabel;
    private javax.swing.JTextField anAnonymousClassFooterField;
    private javax.swing.JLabel anAnonymousClassFooterLabel;
    private javax.swing.JTextField anAnonymousClassHeaderField;
    private javax.swing.JLabel anAnonymousClassHeaderLabel;
    private javax.swing.JTextField bClassField;
    private javax.swing.JLabel bClassLabel;
    private javax.swing.JTextField bFieldsField;
    private javax.swing.JLabel bFieldsLabel;
    private javax.swing.JTextField bImportsField;
    private javax.swing.JLabel bImportsLabel;
    private javax.swing.JTextField bMethodsField;
    private javax.swing.JLabel bMethodsLabel;
    private javax.swing.JTextField bPackageField;
    private javax.swing.JLabel bPackageLabel;
    // End of variables declaration//GEN-END:variables
    
}
