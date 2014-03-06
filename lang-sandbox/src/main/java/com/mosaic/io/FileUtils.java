package com.mosaic.io;

import com.mosaic.lang.functional.Predicate;

import java.io.File;


/**
 *
 */
public class FileUtils {
    /**
     * Delete all files in a directory whose name matches the specified predicate.
     */
    public static void deleteMatchingFiles( File directory, Predicate<String> predicate ) {
        File[] candidateFiles = directory.listFiles();
        if ( candidateFiles == null ) {
            return;
        }

        for ( File f : candidateFiles ) {
            if ( f.isFile() && predicate.invoke(f.getName()) ) {
                f.delete();
            }
        }
    }
}
