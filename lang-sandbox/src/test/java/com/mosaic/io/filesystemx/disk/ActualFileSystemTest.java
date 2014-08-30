package com.mosaic.io.filesystemx.disk;

import com.mosaic.io.filesystemx.BaseFileSystemTestCases;
import com.mosaic.io.filesystemx.FileContents;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileSystemX;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.LiveSystem;
import com.mosaic.lang.system.OSProcess;
import com.mosaic.lang.system.SystemX;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class ActualFileSystemTest extends BaseFileSystemTestCases {

    protected FileSystemX createFileSystem( String path ) {
        return new ActualFileSystem( new File(path) );
    }

// actual files only; inmemory ones will not support this


    @Test
    public void givenLockedFile_tryToLockItAgainFromAnotherProcess_expectThatAttemptToFail() {
        FileX file = fileSystem.getOrCreateFile( "file.lock" );

        file.rw( f -> {
            f.lockFile();


            DebugSystem system  = new DebugSystem();
            OSProcess   process = system.runJavaProcess( LockFileMain.class, file.getFullPath() );

            process.spinUntilComplete( 3000 );

            assertEquals( 1, process.getResultNoBlock().intValue() );  // 1 means that the lock was NOT acquired

            return null;
        });
    }

    @Test
    public void givenLockedFile_unlockTheFileThenTryToLockItAgainFromAnotherProcess_expectThatAttemptToSucceed() {
        FileX file = fileSystem.getOrCreateFile( "file.lock" );

        file.rw( f -> {
            f.lockFile();
            f.unlockFile();


            DebugSystem system  = new DebugSystem();
            OSProcess   process = system.runJavaProcess( LockFileMain.class, file.getFullPath() );

            process.spinUntilComplete( 3000 );

            assertEquals( 0, process.getResultNoBlock().intValue() );  // 0 means that the lock was acquired

            return null;
        });
    }


    public static class LockFileMain {
        public static void main( String[] args ) {
            SystemX system = new LiveSystem();

            FileX file = system.fileSystem.getFile( args[0] );
            FileContents fc = file.openFile( FileModeEnum.READ_ONLY );

            if ( fc.lockFile() ) {
                System.out.println( "file lock acquired" );
                System.exit( 0 );
            } else {
                System.out.println( "file lock not acquired" );
                System.exit( 1 );
            }
        }
    }
}
