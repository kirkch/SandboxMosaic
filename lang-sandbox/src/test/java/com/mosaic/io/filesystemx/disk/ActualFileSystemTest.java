package com.mosaic.io.filesystemx.disk;

import com.mosaic.io.filesystemx.BaseFileSystemTestCases;
import com.mosaic.io.filesystemx.FileSystemX;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.system.DebugSystem;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;


/**
 *
 */
public class ActualFileSystemTest extends BaseFileSystemTestCases {

    protected FileSystemX createFileSystem( String path ) {
        return new ActualFileSystem( new File(path) );
    }

// actual files only; inmemory ones will not support this
    //
    // givenLockedFile_unlockTheFileThenTryToLockItAgainFromAnotherProcess_expectThatAttemptToSucceed




    @Test
    public void givenLockedFile_tryToLockItAgainFromAnotherProcess_expectThatAttemptToFail() {
        FileX file = fileSystem.getOrCreateFile( "file.lock" );

        file.lockFile();


        DebugSystem system = new DebugSystem();

        system.runJavaProcess( LockFileMain.class, file.getFullPath() );


        file.unlockFile();

        assertTrue( file.lockFile() );
        assertTrue( file.isLocked() );
    }


    public static class LockFileMain {
        public static void main( String[] args ) {

        }
    }
}
