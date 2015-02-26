package com.mosaic.io.journal;

import com.mosaic.bytes2.BytesView2;


/**
 *
 */
public abstract class JournalEntry {

    protected long       msgSeq = -1;
    protected BytesView2 bytes  = new BytesView2();

    public long getMessageSeq() {
        return msgSeq;
    }

}
