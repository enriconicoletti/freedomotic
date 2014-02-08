package com.freedomotic.dao.xml;

/**
 * Implementation of EnvironmentDao interface to read XML file from filesystem.
 * This class should be kept hidden outside  package
 */
import com.google.inject.Guice;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.freedomotic.dao.EnvironmentDao;
import com.freedomotic.dao.EnvironmentDao;
import com.freedomotic.environment.EnvironmentLogic;
import com.freedomotic.exceptions.DaoLayerException;
import com.freedomotic.model.environment.Environment;
import com.freedomotic.model.environment.Zone;
import com.freedomotic.persistence.FreedomXStream;
import com.freedomotic.util.DOMValidateDTD;
import com.freedomotic.util.Info;
import com.freedomotic.util.SerialClone;
import java.io.BufferedWriter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

class XmlEnvironmentDao implements EnvironmentDao {

    //this class logger
    private static final Logger LOG = Logger.getLogger(XmlEnvironmentDao.class.getName());
    //to store the unserialized environments
    private static final List<EnvironmentLogic> environments = new ArrayList<EnvironmentLogic>();
    //maps loaded env uuid to the related file
    private static final Map<String, File> filesystem = new HashMap<String, File>();
    //the base filesystem folder in which all environments are saved as XML files (one env for each subfolder)
    private static final File FOLDER = new File(Info.PATH_WORKDIR + "/data/furn/");
    //flag for initialization
    private static boolean alreadyInitialized;

