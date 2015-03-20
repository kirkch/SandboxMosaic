package com.mosaic.io.cli;

import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.LiveSystem;
import com.mosaic.lang.system.OSProcess;
import com.mosaic.lang.system.SystemX;
import com.softwaremosaic.junit.JUnitMosaic;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class CLApp_systemTimeTests {

    private SystemX system = new LiveSystem();


    @Test
    public void startApp_expectItsTimeToMatchSystemClock() throws IOException {
        OSProcess process1 = system.runJavaProcess( ExpectSystemTimeApp.class );

        process1.spinUntilComplete( 3000 );

        assertEquals( 0, process1.getResultNoBlock().intValue() ); // will fail if system time is more than 100ms out
    }


    @Test
    public void startAppWithMemoryMappedFileForSystemTime() throws IOException {
        File f = File.createTempFile( "mmfile", ".dat" );

        try {
            system.clock.memoryMapClock( f );

            system.clock.set( 1111 );

            List<String> stdout = new Vector<>();
//            OSProcess process1 = system.runJavaProcess( ExpectSystemTimeApp.class, stdout::add, "--Xclock="+f.getAbsolutePath() );
            OSProcess process1 = system.runJavaProcess( ExpectMMTimeApp.class, s -> {stdout.add(s); System.out.println(s);}, "--Xclock="+f.getAbsolutePath() );

            JUnitMosaic.spinUntilTrue( 3000, () -> stdout.contains("Started") );

            system.clock.set( 2222 );

            process1.spinUntilComplete( 3000 ); // will fail if the systemx clock did not change in the child process to 2222

            assertEquals( 0, process1.getResultNoBlock().intValue() ); // will fail if the systemx clock was not as expected
        } finally {
            system.stop();

            f.delete();
        }
    }


    public static class ExpectSystemTimeApp extends CLApp {
        public static void main( String[] args ) {
            CLApp app = new ExpectSystemTimeApp();

            System.exit( app.runApp( args ) );
        }

        public ExpectSystemTimeApp() {
            super( "ExpectSystemTimeApp" );
        }


        protected int run() throws Exception {
            long a = System.currentTimeMillis();
            long b = system.getCurrentMillis();

            return Math.abs(b - a) < 100 ? 0 : 1;
        }
    }

    public static class ExpectMMTimeApp extends CLApp {
        public static void main( String[] args ) {
            CLApp app = new ExpectMMTimeApp();

            System.exit( app.runApp( args ) );
        }

        public ExpectMMTimeApp() {
            super( "ExpectMMTimeApp" );
        }

        // expect the system time to start at 1111
        // and then don't exit until the time changes to 2222
        protected int run() throws Exception {
            if ( system.getCurrentMillis() != 1111 ) {
                return 1;
            }

            System.out.println("Started");

            //noinspection ConstantConditions,StatementWithEmptyBody
            while ( system.getCurrentMillis() != 2222 ) {
                Backdoor.sleep( 10 );
            }

            return 0;
        }
    }
}
