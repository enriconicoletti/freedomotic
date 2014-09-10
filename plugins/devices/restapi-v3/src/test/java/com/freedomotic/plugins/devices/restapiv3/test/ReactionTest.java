/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.freedomotic.plugins.devices.restapiv3.test;

import com.freedomotic.plugins.devices.restapiv3.resources.jersey.OldReactionResource;
import com.freedomotic.reactions.Command;
import com.freedomotic.reactions.Reaction;
import com.freedomotic.reactions.Trigger;
import java.util.List;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriBuilderException;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author matteo
 */
public class ReactionTest extends AbstractTest<Reaction>{

    @Override
    public void test() {
     // skip test (need some more work to be ready for this)
    }

    
    @Override
    public void init() throws UriBuilderException, IllegalArgumentException {
        setItem(new Reaction());
        getItem().setUuid(getUuid());
        Command com = new Command();
        com.setName("Reaction Command");
        com.setHardwareLevel(false);
        Trigger t = new Trigger();
        t.setName("Reaction trigger");
        getApi().triggers().create(t);
        getApi().commands().create(com);
        getItem().setTrigger(t);
        
        getItem().addCommand(com);
        initPath(OldReactionResource.class);
        setListType(new GenericType<List<Reaction>>(){});
        setSingleType(new GenericType<Reaction>(){});
    }

    @Override
    protected void putModifications(Reaction orig) {
        orig.getTrigger().setChannel("pippo");
    }

    @Override
    protected void putAssertions(Reaction pre, Reaction post) {
        assertEquals("PUT - trigger channel check", pre.getTrigger().getChannel(), post.getTrigger().getChannel());
    }

    @Override
    protected void getAssertions(Reaction obj) {
       assertEquals("Single test - UUID", getItem().getUuid(), obj.getUuid());
    }

    @Override
    protected void listAssertions(List<Reaction> list) {
        assertEquals("Single test - UUID", getItem().getUuid(), list.get(0).getUuid());
    }

    @Override
    protected String getUuid(Reaction obj) {
        return obj.getUuid();
    }
    
}
