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
        CLApp2 app = new CLApp2(system) {
            public CLOption<String> output;

            {
                this.output = registerOption( "o", "output", "file", "Specify the output directory." );
            }

            protected int _run() {
                fail("should not have been run");

                return -1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "Usage: " + app.getClass().getName(),
            "",
            "Options:",
            "",
            "    -o <file>, --output=<file>",
            "        Specify the output directory.",
            "",
            "    --help",
            "        Display this usage information.",
            ""
        );
    }

    @Test
    public void givenKVFlagWithLongDescription_requestHelp_expectDescriptionToBeWrapped() {
        CLApp2 app = new CLApp2(system) {
            public CLOption<String> output;

            {
                this.output = registerOption( "o", "output", "file", "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" );
            }

            protected int _run() {
                fail("should not have been run");

                return -1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "Usage: " + app.getClass().getName(),
            "",
            "Options:",
            "",
            "    -o <file>, --output=<file>",
            "        0123456789012345678901234567890123456789012345678901234567890123456789012",
            "        345678901234567890123456789.",
            "",
            "    --help",
            "        Display this usage information.",
            ""
        );
    }

    @Test
    public void givenKVFlag_invokeAppWithoutSpecifyingFlag_expectDefaultValueToBeUsed() {
        CLApp2 app = new CLApp2(system) {
            public CLOption<String> output;

            {
                this.output = registerOption( "o", "output", "file", "Specify the output directory." );
            }

            protected int _run() {
                assertNull( output.getValue() );

                return -1;
            }
        };

        assertEquals( -1, app.runApp() );

        system.assertNoAlerts();
    }

    @Test
    public void givenKVFlag_invokeAppWithShortFlag_expectSuppliedValueToBeUsed() {
        CLApp2 app = new CLApp2(system) {
            public CLOption<String> output;

            {
                this.output = registerOption( "o", "output", "file", "Specify the output directory." );
            }

            protected int _run() {
                assertEquals( "foo", output.getValue() );

                return -1;
            }
        };

        assertEquals( -1, app.runApp("-o","foo") );

        system.assertNoAlerts();
    }

    @Test
    public void givenKVFlag_invokeAppWithLongFlag_expectSuppliedValueToBeUsed() {
        CLApp2 app = new CLApp2(system) {
            public CLOption<String> output;

            {
                this.output = registerOption( "o", "output", "file", "Specify the output directory." );
            }

            protected int _run() {
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
        CLApp2 app = new CLApp2(system) {
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

            protected int _run() {
                fail("should not have been run");

                return -1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "Usage: " + app.getClass().getName(),
            "",
            "Options:",
            "",
            "    -n <num>, --line-count=<num>",
            "        Specify the number of lines to be read in.",
            "",
            "    --help",
            "        Display this usage information.",
            ""
        );
    }

    @Test
    public void givenKVFlagWithNonStringValueAndNullDefault_invokeAppWithNoOption_expectNullDefaultValueToBeAvailable() {
        CLApp2 app = new CLApp2(system) {
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

            protected int _run() {
                assertNull( output.getValue() );

                return -1;
            }
        };

        assertEquals( -1, app.runApp() );

        system.assertNoAlerts();
    }

    @Test
    public void givenKVFlagWithNonStringValueAndNonNullDefault_invokeAppWithNoOption_expectDefaultValueToBeAvailable() {
        CLApp2 app = new CLApp2(system) {
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

            protected int _run() {
                assertEquals( 42, output.getValue().intValue() );

                return -1;
            }
        };

        assertEquals( -1, app.runApp() );

        system.assertNoAlerts();
    }

    @Test
    public void givenKVFlagWithNonStringValueAndNonNullDefault_requestHelp_expectDefaultValueToBeDocumented() {
        CLApp2 app = new CLApp2(system) {
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

            protected int _run() {
                assertEquals( 42, output.getValue().intValue() );

                return -1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "Usage: " + app.getClass().getName(),
            "",
            "Options:",
            "",
            "    -n <num>, --line-count=<num>",
            "        Specify the number of lines to be read in. Defaults to 42.",
            "",
            "    --help",
            "        Display this usage information.",
            ""
        );
    }

    @Test
    public void givenKVFlagWithNonStringValue_invokeAppWithValueThatParses_expectValueToBeMadeAvailable() {
        CLApp2 app = new CLApp2(system) {
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

            protected int _run() {
                assertEquals( 113, output.getValue().intValue() );

                return -1;
            }
        };

        assertEquals( -1, app.runApp("-n", "113") );

        system.assertNoAlerts();
    }

    @Test
    public void givenKVFlagWithNonStringValue_invokeAppWithValueUsingShortFormAndNoSpaces_expectValueToBeMadeAvailable() {
        CLApp2 app = new CLApp2(system) {
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

            protected int _run() {
                assertEquals( 113, output.getValue().intValue() );

                return -1;
            }
        };

        assertEquals( -1, app.runApp("-n113") );

        system.assertNoAlerts();
    }

    @Test
    public void givenKVFlagWithNonStringValue_invokeAppWithFlagValueThatWillFailToParse_expectError() {
        CLApp2 app = new CLApp2(system) {
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

            protected int _run() {
                fail( "will not be called" );

                return -1;
            }
        };

        assertEquals( 1, app.runApp("-n113") );

        system.assertDevAuditContains( CLException.class, "Invalid value '113' for option 'line-count'" );
        system.assertDevAuditContains( IllegalArgumentException.class, "SPLAT" );

        system.assertFatalContains( "Invalid value '113' for option 'line-count'" );
    }

}
