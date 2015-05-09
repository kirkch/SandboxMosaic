package com.mosaic.utils.string;

import com.mosaic.lang.QA;


public abstract class BaseCharacterMatcher<T> implements CharacterMatcher<T> {

    private T matchType;

    protected BaseCharacterMatcher( T matchType ) {
        QA.notNull( matchType, "matchType" );

        this.matchType = matchType;
    }

    public T matchType() {
        return matchType;
    }

}
