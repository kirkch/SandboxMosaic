package com.mosaic.parsers.push;

import com.mosaic.io.CharPosition;
import com.mosaic.io.Characters;
import com.mosaic.lang.Validate;

/**
 * A collection of common matchers and matcher decorators.
 */
public class Matchers {

    /**
     * Requires an exact match.
     */
    public static Matcher<String> constant( String targetString ) {
        return new ConstantMatcher(targetString);
    }

}



class ConstantMatcher extends Matcher<String> {

    private final String targetString;

    public ConstantMatcher( String targetString ) {
        this( targetString, null, null, null );

        Validate.isGTZero( targetString.length(), "targetString.length()" );
    }

    private ConstantMatcher( String targetString, String result, Characters remainingBytes, CharPosition startingPosition ) {
        super( result, remainingBytes, startingPosition );

        this.targetString = targetString;
    }

    @Override
    public Matcher<String> processCharacters( Characters in ) {
        int numCharacters = in.length();

//        if ( numCharacters < targetString.length() ) {
//            return this;
//        }

//        if ( in.containsAt(targetString,0) ) {
//            Characters remainingBytes = in.skipCharacters( targetString.length() );
//
//            return new ConstantMatcher( targetString, targetString, remainingBytes, in.getPosition() );
//        } else {
//
//        }

        return this;
    }

}