package com.mosaic.io.filesystemx;

import java.nio.channels.FileChannel;


/**
 *
 */
public enum FileModeEnum {
    READ_ONLY(   "r", FileChannel.MapMode.READ_ONLY  ),
    READ_WRITE( "rw", FileChannel.MapMode.READ_WRITE ),
    WRITE_ONLY(  "w", FileChannel.MapMode.PRIVATE    );



    private String              modeString;
    private FileChannel.MapMode mapMode;

    private FileModeEnum( String modeString, FileChannel.MapMode mapMode ) {
        this.modeString = modeString;
        this.mapMode = mapMode;
    }

    public String toString() {
        return modeString;
    }

    public FileChannel.MapMode toMemoryMapMode() {
        return mapMode;
    }
}
