package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Label;
import com.softwaremosaic.parsers.automata.LabelNode;
import com.softwaremosaic.parsers.automata.Labels;
import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;
import org.junit.Test;

import static com.softwaremosaic.parsers.automata.GraphAssertions.assertGraphEquals;
import static com.softwaremosaic.parsers.automata.regexp.GraphBuilder.CaseSensitivity.CaseInsensitive;
import static com.softwaremosaic.parsers.automata.regexp.GraphBuilder.CaseSensitivity.CaseSensitive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 *
 */
@SuppressWarnings("unchecked")
public class OptionalOpTest {

    private Label<Character> a = Labels.singleValue( 'a' );


    @Test
    public void givenBlankStartingNode_appendACaseSensitive_expectSingleTransition() {
        Node s  = new LabelNode();
        GraphBuilder op = new OptionalOp( new StringOp("a", CaseSensitive) );

        Nodes endNodes = op.appendTo( s );


        assertEquals( 2, endNodes.size() );
        assertSame( s, endNodes.get(1) );

        String[] expected = new String[] {
            "1 -a-> 2e"
        };

        assertGraphEquals( s, expected );
    }

    @Test
    public void givenBlankStartingNode_appendACaseInsensitive_expectSingleTransition() {
        Node s  = new LabelNode();
        GraphBuilder op = new OptionalOp( new StringOp("a", CaseInsensitive) );

        op.appendTo( s );


        String[] expected = new String[] {
            "1 -[Aa]-> 2e"
        };

        assertGraphEquals( s, expected );
    }



    @Test
    public void givenSingleExistingTransition_appendACaseInsensitive_expectSingleTransitionThenLoopBack() {
        LabelNode n1  = new LabelNode();
        LabelNode n2  = new LabelNode();
        GraphBuilder op = new OptionalOp( new StringOp("b", CaseSensitive) );

        n1.append( a, n2 );
        op.appendTo( n2 );


        String[] expected = new String[] {
            "1 -a-> 2 -b-> 3e"
        };

        assertGraphEquals( n1, expected );
    }

    @Test
    public void givenBlankStartingNode_appendAorBWithDifferentEdges_expectBothAAndBToLoopBack() {
        LabelNode n1  = new LabelNode();

        GraphBuilder op = new OptionalOp(
            new OrOp(new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive))
        );

        Nodes nextNodes = op.appendTo( n1 );


        String[] expected = new String[] {
            "1 -a-> 2e",
            "  -b-> 3e"
        };

        assertGraphEquals( n1, expected );

        assertEquals( 3, nextNodes.size() );
        assertTrue( nextNodes.contains(n1) );
    }





// toString

    @Test
    public void givenConstantA_toString() {
        GraphBuilder op = new OptionalOp( new StringOp("a", CaseSensitive) );

        assertEquals( "(a)?", op.toString() );
    }

    @Test
    public void givenAorBWithDifferentEdges_toString() {
        GraphBuilder op = new OptionalOp( new OrOp(new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive)) );

        assertEquals( "(a|b)?", op.toString() );
    }
    
}
