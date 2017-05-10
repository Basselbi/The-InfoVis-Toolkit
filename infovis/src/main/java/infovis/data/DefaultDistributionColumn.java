/**
 * 
 */
package infovis.data;

import infovis.Column;
import infovis.Table;
import infovis.column.BasicObjectColumn;

/**
 * @author nghi
 *
 */
public class DefaultDistributionColumn extends BasicObjectColumn {

	public DefaultDistributionColumn(String name, int reserve) {
		super(name, reserve);
	}

	public DefaultDistributionColumn(String name) {
		super(name);
	}

    /**
     * {@inheritDoc}
     */
	public Object definedValue() {
		// TODO Auto-generated method stub
		return null;
	}
	
    /**
     * {@inheritDoc}
     */
    public Class getValueClass() {
        return DefaultDistribution.class;
    }
    
    /**
     * Returns the element at the specified position in this column.
     * 
     * @param index
     *            index of element to return.
     * 
     * @return the element at the specified position in this column.
     */
    public DefaultDistribution get(int index) {
        return (DefaultDistribution) getObjectAt(index);
    }
	
    /**
     * Replaces the element at the specified position in this column with the
     * specified element.
     * 
     * @param index
     *            index of element to replace.
     * @param element
     *            element to be stored at the specified position.
     */
    public void set(int index, DefaultDistribution element) {
        setObjectAt(index, element);
    }
    /**
     * Returns a column as an <code>DefaultDistribution</code> from an
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param index
     *            index in the <code>Table</code>
     * 
     * @return an <code>DefaultDistributionColumn</code> or null if no such column exists or
     *         the column is not a <code>DefaultDistributionColumn</code>.
     */
    public static DefaultDistributionColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof DefaultDistributionColumn) {
            return (DefaultDistributionColumn) c;
        }
        else {
            return null;
        }
    }

    /**
     * Returns a column as an <code>DefaultDistributionColumn</code> from a
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return an <code>DefaultDistributionColumn</code> or null if no such column exists or
     *         the column is not a <code>DefaultDistributionColumn</code>.
     */
    public static DefaultDistributionColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof DefaultDistributionColumn) {
            return (DefaultDistributionColumn) c;
        }
        else {
            return null;
        }
    }

    /**
     * Returns a column as an <code>DefaultDistributionColumn</code> from a table, creating
     * it if needed.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a column as a <code>DefaultDistributionColumn</code> from a table,
     */
    public static DefaultDistributionColumn findColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c == null) {
            c = new DefaultDistributionColumn(name);
            t.addColumn(c);
        }

        return (DefaultDistributionColumn) c;
    }    

}

