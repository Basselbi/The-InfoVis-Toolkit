/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/

package infovis.example.io;

import infovis.Column;
import infovis.table.DefaultTable;
import infovis.table.io.CSVTableReader;
import infovis.table.io.SODASConstants;
import infovis.table.io.SODASTableReader;
import infovis.table.io.SODASTableWriter;
import infovis.table.io.TableReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;


/**
 * Class SODASTableWriterExample
 * 
 * @author Elie Naulleau
 * @version $Revision: 1.6 $
 */
public class CSV2SODASTableConversionExemple {

	/**
	 * @param args input and output files
	 * @exception Exception if somethings goes wrong
	 */
	public static void main(String[] args) throws Exception {
	    
		if (args.length != 2) {
			System.err.println("Syntax: <input file> <output file>");
			System.exit(1);
		}
		File loggerConfig = new File("properties/log4j.properties");
        if (loggerConfig.exists()) {
            PropertyConfigurator.configure(loggerConfig.toString());
        } else {
            BasicConfigurator.configure();
        }
        File outputfile = new File(args[1]);

		DefaultTable table = new DefaultTable();

		CSVTableReader reader = new CSVTableReader(new FileInputStream(args[0]),	table);
		reader.setSeparator('\t');
		reader.setLabelLinePresent(true);
		reader.setTypeLinePresent(true);
	
		if (reader.load()) {

			
			System.out.println("Table "+args[0]+"loaded successfully");
			

			System.out.println("table.getColumnCount="+table.getColumnCount());
			for(int c=0; c<table.getColumnCount(); c++) {
			    Column col = table.getColumnAt(c);
				System.out.println("Col:"+c + " : "+col.getName()+"->"+col.getClass().getName());
			}
			
			
			// Now writing the table as a SDS file
			FileOutputStream out = new FileOutputStream(outputfile);
			SODASTableWriter writer = new SODASTableWriter(out, "test", table);
			boolean  r = writer.write();
			out.close();
			if(r) 
				System.out.println("SDS file written successfully");
			else
				System.err.println("SDS file write failure");
			// More testing : 
			// Re-read the output file
			// Compare the two tables ...
			
			DefaultTable table2 = new DefaultTable();
			SODASTableReader reader2;
			FileInputStream in = new FileInputStream(outputfile);
			reader2 = new SODASTableReader(in, table2);

			if (reader2.load()) {
				System.out.println("DefaultTable RE-loaded successfully");
				
				if (writer.getNameColumn()==null) {
				    Column c = table2.getColumn(SODASConstants.ATTR_NAME);
				    table2.removeColumn(c);
				}
				else {
				    String name = writer.getNameColumn().getName();
				    if (! name.equals(SODASConstants.ATTR_NAME)) {
				        Column c = table2.getColumn(SODASConstants.ATTR_NAME);
				        c.setName(name);
				    }
				}
				if (writer.getLibelColumn()!=null) {
                    String name = writer.getLibelColumn().getName();
                    if (! name.equals(SODASConstants.ATTR_LABEL)) {
                        Column c = table2.getColumn(SODASConstants.ATTR_LABEL);
                        c.setName(name);
                    }				    
				}
				if(table.equals(table2)) {
				    System.out.println("Original and reloaded table comparison succeeded");
				} else {
				    System.out.println("Original and reloaded table comparison failed (equal==false)");
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
	
	
	public static void fixCSVMetadata4SODAS(DefaultTable table) {
		
		table.getMetadata().addAttribute("CSV ORIGIN", Boolean.TRUE);
		
		// CONTAINS = ()
		
		table.getMetadata().addAttribute("HEADER", Boolean.TRUE);
	    table.getMetadata().addAttribute("INDIVIDUALS", Boolean.TRUE);
		table.getMetadata().addAttribute("VARIABLES", Boolean.TRUE);
		table.getMetadata().addAttribute("HEADER", Boolean.TRUE);
		table.getMetadata().addAttribute("RECTANGLE_MATRIX", Boolean.TRUE);
				
		// FILE  = ()
		table.getMetadata().addAttribute("procedure_name", "infovis.example.io.CSV2SODASTableConversionExemple");
		table.getMetadata().addAttribute("version", "0");
		table.getMetadata().addAttribute("create_date", (new Date()).toString());
		table.getMetadata().addAttribute("filiere_name", "seven");
		table.getMetadata().addAttribute("filiere_path", "?");
		table.getMetadata().addAttribute("base", "?");

		
		// HEADER  = ()
		System.err.println("BIS table.getColumnCount="+table.getColumnCount());
		
		table.getMetadata().addAttribute("title", "temp file");
		table.getMetadata().addAttribute("var_nb", String.valueOf(table.getColumnCount() ) );
		table.getMetadata().addAttribute("indiv_nb", String.valueOf(table.getRowCount()));
		
		
		int nb_nu=0;
		for(int r=0; r<table.getRowCount(); r++) {
			for(int c=0; c<table.getColumnCount(); c++) {
				Object o = table.getValueAt(r, c);
				if(o==null)
				nb_nu++;
			}
		}
		
		table.getMetadata().addAttribute("nb_null", String.valueOf(nb_nu));
		
		
		
	}
	
	
	
}
