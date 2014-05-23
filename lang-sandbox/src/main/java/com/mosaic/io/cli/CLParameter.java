package com.mosaic.io.cli;

import com.mosaic.collections.ConsList;


/**
 *
 */
public interface CLParameter<T> {

    public T getValue();

    /**
     * Try to match the head of the unprocessedInput list.  If there is not match then
     * unprocessedInput will be returned as is.  If however it matches, then this parameter will
     * update itself and removes the processed parts of the args and returns that as the result.
     *
     * @return unprocessedInput the updated list of unprocessedInput after this method has
     *    'consumed' the parts that it has processed
     */
    public ConsList<String> tryToConsumeInput( ConsList<String> unprocessedInput );

    public String getLongName();

    public void setValue( String v );
}
