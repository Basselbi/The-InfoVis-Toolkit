/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;
import infovis.column.event.ColumnChangeEvent;
import infovis.utils.BitSet;
import infovis.utils.ChangeManager;
import infovis.utils.IntIntSortedMap;
import infovis.utils.IntSet;
import infovis.utils.RowComparator;
import infovis.utils.RowIterator;

import java.text.ParseException;
import java.text.ParsePosition;

import javax.swing.event.ChangeEvent;

/**
 * <code>AbstractColumn</code> is the base class for each concrete column.
 * 
 * <p>
 * <code>AbstractColumn</code> implements the notification mechanism and its
 * inhibition.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.32 $
 */
public abstract class AbstractColumn extends ChangeManager implements Column {
    protected IntSet modifs;

    /**
     * Compare two objects that can be null.
     * 
     * @param o1
     *            first object
     * @param o2
     *            second object
     * @return true if both object have the same value or are equal.
     */
    public static boolean equalObj(Object o1, Object o2) {
        if (o1 == o2)
            return true;
        if (o1 != null) {
            if (o2 != null) {
                return o1.equals(o2);
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        // Necessary if this class is used in non-homogenous structures,
        // i.e., EventListenerLists
        if (!(obj instanceof Column))
            return false;

        Column c = (Column) obj;
        if (!equalObj(getName(), c.getName())) {
            return false;
        }
        if (size() != c.size()) {
            return false;
        }
        if (!isInternal() == c.isInternal()) {
            return false;
        }
        if (!equalObj(getFormat(), c.getFormat())) {
            return false;
        }
        if (!getValueClass().equals(c.getValueClass())) {
            return false;
        }
        // if (!equalObj(getMetadata(), c.getMetadata())) {
        // return false;
        // }
        return compareValues(c);
    }

    /**
     * Compare the values of this column and the specified column.
     * 
     * @param c
     *            the column to compare values from
     * @return <code>true</code> if the values match, <code>false</code>
     *         otherwise
     */
    public boolean compareValues(Column c) {
        for (int i = 0; i < size(); i++) {
            if (isValueUndefined(i)) {
                if (!c.isValueUndefined(i)) {
                    return false;
                }
            }
            else {
                if (!equalObj(getValueAt(i), c.getValueAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Object parse(String v) throws ParseException {
        if (v == null)
            return null;
        ParsePosition pos = new ParsePosition(0);
        if (getFormat() != null) {
            Object ret = getFormat().parseObject(v, pos);
            if (pos.getErrorIndex() != -1 || pos.getIndex() != v.length()) {
                throw new ParseException("Column.parse(String) failed", pos
                        .getErrorIndex());
            }
            return ret;
        }
        return v;
    }

    /**
     * {@inheritDoc}
     */
    public String format(Object o) {
        if (o == null)
            return null;
        else if (getFormat() != null) {
            return getFormat().format(o);
        }
        else {
            return o.toString();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setValueAt(int index, String element) throws ParseException {
        if (index >= size()) {
            setSize(index + 1);
        }
        setObjectAt(index, parse(element));
    }

    /**
     * {@inheritDoc}
     */
    public String getValueAt(int index) {
        return format(getObjectAt(index));
    }

    /**
     * {@inheritDoc}
     */
    public void copyValueFrom(int toIndex, Column c, int fromIndex)
            throws ParseException {
        setValueAt(toIndex, c.getValueAt(fromIndex));
    }

    /**
     * Throws a ReadOnlyColumnException with the specified message.
     * 
     * @param msg
     *            the message
     * @throws ReadOnlyColumnException
     *             the exception thrown
     */
    public static void readonly(String msg) throws ReadOnlyColumnException {
        throw new ReadOnlyColumnException(msg);
    }

    /**
     * Throws a ReadOnlyColumnException with a standard message.
     * 
     * @throws ReadOnlyColumnException
     *             the exception thrown
     */
    public static void readonly() throws ReadOnlyColumnException {
        readonly("Trying to change a read-only column");
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getName();
    }

    /**
     * Returns the list of uniq elements of this columns associated with their
     * count.
     * 
     * <p>
     * The list contains tuples: the index of a representative of the value and
     * the number of repetition of this value.
     * 
     * @param comp
     *            a comparator used to order the value or <code>null</code> if
     *            it should appear in the order specified by the column.
     * 
     * @return a <code>IntIntSortedMap</code> that can be read using an
     *         iterator.
     */
    public IntIntSortedMap computeValueMap(RowComparator comp) {
        IntIntSortedMap map = new IntIntSortedMap(comp == null ? this : comp);
        for (RowIterator iter = iterator(); iter.hasNext();) {
            int v = iter.nextRow();
            if (comp != null && comp.isValueUndefined(v)) {
                continue;
            }
            if (map.containsKey(v)) {
                map.put(v, map.get(v) + 1);
            }
            else {
                map.put(v, 1);
            }
        }

        return map;
    }

    /**
     * Returns the list of uniq elements of this columns associated with their
     * count.
     * 
     * <p>
     * The list contains tuples: the index of a representative of the value and
     * the number of repetition of this value.
     * 
     * @return a <code>IntIntSortedMap</code> that can be read using an
     *         iterator.
     */
    public IntIntSortedMap computeValueMap() {
        return computeValueMap(null);
    }
    
    protected void modified() {
        modified(0, size());
    }

    /**
     * {@inheritDoc}
     */
    protected boolean modified(int i) {
        assert (i >= 0);
        if (!hasEventListener())
            return false;
        if (modifs == null) {
            modifs = new BitSet();
        }
        modifs.set(i);
        return super.modified(i);
    }

    /**
     * {@inheritDoc}
     */
    protected void modified(int i, int j) {
        assert (i >= 0);
        if (! hasEventListener())
            return;
        j = Math.max(i, j);
        if (modifs == null) {
            modifs = new BitSet();
        }
        modifs.set(i, j);
        super.modified(i, j);
    }

    protected void modified(IntSet is) {
        if (! hasEventListener())
            return;
        if (modifs == null) {
            modifs = new BitSet();
        }
        modifs.or(is);
        super.modified(is.nextSet(0));
    }

    /**
     * {@inheritDoc}
     */
    protected ChangeEvent createChangeEvent() {
        ChangeEvent ret = new ColumnChangeEvent(this, modifs);
        modifs = null;
        return ret;
    }
}
