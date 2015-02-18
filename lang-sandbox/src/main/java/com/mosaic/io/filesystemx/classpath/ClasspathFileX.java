package com.mosaic.io.filesystemx.classpath;

import com.mosaic.bytes.Bytes;
import com.mosaic.bytes.ClassPathBytes2;
import com.mosaic.bytes2.Bytes2;
import com.mosaic.io.filesystemx.FileContents;
import com.mosaic.io.filesystemx.FileContents2;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.NotFoundException;
import com.mosaic.lang.system.Backdoor;

import java.io.IOException;


/**
 *
 */
public class ClassPathFileX implements FileX {

    private String fullPath;

    public ClassPathFileX( String fullPath ) {
        this.fullPath = fullPath;
    }

    public String getFileName() {
        int i = fullPath.lastIndexOf( '/' );

        if ( i >= 0 ) {
            return fullPath.substring( i+1 );
        } else {
            return fullPath;
        }
    }

    @Override
    public FileContents openFile( FileModeEnum mode ) {
        if ( mode.isWritable() || !mode.isReadable() ) {
            throw new UnsupportedOperationException( "not implemented, use loadBytesRO" );
        }

        return new ClassPathFileContents( lazyLoad() );
    }

    @Override
    public FileContents openFile( FileModeEnum mode, long sizeInBytes ) {
        throw new UnsupportedOperationException( "not implemented, use loadBytesRO" );
    }

    @Override
    public FileContents2 openFile2( FileModeEnum mode ) {
        if ( mode.isWritable() || !mode.isReadable() ) {
            throw new UnsupportedOperationException( "not implemented, use loadBytesRO" );
        }

        return new ClassPathFileContents2( lazyLoad2() );
    }

    @Override
    public FileContents2 openFile2( FileModeEnum mode, long sizeInBytes ) {
        throw new UnsupportedOperationException( "not implemented, use loadBytesRO" );
    }

    public String getFullPath() {
        return fullPath;
    }

    public void delete() {
        throw new UnsupportedOperationException( "cannot delete files from the classpath" );
    }


    public long sizeInBytes() {
        lazyLoad();


        return lazyLoad().sizeBytes();
    }

    public void isReadable( boolean isReadable ) {
        throw new UnsupportedOperationException();
    }

    public boolean isReadable() {
        return true;
    }

    public void isWritable( boolean isWritable ) {
        throw new UnsupportedOperationException();
    }

    public boolean isWritable() {
        return false;
    }

    public void isExecutable( boolean isExecutable ) {
        throw new UnsupportedOperationException();
    }

    public boolean isExecutable() {
        return false;
    }



    private Bytes bytes;

    private Bytes lazyLoad() {
        if ( bytes == null ) {
            try {
                bytes = ClassPathBytes2.loadFromClassPath( this.getClass().getClassLoader(), fullPath );

                if ( bytes == null ) {
                    throw new NotFoundException( String.format("unable to find '%s' on the classpath", fullPath) );
                }
            } catch ( IOException e ) {
                return Backdoor.throwException( e );
            }
        }

        return bytes;
    }

    private Bytes2 bytes2;

    private Bytes2 lazyLoad2() {
        if ( bytes2 == null ) {
            try {
                bytes2 = ClassPathBytes2.loadFromClassPath2( this.getClass().getClassLoader(), fullPath );

                if ( bytes2 == null ) {
                    throw new NotFoundException( String.format("unable to find '%s' on the classpath", fullPath) );
                }
            } catch ( IOException e ) {
                return Backdoor.throwException( e );
            }
        }

        return bytes2;
    }


    private class ClassPathFileContents extends FileContents {
        public ClassPathFileContents( Bytes delegate ) {
            super( delegate );
        }

        public boolean lockFile() {
            throw new UnsupportedOperationException( "files on the classpath cannot be locked" );
        }

        public boolean isLocked() {
            return false;
        }

        public boolean unlockFile() {
            throw new UnsupportedOperationException( "files on the classpath cannot be locked" );
        }
    }


    private class ClassPathFileContents2 extends FileContents2 {
        public ClassPathFileContents2( Bytes2 delegate ) {
            super( delegate );
        }

        public boolean lockFile() {
            throw new UnsupportedOperationException( "files on the classpath cannot be locked" );
        }

        public boolean isLocked() {
            return false;
        }

        public boolean unlockFile() {
            throw new UnsupportedOperationException( "files on the classpath cannot be locked" );
        }
    }
}
