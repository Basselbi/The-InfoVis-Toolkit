/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 */
import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

/**
 * <b>RunAllUnitTests</b> is the main testing suite.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 */
public class RunAllUnitTests {

    /**
     * Creates the suite.
     * @return the test suite
     */
    public static Test suite() {

        File loggerConfig = new File("properties/log4j.properties");
        if (loggerConfig.exists()) {
            PropertyConfigurator.configure(loggerConfig.toString());
        }
        else {
            BasicConfigurator.configure();
        }
        TestSuite suite = new TestSuite();
        suite.addTest(new TestSuite(IdManagerTest.class));
        suite.addTest(new TestSuite(ColumnsTest.class));
        suite.addTest(new TestSuite(TableTest.class));
        suite.addTest(new TestSuite(TreeTest.class));
        suite.addTest(new TestSuite(GraphTest.class));
        suite.addTest(new TestSuite(OrderedColorTest.class));
        suite.addTest(new TestSuite(EqualizedOrderedColorTest.class));
        suite.addTest(new TestSuite(DijkstraTest.class));
        suite.addTest(new TestSuite(PermutationTest.class));
        suite.addTest(new TestSuite(TestRBTree.class));
        suite.addTest(new TestSuite(CSVIOTest.class));
        suite.addTest(new TestSuite(IntervalTest.class));
//        suite.addTest(new TestSuite(OrderingTest.class));
        suite.addTest(new TestSuite(CuthillMcKeeTest.class));

        return suite;
    }

    /**
     * Main program.
     * @param args argument list
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }
}
