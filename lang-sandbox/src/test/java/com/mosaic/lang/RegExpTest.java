package com.mosaic.lang;

import org.junit.Test;

import static org.junit.Assert.*;


public class RegExpTest {


    @Test
    public void extractFrom() {
        assertEquals( "bc", new RegExp("bc").extractMatchFrom("abcd") );
        assertEquals( "ll", new RegExp("ll+").extractMatchFrom("hello") );
        assertEquals( "bc", new RegExp("bc").extractMatchFrom( "abcd" ) );
        assertEquals( "Foo", new RegExp("class[ \t\n\r]+([a-zA-Z$0-9_-]+)").extractMatchFrom("public class \t Foo {",1) );
    }

}