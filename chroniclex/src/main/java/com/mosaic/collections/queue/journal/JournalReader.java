package com.mosaic.collections.queue.journal;

import com.mosaic.bytes.ByteView;
import com.mosaic.collections.queue.ByteQueueReader;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.lang.StartStopMixin;


/**
 *
 */
public class JournalReader<T extends ByteView> extends StartStopMixin<JournalWriter<T>> implements ByteQueueReader<T> {
    private final DirectoryX      dataDirectory;

    private       JournalDataFile dataFile;


    public JournalReader( DirectoryX dataDirectory, String serviceName, Class<T> t ) {
        super( serviceName );

        this.dataDirectory = dataDirectory;
    }


    /**
     * Seek forward or backwards to the specified message.
     *
     * @return false if the message could not be found
     */
    public boolean seekTo( long messageSeq ) {
        return dataFile.seekTo( messageSeq );
    }

    public boolean readNextInto( T view ) {
        if ( dataFile.readNextInto(view) ) {
            return true;
        } else if ( dataFile.hasReachedEOFMarker() ) { // roll over to the next data file
            long nextFileSeq = dataFile.getFileSeq() + 1;

            dataFile.close();

            dataFile = new JournalDataFile(dataDirectory, getServiceName(), nextFileSeq).open();

            return dataFile.readNextInto( view );
        } else {    // the end of the current data file has not been reached;  we are waiting for the next message
            return false;
        }
    }




    protected void doStart() {
        this.dataFile = new JournalDataFile(dataDirectory, getServiceName(), 0).open();
    }

    protected void doStop() {
        this.dataFile.close();

        this.dataFile = null;
    }

}

