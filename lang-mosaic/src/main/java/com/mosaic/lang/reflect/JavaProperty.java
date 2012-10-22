package com.mosaic.lang.reflect;

import com.mosaic.lang.Immutable;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
@SuppressWarnings({"unchecked"})
public class JavaProperty<B> implements Immutable {

    public static <T> JavaProperty<T> toJavaProperty( JavaClass<T> jc, String propertyName ) {
        for ( PropertyDescriptor property : PropertyUtils.getPropertyDescriptors(jc.getJDKClass()) ) {
            if ( property.getName().equals(propertyName) ) {
                return toJavaProperty( jc, propertyName, property );
            }
        }

        return null;
    }

    static Map<String, JavaProperty> getJavaProperties( JavaClass jc ) {
        Map<String,JavaProperty> properties = new ConcurrentHashMap<String, JavaProperty>();

        for ( PropertyDescriptor property : PropertyUtils.getPropertyDescriptors(jc.getJDKClass()) ) {
            String propertyName = property.getName();

            if ( propertyName.equals("class") && property.getPropertyType().equals(Class.class) ) {
                break;
            }

            properties.put( propertyName, toJavaProperty( jc, propertyName, property) );
        }

        return properties;
    }

    private static <T> JavaProperty<T> toJavaProperty( JavaClass<T> jc, String propertyName, PropertyDescriptor property ) {JavaMethod writeMethod = JavaMethod.toJavaMethod( jc, property.getWriteMethod() );
        JavaMethod readMethod  = JavaMethod.toJavaMethod( jc, property.getReadMethod() );

        return new JavaProperty<T>( jc, propertyName, writeMethod, readMethod );
    }

    private JavaClass<B> owningClass;
    private String       propertyName;
    private JavaMethod   setter;
    private JavaMethod   getter;

    private JavaProperty( JavaClass<B> owningClass, String propertyName, JavaMethod setter, JavaMethod getter ) {
        this.owningClass  = owningClass;
        this.propertyName = propertyName;
        this.setter       = setter;
        this.getter       = getter;
    }


    public String getPropertyName() {
        return propertyName;
    }

    public <P> P getValue( B bean ) {
        return (P) getter.invoke( bean, null );
    }

    public void setValue( B bean, Object value ) {
        setter.invoke( bean, new Object[] {value} );
    }

//    public boolean equals(Object o) {
//        return EqualsBuilder.reflectionEquals( this, o );
//    }

    public String toString() {
        return propertyName;
    }

//    public int hashCode() {
//        return HashCodeBuilder.reflectionHashCode( this );
//    }
}
