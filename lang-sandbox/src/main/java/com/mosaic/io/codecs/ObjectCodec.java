package com.mosaic.io.codecs;

import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.text.PullParser;


/**
 *
 */
public interface ObjectCodec<T> {

    public void encode( T v, CharacterStream out );
    public boolean hasValue( PullParser in );
    public T decode( PullParser in );



    public static final ObjectCodec TOSTRING_FORMATTING_CODEC = new ObjectCodec() {
        public void encode( Object v, CharacterStream out ) {
            out.writeString( v.toString() );
        }

        public boolean hasValue( PullParser in ) {
            throw new UnsupportedOperationException( "the default toString codec does not know how to parse, you will need to create a custom instance" );
        }

        public Object decode( PullParser in ) {
            throw new UnsupportedOperationException( "the default toString codec does not know how to parse, you will need to create a custom instance" );
        }
    };
    
}
