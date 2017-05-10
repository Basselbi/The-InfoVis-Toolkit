/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;
import infovis.Table;
import infovis.column.format.CategoricalFormat;
import infovis.metadata.ValueCategory;
import infovis.utils.CaseInsensitiveComparator;
import infovis.utils.Permutation;
import infovis.utils.RowIterator;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import cern.colt.function.IntComparator;

/**
 * Specialization of an IntColumn storing categorical values.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.27 $
 * 
 * @infovis.factory ColumnFactory "categorical" DENSE
 * @infovis.factory ColumnFactory "cat" DENSE
 * @infovis.factory ColumnFactory "category" DENSE
 */
public class CategoricalColumn extends IntColumn {
    private static final long serialVersionUID = 8628407342150620750L;
    private CategoricalFormat format;

    /**
     * Constructor for CategoricalColumn.
     * 
     * @param name
     *            the column name
     */
    public CategoricalColumn(String name) {
        this(name, 10, null);
    }
    
    /**
     * Constructor for CategoricalColumn.
     * 
     * @param name the column name
     * @param params initial category names separated by '/'
     */
    public CategoricalColumn(String name, String params) {
        this(name, 10, null);
        String[] cats = params.split("/");
        for (int i = 0; i < cats.length; i++) {
            format.findCategory(cats[i]);
        }
        format.setOrdered(true);
    }

    /**
     * Constructor for CategoricalColumn.
     * 
     * @param name
     *            the Column name.
     * @param reserve
     *            the initial reserved size.
     * @param map
     *            the initial category map.
     */
    public CategoricalColumn(String name, int reserve, Map map) {
        super(name, reserve);
        if (map == null) {
            map = new TreeMap();
        }
        format = new CategoricalFormat(name);
        setFormat(format);
        getMetadata().addAttribute(
                ValueCategory.VALUE_CATEGORY_TYPE,
                ValueCategory.VALUE_CATEGORY_TYPE_CATEGORICAL);
    }

    /**
     * Returns a column as a <code>IntColumn</code> from an <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param index
     *            index in the <code>DefaultTable</code>
     * 
     * @return a <code>IntColumn</code> or null if no such column exists or
     *         the column is not a <code>IntColumn</code>.
     */
    public static AbstractIntColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof CategoricalColumn) {
            return (CategoricalColumn) c;
        }
        else {
            return null;
        }
    }

    /**
     * Returns a column as a <code>IntColumn</code> from a table, creating it
     * if needed.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a column as a <code>IntColumn</code> from a table,
     */
    public static IntColumn findColumn(Table t, String name) {
        Column c = t.getColumn(name);
        if (c == null) {
            c = new CategoricalColumn(name);
            t.addColumn(c);
        }
        return (CategoricalColumn) c;
    }
    

    
    /**
     * Creates a categorical column from a specified column.
     * @param c the column
     * @return a categorical column
     */
    public static CategoricalColumn create(Column c) {
        if (c instanceof CategoricalColumn) {
            return (CategoricalColumn)c;
        }
        CategoricalColumn cat = new CategoricalColumn(c.getName(), c.size(), null);
        for (RowIterator iter = c.iterator(); iter.hasNext(); ) {
            int row = iter.nextRow();
            cat.setValueOrNullAt(row, c.getValueAt(row));
        }
        cat.getMetadata().setResolveParent(c.getMetadata());
        return cat;
    }

    /**
     * {@inheritDoc}
     */
    public void copyValueFrom(int toIndex, Column c, int fromIndex)
            throws ParseException {
        if (c instanceof CategoricalColumn) {
            CategoricalColumn cc = (CategoricalColumn) c;
            if (!cc.isValueUndefined(fromIndex)) {
                setValueAt(toIndex, cc.getValueAt(fromIndex));
                return;
            }
        }
        super.copyValueFrom(toIndex, c, fromIndex);

    }

    /**
     * Returns the catergory associated with the specified name,
     * creating it if required
     * @param name the category name
     * @return the category
     * @see infovis.column.format.CategoricalFormat#findCategory(String)
     */
    public int findCategory(String name) {
        return format.findCategory(name);
    }

    /**
     * @return Returns the category map.
     */
    public Map getCategories() {
        return format.getCategories();
    }
    
    /**
     * @return the number of categories
     */
    public int getCategoryCount() {
        return format.getCategoryCount();
    }

    /**
     * Returns the catergory associated with the specified name,
     * or -1 if it does not exist
     * @param name the category name
     * @return the category of -1
     * @see infovis.column.format.CategoricalFormat#getCategory(String)
     */
    public int getCategory(String name) {
        return format.getCategory(name);
    }

    /**
     * Returns the name of the category with the specified
     * index.
     * @param index the index
     * @return the category name or null
     * @see infovis.column.format.CategoricalFormat#indexCategory(int)
     */
    public String indexCategory(int index) {
        return format.indexCategory(index);
    }

    /**
     * Associate a value with a category name
     * @param name the category name
     * @param value the value
     * @see infovis.column.format.CategoricalFormat#putCategory(String, int)
     */
    public void putCategory(String name, int value) {
        format.putCategory(name, value);
    }
    
    /**
     * Sort the categories using the specified comparator
     * @param comp the comparator
     */
    public void sortCategories(final Comparator comp) {
        final String[] categories = new String[getCategoryCount()];
        for (int i = 0; i < getCategoryCount(); i++) {
            categories[i] = indexCategory(i);
        }
        Permutation perm = new Permutation(getCategoryCount());
        perm.sort(new IntComparator() {
            public int compare(int i1, int i2) {
                return comp.compare(categories[i1], categories[i2]);
            }
        });
        format.setOrdered(true);
        if (perm.isIdentity()) 
            return;
        format.clear();
        for (int i = 0; i < categories.length; i++) {
            format.findCategory(categories[perm.getDirect(i)]);
        }
        
        disableNotify();
        try {
            for (RowIterator iter = iterator(); iter.hasNext(); ) {
                int i = iter.nextRow();
                set(i, perm.getInverse(get(i)));
            }
        }
        finally {
            enableNotify();
        }
    }
    
    /** 
     * Default comparator.
     */
    public final Comparator DEFAULT_COMPARATOR = new CaseInsensitiveComparator() {
        public int compare(Object o1, Object o2) {
            String s1 = o1.toString();
            String s2 = o2.toString();
            try {
                int v1 = Integer.parseInt(s1);
                int v2 = Integer.parseInt(s2);
                return v1 - v2;
            }
            catch(NumberFormatException e) {
                return super.compare(o1, o2);
            }
        }
    };
    
    /**
     * Sort the categories using a case insensitive comparator.
     */
    public void sortCategories() {
        sortCategories(DEFAULT_COMPARATOR);
    }

    /**
     * @return true if this column is ordered
     */
    public boolean isOrdered() {
        return format.isOrdered();
    }
}
