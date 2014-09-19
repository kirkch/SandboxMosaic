package com.mosaic.io.codecs;

import com.mosaic.bytes.ArrayBytes;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.PullParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class FloatCodecTest {

    private SystemX system = new DebugSystem();


    @Test
    public void encode3dp_roundDown() {
        FloatCodec codec = FloatCodec.FLOAT2DP_CODEC;

        assertEncode( "3.14", codec, 3.141f );
    }

    @Test
    public void encode3dp_roundUp() {
        FloatCodec codec = FloatCodec.FLOAT2DP_CODEC;

        assertEncode( "3.15", codec, 3.145f );
    }

    @Test
    public void decode3dp() {
        FloatCodec codec = FloatCodec.FLOAT2DP_CODEC;

        assertDecode( "3.141", codec, 3.141f );
    }

    @Test
    public void decodeNeg3dp() {
        FloatCodec codec = FloatCodec.FLOAT2DP_CODEC;

        assertDecode( "-3.141", codec, -3.141f );
    }

    private void assertDecode( String input, FloatCodec codec, float expectation ) {
        PullParser parser = new PullParser( new ArrayBytes(input) );

        assertEquals( 0, parser.getPosition() );
        assertEquals( expectation, codec.decode( parser ), 1e-6 );
        assertEquals( input.length(), parser.getPosition() );
    }

    private void assertEncode( String expectation, FloatCodec codec, float v ) {
        UTF8Builder buf = new UTF8Builder(system);

        codec.encode( v, buf );

        assertEquals( expectation, buf.toString() );
    }

}
