/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.io;

import infovis.Column;
import infovis.column.ColumnFactory;
import infovis.column.StringColumn;
import infovis.column.format.UTCDateFormat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.ParseException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.bzip2.CBZip2InputStream;

/**
 * Base class for all the readers.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.30 $
 */
public abstract class AbstractReader {
    private InputStream in;
    private String      name;
    private String      encoding;

    /**
     * Constructor for AbstractReader.
     * 
     * @param name
     *            the resource name.
     * @throws IOException if the underlying stream throws it
     * @throws FileNotFoundException if the file is not found
     */
    public AbstractReader(String name) 
    throws IOException, FileNotFoundException {
        this(open(name), name);
    }
    
    
    /**
     * Constructor for AbstractReader.
     * 
     * @param in
     *            the <code>InputStream</code> for input.
     * @param name
     *            the resource name.
     */
    public AbstractReader(InputStream in, String name) {
        this.in = in;
        this.name = name;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the input <code>InputStream</code>.
     * 
     * @return the input <code>InputStream</code>.
     */
    public InputStream getIn() {
        return in;
    }
    /**
     * Sets the <code>InputStream</code>.
     * 
     * @param in
     *            the <code>InputStream</code>
     */
    public void setIn(InputStream in) {
        this.in = in;
    }

    /**
     * Closes the reader.
     * @throws IOException when the underlying stream does so
     */
    public void close() throws IOException {
        in.close();
        in = null;
    }

    /**
     * Guess the field class from a value.
     * 
     * @param field
     *            the value.
     * 
     * @return A field class.
     */
    public static String guessFieldType(String field) {
        if ((field == null) || (field.length() == 0)) {
            return "string";
        }

        char c = field.charAt(0);

        if (Character.isDigit(c) || c == '-' || c == '+'
                || (c == '.' && Character.isDigit(field.charAt(1)))) {
            UTCDateFormat date = UTCDateFormat.getSharedInstance();
            try {
                date.parse(field);
                return "date";
            } catch (ParseException e) {
                ; // fall through
            }
            try {
                Integer.parseInt(field);
                return "integer";
            } catch (NumberFormatException e) {
                ; // fall through
            }
            try {
                Float.parseFloat(field);
                return "float";
            } catch (NumberFormatException e) {
                ; // fall through
            }
        }

        return "string";
    }
    
    /**
     * Creates a column of the specified type and name.
     * @param type the type name
     * @param label the column name
     * @return a column of the specified type and name
     */
    public static Column createColumn(String type, String label) {
        Column col = ColumnFactory.createColumn(type, label);
        if (col == null) {
            col = new StringColumn(label);
        }
        return col;
    }

    /**
     * Returns a BufferedReader associated with this stream,
     * creating it if it does not exist yet.
     * @return a BufferedReader associated with this stream.
     */
    public BufferedReader getBufferedReader() {
        try {
            if (encoding == null) {
                return new BufferedReader(new InputStreamReader(in));
            }
            else {
                return new BufferedReader(new InputStreamReader(in, encoding));
            }
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * Main method for loading the file.
     * 
     * The loading may fail at any point, leaving the table in an indefinite
     * state if the methods returns false.
     * 
     * @return true if the file has been loaded without error, false otherwise.
     * @throws WrongFormatException if the format is not the one expected.
     */
    public abstract boolean load() throws WrongFormatException;

    /**
     * Returns the default encoding of this reader.
     * @return  the default encoding of this reader
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the default encoding of this reader.
     * @param encoding the default encoding of this reader.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Opens the file name and returns a stream.
     * @param name the file name
     * @return a stream
     * @throws IOException if the file is not a gzip file
     * @throws FileNotFoundException if the file does not exist
     */
    public static InputStream open(String name) 
    throws FileNotFoundException, IOException {
        InputStream is = null;
        if (name.indexOf(':') != -1) {
            try {
                URL url = new URL(name);
                is = url.openStream();
            } catch (Exception e) {
                ; // Ignore 
                //LOG.warn("Opening file "+name, e);
            }
        }
        if (is == null) {
            is = new FileInputStream(name);
        }

        if (name.endsWith(".gz") || name.endsWith(".Z")) {
            is = new GZIPInputStream(is);
        }
        else if (name.endsWith(".bz2")) {
            int b1 = is.read();
            int b2 = is.read();
            assert(b1 == 'B' && b2 == 'Z');
            is = new CBZip2InputStream(is);
        }
        if (is != null && ! (is instanceof BufferedInputStream)) {
            is = new BufferedInputStream(is);
        }

        return is;
    }
}
