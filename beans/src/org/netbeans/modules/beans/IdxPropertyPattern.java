/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.beans;

import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.lang.reflect.Modifier;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import org.openide.DialogDisplayer;

import org.openide.src.MethodElement;
import org.openide.src.MethodParameter;
import org.openide.src.ClassElement;
import org.openide.src.Type;
import org.openide.src.SourceException;
import org.openide.src.Identifier;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.NotifyDescriptor;


/** Class representing JavaBeans IndexedProperty.
 * @author Petr Hrebejk
 */
public class IdxPropertyPattern extends PropertyPattern {

    /** Getter method of this indexed property */
    protected MethodElement indexedGetterMethod = null;
    /** Setter method of this indexed property */
    protected MethodElement indexedSetterMethod = null;

    /** Holds the indexed type of the property resolved from methods. */
    protected Type indexedType;

    /** Creates new IndexedPropertyPattern just one of the methods indexedGetterMethod
     * and indexedSetterMethod may be null. 
     * @param patternAnalyser patternAnalyser which creates this Property.
     * @param getterMethod getterMethod may be <CODE>null</CODE>.
     * @param setterMethod setterMethod may be <CODE>null</CODE>.
     * @param indexedGetterMethod getterMethod of the property or <CODE>null</CODE>.
     * @param indexedSetterMethod setterMethod of the property or <CODE>null</CODE>.
     * @throws IntrospectionException If specified methods do not follow beans Property rules.
     */  
    public IdxPropertyPattern( PatternAnalyser patternAnalyser,
                               MethodElement getterMethod, MethodElement setterMethod,
                               MethodElement indexedGetterMethod, MethodElement indexedSetterMethod )
    throws IntrospectionException {

        super ( patternAnalyser, getterMethod, setterMethod );

        this.indexedGetterMethod = indexedGetterMethod;
        this.indexedSetterMethod = indexedSetterMethod;

        findIndexedPropertyType();
        name = findIndexedPropertyName();
    }

    /** Creates new IndexedPropertyPattern.
     * @param patternAnalyser patternAnalyser which creates this IndexedProperty.
     */
    private IdxPropertyPattern( PatternAnalyser patternAnalyser ) {
        super( patternAnalyser );
    }

    /** Creates new IdxPropertyPattern.
     * @param patternAnalyser patternAnalyser which creates this Property.
     * @param name Name of the Property.
     * @param type Type of the Property ( i.e. Array or Collection )
     * @param indexedType Indexed type of the property.
     * @throws SourceException If the Property can't be created in the source.
     * @return Newly created IdxPropertyPattern.
     */
    static IdxPropertyPattern create( PatternAnalyser patternAnalyser,
                                      String name, String type, String indexedType ) throws SourceException {

        IdxPropertyPattern ipp = new IdxPropertyPattern( patternAnalyser );

        ipp.name = name;
        ipp.type = Type.parse( type );
        ipp.indexedType = Type.parse( indexedType );

        ipp.generateGetterMethod();
        ipp.generateSetterMethod();
        ipp.generateIndexedGetterMethod();
        ipp.generateIndexedSetterMethod();

        return ipp;
    }

