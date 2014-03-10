package com.mosaic.io.filesystemx;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.system.SystemX;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

        Bytes fileContents = newFile.loadBytes( FileModeEnum.READ_ONLY );

        assertEquals( "abc\n123", fileContents.toString() );
        assertEquals( 1, fileSystem.getNumberOfOpenFiles() );

        fileContents.release();
    }

    @Test
    public void givenFile_loadItInThenRelease_expectOpenFileCountToDropByOne() {
        FileX newFile = fileSystem.addFile( "foo.txt", "abc", "123" );

        assertEquals( 0, fileSystem.getNumberOfOpenFiles() );

        Bytes fileContents = newFile.loadBytes( FileModeEnum.READ_ONLY );

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

}
