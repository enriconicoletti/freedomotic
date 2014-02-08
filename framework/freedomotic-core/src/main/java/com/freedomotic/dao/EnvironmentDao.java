package com.freedomotic.dao;

import com.freedomotic.environment.EnvironmentLogic;
import com.freedomotic.exceptions.DaoLayerException;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA. User: nicoletti Date: 8/11/13 Time: 1:55 PM To
 * change this template use File | Settings | File Templates.
 */
// Interface that all EnvironmentDAOs must support
public interface EnvironmentDao {

    public void init() throws DaoLayerException;

    public void close() throws DaoLayerException;

    public void flush() throws DaoLayerException;

    public EnvironmentLogic insert(EnvironmentLogic environment);

    public boolean delete(EnvironmentLogic environment);

    public EnvironmentLogic findByName(String name);

    public EnvironmentLogic findByUuid(String id);

    public EnvironmentLogic findDefaultEnvironment();

    public Collection<EnvironmentLogic> findAll();

    public boolean update(EnvironmentLogic environment);

}
