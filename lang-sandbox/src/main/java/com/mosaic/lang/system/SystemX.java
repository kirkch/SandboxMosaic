package com.mosaic.lang.system;

import com.mosaic.io.IOUtils;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileSystemX;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.Cancelable;
import com.mosaic.lang.QA;
import com.mosaic.lang.ServiceThread;
import com.mosaic.lang.StartStopMixin;
import com.mosaic.lang.functional.TryNow;
import com.mosaic.lang.functional.VoidFunction0;
import com.mosaic.lang.functional.VoidFunction1;
import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.lang.time.DTM;
import com.mosaic.lang.time.Duration;
import com.mosaic.lang.time.SystemClock;
import com.mosaic.utils.ComparatorUtils;
import com.mosaic.utils.ListUtils;
import com.mosaic.utils.WatchDogTimer;
import sun.management.VMManagement;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ForkJoinPool;


/**
 *
 */
public abstract class SystemX extends StartStopMixin<SystemX> {

    public static final int KILOBYTE              = 1024;
    public static final int MEGABYTE              = KILOBYTE*1024;
    public static final int GIGABYTE              = MEGABYTE*1024;

    public static final int SIZEOF_BOOLEAN        = 1;
    public static final int SIZEOF_BYTE           = 1;
    public static final int SIZEOF_CHAR           = 2;
    public static final int SIZEOF_SHORT          = 2;
    public static final int SIZEOF_INT            = 4;
    public static final int SIZEOF_LONG           = 8;
    public static final int SIZEOF_FLOAT          = 4;
    public static final int SIZEOF_DOUBLE         = 8;

    public static final int SIZEOF_UNSIGNED_BYTE  = 1;
    public static final int SIZEOF_UNSIGNED_SHORT = 2;
    public static final int SIZEOF_UNSIGNED_INT   = 4;


    public static final int  UNSIGNED_BYTE_MASK  = 0xFF;
    public static final int  UNSIGNED_SHORT_MASK = 0xFFFF;
    public static final long UNSIGNED_INT_MASK   = 0xFFFFFFFF;

    public static final int MAX_UNSIGNED_INT = (Short.MAX_VALUE << 1)+1;
    public static final byte NULL_BYTE = 0;


    public static Charset UTF8  = Charset.forName( "UTF8" );
    public static Charset ASCII = Charset.forName( "ASCII" );

    public static String NEWLINE = "\n";


    public static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();


    private static final Random RND = new Random();

    private static final boolean areAssertionsEnabled = detectWhetherAssertionsAreEnabled();


    public static int getCacheLineLengthBytes() {
        return 64;  // todo detect or configure this
    }

    /**
     * Returns true when the JVM is in debug mode.  Toggled via the assertions
     * flag -ea.  This flag can be used to strip out debug checks that slow
     * the JVM down.
     */
    public static boolean isDebugRun() {
        return areAssertionsEnabled;
    }

    /**
     * Returns true if we are willing to sacrifice important safety checks for
     * extra speed.
     */
    public static boolean isRecklessRun() {
        return !areAssertionsEnabled;
    }

    public static String getTempDirectory() {
        return System.getProperty( "java.io.tmpdir" );
    }

    public static long nextRandomLong() {
        return RND.nextLong();
    }




    // The latest Intel processors have 3 layers (L1D, L2, and L3); with
    // sizes 32KB, 256KB, and 4-30MB; and ~1ns, ~4ns, and ~15ns latency respectively for a 3.0GHz CPU.
    // => 512, 4096, >131k cache lines for each layer



    @SuppressWarnings({"AssertWithSideEffects", "UnusedAssignment", "ConstantConditions"})
    private static boolean detectWhetherAssertionsAreEnabled() {
        boolean flag = false;

        assert (flag = true);

        return flag;
    }



    public final FileSystemX fileSystem;
    public final SystemClock clock;

    public final CharacterStream stdout;
    public final CharacterStream stderr;

