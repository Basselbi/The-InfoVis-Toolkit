package infovis.column;

import infovis.Column;
import infovis.Table;
import infovis.column.format.ListFormat;

/**
 * <b>ListColumn</b> is a column containing a list of strings.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class ListColumn extends BasicObjectColumn {
    /**
     * The defiens value.
     */
    public static final String[] VALUE = new String[0];

    /**
     * Creates a ListColumn with the specified name and allocated room
     * @param name the name
     * @param reserve the allocated room
     */
    public ListColumn(String name, int reserve) {
        super(name, reserve);
        setFormat(ListFormat.INSTANCE);
    }

    /**
     * Creates a ListColumn with the specified name
     * @param name the name
     */
    public ListColumn(String name) {
        super(name);
        setFormat(ListFormat.INSTANCE);
    }

    /**
     * Returns the element at the specified index
     * @param index the index
     * @return the element
     */
    public String[] get(int index) {
        return (String[]) getObjectAt(index);
    }

    /**
     * Sets the element at the specified index
     * @param index the index
     * @param element the element
     */
    public void set(int index, String[] element) {
        setObjectAt(index, element);
    }

    /**
     * Sets the value at the specified index, extending the table if required
     * @param index the index
     * @param element the element
     */
    public void setExtend(int index, String[] element) {
        super.setExtend(index, element);
    }

    /**
     * Adds the specified element to the column
     * @param element the elementt
     */
    public void add(String[] element) {
        super.add(element);
    }

    /**
     * Fills the specified column with the value
     * @param val the value
     */
    public void fill(String[] val) {
        super.fill(val);
    }

    /**
     *  Returns a ListColumn at the specified int from a table or null
     *  if it does not exist.
     * @param t the table
     * @param index the index
     * @return a ListColumn at that index or null
     */
    public static ListColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof ListColumn) {
            return (ListColumn) c;
        }
        else {
            return null;
        }
    }

    /**
     *  Returns a ListColumn with the specified name from a table or null
     *  if it does not exist.
     * @param t the table
     * @param name the name
     * @return a ListColumn with that name or null
     */
    public static ListColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof ListColumn) {
            return (ListColumn) c;
        }
        else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Class getValueClass() {
        return Object.class;
    }

    /**
     * {@inheritDoc}
     */
    public Object definedValue() {
        return VALUE;
    }
}
