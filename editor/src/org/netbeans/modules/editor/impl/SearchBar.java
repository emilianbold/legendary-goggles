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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.editor.impl;

import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.logging.Level;
import javax.swing.event.PopupMenuEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.editor.*;
import javax.swing.Action;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.Timer;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.editor.lib2.search.EditorFindSupport;
import org.openide.awt.Mnemonics;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;


/**
 * This is an implementation of a Firefox(TM) style Incremental Search Side Bar.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class SearchBar extends JPanel {
    
    private static final Logger LOG = Logger.getLogger(SearchBar.class.getName());
    private static final boolean CLOSE_ON_ENTER = Boolean.getBoolean("org.netbeans.modules.editor.search.closeOnEnter"); // NOI18N

    private static final Insets BUTTON_INSETS = new Insets(2, 1, 0, 1);
    private static final Color NOT_FOUND = Color.RED.darker();
    private static final Color INVALID_REGEXP = Color.red;
    
    // Delay times for incremental search [ms]
    private static final int SEARCH_DELAY_TIME_LONG = 300; // < 3 chars
    private static final int SEARCH_DELAY_TIME_SHORT = 20; // >= 3 chars
    
    private static final int defaultIncremantalSearchComboWidth = 200;
    private static final int maxIncremantalSearchComboWidth = 350;
    
    public static final String INCREMENTAL_SEARCH_FORWARD = "incremental-search-forward";
    public static final String INCREMENTAL_SEARCH_BACKWARD = "incremental-search-backward";

    private JTextComponent component;
    private JButton closeButton;
    private JButton expandButton;
    private JLabel findLabel;
    private JComboBox incrementalSearchComboBox;
    private JTextField incrementalSearchTextField;
    private DocumentListener incrementalSearchTextFieldListener;
    private JButton findNextButton;
    private JButton findPreviousButton;
    private JCheckBox matchCaseCheckBox;
    private JCheckBox wholeWordsCheckBox;
    private JCheckBox regexpCheckBox;
    private JCheckBox highlightCheckBox;
    private JCheckBox wrapAroundCheckBox;
    private Map<Object, Object> findProps;
    private JPopupMenu expandPopup;
    private JPanel padding;
    private boolean searched = false;
    
    /**
     * contains everything that is in Search bar and is possible to move to expand popup
     */
    private final List<Component> inBar = new ArrayList<Component>();
    /**
     * components moved to popup
     */
    private final LinkedList<Component> inPopup = new LinkedList<Component>();
    /**
     * defines index of all components in Search bar
     */
    private final List<Component> barOrder = new ArrayList<Component>();
    private boolean isPopupGoingToShow = false;
    private boolean isPopupShown = false;
    
    @SuppressWarnings("unchecked")
    public SearchBar(final JTextComponent component) {
        this.component = component;
        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (e.getOppositeComponent() instanceof JRootPane) {
                    // Hack for linux where invoking Find from main menu caused focus gained on editor
                    // evein when openning quick search
                    return;
                }
                looseFocus();
            }
        });
        FindSupport findSupport = FindSupport.getFindSupport();
        findProps = new HashMap<Object, Object>(findSupport.getFindProperties());
        
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setFocusCycleRoot(true);
        Color bgColor = getBackground();
        bgColor = new Color( Math.max( 0, bgColor.getRed() - 20 ),
                             Math.max( 0, bgColor.getGreen() - 20 ),
                             Math.max( 0, bgColor.getBlue() - 20 ) );        
        setBackground(bgColor);
        setForeground(UIManager.getColor("textText")); //NOI18N
        
        PropertyChangeListener pcl = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt == null || !"keymap".equals(evt.getPropertyName())) { // NOI18N
                    return;
                }

                Keymap keymap = component.getKeymap();

                if (keymap instanceof MultiKeymap) {
                    MultiKeymap multiKeymap = (MultiKeymap) keymap;

                    Action[] actions = component.getActions();
                    for(Action action:actions) {
                        // Discover the keyStrokes for incremental-search-forward
                        String actionName = (String) action.getValue(Action.NAME);
                        if (actionName == null) {
                            LOG.log(Level.WARNING, "SearchBar: Null Action.NAME property of action: {0}\n", action);
                        }
                        //keystroke for incremental search forward and
                        //keystroke to add standard search next navigation in search bar (by default F3 on win)
                        else if (actionName.equals(INCREMENTAL_SEARCH_FORWARD) || actionName.equals(BaseKit.findNextAction)) {
                            Action incrementalSearchForwardAction = action;
                            KeyStroke[] keyStrokes = multiKeymap.getKeyStrokesForAction(incrementalSearchForwardAction);
                            if (keyStrokes != null) {
                                InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                                for(KeyStroke ks : keyStrokes) {
                                    LOG.log(Level.FINE, "found forward search action, {0}", ks); //NOI18N
                                    inputMap.put(ks, actionName);
                                }
                                getActionMap().put(actionName,
                                    new AbstractAction() {
                                    @Override
                                        public void actionPerformed(ActionEvent e) {
                                            findNext();
                                        }
                                    });
                            }
                        }
                        // Discover the keyStrokes for incremental-search-backward
                        // Discover the keyStrokes for search-backward
                        else if (actionName.equals(INCREMENTAL_SEARCH_BACKWARD) || actionName.equals(BaseKit.findPreviousAction)) {
                            Action incrementalSearchBackwardAction = action;
                            KeyStroke[] keyStrokes = multiKeymap.getKeyStrokesForAction(incrementalSearchBackwardAction);
                            if (keyStrokes != null) {
                                InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                                for(KeyStroke ks : keyStrokes) {
                                    LOG.log(Level.FINE, "found backward search action, {0}", ks); //NOI18N
                                    inputMap.put(ks, actionName);
                                }
                                getActionMap().put(actionName,
                                    new AbstractAction() {
                                    @Override
                                        public void actionPerformed(ActionEvent e) {
                                            findPrevious();
                                        }
                                    });
                            }
                        }
                    }
                }
            }
        };
        component.addPropertyChangeListener(pcl);
        pcl.propertyChange(new PropertyChangeEvent(this, "keymap", null, null));

        // ESCAPE to put focus back in the editor
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true),
            "loose-focus"); // NOI18N
        getActionMap().put("loose-focus", // NOI18N
            new AbstractAction() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    if (!CLOSE_ON_ENTER) {
                        if (!searched) {
                            findNext();
                        }
                    }
                    looseFocus();
                }
            });

        closeButton = new JButton(ImageUtilities.loadImageIcon("org/netbeans/modules/editor/resources/find_close.png", false)); // NOI18N
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                looseFocus();
            }
        });
        closeButton.setToolTipText(NbBundle.getMessage(SearchBar.class, "TOOLTIP_CloseIncrementalSearchSidebar")); // NOI18N
        processButton(closeButton);

        expandButton = new JButton(ImageUtilities.loadImageIcon("org/netbeans/modules/editor/resources/find_expand.png", false)); // NOI18N
        expandButton.setMnemonic(NbBundle.getMessage(SearchBar.class, "CTL_ExpandButton_Mnemonic").charAt(0)); // NOI18N
        processButton(expandButton);
        expandButton.addActionListener(new ActionListener() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    boolean state = !isPopupShown;
                    isPopupShown = state;
                    if (state) {
                        showExpandedMenu();
                    } else {
                        hideExpandedMenu();
                    }
                }
        });
        
        findLabel = new JLabel(); 
        Mnemonics.setLocalizedText( findLabel, NbBundle.getMessage(SearchBar.class, "CTL_Find")); // NOI18N
        
        // configure incremental search text field
        incrementalSearchComboBox = new JComboBox()
        {
            public @Override Dimension getMinimumSize() {
                return getPreferredSize();
            }

            public @Override Dimension getMaximumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getPreferredSize() {
                int width;
                int editsize = this.getEditor().getEditorComponent().getPreferredSize().width + 10;
                if (editsize > defaultIncremantalSearchComboWidth && editsize <  maxIncremantalSearchComboWidth)
                    width = editsize;
                else if (editsize >= maxIncremantalSearchComboWidth)
                    width = maxIncremantalSearchComboWidth;
                else width = defaultIncremantalSearchComboWidth;
                return new Dimension(width,
                        super.getPreferredSize().height);
            }
        };
        
        findLabel.setLabelFor(incrementalSearchComboBox);
        incrementalSearchComboBox.setEditable(true);
        incrementalSearchTextField = (JTextField) incrementalSearchComboBox.getEditor().getEditorComponent();
        incrementalSearchTextField.setToolTipText(NbBundle.getMessage(SearchBar.class, "TOOLTIP_IncrementalSearchText")); // NOI18N

        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                incrementalSearch();
            }
        };

        final Timer searchDelayTimer = new Timer(SEARCH_DELAY_TIME_LONG, al);
        searchDelayTimer.setRepeats(false);
        
        // listen on text change
        incrementalSearchTextFieldListener = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                searched = false;
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                searched = false;
                // text changed - attempt incremental search
                computeLayout();
                if(incrementalSearchTextField.getText().length() > 3) searchDelayTimer.setInitialDelay(SEARCH_DELAY_TIME_SHORT);
                searchDelayTimer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searched = false;
                // text changed - attempt incremental search
                computeLayout();
                if(incrementalSearchTextField.getText().length() <= 3) searchDelayTimer.setInitialDelay(SEARCH_DELAY_TIME_LONG);
                searchDelayTimer.restart();
            }
        };
        incrementalSearchTextField.getDocument().addDocumentListener(incrementalSearchTextFieldListener);
        
        // flatten the action map for the text field to allow removal
        ActionMap origActionMap = incrementalSearchTextField.getActionMap();
        ActionMap newActionMap = new ActionMap();
        for (Object key : origActionMap.allKeys()) {
            newActionMap.put(key, origActionMap.get(key));
        }
        incrementalSearchTextField.setActionMap(newActionMap);

        // ENTER to find next
        incrementalSearchTextField.getInputMap().put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
            "incremental-find-next"); // NOI18N
        incrementalSearchTextField.getActionMap().put("incremental-find-next", // NOI18N
            new AbstractAction() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    if (!CLOSE_ON_ENTER && !searched) {
                        findNext();
                    }
                    findNext();
                    if (CLOSE_ON_ENTER) {
                        looseFocus();
                    }
                }});
        // Shift+ENTER to find previous
        incrementalSearchTextField.getInputMap()
                                  .put(KeyStroke.getKeyStroke(
                KeyEvent.VK_ENTER, InputEvent.SHIFT_MASK, true),
            "incremental-find-previous"); // NOI18N
        incrementalSearchTextField.getActionMap().put("incremental-find-previous", // NOI18N
            new AbstractAction() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    findPrevious();
                    if (CLOSE_ON_ENTER) {
                        looseFocus();
                    }
                }});
        incrementalSearchTextField.getActionMap().remove("toggle-componentOrientation"); // NOI18N

        // Treat Emacs profile specially in order to fix #191895
        if (getCurrentKeyMapProfile().startsWith("Emacs")) { // NOI18N
            class JumpOutOfSearchAction extends AbstractAction {
                private String actionName;
                public JumpOutOfSearchAction(String n) {
                    actionName = n;
                }
                @Override
                public void actionPerformed(ActionEvent e) {
                    looseFocus();
                    ActionEvent ev = new ActionEvent(component, e.getID(), e.getActionCommand(), e.getModifiers());
                    Action action = component.getActionMap().get(actionName);
                    action.actionPerformed(ev);
                }
            }
            String actionName = "caret-begin-line"; // NOI18N
            Action a1 = new JumpOutOfSearchAction(actionName);
            incrementalSearchTextField.getActionMap().put(actionName, a1);
            actionName = "caret-end-line"; // NOI18N
            Action a2 = new JumpOutOfSearchAction(actionName);
            incrementalSearchTextField.getActionMap().put(actionName, a2);

            incrementalSearchTextField.getInputMap().put(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, InputEvent.CTRL_MASK, false), "caret-up-alt"); // NOI18N
            actionName = "caret-up"; // NOI18N
            Action a3 = new JumpOutOfSearchAction(actionName);
            incrementalSearchTextField.getActionMap().put("caret-up-alt", a3); // NOI18N

            incrementalSearchTextField.getInputMap().put(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, InputEvent.CTRL_MASK, false), "caret-down-alt"); // NOI18N
            actionName = "caret-down"; // NOI18N
            Action a4 = new JumpOutOfSearchAction(actionName);
            incrementalSearchTextField.getActionMap().put("caret-down-alt", a4); // NOI18N
        }
        // configure find next button
        findNextButton = new JButton(
            ImageUtilities.loadImageIcon("org/netbeans/modules/editor/resources/find_next.png", false)); // NOI18N
        Mnemonics.setLocalizedText( findNextButton, NbBundle.getMessage(SearchBar.class, "CTL_FindNext")); // NOI18N
        findNextButton.addActionListener(new ActionListener() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    findNext();
                }});
        processButton(findNextButton);

        // configure find previous button
        findPreviousButton = new JButton(
            ImageUtilities.loadImageIcon("org/netbeans/modules/editor/resources/find_previous.png", false)); // NOI18N
        Mnemonics.setLocalizedText(findPreviousButton, NbBundle.getMessage(SearchBar.class, "CTL_FindPrevious")); // NOI18N
        findPreviousButton.addActionListener(new ActionListener() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    findPrevious();
                }
            });
        processButton(findPreviousButton);

        // configure match case check box
        matchCaseCheckBox = new JCheckBox();
        matchCaseCheckBox.setOpaque(false);
        Mnemonics.setLocalizedText(matchCaseCheckBox, NbBundle.getMessage(SearchBar.class, "CTL_MatchCase")); // NOI18N
        matchCaseCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchMatchCase();
                incrementalSearch();
            }
        });
        matchCaseCheckBox.setSelected(getMatchCase());
        processButton(matchCaseCheckBox);

        wholeWordsCheckBox = new JCheckBox();
        wholeWordsCheckBox.setOpaque(false);
        Mnemonics.setLocalizedText(wholeWordsCheckBox, NbBundle.getMessage(SearchBar.class, "CTL_WholeWords")); // NOI18N
        wholeWordsCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchWholeWords();
                incrementalSearch();
            }
        });
        wholeWordsCheckBox.setSelected(getWholeWords());
        wholeWordsCheckBox.setEnabled(!getRegExp());
        processButton(wholeWordsCheckBox);
        
        regexpCheckBox = new JCheckBox();
        regexpCheckBox.setOpaque(false);
        Mnemonics.setLocalizedText(regexpCheckBox, NbBundle.getMessage(SearchBar.class, "CTL_Regexp")); // NOI18N
        regexpCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchRegExp();
                // Switch other checkbozes on/off
                wholeWordsCheckBox.setEnabled(!regexpCheckBox.isSelected());
                incrementalSearch();
            }
        });
        regexpCheckBox.setSelected(getRegExp());
        processButton(regexpCheckBox);
        
        highlightCheckBox = new JCheckBox();
        highlightCheckBox.setOpaque(false);
        Mnemonics.setLocalizedText(highlightCheckBox, NbBundle.getMessage(SearchBar.class, "CTL_Highlight")); // NOI18N
        highlightCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchHighlightResults();
                incrementalSearch();
            }
        });
        highlightCheckBox.setSelected(getHighlightResults());
        processButton(highlightCheckBox);
        
        wrapAroundCheckBox = new JCheckBox();
        wrapAroundCheckBox.setOpaque(false);
        Mnemonics.setLocalizedText(wrapAroundCheckBox, NbBundle.getMessage(SearchBar.class, "CTL_WrapAround")); // NOI18N
        wrapAroundCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchWrapAround();
                incrementalSearch();
            }
        });
        wrapAroundCheckBox.setSelected(getWrapAround());
        processButton(wrapAroundCheckBox);
        
        expandPopup = new JPopupMenu();
        expandPopup.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // check if it was canceled by click on expand button
                if (expandButton.getMousePosition() == null) {
                    expandButton.setContentAreaFilled(false);
                    expandButton.setBorderPainted(false);
                    isPopupShown = false;
                }
            }
        });
        
        // padding at the end of the toolbar
        JPanel spacer = new JPanel();
        spacer.setSize(4, 4);
        spacer.setMaximumSize(new Dimension(4,4));
        spacer.setOpaque(false);
        add(spacer);
        
        add(findLabel);
        add(incrementalSearchComboBox);
        
        JToolBar.Separator leftSeparator = new JToolBar.Separator();
        leftSeparator.setOrientation(SwingConstants.VERTICAL);
        add(leftSeparator);
        
        add(findPreviousButton);
        add(findNextButton);
        
        JToolBar.Separator rightSeparator = new JToolBar.Separator();
        rightSeparator.setOrientation(SwingConstants.VERTICAL);
        add(rightSeparator);
        
        add(matchCaseCheckBox);
        add(wholeWordsCheckBox);
        add(regexpCheckBox);
        add(highlightCheckBox);
        add(wrapAroundCheckBox);
        add(expandButton);
        
        // padding at the end of the toolbar
        padding = new JPanel();
        padding.setOpaque(false);
        add(padding);
        add(closeButton);
        
        makeBarExpandable();
        
        // initially not visible
        setVisible(false);
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                computeLayout();
            }
        });
    }
    
    // From org.netbeans.modules.editor.settings.storage.EditorSettingsImpl
    private static final String DEFAULT_PROFILE = "NetBeans"; //NOI18N
    private static final String FATTR_CURRENT_KEYMAP_PROFILE = "currentKeymap";      // NOI18N
    private static final String KEYMAPS_FOLDER = "Keymaps"; // NOI18N
    
    /* This method is verbatim copy from class
     * org.netbeans.modules.editor.settings.storage.EditorSettingsImpl
     * bacause we don't want to introduce the dependency between this module
     * and Editor Setting Storage module.
     */
    private String getCurrentKeyMapProfile () {
        String currentKeyMapProfile = null;
        FileObject fo = FileUtil.getConfigFile (KEYMAPS_FOLDER);
        if (fo != null) {
            Object o = fo.getAttribute (FATTR_CURRENT_KEYMAP_PROFILE);
            if (o instanceof String) {
                currentKeyMapProfile = (String) o;
            }
        }
        if (currentKeyMapProfile == null) {
            currentKeyMapProfile = DEFAULT_PROFILE;
        }
        return currentKeyMapProfile;
    }    
    private void makeBarExpandable() {
        inBar.add(matchCaseCheckBox);
        inBar.add(wholeWordsCheckBox);
        inBar.add(regexpCheckBox);
        inBar.add(highlightCheckBox);
        inBar.add(wrapAroundCheckBox);
        barOrder.addAll(Arrays.asList(this.getComponents()));
        remove(expandButton);
    }
    
    private void computeLayout() {
        Container parent = this.getParent();
        int parentWidth = parent.getWidth();
        int totalWidth = 0;
        
        boolean change = false;
        
        for (Component c : this.getComponents()) {
            if (c != padding) {
                totalWidth += c.getWidth();
            }
        }
        
        if (totalWidth <= parentWidth) {
            // enough space try to clear expand popup
            while (!inPopup.isEmpty()) {
                Component c = inPopup.getFirst();
                totalWidth += c.getWidth();
                
                if (totalWidth > parentWidth) {
                    break;
                }
                inPopup.removeFirst();
                inBar.add(c);
                expandPopup.remove(c);
                add(c, barOrder.indexOf(c));
                change = true;
            }
        } else {
            // lack of space
            while (totalWidth > parentWidth && !inBar.isEmpty()) {
                Component c = inBar.remove(inBar.size() - 1);
                inPopup.addFirst(c);
                remove(c);
                expandPopup.add(c, 0);
                totalWidth -= c.getWidth();
                change = true;
            }
        }
        
        if (change) {
            if (inPopup.isEmpty()) {
                remove(expandButton);
            } else if (getComponentIndex(expandButton) < 0) {
                add(expandButton, getComponentIndex(padding));
            }
            this.revalidate();
            expandPopup.invalidate();
            expandPopup.validate();
        }
    }

    private int getComponentIndex(Component c) {
        Component[] comps = getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (c == comps[i]) {
                return i;
            }
        }
        return -1;
    }
    
    private void showExpandedMenu() {
        if (!inPopup.isEmpty() && !expandPopup.isVisible()) {
            isPopupGoingToShow = true;
            Insets ins = expandPopup.getInsets();
            // compute popup height since JPopupMenu.getHeight does not work
            expandPopup.show(expandButton, 0, -(matchCaseCheckBox.getHeight() * inPopup.size() + ins.top + ins.bottom));
        }
    }
    
    private void hideExpandedMenu() {
        if (expandPopup.isVisible()) {
            expandPopup.setVisible(false);
            incrementalSearch();
            incrementalSearchTextField.requestFocusInWindow();
        }
    }

    public @Override String getName() {
        //Required for Aqua L&F toolbar UI
        return "editorSearchBar"; // NOI18N
    }
    
    private void gainFocus() {
        FindSupport findSupport = FindSupport.getFindSupport();
        findProps = new HashMap<Object, Object>(findSupport.getFindProperties());

        if (isVisible()) {
            incrementalSearchTextField.requestFocusInWindow();
            return;
        }
        computeLayout();
        isPopupShown = false;
        
        setVisible(true);
        initBlockSearch();
        incrementalSearchTextField.requestFocusInWindow();

        if (incrementalSearchTextField.getText().length() > 0) {
            // preselect the text in incremental search text field
            incrementalSearchTextField.selectAll();
            findPreviousButton.setEnabled(true);
            findNextButton.setEnabled(true);
        }
        else {
            findPreviousButton.setEnabled(false);
            findNextButton.setEnabled(false);
        }
        wholeWordsCheckBox.setSelected(getWholeWords());
        wholeWordsCheckBox.setEnabled(!getRegExp());
        matchCaseCheckBox.setSelected(getMatchCase());
        regexpCheckBox.setSelected(getRegExp());
        highlightCheckBox.setSelected(getHighlightResults());
        wrapAroundCheckBox.setSelected(getWrapAround());
        searched = false;
    }

    private void looseFocus() {
        if (!isVisible()) {
            return;
        }
        if (isPopupGoingToShow) {
            isPopupGoingToShow = false;
            return;
        }
        
        org.netbeans.editor.Utilities.setStatusText(component, "");
        FindSupport.getFindSupport().setBlockSearchHighlight(0, 0);
        FindSupport.getFindSupport().incSearchReset();
        setVisible(false);
        component.requestFocusInWindow();
    }

    private void incrementalSearch() {
        String incrementalSearchText = incrementalSearchTextField.getText();
        boolean empty = incrementalSearchText.length() <= 0;
    
        // Enable/disable the pre/next buttons
        findPreviousButton.setEnabled(!empty);
        findNextButton.setEnabled(!empty);
        
        // configure find properties
        FindSupport findSupport = FindSupport.getFindSupport();

        findProps.put(EditorFindSupport.FIND_WHAT, incrementalSearchText);
        findProps.put(EditorFindSupport.FIND_MATCH_CASE, matchCaseCheckBox.isSelected());
        findProps.put(EditorFindSupport.FIND_WHOLE_WORDS, wholeWordsCheckBox.isSelected());
        findProps.put(EditorFindSupport.FIND_REG_EXP, regexpCheckBox.isSelected());
        findProps.put(EditorFindSupport.FIND_HIGHLIGHT_SEARCH, !empty && highlightCheckBox.isSelected());
        
        findProps.put(EditorFindSupport.FIND_BACKWARD_SEARCH, false);        
        findProps.put(EditorFindSupport.FIND_INC_SEARCH, true);
        
        findSupport.putFindProperties(findProps);
        
        // search starting at current caret position
        int caretPosition = component.getCaretPosition();

        if (regexpCheckBox.isSelected()) {
            Pattern pattern;
            String patternErrorMsg = null;
            try {
                pattern = Pattern.compile(incrementalSearchText);
            } catch (PatternSyntaxException e) {
                pattern = null;
                patternErrorMsg = e.getDescription();
            }
            if (pattern != null) {
                // valid regexp
                incrementalSearchTextField.setForeground(UIManager.getColor("textText")); //NOI18N
                org.netbeans.editor.Utilities.setStatusText(component, "", StatusDisplayer.IMPORTANCE_INCREMENTAL_FIND);
            } else {
                // invalid regexp
                incrementalSearchTextField.setForeground(INVALID_REGEXP);
                org.netbeans.editor.Utilities.setStatusBoldText(component, NbBundle.getMessage(
                    SearchBar.class, "incremental-search-invalid-regexp", patternErrorMsg)); //NOI18N
            }
        } else {
            if (findSupport.incSearch(findProps, caretPosition) || empty) {
                // text found - reset incremental search text field's foreground
                incrementalSearchTextField.setForeground(UIManager.getColor("textText")); //NOI18N
                org.netbeans.editor.Utilities.setStatusText(component, "", StatusDisplayer.IMPORTANCE_INCREMENTAL_FIND);
            } else {
                // text not found - indicate error in incremental search
                // text field with red foreground
                incrementalSearchTextField.setForeground(NOT_FOUND);
                org.netbeans.editor.Utilities.setStatusText(component, NbBundle.getMessage(
                    SearchBar.class, "incremental-search-not-found", incrementalSearchText),
                    StatusDisplayer.IMPORTANCE_INCREMENTAL_FIND); //NOI18N
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    private void findNext() {
        find(true);
    }

    private void findPrevious() {
        find(false);
    }

    private void find(boolean next) {
        String incrementalSearchText = incrementalSearchTextField.getText();
        boolean empty = incrementalSearchText.length() <= 0;

        incrementalSearchTextField.getDocument().removeDocumentListener(incrementalSearchTextFieldListener);
        // Add the text to the top of the list
        for(int i = incrementalSearchComboBox.getItemCount() - 1; i >= 0; i--) {
            String item = (String) incrementalSearchComboBox.getItemAt(i);
            if (item.equals(incrementalSearchText)) {
                incrementalSearchComboBox.removeItemAt(i);
            }
        }
        ((MutableComboBoxModel) incrementalSearchComboBox.getModel()).insertElementAt(incrementalSearchText, 0);
        incrementalSearchComboBox.setSelectedIndex(0);
        incrementalSearchTextField.getDocument().addDocumentListener(incrementalSearchTextFieldListener);
        
        // configure find properties
        FindSupport findSupport = FindSupport.getFindSupport();

        findProps.put(EditorFindSupport.FIND_WHAT, incrementalSearchText);
        findProps.put(EditorFindSupport.FIND_MATCH_CASE, matchCaseCheckBox.isSelected());
        findProps.put(EditorFindSupport.FIND_WHOLE_WORDS, wholeWordsCheckBox.isSelected());
        findProps.put(EditorFindSupport.FIND_REG_EXP, regexpCheckBox.isSelected());
        findProps.put(EditorFindSupport.FIND_BACKWARD_SEARCH, Boolean.FALSE);
        findProps.put(EditorFindSupport.FIND_INC_SEARCH, Boolean.TRUE);
        findProps.put(EditorFindSupport.FIND_HIGHLIGHT_SEARCH, !empty && highlightCheckBox.isSelected());
        findProps.put(EditorFindSupport.FIND_WRAP_SEARCH, wrapAroundCheckBox.isSelected());

        findSupport.putFindProperties(findProps);
        
        if (findSupport.find(findProps, !next) || empty) {
            // text found - reset incremental search text field's foreground
            incrementalSearchTextField.setForeground(UIManager.getColor("textText")); //NOI18N
            searched = true;
        } else {
            // text not found - indicate error in incremental search text field with red foreground
            incrementalSearchTextField.setForeground(NOT_FOUND);
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private void processButton(AbstractButton button) {
        button.setMargin(BUTTON_INSETS);
    }
    
    @SuppressWarnings("unchecked")
    private void initBlockSearch() {
        JTextComponent c = EditorRegistry.lastFocusedComponent();
        String selText = null;
        int startSelection = 0;
        int endSelection = 0;
        boolean blockSearchVisible = false;

        if (c != null) {
            startSelection = c.getSelectionStart();
            endSelection = c.getSelectionEnd();

            Document doc = c.getDocument();
            if (doc instanceof BaseDocument){
                BaseDocument bdoc = (BaseDocument) doc;
                try{
                    int startLine = org.netbeans.editor.Utilities.getLineOffset(bdoc, startSelection);
                    int endLine = org.netbeans.editor.Utilities.getLineOffset(bdoc, endSelection);
                    if (endLine > startLine) {
                        blockSearchVisible = true;
                    }
                } catch (BadLocationException ble){
                }
            }

            // caretPosition = bwdSearch.isSelected() ? c.getSelectionEnd() : c.getSelectionStart();

            if (!blockSearchVisible){
                selText = c.getSelectedText();
                if (selText != null && selText.length() > 0) {
                    int n = selText.indexOf( '\n' );
                    if (n >= 0 ) selText = selText.substring(0, n);
                    incrementalSearchTextField.setText(selText);
                    // findWhat.getEditor().setItem(selText);
                    // changeFindWhat(true);
                } else {
                    String findWhat = (String) FindSupport.getFindSupport().getFindProperty(EditorFindSupport.FIND_WHAT);
                    if (findWhat != null && findWhat.length() > 0) {
                        incrementalSearchTextField.getDocument().removeDocumentListener(incrementalSearchTextFieldListener);
                        incrementalSearchTextField.setText(findWhat);
                        incrementalSearchTextField.getDocument().addDocumentListener(incrementalSearchTextFieldListener);
                    }
                }
            }

            int blockSearchStartOffset = blockSearchVisible ? startSelection : 0;
            int blockSearchEndOffset = blockSearchVisible ? endSelection : 0;

            try{                
                findProps.put(EditorFindSupport.FIND_BLOCK_SEARCH, blockSearchVisible);
                findProps.put(EditorFindSupport.FIND_BLOCK_SEARCH_START, doc.createPosition(blockSearchStartOffset));
                findProps.put(EditorFindSupport.FIND_BLOCK_SEARCH_END, doc.createPosition(blockSearchEndOffset));
                FindSupport.getFindSupport().setBlockSearchHighlight(blockSearchStartOffset, blockSearchEndOffset);
            } catch(BadLocationException ble){
                findProps.put(EditorFindSupport.FIND_BLOCK_SEARCH, Boolean.FALSE);
                findProps.put(EditorFindSupport.FIND_BLOCK_SEARCH_START, null);
                findProps.put(EditorFindSupport.FIND_BLOCK_SEARCH_END, null);
            }
            
            FindSupport.getFindSupport().putFindProperties(findProps);
        }
    }
    
    /**
     * Factory for creating the incremental search sidebar
     */
    public static final class Factory implements SideBarFactory {
        @Override
        public JComponent createSideBar(JTextComponent target) {
            return new SearchBar(target);
        }
    }

    public static class IncrementalSearchForwardAction extends BaseAction {
        
        static final long serialVersionUID = -1;
        
        public IncrementalSearchForwardAction() {
            super(INCREMENTAL_SEARCH_FORWARD, CLEAR_STATUS_TEXT);
            putValue(SHORT_DESCRIPTION, NbBundle.getMessage(IncrementalSearchForwardAction.class, INCREMENTAL_SEARCH_FORWARD));
        }
        
        @Override
        public void actionPerformed(ActionEvent evt, JTextComponent target) {
            if (target != null) {
                EditorUI eui = org.netbeans.editor.Utilities.getEditorUI(target);
                if (eui != null) {
                    //need to find if it has extended editor first, otherwise getExtComponent() will create all sidebars
                    //and other parts of full editor if action is assigned to just editor pane and broke later action logic.
                    JComponent comp = eui.hasExtComponent() ? eui.getExtComponent() : null;
                    if (comp != null) {
                        SearchBar issb = findComponent(comp,SearchBar.class, 5);
                        if (issb != null) {
                            issb.gainFocus();
                        }
                    }
                }
            }
        }
    }

    public static class IncrementalSearchBackwardAction extends BaseAction {
        
        static final long serialVersionUID = -1;
        
        public IncrementalSearchBackwardAction() {
            super(INCREMENTAL_SEARCH_BACKWARD, CLEAR_STATUS_TEXT);
            putValue(SHORT_DESCRIPTION, NbBundle.getMessage(IncrementalSearchBackwardAction.class, INCREMENTAL_SEARCH_BACKWARD));
        }
        
        @Override
        public void actionPerformed(ActionEvent evt, JTextComponent target) {
            if (target != null) {
                EditorUI eui = org.netbeans.editor.Utilities.getEditorUI(target);
                if (eui != null) {
                    //need to find if it has extended editor first, otherwise getExtComponent() will create all sidebars
                    //and other parts of full editor if action is assigned to just editor pane and broke later action logic.
                    JComponent comp = eui.hasExtComponent() ? eui.getExtComponent() : null;
                    if (comp != null) {
                        SearchBar issb = findComponent(comp,SearchBar.class, 5);
                        if (issb != null) {
                            issb.gainFocus();
                        }
                    }
                }
            }
        }
    }

    private static <T> T findComponent(Container container, Class<T> componentClass, int depth) {
        if (depth > 0) {
            for(Component c : container.getComponents()) {
                if (componentClass.isAssignableFrom(c.getClass())) {
                    @SuppressWarnings("unchecked")
                    T target = (T) c;
                    return target;
                } else if (c instanceof Container) {
                    T target = findComponent((Container) c, componentClass, depth - 1);
                    if (target != null) {
                        return target;
                    }
                }
            }
        }
        return null;
    }

    private boolean getMatchCase() {
        Boolean b = (Boolean)findProps.get(EditorFindSupport.FIND_MATCH_CASE);
        return b != null ? b.booleanValue() : false;
    }

    private void switchMatchCase() {
        findProps.put(EditorFindSupport.FIND_MATCH_CASE, !getMatchCase());
    }

    private boolean getWholeWords() {
        Boolean b = (Boolean)findProps.get(EditorFindSupport.FIND_WHOLE_WORDS);
        return b != null ? b.booleanValue() : false;
    }

    private void switchWholeWords() {
        findProps.put(EditorFindSupport.FIND_MATCH_CASE, !getWholeWords());
    }

    private boolean getRegExp() {
        Boolean b = (Boolean)findProps.get(EditorFindSupport.FIND_REG_EXP);
        return b != null ? b.booleanValue() : false;
    }

    private void switchRegExp() {
        findProps.put(EditorFindSupport.FIND_MATCH_CASE, !getRegExp());
    }

    private boolean getHighlightResults() {
        Boolean b = (Boolean)findProps.get(EditorFindSupport.FIND_HIGHLIGHT_SEARCH);
        return b != null ? b.booleanValue() : false;
    }

    private void switchHighlightResults() {
        findProps.put(EditorFindSupport.FIND_MATCH_CASE, !getHighlightResults());
    }
    
    private boolean getWrapAround() {
        Boolean b = (Boolean)findProps.get(EditorFindSupport.FIND_WRAP_SEARCH);
        return b != null ? b.booleanValue() : false;
    }
    
    private void switchWrapAround() {
        findProps.put(EditorFindSupport.FIND_WRAP_SEARCH, !getWrapAround());
    }
}
