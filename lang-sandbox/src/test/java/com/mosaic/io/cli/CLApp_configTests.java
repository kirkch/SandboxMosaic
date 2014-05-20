package com.mosaic.io.cli;

/**
 *
 */
public class CLApp_configTests {

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
}
