package com.mosaic.io.journal;

import com.mosaic.lang.QA;
import com.mosaic.lang.StartStopMixin;


/**
 * Reader for journal data files created using JournalWriter.
 */
public class JournalReader2 extends StartStopMixin<JournalReader2> {

    private final Journal2         journal;
    private       JournalDataFile2 currentDataFile;


    /**
     * Opens the journal at the first message.  Usually this will be the message with seq zero,
     * however if the first file has been archived then it will be the first message of the next
     * file.  If no data files exist, then the first data file will be created empty.
     */
    public JournalReader2( Journal2 journal, String serviceName ) {
        super( serviceName );

        this.journal = journal;
    }


    public boolean readNextInto( JournalEntry journalEntry ) {
        if ( currentDataFile == null ) {
            return false;
        }

        if ( currentDataFile.isReadyToReadNextMessage() ) {
            boolean successFlag = currentDataFile.readNextInto( journalEntry );

            if ( successFlag ) {
                return true;
            } else {
                this.currentDataFile.close();

                this.currentDataFile = currentDataFile.nextFile().open();

                return readNextInto( journalEntry );
            }
        } else {
            return false;
        }
    }

    public boolean seekTo( long messageSeq ) {
        JournalDataFile2 initialDataFile = this.currentDataFile;

        this.currentDataFile = journal.seekTo(messageSeq);

        if ( currentDataFile.getCurrentMessageSeq() == messageSeq ) {
            initialDataFile.close();

            return true;
        } else {
            this.currentDataFile = initialDataFile;

            return false;
        }
    }



    protected void doStart() {
        QA.isNull( currentDataFile, "currentDataFile" );

        this.currentDataFile = journal.selectFirstFileRO().open();
    }

    protected void doStop() {
        if ( currentDataFile == null ) {
            return;
        }

        this.currentDataFile.close();

        this.currentDataFile = null;
    }

}