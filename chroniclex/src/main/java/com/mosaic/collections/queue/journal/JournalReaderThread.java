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
    private final View          bytesView = new View();

    private long pollIntervalMillis = 100;


    public JournalReaderThread( DirectoryX dataDirectory, String serviceName ) {
        this( dataDirectory, serviceName, ThreadType.NON_DAEMON );
    }

    public JournalReaderThread( DirectoryX dataDirectory, String serviceName, ThreadType threadType ) {
        super( serviceName, threadType );

        this.journal = new JournalReader( dataDirectory, getServiceName() );

        serviceDependsUpon( journal );
    }

    public void setPollIntervalMillis( long pollIntervalMillis ) {
        this.pollIntervalMillis = pollIntervalMillis;
    }


    protected long loop() throws InterruptedException {
        while ( journal.readNextInto(bytesView) ) {
            messageReceived( bytesView.bytes, bytesView.base, bytesView.maxExc );
        }

        return pollIntervalMillis;
    }


    protected abstract void messageReceived( Bytes bytes, long offset, long maxExc );


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