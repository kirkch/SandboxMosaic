package com.mosaic.lang.text;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.system.Backdoor;


/**
 * Stores the result of CharacterParser.parse.
 */
public class ParserResult<T> {

    private static final long PRIMITIVE_RESULT_OFFSET = Backdoor.calculateOffsetForField( ParserResult.class, "primitiveResult" );

    private static enum StateEnum {
        NO_MATCH,
        MATCHED_WITH_NO_VALUE,
        MATCHED_WITH_OBJECT,
        MATCHED_WITH_BOOLEAN,
        MATCHED_WITH_BYTE,
        MATCHED_WITH_SHORT,
        MATCHED_WITH_CHARACTER,
        MATCHED_WITH_INT,
        MATCHED_WITH_LONG,
        MATCHED_WITH_FLOAT,
        MATCHED_WITH_DOUBLE
    }


    private StateEnum state           = StateEnum.NO_MATCH;
    private T         result          = null;

    private long      from            = 0;
    private long      toExc           = 0;

    private long      primitiveResult = 0;  // NB have used Unsafe to treat this field as a C union data type


// METHODS TO RECORD RESULT

    public void resultNoMatch() {
        state  = StateEnum.NO_MATCH;
        result = null;
    }

    public void resultMatchedNoValue( long from, long toExc ) {
        this.state           = StateEnum.MATCHED_WITH_NO_VALUE;
        this.result          = null;
        this.from            = from;
        this.toExc           = toExc;
    }

    public void resultMatched( T value, long from, long toExc ) {
        this.state           = StateEnum.MATCHED_WITH_OBJECT;
        this.result          = value;
        this.from            = from;
        this.toExc           = toExc;
    }

    public void resultMatchedBoolean( boolean v, long from, long toExc ) {
        this.state           = StateEnum.MATCHED_WITH_BOOLEAN;
        this.result          = null;
        this.from            = from;
        this.toExc           = toExc;

        this.primitiveResult = v ? 1 : 0;
    }

    public void resultMatchedByte( byte v, long from, long toExc ) {
        this.state           = StateEnum.MATCHED_WITH_BYTE;
        this.result          = null;
        this.from            = from;
        this.toExc           = toExc;

        this.primitiveResult = v;
    }

    public void resultMatchedShort( short v, long from, long toExc ) {
        this.state           = StateEnum.MATCHED_WITH_SHORT;
        this.result          = null;
        this.from            = from;
        this.toExc           = toExc;

        this.primitiveResult = v;
    }

    public void resultMatchedCharacter( char v, long from, long toExc ) {
        this.state           = StateEnum.MATCHED_WITH_CHARACTER;
        this.result          = null;
        this.from            = from;
        this.toExc           = toExc;

        this.primitiveResult = v;
    }

    public void resultMatchedInt( int v, long from, long toExc ) {
        this.state           = StateEnum.MATCHED_WITH_INT;
        this.result          = null;
        this.from            = from;
        this.toExc           = toExc;

        this.primitiveResult = v;
    }

    public void resultMatchedLong( long v, long from, long toExc ) {
        this.state           = StateEnum.MATCHED_WITH_LONG;
        this.result          = null;
        this.from            = from;
        this.toExc           = toExc;

        this.primitiveResult = v;
    }

    public void resultMatchedFloat( float v, long from, long toExc ) {
        this.state           = StateEnum.MATCHED_WITH_FLOAT;
        this.result          = null;
        this.from            = from;
        this.toExc           = toExc;

        setValueFloat( v );
    }

    public void resultMatchedDouble( double v, long from, long toExc ) {
        this.state           = StateEnum.MATCHED_WITH_DOUBLE;
        this.result          = null;
        this.from            = from;
        this.toExc           = toExc;

        setValueDouble( v );
    }

    public void setValueFloat( float v ) {
        Backdoor.setFloat( this, PRIMITIVE_RESULT_OFFSET, v );
    }

    public void setValueDouble( double v ) {
        Backdoor.setDouble( this, PRIMITIVE_RESULT_OFFSET, v );
    }


// METHODS TO QUERY RESULT

    public boolean hasMatched() {
        return state != StateEnum.NO_MATCH;
    }

    public boolean hasValue() {
        return state != StateEnum.MATCHED_WITH_NO_VALUE;
    }

    public T getValue() {
        assert this.state == StateEnum.MATCHED_WITH_OBJECT;

        return result;
    }

    public boolean getValueBoolean() {
        assert this.state == StateEnum.MATCHED_WITH_BOOLEAN;

        return primitiveResult == 1;
    }

    public short getValueShort() {
        assert this.state == StateEnum.MATCHED_WITH_SHORT;

        return (short) primitiveResult;
    }

    public char getValueCharacter() {
        assert this.state == StateEnum.MATCHED_WITH_CHARACTER;

        return (char) primitiveResult;
    }

    public int getValueInt() {
        assert this.state == StateEnum.MATCHED_WITH_INT;

        return (int) primitiveResult;
    }

    public long getValueLong() {
        assert this.state == StateEnum.MATCHED_WITH_LONG;

        return primitiveResult;
    }

    public float getValueFloat() {
        assert this.state == StateEnum.MATCHED_WITH_FLOAT;

        return Backdoor.getFloat( this, PRIMITIVE_RESULT_OFFSET );
    }

    public double getValueDouble() {
        assert this.state == StateEnum.MATCHED_WITH_DOUBLE;

        return Backdoor.getDouble( this, PRIMITIVE_RESULT_OFFSET );
    }

    public long getFrom() {
        return from;
    }

    public long getToExc() {
        return toExc;
    }

    public void setFrom( long from ) {
        this.from = from;
    }

    public long numBytesMatched() {
        return toExc - from;
    }

    public UTF8 getMatchedText( Bytes b ) {
        return new UTF8( b, from, toExc );
    }

}

