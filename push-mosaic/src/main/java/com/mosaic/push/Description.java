package com.mosaic.push;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * DTO class for providing documentation at runtime.
 */
public class Description {

    private String headline;
    private String description;

    public Description( String headline, String description ) {
        this.headline = headline;
        this.description = description;
    }

    /**
     * Short snappy description.
     */
    public String getHeadline() {
        return headline;
    }

    /**
     * All of the full meat.
     */
    public String getDescription() {
        return description;
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
