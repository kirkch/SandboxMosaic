package com.mosaic.io.codecs;

import com.mosaic.io.streams.CharacterStream;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.QA;
import com.mosaic.lang.text.PullParser;


/**
 * Encode/Decode double values.
 */
public abstract class DoubleCodec {

    public abstract void encode( double v, CharacterStream out );
    public abstract boolean hasValue( PullParser in );
    public abstract double decode( PullParser in );
    public abstract int reserveWidth();


    public String toString( double v ) {
        UTF8Builder buf = new UTF8Builder();

        encode( v, buf );

        return buf.toString();
    }

    public static final DoubleCodec DOUBLE2DP_CODEC = new DoubleCodec() {
        public void encode( double v, CharacterStream out ) {
            out.writeDouble( v, 2 );
        }

        public boolean hasValue( PullParser in ) {
            return in.hasDouble();
        }

        public double decode( PullParser in ) {
            QA.isTrue( hasValue(in), "parse called when hasValue() returns false" );

            return in.pullDouble();
        }

        public int reserveWidth() {
            return 19;
        }
    };

}
