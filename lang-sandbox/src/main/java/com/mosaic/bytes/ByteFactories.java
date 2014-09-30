package com.mosaic.bytes;

import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.io.filesystemx.inmemory.InMemoryFile;
import com.mosaic.lang.functional.Function2;
import com.mosaic.lang.functional.LongFunction1;

import java.io.File;


/**
 * A set of stateless factory functions that create different type of Bytes.
 */
public class ByteFactories {

    /**
     * Creates Bytes on the heap.  This is the standard approach and is similar to 'new byte[size]'.
     */
    public static final LongFunction1<Bytes> ONHEAP = new LongFunction1<Bytes>() {
        public Bytes invoke( long size ) {
            return new ArrayBytes( size );
        }
    };

    /**
     * Create Bytes off of the heap.  This approach is similar too memory management in C.  We are
     * responsible for releasing the memory, the garbage collector will not get involved.  Use this
     * approach to allocate memory blocks larger than the max size java array, and/or when GC is
     * struggling.
     */
    public static final LongFunction1<Bytes> OFFHEAP = new LongFunction1<Bytes>() {
        public Bytes invoke( long size ) {
            return new OffHeapBytes( size );
        }
    };

    /**
     * Creates Bytes that are backed by a file.  The file is mapped into memory by the OS, who also
     * manages the persistence of the bytes via paging.  This approach lets us share bytes off heap
     * between two processes, it also offers a very fast mechanism for reading/writing files.  It is
     * faster than using Java's input and output streams.<p/>
     *
     * To aid unit testing, this function is sensitive to the different types of FileX.  An in-memory
     * file will simulate being memory mapped but obviously only within this process.
     */
    public static final Function2<FileX,Long,Bytes> MAPPEDFILE = new Function2<FileX,Long,Bytes>() {
        public Bytes invoke( FileX f, Long size ) {
            if ( f instanceof InMemoryFile ) {              // todo push this behaviour into FileX
                InMemoryFile file = (InMemoryFile) f;

                Bytes b = (Bytes) file.getAttachmentNbl();
                if ( b != null ) {
                    if ( b.sizeBytes() < size ) {
                        b.resize( size );
                    }
                } else {
                    b = OFFHEAP.invoke( size );

                    ((InMemoryFile) f).setAttachmentNbl( b );
                }

                return b;
            } else {
                File file = new File(f.getFullPath());

                return MemoryMappedBytes.mapFile( file, FileModeEnum.READ_ONLY, size );
            }
        }
    };

}
