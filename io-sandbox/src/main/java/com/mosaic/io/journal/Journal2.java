package com.mosaic.io.journal;

import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.FunctionObj2Int;
import com.mosaic.lang.system.SystemX;

import java.util.List;


/**
 * Internal class used by JournalReader2 and JournalWriter2.
 */
class Journal2 {

    public static final long FILEHEADER_SIZE    = 0;
    public static final long PER_MSGHEADER_SIZE = 0;
    public static final long DEFAULT_PER_FILE_SIZE_BYTES = 100 * SystemX.MEGABYTE;


    private final DirectoryX dataDirectory;
    private final String     serviceName;
    private final long       perFileSizeBytes;


    public Journal2( DirectoryX dataDirectory, String serviceName, long perFileSizeBytes ) {
        QA.argNotNull(  dataDirectory,    "dataDirectory"    );
        QA.argIsGTZero( perFileSizeBytes, "perFileSizeBytes" );

        this.dataDirectory    = dataDirectory;
        this.serviceName      = serviceName;
        this.perFileSizeBytes = perFileSizeBytes;
    }


    public JournalDataFile2 selectLastFileRW() {
        int fileSeq = selectLastFileSeq();

        return new JournalDataFile2( dataDirectory, serviceName, fileSeq, perFileSizeBytes, FileModeEnum.READ_WRITE );
    }

    public JournalDataFile2 selectFirstFileRO() {
        int fileSeq = selectFirstFileSeq();

        return new JournalDataFile2( dataDirectory, serviceName, fileSeq, perFileSizeBytes, FileModeEnum.READ_WRITE );
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
            int   i        = selectFileIndexF.invoke( dataFiles );
            FileX tailFile = dataFiles.get( i );

            return JournalDataFile2.extractFileSeq( tailFile, serviceName );
        }
    }

    private List<FileX> fetchSortedFiles() {
        List<FileX> dataFiles = dataDirectory.files( f -> f.getFileName().startsWith(serviceName) && f.getFileName().endsWith(".data") );

        dataFiles.sort( new JournalDataFile2.DataFileNameComparator(serviceName) );

        return dataFiles;
    }

}
