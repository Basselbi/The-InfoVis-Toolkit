/**
 * 
 */
package infovis.data;

import infovis.Column;
import infovis.Table;
import infovis.column.BasicObjectColumn;
import infovis.column.format.CategoricalFormat;
import infovis.column.format.DistributionFormat;

/**
 * @author nghi
 *
 * @infovis.factory ColumnFactory "histogram" DENSE
 */
public class CategoricalDistributionColumn extends BasicObjectColumn {

	public CategoricalDistributionColumn(String name, int reserve) {
		super(name, reserve);
		setFormat(new DistributionFormat(name));
	}

	public CategoricalDistributionColumn(String name) {
		super(name);
		setFormat(new DistributionFormat(name));
	}
	
	public CategoricalFormat getCategoricalFormat() {
	    return (CategoricalFormat)getFormat();
	}

    /**
     * {@inheritDoc}
     */
	public Object definedValue() {
		return new CategoricalDistribution(getCategoricalFormat());
	}
	
    /**
     * {@inheritDoc}
     */
    public Class getValueClass() {
        return CategoricalDistribution.class;
    }
    
    /**
     * Returns the element at the specified position in this column.
     * 
     * @param index
     *            index of element to return.
     * 
     * @return the element at the specified position in this column.
     */
    public CategoricalDistribution get(int index) {
        return (CategoricalDistribution) getObjectAt(index);
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
    public void set(int index, CategoricalDistribution element) {
        setObjectAt(index, element);
    }
    /**
     * Returns a column as an <code>CategoricalDistribution</code> from an
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param index
     *            index in the <code>Table</code>
     * 
     * @return an <code>CategoricalDistributionColumn</code> or null if no such column exists or
     *         the column is not a <code>CategoricalDistributionColumn</code>.
     */
    public static CategoricalDistributionColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof CategoricalDistributionColumn) {
            return (CategoricalDistributionColumn) c;
        }
        else {
            return null;
        }
    }

    /**
     * Returns a column as an <code>CategoricalDistributionColumn</code> from a
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return an <code>CategoricalDistributionColumn</code> or null if no such column exists or
     *         the column is not a <code>CategoricalDistributionColumn</code>.
     */
    public static CategoricalDistributionColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof CategoricalDistributionColumn) {
            return (CategoricalDistributionColumn) c;
        }
        else {
            return null;
        }
    }

    /**
     * Returns a column as an <code>CategoricalDistributionColumn</code> from a table, creating
     * it if needed.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a column as a <code>CategoricalDistributionColumn</code> from a table,
     */
    public static CategoricalDistributionColumn findColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c == null) {
            c = new CategoricalDistributionColumn(name);
            t.addColumn(c);
        }

        return (CategoricalDistributionColumn) c;
    }    

}
