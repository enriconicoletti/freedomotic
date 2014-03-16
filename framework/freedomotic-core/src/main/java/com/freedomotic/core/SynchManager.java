/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.core;

import com.freedomotic.app.Freedomotic;
import com.freedomotic.bus.BusConsumer;
import com.freedomotic.bus.BusMessagesListener;
import com.freedomotic.bus.BusService;
import com.freedomotic.environment.EnvironmentPersistence;
import com.freedomotic.environment.ZoneLogic;
import com.freedomotic.events.ObjectHasChangedBehavior;
import com.freedomotic.events.PersonDetected;
import com.freedomotic.model.ds.Config;
import com.freedomotic.model.geometry.FreedomPoint;
import com.freedomotic.objects.BehaviorLogic;
import com.freedomotic.objects.EnvObjectLogic;
import com.freedomotic.objects.EnvObjectPersistence;
import com.freedomotic.objects.impl.Person;
import com.freedomotic.util.TopologyUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.eclipse.jetty.util.log.Log;

/**
 *
 * @author nicoletti
 */
public class SynchManager implements BusConsumer {

    private static final Logger LOG = Logger.getLogger(TopologyManager.class.getName());
    private static BusMessagesListener listener;
    private static final String LISTEN_CHANNEL = "app.event.sensor.object.behavior.change";
    private final BusService busService;

    public SynchManager() {
        busService = Freedomotic.INJECTOR.getInstance(BusService.class);
        listener = new BusMessagesListener(this);
        listener.consumeEventFrom(LISTEN_CHANNEL);
    }

    @Override
    public void onMessage(ObjectMessage message) {
        Object jmsObject = null;
        try {
            jmsObject = message.getObject();
        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        if (jmsObject instanceof ObjectHasChangedBehavior) {
            ObjectHasChangedBehavior event = (ObjectHasChangedBehavior) jmsObject;
            EnvObjectLogic obj = EnvObjectPersistence.getObjectByUUID(event.getProperty("object.uuid"));
            LOG.warning("ricevuta notifica cambio stato " + event.getPayload().toString());
            for (BehaviorLogic b : obj.getBehaviors()) {
                String value = event.getProperty("object.behavior." + b.getName());
                if (event.getProperty("object.behavior." + b.getName()) != null) {
                    Config conf = new Config();
                    conf.setProperty("value", value);
                    LOG.warning("Synch: cambio behavior " + b.getName() + " in " + value);
                    obj.getBehavior(b.getName()).filterParams(conf, false);
                }
            }
        }
    }
}
