package com.mosaic.io.filesystemx;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.system.SystemX;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        this.fileSystem = createFileSystem( SystemX.getTempDirectory() + "/junit/mosaic/FileSystemTests" + SystemX.nextRandomLong() );
    }

    @After
    public void tearDown() {
        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );

        fileSystem.deleteAll();
    }


    @Test
    public void givenEmptyFileSystem_directories_expectNone() {
        assertEquals( 0, fileSystem.directories().size() );
    }

    @Test
    public void givenEmptyFileSystem_createDirectory_expectToSeeDirectoryInListings() {
        fileSystem.createDirectory( "foo" );

        assertEquals( 1, fileSystem.directories().size() );
        assertEquals( fileSystem.getFullPath()+"/foo", fileSystem.directories().get(0).getFullPath() );
        assertEquals( "foo", fileSystem.directories().get( 0 ).getDirectoryName() );
    }

    @Test
    public void givenFileSystemWithDirectory_createDirectoryAgain_expectItToSilentlyIgnoreTheRequest() {
        fileSystem.createDirectory( "foo" );
        fileSystem.createDirectory( "foo" );

        assertEquals( 1, fileSystem.directories().size() );
        assertEquals( fileSystem.getFullPath()+"/foo", fileSystem.directories().get(0).getFullPath() );
        assertEquals( "foo", fileSystem.directories().get( 0 ).getDirectoryName() );
    }

    @Test
    public void givenFileSystemWithDirectory_fromChildDirectoryGiveAbsoluteDirectoryRef() {
        DirectoryX dir    = fileSystem.createDirectory( "foo" );
        DirectoryX absDir = dir.getDirectory( "/foo" );


        assertEquals( dir.getFullPath(), absDir.getFullPath() );
    }

    @Test
    public void givenEmptyFileSystem_createRandomDirectory_expectToSeeDirectoryInListings() {
        fileSystem.createDirectoryWithRandomName( "foo", "bar" );

        assertEquals( 1, fileSystem.directories().size() );

        String dirName = fileSystem.directories().get(0).getDirectoryName();

        assertTrue( dirName.startsWith( "foo" ) );
        assertTrue( dirName.endsWith( "bar" ) );
        assertTrue( dirName.length() > "foobar".length() );
    }

    @Test
    public void givenFileSystemWithDirectory_deleteAll_expectDirectoryToVanish() {
        fileSystem.createDirectory( "foo" );

        fileSystem.deleteAll();

        assertNull( fileSystem.getDirectory( "foo" ) );
        assertEquals( 0, fileSystem.directories().size() );
    }

    @Test
    public void givenFileSystemWithDirectory_deleteDirectory_expectDirectoryToVanish() {
        DirectoryX dir = fileSystem.createDirectory( "foo" );

        dir.deleteAll();

        assertNull( fileSystem.getDirectory( "foo" ) );
        assertEquals( 0, fileSystem.directories().size() );
    }

    @Test
    public void givenFileSystem_addFile_expectItToBeVisibleInListFiles() {
        FileX newFile = fileSystem.addFile( "foo.txt", "abc", "123" );

        assertEquals( 1, fileSystem.files().size() );
        assertEquals( newFile.getFullPath(), fileSystem.files().get( 0 ).getFullPath() );
    }

    @Test
    public void givenFileSystem_addFile_expectItToBeVisibleViaGetFile() {
        FileX newFile = fileSystem.addFile( "foo.txt", "abc", "123" );

        assertEquals( newFile.getFullPath(), fileSystem.getFile( "foo.txt" ).getFullPath() );
    }

    @Test
    public void givenFileSystem_addNestedFile_expectItToBeVisibleViaGetFile() {
        FileX newFile = fileSystem.addFile( "/foo/foo.txt", "abc", "123" );

        assertEquals( newFile.getFullPath(), fileSystem.getFile( "/foo/foo.txt" ).getFullPath() );

        assertNotNull( fileSystem.getDirectory("foo") );
        assertNotNull( fileSystem.getDirectory("foo").getFile("foo.txt") );
        assertNotNull( fileSystem.getDirectory("foo").getFile("/foo/foo.txt") );
    }

    @Test
    public void givenFile_deleteIt_expectItToNoLongerBeVisible() {
        FileX newFile = fileSystem.addFile( "foo.txt", "abc", "123" );

        newFile.delete();

        assertNull( fileSystem.getFile("foo.txt") );
        assertEquals( 0, fileSystem.files().size() );
    }

    @Test
    public void givenFile_loadItIn() {
        FileX newFile = fileSystem.addFile( "foo.txt", "abc", "123" );

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );

        Bytes fileContents = newFile.openFile( FileModeEnum.READ_ONLY );

        assertEquals( "abc\n123", fileContents.toString() );
        assertEquals( 1, fileSystem.getNumberOfOpenFiles() );

        fileContents.release();
    }

    @Test
    public void givenFile_loadItInThenRelease_expectOpenFileCountToDropByOne() {
        FileX newFile = fileSystem.addFile( "foo.txt", "abc", "123" );

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );

        Bytes fileContents = newFile.openFile( FileModeEnum.READ_ONLY );

        fileContents.release();

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );
    }

    @Test
    public void givenEmptyFileSystem_getOrCreateDirectory_expectDirectoryToBeCreated() {
        DirectoryX dir = fileSystem.getOrCreateDirectory( "dir1" );

        assertEquals( "dir1", dir.getDirectoryName() );
        assertEquals( fileSystem.getFullPath() + "/dir1", dir.getFullPath() );
    }

    @Test
    public void givenEmptyFileSystem_getOrCreateNestedRelativeDirectory_expectDirectoryToBeCreated() {
        DirectoryX dir = fileSystem.getOrCreateDirectory( "dir1/dir2" );

        assertEquals( "dir2", dir.getDirectoryName() );
        assertEquals( fileSystem.getFullPath() + "/dir1/dir2", dir.getFullPath() );
        assertEquals( fileSystem.getFullPath() + "/dir1", fileSystem.getDirectory( "dir1" ).getFullPath() );
    }

    @Test
    public void givenEmptyFileSystem_getOrCreateNestedAbsoluteDirectory_expectDirectoryToBeCreated() {
        DirectoryX dir = fileSystem.getOrCreateDirectory( "/dir1/dir2" );

        assertEquals( "dir2", dir.getDirectoryName() );
        assertEquals( fileSystem.getFullPath() + "/dir1/dir2", dir.getFullPath() );
        assertEquals( fileSystem.getFullPath() + "/dir1", fileSystem.getDirectory( "dir1" ).getFullPath() );
    }

    @Test
    public void givenNestedDirectory_getRelativeNestedDirectory() {
        fileSystem.getOrCreateDirectory( "dir1/dir2" );

        DirectoryX dir = fileSystem.getDirectory( "dir1/dir2" );

        assertEquals( "dir2", dir.getDirectoryName() );
        assertEquals( fileSystem.getFullPath()+"/dir1/dir2", dir.getFullPath() );
        assertEquals( fileSystem.getFullPath() + "/dir1", fileSystem.getDirectory( "dir1" ).getFullPath() );
    }

    @Test
    public void givenNestedDirectory_getAbsoluteNestedDirectory() {
        fileSystem.getOrCreateDirectory( "dir1/dir2" );

        DirectoryX dir = fileSystem.getDirectory( "/dir1/dir2" );

        assertEquals( "dir2", dir.getDirectoryName() );
        assertEquals( fileSystem.getFullPath()+"/dir1/dir2", dir.getFullPath() );
        assertEquals( fileSystem.getFullPath()+"/dir1", fileSystem.getDirectory("dir1").getFullPath() );
    }

    @Test
    public void givenFile_copyFile() {
        FileX newFile   = fileSystem.addFile( "foo.txt", "abc", "123" );
        FileX copiedFile = fileSystem.copyFile( newFile, "copy.txt" );

        Bytes fileContents = copiedFile.openFile( FileModeEnum.READ_ONLY );

        assertEquals( "abc\n123", fileContents.toString() );
        assertEquals( 1, fileSystem.getNumberOfOpenFiles() );

        fileContents.release();
        copiedFile.delete();
    }

