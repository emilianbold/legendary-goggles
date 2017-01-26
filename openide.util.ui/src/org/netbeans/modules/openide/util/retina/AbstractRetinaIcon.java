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

import org.openide.util.RetinaToolkit;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.Icon;

abstract class AbstractRetinaIcon implements Icon {

    protected abstract Image getRetinaImage();

    protected abstract Image getPlainImage();

    private boolean paintRetinaImage(Component c, Graphics g, int x, int y) {
        Image retina = getRetinaImage();
        if (retina == null) {
            return false;
        }
        final Graphics2D g2 = (Graphics2D) g.create(x, y, getIconWidth(), getIconHeight());
        g2.scale(0.5, 0.5);
        g2.drawImage(retina, 0, 0, c);
        g2.dispose();
        return true;
    }

    private boolean paintPlainImage(Component c, Graphics g, int x, int y) {
        Image img = getPlainImage();
        if (img == null) {
            return false;
        }
        g.drawImage(img, x, y, c);
        return true;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (RetinaToolkit.isRetina(g)) {
            if (!paintRetinaImage(c, g, x, y)) {
                paintPlainImage(c, g, x, y);
            }
        } else {
            //non retina screen
            if (!paintPlainImage(c, g, x, y)) {
                paintRetinaImage(c, g, x, y);
            }
        }
    }

}
