package com.mosaic.utils.string;

import com.mosaic.lang.QA;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class CharacterMatchers {
    public static CharacterMatcher constant( String targetStr ) {
        return new ConstantMatcher( targetStr );
    }

    /**
     * Includes line feeds and carridge returns
     */
    public static CharacterMatcher whitespace() {
        return WhitespaceMatcher.INSTANCE;
    }

    public static CharacterMatcher nonWhitespace() {
        return NonWhitespaceMatcher.INSTANCE;
    }

    public static CharacterMatcher regexp( String regexp ) {
        return new JDKRegexpMatcher( Pattern.compile( regexp ) );
    }

    public static CharacterMatcher regexp( Pattern regexp ) {
        return new JDKRegexpMatcher( regexp );
    }

    public static CharacterMatcher javaVariableName() {
        return JavaVariableNameMatcher.INSTANCE;
    }

    public static CharacterMatcher javaVariableType() {
        return JavaVariableTypeMatcher.INSTANCE;
    }

    /**
     *
     * When support commas is false:  [+-]?\d+
     * When support commas is true: then \d+ is changed to support 12,332,111 etc
     */
    public static CharacterMatcher integer( boolean supportCommas ) {
        return supportCommas ? CommaIntegerMatcher.INSTANCE : IntegerMatcher.INSTANCE;
    }

    public static CharacterMatcher digits() {
        return DigitsOnlyMatcher.INSTANCE;
    }

    public static CharacterMatcher everythingExcept( char c ) {
        return new EverythingExceptMatcher(c);
    }

    public static CharacterMatcher everythingExcept( String...constants ) {
        int                numConstants = constants.length;
        CharacterMatcher[] matchers     = new CharacterMatcher[numConstants];

        for ( int i=0; i<numConstants; i++ ) {
            matchers[i] = constant( constants[i] );
        }

        return new EverythingExceptOneOfMatcher(matchers);
    }

    public static CharacterMatcher consumeUptoNCharacters( int c ) {
        return new ConsumeUptoNCharactersMatcher(c);
    }
}


class ConstantMatcher implements CharacterMatcher {
    private String targetStr;

    public ConstantMatcher( String targetStr ) {
        QA.notNull( targetStr, "targetStr" );

        this.targetStr = targetStr;
    }

    public String toString() {
        return targetStr;
    }

    @Override
    public int consumeFrom( CharSequence seq, int minIndex, int maxIndexExc ) {
        int targetStrLength = targetStr.length();
        int maxBufIndex     = maxIndexExc - minIndex;

        if ( targetStrLength > maxBufIndex ) {
            return 0;
        }

        for ( int i=0,j=minIndex; i<targetStrLength; i++,j++ ) {
            if ( targetStr.charAt(i) != seq.charAt(j) ) {
                return 0;
            }
        }

        return targetStrLength;
    }


}

class WhitespaceMatcher implements CharacterMatcher {
    public static final CharacterMatcher INSTANCE = new WhitespaceMatcher();

    public static boolean isWhitespace( char c ) {
        switch(c) {
            case ' ':
            case '\n':
            case '\r':
            case '\t':
                return true;
            default:
                return false;
        }
    }

    private WhitespaceMatcher() {}

    public String toString() {
        return "whitespace";
    }

    @Override
    public int consumeFrom( CharSequence seq, int minIndex, int maxIndexExc ) {
        int count = 0;

        for ( int i=minIndex; i<maxIndexExc; i++ ) {
            char c = seq.charAt(i);

            if ( isWhitespace(c) ) {
                count++;
            } else {
                return count;
            }
        }

        return count;
    }
}

class NonWhitespaceMatcher implements CharacterMatcher {
    public static final CharacterMatcher INSTANCE = new NonWhitespaceMatcher();

    private NonWhitespaceMatcher() {}

    public String toString() {
        return "non-whitespace";
    }

    @Override
    public int consumeFrom( CharSequence seq, int minIndex, int maxIndexExc ) {
        int count = 0;

        for ( int i=minIndex; i<maxIndexExc; i++ ) {
            char c = seq.charAt(i);

            if ( WhitespaceMatcher.isWhitespace( c ) ) {
                return count;
            } else {
                count++;
            }
        }

        return count;
    }
}

