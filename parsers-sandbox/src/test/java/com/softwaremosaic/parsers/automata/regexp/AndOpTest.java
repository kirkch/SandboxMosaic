package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;
import com.softwaremosaic.parsers.automata.LabelNode;
import org.junit.Test;

import static com.softwaremosaic.parsers.automata.GraphAssertions.assertGraphEquals;
import static com.softwaremosaic.parsers.automata.regexp.GraphBuilder.CaseSensitivity.CaseSensitive;
import static org.junit.Assert.assertEquals;

/**
*
*/
@SuppressWarnings("unchecked")
public class AndOpTest {

    @Test
    public void givenBlankStartingNode_appendAThenB_expectTwoEdgesThenLoopBack() {
        Node         s  = new LabelNode();
        GraphBuilder op = new AndOp( new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive) );

        Nodes endNodes = op.appendTo( s );

        assertGraphEquals( s,
                "1 -a-> 2 -b-> 3e"
        );

        assertEquals( 1, endNodes.size() );
    }

    @Test
    public void givenBlankStartingNode_appendAOrBTHENCorD() {
        Node         s   = new LabelNode();
        GraphBuilder or1 = new OrOp( new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive) );
        GraphBuilder or2 = new OrOp( new StringOp("c", CaseSensitive), new StringOp("d", CaseSensitive) );
        GraphBuilder op  = new AndOp( or1, or2 );

        Nodes endNodes = op.appendTo( s );

        assertGraphEquals( s,
                "1 -a-> 2 -c-> 3e",
                "         -d-> 4e",
                "  -b-> 5 -c-> 6e",
                "         -d-> 7e"
        );

        assertEquals( 4, endNodes.size() );
    }

}
