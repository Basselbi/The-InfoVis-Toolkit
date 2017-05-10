/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.table.io;

import infovis.Column;
import infovis.DynamicTable;
import infovis.Table;
import infovis.utils.RowIterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;

import org.apache.log4j.Logger;

/**
 * Read an Excel CSV format into a table.
 * 
 * @version $Revision: 1.34 $
 * @author Jean-Daniel Fekete
 * 
 * @infovis.factory TableReaderFactory csv
 */
public class CSVTableReader extends AbstractTableReader {
    /** Field separator. */
    private char                separator         = ';';
    /** Number of lines to skip at the beginning of the file. */
    private int                 skipLines;
    /** True if first line is labels. */
    private boolean             labelLinePresent  = true;
    /** True if types are declared. */
    private boolean             typeLinePresent   = true;
    /** DefaultTable of column labels */
    protected ArrayList         labels            = new ArrayList();
    /** True is considering quotes */
    private boolean             consideringQuotes = true;
    protected transient boolean emptyField        = false;
    protected transient int     row               = 0;
    protected CSVReader         reader;

    private static final Logger LOG               = Logger.getLogger(CSVTableReader.class);

    /**
     * Constructor.
     * 
     * @param in
     *            the input stream
     * @param table
     *            the table
     */
    public CSVTableReader(InputStream in, Table table) {
        this(in, "CVS", table);
        setCommentChar('#');
    }

    /**
     * Constructor.
     * 
     * @param in
     *            the input stream
     * @param name
     *            the name
     * @param table
     *            the table
     */
    public CSVTableReader(InputStream in, String name, Table table) {
        super(in, name, table);
    }

    /**
     * Returns the column separator character.
     * 
     * @return char the column separator character.
     */
    public char getSeparator() {
        return separator;
    }

    /**
     * Sets the column separator character.
     * 
     * @param separator
     *            the column separator character. The separator to set
     */
    public void setSeparator(char separator) {
        this.separator = separator;
    }

    /**
     * Returns <code>true</code> if the first line contains labels.
     * 
     * @return <code>true</code> if the first line contains labels.
     */
    public boolean isLabelLinePresent() {
        return labelLinePresent;
    }

    /**
     * Sets whether the first line contains labels.
     * 
     * @param labelLinePresent
     *            <code>true</code> the first line contains labels.
     */
    public void setLabelLinePresent(boolean labelLinePresent) {
        this.labelLinePresent = labelLinePresent;
    }

    /**
     * Returns <code>true</code> if a line containing the types should be
     * read.
     * 
     * @return <code>true</code> if a line containing the types should be
     *         read.
     */
    public boolean isTypeLinePresent() {
        return typeLinePresent;
    }

    /**
     * Sets the typeLinePresent.
     * 
     * @param typeLinePresent
     *            The typeLinePresent to set
     */
    public void setTypeLinePresent(boolean typeLinePresent) {
        this.typeLinePresent = typeLinePresent;
    }

    /**
     * Returns the consideringQuotes.
     * 
     * @return boolean
     */
    public boolean isConsideringQuotes() {
        return consideringQuotes;
    }

    /**
     * Sets the consideringQuotes.
     * 
     * @param consideringQuotes
     *            The consideringQuotes to set
     */
    public void setConsideringQuotes(boolean consideringQuotes) {
        this.consideringQuotes = consideringQuotes;
    }

    /**
     * Returns the number of lines to skip at the begining of the file.
     * 
     * @return the number of lines to skip at the begining of the file.
     */
    public int getSkipLines() {
        return skipLines;
    }

    /**
     * Sets the number of lines to skip at the begining of the file.
     * 
     * @param skipLines
     *            the number of lines to skip at the begining of the file.
     */
    public void setSkipLines(int skipLines) {
        this.skipLines = skipLines;
    }

    /**
     * Returns a default field name for a specified index.
     * 
     * @param index
     *            the index.
     * 
     * @return a default field name for a specified index.
     */
    public String defaultFieldName(int index) {
        buffer.setLength(0);

        for (; index >= 0; index = (index / 26) - 1) {
            buffer.insert(0, (char) ((char) (index % 26) + 'A'));
        }

        return getField();
    }

    /**
     * Returns the label at the specified index.
     * 
     * @param index
     *            the index
     * 
     * @return the label at the specified index.
     */
    public String getLabelAt(int index) {
        while (labels.size() <= index) {
            if (labels.size() != 0) {
                LOG.warn("At line "+row+": Extra index created "+index);
            }
            labels.add(defaultFieldName(labels.size()));
        }

        return (String) labels.get(index);
    }

