package com.mosaic.bytes.struct;

import com.mosaic.bytes.ByteFactories;
import com.mosaic.bytes.Bytes;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.LongFunction1;
import com.mosaic.lang.system.SystemX;


/**
 * Provides type safe utils for accessing a Struct.
 *
 * Example usage:
 *
 * <pre>
 * public Account implements ByteView {
 *     // Create a single registry for defining the fixed width struct.
 *     private static final StructRegistry structRegistry = new StructRegistry();
 *
 *     // Register each of the struct's fields in turn;  the order will match the order that
 *     // the fields appear within the underlying bytes.  This only needs to be done once
 *     // per struct, and can be reused/shared.  Thus the registered fields are stored in
 *     // static final fields.
 *     private static final LongField       accountIdField = structRegistry.registerLong();
 *     private static final StringFieldUTF8 nameField      = structRegistry.registerUTF8Field(80);
 *
 *     // the struct itself, it contains the actual data that we are interested in reading. It
 *     // is implemented as a flyweight that must be notified as to which bytes to use and is
 *     // used to get/set each field.  this design pattern was selected so that users have the
 *     // option to reduce object allocations by reusing objects without having to copy data over
 *     // and over.
 *     private Struct struct = structRegistry.createNewStruct();
 *
 *
 *     // example getter/setters
 *     public long getAccountId() {
 *         return accountIdField.get( struct );
 *     }
 *
 *     public void setAccountId( long newId ) {
 *         accountIdField.set( struct, newId );
 *     }
 *
 *
 *     // Method declared on ByteView, implement simply as a pass through to the struct
 *     // which also implements the same interface.  Thus the struct will be told which underlying
 *     // Bytes to use.
 *     public void setBytes( Bytes bytes, long base, long maxExc ) {
 *         this.struct.setBytes( bytes, base, maxExc );
 *     }
 * }
 */
public class StructRegistry {

    /**
     * A debug flag used to detect fields that are registered after the first flyweight has been
     * created.
     */
    private boolean isLocked;


    private long structSizeBytes = 0;

    public Struct createUnallocatedStruct() {
        if ( SystemX.isDebugRun() ) {
            isLocked = true;
        }

        return new Struct( structSizeBytes );
    }

    public Struct createNewOnHeapStruct() {
        return createNewStruct( ByteFactories.ONHEAP );
    }

    public Struct createNewOffHeapStruct() {
        return createNewStruct( ByteFactories.OFFHEAP );
    }

    public Struct createNewStruct( LongFunction1<Bytes> byteFactory ) {
        Struct struct = createUnallocatedStruct();

        Bytes bytes = byteFactory.invoke( structSizeBytes );
        struct.setBytes( bytes, 0, structSizeBytes );

        return struct;
    }

    public long sizeBytes() {
        return structSizeBytes;
    }

    public BooleanField registerBoolean() {
        return registerNewField( new BooleanField(structSizeBytes) );
    }

    public ByteField registerByte() {
        return registerNewField( new ByteField(structSizeBytes) );
    }

    public UnsignedByteField registerUnsignedByte() {
        return registerNewField( new UnsignedByteField(structSizeBytes) );
    }

    public ShortField registerShort() {
        return registerNewField( new ShortField(structSizeBytes) );
    }

    public UnsignedShortField registerUnsignedShort() {
        return registerNewField( new UnsignedShortField(structSizeBytes) );
    }

    public CharacterField registerCharacter() {
        return registerNewField( new CharacterField(structSizeBytes) );
    }

    public IntField registerInteger() {
        return registerNewField( new IntField(structSizeBytes) );
    }

    public UnsignedIntField registerUnsignedInteger() {
        return registerNewField( new UnsignedIntField(structSizeBytes) );
    }

    public LongField registerLong() {
        return registerNewField( new LongField(structSizeBytes) );
    }

    public FloatField registerFloat() {
        return registerNewField( new FloatField(structSizeBytes) );
    }

    public DoubleField registerDouble() {
        return registerNewField( new DoubleField(structSizeBytes) );
    }


    private <T extends StructField> T registerNewField( T field ) {
        assertThatStructDefinitionHasNotBeenLockedYet();

        this.structSizeBytes += field.sizeBytes();

        return field;
    }

    private void assertThatStructDefinitionHasNotBeenLockedYet() {
        if ( SystemX.isDebugRun() ) {
            QA.isFalse( isLocked, "once createNewStruct() has been called for the first time, it is dangerous to register new fields" );
        }
    }


}