/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.model.jclank.bridge;

import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clang.basic.Diagnostic;
import org.clang.basic.DiagnosticIDs;
import org.clang.basic.DiagnosticOptions;
import org.clang.basic.DiagnosticsEngine;
import org.clang.basic.FileManager;
import org.clang.basic.FileSystemOptions;
import org.clang.basic.LangOptions;
import org.clang.basic.Module;
import org.clang.basic.PresumedLoc;
import org.clang.basic.SourceLocation;
import org.clang.basic.SourceManager;
import org.clang.basic.target.TargetInfo;
import org.clang.basic.target.TargetOptions;
import org.clang.basic.tok;
import org.clang.frontend.ClangGlobals;
import org.clang.frontend.CompilerInvocation;
import org.clang.frontend.FrontendOptions;
import org.clang.frontend.InputKind;
import org.clang.frontend.LangStandard;
import org.clang.frontend.PreprocessorOutputOptions;
import org.clang.lex.HeaderSearch;
import org.clang.lex.HeaderSearchOptions;
import org.clang.lex.ModuleIdPath;
import org.clang.lex.ModuleLoadResult;
import org.clang.lex.ModuleLoader;
import org.clang.lex.Preprocessor;
import org.clang.lex.PreprocessorOptions;
import org.clang.lex.Token;
import org.clang.lex.frontend;
import org.clank.java.std;
import org.clank.support.Converted;
import org.clank.support.Destructors;
import org.clank.support.Native;
import static org.clank.support.NativePointer.*;
import org.llvm.adt.IntrusiveRefCntPtr;
import org.llvm.adt.StringRef;
import org.llvm.adt.aliases.SmallVectorChar;
import org.llvm.support.MemoryBuffer;
import org.llvm.support.llvm;
import org.netbeans.modules.cnd.api.project.NativeFileItem;
import org.netbeans.modules.cnd.apt.support.APTTokenStream;
import org.netbeans.modules.cnd.utils.FSPath;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Vladimir Voskresensky
 */
public class CsmJClankSerivices {

    public static APTTokenStream getAPTTokenStream(NativeFileItem nfi) {
        throw new UnsupportedOperationException();
    }

    public static void dumpTokens(NativeFileItem nfi) {
        Preprocessor /*&*/ PP = getPreprocessor(nfi);
        // Start preprocessing the specified input file.
        Token Tok/*J*/ = new Token();
        PP.EnterMainSourceFile();
        do {
            PP.Lex(Tok);
            PP.DumpToken(Tok, true);
            llvm.errs().$out($("\n"));
        } while (Tok.isNot(tok.TokenKind.eof));
    }

    public static void dumpPreprocessed(NativeFileItem nfi) {
        PreprocessorOutputOptions Opts = createPPOptions(nfi);
        Preprocessor /*&*/ PP = getPreprocessor(nfi);
        
        String Source = "#include \"" + nfi.getAbsolutePath() + "\"\0";
        byte[] bytes = Source.getBytes(Charset.forName("UTF-8"));
        StringRef stringRef = new StringRef(bytes, bytes.length-1);
        MemoryBuffer /*P*/ buf = MemoryBuffer.getMemBuffer(stringRef);
        SourceManager SourceMgr = PP.getSourceManager();
        SourceMgr.setMainFileID(SourceMgr.createFileID(buf));
        
        ClangGlobals.DoPrintPreprocessedInput(PP, llvm.outs(), Opts);
    }

    private static Preprocessor getPreprocessor(NativeFileItem nfi) {
        PreprocessorInitializer initializer = new AdvancedPreprocessorInitializer(nfi);
        VoidModuleLoader ModLoader/*J*/ = new VoidModuleLoader();
        Preprocessor PP/*J*/ = initializer.createPreprocessor(ModLoader);
        return PP;
    }

