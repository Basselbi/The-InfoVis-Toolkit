/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column;

import infovis.utils.ByteBufferPool;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * <b>ChunkedFloatColumn</b> is a float column
 * allocating values by chunks.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class ChunkedFloatColumn extends AbstractFloatColumn {
    private static final long serialVersionUID = 171368745920683891L;
    protected transient ArrayList     chunks;
    protected int           size;
    /** Number of bits per chunk. */
    public static final int CHUNK_BITS = ByteBufferPool.CHUNK_BITS;
    /** Size of allocated chunks. */
    public static final int CHUNK_SIZE = ByteBufferPool.CHUNK_SIZE;
    /** Mask of bits. */
    public static final int CHUNK_MASK = ByteBufferPool.CHUNK_MASK;
    
    protected transient ByteBufferPool pool;
    protected transient ArrayList chunkRefs = new ArrayList();
    
    /**
     * Constructor.
     * 
     * @param name the name
     */
    public ChunkedFloatColumn(String name) {
        this(name, CHUNK_SIZE-1, ByteBufferPool.getInstance());
    }

    /**
     * Constructor with a reserved size.
     * @param name the name
     * @param reserve the reserve
     */
    public ChunkedFloatColumn(String name, int reserve, ByteBufferPool pool) {
        super(name);
        this.pool = pool;
        chunks = new ArrayList(10);
        size = 0;
        ensureCapacity(reserve);
    }

    static int indexToChunk(int index) {
        // return index / CHUNK_SIZE;
        return index >> CHUNK_BITS;
    }

    static int indexToCIndex(int index) {
        // return index % CHUNK_SIZE;
        return index & CHUNK_MASK;
    }
    
    protected void newChunk(int c) {
        ByteBuffer chunk = ByteBuffer.allocate(4*CHUNK_SIZE);
        chunks.set(c, chunk);
    }
    
    protected void deleteChunk(int c) {
        chunks.set(c, null);
    }

    protected ByteBuffer getChunk(int c) {
        return (ByteBuffer)chunks.get(c);
    }

    protected void touchChunk(int c) {
    }
    
    protected void clearChunks() {
        chunks.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        super.clear();
        clearChunks();
        size = 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public float get(int index) {
        return getChunk(indexToChunk(index))
            .getFloat(4*indexToCIndex(index));
    }

    /**
     * {@inheritDoc}
     */
    public void set(int index, float value) {
        getChunk(indexToChunk(index))
            .putFloat(4*indexToCIndex(index), value);
        set(index);
    }

    /**
     * {@inheritDoc}
     */
    public void setExtend(int index, float element) {
        assert (index >= 0);
        if (index >= size()) {
            if (index == size) {
                int n = indexToChunk(index);
                if (n == chunks.size()) {
                    chunks.add(null);
                    newChunk(chunks.size()-1);
                }
                size = index + 1;
            }
            else {
                setSize(index + 1);
            }
        }
        set(index, element);
    }
    /**
     * {@inheritDoc}
     */
    public int capacity() {
        return chunks.size() * CHUNK_SIZE;
    }

    /**
     * {@inheritDoc}
     */
    public void ensureCapacity(int minCapacity) {
        int lastChunk = indexToChunk(minCapacity-1);
        while (chunks.size() < lastChunk) {
            chunks.add(null);
            newChunk(chunks.size()-1);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    public void setSize(int newSize) {
        if (newSize == size)
            return;
        try {
            disableNotify();
            super.setSize(newSize);
            int n = indexToChunk(newSize - 1);
            if (newSize > size) {
                while (chunks.size() <= n) {
                    chunks.add(null);
                    newChunk(chunks.size()-1);
                }
            }
            else {
                for (int last = chunks.size() - 1; n < last; last--) {
                    deleteChunk(last);
                    chunks.remove(last);
                }
            }
            size = newSize;
        } finally {
            enableNotify();
        }
    }
    
    protected void createTransients(int n) {
        chunks = new ArrayList(n);
    }
    
    private void writeObject(ObjectOutputStream s) 
        throws IOException {
        s.defaultWriteObject();
        int n = chunks.size();
        s.writeInt(n);
        byte[] buffer = new byte[CHUNK_SIZE*4];
        for (int i = 0; i < n; i++) {
            ByteBuffer bb = getChunk(i);
            bb.get(buffer);
            s.write(buffer);
        }
    }
    
    private void readObject(ObjectInputStream s) 
        throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        int n = s.readInt();
        createTransients(n);
        byte[] buffer = new byte[CHUNK_SIZE*4];
        for (int i = 0; i < n; i++) {
            chunks.add(null);
            newChunk(i);
            ByteBuffer bb = getChunk(i);
            s.read(buffer);
            bb.put(buffer);
            touchChunk(i);
        }
    }

}
