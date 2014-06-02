package com.mosaic.io.codecs;

import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.QA;
import com.mosaic.lang.text.PullParser;


/**
 * Encode/Decode float values.
 */
public interface FloatCodec {

    public void encode( float v, CharacterStream out );
    public boolean hasValue( PullParser in );
    public float decode( PullParser in );



    public static final FloatCodec FLOAT2DP_CODEC = new FloatCodec() {
        public void encode( float v, CharacterStream out ) {
            out.writeFloat( v, 2 );
        }

        public boolean hasValue( PullParser in ) {
            return in.hasFloat();
        }

        public float decode( PullParser in ) {
            QA.isTrue( hasValue(in), "parse called when hasValue() returns false" );

            return in.pullFloat();
        }
    };

}
