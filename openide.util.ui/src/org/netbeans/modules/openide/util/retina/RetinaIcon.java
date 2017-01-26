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

public class RetinaIcon extends AbstractRetinaIcon {

    private final Image plain;
    private final Image retina;
    private final int width;
    private final int height;

    public RetinaIcon(Image plain, Image retina, int width, int height) {
        this.plain = plain;
        this.retina = retina;
        this.width = width;
        this.height = height;
    }

    @Override
    protected Image getRetinaImage() {
        return retina;
    }

    @Override
    protected Image getPlainImage() {
        return plain;
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
