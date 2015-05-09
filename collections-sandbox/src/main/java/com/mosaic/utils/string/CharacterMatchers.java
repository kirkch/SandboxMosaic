package com.mosaic.utils.string;

import com.mosaic.lang.QA;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collection of common character matchers.
 */
public class CharacterMatchers {
    public static <T> CharacterMatcher constant( T matchType, String targetStr ) {
        return new ConstantMatcher<>( matchType, targetStr );
    }

    /**
     * Includes line feeds and carridge returns
     */
    public static <T> CharacterMatcher<T> whitespace( T matchType ) {
        return new WhitespaceMatcher<>(matchType);
    }

    public static <T> CharacterMatcher<T> nonWhitespace( T matchType ) {
        return new NonWhitespaceMatcher<>(matchType);
    }

    public static <T> CharacterMatcher<T> regexp( T matchType, String regexp ) {
        return new JDKRegexpMatcher<>( matchType, Pattern.compile(regexp) );
    }

    public static <T> CharacterMatcher<T> regexp( T matchType, Pattern regexp ) {
        return new JDKRegexpMatcher<>( matchType, regexp );
    }

    public static <T> CharacterMatcher<T> javaVariableName( T matchType ) {
        return new JavaVariableNameMatcher<>( matchType );
    }

    public static <T> CharacterMatcher<T> javaVariableType( T matchType ) {
        return new JavaVariableTypeMatcher<>(matchType);
    }

    public static <T> CharacterMatcher<T> everythingExcept( T matchType, char c ) {
        return new EverythingExceptMatcher<>(matchType, c);
    }

    public static <T> CharacterMatcher<T> everythingExcept( T matchType, String...constants ) {
        int                numConstants = constants.length;
        CharacterMatcher[] matchers     = new CharacterMatcher[numConstants];

        for ( int i=0; i<numConstants; i++ ) {
            matchers[i] = constant( matchType, constants[i] );
        }

        return new EverythingExceptOneOfMatcher<>(matchType, matchers);
    }

    public static <T> CharacterMatcher<T> consumeUptoNCharacters( T matchType, int c ) {
        return new ConsumeUptoNCharactersMatcher<>(matchType, c);
    }
}


class ConstantMatcher<T> extends BaseCharacterMatcher<T> {
    private String targetStr;

    public ConstantMatcher( T matchType, String targetStr ) {
        super( matchType );

        QA.notNull( targetStr, "targetStr" );

        this.targetStr = targetStr;
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

class WhitespaceMatcher<T> extends BaseCharacterMatcher<T> {

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

    WhitespaceMatcher( T matchType ) {
        super(matchType);
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

class NonWhitespaceMatcher<T> extends BaseCharacterMatcher<T> {
    NonWhitespaceMatcher( T matchType ) {
        super( matchType );
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

class JavaVariableNameMatcher<T> extends BaseCharacterMatcher<T> {
    JavaVariableNameMatcher( T matchType ) {
        super(matchType);
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

class JavaVariableTypeMatcher<T> extends BaseCharacterMatcher<T> {

    JavaVariableTypeMatcher( T matchType ) {
        super(matchType);
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

class JDKRegexpMatcher<T> extends BaseCharacterMatcher<T> {
    private Pattern pattern;

    public JDKRegexpMatcher( T matchType, Pattern pattern ) {
        super(matchType);

        this.pattern = pattern;
    }

    @Override
    public int consumeFrom( CharSequence seq, int minIndex, int maxIndexExc ) {
        Matcher m = pattern.matcher( seq );
        m.region( minIndex, maxIndexExc );

        return m.lookingAt() ? (m.end()-minIndex) : 0;
    }
}

class EverythingExceptMatcher<T> extends BaseCharacterMatcher<T> {
    private final char terminatingChar;

    public EverythingExceptMatcher( T matchType, char c ) {
        super( matchType );

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
}

class EverythingExceptOneOfMatcher<T> extends BaseCharacterMatcher<T> {
    private CharacterMatcher[] exclusions;

    public EverythingExceptOneOfMatcher( T matchType, CharacterMatcher[] exclusions ) {
        super( matchType );

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

    private boolean hasHitExclusionAt( CharSequence buf, int i, int maxIndexExc ) {
        for ( CharacterMatcher e : exclusions ) {
            if ( e.consumeFrom(buf, i, maxIndexExc) > 0 ) {
                return true;
            }
        }

        return false;
    }
}

class ConsumeUptoNCharactersMatcher<T> extends BaseCharacterMatcher<T> {
    private final int numCharactersToConsume;

    public ConsumeUptoNCharactersMatcher( T matchType, int numCharacters ) {
        super( matchType );

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
}