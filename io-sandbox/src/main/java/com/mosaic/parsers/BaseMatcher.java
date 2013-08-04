package com.mosaic.parsers;

/**
 *
 */
public abstract class BaseMatcher implements Matcher {

    private String name;
    private String callback;

    public Matcher withName( String name ) {
        this.name = name;

        return this;
    }

    public Matcher withCallback( String callbackMethodName ) {
        this.callback = callbackMethodName;

        return this;
    }


    public String getName() {
        return name;
    }

    public String getCallback() {
        return callback;
    }

}
