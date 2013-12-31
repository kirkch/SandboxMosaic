package com.softwaremosaic.parsers.automata.regexp;

import com.mosaic.collections.FastStack;
import com.mosaic.lang.functional.Function0;
import com.mosaic.lang.functional.Stream;
import com.mosaic.utils.StringUtils;
import com.softwaremosaic.parsers.automata.Labels;

import static com.softwaremosaic.parsers.automata.regexp.GraphBuilder.CaseSensitivity.CaseSensitive;


/**
 * Converts a text definition of a regular expression into an instance of
 * GraphBuilder.  The regular expression can then be appended to any node within
 * an automata, as many times as required and in any place.
 *
 * Supports
 *
 * abc             constant str abc
 * abc*            abc zero or more times
 * (abc)*          abc zero or more times
 * abc?            abc zero or once
 * (abc)?          abc zero or once
 * abc+            abc one or more times
 * (abc)+          abc one or more times
 *
 * ~abc            case insensitive abc
 *
 * [abc]           a or b or c
 * [a-d]           a or b or c or d
 * [^abc]          any char other than a, b or c
 * [abc0-9]        a, b, c, 0, 1, 2, 3, 4, 5, 6, 7, 8 or 9
 * .               any character
 *
 *
 * todo
 * ~[abc]
 * review escaping
 */
@SuppressWarnings("unchecked")
public class RegexpParser {

    public static GraphBuilder compile( String regexp ) {
        return new RegexpParser().parse( regexp );
    }



    private FastStack<GraphBuilder> opStack = new FastStack();


    public GraphBuilder parse( String regexpString ) {
        Stream<Token> tokens = unescape( 0, regexpString );

        while ( tokens.notEmpty() ) {
            tokens = parseNextFragment( tokens );
        }

        assert opStack.size() > 0 : "op stack should not be empty";

        if ( opStack.size() > 1 ) {
            AndOp andOp = new AndOp( opStack.popAll() );

            opStack.push( andOp );
        }

        return opStack.pop();
    }


    private Stream<Token> parseNextFragment( Stream<Token> in ) {
        Token next = in.head();

        if ( next.isSpecial ) {
            return parseSpecial( in );
        } else {
            return parseConstant( in );
        }
    }

    private Stream<Token> parseSpecial( Stream<Token> in ) {
        char c = in.head().c;

        switch ( c ) {
            case '~':
            {
                Stream<Token> next = parseNextFragment( in.tail() );

                opStack.peek().insensitive(true);

                return next;
            }
            case '*':
                opStack.push( new ZeroOrMoreOp(opStack.pop()) );

                return in.tail();
            case '.':
                opStack.push( new LabelOp(Labels.appendAnyCharacter()) );

                return in.tail();
            case '+':
                opStack.push( new OneOrMoreOp(opStack.pop()) );

                return in.tail();
            case '?':
                opStack.push( new OptionalOp(opStack.pop()) );

                return in.tail();
            case '|':
            {
                Stream<Token> next = parseNextFragment( in.tail() );

                orTopTwoOpsOnStack();

                return next;
            }
            case '[':
                return parseCharacterSelection(in);
            case '(':
            {
                Stream<Token> next = parseNextFragment( in.tail() );

                while ( next.notEmpty() && next.head().c != ')' ) {
                    next = parseNextFragment( next );
                }

                if ( next.isEmpty() || !next.head().isSpecial || next.head().c != ')' ) {
                    throw new IllegalArgumentException( "Expected closing bracket ')' but found instead '"+ StringUtils.join(next,"")+"'");
                }

                return next.tail();
            }
            default:
                throw new IllegalStateException( "Unrecognised special char '"+c+"' with remaining chars '"+ StringUtils.join(in,"")+"'");
        }
    }

    private Stream<Token> parseCharacterSelection( Stream<Token> input ) {
        Labels.CharacterSelectionLabel label = Labels.characterSelection();

        opStack.push( new LabelOp(label) );

        Stream<Token> pos = input.tail();

        if ( pos.head().isSpecial && pos.head().c == '^' ) {
            label.invert();

            pos = pos.tail();
        }

        while ( pos.notEmpty() ) {
            Token token = pos.head();
            char  c     = token.c;

            if ( token.isSpecial ) {
                if ( c == ']' ) {
                    return pos.tail();
                } else {
                    label.appendCharacter( c );
                }
            } else if ( isRange(pos) ) {
                pos = pos.tail().tail();

                label.appendRange( c, pos.head().c );
            } else {
                label.appendCharacter( c );
            }

            pos = pos.tail();
        }

//        throw new IllegalArgumentException( "Unable to find closing ']' after '[' at index " + fromInc + " of '"+str+"'"  );
        return pos;
    }

    private boolean isRange( Stream<Token> pos ) {
        Stream<Token> tail = pos.tail();

        Token nextToken = tail.head();
        return tail.notEmpty() && nextToken.isSpecial && nextToken.c == '-';
    }

    private void orTopTwoOpsOnStack() {
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

    private Stream<Token> parseConstant( Stream<Token> in ) {
        StringBuilder buf = new StringBuilder();

        Stream<Token> pos = in;
        while ( pos.notEmpty() ) {
            Token token = pos.head();

            if ( token.isSpecial ) {
                break;
            } else {
                buf.append( token.c );
            }

            pos = pos.tail();
        }

        opStack.push( new StringOp(buf.toString(), CaseSensitive) );

        return pos;
    }


    private static class Token {
        public final char    c;
        public final boolean isSpecial;

        public Token( char c, boolean special ) {
            this.c = c;
            isSpecial = special;
        }

        public String toString() {
            return Character.toString( c );
        }
    }

    private static Stream<Token> unescape( int i, final String str ) {
        if ( i >= str.length() ) {
            return Stream.EMPTY;
        }

              char    c = str.charAt( i );
              boolean isSpecial;
        final int     nextIndex;
        if ( c == '\\' ) {
            c = str.charAt( i+1 );

            nextIndex = i+2;
            isSpecial = false;
        } else {
            isSpecial = RegExpCharacterUtils.isSpecialChar( c );
            nextIndex = i + 1;
        }

        Token token = new Token( c,isSpecial );
        return Stream.create( token, new Function0<Stream<Token>>() {
            public Stream<Token> invoke() {
                return unescape( nextIndex, str );
            }
        } );
    }
}