/*
 * GameDesignPreviewComponent.java
 *
 * Created on June 19, 2007, 2:38 AM
 */

package org.netbeans.modules.vmd.game.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import org.netbeans.modules.vmd.game.model.Editable;
import org.netbeans.modules.vmd.game.model.GlobalRepository;

/**
 *
 * @author  kaja
 */
public class GameDesignPreviewComponent extends JPanel implements MouseListener, PropertyChangeListener {
	
	private static final Color COLOR_HILITE = ColorConstants.COLOR_OUTLINE_HILITE;
	private static final Color COLOR_PLAIN = ColorConstants.COLOR_OUTLINE_PLAIN;
	private static final Color COLOR_BACKGROUND = ColorConstants.COLOR_EDITOR_PANEL;
	
	private GlobalRepository gameDesign;
	
	private Editable editable;
	
	/** Creates new form GameDesignPreviewComponent */
	public GameDesignPreviewComponent(GlobalRepository gameDesign, JComponent preview, String name, Editable editable) {
		this.gameDesign = gameDesign;
		this.editable = editable;
		this.addMouseListener(this);
		initComponents();
		this.labelName.setText(name);
		this.panelPreview.add(preview);
		editable.addPropertyChangeListener(this);
		ToolTipManager.sharedInstance().registerComponent(this);
	}

    @Override
    public String getToolTipText(MouseEvent event) {
        return editable.getName();
    }
	
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPreview = new javax.swing.JPanel();
        labelName = new javax.swing.JLabel();

        setBackground(COLOR_BACKGROUND);

        panelPreview.setBackground(new java.awt.Color(255, 255, 255));
        panelPreview.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(163, 184, 215), 1, true));
        panelPreview.setLayout(new java.awt.BorderLayout());

        labelName.setBackground(COLOR_BACKGROUND);
        labelName.setForeground(new java.awt.Color(100, 123, 156));
        labelName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelName.setText(org.openide.util.NbBundle.getMessage(GameDesignPreviewComponent.class, "GameDesignPreviewComponent.labelName.text")); // NOI18N
        labelName.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(163, 184, 215), 1, true));
        labelName.setInheritsPopupMenu(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelPreview, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelName, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel labelName;
    private javax.swing.JPanel panelPreview;
    // End of variables declaration//GEN-END:variables
	
    public void mouseClicked(MouseEvent e) {
        //System.out.println("Clicked - request editing!");
		this.gameDesign.getMainView().requestEditing(editable);
    }

    public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			this.handlePopup(e);
		}
    }

    public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			this.handlePopup(e);
		}
    }
	
	private void handlePopup(MouseEvent e) {
		JPopupMenu menu = new JPopupMenu();
		List<Action> actions = this.editable.getActions();
		for (Action action : actions) {
			menu.add(action);
		}
		menu.show(this, e.getX(), e.getY());
	}
	
    public void mouseEntered(MouseEvent e) {
        panelPreview.setBorder(new javax.swing.border.LineBorder(COLOR_HILITE, 1, true));
        labelName.setBorder(new javax.swing.border.LineBorder(COLOR_HILITE, 1, true));
		//labelName.setOpaque(true);
    }

    public void mouseExited(MouseEvent e) {
		panelPreview.setBorder(new javax.swing.border.LineBorder(COLOR_PLAIN, 1, true));
		labelName.setBorder(new javax.swing.border.LineBorder(COLOR_PLAIN, 1, true));
		//labelName.setOpaque(false);
    }

    @Override
    public Dimension getMinimumSize() {
        return super.getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return super.getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(125, 150);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        this.panelPreview.repaint();
		if (evt.getPropertyName().equals(Editable.PROPERTY_NAME)) {
			this.labelName.setText((String) evt.getNewValue());
		}
    }

}
