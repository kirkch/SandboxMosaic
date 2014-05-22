package com.mosaic.io.cli;

import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.time.DTM;
import com.mosaic.lang.time.Duration;
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

        system.assertNoAlerts();
    }

    @Test
    public void givenAppWithNoFlagsNoArgsNoDescriptionAndReturnsOneWhenRun_invoke_expectOne() {
        CLApp2 app = new CLApp2(system) {
            protected int _run() {
                return 1;
            }
        };

        assertEquals( 1, app.runApp() );

        system.assertNoAlerts();
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
            "    -?, --help",
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
            "    -?, --help",
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

        system.assertNoAlerts();
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

        system.assertNoAlerts();
    }

    @Test
    public void givenAppThatTakesFlagsOptionsAndArguments_invokeWithAllWithOptionsAndFlagsBeforeArguments_expectValuesToBeSet() {
        CLApp2 app = new CLApp2(system) {
            public CLOption<Boolean> flag1;
            public CLOption<String>  option1;
            public CLArgument<String>  arg1;


            {
                this.flag1   = registerFlag( "f", "flag1", "description 1" );
                this.option1 = registerOption( "o", "option1", "name", "description 2" );
                this.arg1    = registerArgument( "arg1", "description 3" );
            }

            protected int _run() {
                assertTrue( "-f was set but the value did not make it through", flag1.getValue() );
                assertEquals( "-o was set but the value did not make it through", "abc", option1.getValue() );
                assertEquals( "arg1 was not supplied", "foo bar", arg1.getValue() );

                return 42;
            }
        };

        assertEquals( 42, app.runApp("-f", "-o", "abc", "foo bar") );

        system.assertNoAlerts();
    }

    @Test
    public void givenAppThatTakesFlagsOptionsAndArguments_invokeWithAllWithOptionsAndFlagsAfterArguments_expectValuesToBeSet() {
        CLApp2 app = new CLApp2(system) {
            public CLOption<Boolean> flag1;
            public CLOption<String>  option1;
            public CLArgument<String>  arg1;


            {
                this.flag1   = registerFlag( "f", "flag1", "description 1" );
                this.option1 = registerOption( "o", "option1", "name", "description 2" );
                this.arg1    = registerArgument( "arg1", "description 3" );
            }

            protected int _run() {
                assertTrue( "-f was set but the value did not make it through", flag1.getValue() );
                assertEquals( "-o was set but the value did not make it through", "abc", option1.getValue() );
                assertEquals( "arg1 was not supplied", "foo bar", arg1.getValue() );

                return 42;
            }
        };

        assertEquals( 42, app.runApp("foo bar", "-f", "-o", "abc") );

        system.assertNoAlerts();
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

        system.assertNoAlerts();
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

        system.assertNoAlerts();
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

        system.assertOpsAuditContains( "Started at 10:00:00 UTC on 2020/01/01" );
    }



    @Test
    public void afterAppHasCompleted_expectUpTimeToBePrintedToOpsLog() {
        system.clock.fixCurrentDTM( new DTM(2020,1,1, 10,0,0) );

        CLApp2 app = new CLApp2(system) {
            protected int _run() {
                system.clock.add( Duration.minutes(2) );

                return 0;
            }
        };

        assertEquals( 0, app.runApp() );

        system.assertOpsAuditContains( "Ran for 2m.  Ended at 10:02:00 UTC on 2020/01/01." );
    }

    @Test
    public void onStartUp_expectArgumentsUsedToBeEchoedToTheOpsLog() {
        system.clock.fixCurrentDTM( new DTM(2020,1,1, 10,0,0) );

        CLApp2 app = new CLApp2(system) {
            {
                registerFlag( "d", "flag1", "description" );
                registerFlag( "f", "flag2", "description" );
                registerFlag( "a", "flag3", "description" );
                registerFlag( "b", "flag4", "description" );
                registerFlag( "c", "flag5", "description" );
                registerFlag( "e", "long-form", "description" );

                registerArgument( "arg1", "description" );
                registerArgument( "arg2", "description" );
            }

            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp("-d", "-f", "-abc", "--long-form", "arg1", "arg2") );

        system.assertOpsAuditContains( app.getName() + " -d -f -a -b -c --long-form 'arg1' 'arg2'" );
    }

    @Test
    public void onStartUp_expectJavaVersionToBeSentToOpsLog() {
        system.clock.fixCurrentDTM( new DTM(2020,1,1, 10,0,0) );

        CLApp2 app = new CLApp2(system) {
            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp() );

        system.assertOpsAuditContains(
            "Java: " + System.getProperty( "java.runtime.version" ) + " (" +
                System.getProperty( "java.vm.name" ) + " " + System.getProperty( "java.vm.vendor" ) + ")"
        );
    }

    @Test
    public void onStartUp_expectClasspathToBeSentToOpsLog() {
        system.clock.fixCurrentDTM( new DTM(2020,1,1, 10,0,0) );

        CLApp2 app = new CLApp2(system) {
            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp() );

        system.assertOpsAuditContains( "Classpath: " + System.getProperty( "java.class.path" ) );
    }

    @Test
    public void onStartUp_expectLibraryPathToBeSentToOpsLog() {
        system.clock.fixCurrentDTM( new DTM(2020,1,1, 10,0,0) );

        CLApp2 app = new CLApp2(system) {
            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp() );

        system.assertOpsAuditContains( "Library Path: " + System.getProperty( "java.library.path" ) );
    }



//
//
// onStartUp_expectSettingsToBeEchoedToTheOpsLog

//

//



}
