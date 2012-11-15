/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.editor.index;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexer;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexerFactory;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.modules.parsing.spi.indexing.support.IndexDocument;
import org.netbeans.modules.parsing.spi.indexing.support.IndexingSupport;
import org.netbeans.modules.php.api.util.FileUtils;
import static org.netbeans.modules.php.api.util.FileUtils.PHP_MIME_TYPE;
import org.netbeans.modules.php.editor.PredefinedSymbols;
import org.netbeans.modules.php.editor.api.QualifiedName;
import org.netbeans.modules.php.editor.elements.IndexQueryImpl;
import org.netbeans.modules.php.editor.model.ClassConstantElement;
import org.netbeans.modules.php.editor.model.ClassScope;
import org.netbeans.modules.php.editor.model.ConstantElement;
import org.netbeans.modules.php.editor.model.FieldElement;
import org.netbeans.modules.php.editor.model.FileScope;
import org.netbeans.modules.php.editor.model.FunctionScope;
import org.netbeans.modules.php.editor.model.InterfaceScope;
import org.netbeans.modules.php.editor.model.MethodScope;
import org.netbeans.modules.php.editor.model.Model;
import org.netbeans.modules.php.editor.model.Model.Type;
import org.netbeans.modules.php.editor.model.ModelUtils;
import org.netbeans.modules.php.editor.model.NamespaceScope;
import org.netbeans.modules.php.editor.model.TraitScope;
import org.netbeans.modules.php.editor.model.VariableName;
import org.netbeans.modules.php.editor.model.impl.LazyBuild;
import org.netbeans.modules.php.editor.model.impl.VariousUtils;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.astnodes.Identifier;
import org.netbeans.modules.php.editor.parser.astnodes.PHPDocTypeNode;
import org.netbeans.modules.php.editor.parser.astnodes.Program;
import org.netbeans.modules.php.editor.parser.astnodes.Visitor;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.netbeans.modules.php.project.api.PhpSourcePath;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Index Ruby structure into the persistent store for retrieval by
 * {@link JsIndex}.
 *
 * @todo Index methods as func.in and then distinguish between exact completion and multi-completion.
 * @todo Ensure that all the stub files are compileable!
 * @todo Should I perhaps store globals and functions using the same query prefix (since I typically
 *    have to search for both anyway) ? Or perhaps not - not when doing inherited checks...
 * @todo Index file inclusion dependencies! (Uh oh - that means I -do- have to do models for HTML, etc. right?
 *     Or can I perhaps only compute that stuff live?
 * @todo Use the JsCommentLexer to pull out relevant attributes -- @private and such -- and set these
 *     as function attributes.
 * @todo There are duplicate elements -- why???
 *
 * @author Tomasz.Slota@Sun.COM
 */
public final class PHPIndexer extends EmbeddingIndexer {
    private static final Logger LOG = Logger.getLogger(PHPIndexer.class.getName());
    static final boolean PREINDEXING = Boolean.getBoolean("gsf.preindexing");
    @MIMEResolver.ExtensionRegistration(
        extension={ "php", "php3", "php4", "php5", "phtml", "inc", "phpt" },
        displayName="#PHPResolver",
        mimeType=PHP_MIME_TYPE,
        position=282
    )
    @NbBundle.Messages("PHPResolver=PHP Files")
    // a workaround for issue #132388
    private static final Collection<String> INDEXABLE_EXTENSIONS = Arrays.asList(
        "php", "php3", "php4", "php5", "phtml", "inc", "phpt"
    );

    // I need to be able to search several things:
    // (1) by function root name, e.g. quickly all functions that start
    //    with "f" should find unknown.foo.
    // (2) by namespace, e.g. I should be able to quickly find all
    //    "foo.bar.b*" functions
    // (3) constructors
    // (4) global variables, preferably in the same way
    // (5) extends so I can do inheritance inclusion!

    // Solution: Store the following:
    // class:name for each class
    // extend:old:new for each inheritance? Or perhaps do this in the class entry
    // fqn: f.q.n.function/global;sig; for each function
    // base: function;fqn;sig
    // The signature should look like this:
    // ;flags;;args;offset;docoffset;browsercompat;types;
    // (between flags and args you have the case sensitive name for flags)

    public static final String FIELD_BASE = "base"; //NOI18N
    public static final String FIELD_EXTEND = "extend"; //NOI18N
    public static final String FIELD_CLASS = "clz"; //NOI18N
    public static final String FIELD_SUPER_CLASS = "superclz"; //NOI18N
    public static final String FIELD_IFACE = "iface"; //NOI18N
    public static final String FIELD_SUPER_IFACE = "superiface"; //NOI18N
    public static final String FIELD_CONST = "const"; //NOI18N
    public static final String FIELD_CLASS_CONST = "clz.const"; //NOI18N
    public static final String FIELD_FIELD = "field"; //NOI18N
    public static final String FIELD_METHOD = "method"; //NOI18N
    public static final String FIELD_CONSTRUCTOR = "constructor"; //NOI18N
    public static final String FIELD_INCLUDE = "include"; //NOI18N
    public static final String FIELD_IDENTIFIER = "identifier_used"; //NOI18N
    public static final String FIELD_IDENTIFIER_DECLARATION = "identifier_declaration"; //NOI18N
    public static final String FIELD_NAMESPACE = "ns"; //NOI18N
    public static final String FIELD_TRAIT = "trait"; //NOI18N
    public static final String FIELD_USED_TRAIT = "usedtrait"; //NOI18N
    public static final String FIELD_TRAIT_CONFLICT_RESOLUTION = "traitconf"; //NOI18N
    public static final String FIELD_TRAIT_METHOD_ALIAS = "traitmeth"; //NOI18N

    public static final String FIELD_VAR = "var"; //NOI18N
    /** This field is for fast access top level elemnts. */
    public static final String FIELD_TOP_LEVEL = "top"; //NOI18N

    private static final List<String> ALL_FIELDS = new LinkedList<String>(
            Arrays.asList(
                new String[] {
                    FIELD_BASE,
                    FIELD_EXTEND,
                    FIELD_CLASS,
                    FIELD_IFACE,
                    FIELD_CONST,
                    FIELD_CLASS_CONST,
                    FIELD_FIELD,
                    FIELD_METHOD,
                    FIELD_CONSTRUCTOR,
                    FIELD_INCLUDE,
                    FIELD_IDENTIFIER,
                    FIELD_VAR,
                    FIELD_TOP_LEVEL,
                    FIELD_NAMESPACE,
                    FIELD_TRAIT,
                    FIELD_USED_TRAIT,
                    FIELD_TRAIT_CONFLICT_RESOLUTION,
                    FIELD_TRAIT_METHOD_ALIAS
                }
            )
    );

    public static List<String> getAllFields() {
        return new LinkedList<String>(ALL_FIELDS);
    }

    public String getPersistentUrl(File file) {
        String url;
        try {
            url = Utilities.toURI(file).toURL().toExternalForm();
            // Make relative URLs for urls in the libraries
            return PHPIndex.getPreindexUrl(url);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
            return file.getPath();
        }
    }

    @Override
    protected void index(Indexable indexable, Result parserResult, Context context) {
        try {
            PHPParseResult r = (PHPParseResult) parserResult;
            if (r.getProgram() == null) {
                return;
            }
            final FileObject fileObject = r.getSnapshot().getSource().getFileObject();
            assert r.getDiagnostics().isEmpty() || !PhpSourcePath.FileType.INTERNAL.equals(PhpSourcePath.getFileType(fileObject)) : fileObject.getPath();

            boolean isFileEdited = false;
            if (!context.isAllFilesIndexing() && context.checkForEditorModifications()) {
                final JTextComponent jtc = EditorRegistry.lastFocusedComponent();
                if (jtc != null) {
                    Document doc = jtc.getDocument();
                    if (doc != null) {
                        FileObject editedFO = NbEditorUtilities.getFileObject(doc);
                        if (editedFO != null && editedFO.equals(r.getSnapshot().getSource().getFileObject())) {
                            isFileEdited = true;
                        }
                    }
                }
            }
            IndexQueryImpl.clearNamespaceCache();
            List<IndexDocument> documents = new LinkedList<IndexDocument>();
            IndexingSupport support = IndexingSupport.getInstance(context);
            Model model = r.getModel(Type.COMMON);
            final FileScope fileScope = model.getFileScope();
            IndexDocument reverseIdxDocument = support.createDocument(indexable);
            documents.add(reverseIdxDocument);
            for (ClassScope classScope : ModelUtils.getDeclaredClasses(fileScope)) {
                IndexDocument classDocument = support.createDocument(indexable);
                documents.add(classDocument);
                classDocument.addPair(FIELD_CLASS, classScope.getIndexSignature(), true, true);
                QualifiedName superClassName = classScope.getSuperClassName();
                if (superClassName != null) {
                    final String name = superClassName.getName();
                    final String namespaceName = VariousUtils.getFullyQualifiedName(
                            superClassName,
                            classScope.getOffset(),
                            (NamespaceScope) classScope.getInScope()).getNamespaceName();
                    classDocument.addPair(FIELD_SUPER_CLASS, String.format("%s;%s;%s", name.toLowerCase(), name, namespaceName), true, true); //NOI18N
                }
                Set<QualifiedName> superInterfaces = classScope.getSuperInterfaces();
                for (QualifiedName superIfaceName : superInterfaces) {
                    final String name = superIfaceName.getName();
                    final String namespaceName = VariousUtils.getFullyQualifiedName(
                            superIfaceName,
                            classScope.getOffset(),
                            (NamespaceScope) classScope.getInScope()).getNamespaceName();
                    classDocument.addPair(FIELD_SUPER_IFACE, String.format("%s;%s;%s", name.toLowerCase(), name, namespaceName), true, true); //NOI18N
                }
                for (QualifiedName qualifiedName : classScope.getUsedTraits()) {
                    final String name = qualifiedName.getName();
                    final String namespaceName = VariousUtils.getFullyQualifiedName(
                            qualifiedName,
                            classScope.getOffset(),
                            (NamespaceScope) classScope.getInScope()).getNamespaceName();
                    classDocument.addPair(FIELD_USED_TRAIT, String.format("%s;%s;%s", name.toLowerCase(), name, namespaceName), true, true); //NOI18N
                }
                classDocument.addPair(FIELD_TOP_LEVEL, classScope.getName().toLowerCase(), true, true);

                for (MethodScope methodScope : classScope.getDeclaredMethods()) {
                    if (!isFileEdited && methodScope instanceof LazyBuild) {
                        LazyBuild lazyMethod = (LazyBuild) methodScope;
                        if (!lazyMethod.isScanned()) {
                            lazyMethod.scan();
                        }
                    }
                    classDocument.addPair(FIELD_METHOD, methodScope.getIndexSignature(), true, true);
                    if (methodScope.isConstructor()) {
                        classDocument.addPair(FIELD_CONSTRUCTOR, methodScope.getConstructorIndexSignature(), false, true);
                    }
                }
                for (FieldElement fieldElement : classScope.getDeclaredFields()) {
                    classDocument.addPair(FIELD_FIELD, fieldElement.getIndexSignature(), true, true);
                }
                for (ClassConstantElement constantElement : classScope.getDeclaredConstants()) {
                    classDocument.addPair(FIELD_CLASS_CONST, constantElement.getIndexSignature(), true, true);
                }
            }
            for (InterfaceScope ifaceSCope : ModelUtils.getDeclaredInterfaces(fileScope)) {
                IndexDocument classDocument = support.createDocument(indexable);
                documents.add(classDocument);
                classDocument.addPair(FIELD_IFACE, ifaceSCope.getIndexSignature(), true, true);
                Set<QualifiedName> superInterfaces = ifaceSCope.getSuperInterfaces();
                for (QualifiedName superIfaceName : superInterfaces) {
                    final String name = superIfaceName.getName();
                    final String namespaceName = superIfaceName.toNamespaceName().toString();
                    classDocument.addPair(FIELD_SUPER_IFACE, String.format("%s;%s;%s", name.toLowerCase(), name, namespaceName), true, true); //NOI18N
                }

                classDocument.addPair(FIELD_TOP_LEVEL, ifaceSCope.getName().toLowerCase(), true, true);
                for (MethodScope methodScope : ifaceSCope.getDeclaredMethods()) {
                    classDocument.addPair(FIELD_METHOD, methodScope.getIndexSignature(), true, true);
                }
                for (ClassConstantElement constantElement : ifaceSCope.getDeclaredConstants()) {
                    classDocument.addPair(FIELD_CLASS_CONST, constantElement.getIndexSignature(), true, true);
                }
            }
            for (TraitScope traitScope : ModelUtils.getDeclaredTraits(fileScope)) {
                IndexDocument traitDocument = support.createDocument(indexable);
                documents.add(traitDocument);
                traitDocument.addPair(FIELD_TRAIT, traitScope.getIndexSignature(), true, true);
                traitDocument.addPair(FIELD_TOP_LEVEL, traitScope.getName().toLowerCase(), true, true);
                for (QualifiedName qualifiedName : traitScope.getUsedTraits()) {
                    final String name = qualifiedName.getName();
                    final String namespaceName = VariousUtils.getFullyQualifiedName(
                            qualifiedName,
                            traitScope.getOffset(),
                            (NamespaceScope) traitScope.getInScope()).getNamespaceName();
                    traitDocument.addPair(FIELD_USED_TRAIT, String.format("%s;%s;%s", name.toLowerCase(), name, namespaceName), true, true); //NOI18N
                }
                for (MethodScope methodScope : traitScope.getDeclaredMethods()) {
                    traitDocument.addPair(FIELD_METHOD, methodScope.getIndexSignature(), true, true);
                }
                for (FieldElement fieldElement : traitScope.getDeclaredFields()) {
                    traitDocument.addPair(FIELD_FIELD, fieldElement.getIndexSignature(), true, true);
                }
            }

            IndexDocument defaultDocument = support.createDocument(indexable);
            documents.add(defaultDocument);
            for (FunctionScope functionScope : ModelUtils.getDeclaredFunctions(fileScope)) {
                defaultDocument.addPair(FIELD_BASE, functionScope.getIndexSignature(), true, true);
                defaultDocument.addPair(FIELD_TOP_LEVEL, functionScope.getName().toLowerCase(), true, true);
            }
            for (ConstantElement constantElement : ModelUtils.getDeclaredConstants(fileScope)) {
                defaultDocument.addPair(FIELD_CONST, constantElement.getIndexSignature(), true, true);
                defaultDocument.addPair(FIELD_TOP_LEVEL, constantElement.getName().toLowerCase(), true, true);
            }
            for (NamespaceScope nsElement : fileScope.getDeclaredNamespaces()) {
                Collection<? extends VariableName> declaredVariables = nsElement.getDeclaredVariables();
                for (VariableName variableName : declaredVariables) {
                    String varName = variableName.getName();
                    String varNameNoDollar = varName.startsWith("$") ? varName.substring(1) : varName;
                    if (!PredefinedSymbols.isSuperGlobalName(varNameNoDollar)) {
                        final String indexSignature = variableName.getIndexSignature();
                        defaultDocument.addPair(FIELD_VAR, indexSignature, true, true);
                        defaultDocument.addPair(FIELD_TOP_LEVEL, variableName.getName().toLowerCase(), true, true);
                    }
                }
                if (nsElement.isDefaultNamespace()) {
                    continue; // do not index default ns
                }

                defaultDocument.addPair(FIELD_NAMESPACE, nsElement.getIndexSignature(), true, true);
                defaultDocument.addPair(FIELD_TOP_LEVEL, nsElement.getName().toLowerCase(), true, true);
            }
            final IndexDocument identifierDocument = support.createDocument(indexable);
            documents.add(identifierDocument);
            Program program = r.getProgram();
            Visitor identifierVisitor = new DefaultVisitor() {
                @Override
                public void visit(Program node) {
                    scan(node.getStatements());
                    scan(node.getComments());
                }

                @Override
                public void visit(Identifier identifier) {
                    addSignature(IdentifierSignatureFactory.createIdentifier(identifier));
                    super.visit(identifier);
                }

                @Override
                public void visit(PHPDocTypeNode node) {
                    addSignature(IdentifierSignatureFactory.create(node));
                    super.visit(node);
                }

                private void addSignature(final IdentifierSignature signature) {
                    signature.save(identifierDocument, FIELD_IDENTIFIER);
                }
            };
            program.accept(identifierVisitor);
            for (IndexDocument d : documents) {
                support.addDocument(d);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public File getPreindexedData() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * As the above documentation states, this is a temporary solution / hack
     * for 6.1 only.
     */
     public boolean acceptQueryPath(String url) {
        // Filter out JavaScript stuff
        return url.indexOf("jsstubs") == -1 && // NOI18N
                // Filter out Ruby stuff
                url.indexOf("/ruby/") == -1 &&  // NOI18N
                url.indexOf("/gems/") == -1 &&  // NOI18N
                url.indexOf("lib/ruby/") == -1; // NOI18N
     }

     public static final class Factory extends EmbeddingIndexerFactory {

        public static final String NAME = "php"; // NOI18N
        public static final int VERSION = 21;

        @Override
        public EmbeddingIndexer createIndexer(final Indexable indexable, final Snapshot snapshot) {

            if (isIndexable(indexable, snapshot)) {
                return new PHPIndexer();
            } else {
                return null;
            }
        }

        @Override
        public String getIndexerName() {
            return NAME;
        }

        @Override
        public int getIndexVersion() {
            return VERSION;
        }

        private boolean isIndexable(Indexable indexable, Snapshot snapshot) {
            // Cannot call file.getFileObject().getMIMEType() here for several reasons:
            // (1) when cleaning up the index for deleted files, file.getFileObject().getMIMEType()
            //   may return "content/unknown", and in some cases, file.getFileObject() returns null
            // (2) file.getFileObject() can be expensive during startup indexing when we're
            //   rapidly scanning through lots of directories to determine which files are
            //   indexable. This is done using the java.io.File API rather than the more heavyweight
            //   FileObject, and each file.getFileObject() will perform a FileUtil.toFileObject() call.
            // Since the mime resolver for PHP is simple -- it's just based on the file extension,
            // we perform the same check here:
            //if (PHPLanguage.PHP_MIME_TYPE.equals(file.getFileObject().getMIMEType())) { // NOI18N

            FileObject fileObject = snapshot.getSource().getFileObject();

            if (INDEXABLE_EXTENSIONS.contains(fileObject.getExt().toLowerCase())) {
                return true;
            }

            return FileUtils.isPhpFile(fileObject);
        }

        @Override
        public void filesDeleted(Iterable<? extends Indexable> deleted, Context context) {
            try {
                IndexingSupport is = IndexingSupport.getInstance(context);
                for (Indexable i : deleted) {
                    is.removeDocuments(i);
                }
            } catch (IOException ioe) {
                LOG.log(Level.WARNING, null, ioe);
            }
        }

        @Override
        public void rootsRemoved(final Iterable<? extends URL> removedRoots) {

        }

        @Override
        public void filesDirty(Iterable<? extends Indexable> dirty, Context context) {
            try {
                IndexingSupport is = IndexingSupport.getInstance(context);
                for (Indexable i : dirty) {
                    is.markDirtyDocuments(i);
                }
            } catch (IOException ioe) {
                LOG.log(Level.WARNING, null, ioe);
            }
        }
    } // End of Factory class
}
