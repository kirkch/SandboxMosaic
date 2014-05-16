package com.mosaic.io.cli;

import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 */
public class CLAppTest {

    private DebugSystem system = new DebugSystem();


    @Test
    public void givenAppWithNoFlagsNoArgsNoDescriptionAndReturnsZeroWhenRun_invoke_expectZero() {
        CLApp2 app = new CLApp2(system) {
            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp() );

        system.assertNoLogMessages();
        system.assertStandardOutEquals();
    }

    @Test
    public void givenAppWithNoFlagsNoArgsNoDescriptionAndReturnsOneWhenRun_invoke_expectOne() {
        CLApp2 app = new CLApp2(system) {
            protected int _run() {
                return 1;
            }
        };

        assertEquals( 1, app.runApp() );

        system.assertNoLogMessages();
        system.assertStandardOutEquals();
    }

    @Test
    public void givenAppWithNoFlagsNoArgsNoDescriptionAndReturnsOneWhenRun_requestHelp_expectDefaultHelp() {
        CLApp2 app = new CLApp2(system) {
            protected int _run() {
                return 1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "Usage: "+app.getClass().getName(),
            "",
            "optional flags:",
            "",
            "    --help display this usage information",
            ""
        );
    }

    @Test
    public void givenAppWithDescriptionOnly_requestHelp_expectDescriptionInHelp() {
        CLApp2 app = new CLApp2(system) {
            {
                setDescription( "This is a test app.  Enjoy." );
            }

            protected int _run() {
                return 1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "Usage: "+app.getClass().getName(),
            "",
            "This is a test app.  Enjoy.",
            "",
            "optional flags:",
            "",
            "    --help display this usage information",
            ""
        );
    }


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
            "optional flags:",
            "",
            "    --help display this usage information",
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
            "optional flags:",
            "",
            "    --help display this usage information",
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
            "optional flags:",
            "",
            "    --help display this usage information",
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
            "optional flags:",
            "",
            "    --help display this usage information",
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
            "optional flags:",
            "",
            "    --help display this usage information",
            ""
        );
    }






//
// givenOptionalArgumentWithDefaultValue_invokeWithValue_expectTheSuppliedValueToBeUsed
// givenOptionalArgumentWithDefaultValue_invokeWithOutValue_expectTheSuppliedValueToBeTheDefaultValue

// givenBooleanFlag_requestHelp_expectFlagToBeDocumented
// givenBooleanFlag_invokeAppWithoutSpecifyingFlag_expectFlagToBeFalse
// givenBooleanFlag_invokeAppWithShortVersionOfFlag_expectFlagToBeTrue
// givenBooleanFlag_invokeAppWithLongVersionOfFlag_expectFlagToBeTrue

// givenKVFlag_requestHelp_expectFlagToBeDocumented
// givenKVFlag_invokeAppWithoutSpecifyingFlag_expectDefaultValueToBeUsed
// givenKVFlag_invokeAppWithFlag_expectSuppliedValueToBeUsed
// givenKVFlag_invokeAppWithFlagValueThatWillFailToParse_expectError

// givenAppThatThrowsExceptionWhenRun_runApp_expectError


// givenEnumFlag_requestHelp_expectFlagAndValuesToBeDocumented
// givenEnumFlag_invokeAppWithoutSpecifyingFlag_expectDefaultValueToBeUsed
// givenEnumFlag_invokeAppWithFlag_expectSuppliedValueToBeUsed
// givenEnumFlag_invokeAppWithUnknownEnumValue_expectError




// cmd flags args

// -v --verbose
// --logLevel=debug -d --debug
// --logLevel=audit -a --audit
// --logLevel=info -i --info

    private static class AppWithFlags extends CLApp2 {

//        private final CLArgument<LogLevelEnum> logLevelFlag;   // .get()   .setDefaultValue(v)
//
//        private final CLArgument<FileX>        sourceFile;


        public AppWithFlags( SystemX system ) {
            super( system );

//            setAppDescription( " " );
//
//            this.isVerboseCLF = registerBooleanFlag( "verbose", "v", "description" );
//            this.logLevelCLF  = registerEnumFlag( "logLevel", "description", LogLevelEnum.class,
//                    new CLEnum(Debug, "debug", "", "description"),
//                    new CLEnum(Info, "info|verbose", "v", "description"),
//                    new CLEnum(Audit, "audit", "", "description"),
//                    new CLEnum(Warn, "warn", "", "description"),
//                    new CLEnum(Error, "error", "", "description"),
//                    new CLEnum(Fatal, "fatal", "", "description")
//                ).withDefaultOf(Audit);

//            this.sourceFile   = registerArgument[T]( "source", "desc", f(s):T )
        }


        protected int _run() {
            return 0;
        }
    }

}
