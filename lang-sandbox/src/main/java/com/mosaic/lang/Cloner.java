package com.mosaic.lang;

import com.mosaic.lang.reflect.ReflectionException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 */
public class Cloner {
    @SuppressWarnings({"unchecked"})
    public <T> T deepCopy( T v ) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(byteStream);

            o.writeObject( v );
            o.close();

            ObjectInputStream i = new ObjectInputStream(new ByteArrayInputStream(byteStream.toByteArray()));
            return (T) i.readObject();
        } catch ( Exception e ) {
            throw ReflectionException.recast( e );
        }
    }
}
