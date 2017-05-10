package infovis.example.io;

import infovis.table.DefaultTable;
import infovis.table.io.SODASTableReader;
import infovis.table.io.SODASTableWriter;
import infovis.table.io.XMLTableWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
* An example for writing an XML file from a SDS file
* 
* @version $Revision: 1.2 $
* @author Elie Naulleau
*/
public class XMLTableWriterExample {
	/**
	 * @param args
	 *            input and output files
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 1 || args.length > 4) {
			System.err.println("Syntax: <input sds file> <output xml file>");
			System.exit(1);
		}

		File file = new File(args[args.length - 2]);
		File outputfile = new File(args[args.length - 1]);

		System.err.println("input sds  file =" + file.getAbsolutePath());
		System.err.println("output xml file =" + outputfile.getAbsolutePath());

		long t01 = System.currentTimeMillis();
		
		DefaultTable table = new DefaultTable();
		SODASTableReader reader;
		reader = new SODASTableReader(new FileInputStream(file), table);

		if (reader.load()) {
			long t02 = System.currentTimeMillis();
			System.out.println("DefaultTable loaded successfully in "+ String.valueOf(t02-t01) + " ms.");

			// Now writing the table as a XML file
			long t1 = System.currentTimeMillis();
			XMLTableWriter writer = new XMLTableWriter(
					new FileOutputStream(outputfile), "test", table);
			boolean r = writer.write();
			long t2 = System.currentTimeMillis();

			if (r)
				System.out.println("XML file written successfully in " + String.valueOf(t2-t1) + " ms.");
			else
				System.err.println("XML file write failure");
			

			
		} else {
			System.out.println("ERROR: DefaultTable not loaded successfully");
		}
	}
}
