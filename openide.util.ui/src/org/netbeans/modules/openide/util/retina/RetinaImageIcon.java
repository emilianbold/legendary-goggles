/*
 * Copyright 2016, 2017 Emilian Marius Bold
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.netbeans.modules.openide.util.retina;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.RGBImageFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

public class RetinaImageIcon extends ImageIcon implements Lookup.Provider {

    private final Lookup lookup;
    private final AbstractRetinaIcon painter;

    public RetinaImageIcon(final Image plain, final Image retina) {
        super(new RetinaMultiResolutionImage(plain, retina));

        this.lookup = Lookups.singleton(new FilterableIcon() {
            @Override
            public Icon lazyIcon(final RGBImageFilter filter) {
                return new LazyFilteredRetinaIcon(plain, retina, getIconWidth(), getIconHeight(), filter);
            }
        });
        this.painter = new RetinaIcon(plain, retina, getIconWidth(), getIconHeight());
    }


    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        painter.paintIcon(c, g, x, y);
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

}
