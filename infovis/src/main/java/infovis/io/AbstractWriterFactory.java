/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.io;

import infovis.Table;
import infovis.utils.BasicFactory;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

/**
 * Class AbstractWriterFactory
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public abstract class AbstractWriterFactory extends BasicFactory {
    static Logger logger   = Logger.getLogger(AbstractReaderFactory.class);

    /**
     * Creator with a name.
     * @param factoryName the name
     */
    public AbstractWriterFactory(String factoryName) {
        super(factoryName);
    }

    /**
     * Adds a creator of table reader.
     * 
     * @param c
     *            the Creator.
     */
    public void add(Creator c) {
        entry.put(c.getName(), c);
    }

    /**
     * Removes a creator of table reader.
     * 
     * @param c
     *            the Creator.
     * 
     * @return <code>true</code> if the Creator was removed.
     */
    public boolean remove(Creator c) {
        return entry.remove(c.getName())==c;
    }

    /**
     * Returns the creator registered with the specified name
     * 
     * @param name
     *            the name
     * @return the Creator or null
     */
    public Creator getCreatorNamed(String name) {
        return (Creator)entry.get(name);
    }

    /**
     * Returns an <code>AbstractReader</code> able to read the specified
     * resource name or <code>null</code>.
     * 
     * @param name
     *            the resource name.
     * @param table
     *            the table.
     * 
     * @return an <code>AbstractWriter</code> able to read the specified
     *         resource name or <code>null</code>.
     */
    public AbstractWriter create(String name, Table table) {
        int offset = name.lastIndexOf('.');
        if (offset == -1) return null;
        String suffix = name.substring(offset+1);
        if (suffix.equalsIgnoreCase("z") || suffix.equalsIgnoreCase("gz")) {
            int o2 = name.lastIndexOf('.', offset-1);
            if (o2 == -1) {
                return null;
            }
            suffix = name.substring(o2+1, offset);
        }
        Creator c = getCreatorNamed(suffix);
        if (c == null) {
            return null;
        }
        return c.create(name, table);
    }

    /**
     * Tries writing the specified table in the file/stream with the specified name.
     * @param name the name
     * @param table the table
     * @return true if it has been written
     */
    public boolean tryWrite(String name, Table table) {
        AbstractWriter ret = create(name, table);
        if (ret != null) {
            try {
                boolean ok = ret.write();
                if (ok) {
                    return ok;
                }
            } catch (Exception e) {
                logger.error("While writing table "+name, e);
            }
        }
        return false;
    }

    /**
     * <b>Creator</b> is the interface for writers.
     */
    public interface Creator {
        /** @return the name of the creator. */
        public String getName();
        /** @return the file suffix */
        public String getSuffix();
        /**
         *  Creates a writer with a specified name and table.
         * @param name the name
         * @param table the table
         * @return a writer
         */
        public AbstractWriter create(String name, Table table);
        /**
         *  Creates a writer with a specified name and table in an output stream.
         * @param out the output stream 
         * @param name the name
         * @param table the table
         * @return a writer
         */
        public AbstractWriter create(OutputStream out, String name, Table table);
    }

    /**
     * <b>AbstractCreator</b> is a base class for implementing creators.
     * 
     */
    public abstract static class AbstractCreator implements Creator {
        protected String name;
        protected String suffix;

        /**
         * Creator with a name and suffix.
         * @param name the name
         * @param suffix the suffix
         */
        public AbstractCreator(String name, String suffix) {
            this.name = name;
            this.suffix = suffix;
        }

        /**
         * {@inheritDoc}
         */
        public String getName() {
            return name;
        }

        /**
         * {@inheritDoc}
         */
        public String getSuffix() {
            return suffix;
        }

        /**
         * Convenient method to open a compressed stream.
         * @param name the file name
         * @param compress true if compressed
         * @return an outputstream
         * @throws IOException in case of io problem reported by the stream
         */
        public OutputStream open(String name, boolean compress)
                throws IOException {
            OutputStream out = null;
            OutputStream os = new FileOutputStream(name);

            if (compress) {
                os = new GZIPOutputStream(os);
            }
            out = new BufferedOutputStream(os);

            return out;
        }

        /**
         * {@inheritDoc}
         */
        public AbstractWriter create(String name, Table table) {
            boolean compress = false;
            if (name.endsWith(".gz") || name.endsWith(".Z")) {
                compress = true;
            }
            try {
                return create(open(name, compress), name, table);
            } catch (IOException e) {
                logger.error("Cannot create file for " + name, e);
                return null;
            }
        }
    }
}