    /** Creates new indexed property pattern with extended options
     * @param patternAnalyser patternAnalyser which creates this Property.
     * @param name Name of the Property.
     * @param type Type of the Property.
     * @param mode {@link #READ_WRITE Mode} of the new property.
     * @param bound Is the Property bound?
     * @param constrained Is the property constrained?
     * @param withField Should be the private field for this property genareted?
     * @param withReturn Generate return statement in getter?
     * @param withSet Generate seter statement for private field in setter.
     * @param withSupport Generate PropertyChange support?
     * @param niGetter Non-indexed getter method
     * @param niWithReturn Generate return statement in non-indexed getter?
     * @param niSetter Non-indexed setter method
     * @param niWithSet Generate set field statement in non-indexed setter?
     * @throws SourceException If the Property can't be created in the source.
     * @return Newly created PropertyPattern.
     */
    static IdxPropertyPattern create( PatternAnalyser patternAnalyser,
                                      String name, String type,
                                      int mode, boolean bound, boolean constrained,
                                      boolean withField, boolean withReturn,
                                      boolean withSet, boolean withSupport,
                                      boolean niGetter, boolean niWithReturn,
                                      boolean niSetter, boolean niWithSet ) throws SourceException {

        return create(patternAnalyser, name, type, mode, bound, constrained, withField, withReturn, withSet, withSupport, niGetter, niWithReturn, niSetter, niWithSet, false, false );
    }
    /** Creates new indexed property pattern with extended options
     * @param patternAnalyser patternAnalyser which creates this Property.
     * @param name Name of the Property.
     * @param type Type of the Property.
     * @param mode {@link #READ_WRITE Mode} of the new property.
     * @param bound Is the Property bound?
     * @param constrained Is the property constrained?
     * @param withField Should be the private field for this property genareted?
     * @param withReturn Generate return statement in getter?
     * @param withSet Generate seter statement for private field in setter.
     * @param withSupport Generate PropertyChange support?
     * @param niGetter Non-indexed getter method
     * @param niWithReturn Generate return statement in non-indexed getter?
     * @param niSetter Non-indexed setter method
     * @param niWithSet Generate set field statement in non-indexed setter?
     * @param useSupport use change support without prompting
     * @param fromField signalize that all action are activatet on field
     * @throws SourceException If the Property can't be created in the source.
     * @return Newly created PropertyPattern.
     */
    static IdxPropertyPattern create( PatternAnalyser patternAnalyser,
                                      String name, String type,
                                      int mode, boolean bound, boolean constrained,
                                      boolean withField, boolean withReturn,
                                      boolean withSet, boolean withSupport,
                                      boolean niGetter, boolean niWithReturn,
                                      boolean niSetter, boolean niWithSet,
                                      boolean useSupport, boolean fromField ) throws SourceException {

        IdxPropertyPattern ipp = new IdxPropertyPattern( patternAnalyser );

        ipp.name = name;
        ipp.type = null;
        ipp.indexedType = Type.parse( type );

        // Set the non-indexed type when needed
        if ( withField || withSupport || niGetter || niSetter ) {
            ipp.type = Type.createArray( ipp.indexedType );
        }

        // Generate field
        if ( ( withField || withSupport ) && !fromField ) {
            if ( ipp.type != null )
            try {
                ipp.generateField( true );
            } catch (SourceException e) {
                DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message(
                        PatternNode.getString("MSG_Cannot_Create_Field"),
                        NotifyDescriptor.WARNING_MESSAGE));
            }
        }


        // Ensure property change support field and methods exist
        String supportName = null;
        String vetoSupportName = null;

        if ( withSupport ) {

            boolean boundSupport = bound;
            boolean constrainedSupport = constrained;

            
            if( !useSupport ){
                if( boundSupport )
                    if( ( supportName = EventSetInheritanceAnalyser.showInheritanceEventDialog(EventSetInheritanceAnalyser.detectPropertyChangeSupport(  ipp.getDeclaringClass()), "PropertyChangeSupport")) != null ) // NOI18N
                        boundSupport = false;
                if( constrainedSupport )
                    if( ( vetoSupportName = EventSetInheritanceAnalyser.showInheritanceEventDialog(EventSetInheritanceAnalyser.detectVetoableChangeSupport(  ipp.getDeclaringClass()), "VetoableChangeSupport")) != null ) // NOI18N
                        constrainedSupport = false;
            }
            else {
                if( boundSupport )
                    if( ( supportName = EventSetInheritanceAnalyser.getInheritanceEventSupportName(EventSetInheritanceAnalyser.detectPropertyChangeSupport(  ipp.getDeclaringClass()), "PropertyChangeSupport")) != null ) // NOI18N
                        boundSupport = false;
                if( constrainedSupport )
                    if( ( vetoSupportName = EventSetInheritanceAnalyser.getInheritanceEventSupportName(EventSetInheritanceAnalyser.detectVetoableChangeSupport(  ipp.getDeclaringClass()), "VetoableChangeSupport")) != null ) // NOI18N
                        constrainedSupport = false;
            }
            
            if ( boundSupport )
                supportName = BeanPatternGenerator.supportField( ipp.getDeclaringClass() );
            if ( constrainedSupport )
                vetoSupportName = BeanPatternGenerator.vetoSupportField( ipp.getDeclaringClass() );

            if ( boundSupport )
                BeanPatternGenerator.supportListenerMethods( ipp.getDeclaringClass(), supportName );
            if ( constrainedSupport )
                BeanPatternGenerator.vetoSupportListenerMethods( ipp.getDeclaringClass(), vetoSupportName );
        }

