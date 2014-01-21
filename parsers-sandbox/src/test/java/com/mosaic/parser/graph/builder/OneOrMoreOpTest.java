package com.mosaic.parser.graph.builder;

import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;
import com.mosaic.lang.CaseSensitivity;
import org.junit.Test;

import static com.mosaic.parser.graph.TrieAssertions.assertGraphEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;


/**
*
*/
@SuppressWarnings("unchecked")
public class OneOrMoreOpTest {

    @Test
    public void givenBlankStartingNode_appendACaseSensitive_expectOneTransitionThenLoopBack() {
        Node s  = new Node();
        NodeBuilder op = new OneOrMoreOp( new StringOp("a", CaseSensitivity.CaseSensitive) );

        Nodes endNodes = op.appendTo( s );


        String[] expected = new String[] {
                "1 -a-> 2 -a-> 2"
        };

        assertGraphEquals( s, expected );

        assertEquals( 1, endNodes.size() );
        assertNotSame( s, endNodes.get(0) );
    }

}
