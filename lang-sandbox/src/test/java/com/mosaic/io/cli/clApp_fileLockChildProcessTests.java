package com.mosaic.io.cli;

import com.mosaic.io.FileUtils;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileX;
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

    private SystemX system;
    private File    dataDir;

    @Before
    public void setup() throws IOException {
        this.dataDir = FileUtils.makeTempDirectory( "CLApp_fileLockChildProcessTests", ".dataDir" );

        this.system = LiveSystem.withNoLogging(dataDir);

        system.start();
    }

    @After
    public void tearDown() {
        DirectoryX dir = system.getCurrentWorkingDirectory();
        dir.deleteAll();

        system.stop();
    }


    @Test
    public void startTwoAppsInTheirOwnProcesses_expectSecondOneToNotStartDueToLock() throws IOException {
        Vector<String> processOutput1 = new Vector<>();
        Vector<String> processOutput2 = new Vector<>();

        OSProcess process1 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput1::add, "data" );
        JUnitMosaic.spinUntilTrue( () -> processOutput1.contains("App has started") );

        OSProcess process2 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput2::add, "data" );

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

        OSProcess process1 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput1::add, "data" );
        JUnitMosaic.spinUntilTrue( () -> processOutput1.contains("App has started") );

        signalChildProcessesToStop( dataDir );
        process1.spinUntilComplete( 3000 );

        removeSignalForChildProcessesToStop( dataDir );


        OSProcess process2 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput2::add, "data" );
        JUnitMosaic.spinUntilTrue( () -> processOutput2.contains( "App has started" ) );


        signalChildProcessesToStop( dataDir );
        spinUntilProcessesHaveStopped( process1, process2 );


        assertEquals( 0, process1.getResultNoBlock().intValue() );
        assertEquals( 0, process2.getResultNoBlock().intValue() );
    }

    @Test
    public void startTwoAppsInTheirOwnProcesses_killTheFirstProcessBeforeStartingTheSecond_expectTheSecondToTryToRecoverWhereTheDefaultImplIsToAbortTheApp() throws IOException {
        Vector<String> processOutput1 = new Vector<>();
        Vector<String> processOutput2 = new Vector<>();

        OSProcess process1 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput1::add, "data", "-v" );
        JUnitMosaic.spinUntilTrue( () -> processOutput1.contains("App has started") );

        process1.killImmediately();
        process1.spinUntilComplete( 3000);


        OSProcess process2 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput2::add, "data", "-v" );
        JUnitMosaic.spinUntilTrue( () -> processOutput2.contains( "A previous run of the app did not clean up after itself, manual recovery required. Aborting.." ) );


        signalChildProcessesToStop( dataDir );
        spinUntilProcessesHaveStopped( process1, process2 );


        assertEquals( 1, process2.getResultNoBlock().intValue() );
    }

    @Test
    public void startTwoAppsInTheirOwnProcesses_cleanlyShutdownTheFirstProcessBeforeStartingTheSecond_expectTheSecondToStartNormally() throws IOException {
        Vector<String> processOutput1 = new Vector<>();
        Vector<String> processOutput2 = new Vector<>();

        OSProcess process1 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput1::add, "data", "-v" );
        JUnitMosaic.spinUntilTrue( () -> processOutput1.contains("App has started") );

        process1.abort();
        process1.spinUntilComplete( 3000);


        OSProcess process2 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput2::add, "data", "-v" );
        JUnitMosaic.spinUntilTrue( () -> processOutput2.contains( "App has started" ) );

        assertFalse( processOutput2.contains("Previous run did not shutdown cleanly, recovering") );


        signalChildProcessesToStop( dataDir );
        spinUntilProcessesHaveStopped( process1, process2 );


        assertEquals( 0, process2.getResultNoBlock().intValue() );
    }

    @Test
    public void whenTryingToRecoverAnAbortedLock_recoverApp_expectAppToStart() throws IOException {
        Vector<String> processOutput1 = new Vector<>();
        Vector<String> processOutput2 = new Vector<>();

        OSProcess process1 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput1::add, "data" );
        JUnitMosaic.spinUntilTrue( () -> processOutput1.contains("App has started") );

        process1.killImmediately();
        process1.spinUntilComplete(3000);


        OSProcess process2 = system.runJavaProcess( RecoveryApp.class, processOutput2::add, "-v", "data" );
        JUnitMosaic.spinUntilTrue( () -> processOutput2.contains( "Previous run did not shutdown cleanly, recovering" ) );
        JUnitMosaic.spinUntilTrue( () -> processOutput2.contains( "App ran" ) );


        signalChildProcessesToStop( dataDir );
        spinUntilProcessesHaveStopped( process1, process2 );


        assertEquals( 0, process2.getResultNoBlock().intValue() );
    }

    @Test
    public void pidInLockFile() throws IOException {
        Vector<String> processOutput1 = new Vector<>();

        OSProcess process1 = system.runJavaProcess( WaitForSignalFileApp.class, processOutput1::add, "data" );
        JUnitMosaic.spinUntilTrue( () -> processOutput1.contains("App has started") );


        File lockFile = new File(dataDir,"data/LOCK");
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
        File doneFileF = new File( dataDir, "data/DONE" );
        doneFileF.getParentFile().mkdirs();

        RandomAccessFile doneFile = new RandomAccessFile( doneFileF, "rw" );

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

    public static class RecoveryApp extends CLApp {
        public static void main( String[] args ) {
            CLApp app  = new RecoveryApp();

            System.exit( app.runApp( args ) );
        }

        private final CLArgument<DirectoryX> dataDir = getOrCreateDirectoryArgument( "dir", "data directory" );

        public RecoveryApp() {
            super("RecoveryApp");

            useLockFile( dataDir::getValue );
        }


        protected int _run() throws Exception {
            system.userAudit( "App ran" );

            return 0;
        }

        @Override
        protected void recoverFromCrash() {
            system.opsAudit( "Previous run did not shutdown cleanly, recovering" );
        }
    }
}
