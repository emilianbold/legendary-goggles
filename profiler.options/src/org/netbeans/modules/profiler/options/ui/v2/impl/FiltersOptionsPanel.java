/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.profiler.options.ui.v2.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import org.netbeans.lib.profiler.ui.results.PackageColor;
import org.netbeans.lib.profiler.ui.results.PackageColorer;
import org.netbeans.lib.profiler.ui.swing.ProfilerTable;
import org.netbeans.lib.profiler.ui.swing.ProfilerTableContainer;
import org.netbeans.lib.profiler.ui.swing.SmallButton;
import org.netbeans.lib.profiler.ui.swing.renderer.LabelRenderer;
import org.netbeans.modules.profiler.api.ProfilerIDESettings;
import org.netbeans.modules.profiler.api.icons.GeneralIcons;
import org.netbeans.modules.profiler.api.icons.Icons;
import org.netbeans.modules.profiler.options.ui.v2.ProfilerOptionsPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jiri Sedlacek
 */
@ServiceProvider( service = ProfilerOptionsPanel.class, position = 15 )
public final class FiltersOptionsPanel extends ProfilerOptionsPanel {
    
    private final List<PackageColor> colors = new ArrayList();
    private final ColorsTableModel colorsModel = new ColorsTableModel();
    
    
    public FiltersOptionsPanel() {
        initUI();
    }

    
    public String getDisplayName() {
        return "Filters";
    }

    public void storeTo(ProfilerIDESettings settings) {
        PackageColorer.setRegisteredColors(colors);
        for (Window w : Window.getWindows()) w.repaint();
    }

    public void loadFrom(ProfilerIDESettings settings) {
        colors.clear();
        colors.addAll(PackageColorer.getRegisteredColors());
        colorsModel.fireTableDataChanged();
    }

    public boolean equalsTo(ProfilerIDESettings settings) {
        return Objects.equals(PackageColorer.getRegisteredColors(), colors);
    }
    
    
    private void initUI() {
        setLayout(new GridBagLayout());
        
        GridBagConstraints c;
        int y = 0;
        int htab = 8;
        int vgap = 5;
        
        Separator dataTransferSeparator = new Separator("Results Coloring");
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = y++;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, vgap * 2, 0);
        add(dataTransferSeparator, c);
        
