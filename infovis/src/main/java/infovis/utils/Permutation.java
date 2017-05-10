/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;

import cern.colt.Sorting;
import cern.colt.function.IntComparator;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;

/**
 * A <b>Permutation</b> maintains a permutation of indices, used to present
 * an arbitrarily sorted and/or selected view of the original data: 
 * getDirect(index) returns the record of the original table that should
 * be displayed at a given (row or column) index, and getInverse(record) returns
 * the index corresponding to that record, or Graph.NULL if no
 * record is mapped to that index. 
 * 
 * <p>
 * Maintain two tables called <code>direct</code> and <code>inverse</code>.
 * <p>
 * The <code>direct</code> table usually is the result of a indirect sort over
 * a column. It contains the index of the rows sorted in a specific order. For
 * example, if a column containing {6, 7, 5} is sorted in ascending order, the
 * direct table will contain {2, 0, 1}. 
 * <p>
 * The <code>inverse</code> table contains the index of a given row. With the
 * previous example, it contains {1, 2, 0} meaning that the index or rows
 * {0,1,2} are {1,2,0}.  
 * 
 * Where the permutation is sparse, the  <code>inverse</code> table is 
 * replaced by a hashmap.  The function <code>recomputeInverse()</code makes this choice. 
 * All operations that modify the permutation first <code>invalidateInverse()</code> to trigger 
 * recomputation if and when the inverse index is next needed. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.54 $
 */