    @Inject
    public XmlEnvironmentDao() {
        //clear previuous content, if any (this is shared through different class instances)
        //and force reinitialization
        environments.clear();
        alreadyInitialized = false;
        try {
            init();
        } catch (DaoLayerException ex) {
            Logger.getLogger(XmlEnvironmentDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initialize the filesystem DAO (XML files). Loads any environment XML into
     * a Java data structure. No need to call it, automatically managed in the constructor
     *
     * @throws DaoLayerException
     */
    @Override
    public final void init() throws DaoLayerException {
        if (!alreadyInitialized) {
            if (!FOLDER.isDirectory()) {
                throw new IllegalArgumentException("Expect this to be a directory not a file: " + FOLDER);
            }

            FileFilter directoryFilter = new FileFilter() {
                @Override
                public boolean accept(File folder) {
                    return folder.isDirectory();
                }
            };

            File[] dirs = FOLDER.listFiles(directoryFilter);
            for (File dir : dirs) {
                if (dir.isDirectory()) {
                    loadFromFilesystem(dir);
                }
            }
        }
        //set the initialized flag
        alreadyInitialized = true;
    }

    @Override
    public final void close() throws DaoLayerException {
        flush();
    }

    /**
     * Inserts the given environment to the set of available environments. It is
     * not persisted on hard disk, it exists just in memory. Only a deep defense
     * copy of the environment is added so changes to the original environment
     * do not affect the stored environment.
     *
     * @param environment
     * @return true if the environments collection is altered after the
     * operation, false otherwise
     */
    @Override
    public EnvironmentLogic insert(EnvironmentLogic environment) {
        if ((environment == null)
                || (environment.getPojo() == null)
                || (environment.getPojo().getName() == null)
                || environment.getPojo().getName().isEmpty()) {
            throw new IllegalArgumentException("Cannot insert a not valid environment");
        }
        //defensive copy to not affect the passed object with the changes
        Environment pojoCopy = SerialClone.clone(environment.getPojo());
        //setPojo() creates a valid UUIDshould be the last called 
        //after using setters on envLogic.getPojo()
        environment.setPojo(pojoCopy);
        //initialize the cloned environment
        environment.init();
        if (environments.add(environment)) {
            return environment;
        }
        return null;
    }

    @Override
    public boolean delete(EnvironmentLogic environment) {
        return environments.remove(environment);
    }

    @Override
    public EnvironmentLogic findByName(String name) {
        for (EnvironmentLogic env : environments) {
            if (env.getPojo().getName().equalsIgnoreCase(name)) {
                return env;
            }
        }
        return null;
    }

    /**
     * Finds an environment which matches (case sensitive) the uuid in input.
     * The uuid is a string different from the progressive integer of the list
     * in which the environment is saved.
     *
     * @param uuid
     * @return the <link>EnvironmentLogic<link> object, null if not found
     */
    @Override
    public EnvironmentLogic findByUuid(String uuid) {
        for (EnvironmentLogic env : environments) {
            if (env.getPojo().getUUID().equals(uuid)) {
                return env;
            }
        }
        return null;
    }

    /**
     * Returns the default environment. Its the first defined in the XML file.
     *
     * @return an <link>EnvironmentLogic<link>
     */
    @Override
    public EnvironmentLogic findDefaultEnvironment() {
        return environments.get(0);
    }

    /**
     * Get all the loaded environments.
     *
     * @return An unmodifiable Collection of EnvironmentLogic objects
     */
    @Override
    public Collection<EnvironmentLogic> findAll() {
        return Collections.unmodifiableCollection(environments);
    }

    /**
     * Removes and inserts again the environment in input. The environment data
     * are defensive copied and the environment object is re-initialized. It can
     * be expensive if called in a loop. Be sure to do all the changes to the
     * object and then commit them calling this method.
     *
     * @param environment
     * @return true if the collection state is changed, false otherwise. If the
     * object in input is NOT already in the list, it cannot be updated so false
     * is returned and no change is done.
     */
    @Override
    public boolean update(EnvironmentLogic environment) {
        //if the old one is succesfully removed
        if (environments.remove(environment)) {
            if (insert(environment) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Force changes to be persisted on disk
     *
     * @return
     * @throws it.freedomotic.exceptions.DaoLayerException
     */
    @Override
    public void flush() throws DaoLayerException {
        saveToFilesystem();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (EnvironmentLogic environment : environments) {
            buffer.append(environment.getPojo().getName());
            //append "," if not the last element of the list
            if ((environments.size() - 1) != environments.indexOf(environment)) {
                buffer.append(", ");
            }
        }
        return buffer.toString();
    }

    /* 
     **************************************************************************
     * Here below is the private part of this class, it contains the machinery
     * used to load and save XML files.
     **************************************************************************/
    /**
     * Deserialize all environments XML file found in the given folder and loads
     * the into memory which is used like a database for CRUD operations.
     *
     * @param folder
     * @throws DaoLayerException
     */
    private void loadFromFilesystem(File folder) throws DaoLayerException {
        if (folder == null) {
            throw new IllegalArgumentException("Cannot load environments if the source folder is not specified");
        }

        // This filter only returns env files
        FileFilter envFileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile() && file.getName().endsWith(".xenv");
            }
        };

        //deserialize all *.xenv files in the given folder
        File[] files = folder.listFiles(envFileFilter);
        for (File file : files) {
            Environment pojo = deserialize(file);
            EnvironmentLogic environment = Guice.createInjector(new InjectXmlDao()).getInstance(EnvironmentLogic.class);
            if (pojo == null) {
                throw new IllegalStateException("Environment data cannot be null at this stage. Something went wrong during deserialization");
            }
            environment.setPojo(pojo);
            environment.setSource(file);
            if (insert(environment) == null) {
                throw new DaoLayerException("Cannot insert the environment "
                        + environment.getPojo().getName());
            }
        }
    }

    /**
     * Serialize all environments objects saving them in the given folder.
     *
     * @param folder
     * @throws DaoLayerException
     */
    private void saveToFilesystem() throws DaoLayerException {
        for (EnvironmentLogic env : environments) {
            final File file = filesystem.get(env.getPojo().getUUID());
            if (env.getPojo().getUUID() == null || env.getPojo().getUUID().isEmpty()) {
                throw new IllegalStateException("The environment UUID should not be null"
                        + " or empty while serializing the environment to xml file");
            }

            //serialization of a previouly loaded environment
            if (file != null && file.exists()) {
                file.delete();
                serialize(env.getPojo(), file);
            } else {
                //serialization of a new environment
                File folder = new File(Info.PATH_DATA_FOLDER + "/furn"
                        + "/"
                        + env.getPojo().getName()
                        .replaceAll(" ", "-")
                        .toLowerCase()
                        .trim());
                LOG.config("Serializing NEW environment to " + folder);
                if (!folder.exists()) {
                    folder.mkdir();
                    new File(folder + "/data").mkdir();
                    new File(folder + "/data/obj").mkdir();
                    new File(folder + "/data/rea").mkdir();
                    new File(folder + "/data/trg").mkdir();
                    new File(folder + "/data/cmd").mkdir();
                    new File(folder + "/data/resources").mkdir();
                }
                serialize(env.getPojo(),
                        new File(folder + "/" + env.getPojo().getUUID() + ".xenv"));
            }
        }

    }

    /**
     * Persists the given environment to hard disk as xml file. It uses XStream
     * to convert java object into XML.
     *
     * @param env
     * @param file
     * @throws IOException
     */
    private void serialize(Environment env, File file) throws DaoLayerException {
        XStream xstream = FreedomXStream.getEnviromentXstream();

        for (Zone zone : env.getZones()) {
            zone.setObjects(null);
        }

        String xml = xstream.toXML(env);
        FileWriter fstream;
        BufferedWriter out = null;

        try {
            LOG.config("Serializing environment to " + file);
            fstream = new FileWriter(file);
            out = new BufferedWriter(fstream);
            out.write(xml);
            //Close the output stream
            LOG.info("Application environment " + env.getName() + " succesfully serialized");
        } catch (IOException ex) {
            throw new DaoLayerException(ex.getMessage(), ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    LOG.severe(ex.getMessage());
                }
            }
        }
    }

    /**
     * Reads an XML file from hard disk and converts it into a java object. It
     * uses XStream to perform the conversion.
     *
     * @param file
     * @return
     * @throws DaoLayerException
     */
    private Environment deserialize(final File file) throws DaoLayerException {
        XStream xstream = FreedomXStream.getXstream();
        //validate the object against a predefined DTD
        String xml;
        try {
            xml = DOMValidateDTD.validate(file, Info.PATH_CONFIG_FOLDER + "/validator/environment.dtd");
        } catch (IOException ex) {
            throw new DaoLayerException("Cannot validate the environment againt the XML DTD", ex);
        }
        Environment pojo;
        try {
            pojo = (Environment) xstream.fromXML(xml);
            filesystem.put(pojo.getUUID(), file);
            return pojo;
        } catch (XStreamException e) {
            throw new DaoLayerException("XML parsing error. Readed XML is \n" + xml, e);
        }
    }
}
