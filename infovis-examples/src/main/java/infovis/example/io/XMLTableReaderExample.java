package infovis.example.io;

import infovis.table.DefaultTable;
import infovis.table.io.SODASTableWriter;
import infovis.table.io.XMLTableReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
* An example for reading an infovis table file previously marshalled to XML with XStream.
* 
* @version $Revision: 1.2 $
* @author Elie Naulleau
*/

public class XMLTableReaderExample {
	/**
	 * @param args xml input and sds output files
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 1 || args.length > 4) {
			System.err.println("Syntax: <input xml file> <output sds file>");
			System.exit(1);
		}

		File file = new File(args[args.length - 2]);
		File outputfile = new File(args[args.length - 1]);

		System.err.println("input xml  file =" + file.getAbsolutePath());
		System.err.println("output sds file =" + outputfile.getAbsolutePath());
	
		XMLTableReader reader = new XMLTableReader(new FileInputStream(file),  null);
		
		if (reader.load()) {
			System.out.println("DefaultTable loaded successfully from XML file, nbr of columns = "+ reader.getTable().getColumnCount());
			// Now writing the table as a SDS file
			SODASTableWriter writer = new SODASTableWriter(new FileOutputStream(outputfile), "test", reader.getTable());
			boolean  r = writer.write();
			
			if (r)
				System.out.println("SDS file written successfully");
			else
				System.err.println("SDS file write failure");
			

			
		} else {
			System.out.println("ERROR: DefaultTable not loaded successfully");
		}
	}
}
