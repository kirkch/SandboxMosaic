package com.mosaic.io.journal;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.bytes2.BytesView2;
import com.mosaic.lang.QA;


/**
 * FlyWeight/View pattern for accessing entries within a file based journal.
 */
public abstract class JournalEntry {

    protected long       msgSeq = -1;
    protected BytesView2 bytes  = new BytesView2();  // NB accessible by Journal2 directly

    private final int fixedSizeBytes;

    protected JournalEntry( int fixedSizeBytes ) {
        this.fixedSizeBytes = fixedSizeBytes;
    }

    public long getMessageSeq() {
        return msgSeq;
    }

    public int getNumBytes() {
        return fixedSizeBytes;
    }

    public void setView( long msgSeq, Bytes2 bytes, long offset, long maxExc ) {
        QA.isEqualTo( maxExc-offset, fixedSizeBytes, "actualSize", "expectedSize");

        this.msgSeq = msgSeq;

        this.bytes.setBytes( bytes, offset, maxExc );
    }

}
