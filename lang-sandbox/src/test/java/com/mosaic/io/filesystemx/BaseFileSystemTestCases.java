package com.mosaic.io.filesystemx;

import com.mosaic.bytes.Bytes;
import com.mosaic.bytes2.Bytes2;
import com.mosaic.lang.system.SystemX;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


/**
 *
 */
public abstract class BaseFileSystemTestCases {

    protected FileSystemX fileSystem;

    protected abstract FileSystemX createFileSystem( String path );


    @Before
    public void setup() {
        File loc = new File(new File(SystemX.getTempDirectory()), "junit/mosaic/FileSystemTests" + SystemX.nextRandomLong());

        this.fileSystem = createFileSystem( loc.getAbsolutePath() );
    }

    @After
    public void tearDown() {
        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );

        fileSystem.getCurrentWorkingDirectory().deleteAll();
    }


    @Test
    public void givenEmptyFileSystem_directories_expectNone() {
        assertEquals( 0, fileSystem.getCurrentWorkingDirectory().directories().size() );
    }

    @Test
    public void givenEmptyFileSystem_createDirectory_expectToSeeDirectoryInListings() {
        fileSystem.getCurrentWorkingDirectory().createDirectory( "foo" );

        assertEquals( 1, fileSystem.getCurrentWorkingDirectory().directories().size() );
        assertEquals( fileSystem.getCurrentWorkingDirectory().getFullPath()+"/foo", fileSystem.getCurrentWorkingDirectory().directories().get(0).getFullPath() );
        assertEquals( "foo", fileSystem.getCurrentWorkingDirectory().directories().get( 0 ).getDirectoryNameNbl() );
    }

    @Test
    public void givenFileSystemWithDirectory_createDirectoryAgain_expectItToSilentlyIgnoreTheRequest() {
        fileSystem.getCurrentWorkingDirectory().createDirectory( "foo" );
        fileSystem.getCurrentWorkingDirectory().createDirectory( "foo" );

        assertEquals( 1, fileSystem.getCurrentWorkingDirectory().directories().size() );
        assertEquals( fileSystem.getCurrentWorkingDirectory().getFullPath()+"/foo", fileSystem.getCurrentWorkingDirectory().directories().get(0).getFullPath() );
        assertEquals( "foo", fileSystem.getCurrentWorkingDirectory().directories().get( 0 ).getDirectoryNameNbl() );
    }

    @Test
    public void givenFileSystemWithDirectory_fromChildDirectoryGiveAbsoluteDirectoryRef() {
        DirectoryX dir    = fileSystem.getCurrentWorkingDirectory().createDirectory( "foo" );
        DirectoryX absDir = dir.getDirectory( "/foo" );


        assertEquals( dir.getFullPath(), absDir.getFullPath() );
    }

    @Test
    public void givenEmptyFileSystem_createRandomDirectory_expectToSeeDirectoryInListings() {
        fileSystem.getCurrentWorkingDirectory().createDirectoryWithRandomName( "foo", "bar" );

        assertEquals( 1, fileSystem.getCurrentWorkingDirectory().directories().size() );

        String dirName = fileSystem.getCurrentWorkingDirectory().directories().get(0).getDirectoryNameNbl();

        assertTrue( dirName.startsWith( "foo" ) );
        assertTrue( dirName.endsWith( "bar" ) );
        assertTrue( dirName.length() > "foobar".length() );
    }

    @Test
    public void givenFileSystemWithDirectory_deleteAll_expectDirectoryToVanish() {
        fileSystem.getCurrentWorkingDirectory().createDirectory( "foo" );

        fileSystem.getCurrentWorkingDirectory().deleteAll();

        assertNull( fileSystem.getCurrentWorkingDirectory().getDirectory( "foo" ) );
        assertEquals( 0, fileSystem.getCurrentWorkingDirectory().directories().size() );
    }

    @Test
    public void givenFileSystemWithDirectory_deleteDirectory_expectDirectoryToVanish() {
        DirectoryX dir = fileSystem.getCurrentWorkingDirectory().createDirectory( "foo" );

        dir.deleteAll();

        assertNull( fileSystem.getCurrentWorkingDirectory().getDirectory( "foo" ) );
        assertEquals( 0, fileSystem.getCurrentWorkingDirectory().directories().size() );
    }

    @Test
    public void givenFileSystem_addFile_expectItToBeVisibleInListFiles() {
        FileX newFile = fileSystem.getCurrentWorkingDirectory().addFile( "foo.txt", "abc", "123" );

        assertEquals( 1, fileSystem.getCurrentWorkingDirectory().files().size() );
        assertEquals( newFile.getFullPath(), fileSystem.getCurrentWorkingDirectory().files().get( 0 ).getFullPath() );
    }

    @Test
    public void givenFileSystem_addFile_expectItToBeVisibleViaGetFile() {
        FileX newFile = fileSystem.getCurrentWorkingDirectory().addFile( "foo.txt", "abc", "123" );

        assertEquals( newFile.getFullPath(), fileSystem.getCurrentWorkingDirectory().getFile( "foo.txt" ).getFullPath() );
    }

    @Test
    public void givenFileSystem_addNestedFile_expectItToBeVisibleViaGetFile() {
        FileX newFile = fileSystem.getCurrentWorkingDirectory().addFile( "/foo/foo.txt", "abc", "123" );

        assertEquals( newFile.getFullPath(), fileSystem.getCurrentWorkingDirectory().getFile( "foo/foo.txt" ).getFullPath() );

        assertNotNull( fileSystem.getCurrentWorkingDirectory().getDirectory("foo") );
        assertNotNull( fileSystem.getCurrentWorkingDirectory().getDirectory("foo").getFile("foo.txt") );
        assertNotNull( fileSystem.getCurrentWorkingDirectory().getDirectory("foo").getFile("/foo/foo.txt") );
    }

    @Test
    public void givenFile_deleteIt_expectItToNoLongerBeVisible() {
        FileX newFile = fileSystem.getCurrentWorkingDirectory().addFile( "foo.txt", "abc", "123" );

        newFile.delete();

        assertNull( fileSystem.getCurrentWorkingDirectory().getFile("foo.txt") );
        assertEquals( 0, fileSystem.getCurrentWorkingDirectory().files().size() );
    }

    @Test
    public void givenFile_loadItIn() {
        FileX newFile = fileSystem.getCurrentWorkingDirectory().addFile( "foo.txt", "abc", "123" );

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );

        Bytes2 fileContents = newFile.openFile2( FileModeEnum.READ_ONLY );

        assertEquals( "abc\n123", fileContents.toString() );
        assertEquals( 1, fileSystem.getNumberOfOpenFiles() );

        fileContents.release();
        newFile.delete();
    }

    @Test
    public void givenFile_loadItInThenRelease_expectOpenFileCountToDropByOne() {
        FileX newFile = fileSystem.getCurrentWorkingDirectory().addFile( "foo.txt", "abc", "123" );

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );

        Bytes2 fileContents = newFile.openFile2( FileModeEnum.READ_ONLY );

        fileContents.release();

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );
    }

    @Test
    public void givenEmptyFileSystem_getOrCreateDirectory_expectDirectoryToBeCreated() {
        DirectoryX dir = fileSystem.getCurrentWorkingDirectory().getOrCreateDirectory( "dir1" );

        assertEquals( "dir1", dir.getDirectoryNameNbl() );
        assertEquals( fileSystem.getCurrentWorkingDirectory().getFullPath() + "/dir1", dir.getFullPath() );
    }

    @Test
    public void givenEmptyFileSystem_getOrCreateNestedRelativeDirectory_expectDirectoryToBeCreated() {
        DirectoryX dir = fileSystem.getCurrentWorkingDirectory().getOrCreateDirectory( "dir1/dir2" );

        assertEquals( "dir2", dir.getDirectoryNameNbl() );
        assertEquals( fileSystem.getCurrentWorkingDirectory().getFullPath() + "/dir1/dir2", dir.getFullPath() );
        assertEquals( fileSystem.getCurrentWorkingDirectory().getFullPath() + "/dir1", fileSystem.getCurrentWorkingDirectory().getDirectory( "dir1" ).getFullPath() );
    }

    @Test
    public void givenEmptyFileSystem_getOrCreateNestedAbsoluteDirectory_expectDirectoryToBeCreated() {
        DirectoryX dir = fileSystem.getCurrentWorkingDirectory().getOrCreateDirectory( "/dir1/dir2" );

        assertEquals( "dir2", dir.getDirectoryNameNbl() );
        assertEquals( fileSystem.getCurrentWorkingDirectory().getFullPath() + "/dir1/dir2", dir.getFullPath() );
        assertEquals( fileSystem.getCurrentWorkingDirectory().getFullPath() + "/dir1", fileSystem.getCurrentWorkingDirectory().getDirectory( "dir1" ).getFullPath() );
    }

    @Test
    public void givenNestedDirectory_getRelativeNestedDirectory() {
        fileSystem.getCurrentWorkingDirectory().getOrCreateDirectory( "dir1/dir2" );

        DirectoryX dir = fileSystem.getCurrentWorkingDirectory().getDirectory( "dir1/dir2" );

        assertEquals( "dir2", dir.getDirectoryNameNbl() );
        assertEquals( fileSystem.getCurrentWorkingDirectory().getFullPath()+"/dir1/dir2", dir.getFullPath() );
        assertEquals( fileSystem.getCurrentWorkingDirectory().getFullPath() + "/dir1", fileSystem.getCurrentWorkingDirectory().getDirectory( "dir1" ).getFullPath() );
    }

    @Test
    public void givenNestedDirectory_getAbsoluteNestedDirectory() {
        fileSystem.getCurrentWorkingDirectory().getOrCreateDirectory( "dir1/dir2" );

        DirectoryX dir = fileSystem.getCurrentWorkingDirectory().getDirectory( "/dir1/dir2" );

        assertEquals( "dir2", dir.getDirectoryNameNbl() );
        assertEquals( fileSystem.getCurrentWorkingDirectory().getFullPath()+"/dir1/dir2", dir.getFullPath() );
        assertEquals( fileSystem.getCurrentWorkingDirectory().getFullPath()+"/dir1", fileSystem.getCurrentWorkingDirectory().getDirectory("dir1").getFullPath() );
    }

    @Test
    public void givenFile_copyFile() {
        FileX newFile   = fileSystem.getCurrentWorkingDirectory().addFile( "foo.txt", "abc", "123" );
        FileX copiedFile = fileSystem.getCurrentWorkingDirectory().copyFile( newFile, "copy.txt" );

        Bytes2 fileContents = copiedFile.openFile2( FileModeEnum.READ_ONLY );

        assertEquals( "abc\n123", fileContents.toString() );
        assertEquals( 1, fileSystem.getNumberOfOpenFiles() );

        fileContents.release();
        copiedFile.delete();
    }

