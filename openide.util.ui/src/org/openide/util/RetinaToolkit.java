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
package org.openide.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.lang.reflect.Field;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.modules.openide.util.retina.RetinaIcon;
import org.netbeans.modules.openide.util.retina.RetinaImageIcon;

/**
 * Load Retina icons or query the environment about Retina capabilities.
 */
public final class RetinaToolkit {

    private final static RetinaToolkit INSTANCE = new RetinaToolkit();

    public static RetinaToolkit getDefault() {
        return INSTANCE;
    }

    private RetinaToolkit() {
        //nothing
    }

    /**
     * @return true if this machine has any retina display
     */
    public static boolean isRetina() {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();

        for (GraphicsDevice screen : environment.getScreenDevices()) {
            if (isRetina(screen)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param device
     * @return true if the device is a retina device
     */
    public static boolean isRetina(GraphicsDevice device) {
        try {
            Field field = device.getClass().getDeclaredField("scale"); //NOI18N
            field.setAccessible(true);
            Object scale = field.get(device);
            if (scale instanceof Integer && (Integer) scale == 2) {
                return true;
            }
        } catch (IllegalAccessException iea) {
        } catch (IllegalArgumentException iae2) {
        } catch (NoSuchFieldException nsfe) {
        } catch (SecurityException e) {
        }
        return false;
    }

    /**
     *
     * @param g a valid {@link Graphics} instance
     * @return true if the device associated with the {@link Graphics} argument
     * is retina
     */
    public static boolean isRetina(Graphics g) {
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;

            GraphicsDevice device = g2.getDeviceConfiguration().getDevice();

            return isRetina(device);
        }
        return false;
    }


    /**
     * Create retina icon using a specific images
     *
     * @param plain plain image
     * @param retina the retina @2x image
     * @param width plain image width
     * @param height plain image height
     * @return a retina icon
     */
    public Icon createIcon(final Image plain, final Image retina, final int width, final int height) {
        return new RetinaIcon(plain, retina, width, height);
    }

    public ImageIcon createImageIcon(final Image plain, final Image retina) {
        return new RetinaImageIcon(plain, retina);
    }

}
