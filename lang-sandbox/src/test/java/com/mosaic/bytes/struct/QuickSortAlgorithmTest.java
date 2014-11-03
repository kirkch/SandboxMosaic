package com.mosaic.bytes.struct;

import com.mosaic.utils.ComparatorUtils;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Test;
import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.CombinedGenerators;
import net.java.quickcheck.generator.PrimitiveGenerators;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.junit.Assert.*;


@RunWith(JUnitMosaicRunner.class)
@SuppressWarnings({"unchecked", "UnusedDeclaration"})
public class QuickSortAlgorithmTest {

    private static final QuickSortAlgorithm sorter = new QuickSortAlgorithm<int[],Integer>(
        ComparatorUtils::compareAsc,
        (array,i) -> array[(int) i],
        (array,l,r) -> {
            int tmp = array[(int) l];

            array[(int) l] = array[(int) r];
            array[(int) r] = tmp;
        },
        array -> array.length
    );

    private static final Generator arraySizeGenerator = PrimitiveGenerators.integers(0,10000);
    private static final Generator arrayGenerator     = CombinedGenerators.intArrays( PrimitiveGenerators.integers(), arraySizeGenerator );


    @Test
    public void sortTenNumbers() {
        int[] values = new int[] {3,2,1,5,4,6,9,0,7,8};

        sorter.sort( values );


        for ( int i=0; i<values.length; i++ ) {
            assertEquals( i, values[i] );
        }
    }

    @Test(generators={"arrayGenerator"} )
    public void f( int[] array ) {
        sorter.sort( array );

        assertIsOrdered(array);
    }


    private void assertIsOrdered( int[] actual ) {
        int[] expected = cloneAndSortUsingJDKLibrary( actual );

        assertArrayEquals( expected, actual );


        for ( int i=1; i<actual.length; i++ ) {
            assertTrue( actual[i-1] <= actual[i] );
        }
    }

    private int[] cloneAndSortUsingJDKLibrary( int[] array ) {
        int[] clone = array.clone();

        Arrays.sort( clone );

        return clone;
    }

}