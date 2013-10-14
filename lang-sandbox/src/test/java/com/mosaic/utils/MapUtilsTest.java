package com.mosaic.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "unchecked"})
public class MapUtilsTest {

    @Test
    public void asMap() {
        Map expectedMap1 = new HashMap();
        expectedMap1.put("a", 1);

        Map expectedMap2 = new HashMap();
        expectedMap2.put("a", 1);
        expectedMap2.put("b", 2);


        assertEquals(new HashMap(), MapUtils.asMap());
        assertEquals( expectedMap1, MapUtils.asMap("a",1) );
        assertEquals( expectedMap2, MapUtils.asMap("a",1, "b",2) );
    }

}
