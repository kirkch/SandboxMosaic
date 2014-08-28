package com.mosaic.io.cli;

import com.mosaic.collections.concurrent.Future;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.Failure;
import com.mosaic.lang.IllegalStateExceptionX;
import com.mosaic.lang.system.LiveSystem;
import com.mosaic.lang.system.SystemX;
import com.softwaremosaic.junit.JUnitMosaic;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 *
 */
public class CLApp_fileLockChildProcessTests {

    private SystemX system  = LiveSystem.withNoLogging();
    private File    dataDir;

    @Before
    public void setup() throws IOException {
        this.dataDir = File.createTempFile( "CLApp_fileLockChildProcessTests", ".dataDir" );

        this.dataDir.delete();
        this.dataDir.mkdir();

        system.start();
    }

    @After
    public void tearDown() {
        DirectoryX dir = system.getDirectory( dataDir.getAbsolutePath() );
        dir.deleteAll();

        system.stop();
    }


    @Test
    public void startTwoAppsInTheirOwnProcesses_expectSecondOneToNotStartDueToLock() throws IOException {
        Vector<String> processOutput1 = new Vector<>();
        Vector<String> processOutput2 = new Vector<>();

        Future<Integer> process1 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput1::add, dataDir.getAbsolutePath() );
        JUnitMosaic.spinUntilTrue( () -> processOutput1.contains("App has started") );

