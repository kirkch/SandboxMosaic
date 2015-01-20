package com.mosaic.collections.queue;

import com.mosaic.bytes.ByteView;
import com.mosaic.bytes.Bytes;


/**
 *
 */
public abstract class JournalByteView extends ByteView {

    private long msgSeq = -1;


    public void setBytes( Bytes bytes, long base, long maxExc ) {
        this.msgSeq = -1;

        super.setBytes( bytes, base, maxExc );
    }

    public void setBytes( long msgSeq, Bytes bytes, long base, long maxExc ) {
        this.msgSeq = msgSeq;

        super.setBytes( bytes, base, maxExc );
    }

    public long getMessageSeq() {
        return msgSeq;
    }

}
