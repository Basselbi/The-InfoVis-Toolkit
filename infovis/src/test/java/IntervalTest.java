import infovis.data.DoubleInterval;
import infovis.data.Interval;
import junit.framework.TestCase;

/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/

/**
 * Class IntervalTest
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class IntervalTest extends TestCase {
    public IntervalTest(String name) {
        super(name);
    }
    
    public void testInterval() {
        DoubleInterval i = new DoubleInterval(0, 0);
        assertEquals(i, DoubleInterval.ZERO);
        assertTrue(i.isNumber());
        assertTrue(! i.isNegative());
        assertTrue(! i.isPositive());
        assertTrue(! i.isEmpty());
        Interval j = DoubleInterval.add(i, i);
        assertEquals(j, DoubleInterval.ZERO);
        j = new DoubleInterval(-100, 100);
        assertTrue(! j.isEmpty());
        assertTrue(! j.isNegative());
        assertTrue(! j.isPositive());
        DoubleInterval k = new DoubleInterval(1, 10);
        assertTrue(k.intersects(j));
        assertTrue(j.contains(k));
        Interval l = DoubleInterval.mul(j, k);
        assertEquals(-100.0, l.getMin(), 0);
        assertEquals(1000.0, l.getMax(), 0);
        assertTrue(! l.isEmpty());
        DoubleInterval m = new DoubleInterval(l);
        assertEquals(l, m);
        String s = m.toString();
        DoubleInterval n = DoubleInterval.valueOf(s);
        assertEquals(m, n);
        DoubleInterval inf = new DoubleInterval();
        assertTrue(inf.contains(m));
        assertTrue(inf.contains(n));
    }
}