    public final CharacterStream devLog;
    public final CharacterStream opsLog;
    public final CharacterStream userLog;
    public final CharacterStream warnLog;
    public final CharacterStream fatalLog;


    // NB stdin, when needed will be done by subscription with callbacks and not the Java blocking approach


    private final List<VoidFunction0> shutdownHooks = new ArrayList<>();
    private       Thread              masterShutdownHook;

    protected SystemX(
        String          systemName,
        FileSystemX     fileSystem,
        SystemClock     clock,

        CharacterStream stdout,
        CharacterStream stderr,

        CharacterStream devLog,
        CharacterStream opsLog,
        CharacterStream userLog,
        CharacterStream warnLog,
        CharacterStream fatalLog
    ) {
        super( systemName );

        this.fileSystem = fileSystem;
        this.clock      = clock;

        this.stdout     = stdout;
        this.stderr     = stderr;

        this.devLog     = devLog;
        this.opsLog     = opsLog;
        this.userLog    = userLog;
        this.warnLog    = warnLog;
        this.fatalLog   = fatalLog;

        this.masterShutdownHook = new Thread() {
            public void run() {
                runShutdownHooks();
            }
        };

        Runtime.getRuntime().addShutdownHook(masterShutdownHook);
    }


    public DirectoryX getDirectory( String path ) {
        return fileSystem.getCurrentWorkingDirectory().getDirectory( path );
    }

//    public DirectoryX getNonEmptyDirectory( String path ) {
//        QA.argNotBlank( path, "path" );
//
//        DirectoryX dir = getDirectory( path );
//
//        if ( dir == null ) {
//            throw new IllegalStateExceptionX( "Unable to proceed, '%s' does not exist", path );
//        } else if ( dir.isEmpty() ) {
//            throw new IllegalStateExceptionX( "Unable to proceed, no files found at '%s'", path );
//        }
//
//        return dir;
//    }
//
//    public DirectoryX getOrCreateDirectory( String path ) {
//        return fileSystem.getOrCreateDirectory( path );
//    }

    public long getCurrentMillis() {
        return clock.getCurrentMillis();
    }

    public DTM getCurrentDTM() {
        return clock.getCurrentDTM();
    }


    public boolean isDevAuditEnabled() {
        return devLog.isEnabled();
    }

    public boolean isUserAuditEnabled() {
        return userLog.isEnabled();
    }

    public boolean isOpsAuditEnabled() {
        return opsLog.isEnabled();
    }

    public boolean isWarnEnabled() {
        return warnLog.isEnabled();
    }

    public boolean isFatalEnabled() {
        return fatalLog.isEnabled();
    }


    /**
     * Record information that will help a developer diagnose a problem.  This audit level will
     * usually be disabled, as it is likely to be spammy and slow the system down.<p/>
     */
    public void devAudit( String msg, Object... args ) {
        devLog.writeLine( String.format( msg, args ) );
    }

    /**
     * Record information that will help a developer diagnose a problem.  This audit level will
     * usually be disabled, as it is likely to be spammy and slow the system down.<p/>
     */
    public void devAudit( Throwable ex, String msg ) {
        devLog.writeLine( msg );

        devAudit( msg );
        devAudit( ex );
    }

    public void devAudit( LogMessage msg, Object... args ) {
        if ( isDevAuditEnabled() ) {
            String formattedString = msg.getFormattedMessage( args );

            devLog.writeLine( formattedString );

            msg.incDisplayCount();
        }
    }

    /**
     * Record information that will help a developer diagnose a problem.  This audit level will
     * usually be disabled, as it is likely to be spammy and slow the system down.<p/>
     *
     * A developer is unlikely to look at this audit unless a problem is being worked on
     * during development.
     */
    public void devAudit( Throwable ex ) {
        devLog.writeException( ex );
    }

