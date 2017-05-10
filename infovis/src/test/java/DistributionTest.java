import infovis.data.CategoricalDistribution;
import infovis.data.DefaultDistribution;
import infovis.column.format.CategoricalFormat;
import junit.framework.TestCase;


/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/

/**
 * Class DistributionTest
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class DistributionTest extends TestCase {
    /**
     * @param name
     */
    public DistributionTest(String name) {
        super(name);
    }
    
    public void testDistribution() {
        //IAxis axis = new hep.aida.ref.FixedAxis(10, 0, 100);
        //DoubleDistribution d = new DoubleDistribution(axis);
    	DefaultDistribution d1 = new DefaultDistribution(3);
    	this.assertEquals(d1.isEmpty(), true);
    	this.assertEquals(d1.size(), 3);

    	this.assertEquals(d1.getCategoryName(0), "0");
    	this.assertEquals(d1.getCategoryName(1), "1");
    	this.assertEquals(d1.getCategoryName(2), "2");

    	d1.add(0);
    	d1.add(1);
    	d1.add(2);
    	this.assertEquals(d1.contains(0), true);
    	this.assertEquals(d1.contains(1), true);
    	this.assertEquals(d1.contains(2), true);
    	this.assertEquals(d1.contains(3), false);
    	
    	d1.add(0, 1);
    	d1.add(0, 1);
    	d1.add(1, 4);
    	d1.add(2, 6);
    	this.assertEquals(d1.getHeight(0), 3.0);
    	this.assertEquals(d1.getHeight(1), 5.0);
    	this.assertEquals(d1.getHeight(2), 7.0);
    	
    	DefaultDistribution d2 = new DefaultDistribution(3);
    	d2.add(d1);
    	this.assertEquals(d2.getHeight(0), 3.0);
    	this.assertEquals(d2.getHeight(1), 5.0);
    	this.assertEquals(d2.getHeight(2), 7.0);

    	d2.add(d1, 2);
    	this.assertEquals(d2.getHeight(0), 9.0);
    	this.assertEquals(d2.getHeight(1), 15.0);
    	this.assertEquals(d2.getHeight(2), 21.0);
    	
//    	System.out.println(d1.toString());
//    	System.out.println(d2.toString());
    	CategoricalFormat cats = new CategoricalFormat("level");
    	cats.putCategory("basic", 0);
    	cats.putCategory("medium", 1);
    	cats.putCategory("expert", 2);
    	CategoricalDistribution d3 = new CategoricalDistribution(cats);
    	this.assertEquals(d3.isEmpty(), true);
    	this.assertEquals(d3.size(), 3);

    	this.assertEquals(d3.getCategoryName(0), "basic");
    	this.assertEquals(d3.getCategory("basic"), 0);
    	this.assertEquals(d3.getCategoryName(1), "medium");
    	this.assertEquals(d3.getCategory("medium"), 1);
    	this.assertEquals(d3.getCategoryName(2), "expert");
    	this.assertEquals(d3.getCategory("expert"), 2);
    	
    	d3.add(0);
    	d3.add(1, 3);
    	d3.add(2, 1);

    	CategoricalDistribution d4 = new CategoricalDistribution(cats);
    	d4.add(d3);
    	this.assertEquals(d4.getHeight(0), 1.0);
    	this.assertEquals(d4.getHeight(1), 3.0);
    	this.assertEquals(d4.getHeight(2), 1.0);

//    	System.out.println(d3.toString());
//    	System.out.println(d4.toString());
    }

}
