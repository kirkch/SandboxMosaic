package com.mosaic.io.codecs;

import com.mosaic.io.streams.CharacterStream;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.text.PullParser;


/**
 *
 */
public abstract class ObjectCodec<T> {

    public abstract void encode( T v, CharacterStream out );
    public abstract boolean hasValue( PullParser in );
    public abstract T decode( PullParser in );
    public abstract int reserveWidth();

    public String toString( T v ) {
        UTF8Builder buf = new UTF8Builder();

        encode( v, buf );

        return buf.toString();
    }


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

        public int reserveWidth() {
            return 40;
        }
    };
    
}