    /**
     * Record what the users are doing on the system at a level that would make sense to most
     * technical users of the tool.<p/>
     *
     * A user is likely to use this information if the tool is being run directly by them on the
     * command line, or as an audit made available to them via a GUI.  Or support staff may
     * access this information on the users behave in order to answer queries.
     */
    public void userAudit( String msg, Object... args ) {
        userLog.writeLine( String.format( msg, args ) );
    }

    public void userAudit( LogMessage msg, Object... args ) {
        if ( isUserAuditEnabled() ) {
            String formattedString = msg.getFormattedMessage( args );

            userLog.writeLine( formattedString );

            msg.incDisplayCount();
        }
    }

    /**
     * Records information that would be useful to the people responsible for keeping the
     * system up and running.  It is expected that most server tools would have this turned on
     * by default, however most end user command line tools such as most unix command line tools
     * like 'ls' and 'cd' would have this turned off by default.<p/>
     *
     * Ops is likely to refer to this information, as well as the user audit in respond to
     * problems that have been flagged.
     */
    public void opsAudit( String msg, Object... args ) {
        opsLog.writeLine( String.format( msg, args ) );
    }

    public void opsAudit( Throwable ex, String msg, Object... args ) {
        opsLog.writeLine( String.format( msg, args ) );
        opsLog.writeException( ex );
    }

    public void opsAudit( LogMessage msg, Object... args ) {
        if ( isOpsAuditEnabled() ) {
            String formattedString = msg.getFormattedMessage( args );

            opsLog.writeLine( formattedString );

            msg.incDisplayCount();
        }
    }

    /**
     * A problem has been detected, and has either been automatically mitigated or end users will
     * not be aware of the problem (yet).<p/>
     *
     * Upon detection, Ops should review and evaluate any potential impact and remedial actions
     * promptly during normal working hours.
     */
    public void warn( String msg, Object... args ) {
        warnLog.writeLine( String.format( msg, args ) );
    }

    public void warn( LogMessage msg, Object... args ) {
        if ( isWarnEnabled() ) {
            String formattedString = msg.getFormattedMessage( args );

            warnLog.writeLine( formattedString );

            msg.incDisplayCount();
        }
    }

    // todo consider adding a return value to warn and fatal; the receipt can be used
    //   to report when a problem has been resolved.. thus automating updates that a problem
    //   is still ongoing, and reporting how long it took for a problem to be resolved.

    /**
     * The system has visibly failed and requires immediate attention.<p/>
     *
     * Upon detection, drop what you are doing and fix.  Wake up on call staff if required.
     */
    public void fatal( Throwable ex, String msg ) {
        stderr.writeLine( msg );

        fatalLog.writeLine( msg );
        fatal( ex );
    }

    /**
     * The system has visibly failed and requires immediate attention.<p/>
     *
     * Upon detection, drop what you are doing and fix.  Wake up on call staff if required.
     */
    public void fatal( Throwable ex ) {
        fatalLog.writeException( ex );
    }

    /**
     * The system has visibly failed and requires immediate attention.<p/>
     *
     * Upon detection, drop what you are doing and fix.  Wake up on call staff if required.
     */
    public void fatal( String msg, Object... args ) {
        if ( stderr.isEnabled() || fatalLog.isEnabled() ) {
            String formattedText = String.format( msg, args );

            fatalLog.writeLine( formattedText );
            stderr.writeLine( formattedText );
        }
    }

    public void fatal( LogMessage msg, Object... args ) {
        if ( stderr.isEnabled() || fatalLog.isEnabled() ) {
            String formattedString = msg.getFormattedMessage( args );

            fatalLog.writeLine( formattedString );
            stderr.writeLine( formattedString );

            msg.incDisplayCount();
        }
    }

    public DirectoryX getCurrentWorkingDirectory() {
        return fileSystem.getCurrentWorkingDirectory();
    }

    public void setCurrentWorkingDirectory( DirectoryX cwd ) {
        fileSystem.setCurrentWorkingDirectory( cwd );
    }