    /**
     * Adds a label.
     * 
     * @param name
     *            the label
     */
    public void addLabel(String name) {
        labels.add(name);
    }

    /**
     * Returns the column at the specified index in the label table.
     * 
     * @param index
     *            the index
     * 
     * @return the column at the specified index in the label table or null if
     *         the index is invalid or no column of that name exist.
     */
    public Column getColumnAt(int index) {
        String label = getLabelAt(index);
        if (label == null)
            return null;
        return table.getColumn(label);
    }

    protected void disableNotify() {
        table.disableNotify();
    }

    protected void enableNotify() {
        table.enableNotify();
    }

    /**
     * Adds the value of specified column.
     * 
     * @param column
     *            DOCUMENT ME!
     * @param field
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws ParseException
     *             DOCUMENT ME!
     */
    public boolean addField(int column, String field) throws ParseException {
        String label = getLabelAt(column);
        Column col = table.getColumn(label);

        if (col == null) {
            String type = guessFieldType(field);
            col = createColumn(type, label);

            if (col == null) {
                throw new ParseException(
                        "couldn't guess the type of field at column " + column,
                        0);
            }

            table.addColumn(col);
        }

        if (table instanceof DynamicTable) {
            DynamicTable dt = (DynamicTable) table;
            while (dt.getLastRow() < row)
                dt.addRow();
        }
        col.setValueOrNullAt(row, field);

        return true;
    }

    /**
     * @see infovis.io.AbstractReader#load()
     */
    public boolean load() {
        reader = new CSVReader(
                getBufferedReader(), 
                separator, 
                (consideringQuotes ? '"' : 0),
                skipLines);        
        try {
            readLabels();
            readTypes();

            readLines();
        } catch (ParseException e) {
            LOG.error("While loading a CSV file", e);
            return false;
        } catch (IOException e) {
            LOG.error("While loading a CSV file", e);
            return false;
        } finally {
            try {
                close();
            } catch (IOException e) {
                LOG.error("Closing a CSV file", e);
            }
            reader = null;
        }

        return true;
    }

    protected void readLines() throws IOException, ParseException {
        try {
            disableNotify();
            RowIterator iter = table.iterator();
            if (iter.hasNext()) {
                row = iter.nextRow();
            }
            else {
                row = 0;
                iter = null;
            }
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (consideringQuotes 
                        && (line[0].length() == 0 
                            || line[0].charAt(0)=='#')) 
                    continue;

                for (int column = 0; column < line.length; column++) {
                    String field = line[column];
                    addField(column, field);
                }
                if (iter != null && iter.hasNext()) {
                    row = iter.nextRow();
                }
                else {
                    row++;
                    iter = null;
                }
            }
        } finally {
            enableNotify();
        }
    }

    protected void readTypes() throws IOException, ParseException {
        if (! typeLinePresent)
            return;

        String[] line = reader.readNext();
        if (line == null) 
            return;
        for (int column = 0; column < line.length; column++) {
            String typeName = line[column];
            if (typeName == null) {
                typeName = "";
            }
            else {
                typeName = typeName.trim();
            }

            if (! typeName.isEmpty()) {
                String label = getLabelAt(column);
                Column col = createColumn(typeName, label);
                if (col == null) {
                    throw new ParseException(
                            "couldn't understand the type " + typeName
                                    + " of field at column " + column,
                            0);
                }
                table.addColumn(col);
            }
        }
    }
    
//    /**
//     * {@inheritDoc}
//     */
//    public String getField() {
//        if (emptyField) return null;
//        return super.getField();
//    }

    protected void readLabels() throws IOException {
        if (! labelLinePresent)
            return;
        
        String[] line = reader.readNext();
        if (line == null) 
            return;
        for (int column = 0; column < line.length; column++) {
            String label = line[column];
            if (label != null && ! label.isEmpty()) { 
                addLabel(label);
            }
        }
    }

    /**
     * Loads the specified file in the specified table.
     * 
     * @param file
     *            the file
     * @param t
     *            the table
     * @return true if the loading was successful.
     */
    public static boolean load(File file, Table t) {
        try {
            CSVTableReader loader = new CSVTableReader(
                    new FileInputStream(file),
                    t);
            return loader.load();
        } catch (FileNotFoundException e) {
            return false;
        }
    }
}
