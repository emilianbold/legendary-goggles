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

package org.netbeans.modules.java.api.common;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.modules.java.api.common.impl.RootsAccessor;
import org.netbeans.spi.project.support.ant.SourcesHelper;
import org.openide.util.Parameters;

/**
 * Represents a list of root properties used by {@link org.netbeans.modules.java.api.common.queries.QuerySupport#createSources}
 * @since 1.21
 * @author Tomas Zezula
 */
public abstract class Roots {

    private final PropertyChangeSupport support;
    private final boolean isSourceRoot;
    private final boolean supportIncludes;
    private final String type;
    private final String hint;

    static {
        RootsAccessor.setInstance(new MyAccessor());
    }

    Roots (final boolean isSourceRoot,
           final boolean supportIncludes,
           final @NullAllowed String type,
           final @NullAllowed String hint) {
        this.isSourceRoot = isSourceRoot;
        this.supportIncludes = supportIncludes;
        this.type = type;
        this.hint = hint;
        this.support = new PropertyChangeSupport(this);
    }

    /**
     * Returns root's display names
     * @return an array of String
     */
    public abstract @NonNull String[] getRootDisplayNames();

    /**
     * Returns names of Ant properties in the <i>project.properties</i> file holding the roots.
     * @return an array of String.
     */
    public abstract @NonNull String[] getRootProperties();

    /**
     * Adds {@link PropertyChangeListener}, see class description for more information
     * about listening to the source roots changes.
     * @param listener a listener to add.
     */
    public final void addPropertyChangeListener(final @NonNull PropertyChangeListener listener) {
        Parameters.notNull("listener", listener);   //NOI18N
        this.support.addPropertyChangeListener(listener);
    }

    /**
     * Removes {@link PropertyChangeListener}, see class description for more information
     * about listening to the source roots changes.
     * @param listener a listener to remove.
     */
    public final void removePropertyChangeListener(final @NonNull PropertyChangeListener listener) {
        Parameters.notNull("listener", listener);   //NOI18N
        this.support.removePropertyChangeListener(listener);
    }

    final void firePropertyChange(final @NonNull String propName, final @NullAllowed Object oldValue, final @NonNull Object newValue) {
        Parameters.notNull("propName", propName);   //NOI18N
        this.support.firePropertyChange(propName, oldValue, newValue);
    }

    /**
     * Creates roots list which should be registered as non source roots.
     * @see SourcesHelper#addNonSourceRoot(java.lang.String) for details
     * @param rootPropNames Ant properties in the <i>project.properties</i> file holding the roots
     * @return the Roots
     */
    public static Roots nonSourceRoots(final @NonNull String... rootPropNames) {
        Parameters.notNull("rootPropNames", rootPropNames); //NOI18N
        return new NonSourceRoots(rootPropNames);
    }

    /**
     * Creates a source roots list which should be registered as principal and
     * possibly typed roots.
     * @see SourcesHelper for details.
     * @param properties Ant properties in the <i>project.properties</i> file holding the roots
     * @param displayNames the display names of the roots
     * @param supportIncludes when true the roots list supports includes/excludes
     * @param type of the roots, when null the roots are registered as principal roots only
     * @param hint optional hint for {@link SourceGroupModifier}
     * @return the Roots
     */
    public static Roots propertyBased(
            final @NonNull String[] properties,
            final @NonNull String[] displayNames,
            final boolean supportIncludes,
            final @NullAllowed String type,
            final @NullAllowed String hint) {
        Parameters.notNull("properties", properties);
        Parameters.notNull("displayNames", displayNames);
        if (properties.length != displayNames.length) {
            throw new IllegalArgumentException();
        }
        return new PropSourceRoots(properties, displayNames, supportIncludes, type, hint);
    }

    private static class NonSourceRoots extends Roots {

        private final Set<String> rootPropNames;


        private NonSourceRoots(final String... rootPropNames) {
            super(false,false, null, null);
            this.rootPropNames = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(rootPropNames)));
        }

        @Override
        public String[] getRootDisplayNames() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String[] getRootProperties() {
            return rootPropNames.toArray(new String[rootPropNames.size()]);
        }
    }

    private static class PropSourceRoots extends Roots {

        private final String[] props;
        private final String[] names;

        private PropSourceRoots (final String[] props, final String[] names,
                final boolean supportIncludes, final String type, final String hint) {
            super (true,supportIncludes,type,hint);
            this.props = Arrays.copyOf(props, props.length);
            this.names = Arrays.copyOf(names, names.length);
        }

        @Override
        public String[] getRootDisplayNames() {
            return Arrays.copyOf(names, names.length);
        }

        @Override
        public String[] getRootProperties() {
            return Arrays.copyOf(props, props.length);
        }
    }

    private static class MyAccessor extends RootsAccessor {
        @Override
        public boolean isSourceRoot(final @NonNull Roots roots) {
            return roots.isSourceRoot;
        }

        @Override
        public boolean supportIncludes(final @NonNull Roots roots) {
            return roots.supportIncludes;
        }

        @Override
        public String getHint(final @NonNull Roots roots) {
            return roots.hint;
        }

        @Override
        public String getType(final @NonNull Roots roots) {
            return roots.type;
        }
    }

}
