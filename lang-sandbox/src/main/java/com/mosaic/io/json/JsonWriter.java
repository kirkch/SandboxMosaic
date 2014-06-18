package com.mosaic.io.json;

import com.mosaic.io.StringCodec;
import com.mosaic.io.codecs.LongCodec;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.text.UTF8;


/**
 *
 */
public class JsonWriter {

    private static final UTF8 JSON_ATTRIBUTE_KV_SEPARATOR = new UTF8(": ");


    private final CharacterStream out;


    public JsonWriter( CharacterStream out ) {
        this.out = out;
    }


    public void startObject() {

    }

    public void endObject() {

    }

    public void startArray() {

    }

    public void endArray() {

    }

    public void attrString( String name, String value ) {

    }

    public <T> void attrString( String name, T value, StringCodec<T> codec) {

    }

    public void attrLong( UTF8 name, long value ) {
        attrLong( name, value, LongCodec.LONG_CODEC );
    }



    public void attrLong( UTF8 name, long value, LongCodec codec ) {
        out.writeUTF8( name );
        out.writeUTF8( JSON_ATTRIBUTE_KV_SEPARATOR );
        codec.encode( value, out );
    }

    public void attrObjectStart( String name ) {

    }

    public void attrObjectEnd() {

    }

    public void attrArrayStart( String name ) {

    }

    public void attrArrayEnd() {

    }

}
