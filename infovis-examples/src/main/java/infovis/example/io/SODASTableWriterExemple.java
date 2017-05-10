/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/

package infovis.example.io;

import infovis.table.DefaultTable;
import infovis.table.io.SODASTableReader;
import infovis.table.io.SODASTableWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * Class SODASTableWriterExample
 * 
 * @author Elie Naulleau
 * @version $Revision: 1.3 $
 */
public class SODASTableWriterExemple {

	/**
	 * @param args input and output files
	 */
	public static void main(String[] args)  throws Exception {
		if (args.length < 1 || args.length > 4) {
			System.err.println("Syntax: <input file> <output file>");
			System.exit(1);
		}
		
		File file = new File(args[args.length - 2]);
		File outputfile = new File(args[args.length - 1]);
		
		System.err.println("input sds  file ="+file.getAbsolutePath());
		System.err.println("output sds file ="+outputfile.getAbsolutePath());
		
		DefaultTable table = new DefaultTable();
		SODASTableReader reader;
		reader = new SODASTableReader(new FileInputStream(file), table);

		if (reader.load()) {
			System.out.println("DefaultTable loaded successfully");
			
			// Now writing the table as a SDS file
			SODASTableWriter writer = new SODASTableWriter(new FileOutputStream(outputfile), "test", table);
			boolean  r = writer.write();
			
			if(r) 
				System.out.println("Parsed SDS file written successfully");
			else
				System.err.println("SDS file write failure");
			// More testing : 
			// Re-read the output file
			// Compare the two tables ...
			
			DefaultTable table2 = new DefaultTable();
			SODASTableReader reader2;
			reader2 = new SODASTableReader(new FileInputStream(outputfile), table2);

			if (reader2.load()) {
				System.out.println("DefaultTable RE-loaded successfully");
				
				
				if(table.equals(table2)) {
					System.out.println("Original and reloaded table comparison failed (equal==false)");
				} else {
					System.out.println("Original and reloaded table comparison succeeded");
				}
				
			} else
				System.out.println("ERROR : RE-written file not loaded successfully");
		} else {
			System.out.println("ERROR: DefaultTable not loaded successfully");
		}
//		JFrame frame = new JFrame("SODAS File " + file.toString());
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		JTable jtable = new JTable(table);
//		jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		JScrollPane pane = new JScrollPane(jtable,
//				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
//				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		frame.getContentPane().add(pane);
//		frame.pack();
//		frame.setVisible(true);
	}

}
