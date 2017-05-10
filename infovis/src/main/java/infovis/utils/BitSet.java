/*****************************************************************************
 * Copyright (C) 2009 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.utils;

/**
 * Class BitSet
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class BitSet extends java.util.BitSet 
    implements IntSet {

    /**
     * 
     */
    public BitSet() {
        super();
    }

    /**
     * @param nbits
     */
    public BitSet(int nbits) {
        super(nbits);
    }
    
    /**
     * {@inheritDoc}
     */
    public int nextClear(int fromIndex) {
        return nextClearBit(fromIndex);
    }
    
    /**
     * {@inheritDoc}
     */
    public int nextSet(int fromIndex) {
        return nextSetBit(fromIndex);
    }

    /**
     * {@inheritDoc}
     */
    public void or(IntSet set) {
        if (set instanceof java.util.BitSet) {
            java.util.BitSet bs = (java.util.BitSet) set;
            super.or(bs);
        }
        else {
            for (int b = set.nextSet(0); b != -1; b = set.nextSet(b+1)) {
                super.set(b);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void and(IntSet set) {
        if (set instanceof java.util.BitSet) {
            java.util.BitSet bs = (java.util.BitSet) set;
            super.and(bs);
        }
        else {
            for (int b = set.nextClear(0); b != -1; b = set.nextClear(b+1)) {
                super.set(b, false);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator iterator() {
        return new BitSetIterator();
    }
    
    class BitSetIterator extends AbstractRowIterator {
        int next;
        
        BitSetIterator() {
            this(nextSet(0));
        }
        
        BitSetIterator(int n) {
            this.next = n;
        }
        
        public void remove() {
            set(next,false);
        }
        
        public boolean hasNext() {
            return next != -1;
        }
        
        public int peekRow() {
            return next;
        }
        
        public int nextRow() {
            int n = next;
            next = nextSet(n+1);
            return next;
        }
        
        public RowIterator copy() {
            return new BitSetIterator(next);
        }
    };
}
