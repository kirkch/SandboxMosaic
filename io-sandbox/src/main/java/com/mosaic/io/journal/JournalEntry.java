package com.mosaic.io.journal;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.bytes2.BytesView2;


/**
 * FlyWeight/View pattern for accessing entries within a file based journal.
 */
public abstract class JournalEntry {

    protected long       msgSeq = -1;
    protected BytesView2 bytes  = new BytesView2();  // NB accessible by Journal2 directly

    public long getMessageSeq() {
        return msgSeq;
    }

    public void setView( long msgSeq, Bytes2 bytes, long offset, long maxExc ) {
        this.msgSeq = msgSeq;

        this.bytes.setBytes( bytes, offset, maxExc );
    }

}
