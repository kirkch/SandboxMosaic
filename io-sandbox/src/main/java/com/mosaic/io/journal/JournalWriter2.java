package com.mosaic.io.journal;

import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.lang.QA;
import com.mosaic.lang.StartStopMixin;
import com.mosaic.lang.text.UTF8;


/**
 *
 */
public class JournalWriter2 extends StartStopMixin<JournalWriter2> {

    private final Journal2         journal;
    private       JournalDataFile2 currentDataFile;


    public JournalWriter2( DirectoryX dataDirectory, String serviceName ) {
        this( dataDirectory, serviceName, Journal2.DEFAULT_PER_FILE_SIZE_BYTES );
    }

    public JournalWriter2( DirectoryX dataDirectory, String serviceName, long perFileSizeBytes ) {
        super( serviceName );

        this.journal = new Journal2( dataDirectory, serviceName, perFileSizeBytes );
    }


    public void allocateTo( JournalEntry view, int numBytes ) {
        boolean successFlag = currentDataFile.allocateAndAssignTo( view.bytes, numBytes );

        if ( !successFlag ) {  // roll on to a new file
            currentDataFile.close();

            long nextMessageSeq = currentDataFile.getCurrentMessageSeq();

            this.currentDataFile = currentDataFile.nextFile().open();
            this.currentDataFile.setFirstMessageSeq(nextMessageSeq);

            allocateTo( view, numBytes ); // try again after having rolled on to the next data file
        }
    }

    public void completeMessage() {
        currentDataFile.complete();
    }

    public void flush() {
        currentDataFile.flush();
    }



    protected void doStart() throws Exception {
        QA.isNull( currentDataFile, "currentDataFile" );

        this.currentDataFile = journal.selectLastFileRW().open();

        this.currentDataFile.seekToEnd();
    }

    protected void doStop() throws Exception {
        this.currentDataFile.close();

        this.currentDataFile = null;
    }

}