        final String colorString = "ABCabc123"; // NOI18N
        final ProfilerTable colorsTable = new ProfilerTable(colorsModel, false, false, null);
        colorsTable.setMainColumn(1);
        colorsTable.setFitWidthColumn(1);
        LabelRenderer stringRenderer = new LabelRenderer();
        colorsTable.setColumnRenderer(0, stringRenderer);
        colorsTable.setColumnRenderer(1, stringRenderer);
        LabelRenderer colorRenderer = new LabelRenderer() {
            private final Color _fg = new JTable().getForeground();
            private Color fg;
            {
                setText(colorString); // NOI18N
                setHorizontalAlignment(TRAILING);
            }
            public void setValue(Object value, int row) {
                fg = (Color)value;
            }
            public void setForeground(Color color) {
                if (Objects.equals(color, _fg)) super.setForeground(fg);
                else super.setForeground(color);
            }
        };
        colorsTable.setColumnRenderer(2, colorRenderer);
        stringRenderer.setValue("PLACEHOLDER FILTER NAME", -1); // NOI18N
        colorsTable.setDefaultColumnWidth(0, stringRenderer.getPreferredSize().width);
        stringRenderer.setValue(colorString, -1);
        colorsTable.setDefaultColumnWidth(2, stringRenderer.getPreferredSize().width + 10);
        ProfilerTableContainer colorsContainer = new ProfilerTableContainer(colorsTable, true, null);
        colorsContainer.setPreferredSize(new Dimension(1, 1));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = y;
        c.weightx = 1;
        c.weighty = 1;
        c.gridheight = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, htab, 0, 0);
        add(colorsContainer, c);
        
        JButton addButton = new SmallButton("A");
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = y++;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, htab, 0, 0);
        add(addButton, c);
        
        JButton editButton = new SmallButton("E") {
            protected void fireActionPerformed(ActionEvent e) {
                int row = colorsTable.convertRowIndexToModel(colorsTable.getSelectedRow());
                PackageColor selected = colors.get(row);
                PackageColor edited = ColorCustomizer.customize(selected);
                if (edited != null) {
                    selected.setName(edited.getName());
                    selected.setValue(edited.getValue());
                    selected.setColor(edited.getColor());
                    colorsModel.fireTableDataChanged();
                }
            }
        };
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = y++;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, htab, 0, 0);
        add(editButton, c);
        
        JButton removeButton = new SmallButton("R");
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = y++;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, htab, vgap * 2, 0);
        add(removeButton, c);
        
        JButton upButton = new SmallButton(Icons.getIcon(GeneralIcons.UP));
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = y++;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, htab, 0, 0);
        add(upButton, c);
        
        JButton downButton = new SmallButton(Icons.getIcon(GeneralIcons.DOWN));
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = y++;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, htab, 0, 0);
        add(downButton, c);
        
    }
    
    
    private class ColorsTableModel extends AbstractTableModel {
        
        public String getColumnName(int column) {
            switch (column) {
                case 0: return "Filter";
                case 1: return "Packages";
                case 2: return "Color";
                default: return null;
            }
        }

        public int getRowCount() {
            return colors.size();
        }

        public int getColumnCount() {
            return 3;
        }

        public Object getValueAt(int rowIndex, int column) {
            switch (column) {
                case 0: return colors.get(rowIndex).getName();
                case 1: return colors.get(rowIndex).getValue();
                case 2: return colors.get(rowIndex).getColor();
                default: return null;
            }
        }
        
    }
    
    
    private static class ColorCustomizer {
        
        static PackageColor customize(PackageColor color) {
            final PackageColor customized = new PackageColor(color);
            JTextField nameF = new JTextField(customized.getName());
            JTextArea valueA = new JTextArea(customized.getValue());
            valueA.setRows(8);
            valueA.setColumns(45);
            valueA.setLineWrap(true);
            valueA.setWrapStyleWord(true);
            JButton colorB = new JButton() {
                {
                    setIcon(customized.getIcon(16, 12));
                }
                protected void fireActionPerformed(ActionEvent e) {
                    Color c = JColorChooser.showDialog(this, "Choose Filter Color", customized.getColor());
                    if (c != null) {
                        customized.setColor(c);
                        repaint();
                    }
                }
            };
            
            JPanel p = new JPanel(new GridBagLayout());
            GridBagConstraints c;
            int hgap = 10;
            int htab = 5;
            int vgap = 5;
            int y = 0;
            
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = y;
            c.insets = new Insets(vgap * 2, hgap, 0, 0);
            p.add(new JLabel("Name:"), c);
            
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = y;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(vgap * 2, htab, 0, 0);
            p.add(nameF, c);
            
            c = new GridBagConstraints();
            c.gridx = 2;
            c.gridy = y;
            c.insets = new Insets(vgap * 2, hgap, 0, 0);
            p.add(new JLabel("Color:"), c);
            
            c = new GridBagConstraints();
            c.gridx = 3;
            c.gridy = y++;
            c.insets = new Insets(vgap * 2, htab, 0, hgap);
            p.add(colorB, c);
            
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = y;
            c.anchor = GridBagConstraints.NORTHWEST;
            c.insets = new Insets(vgap * 2, hgap, 0, 0);
            p.add(new JLabel("Value:"), c);
            
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = y++;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.weightx = 1;
            c.weighty = 1;
            c.anchor = GridBagConstraints.NORTHWEST;
            c.fill = GridBagConstraints.BOTH;
            c.insets = new Insets(vgap * 2, htab, vgap, hgap);
            p.add(new JScrollPane(valueA), c);
            
            HelpCtx helpCtx = new HelpCtx("PackageColorCustomizer.HelpCtx"); // NOI18N
            DialogDescriptor dd = new DialogDescriptor(p, "Edit Filter", true,
                                  new Object[] { DialogDescriptor.OK_OPTION, DialogDescriptor.CANCEL_OPTION }, 
                                  DialogDescriptor.OK_OPTION, DialogDescriptor.DEFAULT_ALIGN,
                                  helpCtx, null);
            if (DialogDisplayer.getDefault().notify(dd) != DialogDescriptor.OK_OPTION) return null;
            
            customized.setName(nameF.getText().trim());
            customized.setValue(valueA.getText().trim());
        
            return customized;
        }
        
    }
    
}
