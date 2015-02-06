package com.mosaic.bytes.struct;

import com.mosaic.io.filesystemx.FileContents;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.StartStopMixin;
import com.mosaic.lang.StartStoppable;


/**
 * A utility base class for structs that are memory mapped to a file.
 */
public class PersistentStruct<T extends StartStoppable<T>> extends StartStopMixin<T> {

    private final FileX          dataFile;
    private final StructRegistry structRegistry;

    protected Struct       struct;
    private   FileContents fileBytes;


    public PersistentStruct( FileX dataFile, StructRegistry structRegistry ) {
        super( dataFile.getFullPath() );
        this.dataFile       = dataFile;
        this.structRegistry = structRegistry;
    }

    public void sync() {
        fileBytes.flush();
    }

    protected void doStart() throws Exception {
        long structSize = structRegistry.sizeBytes();

        this.struct = new Struct( structSize );

        this.fileBytes = dataFile.openFile( FileModeEnum.READ_WRITE, structSize );
        struct.setBytes( fileBytes, 0, structSize );
    }

    protected void doStop() throws Exception {
        fileBytes.release();

        this.fileBytes = null;
        this.struct    = null;
    }

}