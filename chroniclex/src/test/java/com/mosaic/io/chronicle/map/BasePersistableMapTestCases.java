package com.mosaic.io.chronicle.map;

import com.mosaic.bytes.ArrayBytes;
import com.mosaic.bytes.ByteView;
import com.mosaic.bytes.Bytes;
import com.mosaic.bytes.struct.DoubleField;
import com.mosaic.bytes.struct.LongField;
import com.mosaic.bytes.struct.Struct;
import com.mosaic.bytes.struct.StructRegistry;
import com.mosaic.collections.map.PersistableMap;
import com.mosaic.lang.Service;
import com.mosaic.lang.functional.VoidFunction1;
import org.junit.After;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;


public abstract class BasePersistableMapTestCases {

    protected abstract PersistableMap<String, Account> _createPersistableMap( long fixedKeySize, long fixedValueSize, long maxEntryCount );


    private PersistableMap<String, Account> createPersistableMap( long fixedKeySize, long fixedValueSize, long maxEntryCount ) {
        PersistableMap<String, Account> map = _createPersistableMap( fixedKeySize, fixedValueSize, maxEntryCount );

        map.start();
        openedResources.add( map );

        return map;
    }


    private PersistableMap<String, Account> createPersistableMap( long fixedKeySize, long fixedValueSize, long maxEntryCount, VoidFunction1<PersistableMap<String, Account>> setupMap ) {
        PersistableMap<String, Account> map = createPersistableMap( fixedKeySize, fixedValueSize, maxEntryCount );

        setupMap.invoke( map );

        return map;
    }


    private List<Service> openedResources = new LinkedList<>();

    @After
    public void tearDown() {
        // ensure that all resources that were opened during the test run are shutdown promptly
        for ( Service r : openedResources ) {
            r.stop();
        }
    }


    @Test
    public void givenUnknownKey_expectZeroedOutBytesBack() {
        PersistableMap<String,Account> map = createPersistableMap( 5, ACCOUNT_SIZE, 100 );


        Account account = new Account();
        map.getInto( "acc1", account );

        account.assertIsEmpty(ACCOUNT_SIZE);
    }

    @Test
    public void writeValue_expectToBeAbleToRetrieveValue() {
        Account account = new Account();

        PersistableMap<String,Account> map = createPersistableMap( 5, ACCOUNT_SIZE, 100, m -> {
            m.getInto( "acc1", account );

            account.setAccountId( 42 );
            account.setBalance( 100.17 );
        });


        map.getInto( "acc1", account );

        assertEquals( 42,     account.getAccountId() );
        assertEquals( 100.17, account.getBalance(), 0e-6 );
    }

    @Test
    public void writeTwoValues_expectToBeAbleToRetrieveBoth() {
        Account account = new Account();

        PersistableMap<String,Account> map = createPersistableMap( 5, ACCOUNT_SIZE, 100, m -> {
            m.getInto( "acc1", account );

            account.setAccountId( 42 );
            account.setBalance( 100.17 );

            m.getInto( "acc2", account );

            account.setAccountId( 43 );
            account.setBalance( -2.5 );
        });


        map.getInto( "acc1", account );

        assertEquals( 42,     account.getAccountId() );
        assertEquals( 100.17, account.getBalance(), 0e-6 );


        map.getInto( "acc2", account );

        assertEquals( 43,    account.getAccountId() );
        assertEquals( -2.5, account.getBalance(), 0e-6 );
    }

    @Test
    public void storeAValue_closeMap_expectValueToNotBeFetchable() {
        Account account = new Account();

        PersistableMap<String,Account> map = createPersistableMap( 5, ACCOUNT_SIZE, 100, m -> {
            m.getInto( "acc1", account );

            account.setAccountId( 42 );
            account.setBalance( 100.17 );

            m.getInto( "acc2", account );

            account.setAccountId( 43 );
            account.setBalance( -2.5 );
        });


        map.stop();

        try {
            map.getInto( "acc1", account );
            fail( "expected exception" );
        } catch ( IllegalStateException ex ) {
            assertEquals( ex.getMessage(), "'junit' is not running" );
        }
    }

    @Test
    public void storeAValue_closeThenReopenMap_expectValueToNowBeAvailable() {
        Account account = new Account();

        PersistableMap<String,Account> map = createPersistableMap( 5, ACCOUNT_SIZE, 100, m -> {
            m.getInto( "acc1", account );

            account.setAccountId( 42 );
            account.setBalance( 100.17 );

            m.getInto( "acc2", account );

            account.setAccountId( 43 );
            account.setBalance( -2.5 );
        });


        map.stop();
        map.start();

        map.getInto( "acc1", account );

        assertEquals( 42,     account.getAccountId() );
        assertEquals( 100.17, account.getBalance(), 0e-6 );


        map.getInto( "acc2", account );

        assertEquals( 43,    account.getAccountId() );
        assertEquals( -2.5,  account.getBalance(), 0e-6 );
    }


    private static final StructRegistry structRegistry = new StructRegistry();
    private static final LongField      accountIdField = structRegistry.registerLong();
    private static final DoubleField    balanceField   = structRegistry.registerDouble();

    public static final long ACCOUNT_SIZE = structRegistry.sizeBytes();


    protected class Account extends ByteView {
        private final Struct struct = structRegistry.createUnallocatedStruct();

        public Account() {
            setBytes( new ArrayBytes(ACCOUNT_SIZE), 0, ACCOUNT_SIZE );
        }


        public long sizeBytes() {
            return structRegistry.sizeBytes();
        }

        public long getAccountId() {
            return accountIdField.get( struct );
        }

        public void setAccountId( long newId ) {
            accountIdField.set( struct, newId );
        }

        public double getBalance() {
            return balanceField.get( struct );
        }

        public void setBalance( double newBalance ) {
            balanceField.set( struct, newBalance );
        }

        public void setBytes( Bytes bytes, long base, long maxExc ) {
            super.setBytes( bytes, base, maxExc );

            struct.setBytes( bytes, base, maxExc );
        }

        public void assertIsEmpty( long expectedSize ) {
            assertEquals( expectedSize, maxExc-base );

            for ( long i=base; i<maxExc; i++ ) {
                assertEquals( "index: "+i, 0, bytes.readByte(i, maxExc) );
            }
        }
    }
}