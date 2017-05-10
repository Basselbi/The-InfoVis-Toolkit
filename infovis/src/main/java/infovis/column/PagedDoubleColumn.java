/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column;

import infovis.utils.ByteBufferPool;
import infovis.utils.ByteBufferPool.Ref;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Class PagedDoubleColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class PagedDoubleColumn extends ChunkedDoubleColumn {
    private static final long serialVersionUID = 845389163051035L; 
    
    /**
     * Constructor with a column name.
     * @param name the name
     */
    public PagedDoubleColumn(String name) {
        this(name, 0);
    }

    /**
     * Constructor with a name and a reserved size.
     * @param name the name
     * @param reserve the reserved size
     */
    public PagedDoubleColumn(String name, int reserve) {
        this(name, reserve, ByteBufferPool.getInstance());
    }

    /**
     * Constructor with a name and a reserved size.
     * @param name the name
     * @param reserve the reserved size
     * @param pool the memory pool
     */
    public PagedDoubleColumn(String name, int reserve, ByteBufferPool pool) {
        super(name, reserve, pool); 
    }
    
    /**
     * {@inheritDoc}
     */
    protected void  newChunk(int c) {
        assert(c==chunks.size()-1);
        chunkRefs.add(pool.createRef());
    }

    /**
     * {@inheritDoc}
     */
    protected ByteBuffer getChunk(int index) {
        assert ((index >= 0) && (index < size()));
        int c = indexToChunk(index);
        Ref ref = (Ref)chunkRefs.get(c);
        assert(ref != null);
        ByteBuffer chunk = ref.getBuffer();
        return chunk;
    }
    
    /**
     * {@inheritDoc}
     */
    public void clear() {
        super.clear();
        int n = chunkRefs.size();
        for (int i = n-1; i >= 0; i--) {
            Ref ref = (Ref)chunkRefs.get(i);
            ref.delete();
        }
        chunkRefs.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    protected void set(int index) {
        int c = indexToChunk(index);
        Ref ref = (Ref)chunkRefs.get(c);
        ref.touch();
        super.set(index);
    }
    

    
    /**
     * Swaps in the column.
     */
    public void swapin() {
        for (int c = chunkRefs.size()-1; c >= 0; c--) {
            Ref ref = (Ref)chunkRefs.get(c);
            ref.getBuffer();
        }
    }
    
    
    /**
     * Swaps out the column.
     */
    public void swapout() {
        for (int c = chunkRefs.size()-1; c >= 0; c--) {
            Ref ref = (Ref)chunkRefs.get(c);
            ref.bury();
        }        
    }
    
    protected void createTransients(int n) {
        super.createTransients(n);
        pool = ByteBufferPool.getInstance();
        if (chunkRefs == null) {
            chunkRefs = new ArrayList(n);
        }
    }
}
