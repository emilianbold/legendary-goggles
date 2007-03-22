/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.cnd.modelimpl.csm;

import antlr.collections.AST;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.netbeans.modules.cnd.api.model.CsmClass;
import org.netbeans.modules.cnd.api.model.CsmDeclaration;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmIdentifiable;
import org.netbeans.modules.cnd.api.model.CsmNamespace;
import org.netbeans.modules.cnd.api.model.CsmObject;
import org.netbeans.modules.cnd.api.model.CsmScope;
import org.netbeans.modules.cnd.api.model.CsmScopeElement;
import org.netbeans.modules.cnd.api.model.CsmType;
import org.netbeans.modules.cnd.api.model.CsmTypedef;
import org.netbeans.modules.cnd.api.model.CsmUID;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.apt.utils.TextCache;
import org.netbeans.modules.cnd.modelimpl.csm.core.FileImpl;
import org.netbeans.modules.cnd.modelimpl.parser.generated.CPPTokenTypes;
import org.netbeans.modules.cnd.modelimpl.csm.core.Disposable;
import org.netbeans.modules.cnd.modelimpl.csm.core.OffsetableDeclarationBase;
import org.netbeans.modules.cnd.modelimpl.debug.TraceFlags;
import org.netbeans.modules.cnd.modelimpl.repository.PersistentUtils;
import org.netbeans.modules.cnd.modelimpl.uid.UIDCsmConverter;
import org.netbeans.modules.cnd.modelimpl.uid.UIDObjectFactory;

/**
 * Implements CsmTypedef
 * @author vk155633
 */
public class TypedefImpl extends OffsetableDeclarationBase<CsmTypedef>  implements CsmTypedef, Disposable, CsmScopeElement {
    
    private final String name;
    private final CsmType type;
    
    // only one of containerRef/containerUID must be used (based on USE_REPOSITORY)
    private final CsmIdentifiable containerRef;
    private final CsmUID<CsmIdentifiable> containerUID;
            
    public TypedefImpl(AST ast, CsmFile file, CsmObject container, CsmType type, String name) {
        super(ast, file);
        if (TraceFlags.USE_REPOSITORY && TraceFlags.USE_UID_TO_CONTAINER) {
            this.containerUID = UIDCsmConverter.identifiableToUID((CsmIdentifiable)container);
            assert (containerUID != null || container == null);
            this.containerRef = null;
        } else {
            this.containerRef = (CsmIdentifiable)container;
            this.containerUID = null;
        }        
        if (type == null) {
            this.type = createType(ast);
        } else {
            this.type = type;
        }
        this.name = name;
    }
    
//    Moved to OffsetableDeclarationBase
//    public String getUniqueName() {
//        return getQualifiedName();
//    }
    
    public CsmScope getScope() {
        // TODO: ???
        //return getContainingFile();
        CsmObject container = _getContainer();
        if( container instanceof CsmNamespace ) {
            return (CsmNamespace) container;
        }
        else if( container instanceof CsmClass ) {
            return (CsmClass)container;
        }
        else {
            return getContainingFile();
        }
    }

    public void dispose() {
        CsmScope scope = getScope();
        if( scope instanceof MutableDeclarationsContainer ) {
            ((MutableDeclarationsContainer) scope).removeDeclaration(this);
        }
        FileImpl file = (FileImpl) getContainingFile();
        file.getProjectImpl().unregisterDeclaration(this);
    }

    public String getQualifiedName() {
        CsmObject container = _getContainer();
        if( CsmKindUtilities.isClass(container) ) {
	    return ((CsmClass) container).getQualifiedName() + "::" + getQualifiedNamePostfix(); // NOI18N
	}
	else if( CsmKindUtilities.isNamespace(container) ) {
	    String nsName = ((CsmNamespace) container).getQualifiedName();
	    if( nsName != null && nsName.length() > 0 ) {
		return nsName + "::" + getQualifiedNamePostfix(); // NOI18N
	    }
	}
        return getName();
    }

