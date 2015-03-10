package com.mosaic.io.journal;

import com.mosaic.bytes2.BytesView2;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileContents2;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;

import java.util.Comparator;


/**
 * Internal file used by JournalReader2 and JournalWriter2.
 */
class JournalDataFile2 {


    // Data file format:
    // |------------10 bytes-------------------| -------------8 bytes------------------
    // | version:ushort | startFromMsgSeq:long | (payloadLength:int,payloadHashCode:int, payload:byte[])* |

//    reader stops when payloadLength is read as zero
//    payloadLength of -1 means that the data file has come to an end, and that there will be another data file to roll over to.

//    NB at 100 million messages/sec, a long would take over 299 years to overflow
//     thus we can conclude that using a long for msgCountSoFar will be sufficient


    public static final long FILEHEADER_SIZE                   = 10;
    public static final long PER_MSGHEADER_SIZE                =  8;
    public static final long FILEFOOTER_SIZE                   =  PER_MSGHEADER_SIZE;


    /**
     * declares the version of the spec used to define the files layout
     */
    public static final long FILEHEADER_JOURNALVERSION_INDEX   =  0;

    /**
     * how many messages have preceded this point from other files
     */
    public static final long FILEHEADER_STARTSFROMMSGSEQ_INDEX =  2;

    public static final long PER_MSGHEADER_PAYLOADSIZE_INDEX   =  0;
    public static final long PER_MSGHEADER_HASHCODE_INDEX      =  4;


//    public static final long PER_MSGHEADER_PAYLOADSIZE_SIZE    =  4;
//    public static final long PER_MSGHEADER_HASH_SIZE           =  4;






    private final DirectoryX   dataDirectory;
    private final String       journalName;
    private final int          fileSeq;
    private final long         perFileSizeBytes;
    private final FileModeEnum fileMode;


    private FileX         file;
    private FileContents2 contents;
    private long          fileSize;

    private long          currentIndex;
    private long          currentToExc;
    private long          currentMessageSeq;



    public JournalDataFile2( DirectoryX dataDirectory, String journalName, int fileSeq, long perFileSizeBytes, FileModeEnum readWrite ) {
        this.dataDirectory    = dataDirectory;
        this.journalName      = journalName;
        this.fileSeq          = fileSeq;
        this.perFileSizeBytes = perFileSizeBytes;
        this.fileMode         = readWrite;
    }



    public JournalDataFile2 open() {
        QA.isNull( contents, "contents" );


        this.file           = fetchFile();
        long targetFileSize = Math.max( perFileSizeBytes, file.sizeInBytes() );
        this.contents       = file.openFile2( fileMode, targetFileSize );

        fileSize = targetFileSize;

        seekToBeginningOfFile();

        return this;
    }

    public void seekToEnd() {
        while ( isReadyToReadNextMessage() /*&& !hasReachedEOFMarker()*/ ) {   // reached the end of the journal
            int i = contents.readInt( currentIndex + PER_MSGHEADER_PAYLOADSIZE_INDEX, fileSize );
            QA.isNotZero( i, "i" );


            currentIndex += PER_MSGHEADER_SIZE + i;
            currentMessageSeq++;
        }

        currentToExc = currentIndex;
    }

    public boolean hasReachedEOFMarker() {
        long i   = currentIndex + PER_MSGHEADER_PAYLOADSIZE_INDEX;
        int  len = contents.readInt( i, fileSize );

        return len == -1;
    }

    public void complete() {
        if ( currentIndex == currentToExc ) {
            return;
        }

        int hash = calcHash( currentIndex+PER_MSGHEADER_SIZE, currentToExc );
        contents.writeInt( currentIndex+PER_MSGHEADER_HASHCODE_INDEX, currentToExc, hash );

        Backdoor.storeFence(); // very cheap as there is no contention (only 1 writer)
        // used here so that any local memory readers get to see the change
        // asap, and in a known sequential order

        currentMessageSeq++;

        currentIndex = currentToExc;
    }

    public void flush() {
        if ( contents != null ) {
            Backdoor.storeFence();

            contents.flush();
        }
    }

    public void close() {
        if ( contents != null ) {
            flush();

            contents.release();

            contents = null;
            file     = null;
        }
    }

    public JournalDataFile2 nextFile() {
        return new JournalDataFile2( dataDirectory, journalName, fileSeq+1, perFileSizeBytes, fileMode );
    }


    public void setFirstMessageSeq( long firstMessageSeq ) {
        contents.writeLong( FILEHEADER_STARTSFROMMSGSEQ_INDEX, FILEHEADER_SIZE, firstMessageSeq );

        this.currentMessageSeq = firstMessageSeq;
    }