// UPDATE AN EXISTING FILE

    @Test
    public void givenFile_editContents_expectContentsToPersist() {
        FileX newFile = fileSystem.getCurrentWorkingDirectory().addFile( "foo.txt", "abc" );

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );

        Bytes2 fileContents = newFile.openFile2( FileModeEnum.READ_WRITE );

        fileContents.resize( 6 );
        fileContents.writeUTF8StringUndemarcated( 0, 6, "123456" );
        fileContents.release();

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );


        Bytes2 fileContents2 = newFile.openFile2( FileModeEnum.READ_WRITE );

        assertEquals( "123456", fileContents2.toString() );
        assertEquals( 1, fileSystem.getNumberOfOpenFiles() );

        fileContents2.release();
    }

    @Test
    public void givenFile_writeContentsViaWriteTextOnFileX_expectContentsToPersist() {
        FileX newFile = fileSystem.getCurrentWorkingDirectory().addFile( "foo.txt", "abc" );

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );

        newFile.writeText( "123456" );

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );


        Bytes2 fileContents2 = newFile.openFile2( FileModeEnum.READ_WRITE );

        assertEquals( "123456", fileContents2.toString() );
        assertEquals( 1, fileSystem.getNumberOfOpenFiles() );

        fileContents2.release();
    }

    @Test
    public void lockFileThenWriteToIt() {
        FileX newFile = fileSystem.getCurrentWorkingDirectory().addFile( "foo.txt", "abc" );

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );

        FileContents2 fc = newFile.openFile2( FileModeEnum.READ_WRITE );
        fc.lockFile();
        fc.resize( 6 );
        fc.writeUTF8StringUndemarcated( 0, 6, "123456" );
        fc.unlockFile();
        fc.release();

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );


        Bytes2 fileContents2 = newFile.openFile2( FileModeEnum.READ_WRITE );

        assertEquals( "123456", fileContents2.toString() );
        assertEquals( 1, fileSystem.getNumberOfOpenFiles() );

        fileContents2.release();
    }

