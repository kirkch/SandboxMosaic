package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import org.junit.Test;

import static com.softwaremosaic.parsers.automata.GraphAssertions.assertGraphEquals;
import static com.softwaremosaic.parsers.automata.regexp.AutomataOp.CaseSensitivity.CaseInsensitive;
import static com.softwaremosaic.parsers.automata.regexp.AutomataOp.CaseSensitivity.CaseSensitive;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ZeroOrMoreOpTest {


    @Test
    public void givenBlankStartingNode_appendACaseSensitive_expectSingleTransition() {
        Node s  = new Node("l1");
        AutomataOp op = new ZeroOrMoreOp( new ConstantOp("a", CaseSensitive) );

        op.appendTo( "l1", s );


        String[] expected = new String[] {
                "l1: 1 -a-> 1"
        };

        assertGraphEquals( s, expected );
    }

    @Test
    public void givenBlankStartingNode_appendACaseInsensitive_expectSingleTransition() {
        Node s  = new Node("l1");
        AutomataOp op = new ZeroOrMoreOp( new ConstantOp("a", CaseInsensitive) );

        op.appendTo( "l1", s );


        String[] expected = new String[] {
                "l1: 1 -[Aa]-> 1"
        };

        assertGraphEquals( s, expected );
    }

    @Test
    public void givenSingleExistingTransition_appendACaseInsensitive_expectSingleTransitionThenLoopBack() {
        Node n1  = new Node("l1");
        Node n2  = new Node("l1");
        AutomataOp op = new ZeroOrMoreOp( new ConstantOp("b", CaseSensitive) );

        n1.appendEdge( 'a', n2 );
        op.appendTo( "l1", n2 );


        String[] expected = new String[] {
                "l1: 1 -a-> 2 -b-> 2"
        };

        assertGraphEquals( n1, expected );
    }

    @Test
    public void givenBlankStartingNode_appendAorBWithDifferentEdges_expectBothAAndBToLoopBack() {
        Node n1  = new Node("l1");
        Node n2  = new Node("l1");
        AutomataOp op = new ZeroOrMoreOp( new OrOp(new ConstantOp("a", CaseSensitive), new ConstantOp("b", CaseSensitive)) );

        n1.appendEdge( 'a', n2 );
        op.appendTo( "l1", n2 );


        String[] expected = new String[] {
                "l1: 1 -a-> 2 -[ab]-> 2"
        };

        assertGraphEquals( n1, expected );
    }


    // givenBlankStartingNode_appendAB_expectABThenLoopBack
    //



// toString

    @Test
    public void givenConstantA_toString() {
        AutomataOp op = new ZeroOrMoreOp( new ConstantOp("a", CaseSensitive) );

        assertEquals( "(a)*", op.toString() );
    }

    @Test
    public void givenAorBWithDifferentEdges_toString() {
        AutomataOp op = new ZeroOrMoreOp( new OrOp(new ConstantOp("a", CaseSensitive), new ConstantOp("b", CaseSensitive)) );

        assertEquals( "(a|b)*", op.toString() );
    }
}
