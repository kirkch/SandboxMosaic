package com.mosaic.io.cli;

import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.time.DTM;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;


/**
 *
 */
public class CLAppTest {

    private DebugSystem system = new DebugSystem();


// BASIC INVOCATION TESTS

    @Test
    public void givenAppWithNoFlagsNoArgsNoDescriptionAndReturnsZeroWhenRun_invoke_expectZero() {
        CLApp2 app = new CLApp2(system) {
            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp() );

        system.assertNoErrorsOrFatals();
    }

    @Test
    public void givenAppWithNoFlagsNoArgsNoDescriptionAndReturnsOneWhenRun_invoke_expectOne() {
        CLApp2 app = new CLApp2(system) {
            protected int _run() {
                return 1;
            }
        };

        assertEquals( 1, app.runApp() );

        system.assertNoErrorsOrFatals();
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

        system.assertNoErrorsOrFatals();
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

        system.assertNoErrorsOrFatals();
    }


// STARTUP/SHUTDOWN EVENTS

    @Test
    public void onStartUp_expectSetupMethodToBeInvoked() {
        final AtomicBoolean wasSetupInvoked = new AtomicBoolean( false );

        CLApp2 app = new CLApp2(system) {
            protected void setUpCallback() {
                wasSetupInvoked.set( true );
            }

            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp() );
        assertTrue( "setUp method was not invoked", wasSetupInvoked.get() );

        system.assertNoErrorsOrFatals();
    }

    @Test
    public void afterAppHasCompleted_expectTearDownMethodToBeCalled() {
        final AtomicBoolean wasTearDownInvoked = new AtomicBoolean( false );

        CLApp2 app = new CLApp2(system) {
            protected void tearDownCallback() {
                wasTearDownInvoked.set( true );
            }

            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp() );
        assertTrue( "tearDown method was not invoked", wasTearDownInvoked.get() );

        system.assertNoErrorsOrFatals();
    }

    @Test
    public void afterAppHasErrored_expectTearDownMethodToBeCalled() {
        final AtomicBoolean wasTearDownInvoked = new AtomicBoolean( false );

        CLApp2 app = new CLApp2(system) {
            protected void tearDownCallback() {
                wasTearDownInvoked.set( true );
            }

            protected int _run() {
                throw new RuntimeException( "intended exception" );
            }
        };

        assertEquals( 1, app.runApp() );
        assertTrue( "tearDown method was not invoked", wasTearDownInvoked.get() );
    }

    @Test
    public void onStartUp_expectAuditMessageSpecifyingWhenAppWasStarted() {
        system.clock.fixCurrentDTM( new DTM(2020,1,1, 10,0,0) );

        CLApp2 app = new CLApp2(system) {
            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp() );

        system.assertAudit( app.getName() + " started at 10:00:00 UTC on 2020/01/01" );
    }

    @Test
    public void onStartUp_expectArgumentsUsedToBeEchoedToTheInfoLog() {
        system.clock.fixCurrentDTM( new DTM(2020,1,1, 10,0,0) );

        CLApp2 app = new CLApp2(system) {
            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp() );

        system.assertAudit( app.getName() + " started at 10:00:00 UTC on 2020/01/01" );
    }

// alert( FATAL|ERROR|WARN,
// audit( USER|OPS|DEV,


// fatal - dead                                               fatalLog logFatal
// error - human action                                       errorLog logError
// warn  - something went wrong; recovered                    warnLog  logWarning
// audit - informative to the user (what is the app doing)    userLog  auditUser
// info  - informative to dev/ops                             opsLog   auditOps
// debug - developer info                                     devLog   auditDev
//
// onStartUp_expectJavaVersionToBeSentToDebugLog
// onStartUp_expectClasspathToBeSentToDebugLog
// onStartUp_expectSettingsToBeEchoedToTheDebugLog

// afterAppHasCompleted_expectUpTimeToBePrintedToAudit

//


// givenAppThatTakesFlagsOptionsAndArguments_invokeWithAllWithOptionsAndFlagsBeforeArguments_expectValuesToBeSet
// givenAppThatTakesFlagsOptionsAndArguments_invokeWithAllWithOptionsAndFlagsAfterArguments_expectValuesToBeSet

}