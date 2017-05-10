package infovis.table.io;

import java.io.IOException;
import java.io.OutputStream;

import com.thoughtworks.xstream.XStream;

import infovis.Table;
import infovis.io.AbstractWriter;
import infovis.table.DefaultTable;

/**
* An XML table writer based on XStream (http://xstream.codehaus.org/).
* 
* @version $Revision: 1.2 $
* @author Elie Naulleau
*/
public class XMLTableWriter extends AbstractWriter {
    /**
     * Creates a table writer.
     * @param out the output stream
     * @param name the name
     * @param table the table
     */
	public XMLTableWriter(OutputStream out, String name, Table table) {
		super(out, name, table);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean write() {

		XStream xstream = new XStream();
		XMLTableXStreamMapping.loadMap(xstream);
	
		try {
			// write ( xstream.toXML((DefaultTable)table));
			xstream.toXML((DefaultTable)table, getWriter());
			getWriter().flush();
			getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}
