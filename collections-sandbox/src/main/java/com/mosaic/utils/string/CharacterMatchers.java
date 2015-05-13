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

    public static CharacterMatcher jdkRegexp( String regexp ) {
        return new JDKRegexpMatcher( Pattern.compile( regexp ) );
    }

    public static CharacterMatcher jdkRegexp( Pattern regexp ) {
        return new JDKRegexpMatcher( regexp );
    }

    public static CharacterMatcher javaVariableName() {
        return JavaVariableNameMatcher.INSTANCE;
    }

    public static CharacterMatcher javaVariableType() {
        return JavaVariableTypeMatcher.INSTANCE;
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

    public String description() {
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

    public String description() {
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

    public String description() {
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

class JavaVariableNameMatcher implements CharacterMatcher {
    public static final CharacterMatcher INSTANCE = new JavaVariableNameMatcher();

    private JavaVariableNameMatcher() {}

    public String description() {
        return "java variable name";
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

    public String description() {
        return "java variable type";
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

    public String description() {
        return pattern.pattern();
    }

    @Override
    public int consumeFrom( CharSequence seq, int minIndex, int maxIndexExc ) {
        Matcher m = pattern.matcher( seq );
        m.region( minIndex, maxIndexExc );

        return m.lookingAt() ? (m.end()-minIndex) : 0;
    }
}

class EverythingExceptMatcher implements CharacterMatcher {
    private final char terminatingChar;

    public EverythingExceptMatcher( char c ) {
        terminatingChar = c;
    }

    public String description() {
        return "everything upto and excluding '"+Character.toString(terminatingChar)+"'";
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

class EverythingExceptOneOfMatcher implements CharacterMatcher {
    private CharacterMatcher[] exclusions;

    public EverythingExceptOneOfMatcher( CharacterMatcher[] exclusions ) {
        this.exclusions = exclusions;
    }

    public String description() {
        return "everything except one of '"+ Arrays.toString(exclusions)+"'";
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

class ConsumeUptoNCharactersMatcher implements CharacterMatcher {
    private final int numCharactersToConsume;

    public ConsumeUptoNCharactersMatcher( int numCharacters ) {
        this.numCharactersToConsume = numCharacters;
    }

    public String description() {
        return "up to " + numCharactersToConsume + " characters";
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