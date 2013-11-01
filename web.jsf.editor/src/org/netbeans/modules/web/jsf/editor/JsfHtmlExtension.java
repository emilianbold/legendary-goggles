/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.jsf.editor;

import java.awt.Color;
import java.util.Map.Entry;
import java.util.*;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.html.lexer.HTMLTokenId;
import org.netbeans.api.lexer.*;
import org.netbeans.lib.editor.util.CharSequenceUtilities;
import org.netbeans.modules.csl.api.DeclarationFinder.DeclarationLocation;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.HintsProvider.HintsManager;
import org.netbeans.modules.csl.api.*;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.html.editor.api.completion.HtmlCompletionItem;
import org.netbeans.modules.html.editor.api.gsf.HtmlExtension;
import org.netbeans.modules.html.editor.api.gsf.HtmlParserResult;
import org.netbeans.modules.html.editor.lib.api.elements.*;
import org.netbeans.modules.parsing.api.*;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.modules.web.common.api.FileReferenceCompletion;
import org.netbeans.modules.web.common.taginfo.AttrValueType;
import org.netbeans.modules.web.common.taginfo.LibraryMetadata;
import org.netbeans.modules.web.common.taginfo.TagAttrMetadata;
import org.netbeans.modules.web.common.taginfo.TagMetadata;
import org.netbeans.modules.web.jsf.api.editor.JsfFacesComponentsProvider.FacesComponentLibrary;
import org.netbeans.modules.web.jsf.editor.completion.JsfCompletionItem;
import org.netbeans.modules.web.jsf.editor.facelets.AbstractFaceletsLibrary;
import org.netbeans.modules.web.jsf.editor.facelets.CompositeComponentLibrary;
import org.netbeans.modules.web.jsf.editor.facelets.FaceletsLibraryMetadata;
import org.netbeans.modules.web.jsf.editor.hints.HintsRegistry;
import org.netbeans.modules.web.jsf.editor.index.CompositeComponentModel;
import org.netbeans.modules.web.jsf.editor.index.JsfPageModelFactory;
import org.netbeans.modules.web.jsfapi.api.Attribute;
import org.netbeans.modules.web.jsfapi.api.DefaultLibraryInfo;
import org.netbeans.modules.web.jsfapi.api.JsfUtils;
import org.netbeans.modules.web.jsfapi.api.Library;
import org.netbeans.modules.web.jsfapi.api.LibraryComponent;
import org.netbeans.modules.web.jsfapi.api.NamespaceUtils;
import org.netbeans.modules.web.jsfapi.api.Tag;
import org.netbeans.modules.web.jsfapi.spi.LibraryUtils;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.lexer.MutableTextInput;
import org.openide.filesystems.FileObject;

/**
 * XXX should be rather done by dynamic artificial embedding creation. The
 * support then can be implemented by CSL language mapped to the language
 * mimetype.
 *
 * @author marekfukala
 */
@MimeRegistration(mimeType=JsfUtils.JSF_XHTML_FILE_MIMETYPE, service=HtmlExtension.class)
public class JsfHtmlExtension extends HtmlExtension {

    private static final String EL_ENABLED_KEY = "el_enabled"; //NOI18N
    private static final FilenameSupport FILENAME_SUPPORT = new FilenameSupport();

    @Override
    public Map<OffsetRange, Set<ColoringAttributes>> getHighlights(HtmlParserResult result, SchedulerEvent event) {
        final Map<OffsetRange, Set<ColoringAttributes>> highlights = new HashMap<>();

        //highlight JSF tags
        highlightJsfTags(result, highlights);

        //check if the EL is enabled in the file and enables it if not
        checkELEnabled(result);

        return highlights;

    }

    public void checkELEnabled(HtmlParserResult result) {
        Document doc = result.getSnapshot().getSource().getDocument(true);
        InputAttributes inputAttributes = (InputAttributes) doc.getProperty(InputAttributes.class);
        if (inputAttributes == null) {
            inputAttributes = new InputAttributes();
            doc.putProperty(InputAttributes.class, inputAttributes);
        }
        Language xhtmlLang = Language.find(org.netbeans.modules.web.jsf.editor.JsfUtils.XHTML_MIMETYPE); //NOI18N
        if (inputAttributes.getValue(LanguagePath.get(xhtmlLang), EL_ENABLED_KEY) == null) {
            inputAttributes.setValue(LanguagePath.get(xhtmlLang), EL_ENABLED_KEY, new Object(), false);

            //refresh token hierarchy so the EL becomes lexed
            recolor(doc);
        }
    }

