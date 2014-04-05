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
package com.freedomotic.events;

import com.freedomotic.api.EventTemplate;
import com.freedomotic.app.ApplicationContextLocator;
import com.freedomotic.core.TriggerCheck;
import com.freedomotic.reactions.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Channel <b>app.event.sensor.protocol.read.PROTOCOL_NAME</b> informs about
 * state changes of objects identified by protocol PROTOCOL_NAME
 *
 * @author Enrico
 */
@Component
public final class ProtocolRead extends EventTemplate {

    private String protocol;
    private String address;
    @Autowired
    private TriggerCheck triggerCheck;

    public ProtocolRead() {

    }

    /**
     *
     * @param source
     * @param protocol
     * @param address
     */
    public ProtocolRead(Object source, String protocol, String address) {
        this.setSender(source);
        this.protocol = protocol;
        addProperty("protocol", protocol);
        addProperty("address", address);
        generateEventPayload();
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     *
     */
    @Override
    protected void generateEventPayload() {
        //this is not a good idea but it works for now
        //Freedomotic.INJECTOR.injectMembers(this);
        ApplicationContextLocator.forceInjection(this);
    }

    /**
     *
     * @return
     */
    @Override
    public String getDefaultDestination() {
        /*
         * This method is called before sending the event on the messaging bus.
         * If the event contains behavior.name and behavior.value we can bypass
         * the entire trigger system so the plugin developer doesen't need
         * to define an xml trigger file if he already knows on which behavior
         * he would act. If this two properties are not defined the event
         * is sent as usual.
         *
         */
        String behaviorName = getProperty("behavior.name");

        //TODO: change behaviorValue in behavior.value (must be changed in all triggers)
        String behaviorValue = getProperty("behaviorValue");

        if (!behaviorName.isEmpty() && !behaviorValue.isEmpty()) {
            Trigger trigger = new Trigger();
            trigger.setName("Object behavior change request from " + this.sender);
            trigger.setPersistence(false);
            trigger.setIsHardwareLevel(true);
            trigger.setPayload(payload);
            triggerCheck.check(this, trigger);

            return ""; //this event is not sent on the bus
        } else {
            //return the normal event destination
            return "app.event.sensor.protocol.read." + protocol.trim().toLowerCase();
        }
    }
}
