package com.mosaic.bytes.struct;

import com.mosaic.lang.text.UTF8;


/**
 * Provides access to a UTF8 encoded string field within a structured record.
 */
public interface StringFieldUTF8 {
    public UTF8 get();
    public void set( UTF8 newValue );

    /**
     * Avoids object allocations by pointing the supplied UTF8 object at the same underlying
     * bytes as this UTF8 StringField.
     */
    public void getInto( UTF8 flyweight );
}