        if ( mode == READ_WRITE || mode == READ_ONLY ) {
            if( (fromField && withReturn) || !fromField )
                ipp.generateIndexedGetterMethod( BeanPatternGenerator.idxPropertyGetterBody( name, withReturn ), true );
            if ( ipp.type != null && niGetter )
                ipp.generateGetterMethod( BeanPatternGenerator.propertyGetterBody( name, niWithReturn), true );
        }
        if ( mode == READ_WRITE || mode == WRITE_ONLY ) {
            /*
            ipp.generateIndexedSetterMethod( BeanPatternGenerator.idxPropertySetterBody( name, ipp.getType(),
                bound, constrained, withSet, withSupport, supportName, vetoSupportName ), constrained, true );
            */
            if( (fromField && withSet) || !fromField )
                ipp.generateIndexedSetterMethod( BeanPatternGenerator.idxPropertySetterBody( name, ipp.getIndexedType(),
                                                 bound, constrained, withSet, withSupport, supportName, vetoSupportName ), constrained, true );

            if ( ipp.type != null && niSetter )
                ipp.generateSetterMethod( BeanPatternGenerator.propertySetterBody( name, ipp.getType(),
                                          bound, constrained, niWithSet, withSupport, supportName, vetoSupportName ), constrained, true );
        }
        return ipp;
    }



    /** Gets the name of IdxPropertyPattern
     * @return Name of the Indexed Property
     */
    public Type getIndexedType() {
        return indexedType;
    }

    /** Sets the name of IdxPropertyPattern
     * @param name New name of the property.
     * @throws SourceException If the modification of source code is impossible.
     */
    public void setName(String name) throws  SourceException {
        String oldName = this.name;
        super.setName( name );

        name = capitalizeFirstLetter( name );

        if ( indexedGetterMethod != null ) {
            Identifier idxGetterMethodID = Identifier.create(( indexedGetterMethod.getName().getName().startsWith("get") ? // NOI18N
                                           "get" : "is" ) + name ); // NOI18N
            indexedGetterMethod.setName( idxGetterMethodID );
            String oldGetterComment = MessageFormat.format( PatternNode.getString( "COMMENT_IdxPropertyGetter" ),
                                           new Object[] { oldName } );
            String newGetterComment = MessageFormat.format( PatternNode.getString( "COMMENT_IdxPropertyGetter" ),
                                           new Object[] { getName() } );
            if (!indexedGetterMethod.getJavaDoc().isEmpty() &&
                oldGetterComment.trim().equals(indexedGetterMethod.getJavaDoc().getRawText().trim())) {
                indexedGetterMethod.getJavaDoc().setRawText( newGetterComment );
            }
        }
        if ( indexedSetterMethod != null ) {
            Identifier idxSetterMethodID = Identifier.create( "set" + name ); // NOI18N
            indexedSetterMethod.setName( idxSetterMethodID );
            String oldSetterComment = MessageFormat.format( PatternNode.getString( "COMMENT_IdxPropertySetter" ),
                                           new Object[] { oldName, oldName } );
            String newSetterComment = MessageFormat.format( PatternNode.getString( "COMMENT_IdxPropertySetter" ),
                                           new Object[] { getName(), getName() } );
            if (!indexedSetterMethod.getJavaDoc().isEmpty() &&
                oldSetterComment.trim().equals(indexedSetterMethod.getJavaDoc().getRawText().trim())) {
                indexedSetterMethod.getJavaDoc().setRawText( newSetterComment );
            }
        }
        
        // change body and javadoc of idx accessors if the field has been changed
        if ( estimatedField != null && estimatedField.getName().getName().equals(getName())) {
            int mode = getMode();
            if ( mode == READ_WRITE || mode == READ_ONLY ) {
                String existingGetterBody = indexedGetterMethod.getBody().trim();
                String oldGetterBody1 = BeanPatternGenerator.idxPropertyGetterBody( oldName, true, true ).trim();
                String oldGetterBody2 = BeanPatternGenerator.idxPropertyGetterBody( oldName, true, false ).trim();
                if (existingGetterBody.equals(oldGetterBody1)) {
                    indexedGetterMethod.setBody(BeanPatternGenerator.idxPropertyGetterBody( getName(), true, true));
                } else if (existingGetterBody.equals(oldGetterBody2)) {
                    indexedGetterMethod.setBody(BeanPatternGenerator.idxPropertyGetterBody( getName(), true, false));
                }
            }
            if ( mode == READ_WRITE || mode == WRITE_ONLY ) {
                String existingSetterBody = indexedSetterMethod.getBody().trim();
                String oldSetterBody = BeanPatternGenerator.idxPropertySetterBody (oldName, this.type, false, false, true, false, null, null).trim();
                if (existingSetterBody.equals(oldSetterBody)) {
                    indexedSetterMethod.setBody(BeanPatternGenerator.idxPropertySetterBody (getName(), getType(), false, false, true, false, null, null));

                    if ( indexedSetterMethod != null ) {
                        MethodParameter params[] = indexedSetterMethod.getParameters();
                        params[1].setName(Introspector.decapitalize( name ));
                        indexedSetterMethod.setParameters(params);
                    }
                }
            }
        }
    }




    /** Returns the indexed getter method
     * @return Getter method of the property
     */
    public MethodElement getIndexedGetterMethod() {
        return indexedGetterMethod;
    }

    /** Returns the indexed setter method
     * @return Getter method of the property
     */
    public MethodElement getIndexedSetterMethod() {
        return indexedSetterMethod;
    }

    /** Sets the non-indexed type of IdxPropertyPattern
     * @param type New non-indexed type of the indexed property
     * @throws SourceException If the modification of source code is impossible
     */
    public void setType(Type type) throws SourceException {

        if ( this.type != null && this.type.compareTo( type, true ) )
            return;

        // Remember the old type & old indexed type
        Type oldIndexedType = this.indexedType;
        Type oldType = this.type;

        if ( oldType == null ) {
            this.type = type;
            oldType = type;
            int mode = getMode();
            if ( mode == READ_WRITE || mode == READ_ONLY )
                generateGetterMethod();
            if ( mode == READ_WRITE || mode == WRITE_ONLY )
                generateSetterMethod();
        }
        else
            // Change the type
            super.setType( type );

        // Test if the idexedType is the type of array and change it if so
        if ( type.isArray() && oldType.isArray() && oldType.getElementType().compareTo( oldIndexedType, false ) ) {
            Type newType = type.getElementType();

            if (indexedGetterMethod != null ) {
                indexedGetterMethod.setReturn( newType );
            }
            if (indexedSetterMethod != null ) {
                MethodParameter[] params = indexedSetterMethod.getParameters();
                if ( params.length > 1 ) {
                    params[1].setType( newType );
                    indexedSetterMethod.setParameters( params );
                }
            }

            // Set the type  to new type
            setIndexedType( newType );
        }
    }

    /** Sets the indexed type of IdxPropertyPattern
     * @param type New indexed type of the indexed property
     * @throws SourceException If the modification of source code is impossible
     */
    public void setIndexedType(Type type) throws SourceException {

        if ( this.indexedType.compareTo( type, true ) )
            return;

        // Remember the old type & old indexed type
        Type oldIndexedType = this.indexedType;
        Type oldType = this.type;

        // Change the indexed type
        if (indexedGetterMethod != null ) {
            indexedGetterMethod.setReturn( type );
        }
        if (indexedSetterMethod != null ) {
            MethodParameter[] params = indexedSetterMethod.getParameters();
            if ( params.length > 1 ) {
                params[1].setType( type );
                indexedSetterMethod.setParameters( params );

                String body = indexedSetterMethod.getBody();
                
                //test if body contains change support
                if( body != null && ( body.indexOf(PropertyPattern.PROPERTY_CHANGE) != -1 || body.indexOf(PropertyPattern.VETOABLE_CHANGE) != -1 ) ) {
                    String mssg = MessageFormat.format( PatternNode.getString( "FMT_ChangeMethodBody" ),
                                                        new Object[] { setterMethod.getName().getName() } );
                    NotifyDescriptor nd = new NotifyDescriptor.Confirmation ( mssg, NotifyDescriptor.YES_NO_OPTION );
                    DialogDisplayer.getDefault().notify( nd );
                    if( nd.getValue().equals( NotifyDescriptor.YES_OPTION ) ) {
                        String newBody = regeneratePropertySupport( indexedSetterMethod.getBody(), null, params[1].getName(), type, oldType );
                        if( newBody != null )
                            indexedSetterMethod.setBody(newBody);
                    }
                }
            }
        }

        // Test if the old type of getter and seter was an array of indexedType
        // if so change the type of that array.
        if ( oldType != null && oldType.isArray() && oldType.getElementType().compareTo( oldIndexedType, false ) ) {
            Type newArrayType = Type.createArray( type );
            super.setType( newArrayType );
        }

        indexedType = type;
    }

    /**
     * @param methodBody old method body
     * @param changeType  .. propertyChange, vetoableChange or null if need to change only support field 
     * @param name of property
     * @param type new type of property value
     * @param oldType old type of property value
     * @return null if no change is possible or new body if it is
     */
    private String regenerateIdxPropertySupport( String methodBody, String name, org.openide.src.Type type, org.openide.src.Type oldType ){
        if( methodBody == null )
            return null;
        
        int first = -1;
        String propertyStyle = PropertyActionSettings.getDefault().getPropStyle();
        
        //will search for line containing property support or field
        String oldVarLine = oldType.toString() + " old" + Pattern.capitalizeFirstLetter( name ) + " = " + propertyStyle + name; // NOI18N
        if( (first = methodBody.indexOf( oldVarLine )) == -1 )
            return null;

        if( first == -1 )
            return null;
        
        //find end of statement
        int last = methodBody.indexOf(';', first);
        if( first >= last )
            return null;
        
        StringBuffer newBody = new StringBuffer(100);
        newBody.append( type.toString() );
        newBody.append( " old" ).append( Pattern.capitalizeFirstLetter( name ) ); // NOI18N
        newBody.append( " = " ).append( propertyStyle ).append( name ); // NOI18N            

        StringBuffer sb = new StringBuffer(methodBody);
        sb.delete(first, last);
        sb.insert(first, newBody);
        return sb.toString();        
    }

    /** Returns the mode of the property {@link PropertPattern#READ_WRITE READ_WRITE},
     * {@link PropertPattern#READ_ONLY READ_ONLY} or {@link PropertPattern#WRITE_ONLY WRITE_ONLY}
     * @return Mode of the property
     */
    public int getMode() {
        if ( indexedSetterMethod != null && indexedGetterMethod != null )
            return READ_WRITE;
        else if ( indexedGetterMethod != null && indexedSetterMethod == null )
            return READ_ONLY;
        else if ( indexedSetterMethod != null && indexedGetterMethod == null )
            return WRITE_ONLY;
        else
            return super.getMode();
    }

    /** Sets the property to be writable
     * @param mode New Mode {@link PropertPattern#READ_WRITE READ_WRITE}, 
     *   {@link PropertPattern#READ_ONLY READ_ONLY} or {@link PropertPattern#WRITE_ONLY WRITE_ONLY}
     * @throws SourceException If the modification of source code is impossible.
     */
    public void setMode( int mode ) throws SourceException {
        if ( getMode() == mode )
            return;

        switch ( mode ) {
        case READ_WRITE:
            if ( getterMethod == null )
                generateGetterMethod();
            if ( setterMethod == null )
                generateSetterMethod();
            if ( indexedGetterMethod == null )
                generateIndexedGetterMethod();
            if ( indexedSetterMethod == null )
                generateIndexedSetterMethod();
            break;
        case READ_ONLY:
            if ( getterMethod == null )
                generateGetterMethod();
            if ( indexedGetterMethod == null )
                generateIndexedGetterMethod();
            
            if (setterMethod != null || indexedSetterMethod != null) {
                NotifyDescriptor nd = new NotifyDescriptor.Confirmation ( PatternNode.getString("MSG_Delete_Setters") + PatternNode.getString("MSG_Continue_Confirm"), NotifyDescriptor.YES_NO_OPTION );
                DialogDisplayer.getDefault().notify( nd );
                if( nd.getValue().equals( NotifyDescriptor.YES_OPTION ) ) {
                    if ( setterMethod != null )
                        deleteSetterMethod();
                    if ( indexedSetterMethod != null )
                        deleteIndexedSetterMethod();
                }
            }
            break;
        case WRITE_ONLY:
            if ( setterMethod == null )
                generateSetterMethod();
            if ( indexedSetterMethod == null )
                generateIndexedSetterMethod();
            if (getterMethod != null || indexedGetterMethod != null) {
                NotifyDescriptor nd = new NotifyDescriptor.Confirmation ( PatternNode.getString("MSG_Delete_Getters") + PatternNode.getString("MSG_Continue_Confirm"), NotifyDescriptor.YES_NO_OPTION );
                DialogDisplayer.getDefault().notify( nd );
                if( nd.getValue().equals( NotifyDescriptor.YES_OPTION ) ) {
                    if ( getterMethod != null )
                        deleteGetterMethod();
                    if ( indexedGetterMethod != null )
                        deleteIndexedGetterMethod();
                }
            }
            break;
        }

    }

    /** Gets the cookie of the first available inderxed method
     * @param cookieType Class of the Cookie
     * @return Cookie of indexedGetter or indexedSetter MethodElement
     */
    public Node.Cookie getCookie( Class cookieType ) {
        if ( indexedGetterMethod != null )
            return indexedGetterMethod.getCookie( cookieType );

        if ( indexedSetterMethod != null )
            return indexedSetterMethod.getCookie( cookieType );

        return super.getCookie( cookieType );
    }

    /** Destroys methods associated methods with the pattern in source
     * @throws SourceException If modification of source is impossible
     */
    public void destroy() throws SourceException {
        deleteIndexedSetterMethod();
        deleteIndexedGetterMethod();
        super.destroy();
    }

    // Utitlity methods -------------------------------------------------------------------

    /** Package private constructor. Merges two property descriptors. Where they
     * conflict, gives the second argument (y) priority over the first argumnet (x).
     * @param x The first (lower priority) PropertyPattern.
     * @param y The second (higher priority) PropertyPattern.
     */
    IdxPropertyPattern( PropertyPattern x, PropertyPattern y ) {
        super( x, y );
        if ( x instanceof IdxPropertyPattern ) {
            IdxPropertyPattern ix = (IdxPropertyPattern)x;
            indexedGetterMethod = ix.indexedGetterMethod;
            indexedSetterMethod = ix.indexedSetterMethod;
            indexedType = ix.indexedType;
        }
        if ( y instanceof IdxPropertyPattern ) {
            IdxPropertyPattern iy = (IdxPropertyPattern)y;
            if ( iy.indexedGetterMethod != null )
                indexedGetterMethod = iy.indexedGetterMethod;
            if ( iy.indexedSetterMethod != null )
                indexedSetterMethod = iy.indexedSetterMethod;
            indexedType = iy.indexedType;
        }
        name  = findIndexedPropertyName();
    }

    /** Resolves the indexed type of the property from type of getter and setter.
     * Chcecks for conformance to Beans design patterns.
     * @throws IntrospectionException if the property doesnt folow the design patterns
     */
    private void findIndexedPropertyType() throws IntrospectionException {

        indexedType = null;

        if ( indexedGetterMethod != null ) {
            MethodParameter[] params = indexedGetterMethod.getParameters();
            if ( params.length != 1 ) {
                throw new IntrospectionException( "bad indexed read method arg count" ); // NOI18N
            }
            if ( !params[0].getType().compareTo( Type.INT, false ) ) {
                throw new IntrospectionException( "not int index to indexed read method" ); // NOI18N
            }
            indexedType = indexedGetterMethod.getReturn();
            if ( indexedType.compareTo( Type.VOID, false ) ) {
                throw new IntrospectionException( "indexed read method return void" ); // NOI18N
            }
        }

        if (indexedSetterMethod != null ) {
            MethodParameter params[] = indexedSetterMethod.getParameters();
            if ( params.length != 2 ) {
                throw new IntrospectionException( "bad indexed write method arg count" ); // NOI18N
            }
            if ( !params[0].getType().compareTo( Type.INT, false ) ) {
                throw new IntrospectionException( "non int index to indexed write method" ); // NOI18N
            }
            if (indexedType != null && !indexedType.compareTo( params[1].getType(), false ) ) {
                throw new IntrospectionException(
                    "type mismatch between indexed read and write methods" ); // NOI18N
            }
            indexedType = params[1].getType();
        }

        //type = indexedType;

        Type propType = getType();
        if ( propType != null &&  (!propType.isArray() || !propType.getElementType().compareTo(indexedType, false))) {
            throw new IntrospectionException(
                "type mismatch between property type and indexed type" ); // NOI18N
        }
    }

    /** Based on names of indexedGetter and indexedSetter resolves the name
     * of the indexed property.
     * @return Name of the indexed property
     */ 
    String findIndexedPropertyName() {

        String superName = findPropertyName();

        if ( superName == null ) {
            String methodName = null;

            if ( indexedGetterMethod != null )
                methodName = indexedGetterMethod.getName().getName();
            else if ( indexedSetterMethod != null )
                methodName = indexedSetterMethod.getName().getName();
            else
                throw new InternalError( "Indexed property with all methods == null" ); // NOI18N

            return methodName.startsWith( "is" ) ? // NOI18N
                   Introspector.decapitalize( methodName.substring(2) ) :
                   Introspector.decapitalize( methodName.substring(3) );
        }
        else
            return superName;
    }

    // METHODS FOR GENERATING AND DELETING METHODS AND FIELDS--------------------


    /** Generates non-indexed getter method without body and without Javadoc comment.
     * @throws SourceException If modification of source code is impossible.
     */
    void generateGetterMethod() throws SourceException {
        if ( type != null )
            super.generateGetterMethod();
    }

    /** Generates non-indexed setter method without body and without Javadoc comment.
     * @throws SourceException If modification of source code is impossible.
     */
    void generateSetterMethod() throws SourceException {
        if ( type != null )
            super.generateSetterMethod();
    }

    /** Generates indexed getter method without body and without Javadoc comment.
     * @throws SourceException If modification of source code is impossible.
     */
    void generateIndexedGetterMethod() throws SourceException {
        generateIndexedGetterMethod( null, false );
    }

    /** Generates indexed getter method with body and optionaly with Javadoc comment.
     * @param body Body of the method
     * @param javadoc Generate Javadoc comment?
     * @throws SourceException If modification of source code is impossible.
     */
    void generateIndexedGetterMethod( String body, boolean javadoc ) throws SourceException {

        ClassElement declaringClass = getDeclaringClass();
        MethodElement newGetter = new MethodElement();
        MethodParameter[] newParameters = { new MethodParameter( "index", Type.INT, false ) }; // NOI18N

        newGetter.setName( Identifier.create( "get" + capitalizeFirstLetter( getName() ) ) ); // NOI18N
        newGetter.setReturn( indexedType );
        newGetter.setModifiers( Modifier.PUBLIC );
        newGetter.setParameters( newParameters );
        if ( declaringClass.isInterface() ) {
            newGetter.setBody( null );
        }
        else if ( body != null )
            newGetter.setBody( body );

        if ( javadoc ) {
            String comment = MessageFormat.format( PatternNode.getString( "COMMENT_IdxPropertyGetter" ),
                                                   new Object[] { getName() } );
            newGetter.getJavaDoc().setRawText( comment );
        }

        //System.out.println ("Generating getter" ); // NOI18N

        if ( declaringClass == null )
            throw new SourceException();
        else {
            //System.out.println ( "Adding getter method" ); // NOI18N
            declaringClass.addMethod( newGetter );
            indexedGetterMethod = declaringClass.getMethod( newGetter.getName(), getParameterTypes( newGetter ) );
        }
    }

    /** Generates indexed setter method without body and without Javadoc comment.
     * @throws SourceException If modification of source code is impossible.
     */
    void generateIndexedSetterMethod() throws SourceException {
        generateIndexedSetterMethod(null, false, false );
    }

    /** Generates indexed setter method with body and optionaly with Javadoc comment.
     * @param body Body of the method
     * @param javadoc Generate Javadoc comment?
     * @param constrained Is the property constrained?
     * @throws SourceException If modification of source code is impossible.
     */
    void generateIndexedSetterMethod( String body, boolean constrained, boolean javadoc ) throws SourceException {

        ClassElement declaringClass = getDeclaringClass();
        MethodElement newSetter = new MethodElement();
        MethodParameter[] newParameters = { new MethodParameter( "index", Type.INT, false ), // NOI18N
                                            new MethodParameter( name, indexedType, false ) };

        newSetter.setName( Identifier.create( "set" + capitalizeFirstLetter( getName() ) ) ); // NOI18N
        newSetter.setReturn( Type.VOID );
        newSetter.setModifiers( Modifier.PUBLIC );
        newSetter.setParameters( newParameters );
        if ( constrained )
            newSetter.setExceptions( ( new Identifier[] { Identifier.create( "java.beans.PropertyVetoException" ) } ) ); // NOI18N
        if ( declaringClass.isInterface() ) {
            newSetter.setBody( null );
        }
        else if ( body != null ) {
            newSetter.setBody( body );
        }

        if ( javadoc ) {
            String comment = MessageFormat.format( PatternNode.getString( "COMMENT_IdxPropertySetter" ),
                                                   new Object[] { getName(), name } );
            if ( constrained )
                comment = comment + PatternNode.getString( "COMMENT_Tag_ThrowsPropertyVeto" );
            newSetter.getJavaDoc().setRawText( comment );
        }

        if ( declaringClass == null )
            throw new SourceException();
        else {
            declaringClass.addMethod( newSetter );
            indexedSetterMethod = declaringClass.getMethod( newSetter.getName(), getParameterTypes( newSetter ) );
        }
    }


    /** Deletes the indexed getter method in source
     * @throws SourceException If modification of source code is impossible.
     */
    void deleteIndexedGetterMethod() throws SourceException {

        if ( indexedGetterMethod == null )
            return;

        ClassElement declaringClass = getDeclaringClass();

        if ( declaringClass == null ) {
            throw new SourceException();
        }
        else {
            declaringClass.removeMethod( indexedGetterMethod );
            indexedGetterMethod = null;
        }
    }

    /** Deletes the indexed setter method in source
     * @throws SourceException If modification of source code is impossible.
     */
    void deleteIndexedSetterMethod() throws SourceException {

        if ( indexedSetterMethod == null )
            return;

        ClassElement declaringClass = getDeclaringClass();

        if ( declaringClass == null ) {
            throw new SourceException();
        }
        else {
            declaringClass.removeMethod( indexedSetterMethod );
            indexedSetterMethod = null;
        }


    }

    // Property change support ----------------------------------

    /** Sets the properties to values of other indexed property pattern. If the
     * properties change fires PropertyChange event.
     * @param src Source IdxPropertyPattern it's properties will be copied.
     */
    void copyProperties( IdxPropertyPattern src ) {

        boolean changed = !src.getIndexedType().equals( getIndexedType() ) ||
                          !( src.getType() == null ? getType() == null : src.getType().equals( getType() ) ) ||
                          !src.getName().equals( getName() ) ||
                          !(src.getMode() == getMode()) ||
                          !(src.getEstimatedField() == null ? estimatedField == null : src.getEstimatedField().equals( estimatedField ) );

        if ( src.getIndexedGetterMethod() != indexedGetterMethod )
            indexedGetterMethod = src.getIndexedGetterMethod();
        if ( src.getIndexedSetterMethod() != indexedSetterMethod )
            indexedSetterMethod = src.getIndexedSetterMethod();

        if ( src.getGetterMethod() != getterMethod ) {
            changed = true;
            getterMethod = src.getGetterMethod();
        }
        if ( src.getSetterMethod() != setterMethod ) {
            changed = true;
            setterMethod = src.getSetterMethod();
        }
        if ( src.getEstimatedField() != estimatedField )
            estimatedField = src.getEstimatedField();

        if ( changed ) {
            try {
                type = findPropertyType();
                findIndexedPropertyType();
            }
            catch ( java.beans.IntrospectionException e ) {
                // User's error
            }
            name = findIndexedPropertyName();

            firePropertyChange( new java.beans.PropertyChangeEvent( this, null, null, null ) );
        }
    }

}
