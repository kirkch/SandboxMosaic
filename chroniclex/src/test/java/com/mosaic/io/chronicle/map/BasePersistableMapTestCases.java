package com.mosaic.io.chronicle.map;

import com.mosaic.bytes.ByteFlyWeight;
import com.mosaic.io.bytes.Bytes;
import org.junit.Test;

import static org.junit.Assert.*;


public abstract class BasePersistableMapTestCases {

    protected abstract PersistableMap<String,Account> createPersistableMap( long keySize, long entrySize );



    @Test
    public void givenUnknownKey_expectZeroedOutBytesBack() {
        PersistableMap<String,Account> map = createPersistableMap( 5, 20 );


        Account account = new Account();
        map.getInto( "acc1", account );

//        account.assertIsEmpty(20); TODO
    }




    protected class Account implements ByteFlyWeight {
        private Bytes bytes;
        private long  base;
        private long  maxExc;

        public void setFlyWeightBytes( Bytes bytes, long base, long maxExc ) {
            this.bytes  = bytes;
            this.base   = base;
            this.maxExc = maxExc;
        }

        public void assertIsEmpty( long expectedSize ) {
            assertEquals( expectedSize, maxExc-base );

            for ( long i=base; i<maxExc; i++ ) {
                assertEquals( 0, bytes.readByte(i) );
            }
        }
    }
}