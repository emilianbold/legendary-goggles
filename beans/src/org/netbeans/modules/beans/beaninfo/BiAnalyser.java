/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.beans.beaninfo;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.modules.beans.EventSetPattern;
import org.netbeans.modules.beans.GenerateBeanException;
import org.netbeans.modules.beans.IdxPropertyPattern;
import org.netbeans.modules.beans.PatternAnalyser;
import org.netbeans.modules.beans.PropertyPattern;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/** Analyses the ClassElement trying to find source code patterns i.e.
 * properties or event sets;
 *
 * @author Petr Hrebejk, Petr Suchomel
 */

public final class BiAnalyser {

    private static final String TAB = "    "; // NOI18N
    private static final String TABx2 = TAB +TAB;
    private static final String TABx3 = TAB + TABx2;
    
    private static final String NOI18N_COMMENT = " // NOI18N"; // NOI18N

    private static final String ICONNAME_C16 = "iconNameC16"; // NOI18N
    private static final String ICONNAME_C32 = "iconNameC32"; // NOI18N
    private static final String ICONNAME_M16 = "iconNameM16"; // NOI18N
    private static final String ICONNAME_M32 =  "iconNameM32"; // NOI18N

    private static final String DEFAULT_PROPERTY_INDEX = "defaultPropertyIndex"; // NOI18N
    private static final String DEFAULT_EVENT_INDEX = "defaultEventIndex"; // NOI18N

    /** Holds Bean descriptor */
    List<BiFeature.Descriptor> descriptor;
    
    /** Holds all properties */
    List<BiFeature.Property> properties;

    /** Holds all indexed properties */
    List<BiFeature.IdxProperty> idxProperties;

    /** Holds all events sets */
    List<BiFeature.EventSet> eventSets;

    /** Holds all methods */
    List<BiFeature.Method> methods;

    /** Object representing source code of associated BeanInfo */
    BeanInfoSource bis;

    /** Should bean descriptor be obtained from introspection */
    private boolean nullDescriptor = false;

    /** Should properties be obtained from introspection */
    private boolean nullProperties = false;

    /** Should event sets be obtained from introspection */
    private boolean nullEventSets = false;

    /** Should methods be obtained from introspection */
    private boolean nullMethods = false;

    /** Should bean descriptor have lazy init */
    private boolean lazyDescriptor = true;

    /** Should properties have lazy init */
    private boolean lazyProperties = true;

    /** Should event sets have lazy init */
    private boolean lazyEventSets = true;

    /** Should methods have lazy init */
    private boolean lazyMethods = true;
    
    /** Is the version of BeanInfo generated by older beans module? */
    private final boolean olderVersion;
    /** Is the version of BeanInfo generated by new beans module with superclass? */
    private boolean superClassVersion=true;

    /* Holds the class for which the bean info is generated */
    private String classfqn;

    private String iconC16;
    private String iconM16;
    private String iconC32;
    private String iconM32;
    private int defaultPropertyIndex = -1;
    private int defaultEventIndex = -1;
    private boolean useSuperClass = false;
    private boolean isModified = false;
    
