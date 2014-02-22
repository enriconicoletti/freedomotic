package com.freedomotic.dao.xml;

import com.freedomotic.dao.EnvObjectDao;
import com.freedomotic.environment.EnvironmentPersistence;
import com.freedomotic.exceptions.DaoLayerException;
import com.freedomotic.model.object.EnvObject;
import com.freedomotic.objects.EnvObjectFactory;
import com.freedomotic.objects.EnvObjectLogic;
import com.freedomotic.persistence.FreedomXStream;
import com.freedomotic.util.DOMValidateDTD;
import com.freedomotic.util.Info;
import com.freedomotic.util.SerialClone;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: nicoletti Date: 8/11/13 Time: 2:03 PM To
 * change this template use File | Settings | File Templates.
 */
class XmlEnvObjectDao implements EnvObjectDao {

    private static final Logger LOG = Logger.getLogger(XmlEnvObjectDao.class.getName());
    //contains the loaded objects indexed using the object name as the key
    private static Map<String, EnvObjectLogic> objects = new HashMap<String, EnvObjectLogic>();
    //flag for initialization
    private static boolean alreadyInitialized;
    //the filesystem path from which to load objects xml files
    private File folder;

    @Inject
    public XmlEnvObjectDao() throws DaoLayerException {
        //clear previuous content, if any (this is shared through different class instances)
        //and force reinitialization
        //this.folder = folder;
        objects.clear();
        alreadyInitialized = false;
        //init();
    }

