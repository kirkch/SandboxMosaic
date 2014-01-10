package com.softwaremosaic.parsers.automata;

import com.mosaic.lang.CaseSensitivity;
import com.mosaic.lang.Validate;
import com.mosaic.utils.StringUtils;
import com.softwaremosaic.parsers.automata.regexp.RegExpCharacterUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static <T extends Comparable<T>> Label<T> orValues( Iterable<T> values ) {
      return new OrValuesLabel( values );
    }

    public static <T extends Comparable<T>> Label<T> orValues( T...values ) {
      return new OrValuesLabel( Arrays.asList(values) );
    }

    public static <T extends Comparable<T>> Label<T> orLabels( Iterable<Label<T>> labels ) {
      return new OrLabelsLabel( labels );
    }

    public static <T extends Comparable<T>> Label<T> orLabels( Label<T>...labels ) {
      return new OrLabelsLabel( Arrays.asList(labels) );
    }

    public static Label<Character> caseInsensitive( char c ) {
      return new CaseInsensitiveCharacterLabel(c);
    }

    public static Label<Character> characterRange( char minInc, char maxInc ) {
      return new CharacterRangeLabel( minInc, maxInc );
    }

    public static Label<Character> characterLabel( char c, CaseSensitivity caseSensitivity ) {
        return caseSensitivity.ignoreCase() ? caseInsensitive(c) : singleValue(c);
    }

    public static Label<Character> appendAnyCharacter() {
        return AnyCharacterLabel.INSTANCE;
    }

    /**
     * Supports the character selection options of a [abc] and [^abc] regexp component.
     */
    public static CharacterSelectionLabel characterSelection() {
        return new CharacterSelectionLabel();
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

    private static class AnyCharacterLabel implements Label<Character> {
        public static AnyCharacterLabel INSTANCE = new AnyCharacterLabel();

        public boolean matches( Character input ) {
            return true;
        }

        public String toString() {
            return ".";
        }

        public int compareTo( Label o ) {
            if ( !(o instanceof AnyCharacterLabel) ) {
                return -1;
            }

            return 0;
        }
    }

    private static class OrValuesLabel<T extends Comparable<T>> implements Label<T> {
        private Iterable<T> values;

        public OrValuesLabel( Iterable<T> values ) {
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
            if ( !(o instanceof OrValuesLabel) ) {
                return -1;
            }

            OrValuesLabel<T> other = (OrValuesLabel) o;
            return Objects.equals(this.values, other.values) ? 0 : this.values.iterator().next().compareTo(other.values.iterator().next());
        }
    }

    private static class OrLabelsLabel<T extends Comparable<T>> implements Label<T> {
        private Iterable<Label<T>> labels;

        public OrLabelsLabel( Iterable<Label<T>> labels ) {
            this.labels = labels;
        }

        public boolean matches( T input ) {
            for ( Label<T> l : labels ) {
                if (l.matches( input ) ) {
                    return true;
                }
            }

            return false;
        }

        public String toString() {
            return StringUtils.join( labels, "|" );
        }

        public int compareTo( Label<T> o ) {
            if ( !(o instanceof OrValuesLabel) ) {
                return -1;
            }

            OrLabelsLabel<T> other = (OrLabelsLabel) o;
            return Objects.equals(this.labels, other.labels) ? 0 : this.labels.iterator().next().compareTo(other.labels.iterator().next());
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

    public static class CharacterSelectionLabel implements Label<Character> {

        private boolean invert;
        private List<CharacterSelection> candidates = new ArrayList();


        public boolean matches( Character input ) {
            for ( CharacterSelection predicate : candidates ) {
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
            candidates.add( new CharacterSelection() {
                public boolean matches( Character input ) {
                    return input.charValue() == c;
                }

                public void toString( StringBuilder buf ) {
                    conditionalEscapeAndAppendToBuffer( buf, c );
                }
            });
        }

        public void appendRange( final char minInc, final char maxInc ) {
            Validate.isLTE( minInc, maxInc, "minInc" );

            candidates.add( new CharacterSelection() {
                public boolean matches( Character input ) {
                    char c = input.charValue();

                    return c >= minInc && c <= maxInc;
                }

                public void toString( StringBuilder buf ) {
                    conditionalEscapeAndAppendToBuffer( buf, minInc );
                    buf.append( '-' );
                    conditionalEscapeAndAppendToBuffer( buf, maxInc );
                }
            });
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();

            buf.append( '[' );

            if ( invert ) {
                buf.append( '^' );
            }

            for ( CharacterSelection candidate : candidates ) {
                candidate.toString( buf );
            }

            buf.append( ']' );

            return buf.toString();
        }

        public int compareTo( Label<Character> o ) {
            if ( !(o instanceof CharacterSelectionLabel) ) {
                return -1;
            }

            return this.toString().compareTo( o.toString() );
        }

        private void conditionalEscapeAndAppendToBuffer( StringBuilder buf, char c ) {
            if ( c == '^' || c == ']' || c == '-' ) {
                buf.append( '\\' );
            }

            buf.append(c);
        }
    }

    private static interface CharacterSelection {
        public boolean matches( Character input );
        public void toString( StringBuilder buf );
    }
}
