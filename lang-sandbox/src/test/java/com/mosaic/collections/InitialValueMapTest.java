package com.mosaic.collections;

import com.mosaic.utils.SetUtils;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class InitialValueMapTest {

    @Test
    public void givenBlankMap_fetchKey_expectEmptySetResponse() {
        Map<String,Set<String>> map = InitialValueMap.identityMapOfSets();

        Set<String> set = map.get("foo");
        assertEquals( 0, set.size() );
    }

    @Test
    public void givenMapWithSingleEntry_fetch_expectEntryBack() {
        Map<String,Set<String>> map = InitialValueMap.identityMapOfSets();
        Set<String> set = map.get("foo");
        set.add("1");


        set = map.get("foo");
        assertEquals(SetUtils.asSet("1"), set );
    }

}
