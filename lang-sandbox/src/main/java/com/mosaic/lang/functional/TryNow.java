package com.mosaic.lang.functional;

import com.mosaic.lang.Failure;
import com.mosaic.utils.concurrent.Future;
import com.mosaic.utils.concurrent.FutureNbl;

/**
 * Factory class for jobs that execute immediately.
 */
public class TryNow {

    public static <T> Try<T> tryNow( Function0<T> op ) {
        try {
            return successful( op.invoke() );
        } catch ( Exception e ) {
            return failed( new Failure(e) );
        }
    }

    public static <T> Try<T> successful( T v ) {
        return Future.successful(v);
    }

    public static <T> Try<T> failed( Failure f ) {
        return Future.failed(f);
    }

    public static <T> TryNbl<T> tryNowNbl( Function0<Nullable<T>> op ) {
        try {
            return successfulNbl(op.invoke());
        } catch ( Exception e ) {
            return failedNbl( new Failure(e) );
        }
    }

    public static <T> TryNbl<T> successfulNbl( Nullable<T> v ) {
        return FutureNbl.successful(v);
    }

    public static <T> TryNbl<T> failedNbl( Failure f ) {
        return FutureNbl.failed(f);
    }

}
