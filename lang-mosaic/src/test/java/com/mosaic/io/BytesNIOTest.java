package com.mosaic.io;

import org.junit.Test;

import java.nio.ByteBuffer;

import static junit.framework.Assert.assertEquals;

/**
 *
 */
public class BytesNIOTest extends BaseBytesTestCases {

    @Override
    protected Bytes createBytes( byte[] bytes ) {
        ByteBuffer buf = bytes == null ? null : ByteBuffer.wrap(bytes);

        return Bytes.wrapBytesBuffer( buf );
    }


    @Test
    public void givenNonEmptyByteBuffer_createBytesWrapper_expectOriginaByteBufferToNotHaveBeenModified() {
        ByteBuffer buf = ByteBuffer.wrap( new byte[] {97, 98, 99} );

        Bytes.wrapBytesBuffer( buf );

        assertEquals( 0, buf.position() );
        assertEquals( 3, buf.remaining() );
    }



    @Test
    public void given3ByteBufferWithAssertionsEnabled_mutateOriginalBufferThenCallLength_expectNoSideEffectsAndThusLengthToReturn3() {
        ByteBuffer buf   = ByteBuffer.wrap( new byte[] {97, 98, 99} );
        Bytes      bytes = Bytes.wrapBytesBuffer( buf );

        buf.get();

        assertEquals( 3, bytes.length() );
    }


}
