package com.mosaic.io.cli;

import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.time.DTM;
import com.softwaremosaic.junit.JUnitMosaic;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


/**
 * Tests for restricting the app to one running instance per data directory.
 */
@RunWith( JUnitMosaicRunner.class )
public class CLApp_fileLockInMemoryTests {

    private DebugSystem    system1           = new DebugSystem();
    private DebugSystem    system2           = new DebugSystem();
    private CountDownLatch continuationLatch = new CountDownLatch(1);


    @Test(threadCheck=true)
    public void givenSingleInstanceApp_startTwoApps_expectSecondOneToNotStart() {
        system1.clock.fixCurrentDTM( new DTM(2020,1,1, 10,0,0) );
        system2.clock.fixCurrentDTM( new DTM(2020,1,1, 10,0,0) );

        SingleInstanceApp app1 = new SingleInstanceApp(system1, continuationLatch);
        SingleInstanceApp app2 = new SingleInstanceApp(system1, continuationLatch);

        new Thread( () -> app1.runApp("data") ).start();
        app1.spinUntilAppIsRunning();

        new Thread( () -> app2.runApp("data") ).start();

        JUnitMosaic.spinUntilTrue( Integer.MAX_VALUE, () -> system1.doesFatalAuditContain("Application is already running, only one instance is allowed at a time.") );

        continuationLatch.countDown();


        app1.spinUntilAppShutsDown();
        app2.spinUntilAppShutsDown();
    }

    @Test(threadCheck=true)
    public void givenSingleInstanceApp_startTwoAppsUsingDifferentDataDirectories_expectBothToStartFine() {
        system1.clock.fixCurrentDTM( new DTM(2020,1,1, 10,0,0) );
        system2.clock.fixCurrentDTM( new DTM(2020,1,1, 10,0,0) );

        SingleInstanceApp app1 = new SingleInstanceApp(system1, continuationLatch);
        SingleInstanceApp app2 = new SingleInstanceApp(system1, continuationLatch);

        new Thread( () -> app1.runApp("data1") ).start();

        app1.spinUntilAppIsRunning();

        new Thread( () -> app2.runApp("data2") );

        app2.spinUntilAppIsRunning();


        continuationLatch.countDown();


        app1.spinUntilAppShutsDown();
        app2.spinUntilAppShutsDown();


        assertFalse( system1.doesFatalAuditContain("Application is already running, only one instance is allowed at a time.") );
        assertFalse( system2.doesFatalAuditContain("Application is already running, only one instance is allowed at a time.") );
    }


    private static class SingleInstanceApp extends CLApp {
        private final CountDownLatch continuationLatch;

        private final CLArgument<DirectoryX> dataDir = getOrCreateDirectoryArgument( "dir", "data directory" );

        protected SingleInstanceApp( SystemX system, CountDownLatch continuationLatch ) {
            super( system );

            this.continuationLatch = continuationLatch;

            useLockFile( dataDir::getValue );
        }

        @Override
        protected int _run() throws Exception {
            system.opsAudit( "app is running" );

            assertTrue( continuationLatch.await(10, TimeUnit.SECONDS) );

            return 0;
        }

        public void spinUntilAppIsRunning() {
            JUnitMosaic.spinUntilTrue( () -> debugSystem().doesOpsAuditContain( "app is running" ) );
        }

        public void spinUntilAppShutsDown() {
            JUnitMosaic.spinUntilTrue( () -> debugSystem().doesOpsAuditContain("Ended at") );
        }

        private DebugSystem debugSystem() {
            return (DebugSystem) system;
        }
    }
}