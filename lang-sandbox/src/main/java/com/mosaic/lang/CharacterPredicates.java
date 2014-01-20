package com.mosaic.lang;

import com.mosaic.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 *
 */
@SuppressWarnings("unchecked")
public class CharacterPredicates {

    private static final CharacterPredicate NullCharacterPredicate = constant( (char) 0 );


    public static CharacterPredicate constant( char c ) {
        return new SingleCharacterPredicate(c);
    }

    public static CharacterPredicate alwaysTrue() {
        return AlwaysTrueCharacterPredicate.INSTANCE;
    }

    public static CharacterPredicate alwaysFalse() {
        return AlwaysFalseCharacterPredicate.INSTANCE;
    }

    public static CharacterPredicate eos() {
        return NullCharacterPredicate;
    }

    public static CharacterPredicate notValue( char c ) {
        return new NotCharacterPredicate(c);
    }

    public static CharacterPredicate orPredicates( Iterable<CharacterPredicate> Predicates ) {
        return new OrPredicate( Predicates );
    }

    public static CharacterPredicate orPredicates( CharacterPredicate...Predicates ) {
        return new OrPredicate( Arrays.asList(Predicates) );
    }

    public static CharacterPredicate caseInsensitive( char c ) {
        return new CaseInsensitivePredicate(c);
    }

    public static CharacterPredicate characterRange( char minInc, char maxInc ) {
        return new CharacterRangePredicate( minInc, maxInc );
    }

    public static CharacterPredicate characterPredicate( char c, CaseSensitivity caseSensitivity ) {
        return caseSensitivity.ignoreCase() ? caseInsensitive(c) : constant( c );
    }

    public static CharacterPredicate appendAnyCharacter() {
        return AnyCharacterPredicate.INSTANCE;
    }

    /**
     * Supports the character selection options of a [abc] and [^abc] regexp component.
     */
    public static CharacterSelectionPredicate characterSelection() {
        return new CharacterSelectionPredicate();
    }


    private static char[] SPECIAL_CHARS = new char[] {'^','[', ']', '-'};

    private static String escape( char c ) {
        if ( isSpecialChar(c) ) {
            return "\\"+c;
        } else {
            return Character.toString(c);
        }
    }

    public static boolean isSpecialChar( char c ) {
        for ( char s : SPECIAL_CHARS ) {
            if ( c == s ) {
                return true;
            }
        }

        return false;
    }


    private static class AlwaysTrueCharacterPredicate implements CharacterPredicate {
        public static CharacterPredicate INSTANCE = new AlwaysTrueCharacterPredicate();

        public boolean matches( char input ) {
            return true;
        }

        public String toString() {
            return "$AlwaysTrue$";
        }

        public int compareTo( CharacterPredicate o ) {
            return o == this ? 0 : -1;
        }
    }

    private static class AlwaysFalseCharacterPredicate implements CharacterPredicate {
        public static CharacterPredicate INSTANCE = new AlwaysFalseCharacterPredicate();

        public boolean matches( char input ) {
            return false;
        }

        public String toString() {
            return "$AlwaysFalse$";
        }

        public int compareTo( CharacterPredicate o ) {
            return o == this ? 0 : -1;
        }
    }

    private static class SingleCharacterPredicate implements CharacterPredicate {
        private final char c;

        public SingleCharacterPredicate( char c ) {
            this.c = c;
        }

        public boolean matches( char input ) {
            return input == c;
        }

        public String toString() {
            return escape( c );
        }

        public int compareTo( CharacterPredicate o ) {
            if ( !(o instanceof SingleCharacterPredicate) ) {
                return -1;
            }

            SingleCharacterPredicate other = (SingleCharacterPredicate) o;
            return this.c - other.c;
        }
    }

    private static class NotCharacterPredicate implements CharacterPredicate {
        private char c;

        public NotCharacterPredicate( char c ) {
            this.c = c;
        }

        public boolean matches( char input ) {
            return input != c;
        }

        public String toString() {
            return "[^" + escape(c) + "]";
        }

        public int compareTo( CharacterPredicate o ) {
            if ( !(o instanceof NotCharacterPredicate) ) {
                return -1;
            }

            NotCharacterPredicate other = (NotCharacterPredicate) o;
            return this.c - other.c;
        }
    }

    private static class AnyCharacterPredicate implements CharacterPredicate {
        public static AnyCharacterPredicate INSTANCE = new AnyCharacterPredicate();

        public boolean matches( char input ) {
            return true;
        }

        public String toString() {
            return ".";
        }

        public int compareTo( CharacterPredicate o ) {
            if ( !(o instanceof AnyCharacterPredicate) ) {
                return -1;
            }

            return 0;
        }
    }

    private static class OrPredicate implements CharacterPredicate {
        private Iterable<CharacterPredicate> predicates;

        public OrPredicate( Iterable<CharacterPredicate> predicates ) {
            this.predicates = predicates;
        }

        public boolean matches( char input ) {
            for ( CharacterPredicate p : predicates ) {
                if ( p.matches(input) ) {
                    return true;
                }
            }

            return false;
        }

        public String toString() {
            return StringUtils.join( predicates, "|" );
        }

        public int compareTo( CharacterPredicate o ) {
            if ( !(o instanceof OrPredicate) ) {
                return -1;
            }

            OrPredicate other = (OrPredicate) o;
            return Objects.equals(this.predicates, other.predicates) ? 0 : this.predicates.iterator().next().compareTo(other.predicates.iterator().next());
        }
    }

    private static class CaseInsensitivePredicate implements CharacterPredicate {
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

        public int compareTo( CharacterPredicate o ) {
            if ( !(o instanceof CaseInsensitivePredicate) ) {
                return -1;
            }

            CaseInsensitivePredicate other = (CaseInsensitivePredicate) o;
            return this.lc - other.lc;
        }
    }

    private static class CharacterRangePredicate implements CharacterPredicate {
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

        public int compareTo( CharacterPredicate o ) {
            if ( !(o instanceof CharacterRangePredicate) ) {
                return -1;
            }

            CharacterRangePredicate other = (CharacterRangePredicate) o;
            int a = this.minInc - other.minInc;
            int b = this.maxInc - other.maxInc;

            return a == 0 ? b : a;
        }
    }


    public static class CharacterSelectionPredicate implements CharacterPredicate {

        private boolean invert;
        private List<CharacterPredicate> candidates = new ArrayList();


        public boolean matches( char input ) {
            for ( CharacterPredicate predicate : candidates ) {
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

            for ( CharacterPredicate candidate : candidates ) {
                buf.append( candidate.toString() );
            }

            buf.append( ']' );

            return buf.toString();
        }

        public int compareTo( CharacterPredicate o ) {
            if ( !(o instanceof CharacterSelectionPredicate) ) {
                return -1;
            }

            return this.toString().compareTo( o.toString() );
        }

    }


    private static void conditionalEscapeAndAppendToBuffer( StringBuilder buf, char c ) {
        if ( isSpecialChar(c) ) {
            buf.append( '\\' );
        }

        buf.append(c);
    }

}
