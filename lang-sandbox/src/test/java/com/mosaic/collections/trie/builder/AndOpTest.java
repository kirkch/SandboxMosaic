package com.mosaic.collections.trie.builder;

import com.mosaic.collections.trie.CharacterNode;
import com.mosaic.collections.trie.CharacterNodes;
import org.junit.Test;

import static com.mosaic.collections.trie.TrieAssertions.assertGraphEquals;
import static com.mosaic.lang.CaseSensitivity.CaseSensitive;
import static org.junit.Assert.assertEquals;


/**
*
*/
@SuppressWarnings("unchecked")
public class AndOpTest {

    @Test
    public void givenBlankStartingNode_appendAThenB_expectTwoEdgesThenLoopBack() {
        CharacterNode s  = new CharacterNode();
        TrieBuilderOp op = new AndOp( new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive) );

        CharacterNodes endNodes = op.appendTo( s );

        assertGraphEquals( s,
                "1 -a-> 2 -b-> 3"
        );

        assertEquals( 1, endNodes.size() );
    }

    @Test
    public void givenBlankStartingNode_appendAOrBTHENCorD() {
        CharacterNode s   = new CharacterNode();
        TrieBuilderOp or1 = new OrOp( new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive) );
        TrieBuilderOp or2 = new OrOp( new StringOp("c", CaseSensitive), new StringOp("d", CaseSensitive) );
        TrieBuilderOp op  = new AndOp( or1, or2 );

        CharacterNodes endNodes = op.appendTo( s );

        assertGraphEquals( s,
                "1 -a-> 2 -c-> 3",
                "         -d-> 4",
                "  -b-> 5 -c-> 6",
                "         -d-> 7"
        );

        assertEquals( 4, endNodes.size() );
    }

}
