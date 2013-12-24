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
public class AndOpTest {

    @Test
    public void givenBlankStartingNode_appendAThenB_expectTwoEdgesThenLoopBack() {
        Node s  = new Node("l1");
        AutomataOp op = new AndOp( new ConstantOp("a", CaseSensitive), new ConstantOp("b", CaseSensitive) );

        Nodes endNodes = op.appendTo( "l1", s );

        assertGraphEquals( s,
                "l1: 1 -a-> 2 -b-> 3t"
        );

        assertEquals( 1, endNodes.size() );
    }

    @Test
    public void givenBlankStartingNode_appendAOrBTHENCorD() {
        Node s  = new Node("l1");
        AutomataOp or1 = new OrOp( new ConstantOp("a", CaseSensitive), new ConstantOp("b", CaseSensitive) );
        AutomataOp or2 = new OrOp( new ConstantOp("c", CaseSensitive), new ConstantOp("d", CaseSensitive) );
        AutomataOp op = new AndOp( or1, or2 );

        Nodes endNodes = op.appendTo( "l1", s );

        assertGraphEquals( s,
                "l1: 1 -a-> 2 -c-> 3t",
                "             -d-> 4t",
                "      -b-> 5 -c-> 6t",
                "             -d-> 7t"
        );

        assertEquals( 4, endNodes.size() );
    }

}
