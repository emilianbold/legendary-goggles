/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.iep.editor.wizard;

import java.awt.Component;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.iep.editor.share.SharedConstants;
import org.netbeans.modules.xml.schema.model.GlobalComplexType;
import org.netbeans.modules.xml.schema.model.GlobalElement;
import org.netbeans.modules.xml.schema.model.GlobalType;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class IEPWizardPanel3 implements WizardDescriptor.Panel {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private IEPVisualPanel3 component;

    private WizardDescriptor mDescriptor;
    
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component = new IEPVisualPanel3();
        }
        return component;
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
    // If you have context help:
    // return new HelpCtx(SampleWizardPanel1.class);
    }

    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
    // If it depends on some condition (form filled out...), then:
    // return someCondition();
    // and when this condition changes (last form field filled in...) then:
    // fireChangeEvent();
    // and uncomment the complicated stuff below.
    }

    public final void addChangeListener(ChangeListener l) {
    }

    public final void removeChangeListener(ChangeListener l) {
    }
    /*
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0
    public final void addChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.add(l);
    }
    }
    public final void removeChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.remove(l);
    }
    }
    protected final void fireChangeEvent() {
    Iterator<ChangeListener> it;
    synchronized (listeners) {
    it = new HashSet<ChangeListener>(listeners).iterator();
    }
    ChangeEvent ev = new ChangeEvent(this);
    while (it.hasNext()) {
    it.next().stateChanged(ev);
    }
    }
     */

    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(Object settings) {
        mDescriptor = (WizardDescriptor) settings;
        
        ElementOrType elementOrType = (ElementOrType) mDescriptor.getProperty(WizardConstants.WIZARD_SELECTED_ELEMENT_OR_TYPE_KEY);
        if(elementOrType!= null) {
            List<XSDToIEPAttributeNameVisitor.AttributeNameToType> nameToTypeList;

            if(elementOrType.isElement()) {
                GlobalElement element = elementOrType.getElement();
                nameToTypeList = processElement(element);
                
                 //add one attribute with CLOB type for storing
                //this whole element xml
                XSDToIEPAttributeNameVisitor.AttributeNameToType nameToType = new XSDToIEPAttributeNameVisitor.AttributeNameToType(element.getName(), SharedConstants.SQL_TYPE_CLOB);
                nameToTypeList.add(nameToType);
        
            } else {
                GlobalType type = elementOrType.getType();
                nameToTypeList = processType(type);
                
                if(type instanceof GlobalComplexType) {
                    String name = ((GlobalComplexType)type).getName();
                    //add one attribute with CLOB type for storing
                    //this whole element xml
                    XSDToIEPAttributeNameVisitor.AttributeNameToType nameToType = new XSDToIEPAttributeNameVisitor.AttributeNameToType(name, SharedConstants.SQL_TYPE_CLOB);
                    nameToTypeList.add(nameToType);
                }
            
            }
        
            component.addDefaultIEPAttributes(nameToTypeList);
        }
    }

    public void storeSettings(Object settings) {
    }
    
    private List<XSDToIEPAttributeNameVisitor.AttributeNameToType> processElement(GlobalElement element) {
        XSDToIEPAttributeNameVisitor visitor = new XSDToIEPAttributeNameVisitor();
        element.accept(visitor);
        
        return visitor.getAttributeNameToTypeList();
    }
    
    private List<XSDToIEPAttributeNameVisitor.AttributeNameToType> processType(GlobalType type) {
        XSDToIEPAttributeNameVisitor visitor = new XSDToIEPAttributeNameVisitor();
        type.accept(visitor);
        
        return visitor.getAttributeNameToTypeList();
    }
}

