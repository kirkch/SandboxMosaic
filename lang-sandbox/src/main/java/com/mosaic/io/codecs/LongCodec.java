package com.mosaic.io.codecs;

import com.mosaic.io.streams.CharacterStream;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.PullParser;


/**
 * Encode/Decode long values.
 */
public abstract class LongCodec {

    public abstract void encode( long v, CharacterStream out );
    public abstract boolean hasValue( PullParser in );
    public abstract long decode( PullParser in );

    public abstract int reserveWidth();


    public String toString( SystemX system, long v ) {
        UTF8Builder buf = new UTF8Builder(system);

        encode( v, buf );

        return buf.toString();
    }


    public static final LongCodec LONG_CODEC = new LongCodec() {
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

        public int reserveWidth() {
            return 19;
        }
    };

}
