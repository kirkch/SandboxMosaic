package com.mosaic.collections.queue.journal;

import com.mosaic.collections.queue.ByteQueueCallback;
import com.mosaic.bytes.ByteView;
import com.mosaic.bytes.Bytes;
import com.mosaic.bytes.BytesWrapper;
import com.mosaic.collections.queue.ByteQueueWriter;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.lang.QA;
import com.mosaic.lang.ServiceMixin;
import com.mosaic.lang.functional.VoidFunction0;
import com.mosaic.lang.functional.VoidFunction1;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.UTF8;


/**
 *
 */
public class JournalWriter extends ServiceMixin<JournalWriter> implements ByteQueueWriter {

    public static long sizeOfUTF8String( UTF8 source ) {
        return 2 + source.getByteCount();
    }


    private final BytesWrapper myFlyweight;

    private DirectoryX dataDirectory;
    private long       perFileSizeBytes;

    private JournalDataFile currentDataFile;


    public JournalWriter( DirectoryX dataDirectory, String serviceName ) {
        this( dataDirectory, serviceName, 100*SystemX.MEGABYTE );
    }

    public JournalWriter( DirectoryX dataDirectory, String serviceName, long perFileSizeBytes ) {
        super( serviceName );

        QA.argNotNull(  dataDirectory,    "dataDirectory" );
        QA.argIsGTZero( perFileSizeBytes, "perFileSizeBytes" );


        this.dataDirectory    = dataDirectory;
        this.perFileSizeBytes = perFileSizeBytes;

        this.myFlyweight      = new BytesWrapper();
    }


    public <T extends ByteView> long reserveUsing( T message, int messageSizeBytes ) {
        this.currentDataFile = this.currentDataFile.selectDataFileToWriteNextMessage( messageSizeBytes );

        return currentDataFile.reserveUsing( message, messageSizeBytes );
    }

    public void complete( long messageSeq ) {
        currentDataFile.complete( messageSeq );
    }

    public void sync() {
        currentDataFile.sync();
    }

    /**
     * Reserve n bytes and then provide a callback that will write the data within the reserved
     * region and then mark the write as complete.<p/>
     *
     * This variant of the writeMessage methods is the most efficient;  it will return the underlying
     * bytes object unwrapped, along with the indexes specifying where the reserved region sits.  This
     * requires the caller to calculate the writing index, however it saves on pointer chasing.  Which
     * is a net performance win, in return for a little more developer effort.<p/>
     *
     * This method is approximately 10ms faster than its brothers per millions invocations.  That is
     * approximately 30% faster.
     */
    public void writeMessage( int messageSizeBytes, ByteQueueCallback writerFunction ) {
        this.currentDataFile = this.currentDataFile.selectDataFileToWriteNextMessage( messageSizeBytes );

        currentDataFile.writeMessage( messageSizeBytes, writerFunction );
    }

    /**
     *
     * Reserve n bytes and then provide a callback that will write the data within the reserved
     * region and then mark the write as complete.<p/>
     *
     * This variant will wrap the underlying bytes so that all writes start from zero.  This
     * convenience wrapping slows the journal by approximately 10ms per millions messages.  As
     * measured on a mbp.
     */
    public void writeMessage( int messageSizeBytes, VoidFunction1<Bytes> writerFunction ) {
        long seq = reserveUsing( myFlyweight, messageSizeBytes );

        writerFunction.invoke( myFlyweight );

        complete( seq );
    }

    public <T extends ByteView> void writeMessageUsing( T msg, VoidFunction0 writerFunction ) {
        long seq = reserveUsing( msg, Backdoor.toInt(msg.sizeBytes()) );

        writerFunction.invoke();

        complete( seq );
    }


    protected void doStart() throws Exception {
        this.currentDataFile = JournalDataFile.selectLastFileRW( dataDirectory, getServiceName(), perFileSizeBytes ).open();

        this.currentDataFile.seekToEnd();
    }

    protected void doStop() throws Exception {
        this.currentDataFile.close();

        this.currentDataFile = null;
    }

}
