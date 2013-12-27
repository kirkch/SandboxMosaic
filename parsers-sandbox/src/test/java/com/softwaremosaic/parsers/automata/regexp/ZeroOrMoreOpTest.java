package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.ObjectNode;
import org.junit.Test;

import static com.softwaremosaic.parsers.automata.GraphAssertions.assertGraphEquals;
import static com.softwaremosaic.parsers.automata.regexp.GraphBuilder.CaseSensitivity.CaseInsensitive;
import static com.softwaremosaic.parsers.automata.regexp.GraphBuilder.CaseSensitivity.CaseSensitive;
import static org.junit.Assert.assertEquals;

/**
*
*/
@SuppressWarnings("unchecked")
public class ZeroOrMoreOpTest {


    @Test
    public void givenBlankStartingNode_appendACaseSensitive_expectSingleTransition() {
        Node         s  = new ObjectNode();
        GraphBuilder op = new ZeroOrMoreOp( new StringOp("a", CaseSensitive) );

        op.appendTo( s );


        String[] expected = new String[] {
                "1 -a-> 1"
        };

        assertGraphEquals( s, expected );
    }

    @Test
    public void givenBlankStartingNode_appendACaseInsensitive_expectSingleTransition() {
        Node s  = new ObjectNode();
        GraphBuilder op = new ZeroOrMoreOp( new StringOp("a", CaseInsensitive) );

        op.appendTo( s );


        String[] expected = new String[] {
                "1 -[Aa]-> 1"
        };

        assertGraphEquals( s, expected );
    }

    @Test
    public void givenSingleExistingTransition_appendACaseInsensitive_expectSingleTransitionThenLoopBack() {
        ObjectNode n1  = new ObjectNode();
        ObjectNode n2  = new ObjectNode();
        GraphBuilder op = new ZeroOrMoreOp( new StringOp("b", CaseSensitive) );

        n1.append( 'a', n2 );
        op.appendTo( n2 );


        String[] expected = new String[] {
                "1 -a-> 2 -b-> 2"
        };

        assertGraphEquals( n1, expected );
    }

    @Test
    public void givenBlankStartingNode_appendAorBWithDifferentEdges_expectBothAAndBToLoopBack() {
        ObjectNode n1  = new ObjectNode();
        ObjectNode n2  = new ObjectNode();
        GraphBuilder op = new ZeroOrMoreOp( new OrOp(new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive)) );

        n1.append( 'a', n2 );
        op.appendTo( n2 );


        String[] expected = new String[] {
                "1 -a-> 2 -[ab]-> 2"
        };

        assertGraphEquals( n1, expected );
    }


    // givenBlankStartingNode_appendAB_expectABThenLoopBack
    //



// toString

    @Test
    public void givenConstantA_toString() {
        GraphBuilder op = new ZeroOrMoreOp( new StringOp("a", CaseSensitive) );

        assertEquals( "(a)*", op.toString() );
    }

    @Test
    public void givenAorBWithDifferentEdges_toString() {
        GraphBuilder op = new ZeroOrMoreOp( new OrOp(new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive)) );

        assertEquals( "(a|b)*", op.toString() );
    }
}
