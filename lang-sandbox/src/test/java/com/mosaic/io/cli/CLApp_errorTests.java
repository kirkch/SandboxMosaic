package com.mosaic.io.cli;

import com.mosaic.lang.system.DebugSystem;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 *
 */
public class CLApp_errorTests {

    private DebugSystem system = new DebugSystem();


// UNCAUGHT EXCEPTIONS

    @Test
    public void givenAppThatThrowsExceptionWhenRun_runApp_expectError() {
        CLApp2 app = new CLApp2(system) {
            protected int _run() {
                throw new RuntimeException( "whoops" );
            }
        };

        assertEquals( 1, app.runApp() );

        system.assertStandardErrorEquals( app.getName() + " errored unexpectedly and was aborted. The error was 'RuntimeException:whoops'." );
        system.assertFatalContains( RuntimeException.class, "whoops" );
    }


// MALFORMED SHORT NAME

    @Test
    public void givenFlagWithMultipleLettersForShortForm_expectExceptionAsTheShortFormMustBeSingleLetters() {
        try {
            new CLApp2(system) {
                public CLOption<String> source;

                {
                    this.source = registerOption( "so", "source", "file", "the file to be copied" );
                }

                protected int _run() {
                    return 1;
                }
            };

            fail( "expected exception" );
        } catch ( IllegalArgumentException ex ) {
            assertEquals( "'so' is not a valid short name, short names can only be one character long", ex.getMessage() );
        }
    }




// DUPLICATE NAME TESTS

