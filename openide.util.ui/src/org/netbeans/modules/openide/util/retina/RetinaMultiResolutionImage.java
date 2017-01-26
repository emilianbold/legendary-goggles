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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.Arrays;
import java.util.List;
import sun.awt.image.MultiResolutionImage;

//The menu item on macOS will use AquaIcon.getImageForIcon which creates a sun.lwawt.macosx.CImage. CImage needs MultiResolutionImage for retina images
public class RetinaMultiResolutionImage extends Image implements MultiResolutionImage {

    private final Image plain;
    private final Image retina;

    RetinaMultiResolutionImage(Image plain, Image retina) {
        this.plain = plain;
        this.retina = retina;
    }

    @Override
    public int getWidth(ImageObserver observer) {
        return plain.getWidth(observer);
    }

    @Override
    public int getHeight(ImageObserver observer) {
        return plain.getHeight(observer);
    }

    @Override
    public ImageProducer getSource() {
        return plain.getSource();
    }

    @Override
    public Graphics getGraphics() {
        return plain.getGraphics();
    }

    @Override
    public Object getProperty(String name, ImageObserver observer) {
        return plain.getProperty(name, observer);
    }

    @Override
    public Image getResolutionVariant(int width, int height) {
        if (width == getWidth(null) && height == getHeight(null)) {
            return plain;
        } else {
            return retina;
        }
    }

    @Override
    public List<Image> getResolutionVariants() {
        return Arrays.asList(plain, retina);
    }

}