class IntegerMatcher implements CharacterMatcher {
    public static final CharacterMatcher INSTANCE = new IntegerMatcher();

    private IntegerMatcher() {}

    public String toString() {
        return "int";
    }

    public int consumeFrom( CharSequence seq, int minIndex, int maxIndexExc ) {
        if ( minIndex >= maxIndexExc ) {
            return 0;
        }

        char firstChar = seq.charAt( minIndex );
        if ( firstChar == '+' || firstChar == '-' ) {
            int numDigits = DigitsOnlyMatcher.INSTANCE.consumeFrom( seq, minIndex + 1, maxIndexExc );

            return numDigits == 0 ? 0 : numDigits + 1;
        } else {
            return DigitsOnlyMatcher.INSTANCE.consumeFrom( seq, minIndex, maxIndexExc );
        }
    }
}

class DigitsOnlyMatcher implements CharacterMatcher {
    public static final CharacterMatcher INSTANCE = new DigitsOnlyMatcher();

    private DigitsOnlyMatcher() {}

    public String toString() {
        return "digits";
    }

    public int consumeFrom( CharSequence seq, int minIndex, int maxIndexExc ) {
        int count = 0;

        for ( int i=minIndex; i<maxIndexExc; i++ ) {
            char c = seq.charAt(i);

            if ( c >= '0' && c <= '9' ) {
                count++;
            } else {
                return count;
            }
        }

        return count;
    }
}

class CommaIntegerMatcher implements CharacterMatcher {
    public static final CharacterMatcher INSTANCE = new CommaIntegerMatcher();

    private CommaIntegerMatcher() {}

    public String toString() {
        return "comma-int";
    }

    public int consumeFrom( CharSequence seq, int minIndex, int maxIndexExc ) {
        int n = consumeUpToFirstComma( seq, minIndex, maxIndexExc );

        if ( n == 0 || n > 3 ) {   // don't support 1234,567 as it should be 1,234,567
            return n;
        }

        return consumeCommaBlocks( n, seq, minIndex, maxIndexExc );
    }

    private int consumeUpToFirstComma( CharSequence seq, int minIndex, int maxIndexExc ) {
        return IntegerMatcher.INSTANCE.consumeFrom( seq, minIndex, maxIndexExc );
    }

    private int consumeCommaBlocks( int countSoFar, CharSequence seq, int minIndex, int maxIndexExc ) {
        int count = countSoFar;

        int p = minIndex + countSoFar;
        while ( p < maxIndexExc ) {
            if ( seq.charAt(p) != ',' || p + 4 > maxIndexExc ) { // after a comma there must be 3 digits
                return count;
            }

            for ( int i = p+1; i < p+4; i++ ) {
                char c = seq.charAt( i );

                if ( c < '0' || c > '9' ) {
                    return count;
                }
            }

            p     += 4;
            count += 4;
        }

        return count;
    }
}

class JavaVariableNameMatcher implements CharacterMatcher {
    public static final CharacterMatcher INSTANCE = new JavaVariableNameMatcher();

    private JavaVariableNameMatcher() {}

    public String toString() {
        return "JavaVariableNameMatcher";
    }

    @Override
    public int consumeFrom( CharSequence seq, int minIndex, int maxIndexExc ) {
        if ( maxIndexExc <= minIndex ) {
            return 0;
        }

        if ( !isValidFirstChar(seq.charAt(minIndex)) ) {
            return 0;
        }

        int count = 1;
        for ( int i=minIndex+1; i<maxIndexExc; i++ ) {
            char c = seq.charAt(i);
            if ( isValidChar(c) ) {
                count++;
            } else {
                return count;
            }
        }

        return count;
    }

    private boolean isValidFirstChar( char c ) {
        return isBetween(c, 'a', 'z') || isBetween(c, 'A', 'Z') || c == '_';
    }

    private boolean isValidChar( char c ) {
        return isBetween(c, 'a', 'z') || isBetween(c, 'A', 'Z') || isBetween(c, '0', '9')
            || c == '_' || c == '$';
    }

    private boolean isBetween( char c, char min, char maxInc ) {
        return c >= min && c <= maxInc;
    }
}

