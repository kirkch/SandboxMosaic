package com.mosaic.parser.graph.builder;

import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;
import com.mosaic.lang.CharacterPredicate;
import com.mosaic.lang.CharacterPredicates;
import org.junit.Test;

import static com.mosaic.parser.graph.TrieAssertions.assertGraphEquals;
import static com.mosaic.lang.CaseSensitivity.CaseInsensitive;
import static com.mosaic.lang.CaseSensitivity.CaseSensitive;
import static org.junit.Assert.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public class OptionalOpTest {

    private CharacterPredicate a = CharacterPredicates.constant( 'a' );


    @Test
    public void givenBlankStartingNode_appendACaseSensitive_expectSingleTransition() {
        Node s  = new Node();
        TrieBuilderOp op = new OptionalOp( new StringOp("a", CaseSensitive) );

        Nodes endNodes = op.appendTo( s );


        assertEquals( 2, endNodes.size() );
        assertSame( s, endNodes.get(1) );

        String[] expected = new String[] {
            "1 -a-> 2"
        };

        assertGraphEquals( s, expected );
    }

    @Test
    public void givenBlankStartingNode_appendACaseInsensitive_expectSingleTransition() {
        Node s  = new Node();
        TrieBuilderOp op = new OptionalOp( new StringOp("a", CaseInsensitive) );

        op.appendTo( s );


        String[] expected = new String[] {
            "1 -[Aa]-> 2"
        };

        assertGraphEquals( s, expected );
    }



    @Test
    public void givenSingleExistingTransition_appendACaseInsensitive_expectSingleTransitionThenLoopBack() {
        Node n1  = new Node();
        Node n2  = new Node();
        TrieBuilderOp op = new OptionalOp( new StringOp("b", CaseSensitive) );

        n1.append( a, n2 );
        op.appendTo( n2 );


        String[] expected = new String[] {
            "1 -a-> 2 -b-> 3"
        };

        assertGraphEquals( n1, expected );
    }

    @Test
    public void givenBlankStartingNode_appendAorBWithDifferentEdges_expectBothAAndBToLoopBack() {
        Node n1  = new Node();

        TrieBuilderOp op = new OptionalOp(
            new OrOp(new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive))
        );

        Nodes nextNodes = op.appendTo( n1 );


        String[] expected = new String[] {
            "1 -a-> 2",
            "  -b-> 3"
        };

        assertGraphEquals( n1, expected );

        assertEquals( 3, nextNodes.size() );
        assertTrue( nextNodes.contains(n1) );
    }





// toString

    @Test
    public void givenConstantA_toString() {
        TrieBuilderOp op = new OptionalOp( new StringOp("a", CaseSensitive) );

        assertEquals( "(a)?", op.toString() );
    }

    @Test
    public void givenAorBWithDifferentEdges_toString() {
        TrieBuilderOp op = new OptionalOp( new OrOp(new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive)) );

        assertEquals( "(a|b)?", op.toString() );
    }
    
}
