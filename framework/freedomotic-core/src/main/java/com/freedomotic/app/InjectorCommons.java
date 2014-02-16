/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.app;

import com.freedomotic.api.API;
import com.freedomotic.api.APIStandardImpl;
import com.freedomotic.bus.BusService;
import com.freedomotic.bus.impl.BusServiceImpl;
import com.freedomotic.core.BehaviorManager;
import com.freedomotic.core.JoinPlugin;
import com.freedomotic.core.TriggerCheck;
import com.freedomotic.dao.EnvObjectDao;
import com.freedomotic.dao.EnvironmentDao;
import com.freedomotic.dao.xml.XmlEnvObjectDao;
import com.freedomotic.dao.xml.XmlEnvironmentDao;
import com.freedomotic.events.ProtocolRead;
import com.freedomotic.objects.EnvObjectLogic;
import com.freedomotic.plugins.ClientStorage;
import com.freedomotic.plugins.ClientStorageInMemory;
import com.freedomotic.plugins.filesystem.PluginsManager;
import com.freedomotic.plugins.filesystem.PluginsManagerImpl;
import com.freedomotic.security.Auth;
import com.freedomotic.security.AuthImpl;
import com.freedomotic.util.I18n.I18n;
import com.freedomotic.util.I18n.I18nImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 *
 * @author enrico
 */
public class InjectorCommons extends AbstractModule {

    /**
     *
     */
    @Override
    protected void configure() {
        bind(ClientStorage.class).to(ClientStorageInMemory.class).in(Singleton.class);
        bind(PluginsManager.class).to(PluginsManagerImpl.class).in(Singleton.class);
        //bind(JoinDevice.class).in(Singleton.class);
        bind(JoinPlugin.class).in(Singleton.class);
        bind(TriggerCheck.class).in(Singleton.class);
        
        bind(ProtocolRead.class);
        
        bind(EnvObjectLogic.class).in(Singleton.class);

        bind(AppConfig.class).to(AppConfigImpl.class).in(Singleton.class);

        bind(Auth.class).to(AuthImpl.class).in(Singleton.class);

        bind(I18n.class).to(I18nImpl.class).in(Singleton.class);
        //requestStaticInjection(I18n.class);

        bind(BusService.class).to(BusServiceImpl.class).in(Singleton.class);

        bind(API.class).to(APIStandardImpl.class).in(Singleton.class);

        /*
         * XML Environment dependencies
         */
        //bind interface to implementation
        bind(EnvironmentDao.class).to(XmlEnvironmentDao.class).in(Singleton.class);
        //used to get the default environment from config file
        bind(AppConfig.class).to(AppConfigImpl.class).in(Singleton.class);
        //Used by EnvironmentLogic for method permissions
        bind(Auth.class).to(AuthImpl.class).in(Singleton.class);

        /*
         * XML EnvObjectdependencies
         */
        //bind interface to implementation
        bind(EnvObjectDao.class).to(XmlEnvObjectDao.class).in(Singleton.class);
        //used by EnvObjectLogic to notify a change in object status
        bind(BusService.class).to(BusServiceImpl.class).in(Singleton.class);

    }
}
