package com.mosaic.lang.reflect;

import com.mosaic.lang.Immutable;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 */
@SuppressWarnings({"unchecked"})
public class JavaField<B> implements Serializable, Immutable {
    private static final long serialVersionUID = 1301556766156L;

    private static ConcurrentMap<JavaClass,Map<String,JavaField>> cache = new ConcurrentHashMap<JavaClass,Map<String,JavaField>>();

    /**
     * @return All fields, of any scope declared on the specified class. No inheritance scan as that could cause name clashes.
     */
    static <B> Map<String,JavaField<B>> getAllJavaFieldsFor( JavaClass<B> jc ) {
        Map fields = cache.get( jc );
        if ( fields == null ) {
            fields = extractAllFieldsFrom( jc );

            cache.putIfAbsent( jc, fields );
        }

        return (Map<String,JavaField<B>>) fields;
    }

    private static <B> Map<String,JavaField<B>> extractAllFieldsFrom( JavaClass<B> jc ) {
        Map<String,JavaField<B>> fields = new ConcurrentHashMap();

        if ( !jc.isRootObject() ) {
            for ( Field declaredField : jc.getJDKClass().getDeclaredFields() ) { // NB all fields on that class (no inheritance)
                fields.put( declaredField.getName(), new JavaField<B>(jc, declaredField) );
            }
        }

        return Collections.unmodifiableMap( fields );
    }

    private JavaClass<B> owningClass;
    private Field        declaredField;

    public JavaField( JavaClass<B> jc, Field declaredField ) {
        this.owningClass   = jc;
        this.declaredField = declaredField;

        if ( isPrivate() ) {
            declaredField.setAccessible( true );
        }
    }

    public boolean isPrivate() {
        return Modifier.isPrivate( declaredField.getModifiers() ); 
    }

    public Object getValue( Object target ) {
        try {
            return declaredField.get( target );
        } catch ( IllegalAccessException e ) {
            throw ReflectionException.recast( e );
        }
    }

    public void setValue( Object target, Object newValue ) {
        try {
            declaredField.set( target, newValue );
        } catch ( IllegalAccessException e ) {
            throw ReflectionException.recast( e );
        }
    }

    private Object readResolve() throws ObjectStreamException { // intern upon deserialization
        return this;
    }

    public String getName() {
        return declaredField.getName();
    }

    public boolean isTransient() {
        return Modifier.isTransient( declaredField.getModifiers() );
    }

    public boolean isStatic() {
        return Modifier.isStatic( declaredField.getModifiers() );
    }

    public JavaClass<B> getOwningClass() {
        return owningClass;
    }
}
