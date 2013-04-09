package com.mosaic.parsers.push.matchers;

/**
 *
 */
public interface ZeroOrMoreCallback<T> {

    public void startOfBlockReceived( int lineNumber );

    public void valueReceived( int lineNumber, T value );

    public void endOfBlockReceived( int lineNumber );

}
