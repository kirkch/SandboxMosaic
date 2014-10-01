package com.mosaic.collections.queue.journal;

import com.mosaic.bytes.ByteView;
import com.mosaic.collections.queue.ByteQueueWriter;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.lang.QA;
import com.mosaic.lang.StartStopMixin;
import com.mosaic.lang.functional.VoidFunction1;
import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.lang.system.SystemX;

import static com.mosaic.io.filesystemx.FileModeEnum.READ_WRITE;


/**
 *
 */
public class JournalWriter<T extends ByteView> extends StartStopMixin<JournalWriter<T>> implements ByteQueueWriter<T> {

    private final T    myFlyweight;

    private DirectoryX dataDirectory;
    private long       perFileSizeBytes;

    private JournalDataFile currentDataFile;


    public JournalWriter( DirectoryX dataDirectory, String serviceName, Class<T> t ) {
        this( dataDirectory, serviceName, t, 100*SystemX.MEGABYTE );
    }

    public JournalWriter( DirectoryX dataDirectory, String serviceName, Class<T> t, long perFileSizeBytes ) {
        super( serviceName );

        QA.argNotNull(  dataDirectory,    "dataDirectory" );
        QA.argIsGTZero( perFileSizeBytes, "perFileSizeBytes" );


        this.dataDirectory    = dataDirectory;
        this.perFileSizeBytes = perFileSizeBytes;

        this.myFlyweight      = ReflectionUtils.newInstance( t );
    }


    public long reserveUsing( T message, int messageSizeBytes ) {
        this.currentDataFile = this.currentDataFile.selectDataFileToWriteNextMessage( messageSizeBytes );

        return currentDataFile.reserveUsing( message, messageSizeBytes );
    }

    public void complete( long messageSeq ) {
        currentDataFile.complete( messageSeq );
    }

    public void sync() {
        currentDataFile.sync();
    }

    public void writeMessage( int messageSizeBytes, VoidFunction1<T> writerFunction ) {
        long seq = reserveUsing( myFlyweight, messageSizeBytes );

        writerFunction.invoke( myFlyweight );

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