// LOAD PROPERTIES

    @Test
    public void givenFile_loadProperties() {
        fileSystem.getCurrentWorkingDirectory().addFile( "foo.txt", "key1=a", "key2=b" );

        Map<String,String> properties = fileSystem.getCurrentWorkingDirectory().getFile( "foo.txt" ).loadProperties();

        assertEquals( "a", properties.get("key1") );
        assertEquals( "b", properties.get("key2") );
        assertEquals( 2, properties.size() );
    }

    @Test
    public void givenFileWithComments_loadProperties() {
        fileSystem.getCurrentWorkingDirectory().addFile( "foo.txt", "#properties with comment", "key1=a  # comment2", "key2= b " );

        Map<String,String> properties = fileSystem.getCurrentWorkingDirectory().getFile( "foo.txt" ).loadProperties();

        assertEquals( "a", properties.get("key1") );
        assertEquals( "b", properties.get("key2") );
        assertEquals( 2, properties.size() );
    }

    @Test
    public void givenFileWithKeyWithNoValue() {
        fileSystem.getCurrentWorkingDirectory().addFile( "foo.txt", "#properties with comment", "key1", "key2=" );

        Map<String,String> properties = fileSystem.getCurrentWorkingDirectory().getFile( "foo.txt" ).loadProperties();

        assertEquals( "", properties.get("key1") );
        assertEquals( "", properties.get("key2") );
        assertEquals( 2, properties.size() );
    }

    @Test
    public void givenFile_tryToMutateProperties_expectError() {
        fileSystem.getCurrentWorkingDirectory().addFile( "foo.txt", "key1=a", "key2=b" );

        Map<String,String> properties = fileSystem.getCurrentWorkingDirectory().getFile( "foo.txt" ).loadProperties();


        try {
            properties.put( "a", "b");
            fail("expected exception");
        } catch ( UnsupportedOperationException ex ) {
            assertNull( ex.getMessage() );
        }
    }


