package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;
import org.junit.Test;

import static com.softwaremosaic.parsers.automata.GraphAssertions.assertGraphEquals;
import static com.softwaremosaic.parsers.automata.regexp.AutomataOp.CaseSensitivity.CaseSensitive;
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

}
