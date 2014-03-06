package com.mosaic.io.filesystemx.disk;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.bytes.WrappedBytes;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.Validate;

import java.io.File;


/**
 *
 */
public class ActualFile implements FileX {

    private File            file;
    private ActualDirectory parentDirectory;

    ActualFile( ActualDirectory parentDirectory, File file ) {
        Validate.argNotNull(file,            "file");
        Validate.argNotNull(parentDirectory, "parentDirectory");

        this.file            = file;
        this.parentDirectory = parentDirectory;
    }

    public String getFileName() {
        return file.getName();
    }

    public Bytes loadBytesRO() {
        return loadBytes( FileModeEnum.READ_ONLY );
    }

    public Bytes loadBytesRW() {
        return loadBytes( FileModeEnum.READ_WRITE );
    }

    public Bytes loadBytes( FileModeEnum mode ) {
        Bytes bytes = Bytes.memoryMapFile( file, mode );

        parentDirectory.incrementOpenFileCount();

        return new WrappedBytes(bytes) {
            @Override
            public void release() {
                super.release();

                parentDirectory.decrementOpenFileCount();
            }
        };
    }

    public String getFullPath() {
        return file.getAbsolutePath();
    }

    public void delete() {
        this.file.delete();

        this.file            = null;
        this.parentDirectory = null;
    }

}
