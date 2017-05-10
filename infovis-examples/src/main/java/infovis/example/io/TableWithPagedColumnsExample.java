/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.example.io;

import infovis.column.PagedDoubleColumn;
import infovis.column.PagedFloatColumn;
import infovis.column.PagedIntColumn;
import infovis.table.DefaultTable;
import infovis.utils.Permutation;
import infovis.utils.RowComparator;

import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Class TableWithPagedColumnsExample Simple pour étudier le comportement des
 * PagedColumns
 * 
 * @author EN
 * @version $Revision: 1.3 $
 */
public class TableWithPagedColumnsExample {
	/**
	 * Main program.
	 * 
	 * @param args
	 *            argument list
	 * @throws Exception
	 *             when file does not exist
	 */
	public static void main(String args[]) throws Exception {

		int nrows = 1000000; // 1 millions , -Xmx768M as jvm param, sinon out
		// of memory
		// Avec 4 millions, met environ 4 minutes à afficher la table (disques
		// 10000rpm, Xeon quadCore 2.66Ghz, 4Go RAM, jvm 1.6_03

		DefaultTable table = new DefaultTable();
		System.setProperty("infovis.poolsize", "64M");
		int reserve = 10; // ? quelle sémantique pour ce paramètre liée à un
		// ensureCapacity ulétrieur?
		// Si >120 => null pointer exception
		PagedIntColumn c0 = new PagedIntColumn("C0", reserve);
		final PagedDoubleColumn c1 = new PagedDoubleColumn("C1", reserve);
		PagedIntColumn c2 = new PagedIntColumn("C2", reserve);
		PagedFloatColumn c3 = new PagedFloatColumn("C3", reserve);
		PagedDoubleColumn c4 = new PagedDoubleColumn("C4", reserve);
		table.addColumn(c0);
		table.addColumn(c1);
		table.addColumn(c2);
		table.addColumn(c3);
		table.addColumn(c4);
		/*
		 * Permutation perm = new Permutation( c1.iterator() ); perm.sort(new
		 * RowComparator() { public int compare(int row1, int row2) { double v1 =
		 * c1.getDoubleAt(row1); double v2 = c1.getDoubleAt(row2); if (v1>v2) {
		 * return -1; } else if (v2>v1) { return 1; } else return 0; }
		 * 
		 * public boolean isValueUndefined(int row) { return true; } });
		 */

		Random random = new Random();

		for (int i = 0; i < nrows; i++) {
			c0.add(i);
		}

		for (int i = 0; i < nrows; i++) {

			c1.add(random.nextDouble());
		}

		for (int i = 0; i < nrows; i++) {

			c2.add(random.nextInt());

		}

		for (int i = 0; i < nrows; i++) {

			c3.add(random.nextFloat());

		}

		for (int i = 0; i < nrows; i++) {

			c4.add(random.nextDouble());
		}

		/*
		 * Permutation perm = visualization.getPermutation(); perm.sort(new
		 * RowComparator() { public int compare(int row1, int row2) { if
		 * (tree.isLeaf(row1) && tree.isLeaf(row2)) { return size.compare(row1,
		 * row2); } else { return order.compare(row1, row2); } }
		 * 
		 * public boolean isValueUndefined(int row) { return
		 * order.isValueUndefined(row); } });
		 * visualization.setPermutation(perm);
		 */

		JFrame frame = new JFrame("Table, nrows= " + String.valueOf(nrows));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTable jtable = new JTable(table);
		jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane pane = new JScrollPane(jtable);
		frame.getContentPane().add(pane);
		frame.pack();
		frame.setVisible(true);
	}

	/*
	 * SI System.setProperty("infovis.poolsize", "128M"); Alors cette exception
	 * est émise:
	 * 
	 * Exception in thread "main" java.lang.IllegalArgumentException: Negative
	 * position at sun.nio.ch.FileChannelImpl.write(Unknown Source) at
	 * infovis.utils.ByteBufferPool.pageOut(ByteBufferPool.java:322) at
	 * infovis.utils.ByteBufferPool.pageIn(ByteBufferPool.java:354) at
	 * infovis.utils.ByteBufferPool$Ref.getBuffer(ByteBufferPool.java:421) at
	 * infovis.column.PagedDoubleColumn.getChunk(PagedDoubleColumn.java:71) at
	 * infovis.column.ChunkedDoubleColumn.set(ChunkedDoubleColumn.java:107) at
	 * infovis.column.ChunkedDoubleColumn.setExtend(ChunkedDoubleColumn.java:129)
	 * at infovis.column.AbstractDoubleColumn.add(AbstractDoubleColumn.java:93)
	 * at
	 * infovis.example.io.TableWithPagedColumnsExample.main(TableWithPagedColumnsExample.java:59)
	 * 
	 */
}
