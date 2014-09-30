package com.mosaic.collections.queue.journal;

import com.mosaic.bytes.ByteView;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileContents;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.QA;

import java.util.Comparator;
import java.util.List;

import static com.mosaic.io.filesystemx.FileModeEnum.READ_WRITE;


/**
 *
 */
class JournalDataFile {


//    journalVersion:ushort              // declares the version of the spec used to define the files layout
//    msgCountSoFar:long                 // how many messages have preceded this point
//        // NB at 100 million messages/sec, a long would take over 299 years to overflow
//        // thus we can conclude that using a long for msgCountSoFar will be sufficient
//
//    (payloadLength:int,payloadHashCode:int, payload:byte[])*  // the layout of each event, repeats zero or more times up to the end of the file
//    // reader stops when payloadLength is read as zero
//       payloadLength of -1 means that the data file has come to an end, and that there will be another data file to roll over to.

    public static final long FILEHEADER_JOURNALVERSION_INDEX   =  0;
    public static final long FILEHEADER_STARTSFROMMSGSEQ_INDEX =  2;
    public static final long FILEHEADER_SIZE                   = 10;



    public static final long PERMSGHEADER_PAYLOADSIZE_INDEX    =  0;
    public static final long PERMSGHEADER_HASHCODE_INDEX       =  4;
    public static final long PERMSGHEADER_SIZE                 =  8;

    public static final long PERMSGHEADER_PAYLOADSIZE_SIZE     = 4;
    public static final long PERMSGHEADER_HASH_SIZE            = 4;



    private static class DataFileNameComparator implements Comparator<FileX> {
        private String serviceName;

        public DataFileNameComparator( String serviceName ) {
            this.serviceName = serviceName;
        }

        public int compare( FileX f1, FileX f2 ) {
            int seq1 = extractFileSeq( f1, serviceName );
            int seq2 = extractFileSeq( f2, serviceName );

            return seq1 - seq2;
        }

        public static int extractFileSeq( FileX f, String serviceName ) {
            String name = f.getFileName();
            String seqStr = name.substring( serviceName.length(), name.length()-".data".length() );

            return Integer.parseInt( seqStr );
        }
    }

    public static JournalDataFile selectLastFile( DirectoryX dataDirectory, String serviceName, long perFileSizeBytes, FileModeEnum fileMode ) {
        List<FileX> dataFiles = dataDirectory.files( f -> f.getFileName().startsWith(serviceName) && f.getFileName().endsWith(".data") );

        int fileSeq;
        if ( dataFiles.isEmpty() ) {
            fileSeq = 0;
        } else {
            fileSeq = selectLatestFileSeqFrom( serviceName, dataFiles );
        }

        return new JournalDataFile( dataDirectory, serviceName, fileSeq, perFileSizeBytes, fileMode );
    }

    private static int selectLatestFileSeqFrom( String serviceName, List<FileX> dataFiles ) {
        dataFiles.sort( new DataFileNameComparator( serviceName ) );

        FileX tailFile = dataFiles.get( dataFiles.size() - 1 );

        return DataFileNameComparator.extractFileSeq( tailFile, serviceName );
    }


    private FileX              file;
    private long               fileSize;

    private FileContents       contents;
    private long               currentIndex;
    private long               currentToExc;
    private long               currentMessageSeq;

    private final long         perFileSizeBytes;
    private final FileModeEnum fileMode;

    private final DirectoryX   dataDirectory;
    private final String       journalName;
    private final long         fileSeq;


    public JournalDataFile( DirectoryX dataDirectory, String journalName, long fileSeq ) {
        this( dataDirectory, journalName, fileSeq, 0, FileModeEnum.READ_ONLY );
    }

    public JournalDataFile( DirectoryX dataDirectory, String journalName, long fileSeq, long perFileSizeBytes, FileModeEnum fileMode ) {
        this.dataDirectory    = dataDirectory;
        this.journalName      = journalName;
        this.fileSeq          = fileSeq;
        this.fileMode         = fileMode;

        this.perFileSizeBytes = perFileSizeBytes;
    }



// WRITER FUNCTIONS

    public void sync() {

    }

    public JournalDataFile selectDataFileToWriteNextMessage( int messageSizeBytes ) {
        if ( hasRoomFor(messageSizeBytes) ) {
            return this;
        }


        long nextFileSeq = fileSeq + 1;

        markNextAsEOF();
        close();

        return new JournalDataFile(dataDirectory, journalName, nextFileSeq, perFileSizeBytes, READ_WRITE).open();
    }