    @Test
    public void givenDuplicateLongOptionNames_expectException() {
        try {
            new CLApp2(system) {
                public CLOption<String> source;
                public CLOption<String> destination;

                {
                    setDescription( "this is a test app.  Enjoy" );

                    this.source      = registerOption( "s", "source", "file", "the file to be copied" );
                    this.destination = registerOption( "d", "source", "file", "the location to copy the file to" );
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
    public void givenDuplicateShortOptionNames_expectException() {
        try {
            new CLApp2(system) {
                public CLOption<String> source;
                public CLOption<String> destination;

                {
                    setDescription( "this is a test app.  Enjoy" );

                    this.source      = registerOption( "s", "source", "file", "the file to be copied" );
                    this.destination = registerOption( "s", "destination", "file", "the location to copy the file to" );
                }

                protected int _run() {
                    return 1;
                }
            };

            fail( "expected exception" );
        } catch ( IllegalArgumentException ex ) {
            assertEquals( "'s' has been declared twice", ex.getMessage() );
        }
    }

    @Test
    public void givenDuplicateLongFlagNames_expectException() {
        try {
            new CLApp2(system) {
                public CLOption<Boolean> flag1;
                public CLOption<Boolean> flag2;

                {
                    setDescription( "this is a test app.  Enjoy" );

                    this.flag1 = registerFlag( "a", "flag", "the file to be copied" );
                    this.flag2 = registerFlag( "b", "flag", "the location to copy the file to" );
                }

                protected int _run() {
                    return 1;
                }
            };

            fail( "expected exception" );
        } catch ( IllegalArgumentException ex ) {
            assertEquals( "'flag' has been declared twice", ex.getMessage() );
        }
    }

    @Test
    public void givenDuplicateShortFlagNames_expectException() {
        try {
            new CLApp2(system) {
                public CLOption<Boolean> flag1;
                public CLOption<Boolean> flag2;

                {
                    setDescription( "this is a test app.  Enjoy" );

                    this.flag1 = registerFlag( "a", "flag", "desc1" );
                    this.flag2 = registerFlag( "a", "auto", "desc2" );
                }

                protected int _run() {
                    return 1;
                }
            };

            fail( "expected exception" );
        } catch ( IllegalArgumentException ex ) {
            assertEquals( "'a' has been declared twice", ex.getMessage() );
        }
    }

    @Test
    public void givenDuplicateShortFlagNameThatClashesWithShortOptionName_expectException() {
        try {
            new CLApp2(system) {
                public CLOption<Boolean> flag1;
                public CLOption<String>  option1;

                {
                    setDescription( "this is a test app.  Enjoy" );

                    this.flag1   = registerFlag( "a", "flag", "desc1" );
                    this.option1 = registerOption( "a", "auto", "file", "desc2" );
                }

                protected int _run() {
                    return 1;
                }
            };

            fail( "expected exception" );
        } catch ( IllegalArgumentException ex ) {
            assertEquals( "'a' has been declared twice", ex.getMessage() );
        }
    }

    @Test
    public void givenDuplicateLongFlagNameThatClashesWithShortOptionName_expectException() {
        try {
            new CLApp2(system) {
                public CLOption<Boolean> flag1;
                public CLOption<String>  option1;

                {
                    setDescription( "this is a test app.  Enjoy" );

                    this.flag1   = registerFlag( "a", "r", "desc1" );
                    this.option1 = registerOption( "r", "f", "file", "desc2" );
                }

                protected int _run() {
                    return 1;
                }
            };

            fail( "expected exception" );
        } catch ( IllegalArgumentException ex ) {
            assertEquals( "'r' has been declared twice", ex.getMessage() );
        }
    }

    @Test
    public void givenDuplicateShortFlagNameThatClashesWithLongOptionName_expectException() {
        try {
            new CLApp2(system) {
                public CLOption<Boolean> flag1;
                public CLOption<String>  option1;

                {
                    setDescription( "this is a test app.  Enjoy" );

                    this.flag1   = registerFlag( "a", "flag", "desc1" );
                    this.option1 = registerOption( "b", "a", "file", "desc2" );
                }

                protected int _run() {
                    return 1;
                }
            };

            fail( "expected exception" );
        } catch ( IllegalArgumentException ex ) {
            assertEquals( "'a' has been declared twice", ex.getMessage() );
        }
    }

    @Test
    public void givenDuplicateLongFlagNameThatClashesWithLongOptionName_expectException() {
        try {
            new CLApp2(system) {
                public CLOption<Boolean> flag1;
                public CLOption<String>  option1;

                {
                    setDescription( "this is a test app.  Enjoy" );

                    this.flag1   = registerFlag( "a", "flag", "desc1" );
                    this.option1 = registerOption( "b", "flag", "file", "desc2" );
                }

                protected int _run() {
                    return 1;
                }
            };

            fail( "expected exception" );
        } catch ( IllegalArgumentException ex ) {
            assertEquals( "'flag' has been declared twice", ex.getMessage() );
        }
    }

    @Test
    public void supplyUnknownShortNameFlag_expectFatalErrorAsPerUnixConvention() {
        CLApp2 app = new CLApp2(system) {
            public CLOption<Boolean> flag1;
            public CLOption<String>  option1;

            {
                setDescription( "this is a test app.  Enjoy" );

                this.flag1   = registerFlag( "a", "flag", "desc1" );
                this.option1 = registerOption( "b", "option", "file", "desc2" );
            }

            protected int _run() {
                return 0;
            }
        };

        assertEquals( 1, app.runApp("-f") );

        system.assertFatalContains( "Unknown flag '-f'.  Run with --help for more information." );
        system.assertDevAuditContains( CLException.class, "Unknown flag '-f'.  Run with --help for more information." );
    }

    @Test
    public void supplyUnknownLongNameFlag_expectFatalErrorAsPerUnixConvention() {
        CLApp2 app = new CLApp2(system) {
            public CLOption<Boolean> flag1;
            public CLOption<String>  option1;

            {
                setDescription( "this is a test app.  Enjoy" );

                this.flag1   = registerFlag( "a", "flag", "desc1" );
                this.option1 = registerOption( "b", "option", "file", "desc2" );
            }

            protected int _run() {
                return 0;
            }
        };

        assertEquals( 1, app.runApp("--long-name") );

        system.assertFatalContains( "Unknown flag '--long-name'.  Run with --help for more information." );
        system.assertDevAuditContains( CLException.class, "Unknown flag '--long-name'.  Run with --help for more information." );
    }

}
