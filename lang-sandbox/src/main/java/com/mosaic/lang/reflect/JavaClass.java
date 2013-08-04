package com.mosaic.lang.reflect;

import com.mosaic.lang.Immutable;
import com.mosaic.lang.IsLockable;
import com.mosaic.utils.SetUtils;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@SuppressWarnings({"unchecked"})
public class JavaClass<T> implements Serializable, Immutable {
    private static final long serialVersionUID = 1286261015440L;

    private static final Set immutableClasses = SetUtils.asSet(
            Boolean.class, Byte.class, Character.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class,
            Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE,
            Float.TYPE, Double.TYPE,
            String.class, Currency.class, Locale.class
    );
    

    @SuppressWarnings({"unchecked"})
    public static <T> JavaClass<T> toJavaClass(T o) {
        if ( o == null ) return null;
        
        return new JavaClass(o.getClass());
    }



    @SuppressWarnings({"unchecked"})
    public static <T> JavaClass<T> toJavaClass(Class<T> o) {
        if ( o == null ) return null;
        
        return new JavaClass(o);
    }


    @SuppressWarnings({"unchecked"})
    public static <T> List<JavaClass<? extends T>> asList(Class<? extends T>...classes ) {
        List<JavaClass<? extends T>> list = new ArrayList<JavaClass<? extends T>>( classes.length );

        for (Class c : classes) {
            list.add( toJavaClass(c) );
        }

        return list;
    }




    private Class<T> type;
    private Map<String,JavaProperty> properties;


    protected JavaClass() {}

    protected JavaClass( Class<T> type ) {
        this.type = type;
    }

    public <T extends Annotation> T getAnnotation( Class<T> enumType ) {
        return type.getAnnotation( enumType );
    }


    public String getClassName() {
        return type.getSimpleName();
    }

    public T newInstance() {
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw ReflectionException.recast( e );
        }
    }

    public boolean isImmutable( T o) {
        if ( this.isInstanceOf(Immutable.class) ) {
            return true;
        } else if ( this.isInstanceOf(IsLockable.class) ) {
            return ((IsLockable) o).isLocked();
        } else if ( immutableClasses.contains(type) ) {
            return true;
        }

        return false;
    }

    public boolean isInstanceOf( Class aClass ) {
        return aClass == null ? false : aClass.isAssignableFrom(type);
    }

    public JavaConstructor<T> getConstructorFor( Class...argTypes) {
        try {
            return JavaConstructor.toJavaConstructor( type.getConstructor(argTypes) );
        } catch ( NoSuchMethodException e ) {
            throw ReflectionException.recast(e);
        }
    }

    public JavaMethod<T> getMethod( String methodName, Class...paramTypes ) {
        try {
            return JavaMethod.toJavaMethod( this, type.getMethod(methodName, paramTypes) );
        } catch (NoSuchMethodException e) {
            throw ReflectionException.recast(e);
        }
    }

    public JavaProperty<T> getProperty( String propertyName ) {
        return JavaProperty.toJavaProperty( this, propertyName );
    }

    public synchronized Map<String,JavaProperty> getProperties() {
        if ( properties == null ) {
            properties = JavaProperty.getJavaProperties( this );
        }

        return properties;
    }

    public Map<String, JavaField<T>> getDeclaredFields() {
        return JavaField.getAllJavaFieldsFor( this );
    }
    
//    public boolean equals(Object o) {
//        return EqualsBuilder.reflectionEquals( this, o );
//    }

    public String toString() {
        return type.getName();
    }

    public int hashCode() {
        return type.hashCode();
    }

    private Object readResolve() throws ObjectStreamException { // intern upon deserialization
        return this;
    }

    public Class getJDKClass() {
        return type;
    }

    public JavaClass getParent() {
        return isRootObject() ? null : JavaClass.toJavaClass( type.getSuperclass() );
    }

    public boolean isRootObject() {
        return type == Object.class;
    }

    /**
     * Returns the first matching field starting from this class and searching up its parents.
     *
     * @return null if no match is found
     */
    public JavaField getField( String name ) {
        JavaField<T> field = getDeclaredFields().get( name );
        if ( field == null && !isRootObject() ) {
            field = getParent().getField( name );
        }

        return field;
    }
}
