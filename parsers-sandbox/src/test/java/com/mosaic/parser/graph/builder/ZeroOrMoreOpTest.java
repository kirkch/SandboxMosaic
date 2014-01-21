package com.mosaic.parser.graph.builder;

import com.mosaic.parser.graph.Node;
import com.mosaic.lang.CharacterPredicate;
import com.mosaic.lang.CharacterPredicates;
import org.junit.Test;

import static com.mosaic.parser.graph.TrieAssertions.assertGraphEquals;
import static com.mosaic.lang.CaseSensitivity.CaseInsensitive;
import static com.mosaic.lang.CaseSensitivity.CaseSensitive;
import static org.junit.Assert.assertEquals;


/**
*
*/
@SuppressWarnings("unchecked")
public class ZeroOrMoreOpTest {

    private CharacterPredicate a = CharacterPredicates.constant( 'a' );


    @Test
    public void givenBlankStartingNode_appendACaseSensitive_expectSingleTransition() {
        Node s  = new Node();
        NodeBuilder op = new ZeroOrMoreOp( new StringOp("a", CaseSensitive) );

        op.appendTo( s );


        String[] expected = new String[] {
                "1 -a-> 1"
        };

        assertGraphEquals( s, expected );
    }

    @Test
    public void givenBlankStartingNode_appendACaseInsensitive_expectSingleTransition() {
        Node s  = new Node();
        NodeBuilder op = new ZeroOrMoreOp( new StringOp("a", CaseInsensitive) );

        op.appendTo( s );


        String[] expected = new String[] {
                "1 -[Aa]-> 1"
        };

        assertGraphEquals( s, expected );
    }



    @Test
    public void givenSingleExistingTransition_appendACaseInsensitive_expectSingleTransitionThenLoopBack() {
        Node n1  = new Node();
        Node n2  = new Node();
        NodeBuilder op = new ZeroOrMoreOp( new StringOp("b", CaseSensitive) );

        n1.append( a, n2 );
        op.appendTo( n2 );


        String[] expected = new String[] {
                "1 -a-> 2 -b-> 2"
        };

        assertGraphEquals( n1, expected );
    }

    @Test
    public void givenBlankStartingNode_appendAorBWithDifferentEdges_expectBothAAndBToLoopBack() {
        Node n1  = new Node();
        Node n2  = new Node();
        NodeBuilder op = new ZeroOrMoreOp( new OrOp(new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive)) );

        n1.append( a, n2 );
        op.appendTo( n2 );


        String[] expected = new String[] {
                "1 -a-> 2 -a|b-> 2"
        };

        assertGraphEquals( n1, expected );
    }


    // givenBlankStartingNode_appendAB_expectABThenLoopBack
    //



// toString

    @Test
    public void givenConstantA_toString() {
        NodeBuilder op = new ZeroOrMoreOp( new StringOp("a", CaseSensitive) );

        assertEquals( "(a)*", op.toString() );
    }

    @Test
    public void givenAorBWithDifferentEdges_toString() {
        NodeBuilder op = new ZeroOrMoreOp( new OrOp(new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive)) );

        assertEquals( "(a|b)*", op.toString() );
    }
}
