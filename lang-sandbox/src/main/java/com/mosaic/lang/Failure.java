package com.mosaic.lang;


import java.util.Objects;

/**
 * Represents an error.  Cheaper and less exceptional than java.lang.Exception and less
 * fatal than java.lang.Error; making this class suitable for representing less
 * exceptional cases such as parse exceptions for human input and so forth.<p/>
 *
 * That said, a Failure may still be caused by a more expensive exception and thus
 * this class can also wrap an instance of java.lang.Throwable.<p/>
 *
 * Failures may also be chained together, after all an error may occur while handing
 * a failure.  In which case we do not want to loose information on the root of
 * the problem.
 */
public class Failure {

    private Class     source;
    private String    message;
    private Throwable ex;
    private Failure   chained;


    public Failure( Class source, String message ) {
        this.source  = source;
        this.message = message;
    }

    public Failure( Throwable ex ) {
        this.source  = ex.getClass();
        this.message = ex.getMessage();
        this.ex      = ex;
    }

    /**
     * Chain a series of failures together.  While handling the failure f a new
     * exception ex occurred.
     */
    public Failure( Failure previousFailure, Throwable newException ) {
        this.source  = previousFailure.getSource();
        this.message = previousFailure.getMessage();
        this.chained = previousFailure;
        this.ex      = newException;
    }

    /**
     * Chain a series of failures together.  While handling the failure f a new
     * exception ex occurred.
     */
    public Failure( Failure previousFailure, Failure newFailure ) {
        this.source  = newFailure.getSource();
        this.message = newFailure.getMessage();
        this.chained = previousFailure;
    }

    /**
     * The class that generated this failure.  If an instance of exception is
     * available then the stack trace from there will be more accurate.  However
     * for cases where generating an exception is viewed as too expensive then
     * having the source only is helpful.
     */
    public Class getSource() {
        return source;
    }

    /**
     * A free text description of the problem.
     */
    public String getMessage() {
        return message;
    }


    @Override
    public int hashCode() {
        return source.hashCode();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append( "Failure(" );
        buf.append( source );
        buf.append( ", " );
        buf.append( message );

        if ( chained != null ) {
            buf.append( ", " );
            buf.append( chained );
        }

        if ( ex != null ) {
            buf.append( ", " );
            buf.append( ex );
        }

        buf.append( ")" );

        return buf.toString();
    }

    public boolean equals( Object o ) {
        if ( !(o instanceof Failure) ) {
            return false;
        } else if ( o == this ) {
            return true;
        }

        Failure other = (Failure) o;
        return this.source == other.source
                && Objects.equals(this.message, other.message)
                && Objects.equals(this.chained, other.chained)
                && equalsExceptions(this.ex, other.ex);
    }

    private boolean equalsExceptions( Throwable a, Throwable b ) {
        if ( a == b ) {
            return true;
        } else if ( a == null || b == null ) {
            return false;
        }

        return Objects.equals(a.getClass(), b.getClass() )
                && Objects.equals(a.getMessage(), b.getMessage() );
    }

}
