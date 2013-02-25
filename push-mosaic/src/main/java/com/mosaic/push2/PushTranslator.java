package com.mosaic.push2;



/**
 *
 */
public abstract class PushTranslator<A,B> {

    protected PushTranslator( PushTranslatorDelegate<B> delegate ) {

    }

    public abstract PushTranslator<A,B> process( A data );

    public abstract PushTranslator<A,B> endOfStream();

    public abstract PushTranslator<A,B> streamError();
}
