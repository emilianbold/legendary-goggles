#Signature file v4.1
#Version 1.22.0

CLSS public abstract interface !annotation java.lang.Deprecated
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
intf java.lang.annotation.Annotation

CLSS public java.lang.Object
cons public init()
meth protected java.lang.Object clone() throws java.lang.CloneNotSupportedException
meth protected void finalize() throws java.lang.Throwable
meth public boolean equals(java.lang.Object)
meth public final java.lang.Class<?> getClass()
meth public final void notify()
meth public final void notifyAll()
meth public final void wait() throws java.lang.InterruptedException
meth public final void wait(long) throws java.lang.InterruptedException
meth public final void wait(long,int) throws java.lang.InterruptedException
meth public int hashCode()
meth public java.lang.String toString()

CLSS public abstract interface java.lang.annotation.Annotation
meth public abstract boolean equals(java.lang.Object)
meth public abstract int hashCode()
meth public abstract java.lang.Class<? extends java.lang.annotation.Annotation> annotationType()
meth public abstract java.lang.String toString()

CLSS public abstract interface !annotation java.lang.annotation.Documented
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[ANNOTATION_TYPE])
intf java.lang.annotation.Annotation

CLSS public abstract interface !annotation java.lang.annotation.Retention
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[ANNOTATION_TYPE])
intf java.lang.annotation.Annotation
meth public abstract java.lang.annotation.RetentionPolicy value()

CLSS public abstract interface !annotation java.lang.annotation.Target
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[ANNOTATION_TYPE])
intf java.lang.annotation.Annotation
meth public abstract java.lang.annotation.ElementType[] value()

CLSS public final org.netbeans.api.java.source.ui.DialogBinding
 anno 0 java.lang.Deprecated()
meth public static org.netbeans.api.java.source.JavaSource bindComponentToFile(org.openide.filesystems.FileObject,int,int,javax.swing.text.JTextComponent)
 anno 0 java.lang.Deprecated()
supr java.lang.Object

CLSS public final org.netbeans.api.java.source.ui.ElementHeaders
fld public final static java.lang.String ANNOTATIONS = "%annotations%"
fld public final static java.lang.String EXTENDS = "%extends%"
fld public final static java.lang.String FLAGS = "%flags%"
fld public final static java.lang.String IMPLEMENTS = "%implements%"
fld public final static java.lang.String NAME = "%name%"
fld public final static java.lang.String PARAMETERS = "%parameters%"
fld public final static java.lang.String THROWS = "%throws%"
fld public final static java.lang.String TYPE = "%type%"
fld public final static java.lang.String TYPEPARAMETERS = "%typeparameters%"
meth public static int getDistance(java.lang.String,java.lang.String)
meth public static java.lang.String getHeader(com.sun.source.util.TreePath,org.netbeans.api.java.source.CompilationInfo,java.lang.String)
meth public static java.lang.String getHeader(javax.lang.model.element.Element,org.netbeans.api.java.source.CompilationInfo,java.lang.String)
supr java.lang.Object

CLSS public org.netbeans.api.java.source.ui.ElementIcons
meth public static javax.swing.Icon getElementIcon(javax.lang.model.element.ElementKind,java.util.Collection<javax.lang.model.element.Modifier>)
supr java.lang.Object

CLSS public org.netbeans.api.java.source.ui.ElementJavadoc
meth public final static org.netbeans.api.java.source.ui.ElementJavadoc create(org.netbeans.api.java.source.CompilationInfo,javax.lang.model.element.Element)
meth public final static org.netbeans.api.java.source.ui.ElementJavadoc create(org.netbeans.api.java.source.CompilationInfo,javax.lang.model.element.Element,java.util.concurrent.Callable<java.lang.Boolean>)
meth public java.lang.String getText()
meth public java.net.URL getURL()
meth public java.util.concurrent.Future<java.lang.String> getTextAsync()
meth public javax.swing.Action getGotoSourceAction()
meth public org.netbeans.api.java.source.ui.ElementJavadoc resolveLink(java.lang.String)
supr java.lang.Object
hfds API,CODE_TAG,DEPRECATED_TAG,INHERIT_DOC_TAG,LANGS,LINKPLAIN_TAG,LITERAL_TAG,PARAM_TAG,RETURN_TAG,RP,SEE_TAG,SINCE_TAG,THROWS_TAG,VALUE_TAG,content,cpInfo,docURL,goToSource,linkCounter,links
hcls Now,RemoteJavadocException

CLSS public final org.netbeans.api.java.source.ui.ElementOpen
meth public static boolean open(org.netbeans.api.java.source.ClasspathInfo,javax.lang.model.element.Element)
meth public static boolean open(org.netbeans.api.java.source.ClasspathInfo,org.netbeans.api.java.source.ElementHandle<? extends javax.lang.model.element.Element>)
meth public static boolean open(org.openide.filesystems.FileObject,org.netbeans.api.java.source.ElementHandle<? extends javax.lang.model.element.Element>)
supr java.lang.Object
hfds AWT_TIMEOUT,NON_AWT_TIMEOUT,log
hcls FindDeclarationVisitor

CLSS public org.netbeans.api.java.source.ui.ScanDialog
meth public static boolean runWhenScanFinished(java.lang.Runnable,java.lang.String)
supr java.lang.Object

CLSS public final org.netbeans.api.java.source.ui.TypeElementFinder
cons public init()
innr public abstract interface static Customizer
meth public static org.netbeans.api.java.source.ElementHandle<javax.lang.model.element.TypeElement> find(org.netbeans.api.java.source.ClasspathInfo,org.netbeans.api.java.source.ui.TypeElementFinder$Customizer)
supr java.lang.Object

CLSS public abstract interface static org.netbeans.api.java.source.ui.TypeElementFinder$Customizer
 outer org.netbeans.api.java.source.ui.TypeElementFinder
meth public abstract boolean accept(org.netbeans.api.java.source.ElementHandle<javax.lang.model.element.TypeElement>)
meth public abstract java.util.Set<org.netbeans.api.java.source.ElementHandle<javax.lang.model.element.TypeElement>> query(org.netbeans.api.java.source.ClasspathInfo,java.lang.String,org.netbeans.api.java.source.ClassIndex$NameKind,java.util.Set<org.netbeans.api.java.source.ClassIndex$SearchScope>)

