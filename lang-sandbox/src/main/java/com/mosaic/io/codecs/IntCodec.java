package com.mosaic.io.codecs;

import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.QA;
import com.mosaic.lang.text.PullParser;


/**
 * Encode/Decode int values.
 */
public interface IntCodec {

    public void encode( int v, CharacterStream out );
    public boolean hasValue( PullParser in );
    public int decode( PullParser in );



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
