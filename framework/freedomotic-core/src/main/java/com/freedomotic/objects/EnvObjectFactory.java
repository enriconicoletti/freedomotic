/**
 *
 * Copyright (c) 2009-2013 Freedomotic team http://freedomotic.com
 *
 * This file is part of Freedomotic
 *
 * This Program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * This Program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Freedomotic; see the file COPYING. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.freedomotic.objects;

import com.freedomotic.app.Freedomotic;
import com.freedomotic.exceptions.DaoLayerException;
import com.freedomotic.model.geometry.FreedomPolygon;
import com.freedomotic.model.object.Behavior;
import com.freedomotic.model.object.BooleanBehavior;
import com.freedomotic.model.object.EnvObject;
import com.freedomotic.model.object.Representation;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Enrico
 */
public final class EnvObjectFactory {

    private EnvObjectFactory() {
        // Suppress default constructor for noninstantiability
        throw new AssertionError();
    }

    /**
     * Instantiate the right logic manager for an object pojo using the pojo
     * "type" field
     *
     * @param pojo
     * @return
     * @throws com.freedomotic.exceptions.DaoLayerException
     */
    public static EnvObjectLogic create(EnvObject pojo)
            throws DaoLayerException {
        if (pojo == null) {
            throw new IllegalArgumentException("Cannot create an object logic from null object data");
        }

        try {
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class<?> clazz = classLoader.loadClass(pojo.getHierarchy()); //eg: com.freedomotic.objects.impl.ElectricDevice

            EnvObjectLogic logic = (EnvObjectLogic) Freedomotic.INJECTOR.getInstance(clazz);
            logic.setPojo(pojo);

            return logic;
        } catch (ClassNotFoundException ex) {
            throw new DaoLayerException("Class '" + pojo.getHierarchy() + "' not found. "
                    + "The related object plugin is not "
                    + "loaded succesfully or you have a wrong hierarchy "
                    + "value in your XML definition of the object."
                    + "The hierarchy value is composed by the package name plus the java file name "
                    + "like com.freedomotic.objects.impl.Light not com.freedomotic.objects.impl.ElectricDevice.Light");
        }
    }

    /**
     * Creates an empty and very generic object. This object however is fully
     * initialized to avoid NullPointerExceptions on mandatory fields. Fields
     * like 'name' can be customized after receiving the initialized instance.
     *
     * @return A generic object which can be customized later
     * @throws com.freedomotic.exceptions.DaoLayerException
     */
    public static EnvObjectLogic createDummy() throws DaoLayerException {
        EnvObject data = new EnvObject();
        data.setName("A basic EnvObject");
        data.setPhisicalAddress("unknown");
        data.setProtocol("unknown");
        data.setType("EnvObject.ElectricDevice");
        //this fields are mandatory to avoid NullPointerExceptions
        data.setHierarchy("com.freedomotic.objects.impl.ElectricDevice");
        Representation rep = new Representation();
        rep.setShape(new FreedomPolygon());
        List<Representation> reps = new ArrayList<Representation>();
        reps.add(rep);
        data.setRepresentations(reps);
        data.setCurrentRepresentation(0);
        BooleanBehavior powered = new BooleanBehavior();
        powered.setName("powered");
        List<Behavior> behaviors = new ArrayList<Behavior>();
        behaviors.add(powered);
        data.setBehaviors(behaviors);
        data.setEnvironmentID("df28cda0-a866-11e2-9e96-0800200c9a66");
        EnvObjectLogic logic = create(data);
        return logic;
    }
}
