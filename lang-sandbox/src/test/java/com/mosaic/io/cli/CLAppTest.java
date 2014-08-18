package com.mosaic.io.cli;

import com.mosaic.lang.StartStopMixin;
import com.mosaic.lang.functional.Function0;
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
        CLApp app = new CLApp(system) {
            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp() );

        system.assertNoAlerts();
    }

    @Test
    public void givenAppWithNoFlagsNoArgsNoDescriptionAndReturnsOneWhenRun_invoke_expectOne() {
        CLApp app = new CLApp(system) {
            protected int _run() {
                return 1;
            }
        };

        assertEquals( 1, app.runApp() );

        system.assertNoAlerts();
    }

    @Test
    public void givenAppWithNoFlagsNoArgsNoDescriptionAndReturnsOneWhenRun_requestHelp_expectDefaultHelp() {
        CLApp app = new CLApp(system) {
            protected int _run() {
                return 1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "",
            "Usage: "+app.getClass().getName(),
            "",
            "Options:",
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
    public void givenAppWithDescriptionOnly_requestHelp_expectDescriptionInHelp() {
        CLApp app = new CLApp(system) {
            {
                setDescription( "This is a test app.  Enjoy." );
            }

            protected int _run() {
                return 1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "",
            "Usage: "+app.getClass().getName(),
            "",
            "    This is a test app.  Enjoy.",
            "",
            "Options:",
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


// MULTIPLE FLAGS OPTIONS ARG TESTS


    @Test
    public void givenOneOptionAndOneFlag_supplyBothSeparately_expectValues() {
        CLApp app = new CLApp(system) {
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
        CLApp app = new CLApp(system) {
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
        CLApp app = new CLApp(system) {
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
        CLApp app = new CLApp(system) {
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

    private static class StartStopMock extends StartStopMixin<StartStopMock> {
        public StartStopMock() {
            super( "mock" );
        }

        protected void doStart() {}
        protected void doStop() {}
    }

    @Test
    public void onStartup_expectChainedServicesToBeStartedFirst() {

        final StartStopMock service = new StartStopMock();

        CLApp app = new CLApp(system) {
            Function0<StartStopMock> s = registerService( new Function0<StartStopMock>() {
                public StartStopMock invoke() {
                    return service;
                }
            } );


            protected int _run() {
                assertTrue( "chained service should be running", s.invoke().isRunning() );

                return 0;
            }
        };

        runAppAndAssertReturnCode( app, 0 );
        assertFalse( "chained service should have stopped", service.isRunning() );

        system.assertNoAlerts();
    }

    @Test
    public void afterAppHasCompleted_expectAfterShutdownMethodToBeCalled() {
        final AtomicBoolean wasTearDownInvoked = new AtomicBoolean( false );

        CLApp app = new CLApp(system) {
            @Override
            protected void afterShutdown() {
                wasTearDownInvoked.set( true );
            }

            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp() );
        assertTrue( "afterShutdown method was not invoked", wasTearDownInvoked.get() );

        system.assertNoAlerts();
    }

    @Test
    public void afterAppHasCompleted_expectBeforeShutdownMethodToBeCalled() {
        final AtomicBoolean wasTearDownInvoked = new AtomicBoolean( false );

        CLApp app = new CLApp(system) {
            protected void beforeShutdown() {
                wasTearDownInvoked.set( true );
            }

            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp() );
        assertTrue( "afterShutdown method was not invoked", wasTearDownInvoked.get() );

        system.assertNoAlerts();
    }

    @Test
    public void afterAppHasErrored_expectBeforeShutdownMethodToBeCalled() {
        final AtomicBoolean wasTearDownInvoked = new AtomicBoolean( false );

        CLApp app = new CLApp(system) {
            protected void beforeShutdown() {
                wasTearDownInvoked.set( true );
            }

            protected int _run() {
                throw new RuntimeException( "intended exception" );
            }
        };

        assertEquals( 1, app.runApp() );
        assertTrue( "beforeShutdown method was not invoked", wasTearDownInvoked.get() );
    }

    @Test
    public void afterAppHasErrored_expectAfterShutdownMethodToBeCalled() {
        final AtomicBoolean wasTearDownInvoked = new AtomicBoolean( false );

        CLApp app = new CLApp(system) {
            protected void afterShutdown() {
                wasTearDownInvoked.set( true );
            }

            protected int _run() {
                throw new RuntimeException( "intended exception" );
            }
        };

        assertEquals( 1, app.runApp() );
        assertTrue( "afterShutdown method was not invoked", wasTearDownInvoked.get() );
    }

    @Test
    public void onStartUp_expectAuditMessageSpecifyingWhenAppWasStarted() {
        system.clock.fixCurrentDTM( new DTM(2020,1,1, 10,0,0) );

        CLApp app = new CLApp(system) {
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

        CLApp app = new CLApp(system) {
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

        CLApp app = new CLApp(system) {
            {
                registerFlag( "d", "flag1", "description" );
                registerFlag( "f", "flag2", "description" );
                registerFlag( "a", "flag3", "description" );
                registerFlag( "b", "flag4", "description" );
                registerFlag( "r", "flag5", "description" );
                registerFlag( "e", "long-form", "description" );

                registerArgument( "arg1", "description" );
                registerArgument( "arg2", "description" );
            }

            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp("-d", "-f", "-abr", "--long-form", "arg1", "arg2") );

        system.assertOpsAuditContains( app.getName() + " -d -f -a -b -r --long-form 'arg1' 'arg2'" );
    }

    @Test
    public void onStartUp_expectJavaVersionToBeSentToOpsLog() {
        system.clock.fixCurrentDTM( new DTM(2020,1,1, 10,0,0) );

        CLApp app = new CLApp(system) {
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

        CLApp app = new CLApp(system) {
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

        CLApp app = new CLApp(system) {
            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp() );

        system.assertOpsAuditContains( "Library Path: " + System.getProperty( "java.library.path" ) );
    }

    @Test
    public void onStartUp_expectAllCLParameterValuesToBeSentToTheOpsLog() {
        system.clock.fixCurrentDTM( new DTM(2020,1,1, 10,0,0) );

        CLApp app = new CLApp(system) {
            {
                registerFlag( "a", "flag1", "description" );
                registerOption( "b", "flag2", "flag", "description" );
            }

            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp("--flag2=f2") );

        system.assertOpsAuditContains( "Config:" );
        system.assertOpsAuditContains( "  flag1=false" );
        system.assertOpsAuditContains( "  flag2=f2" );
    }


    static void runAppAndAssertReturnCode( CLApp app, int expectedRC, String...args ) {
        int actualRC = app.runApp( args );

        if ( expectedRC != actualRC ) {
            ((DebugSystem)app.system).dumpLog();

            fail("App returned " + actualRC + ", expected " + expectedRC );
        }
    }
}
