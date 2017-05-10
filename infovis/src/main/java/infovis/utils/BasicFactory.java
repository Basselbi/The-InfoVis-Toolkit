/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Base class for factories.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public abstract class BasicFactory {
    private static Logger LOG = Logger.getLogger(BasicFactory.class);
    protected Map entry;
    private String name;
    
    /**
     * Create a BasicFactory with a specified name.
     * 
     * @param name the Factory name
     */
    public BasicFactory(String name) {
        this();
        this.name = name;
        addDefaultCreators(name);
    }
    
    /**
     * Creates a BasicFactory (you should set the name and load
     * the entry by yourself.
     */
    public BasicFactory() {
        entry = createMap();
    }
    
    protected void setName(String name) {
        this.name = name;
    }
    
    protected Map createMap() {
        return new HashMap();
    }
    
    /**
     * Loads a properties from a resource name.
     * @param factoryName the factory name
     * @return the properties
     */
    public static Properties loadProperties(String factoryName) {
        String resourceName = "resources/"+factoryName+".properties";
        InputStream in = 
            BasicFactory.class.getClassLoader().getResourceAsStream(resourceName);
        if (in != null)
            try {
            Properties props = new Properties();
            props.load(in);
            return props;
        }
        catch(FileNotFoundException e) {
            LOG.error("Cannot find factory file "+resourceName, e);
        }
        catch(IOException e) {
            LOG.error("Exception reading factory file "+resourceName, e);
        }        
        return null;
    }
    
    protected void addDefaultCreators(String factoryName) {
        name = factoryName;
        //if (true) return;
        Properties props = loadProperties(factoryName);
        if (props == null) {
            LOG.error("Cannot read factory properties from "+factoryName);
            throw new RuntimeException("Cannot read factory properties from "+factoryName);
        }
        for (int i = 0; i < 1000; i++) {
            String suffix = "." + i;
            String name = props.getProperty("name" + suffix);
            if (name == null) {
                break;
            }
            String data = props.getProperty("data1" + suffix);
            String className = props.getProperty("class"
                    + suffix);
            add(name, className, data);
        }
    }
    
    /**
     * Returns the entry associated with a specified name
     * in the Factory or null.
     * @param name the name
     * @return the entry of null
     */
    public Object getEntry(String name) {
        return entry.get(name);
    }
    
    /**
     * Associates a specified entry to a specified name
     * in the Factory using a priority.
     * @param name the name
     * @param o the entry
     * @return <code>true</code> if the entry has been
     * added
     */
    public boolean putEntry(String name, Object o) {
        return entry.put(name, o) != null;
    }
    
    /**
     * @return an Iterator over the names of the entries
     */
    public Iterator iterator() {
        return entry.keySet().iterator();
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    protected void addClass(String name, Class theClass) {
        putEntry(name, theClass);
    }
    
    protected Class getClassFor(String name) {
        return (Class)getEntry(name);
    }
    
    protected Object createFor(String name) {
        Class c = getClassFor(name);
        if (c == null) {
            return null;
        }
        try {
            Constructor cons = c.getConstructor((Class[])null);
            return cons.newInstance((Object[])null);
        } catch (Exception e) {
            LOG.error("Cannot call constructor for "+c.getName(), e);
            return null;
        }
    }
    
    /**
     * Adds a new creator in the factory.
     * @param name the name of the entry
     * @param className the class name responsible for creating it
     * @param data extra data given to the class
     */
    public abstract void add(String name, String className, String data);

}
