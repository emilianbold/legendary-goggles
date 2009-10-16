/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.api.visual.vmd;

import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.widget.Widget;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

/**
 * This class represents a node anchor used in VMD visualization style. The anchor could be assign by multiple connection widgets.
 * For each usage the anchor resolves a different position.
 * The positions are resolved at the top and the bottom of the widget where the anchor is attached to.
 *
 * @author David Kaspar
 */
public class VMDNodeAnchor extends Anchor {

    private boolean requiresRecalculation = true;

    private HashMap<Entry, Result> results = new HashMap<Entry, Result> ();
    private final boolean vertical;
    private VMDColorScheme scheme;

    /**
     * Creates a node anchor with vertical direction.
     * @param widget the node widget where the anchor is attached to
     */
    public VMDNodeAnchor (Widget widget) {
        this (widget, true);
    }

    /**
     * Creates a node anchor.
     * @param widget the node widget where the anchor is attached to
     * @param vertical if true, then anchors are placed vertically; if false, then anchors are placed horizontally
     */
    public VMDNodeAnchor (Widget widget, boolean vertical) {
        this (widget, vertical, VMDFactory.getOriginalScheme ());
    }

    /**
     * Creates a node anchor.
     * @param widget the node widget where the anchor is attached to
     * @param vertical if true, then anchors are placed vertically; if false, then anchors are placed horizontally
     * @param scheme color scheme
     * @since 2.5
     */
    public VMDNodeAnchor (Widget widget, boolean vertical, VMDColorScheme scheme) {
        super (widget);
        assert widget != null;
        assert scheme != null;
        this.vertical = vertical;
        this.scheme = scheme;
    }

    /**
     * Notifies when an entry is registered
     * @param entry the registered entry
     */
    protected void notifyEntryAdded (Entry entry) {
        requiresRecalculation = true;
    }

    /**
     * Notifies when an entry is unregistered
     * @param entry the unregistered entry
     */
    protected void notifyEntryRemoved (Entry entry) {
        results.remove (entry);
        requiresRecalculation = true;
    }

    /**
     * Notifies when the anchor is going to be revalidated.
     * @since 2.8
     */
    protected void notifyRevalidate () {
        requiresRecalculation = true;
    }

    private void recalculate () {
        if (! requiresRecalculation)
            return;

        Widget widget = getRelatedWidget ();
        Point relatedLocation = getRelatedSceneLocation ();

        Rectangle bounds = widget.convertLocalToScene (widget.getBounds ());

        HashMap<Entry, Float> topmap = new HashMap<Entry, Float> ();
        HashMap<Entry, Float> bottommap = new HashMap<Entry, Float> ();

        for (Entry entry : getEntries ()) {
            Point oppositeLocation = getOppositeSceneLocation (entry);
            if (oppositeLocation == null  ||  relatedLocation == null) {
                results.put (entry, new Result (new Point (bounds.x, bounds.y), DIRECTION_ANY));
                continue;
            }

            int dy = oppositeLocation.y - relatedLocation.y;
            int dx = oppositeLocation.x - relatedLocation.x;

            if (vertical) {
                if (dy > 0)
                    bottommap.put (entry, (float) dx / (float) dy);
                else if (dy < 0)
                    topmap.put (entry, (float) - dx / (float) dy);
                else
                    topmap.put (entry, dx < 0 ? Float.MAX_VALUE : Float.MIN_VALUE);
            } else {
                if (dx > 0)
                    bottommap.put (entry, (float) dy / (float) dx);
                else if (dy < 0)
                    topmap.put (entry, (float) - dy / (float) dx);
                else
                    topmap.put (entry, dy < 0 ? Float.MAX_VALUE : Float.MIN_VALUE);
            }
        }

        Entry[] topList = toArray (topmap);
        Entry[] bottomList = toArray (bottommap);

        int pinGap = scheme.getNodeAnchorGap (this);
        int y = bounds.y - pinGap;
        int x = bounds.x - pinGap;
        int len = topList.length;

        for (int a = 0; a < len; a ++) {
            Entry entry = topList[a];
            if (vertical)
                x = bounds.x + (a + 1) * bounds.width / (len + 1);
            else
                y = bounds.y + (a + 1) * bounds.height / (len + 1);
            results.put (entry, new Result (new Point (x, y), vertical ? Direction.TOP : Direction.LEFT));
        }

        y = bounds.y + bounds.height + pinGap;
        x = bounds.x + bounds.width + pinGap;
        len = bottomList.length;

        for (int a = 0; a < len; a ++) {
            Entry entry = bottomList[a];
            if (vertical)
                x = bounds.x + (a + 1) * bounds.width / (len + 1);
            else
                y = bounds.y + (a + 1) * bounds.height / (len + 1);
            results.put (entry, new Result (new Point (x, y), vertical ? Direction.BOTTOM : Direction.RIGHT));
        }

        requiresRecalculation = false;
    }

    private Entry[] toArray (final HashMap<Entry, Float> map) {
        Set<Entry> keys = map.keySet ();
        Entry[] entries = keys.toArray (new Entry[keys.size ()]);
        Arrays.sort (entries, new Comparator<Entry>() {
            public int compare (Entry o1, Entry o2) {
                float f = map.get (o1) - map.get (o2);
                if (f > 0.0f)
                    return 1;
                else if (f < 0.0f)
                    return -1;
                else
                    return 0;
            }
        });
        return entries;
    }

    /**
     * Computes a result (position and direction) for a specific entry.
     * @param entry the entry
     * @return the calculated result
     */
    public Result compute (Entry entry) {
        recalculate ();
        return results.get (entry);
    }

}
