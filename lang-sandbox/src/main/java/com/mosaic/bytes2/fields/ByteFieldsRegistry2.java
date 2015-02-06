package com.mosaic.bytes2.fields;

import com.mosaic.lang.Lockable;


/**
 * A factory for creating ByteFields.  A ByteField is a subset of bytes assigned to represent
 * a value, for example an int.  Combined together, a series of ByteFields elevate a region of
 * bytes into a fixed width struct similar to the struct keyword in C.
 *
 *
 * Example usage:
 *
 * <pre>
 * public Account {
 *     // Create a single registry for defining the fixed width struct.
 *     private static final ByteFieldRegistry registry = new ByteFieldRegistry();
 *
 *     // Register each of the struct's fields in turn;  the order will match the order that
 *     // the fields appear within the underlying bytes.  This only needs to be done once
 *     // per struct, and can be reused/shared.  Thus the registered fields are stored in
 *     // static final fields.
 *     private static final LongField       accountIdField = registry.registerLong();
 *     private static final StringFieldUTF8 nameField      = registry.registerUTF8Field(80);
 *
 *
 *     private Bytes bytes = ....
 *
 *     // example getter/setters
 *     public long getAccountId() {
 *         return accountIdField.get( bytes );
 *     }
 *
 *     public void setAccountId( long newId ) {
 *         accountIdField.set( bytes, newId );
 *     }
 * }
 */
public class ByteFieldsRegistry2 extends Lockable<ByteFieldsRegistry2> {

    /**
     * A debug flag used to detect fields that are registered after the first flyweight has been
     * created.
     */
    private boolean isLocked;


    private long numBytesAssignedSoFar = 0;


    public long sizeBytes() {
        return numBytesAssignedSoFar;
    }

    public BooleanField2 registerBoolean() {
        return registerNewField( new BooleanField2(numBytesAssignedSoFar) );
    }

    public ByteField2 registerByte() {
        return registerNewField( new ByteField2(numBytesAssignedSoFar) );
    }

    public UnsignedByteField2 registerUnsignedByte() {
        return registerNewField( new UnsignedByteField2(numBytesAssignedSoFar) );
    }

    public ShortField2 registerShort() {
        return registerNewField( new ShortField2(numBytesAssignedSoFar) );
    }

    public UnsignedShortField2 registerUnsignedShort() {
        return registerNewField( new UnsignedShortField2(numBytesAssignedSoFar) );
    }

    public CharacterField2 registerCharacter() {
        return registerNewField( new CharacterField2(numBytesAssignedSoFar) );
    }

    public IntField2 registerInteger() {
        return registerNewField( new IntField2(numBytesAssignedSoFar) );
    }

    public UnsignedIntField2 registerUnsignedInteger() {
        return registerNewField( new UnsignedIntField2(numBytesAssignedSoFar) );
    }

    public LongField2 registerLong() {
        return registerNewField( new LongField2(numBytesAssignedSoFar) );
    }

    public FloatField2 registerFloat() {
        return registerNewField( new FloatField2(numBytesAssignedSoFar) );
    }

    public DoubleField2 registerDouble() {
        return registerNewField( new DoubleField2(numBytesAssignedSoFar) );
    }

    public ByteArrayField2 registerByteArray( int len ) {
        return registerNewField( new ByteArrayField2(numBytesAssignedSoFar, len) );
    }


    private <T extends BytesField2> T registerNewField( T field ) {
        throwIfLocked();

        this.numBytesAssignedSoFar += field.sizeBytes();

        return field;
    }

}