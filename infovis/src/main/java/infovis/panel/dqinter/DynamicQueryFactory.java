/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.dqinter;

import infovis.Column;
import infovis.column.NumberColumn;
import infovis.column.StringColumn;
import infovis.metadata.ValueCategory;
import infovis.panel.DynamicQuery;
import infovis.utils.BasicFactory;

import java.util.Iterator;

/**
 * <b>DynamicQueryFactory</b> is a factory that
 * creates Dynamic Query controls from a Column.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
public class DynamicQueryFactory extends BasicFactory {
    private static DynamicQueryFactory instance;
    

    /**
     * Default type of query.
     */
    static public final String QUERY_TYPE_DEFAULT = "default";
//    static public final String QUERY_TYPE_SLIDER = "slider";
//    static public final String QUERY_TYPE_TOGGLE = "toggle";
//    static public final String QUERY_TYPE_RADIO = "radio";
    /**
     * Want a query for search.
     */
    static public final String QUERY_TYPE_SEARCH = "search";

    /**
     * 
     * @return a static instance of the factory.
     */
    public static DynamicQueryFactory getInstance() {
        if (instance == null) {
            instance = new DynamicQueryFactory();
        }
        return instance;
    }
    
    /**
     * Sets the static instance of factories.
     * @param inst
     */
    public static void setInstance(DynamicQueryFactory inst) {
        instance = inst;
    }
    
    /**
     * Creates a dynamic query for the specified column with the specified type.
     * @param c the column
     * @param type the type
     * @return a dynamic query
     */
    public static DynamicQuery createDQ(Column c, String type) {
        return getInstance().create(c, type);
    }
    
    /**
     * Creates a dynamic query for the specified column with the default query type.
     * @param c the column
     * @return a dynamic query
     */
    public static DynamicQuery createDQ(Column c) {
        return getInstance().create(c);
    }
    
    /**
     * Constructor for DynamicQueryFactory.
     */
    public DynamicQueryFactory() {
        addDefaultCreators("dynamicqueryfactory");
    }

    protected void addDefaultCreators(String factoryName) {
        super.addDefaultCreators(factoryName);
        add(new Creator() {
            /** {@inheritDoc} */
            public String getName() {
                return "Regexp Search Dynamic Query";
            }
            public DynamicQuery create(Column c, String type) {
                if (type == QUERY_TYPE_SEARCH || c instanceof StringColumn) {
                    return new StringSearchDynamicQuery(c);
                }
                return null;
            }
        });
        add(new Creator() {
            /** {@inheritDoc} */
            public String getName() {
                return "Range Slider Dynamic Query";
            }
            /** {@inheritDoc} */
            public DynamicQuery create(Column c, String type) {
                int category = ValueCategory.findValueCategory(c);
            
                if (c instanceof NumberColumn
                    && category != ValueCategory.TYPE_CATEGORIAL) {
                    NumberColumn number = (NumberColumn) c;
                    return new NumberColumnBoundedRangeModel(number);
                }
                return null;
            }
        });
        add(new Creator() {
            /**
             * {@inheritDoc}
             */
            public String getName() {
                return "Categorical Dynamic Query";
            }
            /** {@inheritDoc} */            
            public DynamicQuery create(Column c, String type) {
                int category = ValueCategory.findValueCategory(c);
                if (c instanceof NumberColumn
                    && category == ValueCategory.TYPE_CATEGORIAL) {
                    NumberColumn number = (NumberColumn) c;
                    return new CategoricalDynamicQuery(number);
                }
                return null;
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(String name, String className, String data) {
        //TODO
        //Don't know how to do it yet
    }
    
    /**
     * {@inheritDoc}
     */
    public Iterator iterator() {
        return entry.values().iterator();
    }

    /**
     * Creates a dynamic query from a column.
     *
     * @param c The column
     * @param type the default type of DynamicQuery.
     *
     * @return A Dynamic query or null.
     */
    public DynamicQuery create(
        Column c,
        String type) {
        DynamicQuery ret = null;
        for (Iterator iter = iterator(); iter.hasNext(); ) {
            Creator creator = (Creator) iter.next();
            ret = creator.create(c, type);
            if (ret != null)
                break;
        }
        return ret;
    }

    /**
     * Creates a dynamic query of default type from a column.
     *
     * @param c The column
     *
     * @return A Dynamic query or null.
     */
    public DynamicQuery create(Column c) {
        return create(c, QUERY_TYPE_DEFAULT);
    }

    /**
     * Adds a default creator for a specific kind of column.
     *
     * @param c The creator
     */
    public void add(Creator c) {
        putEntry(c.getName(), c);
    }

    /**
     * Creator interface for building a Dynamic Query from a column type.
     */
    public interface Creator {
        /**
         * @return the name
         */
        String getName();
        /**
         * Creates a Dynamic Query from a column.
         * @param c the column
         * @param type the type
         * @return a dynamic query
         */
        DynamicQuery create(Column c, String type);
    }
 }
