/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.app;

import com.freedomotic.model.environment.Environment;
import com.freedomotic.model.environment.Zone;
import com.freedomotic.model.geometry.FreedomColor;
import com.freedomotic.model.geometry.FreedomPoint;
import com.freedomotic.model.geometry.FreedomPolygon;
import com.freedomotic.model.object.Behavior;
import com.freedomotic.model.object.BooleanBehavior;
import com.freedomotic.model.object.EnvObject;
import com.freedomotic.model.object.Properties;
import com.freedomotic.repository.EnvObjectRepository;
import com.freedomotic.repository.EnvironmentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
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
public class RepositoryTest {

    @Autowired
    private EnvironmentRepository envRepo;
    @Autowired
    private EnvObjectRepository objRepo;

    public RepositoryTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSomeMethod() {
        Environment env = new Environment();
        env.setName("First floor");
        env.setBackgroundImage("map3.png");
        env.setRenderer("photo");
        env.setUuid(UUID.randomUUID().toString());
        FreedomColor color = new FreedomColor(10, 10, 10);
        env.setBackgroundColor(color);
        Zone zone = new Zone();
        zone.setName("test zone");
        zone.setDescription("a zone description");
        FreedomPolygon poly = new FreedomPolygon();
        poly.append(new FreedomPoint(0, 0));
        poly.append(new FreedomPoint(20, 0));
        poly.append(new FreedomPoint(20, 20));
        poly.append(new FreedomPoint(0, 20));
        zone.setShape(poly);
        env.addZone(zone);
        envRepo.save(env);
        EnvObject obj = new EnvObject();
        obj.setName("test Object");
        Properties prop = new Properties();
        prop.setProperty("key", "value");
        obj.setActions(prop);
        obj.setTriggers(prop);
        Behavior b = new BooleanBehavior();
        b.setName("powered");
        b.setDescription("An electric device behavior");
        b.setReadonly(false);
        List<Behavior> behaviors = new ArrayList<Behavior>();
        behaviors.add(b);
        obj.setBehaviors(behaviors);
        objRepo.save(obj);
    }

}
