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

import com.freedomotic.app.ApplicationContextLocator;
import com.freedomotic.exceptions.DaoLayerException;
import com.freedomotic.model.object.EnvObject;
import java.net.URLClassLoader;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Enrico
 */
//@Configurable(autowire = Autowire.BY_TYPE)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public final class EnvObjectFactory {

    //@Autowired
    //private AutowireCapableBeanFactory beanFactory;

    public EnvObjectFactory() {
        // Suppress default constructor for noninstantiability
        //throw new AssertionError();
    }

    /**
     * Instantiate the right logic manager for an object pojo using the pojo
     * "type" field
     *
     * @param pojo
     * @return
     * @throws com.freedomotic.exceptions.DaoLayerException
     */
    public EnvObjectLogic create(EnvObject pojo)
            throws DaoLayerException {
        if (pojo == null) {
            throw new IllegalArgumentException("Cannot create an object logic from null object data");
        }

        try {
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class<?> clazz = classLoader.loadClass(pojo.getHierarchy()); //eg: com.freedomotic.objects.impl.ElectricDevice

            //EnvObjectLogic logic = (EnvObjectLogic) Freedomotic.INJECTOR.getInstance(clazz);
            EnvObjectLogic logic = (EnvObjectLogic) clazz.newInstance();
            ApplicationContext context = ApplicationContextLocator.getApplicationContext();
            AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
            factory.autowireBean(logic);
            logic.setPojo(pojo);

            return logic;
        } catch (InstantiationException ex) {
            throw new DaoLayerException(ex);
        } catch (IllegalAccessException ex) {
            throw new DaoLayerException(ex);
        } catch (ClassNotFoundException ex) {
            throw new DaoLayerException("Class '" + pojo.getHierarchy() + "' not found. "
                    + "The related object plugin is not "
                    + "loaded succesfully or you have a wrong hierarchy "
                    + "value in your XML definition of the object."
                    + "The hierarchy value is composed by the package name plus the java file name "
                    + "like com.freedomotic.objects.impl.Light not com.freedomotic.objects.impl.ElectricDevice.Light");
        }
    }
}
