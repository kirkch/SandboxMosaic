package com.mosaic.parser.graph.builder;

import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;
import org.junit.Test;

import static com.mosaic.parser.graph.TrieAssertions.assertGraphEquals;
import static com.mosaic.lang.CaseSensitivity.CaseSensitive;
import static org.junit.Assert.assertEquals;


/**
*
*/
@SuppressWarnings("unchecked")
public class AndOpTest {

    @Test
    public void givenBlankStartingNode_appendAThenB_expectTwoEdgesThenLoopBack() {
        Node s  = new Node();
        TrieBuilderOp op = new AndOp( new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive) );

        Nodes endNodes = op.appendTo( s );

        assertGraphEquals( s,
                "1 -a-> 2 -b-> 3"
        );

        assertEquals( 1, endNodes.size() );
    }

    @Test
    public void givenBlankStartingNode_appendAOrBTHENCorD() {
        Node s   = new Node();
        TrieBuilderOp or1 = new OrOp( new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive) );
        TrieBuilderOp or2 = new OrOp( new StringOp("c", CaseSensitive), new StringOp("d", CaseSensitive) );
        TrieBuilderOp op  = new AndOp( or1, or2 );

        Nodes endNodes = op.appendTo( s );

        assertGraphEquals( s,
                "1 -a-> 2 -c-> 3",
                "         -d-> 4",
                "  -b-> 5 -c-> 6",
                "         -d-> 7"
        );

        assertEquals( 4, endNodes.size() );
    }

}
