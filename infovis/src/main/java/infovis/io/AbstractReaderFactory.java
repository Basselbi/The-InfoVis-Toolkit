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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.SwingUtilities;

import org.apache.commons.compress.bzip2.CBZip2InputStream;
import org.apache.log4j.Logger;

/**
 * Abstract factory of table readers.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.26 $
 */
public abstract class AbstractReaderFactory extends BasicFactory {
    private static Logger LOG = Logger.getLogger(AbstractReaderFactory.class);
    
    protected static Component frame;

    /** ID for ActionEvent when asynchronous read is OK. */
    public static final int READ_OK = 0;
    /** ID for ActionEvent when asynchronous read has been interrupted. */
    public static final int READ_INTERRUPTED = 1;
    /** ID for ActionEvent when asynchronous read has an error. */
    public static final int READ_ERROR = -1;
    /** Name for ActionEvent when asynchronous read is OK. */
    public static final String READ_OK_MSG = "READ_OK";
    /** Name for ActionEvent when asynchronous read has been interrupted. */
    public static final String READ_ERROR_MSG = "READ_ERROR";
    /** Name for ActionEvent when asynchronous read has an error. */
    public static final String READ_INTERRUPTED_MSG = "READ_INTERRUPTED";

    /**
     * Constructor for TableReaderFactory.
     * @param factoryName the name of the factory
     */
    public AbstractReaderFactory(String factoryName) {
        super(factoryName);
    }
    
    /**
     * @return the frame
     */
    public static Component getFrame() {
        return frame;
    }
    
    /**
     * Sets the frame for creating a ProgressMonitor
     * @param f the frame to set
     */
    public static void setFrame(Component f) {
        frame = f;
    }

    /**
     * Adds a creator of table reader.
     * 
     * @param c
     *            the Creator.
     */
    public void add(Creator c) {
        putEntry(c.getName(), c);
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
        return c==entry.remove(c.getName());
    }

    /**
     * Returns an iterator over the added entry.
     * 
     * @return an iterator over the added entry.
     */
    public Iterator iterator() {
        return entry.values().iterator();
    }

