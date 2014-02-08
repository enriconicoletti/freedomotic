/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.dao.xml;


import com.freedomotic.app.AppConfig;
import com.freedomotic.app.AppConfigImpl;
import com.freedomotic.bus.BusService;
import com.freedomotic.bus.impl.BusServiceImpl;
import com.freedomotic.dao.EnvObjectDao;
import com.freedomotic.dao.EnvironmentDao;
import com.freedomotic.security.Auth;
import com.freedomotic.security.AuthImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 *
 * @author enrico
 */
public class InjectXmlDao extends AbstractModule {

    @Override
    protected void configure() {
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
