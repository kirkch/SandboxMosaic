package com.mosaic.collections.trie.builder;

import com.mosaic.collections.trie.CharacterNode;
import org.junit.Test;

import static com.mosaic.collections.trie.TrieAssertions.assertGraphEquals;
import static com.mosaic.lang.CaseSensitivity.CaseInsensitive;
import static com.mosaic.lang.CaseSensitivity.CaseSensitive;
import static org.junit.Assert.assertEquals;


/**
 *
 */
@SuppressWarnings("unchecked")
public class StringOpTest {

    @Test
    public void givenBlankStartingNode_appendACaseSensitive_expectSingleTransition() {
        CharacterNode s  = new CharacterNode();
        TrieBuilderOp op = new StringOp( "a", CaseSensitive );

        op.appendTo( s );


        assertGraphEquals( s, "1 -a-> 2" );
    }

    @Test
    public void givenBlankStartingNode_appendACaseInsensitive_expectTwoTransitionsToOneNode() {
        CharacterNode s  = new CharacterNode();
        TrieBuilderOp op = new StringOp( "a", CaseInsensitive );

        op.appendTo( s );

        assertGraphEquals( s, "1 -[Aa]-> 2" );
    }

    @Test
    public void givenBlankStartingNode_appendABCaseSensitive() {
        CharacterNode s  = new CharacterNode();
        TrieBuilderOp op = new StringOp( "ab", CaseSensitive );

        op.appendTo( s );


        assertGraphEquals( s, "1 -a-> 2 -b-> 3" );
    }

    @Test
    public void givenBlankStartingNode_appendABCaseInsensitive() {
        CharacterNode s  = new CharacterNode();
        TrieBuilderOp op = new StringOp( "ab", CaseInsensitive );

        op.appendTo( s );


        assertGraphEquals( s, "1 -[Aa]-> 2 -[Bb]-> 3" );
    }



// toString

    @Test
    public void givenCaseSensitive_toString_expectLowercaseString() {
        TrieBuilderOp op = new StringOp( "aB", CaseSensitive );

        assertEquals( "aB", op.toString() );
    }

    @Test
    public void givenCaseInsensitive_toString_expectLowerAndUppercaseString() {
        TrieBuilderOp op = new StringOp( "aB", CaseInsensitive );

        assertEquals( "~ab", op.toString() );
    }

    @Test
    public void givenSpecialCharsInString_toString_escapedString() {
        TrieBuilderOp op = new StringOp( "~a*B", CaseSensitive );

        assertEquals( "\\~a\\*B", op.toString() );
    }

}