// FILE LOCK

    @Test
    public void givenFileThatDoesNotExist_callIsLocked_expectFalse() {
        FileContents file = fileSystem.getCurrentWorkingDirectory().getOrCreateFile( "file.lock" ).rw( fc -> {
                assertFalse( fc.isLocked() );
                return null;
            }
        );
    }

    @Test
    public void givenFileThatDoesNotExist_requestLock_expectFileToBeLocked() {
        FileContents2 file = fileSystem.getCurrentWorkingDirectory().getOrCreateFile( "file.lock" ).openFile2( FileModeEnum.READ_WRITE );

        assertTrue( file.lockFile() );
        assertTrue( file.isLocked() );

        file.release();
    }

    @Test
    public void givenLockedFile_tryToLockAgain_expectLockFileToReturnFalseAndTheFileToRemainLocked() {
        FileContents2 file = fileSystem.getCurrentWorkingDirectory().getOrCreateFile( "file.lock" ).openFile2( FileModeEnum.READ_WRITE );

        file.lockFile();

        assertFalse( file.lockFile() );
        assertTrue( file.isLocked() );

        file.release();
    }

    @Test
    public void givenLockedFile_callUnlock_expectFileToBeUnlocked() {
        FileContents2 file = fileSystem.getCurrentWorkingDirectory().getOrCreateFile( "file.lock" ).openFile2( FileModeEnum.READ_WRITE );

        file.lockFile();

        assertTrue( file.unlockFile() );
        assertFalse( file.isLocked() );

        file.release();
    }

    @Test
    public void givenExistingUnlockedFile_requestLock_expectLockToBeAcquired() {
        FileContents2 file = fileSystem.getCurrentWorkingDirectory().getOrCreateFile( "file.lock" ).openFile2( FileModeEnum.READ_WRITE );

        file.lockFile();
        file.unlockFile();

        assertTrue( file.lockFile() );
        assertTrue( file.isLocked() );

        file.release();
    }

    @Test
    public void givenNonLockedFile_callUnlock_expectNoChange() {
        FileContents2 file = fileSystem.getCurrentWorkingDirectory().getOrCreateFile( "file.lock" ).openFile2( FileModeEnum.READ_WRITE );

        assertFalse( file.unlockFile() );
        assertFalse( file.isLocked() );

        file.release();
    }

}