    public long reserveUsing( ByteView message, int messageSizeBytes ) {
        QA.isEqualTo( currentIndex, currentToExc, "currentIndex", "currentToExc" );

        long payloadIndex = currentIndex  + PERMSGHEADER_SIZE;
        this.currentToExc = payloadIndex + messageSizeBytes;

        contents.writeInt( currentIndex + PERMSGHEADER_PAYLOADSIZE_INDEX, currentToExc, messageSizeBytes );
        message.setBytes( contents, payloadIndex, currentToExc );

        return currentMessageSeq;
    }

    public void complete( long messageSeq ) {
        QA.isEqualTo( messageSeq, currentMessageSeq, "messageSeq", "this.currentMessageSeq" );

        int hash = calcHash( currentIndex+PERMSGHEADER_SIZE, currentToExc );
        contents.writeInt( currentIndex+PERMSGHEADER_HASHCODE_INDEX, currentToExc, hash );

        currentMessageSeq++;

        currentIndex = currentToExc;
    }

    public void seekToEnd() {
        while ( isReadyToReadNextMessage() && !hasReachedEOFMarker() ) {   // reached the end of the journal
            int i = contents.readInt( currentIndex + PERMSGHEADER_PAYLOADSIZE_INDEX, fileSize );
            QA.isNotZero( i, "i" );


            currentIndex += PERMSGHEADER_SIZE + i;
            currentMessageSeq++;
        }

        currentToExc = currentIndex;
    }

    private boolean hasRoomFor( int messageSizeBytes ) {
        return currentIndex + PERMSGHEADER_SIZE + messageSizeBytes + PERMSGHEADER_PAYLOADSIZE_SIZE <= fileSize;
    }

    private void markNextAsEOF() {
        contents.writeInt( currentIndex + PERMSGHEADER_PAYLOADSIZE_INDEX, fileSize, -1 );
    }

// READER FUNCTIONS


    public boolean seekTo( long targetSeq ) {
        seekToBeginningOfFile();

        long currentSeq = 0;

        while ( currentSeq != targetSeq ) {
            if ( !isReadyToReadNextMessage() ) {   // reached the end of the journal
                return false;
            }

            scrollToNext();

            currentSeq++;
        }

        return true;
    }

    public boolean readNextInto( ByteView view ) {
        if ( !isReadyToReadNextMessage() ) {
            return false;
        }

        long payloadStart = currentIndex+PERMSGHEADER_SIZE;

        scrollToNext();  // currentIndex now points at start of next message

        long payloadEnd = currentIndex;

        view.setBytes( contents, payloadStart, payloadEnd );

        return true;
    }

    private void scrollToNext() {
        QA.isTrue( isReadyToReadNextMessage(), "scrollToNext() called when isReady returned false" );

        int  len       = contents.readInt( currentIndex+PERMSGHEADER_PAYLOADSIZE_INDEX, fileSize );
        long nextIndex = currentIndex+PERMSGHEADER_SIZE+len;

        this.currentIndex = nextIndex;
    }


// SHARED

    public JournalDataFile open() {
        QA.isNull( contents, "contents" );


        this.file           = fetchFile();
        long targetFileSize = Math.max( perFileSizeBytes, file.sizeInBytes() );
        this.contents       = file.openFile( fileMode, targetFileSize );

        fileSize = targetFileSize;

        seekToBeginningOfFile();

        return this;
    }

    public void close() {
        contents.release();

        contents = null;
    }

    public boolean hasReachedEOFMarker() {
        long i = currentIndex + PERMSGHEADER_PAYLOADSIZE_INDEX;
        int len = contents.readInt( i, fileSize );

        return len == -1;
    }

    public long getFileSeq() {
        return fileSeq;
    }

    private FileX fetchFile() {
        String fileName = journalName + fileSeq + ".data";
        FileX  file     = dataDirectory.getFile( fileName );

        if ( file == null ) {
            if ( fileMode.isWritable() ) {
                file = dataDirectory.getOrCreateFile( fileName );
            } else {
                throw new JournalNotFoundException( dataDirectory.getFullPath()+"/"+fileName );
            }
        }

        return file;
    }

    private int calcHash( long l, long currentToExc ) {
        return 42;
    }

    private boolean isReadyToReadNextMessage() {
        long hashCodeOffset = currentIndex + PERMSGHEADER_HASHCODE_INDEX;
        if ( (hashCodeOffset+4) >= fileSize ) {   // do not run past the end of the file
            return false;
        }
        int hash = contents.readInt( hashCodeOffset, fileSize );

        return hash != 0;
    }

    private void seekToBeginningOfFile() {
        this.currentIndex      = FILEHEADER_SIZE;
        this.currentMessageSeq = contents.readLong( FILEHEADER_STARTSFROMMSGSEQ_INDEX, FILEHEADER_SIZE );
        this.currentToExc      = currentIndex;
    }
}