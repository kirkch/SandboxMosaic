package com.mosaic.bytes2.fields;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.bytes2.impl.ArrayBytes2;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class ByteFieldRegistryTestCases {

    private Bytes2        bytes;
    private RedBullStruct bull;


    @Before
    public void setup() {
        bytes = new ArrayBytes2( RedBullStruct.SIZE_BYTES*10 );
        bull  = new RedBullStruct(bytes);
    }

    @After
    public void tearDown() {
        bull.release();
    }



    @Test
    public void createStruct_expectFieldsToBeDefaultValues() {
        assertEquals( false, bull.getHasWings() );
        assertEquals( 0, bull.getAge() );
        assertEquals( 0, bull.getWeight(), 1e-6 );
    }

    @Test
    public void createStruct_setFields() {
        bull.setAge( 10 );
        bull.setWeight( 142.2f );
        bull.setHasWings( true );

        assertEquals( true, bull.getHasWings() );
        assertEquals( 10, bull.getAge() );
        assertEquals( 142.2, bull.getWeight(), 1e-4 );
    }

    @Test
    @SuppressWarnings("PointlessArithmeticExpression") // NB not pointless here, as they express the reason for the values used
    public void createStruct_setFieldsOnTwoStructs() {
        bull.setAge( 10 );
        bull.setWeight( 142.2f );
        bull.setHasWings( true );

        bull.setBytes( bytes, RedBullStruct.SIZE_BYTES );

        bull.setAge( 20 );
        bull.setWeight( 242.2f );
        bull.setHasWings( false );


        bull.setBytes( bytes, RedBullStruct.SIZE_BYTES*0 );

        assertEquals( true, bull.getHasWings() );
        assertEquals( 10, bull.getAge() );
        assertEquals( 142.2, bull.getWeight(), 1e-4 );


        bull.setBytes( bytes, RedBullStruct.SIZE_BYTES*1 );

        assertEquals( false, bull.getHasWings() );
        assertEquals( 20, bull.getAge() );
        assertEquals( 242.2, bull.getWeight(), 1e-4 );
    }

}
