package com.mosaic.io.filesystemx;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.bytes2.impl.BytesWrapper2;


/**
 *
 */
public abstract class FileContents2 extends BytesWrapper2 {

    public FileContents2( Bytes2 delegate ) {
        super( delegate );
    }

    /**
     * Takes out a file system level lock.  Depending on the underlying OS, the lock may only
     * be honored if the lock is explicitly checked;  So be sure to test the lock before using
     * the file.
     *
     * @return true if the lock has been acquired by this call, false means that another process
     *   already holds the lock
     */
    public abstract boolean lockFile();

    /**
     * Returns true if this file is currently locked by this process.  NB one must have invoked lockFile() first,
     * otherwise this method will always return false even if the file has been locked by another
     * JVM.  This is because of a limitation in the underlying API, which requires a lock attempt
     * before determining if the file is locked.
     */
    public abstract boolean isLocked();

    /**
     * Releases the file lock.
     *
     * @return returns true if the lock was released by this call, otherwise false
     */
    public abstract boolean unlockFile();

}
