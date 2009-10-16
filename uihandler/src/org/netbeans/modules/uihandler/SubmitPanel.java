/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.uihandler;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.ChoiceView;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 * @author  Jaroslav Tulach
 */
public class SubmitPanel extends javax.swing.JPanel 
        implements ExplorerManager.Provider, PropertyChangeListener, CaretListener, Comparator<Object>, Runnable {

    private EditorKit betterXMLKit;

    public SubmitPanel() {
        manager = new ExplorerManager();

        initComponents();
        RequestProcessor.getDefault().post(this);
    }

    public void run() {
        if (EventQueue.isDispatchThread()) {
            try {
                String content = text.getDocument().getText(0, text.getDocument().getLength());
                text.setEditorKit(betterXMLKit);
                setText(content);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            betterXMLKit = CloneableEditorSupport.getEditorKit("text/xml");
            if (betterXMLKit != null) {
                EventQueue.invokeLater(this);
            }
        }
    }
   
    @Override
    public void addNotify() {
        super.addNotify();
        
        text.addCaretListener(this);
        manager.addPropertyChangeListener(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        try {
            remove = new javax.swing.JComboBox();
            jLabel1 = new javax.swing.JLabel();
            jScrollPane2 = new javax.swing.JScrollPane();
            text = new javax.swing.JEditorPane();

            setPreferredSize(new java.awt.Dimension(640, 480));

            remove.setModel(null );
        } catch (Exception ex) {
        }
        remove = new ChoiceView();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(SubmitPanel.class, "SubmitPanel.jLabel1.text_1")); // NOI18N

        text.setEditable(false);
        jScrollPane2.setViewportView(text);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(remove, 0, 510, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(remove, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public ExplorerManager getExplorerManager() {
        return manager;
    }

    void setText(String content){
        try {
            text.getDocument().remove(0, text.getDocument().getLength());
            text.getDocument().insertString(0, content, null);
            text.getCaret().setDot(0);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public void propertyChange(PropertyChangeEvent ev) {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(ev.getPropertyName())) {
            Node[] arr = getExplorerManager().getSelectedNodes();
            if (arr.length != 1) {
                return;
            }

            Object o = arr[0].getValue("offset"); // NOI18N
            if (o instanceof Integer) {
                text.removeCaretListener(this);
                text.getCaret().setDot((Integer)o);
                text.addCaretListener(this);
                text.requestFocus();
            }
        }
    }
    
    public void caretUpdate(CaretEvent e) {
        int offset = text.getCaretPosition();
        
        Node[] arr = getExplorerManager().getRootContext().getChildren().getNodes(true);
        int index = Arrays.binarySearch(arr, offset, this);
        if (index < -1) {
            index = -index - 2;
        }
        if (index >= 0 && index < arr.length) {
            try {
                getExplorerManager().removePropertyChangeListener(this);
                getExplorerManager().setSelectedNodes(new Node[]{arr[index]});
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                getExplorerManager().addPropertyChangeListener(this);
            }
        }
    }

    public int compare(Object o1, Object o2) {
        if (o1 instanceof Node) o1 = ((Node)o1).getValue("offset"); // NOI18N
        if (o2 instanceof Node) o2 = ((Node)o2).getValue("offset"); // NOI18N
        
        if (o1 instanceof Integer && o2 instanceof Integer) {
            return ((Integer)o1) - ((Integer)o2);
        }
        throw new IllegalArgumentException("o1: " + o1 + " o2: " + o2); // NOI18N
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JComboBox remove;
    private javax.swing.JEditorPane text;
    // End of variables declaration//GEN-END:variables
    
    
    private ExplorerManager manager;
}
