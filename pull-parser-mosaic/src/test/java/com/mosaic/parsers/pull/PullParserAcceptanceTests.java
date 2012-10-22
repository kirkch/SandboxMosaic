package com.mosaic.parsers.pull;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static com.mosaic.parsers.pull.BNFExpressions.*;
import static org.junit.Assert.*;

/**
 *
 */
public class PullParserAcceptanceTests {

    @Test
    public void f() throws IOException {
        PullParser p = new PullParser();

        p.register( "FOO", constant("foo") );


        Match result = p.parse( "FOO", null, new Tokenizer(new StringReader("foo")) );

        assertEquals( new TextPosition(1,1), result.getPos() );
        assertTrue( result.hasValue() );
        assertEquals( "foo", result.getValue() );
    }

    // Parser p = new Parser();
    // p.register( "IDENTITY_FIELD",  keep(FIELDNAME), ":", keep(TYPE) ).withCallback()
    // p.register( "IDENTITY_FIELDS", seq(IDENTITY_FIELD) )
    // p.register( "CLASS",           keep(CLASSNAME), opt("(", keep(IDENTITY_FIELDS), ")") )




    // CLASSNAME <~ ["(" ~> IDENTITY_FIELDS <~ ")"]      => callback
    // CLASSNAME <~ opt("(" ~> IDENTITY_FIELDS <~ ")")
    // IDENTITY_FIELDS = seq( IDENTITY_FIELD )
    // IDENTITY_FIELD  = FIELDNAME <~ ":" ~> TYPE

}