    private static PreprocessorOutputOptions createPPOptions(NativeFileItem nfi) {
        PreprocessorOutputOptions opts = new PreprocessorOutputOptions();
        opts.ShowCPP = true;
        opts.ShowComments = true;
        opts.ShowLineMarkers = true;
        opts.ShowMacros = true;
        opts.ShowMacroComments = true;
        return opts;
    }

    private static interface PreprocessorInitializer {
        Preprocessor createPreprocessor(ModuleLoader modLoader);
    }

    private static class DefaultPreprocessorInitializer implements PreprocessorInitializer {

        protected final FileSystemOptions FileMgrOpts;
        protected final FileManager FileMgr;
        protected final IntrusiveRefCntPtr<DiagnosticIDs> DiagID;
        protected final DiagnosticsEngine Diags;
        protected final SourceManager SourceMgr;
        protected final LangOptions LangOpts;
        protected final IntrusiveRefCntPtr<TargetOptions> TargetOpts;
        protected final IntrusiveRefCntPtr<TargetInfo> Target;

        public DefaultPreprocessorInitializer() {
            this.FileMgrOpts = new FileSystemOptions();
            this.FileMgr = new FileManager(FileMgrOpts);
            this.DiagID = new IntrusiveRefCntPtr<>(new DiagnosticIDs());
            this.Diags = new DiagnosticsEngine(DiagID, new DiagnosticOptions(), new TestDiagnosticConsumer());
            this.SourceMgr = new SourceManager(Diags, FileMgr);
            this.LangOpts = new LangOptions();
            this.TargetOpts = new IntrusiveRefCntPtr<>(new TargetOptions());
            this.Target = new IntrusiveRefCntPtr<>();
            //END JInit
        }

        @Override
        public Preprocessor createPreprocessor(ModuleLoader ModLoader) {
            PreprocessorOptions PPOpts = createPreprocessorOptions();
            HeaderSearchOptions HSOpts = createHeaderSearchOptions();
            HeaderSearch HS = new HeaderSearch(new IntrusiveRefCntPtr<>(HSOpts), SourceMgr, Diags, LangOpts, Target.getPtr());
            ClangGlobals.ApplyHeaderSearchOptions(HS, HSOpts, LangOpts, Target.getPtr().getTriple());
            Preprocessor PP = new Preprocessor(new IntrusiveRefCntPtr<>(PPOpts), Diags, LangOpts, Target.getPtr(), SourceMgr, HS, ModLoader, null, false, false);
            ClangGlobals.InitializePreprocessor(PP, PPOpts, HSOpts, new FrontendOptions());
            return PP;
        }

        protected HeaderSearchOptions createHeaderSearchOptions() {
            HeaderSearchOptions HSOpts = new HeaderSearchOptions();
            HSOpts.UseBuiltinIncludes = false;
            HSOpts.UseLibcxx = false;
            HSOpts.UseStandardCXXIncludes = false;
            HSOpts.UseStandardSystemIncludes = false;
            
            if (false) {
                HSOpts.Verbose = true;
            }
            return HSOpts;
        }

        protected PreprocessorOptions createPreprocessorOptions() {
            PreprocessorOptions out = new PreprocessorOptions();
            out.UsePredefines = false;
            return out;
        }
    }

    private static class AdvancedPreprocessorInitializer extends DefaultPreprocessorInitializer {
        private final NativeFileItem startEntry;

        public AdvancedPreprocessorInitializer(NativeFileItem startEntry) {
            super();
            this.startEntry = startEntry;
            setLangDefaults(this.LangOpts, startEntry);
            this.TargetOpts.$arrow().Triple.$assign("x86_64");
            this.Target.$assign(new IntrusiveRefCntPtr<>(TargetInfo.CreateTargetInfo(Diags, /*AddrOf*/ TargetOpts.$star())));
        }
        
