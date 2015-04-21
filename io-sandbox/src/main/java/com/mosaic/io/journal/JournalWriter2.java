package com.mosaic.io.journal;

import com.mosaic.lang.QA;
import com.mosaic.lang.ServiceMixin;


/**
 *
 */
public class JournalWriter2 extends ServiceMixin<JournalWriter2> {

    private final Journal2         journal;
    private       JournalDataFile2 currentDataFile;


    JournalWriter2( Journal2 journal, String serviceName ) {
        super( serviceName );

        this.journal = journal;
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
