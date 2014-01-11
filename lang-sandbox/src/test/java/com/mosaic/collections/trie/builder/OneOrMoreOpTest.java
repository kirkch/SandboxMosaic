package com.mosaic.collections.trie.builder;

import com.mosaic.collections.trie.CharacterNode;
import com.mosaic.collections.trie.CharacterNodes;
import com.mosaic.lang.CaseSensitivity;
import org.junit.Test;

import static com.mosaic.collections.trie.TrieAssertions.assertGraphEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;


/**
*
*/
@SuppressWarnings("unchecked")
public class OneOrMoreOpTest {

    @Test
    public void givenBlankStartingNode_appendACaseSensitive_expectOneTransitionThenLoopBack() {
        CharacterNode s  = new CharacterNode();
        TrieBuilderOp op = new OneOrMoreOp( new StringOp("a", CaseSensitivity.CaseSensitive) );

        CharacterNodes endNodes = op.appendTo( s );


        String[] expected = new String[] {
                "1 -a-> 2 -a-> 2"
        };

        assertGraphEquals( s, expected );

        assertEquals( 1, endNodes.size() );
        assertNotSame( s, endNodes.get(0) );
    }

}
