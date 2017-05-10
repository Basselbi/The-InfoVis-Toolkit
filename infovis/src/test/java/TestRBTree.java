import cern.colt.function.IntIntProcedure;
import cern.colt.function.IntProcedure;
import infovis.utils.*;
import junit.framework.TestCase;
/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

/**
 * Class TestRBTree
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class TestRBTree extends TestCase {

    public TestRBTree(String name) {
        super(name);
    }
    
    public void testRBTree() {
        RBTree other = null;
        final RBTree rbtree = new RBTree(other);
        IntIntSortedMap sortedMap = new IntIntSortedMap();
        
        int i;
        Permutation perm = new Permutation(100);
        perm.shuffle();
        for (i = 0; i < 100; i++) {
            rbtree.put(perm.getDirect(i), perm.getDirect(i));
            sortedMap.put(perm.getDirect(i), perm.getDirect(i));
        }
        
        RowIterator rbi = rbtree.keyIterator();
        RowIterator smi = sortedMap.keyIterator();
        while (rbi.hasNext() && smi.hasNext()) {
            assertEquals(rbi.nextRow(), smi.nextRow());
        }
        assertEquals(rbi.hasNext(), smi.hasNext());
        
        for (i = 0; i < 10; i++) {
            rbtree.remove(perm.getDirect(i));
            sortedMap.remove(perm.getDirect(i));
        }
        rbi = rbtree.keyIterator();
        smi = sortedMap.keyIterator();
        while (rbi.hasNext() && smi.hasNext()) {
            assertEquals(rbi.nextRow(), smi.nextRow());
        }
        assertEquals(rbi.hasNext(), smi.hasNext());
        
        rbtree.clear();
        assertEquals(0, rbtree.size());
        rbtree.put(144,144);
        rbtree.put(145,145);
        RowIterator iter = rbtree.valueIterator(); 
        assertTrue(iter.hasNext());
        assertEquals(144, iter.nextRow());
        assertTrue(iter.hasNext());
        assertEquals(145, iter.nextRow());
        assertTrue(!iter.hasNext());
        rbtree.clear();
        for (i = 0; i < 100; i++) {
            rbtree.put(perm.getDirect(i), perm.getDirect(i));
        }
        i = 50;
        for (rbi = rbtree.keyIterator(50); rbi.hasNext(); i++) {
            int key = rbi.nextRow();
            assertEquals(i, key);
            rbi.remove();
        }
        assertEquals(50, rbtree.size());
        if (rbtree.removeKey(30)) {
            assertEquals(49, rbtree.size());
        }
        else {
            assertEquals(50, rbtree.size());
        }
        final RBTree tree2 = new RBTree(rbtree);
        assertEquals(tree2.size(), rbtree.size());
        rbtree.forEachPair(new IntIntProcedure() {
            public boolean apply(int key, int val) {
                assertEquals(tree2.get(key), val);
                return true;
            }
        });
        tree2.forEachKey(new IntProcedure() {
            public boolean apply(int key) {
                assertTrue("Key not defined", rbtree.containsKey(key));
                return true;
            }
        });
        tree2.forEachKey(new IntProcedure() {
            int count = 0;
            public boolean apply(int key) {
                assertTrue("Iteration not finished", count <= 40);
                return count < 40;
            }
        });
        
    }

}
