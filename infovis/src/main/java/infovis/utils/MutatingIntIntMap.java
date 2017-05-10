/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.utils;

import cern.colt.function.IntIntProcedure;
import cern.colt.function.IntProcedure;
import cern.colt.list.IntArrayList;
import cern.colt.map.AbstractIntIntMap;
import cern.colt.map.OpenIntIntHashMap;

/**
 * <b>MutatingIntIntMap</b> is an IntInt map that
 * changes its internal representation depending on
 * its load factor.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class MutatingIntIntMap extends AbstractIntIntMap {
    protected IntArrayList denseMap;
    protected OpenIntIntHashMap sparseMap;
    protected transient int maxIndex;
    protected transient int modCount;
    protected transient int mutationMod;
    protected int initialCapacity;
    
    /**
     * Constructs an empty map with default capacity and default load factors.
     */
    public MutatingIntIntMap() {
        this(defaultCapacity);
    }
    /**
     * Constructs an empty map with the specified initial capacity and default load factors.
     *
     * @param      initialCapacity   the initial capacity of the map.
     * @throws     IllegalArgumentException if the initial capacity is less
     *             than zero.
     */
    public MutatingIntIntMap(int initialCapacity) {
        this(initialCapacity, defaultMinLoadFactor, defaultMaxLoadFactor);
    }
    /**
     * Constructs an empty map with
     * the specified initial capacity and the specified minimum and maximum load factor.
     *
     * @param      initialCapacity   the initial capacity.
     * @param      minLoadFactor        the minimum load factor.
     * @param      maxLoadFactor        the maximum load factor.
     * @throws  IllegalArgumentException if <tt>initialCapacity < 0 || (minLoadFactor < 0.0 || minLoadFactor >= 1.0) || (maxLoadFactor <= 0.0 || maxLoadFactor >= 1.0) || (minLoadFactor >= maxLoadFactor)</tt>.
     */
    public MutatingIntIntMap(int initialCapacity, double minLoadFactor, double maxLoadFactor) {
        this.minLoadFactor = minLoadFactor;
        this.maxLoadFactor = maxLoadFactor;
        this.initialCapacity = initialCapacity;
        sparseMap = new OpenIntIntHashMap(initialCapacity, minLoadFactor, maxLoadFactor);
        maxIndex = -1;
    }
    
    /**
     * {@inheritDoc}
     */
    public void clear() {
        sparseMap.clear();
        denseMap = null;
        maxIndex = -1;
    }
    
    /**
     * {@inheritDoc}
     */
    public int size() {
        if (denseMap != null) {
            return denseMap.size() - sparseMap.size();
        }
        else {
            return sparseMap.size();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Object clone() {
        MutatingIntIntMap copy = (MutatingIntIntMap)super.clone();
        if (copy.denseMap != null) {
            copy.denseMap = copy.denseMap.copy();
        }
        copy.sparseMap = (OpenIntIntHashMap)copy.sparseMap.copy();
        return copy;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean containsKey(int key) {
        if (denseMap != null) {
            return key < denseMap.size() && ! sparseMap.containsKey(key);
        }
        else {
            return sparseMap.containsKey(key);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean containsValue(int value) {
        if (denseMap != null) {
            return keyOf(value)>=0;
        }
        else {
            return sparseMap.containsValue(value);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void ensureCapacity(int minCapacity) {
        if (denseMap != null) {
            denseMap.ensureCapacity(minCapacity);
        }
        else {
            sparseMap.ensureCapacity(minCapacity);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean forEachKey(IntProcedure proc) {
        if (denseMap != null) {
            for (int i = 0; i < denseMap.size(); i++) {
                if (! sparseMap.containsKey(i)) {
                    if (! proc.apply(i))
                        return false;
                }
            }
            return true;
        }
        else {
            return sparseMap.forEachKey(proc);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean forEachPair(IntIntProcedure proc) {
        if (denseMap != null) {
            for (int i = 0; i < denseMap.size(); i++) {
                if (! sparseMap.containsKey(i)) {
                    if (! proc.apply(i, denseMap.getQuick(i)))
                        return false;
                }
            }
            return true;
        }
        else {
            return sparseMap.forEachPair(proc);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public int get(int key) {
        if (denseMap != null) {
            if (! sparseMap.containsKey(key))
                return 0; // not contained
            return denseMap.get(key);
        }
        else {
            return sparseMap.get(key);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public int keyOf(int value) {
        if (denseMap != null) {
            for (int i = 0; i < denseMap.size(); i++) {
                if (! sparseMap.containsKey(i)) {
                    if (value==denseMap.getQuick(i)) {
                        return i;
                    }
                }
            }
            return Integer.MIN_VALUE;
        }
        else {
            return sparseMap.keyOf(value);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void keys(IntArrayList list) {
        if (denseMap != null) {
            list.setSize(distinct);
            int[] elements = list.elements();
            
            int j=0;
            for (int i = 0; i < denseMap.size(); i++) {
                if (! sparseMap.containsKey(i)) {
                    elements[j++] = i;
                }
            }
        }
        else {
            sparseMap.keys(list);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void pairsMatching(
            IntIntProcedure condition, 
            IntArrayList keyList, 
            IntArrayList valueList) {
        if (denseMap != null) {
            keyList.clear();
            valueList.clear();
            for (int i = 0; i < denseMap.size(); i++) {
                if (! sparseMap.containsKey(i)) {
                    keyList.add(i);
                    valueList.add(denseMap.getQuick(i));
                }
            }
        }
        else {
            sparseMap.pairsMatching(condition, keyList, valueList);
        }
    }
    
    protected int sparseAlloc(int size) {
        return (int)(size*9/minLoadFactor);
    }
    
    protected int denseAlloc(int size, int gaps) {
        return size*4 + sparseAlloc(gaps);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean put(int key, int value) {
        assert(key >= 0);
        if (containsKey(key)) {
            if (denseMap != null) {
                denseMap.set(key, value);
            }
            else {
                sparseMap.put(key, value);
            }
            return false;
        }
        modCount++;
        if (size()==0 && key == 0) {
            denseMap = new IntArrayList(initialCapacity);
        }
        if (denseMap != null) {
            if (key < denseMap.size()) {
                assert(sparseMap.containsKey(key));
                sparseMap.removeKey(key);
                denseMap.set(key, value);
            }
            else if (key == denseMap.size()) {
                maxIndex = key;
                denseMap.add(key);
            }
            else {
                if (worthMutating()) {
                    // should insert "gap" entries in the sparse map to fill the gap
                    int gap = key - denseMap.size();
                    int sparseSize = sparseAlloc(size()+1);
                    int denseSize = denseAlloc(key+1, gap + sparseMap.size());
    
                    if (sparseSize < denseSize) {
                        mutate();
                        return put(key, value);
                    }
                }
                for (int i = denseMap.size(); i < key; i++) {
                    sparseMap.put(i, i);
                }
                maxIndex = key;
                denseMap.ensureCapacity(key+1);
                denseMap.set(key, value);
            }
        }
        else {
            assert(key != maxIndex);
            maxIndex = Math.max(maxIndex, key);
            if (worthMutating()) {
                int sparseSize = sparseAlloc(size()+1);
                int denseSize = denseAlloc(maxIndex+1, maxIndex+1-size());
            
                if (sparseSize > denseSize) {
                    mutate();
                    return put(key, value);
                }
            }
            return sparseMap.put(key, value);
        }
        return true;
    }
    
    /**
     * @return <code>true</code> if mutation is worth happening
     */
    public boolean worthMutating() {
        return (modCount-mutationMod) > 10;
    }
    
    /**
     * Change the internal representation.
     */
    public void mutate() {
//        System.err.println("Mutating #"+hashCode());
        mutationMod = modCount;
        OpenIntIntHashMap newSparseMap = new OpenIntIntHashMap(size()+1, minLoadFactor, maxLoadFactor);
        if (denseMap != null) {
            for (int i = 0; i < denseMap.size(); i++) {
                if (! sparseMap.containsKey(i)) {
                    newSparseMap.put(i, denseMap.getQuick(i));
                }
            }
            denseMap = null;
        }
        else {
            denseMap = new IntArrayList((maxIndex+1)*3/2+1);
            denseMap.setSize(maxIndex+1);
            for (int i = 0; i <= maxIndex; i++) {
                if (sparseMap.containsKey(i)) {
                    denseMap.set(i, sparseMap.get(i));
                }
                else {
                    newSparseMap.put(i, i);
                }
            }
        }
        sparseMap = newSparseMap;
    }
    
    protected void maybeMutate(int denseSize, int sparseSize) {
        if (! worthMutating()) {
            return; // don't mutate too often
        }
        if (denseMap != null) {
            if (sparseSize < denseSize) { 
                mutate();
            }
        }
        else {
            if (sparseSize > denseSize) { 
                mutate();
            }
        }
    }

    /**
     * Mutate if the representation is not optimal.
     */
    public void maybeMutate() {
        if ((modCount-mutationMod) <= 1000) {
            return; // don't mutate too often
        }
        int sparseSize = sparseAlloc(size());
        int denseSize;
        if (denseMap != null) {
            denseSize = denseAlloc(maxIndex+1, sparseMap.size());
        }
        else {
            denseSize = denseAlloc(maxIndex+1, maxIndex+1-sparseMap.size());
        }
        maybeMutate(denseSize, sparseSize);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean removeKey(int key) {
        if (! containsKey(key)) {
            return false;
        }
        modCount++;
        if (denseMap != null) {
            if (key == (denseMap.size()-1)) {
                denseMap.setSize(key);
                maxIndex = key-1;
                for (key--; key >= 0; key--) {
                    if (sparseMap.containsKey(key)) {
                        sparseMap.removeKey(key);
                        denseMap.setSize(key);
                        maxIndex = key-1;
                    }
                }
            }
            else {
                sparseMap.put(key, key);
            }
            maybeMutate();
        }
        else {
            sparseMap.removeKey(key);
            maxIndex = key;
            for (key--; key >= 0; key--) {
                if (sparseMap.containsKey(key)) {
                    break;
                }
                maxIndex = key;
            }
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public void trimToSize() {
        if (denseMap != null) {
            denseMap.trimToSize();
        }
        sparseMap.trimToSize();
    }
    
    /**
     * {@inheritDoc}
     */
    public void values(IntArrayList list) {
        if (denseMap != null) {
            for (int i = 0; i < denseMap.size(); i++) {
                if (! sparseMap.containsKey(i)) {
                    list.add(denseMap.getQuick(i));
                }
            }
        }
        else {
            sparseMap.values(list);
        }
    }
}