// UPDATE AN EXISTING FILE

    @Test
    public void givenFile_editContents_expectContentsToPersist() {
        FileX newFile = fileSystem.addFile( "foo.txt", "abc" );

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );

        Bytes fileContents = newFile.openFile( FileModeEnum.READ_WRITE );

        fileContents.resize( 6 );
        fileContents.writeText( "123456" );
        fileContents.release();

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );


        Bytes fileContents2 = newFile.openFile( FileModeEnum.READ_WRITE );

        assertEquals( "123456", fileContents2.toString() );
        assertEquals( 1, fileSystem.getNumberOfOpenFiles() );

        fileContents2.release();
    }

    @Test
    public void givenFile_writeContentsViaWriteTextOnFileX_expectContentsToPersist() {
        FileX newFile = fileSystem.addFile( "foo.txt", "abc" );

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );

        newFile.writeText( "123456" );

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );


        Bytes fileContents2 = newFile.openFile( FileModeEnum.READ_WRITE );

        assertEquals( "123456", fileContents2.toString() );
        assertEquals( 1, fileSystem.getNumberOfOpenFiles() );

        fileContents2.release();
    }

    @Test
    public void lockFileThenWriteToIt() {
        FileX newFile = fileSystem.addFile( "foo.txt", "abc" );

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );

        FileContents fc = newFile.openFile( FileModeEnum.READ_WRITE );
        fc.lockFile();
        fc.resize( 6 );
        fc.writeText( "123456" );
        fc.unlockFile();
        fc.release();

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );


        Bytes fileContents2 = newFile.openFile( FileModeEnum.READ_WRITE );

        assertEquals( "123456", fileContents2.toString() );
        assertEquals( 1, fileSystem.getNumberOfOpenFiles() );

        fileContents2.release();
    }

