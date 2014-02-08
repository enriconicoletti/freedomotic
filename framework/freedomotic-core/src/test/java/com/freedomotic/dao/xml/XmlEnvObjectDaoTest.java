/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.dao.xml;

import com.freedomotic.dao.EnvObjectDao;
import com.freedomotic.environment.EnvironmentLogic;
import com.freedomotic.exceptions.DaoLayerException;
import com.freedomotic.model.environment.Environment;
import com.freedomotic.objects.EnvObjectFactory;
import com.freedomotic.objects.EnvObjectLogic;
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
 * @author enrico
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceInjectors({InjectXmlDao.class})
public class XmlEnvObjectDaoTest {

    private static final Logger LOG = Logger.getLogger(XmlEnvObjectDaoTest.class.getName());
    
    @Inject
    private EnvObjectDao objDao;


    public XmlEnvObjectDaoTest() {
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

    /**
     * Test of insert method, of class XmlEnvObjectDao.
     * @throws com.freedomotic.exceptions.DaoLayerException
     */
    @Test
    public void testInsert() throws DaoLayerException {
        LOG.info("Test inserting a new EnvObject in XmlEnvObjectDao");
        //this will also trigger an ObjectChangedState notification
        EnvObjectLogic obj = EnvObjectFactory.createDummy();
        EnvObjectLogic insert = objDao.insert(obj);
        assertNotNull("Adding new envobjects should be successful", objDao.findByUuid(insert.getPojo().getUUID()));
    }

    /**
     * Test of delete method, of class XmlEnvObjectDao.
     * @throws com.freedomotic.exceptions.DaoLayerException
     */ 
     
    @Test
    public void testDelete()  throws DaoLayerException {
        LOG.info("Test deleting EnvObject in XmlEnvObjectDao");
        
        //create a valid envobject to insert
        EnvObjectLogic obj = EnvObjectFactory.createDummy();
        EnvObjectLogic insert = objDao.insert(obj);

        //remove the environment
        boolean remove = objDao.delete(insert);
        assertEquals("Remove should be successful", true, remove);
    }

    /**
     * Test of findByName method, of class XmlEnvObjectDao.
     
    @Test
    public void testFindByName() {
        System.out.println("findByName");
        String name = "";
        XmlEnvObjectDao instance = null;
        EnvObjectLogic expResult = null;
        EnvObjectLogic result = instance.findByName(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findByUuid method, of class XmlEnvObjectDao.
     
    @Test
    public void testFindByUuid() {
        System.out.println("findByUuid");
        String uuid = "";
        XmlEnvObjectDao instance = null;
        EnvObjectLogic expResult = null;
        EnvObjectLogic result = instance.findByUuid(uuid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAll method, of class XmlEnvObjectDao.
     
    @Test
    public void testFindAll() {
        System.out.println("findAll");
        XmlEnvObjectDao instance = null;
        Collection<EnvObjectLogic> expResult = null;
        Collection<EnvObjectLogic> result = instance.findAll();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class XmlEnvObjectDao.
     
    @Test
    public void testUpdate() {
        System.out.println("update");
        EnvObjectLogic obj = null;
        XmlEnvObjectDao instance = null;
        boolean expResult = false;
        boolean result = instance.update(obj);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findByAddress method, of class XmlEnvObjectDao.
     
    @Test
    public void testFindByAddress() {
        System.out.println("findByAddress");
        String protocol = "";
        String address = "";
        XmlEnvObjectDao instance = null;
        EnvObjectLogic expResult = null;
        EnvObjectLogic result = instance.findByAddress(protocol, address);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findByProtocol method, of class XmlEnvObjectDao.
     
    @Test
    public void testFindByProtocol() {
        System.out.println("findByProtocol");
        String protocol = "";
        XmlEnvObjectDao instance = null;
        Collection<EnvObjectLogic> expResult = null;
        Collection<EnvObjectLogic> result = instance.findByProtocol(protocol);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findByTags method, of class XmlEnvObjectDao.
     
    @Test
    public void testFindByTags() {
        System.out.println("findByTags");
        Set<String> tags = null;
        XmlEnvObjectDao instance = null;
        Collection<EnvObjectLogic> expResult = null;
        Collection<EnvObjectLogic> result = instance.findByTags(tags);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findByEnvironment method, of class XmlEnvObjectDao.
     
    @Test
    public void testFindByEnvironment() {
        System.out.println("findByEnvironment");
        String uuid = "";
        XmlEnvObjectDao instance = null;
        Collection<EnvObjectLogic> expResult = null;
        Collection<EnvObjectLogic> result = instance.findByEnvironment(uuid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
*/
}
