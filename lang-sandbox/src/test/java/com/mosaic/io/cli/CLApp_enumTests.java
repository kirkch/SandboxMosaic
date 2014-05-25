package com.mosaic.io.cli;

import com.mosaic.lang.system.DebugSystem;
import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;


/**
 *
 */
public class CLApp_enumTests {

    private DebugSystem system = new DebugSystem();

    public static enum ColourEnum {
        RED, GREEN, BLUE
    }


// NO DEFAULT VALUE

    @Test
    public void givenEnumFlag_requestHelp_expectFlagAndValuesToBeDocumented() {
        CLApp app = new CLApp(system) {
            CLOption<ColourEnum> colour;

            {
                colour = registerEnum( "r", "colour", "specify a colour", ColourEnum.class );
            }

            protected int _run() {
                throw new RuntimeException( "_run was not expected to have been called" );
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "Usage: "+app.getClass().getName(),
            "",
            "Options:",
            "",
            "    -r <colour>, --colour=<colour>",
            "        Specify a colour.  The valid values are: Red, Green or Blue.",
            "",
            "    -c <file>, --config=<file>",
            "        Any command line option may be included within a properties file and",
            "        specified here.",
            "",
            "    -?, --help",
            "        Display this usage information.",
            ""
        );
    }

    @Test
    public void givenEnumFlag_invokeAppWithoutSpecifyingFlag_expectDefaultValueToBeUsed() {
        CLApp app = new CLApp(system) {
            CLOption<ColourEnum> colour;

            {
                colour = registerEnum( "r", "colour", "specify a colour", ColourEnum.class );
            }

            protected int _run() {
                assertNull( colour.getValue() );

                return 42;
            }
        };

        assertEquals( 42, app.runApp() );

        system.assertNoAlerts();
    }

    @Test
    public void givenEnumFlag_invokeAppWithShortForm_expectSuppliedValueToBeUsed() {
        CLApp app = new CLApp(system) {
            CLOption<ColourEnum> colour;

            {
                colour = registerEnum( "r", "colour", "specify a colour", ColourEnum.class );
            }

            protected int _run() {
                assertEquals( ColourEnum.GREEN, colour.getValue() );

                return 42;
            }
        };

        assertEquals( 42, app.runApp("-r", "green") );

        system.assertNoAlerts();
    }

    @Test
    public void givenEnumFlag_invokeAppWithLongForm_expectSuppliedValueToBeUsed() {
        CLApp app = new CLApp(system) {
            CLOption<ColourEnum> colour;

            {
                colour = registerEnum( "r", "colour", "specify a colour", ColourEnum.class );
            }

            protected int _run() {
                assertEquals( ColourEnum.RED, colour.getValue() );

                return 42;
            }
        };

        assertEquals( 42, app.runApp("--colour", "red") );

        system.assertNoAlerts();
    }

    @Test
    public void givenEnumFlag_invokeAppWithUnknownEnumValue_expectError() {
        CLApp app = new CLApp(system) {
            CLOption<ColourEnum> colour;

            {
                colour = registerEnum( "r", "colour", "specify a colour", ColourEnum.class );
            }

            protected int _run() {
                throw new RuntimeException( "_run was not expected to have been called" );
            }
        };

        assertEquals( 1, app.runApp("--colour", "purple") );

        system.assertStandardErrorEquals( "Invalid value 'purple' for option 'colour'" );
        system.assertFatalContains( "Invalid value 'purple' for option 'colour'" );
    }


// WITH DEFAULT VALUE


    @Test
    public void givenEnumFlagWithDefaultValue_requestHelp_expectFlagAndValuesToBeDocumented() {
        CLApp app = new CLApp(system) {
            CLOption<ColourEnum> colour;

            {
                colour = registerEnum( "r", "colour", "specify a colour", ColourEnum.class, ColourEnum.RED );
            }

            protected int _run() {
                throw new RuntimeException( "_run was not expected to have been called" );
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "Usage: "+app.getClass().getName(),
            "",
            "Options:",
            "",
            "    -r <colour>, --colour=<colour>",
            "        Specify a colour.  The valid values are: Red, Green or Blue.  Defaults to",
            "        Red.",
            "",
            "    -c <file>, --config=<file>",
            "        Any command line option may be included within a properties file and",
            "        specified here.",
            "",
            "    -?, --help",
            "        Display this usage information.",
            ""
        );
    }

    @Test
    public void givenEnumFlagWithDefaultValue_invokeAppWithoutSpecifyingFlag_expectDefaultValueToBeUsed() {
        CLApp app = new CLApp(system) {
            CLOption<ColourEnum> colour;

            {
                colour = registerEnum( "r", "colour", "specify a colour", ColourEnum.class, ColourEnum.BLUE );
            }

            protected int _run() {
                assertEquals( ColourEnum.BLUE, colour.getValue() );

                return 42;
            }
        };

        assertEquals( 42, app.runApp() );

        system.assertNoAlerts();
    }


}
