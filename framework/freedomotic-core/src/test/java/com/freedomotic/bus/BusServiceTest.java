/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.bus;

import com.freedomotic.app.SpringConfig;
import com.freedomotic.events.ProtocolRead;
import com.freedomotic.reactions.Command;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author nicoletti
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfig.class)
public class BusServiceTest {

    @Autowired
    private BusService busService;

    /**
     *
     */
    public BusServiceTest() {
    }

    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
    }

    /**
     *
     */
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     *
     */
    @Before
    public void setUp() {
    }

    /**
     *
     */
    @After
    public void tearDown() {
    }

    /**
     * Test of send method, of class BusService.
     */
    @Test
    public void testSendCommand() {
        LOG.info("Test bus send command asynch");
        Command command = new Command();
        command.setReceiver("unlistened.test.channel");
        assertFalse("Unsent command should be marked with executed=false flag", command.isExecuted());
        Command result = busService.send(command);
        assertTrue("Succesfully sent command should be marked as executed", result.isExecuted());
    }

    @Test
    public void testSendEvent() {
        LOG.info("Test bus send event");
        final ProtocolRead event = new ProtocolRead(this, "test", "test");
        event.addProperty("test.property.key", "test.property.value");
        event.addProperty("behavior.name", "test.property.value");
        event.addProperty("behaviorValue", "test.property.value");

        BusMessagesListener listener = new BusMessagesListener(busService, new BusConsumer() {

            @Override
            public void onMessage(ObjectMessage message) {
                LOG.info("Received test event " + message);
                try {
                    ProtocolRead event = (ProtocolRead) message.getObject();
                    assertEquals(event.getProperty("test.property.key"), "test.property.value");
                } catch (JMSException ex) {
                    Logger.getLogger(BusServiceTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        listener.consumeEventFrom(event.getDefaultDestination());
        busService.send(event);

        try {
            //give it some time to receive the event before exiting the test
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(BusServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of reply method, of class BusService.
     */
    @Test
    public void testSendCommandAndWaitTimeout() {
        LOG.info("Test send command and do not reply to test timeout");
        Command command = new Command();
        //send it on unlistened channel so it will not receive reply within timeout
        command.setReceiver("unlistened.test.channel");
        command.setReplyTimeout(2000); //wait reply for two seconds
        Command result = busService.send(command);
        assertEquals("Timeout reply command is the original command", result, command);
        assertFalse("Timeout reply marks the original command as not executed", result.isExecuted());
    }

    /**
     * Test of reply method, of class BusService.
     */
    @Test
    public void testSendCommandAndWaitReply() {
        LOG.info("Test send command and wait for reply");

        //create a listener for this command
        BusMessagesListener listener = new BusMessagesListener(busService, new BusConsumer() {

            @Override
            public void onMessage(ObjectMessage message) {
                assertNotNull("Received message should be not null", message);
                try {
                    Command c = (Command) message.getObject();
                    c.setProperty("receiver-reply", "OK");
                } catch (JMSException ex) {
                    Logger.getLogger(BusServiceTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
        listener.consumeCommandFrom("wait.for.reply.here");

        //prepare and send the command
        Command command = new Command();
        command.setReceiver("wait.for.reply.here");
        command.setReplyTimeout(2000); //wait reply for two seconds
        Command result = busService.send(command);

        //the reply should have the property addedd by the listener
        assertTrue("Command reply was received", result.getProperty("receiver-reply").equals("OK"));
    }

    private static final Logger LOG = Logger.getLogger(BusServiceTest.class.getName());
}
