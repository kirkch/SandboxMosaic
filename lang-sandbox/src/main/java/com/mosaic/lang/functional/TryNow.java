package com.mosaic.lang.functional;

import com.mosaic.lang.Failure;
import com.mosaic.utils.concurrent.Future;

/**
 * Factory class for jobs that execute immediately.
 */
public class TryNow {

    public static <T> Try<T> tryNow( Function0<T> op ) {
        try {
            return Future.successful( op.invoke() );
        } catch ( Exception e ) {
            return Future.failed( new Failure(e) );
        }
    }

}
