package com.mosaic.io.codecs;

import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.QA;
import com.mosaic.lang.text.PullParser;


/**
 * Encode/Decode long values.
 */
public interface LongCodec {

    public void encode( long v, CharacterStream out );
    public boolean hasValue( PullParser in );
    public long decode( PullParser in );



    public static final LongCodec LONG2DP_CODEC = new LongCodec() {
        public void encode( long v, CharacterStream out ) {
            out.writeLong( v );
        }

        public boolean hasValue( PullParser in ) {
            return in.hasLong();
        }

        public long decode( PullParser in ) {
            QA.isTrue( hasValue(in), "parse called when hasValue() returns false" );

            return in.pullLong();
        }
    };

}
