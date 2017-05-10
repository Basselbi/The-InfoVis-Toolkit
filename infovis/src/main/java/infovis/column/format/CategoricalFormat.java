/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.format;

import infovis.Column;
import infovis.column.ColumnFactory;
import infovis.column.StringColumn;
import infovis.table.DefaultTable;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Format for storing categorical string data in an IntColumn.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.13 $
 */
public class CategoricalFormat extends Format implements FormatMetadata {
    protected Map          categories;
    protected StringColumn inverse;
    protected DefaultTable table;
    protected boolean      ordered;

    /**
     * Constructor.
     * 
     * @param name
     *            the categorical format name.
     */
    public CategoricalFormat(String name) {
        categories = new HashMap();
        table = new DefaultTable();
        inverse = new StringColumn("name");
        table.addColumn(inverse);
    }

    /**
     * Constructor.
     */
    public CategoricalFormat() {
        this("#categories");
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return categories.hashCode() + table.hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof CategoricalFormat)) {
            return false;
        }
        CategoricalFormat other = (CategoricalFormat) obj;
        if (!categories.equals(other.categories)) {
            return false;
        }
        if (!table.equals(other.table)) {
            return false;
        }
        return true;
    }

    /**
     * Associate a category name with a value.
     * 
     * @param name
     *            the category name
     * @param value
     *            the integer value.
     */
    public void putCategory(String name, int value) {
        if (value == -1) return;
        Integer i = new Integer(value);
        categories.put(name, i);
        inverse.setExtend(value, name);
    }

    /**
     * Get the category associated with a name.
     * 
     * @param name
     *            the name.
     * @return the category or -1 if not defined.
     */
    public int getCategory(String name) {
        Integer i = (Integer) categories.get(name);
        if (i == null)
            return -1;
        return i.intValue();
    }
    
    /**
     * Returns an attribute associated with a specified category.
     * @param cat the category
     * @param attr the attribute name
     * @return the attribute value
     */
    public Object getCategoryAttribute(int cat, String attr) {
        Column col = table.getColumn(attr);
        if (col == null) return null;
        return col.getObjectAt(cat);
    }
    
    /**
     * Adds a new attribute associated with categories.
     * @param name the attribute name
     * @param type the attribute type as parsed by AbstractReader#createColumn
     */
    public void addCategoryAttribute(String name, String type) {
        if (table.getColumn(name) != null) return;
        Column col = ColumnFactory.createColumn(type, name);
        if (col == null) {
            throw new java.lang.TypeNotPresentException(
                    "No class for type "+type, null);
        }
        table.addColumn(col);
    }
    
    /**
     * Sets the attribute of the specified category to the value.
     * @param cat the category
     * @param attr the attribute name
     * @param value the value to set
     */
    public void setCategoryAttribute(int cat, String attr, Object value) {
        Column col = table.getColumn(attr);
        if (cat >= col.size()) {
            col.setSize(cat+1);
        }
        col.setObjectAt(cat, value);
    }
    
    /**
     * @return the attribute names.
     */
    public String[] getCategoryAttributes() {
        String[] attr = new String[table.size()];
        for (int a = 0; a < table.size(); a++) {
            attr[a] = table.getColumnName(a);
        }
        return attr;
    }

    /**
     * Find a category associated with a name, creating it if it doesn't exist
     * yet.
     * 
     * @param name
     *            the category name
     * @return the integer value associated with the name.
     */
    public int findCategory(String name) {
        if (name == null) return -1;
        else if (name.isEmpty()) {
            return -1;
        }
        Integer i = (Integer) categories.get(name);
        if (i == null) {
            int v = categories.size();
            putCategory(name, v);
            return v;
        }
        return i.intValue();
    }
    
    /**
     * @return the number of categories
     */
    public int getCategoryCount() {
        return categories.size();
    }
    
    /**
     * Add a set of categories coming from another CategoricalFormat
     * @param cf2 The categorical format to merge in.
     * 
     */
    public void merge(CategoricalFormat cf2) {
        int v = categories.size();
        Iterator it = cf2.getCategories().keySet().iterator(); 
        while(it.hasNext()) {
        	String name = it.next().toString();
			if (!categories.containsKey(name))
				putCategory(name, v++);
        	
        }
    }
    
    

    /**
     * Returns the category name given its index.
     * 
     * @param index
     *            the category index
     * @return the category name.
     */
    public String indexCategory(int index) {
        return inverse.get(index);
    }
    
    /**
     * Returns the category name given its index.
     * 
     * @param cat
     *            the category index
     * @return the category name.
     */
    public String getCategoryName(int cat) {
        return indexCategory(cat);
    }

    /**
     * Clears the association maps.
     */
    public void clear() {
        categories.clear();
        inverse.clear();
    }

    /**
     * @see java.text.Format#format(Object, StringBuffer, FieldPosition)
     */
    public StringBuffer format(
            Object obj,
            StringBuffer toAppendTo,
            FieldPosition pos) {
        if (!(obj instanceof Integer))
            return null;
        pos.setBeginIndex(toAppendTo.length());
        toAppendTo.append(inverse.get(((Integer) obj).intValue()));
        pos.setEndIndex(toAppendTo.length());
        return toAppendTo;
    }

    /**
     * @see java.text.Format#parseObject(String, ParsePosition)
     */
    public Object parseObject(String source, ParsePosition pos) {
        int index = pos.getIndex();
        String catName = index == 0 ? source : source.substring(index);
        pos.setIndex(source.length());
        if (findCategory(catName) != -1) {
            return (Integer) categories.get(catName);
        }
        return null;
    }

    /**
     * Returns the categories map.
     * 
     * @return the categories map.
     */
    public Map getCategories() {
        return categories;
    }
    
    /**
     * {@inheritDoc}
     */
    public void addMetadata(String type, String key, String value) {
        if ("category".equals(type)) {
            int v = Integer.parseInt(value.toString());
            putCategory(key, v);
        }
        else if ("label".equals(type)) {
            int v = getCategory(key);
            addCategoryAttribute("label", "string");
            setCategoryAttribute(v, "label", value);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Iterator formatMetadataIterator() {
        // TODO
        return null;
    }
    
    /**
     * @return the ordered
     */
    public boolean isOrdered() {
        return ordered;
    }
    
    /**
     * @param ordered the ordered to set
     */
    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }
}
