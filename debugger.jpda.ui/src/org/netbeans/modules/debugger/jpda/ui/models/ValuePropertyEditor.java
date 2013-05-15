/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.debugger.jpda.ui.models;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.InvalidObjectException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.InvalidExpressionException;
import org.netbeans.api.debugger.jpda.JPDAClassType;
import org.netbeans.api.debugger.jpda.MutableVariable;
import org.netbeans.api.debugger.jpda.ObjectVariable;
import org.netbeans.api.debugger.jpda.Super;
import org.netbeans.api.debugger.jpda.Variable;
import org.netbeans.spi.debugger.ContextProvider;
import org.netbeans.spi.viewmodel.TableModel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.util.RequestProcessor;

/**
 * Property editor of a variable, that delegates to the property editor
 * of the variable's mirror.
 * 
 * @author Martin Entlicher
 */
class ValuePropertyEditor implements ExPropertyEditor {
    
    private static final Logger logger = Logger.getLogger(ValuePropertyEditor.class.getName());
    
    private ContextProvider contextProvider;
    private PropertyEditor delegatePropertyEditor;
    private Class mirrorClass;
    private Object currentValue;
    private Object delegateValue;
    private PropertyEnv env;
    private final List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
    private VariablesTableModel vtm;
    
    ValuePropertyEditor(ContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    static boolean hasPropertyEditorFor(Variable var) {
        if (!(var instanceof ObjectVariable)) {
            return false;
        }
        String type = var.getType();
        try {
            Class clazz = Class.forName(type);
            //return PropertyEditorManager.findEditor(clazz) != null;
            return hasPropertyEditorFor(clazz);
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
    private static boolean hasPropertyEditorFor(final Class clazz) {
        if (SwingUtilities.isEventDispatchThread()) {
            return findPropertyEditor(clazz) != null;
        } else {
            final boolean[] has = new boolean[] { false };
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        has[0] = findPropertyEditor(clazz) != null;
                    }
                });
            } catch (InterruptedException ex) {
            } catch (InvocationTargetException ex) {
            }
            return has[0];
        }
    }
    
    private static PropertyEditor findPropertyEditor(Class clazz) {
        if (Object.class.equals(clazz)) {
            return null;
        }
        PropertyEditor pe = PropertyEditorManager.findEditor(clazz);
        if (pe == null) {
            clazz = clazz.getSuperclass();
            if (clazz != null) {
                return findPropertyEditor(clazz);
            }
        }
        return pe;
    }

    @Override
    public void setValue(Object value) {
        logger.log(Level.FINE, "ValuePropertyEditor.setValue({0})", value);
        /*
        if (delegatePropertyEditor != null) {
            for (PropertyChangeListener l : listeners) {
                delegatePropertyEditor.removePropertyChangeListener(l);
            }
        }*/
        this.currentValue = value;
        Class clazz;
        Object valueMirror;
        if (value instanceof String) {
            clazz = String.class;
            valueMirror = value;
        } else if (value instanceof VariableWithMirror) {
            valueMirror = ((VariableWithMirror) value).createMirrorObject();
            clazz = valueMirror.getClass();
        } else if (value instanceof Variable) {
            Variable var = (Variable) value;
            valueMirror = VariablesTableModel.getMirrorFor(var);
            if (valueMirror != null) {
                clazz = valueMirror.getClass();
            } else {
                clazz = String.class;
                valueMirror = var.getValue();
            }
        } else {
            throw new IllegalArgumentException(value.toString());
        }
        boolean doAttach = false;
        if (delegatePropertyEditor == null || clazz != mirrorClass) {
            if (delegatePropertyEditor != null) {
                for (PropertyChangeListener l : listeners) {
                    delegatePropertyEditor.removePropertyChangeListener(l);
                }
            }
            PropertyEditor propertyEditor = findPropertyEditor(clazz);
            if (propertyEditor == null) {
                clazz = String.class;
                propertyEditor = PropertyEditorManager.findEditor(String.class);
                valueMirror = ((Variable) value).getValue();
            }
            mirrorClass = clazz;
            delegatePropertyEditor = propertyEditor;
            if (env != null && propertyEditor instanceof ExPropertyEditor) {
                doAttach = true;
            }
            for (PropertyChangeListener l : listeners) {
                delegatePropertyEditor.addPropertyChangeListener(l);
            }
        }
        delegateValue = valueMirror;
        delegatePropertyEditor.setValue(valueMirror);
        if (doAttach) {
            ((ExPropertyEditor) delegatePropertyEditor).attachEnv(env);
        }
        logger.log(Level.FINE, "  delegatePropertyEditor = {0}", delegatePropertyEditor);
        /*for (PropertyChangeListener l : listeners) {
            delegatePropertyEditor.addPropertyChangeListener(l);
        }*/
    }

    @Override
    public Object getValue() {
        if (delegatePropertyEditor == null) {
            logger.log(Level.FINE, "ValuePropertyEditor.getValue() = (null) {0}", currentValue);
            return currentValue;
        }
        Object dpeValue = delegatePropertyEditor.getValue();
        if (dpeValue instanceof String) {//!(currentValue instanceof Variable)) {
            logger.log(Level.FINE, "ValuePropertyEditor.getValue() = (delegate''s) {0}", dpeValue);
            return dpeValue;
        } else {
            if (dpeValue != delegateValue && currentValue instanceof MutableVariable) {
                if (!(currentValue instanceof VariableWithMirror)) {
                    setOrigValue(currentValue);
                    currentValue = new VariableWithMirror(dpeValue);
                } else {
                    ((VariableWithMirror) currentValue).setFromMirrorObject(dpeValue);
                }
                //setVarFromMirror((MutableVariable) currentValue, dpeValue);
                /*
                try {
                    ((MutableVariable) currentValue).setFromMirrorObject(dpeValue);
                } catch (InvalidObjectException ex) {
                    // TODO: Warn the user.
                }
                */
            }
            logger.log(Level.FINE, "ValuePropertyEditor.getValue() = (current) {0}", currentValue);
            return currentValue;
        }
    }
    
    private void setOrigValue(Object obj) {
        if (obj instanceof Variable) {
            Variable var = (Variable) obj;
            if (vtm == null) {
                List<? extends TableModel> models = contextProvider.lookup("LocalsView", TableModel.class);
                for (TableModel tm : models) {
                    if (tm instanceof VariablesTableModel) {
                        vtm = (VariablesTableModel) tm;
                        break;
                    }
                }
                if (vtm == null) {
                    return ;
                }
            }
            vtm.setOrigValue(var);
        }
    }
    
    private void setVarFromMirror(final MutableVariable var, final Object mirror) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    var.setFromMirrorObject(mirror);
                } catch (InvalidObjectException ioex) {
                    NotifyDescriptor nd = new NotifyDescriptor.Message(ioex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            RequestProcessor rp = contextProvider.lookupFirst(null, RequestProcessor.class);
            rp.post(run);
        } else {
            run.run();
        }
    }

    @Override
    public boolean isPaintable() {
        //System.out.println("ValuePropertyEditor.isPaintable("+delegatePropertyEditor+")");
        return delegatePropertyEditor.isPaintable();
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        delegatePropertyEditor.paintValue(gfx, box);
    }

    @Override
    public String getJavaInitializationString() {
        return delegatePropertyEditor.getJavaInitializationString();
    }

    @Override
    public String getAsText() {
        return delegatePropertyEditor.getAsText();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        logger.log(Level.FINE, "ValuePropertyEditor.setAsText({0}) calling {1}",
                   new Object[]{text, delegatePropertyEditor});
        delegatePropertyEditor.setAsText(text);
    }

    @Override
    public String[] getTags() {
        return delegatePropertyEditor.getTags();
    }

    @Override
    public Component getCustomEditor() {
        return delegatePropertyEditor.getCustomEditor();
    }

    @Override
    public boolean supportsCustomEditor() {
        logger.log(Level.FINE, "ValuePropertyEditor.supportsCustomEditor({0})",
                   delegatePropertyEditor);
        return delegatePropertyEditor.supportsCustomEditor();
    }

    @Override
    public void attachEnv(PropertyEnv env) {
        //System.out.println("ValuePropertyEditor.attachEnv("+env+"), feature descriptor = "+env.getFeatureDescriptor());
        if (delegatePropertyEditor instanceof ExPropertyEditor) {
            //System.out.println("  attaches to "+delegatePropertyEditor);
            ((ExPropertyEditor) delegatePropertyEditor).attachEnv(env);
            this.env = env;
        }
    }
    
    void checkPropertyEnv() {
        if (env != null && delegatePropertyEditor instanceof ExPropertyEditor) {
            ((ExPropertyEditor) delegatePropertyEditor).attachEnv(env);
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
        logger.log(Level.FINE, "ValuePropertyEditor.addPropertyChangeListener({0})", listener);
        delegatePropertyEditor.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
        logger.log(Level.FINE, "ValuePropertyEditor.removePropertyChangeListener({0})", listener);
        delegatePropertyEditor.removePropertyChangeListener(listener);
    }
    
    /**
     * An artificial variable that holds the newly set mirror object.
     */
    static class VariableWithMirror implements MutableVariable, ObjectVariable {
        
        private Object mirror;
        
        VariableWithMirror(Object mirror) {
            this.mirror = mirror;
        }

        @Override
        public void setValue(String value) throws InvalidExpressionException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setFromMirrorObject(Object obj) {
            this.mirror = obj;
        }

        @Override
        public String getType() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getValue() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Object createMirrorObject() {
            return mirror;
        }

        @Override
        public String getToStringValue() throws InvalidExpressionException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Variable invokeMethod(String methodName, String signature, Variable[] arguments) throws NoSuchMethodException, InvalidExpressionException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int getFieldsCount() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Field getField(String name) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Field[] getFields(int from, int to) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Field[] getAllStaticFields(int from, int to) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Field[] getInheritedFields(int from, int to) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public List<ObjectVariable> getReferringObjects(long maxReferrers) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Super getSuper() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public JPDAClassType getClassType() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public long getUniqueID() {
            throw new UnsupportedOperationException("Not supported.");
        }
        
    }
}
