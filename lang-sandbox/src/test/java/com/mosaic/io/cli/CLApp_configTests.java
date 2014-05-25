package com.mosaic.io.cli;

import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.system.DebugSystem;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class CLApp_configTests {

    private DebugSystem system = new DebugSystem();


    @Test
    public void givenSettingsInShortForm_readKV_expectValue() {
        CLApp app = new CLApp(system) {
            public CLOption<String> maxSize;

            {
                this.maxSize = registerOption( "", "max.size", "size", "desc1" );
            }

            protected int _run() {
                assertEquals( "1024kb", maxSize.getValue() );

                return 42;
            }
        };

        system.fileSystem.addFile( "props.conf", "max.size=1024kb" );

        assertEquals( 42, app.runApp("-c", "props.conf") );

        system.assertNoAlerts();
    }

    @Test
    public void givenSettingsInLongForm_readKV_expectValue() {
        CLApp app = new CLApp(system) {
            public CLOption<String> maxSize;

            {
                this.maxSize = registerOption( "", "max.size", "size", "desc1" );
            }

            protected int _run() {
                assertEquals( "1024kb", maxSize.getValue() );

                return 42;
            }
        };

        system.fileSystem.addFile( "props.conf", "max.size=1024kb" );

        assertEquals( 42, app.runApp("--config=props.conf") );

        system.assertNoAlerts();
    }

    @Test
    public void givenSettingsInFile_alsoSpecifyTheSameOptionOnTheCommandLine_expectValueOnCommandLineToTakePrecedence() {
        CLApp app = new CLApp(system) {
            public CLOption<String> maxSize;

            {
                this.maxSize = registerOption( "", "max.size", "size", "desc1" );
            }

            protected int _run() {
                assertEquals( "1mb", maxSize.getValue() );

                return 42;
            }
        };

        system.fileSystem.addFile( "props.conf", "max.size=1024kb" );

        assertEquals( 42, app.runApp("--config=props.conf", "--max.size=1mb") );

        system.assertNoAlerts();
    }

    @Test
    public void givenSettingsWithComments_expectCommentsToBeStrippedOut() {
        CLApp app = new CLApp(system) {
            public CLOption<String> maxSize;

            {
                this.maxSize = registerOption( "", "max.size", "size", "desc1" );
            }

            protected int _run() {
                assertEquals( "1024kb", maxSize.getValue() );

                return 42;
            }
        };

        system.fileSystem.addFile( "props.conf", "# comment 1", "", "max.size=  1024kb    # comment 2" );

        assertEquals( 42, app.runApp("--config=props.conf") );

        system.assertNoAlerts();
    }

    @Test
    public void givenSettingsFileThatDoesNotExist_expectError() {
        CLApp app = new CLApp(system) {
            public CLOption<String> maxSize;

            {
                this.maxSize = registerOption( "", "max.size", "size", "desc1" );
            }

            protected int _run() {
                assertEquals( "1024kb", maxSize.getValue() );

                return 42;
            }
        };

        assertEquals( 1, app.runApp( "--config=props.conf" ) );

        system.assertStandardErrorEquals( "Unable to find file 'props.conf' specified by --config." );
        system.assertFatalContains( "Unable to find file 'props.conf' specified by --config." );
    }

    @Test
    public void givenSettingsThatDoesExistButIsNotReadable_expectError() {
        CLApp app = new CLApp(system) {
            public CLOption<String> maxSize;

            {
                this.maxSize = registerOption( "", "max.size", "size", "desc1" );
            }

            protected int _run() {
                assertEquals( "1024kb", maxSize.getValue() );

                return 42;
            }
        };

        FileX file = system.fileSystem.addFile( "props.conf", "max.size=1024kb" );
        file.isReadable( false );

        assertEquals( 1, app.runApp("--config=props.conf") );

        system.assertStandardErrorEquals( "Unable to load file 'props.conf' specified by --config, permission denied." );
        system.assertFatalContains( "Unable to load file 'props.conf' specified by --config, permission denied." );
    }

    @Test
    public void placeAUnrecognisedKeyInTheSettingsFile_expectError() {
        CLApp app = new CLApp(system) {
            public CLOption<String> maxSize;

            {
                this.maxSize = registerOption( "", "max.size", "size", "desc1" );
            }

            protected int _run() {
                assertEquals( "1024kb", maxSize.getValue() );

                return 42;
            }
        };

        system.fileSystem.addFile( "props.conf", "max.size=1024kb", "key2=123" );

        assertEquals( 1, app.runApp("--config=props.conf") );

        system.assertStandardErrorEquals( "Unknown setting 'key2' in 'props.conf' specified by --config." );
        system.assertFatalContains( "Unknown setting 'key2' in 'props.conf' specified by --config." );
    }

}