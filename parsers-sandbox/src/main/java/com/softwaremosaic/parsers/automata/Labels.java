package com.softwaremosaic.parsers.automata;

import com.mosaic.utils.StringUtils;
import com.softwaremosaic.parsers.automata.regexp.GraphBuilder;
import com.softwaremosaic.parsers.automata.regexp.RegExpCharacterUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 */
@SuppressWarnings("unchecked")
public class Labels {

    public static <T extends Comparable<T>> Label<T> singleValue( T v ) {
      return new SingleValueLabel(v);
    }

    public static <T extends Comparable<T>> Label<T> notValue( T v ) {
      return new NotValueLabel<>(v);
    }

    public static <T extends Comparable<T>> Label<T> or( Iterable<T> values ) {
      return new OrLabel( values );
    }

    public static <T extends Comparable<T>> Label<T> or( T[] values ) {
      return new OrLabel( Arrays.asList(values) );
    }

    public static Label<Character> caseInsensitive( char c ) {
      return new CaseInsensitiveCharacterLabel(c);
    }

    public static Label<Character> characterRange( char minInc, char maxInc ) {
      return new CharacterRangeLabel( minInc, maxInc );
    }

    public static Label<Character> characterLabel( char c, GraphBuilder.CaseSensitivity caseSensitivity ) {
        return caseSensitivity.ignoreCase() ? caseInsensitive(c) : singleValue(c);
    }


    private static class SingleValueLabel<T extends Comparable<T>> implements Label<T> {
        private final T v;

        public SingleValueLabel( T v ) {
            this.v = v;
        }

        public boolean matches( T input ) {
            return Objects.equals( v, input );
        }

        public String toString() {
            return RegExpCharacterUtils.escape(v.toString());
        }

        public int compareTo( Label<T> o ) {
            if ( !(o instanceof SingleValueLabel) ) {
                return -1;
            }

            SingleValueLabel<T> other = (SingleValueLabel) o;
            return this.v.compareTo(other.v);
        }
    }

    private static class NotValueLabel<T extends Comparable<T>> implements Label<T> {
        private T v;

        public NotValueLabel( T v ) {
            this.v = v;
        }

        public boolean matches( T input ) {
            return !Objects.equals( v, input );
        }

        public String toString() {
            return "[^" + RegExpCharacterUtils.escape(v.toString()) + "]";
        }

        public int compareTo( Label<T> o ) {
            if ( !(o instanceof NotValueLabel) ) {
                return -1;
            }

            NotValueLabel<T> other = (NotValueLabel) o;
            return this.v.compareTo(other.v);
        }
    }

    private static class OrLabel<T extends Comparable<T>> implements Label<T> {
        private Iterable<T> values;

        public OrLabel( Iterable<T> values ) {
            this.values = values;
        }

        public boolean matches( T input ) {
            for ( T v : values ) {
                if ( Objects.equals(v,input) ) {
                    return true;
                }
            }

            return false;
        }

        public String toString() {
            return StringUtils.join( values, "|" );
        }

        public int compareTo( Label<T> o ) {
            if ( !(o instanceof OrLabel) ) {
                return -1;
            }

            OrLabel<T> other = (OrLabel) o;
            return Objects.equals(this.values, other.values) ? 0 : this.values.iterator().next().compareTo(other.values.iterator().next());
        }
    }

    private static class CaseInsensitiveCharacterLabel implements Label<Character> {
        private char lc;
        private char uc;

        public CaseInsensitiveCharacterLabel( char c ) {
            lc = Character.toLowerCase( c );
            uc = Character.toUpperCase( c );
        }

        public boolean matches( Character c ) {
            return c == lc || c == uc;
        }

        public String toString() {
            return "["+uc+lc+"]";
        }

        public int compareTo( Label<Character> o ) {
            if ( !(o instanceof CaseInsensitiveCharacterLabel) ) {
                return -1;
            }

            CaseInsensitiveCharacterLabel other = (CaseInsensitiveCharacterLabel) o;
            return this.lc - other.lc;
        }
    }

    private static class CharacterRangeLabel implements Label<Character> {
        private char minInc;
        private char maxInc;

        public CharacterRangeLabel( char minInc, char maxInc ) {
            this.minInc = minInc;
            this.maxInc = maxInc;
        }

        public boolean matches( Character input ) {
            return input >= minInc && input <= maxInc;
        }

        public String toString() {
            return "["+minInc+"-"+maxInc+"]";
        }

        public int compareTo( Label<Character> o ) {
            if ( !(o instanceof CharacterRangeLabel) ) {
                return -1;
            }

            CharacterRangeLabel other = (CharacterRangeLabel) o;
            int a = this.minInc - other.minInc;
            int b = this.maxInc - other.maxInc;

            return a == 0 ? b : a;
        }
    }
}
