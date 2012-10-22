package com.mosaic.lang.reflect;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class JavaConstructorTest {
    @Test
    public void createObjectWithZeroArgConstructor() {
        JavaClass<JavaConstructorTest> javaClass = JavaClass.toJavaClass(JavaConstructorTest.class);
        JavaConstructor<JavaConstructorTest> constructor = javaClass.getConstructorFor();

        assertTrue( constructor.newInstance() != null );
    }

    @Test
    public void createObjectWithOneArgConstructor() {
        JavaClass<Integer> javaClass = JavaClass.toJavaClass(Integer.class);
        JavaConstructor<Integer> constructor = javaClass.getConstructorFor(String.class);

        assertEquals( new Integer(10), constructor.newInstance("10") );
    }
}
