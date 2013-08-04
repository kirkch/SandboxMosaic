package com.mosaic.lang;

/**
 *
 */
public class Result<S,F> {

    public static final Result NONE = new Result( false, null );

    public static <S,F> Result<S,F> success( S v ) {
        return new Result( false, v );
    }

    public static <S,F> Result<S,F> error( F v ) {
        return new Result( true, v );
    }


    private final boolean isError;
    private final S       successValue;
    private final F       errorValue;


    protected Result( boolean isError, Object value ) {
        this.isError = isError;

        if ( isError ) {
            this.successValue = null;
            this.errorValue   = (F) value;
        } else {
            this.successValue = (S) value;
            this.errorValue   = null;
        }
    }


    public S getValue() {
        return successValue;
    }

    public F getError() {
        return errorValue;
    }

    public boolean hasValue() {
        return !isError;
    }

    public boolean isError() {
        return isError;
    }

    public boolean isNone() {
        return hasValue() && successValue == null;
    }

    public boolean isDefined() {
        return hasValue() && successValue != null;
    }


//    public int hashCode() {
//        return HashCodeBuilder.reflectionHashCode( this );
//    }
//
//    public String toString() {
//        return ToStringBuilder.reflectionToString( this, ToStringStyle.SHORT_PREFIX_STYLE );
//    }
//
//    public boolean equals( Object o ) {
//        return EqualsBuilder.reflectionEquals( this, o );
//    }
}
