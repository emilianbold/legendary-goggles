/*
 * TabsIndentsPanel.java
 *
 * Created on January 30, 2008, 3:56 PM
 */

package org.netbeans.modules.cnd.editor.options;

import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.cnd.editor.api.CodeStyle;
import org.openide.util.NbBundle;

/**
 * was cloned from org.netbeans.modules.java.ui.FmtTabsIndents
 * 
 * @author Alexander Simon
 */
public class TabsIndentsPanel extends javax.swing.JPanel {

    /** Creates new form TabsIndentsPanel */
    public TabsIndentsPanel(CodeStyle.Language language) {
        initComponents();
        statementContinuationIndent.putClientProperty(CategorySupport.OPTION_ID, EditorOptions.CC_FORMAT_STATEMENT_CONTINUATION_INDENT);
        indentCasesFromSwitch.putClientProperty(CategorySupport.OPTION_ID, EditorOptions.indentCasesFromSwitch);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        statementContinuationIndent = new javax.swing.JTextField();
        indentCasesFromSwitch = new javax.swing.JCheckBox();

        jLabel1.setLabelFor(statementContinuationIndent);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(TabsIndentsPanel.class, "PROP_indentEngine_CCFormatStatementContinuationIndent")); // NOI18N

        statementContinuationIndent.setText(org.openide.util.NbBundle.getMessage(TabsIndentsPanel.class, "TabsIndentsPanel.statementContinuationIndent.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(indentCasesFromSwitch, org.openide.util.NbBundle.getMessage(TabsIndentsPanel.class, "TabsIndentsPanel.indentCasesFromSwitch.text")); // NOI18N
        indentCasesFromSwitch.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        indentCasesFromSwitch.setMargin(new java.awt.Insets(0, 0, 0, 0));
        indentCasesFromSwitch.setOpaque(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(statementContinuationIndent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(indentCasesFromSwitch))
                .addContainerGap(167, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(statementContinuationIndent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(indentCasesFromSwitch)
                .addContainerGap(252, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    public static Category getController(CodeStyle.Language language) {
        Map<String,Object> force = new HashMap<String,Object>();
        return new CategorySupport(
                language,
                "LBL_TabsAndIndents",    // NOI18N   
                new TabsIndentsPanel(language), 
                NbBundle.getMessage(TabsIndentsPanel.class, "SAMPLE_TabsIndents"), // NOI18N
                force);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox indentCasesFromSwitch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField statementContinuationIndent;
    // End of variables declaration//GEN-END:variables

}
