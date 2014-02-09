/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.api;

import com.freedomotic.app.AppConfig;
import com.freedomotic.core.ResourcesManager;
import com.freedomotic.dao.EnvObjectDao;
import com.freedomotic.dao.EnvironmentDao;
import com.freedomotic.environment.EnvironmentLogic;
import com.freedomotic.objects.EnvObjectLogic;
import com.freedomotic.plugins.ClientStorage;
import com.freedomotic.plugins.filesystem.PluginsManager;
import com.freedomotic.security.Auth;
import com.freedomotic.util.I18n.I18n;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Implements the standard freedomotic APIs available to plugins. This class
 * returns only unmodifiable collections, so the returned collections are just a
 * read-only view of current underlying data. This data are not immutable
 * themselves, but they are immutable trough the references retrieved from the
 * methods of this class.
 *
 * @author enrico
 */
@Singleton
public class APIStandardImpl
        implements API {

    private final EnvironmentDao environment;
    private final EnvObjectDao object;
    private final ClientStorage clientStorage;
    private final AppConfig config;
    private final Auth auth;
    private final I18n i18n;
    private final PluginsManager plugManager;

    /**
     *
     * @param environment
     * @param object
     * @param clientStorage
     * @param config
     * @param auth
     * @param i18n
     * @param plugManager
     */
    @Inject
    public APIStandardImpl(
            EnvironmentDao environment,
            EnvObjectDao object,
            ClientStorage clientStorage,
            AppConfig config,
            Auth auth,
            I18n i18n,
            PluginsManager plugManager) {
        this.environment = environment;
        this.object = object;
        this.clientStorage = clientStorage;
        this.config = config;
        this.auth = auth;
        this.i18n = i18n;
        this.plugManager = plugManager;
        System.out.println("auth in apiimpl is " + this.auth);
    }

    /**
     *
     * @return
     */
    @Override
    public AppConfig getConfig() {
        return config;
    }

    /**
     *
     * @param obj
     * @param MAKE_UNIQUE
     * @return
     */
    @Override
    public EnvObjectLogic addObject(EnvObjectLogic obj, boolean MAKE_UNIQUE) {
        return object.insert(obj);
    }

    /**
     *
     * @return
     */
    @Override
    public Collection<EnvObjectLogic> getObjectList() {
        return object.findAll();
    }

    /**
     *
     * @param name
     * @return
     */
    @Override
    public EnvObjectLogic getObjectByName(String name) {
        return object.findByName(name);
    }

    /**
     *
     * @param uuid
     * @return
     */
    @Override
    public EnvObjectLogic getObjectByUUID(String uuid) {
        return object.findByUuid(uuid);
    }

    /**
     *
     * @param protocol
     * @param address
     * @return
     */
    @Override
    public EnvObjectLogic getObjectByAddress(String protocol, String address) {
        return object.findByAddress(protocol, address);
    }

    /**
     *
     * @param protocol
     * @return
     */
    @Override
    public Collection<EnvObjectLogic> getObjectByProtocol(String protocol) {
        return object.findByProtocol(protocol);
    }

    /**
     *
     * @param uuid
     * @return
     */
    @Override
    public Collection<EnvObjectLogic> getObjectByEnvironment(String uuid) {
        return object.findByEnvironment(uuid);
    }

    /**
     *
     * @param input
     */
    @Override
    public void removeObject(EnvObjectLogic input) {
        object.delete(input);
    }

    /**
     *
     * @param obj
     * @param MAKE_UNIQUE
     * @return
     */
    @Override
    public EnvironmentLogic addEnvironment(EnvironmentLogic obj, boolean MAKE_UNIQUE) {
        return environment.insert(obj);
    }

    /**
     *
     * @return
     */
    @Override
    public Collection<EnvironmentLogic> getEnvironments() {
        return environment.findAll();
    }

    /**
     *
     * @param UUID
     * @return
     */
    @Override
    public EnvironmentLogic getEnvByUUID(String UUID) {
        return environment.findByUuid(UUID);
    }

    /**
     *
     * @param input
     */
    @Override
    public void removeEnvironment(EnvironmentLogic input) {
        environment.delete(input);
    }

    /**
     *
     * @param filter
     * @return
     */
    @Override
    public Collection<Client> getClients(String filter) {
        return clientStorage.getClients(filter);
    }
    
    /**
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
     *
     * @return
     */
    @Override
    public ClientStorage getClientStorage() {
        return clientStorage;
    }
    
    /**
     *
     * @return
     */
    @Override
    public Auth getAuth(){
        return auth;
    }

    /**
     *
     * @return
     */
    @Override
    public I18n getI18n() {
        return i18n;
    }

    /**
     *
     * @return
     */
    @Override
    public PluginsManager getPluginManager() {
        return plugManager;
    }

    /**
     *
     * @param tag
     * @return
     */
    @Override
    public Collection<EnvObjectLogic> getObjectByTag(Set<String> tag) {
        return object.findByTags(tag);
    }
}
