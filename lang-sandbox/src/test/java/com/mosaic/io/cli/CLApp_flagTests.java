package com.mosaic.io.cli;

import com.mosaic.lang.system.DebugSystem;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 *
 */
public class CLApp_flagTests {

    private DebugSystem system = new DebugSystem();


// BOOLEAN OPTIONS  (AKA FLAG)

    @Test
    public void givenBooleanFlag_requestHelp_expectFlagToBeDocumented() {
        CLApp app = new CLApp(system) {
            public CLOption<Boolean> debug;

            {
                this.debug = registerFlag( "d", "debug", "Enable debug mode." );
            }

            protected int _run() {
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
            "    -d, --debug",
            "        Enable debug mode.",
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
    public void givenBooleanFlag_supplyDescriptionInLowerCaseWithNoFullStop_expectThemToBeAdded() {
        CLApp app = new CLApp(system) {
            public CLOption<Boolean> debug;

            {
                this.debug = registerFlag( "d", "debug", "enable debug mode" );
            }

            protected int _run() {
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
            "    -d, --debug",
            "        Enable debug mode.",
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
    public void givenBooleanFlag_invokeAppWithoutSpecifyingFlag_expectFlagToBeFalse() {
        CLApp app = new CLApp(system) {
            public CLOption<Boolean> debug;

            {
                this.debug = registerFlag( "d", "debug", "Enable debug mode." );
            }

            protected int _run() {
                assertFalse( debug.getValue() );

                return 42;
            }
        };

        assertEquals( 42, app.runApp() );

        system.assertNoAlerts();
    }

    @Test
    public void givenBooleanFlag_invokeAppWithShortVersionOfFlag_expectFlagToBeTrue() {
        CLApp app = new CLApp(system) {
            public CLOption<Boolean> debug;

            {
                this.debug = registerFlag( "d", "debug", "Enable debug mode." );
            }

            protected int _run() {
                assertTrue( "-d was not supplied", debug.getValue() );

                return 42;
            }
        };

        assertEquals( 42, app.runApp("-d") );

        system.assertNoAlerts();
    }

    @Test
    public void givenBooleanFlag_invokeAppWithLongVersionOfFlag_expectFlagToBeTrue() {
        CLApp app = new CLApp(system) {
            public CLOption<Boolean> debug;

            {
                this.debug = registerFlag( "d", "debug", "Enable debug mode." );
            }

            protected int _run() {
                assertTrue( "-d was not supplied", debug.getValue() );

                return 42;
            }
        };

        assertEquals( 42, app.runApp("--debug") );

        system.assertNoAlerts();
    }


// MULTIPLE FLAGS

    @Test
    public void givenThreeFlags_supplyTwoSeparatedBySpaced_expectThoseToFlagsToBeTrueAndTheThirdFalse() {
        CLApp app = new CLApp(system) {
            public CLOption<Boolean> flag1;
            public CLOption<Boolean> flag2;
            public CLOption<Boolean> flag3;

            {
                this.flag1 = registerFlag( "a", "flag1", "description 1" );
                this.flag2 = registerFlag( "b", "flag2", "description 2" );
                this.flag3 = registerFlag( "k", "flag3", "description 3" );
            }

            protected int _run() {
                assertTrue( "-a was set but the value did not make it through", flag1.getValue() );
                assertTrue( "-k was set but the value did not make it through", flag3.getValue() );
                assertFalse( flag2.getValue() );

                return 42;
            }
        };

        assertEquals( 42, app.runApp("-k", "-a") );

        system.assertNoAlerts();
    }

    @Test
    public void givenThreeFlags_supplyTwoCombinedTogether_expectThoseToFlagsToBeTrueAndTheThirdFalse() {
        CLApp app = new CLApp(system) {
            public CLOption<Boolean> flag1;
            public CLOption<Boolean> flag2;
            public CLOption<Boolean> flag3;

            {
                this.flag1 = registerFlag( "a", "flag1", "description 1" );
                this.flag2 = registerFlag( "b", "flag2", "description 2" );
                this.flag3 = registerFlag( "k", "flag3", "description 3" );
            }

            protected int _run() {
                assertTrue( "-a was set but the value did not make it through", flag1.getValue() );
                assertTrue( "-k was set but the value did not make it through", flag3.getValue() );
                assertFalse( flag2.getValue() );

                return 42;
            }
        };

        assertEquals( 42, app.runApp("-ka") );

        system.assertNoAlerts();
    }


}
