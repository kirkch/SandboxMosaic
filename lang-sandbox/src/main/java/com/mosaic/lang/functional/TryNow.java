package com.mosaic.lang.functional;

import com.mosaic.lang.Failure;
import com.mosaic.collections.concurrent.Future;
import com.mosaic.collections.concurrent.FutureNbl;

/**
 * Factory class for jobs that execute immediately.
 */
@SuppressWarnings( "unchecked" )
public class TryNow {

    public static final TryNbl NULL = successfulNbl(Nullable.NULL);

    /**
     * Wraps the specified operation with a try/catch block and returns
     * the result as an instance of Try.
     */
    public static <T> Try<T> tryNow( Function0ThatThrows<T> op ) {
        try {
            return successful( op.invoke() );
        } catch ( Exception e ) {
            return failed( new Failure(e) );
        }
    }

    /**
     * Factory method for Try.  Captures the specified value as a happy path result.
     */
    public static <T> Try<T> successful( T v ) {
        return Future.successful(v);
    }

    /**
     * Factory method for Try.  Captures information as to why the operation
     * failed.
     */
    public static <T> Try<T> failed( Failure f ) {
        return Future.failed(f);
    }

    /**
     * Wraps the specified operation with a try/catch block and returns
     * the result as an instance of TryNbl.
     */
    public static <T> TryNbl<T> tryNowNbl( Function0<Nullable<T>> op ) {
        try {
            return successfulNbl(op.invoke());
        } catch ( Exception e ) {
            return failedNbl( new Failure(e) );
        }
    }

    /**
     * Factory method for TryNbl.  Captures the specified value as a success case.
     */
    public static <T> TryNbl<T> successfulNbl( T v ) {
        return FutureNbl.successful( Nullable.createNullable(v) );
    }

    /**
     * Factory method for TryNbl.  Captures the specified value as a success case.
     */
    public static <T> TryNbl<T> successfulNbl( Nullable<T> v ) {
        return FutureNbl.successful(v);
    }

    /**
     * Factory method for TryNbl.  Captures information as to why the operation
     * failed.
     */
    public static <T> TryNbl<T> failedNbl( Failure f ) {
        return FutureNbl.failed(f);
    }

}
