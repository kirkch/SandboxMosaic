package com.mosaic.lang.reflect;

import com.mosaic.lang.Lockable;
import com.mosaic.lang.Cloner;
import com.mosaic.lang.Immutable;
import com.mosaic.lang.ThreadSafe;
import com.mosaic.lang.time.DTM;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimerTask;

import static com.mosaic.lang.reflect.JavaClass.toJavaClass;
import static junit.framework.Assert.*;

/**
 *
 */
@Ignore
@SuppressWarnings({"unchecked"})
public class JavaClassTest {
    @Test
    public void testToJavaClassCanHandleNull() {
        JavaClass<Integer> nullType = toJavaClass(null);
        assertNull( nullType );
    }

    @Test
    public void testGetSimpleName() {
        JavaClass<Integer> integerType = toJavaClass(42);
        assertEquals( "Integer", integerType.getClassName() );

        JavaClass<Integer> intType = toJavaClass(Integer.TYPE);
        assertEquals( "int", intType.getClassName() );
    }

    @Test
    public void testNewInstanceWithNoArgsConstructor() {
        JavaClass<JavaClassTest> clazz = toJavaClass(JavaClassTest.class);
        JavaConstructor<JavaClassTest> c = clazz.getConstructorFor();
        JavaClassTest v = c.newInstance();

        assertTrue( v != null );
    }

    @Test
    public void testNewInstanceWithOneArgsConstructor() {
        JavaClass<Integer> clazz = toJavaClass(Integer.class);
        JavaConstructor<Integer> c = clazz.getConstructorFor(String.class);
        Integer v = c.newInstance("42");

        assertEquals( 42, v.intValue() );
    }

    @Test
    public void testNewInstanceWithTwoArgsConstructor() {
        JavaClass<BeanWithTwoArgConstructor> clazz = toJavaClass(BeanWithTwoArgConstructor.class);
        JavaConstructor<BeanWithTwoArgConstructor> c = clazz.getConstructorFor(String.class, Integer.TYPE);
        BeanWithTwoArgConstructor v = c.newInstance("Hello", 42);

        assertEquals( "Hello", v.a1 );
        assertEquals( 42, v.a2 );
    }

    @Test
    public void testNewInstanceWithMismatchedArgsConstructor() {
        JavaClass<BeanWithTwoArgConstructor> clazz = toJavaClass(BeanWithTwoArgConstructor.class);
        JavaConstructor<BeanWithTwoArgConstructor> c = clazz.getConstructorFor(String.class, Integer.TYPE);


        try {
            c.newInstance(42);
            fail( "expected IllegalArgumentException" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "wrong number of arguments", e.getMessage() );
        }

        try {
            c.newInstance(42, "Hello");
            fail( "expected IllegalArgumentException" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "argument type mismatch", e.getMessage() );
        }
    }

    @Test
    public void testIsImmutable() {
        assertFalse( toJavaClass(BeanWithTwoArgConstructor.class).isImmutable(new BeanWithTwoArgConstructor("s", 1)) );
        assertTrue( toJavaClass(ClassThatImplementsImmutableInterface.class).isImmutable(new ClassThatImplementsImmutableInterface()) );

        assertTrue( toJavaClass(Boolean.class).isImmutable(true) );
        assertTrue( toJavaClass(Byte.class).isImmutable((byte) 1) );
        assertTrue( toJavaClass(Character.class).isImmutable('a') );
        assertTrue( toJavaClass(Short.class).isImmutable((short) 23) );
        assertTrue( toJavaClass(Integer.class).isImmutable(42) );
        assertTrue( toJavaClass(Long.class).isImmutable(62L) );
        assertTrue( toJavaClass(Float.class).isImmutable(10.1f) );
        assertTrue( toJavaClass(Double.class).isImmutable(22.1) );
        assertTrue( toJavaClass(Boolean.TYPE).isImmutable(false) );
        assertTrue( toJavaClass(Byte.TYPE).isImmutable((byte) 2) );
        assertTrue( toJavaClass(Character.TYPE).isImmutable('r') );
        assertTrue( toJavaClass(Short.TYPE).isImmutable((short) 54) );
        assertTrue( toJavaClass(Integer.TYPE).isImmutable(11) );
        assertTrue( toJavaClass(Long.TYPE).isImmutable(22L) );
        assertTrue( toJavaClass(Float.TYPE).isImmutable(54.3f) );
        assertTrue( toJavaClass(Double.TYPE).isImmutable(22.1) );
        
        assertTrue( toJavaClass(Locale.class).isImmutable(Locale.UK) );
        assertTrue( toJavaClass(Currency.class).isImmutable(Currency.getInstance("GBP")) );
        assertTrue( toJavaClass(DTM.class).isImmutable(new DTM(2010,3,20, 1,10,44)) );


        MyLockableBean b = new MyLockableBean();
        assertFalse( toJavaClass(MyLockableBean.class).isImmutable(b) );

        b.lock();
        assertTrue( toJavaClass(MyLockableBean.class).isImmutable(b) );
    }

