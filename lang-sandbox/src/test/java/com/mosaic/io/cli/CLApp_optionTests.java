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


// BOOLEAN OPTIONS  (AKA FLAG)

    @Test
    public void givenBooleanFlag_requestHelp_expectFlagToBeDocumented() {
        CLApp2 app = new CLApp2(system) {
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
            "Usage: " + app.getClass().getName(),
            "",
            "Options:",
            "",
            "    -d, --debug",
            "        Enable debug mode.",
            "",
            "    --help",
            "        Display this usage information.",
            ""
        );
    }

    @Test
    public void givenBooleanFlag_supplyDescriptionInLowerCaseWithNoFullStop_expectThemToBeAdded() {
        CLApp2 app = new CLApp2(system) {
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
            "Usage: " + app.getClass().getName(),
            "",
            "Options:",
            "",
            "    -d, --debug",
            "        Enable debug mode.",
            "",
            "    --help",
            "        Display this usage information.",
            ""
        );
    }

    @Test
    public void givenBooleanFlag_invokeAppWithoutSpecifyingFlag_expectFlagToBeFalse() {
        CLApp2 app = new CLApp2(system) {
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

        system.assertNoOutput();
    }

    @Test
    public void givenBooleanFlag_invokeAppWithShortVersionOfFlag_expectFlagToBeTrue() {
        CLApp2 app = new CLApp2(system) {
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

        system.assertNoOutput();
    }

    @Test
    public void givenBooleanFlag_invokeAppWithLongVersionOfFlag_expectFlagToBeTrue() {
        CLApp2 app = new CLApp2(system) {
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

        system.assertNoOutput();
    }


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


//givenFlagWithMultipleLettersForShortForm_expectExceptionAsTheShortFormMustBeSingleLetters
    // or
//givenFlagWithMultipleLettersForShortForm_supplyShortForm_expectValueToBeSet

// givenDuplicateArgNames_expectException
// givenDuplicateLongOptionNames_expectException
// givenDuplicateShortOptionNames_expectException
// givenDuplicateLongFlagNames_expectException
// givenDuplicateShortFlagNames_expectException
// givenDuplicateShortFlagNameThatClashesWithShortOptionName_expectException
// givenDuplicateLongFlagNameThatClashesWithShortOptionName_expectException
// givenDuplicateShortFlagNameThatClashesWithLongOptionName_expectException
// givenDuplicateLongFlagNameThatClashesWithLongOptionName_expectException


// givenThreeFlags_supplyTwoSeparatedBySpaced_expectThoseToFlagsToBeTrueAndTheThirdFalse
// givenThreeFlags_supplyTwoCombinedTogether_expectThoseToFlagsToBeTrueAndTheThirdFalse
// givenOneOptionAndOneFlag_supplyBothSeparately_expectValues
// givenOneOptionAndOneFlag_supplyBothConcatenatedTogetherUsingShortForm_expectValues
// supplyUnknownFlag_expectFatalErrorAsPerUnixConvention



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
