/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.io;

import infovis.Column;
import infovis.Table;
import infovis.column.ColumnFactory;
import infovis.column.ColumnFilter;
import infovis.column.filter.ComposeOrFilter;
import infovis.column.filter.FilterNone;
import infovis.column.filter.IOColumnFilter;
import infovis.column.filter.InternalFilter;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.bzip2.CBZip2OutputStream;

/**
 * Abstract base class for Table writers.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 */
public abstract class AbstractWriter {
    private OutputStream   out;
    private BufferedWriter wout;
    private String         encoding;
    protected Table        table;
    protected String       name;
    protected StringBuffer buffer;
    /** DefaultTable of column labels */
    private ArrayList      columnLabels;
    private ColumnFilter   columnFilter = new ComposeOrFilter(
            InternalFilter.sharedInstance(), 
            IOColumnFilter.sharedInstance());
    

    /**
     * Constructor for AbstractWriter
     * 
     * @param name the name
     * @param table
     *            the Table.
     * @throws IOException if the underlying stream throws it
     */
    public AbstractWriter(String name, Table table) throws IOException {
        this(open(name), name, table);
    }
    /**
     * Constructor for AbstractWriter
     *
     * @param name the name
     * @param out
     *            the Writer
     * @param table
     *            the Table.
     */
    public AbstractWriter(OutputStream out, String name, Table table) {
        this.out = out;
        this.table = table;
        this.buffer = new StringBuffer();
    }

    /**
     * @return the name of the writer
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a type name from a column.
     * 
     * @param col
     *            the column
     * @return a type name for creating a column of that class.
     */
    public static String namedType(Column col) {
        return ColumnFactory.getInstance().getTypeName(col);
    }
    

    /**
     * Adds the name of a Column to write to the list of written column.
     * 
     * @param name
     *            the Column name.
     */
    public void addColumnLabel(String name) {
        if (columnLabels == null) {
            columnLabels = new ArrayList();
        }

        columnLabels.add(name);
    }
    
    /**
     * @return the columnFilter
     */
    public ColumnFilter getColumnFilter() {
        return columnFilter;
    }
    
    /**
     * @param columnFilter the columnFilter to set
     */
    public void setColumnFilter(ColumnFilter columnFilter) {
        this.columnFilter = columnFilter;
    }
    
    /**
     * Adds the name of all the non-internal Columns to the list of written
     * columns.
     */
    public void addAllColumnLabels() {
        int col;
        ColumnFilter f = getColumnFilter();
        if (f == null) {
            f = FilterNone.getSharedInstance();
        }

        for (col = 0; col < table.getColumnCount(); col++) {
            Column c = table.getColumnAt(col);

            if (f.filter(c)) {
                continue;
            }

            addColumnLabel(c.getName());
        }
    }

    /**
     * Returns the name of the column to write at a specified index.
     * 
     * @param col
     *            the index.
     * 
     * @return the name of the column to write at a specified index.
     */
    public String getColumnLabelAt(int col) {
        return (String) columnLabels.get(col);
    }

    /**
     * Returns the ArrayList of column names to write.
     * 
     * @return the ArrayList of column names to write.
     */
    public ArrayList getColumnLabels() {
        return columnLabels;
    }

    /**
     * @return the output stream
     */
    public OutputStream getOut() {
        if (wout != null) {
            return null;
        }
        return out;
    }
    
    /**
     * @return a writer, creating it if necessary using
     * the encoding.
     */
    public BufferedWriter getWriter() {
        if (wout == null)
            try {
                if (encoding != null) {
                    wout = new BufferedWriter(new OutputStreamWriter(
                            out,
                            encoding));
                }
                else {
                    wout = new BufferedWriter(new OutputStreamWriter(out));
                }
            } catch (UnsupportedEncodingException e) {
                wout = new BufferedWriter(new OutputStreamWriter(out));
            }
        return wout;
    }

    /**
     * Write one character in the output writer.
     * 
     * @param c
     *            the character.
     * @throws IOException if the underlying stream sends it
     */
    public final void write(char c) throws IOException {
        getWriter().write(c);
    }

    /**
     * Write a string in the output writer.
     * 
     * @param s
     *            the string.
     * @throws IOException if the underlying stream sends it
     */
    public final void write(String s) throws IOException {
        if (s == null)
            return;
        else if (s.equals("")) {
            s = "\'\'";
        }
        getWriter().write(s);
    }

    /**
     * Write the StringBuffer in the output writer and clears it.
     * @throws IOException if the underlying stream sends it
     */
    public void writeBuffer() throws IOException {
        getWriter().write(buffer.toString());
        buffer.setLength(0);
    }

    /**
     * Write a newline in the ouput writer.
     * @throws IOException if the underlying stream sends it
     */
    public void writeln() throws IOException {
        writeBuffer();
        write('\n');
    }

    /**
     * Returns the specified string quoted.
     * @param s the string
     * @return the string quoted
     */
    public String quoteString(String s) {
        StringBuffer sb = new StringBuffer();
        sb.append('"');
        int i = s.indexOf('"');
        int j = 0;
        while (i != -1) {
            sb.append(s.substring(j, i-1));
            sb.append("\\\"");
            j = i+1;
            i = s.indexOf('"', j);
        }
        if (j != s.length()) {
            sb.append(s.substring(j, s.length()));
        }
        sb.append('"');
        return sb.toString();
    }
    
    /**
     * Flushes te stream.
     * @throws IOException if the underlying stream sends it
     */
    public void flush() throws IOException {
        getWriter().flush();
    }

    /**
     * Abstract method that performs the actual writing of data.
     * @return true if the file has been correctly written
     */
    public abstract boolean write();

    /**
     * @return the encoding.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the encoding.
     * @param encoding the encoding name
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    
    /**
     * Open an output stream given a file name.
     * @param name the file name
     * @return the output stream
     * @throws IOException
     */
    public static OutputStream open(String name)
            throws IOException {
        OutputStream out = null;
        OutputStream os = new FileOutputStream(name);

        if (name.endsWith(".gz") || name.endsWith(".Z")) {
            os = new GZIPOutputStream(os);
        }
        else if (name.endsWith(".bz2")) {
            os = new CBZip2OutputStream(os);
        }
        out = new BufferedOutputStream(os);

        return out;
    }
    
    /**
     * Close open files.
     * @throws IOException if close throws the exception
     */
    public void close() throws IOException {
        if (wout != null) {
            wout.close();
            wout = null;
        }
        if (out != null) {
            out.close();
            out = null;
        }
    }
}
