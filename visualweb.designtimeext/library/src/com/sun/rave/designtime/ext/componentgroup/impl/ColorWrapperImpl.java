/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://woodstock.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://woodstock.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 */
package com.sun.rave.designtime.ext.componentgroup.impl;

import com.sun.rave.designtime.ext.componentgroup.ColorWrapper;
import java.awt.Color;

/**
 * <p>Implementation that wraps a color that is stored in the design context data.</p>
 * @author mbohm
 */
public class ColorWrapperImpl implements ColorWrapper {

    private Color color;
    
    /**
     * <p>Constructor that accepts a <code>Color</code>.</p>
     */ 
    public ColorWrapperImpl(Color color) {
        this.color = color;
    }
    
    /**
     * <p>Constructor that accepts a <code>String</code> 
     * representing a color.</p>
     */ 
    public ColorWrapperImpl(String fromString) {
        String[] split = fromString.split(","); // NOI18N
        if (split.length > 2) {
            int r = Integer.parseInt(split[0]);
            int g = Integer.parseInt(split[1]);
            int b = Integer.parseInt(split[2]);
            this.color = new Color(r, g, b);
        }
    }
   
    /**
     * <p>Get the wrapped color.</p>
     */ 
    public Color getColor() {
        return color;
    }
    
    /**
     * <p>Get a string containing the RGB information for the wrapped color.</p>
     */
    public String toString() {
        if (color != null) {
            return color.getRed() + "," + // NOI18N
                   color.getGreen() + "," +  // NOI18N
                   color.getBlue();
        }
        return "";  // NOI18N
    }
}
