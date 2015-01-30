/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2015 Oracle and/or its affiliates. All rights reserved.
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
package org.netbeans.lib.profiler.ui.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import org.netbeans.lib.profiler.ui.UIUtils;
import org.netbeans.lib.profiler.ui.components.CloseButton;
import org.netbeans.modules.profiler.api.ProfilerDialogs;
import org.netbeans.modules.profiler.api.icons.GeneralIcons;
import org.netbeans.modules.profiler.api.icons.Icons;

/**
 *
 * @author Jiri Sedlacek
 */
public final class SearchUtils {
    
    public static final String FIND_ACTION_KEY = "find-action-key"; // NOI18N
    
    public static boolean findString(ProfilerTable table, String text, boolean next) {
        int rowCount = table.getRowCount();
        
        if (rowCount == 0) {
            ProfilerDialogs.displayWarning("No data to search.", "Find", null);
            return false;
        } else if (rowCount == 1) {
            return false;
        }
        
        int selectedRow = table.getSelectedRow();
        boolean fromSelection = selectedRow != -1;
        if (!fromSelection) selectedRow = next ? 0 : rowCount - 1;
        else selectedRow = moveRow(selectedRow, rowCount, next, false);
        
        int mainColumn = table.getMainColumn();
        
        int searchSteps = fromSelection ? rowCount - 1 : rowCount;
        for (int i = 0; i < searchSteps; i++) {
            if (table.getStringValue(selectedRow, mainColumn).contains(text)) {
                table.selectRow(selectedRow, true);
                return true;
            }
            selectedRow = moveRow(selectedRow, rowCount, next, fromSelection && i < searchSteps - 1);
            if (selectedRow == -1) return false;
        }
        
        ProfilerDialogs.displayInfo("Searched string not found.", "Find", null);
        return false;
    }
    
    private static int moveRow(int row, int rowCount, boolean next, boolean notifyMargin) {
        int newRow = next ? row + 1 : row - 1;
        
        if (newRow == -1 || newRow == rowCount) {
//            if (notifyMargin && !ProfilerDialogs.displayConfirmation(getEndMessage(next), "Find")) newRow = -1;
//            else newRow = next ? 0 : rowCount - 1;
            newRow = next ? 0 : rowCount - 1;
        }
        
        return newRow;
    }
    
//    private static String getEndMessage(boolean next) {
//        return next ? "Reached end of view. Continue from top?" :
//                      "Reached top of view. Continue from end?";
//    }
    
    
    public static JComponent createSearchPanel(final ProfilerTable table) {
        JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
        if (UIUtils.isGTKLookAndFeel() || UIUtils.isNimbusLookAndFeel())
                toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.LINE_AXIS));
        toolbar.setBorder(BorderFactory.createEmptyBorder(1, 2, 0, 2));
        toolbar.setBorderPainted(false);
        toolbar.setRollover(true);
        toolbar.setFloatable(false);
        toolbar.setOpaque(false);
        
        toolbar.add(Box.createHorizontalStrut(6));
        toolbar.add(new JLabel("Find:"));
        toolbar.add(Box.createHorizontalStrut(3));
        
        final EditableHistoryCombo combo = new EditableHistoryCombo();        
        final JTextComponent textC = combo.getTextComponent();
        
        toolbar.add(combo);
        
        toolbar.add(Box.createHorizontalStrut(5));
        
        KeyStroke escKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke prevKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_MASK);
        KeyStroke nextKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        
        final JButton prev = new JButton("Previous", Icons.getIcon(GeneralIcons.FIND_PREVIOUS)) {
            protected void fireActionPerformed(ActionEvent e) {
                super.fireActionPerformed(e);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        String search = getSearchString(combo);
                        if (search == null || search.isEmpty()) return;
                        if (findString(table, search, false)) combo.addItem(search);
                    }
                });
            }
        };
        String prevAccelerator = UIUtils.keyAcceleratorString(prevKey);
        prev.setToolTipText("Find previous occurence (" + prevAccelerator + ")");
        prev.setEnabled(false);
        toolbar.add(prev);
        
        toolbar.add(Box.createHorizontalStrut(2));
        
        final JButton next = new JButton("Next", Icons.getIcon(GeneralIcons.FIND_NEXT)) {
            protected void fireActionPerformed(ActionEvent e) {
                super.fireActionPerformed(e);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        String search = getSearchString(combo);
                        if (search == null || search.isEmpty()) return;
                        if (findString(table, search, true)) combo.addItem(search);
                    }
                });
            }
        };
        String nextAccelerator = UIUtils.keyAcceleratorString(nextKey);
        next.setToolTipText("Find next occurence (" + nextAccelerator + ")");
        next.setEnabled(false);
        toolbar.add(next);
        
        toolbar.add(Box.createHorizontalStrut(2));
        
        combo.setOnTextChangeHandler(new Runnable() {
            public void run() {
                boolean enable = !(table instanceof ProfilerTreeTable) && !combo.getText().trim().isEmpty();// NOTE: temporarily disabled for TreeTables
                prev.setEnabled(enable);
                next.setEnabled(enable);
            }
        });
        
        final JPanel panel = new JPanel(new BorderLayout()) {
            public void setVisible(boolean visible) {
                super.setVisible(visible);
                if (!visible) table.requestFocusInWindow();
            }
            public boolean requestFocusInWindow() {
                if (textC != null) textC.selectAll();
                return combo.requestFocusInWindow();
            }
        };
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("controlShadow"))); // NOI18N
        panel.add(toolbar, BorderLayout.CENTER);
        
        final Runnable hider = new Runnable() { public void run() { panel.setVisible(false); } };
        JButton closeButton = CloseButton.create(hider);
        String escAccelerator = UIUtils.keyAcceleratorString(escKey);
        closeButton.setToolTipText("Close Find sidebar (" + escAccelerator + ")");
        panel.add(closeButton, BorderLayout.EAST);
        
        String HIDE = "hide-action"; // NOI18N
        InputMap map = panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        Action hiderAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) { hider.run(); }
        };
        panel.getActionMap().put(HIDE, hiderAction);
        map.put(escKey, HIDE);
        
        if (textC != null) {
            map = textC.getInputMap();
            String NEXT = "search-next-action"; // NOI18N
            Action nextAction = new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    if (combo.isPopupVisible()) combo.hidePopup();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() { if (next.isEnabled()) next.doClick(); }
                    });
                }
            };
            textC.getActionMap().put(NEXT, nextAction);
            map.put(nextKey, NEXT);

            String PREV = "search-prev-action"; // NOI18N
            Action prevAction = new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    if (combo.isPopupVisible()) combo.hidePopup();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() { if (next.isEnabled()) prev.doClick(); }
                    });
                }
            };
            textC.getActionMap().put(PREV, prevAction);
            map.put(prevKey, PREV);
        }
        
        return panel;
    }
    
    private static String getSearchString(EditableHistoryCombo combo) {
        String search = combo.getText();
        return search == null ? null : search.trim();
    }
    
}
