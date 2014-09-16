package com.mosaic.io.chronicle.map;

import com.mosaic.bytes.ByteView;
import com.mosaic.bytes.Bytes2;
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




    protected class Account implements ByteView {
        private Bytes2 bytes;
        private long  base;
        private long  maxExc;

        public void setBytes( Bytes2 bytes, long base, long maxExc ) {
            this.bytes  = bytes;
            this.base   = base;
            this.maxExc = maxExc;
        }

        public void assertIsEmpty( long expectedSize ) {
            assertEquals( expectedSize, maxExc-base );

            for ( long i=base; i<maxExc; i++ ) {
                assertEquals( 0, bytes.readByte(i, maxExc) );
            }
        }
    }
}