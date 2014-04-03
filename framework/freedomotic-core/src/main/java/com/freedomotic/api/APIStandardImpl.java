/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.api;

import com.freedomotic.app.AppConfig;
import com.freedomotic.core.ResourcesManager;
import com.freedomotic.environment.EnvironmentLogic;
import com.freedomotic.environment.EnvironmentPersistence;
import com.freedomotic.objects.EnvObjectLogic;
import com.freedomotic.objects.EnvObjectPersistence;
import com.freedomotic.plugins.ClientStorage;
import com.freedomotic.plugins.filesystem.PluginsManager;
import com.freedomotic.security.Auth;
import com.freedomotic.util.I18n.I18n;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implements the standard freedomotic APIs available to plugins. This class
 * returns only unmodifiable collections, so the returned collections are just a
 * read-only view of current underlying data. This data are not immutable
 * themselves, but they are immutable trough the references retrieved from the
 * methods of this class.
 *
 * @author enrico
 */
public class APIStandardImpl implements API {

    @Autowired
    private EnvObjectPersistence object;
    @Autowired
    private ClientStorage clientStorage;
    @Autowired
    private AppConfig config;
    @Autowired
    private Auth auth;
    @Autowired
    private I18n i18n;
    @Autowired
    private PluginsManager plugManager;
    @Autowired
    private EnvironmentPersistence environment;

    public APIStandardImpl() {

    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public AppConfig getConfig() {
        return config;
    }

    /**
     * {@inheritDoc}
     *
     * @param obj
     * @param MAKE_UNIQUE
     * @return
     */
    @Override
    public EnvObjectLogic addObject(EnvObjectLogic obj, boolean MAKE_UNIQUE) {
        return object.add(obj, MAKE_UNIQUE);
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public Collection<EnvObjectLogic> getObjectList() {
        return /*Collections.unmodifiableList(*/ object.getObjectList(); /*);*/

    }

    /**
     * {@inheritDoc}
     *
     * @param name
     * @return
     */
    @Override
    public EnvObjectLogic getObjectByName(String name) {
        return object.getObjectByName(name);
    }

    /**
     * {@inheritDoc}
     *
     * @param uuid
     * @return
     */
    @Override
    public EnvObjectLogic getObjectByUUID(String uuid) {
        return object.getObjectByUUID(uuid);
    }

    /**
     * {@inheritDoc}
     *
     * @param protocol
     * @param address
     * @return
     */
    @Override
    public Collection<EnvObjectLogic> getObjectByAddress(String protocol, String address) {
        return object.getObjectByAddress(protocol, address);
    }

    /**
     * {@inheritDoc}
     *
     * @param protocol
     * @return
     */
    @Override
    public Collection<EnvObjectLogic> getObjectByProtocol(String protocol) {
        return object.getObjectByProtocol(protocol);
    }

    /**
     * {@inheritDoc}
     *
     * @param uuid
     * @return
     */
    @Override
    public Collection<EnvObjectLogic> getObjectByEnvironment(String uuid) {
        return object.getObjectByEnvironment(uuid);
    }

    /**
     * {@inheritDoc}
     *
     * @param input
     */
    @Override
    public void removeObject(EnvObjectLogic input) {
        object.remove(input);
    }

    /**
     * {@inheritDoc}
     *
     * @param obj
     * @param MAKE_UNIQUE
     * @return
     */
    @Override
    public EnvironmentLogic addEnvironment(EnvironmentLogic obj, boolean MAKE_UNIQUE) {
        return environment.add(obj, MAKE_UNIQUE);
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public List<EnvironmentLogic> getEnvironments() {
        return environment.getEnvironments();
    }

    /**
     * {@inheritDoc}
     *
     * @param UUID
     * @return
     */
    @Override
    public EnvironmentLogic getEnvByUUID(String UUID) {
        return environment.getEnvByUUID(UUID);
    }

    /**
     * {@inheritDoc}
     *
     * @param input
     */
    @Override
    public void removeEnvironment(EnvironmentLogic input) {
        environment.remove(input);
    }

    /**
     * {@inheritDoc}
     *
     * @param filter
     * @return
     */
    @Override
    public Collection<Client> getClients(String filter) {
        return clientStorage.getClients(filter);
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public Collection<Client> getClients() {
        return clientStorage.getClients();
    }

    public BufferedImage getResource(String resourceIdentifier) {
        return ResourcesManager.getResource(resourceIdentifier);
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public ClientStorage getClientStorage() {
        return clientStorage;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public Auth getAuth() {
        return auth;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public I18n getI18n() {
        return i18n;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public PluginsManager getPluginManager() {
        return plugManager;
    }

    /**
     * {@inheritDoc}
     *
     * @param tag
     * @return
     */
    @Override
    public Collection<EnvObjectLogic> getObjectByTag(String tag) {
        return object.getObjectByTags(tag);
    }
}
