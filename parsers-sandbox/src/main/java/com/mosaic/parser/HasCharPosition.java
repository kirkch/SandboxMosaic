package com.mosaic.parser;

import com.mosaic.io.CharPosition;


public interface HasCharPosition {
    public CharPosition getFromNbl();

    public CharPosition getToExcNbl();

    public void setPosition( CharPosition from, CharPosition toExc );
}