    @Override
    public final void init() throws DaoLayerException {
        if (!alreadyInitialized) {
            if (!folder.isDirectory()) {
                throw new IllegalArgumentException("Expect this to be a directory not a file: " + folder);
            }

            FileFilter directoryFilter = new FileFilter() {
                @Override
                public boolean accept(File folder) {
                    return folder.isDirectory();
                }
            };

            File[] dirs = folder.listFiles(directoryFilter);
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
    public void close() throws DaoLayerException {
        flush();
    }

    @Override
    public void flush() throws DaoLayerException {
        saveToFilesystem();
    }

    @Override
    public EnvObjectLogic insert(EnvObjectLogic obj) {
        if ((obj == null)
                || (obj.getPojo() == null)
                || (obj.getPojo().getName() == null)
                || obj.getPojo().getName().isEmpty()) {
            throw new IllegalArgumentException("This is not a valid object");
        }

        //defensive copy to not affect the passed object with the changes
        EnvObject pojoCopy = SerialClone.clone(obj.getPojo());
        //search for same or similar names and append an ordinal roman number if needed
        int occurrences = 0;
        for (EnvObjectLogic item : objects.values()) {
            if (item.getPojo().getName().trim().toLowerCase()
                    .startsWith(item.getPojo().getName().toLowerCase())) {
                occurrences++;
            }
        }
        if (occurrences > 1) {
            pojoCopy.setName(obj.getPojo().getName() + " " + convertToRoman(occurrences));
        } else {
            pojoCopy.setName(obj.getPojo().getName());
        }
        //reset the other paramenters
        pojoCopy.setProtocol(obj.getPojo().getProtocol());
        pojoCopy.setPhisicalAddress("unknown");
        pojoCopy.getCurrentRepresentation().getOffset().setX(obj.getPojo().getCurrentRepresentation().getOffset().getX() + 60);
        pojoCopy.getCurrentRepresentation().getOffset().setY(obj.getPojo().getCurrentRepresentation().getOffset().getY() + 60);
        pojoCopy.setUUID(UUID.randomUUID().toString());

        try {
            obj = EnvObjectFactory.create(pojoCopy);
        } catch (DaoLayerException ex) {
            LOG.warning(ex.getMessage());
        }

        if (!objects.containsKey(obj.getPojo().getName())) {
            obj.init();
            objects.put(obj.getPojo().getName(), obj);
            obj.setChanged(true);
        } else {
            throw new RuntimeException("Cannot add the same object more than one time");
        }

        return obj;
    }

    @Override
    public boolean delete(EnvObjectLogic obj) {
        EnvObjectLogic remove = objects.remove(obj.getPojo().getName());
        //force repainting on frontends clients
        obj.setChanged(true);
        //free memory
        obj.destroy();
        //true if not null
        return (remove != null);
    }

    @Override
    public EnvObjectLogic findByName(String name) {
        for (EnvObjectLogic obj : objects.values()) {
            if (obj.getPojo().getName().equalsIgnoreCase(name)) {
                return obj;
            }
        }
        return null;
    }

    @Override
    public EnvObjectLogic findByUuid(String uuid) {
        for (EnvObjectLogic obj : objects.values()) {
            if (obj.getPojo().getUUID().equalsIgnoreCase(uuid)) {
                return obj;
            }
        }
        return null;
    }

    @Override
    public Collection<EnvObjectLogic> findAll() {
        return Collections.unmodifiableCollection(objects.values());
    }

    @Override
    public boolean update(EnvObjectLogic obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EnvObjectLogic findByAddress(String protocol, String address) {
        if ((protocol == null)
                || (address == null)
                || protocol.trim().equalsIgnoreCase("unknown")
                || address.trim().equalsIgnoreCase("unknown")
                || protocol.isEmpty()
                || address.isEmpty()) {
            throw new IllegalArgumentException("Protocol or address values are not valid (eg: null)");
        }

        for (EnvObjectLogic obj : objects.values()) {
            if ((obj.getPojo().getProtocol().equalsIgnoreCase(protocol.trim()))
                    && (obj.getPojo().getPhisicalAddress().equalsIgnoreCase(address.trim()))) {
                return obj;
            }
        }
        return null;
    }

    @Override
    public Collection<EnvObjectLogic> findByProtocol(String protocol) {
        ArrayList<EnvObjectLogic> list = new ArrayList<EnvObjectLogic>();
        for (EnvObjectLogic obj : objects.values()) {
            if (obj.getPojo().getProtocol().equalsIgnoreCase(protocol.trim())) {
                list.add(obj);
            }
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public Collection<EnvObjectLogic> findByTags(Set<String> tags) {
        ArrayList<EnvObjectLogic> list = new ArrayList<EnvObjectLogic>();
        // search every object for at least one tag 
        for (EnvObjectLogic obj : objects.values()) {
            if (obj.getPojo().getTagsList().containsAll(tags)) {
                list.add(obj);
            }
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public Collection<EnvObjectLogic> findByEnvironment(String uuid) {
        ArrayList<EnvObjectLogic> list = new ArrayList<EnvObjectLogic>();
        for (EnvObjectLogic obj : objects.values()) {
            if (obj.getPojo().getEnvironmentID().equalsIgnoreCase(uuid)) {
                list.add(obj);
            }
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Loads the object file from file but NOT add the object to the list
     *
     * @param file
     * @return an EnvObjectLogic or null
     * @throws it.freedomotic.exceptions.DaoLayerException
     */
    private EnvObjectLogic loadFromFilesystem(File file) throws DaoLayerException {
        XStream xstream = FreedomXStream.getXstream();
        try {
            String xml = DOMValidateDTD.validate(file, Info.getApplicationPath()
                    + "/config/validator/object.dtd");

            EnvObject pojo = (EnvObject) xstream.fromXML(xml);
            EnvObjectLogic obj = EnvObjectFactory.create(pojo);
            LOG.log(Level.FINE, "Created a new logic for {0} of type {1}",
                    new Object[]{obj.getPojo().getName(),
                        obj.getClass().getCanonicalName().toString()});
            return obj;
        } catch (IOException ex) {
            throw new DaoLayerException(ex.getMessage(), ex);
        } catch (XStreamException e) {
            throw new DaoLayerException(e.getMessage(), e);
        }
    }

    private void saveToFilesystem() throws DaoLayerException {
        if (objects.isEmpty()) {
            throw new DaoLayerException("There are no object to persist, " + folder.getAbsolutePath()
                    + " will not be altered.");
        }

        if (!folder.isDirectory()) {
            throw new DaoLayerException(folder.getAbsoluteFile() + " is not a valid object folder. Skipped");
        }

        try {
            XStream xstream = FreedomXStream.getXstream();
            //cleanup the folder
            deleteObjectFiles(folder);

            for (EnvObjectLogic obj : objects.values()) {
                String uuid = obj.getPojo().getUUID();

                if ((uuid == null) || uuid.isEmpty()) {
                    obj.getPojo().setUUID(UUID.randomUUID().toString());
                }

                if ((obj.getPojo().getEnvironmentID() == null)
                        || obj.getPojo().getEnvironmentID().isEmpty()) {
                    obj.getPojo()
                            .setEnvironmentID(EnvironmentPersistence.getEnvironments().get(0).getPojo().getUUID());
                }

                String fileName = obj.getPojo().getUUID() + ".xobj";
                BufferedWriter out = new BufferedWriter(new FileWriter(folder + "/" + fileName));
                //persist only the data not the logic
                out.write(xstream.toXML(obj.getPojo()));
                //Close the output stream
                out.close();
            }
        } catch (IOException ex) {
            throw new DaoLayerException(ex);
        }
    }

    private void deleteObjectFiles(File folder) throws DaoLayerException {
        if ((folder == null) || !folder.isDirectory()) {
            throw new IllegalArgumentException("Unable to delete objects files "
                    + "in a null or not valid folder");
        }

        // This filter only returns object files
        FileFilter objectFileFileter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile() && file.getName().endsWith(".xobj");
            }
        };

        File[] files = folder.listFiles(objectFileFileter);

        for (File file : files) {
            boolean deleted = file.delete();
            if (!deleted) {
                throw new DaoLayerException("Unable to delete file " + file.getAbsoluteFile());
            }
        }
    }

    /**
     * Returns the roman number representation of an int
     *
     * @param number
     * @return
     */
    private String convertToRoman(int number) {
        String riman[] = {"M", "XM", "CM", "D", "XD", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        int arab[] = {1000, 990, 900, 500, 490, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (number > 0 || arab.length == (i - 1)) {
            while ((number - arab[i]) >= 0) {
                number -= arab[i];
                result.append(riman[i]);
            }
            i++;
        }
        return result.toString();
    }
}
