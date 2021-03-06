package com.mosaic.lang.system;

import com.mosaic.bytes.Bytes;
import com.mosaic.io.FileUtils;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileSystemX;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.io.filesystemx.disk.ActualFileSystem;
import com.mosaic.io.filesystemx.inmemory.InMemoryFileSystem;
import com.mosaic.io.streams.CapturingCharacterStream;
import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.lang.text.PullParser;
import com.mosaic.lang.time.Duration;
import com.mosaic.lang.time.SystemClock;
import com.mosaic.utils.StringUtils;
import org.junit.ComparisonFailure;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Vector;


/**
 *
 */
@SuppressWarnings({"UnusedDeclaration","unchecked"})
public class DebugSystem extends SystemX {
    private static final String DEV   = "DEV";
    private static final String OPS   = "OPS";
    private static final String USER  = "USER";
    private static final String WARN  = "WARN";
    private static final String FATAL = "FATAL";


    public static DebugSystem withActualFileSystem() {
        return withActualFileSystem( FileUtils.makeTempDirectory("debug",".tmp").getAbsolutePath() );
    }

    public static DebugSystem withActualFileSystem( String root ) {
        return new DebugSystem(
            ReflectionUtils.getCallersClass().getSimpleName(),
            new ActualFileSystem(root),
            new SystemClock(),
            new Vector<>(),
            new Vector<>(),
            new Vector<>(),
            new Vector<>(),
            System.nanoTime()
        );
    }


    /**
     * Format:
     *
     * [LEVEL nanosFromStart]: message
     */
    public final List<String> logOutput;

    public final List<String> allOutput;

    public final List<String> standardOutText;
    public final List<String> standardErrorText;


    public DebugSystem() {
        this( ReflectionUtils.getCallersClass().getSimpleName() );
    }
    public DebugSystem(FileSystemX fs) {
        this( ReflectionUtils.getCallersClass().getSimpleName(), fs );
    }

    public DebugSystem( String systemName ) {
        this( systemName, new InMemoryFileSystem() );
    }

    public DebugSystem( String systemName, FileSystemX fileSystem ) {
        this(
            systemName,
            fileSystem,
            new SystemClock(),
            new Vector<>(),
            new Vector<>(),
            new Vector<>(),
            new Vector<>(),
            System.nanoTime()
        );
    }


    private DebugSystem(
        String             systemName,
        FileSystemX        system,
        SystemClock        clock,
        List<String>       allOutput,
        List<String>       stdOutText,
        List<String>       stdErrorText,
        List<String>       logOutput,
        long               startTime
    ) {
        super(
            systemName,
            system,
            clock,
            new CapturingCharacterStream( stdOutText, allOutput ),
            new CapturingCharacterStream( stdErrorText, allOutput ),
            new CapturingLogCharacterStream(DEV, startTime, logOutput, allOutput),
            new CapturingLogCharacterStream(OPS, startTime, logOutput, allOutput),
            new CapturingLogCharacterStream(USER, startTime, logOutput, allOutput),
            new CapturingLogCharacterStream(WARN, startTime, logOutput, allOutput),
            new CapturingLogCharacterStream(FATAL, startTime, logOutput, allOutput)
        );

        this.allOutput         = allOutput;
        this.standardOutText   = stdOutText;
        this.standardErrorText = stdErrorText;
        this.logOutput         = logOutput;
    }

    public void assertDevAuditContains( String expectedMessage ) {
        assertLogMessageContains( DEV, expectedMessage );
    }

    public void assertDevAuditContains( Class<? extends Throwable> expectedExceptionType, String expectedMessage ) {
        assertDevAuditContains( expectedExceptionType.getSimpleName() + ": " + expectedMessage );
    }

    public boolean doesDevAuditContain( String expectedMessage ) {
        return doesLogContain( DEV, expectedMessage );
    }

    public void assertOpsAuditContains( String expectedMessage ) {
        assertLogMessageContains( OPS, expectedMessage );
    }

    public boolean doesOpsAuditContain( String expectedMessage ) {
        return doesLogContain( OPS, expectedMessage );
    }

    public void assertUserAuditContains( String expectedMessage ) {
        assertLogMessageContains( USER, expectedMessage );
    }

    public boolean doesUserAuditContain( String expectedMessage ) {
        return doesLogContain( USER, expectedMessage );
    }

    public void assertWarnContains( String expectedMessage ) {
        assertLogMessageContains( WARN, expectedMessage );
    }

    public boolean doesWarnAuditContain( String expectedMessage ) {
        return doesLogContain( WARN, expectedMessage );
    }

    public void assertFatalContains( Class<? extends Throwable> expectedExceptionType, String expectedMessage ) {
        assertFatalContains( expectedExceptionType.getSimpleName() + ": " + expectedMessage );
    }

