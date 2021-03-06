/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.project.ui.customizer;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collection;
import java.util.LinkedHashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.php.project.connections.ConfigManager;
import org.netbeans.modules.php.project.connections.ConfigManager.Configuration;
import org.netbeans.modules.php.project.ui.customizer.PhpProjectProperties.RunAsType;
import org.netbeans.modules.php.project.ui.customizer.RunAsPanel.InsidePanel;

/**
 * @author Radek Matous
 */
public final class RunAsPanel extends JPanel {

    private static final long serialVersionUID = -5723489817914071L;
    private static final Font JL_PLAIN_FONT = new JLabel().getFont().deriveFont(Font.PLAIN);
    private static final Font JL_BOLD_FONT = JL_PLAIN_FONT.deriveFont(Font.BOLD);

    private final LinkedHashMap<String, InsidePanel> allInsidePanels;
    private final ComboModel comboBoxModel = new ComboModel();

    public RunAsPanel(InsidePanel[] cards) {
        assert cards != null;
        allInsidePanels = new LinkedHashMap<>();
        for (InsidePanel basicCard : cards) {
            this.allInsidePanels.put(basicCard.getDisplayName(), basicCard);
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (!comboBoxModel.isInitialized) {
            Collection<InsidePanel> insidePanels = allInsidePanels.values();
            initComboModel(insidePanels);
            for (InsidePanel insidePanel : insidePanels) {
                final JComboBox<String> comboBox = insidePanel.getRunAsCombo();
                comboBox.setModel(comboBoxModel);
            }
            comboBoxModel.setAsInitialized();
        }
    }

    private void initComboModel(Collection<InsidePanel> insidePanels) {
        comboBoxModel.removeAllElements();
        for (InsidePanel insidePanel : insidePanels) {
            add(insidePanel, insidePanel.getDisplayName());
            comboBoxModel.addElement(insidePanel.getDisplayName());
        }
    }

    private void selectInsidePanel(String name) {
        CardLayout cl = (CardLayout) (getLayout());
        InsidePanel current = allInsidePanels.get(name);
        Dimension preferredSize = current.getPreferredSize();
        setPreferredSize(preferredSize);
        cl.show(this, name);
    }

    public abstract static class InsidePanel extends JPanel implements ChangeListener {
        private final ConfigManager manager;

        public InsidePanel(ConfigManager manager) {
            this.manager = manager;
            manager.addChangeListener(this);
        }

        protected abstract RunAsType getRunAsType();

        protected abstract String getDisplayName();

        protected abstract JComboBox<String> getRunAsCombo();
        protected abstract JLabel getRunAsLabel();

        protected abstract void loadFields();

        protected abstract void validateFields();

        protected boolean isDefault() {
            return false;
        }

        //active configuration was changed
        @Override
        public final void stateChanged(ChangeEvent e) {
            String initPanelName = getValue(PhpProjectProperties.RUN_AS);
            if ((initPanelName == null && isDefault()) || (initPanelName != null && initPanelName.equals(getRunAsType().name()))) {
                final JComboBox comboBox = getRunAsCombo();
                comboBox.getModel().setSelectedItem(getDisplayName());
            }
        }

        protected final String getCurrentValue(String propertyName) {
            return getValue(currentCfg(), propertyName);
        }

        protected final String getDefaultValue(String propertyName) {
            return getValue(defaultCfg(), propertyName);
        }

        protected final String getValue(String propertyName) {
            String value = getCurrentValue(propertyName);
            value = value == null ? getDefaultValue(propertyName) : value;
            return value;
        }

        protected final void markAsModified(JComponent label, String propertyName, String value) {
            final String defaultValue = getDefaultValue(propertyName);
            if (currentCfg().isDefault() || value.equals(defaultValue) /*|| defaultValue == null*/) {
                label.setFont(RunAsPanel.JL_PLAIN_FONT);
            } else {
                label.setFont(RunAsPanel.JL_BOLD_FONT);
            }
        }

        protected final String getValue(Configuration configuration, String propertyName) {
            return configuration.getValue(propertyName);
        }

        protected final void putValue(Configuration configuration, String propertyName, String value) {
            configuration.putValue(propertyName, value);
        }

        protected final void putValue(String propertyName, String value) {
            value = value != null ? value.trim() : ""; // NOI18N

            if (!currentCfg().isDefault() && value.equals(getDefaultValue(propertyName))) {
                // default value, do not store as such
                value = null;
            }
            putValue(currentCfg(), propertyName, value);
        }

        protected final void putValueAndMarkAsModified(JLabel label, String propertyName, String value) {
            value = value != null ? value.trim() : ""; // NOI18N

            putValue(propertyName, value);
            markAsModified(label, propertyName, value);
        }

        protected final Configuration currentCfg() {
            return getManager().currentConfiguration();
        }

        protected final Configuration cfgFor(String activeConfig) {
            return getManager().configurationFor(activeConfig);
        }

        protected final Configuration defaultCfg() {
            return getManager().defaultConfiguration();
        }

        protected final ConfigManager getManager() {
            return manager;
        }

        protected abstract class TextFieldUpdater implements DocumentListener {
            private final JLabel label;
            private final JTextField field;
            private final String propName;

            public TextFieldUpdater(String propName, JLabel label, JTextField field) {
                this.propName = propName;
                this.label = label;
                this.field = field;
            }

            protected abstract String getDefaultValue();

            @Override
            public final void insertUpdate(DocumentEvent e) {
                processUpdate();
            }

            @Override
            public final void removeUpdate(DocumentEvent e) {
                processUpdate();
            }

            @Override
            public final void changedUpdate(DocumentEvent e) {
                processUpdate();
            }

            protected final String getPropName() {
                return propName;
            }

            // can be overriden
            protected void processUpdate() {
                putValue(propName, getPropValue());
                markAsModified(label, propName, getPropValue());
                validateFields();
            }

            // can be overriden
            protected String getPropValue() {
                return field.getText();
            }
        }
    }

    private class ComboModel extends DefaultComboBoxModel<String> {

        private static final long serialVersionUID = -68784654654657987L;

        private boolean isInitialized;


        private void setAsInitialized() {
            isInitialized = true;
        }

        @Override
        public void setSelectedItem(Object anObject) {
            super.setSelectedItem(anObject);
            if (isInitialized) {
                String name = (String) anObject;
                selectInsidePanel(name);
                InsidePanel current = allInsidePanels.get(name);
                if (current != null) {
                    current.loadFields();
                    current.validateFields();
                    current.putValue(current.currentCfg(), PhpProjectProperties.RUN_AS, current.getRunAsType().name());
                    current.markAsModified(current.getRunAsLabel(), PhpProjectProperties.RUN_AS, current.getRunAsType().name());
                }
            }
        }
    }
}
