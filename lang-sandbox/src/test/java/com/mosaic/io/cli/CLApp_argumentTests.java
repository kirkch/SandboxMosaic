package com.mosaic.io.cli;

import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.system.DebugSystem;
import org.junit.Test;

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
    public void givenAppWithTwoMandatoryStringArgs_requestHelp_expectDescriptionInHelp() {
        CLApp2 app = new CLApp2(system) {
            public CLArgument<String> source;
            public CLArgument<String> destination;

            {
                setDescription( "This is a test app.  Enjoy." );

                this.source      = registerArgument( "source", "the file to be copied" );
                this.destination = registerArgument( "destination", "where to copy the file to" );
            }

            protected int _run() {
                return 1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "Usage: "+app.getClass().getName()+ " source destination",
            "",
            "This is a test app.  Enjoy.",
            "",
            "    source      - the file to be copied",
            "    destination - where to copy the file to",
            "",
            "Options:",
            "",
            "    --help",
            "        display this usage information",
            ""
        );

        system.assertStandardErrorEquals();
    }

    @Test
    public void checkThatDescriptionIsWrappedAt80Characters() {
        CLApp2 app = new CLApp2(system) {
            {
                setDescription( "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" );
            }

            protected int _run() {
                return 1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "Usage: "+app.getClass().getName(),
            "",
            "12345678901234567890123456789012345678901234567890123456789012345678901234567890",
            "12345678901234567890",
            "",
            "Options:",
            "",
            "    --help",
            "        display this usage information",
            ""
        );
    }

    @Test
    public void checkThatArgumentNameAndDescriptionsAreWrappedAt80Characters() {
        CLApp2 app = new CLApp2(system) {
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
            "Usage: "+app.getClass().getName()+ " source destination",
            "",
            "This is a test app.  Enjoy.",
            "",
            "    source      - 12345678901234567890123456789012345678901234567890123456789012",
            "                  34567890123456789012345678901234567890",
            "    destination - 12345678901234567890123456789012345678901234567890123456789012",
            "                  34567890123456789012345678901234567890",
            "",
            "Options:",
            "",
            "    --help",
            "        display this usage information",
            ""
        );
    }



    @Test
    public void givenAppWithTwoMandatoryStringArgs_invokeWithOneMissingArg_expectErrorAndUsageDescription() {
        CLApp2 app = new CLApp2(system) {
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

        system.assertStandardErrorEquals(
            "Missing required argument 'destination', for more information invoke with --help."
        );

        system.assertStandardOutEquals();
        system.assertNoLogMessages();
    }

    @Test
    public void givenAppWithTwoMandatoryStringArgs_invokeWithBothMissing_expectErrorAndUsageDescription() {
        CLApp2 app = new CLApp2(system) {
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

        system.assertStandardErrorEquals(
            "Missing required argument 'source', for more information invoke with --help."
        );

        system.assertStandardOutEquals();
        system.assertNoLogMessages();
    }

    @Test
    public void givenAppWithOneMandatoryOneOptionalArg_requestHelp_expectArgDescriptionsInTheHelp() {
        CLApp2 app = new CLApp2(system) {
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
            "Usage: " + app.getClass().getName() + " source [destination]",
            "",
            "This is a test app.  Enjoy.",
            "",
            "    source      - the file to be copied",
            "    destination - where to copy the file to",
            "",
            "Options:",
            "",
            "    --help",
            "        display this usage information",
            ""
        );
    }

    @Test
    public void givenAppWithOneMandatoryOneOptionalArg_invokeWithBothArgs_expectItToRun() {
        CLApp2 app = new CLApp2(system) {
            public CLArgument<String> source;
            public CLArgument<String> destination;

            {
                setDescription( "This is a test app.  Enjoy." );

                this.source      = registerArgument( "source", "the file to be copied" );
                this.destination = registerArgumentOptional( "destination", "where to copy the file to" );
            }

            protected int _run() {
                assertEquals( "a", source.getValue() );
                assertEquals( "b", destination.getValue() );

                return 0;
            }
        };

        assertEquals( 0, app.runApp("a","b") );

        system.assertNoOutput();
    }

    @Test
    public void givenAppWithOneMandatoryOneOptionalArg_invokeWithMandatoryArgOnly_expectItToRun() {
        CLApp2 app = new CLApp2(system) {
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

        system.assertNoOutput();
    }

    @Test
    public void givenAppWithOneMandatoryOneOptionalArg_invokeWithOutMandatoryArg_expectError() {
        CLApp2 app = new CLApp2(system) {
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
            new CLApp2(system) {
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

        CLApp2 app = new CLApp2(system) {
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

        system.assertFatal( "Invalid value for 'repeatCount', for more information invoke with --help." );
        system.assertFatal( NumberFormatException.class, "For input string: \"three\"" );
    }

    @Test
    public void givenTypedArgument_invokeWithArgThatWillPassParsing_expectItToRun() {
        final Function1<String,Integer> parseIntegerFunc = new Function1<String, Integer>() {
            public Integer invoke( String arg ) {
                return Integer.parseInt( arg );
            }
        };

        CLApp2 app = new CLApp2(system) {
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

        system.assertNoOutput();
    }


// OPTIONAL ARGUMENTS WITH DEFAULT VALUES

    @Test
    public void givenOptionalArgumentWithDefaultValue_requestHelp_expectDefaultValueToBeDisplayedInHelp() {
        CLApp2 app = new CLApp2(system) {
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
            "Usage: " + app.getClass().getName() + " [directory]",
            "",
            "    directory - The directory to scan. Defaults to 'foo'.",
            "",
            "Options:",
            "",
            "    --help",
            "        display this usage information",
            ""
        );
    }

    @Test
    public void givenOptionalArgumentWithDefaultValue_invokeWithValue_expectTheSuppliedValueToBeUsed() {
        CLApp2 app = new CLApp2(system) {
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

        system.assertNoOutput();
    }

    @Test
    public void givenOptionalArgumentWithDefaultValue_invokeWithOutValue_expectTheSuppliedValueToBeTheDefaultValue() {
        CLApp2 app = new CLApp2(system) {
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

        system.assertNoOutput();
    }

}
