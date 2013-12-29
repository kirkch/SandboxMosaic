package com.mosaic.utils;

import com.mosaic.lang.functional.Predicate;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 */
public class EscapableCharacterIteratorTest {

    private static final Predicate<Character> PREDICATE = new Predicate<Character>() {
        public Boolean invoke( Character arg ) {
            char c = arg.charValue();

            return c == '*' || c == '+';
        }
    };


    @Test
    public void givenEmptyString_expectNoNextChar() {
        EscapableCharacterIterator it = new EscapableCharacterIterator( "", '\\', PREDICATE );

        assertFalse( it.hasNext() );
    }

    @Test
    public void givenAB_expectToBeAbleToIterateOverAB() {
        EscapableCharacterIterator it = new EscapableCharacterIterator( "ab", '\\', PREDICATE );

        assertTrue( it.hasNext() );
        assertEquals( 'a', it.next() );
        assertFalse( it.isSpecial() );
        assertTrue( it.hasNext() );
        assertEquals( 'b', it.next() );
        assertFalse( it.isSpecial() );
        assertFalse( it.hasNext() );
    }

    @Test
    public void givenAStarBStart_expectToBeAbleToIterateOverAB() {
        EscapableCharacterIterator it = new EscapableCharacterIterator( "a*b*", '\\', PREDICATE );

        assertTrue( it.hasNext() );
        assertEquals( 'a', it.next() );
        assertFalse( it.isSpecial() );
        assertTrue( it.hasNext() );

        assertEquals( '*', it.next() );
        assertTrue( it.isSpecial() );
        assertTrue( it.hasNext() );

        assertEquals( 'b', it.next() );
        assertFalse( it.isSpecial() );
        assertTrue( it.hasNext() );

        assertEquals( '*', it.next() );
        assertTrue( it.isSpecial() );
        assertFalse( it.hasNext() );
    }

    @Test
    public void givenAEscStarBStart_expectToBeAbleToIterateOverAB() {
        EscapableCharacterIterator it = new EscapableCharacterIterator( "a\\*b\\\\*", '\\', PREDICATE );

        assertTrue( it.hasNext() );
        assertEquals( 'a', it.next() );
        assertFalse( it.isSpecial() );
        assertTrue( it.hasNext() );

        assertEquals( '*', it.next() );
        assertFalse( it.isSpecial() );
        assertTrue( it.hasNext() );

        assertEquals( 'b', it.next() );
        assertFalse( it.isSpecial() );
        assertTrue( it.hasNext() );

        assertEquals( '\\', it.next() );
        assertFalse( it.isSpecial() );
        assertTrue( it.hasNext() );

        assertEquals( '*', it.next() );
        assertTrue( it.isSpecial() );
        assertFalse( it.hasNext() );
    }

}
