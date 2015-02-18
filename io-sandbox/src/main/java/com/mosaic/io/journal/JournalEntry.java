package com.mosaic.io.journal;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.bytes2.BytesView2;


/**
 *
 */
public abstract class JournalEntry {

    private   long       msgSeq = -1;
    protected BytesView2 bytes  = new BytesView2();


    public void setBytes( long msgSeq, Bytes2 bytes, long base, long maxExc ) {
        this.msgSeq = msgSeq;

        this.bytes.setBytes( bytes, base, maxExc );
    }

    public void setBytes( long msgSeq, BytesView2 bytes ) {
        this.msgSeq = msgSeq;
        this.bytes  = bytes;
    }

    public long getMessageSeq() {
        return msgSeq;
    }

}
