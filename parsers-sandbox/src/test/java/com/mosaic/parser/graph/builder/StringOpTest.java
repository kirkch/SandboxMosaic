package com.mosaic.parser.graph.builder;

import com.mosaic.parser.graph.Node;
import org.junit.Test;

import static com.mosaic.parser.graph.TrieAssertions.assertGraphEquals;
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
        Node s  = new Node();
        NodeBuilder op = new StringOp( "a", CaseSensitive );

        op.appendTo( s );


        assertGraphEquals( s, "1 -a-> 2" );
    }

    @Test
    public void givenBlankStartingNode_appendACaseInsensitive_expectTwoTransitionsToOneNode() {
        Node s  = new Node();
        NodeBuilder op = new StringOp( "a", CaseInsensitive );

        op.appendTo( s );

        assertGraphEquals( s, "1 -[Aa]-> 2" );
    }

    @Test
    public void givenBlankStartingNode_appendABCaseSensitive() {
        Node s  = new Node();
        NodeBuilder op = new StringOp( "ab", CaseSensitive );

        op.appendTo( s );


        assertGraphEquals( s, "1 -a-> 2 -b-> 3" );
    }

    @Test
    public void givenBlankStartingNode_appendABCaseInsensitive() {
        Node s  = new Node();
        NodeBuilder op = new StringOp( "ab", CaseInsensitive );

        op.appendTo( s );


        assertGraphEquals( s, "1 -[Aa]-> 2 -[Bb]-> 3" );
    }



// toString

    @Test
    public void givenCaseSensitive_toString_expectLowercaseString() {
        NodeBuilder op = new StringOp( "aB", CaseSensitive );

        assertEquals( "aB", op.toString() );
    }

    @Test
    public void givenCaseInsensitive_toString_expectLowerAndUppercaseString() {
        NodeBuilder op = new StringOp( "aB", CaseInsensitive );

        assertEquals( "~ab", op.toString() );
    }

    @Test
    public void givenSpecialCharsInString_toString_escapedString() {
        NodeBuilder op = new StringOp( "~a*B", CaseSensitive );

        assertEquals( "\\~a\\*B", op.toString() );
    }

}