        @Override
        protected HeaderSearchOptions createHeaderSearchOptions() {
            HeaderSearchOptions options = super.createHeaderSearchOptions();
            // -isystem
            for (FSPath fSPath : startEntry.getSystemIncludePaths()) {
                FileObject fileObject = fSPath.getFileObject();
                if (fileObject != null && fileObject.isFolder()) {
                    options.AddSystemHeaderPrefix(new StringRef(fSPath.getPath()), true);
                }                
            }
            // -I
            for (FSPath fSPath : startEntry.getSystemIncludePaths()) {
                FileObject fileObject = fSPath.getFileObject();
                if (fileObject != null && fileObject.isFolder()) {
                    options.AddPath(new StringRef(fSPath.getPath()), frontend.IncludeDirGroup.Angled, false, true);
                }
            }
            return options;
        }

        @Override
        protected PreprocessorOptions createPreprocessorOptions() {
            PreprocessorOptions out = super.createPreprocessorOptions();
            // handle -include
            for (FSPath fSPath : startEntry.getUserIncludePaths()) {
                FileObject fileObject = fSPath.getFileObject();
                if (fileObject != null && fileObject.isData()) {
                    out.Includes.push_back(fSPath.getPath());
                }                
            }
            for (String macro : startEntry.getSystemMacroDefinitions()) {
                out.addMacroDef(new StringRef(macro));
            }
            for (String macro : startEntry.getUserMacroDefinitions()) {
                out.addMacroDef(new StringRef(macro));
            }
            if (false) {
                // remap unsaved files as memory buffers
                out.addRemappedFile(null, (MemoryBuffer) null);
            }
            return out;
        }                

    }

    private static void setLangDefaults(LangOptions LangOpts, NativeFileItem startEntry) {
        LangStandard.Kind lang_std = LangStandard.Kind.lang_unspecified;
        InputKind lang = InputKind.IK_None;
        switch (startEntry.getLanguage()) {
            case C:
            case C_HEADER:
                lang = InputKind.IK_C;
                break;
            case CPP:
                lang = InputKind.IK_CXX;
                break;
            case FORTRAN:
            case OTHER:
            default:
                throw new AssertionError(startEntry.getLanguage().name());
        }
        switch (startEntry.getLanguageFlavor()) {
            case DEFAULT:
            case UNKNOWN:
                break;
            case C:
                break;
            case C89:
                lang_std = LangStandard.Kind.lang_c89;
                break;
            case C99:
                lang_std = LangStandard.Kind.lang_c99;
                break;
            case CPP:
                lang_std = LangStandard.Kind.lang_cxx03;
                break;
            case CPP11:
                lang_std = LangStandard.Kind.lang_cxx11;
                break;
            case C11:
                lang_std = LangStandard.Kind.lang_c11;
                break;
            case CPP14:
                // FIXME
                lang_std = LangStandard.Kind.lang_cxx1y;
                break;
            case F77:
            case F90:
            case F95:
            default:
                throw new AssertionError(startEntry.getLanguageFlavor().name());
        }
        CompilerInvocation.setLangDefaults(LangOpts, lang, lang_std);
    }
    
    private static Level toLoggerLevel(DiagnosticsEngine.Level DiagLevel) {
        switch (DiagLevel) {
            case Ignored:
            case Note:
                return Level.INFO;
            case Warning:
                return Level.WARNING;
            case Error:
            case Fatal:
                return Level.SEVERE;
            default:
                throw new AssertionError(DiagLevel.name());
        }
    }
        
    private static class TestDiagnosticConsumer extends /*public*/ org.clang.basic.DiagnosticConsumer {

        @Override
        public void HandleDiagnostic(DiagnosticsEngine.Level DiagLevel, Diagnostic Info) {
            SmallVectorChar out = new SmallVectorChar(1024);
            Info.FormatDiagnostic(out);
            String Loc = "";
            if (Info.hasSourceManager()) {
                PresumedLoc presumedLoc = Info.getSourceManager().getPresumedLoc(Info.getLocation());
                if (presumedLoc.isValid()) {
                    Loc = "In file " + Native.$toString(presumedLoc.getFilename())
                            + " line " + presumedLoc.getLine()
                            + ", col " + presumedLoc.getColumn()
                            + "\n";
                }
            }
            Level level = toLoggerLevel(DiagLevel);
            Logger.getLogger(TestDiagnosticConsumer.class.getName()).log(level, "{2}{0}:{1}\n", new Object[]{DiagLevel, new std.string(out.data()).toJavaString(), Loc});
        }
    }

