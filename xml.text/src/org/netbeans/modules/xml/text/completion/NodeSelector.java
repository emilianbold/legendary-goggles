/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.xml.text.completion;

import java.awt.Container;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JEditorPane;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;

import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.xml.api.model.GrammarQuery;
import org.netbeans.modules.xml.api.model.HintContext;
import org.netbeans.modules.xml.text.completion.XMLCompletionQuery;
import org.netbeans.modules.xml.text.completion.GrammarManager;
import org.netbeans.modules.xml.text.syntax.XMLSyntaxSupport;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;

/**
 * Finds SyntaxNode at the Carat position in the text editor
 * and asks grammars to provide customizer and properties.
 * The customizer and properties are added to the selected node in the editors
 * TopComponent.
 *
 * @author  asgeir@dimonsoftware.com
 */
public class NodeSelector {
    
    /** Listener on caret movements */
    private CaretListener caretListener;
    
    /** Timer which countdowns the "update selected element node" time. */ // NOI18N
    private Timer timerSelNodes;
    
    /** The last caret offset position. */
    private int lastCaretOffset = -1;
    
    /** Default delay between cursor movement and updating selected element nodes. */
    private static final int SELECTED_NODES_DELAY = 500;
    
    private JEditorPane pane;
    
    private XMLSyntaxSupport syntaxSupport;
    
    private Node originalUINode;
    
    HintContext hintContext;
    
    /** Creates a new instance of NodeSelector */
    public NodeSelector(final JEditorPane pane) {
        this.pane = pane;
        
        caretListener = new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                restartTimerSelNodes(e.getDot());
            }
        };
        
        timerSelNodes = new Timer(100, new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (lastCaretOffset == -1 && pane != null) {
                    Caret caret = pane.getCaret();
                    if (caret != null)
                        lastCaretOffset = caret.getDot();
                }
                selectElementsAtOffset(lastCaretOffset);
            }
        });
        timerSelNodes.setInitialDelay(100);
        timerSelNodes.setRepeats(false);
        timerSelNodes.restart();
        
        pane.addCaretListener(caretListener);
    }
    
    /** Restart the timer which updates the selected nodes after the specified delay from
     * last caret movement.
     */
    void restartTimerSelNodes(int pos) {
        timerSelNodes.setInitialDelay(SELECTED_NODES_DELAY);
        lastCaretOffset = pos;
        timerSelNodes.restart();
    }
    
    /** Selects element at the given position. */
    void selectElementsAtOffset(final int offset) {
        if (syntaxSupport == null) {
            Document doc = pane.getDocument();
            if (doc instanceof BaseDocument) {
                syntaxSupport = (XMLSyntaxSupport) ((BaseDocument)doc).getSyntaxSupport();
            }
            if (syntaxSupport == null) {
                return;
            }
        }
        
        Container parent = pane.getParent();
        while (parent != null && !(parent instanceof TopComponent)){
            parent = parent.getParent();
        }
        if (parent == null) {
            return;
        }
        
        TopComponent topComp = (TopComponent)parent;
        Node activeNodes[] = topComp.getActivatedNodes();
        if (activeNodes == null || activeNodes.length == 0) {
            return; // No nodes active
        }
        
        if (originalUINode == null) {
            originalUINode = activeNodes[0];
        }
        
        GrammarQuery grammarQuery = XMLCompletionQuery.getPerformer(pane.getDocument(), syntaxSupport);
        if (grammarQuery == null) {
            return;
        }
        
        SyntaxQueryHelper helper = null;
        try {
            helper = new SyntaxQueryHelper(syntaxSupport, offset);
        } catch (BadLocationException e) {
            topComp.setActivatedNodes(new Node[]{new DelegatingNode(originalUINode, null, null)});
            return;
        }
        
        Node newUiNode = new DelegatingNode(originalUINode, grammarQuery, helper.getContext());
        
        topComp.setActivatedNodes(new Node[]{newUiNode});
    }
    
    private class DelegatingNode extends PeerNode {
        
        GrammarQuery grammarQuery;
        
        HintContext hintContext;
        
        Node.PropertySet nodePropertySet;
        
        public DelegatingNode(Node peer, GrammarQuery grammarQuery, HintContext hintContext) {
            super(peer);
            this.grammarQuery = grammarQuery;
            this.hintContext = hintContext;
        }
        
        public java.awt.Component getCustomizer() {
            if (grammarQuery == null || hintContext == null) {
                return super.getCustomizer();
            } else {
                return grammarQuery.getCustomizer(hintContext);
            }
        }
        
        public boolean hasCustomizer() {
            if (grammarQuery == null || hintContext == null) {
                return super.hasCustomizer();
            } else {
                return grammarQuery.hasCustomizer(hintContext);
            }
        }
        
        public Node.PropertySet[] getPropertySets() {
            if (nodePropertySet == null) {
                nodePropertySet = new Node.PropertySet("Node properties", "Node properties",
                "Shows properties specific for the selected node in the text editor") {
                    public Node.Property[] getProperties() {
                        if (grammarQuery != null && hintContext != null) {
                            Node.Property[] nodeProperties = grammarQuery.getProperties(hintContext);
                            if (nodeProperties != null && nodeProperties.length > 0) {
                                // The GrammarQuery controls the properties
                                return nodeProperties;
                            }
                        }
                        
                        // By default, we try to create properties from the attributes of the
                        // selected element.
                        org.w3c.dom.Element attributeOwningElem = null;
                        if (hintContext != null) {
                            if (hintContext.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                                attributeOwningElem = (org.w3c.dom.Element)hintContext;
                            } else if (hintContext.getNodeType() == org.w3c.dom.Node.ATTRIBUTE_NODE) {
                                attributeOwningElem = (org.w3c.dom.Element)((org.w3c.dom.Attr)hintContext).getOwnerElement();
                            }
                        }
                        
                        if (attributeOwningElem != null) {
                            // We have a selected element that might have attributes 
                            org.w3c.dom.NamedNodeMap attributes = attributeOwningElem.getAttributes();
                            Node.Property[] nodeProperties = new Node.Property[attributes.getLength()];
                            for (int ind = 0; ind < attributes.getLength(); ind++) {
                                org.w3c.dom.Node node = attributes.item(ind);
                                nodeProperties[ind] = new AttributeProperty(attributeOwningElem, node.getNodeName());          
                            }
                            
                            return nodeProperties;
                        }
                        
                        return new Node.Property[0];
                    }
                };
            }
            
            Node.PropertySet[] parentPropSets = super.getPropertySets();
            Node.PropertySet[] newPropSets = new Node.PropertySet[parentPropSets.length + 1];
            for (int ind = 0; ind < parentPropSets.length; ind++) {
                newPropSets[ind] = parentPropSets[ind];
            }
            newPropSets[parentPropSets.length] = nodePropertySet;
            return newPropSets;
        }
    }
    
    private class AttributeProperty extends org.openide.nodes.PropertySupport {
        String propName;
        org.w3c.dom.Element ownerElem;
        
        public AttributeProperty(org.w3c.dom.Element ownerElem, String propName) {
            super(propName, String.class, propName, propName, true, true);
            this.ownerElem = ownerElem;
            this.propName = propName;
        }
        
        public void setValue(Object value) {
            ownerElem.setAttribute(propName, (String)value);
        }
        
        public Object getValue() {
            return ownerElem.getAttribute(propName);
        }
    }
}