    /**
     * Returns the Creator with the specified name.
     * @param name the name
     * @return a Creator or null
     */
    public Creator getCreatorNamed(String name) {
        return (Creator)getEntry(name);
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
     * @return an <code>AbstractReader</code> able to read the specified
     *         resource name or <code>null</code>.
     */
    public AbstractReader create(String name, Table table) {
        AbstractReader ret = null;
        for (Iterator iter = iterator(); iter.hasNext(); ) {
            Creator c = (Creator)iter.next();
            ret = c.create(name, table);
            if (ret != null)
                break;
        }
        return ret;
    }

    /**
     * Creates an abstract reader on a specified input stream with a name on
     * a table
     * @param in the input stream
     * @param name the name
     * @param table the table
     * @return an AbstractReader if a creator is found or null otherwise
     */
    public AbstractReader create(InputStream in, String name, Table table) {
        AbstractReader ret = null;
        for (Iterator iter = iterator(); iter.hasNext(); ) {
            Creator c = (Creator)iter.next();
            ret = c.create(in, name, table);
            if (ret != null)
                break;
        }
        return ret;
    }

    /**
     * Tries to read a table.
     * @param name the name 
     * @param table the table
     * @return true if the table has been read.
     */
    public boolean tryRead(String name, Table table) {
        AbstractReader ret = create(name, table);
        if (ret != null) {
            try {
                boolean ok = ret.load();
                if (ok)
                    return ok;
            } catch (Exception e) {
                LOG.error("Cannot read table", e);
            }
        }
        return false;
    }
    
    /**
     * Tries to read a table asynchronously and calls the action when done.
     * @param name the name 
     * @param table the table
     * @param action the action listener to call when finished
     */
    public void tryRead(String name, Table table, ActionListener action) {
        AbstractReader ret = create(name, table);
        tryRead(ret, action);
    }

    /**
     * Load the reader in a thread and calls the action with "READ_OK" or "READ_ERROR"
     * depending on the result.
     * @param reader the reader to load
     * @param action the action to perform
     */
    public static void tryRead(final AbstractReader reader, final ActionListener action) {
        if (reader == null) {
            action.actionPerformed(
                    new ActionEvent(
                            action, //FIXME cannot put the null reader 
                            READ_INTERRUPTED, 
                            READ_INTERRUPTED_MSG));
        }
        Thread t = new Thread(new Runnable() {
            public void run() {
                ActionEvent ev;
                try {
                    if (reader != null && reader.load()) { 
                        ev = new ActionEvent(reader, READ_OK, READ_OK_MSG);
                    }
                    else {
                        ev = new ActionEvent(reader, READ_ERROR, READ_ERROR_MSG);
                    }
                }
                catch(Exception e) {
                    ev = new ActionEvent(reader, READ_INTERRUPTED, READ_INTERRUPTED_MSG);
                }
                final ActionEvent nev = ev;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        action.actionPerformed(nev);
                    }
                });
            }
        });
        t.setName("InfoVis Loader");
        t.start();
    }

    /**
     * Interface for Table Reader entry.
     */
    public static interface Creator {
        /**
         * @return the Creator's name
         */
        public String getName();

        /**
         * Creates an AbstractReader from a name on a table
         * @param name the name
         * @param table the table
         * @return an AbstractCreator or null
         */
        public AbstractReader create(String name, Table table);
        
        /**
         * Creates an AbstractReader on a table from a name and an input stream.
         * @param in the input stream
         * @param name the name
         * @param table the table
         * @return an AbstractCreator or null
         */
        public AbstractReader create(InputStream in, String name,
                Table table);
    }

    /**
     * <b>AbstractCreator</b> is an abstract class for file creators.
     * 
     * @author Jean-Daniel Fekete
     */
    public abstract class AbstractCreator implements Creator {
        protected String suffix;
        protected boolean needingOpen;

        /**
         * Creator.
         * @param suffix the file suffix
         * @param needingOpen true if the creator need opening the file
         */
        public AbstractCreator(String suffix, boolean needingOpen) {
            this.suffix = suffix;
            this.needingOpen = needingOpen;
        }
        
        /**
         * Creator needing opening.
         * @param suffix the file suffix
         */
        public AbstractCreator(String suffix) {
            this(suffix, true);
        }

        /**
         * {@inheritDoc}
         */
        public String getName() {
            return suffix;
        }

        /**
         * Opens the specified input stream, decompressing it if necessary 
         * @param name the file name
         * @param decompress true if it should be decompressed
         * @return an Input Stream
         * @throws IOException if input exception occurs
         * @throws FileNotFoundException if the file name does not exist
         */
        public InputStream open(String name, boolean decompress)
                throws IOException, FileNotFoundException {
            if (! needingOpen) {
                return null;
            }
            InputStream is = null;
            int size = 0;
            
            if (name.indexOf(':') != -1) { 
                try {
                    URL url = new URL(name);
                    URLConnection con = url.openConnection();
                    is = con.getInputStream();
                    size = con.getContentLength();
                } catch (Exception e) {
                    ; // Ignore 
                    //LOG.warn("Opening file "+name, e);
                }
            }
            if (is == null) {
                is = getClass().getClassLoader().getResourceAsStream(name);
                if (is == null) {
                    is = new FileInputStream(name);
                }
                size = is.available();
            }
            
            if (frame != null) {
                ProgressMonitorInputStream p = 
                    new ProgressMonitorInputStream(frame, "Loading "+name, is);
                is = p;
                ProgressMonitor m = p.getProgressMonitor();
//                if (m.getMaximum() > 100000) { //FIXME pb in progressMonitor
                    m.setMillisToPopup(1000);
                    m.setMillisToDecideToPopup(200);
//                    m.setProgress((m.getMaximum()/100)+1);
                    m.setMaximum(size);
//                }
            }

            if (decompress) {
                if (name.endsWith(".Z") || name.endsWith(".gz")) {
                    is = new GZIPInputStream(is);
                }
                else if (name.endsWith(".bz2")) {
                    int b1 = is.read();
                    int b2 = is.read();
                    assert(b1 == 'B' && b2 == 'Z');
                    is = new CBZip2InputStream(is);                    
                }
            }
            if (is != null && ! (is instanceof BufferedInputStream)) {
                is = new BufferedInputStream(is);
            }

            return is;
        }

        /**
         * {@inheritDoc}
         */
        public AbstractReader create(String name, Table table) {
            boolean decompress = false;
            String realName = name;
            if (! needingOpen) {
                return create(null, name, table);
            }
            if (name.endsWith(".gz") 
                    || name.endsWith(".Z")
                    || name.endsWith(".bz2")) {
                decompress = true;
                realName = name.substring(0, name.lastIndexOf('.'));
            }
            if (!realName.endsWith("." + suffix))
                return null;
            try {
                return create(open(name, decompress), name, table);
            } catch (Exception e) {
                LOG.error("Creating from file "+name, e);
                return null;
            }
        }
    }
}