package com.mosaic.parser;

public interface CharacterParser<L,R> {

    public ParseResult<L,R> parseFrom( ParserStream in );

}
