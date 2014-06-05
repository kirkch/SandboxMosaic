package com.mosaic.io.codecs;

import com.mosaic.io.streams.CharacterStream;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.QA;
import com.mosaic.lang.text.PullParser;


/**
 * Encode/Decode int values.
 */
public abstract class IntCodec {

    public abstract void encode( int v, CharacterStream out );
    public abstract boolean hasValue( PullParser in );
    public abstract int decode( PullParser in );


    public String toString( int v ) {
        UTF8Builder buf = new UTF8Builder();

        encode( v, buf );

        return buf.toString();
    }


    public static final IntCodec INT_CODEC = new IntCodec() {
        public void encode( int v, CharacterStream out ) {
            out.writeInt( v );
        }

        public boolean hasValue( PullParser in ) {
            return in.hasInt();
        }

        public int decode( PullParser in ) {
            QA.isTrue( hasValue(in), "parse called when hasValue() returns false" );

            return in.pullInt();
        }
    };

}
