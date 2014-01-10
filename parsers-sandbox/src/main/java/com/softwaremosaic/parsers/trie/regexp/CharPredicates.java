package com.softwaremosaic.parsers.trie.regexp;

import com.mosaic.io.CharPredicate;
import com.mosaic.lang.Validate;
import com.mosaic.utils.StringUtils;
import com.softwaremosaic.parsers.automata.regexp.GraphBuilder;
import com.softwaremosaic.parsers.automata.regexp.RegExpCharacterUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 *
 */
public class CharPredicates {

    public static CharPredicate constant( char c ) {
        return new SingleCharPredicate(c);
    }

    public static CharPredicate notValue( char c ) {
        return new NotCharPredicate(c);
    }

//    public static CharPredicate orValues( Iterable<T> values ) {
//        return new OrValuesPredicate( values );
//    }
//
//    public static CharPredicate orValues( T...values ) {
//        return new OrValuesPredicate( Arrays.asList( values ) );
//    }

    public static CharPredicate orPredicates( Iterable<CharPredicate> Predicates ) {
        return new OrPredicate( Predicates );
    }

    public static CharPredicate orPredicates( CharPredicate...Predicates ) {
        return new OrPredicate( Arrays.asList(Predicates) );
    }

    public static CharPredicate caseInsensitive( char c ) {
        return new CaseInsensitivePredicate(c);
    }

    public static CharPredicate characterRange( char minInc, char maxInc ) {
        return new CharacterRangePredicate( minInc, maxInc );
    }

    public static CharPredicate characterPredicate( char c, GraphBuilder.CaseSensitivity caseSensitivity ) {
        return caseSensitivity.ignoreCase() ? caseInsensitive(c) : constant( c );
    }

    public static CharPredicate appendAnyCharacter() {
        return AnyCharacterPredicate.INSTANCE;
    }

    /**
     * Supports the character selection options of a [abc] and [^abc] regexp component.
     */
    public static CharacterSelectionPredicate characterSelection() {
        return new CharacterSelectionPredicate();
    }


    private static class SingleCharPredicate implements CharPredicate {
        private final char c;

        public SingleCharPredicate( char c ) {
            this.c = c;
        }

        public boolean matches( char input ) {
            return input == c;
        }

        public String toString() {
            return RegExpCharacterUtils.escape( c );
        }

        public int compareTo( CharPredicate o ) {
            if ( !(o instanceof SingleCharPredicate) ) {
                return -1;
            }

            SingleCharPredicate other = (SingleCharPredicate) o;
            return this.c - other.c;
        }
    }

    private static class NotCharPredicate implements CharPredicate {
        private char c;

        public NotCharPredicate( char c ) {
            this.c = c;
        }

        public boolean matches( char input ) {
            return input != c;
        }

        public String toString() {
            return "[^" + RegExpCharacterUtils.escape(c) + "]";
        }

        public int compareTo( CharPredicate o ) {
            if ( !(o instanceof NotCharPredicate) ) {
                return -1;
            }

            NotCharPredicate other = (NotCharPredicate) o;
            return this.c - other.c;
        }
    }

    private static class AnyCharacterPredicate implements CharPredicate {
        public static AnyCharacterPredicate INSTANCE = new AnyCharacterPredicate();

        public boolean matches( char input ) {
            return true;
        }

        public String toString() {
            return ".";
        }

        public int compareTo( CharPredicate o ) {
            if ( !(o instanceof AnyCharacterPredicate) ) {
                return -1;
            }

            return 0;
        }
    }

    private static class OrPredicate implements CharPredicate {
        private Iterable<CharPredicate> predicates;

        public OrPredicate( Iterable<CharPredicate> predicates ) {
            this.predicates = predicates;
        }

        public boolean matches( char input ) {
            for ( CharPredicate p : predicates ) {
                if ( p.matches(input) ) {
                    return true;
                }
            }

            return false;
        }

        public String toString() {
            return StringUtils.join( predicates, "|" );
        }

        public int compareTo( CharPredicate o ) {
            if ( !(o instanceof OrPredicate) ) {
                return -1;
            }

            OrPredicate other = (OrPredicate) o;
            return Objects.equals(this.predicates, other.predicates) ? 0 : this.predicates.iterator().next().compareTo(other.predicates.iterator().next());
        }
    }

    private static class CaseInsensitivePredicate implements CharPredicate {
        private char lc;
        private char uc;

        public CaseInsensitivePredicate( char c ) {
            lc = Character.toLowerCase( c );
            uc = Character.toUpperCase( c );
        }

        public boolean matches( char c ) {
            return c == lc || c == uc;
        }

        public String toString() {
            return "["+uc+lc+"]";
        }

        public int compareTo( CharPredicate o ) {
            if ( !(o instanceof CaseInsensitivePredicate) ) {
                return -1;
            }

            CaseInsensitivePredicate other = (CaseInsensitivePredicate) o;
            return this.lc - other.lc;
        }
    }

    private static class CharacterRangePredicate implements CharPredicate {
        private char minInc;
        private char maxInc;

        public CharacterRangePredicate( char minInc, char maxInc ) {
            this.minInc = minInc;
            this.maxInc = maxInc;
        }

        public boolean matches( char input ) {
            return input >= minInc && input <= maxInc;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();

            buf.append( '[' );
            conditionalEscapeAndAppendToBuffer( buf, minInc );
            buf.append( '-' );
            conditionalEscapeAndAppendToBuffer( buf, maxInc );
            buf.append( ']' );

            return buf.toString();
        }

        public int compareTo( CharPredicate o ) {
            if ( !(o instanceof CharacterRangePredicate) ) {
                return -1;
            }

            CharacterRangePredicate other = (CharacterRangePredicate) o;
            int a = this.minInc - other.minInc;
            int b = this.maxInc - other.maxInc;

            return a == 0 ? b : a;
        }
    }

    public static class CharacterSelectionPredicate implements CharPredicate {

        private boolean invert;
        private List<CharPredicate> candidates = new ArrayList();


        public boolean matches( char input ) {
            for ( CharPredicate predicate : candidates ) {
                if ( predicate.matches(input) ) {
                    return !invert;
                }
            }

            return invert;
        }

        public void invert() {
            this.invert = true;
        }

        public void appendCharacter( final char c ) {
            candidates.add( constant( c ) );
        }

        public void appendRange( final char minInc, final char maxInc ) {
            Validate.isLTE( minInc, maxInc, "minInc" );

            candidates.add( new CharacterRangePredicate(minInc,maxInc) {
                public String toString() {
                    StringBuilder buf = new StringBuilder();

                    conditionalEscapeAndAppendToBuffer( buf, minInc );
                    buf.append( '-' );
                    conditionalEscapeAndAppendToBuffer( buf, maxInc );

                    return buf.toString();
                }
            });
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();

            buf.append( '[' );

            if ( invert ) {
                buf.append( '^' );
            }

            for ( CharPredicate candidate : candidates ) {
                buf.append( candidate.toString() );
            }

            buf.append( ']' );

            return buf.toString();
        }

        public int compareTo( CharPredicate o ) {
            if ( !(o instanceof CharacterSelectionPredicate) ) {
                return -1;
            }

            return this.toString().compareTo( o.toString() );
        }

    }


    private static void conditionalEscapeAndAppendToBuffer( StringBuilder buf, char c ) {
        if ( c == '^' || c == ']' || c == '-' ) {
            buf.append( '\\' );
        }

        buf.append(c);
    }

}
