package com.mosaic.io.cli;

import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.system.DebugSystem;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 */
public class CLApp_optionTests {

    private DebugSystem system = new DebugSystem();


// KV OPTIONS  (STRING)

    @Test
    public void givenKVFlag_requestHelp_expectFlagToBeDocumented() {
        CLApp app = new CLApp(system) {
            public CLOption<String> output;

            {
                this.output = registerOption( "o", "output", "file", "Specify the output directory." );
            }

            protected int run() {
                fail("should not have been run");

                return -1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "",
            "Usage: " + app.getClass().getName(),
            "",
            "Options:",
            "",
            "    -o <file>, --output=<file>",
            "        Specify the output directory.",
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
            "",
            "    --Xclock=<file>",
            "        Share system time via the specified file.  Only used for testing",
            "        purposes.",
            ""
        );
    }

    @Test
    public void givenKVFlagWithLongDescription_requestHelp_expectDescriptionToBeWrapped() {
        CLApp app = new CLApp(system) {
            public CLOption<String> output;

            {
                this.output = registerOption( "o", "output", "file", "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" );
            }

            protected int run() {
                fail("should not have been run");

                return -1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "",
            "Usage: " + app.getClass().getName(),
            "",
            "Options:",
            "",
            "    -o <file>, --output=<file>",
            "        0123456789012345678901234567890123456789012345678901234567890123456789012",
            "        345678901234567890123456789.",
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
            "",
            "    --Xclock=<file>",
            "        Share system time via the specified file.  Only used for testing",
            "        purposes.",
            ""
        );
    }

    @Test
    public void givenKVFlag_invokeAppWithoutSpecifyingFlag_expectDefaultValueToBeUsed() {
        CLApp app = new CLApp(system) {
            public CLOption<String> output;

            {
                this.output = registerOption( "o", "output", "file", "Specify the output directory." );
            }

            protected int run() {
                assertNull( output.getValue() );

                return -1;
            }
        };

        assertEquals( -1, app.runApp() );

        system.assertNoAlerts();
    }

    @Test
    public void givenKVFlag_invokeAppWithShortFlag_expectSuppliedValueToBeUsed() {
        CLApp app = new CLApp(system) {
            public CLOption<String> output;

            {
                this.output = registerOption( "o", "output", "file", "Specify the output directory." );
            }

            protected int run() {
                assertEquals( "foo", output.getValue() );

                return -1;
            }
        };

        assertEquals( -1, app.runApp("-o","foo") );

        system.assertNoAlerts();
    }

    @Test
    public void givenKVFlag_invokeAppWithLongFlag_expectSuppliedValueToBeUsed() {
        CLApp app = new CLApp(system) {
            public CLOption<String> output;

            {
                this.output = registerOption( "o", "output", "file", "Specify the output directory." );
            }

            protected int run() {
                assertEquals( "foo bar", output.getValue() );

                return -1;
            }
        };

        assertEquals( -1, app.runApp("--output=foo bar") );

        system.assertNoAlerts();
    }


// KV OPTIONS  (Non String -- CUSTOM PARSER)

    @Test
    public void givenKVFlagWithNonStringValueAndNullDefault_requestHelp_expectDocumentation() {
        CLApp app = new CLApp(system) {
            public CLOption<Integer> output;

            {
                this.output = registerOption(
                    "n",
                    "line-count",
                    "num",
                    "specify the number of lines to be read in",
                    null,
                    new Function1<String,Integer>() {
                        public Integer invoke( String arg ) {
                            return Integer.parseInt( arg );
                        }
                    }
                );
            }

            protected int run() {
                fail("should not have been run");

                return -1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "",
            "Usage: " + app.getClass().getName(),
            "",
            "Options:",
            "",
            "    -n <num>, --line-count=<num>",
            "        Specify the number of lines to be read in.",
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
            "",
            "    --Xclock=<file>",
            "        Share system time via the specified file.  Only used for testing",
            "        purposes.",
            ""
        );
    }

    @Test
    public void givenKVFlagWithNonStringValueAndNullDefault_invokeAppWithNoOption_expectNullDefaultValueToBeAvailable() {
        CLApp app = new CLApp(system) {
            public CLOption<Integer> output;

            {
                this.output = registerOption(
                    "n",
                    "line-count",
                    "num",
                    "specify the number of lines to be read in",
                    null,
                    new Function1<String,Integer>() {
                        public Integer invoke( String arg ) {
                            return Integer.parseInt( arg );
                        }
                    }
                );
            }

            protected int run() {
                assertNull( output.getValue() );

                return -1;
            }
        };

        assertEquals( -1, app.runApp() );

        system.assertNoAlerts();
    }

    @Test
    public void givenKVFlagWithNonStringValueAndNonNullDefault_invokeAppWithNoOption_expectDefaultValueToBeAvailable() {
        CLApp app = new CLApp(system) {
            public CLOption<Integer> output;

            {
                this.output = registerOption(
                    "n",
                    "line-count",
                    "num",
                    "specify the number of lines to be read in",
                    42,
                    new Function1<String,Integer>() {
                        public Integer invoke( String arg ) {
                            return Integer.parseInt( arg );
                        }
                    }
                );
            }

            protected int run() {
                assertEquals( 42, output.getValue().intValue() );

                return -1;
            }
        };

        assertEquals( -1, app.runApp() );

        system.assertNoAlerts();
    }

    @Test
    public void givenKVFlagWithNonStringValueAndNonNullDefault_requestHelp_expectDefaultValueToBeDocumented() {
        CLApp app = new CLApp(system) {
            public CLOption<Integer> output;

            {
                this.output = registerOption(
                    "n",
                    "line-count",
                    "num",
                    "specify the number of lines to be read in",
                    42,
                    new Function1<String,Integer>() {
                        public Integer invoke( String arg ) {
                            return Integer.parseInt( arg );
                        }
                    }
                );
            }

            protected int run() {
                assertEquals( 42, output.getValue().intValue() );

                return -1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "",
            "Usage: " + app.getClass().getName(),
            "",
            "Options:",
            "",
            "    -n <num>, --line-count=<num>",
            "        Specify the number of lines to be read in.  Defaults to 42.",
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
            "",
            "    --Xclock=<file>",
            "        Share system time via the specified file.  Only used for testing",
            "        purposes.",
            ""
        );
    }

    @Test
    public void givenKVFlagWithNonStringValue_invokeAppWithValueThatParses_expectValueToBeMadeAvailable() {
        CLApp app = new CLApp(system) {
            public CLOption<Integer> output;

            {
                this.output = registerOption(
                    "n",
                    "line-count",
                    "num",
                    "specify the number of lines to be read in",
                    42,
                    new Function1<String,Integer>() {
                        public Integer invoke( String arg ) {
                            return Integer.parseInt( arg );
                        }
                    }
                );
            }

            protected int run() {
                assertEquals( 113, output.getValue().intValue() );

                return -1;
            }
        };

        assertEquals( -1, app.runApp("-n", "113") );

        system.assertNoAlerts();
    }

    @Test
    public void givenKVFlagWithNonStringValue_invokeAppWithValueUsingShortFormAndNoSpaces_expectValueToBeMadeAvailable() {
        CLApp app = new CLApp(system) {
            public CLOption<Integer> output;

            {
                this.output = registerOption(
                    "n",
                    "line-count",
                    "num",
                    "specify the number of lines to be read in",
                    42,
                    new Function1<String,Integer>() {
                        public Integer invoke( String arg ) {
                            return Integer.parseInt( arg );
                        }
                    }
                );
            }

            protected int run() {
                assertEquals( 113, output.getValue().intValue() );

                return -1;
            }
        };

        assertEquals( -1, app.runApp("-n113") );

        system.assertNoAlerts();
    }

    @Test
    public void givenKVFlagWithNonStringValue_invokeAppWithFlagValueThatWillFailToParse_expectError() {
        CLApp app = new CLApp(system) {
            public CLOption<Integer> output;

            {
                this.output = registerOption(
                    "n",
                    "line-count",
                    "num",
                    "specify the number of lines to be read in",
                    42,
                    new Function1<String,Integer>() {
                        public Integer invoke( String arg ) {
                            assertEquals( "113", arg );

                            throw new IllegalArgumentException( "SPLAT" );
                        }
                    }
                );
            }

            protected int run() {
                fail( "will not be called" );

                return -1;
            }
        };

        assertEquals( 1, app.runApp("-n113") );

        system.assertFatalContains( CLException.class, "Invalid value '113' for option 'line-count'" );
        system.assertFatalContains( IllegalArgumentException.class, "SPLAT" );
    }

}
