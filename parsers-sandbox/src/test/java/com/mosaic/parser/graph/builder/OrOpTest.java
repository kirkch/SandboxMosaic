package com.mosaic.parser.graph.builder;

import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;
import org.junit.Test;

import static com.mosaic.parser.graph.TrieAssertions.assertGraphEquals;
import static com.mosaic.lang.CaseSensitivity.CaseInsensitive;
import static com.mosaic.lang.CaseSensitivity.CaseSensitive;
import static org.junit.Assert.assertEquals;


/**
*
*/
@SuppressWarnings("unchecked")
public class OrOpTest {

    @Test
    public void givenBlankStartingNode_appendAorB_ExpectTwoEdgesToDifferentNodes() {
        Node s  = new Node();
        TrieBuilderOp op = new OrOp( new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive) );

        Nodes endNodes = op.appendTo( s );

        assertGraphEquals( s,
                "1 -a-> 2",
                "  -b-> 3"
        );

        assertEquals( 2, endNodes.size() );
    }


// toString


    @Test
    public void appendACaseSensitive_toString() {
        TrieBuilderOp op = new OrOp( new StringOp("a", CaseSensitive) );

        assertEquals( "a", op.toString() );
    }
    @Test
    public void appendACaseInsensitive_toString() {
        TrieBuilderOp op = new OrOp( new StringOp("a", CaseInsensitive) );

        assertEquals( "~a", op.toString() );
    }

    @Test
    public void appendAorB_toString() {
        TrieBuilderOp op = new OrOp( new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive) );

        assertEquals( "a|b", op.toString() );
    }

    @Test
    public void appendAorBorC_toString() {
        TrieBuilderOp op = new OrOp( new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive), new StringOp("c", CaseSensitive) );

        assertEquals( "a|b|c", op.toString() );
    }

    @Test
    public void appendAorBorCorD_toString() {
        TrieBuilderOp op = new OrOp( new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive), new StringOp("c", CaseSensitive), new StringOp("d", CaseSensitive) );

        assertEquals( "a|b|c|d", op.toString() );
    }

    @Test
    public void appendABC_toString() {
        TrieBuilderOp op = new OrOp( new StringOp("abc", CaseSensitive) );

        assertEquals( "abc", op.toString() );
    }

    @Test
    public void appendABor12_toString() {
        TrieBuilderOp op = new OrOp( new StringOp("ab", CaseSensitive), new StringOp("12", CaseSensitive) );

        assertEquals( "ab|12", op.toString() );
    }

    @Test
    public void appendAorBorCorDor123_toString() {
        TrieBuilderOp op = new OrOp( new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive), new StringOp("c", CaseSensitive), new StringOp("123", CaseSensitive) );

        assertEquals( "a|b|c|123", op.toString() );
    }

}
