/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column.format;

import infovis.data.CategoricalDistribution;
import infovis.data.Distribution;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import cern.colt.list.DoubleArrayList;

/**
 * <b>DistributionFormat</b> is used to format distributions.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class DistributionFormat extends CategoricalFormat {
    static protected final  Pattern PAT = Pattern.compile("\\(([0-9.]+)\\)[,]?");
    static final Logger LOG = Logger.getLogger(DistributionFormat.class);

    /**
     * Creates a distribution format with the specified name.
     * @param name the name
     */
    public DistributionFormat(String name) {
        super(name);
    }
    
    /**
     * Creates a distribution format.
     */
    public DistributionFormat() {
        this("#distribution");
    }
    
    /**
     * {@inheritDoc}
     */
    public StringBuffer format(
            Object obj,
            StringBuffer toAppendTo,
            FieldPosition pos) {
        if (! (obj instanceof Distribution)) {
            return null;
        }
        Distribution dist = (Distribution)obj;
        
        pos.setBeginIndex(toAppendTo.length());
        int n = dist.size();
        boolean appended = false;
        for (int cat = 0; cat < n; cat++) {
            double h = dist.getHeight(cat);
            if (h == 0) continue;
            if (appended) {
                toAppendTo.append(",");
            }
            else {
                appended = true;
            }
            String catName = getCategoryName(cat);
            if (catName == null) {
                catName = Integer.toString(cat);
                LOG.warn("Category "+cat+" has no name");
            }
            toAppendTo.append(catName);
            toAppendTo.append('(');
            int hi = (int)h;
            if (hi==h) {
                toAppendTo.append(Integer.toString(hi));
            }
            else {
                toAppendTo.append(Double.toString(h));
            }
            toAppendTo.append(')');
        }
        return toAppendTo;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object parseObject(String source, ParsePosition pos) {
        int index = pos.getIndex();

        DoubleArrayList values = new DoubleArrayList(getCategories().size());
        Matcher m = PAT.matcher(source);
        if (index != 0) {
            m.region(index, source.length());
        }
        while(m.find()) {
            int pindex = m.start();
            String catName = source.substring(index, pindex);
            
            int cat = -1;
            try {
                cat = Integer.parseInt(catName);
            }
            catch(NumberFormatException e) {
                cat = findCategory(catName);                
            }
            if (cat >= values.size()) {
                values.setSize(cat+1);
            }
            String valueStr = source.substring(m.start(1), m.end(1));
            try {
                double d = Double.parseDouble(valueStr);
                values.setQuick(cat, values.getQuick(cat)+d);
            }
            catch(NumberFormatException e) {
                LOG.error("Cannot parse a value for category "+catName, e);
                // do nothing
            }
            index = m.end();
            pos.setIndex(index);
        }
        CategoricalDistribution dist = new CategoricalDistribution(
                this, 
                values.elements());

        return dist;
    }
}
