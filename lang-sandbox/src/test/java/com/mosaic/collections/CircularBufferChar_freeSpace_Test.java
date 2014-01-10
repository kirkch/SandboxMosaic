package com.mosaic.collections;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class CircularBufferChar_freeSpace_Test {

    @Test
    public void givenDefaultNewlyCreatedBufferSize10_expectItToBeEmpty() {
        CircularBufferChar buf = new CircularBufferChar( 10 );

        assertEquals( 16, buf.freeSpace() );
    }

    @Test
    public void givenDefaultNewlyCreatedBufferSize3_expectItToBeEmpty() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertEquals( 4, buf.freeSpace() );
    }

    @Test
    public void appendOneCharacter_expectFreeSpaceToDrop() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append( 'a' );
        assertEquals( 3, buf.freeSpace() );
    }

    @Test
    public void appendTwoCharacters_expectFreeSpaceToDrop() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append( 'a' );
        buf.append( 'b' );
        assertEquals( 2, buf.freeSpace() );
    }

    @Test
    public void appendThreeCharacters_expectFreeSpaceToDrop() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append( 'a' );
        buf.append( 'b' );
        buf.append( 'b' );
        assertEquals( 1, buf.freeSpace() );
    }

    @Test
    public void appendCharactersToOverflowByOne_expectFreeSpaceNotToDropAfterLimitReached() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append( 'a' );
        buf.append( 'b' );
        buf.append( 'b' );
        buf.append( 'b' );
        assertEquals( 0, buf.freeSpace() );
    }


}
