package infovis.table.io;

import java.io.InputStream;
import com.thoughtworks.xstream.XStream;

import infovis.Table;
import infovis.io.WrongFormatException;
import infovis.table.DefaultTable;


/**
* An XML table reader based on XStream (http://xstream.codehaus.org/).
* 
* @version $Revision: 1.3 $
* @author Elie Naulleau
*/
public class XMLTableReader  extends AbstractTableReader {

	  /**
     * Creates a Reader with an input stream and a table.
     * @param in the input stream
     * @param table the table
     */
    public XMLTableReader(InputStream in, Table table) {
        super(in, "XML", table);
    }
    
	public boolean load() throws WrongFormatException {
		XStream xstream = new XStream();
		XMLTableXStreamMapping.loadMap(xstream);
		
		try {
		table = (DefaultTable) xstream.fromXML(getIn());
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
