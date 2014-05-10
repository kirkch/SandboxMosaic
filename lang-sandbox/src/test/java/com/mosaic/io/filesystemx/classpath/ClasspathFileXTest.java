package com.mosaic.io.filesystemx.classpath;

import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.NotFoundException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 *
 */
public class ClasspathFileXTest {

    @Test
    public void fileDoesNotExist_getFileName_expectFileName() {
        FileX file = new ClasspathFileX("/a/b/Foo.txt");

        assertEquals( "Foo.txt", file.getFileName() );
    }

    @Test
    public void fileNameOnly_getFileName_expectFileName() {
        FileX file = new ClasspathFileX("Foo.txt");

        assertEquals( "Foo.txt", file.getFileName() );
    }

    @Test
    public void fileDoesNotExist_getFullPath_expectFileName() {
        FileX file = new ClasspathFileX("/a/b/Foo.txt");

        assertEquals( "/a/b/Foo.txt", file.getFullPath() );
    }

    @Test
    public void fileDoesExist_delete_throwUnsupportedOpEx() {
        FileX file = new ClasspathFileX("/data/names.txt");

        try {
            file.delete();
            fail( "expected exception" );
        } catch ( UnsupportedOperationException ex ) {
            assertEquals( "cannot delete files from the classpath", ex.getMessage() );
        }
    }

    @Test
    public void fileDoesNotExist_sizeBytes_throwFileNotFound() {
        FileX file = new ClasspathFileX("/a/b/Foo.txt");

        try {
            file.sizeInBytes();
            fail( "expected exception" );
        } catch ( NotFoundException ex ) {
            assertEquals( "unable to find '/a/b/Foo.txt' on the classpath", ex.getMessage() );
        }
    }

    @Test
    public void fileDoesExist_sizeBytes_throwFileNotFound() {
        FileX file = new ClasspathFileX("/movies.txt");

        assertEquals( 37, file.sizeInBytes() );
    }

}
