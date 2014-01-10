package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Label;
import com.softwaremosaic.parsers.automata.LabelNode;
import com.softwaremosaic.parsers.automata.Labels;
import com.softwaremosaic.parsers.automata.Node;
import org.junit.Test;

import static com.mosaic.lang.CaseSensitivity.*;
import static com.softwaremosaic.parsers.automata.GraphAssertions.assertGraphEquals;
import static org.junit.Assert.assertEquals;

/**
*
*/
@SuppressWarnings("unchecked")
public class ZeroOrMoreOpTest {

    private Label<Character> a = Labels.singleValue( 'a' );


    @Test
    public void givenBlankStartingNode_appendACaseSensitive_expectSingleTransition() {
        Node         s  = new LabelNode();
        GraphBuilder op = new ZeroOrMoreOp( new StringOp("a", CaseSensitive) );

        op.appendTo( s );


        String[] expected = new String[] {
                "1 -a-> 1"
        };

        assertGraphEquals( s, expected );
    }

    @Test
    public void givenBlankStartingNode_appendACaseInsensitive_expectSingleTransition() {
        Node s  = new LabelNode();
        GraphBuilder op = new ZeroOrMoreOp( new StringOp("a", CaseInsensitive) );

        op.appendTo( s );


        String[] expected = new String[] {
                "1 -[Aa]-> 1"
        };

        assertGraphEquals( s, expected );
    }



    @Test
    public void givenSingleExistingTransition_appendACaseInsensitive_expectSingleTransitionThenLoopBack() {
        LabelNode n1  = new LabelNode();
        LabelNode n2  = new LabelNode();
        GraphBuilder op = new ZeroOrMoreOp( new StringOp("b", CaseSensitive) );

        n1.append( a, n2 );
        op.appendTo( n2 );


        String[] expected = new String[] {
                "1 -a-> 2 -b-> 2"
        };

        assertGraphEquals( n1, expected );
    }

    @Test
    public void givenBlankStartingNode_appendAorBWithDifferentEdges_expectBothAAndBToLoopBack() {
        LabelNode n1  = new LabelNode();
        LabelNode n2  = new LabelNode();
        GraphBuilder op = new ZeroOrMoreOp( new OrOp(new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive)) );

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
        GraphBuilder op = new ZeroOrMoreOp( new StringOp("a", CaseSensitive) );

        assertEquals( "(a)*", op.toString() );
    }

    @Test
    public void givenAorBWithDifferentEdges_toString() {
        GraphBuilder op = new ZeroOrMoreOp( new OrOp(new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive)) );

        assertEquals( "(a|b)*", op.toString() );
    }
}
