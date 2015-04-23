package com.mosaic.io.journal;

import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.QA;
import com.mosaic.lang.Service;
import com.mosaic.lang.ServiceThread;
import com.mosaic.lang.functional.Function0;
import com.mosaic.lang.functional.FunctionObj2Int;
import com.mosaic.lang.functional.VoidFunction0;
import com.mosaic.lang.system.SystemX;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.mosaic.lang.ServiceThread.ThreadType.*;


/**
 * A high speed, low GC, single writer, multiple reader, file backed journal.  A journal can be
 * used to capture events and to replay them, either for recovery of a JVM on restart or to
 * distribute events between JVMs.
 */
public class Journal2 {

    static final long FILEHEADER_SIZE             = JournalDataFile2.FILEHEADER_SIZE;
    static final long FILEFOOTER_SIZE             = JournalDataFile2.FILEFOOTER_SIZE;
    static final long PER_MSGHEADER_SIZE          = JournalDataFile2.PER_MSGHEADER_SIZE;
    static final long DEFAULT_PER_FILE_SIZE_BYTES = 100 * SystemX.MEGABYTE;


    final DirectoryX dataDirectory;
    final String     serviceName;
    final long       perFileSizeBytes;

    private final Function0<String> writerServiceNameFactory;
    private final Function0<String> readerServiceNameFactory;
    private final Function0<String> asyncReaderServiceNameFactory;


    public Journal2( DirectoryX dataDirectory, String serviceName ) {
        this( dataDirectory, serviceName, DEFAULT_PER_FILE_SIZE_BYTES );
    }

    public Journal2( DirectoryX dataDirectory, String serviceName, long perFileSizeBytes ) {
        QA.argNotNull(  dataDirectory,    "dataDirectory"    );
        QA.argIsGTZero( perFileSizeBytes, "perFileSizeBytes" );

        this.dataDirectory    = dataDirectory;
        this.serviceName      = serviceName;
        this.perFileSizeBytes = perFileSizeBytes;

        this.writerServiceNameFactory      = NameFactoryUtils.createSequenceNameFactory(serviceName+"-writer");
        this.readerServiceNameFactory      = NameFactoryUtils.createSequenceNameFactory(serviceName+"-reader");
        this.asyncReaderServiceNameFactory = NameFactoryUtils.createSequenceNameFactory(serviceName+"-asyncreader");
    }


    public JournalWriter2 createWriter() {
        String writerServiceName = writerServiceNameFactory.invoke();

        return new JournalWriter2( this, writerServiceName );
    }

    public JournalReader2 createReader() {
        String readerServiceName = readerServiceNameFactory.invoke();

        return new JournalReader2( this, readerServiceName );
    }

    public Service createReaderAsync( JournalReaderCallback callback ) {
        return createReaderAsync( callback, () -> {}, 0 );
    }

    public Service createReaderAsync( JournalReaderCallback callback, VoidFunction0 idleCallback ) {
        return createReaderAsync( callback, idleCallback, 0 );
    }

    public Service createReaderAsync( JournalReaderCallback callback, VoidFunction0 idleCallback, long fromSeq ) {
        String         readerName = asyncReaderServiceNameFactory.invoke();
        JournalReader2 reader     = createReader();


        ServiceThread thread = new ServiceThread(readerName, NON_DAEMON) {
            private long sequentialIdleCount = 0;

            protected long loop() throws InterruptedException {
                boolean successFlag = reader.readNextUsingCallback( callback );

                if ( successFlag ) {
                    sequentialIdleCount = 0;

                    return 0;
                } else {
                    sequentialIdleCount++;

                    if ( sequentialIdleCount == 10 ) {
                        idleCallback.invoke();
                    }

                    return 10;
                }
            }
        };

        reader.onStartAfter( () -> {
            if ( fromSeq > 0 ) {
                reader.seekTo( fromSeq );
            }
        } );

        thread.registerServicesBefore( reader );

        return thread;
    }





