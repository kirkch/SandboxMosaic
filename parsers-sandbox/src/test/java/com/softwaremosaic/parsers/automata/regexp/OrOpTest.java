package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.LabelNode;
import com.softwaremosaic.parsers.automata.Nodes;
import org.junit.Test;

import static com.softwaremosaic.parsers.automata.GraphAssertions.assertGraphEquals;
import static com.softwaremosaic.parsers.automata.regexp.GraphBuilder.CaseSensitivity.*;
import static org.junit.Assert.assertEquals;

/**
*
*/
@SuppressWarnings("unchecked")
public class OrOpTest {

    @Test
    public void givenBlankStartingNode_appendAorB_ExpectTwoEdgesToDifferentNodes() {
        LabelNode s  = new LabelNode();
        GraphBuilder op = new OrOp( new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive) );

        Nodes endNodes = op.appendTo( s );

        assertGraphEquals( s,
                "1 -a-> 2e",
                "  -b-> 3e"
        );

        assertEquals( 2, endNodes.size() );
    }


// toString


    @Test
    public void appendACaseSensitive_toString() {
        GraphBuilder op = new OrOp( new StringOp("a", CaseSensitive) );

        assertEquals( "a", op.toString() );
    }
    @Test
    public void appendACaseInsensitive_toString() {
        GraphBuilder op = new OrOp( new StringOp("a", CaseInsensitive) );

        assertEquals( "~a", op.toString() );
    }

    @Test
    public void appendAorB_toString() {
        GraphBuilder op = new OrOp( new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive) );

        assertEquals( "a|b", op.toString() );
    }

    @Test
    public void appendAorBorC_toString() {
        GraphBuilder op = new OrOp( new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive), new StringOp("c", CaseSensitive) );

        assertEquals( "a|b|c", op.toString() );
    }

    @Test
    public void appendAorBorCorD_toString() {
        GraphBuilder op = new OrOp( new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive), new StringOp("c", CaseSensitive), new StringOp("d", CaseSensitive) );

        assertEquals( "a|b|c|d", op.toString() );
    }

    @Test
    public void appendABC_toString() {
        GraphBuilder op = new OrOp( new StringOp("abc", CaseSensitive) );

        assertEquals( "abc", op.toString() );
    }

    @Test
    public void appendABor12_toString() {
        GraphBuilder op = new OrOp( new StringOp("ab", CaseSensitive), new StringOp("12", CaseSensitive) );

        assertEquals( "ab|12", op.toString() );
    }

    @Test
    public void appendAorBorCorDor123_toString() {
        GraphBuilder op = new OrOp( new StringOp("a", CaseSensitive), new StringOp("b", CaseSensitive), new StringOp("c", CaseSensitive), new StringOp("123", CaseSensitive) );

        assertEquals( "a|b|c|123", op.toString() );
    }

}
