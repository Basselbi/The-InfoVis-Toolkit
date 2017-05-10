import infovis.Column;
import infovis.column.*;
import infovis.column.format.CategoricalFormat;
import infovis.data.DoubleInterval;
import infovis.data.Interval;
import infovis.data.IntervalColumn;
import infovis.utils.IntIntSortedMap;

import java.io.*;
import java.text.ParseException;

import javax.swing.text.MutableAttributeSet;

import junit.framework.TestCase;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.26 $
 */
public class ColumnsTest extends TestCase {

    public ColumnsTest(String name) {
        super(name);
    }
    

    public void testColumnProperties(Column column) {
        assertTrue("Null is never equals to a column", !column.equals(null));
        assertTrue("Column not a String", !column.equals("foo"));
        
        column.clear();
        assertEquals(column.size(), 0);
        assertEquals(column.isEmpty(), true);
        MutableAttributeSet attr = column.getClientProperty();
        assertEquals(attr.getAttributeCount(),0);
        attr = column.getMetadata();
        assertEquals(attr.getAttributeCount(),0);
        String name = column.getName();
        column.setName("#_12_");
        assertEquals(column.getName(), "#_12_");
        assertEquals(column.isInternal(), true);
        column.setName(name);
        assertEquals(column.getName(), name);
        assertEquals(column.isValueUndefined(10), true);
        try {
            column.setValueAt(10, null);
            assertEquals(11, column.size());
        }
        catch (ParseException e) {
            assertEquals(0, column.size());
        }
        
        column.setValueOrNullAt(10, "@@");
        if (! column.isValueUndefined(10)) {
            assertEquals(column.getValueAt(10),"@@");
        }

        int i;

        for (i = 0; i < 10; i++) {
            assertEquals(column.isValueUndefined(i), true);
        }
        try {
            column.setValueAt(2, "2");
            assertEquals(column.isValueUndefined(2), false);
        }
        catch (ParseException e) {
            assertEquals(column.isValueUndefined(2), true);
        }
        
        testSerializeColumn(column);
        column.clear();
        
    }
    
    public void testSerializeColumn(Column column) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutput sout = new ObjectOutputStream(out);
            sout.writeObject(column);
            sout.flush();
            sout.close();
        
            InputStream sin = new ByteArrayInputStream(out.toByteArray());
            ObjectInputStream s = new ObjectInputStream(sin);
            Column c2 = (Column)s.readObject();
            assertTrue("Serialized object differs from original", column.equals(c2));
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Exception while writing or readeing serialized object"+e.getMessage()); 
        }
    }

    public void testNumberColumnProperties(NumberColumn column) {
        testColumnProperties(column);

    }

    public void testIntColumn() {
        IntColumn column = new IntColumn("IntColumn");
        testIntcolumn(column);
    }
    
    public void testIntcolumn(AbstractIntColumn column) {
        testNumberColumnProperties(column);
        try {
            column.setValueAt(2, "32");
            assertEquals(column.get(2), 32);
        }
        catch (ParseException e) {
            assertTrue("Parse error for IntColumn", false);
        }
        try {
            column.setValueAt(2, "32.5");
            assertEquals(column.get(2), 32);
            assertTrue("Too lax parsing for IntColumn", false);
        }
        catch (ParseException e) {
            assertTrue("Parse error for IntColumn", true);
        }
        try {
            column.setValueAt(3, "");
            assertTrue(
                "Invalid empty value parsed for IntColumn",
                false);
        }
        catch (ParseException e) {
            assertTrue(
                "Row 3 should be undefined",
                column.isValueUndefined(3));
        }

        assertTrue(!column.isValueUndefined(2));
        assertEquals(32, column.getMin());
        assertEquals(32, column.getMax());

        column.setIntAt(1, 54);
        column.setExtend(3, 12);

        assertEquals(12,column.getMin());
        assertEquals(54, column.getMax());
        column.setValueUndefined(1, true);

        assertEquals(12, column.getMin());
        assertEquals(32, column.getMax());
        column.add(11);
        assertEquals(11, column.getMin());

        column.setExtend(100, 123);
        assertEquals(column.getMin(), 11);
        assertEquals(column.getMax(), 123);
        assertEquals(column.size(), 101);
    }
    
    public void testChunkedIntColumn() {
        ChunkedIntColumn column = new ChunkedIntColumn("ChunkedIntColumn");
        testIntcolumn(column);
    }
    
    public void testPagedIntColumn() {
        ChunkedIntColumn column = new PagedIntColumn("PagedIntColumn");
        testIntcolumn(column);
    }

    public void testIntSparseColumn() {
        IntSparseColumn column = new IntSparseColumn("IntColumn");
        testIntcolumn(column);
    }

    public void testLongColumn() {
        LongColumn column = new LongColumn("LongColumn");
        testLongColumn(column);
    }
    
    public void testDateColumn() {
        DateColumn column = new DateColumn("DateColumn");
        testNumberColumnProperties(column);
        //TODO
    }
    
    public void testLongColumn(LongColumn column) {
        testNumberColumnProperties(column);

        try {
            column.setValueAt(2, "32");
            assertEquals(column.get(2), 32);
        }
        catch (ParseException e) {
            assertTrue("Parse error", false);
        }
        try {
            column.setValueAt(2, "32.5");
            assertEquals(column.get(2), 32);
            assertTrue("Too lax parsing", false);
        }
        catch (ParseException e) {
            assertTrue("Parse error", true);
        }
        try {
            column.setValueAt(3, "");
            assertTrue("Invalid empty value parsed", false);
        }
        catch (ParseException e) {
            assertTrue(
                "Row 3 should be undefined",
                column.isValueUndefined(3));
        }

        assertTrue(!column.isValueUndefined(2));
        assertEquals(column.getMin(), 32);
        assertEquals(column.getMax(), 32);

        column.set(1, 54);
        column.setExtend(3, 12);

        assertEquals(column.getMin(), 12);
        assertEquals(column.getMax(), 54);
        column.setValueUndefined(1, true);

        assertEquals(column.getMin(), 12);
        assertEquals(column.getMax(), 32);

        column.setExtend(100, 12312312312312312L);
        assertEquals(column.getMin(), 12);
        assertEquals(column.getMax(), 12312312312312312L);
        assertEquals(column.size(), 101);

    }
    
    public void testFloatColumn(AbstractFloatColumn column) {
        testNumberColumnProperties(column);
        testSerializeColumn(column);
        //TODO
    }

    public void testFloatColumn() {
        FloatColumn column = new FloatColumn("FloatColumn");
        testFloatColumn(column);
    }
    
    public void testChunkedFloatColumn() {
        ChunkedFloatColumn column = new ChunkedFloatColumn("ChunkedFloatColumn");
        testFloatColumn(column);
    }
    
    public void testPagedFloatColumn() {
        PagedFloatColumn column = new PagedFloatColumn("PagedFloatColumn");
        testFloatColumn(column);
    }

    public void testDoubleColumn() {
        testDoubleColumn(new DoubleColumn("DoubleColumn"));
    }
    
    public void testChunkedDoubleColumn() {
        testDoubleColumn(new ChunkedDoubleColumn("ChunkedDoubleColumn"));
    }
    
    public void testPagedDoubleColumn() {
        testDoubleColumn(new PagedDoubleColumn("PagedDoubleColumn"));
    }
    
