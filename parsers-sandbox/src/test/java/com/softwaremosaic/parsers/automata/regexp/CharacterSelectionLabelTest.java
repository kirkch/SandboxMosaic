package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Test;
import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.PrimitiveGenerators;
import org.junit.runner.RunWith;

import static com.softwaremosaic.parsers.automata.Labels.CharacterSelectionLabel;
import static org.junit.Assert.*;

/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
public class CharacterSelectionLabelTest {

    @SuppressWarnings("UnusedDeclaration")
    private Generator<Character> CHARGEN = PrimitiveGenerators.characters();


    @Test(generators = {"CHARGEN"})
    public void emptyLabel_matchesNothing( char c ) {
        CharacterSelectionLabel label = new CharacterSelectionLabel();

        assertFalse( "'"+c+"' should not have matched '"+label+"'", label.matches( c ) );
    }

    @Test(generators = {"CHARGEN"})
    public void singleCharSelection_matchesOnlyOneCharacter( char c ) {
        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.appendCharacter( 'a' );

        if ( c == 'a' ) {
            assertTrue( "'" + c + "' should match '" + label + "'", label.matches( c ) );
        } else {
            assertFalse( "'" + c + "' should not match '" + label + "'", label.matches( c ) );
        }
    }

    @Test(generators = {"CHARGEN"})
    public void threeSingleCharSelection_matchesOnlyOneCharacter( char c ) {
        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.appendCharacter( 'a' );
        label.appendCharacter( 'd' );
        label.appendCharacter( '~' );

        if ( c == 'a' || c == 'd' || c == '~' ) {
            assertTrue( "'" + c + "' should match '" + label + "'", label.matches( c ) );
        } else {
            assertFalse( "'" + c + "' should not match '" + label + "'", label.matches( c ) );
        }
    }

    @Test(generators = {"CHARGEN"})
    public void invertThreeSingleCharSelection_matchesOnlyOneCharacter( char c ) {
        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.appendCharacter( 'a' );
        label.appendCharacter( 'd' );
        label.appendCharacter( '~' );

        label.invert();


        if ( c == 'a' || c == 'd' || c == '~' ) {
            assertFalse( "'" + c + "' should not match '" + label + "'", label.matches( c ) );
        } else {
            assertTrue( "'" + c + "' should match '" + label + "'", label.matches( c ) );
        }
    }

    @Test(generators = {"CHARGEN", "CHARGEN"})
    public void rangeSelection_matchesSpecifiedRangeCharacter( char min, char c ) {
        char max = (char) (min + 4);

        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.appendRange( min, max );

        if ( c >= min && c <= max ) {
            assertTrue( "'" + c + "' should match '" + label + "'", label.matches( c ) );
        } else {
            assertFalse( "'" + c + "' should not match '" + label + "'", label.matches( c ) );
        }
    }

    @Test(generators = {"CHARGEN", "CHARGEN"})
    public void invertRangeSelection_matchesSpecifiedRangeCharacter( char min, char c ) {
        char max = (char) (min + 4);

        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.appendRange( min, max );

        label.invert();

        if ( c >= min && c <= max ) {
            assertFalse( "'" + c + "' should not match '" + label + "'", label.matches( c ) );
        } else {
            assertTrue( "'" + c + "' should match '" + label + "'", label.matches( c ) );
        }
    }

    @Test(generators = {"CHARGEN", "CHARGEN"})
    public void invertedMixOfRangeAndSingleCharSelections_matchesSpecifiedRangeCharacter( char min, char c ) {
        char max = (char) (min + 4);

        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.appendRange( min, max );
        label.appendCharacter( 'a' );
        label.appendCharacter( 'd' );
        label.appendCharacter( 'f' );

        label.invert();


        if ( c >= min && c <= max || c == 'a' || c == 'd' || c == 'f') {
            assertFalse( "'" + c + "' should not match '" + label + "'", label.matches( c ) );
        } else {
            assertTrue( "'" + c + "' should match '" + label + "'", label.matches( c ) );
        }
    }


// toString

    @Test
    public void singleCharSelection_toString() {
        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.appendCharacter( 'a' );

        assertEquals( "[a]", label.toString() );
    }

    @Test
    public void threeSingleCharSelection_toString() {
        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.appendCharacter( 'a' );
        label.appendCharacter( 'd' );
        label.appendCharacter( '~' );

        assertEquals( "[ad~]", label.toString() );
    }

    @Test
    public void invertedThreeSingleCharSelection_toString() {
        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.appendCharacter( 'a' );
        label.appendCharacter( 'd' );
        label.appendCharacter( '~' );

        label.invert();


        assertEquals( "[^ad~]", label.toString() );
    }

    @Test
    public void singleCharSelectionWithUp_toString_expectEscape() {
        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.appendCharacter( '^' );
        label.appendCharacter( 'a' );
        label.appendCharacter( '^' );

        assertEquals( "[\\^a\\^]", label.toString() );
    }

    @Test
    public void singleCharSelectionWithCloseSquareBracket_toString_expectEscape() {
        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.appendCharacter( 'a' );
        label.appendCharacter( ']' );

        assertEquals( "[a\\]]", label.toString() );
    }

    @Test
    public void singleCharSelectionWithDash_toString_expectEscape() {
        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.appendCharacter( 'a' );
        label.appendCharacter( '-' );
        label.appendCharacter( 'd' );

        assertEquals( "[a\\-d]", label.toString() );
    }

    @Test
    public void rangeSelection_toString() {
        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.appendRange( 'a', 'd' );

        assertEquals( "[a-d]", label.toString() );
    }

    @Test
    public void mixOfRangeAndSingleCharSelections_toString() {
        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.appendRange( '0', '9' );
        label.appendCharacter( 'a' );
        label.appendCharacter( 'd' );
        label.appendCharacter( 'f' );

        assertEquals( "[0-9adf]", label.toString() );
    }

    @Test
    public void invertedMixOfRangeAndSingleCharSelections_toString() {
        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.invert();
        label.appendRange( '0', '9' );
        label.appendCharacter( 'a' );
        label.appendCharacter( 'd' );
        label.appendCharacter( 'f' );

        assertEquals( "[^0-9adf]", label.toString() );
    }

    @Test
    public void invertedRangeSelection_toString() {
        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.appendRange( 'a', 'd' );
        label.invert();

        assertEquals( "[^a-d]", label.toString() );
    }

    @Test
    public void rangeSelectionMinDash_toString_expectEscaping() {
        CharacterSelectionLabel label = new CharacterSelectionLabel();
        label.appendRange( '-', '^' );

        assertEquals( "[\\--\\^]", label.toString() );
    }

}
