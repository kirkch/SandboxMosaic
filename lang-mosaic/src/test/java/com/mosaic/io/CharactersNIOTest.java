package com.mosaic.io;

import org.junit.Test;

import java.nio.CharBuffer;

import static junit.framework.Assert.assertEquals;

/**
 *
 */
public class CharactersNIOTest extends BaseCharactersTestCases {

    @Override
    protected Characters createCharacters( char[] chars ) {
        CharBuffer buf = chars == null ? null : CharBuffer.wrap(chars);

        return Characters.wrapCharBuffer( buf );
    }


    @Test
    public void givenNonEmptyCharBuffer_createCharsWrapper_expectOriginaCharBufferToNotHaveBeenModified() {
        CharBuffer buf = CharBuffer.wrap( new char[] {97, 98, 99} );

        Characters.wrapCharBuffer( buf );

        assertEquals( 0, buf.position() );
        assertEquals( 3, buf.remaining() );
    }



    @Test
    public void given3CharBufferWithAssertionsEnabled_mutateOriginalBufferThenCallLength_expectNoSideEffectsAndThusLengthToReturn3() {
        CharBuffer buf   = CharBuffer.wrap( new char[] {97, 98, 99} );
        Characters chars = Characters.wrapCharBuffer( buf );

        buf.get();

        assertEquals( 3, chars.length() );
    }

}
