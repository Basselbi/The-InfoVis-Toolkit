/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column;

import java.nio.ByteBuffer;

import infovis.utils.ByteBufferPool;

/**
 * Class ChunkedColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public interface ChunkedColumn {
    /** Number of bits per chunk. */
    public int CHUNK_BITS = ByteBufferPool.CHUNK_BITS;
    /** Size of allocated chunks. */
    public int CHUNK_SIZE = ByteBufferPool.CHUNK_SIZE;
    /** Mask of bits. */
    public int CHUNK_MASK = ByteBufferPool.CHUNK_MASK;
    
    void newChunk(int c);
    void deleteChunk(int c);
    void touchChunk(int c);
    ByteBuffer getChunk(int index);
    void clearChunks();
    int chunkCount();
}
