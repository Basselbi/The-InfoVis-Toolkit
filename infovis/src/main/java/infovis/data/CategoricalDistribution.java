/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.data;

import infovis.column.format.CategoricalFormat;

/**
 * Class CategoricalDistribution
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class CategoricalDistribution extends DefaultDistribution {
    protected CategoricalFormat format;
    
    public CategoricalDistribution(CategoricalFormat format) {
        super(format.getCategories().size());
        this.format = format;
    }
    
    public CategoricalDistribution(CategoricalFormat format, double[] height) {
        super(height);
        this.format = format;
    }

    public int getCategory(String name) {
        return format.getCategory(name);
    }
    
    public String getCategoryName(int cat) {
        return format.indexCategory(cat);
    }

    /** Necessary if there is assurance that the categories are shared through the entire column.
     * but are be distinct in each cell of the CategoricalDistributionColumn
     * storing a CategoricalDistribution. Example : a small lexicon stored as an histogramm in each cell. (elie)
     * @return the CategoricalFormat of the distribution
     */
	public CategoricalFormat getCategoricalFormat() {
		return format;
	}
    
    
}
