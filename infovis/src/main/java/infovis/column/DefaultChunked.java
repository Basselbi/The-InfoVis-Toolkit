/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Class DefaultChunked
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class DefaultChunked implements ChunkedColumn, Serializable {
    protected transient ArrayList chunks;
    protected int                 bytesPerCell;

    public DefaultChunked(int bytesPerCel) {
        chunks = new ArrayList();
        this.bytesPerCell = bytesPerCel;
    }

    static int indexToChunk(int index) {
        // return index / CHUNK_SIZE;
        return index >> CHUNK_BITS;
    }

    static int indexToCIndex(int index) {
        // return index % CHUNK_SIZE;
        return index & CHUNK_MASK;
    }

    public void newChunk(int c) {
        ByteBuffer chunk = ByteBuffer.allocate(bytesPerCell * CHUNK_SIZE);
        chunks.set(c, chunk);
    }

    public void deleteChunk(int c) {
        chunks.set(c, null);
    }

    public ByteBuffer getChunk(int c) {
        return (ByteBuffer) chunks.get(c);
    }

    public void touchChunk(int c) {
    }

    public void clearChunks() {
        chunks.clear();
    }

    /**
     * {@inheritDoc}
     */
    public int chunkCount() {
        return chunks.size();
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        int n = chunks.size();
        s.writeInt(n);
        byte[] buffer = new byte[CHUNK_SIZE * 4];
        for (int i = 0; i < n; i++) {
            ByteBuffer bb = getChunk(i);
            bb.get(buffer);
            s.write(buffer);
        }
    }

    protected void createTransients(int n) {
        chunks = new ArrayList(n);
    }

    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        int n = s.readInt();

        byte[] buffer = new byte[CHUNK_SIZE * 4];
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
