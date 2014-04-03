/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.api;

import com.freedomotic.app.AppConfig;
import com.freedomotic.environment.EnvironmentLogic;
import com.freedomotic.objects.EnvObjectLogic;
import com.freedomotic.plugins.ClientStorage;
import com.freedomotic.plugins.filesystem.PluginsManager;
import com.freedomotic.security.Auth;
import com.freedomotic.util.I18n.I18n;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author enrico
 */
public interface API {

    /**
     * Get the plugin configuration
     *
     * @return
     */
    public AppConfig getConfig();

    /**
     * Get authorization and authentication settings
     *
     * @return
     */
    public Auth getAuth();

    /**
     * Get internationalization module
     *
     * @return
     */
    public I18n getI18n();

    /**
     * Add an object to the current active environment
     *
     * @param obj
     * @param MAKE_UNIQUE
     * @return
     */
    public EnvObjectLogic addObject(final EnvObjectLogic obj, final boolean MAKE_UNIQUE);

    /**
     * Get the list of loaded freedomotic extensions. Extensions are composed by
     * java dynamically loaded plugins and remotely connected clients developed
     * in languages different from Java.
     *
     * @return
     */
    public ClientStorage getClientStorage();

    /**
     * Get the list of objects contained in all environments, not just the
     * active one.
     *
     * @return
     */
    public Collection<EnvObjectLogic> getObjectList();

    /**
     * Get an object using it's name as an identifier. Freedomotic guarantees
     * that cannot be two objects with the same name. Matching is case
     * insensitive.
     *
     * @param name
     * @return
     */
    public EnvObjectLogic getObjectByName(String name);

    /**
     * Get an object reverence using it's global ID
     *
     * @param uuid
     * @return
     */
    public EnvObjectLogic getObjectByUUID(String uuid);

    /**
     * Get a lost of objects sharing the same tag
     *
     * @param tag
     * @return
     */
    public Collection<EnvObjectLogic> getObjectByTag(String tag);

    /**
     * Get an single object using protocol+address to identify it uniquely.
     * Cannot exists two object with the same protocol+address.
     *
     * @param protocol
     * @param address
     * @return
     */
    public Collection<EnvObjectLogic> getObjectByAddress(String protocol, String address);

    /**
     * Get all objects driven by a particular protocol. Protocol is a free-form
     * string declared by the related protocol plugin itself, check it's
     * configuration to know the exact string name.
     *
     * @param protocol
     * @return
     */
    public Collection<EnvObjectLogic> getObjectByProtocol(String protocol);

    /**
     * Get the list of object in the environment specified using a global ID
     *
     * @param uuid
     * @return
     */
    public Collection<EnvObjectLogic> getObjectByEnvironment(String uuid);

    /**
     * Remove an object from the global list of loaded objects. It is removed
     * from any environment, not just the current active one
     *
     * @param input
     */
    public void removeObject(EnvObjectLogic input);

    /**
     * Add a new environment (eg: a floor on the map)
     *
     * @param obj
     * @param MAKE_UNIQUE
     * @return
     */
    public EnvironmentLogic addEnvironment(final EnvironmentLogic obj, boolean MAKE_UNIQUE);

    /**
     * Get the list of loaded environments (eg: list of floors on the map)
     *
     * @return
     */
    public List<EnvironmentLogic> getEnvironments();

    /**
     * Get an environment using it's global id as an identifier
     *
     * @param UUID
     * @return
     */
    public EnvironmentLogic getEnvByUUID(String UUID);

    /**
     * Remove an environment from the list
     *
     * @param input
     */
    public void removeEnvironment(EnvironmentLogic input);

    /**
     * Get the list of loaded clients. A client can be a freedomotic java
     * extension (plugin) or any remotely connected software.
     *
     * @return
     */
    public Collection<Client> getClients();

    /**
     * Get a list of clients filtering by type. Valid types are 'plugin',
     * 'object', 'event'
     *
     * @param filter
     * @return
     */
    public Collection<Client> getClients(String filter);

    /**
     * Get the plugin manager, which can be used to manipulate the client list.
     * It's usage it's discouraged.
     *
     * @return
     */
    public PluginsManager getPluginManager();

    /**
     * Get a resource like a image stored in the freedomotic data/resources
     * folder. Resources are classpath loaded (by name) regardless the folder in
     * which they are stored. Be sure to not have two resources with the same
     * name. It's a good practice to append a namespace in at the beginning of
     * the file name like it.freedomotic.icons.myimage.png
     *
     * @param resourceIdentifier
     * @return
     */
    public BufferedImage getResource(String resourceIdentifier);
}
