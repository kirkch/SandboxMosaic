package com.mosaic.io.journal;

import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.lang.QA;
import com.mosaic.lang.StartStopMixin;
import com.mosaic.lang.system.SystemX;


/**
 *
 */
public class JournalWriter2 extends StartStopMixin<JournalWriter2> {


    private final Journal2 journal;

    private JournalDataFile2 currentDataFile;


    public JournalWriter2( DirectoryX dataDirectory, String serviceName ) {
        this( dataDirectory, serviceName, Journal2.DEFAULT_PER_FILE_SIZE_BYTES );
    }

    public JournalWriter2( DirectoryX dataDirectory, String serviceName, long perFileSizeBytes ) {
        super( serviceName );

        this.journal = new Journal2( dataDirectory, serviceName, perFileSizeBytes );

//        this.appendDependency( journal );
    }


    public void allocateTo( JournalEntry view, int numBytes ) {
        currentDataFile.allocateAndAssignTo( view.bytes, numBytes );
    }

    public void completeMessage() {
        currentDataFile.complete();
    }

    public void flush() {
        currentDataFile.flush();
    }

//    public <T extends ByteView> long reserveUsing( T message, int messageSizeBytes ) {
//        this.currentDataFile = this.currentDataFile.selectDataFileToWriteNextMessage( messageSizeBytes );
//
//        return currentDataFile.reserveUsing( message, messageSizeBytes );
//    }
//
//    public void complete( long messageSeq ) {
//        currentDataFile.complete( messageSeq );
//    }
//
//    public void sync() {
//        currentDataFile.sync();
//    }
//
//    /**
//     * Reserve n bytes and then provide a callback that will write the data within the reserved
//     * region and then mark the write as complete.<p/>
//     *
//     * This variant of the writeMessage methods is the most efficient;  it will return the underlying
//     * bytes object unwrapped, along with the indexes specifying where the reserved region sits.  This
//     * requires the caller to calculate the writing index, however it saves on pointer chasing.  Which
//     * is a net performance win, in return for a little more developer effort.<p/>
//     *
//     * This method is approximately 10ms faster than its brothers per millions invocations.  That is
//     * approximately 30% faster.
//     */
//    public void writeMessage( int messageSizeBytes, ByteQueueCallback writerFunction ) {
//        this.currentDataFile = this.currentDataFile.selectDataFileToWriteNextMessage( messageSizeBytes );
//
//        currentDataFile.writeMessage( messageSizeBytes, writerFunction );
//    }
//
//    /**
//     *
//     * Reserve n bytes and then provide a callback that will write the data within the reserved
//     * region and then mark the write as complete.<p/>
//     *
//     * This variant will wrap the underlying bytes so that all writes start from zero.  This
//     * convenience wrapping slows the journal by approximately 10ms per millions messages.  As
//     * measured on a mbp.
//     */
//    public void writeMessage( int messageSizeBytes, VoidFunction1<Bytes> writerFunction ) {
//        long seq = reserveUsing( myFlyweight, messageSizeBytes );
//
//        writerFunction.invoke( myFlyweight );
//
//        complete( seq );
//    }

//    public <T extends JournalEntry> void writeMessageUsing( T msg, VoidFunction0 writerFunction ) {
//        long seq = reserveUsing( msg, Backdoor.toInt(msg.sizeBytes()) );
//
//        writerFunction.invoke();
//
//        complete( seq );
//    }


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
