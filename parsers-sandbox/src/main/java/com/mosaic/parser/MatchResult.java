package com.mosaic.parser;

import com.mosaic.io.CharPosition;
import com.mosaic.lang.QA;


public interface MatchResult<T> {
    public boolean isMatch();

    public CharSequence getMatchedText();

    public T getMatchedType();

    public CharPosition getPosition();
}

class NoMatch<T> implements MatchResult<T> {

    public boolean isMatch() {
        return false;
    }

    public CharSequence getMatchedText() {
        return null;
    }

    public T getMatchedType() {
        return null;
    }

    public CharPosition getPosition() {
        return null;
    }
}

class SuccessfulMatch<T> implements MatchResult<T> {

    private CharPosition position;
    private T            matchType;
    private CharSequence matchedText;


    public SuccessfulMatch( CharPosition position, T matchType, CharSequence matchedText ) {
        QA.notNull( position, "position" );
        QA.notNull( matchType, "matchType" );
        QA.notNull( matchedText, "matchedText" );

        this.position    = position;
        this.matchType   = matchType;
        this.matchedText = matchedText;
    }

    public boolean isMatch() {
        return true;
    }

    public CharSequence getMatchedText() {
        return matchedText;
    }

    public T getMatchedType() {
        return matchType;
    }

    public CharPosition getPosition() {
        return position;
    }

}
