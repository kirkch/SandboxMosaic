package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.LabelNode;
import org.junit.Test;

import static com.softwaremosaic.parsers.automata.GraphAssertions.assertGraphEquals;
import static com.softwaremosaic.parsers.automata.regexp.GraphBuilder.CaseSensitivity.*;
import static org.junit.Assert.assertEquals;


/**
 *
 */
@SuppressWarnings("unchecked")
public class StringOpTest {

    @Test
    public void givenBlankStartingNode_appendACaseSensitive_expectSingleTransition() {
        LabelNode s  = new LabelNode();
        GraphBuilder op = new StringOp( "a", CaseSensitive );

        op.appendTo( s );


        assertGraphEquals( s, "1 -a-> 2e" );
    }

    @Test
    public void givenBlankStartingNode_appendACaseInsensitive_expectTwoTransitionsToOneNode() {
        LabelNode s  = new LabelNode();
        GraphBuilder op = new StringOp( "a", CaseInsensitive );

        op.appendTo( s );

        assertGraphEquals( s, "1 -[Aa]-> 2e" );
    }

    @Test
    public void givenBlankStartingNode_appendABCaseSensitive() {
        LabelNode s  = new LabelNode();
        GraphBuilder op = new StringOp( "ab", CaseSensitive );

        op.appendTo( s );


        assertGraphEquals( s, "1 -a-> 2 -b-> 3e" );
    }

    @Test
    public void givenBlankStartingNode_appendABCaseInsensitive() {
        LabelNode s  = new LabelNode();
        GraphBuilder op = new StringOp( "ab", CaseInsensitive );

        op.appendTo( s );


        assertGraphEquals( s, "1 -[Aa]-> 2 -[Bb]-> 3e" );
    }



// toString

    @Test
    public void givenCaseSensitive_toString_expectLowercaseString() {
        GraphBuilder op = new StringOp( "aB", CaseSensitive );

        assertEquals( "aB", op.toString() );
    }

    @Test
    public void givenCaseInsensitive_toString_expectLowerAndUppercaseString() {
        GraphBuilder op = new StringOp( "aB", CaseInsensitive );

        assertEquals( "~ab", op.toString() );
    }

    @Test
    public void givenSpecialCharsInString_toString_escapedString() {
        GraphBuilder op = new StringOp( "~a*B", CaseSensitive );

        assertEquals( "\\~a\\*B", op.toString() );
    }

}
