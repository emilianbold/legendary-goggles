/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.spi.project.support.ant.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.ButtonModel;
import javax.swing.JToggleButton;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.ErrorManager;

/** Serves as utility class for storing Swing models into project
 * properties. Usefull for creating project customizers. <CODE>StoreGroup</CODE>
 * is capable of doing two things: First create the representation of the project properties which
 * can be used in the GUI. Second at some time convert the objects back to the ANT properties form and
 * store them into the project properties.<br>
 * <b>For creating the object representation.</b>
 *     <ol>
 *         <li>Create new instance of StoreGroup for each group of properties you want to store later
 *              e.g. project and private. Sometimes it might be useful to create temporary source group
 *              which will only be used for creating the models without being used for storing. E.g.
 *              for properties which need special handling.</li>
 *          <li>Call the factory methods e.g. {@link #createToggleButtonModel}, {@link #createStringDocument}, etc. which
 *              will create the swing models for you.</li>
 *          <li>Use the models in your Swing controls by calling <CODE>setModel()</CODE> or <CODE>setDocument()</CODE></li>
 *      </ol>                                         
 * <b>For storing the models back to the proprties of project.</b>
 *       <ol>
 *          <li>Get the EditableProperties you want to store the model in e.g. private or project 
 *              properties</li>
 *          <li>Call the store method on given <CODE>SourceGroup<CODE> with the {@link EditableProperties} as parameter</li>
 *          <li>Manually store models which need some special handling.</li>
 *       </ol>
 *
 * @author Petr Hrebejk
 */
public class StoreGroup {

    /** The object array serves as holder for various infos about models
     * first is allways the model. The rest depends on the model type
     * 1) Buttion model kind, inverted
     * 2) String model (not used)
     */
    private Map /*<String,Object[]|Document>*/ models;

    private static final Integer BOOLEAN_KIND_TF = new Integer( 0 );
    private static final Integer BOOLEAN_KIND_YN = new Integer( 1 );
    private static final Integer BOOLEAN_KIND_ED = new Integer( 2 );

    public StoreGroup() {
        models = new HashMap();
    }

    // Public methods ------------------------------------------------------

    /** Stores all models created in the StoreGroup into given
     * EditableProperties.
     * @param editableProperties The properties where to store the
     *        values.
     */
    public void store( EditableProperties editableProperties ) {

        for( Iterator it = models.keySet().iterator(); it.hasNext(); ) {
            String key = (String)it.next();
            Object[] params = (Object[])models.get( key );

            if ( params[0] instanceof ButtonModel ) {
                ButtonModel model = (ButtonModel)params[0];
                boolean value = model.isSelected();
                if ( params[2] == Boolean.TRUE ) {
                    value = !value;
                }
                editableProperties.setProperty( key, encodeBoolean( value, (Integer)params[1] ) );
            }
            else if ( params[0] instanceof Document ) {
                Document doc = (Document)params[0];
                String txt;
                try {
                    txt = doc.getText(0, doc.getLength());
                } catch (BadLocationException e) {
                    txt = ""; // NOI18N
                }                    
                editableProperties.setProperty( key, txt );
            }

        }

    }

    /** Creates toogle button model representing a boolean in the StoreGroup. <BR>
     * In case the value is one of "true", "yes" "on" the button model 
     * will be "selcted". If the property does not exist or is set
     * to some other value the result of isPressed will be false.<BR>
     * Call to the store() method stores the model in appropriate form
     * e.g "true/false", "yes/no", "on/off".<BR>
     * Method will throw <CODE>IllegalArgumentException</CODE> if you try to get more
     * than one model for one property.
     * @param evaluator The PropertyEvaluator to be used to evaluate given 
     *        property
     * @param propertyName Name of the ANT property
     * @return ButtonModel representing the value
     */
    public final JToggleButton.ToggleButtonModel createToggleButtonModel( PropertyEvaluator evaluator, String propertyName ) {
        return createBooleanButtonModel( evaluator, propertyName, false );
    }