    //<editor-fold defaultstate="collapsed" desc="<anonymous namespace>::VoidModuleLoader">
    @Converted(kind = Converted.Kind.AUTO, source = "${LLVM_SRC}/llvm/tools/clang/unittests/Lex/LexerTest.cpp", line = 31,
            cmd = "jclank.sh ${LLVM_SRC}/llvm/tools/clang/unittests/Lex/LexerTest.cpp -filter=<anonymous namespace>::VoidModuleLoader")
//</editor-fold>
    public static class VoidModuleLoader extends /*public*/ ModuleLoader implements Destructors.ClassWithDestructor {

        //<editor-fold defaultstate="collapsed" desc="<anonymous namespace>::VoidModuleLoader::loadModule">
        @Converted(kind = Converted.Kind.AUTO, source = "${LLVM_SRC}/llvm/tools/clang/unittests/Lex/LexerTest.cpp", line = 32,
                cmd = "jclank.sh ${LLVM_SRC}/llvm/tools/clang/unittests/Lex/LexerTest.cpp -filter=<anonymous namespace>::VoidModuleLoader::loadModule")
        //</editor-fold>
        @Override
        public/*private*/ /*virtual*/ ModuleLoadResult loadModule(SourceLocation ImportLoc, ModuleIdPath Path, Module.NameVisibilityKind Visibility, boolean IsInclusionDirective) {
            return new ModuleLoadResult();
        }

        //<editor-fold defaultstate="collapsed" desc="<anonymous namespace>::VoidModuleLoader::makeModuleVisible">
        @Converted(kind = Converted.Kind.AUTO, source = "${LLVM_SRC}/llvm/tools/clang/unittests/Lex/LexerTest.cpp", line = 39,
                cmd = "jclank.sh ${LLVM_SRC}/llvm/tools/clang/unittests/Lex/LexerTest.cpp -filter=<anonymous namespace>::VoidModuleLoader::makeModuleVisible")
        //</editor-fold>
        @Override
        public/*private*/ /*virtual*/ void makeModuleVisible(Module /*P*/ Mod, Module.NameVisibilityKind Visibility, SourceLocation ImportLoc, boolean Complain) {
        }

        //<editor-fold defaultstate="collapsed" desc="<anonymous namespace>::VoidModuleLoader::~VoidModuleLoader">
        @Converted(kind = Converted.Kind.AUTO, source = "${LLVM_SRC}/llvm/tools/clang/unittests/Lex/LexerTest.cpp", line = 31,
                cmd = "jclank.sh ${LLVM_SRC}/llvm/tools/clang/unittests/Lex/LexerTest.cpp -filter=<anonymous namespace>::VoidModuleLoader::~VoidModuleLoader")
        //</editor-fold>
        @Override
        public /*inline*/ void $destroy() {
            super.$destroy();
        }

        //<editor-fold defaultstate="collapsed" desc="<anonymous namespace>::VoidModuleLoader::VoidModuleLoader">
        @Converted(kind = Converted.Kind.AUTO, source = "${LLVM_SRC}/llvm/tools/clang/unittests/Lex/LexerTest.cpp", line = 31,
                cmd = "jclank.sh ${LLVM_SRC}/llvm/tools/clang/unittests/Lex/LexerTest.cpp -filter=<anonymous namespace>::VoidModuleLoader::VoidModuleLoader")
        //</editor-fold>
        public /*inline*/ VoidModuleLoader() {
            /* : ModuleLoader()*/
            //START JInit
            super();
            //END JInit
        }

    }
}
