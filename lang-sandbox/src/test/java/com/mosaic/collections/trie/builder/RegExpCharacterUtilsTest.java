package com.mosaic.collections.trie.builder;

import com.mosaic.utils.SetUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class RegExpCharacterUtilsTest {

    private StringBuilder buf = new StringBuilder();

    @Test
    public void givenA_expectA() {
        RegExpCharacterUtils.formatCharacters( buf, SetUtils.asSet('a') );

        assertEquals( "a", buf.toString() );
    }


    @Test
    public void givenAC_expectAC() {
        RegExpCharacterUtils.formatCharacters( buf, SetUtils.asSet('a','c') );

        assertEquals( "[ac]", buf.toString() );
    }

    @Test
    public void givenABC_expectABC() {
        RegExpCharacterUtils.formatCharacters( buf, SetUtils.asSet('a','b', 'c') );

        assertEquals( "[abc]", buf.toString() );
    }

    @Test
    public void givenABCD_expectA2D() {
        RegExpCharacterUtils.formatCharacters( buf, SetUtils.asSet('a','b', 'c', 'd') );

        assertEquals( "[a-d]", buf.toString() );
    }

    @Test
    public void givenABCD0145678xyz_expect01428xyz() {
        RegExpCharacterUtils.formatCharacters( buf, SetUtils.asSet('a','b', 'c', 'd','0','1','4','5','6','7','8','x','y','z') );

        assertEquals( "[014-8a-dxyz]", buf.toString() );
    }

}