    /**
     * Registers an instance of StartStoppable to share the same life cycle as this instance of
     * SystemX.
     */
    public <T> T registerService( T newService ) {
        this.appendDependency( newService );

        return newService;
    }

    protected void doStart() {
        clock.start();

        registerService( new TimerThread(getServiceName()) );
    }

    protected void doStop() {
        runShutdownHooks();

        TryNow.tryNow( () -> Runtime.getRuntime().removeShutdownHook( masterShutdownHook ) );

        clock.stop();
    }


    public int getProcessId() {
        VMManagement vmManagement = ReflectionUtils.getPrivateField( ManagementFactory.getRuntimeMXBean(), "jvm" );

        return ReflectionUtils.invokePrivateMethod( vmManagement, "getProcessId" );
    }




    /**
     * Invokes the specified Java Class as a child OS process.  The child process will use the
     * same classpath as the currently running process.
     */
    public OSProcess runJavaProcess( Class main, String... args ) {
        return runJavaProcess( main, IOUtils.LINE_SEPARATOR, line -> {}, args );
    }

    /**
     * Invokes the specified Java Class as a child OS process.  The child process will use the
     * same classpath as the currently running process.
     */
    public OSProcess runJavaProcess( Class main, VoidFunction1<String> stdoutCallback, String... args ) {
        return runJavaProcess( main, IOUtils.LINE_SEPARATOR, stdoutCallback, args );
    }

    /**
     * Invokes the specified Java Class as a child OS process.  The child process will use the
     * same classpath as the currently running process.
     */
    public OSProcess runJavaProcess( Class main, String lineSeparator, VoidFunction1<String> stdoutCallback, String... args ) {
        String javaHome  = System.getProperty( "java.home" );
        String javaCmd   = new File( javaHome, "bin/java" ).getAbsolutePath();
        String classPath = System.getProperty( "java.class.path" );

        List<String> javaCmdArgs = new ArrayList<>(3+args.length);
        javaCmdArgs.add( "-cp" );
        javaCmdArgs.add( classPath );
        javaCmdArgs.add( main.getName() );
        Collections.addAll( javaCmdArgs, args );


        ProcessRunner runner = new ProcessRunner( this, javaCmd, javaCmdArgs, stdoutCallback ).withLineSeparator( lineSeparator );

        runner.setCWD( fileSystem.getCurrentWorkingDirectory().getFullPath() );

        return runner.run();
    }

    public Cancelable addShutdownHook( VoidFunction0 callback ) {
        synchronized (shutdownHooks) {
            shutdownHooks.add( callback );

            return () -> {
                shutdownHooks.remove( callback );
            };
        }
    }


    private void runShutdownHooks() {
        synchronized (shutdownHooks) {
            if ( shutdownHooks.isEmpty() ) {
                return;
            }

            devAudit( "Shutdown hook triggered: running "+shutdownHooks.size() + " shutdown hooks" );

            // originally used a list of shutdown hooks, with no extra threads.. however
            // when working with child processes via ProcessBuilder; I found that only the
            // first child process would be terminated.

            List<Thread> threads = ListUtils.map( shutdownHooks, hook -> {
                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            hook.invoke();
                        }
                    };

                    t.start();

                    return t;
                }
            );