    @Test
    public void testEquals() {
        assertTrue( new JavaClass(Integer.class).equals(new JavaClass(Integer.class)) );
        assertTrue( new JavaClass(Integer.TYPE).equals(new JavaClass(Integer.TYPE)) );

        assertFalse( new JavaClass(Integer.TYPE).equals(new JavaClass(Integer.class)) );
    }

    @Test
    public void testToString() {
        assertEquals( "java.lang.Integer", toJavaClass(Integer.class).toString() );
        assertEquals( "int", toJavaClass(Integer.TYPE).toString() );
    }

    @Test
    public void testHashCode() {
        assertEquals( new JavaClass(Integer.class).hashCode(), new JavaClass(Integer.class).hashCode() );
        assertEquals( new JavaClass(Integer.TYPE).hashCode(), new JavaClass(Integer.TYPE).hashCode() );


        assertTrue( new JavaClass(Integer.TYPE).hashCode() != new JavaClass(Integer.class).hashCode() );

    }

    @Test
    public void testToJavClassHandlesNull() {
        assertNull( toJavaClass((Class) null) );
        assertNull( toJavaClass((Object) null) );
    }

    @Test
    public void testToJavaClassInternsClasses() {
        assertTrue( toJavaClass(Integer.class) == toJavaClass(Integer.class) );
        assertTrue( toJavaClass(Integer.class) != toJavaClass(Integer.TYPE) );
    }

    @Test
    public void testIsSerializableAndDeserializedClassIsInterned() throws IOException, ClassNotFoundException {
        JavaClass deserializedObject = new Cloner().deepCopy( toJavaClass( Integer.class ) );

        assertTrue( "deserializing did not intern", deserializedObject == toJavaClass(Integer.class) );
    }


    @Test
    public void testAsListEmpty() {
        List<JavaClass<? extends Object>> classes = JavaClass.asList();

        assertEquals( 0, classes.size() );
    }

    @Test
    public void testAsListNotEmpty() {
        List<JavaClass<? extends Comparable>> classes = JavaClass.<Comparable>asList(Integer.class, String.class);

        assertEquals( 2, classes.size() );
        assertEquals( toJavaClass(Integer.class), classes.get(0) );
        assertEquals( toJavaClass(String.class), classes.get( 1 ) );
    }

    @Test
    public void testIsInstanceOfNullArg() {
        assertFalse( toJavaClass( Integer.class ).isInstanceOf( null ) );
    }

    @Test
    public void testIsInstanceOfSelf() {
        assertTrue( toJavaClass( Integer.class ).isInstanceOf( Integer.class ) );
    }

    @Test
    public void testIsInstanceOfParentClass() {
        assertTrue( toJavaClass(Integer.class).isInstanceOf(Number.class) );
    }

    @Test
    public void testIsInstanceOfChildClass() {
        assertFalse( toJavaClass( Number.class ).isInstanceOf( Integer.class ) );
    }

    @Test
    public void testIsInstanceOfInterface() {
        assertTrue( toJavaClass( Integer.class ).isInstanceOf( Serializable.class ) );
    }

    @Test
    public void testNewInstanceOnObjectWithPrivateConstructor() {
        JavaClass c = toJavaClass( TestBeanWithPrivateConstructor.class );

        try {
            c.newInstance();
            fail( "Expected ReflectionException(IllegalAccessException)" );
        } catch ( ReflectionException e ) {
            assertEquals( "java.lang.IllegalAccessException: Class com.mosaic.lang.reflect.JavaClass can not access a member of class com.mosaic.lang.reflect.JavaClassTest$TestBeanWithPrivateConstructor with modifiers \"private\"", e.getMessage() );
            assertTrue( e.getCause() instanceof IllegalAccessException );
        }
    }

