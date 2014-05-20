package com.mosaic.io.cli;

import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
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

        system.assertNoOutput();
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

        system.assertNoOutput();
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

        system.assertNoOutput();
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

        system.assertNoOutput();
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

        system.assertNoOutput();
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

        system.assertNoOutput();
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

        system.assertNoOutput();
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

        system.assertDebug( CLException.class, "Invalid value '113' for option 'line-count'" );
        system.assertDebug( IllegalArgumentException.class, "SPLAT" );

        system.assertFatal( "Invalid value '113' for option 'line-count'" );
    }







// givenEnumFlag_requestHelp_expectFlagAndValuesToBeDocumented
// givenEnumFlag_invokeAppWithoutSpecifyingFlag_expectDefaultValueToBeUsed
// givenEnumFlag_invokeAppWithFlag_expectSuppliedValueToBeUsed
// givenEnumFlag_invokeAppWithUnknownEnumValue_expectError



// SETTINGS

// givenKVFlag_supplyValueInSettingsFile_expectValue
// givenKVFlag_supplyValueInSettingsFileANDSupplyOnCLI_expectValueFromCLI

// specifySettingsFileWith_dashDashSettings_expectValueToBePickedUp
// placeSettingsInAppNameDotProperties_expectValueToBePickedUp
// placeSettingsHomeDirectoryDotAppName_expectValueToBePickedUp
// placeAUnrecognisedKeyInTheSettingsFile_expectAWarningOnStartup
// placeAUnrecognisedKeyInTheSettingsFile_requestSettingsToBePruned_expectUnrecognisedKeyToBeRemovedWithAUDITMessage //--prune-settings

// OPTIONS
// -c  --counter
// -c=true
// -c=false   (t|f|y|n|yes|no|0|1)
// -k value
// -k=value
// --key value
// --key=value


    // todo accept -help --help and -?   it is unclear which is the true convention; thus the idea to support all
    //      ls -help   git --help


// givenAppThatThrowsExceptionWhenRun_runApp_expectError


// givenAppThatTakesFlagsOptionsAndArguments_invokeWithAllWithOptionsAndFlagsBeforeArguments_expectValuesToBeSet
// givenAppThatTakesFlagsOptionsAndArguments_invokeWithAllWithOptionsAndFlagsAfterArguments_expectValuesToBeSet


// onStartUp_expectSetupMethodToBeInvoked
// afterAppHasCompleted_expectTearDownMethodToBeCalled
// onStartUp_expectAuditMessageSpecifyingWhenAppWasStarted
// onStartUp_expectSettingsToBeEchoedToTheInfoLog
// onStartUp_expectArgumentsUsedToBeEchoedToTheInfoLog
// onStartUp_expectJavaVersionToBeSentToDebugLog
// onStartUp_expectClasspathToBeSentToDebugLog



// cmd OPTIONS args

// -v --verbose
// --logLevel=debug -d --debug
// --logLevel=audit -a --audit
// --logLevel=info -i --info

    private static class AppWithOPTIONS extends CLApp2 {

//        private final CLArgument<LogLevelEnum> logLevelFlag;   // .get()   .setDefaultValue(v)
//
//        private final CLArgument<FileX>        sourceFile;


        public AppWithOPTIONS( SystemX system ) {
            super( system );

//            setAppDescription( " " );
//
//            this.isVerboseCLF = registerFlag( "verbose", "v", "description" );
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
