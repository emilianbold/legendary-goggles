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
package org.netbeans.modules.visualweb.faces.dt.std;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import javax.faces.application.Application;
import javax.faces.el.ValueBinding;
import com.sun.rave.designtime.DesignProperty;
import com.sun.rave.designtime.faces.FacesDesignContext;
import com.sun.rave.designtime.faces.FacesDesignProperty;
import com.sun.rave.designtime.faces.FacesBindingPropertyEditor;

public class ValueBindingPropertyEditor extends PropertyEditorSupport implements
    FacesBindingPropertyEditor {

    //------------------------------------------------------------------------------- PropertyEditor

    public Object getValue() {
        Object value = super.getValue();
        //System.err.println("VBPE.getValue value:" + value + "        " + System.currentTimeMillis());
        return value;
    }

    public void setValue(Object value) {
        Object v = getValue();
//        if ((value == v) ||
//            (value != null && value.equals(v)) ||
//            (value instanceof ValueBinding && ((ValueBinding)value).getExpressionString().equals(v)) ||
//            (v instanceof ValueBinding && ((ValueBinding)v).getExpressionString().equals(value)) ||
//            (value instanceof ValueBinding && v instanceof ValueBinding &&
//                ((ValueBinding)value).getExpressionString().equals(((ValueBinding)v).getExpressionString()))) {
//            return;
//        }
        //System.err.println("VBPE.setValue value:" + value + "        " + System.currentTimeMillis());
        this.quiet = true;
        if (facesDesignProperty != null && facesDesignProperty.isBound()) {
            super.setValue(facesDesignProperty.getValueBinding());
        } else {
            super.setValue(value);
        }
        this.quiet = false;
    }

    protected void superSetValue(Object value) {

        super.setValue(value);
    }

    protected boolean quiet = false;
    public void firePropertyChange() {
        if (quiet) {
            return;
        }
        super.firePropertyChange();
    }

    public String getAsText() {
        Object value = getValue();
        if (value instanceof ValueBinding) {
            return ((ValueBinding)value).getExpressionString();
        } else if (facesDesignProperty != null && facesDesignProperty.isBound()) {
            return facesDesignProperty.getValueBinding().getExpressionString();
        }
        return value != null ? value.toString() : ""; //NOI18N
    }

    public void setAsText(String text) throws IllegalArgumentException {
        if (text.startsWith("#{")) { //NOI18N
            FacesDesignContext fctx = (FacesDesignContext)liveProperty.getDesignBean().getDesignContext();
            Application app = fctx.getFacesContext().getApplication();
            super.setValue(app.createValueBinding(text));
        } else if (text.length() > 0) {
            super.setValue(text);
        } else {
            super.setValue(null);
        }
    }

    public String getJavaInitializationString() {
        return "\"" + getAsText() + "\""; //NOI18N
    }

    public boolean supportsCustomEditor() {
        return true;
    }

    public Component getCustomEditor() {
        ValueBindingPanel vbp = new ValueBindingPanel(this, liveProperty);
        return vbp;
    }

    //--------------------------------------------------------------------------- PropertyEditor2

    // use only for reference and lookup

    protected FacesDesignProperty facesDesignProperty;
    protected DesignProperty liveProperty;

    public void setDesignProperty(DesignProperty lp) {
        this.liveProperty = lp;
        this.facesDesignProperty = lp instanceof FacesDesignProperty ? (FacesDesignProperty)lp : null;
    }
}
