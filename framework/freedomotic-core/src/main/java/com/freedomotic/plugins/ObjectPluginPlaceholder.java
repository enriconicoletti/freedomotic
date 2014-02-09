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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.plugins;

import com.freedomotic.api.Client;
import com.freedomotic.dao.EnvObjectDao;
import com.freedomotic.dao.xml.XmlEnvObjectDao;
import com.freedomotic.environment.EnvironmentLogic;
import com.freedomotic.exceptions.DaoLayerException;
import com.freedomotic.model.ds.Config;
import com.freedomotic.model.object.EnvObject;
import com.freedomotic.objects.EnvObjectFactory;
import com.freedomotic.objects.EnvObjectLogic;
import com.freedomotic.persistence.FreedomXStream;
import com.freedomotic.util.DOMValidateDTD;
import com.freedomotic.util.Info;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.inject.Inject;

/**
 *
 * @author enrico
 */
public class ObjectPluginPlaceholder implements Client {

    private final File example;
    private final EnvObjectLogic object;
    private final Config config;
    @Inject
    private EnvObjectDao objDao;

    private static final Logger LOG = Logger.getLogger(ObjectPluginPlaceholder.class.getName());

    /**
     *
     * @param example
     * @throws DaoLayerException
     */
    public ObjectPluginPlaceholder(File example) throws DaoLayerException {
        this.example = example;
        object = loadObject(example);
        config = new Config();
    }

    /**
     *
     * @return
     */
    public EnvObjectLogic getObject() {
        return object;
    }

    /**
     *
     * @param name
     */
    @Override
    public void setName(String name) {
        //no name change allowed. do nothing
    }

    /**
     *
     * @return
     */
    @Override
    public String getDescription() {
        return object.getPojo().getDescription();
    }

    /**
     *
     * @param description
     */
    @Override
    public void setDescription(String description) {
        //no change allowed
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return object.getPojo().getName();
    }

    /**
     *
     * @return
     */
    @Override
    public String getType() {
        return "Object";
    }

    /**
     *
     */
    @Override
    public void start() {
        objDao.insert(object);
    }

    /**
     *
     */
    @Override
    public void stop() {
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isRunning() {
        //is running if there is already an object of this kind in the map
//        boolean found = false;
//        for (EnvObjectLogic obj : EnvObjectPersistence.getObjectList()) {
//            if (obj.getClass().getCanonicalName().equals(clazz.getCanonicalName())) {
//                found = true;
//            }
//        }
//        return found;
        return true;
    }

    /**
     *
     * @return
     */
    public File getTemplate() {
        return example;
    }

    /**
     *
     */
    @Override
    public void showGui() {
    }

    /**
     *
     */
    @Override
    public void hideGui() {
    }

    /**
     *
     * @return
     */
    @Override
    public Config getConfiguration() {
        return config;
    }

    /**
     *
     * @param env
     */
    public void startOnEnv(EnvironmentLogic env) {
        if (env == null) {
            throw new IllegalArgumentException("Cannot place an object on a null environment");
        }
        EnvObjectLogic obj = objDao.insert(object);
        obj.setEnvironment(env);
    }

    /**
     * Loads the object file from file but NOT add the object to the list
     *
     * @param file
     * @return
     */
    //REGRESSION
    public static EnvObjectLogic loadObject(File file) throws DaoLayerException {
        XStream xstream = FreedomXStream.getXstream();

        //validate the object against a predefined DTD
        String xml;

        try {
            xml = DOMValidateDTD.validate(file, Info.getApplicationPath() + "/config/validator/object.dtd");

            EnvObject pojo = (EnvObject) xstream.fromXML(xml);
            EnvObjectLogic objectLogic = null;

            try {
                objectLogic = EnvObjectFactory.create(pojo);
                LOG.config("Created a new logic for " + objectLogic.getPojo().getName()
                        + " of type " + objectLogic.getClass().getCanonicalName().toString());

                return objectLogic;
            } catch (DaoLayerException daoLayerException) {
                LOG.warning(daoLayerException.getMessage());
            }
        } catch (IOException ex) {
            throw new DaoLayerException(ex.getMessage(), ex);
        } catch (XStreamException e) {
            throw new DaoLayerException(e.getMessage(), e);
        }

        return null;
    }
}
