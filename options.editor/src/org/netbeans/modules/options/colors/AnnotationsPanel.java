/*
 * AnnotationsPanel1.java
 *
 * Created on January 17, 2006, 4:27 PM
 */

package org.netbeans.modules.options.colors;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.netbeans.api.editor.settings.EditorStyleConstants;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

/**
 *
 * @author  Jan Jancura
 */
public class AnnotationsPanel extends JPanel implements ActionListener, 
PropertyChangeListener {
    
    private ColorModel          colorModel;
    private boolean		listen = false;
    private String              currentScheme;
    private Map                 schemes = new HashMap ();
    private Set                 toBeSaved = new HashSet ();
    private boolean             changed = false;
    
    
    /** Creates new form AnnotationsPanel1 */
    public AnnotationsPanel() {
        initComponents();
        
        // 1) init components
        cbForeground.getAccessibleContext ().setAccessibleName (loc ("AN_Foreground_Chooser"));
        cbForeground.getAccessibleContext ().setAccessibleDescription (loc ("AD_Foreground_Chooser"));
        cbBackground.getAccessibleContext ().setAccessibleName (loc ("AN_Background_Chooser"));
        cbBackground.getAccessibleContext ().setAccessibleDescription (loc ("AD_Background_Chooser"));
        cbWaveUnderlined.getAccessibleContext ().setAccessibleName (loc ("AN_Wave_Underlined"));
        cbWaveUnderlined.getAccessibleContext ().setAccessibleDescription (loc ("AD_Wave_Underlined"));
        lCategories.getAccessibleContext ().setAccessibleName (loc ("AN_Categories"));
        lCategories.getAccessibleContext ().setAccessibleDescription (loc ("AD_Categories"));
        ColorComboBox.init (cbForeground);
        ColorComboBox.init (cbBackground);
        ColorComboBox.init (cbWaveUnderlined);
        lCategories.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
        lCategories.setVisibleRowCount (3);
        lCategories.addListSelectionListener (new ListSelectionListener () {
            public void valueChanged (ListSelectionEvent e) {
                if (!listen) return;
                refreshUI ();
            }
        });
	lCategories.setCellRenderer (new CategoryRenderer ());
        cbForeground.addPropertyChangeListener (this);
        ((JComponent)cbForeground.getEditor()).addPropertyChangeListener (this);
        cbBackground.addPropertyChangeListener (this);
        ((JComponent)cbBackground.getEditor()).addPropertyChangeListener (this);
        cbWaveUnderlined.addPropertyChangeListener (this);
        ((JComponent)cbWaveUnderlined.getEditor()).addPropertyChangeListener (this);
        
        lCategory.setLabelFor (lCategories);
        loc(lCategory, "CTL_Category");
        loc(lForeground, "CTL_Foreground_label");
        loc(lWaveUnderlined, "CTL_Wave_underlined_label");
        loc(lbackground, "CTL_Background_label");
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        lCategory = new javax.swing.JLabel();
        cpCategories = new javax.swing.JScrollPane();
        lCategories = new javax.swing.JList();
        lForeground = new javax.swing.JLabel();
        lbackground = new javax.swing.JLabel();
        lWaveUnderlined = new javax.swing.JLabel();
        cbForeground = new javax.swing.JComboBox();
        cbBackground = new javax.swing.JComboBox();
        cbWaveUnderlined = new javax.swing.JComboBox();

        lCategory.setText("Category:");

        cpCategories.setViewportView(lCategories);

        lForeground.setText("Foreground:");

        lbackground.setText("Background:");

        lWaveUnderlined.setText("Wave Underlined:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(cpCategories, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                        .add(20, 20, 20)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lbackground)
                            .add(lWaveUnderlined)
                            .add(lForeground))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(cbForeground, 0, 71, Short.MAX_VALUE)
                            .add(cbBackground, 0, 71, Short.MAX_VALUE)
                            .add(cbWaveUnderlined, 0, 71, Short.MAX_VALUE)))
                    .add(lCategory))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lCategory)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lForeground)
                            .add(cbForeground, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lbackground)
                            .add(cbBackground, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lWaveUnderlined)
                            .add(cbWaveUnderlined, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(cpCategories, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbBackground;
    private javax.swing.JComboBox cbForeground;
    private javax.swing.JComboBox cbWaveUnderlined;
    private javax.swing.JScrollPane cpCategories;
    private javax.swing.JList lCategories;
    private javax.swing.JLabel lCategory;
    private javax.swing.JLabel lForeground;
    private javax.swing.JLabel lWaveUnderlined;
    private javax.swing.JLabel lbackground;
    // End of variables declaration//GEN-END:variables
    
 
    public void actionPerformed (ActionEvent evt) {
        if (!listen) return;
        updateData ();
        changed = true;
    }
    
    public void propertyChange (PropertyChangeEvent evt) {
        if (!listen) return;
        if (evt.getPropertyName () != ColorComboBox.PROP_COLOR) return;
        updateData ();
    }
    
    void update (ColorModel colorModel) {
        this.colorModel = colorModel;
        listen = false;
        currentScheme = colorModel.getCurrentProfile ();
        lCategories.setListData (getAnnotations (currentScheme));
        if (lCategories.getModel ().getSize () > 0)
            lCategories.setSelectedIndex (0);
        refreshUI ();
        listen = true;
        changed = false;
    }
    
    void cancel () {
        toBeSaved = new HashSet ();
        schemes = new HashMap ();
        changed = false;
    }
    
    void applyChanges () {
        if (colorModel == null) return;
        Iterator it = toBeSaved.iterator ();
        while (it.hasNext ()) {
            String scheme = (String) it.next ();
            colorModel.setAnnotations (scheme, getAnnotations (scheme));
        }
        toBeSaved = new HashSet ();
        schemes = new HashMap ();
    }
    
    boolean isChanged () {
        return changed;
    }
    
    public void setCurrentProfile (String currentScheme) {
        String oldScheme = this.currentScheme;
        this.currentScheme = currentScheme;
        Vector v = getAnnotations (currentScheme);
        if (v == null) {
            // clone scheme
            v = getAnnotations (oldScheme);
            schemes.put (currentScheme, new Vector (v));
            toBeSaved.add (currentScheme);
            v = getAnnotations (currentScheme);
        }
        lCategories.setListData (v);
        if (lCategories.getModel ().getSize () > 0)
            lCategories.setSelectedIndex (0);
        refreshUI ();
    }
    
    void deleteProfile (String scheme) {
    }
        
    
    // other methods ...........................................................
    
    private static String loc (String key) {
        return NbBundle.getMessage (SyntaxColoringPanel.class, key);
    }
    
    private static void loc (Component c, String key) {
        if (c instanceof AbstractButton)
            Mnemonics.setLocalizedText (
                (AbstractButton) c, 
                loc (key)
            );
        else
            Mnemonics.setLocalizedText (
                (JLabel) c, 
                loc (key)
            );
    }

    private void updateData () {
        Vector annotations = getAnnotations (currentScheme);
	SimpleAttributeSet c = (SimpleAttributeSet) annotations.get 
	    (lCategories.getSelectedIndex ());
        Color color = ((ColorValue) cbBackground.getSelectedItem ()).color;
        if (color != null)
            c.addAttribute (
                StyleConstants.Background,
                color
            );
        else
            c.removeAttribute (StyleConstants.Background);
        color = ((ColorValue) cbForeground.getSelectedItem ()).color;
        if (color != null)
            c.addAttribute (
                StyleConstants.Foreground,
                color
            );
        else
            c.removeAttribute (StyleConstants.Foreground);
        color = ((ColorValue) cbWaveUnderlined.getSelectedItem ()).color;
        if (color != null)
            c.addAttribute (
                EditorStyleConstants.WaveUnderlineColor,
                color
            );
        else
            c.removeAttribute (EditorStyleConstants.WaveUnderlineColor);
        toBeSaved.add (currentScheme);
    }
    
    private void refreshUI () {
        int index = lCategories.getSelectedIndex ();
        if (index < 0) {
	    // no category selected
            cbForeground.setEnabled (false);
            cbBackground.setEnabled (false);
            cbWaveUnderlined.setEnabled (false);
            return;
        }
        cbForeground.setEnabled (true);
        cbBackground.setEnabled (true);
        cbWaveUnderlined.setEnabled (true);
        
        listen = false;
        
        // set defaults
        AttributeSet defAs = getDefaultColoring();
        if (defAs != null) {
            Color inheritedForeground = (Color) defAs.getAttribute(StyleConstants.Foreground);
            if (inheritedForeground == null) {
                inheritedForeground = Color.black;
            }
            ColorComboBox.setInheritedColor(cbForeground, inheritedForeground);
            
            Color inheritedBackground = (Color) defAs.getAttribute(StyleConstants.Background);
            if (inheritedBackground == null) {
                inheritedBackground = Color.white;
            }
            ColorComboBox.setInheritedColor(cbBackground, inheritedBackground);
        }

        // set values
        Vector annotations = getAnnotations (currentScheme);
        AttributeSet c = (AttributeSet) annotations.get (index);
        ColorComboBox.setColor (
            cbForeground,
            (Color) c.getAttribute (StyleConstants.Foreground)
        );
        ColorComboBox.setColor (
            cbBackground,
            (Color) c.getAttribute (StyleConstants.Background)
        );
        ColorComboBox.setColor (
            cbWaveUnderlined,
            (Color) c.getAttribute (EditorStyleConstants.WaveUnderlineColor)
        );
        listen = true;
    }
    
    private AttributeSet getDefaultColoring() {
        Collection/*<AttributeSet>*/ defaults = colorModel.getCategories(currentScheme, ColorModel.ALL_LANGUAGES);
        
        for(Iterator i = defaults.iterator(); i.hasNext(); ) {
            AttributeSet as = (AttributeSet) i.next();
            String name = (String) as.getAttribute(StyleConstants.NameAttribute);
            if (name != null && "default".equals(name)) { //NOI18N
                return as;
            }
        }
        
        return null;
    }
    
    private Vector getAnnotations (String scheme) {
        if (!schemes.containsKey (scheme)) {
            Collection c = colorModel.getAnnotations (currentScheme);
            if (c == null) return null;
            List l = new ArrayList (c);
            Collections.sort (l, new CategoryComparator ());
            schemes.put (scheme, new Vector (l));
        }
        return (Vector) schemes.get (scheme);
    }
}
