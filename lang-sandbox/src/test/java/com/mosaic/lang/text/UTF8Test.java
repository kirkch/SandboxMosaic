package com.mosaic.lang.text;

import org.junit.Test;

import static org.junit.Assert.*;


public class UTF8Test {

    @Test
    public void truncateToNumOfBytes() {
        UTF8 str1 = new UTF8("abcde");
        assertSame( str1, str1.truncateToNumOfBytes(str1.getByteCount()+1) );
        assertSame( str1, str1.truncateToNumOfBytes(str1.getByteCount()) );

        assertEquals( new UTF8("abcd"), str1.truncateToNumOfBytes(str1.getByteCount()-1) );
        assertEquals( new UTF8("abc"), str1.truncateToNumOfBytes(str1.getByteCount()-2) );
        assertEquals( new UTF8(""), str1.truncateToNumOfBytes(0) );
        assertEquals( new UTF8(""), str1.truncateToNumOfBytes(-1) );


        assertEquals( new UTF8("a£"), new UTF8("a£££").truncateToNumOfBytes(3) );
        assertEquals( new UTF8("a"), new UTF8("a£££").truncateToNumOfBytes(2) );
        assertEquals( new UTF8("a"), new UTF8("a£££").truncateToNumOfBytes(1) );


        assertEquals( new UTF8("aグ£a£"), new UTF8("aグ£a£").truncateToNumOfBytes(9) );
        assertEquals( new UTF8("aグ£a"), new UTF8("aグ£a£").truncateToNumOfBytes(8) );
        assertEquals( new UTF8("aグ£a"), new UTF8("aグ£a£").truncateToNumOfBytes(7) );
        assertEquals( new UTF8("aグ£"), new UTF8("aグ£a£").truncateToNumOfBytes(6) );
        assertEquals( new UTF8("aグ"), new UTF8("aグ£a£").truncateToNumOfBytes(5) );
        assertEquals( new UTF8("aグ"), new UTF8("aグ£a£").truncateToNumOfBytes(4) );
        assertEquals( new UTF8("a"), new UTF8("aグ£a£").truncateToNumOfBytes(3) );
        assertEquals( new UTF8("a"), new UTF8("aグ£a£").truncateToNumOfBytes(2) );
        assertEquals( new UTF8("a"), new UTF8("aグ£a£").truncateToNumOfBytes(1) );
        assertEquals( new UTF8(""), new UTF8("aグ£a£").truncateToNumOfBytes(0) );
    }

}