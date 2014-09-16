package com.mosaic.io.codecs;

import com.mosaic.io.streams.CharacterStream;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.PullParser;


/**
 * Encode/Decode float values.
 */
public abstract class FloatCodec {

    public abstract void encode( float v, CharacterStream out );
    public abstract boolean hasValue( PullParser in );
    public abstract float decode( PullParser in );
    public abstract int reserveWidth();


    public String toString( SystemX system, float v ) {
        UTF8Builder buf = new UTF8Builder(system);

        encode( v, buf );

        return buf.toString();
    }


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

        public int reserveWidth() {
            return 9;
        }
    };
}
