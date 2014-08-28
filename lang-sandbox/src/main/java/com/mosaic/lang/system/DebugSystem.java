package com.mosaic.lang.system;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.io.filesystemx.inmemory.InMemoryFileSystem;
import com.mosaic.io.streams.CapturingCharacterStream;
import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.lang.text.PullParser;
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
public class DebugSystem extends SystemX {
    private static final String DEV   = "DEV";
    private static final String OPS   = "OPS";
    private static final String USER  = "USER";
    private static final String WARN  = "WARN";
    private static final String FATAL = "FATAL";

    /**
     * Format:
     *
     * [LEVEL nanosFromStart]: message
     */
    private final List<String> logOutput;

    private final List<String> standardOutText;
    private final List<String> standardErrorText;


    public DebugSystem() {
        this( ReflectionUtils.getCallersClass().getSimpleName() );
    }

    public DebugSystem( String systemName ) {
        this(
            systemName,
            new InMemoryFileSystem(),
            new SystemClock(),
            new Vector<String>(),
            new Vector<String>(),
            new Vector<String>(),
            System.nanoTime()
        );
    }


    private DebugSystem(
        String             systemName,
        InMemoryFileSystem system,
        SystemClock        clock,
        List<String>       stdOutText,
        List<String>       stdErrorText,
        List<String>       logOutput,
        long               startTime
    ) {
        super(
            systemName,
            system,
            clock,
            new CapturingCharacterStream( stdOutText ),
            new CapturingCharacterStream( stdErrorText ),
            new CapturingLogCharacterStream(DEV, logOutput, startTime),
            new CapturingLogCharacterStream(OPS, logOutput, startTime),
            new CapturingLogCharacterStream(USER, logOutput, startTime),
            new CapturingLogCharacterStream(WARN, logOutput, startTime),
            new CapturingLogCharacterStream(FATAL, logOutput, startTime)
        );

        this.standardOutText   = stdOutText;
        this.standardErrorText = stdErrorText;
        this.logOutput         = logOutput;
    }


    public void tearDown() {
        fileSystem.deleteAll();
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
        FileX f = fileSystem.getFile( filePath );

        if ( f == null ) {
            throw new AssertionError( "File not found: " + filePath );
        }

        Bytes fileContents = f.loadBytes( FileModeEnum.READ_ONLY );
        List<String> actualLines = PullParser.toLines( fileContents );

        assertEquals(
            "File " + filePath + " existed but the contents was not what we expected",
            Arrays.asList( expectedLines ),
            actualLines
        );
    }

    public void assertEmptyDirectory( String dirPath ) {
        DirectoryX dir = fileSystem.getDirectory( dirPath );

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
        for ( String line : logOutput ) {
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


    private static class CapturingLogCharacterStream extends CapturingCharacterStream {
        private String logLevel;

        private long startTimeNanos;

        public CapturingLogCharacterStream( String logLevel, List<String> auditText, long startTimeNanos ) {
            super(auditText);

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


