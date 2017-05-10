/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.utils;

import java.util.NoSuchElementException;

import cern.colt.function.IntComparator;
import cern.colt.list.IntArrayList;

/**
 * Class IntHeap
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class IntHeap {
    protected IntArrayList heap;
    protected IntComparator comp;
    private static final int TOP = 0;   // the index of the top of the heap
    
    /**
     * Creates an empty heap.
     */
    public IntHeap() {
        this(IdRowComparator.getInstance());
    }
    
    /**
     * Creates an IntHead with a specified comparator.
     * @param comp the comparator
     */
    public IntHeap(IntComparator comp) {
        heap = new IntArrayList();
        this.comp = comp;
    }
    
    protected IntHeap(IntArrayList heap, IntComparator comp) {
        this.heap = heap.copy();
        this.comp = comp;
    }
    
    /**
     * @return a copy of this heap
     */
    public IntHeap copy() {
        return new IntHeap(heap, comp);
    }
    
    /**
     * Removes all the elements.
     */
    public void clear() {
        heap.clear();
    }
    
    /**
     * @return the number of elements in the heap
     */
    public int size() {
        return heap.size();
    }
    
    /**
     * Inserts a value in the heap.
     * @param v the value
     */
    public void insert(int v) {
       int i = heap.size();
       heap.add(-1); // value is not important
       percolateUp(i, v);
    }

    /**
     * Returns true if the heap is empty.
     * @return true if the heap is empty
     */
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    /**
     * Returns the first element in the heap.
     * @return the first element in the heap
     * @throws NoSuchElementException if the heap is empty
     */
    public int peek() throws NoSuchElementException {
        return heap.get(TOP);
    }

    /**
     * Returns and removes the first element in the heap. 
     * @return the first element in the heap
     * @throws NoSuchElementException if the heap is empty
     */
    public int pop() throws NoSuchElementException {
        int top = heap.get(TOP);
        
        int bottomElt = heap.get(heap.size()-1);
        heap.set(TOP, bottomElt);
        heap.remove(heap.size() - 1);  // remove the last element
        if (heap.size() > 1)
            percolateDown(TOP);

        return top;
    }
    
    /**
     * Moves the element at position <code>cur</code> closer to 
     * the bottom of the heap, or returns if no further motion is
     * necessary.  Calls itself recursively if further motion is 
     * possible.
     */
    private void percolateDown(int cur) {
        int left = lChild(cur);
        int right = rChild(cur);
        int smallest;

        if ((left < heap.size()) 
                && (comp.compare(heap.get(left), heap.get(cur)) < 0))
            smallest = left;
        else
            smallest = cur;

        if ((right < heap.size()) 
                && (comp.compare(heap.get(right), heap.get(smallest)) < 0))
            smallest = right;

        if (cur != smallest) {
            swap(cur, smallest);
            percolateDown(smallest);
        }
    }

    /**
     * Moves the element <code>o</code> at position <code>cur</code> 
     * as high as it can go in the heap.  Returns the new position of the 
     * element in the heap.
     */
    private int percolateUp(int cur, int o) {
        int i = cur;
        
        while ((i > TOP) 
                && (comp.compare(heap.get(parent(i)), o) > 0)) {
            int parentElt = heap.get(parent(i));
            heap.set(i, parentElt);
            i = parent(i);
        }
        
        // place object in heap at appropriate place
        heap.set(i, o);

        return i;
    }
    
    /**
     * Returns the index of the left child of the element at 
     * index <code>i</code> of the heap.
     * @param i
     * @return
     */
    private static int lChild(int i) {
        return (i<<1) + 1;
    }
    
    /**
     * Returns the index of the right child of the element at 
     * index <code>i</code> of the heap.
     * @param i
     * @return
     */
    private static int rChild(int i) {
        return (i<<1) + 2;
    }
    
    /**
     * Returns the index of the parent of the element at 
     * index <code>i</code> of the heap.
     * @param i
     * @return
     */
    private static int parent(int i) {
        return (i-1)>>1;
    }
    
    /**
     * Swaps the positions of the elements at indices <code>i</code>
     * and <code>j</code> of the heap.
     * @param i
     * @param j
     */
    private void swap(int i, int j) {
        int iElt = heap.get(i);
        int jElt = heap.get(j);

        heap.set(i, jElt);
        heap.set(j, iElt);
    }
}
