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

import com.freedomotic.app.Freedomotic;
import com.freedomotic.bus.BusConsumer;
import com.freedomotic.bus.BusService;
import com.freedomotic.events.PluginHasChanged;
import com.freedomotic.exceptions.UnableToExecuteException;
import com.freedomotic.reactions.Command;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

/**
 * Uses a Template Method pattern which allows subclass to define how to perform
 * a command or how to act when a specific event is received.
 */
public abstract class Protocol
        extends Plugin
        implements BusConsumer {

    private static final Logger LOG = Logger.getLogger(Protocol.class.getName());
    private int pollingWaitTime = -1;
    private Protocol.SensorThread sensorThread;
    private volatile Destination lastDestination;

    /**
     *
     */
    protected abstract void onRun();

    /**
     *
     * @param c
     * @throws IOException
     * @throws UnableToExecuteException
     */
    protected abstract void onCommand(Command c)
            throws IOException, UnableToExecuteException;

    /**
     *
     * @param c
     * @return
     */
    protected abstract boolean canExecute(Command c);

    /**
     *
     * @param event
     */
    protected abstract void onEvent(EventTemplate event);

    /**
     *
     * @param pluginName
     * @param manifest
     */
    public Protocol(String pluginName, String manifest) {

        super(pluginName, manifest);
        //this.busService = Freedomotic.INJECTOR.getInstance(BusService.class);
    }



    /**
     *
     * @param ev
     */
    public void notifyEvent(EventTemplate ev) {
        if (isRunning) {
            notifyEvent(ev,
                    ev.getDefaultDestination());
        }
    }

    /**
     *
     * @param ev
     * @param destination
     */
    public void notifyEvent(EventTemplate ev, String destination) {
        if (isRunning) {
            LOG.fine("Sensor " + this.getName() + " notify event " + ev.getEventName() + ":"
                    + ev.getPayload().toString());
            busService.send(ev, destination);
        }
    }

    /**
     *
     */
    @Override
    public void start() {
        if (!isRunning) {

            Runnable action = new Runnable() {
                @Override
                public synchronized void run() {
                    onStart();
                    sensorThread = new Protocol.SensorThread();
                    sensorThread.start();
                    PluginHasChanged event = new PluginHasChanged(this, getName(), PluginHasChanged.PluginActions.START);
                    busService.send(event);
                    isRunning = true;
                }
            };

            getApi().getAuth().pluginExecutePrivileged(this, action);
        }
    }

    /**
     *
     */
    @Override
    public void stop() {
        if (isRunning) {
            Runnable action = new Runnable() {
                @Override
                public synchronized void run() {
                    isRunning = false;
                    onStop();
                    sensorThread = null;
                    notify();
                    PluginHasChanged event = new PluginHasChanged(this, getName(), PluginHasChanged.PluginActions.STOP);
                    busService.send(event);
                }
            };
            getApi().getAuth().pluginExecutePrivileged(this, action);
        }
    }

    /**
     *
     * @param wait
     */
    public final void setPollingWait(int wait) {
        if (wait > 0) {
            pollingWaitTime = wait;
        }
    }

    /**
     *
     * @return
     */
    public int getScheduleRate() {
        return pollingWaitTime;
    }

    private boolean isPollingSensor() {
        if (pollingWaitTime > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public final void onMessage(final ObjectMessage message) {
        if (!isRunning) {
            LOG.config("Protocol '" + getName()
                    + "' receives a command while is not running. Plugin tries to turn on itself...");
            start();
        }

        Object payload = null;

        try {
            payload = message.getObject();

            if (payload instanceof Command) {
                final Command command = (Command) payload;
                LOG.config(this.getName() + " receives command " + command.getName()
                        + " with parametes {{" + command.getProperties() + "}}");

                Protocol.ActuatorPerforms task;
                lastDestination = message.getJMSReplyTo();
                task
                        = new Protocol.ActuatorPerforms(command,
                                message.getJMSReplyTo(),
                                message.getJMSCorrelationID());
                task.start();
            } else {
                if (payload instanceof EventTemplate) {
                    final EventTemplate event = (EventTemplate) payload;
                    onEvent(event);
                }
            }
        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, null, ex);

        }
    }

    private class ActuatorPerforms
            extends Thread {

        private Command command;
        private Destination reply;
        private String correlationID;

        ActuatorPerforms(Command c, Destination reply, String correlationID) {
            this.command = c;
            this.reply = reply;
            this.correlationID = correlationID;
            this.setName("freedom-protocol-executor");
        }

        @Override
        public void run() {
            try {
                command.setExecuted(true);
                onCommand(command);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            } catch (UnableToExecuteException ex) {
                command.setExecuted(false);
            }

            if ((getConfiguration().getBooleanProperty("automatic-reply-to-commands", true) == true) //default value is true
                    && (command.getReplyTimeout() > 0)) {
                busService.reply(command, reply, correlationID); //sends back the command marked as executed or not
            }
        }
    }

    /**
     *
     * @param command
     * @return
     */
    protected Command send(Command command) {
        return busService.send(command);
    }

    /**
     *
     * @param command
     */
    public void reply(Command command) {
        // sends back the command
        final String defaultCorrelationID = "-1";
        busService.reply(command, lastDestination, defaultCorrelationID);

    }

    private class SensorThread
            extends Thread {

        @Override
        public void run() {

            if (isPollingSensor()) {
                Thread thisThread = Thread.currentThread();

                while (sensorThread == thisThread) {
                    try {
                        Thread.sleep(pollingWaitTime);

                        synchronized (this) {
                            while (!isRunning && (sensorThread == thisThread)) {
                                wait();
                            }
                        }
                    } catch (InterruptedException e) {
                        // TODO do Log?
                    }

                    onRun();
                }
            } else {
                if (isRunning) {
                    onRun();
                }
            }
        }
    }
}
