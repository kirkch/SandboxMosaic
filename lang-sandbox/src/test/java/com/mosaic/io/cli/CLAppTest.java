package com.mosaic.io.cli;

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
            "Options:",
            "",
            "    --help",
            "        Display this usage information.",
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
            "Options:",
            "",
            "    --help",
            "        Display this usage information.",
            ""
        );
    }


// MULTIPLE FLAGS OPTIONS ARG TESTS


    @Test
    public void givenOneOptionAndOneFlag_supplyBothSeparately_expectValues() {
        CLApp2 app = new CLApp2(system) {
            public CLOption<Boolean> flag1;
            public CLOption<String>  option1;

            {
                this.flag1   = registerFlag( "f", "flag1", "description 1" );
                this.option1 = registerOption( "o", "option1", "name", "description 2" );
            }

            protected int _run() {
                assertTrue( "-f was set but the value did not make it through", flag1.getValue() );
                assertEquals( "-o was set but the value did not make it through", "abc", option1.getValue() );

                return 42;
            }
        };

        assertEquals( 42, app.runApp("-o", "abc", "-f") );

        system.assertNoOutput();
    }

    @Test
    public void givenOneOptionAndOneFlag_supplyBothConcatenatedTogetherUsingShortForm_expectValues() {
        CLApp2 app = new CLApp2(system) {
            public CLOption<Boolean> flag1;
            public CLOption<String>  option1;

            {
                this.flag1   = registerFlag( "f", "flag1", "description 1" );
                this.option1 = registerOption( "o", "option1", "name", "description 2" );
            }

            protected int _run() {
                assertTrue( "-f was set but the value did not make it through", flag1.getValue() );
                assertEquals( "-o was set but the value did not make it through", "abc", option1.getValue() );

                return 42;
            }
        };

        assertEquals( 42, app.runApp("-foabc") );

        system.assertNoOutput();
    }

}