    JournalDataFile2 selectLastFileRW() {
        int fileSeq = selectLastFileSeq();

        return newDataFile( fileSeq, FileModeEnum.READ_WRITE );
    }

    JournalDataFile2 selectFirstFileRO() {
        int fileSeq = selectFirstFileSeq();

        return newDataFile( fileSeq, FileModeEnum.READ_ONLY );
    }

    /**
     * Seeks to the closest message that it can find that is <= targetMessageSeq
     */
    JournalDataFile2 seekTo( long targetMessageSeq ) {
        int fileSeq = findJournalFileThatContainsMessageSeq( targetMessageSeq );

        JournalDataFile2 dataFile = newDataFile( fileSeq, FileModeEnum.READ_ONLY ).open();

        while ( dataFile.getCurrentMessageSeq() != targetMessageSeq ) {
            boolean successFlag = dataFile.scrollToNext();

            if ( !successFlag ) {
                return dataFile;
            }
        }

        return dataFile;
    }


    private JournalDataFile2 newDataFile( int fileSeq, FileModeEnum fileMode ) {
        return new JournalDataFile2( dataDirectory, serviceName, fileSeq, perFileSizeBytes, fileMode );
    }

    private int selectLastFileSeq() {
        return selectFileSeq( files -> files.size() - 1 );
    }

    private int selectFirstFileSeq() {
        return selectFileSeq( files -> 0 );
    }

    /**
     *
     * @param selectFileIndexF given a list of FileX's, return the index of the lists element that contains the file
     *                         that we want to open
     * @return the file seq number (as postfixed within the files name -- seq is not being used as a synonym for index)
     */
    private int selectFileSeq( FunctionObj2Int<List<FileX>> selectFileIndexF ) {
        List<FileX> dataFiles = fetchSortedFiles();

        if ( dataFiles.isEmpty() ) {
            return 0;
        } else {
            int   i            = selectFileIndexF.invoke( dataFiles );
            FileX selectedFile = dataFiles.get( i );

            return JournalDataFile2.extractFileSeq( selectedFile, serviceName );
        }
    }

    private List<FileX> fetchSortedFiles() {
        List<FileX> dataFiles = dataDirectory.files( f -> f.getFileName().startsWith(serviceName) && f.getFileName().endsWith(".data") );

        dataFiles.sort( new JournalDataFile2.DataFileNameComparator(serviceName) );

        return dataFiles;
    }

    private int findJournalFileThatContainsMessageSeq( long targetMessageSeq ) {
        return selectFileSeq( files -> {
            // scan through the data files in order, looking for targetMessageSeq.
            JournalDataFile2 previousDataFile        = null;
            int              indexOfPreviousDataFile = -1;
            for ( int i = 0; i < files.size(); i++ ) {
                FileX f = files.get( i );
                int fs = JournalDataFile2.extractFileSeq( f, serviceName );
                JournalDataFile2 dataFile = new JournalDataFile2( dataDirectory, serviceName, fs, perFileSizeBytes, FileModeEnum.READ_WRITE ).open();

                try {
                    if ( dataFile.getCurrentMessageSeq() > targetMessageSeq ) {
                        if ( previousDataFile == null ) {
                            throw new JournalNotFoundException2(
                                String.format( "Unable to find msg seq '%s' under '%s'; has the data file been removed?", targetMessageSeq, dataDirectory.getFullPath() + "/" + serviceName )
                            );
                        }

                        return i-1;
                    } else if ( dataFile.getCurrentMessageSeq() == targetMessageSeq ) {
                        return i;
                    }
                } finally {
                    dataFile.close();
                }

                previousDataFile        = dataFile;
                indexOfPreviousDataFile = i;
            }

            if ( previousDataFile == null ) {
                throw new JournalNotFoundException2(
                    String.format( "Unable to find journal data file containing msg seq '%s' under '%s'", targetMessageSeq, dataDirectory.getFullPath() + "/" + serviceName )
                );
            }

            return indexOfPreviousDataFile;
        } );
    }

}
