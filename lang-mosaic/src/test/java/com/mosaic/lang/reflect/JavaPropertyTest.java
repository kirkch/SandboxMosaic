package com.mosaic.lang.reflect;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 *
 */
@SuppressWarnings({"UnusedDeclaration"})
public class JavaPropertyTest {

    @Test
    public void fetchPropertyAndSetGetIt() {
        JavaClass<BeanWithSetterAndGetter>    jc = JavaClass.toJavaClass( BeanWithSetterAndGetter.class );
        JavaProperty<BeanWithSetterAndGetter> a1 = jc.getProperty( "a1" );


        BeanWithSetterAndGetter bean = jc.newInstance();
        assertNull( a1.getValue(bean) );
        a1.setValue( bean, "foo" );
        assertEquals( "foo", a1.getValue(bean) );
    }

    public static class BeanWithSetterAndGetter {
        public String a1;
        public int a2;


        public String getA1() {
            return a1;
        }

        public void setA1( String a1 ) {
            this.a1 = a1;
        }

        public int getA2() {
            return a2;
        }

        public void setA2( int a2 ) {
            this.a2 = a2;
        }
    }
}
