package com.mosaic.lang;

/**
 *
 */
@Deprecated
public class ResultDeprecated<S,F> {

    public static final ResultDeprecated NONE = new ResultDeprecated( false, null );

    public static <S,F> ResultDeprecated<S,F> success( S v ) {
        return new ResultDeprecated( false, v );
    }

    public static <S,F> ResultDeprecated<S,F> error( F v ) {
        return new ResultDeprecated( true, v );
    }


    private final boolean isError;
    private final S       successValue;
    private final F       errorValue;


    protected ResultDeprecated(boolean isError, Object value) {
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
