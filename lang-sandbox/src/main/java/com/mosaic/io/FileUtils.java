package com.mosaic.io;

import com.mosaic.lang.functional.Predicate;
import com.mosaic.lang.system.Backdoor;

import java.io.File;
import java.io.IOException;


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

    public static File makeTempDirectory( String prefix, String postfix ) {
        try {
            File f = File.createTempFile( prefix, postfix );

            f.delete();
            f.mkdir();

            return f;
        } catch ( IOException ex ) {
            Backdoor.throwException( ex );
            return null; // unreachable
        }
    }

    public static int deleteAll( File f ) {
        int count = 0;

        File[] children = f.listFiles();
        if ( children != null ) {
            for ( File child : children ) {
                if ( child.isDirectory() ) {
                    count += deleteAll(child);
                } else {
                    child.delete();
                    count += 1;
                }
            }
        }

        if ( f.delete() ) {
            count += 1;
        }

        return count;
    }
}
