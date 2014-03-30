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
package com.freedomotic.api;

import com.freedomotic.app.ApplicationContextLocator;
import com.freedomotic.app.ConfigPersistence;
import com.freedomotic.app.Freedomotic;
import com.freedomotic.bus.BusConsumer;
import com.freedomotic.bus.BusMessagesListener;
import com.freedomotic.bus.BusService;
import com.freedomotic.events.PluginHasChanged;
import com.freedomotic.events.PluginHasChanged.PluginActions;
import com.freedomotic.model.ds.Config;
import com.freedomotic.util.EqualsUtil;
import com.freedomotic.util.Info;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.jms.ObjectMessage;
import javax.swing.JFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author nicoletti
 */
@Component
public class Plugin implements Client, BusConsumer {

    protected volatile boolean isRunning;
    private String pluginName;
    private String type = "Plugin";
    protected JFrame gui;
    protected String description;
    protected String version;
    protected String requiredVersion;
    protected String category;
    protected String shortName;
    protected String listenOn;
    protected String sendOn;
    private File path;
    final static int SAME_VERSION = 0;
    final static int FIRST_IS_OLDER = -1;
    final static int LAST_IS_OLDER = 1;
    @Autowired
    private API api;
    @Autowired
    protected BusService busService;
    //@Autowired 
    private BusMessagesListener listener;
    //@Autowired 
    //private AutowireCapableBeanFactory beanFactory;
    public Config configuration;

    private static final String ACTUATORS_QUEUE_DOMAIN = "app.actuators.";

    /**
     *
     * @param pluginName
     * @param manifestPath
     */
    public Plugin(String pluginName, String manifestPath) {
        this(pluginName);
        path = new File(Info.getDevicesPath() + manifestPath);
        loadConfiguration(path);
    }

    public Plugin() {

    }

    /**
     *
     * @param pluginName
     * @param manifest
     */
    public Plugin(String pluginName, Config manifest) {
        this(pluginName);
        loadConfiguration(path);
    }

    /**
     *
     * @param pluginName
     */
    public Plugin(String pluginName) {
        setName(pluginName);
        //this.busService = Freedomotic.INJECTOR.getInstance(BusService.class);
    }

    /**
     *
     * @return
     */
    public File getFile() {
        return path;
    }

    /**
     *
     * @return
     */
    public API getApi() {
        return api;
    }

    /**
     *
     */
    protected void onStart() {
    }

    /**
     *
     */
    protected void onStop() {
    }

    /**
     *
     * @param description
     */
    @Override
    public final void setDescription(String description) {
        if (!getDescription().equalsIgnoreCase(description)) {
            this.description = description;

            PluginHasChanged event = new PluginHasChanged(this,
                    this.getName(),
                    PluginActions.DESCRIPTION);
            //REGRESSION: busService.send(event);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String getDescription() {
        if (description == null) {
            return getName();
        } else {
            return description;
        }
    }

    /**
     *
     * @return
     */
    @Override
    public final Config getConfiguration() {
        return configuration;
    }

    /**
     *
     * @return
     */
    public final String getReadQueue() {
        return listenOn;
    }

    /**
     *
     * @return
     */
    public final String getCategory() {
        return category;
    }

    /**
     *
     * @return
     */
    public String getRequiredVersion() {
        return requiredVersion;
    }

    /**
     *
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     *
     * @param window
     */
    public void bindGuiToPlugin(JFrame window) {
        gui = window;
        gui.setVisible(false);
        gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     *
     */
    @Override
    public void showGui() {
        if (!isRunning()) {
            start();
        }

        onShowGui();

        if (gui != null) {
            gui.setVisible(true);
        } else {
            LOG.warning("ERROR: plugin gui is null");
        }
    }

    /**
     *
     */
    @Override
    public void hideGui() {
        onHideGui();

        if (gui != null) {
            gui.setVisible(false);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return pluginName;
    }

    /**
     *
     * @return
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isRunning() {
        return isRunning;
    }

    /**
     *
     * @return
     */
    public String getClassName() {
        return (this.getClass().getSimpleName());
    }

    /**
     *
     * @param name
     */
    @Override
    public final void setName(String name) {
        pluginName = name;
    }

//    public boolean isConnected() {
//        return isConnected;
//    }
//
//    public void setConnected() {
//        isConnected = true;
//    }
    /**
     *
     * @param aThat
     * @return
     */
    @Override
    public boolean equals(Object aThat) {
        //check for self-comparison
        if (this == aThat) {
            return true;
        }

        //use instanceof instead of getClass here for two reasons
        //1. if need be, it can match any supertype, and not just one class;
        //2. it renders an explict check for "that == null" redundant, since
        //it does the check for null already - "null instanceof [type]" always
        //returns false. (See Effective Java by Joshua Bloch.)
        if (!(aThat instanceof Plugin)) {
            return false;
        }

        //Alternative to the above line :
        //if ( aThat == null || aThat.getClass() != this.getClass() ) return false;
        //cast to native object is now safe
        Plugin that = (Plugin) aThat;

        //now a proper field-by-field evaluation can be made
        return EqualsUtil.areEqual(this.getName().toLowerCase(),
                that.getName().toLowerCase());
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = (53 * hash) + ((this.pluginName != null) ? this.pluginName.hashCode() : 0);

        return hash;
    }

    private void loadConfiguration(File manifest) {
        try {
            configuration = ConfigPersistence.deserialize(path);
        } catch (IOException ex) {
            LOG.severe("Missing manifest " + path.toString() + " for plugin " + getName());
            //setDescription("Missing manifest file " + path.toString());
        }
    }

    public void init() {
        ApplicationContext context = ApplicationContextLocator.getApplicationContext();
        AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
        factory.autowireBean(this);
        setDescription("No description");
        description = configuration.getStringProperty("description", "Missing plugin manifest");
        setDescription(description);
        category = configuration.getStringProperty("category", "undefined");
        shortName = configuration.getStringProperty("short-name", "undefined");
        listenOn = configuration.getStringProperty("listen-on", "undefined");
        sendOn = configuration.getStringProperty("send-on", "undefined");
        register();
    }

    private void register() {
        listener = new BusMessagesListener(busService, this);
        listener.consumeCommandFrom(getCommandsChannelToListen());
    }

    /**
     *
     * @param listento
     */
    public void addEventListener(String listento) {
        listener.consumeEventFrom(listento);
    }

    private String getCommandsChannelToListen() {
        String defaultQueue = ACTUATORS_QUEUE_DOMAIN + category + "." + shortName;
        String customizedQueue = ACTUATORS_QUEUE_DOMAIN + listenOn;

        if (getReadQueue().equalsIgnoreCase("undefined")) {
            listenOn = defaultQueue + ".in";

            return listenOn;
        } else {
            return customizedQueue;
        }
    }

    /**
     *
     */
    protected void onShowGui() {
    }

    /**
     *
     */
    protected void onHideGui() {
    }

    /**
     *
     */
    @Override
    public void start() {
        //do not add code here
    }

    /**
     *
     */
    @Override
    public void stop() {
        //do not add code here
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     *
     */
    public void loadPermissionsFromManifest() {
        LOG.info(getApi().toString());
        LOG.info(getApi().getAuth().toString());
        getApi().getAuth().setPluginPrivileges(this, configuration.getStringProperty("permissions", getApi().getAuth().getPluginDefaultPermission()));
    }
    private static final Logger LOG = Logger.getLogger(Plugin.class.getName());

    @Override
    public void onMessage(ObjectMessage message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