class JavaVariableTypeMatcher implements CharacterMatcher {
    public static final CharacterMatcher INSTANCE = new JavaVariableTypeMatcher();

    private JavaVariableTypeMatcher() {}

    public String toString() {
        return "JavaVariableTypeMatcher";
    }

    @Override
    public int consumeFrom( CharSequence seq, int minIndex, int maxIndexExc ) {
        if ( maxIndexExc <= minIndex ) {
            return 0;
        }

        if ( !isValidFirstChar(seq.charAt(minIndex)) ) {
            return 0;
        }

        int count = 1;
        for ( int i=minIndex+1; i<maxIndexExc; i++ ) {
            char c = seq.charAt(i);
            if ( isValidChar(c) ) {
                count++;
            } else {
                return count;
            }
        }

        return count;
    }

    private boolean isValidFirstChar( char c ) {
        return isBetween(c, 'a', 'z') || isBetween(c, 'A', 'Z') || c == '_';
    }

    private boolean isValidChar( char c ) {
        return isBetween(c, 'a', 'z') || isBetween(c, 'A', 'Z') || isBetween(c, '0', '9')
            || c == '_' || c == '.';
    }

    private boolean isBetween( char c, char min, char maxInc ) {
        return c >= min && c <= maxInc;
    }
}

class JDKRegexpMatcher implements CharacterMatcher {
    private Pattern pattern;

    public JDKRegexpMatcher( Pattern pattern ) {
        this.pattern = pattern;
    }

    @Override
    public int consumeFrom( CharSequence seq, int minIndex, int maxIndexExc ) {
        Matcher m = pattern.matcher( seq );
        m.region( minIndex, maxIndexExc );

        return m.lookingAt() ? (m.end()-minIndex) : 0;
    }

    public String toString() {
        return pattern.toString();
    }
}

class EverythingExceptMatcher implements CharacterMatcher {
    private final char terminatingChar;

    public EverythingExceptMatcher( char c ) {
        terminatingChar = c;
    }

    @Override
    public int consumeFrom( CharSequence seq, int minIndex, int maxIndexExc ) {
        int count = 0;

        for ( int i=minIndex; i<maxIndexExc; i++ ) {
            if ( seq.charAt(i) != terminatingChar ) {
                count++;
            } else {
                return count;
            }
        }

        return count;
    }

    public String toString() {
        return "EverythingExcept("+terminatingChar+")";
    }
}

class EverythingExceptOneOfMatcher implements CharacterMatcher {
    private CharacterMatcher[] exclusions;

    public EverythingExceptOneOfMatcher( CharacterMatcher[] exclusions ) {
        this.exclusions = exclusions;
    }

    @Override
    public int consumeFrom( CharSequence seq, int minIndex, int maxIndexExc ) {
        int count = 0;

        for ( int i=minIndex; i<maxIndexExc; i++ ) {
            if ( hasHitExclusionAt(seq,i,maxIndexExc) ) {
                return count;
            } else {
                count++;
            }
        }

        return count;
    }

    public String toString() {
        return "EverythingExceptOneOf"+Arrays.asList(exclusions);
    }

    private boolean hasHitExclusionAt( CharSequence buf, int i, int maxIndexExc ) {
        for ( CharacterMatcher e : exclusions ) {
            if ( e.consumeFrom(buf, i, maxIndexExc) > 0 ) {
                return true;
            }
        }

        return false;
    }
}

class ConsumeUptoNCharactersMatcher implements CharacterMatcher {
    private final int numCharactersToConsume;

    public ConsumeUptoNCharactersMatcher( int numCharacters ) {
        this.numCharactersToConsume = numCharacters;
    }

    @Override
    public int consumeFrom( CharSequence seq, int minIndex, int maxIndexExc ) {
        int numCharactersAvailable = applyLowerBoundZero( maxIndexExc - minIndex );

        return Math.min( numCharactersToConsume, numCharactersAvailable );
    }

    private int applyLowerBoundZero( int v ) {
        return v < 0 ? 0 : v;
    }

    public String toString() {
        return "ConsumeUpToNCharacters("+numCharactersToConsume+")";
    }
}