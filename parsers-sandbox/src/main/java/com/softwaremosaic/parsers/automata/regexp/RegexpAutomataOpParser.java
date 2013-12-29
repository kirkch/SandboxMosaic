package com.softwaremosaic.parsers.automata.regexp;

import com.mosaic.collections.FastStack;
import com.mosaic.lang.functional.Predicate;
import com.mosaic.utils.EscapableCharacterIterator;
import com.softwaremosaic.parsers.automata.Labels;

import static com.softwaremosaic.parsers.automata.regexp.GraphBuilder.CaseSensitivity.CaseInsensitive;
import static com.softwaremosaic.parsers.automata.regexp.GraphBuilder.CaseSensitivity.CaseSensitive;
import static com.softwaremosaic.parsers.automata.regexp.RegExpCharacterUtils.isSpecialChar;


/**
 * Converts a text definition of a regular expression into an instance of
 * GraphBuilder.  The regular expression can then be appended to any node within
 * an automata, as many times as required and in any place.
 *
 * Supports
 *
 * abc
 * abc*
 * (abc)*
 * abc?
 * (abc)?
 * abc+
 * (abc)+
 *
 * ~abc
 *
 * [abc]
 * [a-d]
 *
 *
 * todo
 * ~[abc]
 * [^abc]
 * [abc0-9]
 * .
 */
@SuppressWarnings("unchecked")
public class RegexpAutomataOpParser {

    private static final Predicate<Character> PREDICATE = new Predicate<Character>() {
        public Boolean invoke( Character arg ) {
            return RegExpCharacterUtils.isSpecialChar( arg );
        }
    };


    private FastStack<GraphBuilder> opStack = new FastStack();


    public GraphBuilder parse( String regexpString ) {
        EscapableCharacterIterator it = new EscapableCharacterIterator(regexpString, '\\', PREDICATE);


        int pos = 0;
        do {
            pos = parseNext( pos, regexpString );
        } while ( pos < regexpString.length() );

        assert opStack.size() == 1;

        return opStack.pop();
    }


    private int parseNext( int i, String str ) {
        if ( i >= str.length() ) {
            return i;
        }

        char c = str.charAt(i);
        switch ( c ) {
//            case '\\':
//                return parseConstant( i+1, str );
            case '*':
                return parseZeroOrMore(i, str);
            case '+':
                return parseOneOrMore( i, str );
            case '?':
                return parseOptional( i, str );
            case '[':
                return parseCharacterSelection( i, str );
            case '|':
                int next = parseNext( i+1, str );

                parseOr(i, str);

                return next;
            case '(':
                int expectedBracketPos = parseNext( i + 1, str );

                char nextChar = charAtNbl( expectedBracketPos, str );

                while ( nextChar != ')' && isSpecialChar(nextChar) ) {
                    expectedBracketPos = parseNext( expectedBracketPos, str );

                    nextChar = charAtNbl( expectedBracketPos, str );
                }

                if ( nextChar != ')' ) {
                    throw new IllegalArgumentException( "Expected closing bracket ')' at index "+expectedBracketPos+ " of '"+str+"'" );
                }

                return expectedBracketPos+1;
            case '~':
            default:
                return parseConstant( i, str );
        }
    }

    private char charAtNbl( int i, String str ) {
        return i >= str.length() ? 0 : str.charAt(i);
    }

    private int parseZeroOrMore( int i, String str ) {
        assert str.charAt(i) == '*';

        opStack.push( new ZeroOrMoreOp(opStack.pop()) );

        return i+1;
    }

    private int parseOneOrMore( int i, String str ) {
        assert str.charAt(i) == '+';

        opStack.push( new OneOrMoreOp(opStack.pop()) );

        return i+1;
    }

    private int parseOptional( int i, String str ) {
        assert str.charAt(i) == '?';

        opStack.push( new OptionalOp(opStack.pop()) );

        return i+1;
    }

    private int parseCharacterSelection( int fromInc, String str ) {
        assert str.charAt(fromInc) == '[';

        Labels.CharacterSelectionLabel label = Labels.characterSelection();

        opStack.push( new LabelOp(label) );

        int max = str.length();
        for ( int i=fromInc+1; i< max; i++ ) {
            char c = str.charAt( i );

            if ( c == '\\' && i+1 < max ) {                    // escaped char
                label.appendCharacter( str.charAt(i+1) );

                i += 1;
            } else if (c == '^' && i == fromInc+1 ) {          // inverted set eg [^abc]
                label.invert();
            } else if ( c == ']' ) {                           // end of set
                return i+1;
            } else if ( i+2 < max ) {                          // range handling
                char nextChar     = str.charAt(i+1);

                if ( nextChar == '-' ) {
                    char nextNextChar = str.charAt(i+2);

                    label.appendRange( c, nextNextChar );

                    i += 2;
                } else {
                    label.appendCharacter( c );
                }
            } else {
                label.appendCharacter( c );
            }
        }

        throw new IllegalArgumentException( "Unable to find closing ']' after '[' at index " + fromInc + " of '"+str+"'"  );
    }

    private void parseOr( int i, String str ) {
        assert str.charAt(i) == '|';

        GraphBuilder b = opStack.pop();
        GraphBuilder a = opStack.pop();

        if ( a.getClass() == OrOp.class ) {
            OrOp op = (OrOp) a;

            op.add( b );

            opStack.push( op );
        } else {
            opStack.push( new OrOp(a,b) );
        }
    }

    private int parseConstant( int i, String str ) {
        GraphBuilder op;

        int endExc = nextSpecialCharacter( i+1, str );

        if ( str.charAt(i) == '~' ) {
            op = new StringOp(str.substring(i+1,endExc), CaseInsensitive );
        } else {
            op = new StringOp(str.substring(i,endExc), CaseSensitive );
        }

        opStack.push( op );

        return endExc;
    }

    private int nextSpecialCharacter( int fromInc, String str ) {
        for ( int i=fromInc; i<str.length(); i++ ) {
            char c = str.charAt(i);

            if ( isSpecialChar( c ) ) {
                return i;
            }
        }

        return str.length();
    }

}
