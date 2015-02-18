package com.mosaic.io.journal;


import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.lang.QA;
import com.mosaic.lang.StartStopMixin;


/**
 * Reader for journal data files created using JournalWriter.
 */
public class JournalReader2 extends StartStopMixin<JournalReader2> {
    private final DirectoryX dataDirectory;
    private final long            startFrom;

//    private       JournalDataFile dataFile;


    /**
     * Opens the journal at the first message.  Usually this will be the message with seq zero,
     * however if the first file has been archived then it will be the first message of the next
     * file.  If no data files exist, then the first data file will be created empty.
     */
    public JournalReader2( DirectoryX dataDirectory, String serviceName ) {
        this( dataDirectory, serviceName, 0 );
    }

    public JournalReader2( DirectoryX dataDirectory, String serviceName, long startFrom ) {
        super( serviceName );

        QA.argIsGTEZero( startFrom, "startFrom" );
        QA.argNotNull( dataDirectory, "dataDirectory" );

        this.dataDirectory = dataDirectory;
        this.startFrom     = startFrom;
    }


    public boolean readNextInto( JournalEntry journalEntry ) {
        return false;
    }

    public boolean seekTo( long i ) {
        return false;
    }


//    /**
//     * Seek forward or backwards to the specified message.
//     *
//     * @return false if the message could not be found
//     */
//    public boolean seekTo( long messageSeq ) {
//        dataFile.close();
//
//        this.dataFile = JournalDataFile.openAtRO( dataDirectory, getServiceName(), messageSeq ).open();
//
//        return dataFile.seekTo( messageSeq );
//    }
//
//    public boolean readNext( ByteQueueCallback readerFunction ) {
//        if ( dataFile == null ) {
//            return false;
//        } else if ( dataFile.readNext(readerFunction) ) {
//            return true;
//        } else if ( dataFile.hasReachedEOFMarker() ) { // roll over to the next data file
//            long nextFileSeq = dataFile.getFileSeq() + 1;
//
//            dataFile.close();
//
//            dataFile = new JournalDataFile(dataDirectory, getServiceName(), nextFileSeq).open();
//
//            return dataFile.readNext( readerFunction );
//        } else {    // the end of the current data file has not been reached;  we are waiting for the next message
//            return false;
//        }
//    }
//
//    public <T extends JournalByteView> boolean readNextInto( T view ) {
//        return readNext( view::setBytes );
//    }
//
//
//    protected void doStart() {
//        this.dataFile = JournalDataFile.openAtRO( dataDirectory, getServiceName(), startFrom ).open();
//
//        this.dataFile.seekTo( startFrom );
//    }
//
//    protected void doStop() {
//        if ( dataFile == null ) {
//            return;
//        }
//
//        this.dataFile.close();
//
//        this.dataFile = null;
//    }

}