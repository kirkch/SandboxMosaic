package com.mosaic.lang.system;

import com.mosaic.collections.concurrent.Future;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileSystemX;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.Cancelable;
import com.mosaic.lang.IllegalStateExceptionX;
import com.mosaic.lang.QA;
import com.mosaic.lang.StartStopMixin;
import com.mosaic.lang.functional.VoidFunction0;
import com.mosaic.lang.functional.VoidFunction1;
import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.lang.time.DTM;
import com.mosaic.lang.time.SystemClock;
import com.mosaic.utils.ListUtils;
import sun.management.VMManagement;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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


    public static Charset UTF8  = Charset.forName( "UTF8" );
    public static Charset ASCII = Charset.forName( "ASCII" );

    public static String NEWLINE = "\n";


    public static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();


    private static final Random RND = new Random();

    private static final boolean areAssertionsEnabled = detectWhetherAssertionsAreEnabled();
    private DirectoryX currentWorkingDirectory;


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

    private final boolean isDevAuditEnabled;
    private final boolean isOpsAuditEnabled;
    private final boolean isUserAuditEnabled;
    private final boolean isWarnEnabled;
    private final boolean isFatalEnabled;


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
        this.clock = clock;

        this.stdout = stdout;
        this.stderr = stderr;

        this.devLog = devLog;
        this.opsLog = opsLog;
        this.userLog = userLog;
        this.warnLog = warnLog;
        this.fatalLog = fatalLog;

        this.isDevAuditEnabled = devLog.isEnabled();
        this.isOpsAuditEnabled = opsLog.isEnabled();
        this.isUserAuditEnabled = userLog.isEnabled();
        this.isWarnEnabled = warnLog.isEnabled();
        this.isFatalEnabled = fatalLog.isEnabled();

        this.currentWorkingDirectory = fileSystem.getRoot();

        this.masterShutdownHook = new Thread() {
            public void run() {
                runShutdownHooks();
            }
        };

        Runtime.getRuntime().addShutdownHook(masterShutdownHook);
    }


    public DirectoryX getDirectory( String path ) {
        return fileSystem.getDirectory( path );
    }

    public DirectoryX getNonEmptyDirectory( String path ) {
        QA.argNotBlank( path, "path" );

        DirectoryX dir = getDirectory( path );

        if ( dir == null ) {
            throw new IllegalStateExceptionX( "Unable to proceed, '%s' does not exist", path );
        } else if ( dir.isEmpty() ) {
            throw new IllegalStateExceptionX( "Unable to proceed, no files found at '%s'", path );
        }

        return dir;
    }

    public DirectoryX getOrCreateDirectory( String path ) {
        return fileSystem.getOrCreateDirectory( path );
    }

    public long getCurrentMillis() {
        return clock.getCurrentMillis();
    }

    public DTM getCurrentDTM() {
        return clock.getCurrentDTM();
    }


    public boolean isDevAuditEnabled() {
        return isDevAuditEnabled;
    }

    public boolean isUserAuditEnabled() {
        return isUserAuditEnabled;
    }

    public boolean isOpsAuditEnabled() {
        return isOpsAuditEnabled;
    }

    public boolean isWarnEnabled() {
        return isWarnEnabled;
    }

    public boolean isFatalEnabled() {
        return isFatalEnabled;
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

    public DirectoryX getCurrentWorkingDirectory() {
        return currentWorkingDirectory;
    }

    public void setCurrentWorkingDirectory( DirectoryX cwd ) {
        this.currentWorkingDirectory = cwd;
    }


    /**
     * Registers an instance of StartStoppable to share the same life cycle as this instance of
     * SystemX.
     */
    public <T> T registerService( T newService ) {
        this.serviceDependsUpon( newService );

        return newService;
    }

    protected void doStart() {}

    protected void doStop() {
        runShutdownHooks();

        Runtime.getRuntime().removeShutdownHook( masterShutdownHook );
    }


    public int getProcessId() {
        VMManagement vmManagement = ReflectionUtils.getPrivateField( ManagementFactory.getRuntimeMXBean(), "jvm" );

        return ReflectionUtils.invokePrivateMethod( vmManagement, "getProcessId" );
    }




    /**
     * Invokes the specified Java Class as a child OS process.  The child process will use the
     * same classpath as the currently running process.
     */
    public Future<Integer> runJavaProcess( Class main, String... args ) {
        return runJavaProcess( main, line -> {
        }, args );
    }

    /**
     * Invokes the specified Java Class as a child OS process.  The child process will use the
     * same classpath as the currently running process.
     */
    public Future<Integer> runJavaProcess( Class main, VoidFunction1<String> stdoutCallback, String... args ) {
        String javaHome  = System.getProperty( "java.home" );
        String javaCmd   = new File( javaHome, "bin/java" ).getAbsolutePath();
        String classPath = System.getProperty( "java.class.path" );

        List<String> javaCmdArgs = new ArrayList<>(3+args.length);
        javaCmdArgs.add( "-cp" );
        javaCmdArgs.add( classPath );
        javaCmdArgs.add( main.getName() );
        Collections.addAll( javaCmdArgs, args );

        ProcessRunner runner = new ProcessRunner( this, javaCmd, javaCmdArgs, stdoutCallback );

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
}
