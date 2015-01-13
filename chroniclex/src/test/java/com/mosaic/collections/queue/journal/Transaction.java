package com.mosaic.collections.queue.journal;

import com.mosaic.collections.queue.JournalByteView;


/**
 *
 */

public class Transaction extends JournalByteView {
    static final long FROM_INDEX  = 0;
    static final long TO_INDEX    = 8;
    static final long AMT_INDEX   = 16;
    static final int  RECORD_SIZE = 24;

    public long sizeBytes() {
        return RECORD_SIZE;
    }


    public long getFrom() {
        return bytes.readLong( base+FROM_INDEX, base+RECORD_SIZE );
    }

    public long getTo() {
        return bytes.readLong( base+TO_INDEX, base+RECORD_SIZE );
    }

    public double getAmount() {
        return bytes.readDouble( base+AMT_INDEX, base+RECORD_SIZE );
    }

    public void setFrom( long newValue ) {
        bytes.writeLong( base+FROM_INDEX, base+RECORD_SIZE, newValue );
    }

    public void setTo( long newValue ) {
        bytes.writeLong( base+TO_INDEX, base+RECORD_SIZE, newValue );
    }

    public void setAmount( double newAmount ) {
        bytes.writeDouble( base+AMT_INDEX, base+RECORD_SIZE, newAmount );
    }
}
