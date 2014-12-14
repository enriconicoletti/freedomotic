/**
 *
 * Copyright (c) 2009-2014 Freedomotic team http://freedomotic.com
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
package com.freedomotic.plugins.devices.restapiv3.filters;

import com.freedomotic.plugins.devices.restapiv3.resources.jersey.EnvironmentResource;
import com.freedomotic.plugins.devices.restapiv3.resources.jersey.HardwareCommandResource;
import com.freedomotic.plugins.devices.restapiv3.resources.jersey.ImageResource;
import com.freedomotic.plugins.devices.restapiv3.resources.jersey.MarketplaceCategoryResource;
import com.freedomotic.plugins.devices.restapiv3.resources.jersey.MarketplacePluginsResource;
import com.freedomotic.plugins.devices.restapiv3.resources.jersey.MarketplaceProvidersResource;
import com.freedomotic.plugins.devices.restapiv3.resources.jersey.MarketplaceResource;
import com.freedomotic.plugins.devices.restapiv3.resources.jersey.OldReactionResource;
import com.freedomotic.plugins.devices.restapiv3.resources.jersey.PluginResource;
import com.freedomotic.plugins.devices.restapiv3.resources.jersey.ReactionResource;
import com.freedomotic.plugins.devices.restapiv3.resources.jersey.RoleResource;
import com.freedomotic.plugins.devices.restapiv3.resources.jersey.RoomResource;
import com.freedomotic.plugins.devices.restapiv3.resources.jersey.SystemResource;
import com.freedomotic.plugins.devices.restapiv3.resources.jersey.ThingResource;
import com.freedomotic.plugins.devices.restapiv3.resources.jersey.TriggerResource;
import com.freedomotic.plugins.devices.restapiv3.resources.jersey.UserCommandResource;
import com.freedomotic.plugins.devices.restapiv3.resources.jersey.UserResource;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import javax.inject.Inject;

/**
 *
 * @author matteo
 */
public class GuiceServletConfig extends GuiceServletContextListener {

    final Injector parentInjector;

    @Inject
    public GuiceServletConfig(Injector injector) {
        this.parentInjector = injector;
    }

    @Override
    protected Injector getInjector() {
        return parentInjector.createChildInjector(new ServletModule() {
            @Override
            protected void configureServlets() {

                bind(EnvironmentResource.class);
                bind(HardwareCommandResource.class);
                bind(ImageResource.class);
                bind(MarketplaceCategoryResource.class);
                bind(MarketplacePluginsResource.class);
                bind(MarketplaceProvidersResource.class);
                bind(MarketplaceResource.class);
                bind(OldReactionResource.class);
                bind(PluginResource.class);
                bind(ReactionResource.class);
                bind(RoleResource.class);
                bind(SystemResource.class);
                bind(ThingResource.class);
                bind(TriggerResource.class);
                bind(UserCommandResource.class);
                bind(UserResource.class);
                serve("/v3/*").with(GuiceContainer.class);
                // this is the important part - it will cause Jersey to get the child injector
                binder().bind(GuiceContainer.class).asEagerSingleton();
            }
        });
    }
}
