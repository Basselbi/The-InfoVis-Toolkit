/**
 * 
 */

import infovis.column.format.CategoricalFormat;
import infovis.data.CategoricalDistribution;
import infovis.data.CategoricalDistributionColumn;
import infovis.column.format.CategoricalFormat;
import junit.framework.TestCase;

/**
 * @author nghi
 *
 */
public class CategoricalDistributionColumnTest extends TestCase {
	public CategoricalDistributionColumnTest(String name) {
		super(name);
	}
	
	public void testCategoricalDistributionColumn() {
		CategoricalDistributionColumn col0 = new CategoricalDistributionColumn("level");
		this.assertEquals(col0.isEmpty(), true);
		this.assertEquals(col0.size(), 0);
		this.assertEquals(col0.getName(), "level");
		
    	CategoricalFormat cats = new CategoricalFormat("level");
    	cats.putCategory("basic", 0);
    	cats.putCategory("medium", 1);
    	cats.putCategory("expert", 2);	

    	CategoricalDistribution d0 = new CategoricalDistribution(cats);
    	d0.add(0);
    	d0.add(1, 2);
    	d0.add(2);
    	col0.add(d0);

    	CategoricalDistribution d1 = new CategoricalDistribution(cats);
    	d1.add(0, 2);
    	d1.add(1, 1);
    	d1.add(2, 3);
    	col0.add(d1);
    	
    	this.assertEquals(col0.size(), 2);
    	this.assertEquals(col0.getObjectAt(0), d0);
    	this.assertEquals(col0.get(1), d1);
    	this.assertEquals(col0.getValueClass(), CategoricalDistribution.class);
    	// this.assertEquals(col0.getMinIndex(), 0);
    	// this.assertEquals(col0.getMaxIndex(), 2);
    	
    	col0.remove(0);
    	col0.remove(d1);
    	
		this.assertEquals(col0.isEmpty(), true);
		
	}

}
