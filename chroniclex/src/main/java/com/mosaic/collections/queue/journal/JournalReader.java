package com.mosaic.collections.queue.journal;

import com.mosaic.bytes.ByteView;
import com.mosaic.collections.queue.ByteQueueReader;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.lang.QA;
import com.mosaic.lang.StartStopMixin;


/**
 *
 */
public class JournalReader extends StartStopMixin<JournalReader> implements ByteQueueReader {
    private final DirectoryX      dataDirectory;
    private final long            startFrom;

    private       JournalDataFile dataFile;


    public JournalReader( DirectoryX dataDirectory, String serviceName ) {
        this( dataDirectory, serviceName, 0 );
    }

    public JournalReader( DirectoryX dataDirectory, String serviceName, long startFrom ) {
        super( serviceName );

        QA.argIsGTEZero( startFrom, "startFrom" );
        QA.argNotNull( dataDirectory, "dataDirectory" );

        this.dataDirectory = dataDirectory;
        this.startFrom     = startFrom;
    }


    /**
     * Seek forward or backwards to the specified message.
     *
     * @return false if the message could not be found
     */
    public boolean seekTo( long messageSeq ) {
        dataFile.close();

        this.dataFile = JournalDataFile.openAtRO( dataDirectory, getServiceName(), messageSeq ).open();

        return dataFile.seekTo( messageSeq );
    }

    public <T extends ByteView> boolean readNextInto( T view ) {
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
        this.dataFile = JournalDataFile.openAtRO( dataDirectory, getServiceName(), startFrom ).open();

        this.dataFile.seekTo( startFrom );
    }

    protected void doStop() {
        if ( dataFile == null ) {
            return;
        }

        this.dataFile.close();

        this.dataFile = null;
    }

}

