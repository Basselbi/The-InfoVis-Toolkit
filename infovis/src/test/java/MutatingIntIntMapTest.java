import infovis.utils.MutatingIntIntMap;
import junit.framework.TestCase;

/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/

/**
 * Class MutatingIntIntMapTest
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class MutatingIntIntMapTest extends TestCase {

    /**
     * Constructor.
     * @param name the name
     */
    public MutatingIntIntMapTest(String name) {
        super(name);
    }

    /**
     * Performs the tests.
     */
    public void testMutatingIntIntMap() {
        MutatingIntIntMap map = new MutatingIntIntMap();
        
        assertEquals(0, map.size());
        
        for (int i = 0; i < 100; i++) {
            map.put(i, i);
        }
        assertEquals(100, map.size());
        
        map.clear();
        for (int i = 10; i < 100; i++) {
            map.put(i, i);
        }
        assertEquals(90, map.size());
        
        
    }
}