//    public void testColtDoubleColumn() {
//        testDoubleColumn(new ColtDoubleColumn("COltDoubleColumn"));
//    }

    public void testDoubleColumn(NumberColumn column) {
        testNumberColumnProperties(column);

        try {
            column.setValueAt(2, "32");
            assertEquals(column.getDoubleAt(2), 32.0, 0);
        }
        catch (ParseException e) {
            assertTrue("Parse error", false);
        }
        try {
            column.setValueAt(2, "32.5");
            assertEquals(column.getDoubleAt(2), 32.5, 0);
            assertTrue("Good parsing", true);
        }
        catch (ParseException e) {
            assertTrue("Parse error", false);
        }
        try {
            column.setValueAt(3, "");
            assertTrue("Invalid empty value parsed", false);
        }
        catch (ParseException e) {
            assertTrue(
                "Row 3 should be undefined",
                column.isValueUndefined(3));
        }

        assertTrue(!column.isValueUndefined(2));
        assertEquals(column.getDoubleMin(), 32.5, 0);
        assertEquals(column.getDoubleMax(), 32.5, 0);

        column.setDoubleAt(1, 54);
        column.setDoubleAt(3, 12);

        assertEquals(column.getDoubleMin(), 12, 0);
        assertEquals(column.getDoubleMax(), 54, 0);
        column.setValueUndefined(1, true);

        assertEquals(column.getDoubleMin(), 12, 0);
        assertEquals(column.getDoubleMax(), 32.5, 0);

        column.setDoubleAt(100, 1231231231.2312312);
        assertEquals(column.getDoubleMin(), 12, 0);
        assertEquals(column.getDoubleMax(), 1231231231.2312312, 0);
        assertEquals(column.size(), 101);
    }

    public void testStringColumn() {
        StringColumn column = new StringColumn("StringColumn");
        testColumnProperties(column);

    }

    public void testObjectColumn() {
        ObjectColumn column = new ObjectColumn("ObjectColumn");
        testColumnProperties(column);

    }

    public void testCategoricalColumn() {
        //CategoricalColumn column = new CategoricalColumn("CategoricalColumn");
        IntColumn column = new IntColumn("CategoricalColumn");
        column.setFormat(new CategoricalFormat());
        testColumnProperties(column);

        column.clear();
        column.setFormat(new CategoricalFormat());
        try {
            column.addValue("one");
        }
        catch (ParseException e) {
        }
        assertEquals(column.size(), 1);
        assertEquals(column.get(0), 0);
        assertEquals(column.getValueAt(0), "one");
        try {
            column.addValue("two");
        }
        catch (ParseException e) {
        }
        assertEquals(column.size(), 2);
        assertEquals(column.get(1), 1);
        assertEquals(column.getValueAt(1), "two");
        try {
            column.addValue("three");
        }
        catch (ParseException e) {
        }
        assertEquals(column.size(), 3);
        assertEquals(column.get(2), 2);
        assertEquals(column.getValueAt(2), "three");
        try {
            column.addValue("two");
        }
        catch (ParseException e) {
        }
        assertEquals(column.size(), 4);
        assertEquals(column.get(3), 1);
        assertEquals(column.getValueAt(3), "two");
    }
    
    public void testIntArrayColumn() {
        IntArrayColumn column = new IntArrayColumn("IntArrayColumn");
        testColumnProperties(column);
    }
    
    public void testIntervalColumn() {
        IntervalColumn column = new IntervalColumn("IntervalColumn");
        testColumnProperties(column);
        column.clear();
        Interval i1 = new DoubleInterval(1, 101);
        column.add(new DoubleInterval(0, 100));
        column.addValueOrNull("1:101");
        assertNotNull(column.getObjectAt(1));
        assertEquals(i1, column.getObjectAt(1));
        assertEquals(0, column.getMinIndex());
        assertEquals(1, column.getMaxIndex());
        column.addValueOrNull("-1:1");
        column.addValueOrNull("-2:200");
        column.addValueOrNull("100.324:300.3");
        assertEquals(3, column.getMinIndex());
        assertEquals(4, column.getMaxIndex());
    }
}