            for ( Thread t : threads ) {
                try {
                    t.join();

                    shutdownHooks.clear();
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Cancelable scheduleCallback( long delayMillis, VoidFunction0 callback ) {
        return scheduleCallback( Duration.millis(delayMillis), callback );
    }

    public Cancelable scheduleCallback( Duration delay, VoidFunction0 callback ) {
        QA.argIsGTEZero( delay.getMillis(), "delay" );

        TimerJob job = new TimerJob(getCurrentMillis()+delay.getMillis(), callback);

        synchronized (timerJobs) {
            Map.Entry<Long, List<TimerJob>> nextJobEntry = timerJobs.firstEntry();
            long oldNextJobMillis = nextJobEntry == null ? Long.MAX_VALUE : nextJobEntry.getKey().longValue();

            timerJobs.compute(
                job.getWhenMillis(),
                (k,currentList) -> {
                    if ( currentList == null ) {
                        return ListUtils.newLinkedList(job);
                    } else {
                        currentList.add( job );

                        return currentList;
                    }
                }
            );
        }

        return job;
    }

    public WatchDogTimer scheduleWatchDog( long alertAfterMillis, VoidFunction0 callback ) {
        return scheduleWatchDog( Duration.millis(alertAfterMillis), callback );
    }

    public WatchDogTimer scheduleWatchDog( Duration alertAfter, VoidFunction0 callback ) {
        return new WatchDogTimer( this, alertAfter, callback );
    }


    // todo move timer out to its own class, and re-implement using a scalable timer algorithm

    private final ConcurrentSkipListMap<Long,List<TimerJob>> timerJobs = new ConcurrentSkipListMap<>();

    public void setDevAuditEnabled( boolean flag ) {
        devLog.setEnabled( flag );
    }

    public void setOpsAuditEnabled( boolean flag ) {
        opsLog.setEnabled( flag );
    }

    public static int getCoreCount() {
        return Math.max( 1, Runtime.getRuntime().availableProcessors()/2 );  // assumes hyper threading is turned on
    }

    private class TimerThread extends ServiceThread<TimerThread> {
        // the larger this value, the more jobs can miss their scheduled time.  On the whole,
        // the timing should be fairly accurate.  However there is no interrupt implemented
        // for the timer thread, so if the thread is asleep on an empty queue (which will be the MAX sleep)
        // then it will not immediately here a new job come in.  Which if scheduled for 43ms, then it will
        // be out.  For now I am accepting a 'fair' effort and moving on to other coding tasks.
        private static final long MAX_SLEEP_MILLIS = 200;

        public TimerThread( String serviceName ) {
            super( "SystemX."+serviceName + "TimerThread", ThreadType.DAEMON );
        }

        protected long loop() throws InterruptedException {
            synchronized (timerJobs) {
                Map.Entry<Long, List<TimerJob>> nextJobEntry = timerJobs.firstEntry();
                if ( nextJobEntry == null ) {
                    return MAX_SLEEP_MILLIS;
                }

                long nowMillis = getCurrentMillis();

                if ( nowMillis >= nextJobEntry.getKey() ) {
                    for ( TimerJob job : nextJobEntry.getValue() ) {
                        if ( !job.isCancelled() ) {
                            TryNow.tryNow( job::invoke ); // TODO move this invocation outside of the synchronized block
                        }
                    }

                    timerJobs.remove( nextJobEntry.getKey() );
                }
            }

            Map.Entry<Long, List<TimerJob>> nextJobEntry = timerJobs.firstEntry();
            return Math.min( MAX_SLEEP_MILLIS, nextJobEntry == null ? MAX_SLEEP_MILLIS : nextJobEntry.getKey() - getCurrentMillis() );
        }
    }

    private static class TimerJob implements Cancelable, Comparable<TimerJob> {
        private long          whenMillis;
        private VoidFunction0 task;
        private boolean       isCancelled;

        public TimerJob( long whenMillis, VoidFunction0 task ) {
            this.whenMillis = whenMillis;
            this.task       = task;
        }

        public long getWhenMillis() {
            return whenMillis;
        }

        public boolean isCancelled() {
            return isCancelled;
        }

        public void cancel() {
            isCancelled = true;
        }

        public void invoke() {
            if ( isCancelled ) {
                return;
            }

            task.invoke();
        }

        public int compareTo( TimerJob o ) {
            return ComparatorUtils.compareAsc( this.getWhenMillis(), o.getWhenMillis() );
        }
    }
}
