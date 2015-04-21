package com.mosaic.collections.queue.journal;

import com.mosaic.bytes.ByteView;
import com.mosaic.bytes.Bytes;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.lang.ServiceThread;
import com.mosaic.lang.text.UTF8;


/**
 *
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class JournalReaderThread extends ServiceThread<JournalReaderThread> {

    private final JournalReader journal;

    private long pollIntervalMillis = 100;


    public JournalReaderThread( DirectoryX dataDirectory, String serviceName ) {
        this( dataDirectory, serviceName, ThreadType.NON_DAEMON );
    }

    public JournalReaderThread( DirectoryX dataDirectory, String serviceName, ThreadType threadType ) {
        super( serviceName, threadType );

        this.journal = new JournalReader( dataDirectory, getServiceName() );

        registerServicesBefore( journal );
    }

    public void setPollIntervalMillis( long pollIntervalMillis ) {
        this.pollIntervalMillis = pollIntervalMillis;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    protected long loop() throws InterruptedException {
        while ( journal.readNext(this::messageReceived) ) {}

        sendIdleNotification();

        return pollIntervalMillis;
    }


    protected abstract void messageReceived( long msgSeq, Bytes bytes, long offset, long maxExc );

    protected void sendIdleNotification() {}

    protected long sizeOfUTF8String( UTF8 str ) {
        return JournalWriter.sizeOfUTF8String( str );
    }


    private static class View extends ByteView {
        protected Bytes bytes;
        protected long  base;
        protected long  maxExc;

        public long sizeBytes() {
            return maxExc - base;
        }

        public void setBytes( Bytes bytes, long base, long maxExc ) {
            this.bytes  = bytes;
            this.base   = base;
            this.maxExc = maxExc;
        }
    }
}