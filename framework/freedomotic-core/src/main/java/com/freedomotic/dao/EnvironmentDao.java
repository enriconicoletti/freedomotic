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

    void init() throws DaoLayerException;

    void close() throws DaoLayerException;

    void flush() throws DaoLayerException;

    boolean delete(EnvironmentLogic environment);

    boolean update(EnvironmentLogic environment);

    EnvironmentLogic insert(EnvironmentLogic environment);

    EnvironmentLogic findByName(String name);

    EnvironmentLogic findByUuid(String id);

    EnvironmentLogic findDefault();

    Collection<EnvironmentLogic> findAll();

}