    private int getIndexOfMethod(List<BiFeature.Method> al, ElementHandle<ExecutableElement> method) {
        if (method == null) return -1;
        
        int i = 0;
        for (BiFeature.Method bifMethod : al) {
            if (method.equals(bifMethod.getElement())) {
                return i;
            }
            ++i;
        }
        
        return -1;
    }
    /** Creates Bean Info analyser which contains all patterns from PatternAnalyser
    */
    BiAnalyser ( PatternAnalyser pa, CompilationInfo javac ) throws GenerateBeanException {
        int index;

        // Try to find and analyse existing bean info
        bis = new BeanInfoSource( pa.getFileObject() );
        olderVersion = (bis.isNbBeanInfo() && bis.getMethodsSection() == null);
        superClassVersion = (bis.isNbSuperclass() || !bis.exists());
        
        TypeElement classElement = pa.getClassElementHandle().resolve(javac);
        this.classfqn = classElement.getQualifiedName().toString();
        
        // Fill Descriptor list (only in case we have new templates)
        descriptor = new ArrayList<BiFeature.Descriptor>();
        descriptor.add(new BiFeature.Descriptor(classElement));

        // Fill methods list (only in case we have new templates)
        methods = new  ArrayList<BiFeature.Method>();
        if (!olderVersion) {
            for (ExecutableElement method : ElementFilter.methodsIn(classElement.getEnclosedElements())) {
                methods.add(new BiFeature.Method(method, pa, javac));
            }
        }

        // Fill properties list
        List<PropertyPattern> propertyPatterns = pa.getPropertyPatterns();
        properties = new  ArrayList<BiFeature.Property>(propertyPatterns.size());
        for (PropertyPattern pp : propertyPatterns) {
            properties.add(new BiFeature.Property(pp, javac));
            for (int i = 0; i < methods.size(); i ++) {
                if ((index = getIndexOfMethod(methods, pp.getGetterMethod())) != -1) methods.remove(index);
                if ((index = getIndexOfMethod(methods, pp.getSetterMethod())) != -1) methods.remove(index);
            }
        }

        // Fill indexed properties list
        List<IdxPropertyPattern> idxPropertyPatterns = pa.getIdxPropertyPatterns();
        idxProperties = new  ArrayList<BiFeature.IdxProperty>(idxPropertyPatterns.size());
        for (IdxPropertyPattern ipp : idxPropertyPatterns) {
            TypeMirror type = ipp.getType().resolve(javac);
            TypeMirror idxtype = ipp.getIndexedType().resolve(javac);
            if (type.getKind() != TypeKind.ARRAY || !javac.getTypes().isSameType(((ArrayType) type).getComponentType(), idxtype)) {
                continue;
            }

            idxProperties.add(new BiFeature.IdxProperty(ipp, javac));
            if ((index = getIndexOfMethod(methods, ipp.getGetterMethod())) != -1) methods.remove(index);
            if ((index = getIndexOfMethod(methods, ipp.getSetterMethod())) != -1) methods.remove(index);
            if ((index = getIndexOfMethod(methods, ipp.getIndexedGetterMethod())) != -1) methods.remove(index);
            if ((index = getIndexOfMethod(methods, ipp.getIndexedSetterMethod())) != -1) methods.remove(index);
        }

        // Fill event sets list
        List<EventSetPattern> eventSetPatterns = pa.getEventSetPatterns();
        eventSets = new  ArrayList<BiFeature.EventSet>(eventSetPatterns.size());
        for (EventSetPattern esp : eventSetPatterns) {
            eventSets.add(new BiFeature.EventSet(esp, javac));
            if ((index = getIndexOfMethod(methods, esp.getRemoveListenerMethod())) != -1) methods.remove(index);
            if ((index = getIndexOfMethod(methods, esp.getAddListenerMethod())) != -1) methods.remove(index);
        }

        analyzeBeanInfoSource( );

    }
    
    List<BiFeature.Descriptor> getDescriptor() {
        return descriptor;
    }
    
    List<BiFeature.Property> getProperties() {
        return properties;
    }

    List<BiFeature.IdxProperty> getIdxProperties() {
        return idxProperties;
    }

    List<BiFeature.EventSet> getEventSets() {
        return eventSets;
    }

    List<BiFeature.Method> getMethods() {
        return methods;
    }

    public boolean isOlderVersion() {
        return olderVersion;
    }
    
    public boolean isSuperclassVersion() {
        return superClassVersion;
    }

    public String getIconC16() {
        return iconC16;
    }

    public void setIconC16(String iconC16) {
        this.iconC16 = iconC16;
        setModified();
    }

    public String getIconM16() {
        return iconM16;
    }

    public void setIconM16(String iconM16) {
        this.iconM16 = iconM16;
        setModified();
    }

    public String getIconC32() {
        return iconC32;
    }

    public void setIconC32(String iconC32) {
        this.iconC32 = iconC32;
        setModified();
    }

    public String getIconM32() {
        return iconM32;
    }