// LOAD PROPERTIES

    @Test
    public void givenFile_loadProperties() {
        fileSystem.addFile( "foo.txt", "key1=a", "key2=b" );

        Map<String,String> properties = fileSystem.getFile( "foo.txt" ).loadProperties();

        assertEquals( "a", properties.get("key1") );
        assertEquals( "b", properties.get("key2") );
        assertEquals( 2, properties.size() );
    }

    @Test
    public void givenFileWithComments_loadProperties() {
        fileSystem.addFile( "foo.txt", "#properties with comment", "key1=a  # comment2", "key2= b " );

        Map<String,String> properties = fileSystem.getFile( "foo.txt" ).loadProperties();

        assertEquals( "a", properties.get("key1") );
        assertEquals( "b", properties.get("key2") );
        assertEquals( 2, properties.size() );
    }

    @Test
    public void givenFileWithKeyWithNoValue() {
        fileSystem.addFile( "foo.txt", "#properties with comment", "key1", "key2=" );

        Map<String,String> properties = fileSystem.getFile( "foo.txt" ).loadProperties();

        assertEquals( "", properties.get("key1") );
        assertEquals( "", properties.get("key2") );
        assertEquals( 2, properties.size() );
    }

    @Test
    public void givenFile_tryToMutateProperties_expectError() {
        fileSystem.addFile( "foo.txt", "key1=a", "key2=b" );

        Map<String,String> properties = fileSystem.getFile( "foo.txt" ).loadProperties();


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
        FileContents file = fileSystem.getOrCreateFile( "file.lock" ).rw( fc -> {
                assertFalse( fc.isLocked() );
                return null;
            }
        );
    }

    @Test
    public void givenFileThatDoesNotExist_requestLock_expectFileToBeLocked() {
        FileContents file = fileSystem.getOrCreateFile( "file.lock" ).openFile( FileModeEnum.READ_WRITE );

        assertTrue( file.lockFile() );
        assertTrue( file.isLocked() );

        file.release();
    }

    @Test
    public void givenLockedFile_tryToLockAgain_expectLockFileToReturnFalseAndTheFileToRemainLocked() {
        FileContents file = fileSystem.getOrCreateFile( "file.lock" ).openFile( FileModeEnum.READ_WRITE );

        file.lockFile();

        assertFalse( file.lockFile() );
        assertTrue( file.isLocked() );

        file.release();
    }

    @Test
    public void givenLockedFile_callUnlock_expectFileToBeUnlocked() {
        FileContents file = fileSystem.getOrCreateFile( "file.lock" ).openFile( FileModeEnum.READ_WRITE );

        file.lockFile();

        assertTrue( file.unlockFile() );
        assertFalse( file.isLocked() );

        file.release();
    }

    @Test
    public void givenExistingUnlockedFile_requestLock_expectLockToBeAcquired() {
        FileContents file = fileSystem.getOrCreateFile( "file.lock" ).openFile( FileModeEnum.READ_WRITE );

        file.lockFile();
        file.unlockFile();

        assertTrue( file.lockFile() );
        assertTrue( file.isLocked() );

        file.release();
    }

    @Test
    public void givenNonLockedFile_callUnlock_expectNoChange() {
        FileContents file = fileSystem.getOrCreateFile( "file.lock" ).openFile( FileModeEnum.READ_WRITE );

        assertFalse( file.unlockFile() );
        assertFalse( file.isLocked() );

        file.release();
    }

}
