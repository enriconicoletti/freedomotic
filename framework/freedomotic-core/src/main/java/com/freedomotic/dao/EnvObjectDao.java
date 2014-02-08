package com.freedomotic.dao;

import com.freedomotic.exceptions.DaoLayerException;
import com.freedomotic.objects.EnvObjectLogic;
import java.util.Collection;
import java.util.Set;

/**
 * Created with IntelliJ IDEA. User: nicoletti Date: 8/11/13 Time: 1:55 PM To
 * change this template use File | Settings | File Templates.
 */
public interface EnvObjectDao {

    public void init() throws DaoLayerException;

    public void close() throws DaoLayerException;

    public void flush() throws DaoLayerException;

    public EnvObjectLogic insert(EnvObjectLogic obj);

    public boolean delete(EnvObjectLogic obj);

    public EnvObjectLogic findByName(String name);

    public EnvObjectLogic findByUuid(String id);

    public EnvObjectLogic findByAddress(String protocol, String address);

    public Collection<EnvObjectLogic> findByProtocol(String protocol);

    public Collection<EnvObjectLogic> findByTags(Set<String> tag);

    public Collection<EnvObjectLogic> findByEnvironment(String uuid);

    public Collection<EnvObjectLogic> findAll();

    public boolean update(EnvObjectLogic obj);
}