    public void assertFatalContains( String expectedMessage ) {
        assertLogMessageContains( FATAL, expectedMessage );
    }

    public boolean doesFatalAuditContain( String expectedMessage ) {
        return doesLogContain( FATAL, expectedMessage );
    }

    private void assertNoLogMessages( String logLevel ) {
        String expectedPrefix = "["+logLevel+" ";

        int count = 0;

        for ( String msg : logOutput ) {
            if ( msg.startsWith(expectedPrefix) ) {
                count++;
            }
        }

        if ( count > 0 ) {
            reportError( "Expected no "+logLevel.toLowerCase()+"s, instead found " + count + " of them" );
        }
    }

    public void assertNoDeveloperMessages() {
        assertNoLogMessages( DEV );
    }

    public void assertNoOpsMessages() {
        assertNoLogMessages( OPS );
    }

    public void assertNoUserMessages() {
        assertNoLogMessages( USER );
    }

    public void assertNoWarnings() {
        assertNoLogMessages( WARN );
    }

    public void assertNoFatals() {
        assertNoLogMessages( FATAL );
    }

    public void assertHasFile( String filePath, String...expectedLines ) {
        FileX f = fileSystem.getCurrentWorkingDirectory().getFile( filePath );

        if ( f == null ) {
            throw new AssertionError( "File not found: " + filePath );
        }

        Bytes fileContents = f.openFile( FileModeEnum.READ_ONLY );
        List<String> actualLines = PullParser.toLines( fileContents );

        assertEquals(
            "File " + filePath + " existed but the contents was not what we expected",
            Arrays.asList( expectedLines ),
            actualLines
        );
    }

    public void assertEmptyDirectory( String dirPath ) {
        DirectoryX dir = fileSystem.getCurrentWorkingDirectory().getDirectory( dirPath );

        if ( dir != null ) {
            List<FileX> files = dir.files();

            if ( files.size() > 0 ) {
                throw new ComparisonFailure( "Expected "+dirPath+" to be empty", "[]", files.toString() );
            }
        }

    }

    private void assertEquals( String msg, List<String> expectedLines, List<String> actualLines ) {
        if ( !expectedLines.equals(actualLines) ) {
            throw new ComparisonFailure( msg, StringUtils.join(expectedLines,"\n"), StringUtils.join(actualLines,"\n") );
        }
    }

    public void assertNoAlerts() {
        assertNoWarnings();
        assertNoFatals();
    }

    public void assertNoLogMessages() {
        assertNoDeveloperMessages();
        assertNoOpsMessages();
        assertNoUserMessages();
    }

    private void assertLogMessageContains( String expectedLogLevel, String expectedMessage ) {
        if ( !doesLogContain(expectedLogLevel, expectedMessage) ) {
            reportError(
                String.format(
                    "Failed to find '%s' amongst the " + expectedLogLevel.toLowerCase() + " messages",
                    expectedMessage
                )
            );
        }
    }

    private boolean doesLogContain( String expectedLogLevel, String expectedMessage ) {
        try {
            String expectedPrefix = "[" + expectedLogLevel + " ";

            expectedMessage = expectedMessage.trim();

            for ( String x : logOutput ) {
                if ( x.startsWith( expectedPrefix ) && x.contains(expectedMessage) ) {
                    return true;
                }
            }
        } catch ( ConcurrentModificationException ex ) {
            // asserting a log is done during unit tests only; when spinning on a log
            // waiting for an event to occur then ConcurrentModificationException is
            // a very real possibility but is totally benign... so we skip it
        }

        return false;
    }

    private void reportError( String msg ) {
        dumpLog();

        throw new AssertionError( msg );
    }

    public void dumpLog() {
        for ( String line : allOutput ) {
            System.out.println( line );
        }
    }

    public void assertNoOutput() {
        assertStandardErrorEquals();
        assertStandardOutEquals();
    }

    public void assertStandardOutEquals( String...expectedLines ) {
        assertEquals( "standard output was not what we expected", Arrays.asList(expectedLines), this.standardOutText );
    }

    public void assertStandardErrorEquals( String...expectedLines ) {
        assertEquals( "standard error was not what we expected", Arrays.asList(expectedLines), this.standardErrorText );
    }

    public void incClock( Duration millis ) {
        clock.add( millis );
    }


    private static class CapturingLogCharacterStream extends CapturingCharacterStream {
        private String logLevel;

        private long startTimeNanos;

        public CapturingLogCharacterStream( String logLevel, long startTimeNanos, List<String>...audits) {
            super(audits);

            this.logLevel       = logLevel;
            this.startTimeNanos = startTimeNanos;
        }

        @Override
        protected String formatLine( String line ) {
            double time = (System.nanoTime() - startTimeNanos)/1000000.0;

            return "["+logLevel+" "+time+"]: "+ line;
        }
    }
}