    public String getName() {
        /*if( name == null ) {
            AST tokId = null;
            for( AST token = getAst().getFirstChild(); token != null; token = token.getNextSibling() ) {
                if( token.getType() == CPPTokenTypes.CSM_QUALIFIED_ID ) {
                    AST child = token.getFirstChild();
                    if( child != null && child.getType() == CPPTokenTypes.ID ) {
                        name = child.getText();
                    }
                }
            }
        }
        if( name == null ) {
            name = "";
        }*/
        return name;
    }

    public CsmDeclaration.Kind getKind() {
        return CsmDeclaration.Kind.TYPEDEF;
    }
    
    private final CsmType createType(AST node) {
        //
        // TODO: replace this horrible code with correct one
        //
        //if( type == null ) {
            AST ptrOperator = null;
            int arrayDepth = 0;
            AST classifier = null;
            for( AST token = node.getFirstChild(); token != null; token = token.getNextSibling() ) {
//                if( token.getType() == CPPTokenTypes.CSM_TYPE_COMPOUND || 
//                        token.getType() == CPPTokenTypes.CSM_TYPE_BUILTIN ) {
//                    classifier = token;
//                    break;
//                }
                switch( token.getType() ) {
                    case CPPTokenTypes.CSM_TYPE_COMPOUND:
                    case CPPTokenTypes.CSM_TYPE_BUILTIN:
                        classifier = token;
                        break;
                    case CPPTokenTypes.LITERAL_struct:
                        AST next = token.getNextSibling();
                        if( next != null && next.getType() == CPPTokenTypes.CSM_QUALIFIED_ID ) {
                            classifier = next;
                            break;
                        }
                        break;
                }
                if( classifier != null ) {
                    break;
                }
            }
            if( classifier != null ) {
                return TypeImpl.createType(classifier, getContainingFile(), ptrOperator, arrayDepth);
            }
        //}
            return null;
    }
    
    public CsmType getType() {
        return type;
    }

    private CsmObject _getContainer() {
        if (TraceFlags.USE_REPOSITORY && TraceFlags.USE_UID_TO_CONTAINER) {
            CsmObject container = (CsmObject) UIDCsmConverter.UIDToIdentifiable(this.containerUID);
            assert (container != null || containerUID == null) : "null object for UID " + containerUID;
            return container;
        } else {
            return (CsmObject)containerRef;
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // impl of SelfPersistent
    
    public void write(DataOutput output) throws IOException {
        super.write(output);
        assert this.name != null;
        output.writeUTF(this.name);
        assert this.type != null;
        PersistentUtils.writeType(this.type, output);

        // prepare uid to write
        CsmUID<CsmIdentifiable> writeContainerUID;
        if (TraceFlags.USE_UID_TO_CONTAINER) {
            writeContainerUID = this.containerUID;
        } else {
            // save reference
            assert this.containerRef != null;
            writeContainerUID = UIDCsmConverter.identifiableToUID((CsmIdentifiable)this.containerRef);        
        }
        // not null
        assert writeContainerUID != null;
        UIDObjectFactory.getDefaultFactory().writeUID(writeContainerUID, output);        
    }  
    
    public TypedefImpl(DataInput input) throws IOException {
        super(input);
        this.name = TextCache.getString(input.readUTF());
        assert this.name != null;
        this.type = PersistentUtils.readType(input);
        assert this.type != null;
        
        CsmUID<CsmIdentifiable> readContainerUID = UIDObjectFactory.getDefaultFactory().readUID(input);
        // not null UID
        assert readContainerUID != null;
        if (TraceFlags.USE_UID_TO_CONTAINER) {
            this.containerUID = readContainerUID;
            
            this.containerRef = null;
        } else {
            // restore reference
            this.containerRef = UIDCsmConverter.UIDToIdentifiable(readContainerUID);
            assert this.containerRef != null || readContainerUID == null : "no object for UID " + readContainerUID;
            
            this.containerUID = null;
        } 
        
        assert TraceFlags.USE_REPOSITORY;
        
    }       
}