    @Test
    public void testNewInstanceOnObjectWithNoZeroArgConstructor() {
        JavaClass c = toJavaClass( BeanWithTwoArgConstructor.class );

        try {
            c.newInstance();
            fail( "Expected ReflectionException(IllegalAccessException)" );
        } catch ( ReflectionException e ) {
            assertEquals( "java.lang.InstantiationException: com.mosaic.lang.reflect.JavaClassTest$BeanWithTwoArgConstructor", e.getMessage() );
            assertTrue( e.getCause() instanceof InstantiationException );
        }
    }

    @Test
    public void testNewInstanceOnObjectWithZeroArgConstructor() {
        JavaClass<JavaClassTest> c = toJavaClass( JavaClassTest.class );

        assertTrue( c.newInstance() != null );
    }

    @Test
    public void testNewInstanceOnObjectWithZeroArgConstructorThatErrors() {
        JavaClass c = toJavaClass( TestBeanWithZeroConstructorThatErrors.class );

        try {
            c.newInstance();
            fail( "expected" );
        } catch ( IllegalStateException e ) {
            assertEquals( "rar", e.getMessage() );
        }
    }

    @Test
    public void getMethodFromAbstractClass() {
        JavaClass timerTask = toJavaClass(TimerTask.class);

        JavaMethod runMethod = timerTask.getMethod("run");

        assertTrue( runMethod != null );
    }

    @Test
    public void fetchClassAnnotationThatIsNotDeclared() {
        JavaClass jc = toJavaClass( TestBeanWithPrivateConstructor.class );

        assertNull( jc.getAnnotation( ThreadSafe.class ) );
    }

    @Test
    public void fetchClassAnnotationThatIsDeclared() {
        JavaClass jc = toJavaClass(ClassThatImplementsImmutableInterface.class);

        assertNotNull( jc.getAnnotation( ThreadSafe.class ) );
    }

    @Test
    public void showThatGetPropertyThatDoesNotExistReturnsNull() {
        JavaClass<BeanWithTwoArgConstructor> jc = JavaClass.toJavaClass( BeanWithTwoArgConstructor.class );

        assertNull( jc.getProperty( "b" ) );
    }

    @Test
    public void showThatGetPropertyOnPropertyThatHasSetterAndGetter() {
        JavaClass<BeanWithTwoArgConstructor> jc = JavaClass.toJavaClass( BeanWithTwoArgConstructor.class );
        
        assertNotNull( jc.getProperty( "a1" ) );
    }

    @Test
    public void showThatGetPropertiesReturnsProperties() {
        JavaClass<BeanWithTwoArgConstructor> jc = JavaClass.toJavaClass( BeanWithTwoArgConstructor.class );
        Map<String,JavaProperty> properties = jc.getProperties();

        assertEquals( 2, properties.size() );
        assertTrue( properties.containsKey("a1") );
        assertTrue( properties.containsKey("a2") );
        assertFalse( properties.containsKey("a3") );
    }
    
    @Test
    public void showThatGetParentReturnsTheClassesParent() {
        JavaClass<BeanWithTwoArgConstructor> jc = JavaClass.toJavaClass( BeanWithTwoArgConstructor.class );
        assertFalse( jc.isRootObject() );

        JavaClass parent = jc.getParent();
        assertEquals( Object.class, parent.getJDKClass() );
        assertEquals( null, parent.getParent() );
        assertTrue( parent.isRootObject() );
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static class BeanWithTwoArgConstructor {
        public String a1;
        public int a2;

        public BeanWithTwoArgConstructor( String a1, int a2 ) {
            this.a1 = a1;
            this.a2 = a2;
        }

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

    @ThreadSafe
    public static class ClassThatImplementsImmutableInterface implements Immutable {
        
    }

    public static class TestBeanWithPrivateConstructor {
        private TestBeanWithPrivateConstructor() {}
    }

    public static class TestBeanWithZeroConstructorThatErrors {
        public TestBeanWithZeroConstructorThatErrors() {
            throw new IllegalStateException( "rar" );
        }
    }

    private static class MyLockableBean extends Lockable<MyLockableBean> {
        
    }
}
