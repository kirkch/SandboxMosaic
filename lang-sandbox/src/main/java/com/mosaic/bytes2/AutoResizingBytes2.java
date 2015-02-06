package com.mosaic.bytes2;

import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;


/**
 *
 */
public class AutoResizingBytes2 extends BytesView2 {

    private SystemX system;
    private String  name;
    private long    maxExpectedSize;


    /**
     *
     * @param system          used for logging
     * @param name            used for logging
     * @param maxExpectedSize warn if the bytes grows past this size
     */
    public AutoResizingBytes2( SystemX system, Bytes2 delegate, String name, long maxExpectedSize ) {
        super( delegate );

        this.system = system;
        this.name = name;
        this.maxExpectedSize = maxExpectedSize;
    }

    protected void touchRW( long offset, long maxExc, long size ) {
        resizeIfNeeded( maxExc );
    }

    private void resizeIfNeeded( long requiredEndExc ) {
        long originalSize  = sizeBytes();

        if ( requiredEndExc > originalSize ) {
            long newTargetSize = selectNewSize( originalSize, requiredEndExc );

            super.resize( newTargetSize );

            system.devAudit( "ResizableBytes '%s' has been resized from %s to %s bytes", name, originalSize, newTargetSize );

            QA.argIsEqualTo( newTargetSize, super.sizeBytes(), "newLength", "super.bufferLength()" );

            if ( newTargetSize > maxExpectedSize ) {
                system.warn( "ResizableBytes '%s' has grown beyond the max expected size of '%s' bytes to '%s'", name, maxExpectedSize, newTargetSize );
            }
        }
    }

    private long selectNewSize( long currentSize, long requiredEndExc ) {
        return Math.max( currentSize*2, requiredEndExc );
    }

}
