/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.table.io;

import java.io.IOException;
import java.io.InputStream;

import infovis.Table;
import infovis.graph.io.DOTGraphReader;
import infovis.io.WrongFormatException;
import org.apache.log4j.Logger;

/**
 * Class SODASTableReader
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 * 
 * @infovis.factory TableReaderFactory sds
 */
public class SODASTableReader extends AbstractTableReader {
    private static final Logger      logger = Logger.getLogger(DOTGraphReader.class);
    protected SODASLexer  lexer;
    protected SODASParser parser;
    
    /**
     * Creates a Reader with an input stream and a table.
     * @param in the input stream
     * @param table the table
     */
    public SODASTableReader(InputStream in, Table table) {
        super(in, "SODAS", table);
    }
    
    /**
     * Creates a Reader with an input stream, a name and a table.
     * @param in the input stream
     * @param name the name
     * @param table the table
     */
    public SODASTableReader(InputStream in, String name, Table table) {
        super(in, name, table);
    }
    /**
     * {@inheritDoc}
     */
    public boolean load() throws WrongFormatException {
        lexer = new SODASLexer(getIn());
        parser = new SODASParser(lexer);
        parser.setTable(getTable());
        try {
            parser.sodas();
        }
        catch(Exception e) {
            logger.error("Cannot parse file "+getName(), e);
            return false;
        }
        finally {
            try {
                getIn().close();
            } catch (IOException e) {
                logger.error("Error closing file " + getName(), e);
            }
        }
        return true;
    }

}
