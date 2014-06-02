package com.mosaic.io.codecs;

import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.QA;
import com.mosaic.lang.text.PullParser;


/**
 * Encode/Decode double values.
 */
public interface DoubleCodec {

    public void encode( double v, CharacterStream out );
    public boolean hasValue( PullParser in );
    public double decode( PullParser in );



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
    };

}