    private void recolor(final Document doc) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                NbEditorDocument nbdoc = (NbEditorDocument) doc;
                nbdoc.extWriteLock();
                try {
                    MutableTextInput mti = (MutableTextInput) doc.getProperty(MutableTextInput.class);
                    if (mti != null) {
                        mti.tokenHierarchyControl().rebuild();
                    }
                } finally {
                    nbdoc.extWriteUnlock();
                }
            }
        });
    }

    private void highlightJsfTags(HtmlParserResult result, final Map<OffsetRange, Set<ColoringAttributes>> highlights) {
        final Snapshot snapshot = result.getSnapshot();
        Source source = snapshot.getSource();
        JsfSupportImpl jsfs = JsfSupportImpl.findFor(source);
        if (jsfs == null) {
            return;
        }
        Map<String, Library> libs = jsfs.getLibraries();
        Map<String, String> nss = result.getNamespaces();

        //1. resolve which declared libraries are available on classpath

        //2. resolve which tag prefixes are registered for libraries, either available or missing
        // add hint for missing library

        for (String namespace : nss.keySet()) {
            Node root = result.root(namespace);
            if (root != null) {
                final Library tldl = NamespaceUtils.getForNs(libs, namespace);
                ElementUtils.visitChildren(root, new ElementVisitor() {
                    @Override
                    public void visit(Element element) {
                        if (element.type() == ElementType.OPEN_TAG
                                || element.type() == ElementType.CLOSE_TAG) {
                            Named named = (Named) element;

                            if (named.namespacePrefix() != null) {
                                Set<ColoringAttributes> coloring = tldl == null ? ColoringAttributes.CLASS_SET : ColoringAttributes.METHOD_SET;
                                try {
                                    highlight(snapshot, named, highlights, coloring);
                                } catch (BadLocationException ex) {
                                    //just ignore
                                }
                            }
                        }
                    }
                });
            }
        }

    }

    private void highlight(Snapshot s, Named node, Map<OffsetRange, Set<ColoringAttributes>> hls, Set<ColoringAttributes> cas) throws BadLocationException {
        // "<div" id='x'> part
        int prefixLen = node.type() == ElementType.OPEN_TAG ? 1 : 2; //"<" open; "</" close
        hls.put(getDocumentOffsetRange(s, node.from(), node.from() + node.name().length() + prefixLen /* tag open symbol len */),
                cas);
        // <div id='x'">" part
        hls.put(getDocumentOffsetRange(s, node.to() - 1, node.to()),
                cas);

    }

    private OffsetRange getDocumentOffsetRange(Snapshot s, int astFrom, int astTo) throws BadLocationException {
        int from = s.getOriginalOffset(astFrom);
        int to = s.getOriginalOffset(astTo);

        if (from == -1 || to == -1) {
            throw new BadLocationException("Cannot convert snapshot offset to document offset", -1); //NOI18N
        }

        return new OffsetRange(from, to);
    }

    @Override
    public List<CompletionItem> completeOpenTags(CompletionContext context) {
        HtmlParserResult result = context.getResult();
        Source source = result.getSnapshot().getSource();
        JsfSupportImpl jsfs = JsfSupportImpl.findFor(source);
        if (jsfs == null) {
            return Collections.emptyList();
        }
        Map<String, Library> libs = jsfs.getLibraries();
        Set<Library> librariesSet = new HashSet<>(libs.values());
        //uri to prefix map
        Map<String, String> declaredNS = result.getNamespaces();

        List<CompletionItem> items = new ArrayList<>();

        int colonIndex = context.getPrefix().indexOf(':');
        if (colonIndex == -1) {
            //editing namespace or tag w/o ns
            //offer all tags
            for (Library lib : librariesSet) {
                String declaredPrefix = NamespaceUtils.getForNs(declaredNS, lib.getNamespace());
                if (declaredPrefix == null) {
                    //undeclared prefix, try to match with default library prefix
                    if (lib.getDefaultPrefix() != null && lib.getDefaultPrefix().startsWith(context.getPrefix())) {
                        items.addAll(queryLibrary(context, lib, lib.getDefaultPrefix(), true, jsfs.isJsf22Plus()));
                    }
                } else {
                    items.addAll(queryLibrary(context, lib, declaredPrefix, false, jsfs.isJsf22Plus()));
                }
            }
        } else {
            String tagNamePrefix = context.getPrefix().substring(0, colonIndex);
            //find a namespace according to the prefix
            String namespace = getUriForPrefix(tagNamePrefix, declaredNS);
            if (namespace == null) {
                //undeclared prefix, check if a taglib contains it as
                //default prefix. If so, offer it in the cc w/ tag autoimport function
                for (Library lib : librariesSet) {
                    if (lib.getDefaultPrefix() != null && lib.getDefaultPrefix().equals(tagNamePrefix)) {
                        //match
                        items.addAll(queryLibrary(context, lib, tagNamePrefix, true, jsfs.isJsf22Plus()));
                    }
                }

            } else {
                //query only associated lib
                Library lib = NamespaceUtils.getForNs(libs, namespace);
                if (lib == null) {
                    //no such lib, exit
                    return Collections.emptyList();
                } else {
                    //query the library
                    items.addAll(queryLibrary(context, lib, tagNamePrefix, false, jsfs.isJsf22Plus()));
                }
            }
        }

        //filter the items according to the prefix
        Iterator<CompletionItem> itr = items.iterator();
        while (itr.hasNext()) {
            if (!CharSequenceUtilities.startsWith(itr.next().getInsertPrefix(), context.getPrefix())) {
                itr.remove();
            }
        }

        return items;

    }

    private String getUriForPrefix(String prefix, Map<String, String> namespaces) {
        for (Entry<String, String> entry : namespaces.entrySet()) {
            if (prefix.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Collection<CompletionItem> queryLibrary(CompletionContext context, Library lib, String nsPrefix, boolean undeclared, boolean isJsf22Plus) {
        Collection<CompletionItem> items = new ArrayList<>();
        for (LibraryComponent component : lib.getComponents()) {
            if (!(component instanceof AbstractFaceletsLibrary.Function)) {
                items.add(JsfCompletionItem.createTag(context.getCCItemStartOffset(), component, nsPrefix, undeclared, isJsf22Plus));
            }
        }

        return items;
    }

    @Override
    public List<CompletionItem> completeAttributes(CompletionContext context) {
        HtmlParserResult result = context.getResult();
        Source source = result.getSnapshot().getSource();
        JsfSupportImpl jsfs = JsfSupportImpl.findFor(source);
        if (jsfs == null) {
            return Collections.emptyList();
        }
        Map<String, Library> libs = jsfs.getLibraries();
        //uri to prefix map
        Map<String, String> declaredNS = result.getNamespaces();

        List<CompletionItem> items = new ArrayList<>();

        Element queriedNode = context.getCurrentNode();
        if (queriedNode.type() != ElementType.OPEN_TAG) {
            return Collections.emptyList();
        }
        OpenTag ot = (OpenTag) queriedNode;
        CharSequence nsPrefix = ot.namespacePrefix();
        if (nsPrefix == null) {
            //jsf tag always have a prefix
            return Collections.emptyList();
        }
        String tagName = ot.unqualifiedName().toString();

        String namespace = getUriForPrefix(nsPrefix.toString(), declaredNS);
        Library flib = NamespaceUtils.getForNs(libs, namespace);
        if (flib == null) {
            //The facelets library not found. This happens if one declares
            //a namespace which is not matched to any existing library
            return Collections.emptyList();
        }

        LibraryComponent comp = flib.getComponent(tagName);
        if (comp != null) {
            Tag tag = comp.getTag();
            if (tag != null) {
                Collection<Attribute> attrs = tag.getAttributes();
                //TODO resolve help
                Collection<String> existingAttrNames = new ArrayList<>();
                for (org.netbeans.modules.html.editor.lib.api.elements.Attribute a : ot.attributes()) {
                    existingAttrNames.add(a.name().toString());
                }

                for (Attribute a : attrs) {
                    String attrName = a.getName();
                    if (!existingAttrNames.contains(attrName)
                            || existingAttrNames.contains(context.getItemText())) {
                        //show only unused attributes except the one where the caret currently stays
                        //this is because of we need to show the item in the completion since
                        //use might want to see javadoc of already used attribute
                        items.add(JsfCompletionItem.createAttribute(attrName, context.getCCItemStartOffset(), flib, tag, a));
                    }
                }
            }

        }


        if (context.getPrefix().length() > 0) {
            //filter the items according to the prefix
            Iterator<CompletionItem> itr = items.iterator();
            while (itr.hasNext()) {
                CharSequence insertPrefix = itr.next().getInsertPrefix();
                if(insertPrefix != null) {
                    if (!CharSequenceUtilities.startsWith(insertPrefix, context.getPrefix())) {
                        itr.remove();
                    }
                }
            }
        }

        return items;
    }

    @Override
    public List<CompletionItem> completeAttributeValue(CompletionContext context) {
        List<CompletionItem> items = new ArrayList<>();

        JsfSupportImpl jsfs = JsfSupportImpl.findFor(context.getResult().getSnapshot().getSource());
        String ns = ElementUtils.getNamespace(context.getCurrentNode());
        OpenTag openTag = context.getCurrentNode().type() == ElementType.OPEN_TAG 
                ? (OpenTag) context.getCurrentNode() : null;

        //complete xmlns attribute value
        if(jsfs != null) {
            completeXMLNSAttribute(context, items, jsfs);
        }
        
        if(ns == null || openTag == null) {
            return items;
        }
        
        //first try to complete using special metadata
        completeTagLibraryMetadata(context, items, ns, openTag);

        if(jsfs == null) {
            return items;
        }

        //then try to complete according to the attribute type (taken from the library descriptor)
        completeValueAccordingToType(context, items, ns, openTag, jsfs);

        // completion for files in cases of ui:include src attribute
        completeFaceletsFromProject(context, items, ns, openTag);

        //facets
        completeFacetsInCCImpl(context, items, ns, openTag, jsfs);
        completeFacets(context, items, ns, openTag, jsfs);

        return items;
    }

    //1.
    //<cc:implementation>
    //<cc:render/insertFacet name="|" />  
    //</cc:implementation>
    //offsers facet declarations only from within this document
    private void completeFacetsInCCImpl(CompletionContext context, List<CompletionItem> items, String ns, OpenTag openTag, JsfSupportImpl jsfs) {
        if ("http://java.sun.com/jsf/composite".equalsIgnoreCase(ns) || "http://xmlns.jcp.org/jsf/composite".equalsIgnoreCase(ns)) {
            String tagName = openTag.unqualifiedName().toString();
            if ("renderFacet".equalsIgnoreCase(tagName) || "insertFacet".equalsIgnoreCase(tagName)) { //NOI18N
                if ("name".equalsIgnoreCase(context.getAttributeName())) { //NOI18N
                    CompositeComponentModel ccModel = (CompositeComponentModel) JsfPageModelFactory.getFactory(CompositeComponentModel.Factory.class).getModel(context.getResult());
                    if (ccModel != null) {
                        Collection<String> facets = ccModel.getDeclaredFacets();
                        for (String facet : facets) {
                            items.add(HtmlCompletionItem.createAttributeValue(facet, context.getCCItemStartOffset(), !context.isValueQuoted())); //NOI18N
                        }
                    }
                }
            }
        }
    }

    //2.<f:facet name="|">
    //offsers all facetes
    private void completeFacets(CompletionContext context, List<CompletionItem> items, String ns, OpenTag openTag, JsfSupportImpl jsfs) {
        if ("http://java.sun.com/jsf/core".equalsIgnoreCase(ns) || "http://xmlns.jcp.org/jsf/core".equalsIgnoreCase(ns)) {
            String tagName = openTag.unqualifiedName().toString();
            if ("facet".equalsIgnoreCase(tagName)) { //NOI18N
                if ("name".equalsIgnoreCase(context.getAttributeName())) { //NOI18N
                    //try to get composite library model for all declared libraries and extract facets from there
                    for(String libraryNs : context.getResult().getNamespaces().keySet()) {
                        Library library = jsfs.getLibrary(libraryNs);
                        if(library != null) {
                            if(library instanceof CompositeComponentLibrary) {
                                Collection<? extends LibraryComponent> lcs = library.getComponents();
                                for(LibraryComponent lc : lcs) {
                                    CompositeComponentLibrary.CompositeComponent ccomp = (CompositeComponentLibrary.CompositeComponent)lc;
                                    CompositeComponentModel model = ccomp.getComponentModel();
                                    for(String facetName : model.getDeclaredFacets()) {
                                        items.add(HtmlCompletionItem.createAttributeValue(facetName, context.getCCItemStartOffset(), !context.isValueQuoted())); //NOI18N
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void completeValueAccordingToType(CompletionContext context, List<CompletionItem> items, String ns, OpenTag openTag, JsfSupportImpl jsfs) {
        Library lib = jsfs.getLibrary(ns);
        if (lib == null) {
            return;
        }

        String tagName = openTag.unqualifiedName().toString();

        LibraryComponent comp = lib.getComponent(tagName);
        if (comp == null) {
            return;
        }

        String attrName = context.getAttributeName();
        Attribute attr = comp.getTag().getAttribute(attrName);
        if (attr == null) {
            return;
        }

        //TODO: Add more types and generalize the code then
        String aType = attr.getType();
        if ("boolean".equals(aType) || "java.lang.Boolean".equals(aType)) { //NOI18N
            //boolean type
            items.add(HtmlCompletionItem.createAttributeValue("true", context.getCCItemStartOffset(), !context.isValueQuoted())); //NOI18N
            items.add(HtmlCompletionItem.createAttributeValue("false", context.getCCItemStartOffset(), !context.isValueQuoted())); //NOI18N
        }

    }

    private static void completeFaceletsFromProject(CompletionContext context, List<CompletionItem> items, String ns, OpenTag openTag) {
        if (NamespaceUtils.containsNsOf(Collections.singleton(ns), DefaultLibraryInfo.FACELETS)
                && "src".equals(context.getAttributeName())) { //NOI18N
            items.addAll(FILENAME_SUPPORT.getItems(
                    context.getResult().getSnapshot().getSource().getFileObject(),
                    context.getCCItemStartOffset(),
                    context.getPrefix()));
        }
    }

    private void completeXMLNSAttribute(CompletionContext context, List<CompletionItem> items, JsfSupportImpl jsfs) {
        if (context.getAttributeName().toLowerCase(Locale.ENGLISH).startsWith("xmlns")) { //NOI18N
            //xml namespace completion for facelets namespaces
            Set<String> nss = NamespaceUtils.getAvailableNss(jsfs.getLibraries(), jsfs.isJsf22Plus());

            //add also xhtml ns to the completion
            nss.add(LibraryUtils.XHTML_NS);
            for (String namespace : nss) {
                if (namespace.startsWith(context.getPrefix())) {
                    items.add(HtmlCompletionItem.createAttributeValue(namespace, context.getCCItemStartOffset(), !context.isValueQuoted()));
                }
            }
        }
    }

    private void completeTagLibraryMetadata(CompletionContext context, List<CompletionItem> items, String ns, OpenTag openTag) {
        String attrName = context.getAttributeName();
        String tagName = openTag.unqualifiedName().toString();
        LibraryMetadata lib = FaceletsLibraryMetadata.get(ns);

        if (lib != null) {
            TagMetadata tag = lib.getTag(tagName);

            if (tag != null) {
                TagAttrMetadata attr = tag.getAttribute(attrName);

                if (attr != null) {
                    Collection<AttrValueType> valueTypes = attr.getValueTypes();

                    if (valueTypes != null) {
                        for (AttrValueType valueType : valueTypes) {
                            String[] possibleVals = valueType.getPossibleValues();

                            if (possibleVals != null) {
                                for (String val : possibleVals) {
                                    if (val.startsWith(context.getPrefix())) {
                                        CompletionItem itm = HtmlCompletionItem.createAttributeValue(val,
                                                context.getCCItemStartOffset(),
                                                !context.isValueQuoted());

                                        items.add(itm);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    public DeclarationLocation findDeclaration(ParserResult result, final int caretOffset) {
        assert result instanceof HtmlParserResult;
        HtmlParserResult htmlresult = (HtmlParserResult) result;
        Element leaf = htmlresult.findByPhysicalRange(caretOffset, true);

        if (leaf == null || leaf.type() != ElementType.OPEN_TAG) {
            return DeclarationLocation.NONE;
        }

        JsfSupportImpl jsfs = JsfSupportImpl.findFor(result.getSnapshot().getSource());
        if (jsfs == null) {
            return DeclarationLocation.NONE;
        }

        String ns = ElementUtils.getNamespace(leaf);
        if (ns == null) {
            return DeclarationLocation.NONE;
        }

        Library lib = jsfs.getLibrary(ns);
        if (lib == null) {
            return DeclarationLocation.NONE;
        }

        TokenSequence ts = JsfNavigationHelper.getTokenSequenceAtCaret(result.getSnapshot().getTokenHierarchy(), caretOffset);
        if (ts == null) {
            return DeclarationLocation.NONE;
        }

        Token t = ts.token();
        if (t.id() == HTMLTokenId.VALUE) {
            String value = CharSequenceUtilities.toString(ts.token().text()).replaceAll("[\"']", ""); //NOI18N
            String attribute = ""; //NOI18N
            while (ts.movePrevious()) {
                if (ts.token().id() == HTMLTokenId.TAG_OPEN) {
                    String tag = CharSequenceUtilities.toString(ts.token().text());
                    return JsfNavigationHelper.goToReferencedFile(htmlresult, caretOffset, tag, attribute, value);
                } else if (ts.token().id() == HTMLTokenId.ARGUMENT && attribute.isEmpty()) {
                    attribute = CharSequenceUtilities.toString(ts.token().text());
                }
            }
        } else {
            if (lib instanceof CompositeComponentLibrary) {
                return JsfNavigationHelper.goToCompositeComponentLibrary(htmlresult, caretOffset, lib);
            } else if (lib instanceof FacesComponentLibrary) {
                return JsfNavigationHelper.goToFacesComponentLibrary(htmlresult, caretOffset, (FacesComponentLibrary) lib);
            }
        }

        return DeclarationLocation.NONE;

    }

    @Override
    public OffsetRange getReferenceSpan(final Document doc, final int caretOffset) {
        TokenHierarchy th = TokenHierarchy.get(doc);
        TokenSequence ts = JsfNavigationHelper.getTokenSequenceAtCaret(th, caretOffset);
        if (ts == null) {
            return OffsetRange.NONE;
        }

        Token t = ts.token();
        if (t.id() == HTMLTokenId.TAG_OPEN) {
            if (CharSequenceUtilities.indexOf(t.text(), ':') != -1) {
                return new OffsetRange(ts.offset(), ts.offset() + t.length());
            }
        } else if (t.id() == HTMLTokenId.ARGUMENT) {
            int from = ts.offset();
            int to = from + t.text().length();
            //try to find the tag and check if there is a prefix
            while (ts.movePrevious()) {
                if (ts.token().id() == HTMLTokenId.TAG_OPEN) {
                    if (CharSequenceUtilities.indexOf(ts.token().text(), ':') != -1) {
                        return new OffsetRange(from, to);
                    } else {
                        break;
                    }
                }
            }
        } else if (t.id() == HTMLTokenId.VALUE) {
            CharSequence value = ts.token().text();
            int from = ts.offset();
            int to = from + t.text().length();
            //try to find the tag and check if there is a prefix
            while (ts.movePrevious()) {
                if (ts.token().id() == HTMLTokenId.TAG_OPEN) {
                    if (CharSequenceUtilities.indexOf(ts.token().text(), "include") != -1) {
                        if (CharSequenceUtilities.indexOf(value, "'") != -1 || CharSequenceUtilities.indexOf(value, "\"") != -1) {
                            from++; to--;
                        }
                        return new OffsetRange(from, to);
                    }
                    break;
                }
            }
        }

        return OffsetRange.NONE;
    }

    @Override
    public void computeErrors(HintsManager manager, RuleContext context, List<Hint> hints, List<Error> unhandled) {
        //just delegate to the hints registry and add all gathered results
        hints.addAll(HintsRegistry.getDefault().gatherHints(context));
    }

    @Override
    public void computeSelectionHints(HintsManager manager, RuleContext context, List<Hint> hints, int start, int end) {
        //inject composite component support
        Hint injectCC = InjectCompositeComponent.getHint(context, start, end);
        if (injectCC != null) {
            hints.add(injectCC);
        }
    }

    private static class FilenameSupport extends FileReferenceCompletion<HtmlCompletionItem> {

        @Override
        public HtmlCompletionItem createFileItem(FileObject file, int anchor) {
            return HtmlCompletionItem.createFileCompletionItem(file, anchor);
        }

        @Override
        public HtmlCompletionItem createGoUpItem(int anchor, Color color, ImageIcon icon) {
            return HtmlCompletionItem.createGoUpFileCompletionItem(anchor, color, icon); // NOI18N
        }
    }
}
