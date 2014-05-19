package com.mosaic.lang.system;

import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileSystemX;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.IllegalStateExceptionX;
import com.mosaic.lang.QA;
import com.mosaic.lang.time.SystemClock;

import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;


/**
 *
 */
public abstract class SystemX {

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

    public final CharacterStream debug;
    public final CharacterStream audit;
    public final CharacterStream info;
    public final CharacterStream warn;
    public final CharacterStream error;
    public final CharacterStream fatal;

    private final boolean isDebugEnabled;
    private final boolean isAuditEnabled;
    private final boolean isInfoEnabled;
    private final boolean isWarnEnabled;
    private final boolean isErrorEnabled;
    private final boolean isFatalEnabled;


    // NB stdin, when needed will be done by subscription with callbacks and not the Java blocking approach


    protected SystemX(
        FileSystemX     fileSystem,
        SystemClock     clock,

        CharacterStream stdout,
        CharacterStream stderr,

        CharacterStream debug,
        CharacterStream audit,
        CharacterStream info,
        CharacterStream warn,
        CharacterStream error,
        CharacterStream fatal
    ) {
        this.fileSystem = fileSystem;
        this.clock      = clock;

        this.stdout     = stdout;
        this.stderr     = stderr;

        this.debug      = debug;
        this.audit      = audit;
        this.info       = info;
        this.warn       = warn;
        this.error      = error;
        this.fatal      = fatal;

        this.isDebugEnabled = debug.isEnabled();
        this.isAuditEnabled = audit.isEnabled();
        this.isInfoEnabled  = info.isEnabled();
        this.isWarnEnabled  = warn.isEnabled();
        this.isErrorEnabled = error.isEnabled();
        this.isFatalEnabled = fatal.isEnabled();
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

    public boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    public boolean isAuditEnabled() {
        return isAuditEnabled;
    }

    public boolean isInfoEnabled() {
        return isInfoEnabled;
    }

    public boolean isWarnEnabled() {
        return isWarnEnabled;
    }

    public boolean isErrorEnabled() {
        return isErrorEnabled;
    }

    public boolean isFatalEnabled() {
        return isFatalEnabled;
    }

    public void debug( String msg, Object... args ) {
        debug.writeLine( String.format(msg,args) );
    }

    public void debug( Throwable ex, String msg ) {
        debug.writeLine( msg );

        debug( msg );
        debug( ex );
    }

    public void debug( Throwable ex ) {
        debug.writeException( ex );
    }

    public void audit( String msg, Object... args ) {
        audit.writeLine( String.format(msg,args) );
    }

    public void info( String msg, Object... args ) {
        info.writeLine( String.format( msg, args ) );
    }

    public void warn( String msg, Object... args ) {
        warn.writeLine( String.format( msg, args ) );
    }

    public void error( Throwable ex ) {
        error.writeException( ex );
    }

    public void error( String msg, Object... args ) {
        error.writeLine( String.format( msg, args ) );
    }

    public void fatal( Throwable ex, String msg ) {
        stderr.writeLine( msg );

        fatal( msg );
        fatal( ex );
    }

    public void fatal( Throwable ex ) {
        fatal.writeException( ex );
    }

    public void fatal( String msg, Object... args ) {
        fatal.writeLine( String.format(msg,args) );
    }


}
