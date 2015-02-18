package com.mosaic.io.journal;

import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.QA;

import java.util.List;


/**
 * Internal class used by JournalReader2 and JournalWriter2.
 */
class Journal2 {

    public static final long FILEHEADER_SIZE    = 0;
    public static final long PER_MSGHEADER_SIZE = 0;


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




    private int selectLastFileSeq() {
        List<FileX> dataFiles = dataDirectory.files( f -> f.getFileName().startsWith(serviceName) && f.getFileName().endsWith(".data") );

        if ( dataFiles.isEmpty() ) {
            return 0;
        } else {
            dataFiles.sort( new JournalDataFile2.DataFileNameComparator(serviceName) );

            FileX tailFile = dataFiles.get( dataFiles.size() - 1 );

            return JournalDataFile2.extractFileSeq( tailFile, serviceName );
        }
    }

}
