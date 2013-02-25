package com.mosaic.push2;

/**
 *
 */
public interface PushTranslatorDelegate<B> {
    public void receive( B data );

    public void error();

    public void endOfStream();
}