public class Permutation extends ChangeManager 
    implements RowComparator, RowFilter, Serializable {
    protected IntArrayList                direct;
    protected transient IntArrayList      inverse;
    protected transient OpenIntIntHashMap inverseMap;
    protected transient int               minIndex;
    protected transient int               maxIndex;
    protected ArrayList                   listeners;
    private transient ChangeEvent         changeEvent;

    /**
     * Creates an identity permutation for a specified size.
     * 
     * @param size
     *            the size
     */
    public Permutation(int size) {
        this();
        fillPermutation(size);
    }

    /**
     * Creates a permutation from an iterator that enumerates the valid indexes.
     * 
     * @param iter
     *            the iterator
     */
    public Permutation(RowIterator iter) {
        this();
        fillPermutation(iter);
    }

    /**
     * Creates a permutation given its direct table.
     * 
     * @param perm
     *            the direct table
     */
    public Permutation(int[] perm) {
        this();
        assert (checkPermutation(perm, perm.length));
        direct = new IntArrayList(perm);
    }
    
    
    /**
     * Creates a permutation given its direct table.
     * 
     * @param perm
     *            the direct table
     */
    public Permutation(IntArrayList perm) {
        this();
        assert (checkPermutation(perm.elements(), perm.size()));
        direct.addAllOf(perm);
    }
    
    /**
     * Copies a permutation.
     * @param perm the permutation to copy
     */
    public Permutation(Permutation perm) {
        this(perm.getDirect());
    }

    /**
     * Creates an empty permutation.
     */
    public Permutation() {
        direct = new IntArrayList();
        minIndex = -1;
        maxIndex = -1;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (super.equals(obj))
            return true;
        if (!(obj instanceof Permutation)) {
            return false;
        }
        Permutation other = (Permutation) obj;
        return direct.equals(other.direct);
        // return true;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int result = 1;
        int[] elements = direct.elements();
        int n = direct.size();
        for (int i = 0; i < n; i++) {
            result = 31 * result + elements[i];
        }

        return result;
    }

    /**
     * Checks whether a specified permutation is valid.
     * 
     * @param perm
     *            the permutation
     * @param size
     *            the size
     * @return <code>true</code> if it is valid, <code>false</code>
     *         otherwise
     */
    public boolean checkPermutation(int[] perm, int size) {
        OpenIntIntHashMap hash = new OpenIntIntHashMap();

        for (int i = 0; i < size; i++) {
            if (!hash.put(perm[i], i)) {
                return false;
            }
        }

        return true;
    }

    protected ChangeEvent createChangeEvent() {
        if (changeEvent == null) {
            changeEvent = new ChangeEvent(this);
        }
        return changeEvent;
    }
    
    protected void modified() {
        super.modified(0, size());
    }

    /**
     * Empties the permutation.
     */
    public void clear() {
        modified();
        minIndex = -1;
        maxIndex = -1;
        direct.clear();
        invalidateInverse();
    }

    /**
     * Creates an identity permutation with the specified size.
     * 
     * @param size
     *            the size
     */
    public void fillPermutation(int size) {
        disableNotify();
        clear();
        minIndex = 0;
        maxIndex = size - 1;
        for (int i = 0; i < size; i++) {
            direct.add(i);
        }
        inverseMap = null;
        inverse = direct;
        enableNotify();
    }

    /**
     * Adds a direct element.
     * 
     * @param row the row
     * 
     */
    public void add(int row) {
        assert(getInverse(row) == -1)
            : "row "+row+" already exists in this permutation";
        direct.add(row);
        invalidateInverse();
        modified(direct.size()-1);
    }

    /**
     * Removes a direct element.
     * @param i the index
     */
    public void remove(int i) {
        direct.remove(i);
        invalidateInverse();
        modified(i, size());
    }

    /**
     * Inserts a row before a specified index in this permutation. 
     * @param i the index
     * @param row the row to insert
     */
    public void insert(int i, int row) {
        assert(getInverse(row) == -1)
        : "row "+row+" already exists in this permutation";
        direct.beforeInsert(i, row);
        invalidateInverse();
        modified(i,direct.size());
    }

    /**
     * Exchange two value
     * @param i the first value
     * @param j the second value
     */
    public void swap(int i, int j) {
        if (i == j) return;
        int tmp = direct.get(i);
        direct.set(i, direct.get(j));
        direct.set(j, tmp);
        invalidateInverse();
        disableNotify();
        modified(i);
        modified(j);
        enableNotify();
    }

    /**
     * Fills the permutation with an iterator containing direct indices.
     * 
     * @param iter
     *            the iterator
     */
    public void fillPermutation(RowIterator iter) {
        disableNotify();
        clear();
        try {
            while (iter.hasNext()) {
                int i = iter.nextRow();
                direct.add(i);
            }
        } finally {
            enableNotify();
        }
    }

    /**
     * Fills the permutation with a direct permutation.
     * 
     * @param perm
     *            the permutation
     */
    public void fillPermutation(int[] perm) {
        disableNotify();
        clear();
        try {
            for (int i = 0; i < perm.length; i++) {
                direct.add(perm[i]);
            }
        } finally {
            enableNotify();
        }
    }

    /**
     * Fills the permutation with a direct permutation.
     * 
     * @param perm
     *            the permutation
     */
    public void fillPermutation(IntArrayList perm) {
        disableNotify();
        clear();
        try {
            for (int i = 0; i < perm.size(); i++) {
                direct.add(perm.get(i));
            }
        } finally {
            enableNotify();
        }
    }

    /**
     * Fills the permutation with filtered values from 0 to size-1.
     * 
     * @param size
     *            the size
     * @param comp
     *            the comparator, used only for filtering.
     */
    public void fillPermutation(int size, RowComparator comp) {
        disableNotify();
        clear();
        for (int i = 0; i < size; i++) {
            if (!comp.isValueUndefined(i)) {
                direct.add(i);
            }
        }
        enableNotify();
    }

    /**
     * Fills the permutation with filtered values from 0 to size-1.
     * 
     * @param size
     *            the size
     * @param filter
     *            the filter
     */
    public void fillPermutation(int size, RowFilter filter) {
        disableNotify();
        clear();
        for (int i = 0; i < size; i++) {
            if (!filter.isFiltered(i)) {
                direct.add(i);
            }
        }
        enableNotify();
    }

    /**
     * Shuffles the permutation.
     */
    public void shuffle() {
        direct.shuffle();
        invalidateInverse();
        modified(0,size());
//        shuffle((int) System.currentTimeMillis());
    }

    /**
     * Reverses the order of the permutation, making it read in order from its former
     * maximum to its former minimum.  This has the effect of mirror-imaging the
     * row or column display on the screen, i.e. from top to bottom or left to right.
     */
    public void inverse() {
        for (int min = 0, max = size() - 1; min < max; min++, max--) {
            int tmp = direct.getQuick(min);
            direct.setQuick(min, direct.getQuick(max));
            direct.setQuick(max, tmp);
        }
        invalidateInverse();
        modified(0,size());
    }
    
    /**
     * @return the direct array list
     */
    public IntArrayList getDirect() {
    	return direct.copy();
    }

    /**
     * Returns the record of the original table that should
     * be displayed at a given (row or column) index.
     * 
     * @param i
     *            the index
     * 
     * @return the row column at a specified index.
     */
    public int getDirect(int i) {
        return direct.get(i);
    }
    
    /**
     * Returns a permuted number if p is non-null; else the original number
     * @param p	Permutation, which may be null
     * @param i Index
     * @return	Permuted number if p is non-null; else the original number
     */
    public static int getDirect( Permutation p, int i ) {
    	if( p == null )
    		return i;
    	if (i >= p.size())
    	    return -1;
    	/* NAT if(p.size()>=i)
    		return -1;*/
    	return p.getDirect( i ); 
    }

    /**
     * Returns the number of valid indexes in the permutation.
     * 
     * @return the number of valid indexes in the permutation
     */
    public int getDirectCount() {
        return direct.size();
    }
    
    /**
     * @return true if the permutation is the identity
     */
    public boolean isIdentity() {
        for (int i = 0; i < direct.size(); i++) {
            if (direct.get(i) != i)
                return false;
        }
        return true;
    }

    /**
     * Returns the (row or column) index corresponding to a record of the original table,
     * or Graph.NULL if no record is mapped to that index. 
     * 
     * @param i the record of the original table
     * 
     * @return 	the (row or column) index corresponding to that record, or Graph.NULL 
     * if no record is mapped to that index.
     */
    public int getInverse(int i) {
        recomputeInverse();
        if (i < minIndex || i > maxIndex)
            return -1;
        
        if  (inverse != null)
            return inverse.get(i-minIndex);
        else if (inverseMap != null) {
            int ret = inverseMap.get(i); // get returns 0 when the key is not there
            if (ret == 0 && ! inverseMap.containsKey(i))
                return -1;
            return ret;
        }
        else {
            return i - minIndex; // special case
        }
            
    }
    
    /**
     * Return the row or column index of a specified vertex# v in Permutation p, or v
     * (the unchanged vertex number) if p is null.
     * 
     * @param p	a Permutation or null
     * @param v	specified vertex#  
     * @return	row or column index of vertex# v in Permutation p, or v (unchanged) if p 
     * is null
     */
    public static int getInverse( Permutation p, int v ) {
    	if( p == null )
    		return v;
    	return p.getInverse( v );
    }

    protected void invalidateInverse() {
        inverse = null;
        inverseMap = null;
        minIndex = maxIndex = -1;
    }

    /**
     * Compute either a direct or hashed inverse index (from display location
     * back to the value that created it), whichever will be smaller.  Note that 
     * a hashed index requires 4x the space per occupied element (a key and a value,  
     * with half the entries left empty so open addressing (re-hashing collisions)
     * doesn't generate long chains.  So sparse permutations with less than 1/4 of the 
     * possible entries (i.e. max - min value) filled are hashed, but anything denser
     * uses an array.
     *
     */
    protected void recomputeInverse() {
        if (inverse != null || inverseMap != null) 
            return;
        if (direct.size() == 0) return;
        if (maxIndex == -1 || minIndex == -1) {
            maxIndex = 0;
            minIndex = Integer.MAX_VALUE;
            for (int i = direct.size()-1; i >= 0; i--) {
                int v = direct.getQuick(i);
                maxIndex = Math.max(maxIndex, v);
                minIndex = Math.min(minIndex, v);
            }
        }
        int size = maxIndex - minIndex + 1;
        
        // 4 is: one for key, one for value with a load factor of 0.5 
        int hashSize = direct.size()*4;
        if (hashSize < size) {
            inverseMap = new OpenIntIntHashMap(direct.size());
            for (int i = direct.size()-1; i >= 0; i--) {
                int index = direct.get(i);
                assert (!inverseMap.containsKey(index))
                : "index: " + index  
                + " inverse: " + inverseMap.get(index);
                inverseMap.put(index, i);
            }            
        }
        else {
            inverse = new IntArrayList(size);
            inverse.setSize(size);
            inverse.fillFromToWith(0, size-1, -1);
            for (int i = direct.size(); --i >= 0; ) {
                int index = direct.get(i)-minIndex;
                assert (inverse.getQuick(index) == -1)
                : "index: " + index  
                + " inverse: " + inverse.getQuick(index);
                //@TODO Doesn't always hold. Does this mean data overwrite?
                //If so, make exception not assertion
                inverse.setQuick(index, i);
            }
        }
//        modified();
    }

    /**
     * Sorts the permutation according to a comparator.
     * 
     * @param comp
     *            the comparator
     */
    public void sort(IntComparator comp) {
        Sorting.quickSort(direct.elements(), 0, size(), comp);
        invalidateInverse();
        modified();
    }
    
    /**
     * Sorts the permutation according to a comparator.
     * 
     * @param comp
     *            the comparator
     */
    public void stableSort(IntComparator comp) {
        Sorting.mergeSort(direct.elements(), 0, size(), comp);
        invalidateInverse();
        modified();
    }

    /**
     * Applies a permutation to the current permutation.
     * 
     * @param perm
     *            the permutation
     */
    public void permute(Permutation perm) {
        permute(perm.direct);
    }

    /**
     * Applies a permutation to the current permutation.
     * 
     * @param perm
     *            a table of indices
     */
    public void permute(IntArrayList perm) {
        assert (size() >= perm.size());
        IntArrayList newDirect = new IntArrayList(perm.size());
        newDirect.setSize(perm.size());
        for (int i = 0; i < perm.size(); i++) {
            newDirect.set(i, direct.get(perm.get(i)));
        }
        direct = newDirect;
        invalidateInverse();
        modified();
    }

    /**
     * Applies a permutation to the current permutation.
     * 
     * @param perm
     *            a table of indices
     */
    public void permute(int[] perm) {
        assert (size() >= perm.length);
        IntArrayList newDirect = new IntArrayList(perm.length);
        newDirect.setSize(perm.length);
        for (int i = 0; i < perm.length; i++) {
            newDirect.set(i, direct.get(perm[i]));
        }
        direct = newDirect;
        invalidateInverse();
        modified(0,size()-1);
    }

    /**
     * Filters the permutation, removing values undefined in the comparator.
     * 
     * @param comp
     *            the comparator.
     */
    public void filter(RowComparator comp) {
        int modified = -1;
        int last = size()-1;

        for (int i = 0; i < direct.size(); i++) {
            if (comp.isValueUndefined(direct.get(i))) {
                direct.remove(i);
                if (modified == -1)
                    modified = i;
                i--;
            }
        }
        if (modified != -1) {
            invalidateInverse();
            modified(modified,last);
        }
    }

    /**
     * Filters the permutation.
     * 
     * @param filter
     *            the filter
     */
    public void filter(RowFilter filter) {
        int modified = -1;
        int last = size()-1;
        for (int i = 0; i < direct.size(); i++) {
            if (filter.isFiltered(direct.get(i))) {
                direct.remove(i);
                if (modified == -1)
                    modified = i;
                i--;
            }
        }
        if (modified != -1) {
            invalidateInverse();
            modified(modified, last);
        }
    }

    /**
     * Filters out one row in the permutation.
     * 
     * @param row
     *            the row
     */
    public void filter(int row) {
        // TODO test
        int i = getInverse(row);
        if (i < 0)
            return;
        direct.remove(i);
        invalidateInverse();
        modified(i, size()-1);
    }

    /**
     * Filters out a set of rows.
     * 
     * @param iter
     *            the iterator over the filtered rows
     */
    public void filter(RowIterator iter) {
        IntArrayList rows = new IntArrayList();
        while(iter.hasNext()) {
            int row = iter.nextRow();
            int index = getInverse(row); 
            if (index != -1) {
                rows.add(index);
            }
        }
        Sorting.quickSort(rows.elements(), 0, rows.size()-1, new IntComparator() {
            public int compare(int a, int b) {
                return getInverse(a)-getInverse(b);
            }
        });
        for (int i = rows.size()-1; i >= 0; i--) {
            direct.remove(rows.getQuick(i));
        }
        invalidateInverse();
        modified();
    }

    /**
     * Returns the size of the direct table.
     * 
     * @return the size of the direct table.
     */
    public int size() {
        return direct.size();
    }

    /**
     * Returns an iterator over the indexes maintained by the permutation.
     * 
     * @return an iterator over the indexes maintained by the permutation
     */
    public RowIterator iterator() {
        return new TableIterator(0, direct.size()) {
            public int nextRow() {
                return direct.get(super.nextRow());
            }

            public int peekRow() {
                return direct.get(super.peekRow());
            }
        };
    }

    /**
     * Returns an iterator over the indexes maintained by the permutation in
     * reverse order.
     * 
     * @return an iterator over the indexes maintained by the permutation in
     *         reverse order.
     */
    public RowIterator reverseIterator() {
        return new TableIterator(direct.size() - 1, -1, false) {
            public int nextRow() {
                return direct.get(super.nextRow());
            }

            public int peekRow() {
                return direct.get(super.peekRow());
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public int compare(int row1, int row2) {
        int a = getInverse(row1);
        int b = getInverse(row2);
        if (a == -1) {
            if (b == -1) {
                return a - b;
            }
            else
                return 1;
        }
        else if (b == -1) {
            return -1;
        }
        return a - b;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValueUndefined(int row) {
        return getInverse(row) == -1;
    }

    /**
     * Returns the minumum value hold by the permutation.
     * 
     * @return the minumum value
     */
    public int getMinIndex() {
        recomputeInverse();
        return minIndex;
    }

    /**
     * Returns the maximum value hold by the permutation.
     * 
     * @return the maximum value
     */
    public int getMaxIndex() {
        recomputeInverse();
        return maxIndex;
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < size(); i++) {
            if (i != 0) {
                sb.append(" ");
            }
            sb.append(getDirect(i));
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Returns true if the specified row of the original table is
     * filtered out.
     * @param row 	the row of the original table to check
     * @return 		true if the specified row of the original table is
     * filtered out
     */
	public boolean isFiltered(int row) {
		return getInverse(row) < 0;
	}
	
	/**
	 * Applies a permutation to a specified table.
	 * @param indices the table containing the indices
	 * @return a permuted table
	 */
	public int[] applyTo(int[] indices) {
	    int[] ret = new int[indices.length];
	    for (int k = indices.length; --k >= 0; ) {
	        int i = indices[k];
	        if (i < 0 || i > size()) continue;
	        ret[k] = getDirect(i);
	    }
	    return ret;
	}
}
