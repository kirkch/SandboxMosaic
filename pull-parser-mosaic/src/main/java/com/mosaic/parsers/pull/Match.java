package com.mosaic.parsers.pull;

import com.mosaic.lang.Result;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 */
public class Match<T> extends Result<T,String>{

    public static <T> Match<T> match( TextPosition pos, T parsedValue ) {
        return new Match( false, pos, parsedValue );
    }

    public static <T> Match<T> failedMatch( TextPosition pos, String errorMessage ) {
        return new Match( true, pos, errorMessage );
    }

    public static <T> Match<T> none( TextPosition pos ) {
        return new Match( false, pos, null  );
    }


    private final TextPosition pos;


    protected Match( boolean isError, TextPosition pos, Object value ) {
        super( isError, value );

        this.pos  = pos;
    }

    public TextPosition getPos() {
        return pos;
    }

    public int getLine() {
        return pos.getLine();
    }

    public int getColumn() {
        return pos.getColumn();
    }


    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode( this );
    }

    public String toString() {
        return ToStringBuilder.reflectionToString( this, ToStringStyle.SHORT_PREFIX_STYLE );
    }

    public boolean equals( Object o ) {
        return EqualsBuilder.reflectionEquals( this, o );
    }
}