    private void seekToBeginningOfFile() {
        this.currentIndex      = FILEHEADER_SIZE;
        this.currentToExc      = currentIndex;
        this.currentMessageSeq = contents.readLong( FILEHEADER_STARTSFROMMSGSEQ_INDEX, FILEHEADER_SIZE );
    }

    private FileX fetchFile() {
        String fileName = journalName + fileSeq + ".data";

        this.file = dataDirectory.getOrCreateFile( fileName );

        return file;
    }

    public boolean isReadyToReadNextMessage() {
        long hashCodeOffset = currentIndex + PER_MSGHEADER_HASHCODE_INDEX;
        if ( (hashCodeOffset+4) > fileSize ) {   // do not run past the end of the file
            return false;
        }

        Backdoor.loadFence();

        int hash = contents.readInt( hashCodeOffset, fileSize );

        return hash != 0;
    }

    /**
     *
     * @return true on successful allocation, and false if the end of the file has been reached
     */
    public boolean allocateAndAssignTo( BytesView2 view, int messageSizeBytes ) {
        QA.isEqualTo( currentIndex, currentToExc, "currentIndex", "currentToExc" );


        long payloadIndex = currentIndex + PER_MSGHEADER_SIZE;
        long proposedEndOfMessage = payloadIndex + messageSizeBytes;

        if ( proposedEndOfMessage > this.fileSize - FILEFOOTER_SIZE ) {  // todo add unit test that shows the need for the footer AND create a constant
            contents.writeInt( currentIndex + PER_MSGHEADER_PAYLOADSIZE_INDEX, fileSize, -1 );
            contents.writeInt( currentIndex + PER_MSGHEADER_HASHCODE_INDEX,    fileSize, -1 );

            return false;
        }

        this.currentToExc = proposedEndOfMessage;

        contents.writeInt( currentIndex + PER_MSGHEADER_PAYLOADSIZE_INDEX, currentToExc, messageSizeBytes );
        view.setBytes( contents, payloadIndex, currentToExc );

        return true;
    }

    /**
     *
     * @return returns false when the next message is not ready or the end of the file has been reached.
     *         To tell the difference call isReadyToReadNextMessage, on end of file it will return true
     *         but this method will return false.
     */
    public boolean readNextInto( JournalEntry entry ) {
        if ( !isReadyToReadNextMessage() ) {
            return false;
        }

//        QA.isEqualTo( currentIndex, currentToExc, "currentIndex", "currentToExc" );

        long payloadIndex  = currentIndex + PER_MSGHEADER_SIZE;
        int  payloadLength = contents.readInt(currentIndex + PER_MSGHEADER_PAYLOADSIZE_INDEX, fileSize);

        if ( payloadLength == -1 ) {
            return false;
        }

        entry.bytes.setBytes( contents, payloadIndex, payloadIndex+payloadLength );
        entry.msgSeq = this.currentMessageSeq;

        scrollToNext();

        return true;
    }


    public static int extractFileSeq( FileX f, String serviceName ) {
        String name = f.getFileName();
        String seqStr = name.substring( serviceName.length(), name.length()-".data".length() );

        return Integer.parseInt( seqStr );
    }


    public boolean scrollToNext() {
        if ( isReadyToReadNextMessage() ) {
            int  len       = contents.readInt( currentIndex + PER_MSGHEADER_PAYLOADSIZE_INDEX, fileSize );
            long nextIndex = currentIndex + PER_MSGHEADER_SIZE + len;

            this.currentIndex       = nextIndex;
            this.currentMessageSeq += 1;

            return true;
        } else {
            return false;
        }
    }

    private int calcHash( long fromInc, long toExc ) {
        long sum = 7;

        // incrementing in 8's means that some bytes at the end of a message may not be included
        // this is a pragmatic trade-off between robustness and speed.  Summing longs is much
        // faster than summing bytes, and is marginally faster than ints.  An extra for loop
        // to catch the stragglers doubles the cost of the checksum.  If this becomes a serious
        // concern, then we can always pad the affected messages out to be a multiple of 8.
        for ( long i=fromInc; i<toExc-8; i+=8 ) {
            sum += contents.readLong( i, toExc );

            sum += sum;  // double add here makes the checksum sensitive to the order of the bytes
        }

        return (int) sum;
    }

    public long getCurrentMessageSeq() {
        return currentMessageSeq;
    }


    static class DataFileNameComparator implements Comparator<FileX> {
        private String serviceName;

        public DataFileNameComparator( String serviceName ) {
            this.serviceName = serviceName;
        }

        public int compare( FileX f1, FileX f2 ) {
            int seq1 = extractFileSeq( f1, serviceName );
            int seq2 = extractFileSeq( f2, serviceName );

            return seq1 - seq2;
        }
    }

}