    /** Creates toggle button model representing a boolean in the StoreGroup. <BR>
     * In case the value is one of "true", "yes" "on" the button model 
     * will NOT be "selcted". If the property does not exist or is set
     * to some other value the result of isPressed will be true.<BR>
     * Call to the store() method stores the model in appropriate form
     * e.g "true/false", "yes/no", "on/off".<BR>
     * Method will throw <CODE>IllegalArgumentException</CODE> if you try to get more
     * than one model for one property.
     * @param evaluator The PropertyEvaluator to be used to evaluate given 
     *        property
     * @param propertyName Name of the ANT property
     * @return ButtonModel representing the value
     */
    public final JToggleButton.ToggleButtonModel createInverseToggleButtonModel( PropertyEvaluator evaluator, String propertyName ) {
        return createBooleanButtonModel( evaluator, propertyName, true );
    }

    /** Creates Document containing the string value of given property. 
     * If the property does not extsts or the value of it is null the
     * resulting document will be empty.<BR>
     * Method will throw <CODE>IllegalArgumentException</CODE> if you try to get more
     * than one model for one property.
     * @param evaluator The PropertyEvaluator to be used to evaluate given 
     *        property
     * @param propertyName Name of the ANT property
     * @return ButtonModel representing the value
     */
    public final Document createStringDocument( PropertyEvaluator evaluator, String propertyName ) {

        checkModelDoesNotExist( propertyName );
        
        String value = evaluator.getProperty( propertyName );
        if ( value == null ) {
            value = ""; // NOI18N
        }

        try {
            Document d = new PlainDocument();
            d.remove(0, d.getLength());
            d.insertString(0, value, null);
            models.put( propertyName, new Object[] { d } );
            return d;
        }
        catch ( BadLocationException e ) {
            assert false : "Bad location exception from new document."; // NOI18N
            return new PlainDocument();
        }
    }

    // Private methods -----------------------------------------------------

    private void checkModelDoesNotExist( String propertyName ) {
        if ( models.get( propertyName ) != null ) {
            throw new IllegalArgumentException( "Model for property " + propertyName + "already exists." );
        }
    }
    
    private final JToggleButton.ToggleButtonModel createBooleanButtonModel( PropertyEvaluator evaluator, String propName, boolean invert ) {

        checkModelDoesNotExist( propName );
        
        String value = evaluator.getProperty( propName );

        boolean isSelected = false;

        Integer kind = BOOLEAN_KIND_TF;

        if ( value != null ) {
           String lowercaseValue = value.toLowerCase();

           if ( lowercaseValue.equals( "yes" ) || lowercaseValue.equals( "no" ) ) { // NOI18N
               kind = BOOLEAN_KIND_YN;
           }
           else if ( lowercaseValue.equals( "on" ) || lowercaseValue.equals( "off" ) ) { // NOI18N
               kind = BOOLEAN_KIND_ED;
           }

           if ( lowercaseValue.equals( "true") || // NOI18N
                lowercaseValue.equals( "yes") || // NOI18N
                lowercaseValue.equals( "on") ) {// NOI18N
               isSelected = true;                   
           } 
        }

        JToggleButton.ToggleButtonModel bm = new JToggleButton.ToggleButtonModel();
        bm.setSelected( invert ? !isSelected : isSelected );
        models.put( propName, new Object[] { bm, kind, Boolean.valueOf( invert ) } );
        return bm;
    }

    private static String encodeBoolean( boolean value, Integer kind ) {

        if ( kind == BOOLEAN_KIND_ED ) {
            return value ? "on" : "off"; // NOI18N
        }
        else if ( kind == BOOLEAN_KIND_YN ) { // NOI18N
            return value ? "yes" : "no";
        }
        else {
            return value ? "true" : "false"; // NOI18N
        }
    }

}
       
