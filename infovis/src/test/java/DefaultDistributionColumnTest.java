
import infovis.data.DefaultDistribution;
import infovis.data.DefaultDistributionColumn;
import junit.framework.TestCase;

public class DefaultDistributionColumnTest extends TestCase {
	public DefaultDistributionColumnTest(String name) {
		super(name);
	}
	
	public void testDefaultDistributionColumn() {
		DefaultDistributionColumn col0 = new DefaultDistributionColumn("level");
		this.assertEquals(col0.isEmpty(), true);
		this.assertEquals(col0.size(), 0);
		this.assertEquals(col0.getName(), "level");
		
    	DefaultDistribution d0 = new DefaultDistribution(3);
    	d0.add(0, 2);
    	d0.add(1);
    	d0.add(2);
    	col0.add(d0);

    	DefaultDistribution d1 = new DefaultDistribution(3);
    	d1.add(d0);
    	col0.add(d1);
    	
    	DefaultDistribution d2 = new DefaultDistribution(3);
    	d2.add(d0, 2);
    	col0.add(d2);
    	
    	
    	this.assertEquals(col0.size(), 3);
    	this.assertEquals(col0.getObjectAt(0), d0);
    	this.assertEquals(col0.get(1), d1);
    	this.assertEquals(col0.getValueClass(), DefaultDistribution.class);
    	// this.assertEquals(col0.getMinIndex(), 0);
    	// this.assertEquals(col0.getMaxIndex(), 2);
    	col0.remove(0);
    	col0.remove(d1);
    	col0.remove(d2);    	
    	
		this.assertEquals(col0.isEmpty(), true);
	}

}

