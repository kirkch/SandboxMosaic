package com.mosaic.io.cli;

import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.utils.ListUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mosaic.io.cli.CLAppTest.runAppAndAssertReturnCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;


/**
 *
 */
public class CLApp_argumentTests {

    private DebugSystem system = new DebugSystem();


// ARGUMENT HANDLING

    @Test
    public void givenDuplicateArgNames_expectException() {
        try {
            new CLApp(system) {
                public CLArgument<String> source;
                public CLArgument<String> destination;

                {
                    setDescription( "this is a test app.  Enjoy" );

                    this.source      = registerArgument( "source", "the file to be copied" );
                    this.destination = registerArgument( "source", "where to copy the file to" );
                }

                protected int _run() {
                    return 1;
                }
            };

            fail( "expected exception" );
        } catch ( IllegalArgumentException ex ) {
            assertEquals( "'source' has been declared twice", ex.getMessage() );
        }
    }

    @Test
    public void givenAppWithTwoMandatoryStringArgs_requestHelp_expectDescriptionInHelp() {
        CLApp app = new CLApp(system) {
            public CLArgument<String> source;
            public CLArgument<String> destination;

            {
                setDescription( "this is a test app.  Enjoy" );

                this.source      = registerArgument( "source", "the file to be copied" );
                this.destination = registerArgument( "destination", "where to copy the file to" );
            }

            protected int _run() {
                return 1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "",
            "Usage: "+app.getClass().getName()+ " source destination",
            "",
            "    This is a test app.  Enjoy.",
            "",
            "Arguments:",
            "",
            "    source      - The file to be copied.",
            "    destination - Where to copy the file to.",
            "",
            "Options:",
            "",
            "    -c <file>, --config=<file>",
            "        Any command line option may be included within a properties file and",
            "        specified here.",
            "",
            "    -?, --help",
            "        Display this usage information.",
            "",
            "    -v, --verbose",
            "        Include operational context in logging suitable for Ops. To enable full",
            "        developer debugging output then pass -ea to the JVM.",
            ""
        );

        system.assertStandardErrorEquals();
    }

    @Test
    public void checkThatDescriptionIsWrappedAt80Characters() {
        CLApp app = new CLApp(system) {
            {
                setDescription( "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" );
            }

            protected int _run() {
                return 1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "",
            "Usage: "+app.getClass().getName(),
            "",
            "    12345678901234567890123456789012345678901234567890123456789012345678901234567890",
            "    12345678901234567890.",
            "",
            "Options:",
            "",
            "    -c <file>, --config=<file>",
            "        Any command line option may be included within a properties file and",
            "        specified here.",
            "",
            "    -?, --help",
            "        Display this usage information.",
            "",
            "    -v, --verbose",
            "        Include operational context in logging suitable for Ops. To enable full",
            "        developer debugging output then pass -ea to the JVM.",
            ""
        );
    }

    @Test
    public void checkThatArgumentNameAndDescriptionsAreWrappedAt80Characters() {
        CLApp app = new CLApp(system) {
            public CLArgument<String> source;
            public CLArgument<String> destination;

            {
                setDescription( "This is a test app.  Enjoy." );

                this.source      = registerArgument( "source", "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" );
                this.destination = registerArgument( "destination", "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" );
            }

            protected int _run() {
                return 10;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "",
            "Usage: "+app.getClass().getName()+ " source destination",
            "",
            "    This is a test app.  Enjoy.",
            "",
            "Arguments:",
            "",
            "    source      - 12345678901234567890123456789012345678901234567890123456789012",
            "                  34567890123456789012345678901234567890.",
            "    destination - 12345678901234567890123456789012345678901234567890123456789012",
            "                  34567890123456789012345678901234567890.",
            "",
            "Options:",
            "",
            "    -c <file>, --config=<file>",
            "        Any command line option may be included within a properties file and",
            "        specified here.",
            "",
            "    -?, --help",
            "        Display this usage information.",
            "",
            "    -v, --verbose",
            "        Include operational context in logging suitable for Ops. To enable full",
            "        developer debugging output then pass -ea to the JVM.",
            ""
        );
    }



    @Test
    public void givenAppWithTwoMandatoryStringArgs_invokeWithOneMissingArg_expectErrorAndUsageDescription() {
        CLApp app = new CLApp(system) {
            public CLArgument<String> source;
            public CLArgument<String> destination;

            {
                setDescription( "This is a test app.  Enjoy." );

                this.source      = registerArgument( "source", "the file to be copied" );
                this.destination = registerArgument( "destination", "where to copy the file to" );
            }

            protected int _run() {
                return 0;
            }
        };

        assertEquals( 1, app.runApp("abc") );

        system.assertStandardErrorEquals("Missing required argument 'destination', for more information invoke with --help.");
        system.assertFatalContains("Missing required argument 'destination', for more information invoke with --help.");

        system.assertStandardOutEquals();
    }

    @Test
    public void givenAppWithTwoMandatoryStringArgs_invokeWithBothMissing_expectErrorAndUsageDescription() {
        CLApp app = new CLApp(system) {
            public CLArgument<String> source;
            public CLArgument<String> destination;

            {
                setDescription( "This is a test app.  Enjoy." );

                this.source      = registerArgument( "source", "the file to be copied" );
                this.destination = registerArgument( "destination", "where to copy the file to" );
            }

            protected int _run() {
                return 0;
            }
        };

        assertEquals( 1, app.runApp() );

        system.assertStandardErrorEquals("Missing required argument 'source', for more information invoke with --help.");
        system.assertFatalContains("Missing required argument 'source', for more information invoke with --help.");

        system.assertStandardOutEquals();
    }

    @Test
    public void givenAppWithOneMandatoryOneOptionalArg_requestHelp_expectArgDescriptionsInTheHelp() {
        CLApp app = new CLApp(system) {
            public CLArgument<String> source;
            public CLArgument<String> destination;

            {
                setDescription( "This is a test app.  Enjoy." );

                this.source      = registerArgument( "source", "the file to be copied" );
                this.destination = registerArgumentOptional( "destination", "where to copy the file to" );
            }

            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "",
            "Usage: " + app.getClass().getName() + " source [destination]",
            "",
            "    This is a test app.  Enjoy.",
            "",
            "Arguments:",
            "",
            "    source      - The file to be copied.",
            "    destination - Where to copy the file to.",
            "",
            "Options:",
            "",
            "    -c <file>, --config=<file>",
            "        Any command line option may be included within a properties file and",
            "        specified here.",
            "",
            "    -?, --help",
            "        Display this usage information.",
            "",
            "    -v, --verbose",
            "        Include operational context in logging suitable for Ops. To enable full",
            "        developer debugging output then pass -ea to the JVM.",
            ""
        );
    }

    @Test
    public void givenAppWithOneMandatoryOneOptionalArg_invokeWithBothArgs_expectItToRun() {
        CLApp app = new CLApp(system) {
            public CLArgument<String> source;
            public CLArgument<String> destination;

            {
                setDescription( "This is a test app.  Enjoy." );

                this.source      = registerArgument( "source", "The file to be copied." );
                this.destination = registerArgumentOptional( "destination", "Where to copy the file to." );
            }

            protected int _run() {
                assertEquals( "a", source.getValue() );
                assertEquals( "b", destination.getValue() );

                return 0;
            }
        };

        assertEquals( 0, app.runApp("a","b") );

        system.assertNoAlerts();
    }

    @Test
    public void givenAppWithOneMandatoryOneOptionalArg_invokeWithMandatoryArgOnly_expectItToRun() {
        CLApp app = new CLApp(system) {
            public CLArgument<String> source;
            public CLArgument<String> destination;

            {
                setDescription( "This is a test app.  Enjoy." );

                this.source      = registerArgument( "source", "the file to be copied" );
                this.destination = registerArgumentOptional( "destination", "where to copy the file to" );
            }

            protected int _run() {
                assertEquals( "a", source.getValue() );
                assertNull( destination.getValue() );

                return 0;
            }
        };

        assertEquals( 0, app.runApp("a") );

        system.assertNoAlerts();
    }

    @Test
    public void givenAppWithOneMandatoryOneOptionalArg_invokeWithOutMandatoryArg_expectError() {
        CLApp app = new CLApp(system) {
            public CLArgument<String> source;
            public CLArgument<String> destination;

            {
                setDescription( "This is a test app.  Enjoy." );

                this.source      = registerArgument( "source", "the file to be copied" );
                this.destination = registerArgumentOptional( "destination", "where to copy the file to" );
            }

            protected int _run() {
                throw new AssertionError( "must not be called" );
            }
        };

        assertEquals( 1, app.runApp() );

        system.assertStandardErrorEquals( "Missing required argument 'source', for more information invoke with --help." );
    }

    @Test
    public void tryToCreateAppWithOptionalThenMandatoryArgs_expectErrorAsMandatoryMustGoBeforeOptional() {
        try {
            new CLApp(system) {
                public CLArgument<String> source;
                public CLArgument<String> destination;

                {
                    setDescription( "This is a test app.  Enjoy." );

                    // the order of these two register calls matter;  mandatory args must come first
                    this.destination = registerArgumentOptional( "destination", "where to copy the file to" );
                    this.source      = registerArgument( "source", "the file to be copied" );
                }

                protected int _run() {
                    throw new AssertionError( "must not be called" );
                }
            };

            fail( "Expected exception" );
        } catch ( IllegalStateException ex ) {
            assertEquals( "Mandatory argument 'source' must be declared before all optional arguments", ex.getMessage() );
        }
    }

// PARSING TYPED ARGUMENTS

    @Test
    public void givenTypedArgument_invokeWithArgThatWillFailParsing_expectError() {
        final Function1<String,Integer> parseIntegerFunc = new Function1<String, Integer>() {
            public Integer invoke( String arg ) {
                return Integer.parseInt( arg );
            }
        };

        CLApp app = new CLApp(system) {
            public CLArgument<Integer> repeatCount;

            {
                this.repeatCount = registerArgument( "repeatCount", "the number of times to repeat the operation", parseIntegerFunc );
            }

            protected int _run() {
                throw new AssertionError( "must not be called" );
            }
        };

        assertEquals( 1, app.runApp("three") );

        system.assertStandardErrorEquals( "Invalid value for 'repeatCount', for more information invoke with --help." );

        system.assertFatalContains( "Invalid value for 'repeatCount', for more information invoke with --help." );
        system.assertFatalContains( NumberFormatException.class, "For input string: \"three\"" );
    }

    @Test
    public void givenTypedArgument_invokeWithArgThatWillPassParsing_expectItToRun() {
        final Function1<String,Integer> parseIntegerFunc = new Function1<String, Integer>() {
            public Integer invoke( String arg ) {
                return Integer.parseInt( arg );
            }
        };

        CLApp app = new CLApp(system) {
            public CLArgument<Integer> repeatCount;

            {
                this.repeatCount = registerArgument( "repeatCount", "the number of times to repeat the operation", parseIntegerFunc );
            }

            protected int _run() {
                assertEquals( 3, repeatCount.getValue().intValue() );

                return 0;
            }
        };

        assertEquals( 0, app.runApp("3") );

        system.assertNoAlerts();
    }


// OPTIONAL ARGUMENTS WITH DEFAULT VALUES

    @Test
    public void givenOptionalArgumentWithDefaultValue_requestHelp_expectDefaultValueToBeDisplayedInHelp() {
        CLApp app = new CLApp(system) {
            public CLArgument<String> directory;

            {
                this.directory = registerArgumentOptional( "directory", "The directory to scan." ).withDefaultValue("foo");
            }

            protected int _run() {
                throw new AssertionError( "must not be called" );
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "",
            "Usage: " + app.getClass().getName() + " [directory]",
            "",
            "Arguments:",
            "",
            "    directory - The directory to scan. Defaults to 'foo'.",
            "",
            "Options:",
            "",
            "    -c <file>, --config=<file>",
            "        Any command line option may be included within a properties file and",
            "        specified here.",
            "",
            "    -?, --help",
            "        Display this usage information.",
            "",
            "    -v, --verbose",
            "        Include operational context in logging suitable for Ops. To enable full",
            "        developer debugging output then pass -ea to the JVM.",
            ""
        );
    }

    @Test
    public void givenOptionalArgumentWithDefaultValue_invokeWithValue_expectTheSuppliedValueToBeUsed() {
        CLApp app = new CLApp(system) {
            public CLArgument<String> directory;

            {
                this.directory = registerArgumentOptional( "directory", "The directory to scan." ).withDefaultValue("foo");
            }

            protected int _run() {
                assertEquals( "abc", directory.getValue() );

                return 42;
            }
        };

        assertEquals( 42, app.runApp("abc") );

        system.assertNoAlerts();
    }

    @Test
    public void givenOptionalArgumentWithDefaultValue_invokeWithOutValue_expectTheSuppliedValueToBeTheDefaultValue() {
        CLApp app = new CLApp(system) {
            public CLArgument<String> directory;

            {
                this.directory = registerArgumentOptional( "directory", "The directory to scan." ).withDefaultValue("foo");
            }

            protected int _run() {
                assertEquals( "foo", directory.getValue() );

                return 42;
            }
        };

        assertEquals( 42, app.runApp() );

        system.assertNoAlerts();
    }

// FILE ARGUMENTS

    @Test
    public void givenArgumentThatScansDirectoryForFiles_givenFilesInDirAndSubDirs_expectItToReturnTheFilesInDirWithoutRecursing() {
        CLApp app = new CLApp(system) {
            public CLArgument<Iterable<FileX>> files = scanForFilesArgument( "directory", "The directory to scan.", ".xml" );

            protected int _run() {
                List<String> actualFileNames = ListUtils.map( files.getValue(), new Function1<FileX, String>() {
                    public String invoke( FileX f ) {
                        return f.getFileName();
                    }
                } );

                Collections.sort(actualFileNames);

                assertEquals( Arrays.asList("a.xml","b.xml"), actualFileNames );

                return 42;
            }
        };

        DirectoryX files = system.getOrCreateDirectory( "files" );
        files.addFile( "a.xml", "123" );
        files.addFile( "b.xml", "123" );

        DirectoryX subdir = files.createDirectory( "subdir" );
        subdir.addFile( "c.xml", "123" );

        runAppAndAssertReturnCode( app, 42, "files" );

        system.assertNoAlerts();
    }

    @Test
    public void givenArgumentThatScansDirectoryForFiles_givenMissingDirectory_expectError() {
        CLApp app = new CLApp(system) {
            public CLArgument<Iterable<FileX>> files = scanForFilesArgument( "directory", "The directory to scan.", ".xml" );

            protected int _run() {
                return 42;
            }
        };

        assertEquals( 1, app.runApp("files") );

        system.assertStandardErrorEquals( "Directory 'files' does not exist." );
        system.assertFatalContains( CLException.class, "Directory 'files' does not exist." );
    }

    @Test
    public void givenArgumentThatScansDirectoryForFiles_giveFile_expectFileFound() {
        CLApp app = new CLApp(system) {
            public CLArgument<Iterable<FileX>> files = scanForFilesArgument( "directory", "The directory to scan.", ".xml" );

            protected int _run() {
                List<String> actualFileNames = ListUtils.map( files.getValue(), new Function1<FileX, String>() {
                    public String invoke( FileX f ) {
                        return f.getFileName();
                    }
                } );

                Collections.sort(actualFileNames);

                assertEquals( Arrays.asList("a.xml"), actualFileNames );

                return 42;
            }
        };

        DirectoryX dir = system.getOrCreateDirectory( "dir" );
        dir.addFile( "a.xml", "123" );

        system.setCurrentWorkingDirectory( dir );

        runAppAndAssertReturnCode( app, 42, "a.xml" );

        system.assertNoAlerts();
    }

    @Test
    public void getOrCreateDirectoryArgument_givenDirectory_expectDirectoryToBeReturned() {
        CLApp app = new CLApp(system) {
            public CLArgument<DirectoryX> dir = getOrCreateDirectoryArgument( "directory", "The directory to scan." );

            protected int _run() {
                assertEquals("logs", dir.getValue().getDirectoryName());
                return 42;
            }
        };

        system.getOrCreateDirectory( "logs" );

        assertEquals( 42, app.runApp( "logs" ) );

        system.assertNoAlerts();
    }

    @Test
    public void getOrCreateDirectoryArgument_givenCWD_expectCWDInPath() {
        CLApp app = new CLApp(system) {
            public CLArgument<DirectoryX> dir = getOrCreateDirectoryArgument( "directory", "The directory to scan." );

            protected int _run() {
                assertEquals("/abc/logs", dir.getValue().getFullPath());
                return 42;
            }
        };

        DirectoryX cwd = system.getOrCreateDirectory( "abc" );
        system.setCurrentWorkingDirectory( cwd );

        runAppAndAssertReturnCode( app, 42, "logs" );

        system.assertNoAlerts();
    }

    @Test
    public void getOrCreateDirectoryArgument_givenMissingDirectory_expectDirectoryToBeCreated() {
        CLApp app = new CLApp(system) {
            public CLArgument<DirectoryX> dir = getOrCreateDirectoryArgument( "directory", "The directory to scan." );

            protected int _run() {
                assertEquals( "logs", dir.getValue().getDirectoryName() );
                return 42;
            }
        };

        assertEquals( 42, app.runApp( "logs" ) );

        system.assertNoAlerts();
    }

}
