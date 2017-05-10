/*****************************************************************************
 * Copyright (C) 2007 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import cern.colt.list.LongArrayList;

/**
 * <b>ByteBufferPool</b> maintains a LRU pool of ByteBuffer objects.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class ByteBufferPool {
    /** Number of bits per page. */
    public static final int             CHUNK_BITS  = 14;
    /** Size of allocated chunks. */
    public static final int             CHUNK_SIZE  = (1 << CHUNK_BITS);
    /** Mask of bits. */
    public static final int             CHUNK_MASK  = (CHUNK_SIZE - 1);
    protected transient ArrayList       pages;
    protected int                       maxPages;
    // Cacheing mechanism, not to be serialized automatically
    protected volatile int              lastOffset;
    protected volatile RandomAccessFile pageFile;
    protected transient Ref             header      = new Ref();
    protected transient int             size;
    private transient int               lastTime;
    protected volatile LongArrayList    freeOffsets = new LongArrayList();
    protected int                       pageInCount;
    protected int                       pageOutCount;

    private static ByteBufferPool       INSTANCE;
    private static final Logger         LOG         
        = Logger.getLogger(ByteBufferPool.class);

    /**
     * @return the convenient instance
     */
    public static ByteBufferPool getInstance() {
        if (INSTANCE == null) {
            String def = "20m";
            String size = System.getProperty("infovis.poolsize", def);
            if (size == null || size.length() == 0) {
            	size = def;
            }
            char c = size.charAt(size.length()-1);
            int s;
            if (Character.isDigit(c)) {
            	s = Integer.parseInt(size);
            }
            else {
            	s = Integer.parseInt(size.substring(0, size.length()-1));
            	switch(c) {
            	case 'k':
            	case 'K':
            		s *= 1024;
            		break;
            	case 'm':
            	case 'M':
            		s *= 1024*1024;
            		break;
            	case 'g':
            	case 'G':
            		s *= 1024*1024*1024;
            		break;
            	default:
            		LOG.warn("Unkown allocation unit: "+c);
            	}
            }
            LOG.info("Allocating a memory pool of " + s + " cells ("
                    + (s * 4 / 1024) + ") Kb");
            INSTANCE = new ByteBufferPool(s);
        }
        return INSTANCE;
    }

    /**
     * Creates a ByteBufferPool with a specified maximum memory.
     * 
     * @param maxMemory
     *            the maximum amount or memory used
     */
    public ByteBufferPool(int maxMemory) {
        this.maxPages = (maxMemory + CHUNK_SIZE - 1) >> CHUNK_BITS;
        header.next = header.prev = header;
    }
    
    /**
     * Open and lock a page file in the tmp directory.
     * 
     * <p>It tries to create a RandomAccessFile on a file in the tmp
     * directory with the specified prefix.  If the file exists, it
     * tries to lock it exclusively.  If it can't, it means another
     * Infovis program is already using the page file and it tries to
     * access/create another one with the same prefix until it succeeds
     * or too many files have been tried (100 currently).
     * 
     * @param prefix the file name prefix in the tmp directory
     * @return the RandomAccessFile or null 
     * @throws IOException IOException
     */
    public static RandomAccessFile openLockPageFile(String prefix) {
        RandomAccessFile ra = null;
        try {
            File file = File.createTempFile("InfoVis", null);
            File tmpDir = file.getParentFile();
            file.delete();
            for (int i = 0; i < 100; i++) {
                file = new File(tmpDir, prefix+i+".tmp");
                boolean exists = file.exists();
                ra = new RandomAccessFile(file, "rw");
                FileLock lock = ra.getChannel().tryLock();
                if (lock != null) { 
                    if (exists) {
                        LOG.info("Reusing page file for the ByteBufferPool " + file);
                    }
                    else {
                        LOG.info("Creating page file for the ByteBufferPool " + file);
                    }
                    file.deleteOnExit(); // just in case...
                    break;
                }
                else
                    ra = null;
            }
        }
        catch(IOException e) {
            LOG.error("Cannot create tmp file", e);
            return null;
        }
        
        return ra;
    }

    protected RandomAccessFile getPageFile() {
        if (pageFile == null) {
            try {
                pageFile = openLockPageFile("InfoVis");
                
            } catch (Exception e) {
                String msg = "Cannot create page file";
                LOG.error(msg, e);
                throw new java.lang.RuntimeException(msg, e);
            }
            Timer poolTimer = new Timer("InfoVis Pool Monitor");
            poolTimer.schedule(new TimerTask() {
                int inCount = pageInCount;
                int outCount = pageOutCount;
                public void run() {
                    int in = pageInCount - inCount;
                    int out = pageOutCount - outCount;
                    inCount = pageInCount;
                    outCount = pageOutCount;
                    if (in != 0 || out != 0)
                        LOG.info("Pool activity: in="+in+" out="+out);
                }
            }, 5000, 5000);
        }
        return pageFile;
    }

    /**
     * Sets the maximum memory this pool is allowed to allocate.
     * 
     * @param max
     *            the maximum memory to allocate
     */
    public void setMaxMemory(int max) {
        int maxPages = (max + CHUNK_SIZE - 1) >> CHUNK_BITS;
        if (maxPages == this.maxPages)
            return;
        if (maxPages < this.maxPages) {
            while (size >= maxPages) {
                delete(header.prev);
            }
        }
        this.maxPages = maxPages;
    }

    /**
     * @return the maximum amount of memory used
     */
    public int getMaxMemory() {
        return maxPages << CHUNK_BITS;
    }
    
    /**
     * @return the pageInCount
     */
    public int getPageInCount() {
        return pageInCount;
    }
    
    /**
     * @return the pageOutCount
     */
    public int getPageOutCount() {
        return pageOutCount;
    }

    /**
     * Creates a new Ref.
     * 
     * @return the Ref
     */
    public Ref createRef() {
        return new Ref();
    }

    protected boolean unlink(Ref e) {
        if (e == header) {
            LOG.error("Trying to unlink the header");
            throw new NoSuchElementException();
        }
        if (e.next == null) {
            return false;
        }
        e.prev.next = e.next;
        e.next.prev = e.prev;
        e.next = e.prev = null;
        size--;
        // assert(size==computeLength());
        return true;
    }

    /**
     * Deletes the specified Ref: it is no longer managed by this pool.
     * 
     * @param e
     *            the Ref
     * @return true if it was managed before, false otherwise
     */
    public boolean delete(Ref e) {
        assert(e.getPool()==this);
        boolean ret = unlink(e);
        e.buffer = null;
        if (e.fileOffset != -1) {
            freeOffsets.add(e.fileOffset);
            LOG.info("Reclaiming byte offset " + e.fileOffset);
            e.fileOffset = -1;
        }
        return ret;
    }

    protected void addBefore(Ref ref, Ref e) {
        unlink(ref);
        ref.next = e;
        ref.prev = e.prev;
        ref.prev.next = ref;
        ref.next.prev = ref;
        size++;
        // assert(size==computeLength());
    }

    // private int computeLength() {
    // int l = 0;
    // for (Ref r = header.next; r != header; r = r.next) {
    // l++;
    // }
    // return l;
    // }
    //    
    // private boolean checkLastOldest() {
    // if (size == 0) return true;
    // int last = header.prev.time;
    // for (Ref r = header.next; r != header.prev; r = r.next) {
    // if (last > r.time)
    // return false;
    // }
    //        
    //        
    // return true;
    // }
    //    
    protected void moveToFront(Ref ref) {
        if (header.next == ref)
            return;
        addBefore(ref, header.next);
        ref.time = lastTime++;
    }
    
    protected void moveToBack(Ref ref) {
        if (header.prev == ref)
            return;
        addBefore(ref, header);
    }

    protected void pageOut(Ref ref) {
        ByteBuffer chunk = ref.buffer;
        unlink(ref);
        ref.buffer = null;
        if (!ref.isDirty()) {
            return;
        }

        try {
            assert (chunk != null);
            long offset = ref.findFileOffset();
            getPageFile();
            if (pageFile.length() < (offset + CHUNK_SIZE)) {
                pageFile.setLength(offset + CHUNK_SIZE);
            }
            FileChannel channel = pageFile.getChannel();
            chunk.position(0);
            int n = channel.write(chunk, offset);
            if (n != (CHUNK_SIZE * 4)) {
                LOG.warn("Wrote only " + n + " bytes instead of "
                        + (CHUNK_SIZE * 4));
                while (chunk.remaining() != 0) {
                    offset += n;
                    n = channel.write(chunk, offset);
                    assert (n > 0);
                }
            }
            ref.setDirty(false);
            pageOutCount++;
        } catch (IOException e) {
            String msg = "Error writing page file";
            LOG.error(msg);
            throw new RuntimeException(msg, e);
        }
    }

    protected void pageIn(Ref ref) {
        assert (ref.buffer == null);
        if (size < maxPages) {
            ByteBuffer buffer = ByteBuffer.allocate(4 * CHUNK_SIZE);
            ref.buffer = buffer;
            ref.setDirty(false);
            moveToFront(ref);
            return;
        }
        Ref oldest = header.prev;
        assert (oldest != null && oldest.buffer != null);
        // assert(checkLastOldest());
        ByteBuffer chunk = oldest.buffer;
        pageOut(oldest);
        assert (oldest.buffer == null);
        ref.buffer = chunk;
        long offset = ref.getFileOffset();
        if (offset >= 0) {
            try {
                assert (pageFile.length() >= (offset + CHUNK_SIZE));
                FileChannel channel = pageFile.getChannel();
                chunk.clear();
                int n = channel.read(chunk, offset);
                if (n != (CHUNK_SIZE * 4)) {
                    LOG.warn("Read only " + n + " bytes instead of "
                            + (CHUNK_SIZE * 4));
                    while (chunk.remaining() != 0) {
                        offset += n;
                        n = channel.read(chunk, offset);
                        assert (n > 0);
                    }
                }
                chunk.position(0);
                assert (!ref.isDirty());
                pageInCount++;
            } catch (IOException e) {
                String msg = "Error reading page file";
                LOG.error(msg);
                throw new RuntimeException(msg, e);
            }
        }
        moveToFront(ref);
    }

    /**
     * <b>Ref</b> is a paged ByteBuffer holder.
     * 
     * @author Jean-Daniel Fekete
     */
    public class Ref {
        boolean    dirty;
        long       fileOffset;
        ByteBuffer buffer;
        Ref        next;
        Ref        prev;
        int        time;

        private Ref(ByteBuffer buffer, Ref next, Ref prev) {
            this.fileOffset = -1;
            this.buffer = buffer;
            this.next = next;
            this.prev = prev;
        }

        private Ref() {
            this(null, null, null);
        }

        /**
         * @return the associated pool
         */
        public ByteBufferPool getPool() {
            return ByteBufferPool.this;
        }

        /**
         * @return the ByteBuffer or null
         */
        public ByteBuffer getBuffer() {
            if (buffer == null) {
                pageIn(this);
            }
            else {
                moveToFront(this);
            }
            return buffer;
        }
        
        /**
         * Sets the buffer low on the LRU.
         *
         */
        public void bury() {
            if (buffer != null) {
                moveToBack(this);
            }
        }

        /**
         * Declares the buffer dirty
         */
        public void touch() {
            assert (buffer != null);
            dirty = true;
        }

        /**
         * {@inheritDoc}
         */
        protected void finalize() throws Throwable {
            LOG.info("Reclaiming " + this);
            delete();
        }

        /**
         * Free the ressource associated with this ref.
         */
        public void delete() {
            ByteBufferPool.this.delete(this);
        }

        /**
         * @return the dirty
         */
        public boolean isDirty() {
            return dirty;
        }

        /**
         * Sets the dirty bit
         * 
         * @param dirty
         *            the dirty to set
         */
        private void setDirty(boolean dirty) {
            this.dirty = dirty;
        }

        /**
         * @return the fileOffset
         */
        private long getFileOffset() {
            return fileOffset;
        }

        private long findFileOffset() {
            if (fileOffset == -1) {
                if (freeOffsets.isEmpty()) {
                    setFileOffset(lastOffset);
                    lastOffset += CHUNK_SIZE * 4;
                }
                else {
                    long o = freeOffsets.get(freeOffsets.size() - 1);
                    freeOffsets.remove(freeOffsets.size() - 1);
                    setFileOffset(o);
                }
            }
            return fileOffset;
        }

        /**
         * @param fileOffset
         *            the fileOffset to set
         */
        private void setFileOffset(long fileOffset) {
            this.fileOffset = fileOffset;
        }
    }
}
