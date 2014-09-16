package com.mosaic.io.codecs;

import com.mosaic.io.streams.CharacterStream;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.PullParser;


/**
 *
 */
public abstract class BooleanCodec {

    public abstract void encode( boolean v, CharacterStream out );
    public abstract boolean hasValue( PullParser in );
    public abstract boolean decode( PullParser in );

    public abstract int reserveWidth();


    public String toString( SystemX system, boolean v ) {
        UTF8Builder buf = new UTF8Builder(system);

        encode( v, buf );

        return buf.toString();
    }


    public static final BooleanCodec BOOLEAN_CODEC = new BooleanCodec() {
        public void encode( boolean v, CharacterStream out ) {
            out.writeBoolean( v );
        }

        public boolean hasValue( PullParser in ) {
            return in.hasBoolean();
        }

        public boolean decode( PullParser in ) {
            QA.isTrue( hasValue( in ), "parse called when hasValue() returns false" );

            return in.pullBoolean();
        }

        public int reserveWidth() {
            return 5;
        }
    };
}
