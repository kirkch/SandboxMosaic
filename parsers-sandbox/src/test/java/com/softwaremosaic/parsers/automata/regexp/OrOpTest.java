package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;
import org.junit.Test;

import static com.softwaremosaic.parsers.automata.GraphAssertions.assertGraphEquals;
import static com.softwaremosaic.parsers.automata.regexp.AutomataOp.CaseSensitivity.*;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class OrOpTest {

    @Test
    public void givenBlankStartingNode_appendAorB_ExpectTwoEdgesToDifferentNodes() {
        Node s  = new Node("l1");
        AutomataOp op = new OrOp( new ConstantOp("a", CaseSensitive), new ConstantOp("b", CaseSensitive) );

        Nodes endNodes = op.appendTo( "l1", s );

        assertGraphEquals( s,
                "l1: 1 -a-> 2t",
                "      -b-> 3t"
        );

        assertEquals( 2, endNodes.size() );
    }


// toString


    @Test
    public void appendACaseSensitive_toString() {
        AutomataOp op = new OrOp( new ConstantOp("a", CaseSensitive) );

        assertEquals( "a", op.toString() );
    }
    @Test
    public void appendACaseInsensitive_toString() {
        AutomataOp op = new OrOp( new ConstantOp("a", CaseInsensitive) );

        assertEquals( "~a", op.toString() );
    }

    @Test
    public void appendAorB_toString() {
        AutomataOp op = new OrOp( new ConstantOp("a", CaseSensitive), new ConstantOp("b", CaseSensitive) );

        assertEquals( "a|b", op.toString() );
    }

    @Test
    public void appendAorBorC_toString() {
        AutomataOp op = new OrOp( new ConstantOp("a", CaseSensitive), new ConstantOp("b", CaseSensitive), new ConstantOp("c", CaseSensitive) );

        assertEquals( "a|b|c", op.toString() );
    }

    @Test
    public void appendAorBorCorD_toString() {
        AutomataOp op = new OrOp( new ConstantOp("a", CaseSensitive), new ConstantOp("b", CaseSensitive), new ConstantOp("c", CaseSensitive), new ConstantOp("d", CaseSensitive) );

        assertEquals( "a|b|c|d", op.toString() );
    }

    @Test
    public void appendABC_toString() {
        AutomataOp op = new OrOp( new ConstantOp("abc", CaseSensitive) );

        assertEquals( "abc", op.toString() );
    }

    @Test
    public void appendABor12_toString() {
        AutomataOp op = new OrOp( new ConstantOp("ab", CaseSensitive), new ConstantOp("12", CaseSensitive) );

        assertEquals( "ab|12", op.toString() );
    }

    @Test
    public void appendAorBorCorDor123_toString() {
        AutomataOp op = new OrOp( new ConstantOp("a", CaseSensitive), new ConstantOp("b", CaseSensitive), new ConstantOp("c", CaseSensitive), new ConstantOp("123", CaseSensitive) );

        assertEquals( "a|b|c|123", op.toString() );
    }

}