        Future<Integer> process2 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput2::add, dataDir.getAbsolutePath() );
        JUnitMosaic.spinUntilTrue( () -> processOutput2.contains("Application is already running, only one instance is allowed at a time.") );


        process2.spinUntilComplete( 3000 );

        assertTrue( process2.isComplete() );


        signalChildProcessesToStop( dataDir );
        spinUntilProcessesHaveStopped( process1, process2 );


        assertEquals( 0, process1.getResultNoBlock().intValue() );
        assertEquals( 1, process2.getResultNoBlock().intValue() );
    }

    @Test
    public void startTwoAppsOneAfterTheOtherHasFinishedInTheirOwnProcesses_expectBothToStart() throws IOException {
        Vector<String> processOutput1 = new Vector<>();
        Vector<String> processOutput2 = new Vector<>();

        Future<Integer> process1 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput1::add, dataDir.getAbsolutePath() );
        JUnitMosaic.spinUntilTrue( () -> processOutput1.contains("App has started") );

        signalChildProcessesToStop( dataDir );
        process1.spinUntilComplete( 3000 );

        removeSignalForChildProcessesToStop( dataDir );


        Future<Integer> process2 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput2::add, dataDir.getAbsolutePath() );
        JUnitMosaic.spinUntilTrue( () -> processOutput2.contains( "App has started" ) );


        signalChildProcessesToStop( dataDir );
        spinUntilProcessesHaveStopped( process1, process2 );


        assertEquals( 0, process1.getResultNoBlock().intValue() );
        assertEquals( 0, process2.getResultNoBlock().intValue() );
    }

    @Test
    public void startTwoAppsInTheirOwnProcesses_killTheFirstProcessBeforeStartingTheSecond_expectTheSecondToRecoverTheLockFileAndToStart() throws IOException {
        Vector<String> processOutput1 = new Vector<>();
        Vector<String> processOutput2 = new Vector<>();

        Future<Integer> process1 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput1::add, dataDir.getAbsolutePath() );
        JUnitMosaic.spinUntilTrue( () -> processOutput1.contains("App has started") );

        process1.completeWithFailure( new Failure( "abort the process" ) );
        process1.spinUntilComplete(3000);


        Future<Integer> process2 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput2::add, dataDir.getAbsolutePath() );
        JUnitMosaic.spinUntilTrue( () -> processOutput2.contains( "Previous run did not shutdown cleanly, recovering" ) );
        JUnitMosaic.spinUntilTrue( () -> processOutput2.contains( "App has started" ) );


        signalChildProcessesToStop( dataDir );
        spinUntilProcessesHaveStopped( process1, process2 );


        assertEquals( 0, process2.getResultNoBlock().intValue() );
    }

    @Test
    public void whenTryingToRecoverAnAbortedLock_throwAnException_expectRecoveryToFailAndTheDirtyLockFileToRemain() throws IOException {
        Vector<String> processOutput1 = new Vector<>();
        Vector<String> processOutput2 = new Vector<>();

        Future<Integer> process1 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput1::add, dataDir.getAbsolutePath() );
        JUnitMosaic.spinUntilTrue( () -> processOutput1.contains("App has started") );

        process1.completeWithFailure( new Failure("abort the process") );
        process1.spinUntilComplete(3000);


        Future<Integer> process2 = system.runJavaProcess( FailRecoveryApp.class, processOutput2::add, dataDir.getAbsolutePath() );
        JUnitMosaic.spinUntilTrue( () -> processOutput2.contains( "FailRecoveryApp errored unexpectedly and was aborted. The error was 'A previous run of the app did not clean up after itself, manual recovery required.'." ) );


        signalChildProcessesToStop( dataDir );
        spinUntilProcessesHaveStopped( process1, process2 );


        assertEquals( 1, process2.getResultNoBlock().intValue() );
    }




    //    @Test
    public void f() throws IOException, InterruptedException {
        File f = new File("/Users/ck/f.txt");


        RandomAccessFile raf = new RandomAccessFile( f, "rw" );
        FileLock lock = raf.getChannel().tryLock();

        if ( lock == null ) {
            System.out.println("unable to acquire lock");
            return;
        }

        System.out.println( "is locked = " + lock.isValid() );

        raf.write( "Hello CK\n".getBytes("UTF-8") );


        Thread.sleep( 10000 );
        lock.release();
        raf.close();
    }



    private void spinUntilProcessesHaveStopped( Future<Integer> process1, Future<Integer> process2 ) {
        process1.spinUntilComplete( 3000 );
        process2.spinUntilComplete( 3000 );
    }

    private void signalChildProcessesToStop( File dataDir ) throws IOException {
        RandomAccessFile doneFile = new RandomAccessFile( new File(dataDir,"DONE"), "rw" );
        doneFile.close();
    }

    private void removeSignalForChildProcessesToStop( File dataDir ) {
        File doneFile = new File(dataDir,"DONE");

        doneFile.delete();
    }

    public static class WaitForSignalFileApp extends CLApp {
        public static void main( String[] args ) {
            CLApp app  = new WaitForSignalFileApp();

            System.exit( app.runApp( args ) );
        }

        private final CLArgument<DirectoryX> dataDir = getOrCreateDirectoryArgument( "dir", "data directory" );

        public WaitForSignalFileApp() {
            super("WaitForSignalTestApp");

            useLockFile( dataDir::getValue );
        }


        protected int _run() throws Exception {
            try {
                DirectoryX dir = dataDir.getValue();

                system.opsAudit( "App has started" );
                system.opsAudit( "dataDirectory=" + dir.getFullPath() );

                pollForContinuationFile( dir );
            } catch ( Throwable ex ) {
                ex.printStackTrace();
            }


            return 0;
        }

        private void pollForContinuationFile( DirectoryX dir ) {
            FileX continuationFile = dir.getFile( "DONE" );

            while ( continuationFile == null ) {
                continuationFile = dir.getFile( "DONE" );
            }
        }
    }

    public static class FailRecoveryApp extends CLApp {
        public static void main( String[] args ) {
            CLApp app  = new FailRecoveryApp();

            System.exit( app.runApp( args ) );
        }

        private final CLArgument<DirectoryX> dataDir = getOrCreateDirectoryArgument( "dir", "data directory" );

        public FailRecoveryApp() {
            super("FailRecoveryApp");

            useLockFile( dataDir::getValue );
        }


        protected int _run() throws Exception {
            return 0;
        }

        @Override
        protected void recoverFromCrash() {
            throw new IllegalStateException( "A previous run of the app did not clean up after itself, manual recovery required." );
        }
    }
}