    public void setIconM32(String iconM32) {
        this.iconM32 = iconM32;
        setModified();
    }

    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }

    public void setDefaultPropertyIndex(int defaultPropertyIndex) {
        this.defaultPropertyIndex = defaultPropertyIndex;
        setModified();
    }

    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }

    public void setDefaultEventIndex(int defaultEventIndex) {
        this.defaultEventIndex = defaultEventIndex;
        setModified();
    }

    /** Getter for property useSuperClass.
     * @return Value of property useSuperClass.
     */
    public boolean isUseSuperClass() {
        return this.useSuperClass;
    }
    
    /** Setter for property useSuperClass.
     * @param useSuperClass New value of property useSuperClass.
     */
    public void setUseSuperClass(boolean useSuperClass) {
        this.useSuperClass = useSuperClass;
        setModified();
    }
    
    boolean isNullDescriptor() {
        return nullDescriptor;
    }

    boolean isNullProperties() {
        return nullProperties;
    }

    boolean isNullMethods() {
        return nullMethods;
    }

    void setNullDescriptor( boolean nullDescriptor ) {
        this.nullDescriptor = nullDescriptor;
        setModified();
    }

    void setNullProperties( boolean nullProperties ) {
        this.nullProperties = nullProperties;
        setModified();
    }

    void setNullMethods( boolean nullMethods ) {
        this.nullMethods = nullMethods;
        setModified();
    }

    boolean isNullEventSets() {
        return nullEventSets;
    }

    void setNullEventSets( boolean nullEventSets ) {
        this.nullEventSets = nullEventSets;
        setModified();
    }

    public boolean isLazyDescriptor() {
        return lazyDescriptor;
    }

    public boolean isLazyProperties() {
        return lazyProperties;
    }

    public boolean isLazyMethods() {
        return lazyMethods;
    }

    public void setLazyDescriptor( boolean lazyDescriptor ) {
        this.lazyDescriptor = lazyDescriptor;
        setModified();
    }

    public void setLazyProperties( boolean lazyProperties ) {
        this.lazyProperties = lazyProperties;
        setModified();
    }

    public void setLazyMethods( boolean lazyMethods ) {
        this.lazyMethods = lazyMethods;
        setModified();
    }

    public boolean isLazyEventSets() {
        return lazyEventSets;
    }

    public void setLazyEventSets( boolean lazyEventSets ) {
        this.lazyEventSets = lazyEventSets;
        setModified();
    }
    
    void regenerateSource() {
        if ( bis.exists() && !bis.isNbBeanInfo()) {
            throw new IllegalStateException();
        }
        
        Runnable task = new Runnable() {

                public void run() {
                    regenerateSourceImpl();
                }
            };
            
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(task);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    void regenerateSourceImpl() {
        DataObject dataObject = bis.getDataObject();
        EditorCookie editor = dataObject.getLookup().lookup(EditorCookie.class);
        StyledDocument doc = editor.getDocument();
        NbDocument.runAtomic(doc, new Runnable() {
                public void run()  {
                    regenerateBeanDescriptor();
                    regenerateProperties();
                    regenerateEvents();
                    if (!olderVersion) {
                        regenerateMethods();
                    }
                    regenerateIcons();
                    regenerateDefaultIdx();
                    regenerateSuperclass();
                    isModified = false;
                }
        } );
    }
    
    void openSource() {

        if ( bis.exists() ) {

            if ( !bis.isNbBeanInfo() ) {
                
                String mssg = GenerateBeanInfoAction.getString( "MSG_BeanInfoExists" );  // NOI18N
                NotifyDescriptor nd = new NotifyDescriptor.Confirmation ( mssg, NotifyDescriptor.YES_NO_OPTION );
                DialogDisplayer.getDefault().notify( nd );
                if ( !nd.getValue().equals ( NotifyDescriptor.YES_OPTION ) ) {
                    return;
                }

                try {
                    bis.delete();
                }
                catch ( IOException e ) {
                    mssg = GenerateBeanInfoAction.getString( "MSG_BeanInfoCantDelete" );  // NOI18N
                    nd = new NotifyDescriptor.Message ( mssg );
                    DialogDisplayer.getDefault().notify( nd );
                    return;
                }
                bis.createFromTemplate(iconBlockRequired());
                regenerateSource();
            }
        }
        else {
            // notify user about missing beaninfo and ask if generate new one.
            String mssg = NbBundle.getMessage(BiAnalyser.class, "MSG_BeanInfoNotExists");
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation ( mssg, NotifyDescriptor.YES_NO_OPTION );
            DialogDisplayer.getDefault().notify( nd );
            if ( !nd.getValue().equals ( NotifyDescriptor.YES_OPTION ) ) {
                return;
            }
            bis.createFromTemplate(iconBlockRequired());
            regenerateSource();

            if ( !bis.isNbBeanInfo() ) {
                // XXX notify user about wrong template
                return;
            }

        }
        
        bis.open();

    }

    private void regenerateBeanDescriptor() {
        StringBuilder sb = new StringBuilder( 512 );
                
        if ( nullDescriptor ) {
            sb.append( TAB + GenerateBeanInfoAction.getString( "COMMENT_NullDescriptor" ) );  // NOI18N
            sb.append( TAB + "private static BeanDescriptor beanDescriptor = null;\n" ); // NOI18N
            sb.append( TAB  + "private static BeanDescriptor getBdescriptor(){\n\n");  // NOI18N
            bis.setDescriptorSection( sb.toString(), "\n" + TABx2+ "return beanDescriptor;\n" + TAB + "}\n\n" ); // NOI18N
            return;
        }
        
        for (BiFeature.Descriptor bif : getDescriptor()) {
            if( bif.isIncluded() ){
                sb.append( TAB + GenerateBeanInfoAction.getString("COMMENT_BeanDescriptor" ));   // NOI18N
                if( !lazyDescriptor ){
                    //this code is used for static init                    
                    sb.append( TAB + "private static BeanDescriptor beanDescriptor = ");    // NOI18N
                    sb.append( bif.getCreationString() );
                    sb.append(';');
                    appendNoi18nText(sb);
                    sb.append( "\n\n" ); // NOI18N

                    sb.append( TAB  + "private static BeanDescriptor getBdescriptor(){\n");  // NOI18N
                    sb.append( TABx2+ "return beanDescriptor;\n" + TAB + "}\n\n" ); // NOI18N

                    sb.append( TAB + "static {\n" ); // NOI18N
                }
                else {
                    sb.append( TAB + "/*lazy BeanDescriptor*/\n");    // NOI18N
                    sb.append( TAB  + "private static BeanDescriptor getBdescriptor(){\n");  // NOI18N
                    sb.append( TABx2+ "BeanDescriptor beanDescriptor = ");  // NOI18N
                    sb.append( bif.getCreationString() );
                    sb.append(';');
                    appendNoi18nText(sb);
                    sb.append( '\n' ); // NOI18N
                }
                
                for (String line : bif.getCustomizationStrings()) {
                    sb.append( TABx2 + "beanDescriptor."); // NOI18N
                    sb.append( line ).append( ";\n" ); // NOI18N
                }
                if( !lazyDescriptor ){
                    bis.setDescriptorSection( sb.toString(), "}\n"); // NOI18N
                }
                else {
                    bis.setDescriptorSection( sb.toString(), TABx2+ "return beanDescriptor;\n" + TAB+ "}\n"); // NOI18N
                }
            }            
        }
    }
    
    /** Regenerates the property section of BeanInfo */
    private void regenerateProperties() {
        StringBuilder sb = new StringBuilder(512);
        int propertyCount = 0;

        if ( nullProperties ) {
            sb.append( TAB + GenerateBeanInfoAction.getString( "COMMENT_NullProperties" ) );  // NOI18N
            sb.append( TAB + "private static PropertyDescriptor[] properties = null;\n" ); // NOI18N
            sb.append( TAB  + "private static PropertyDescriptor[] getPdescriptor(){\n");  // NOI18N
            bis.setPropertiesSection( sb.toString(), TABx2+ "return properties;\n" + TAB + "}\n\n" ); // NOI18N
            return;
        }

        // Make common list of all properites
        Set<BiFeature.Property> allProperties = new  TreeSet<BiFeature.Property>(getProperties());
        allProperties.addAll(getIdxProperties());

        sb.append( TAB + GenerateBeanInfoAction.getString( "COMMENT_PropertyIdentifiers" ) );  // NOI18N

        for (BiFeature.Property bif : allProperties) {

            if ( bif.isIncluded() ) {
                sb.append( TAB + "private static final int " ); // NOI18N
                // This prefix MUST be consistent w/ BiFeature.IdxProperty analyser
                sb.append( "PROPERTY_" + bif.getName() ); // NOI18N
                sb.append( " = " + (propertyCount++) + ";" ); // NOI18N
                sb.append( "\n" ); // NOI18N
            }
        }

        sb.append( "\n" + TAB + GenerateBeanInfoAction.getString("COMMENT_PropertyArray" ));  // NOI18N
        if( !lazyProperties ){            
            sb.append( TAB + "private static PropertyDescriptor[] properties = new PropertyDescriptor[" + // NOI18N
                       propertyCount + "];\n\n" ); // NOI18N
            sb.append( TAB  + "private static PropertyDescriptor[] getPdescriptor(){\n");  // NOI18N
            sb.append( TABx2+ "return properties;\n" + TAB + "}\n\n" ); // NOI18N
        }
        else{
            //lazy init
            sb.append( TAB + "/*lazy PropertyDescriptor*/\n");    // NOI18N
            sb.append( TAB  + "private static PropertyDescriptor[] getPdescriptor(){\n");   // NOI18N
            sb.append( TABx2+ "PropertyDescriptor[] properties = new PropertyDescriptor[");  // NOI18N
            sb.append( propertyCount );
            sb.append( "];\n" ); // NOI18N
        }
        if ( propertyCount > 0) {
            if( !lazyProperties ){
                sb.append( TAB + "static {\n" + TABx2 + "try {\n" ); // NOI18N
            }
            else {
                sb.append( TAB + "\n" + TABx2 + "try {\n" ); // NOI18N
            }
        }
        
        for (BiFeature.Property bif : allProperties) {

            if ( bif.isIncluded() ) {
                sb.append( TABx3 + "properties[PROPERTY_" ).append( bif.getName() ).append("] = "); // NOI18N
                sb.append(bif.getCreationString());
                sb.append(';');
                appendNoi18nText(sb);
                sb.append('\n');

                for (String line : bif.getCustomizationStrings()) {
                    sb.append( TABx3 + "properties[PROPERTY_" ).append( bif.getName() ).append("]."); // NOI18N
                    sb.append( line ).append( ";\n" ); // NOI18N
                }
            }
        }

        if ( propertyCount > 0 )
            sb.append( TABx2 + "}\n" +  TABx2 + "catch(IntrospectionException e) {\n" + TABx3 + "e.printStackTrace();\n" + TABx2 + "}" ); // NOI18N

        if( !lazyProperties ){
            bis.setPropertiesSection( sb.toString(), propertyCount > 0 ? "}\n" : "  \n" ); // NOI18N
        }
        else{
            bis.setPropertiesSection( sb.toString(), TABx2+ "return properties;\n" + TAB + "}\n"); // NOI18N
        }
    }

    /** Regenerates the method section of BeanInfo */
    private void regenerateMethods() {
        StringBuilder sb = new StringBuilder( 512 );
        int methodCount = 0;


        if ( nullMethods ) {
            sb.append( TAB + GenerateBeanInfoAction.getString( "COMMENT_NullMethods" ) );  // NOI18N
            sb.append( TAB + "private static MethodDescriptor[] methods = null;\n" ); // NOI18N
            sb.append( TAB  + "private static MethodDescriptor[] getMdescriptor(){\n");  // NOI18N
            bis.setMethodsSection( sb.toString(), TABx2+ "return methods;\n" + TAB + "}\n\n" ); // NOI18N
            return;
        }

        // Make common list of all methods
        Set<BiFeature.Method> allMethods = new  TreeSet<BiFeature.Method>( getMethods() );

        sb.append( TAB + GenerateBeanInfoAction.getString( "COMMENT_MethodIdentifiers" ) );  // NOI18N

        for (BiFeature.Method bif : allMethods) {
            if ( bif.isIncluded() ) {
                sb.append( TAB + "private static final int " ); // NOI18N
                sb.append( "METHOD_" + bif.getName() + methodCount ); // NOI18N
                sb.append( " = " + (methodCount++) + ";" ); // NOI18N
                sb.append( "\n" ); // NOI18N
            }
        }

        sb.append( "\n" + TAB + GenerateBeanInfoAction.getString("COMMENT_MethodArray" ));  // NOI18N
        if( !lazyMethods ){  
            sb.append( TAB + "private static MethodDescriptor[] methods = new MethodDescriptor[" + // NOI18N
                       methodCount + "];\n\n" ); // NOI18N
            sb.append( TAB  + "private static MethodDescriptor[] getMdescriptor(){\n");  // NOI18N
            sb.append( TABx2+ "return methods;\n" + TAB + "}\n\n" ); // NOI18N
        }
        else{
            //lazy init
            sb.append( TAB + "/*lazy MethodDescriptor*/\n");    // NOI18N
            sb.append( TAB  + "private static MethodDescriptor[] getMdescriptor(){\n");  // NOI18N
            sb.append( TABx2+ "MethodDescriptor[] methods = new MethodDescriptor[");  // NOI18N
            sb.append( methodCount );
            sb.append( "];\n" ); // NOI18N
        }
            

        if ( methodCount > 0) {
            if( !lazyMethods ){           
                sb.append( TAB + "static {\n" + TABx2 + "try {\n" ); // NOI18N
            }
            else {
                sb.append( TAB + "\n" + TABx2 + "try {\n" ); // NOI18N
            }
        }

        Iterator<BiFeature.Method> it = allMethods.iterator();
        
        for ( int i = 0, lCurMethodCount = 0; it.hasNext(); ) {
            BiFeature bif = it.next();

            if ( bif.isIncluded() ) {
                sb.append( TABx3 + "methods[METHOD_" ).append( bif.getName() ).append(lCurMethodCount++ + "] = "); // NOI18N
                sb.append(bif.getCreationString());
                sb.append(';');
                appendNoi18nText(sb);
                sb.append('\n');

                for (String line : bif.getCustomizationStrings()) {
                    sb.append( TABx3 + "methods[METHOD_" ).append( bif.getName() ).append(i + "]."); // NOI18N
                    sb.append( line ).append( ";\n" ); // NOI18N
                }
                i++;
            }
        }

        if ( methodCount > 0 )
            sb.append( TABx2 + "}\n" +  TABx2 + "catch( Exception e) {}" ); // NOI18N

        if( !lazyMethods ){
            bis.setMethodsSection( sb.toString(), methodCount > 0 ? "}\n" : "  \n" ); // NOI18N
        }
        else{
            bis.setMethodsSection( sb.toString(), TABx2+ "return methods;\n" + TAB + "}\n"); // NOI18N
        }
    }

    /** Regenerates the event set section of BeanInfo */
    private void regenerateEvents() {
        StringBuilder sb = new StringBuilder( 512 );
        int eventCount = 0;

        if ( nullEventSets ) {
            sb.append( TAB + GenerateBeanInfoAction.getString( "COMMENT_NullEventSets" ) );  // NOI18N
            sb.append( TAB + "private static EventSetDescriptor[] eventSets = null;\n" ); // NOI18N
            sb.append( TAB  + "private static EventSetDescriptor[] getEdescriptor(){\n");  // NOI18N
            bis.setEventSetsSection( sb.toString(), TABx2+ "return eventSets;\n" + TAB + "}\n\n" ); // NOI18N
            return;
        }

        sb.append( TAB + GenerateBeanInfoAction.getString("COMMENT_EventSetsIdentifiers") );  // NOI18N

        Set<BiFeature.EventSet> events = new  TreeSet<BiFeature.EventSet>(eventSets);
        for (BiFeature.EventSet bif : events) {
            if ( bif.isIncluded() ) {
                sb.append( TAB + "private static final int " ); // NOI18N
                sb.append( "EVENT_" + bif.getName() ); // NOI18N
                sb.append( " = " + (eventCount++) + ";" ); // NOI18N
                sb.append( "\n" ); // NOI18N
            }
        }

        sb.append( "\n" + TAB + GenerateBeanInfoAction.getString("COMMENT_EventSetsArray"));
        if( !lazyEventSets ){            
            sb.append( TAB + "private static EventSetDescriptor[] eventSets = new EventSetDescriptor[" + // NOI18N
                       eventCount + "];\n\n" ); // NOI18N
            sb.append( TAB  + "private static EventSetDescriptor[] getEdescriptor(){\n");  // NOI18N
            sb.append( TABx2+ "return eventSets;\n" + TAB + "}\n\n" ); // NOI18N
        }
        else{
            //lazy init
            sb.append( TAB + "/*lazy EventSetDescriptor*/\n");    // NOI18N
            sb.append( TAB  + "private static EventSetDescriptor[] getEdescriptor(){\n");  // NOI18N
            sb.append( TABx2+ "EventSetDescriptor[] eventSets = new EventSetDescriptor[");  // NOI18N
            sb.append( eventCount );
            sb.append( "];\n" ); // NOI18N
        }

        if ( eventCount > 0 ){
            if( !lazyEventSets ){
                sb.append( TAB + "static {\n" + TABx2 + "try {\n" ); // NOI18N
            }
            else {
                sb.append( TAB + "\n" + TABx2 + "try {\n" ); // NOI18N
            }
        }

        for (BiFeature.EventSet bif : events) {
            if ( bif.isIncluded() ) {
                // the index prefix MUST be consistent w/ BiFeature.EventSet analyser.
                sb.append( TABx3 + "eventSets[EVENT_" ).append( bif.getName() ).append("] = "); // NOI18N
                sb.append( bif.getCreationString() );
                sb.append(';');
                appendNoi18nText(sb);
                sb.append('\n');

                for (String line : bif.getCustomizationStrings()) {
                    sb.append( TABx3 + "eventSets[EVENT_" ).append( bif.getName() ).append("]."); // NOI18N
                    sb.append( line ).append( ";\n" ); // NOI18N
                }
            }
        }

        if ( eventCount > 0 )
            sb.append( TABx2 + "}\n" +  TABx2 + "catch(IntrospectionException e) {\n" + TABx3 + "e.printStackTrace();\n" + TABx2 + "}" ); // NOI18N

        if( !lazyEventSets ){
            bis.setEventSetsSection( sb.toString(), eventCount > 0 ? "}\n" : "  \n"); // NOI18N
        }
        else{
            bis.setEventSetsSection( sb.toString(), TABx2+ "return eventSets;\n" + TAB + "}\n"); // NOI18N
        }
    }

    /** Generate image icon section */
    private void regenerateIcons() {
        if(  iconBlockRequired() ) {
            StringBuilder sb = new StringBuilder( 200 );

            sb.append( getIconDeclaration( ICONNAME_C16, iconC16 ));
            sb.append( getIconDeclaration( ICONNAME_C32, iconC32 ));
            sb.append( getIconDeclaration( ICONNAME_M16, iconM16 ));
            sb.append( getIconDeclaration( ICONNAME_M32, iconM32 ));

            bis.setIconsSection( sb.toString() );
        }
    }

    private boolean iconBlockRequired(){
        return (iconC16 != null | iconC32 != null | iconM16 != null | iconM32 != null);
    }
    
    private static String getIconDeclaration( String name, String resource ) {
        StringBuilder sb = new StringBuilder( 80 );

        sb.append( TAB + "private static String " ).append( name ).append( " = "); // NOI18N
        if ( resource == null || resource.trim().length() == 0 )
            sb.append( "null;\n"); // NOI18N
        else
            sb.append("\"").append( resource.trim() ).append("\";\n"); // NOI18N
        return sb.toString();
    }

    private void regenerateDefaultIdx() {
        StringBuilder sb = new StringBuilder(100);

        sb.append( TAB + "private static final int " + DEFAULT_PROPERTY_INDEX + " = ").append( defaultPropertyIndex ).append( ";\n"); // NOI18N
        sb.append( TAB + "private static final int " + DEFAULT_EVENT_INDEX + " = ").append( defaultEventIndex ).append( ";\n"); // NOI18N

        bis.setDefaultIdxSection( sb.toString() );
    }

    private void regenerateSuperclass() {
        StringBuilder sb = new StringBuilder(100);
        if( this.isUseSuperClass() ){
            sb.append( TAB + "public BeanInfo[] getAdditionalBeanInfo() {\n");  // NOI18N
            sb.append( TABx2 + "Class superclass = " + classfqn + ".class.getSuperclass();\n");  // NOI18N
            sb.append( TABx2 + "BeanInfo sbi = null;\n");  // NOI18N
            sb.append( TABx2 + "try {\n");  // NOI18N
            sb.append( TABx2 + TAB + "sbi = Introspector.getBeanInfo(superclass);\n");  // NOI18N
                          
            bis.setSuperclassSection( sb.toString(), TABx3 + "}\ncatch(IntrospectionException ex) {\n}\n\nreturn new BeanInfo[] { sbi };\n}\n"); // NOI18N
        }
        else{
            bis.setSuperclassSection( "\n", "\n");  // NOI18N
        }
    }

    /** Analyzes existing BeanInfo */
    private void analyzeBeanInfoSource() throws GenerateBeanException {

        if ( !bis.isNbBeanInfo() )
            return;

        String section = bis.getIconsSection();
        List<String> code = normalizeText( section );
        setIconsFromBeanInfo( code );

        section = bis.getDefaultIdxSection();
        code = normalizeText( section );
        setDefaultIdxFromBeanInfo( code );

        section = bis.getDescriptorSection();
        code = normalizeText( section );
        nullDescriptor = setPropertiesFromBeanInfo( descriptor, code, "BeanDescriptor" ); // NOI18N
        if ( !nullDescriptor ){
            setLazyDescriptor( isLazy( code, "BeanDescriptor" ) ); // NOI18N
        }
        
        section = bis.getPropertiesSection();
        code = normalizeText( section );
        nullProperties = setPropertiesFromBeanInfo( properties, code, "PropertyDescriptor[]" ); // NOI18N
        if ( !nullProperties ){
            setLazyProperties( isLazy( code, "PropertyDescriptor" ) ); // NOI18N
            setPropertiesFromBeanInfo( idxProperties, code, "PropertyDescriptor[]" ); // NOI18N
        }
        
        section = bis.getMethodsSection();
        if (section == null) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(GenerateBeanInfoAction.getString("MSG_Old_Version"), NotifyDescriptor.WARNING_MESSAGE));  // NOI18N
            nullMethods = true;
        } else {
            code = normalizeText(section);
            nullMethods = setPropertiesFromBeanInfo(methods, code, "MethodDescriptor[]"); // NOI18N
            if( !nullMethods ){
                setLazyMethods( isLazy( code, "MethodDescriptor" ) ); // NOI18N
            }
        }

        section = bis.getEventSetsSection();
        code = normalizeText( section );
        nullEventSets = setPropertiesFromBeanInfo( eventSets, code, "EventSetDescriptor[]" ); // NOI18N
        if( !nullEventSets ){
            setLazyEventSets( isLazy( code, "EventSetDescriptor" ) ); // NOI18N
        }

        section = bis.getSuperclassSection();
        code = normalizeText( section );
        setUseSuperClass(hasSuperClass(code));
    }

    /** "Normalizes" the JavaCode. Removes all unneeded whitespaces. Makes strings from
     * commands. 
     * @param code String containg the java source code
     * @return Normalized code as collection of string.
     */
    static List<String> normalizeText( String code ) {

        List<String> result = new  ArrayList<String>();
        StringBuilder sb = new StringBuilder( 100 );

        final int IN_TEXT = 0;
        final int IN_WHITE = 1;
        int mode = IN_WHITE;
        final int noi18n_length = NOI18N_COMMENT.length();
        boolean eo_javaid = false;
        boolean guarded = false;    //guarded beetwen ""
        boolean escape = false;    //guarded beetwen ""
        
        for ( int i = 0; code != null && i < code.length(); i++ ) {
            char ch = code.charAt( i );
            
            if( ch != '\"' )
                escape = false;
            
            switch ( mode ) {
            case IN_TEXT:
                if ( !Character.isWhitespace( ch ) ) {
                    if ( ch == ';' ) {
                        sb.append( ch );
                        // check if there is "NOI18N" comment appended
                        if (i + noi18n_length < code.length() && NOI18N_COMMENT.equals(code.substring(i + 1, i + 1 + noi18n_length))) {
                            sb.append(NOI18N_COMMENT);
                            i += noi18n_length;
                        }
                        result.add( sb.toString() );
                        sb.setLength( 0 );
                        mode = IN_WHITE;
                        eo_javaid = false;
                    }
                    else if ( ch == '\\' ){
                        escape = true;
                        sb.append( ch );
                    }
                    else if ( ch == '\"' ){
                        if( !escape )
                            guarded = !guarded;
                        escape = false;
                        sb.append( ch );
                    }
                    else    
                        sb.append( ch );
                }
                else {
                    if( guarded )
                        sb.append( ch );
                    else{
                        eo_javaid = Character.isJavaIdentifierPart ( code.charAt( i - 1 ) );
                        mode = IN_WHITE;
                    }
                }
                break;
            case IN_WHITE:
                if ( !Character.isWhitespace( ch ) ) {
                    if ( eo_javaid && Character.isJavaIdentifierStart ( ch ) )
                        sb.append( ' ' );
                    else if ( ch == '\\' ){
                        escape = true;
                        sb.append( ch );
                    }
                    else if ( ch == '\"' ) {
                        if( !escape )
                            guarded = !guarded;
                        escape = false;
                    }
                    sb.append( ch );
                    mode = IN_TEXT;                    
                }
                break;
            }
        }
        
        if (sb.length() > 0) result.add(sb.toString());
        
        return result;

    }

    static String[] getParameters( String command ) {
        String paramString;

        int beg = command.indexOf( '(' );
        int end = command.lastIndexOf( ')' );

        if ( beg != -1 && end != -1 && ( ++beg < end ) )
            paramString = command.substring( beg, end );
        else
            return new String[0];

        StringTokenizer strTok = new StringTokenizer( paramString, "," ); // NOI18N

        String[] resultStrs = new String[ strTok.countTokens() ];

        for ( int i = 0; strTok.hasMoreTokens(); i++ )
            resultStrs[i] = strTok.nextToken();

        return resultStrs;
    }

    static String getArgumentParameter( String command ) {
        int beg = command.indexOf( '(' );
        int end = command.lastIndexOf( ')' );

        if ( beg != -1 && end != -1 && ( ++beg < end ) )
            return command.substring( beg, end );
        else
            return null;
    }
    
    /** Gets the initializer */
    static String getInitializer( String command ) {

        int beg = command.lastIndexOf( '=' );
        int end = command.lastIndexOf( ';' );

        if ( beg != -1 && end != -1 && ( ++beg < end ) )
            return command.substring( beg, end ).trim();
        else
            return null;
    }

    /** test if initializer is lazy */    
    static boolean isLazy( List<String> code, String name ) {
        for (String statement : code) {
            if ( statement.indexOf( name ) != -1 ){
                if( statement.indexOf( "/*lazy " + name + "*/" ) != -1 ){  // NOI18N
                    return true;
                }
            }
        }
        return false;
    }

    static boolean hasSuperClass( List<String> code ) {
        for (String statement : code) {
            //System.out.println(statement);
            if ( statement.indexOf( "public BeanInfo[] getAdditionalBeanInfo()" ) != -1 ){  // NOI18N
                    return true;
            }
        }
        return false;
    }

    /** Removes Quotation marks */
    static String removeQuotation( String text ) {

        int beg = text.indexOf( '"' );
        int end = text.lastIndexOf( '"' );

        if ( beg != -1 && end != -1 && ( ++beg < end ) )
            return text.substring( beg, end );
        else
            return null;
    }


    /** Let's the collection of features check for it's properties in BeanInfo */
    boolean setPropertiesFromBeanInfo( List<? extends BiFeature> features, List<String> code, String name ) throws GenerateBeanException {
        for (String statement : code) {
            if ( statement.indexOf( name ) != -1 )
                
                if ( "null".equals(getInitializer( statement ))  ){ // NOI18N 
                    return true;
                }
                else
                    break;  //others f.e. null/*lazy*/
        }

        for (BiFeature bif : features) {
            bif.setBrackets(bif.getBrackets());
            bif.analyzeCustomization( code );            
        }

        return false;
    }

    /** Analyze icons properties from bean info */

    void setIconsFromBeanInfo ( List<String> code ) {
        for (String statement : code) {
            if ( statement.indexOf( ICONNAME_C16 ) != -1 ) {
                iconC16 = removeQuotation( getInitializer( statement ) );
                continue;
            }
            if ( statement.indexOf( ICONNAME_C32 ) != -1 ) {
                iconC32 = removeQuotation( getInitializer( statement ) );
                continue;
            }
            if ( statement.indexOf( ICONNAME_M16 ) != -1 ) {
                iconM16 = removeQuotation( getInitializer( statement ) );
                continue;
            }
            if ( statement.indexOf( ICONNAME_M32 ) != -1 ) {
                iconM32 = removeQuotation( getInitializer( statement ) );
                continue;
            }
        }
    }


    /** Analyze default section  */

    void setDefaultIdxFromBeanInfo( List<String> code ) {
        for (String statement : code) {
            if ( statement.indexOf( DEFAULT_PROPERTY_INDEX ) != -1 ) {
                try {
                    defaultPropertyIndex = Integer.parseInt( getInitializer( statement ) );
                }
                catch ( java.lang.NumberFormatException e ) {
                    defaultPropertyIndex = -1;
                }

                continue;
            }
            if ( statement.indexOf( DEFAULT_EVENT_INDEX ) != -1 ) {
                try {
                    defaultEventIndex = Integer.parseInt( getInitializer( statement ) );
                }
                catch ( java.lang.NumberFormatException e ) {
                    defaultEventIndex = -1;
                }

                continue;
            }

        }
    }
    
    private void appendNoi18nText(StringBuilder sb) {
        sb.append(NOI18N_COMMENT);
    }
    
    private void setModified() {
        this.isModified = true;
    }
    
    public boolean isModified() {
        return this.isModified;
    }
}
