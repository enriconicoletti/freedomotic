/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.plugins.filesystem;

import com.freedomotic.app.Freedomotic;
import com.freedomotic.util.Info;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.apache.log4j.Logger;

/**
 * Creates the instances of <code>BoundleLoader</code> used to loadBoundle plugins from
 * filesystem.
 * @author enrico
 */
class BoundleLoaderFactory {
    // Parameters

    private static final Class[] PARAMETERS = new Class[]{URL.class};

    /**
     * Takes in input the type of the plugins and from this creates a pointer to the right 
     * filesystem folder, used later to loadBoundle all plugins at this path.
     * @param type The type of the plugin (DEVICE, OBJECT, EVENT)
     * @return a list of object to trigger the loading of the plugin jar package
     */
    protected List<BoundleLoader> getBoundleLoaders(int type) {
        List results = new ArrayList<BoundleLoader>();
        File directory;

        switch (type) {
            case PluginsManager.TYPE_DEVICE:
                directory = Info.PATH_DEVICES_FOLDER;

                break;

            case PluginsManager.TYPE_EVENT:
                directory = Info.PATH_EVENTS_FOLDER;

                break;

            case PluginsManager.TYPE_OBJECT:
                directory = Info.PATH_OBJECTS_FOLDER;

                break;

            default:
                throw new AssertionError();
        }

        if (directory.isDirectory()) {
            //search in subfolder. Go down a level starting from /plugins/TYPE/. Is not real recursive
            for (File subfolder : directory.listFiles()) {
                if (subfolder.isDirectory()) {
                    results.add(getSingleBoundleLoader(subfolder));
                }
            }
        }

        return results;
    }

    /**
     * Returns a single plugin package factory that can be used to loadBoundle all the
     * plugins it has inside
     *
     * @param directory The filesystem folder from which loadBoundle the plugins. This
     * folder can have other subfolder that contains specific plugin types
     * (DEVICES, OBJECTS, EVENTS)
     * @return
     */
    protected BoundleLoader getSingleBoundleLoader(File directory) {
        //intantiate the right loader based on the directory passed to the searchIn method
        String devicesPath = new File(Info.PATH_PLUGINS_FOLDER + "/devices/").toString();

        if (directory.toString().startsWith(devicesPath)) {
            return new BoundleLoaderDevices(directory);
        }

        String objectsPath = new File(Info.PATH_PLUGINS_FOLDER + "/objects/").toString();

        if (directory.toString().startsWith(objectsPath)) {
            return new BoundleLoaderObjects(directory);
        }

        String eventsPath = new File(Info.PATH_PLUGINS_FOLDER + "/events/").toString();

        if (directory.toString().startsWith(eventsPath)) {
            return new BoundleLoaderEvents(directory);
        }

        throw new IllegalStateException("Cannot find a suitable plugin boundle loader to load plugins in " + directory.getAbsolutePath());
    }

    protected static List<String> getClassesInside(String jarName)
            throws IOException {
        ArrayList<String> classes = new ArrayList<String>(10);
        JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
        JarEntry jarEntry;

        try {
            while (true) {
                jarEntry = jarFile.getNextJarEntry();
                
                if (jarEntry == null) {
                    break;
                }
                
                if (jarEntry.getName().endsWith(".class")) {
                    classes.add(jarEntry.getName().replaceAll("/", "\\."));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BoundleLoaderFactory.class).warn(ex.getMessage());
        } finally {
            jarFile.close();
        }

        return classes;
    }

    protected static Class getClass(File file, String name)
            throws Exception {
        addURL(file.toURL());

        URLClassLoader clazzLoader;
        Class clazz;
        String filePath = file.getAbsolutePath();
        filePath = "jar:file://" + filePath + "!/";

        URL url = new File(filePath).toURL();
        clazzLoader = new URLClassLoader(new URL[]{url});
        clazz = clazzLoader.loadClass(name);

        return clazz;
    }

    private static void addURL(URL u)
            throws IOException {
        URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        URL[] urls = sysLoader.getURLs();

        for (int i = 0; i < urls.length; i++) {
            if (urls[i].toString().equalsIgnoreCase(u.toString())) {
                return;
            }
        }

        Class sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", PARAMETERS);
            method.setAccessible(true);
            method.invoke(sysLoader,
                    new Object[]{u});
        } catch (Throwable t) {
            LOG.severe(Freedomotic.getStackTraceInfo(t));
            throw new IOException("Error, could not add URL to system classloader");
        }
    }
    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(BoundleLoaderFactory.class.getName());
}
