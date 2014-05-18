package com.mosaic.io.cli;

import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 */
public class CLApp_optionTests {

    private DebugSystem system = new DebugSystem();


// BOOLEAN OPTIONS

    @Test
    public void givenBooleanFlag_requestHelp_expectFlagToBeDocumented() {
        CLApp2 app = new CLApp2(system) {
            public CLOption<Boolean> debug;

            {
                this.debug = registerBooleanOption( "d", "debug", "Enable debug mode." );
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
            "        display this usage information",
            ""
        );
    }

    @Test
    public void givenBooleanFlag_invokeAppWithoutSpecifyingFlag_expectFlagToBeFalse() {
        CLApp2 app = new CLApp2(system) {
            public CLOption<Boolean> debug;

            {
                this.debug = registerBooleanOption( "d", "debug", "Enable debug mode." );
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
                this.debug = registerBooleanOption( "d", "debug", "Enable debug mode." );
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
                this.debug = registerBooleanOption( "d", "debug", "Enable debug mode." );
            }

            protected int _run() {
                assertTrue( "-d was not supplied", debug.getValue() );

                return 42;
            }
        };

        assertEquals( 42, app.runApp("--debug") );

        system.assertNoOutput();
    }



//
//
//
//
//
// givenBooleanFlag_invokeAppWithKVShortVersionUsingSpaceTrue_expectFlagToBeTrue
// givenBooleanFlag_invokeAppWithKVShortVersionUsingSpaceFalse_expectFlagToBeFalse
// givenBooleanFlag_invokeAppWithKVShortVersionUsingEqualsTrue_expectFlagToBeTrue
// givenBooleanFlag_invokeAppWithKVShortVersionUsingEqualsFalse_expectFlagToBeFalse
// givenBooleanFlag_invokeAppWithKVLongVersionUsingSpaceTrue_expectFlagToBeTrue
// givenBooleanFlag_invokeAppWithKVLongVersionUsingSpaceFalse_expectFlagToBeFalse
// givenBooleanFlag_invokeAppWithKVLongVersionUsingEqualsTrue_expectFlagToBeTrue
// givenBooleanFlag_invokeAppWithKVLongVersionUsingEqualsT_expectFlagToBeTrue
// givenBooleanFlag_invokeAppWithKVLongVersionUsingEqualsY_expectFlagToBeTrue
// givenBooleanFlag_invokeAppWithKVLongVersionUsingEqualsN_expectFlagToBeFalse
// givenBooleanFlag_invokeAppWithKVLongVersionUsingEqualsNo_expectFlagToBeFalse
// givenBooleanFlag_invokeAppWithKVLongVersionUsingEqualsFalse_expectFlagToBeFalse

// givenBooleanFlagWithMultipleShortForms_specifyFirstShortForm_expectValueToBeRecognised
// givenBooleanFlagWithMultipleShortForms_specifySecondShortForm_expectValueToBeRecognised


// givenKVFlag_requestHelp_expectFlagToBeDocumented
// givenKVFlag_invokeAppWithoutSpecifyingFlag_expectDefaultValueToBeUsed
// givenKVFlag_invokeAppWithShortFlagAndEquals_expectSuppliedValueToBeUsed
// givenKVFlag_invokeAppWithShortFlagAndSpace_expectSuppliedValueToBeUsed
// givenKVFlag_invokeAppWithLongFlagAndEquals_expectSuppliedValueToBeUsed
// givenKVFlag_invokeAppWithLongFlagAndSpace_expectSuppliedValueToBeUsed
// givenKVFlag_invokeAppWithFlagValueThatWillFailToParse_expectError

// givenAppThatThrowsExceptionWhenRun_runApp_expectError


// givenEnumFlag_requestHelp_expectFlagAndValuesToBeDocumented
// givenEnumFlag_invokeAppWithoutSpecifyingFlag_expectDefaultValueToBeUsed
// givenEnumFlag_invokeAppWithFlag_expectSuppliedValueToBeUsed
// givenEnumFlag_invokeAppWithUnknownEnumValue_expectError

// supplyUnknownFlag_expectWarning

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


// onStartUp_expectSetupMethodToBeInvoked
// afterAppHasCompleted_expectTearDownMethodToBeCalled
// onStartUp_expectAuditMessageSpecifyingWhenAppWasStarted
// onStartUp_expectSettingsToBeEchoedToTheInfoLog
// onStartUp_expectArgumentsUsedToBeEchoedToTheInfoLog
// onStartUp_expectJavaVersionToBeSentToDebugLog
// onStartUp_expectClasspathToBeSentToDebugLog



// todo mix OPTIONS and args together


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
//            this.isVerboseCLF = registerBooleanOption( "verbose", "v", "description" );
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
