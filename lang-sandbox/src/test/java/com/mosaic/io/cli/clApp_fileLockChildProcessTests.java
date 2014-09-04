package com.mosaic.io.cli;

import com.mosaic.io.FileUtils;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.Failure;
import com.mosaic.lang.system.LiveSystem;
import com.mosaic.lang.system.OSProcess;
import com.mosaic.lang.system.SystemX;
import com.softwaremosaic.junit.JUnitMosaic;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Vector;

import static org.junit.Assert.*;


/**
 *
 */
public class CLApp_fileLockChildProcessTests {

    private SystemX system  = LiveSystem.withNoLogging();
    private File    dataDir;

    @Before
    public void setup() throws IOException {
        this.dataDir = FileUtils.makeTempDirectory( "CLApp_fileLockChildProcessTests", ".dataDir" );

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

        OSProcess process1 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput1::add, dataDir.getAbsolutePath() );
        JUnitMosaic.spinUntilTrue( () -> processOutput1.contains("App has started") );

        OSProcess process2 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput2::add, dataDir.getAbsolutePath() );

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

        OSProcess process1 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput1::add, dataDir.getAbsolutePath() );
        JUnitMosaic.spinUntilTrue( () -> processOutput1.contains("App has started") );

        signalChildProcessesToStop( dataDir );
        process1.spinUntilComplete( 3000 );

        removeSignalForChildProcessesToStop( dataDir );


        OSProcess process2 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput2::add, dataDir.getAbsolutePath() );
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

        OSProcess process1 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput1::add, dataDir.getAbsolutePath(), "-v" );
        JUnitMosaic.spinUntilTrue( () -> processOutput1.contains("App has started") );

        process1.completeWithFailure( new Failure( "abort the process" ) );
        process1.spinUntilComplete( 3000);


        OSProcess process2 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput2::add, dataDir.getAbsolutePath(), "-v" );
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

        OSProcess process1 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput1::add, dataDir.getAbsolutePath() );
        JUnitMosaic.spinUntilTrue( () -> processOutput1.contains("App has started") );

        process1.completeWithFailure( new Failure("abort the process") );
        process1.spinUntilComplete(3000);


        OSProcess process2 = system.runJavaProcess( FailRecoveryApp.class, processOutput2::add, dataDir.getAbsolutePath() );
        JUnitMosaic.spinUntilTrue( () -> processOutput2.contains( "FailRecoveryApp errored unexpectedly and was aborted. The error was 'A previous run of the app did not clean up after itself, manual recovery required.'." ) );


        signalChildProcessesToStop( dataDir );
        spinUntilProcessesHaveStopped( process1, process2 );


        assertEquals( 1, process2.getResultNoBlock().intValue() );
    }

    @Test
    public void pidInLockFile() throws IOException {
        Vector<String> processOutput1 = new Vector<>();

        OSProcess process1 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput1::add, dataDir.getAbsolutePath() );
        JUnitMosaic.spinUntilTrue( () -> processOutput1.contains("App has started") );


        File lockFile = new File(dataDir,"LOCK");
        BufferedReader in = new BufferedReader( new InputStreamReader(new FileInputStream(lockFile)) );

        String contents = in.readLine();

        assertEquals( Integer.toString(process1.getPid()), contents );

        signalChildProcessesToStop( dataDir );
        spinUntilProcessesHaveStopped( process1, process1 );


        assertEquals( 0, process1.getResultNoBlock().intValue() );
    }



    private void spinUntilProcessesHaveStopped( OSProcess process1, OSProcess process2 ) {
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

                system.userAudit( "dataDirectory=" + dir.getFullPath() );
                system.userAudit( system.fileSystem.getClass().getName() );
                system.userAudit( "App has started" );

                pollForContinuationFile( dir );

                system.opsAudit( "Continuation file detected, exiting" );
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
