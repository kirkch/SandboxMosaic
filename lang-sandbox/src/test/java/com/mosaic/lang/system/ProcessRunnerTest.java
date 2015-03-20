package com.mosaic.lang.system;

import com.mosaic.lang.Failure;
import com.mosaic.lang.time.Duration;
import com.softwaremosaic.junit.JUnitMosaic;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Vector;

import static org.junit.Assert.*;


@RunWith(JUnitMosaicRunner.class)
public class ProcessRunnerTest {

    private DebugSystem system = new DebugSystem();


    @Test(threadCheck=true)
    public void runCommandThatDoesNotExist_expectFailure() {
        ProcessRunner runner = new ProcessRunner(system, "ffff");
        OSProcess     result = runner.run();

        result.spinUntilComplete(1000);

        assertEquals( IOException.class, result.getFailureNoBlock().getSource() );
        assertTrue( result.getFailureNoBlock().getMessage().contains( "No such file or directory") );
    }

    @Test(threadCheck=true)
    public void runCommand_thatExitsWithZero() {
        ProcessRunner runner  = new ProcessRunner(system, "java", "-version");
        OSProcess     result  = runner.run();

        result.spinUntilComplete(1000);

        assertEquals( 0, result.getResultNoBlock().intValue() );
    }

    @Test(threadCheck=true)
    public void runCommand_thatExitsWithOne() {
        ProcessRunner runner = new ProcessRunner(system, "java");
        OSProcess     result = runner.run();

        result.spinUntilComplete(1000);

        assertEquals( 1, result.getResultNoBlock().intValue() );
    }

    @Test(threadCheck=true)
    public void runCommand_that() {
        ProcessRunner runner = new ProcessRunner(system, "java", "-cp", System.getProperty("java.class.path"), SleepMain.class.getName());
        OSProcess     result = runner.run();

        long startMillis = System.currentTimeMillis();
        result.spinUntilComplete(1000);
        long durationMillis = System.currentTimeMillis() - startMillis;


        int mainMethodDuration = result.getResultNoBlock();         // SleepMain uses the return status code as the duration in millis

        assertTrue( mainMethodDuration >= SleepMain.SLEEP_MILLIS ); // main method must always take as long as its sleep
        assertTrue( durationMillis >= mainMethodDuration );         // the process builder must take at least as long as the main method that gets called
    }

    @Test(threadCheck=true)
    public void captureSystemOutWhileTheCommandIsRunning() {
        Vector<String> capturedOutput = new Vector<>();
        String[]       args           = {"-cp", System.getProperty("java.class.path"), NoisyMain.class.getName()};
        ProcessRunner  runner         = new ProcessRunner(system, "java", args, capturedOutput::add);
        OSProcess      result         = runner.run();


        JUnitMosaic.spinUntilTrue( () -> capturedOutput.size() >= 2 );

        result.spinUntilComplete(1000);


        String[] expectedText = {"Hello World", "I am noisy Main"};
        assertEquals( 0, result.getResultNoBlock().intValue() );
        assertArrayEquals( expectedText, capturedOutput.toArray() );
    }

    @Test(threadCheck=true)
    public void captureSystemStdErrWhileTheCommandIsRunning() {
        Vector<String> capturedOutput = new Vector<>();
        String[]       args           = {"-cp", System.getProperty("java.class.path"), ErrorMain.class.getName()};
        ProcessRunner  runner         = new ProcessRunner(system, "java", args, capturedOutput::add);
        OSProcess      result         = runner.run();


        JUnitMosaic.spinUntilTrue( () -> capturedOutput.size() >= 1 );

        result.spinUntilComplete(1000);


        String[] expectedText = {"Hello Error"};
        assertEquals( 0, result.getResultNoBlock().intValue() );
        assertArrayEquals( expectedText, capturedOutput.toArray() );
    }

    @Test(threadCheck=true)
    public void abortARunningChildProcess() {
        String[]      args   = {"-cp", System.getProperty("java.class.path"), NeverCompletesMain.class.getName()};
        ProcessRunner runner = new ProcessRunner(system, "java", args);
        OSProcess     result = runner.run();


        result.completeWithFailure( new Failure( this.getClass(), "abort" ) );
    }

    @Test(threadCheck=true)
    public void writeTextToTheChildProcessesStdIn_expectTheChildProcessToBeAbleToReadIt() {
        String[]      args    = {"-cp", System.getProperty("java.class.path"), EchoMain.class.getName()};
        ProcessRunner runner  = new ProcessRunner(system, "java", args, System.out::println);
        OSProcess     process = runner.run();

        process.getPipedWriter().println( "hello world" );
        process.getPipedWriter().flush();

        // NB EchoMain exits with status zero iff the string 'hello world' is read in
        assertEquals( 0, process.spinUntilComplete(1000).getResultNoBlock().intValue() );
    }



    public static class SleepMain {
        public static final long SLEEP_MILLIS = 30;

        public static void main( String[] args ) {
            long startMillis = System.currentTimeMillis();

            Backdoor.sleep(Duration.millis(SLEEP_MILLIS));

            long durationMillis = System.currentTimeMillis() - startMillis;

            System.exit( (int) durationMillis );
        }
    }

    public static class NoisyMain {
        public static void main( String[] args ) {
            System.out.println( "Hello World" );
            System.out.println( "I am noisy Main" );
        }
    }

    public static class ErrorMain {
        public static void main( String[] args ) {
            System.err.println( "Hello Error" );
        }
    }


    public static class NeverCompletesMain {
        public static void main( String[] args ) {
            Backdoor.sleep( Duration.days(1) );
        }
    }

    public static class EchoMain {
        public static void main( String[] args ) throws IOException {
            System.out.println( "STARTED" );

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            String input = in.readLine();
            System.out.println( "READ: '"+input+"'" );

            if ( Objects.equals(input,"hello world") ) {
                System.out.println(input);
                System.exit( 0 );
            } else {
                System.exit( 1 );
            }
        }
    }
}