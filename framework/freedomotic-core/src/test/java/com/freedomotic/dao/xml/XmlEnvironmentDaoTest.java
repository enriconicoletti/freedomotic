/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.dao.xml;

import com.freedomotic.dao.EnvironmentDao;
import com.freedomotic.environment.EnvironmentLogic;
import com.freedomotic.exceptions.DaoLayerException;
import com.freedomotic.model.environment.Environment;
import com.freedomotic.testutils.GuiceJUnitRunner;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author nicoletti
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceInjectors({InjectXmlDao.class})
public class XmlEnvironmentDaoTest {

    private static final Logger LOG = Logger.getLogger(XmlEnvironmentDaoTest.class.getName());

    @Inject
    private EnvironmentDao envDao;
    @Inject
    private EnvironmentLogic env;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws DaoLayerException {
    }

    @After
    public void tearDown() throws DaoLayerException {
    }

    /**
     * Test of insert method, of class EnvironmentDao.
     *
     * @throws it.freedomotic.exceptions.DaoLayerException
     */
    @Test
    public void testInsert() throws DaoLayerException {
        LOG.info("Test inserting a new environment in XMLEnvironmentDao");

        Environment data = new Environment();
        data.setName("Environment created in UnitTests");
        env.setPojo(data);

        EnvironmentLogic insert = envDao.insert(env);

        assertNotNull("Insert environment should be successful", envDao.findByUuid(insert.getPojo().getUUID()));
    }

    /**
     * Test of delete method, of class EnvironmentDao.
     *
     * @throws it.freedomotic.exceptions.DaoLayerException
     */
    @Test
    public void testDelete() throws DaoLayerException {
        LOG.info("Test deleting first environment in XMLEnvironmentDao");

        //create a valid environment to insert
        Environment data = new Environment();
        data.setName("Environment to be deleted");
        env.setPojo(data);

        //insert the new environment
        EnvironmentLogic insert = envDao.insert(env);
        assertNotNull(insert);

        //remove the environment
        boolean remove = envDao.delete(insert);
        assertEquals("Remove should be successful", true, remove);
    }

    /**
     * Test of findByName method, of class EnvironmentDao.
     *
     * @throws it.freedomotic.exceptions.DaoLayerException
     */
    @Test
    public void testFindByName() throws DaoLayerException {
        LOG.info("Test search environment by name");

        //create a valid environment to insert
        Environment data = new Environment();
        data.setName("Environment to be found");
        env.setPojo(data);

        //insert the new environment
        EnvironmentLogic insert = envDao.insert(env);
        assertNotNull(insert);
        
        //find the environment (should be case insensitive)
        EnvironmentLogic findByName = envDao.findByName("ENVIronMent TO BE Found");
        assertNotNull("Find environment by name (case insensitive)", findByName);
    }

    /**
     * Test of findByUuid method, of class EnvironmentDao.
     *
     * @throws it.freedomotic.exceptions.DaoLayerException
     */
    @Test
    public void testFindByUuid() throws DaoLayerException {
        LOG.info("Test search environment by uuid");

        //create a valid environment to insert
        Environment data = new Environment();
        data.setName("Environment to be found");
        env.setPojo(data);

        //insert the new environment
        EnvironmentLogic insert = envDao.insert(env);
        assertNotNull(insert);
        
        //find the environment
        EnvironmentLogic findByUuid = envDao.findByUuid(insert.getPojo().getUUID());
        assertNotNull("Find environment by uuid", findByUuid);
    }

    /**
     * Test of findDefault method, of class EnvironmentDao.
     *
     * @throws it.freedomotic.exceptions.DaoLayerException
     */
    @Test
    public void testFindDefaultEnvironment() throws DaoLayerException {
        LOG.info("Test search default environment");

        //create a valid environment to insert
        Environment data = new Environment();
        data.setName("Another environment");
        env.setPojo(data);

        //insert the new environment
        EnvironmentLogic insert = envDao.insert(env);
        assertNotNull(insert);
        
        //find the environment
        EnvironmentLogic found = envDao.findDefault();
        assertNotNull("Find default environment", found);
    }

    /**
     * Test of findAll method, of class EnvironmentDao.
     *
     * @throws it.freedomotic.exceptions.DaoLayerException
     */
    @Test
    public void testFindAll() throws DaoLayerException {
        LOG.info("Test search all environments");

        //create a valid environment to insert
        Environment data = new Environment();
        data.setName("Another environment");
        env.setPojo(data);

        //insert the new environment
        EnvironmentLogic insert = envDao.insert(env);
        assertNotNull(insert);
        //find the environment
        assertNotNull("The just inserted environment should be in the list",
                envDao.findByUuid(env.getUUID()));
    }

    /**
     * Test of update method, of class EnvironmentDao.
     *
     * @throws it.freedomotic.exceptions.DaoLayerException
     */
    @Test
    public void testUpdate() throws DaoLayerException {
        LOG.info("Test search default environment");

        //create a valid environment to insert
        Environment data = new Environment();
        data.setName("Another environment");
        env.setPojo(data);

        //insert the new environment
        EnvironmentLogic insert = envDao.insert(env);
        assertNotNull(insert);

        //edit the environment
        env.getPojo().setName("Edited Environment");

        //update the environment
        boolean done = envDao.update(env);
        assertEquals("Environment updated", true, done);
        
        //get the updated environment
        EnvironmentLogic found = envDao.findByUuid(insert.getPojo().getUUID());
        assertEquals("Update environment name should be successfull", "Edited Environment", found.getPojo().getName());
    }
}
