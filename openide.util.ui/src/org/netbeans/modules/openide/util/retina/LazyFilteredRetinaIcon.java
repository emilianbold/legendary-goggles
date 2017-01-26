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

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import javax.swing.ImageIcon;

public class LazyFilteredRetinaIcon extends AbstractRetinaIcon {

    private final Image plain;
    private final Image retina;
    private final int width;
    private final int height;
    private final RGBImageFilter filter;

    private Image lazyRetina = null;
    private Image lazyPlain = null;

    LazyFilteredRetinaIcon(Image plain, Image retina, int width, int height, RGBImageFilter filter) {
        this.plain = plain;
        this.retina = retina;
        this.width = width;
        this.height = height;
        this.filter = filter;
    }

    private Image filter(Image img) {
        Image changed = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(img.getSource(), filter));
        //creating this temporary ImageIcon just so we wait for the image to load. Alternatively we could use MediaTracker, etc.
        return new ImageIcon(changed).getImage();
    }

    @Override
    protected Image getRetinaImage() {
        if (lazyRetina == null) {
            lazyRetina = filter(retina);
        }
        return lazyRetina;
    }

    @Override
    protected Image getPlainImage() {
        if (lazyPlain == null) {
            lazyPlain = filter(plain);
        }
        return lazyPlain;
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }
}
