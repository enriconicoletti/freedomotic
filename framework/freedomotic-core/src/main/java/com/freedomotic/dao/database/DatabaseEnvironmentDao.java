/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.dao.database;

import com.freedomotic.dao.EnvironmentDao;
import com.freedomotic.environment.EnvironmentLogic;
import com.freedomotic.exceptions.DaoLayerException;
import java.util.Collection;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author nicoletti
 */
public class DatabaseEnvironmentDao implements EnvironmentDao {

    @Inject
    @PersistenceContext(unitName="persistence-freedomotic")
    private EntityManager em;

    @Override
    public void init() throws DaoLayerException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() throws DaoLayerException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void flush() throws DaoLayerException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EnvironmentLogic insert(EnvironmentLogic environment) {
        em.persist(environment);
        return environment;
    }

    @Override
    public boolean delete(EnvironmentLogic environment) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EnvironmentLogic findByName(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EnvironmentLogic findByUuid(String uuid) {
        return em.find(EnvironmentLogic.class, uuid);
    }

    @Override
    public EnvironmentLogic findDefault() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<EnvironmentLogic> findAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean update(EnvironmentLogic environment) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
