package com.mosaic.io.journal;

import com.mosaic.bytes2.fields.ByteFieldsRegistry2;
import com.mosaic.bytes2.fields.DoubleField2;
import com.mosaic.bytes2.fields.LongField2;


/**
 *
 */
public class Transaction2 extends JournalEntry {

    private static final ByteFieldsRegistry2 registry = new ByteFieldsRegistry2();

    private static final LongField2   fromField   = registry.registerLong();
    private static final LongField2   toField     = registry.registerLong();
    private static final DoubleField2 amountField = registry.registerDouble();

    public static final int SIZE_BYTES = registry.sizeBytes();



    public long getFrom() {
        return fromField.get( bytes );
    }

    public long getTo() {
        return toField.get(bytes);
    }

    public double getAmount() {
        return amountField.get(bytes);
    }

    public void setFrom( long newValue ) {
        fromField.set(bytes, newValue);
    }

    public void setTo( long newValue ) {
        toField.set(bytes, newValue);
    }

    public void setAmount( double newAmount ) {
        amountField.set(bytes, newAmount);
    }


    @Override
    public String toString() {
        return "Transaction("+getFrom()+","+getTo()+","+getAmount()+")";
    }
}

