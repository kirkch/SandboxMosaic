package com.mosaic.lang.reflect;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
@SuppressWarnings({"UnusedDeclaration"})
public class JavaMethodTest {
   

    @Test
    public void testWrapObject_nullTargetObj() throws Exception {
        JavaClass<SomeBean>  c = JavaClass.toJavaClass(SomeBean.class);
        JavaMethod<SomeBean> m = c.getMethod("getName");

        try {
            m.interceptMethodCall( null, new Aspect() {
                @Override
                public Object intercepted(Object o, Object[] args, JavaMethod m) {
                    return "Sam";
                }
            } );

            fail( "Expected IllegalArgumentException" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'wrappedObject' must not be null", e.getMessage() );
        }
    }

    @Test
    public void testWrapObject_nullAspect() throws Exception {
        JavaClass<SomeBean>  c = JavaClass.toJavaClass(SomeBean.class);
        JavaMethod<SomeBean> m = c.getMethod("getName");

        SomeBean originalBean = new SomeBean();
        originalBean.setName("John");
        assertEquals( "John", originalBean.getName() );

        try {
            m.interceptMethodCall( originalBean, null );

            fail( "Expected IllegalArgumentException" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'aspect' must not be null", e.getMessage() );
        }
    }

    @Test
    public void testWrapObject_objectZeroArgMethod() throws Exception {
        JavaClass<SomeBean>  c = JavaClass.toJavaClass(SomeBean.class);
        JavaMethod<SomeBean> m = c.getMethod("getName");

        SomeBean originalBean = new SomeBean();
        originalBean.setName("John");
        assertEquals( "John", originalBean.getName() );

        SomeBean wrappedBean = m.interceptMethodCall( originalBean, new Aspect() {
            @Override
            public Object intercepted(Object o, Object[] args, JavaMethod m) {
                return "Sam";
            }
        } );

        assertEquals( "Sam", wrappedBean.getName() );

        // change the name, without intercepting the setter
        wrappedBean.setName( "Jim" );
        assertEquals( "Sam", wrappedBean.getName() );
        assertEquals( "Jim", originalBean.getName() );


        assertEquals( originalBean.hashCode(), wrappedBean.hashCode() );
    }

    @Test
    public void testWrapObject_objectOneArgMethod() throws Exception {
        JavaClass<SomeBean> c = JavaClass.toJavaClass(SomeBean.class);
        JavaMethod<SomeBean> m = c.getMethod("setName", String.class);

        SomeBean originalBean = new SomeBean();
        originalBean.setName("John");
        assertEquals( "John", originalBean.getName() );

        SomeBean wrappedBean = m.interceptMethodCall( originalBean, new Aspect() {
            @Override
            public Object intercepted(Object o, Object[] args, JavaMethod m) {
                args[0] = args[0] + " Bar";
                return m.invoke(o, args);
            }
        } );

        assertEquals( "John", wrappedBean.getName() );

        // change the name, the setter is intercepted
        wrappedBean.setName( "Foo" );
        assertEquals( "Foo Bar", wrappedBean.getName() );
        assertEquals( "Foo Bar", originalBean.getName() );
    }

    @Test
    public void testWrapObject_interface() throws Exception {
        JavaClass<Runnable> c = JavaClass.toJavaClass(Runnable.class);
        JavaMethod<Runnable> m = c.getMethod("run");

        final AtomicInteger hasRunCount = new AtomicInteger(0);

        Runnable originalBean = new Runnable() {
            @Override
            public void run() {
                hasRunCount.incrementAndGet();
            }
        };

        Runnable wrappedBean = m.interceptMethodCall( originalBean, new Aspect() {
            @Override
            public Object intercepted(Object o, Object[] args, JavaMethod m) {
                m.invoke(o, args);
                return m.invoke(o, args);
            }
        } );

        wrappedBean.run();
        assertEquals( 2, hasRunCount.get() );
    }

    @Test
    public void testWrapObject_finalObject() throws Exception {
        JavaClass<BeanDeclaredFinal> c = JavaClass.toJavaClass(BeanDeclaredFinal.class);
        JavaMethod<BeanDeclaredFinal> m = c.getMethod("getName");

        BeanDeclaredFinal originalBean = new BeanDeclaredFinal();

        try {
            m.interceptMethodCall( originalBean, new Aspect() {
                @Override
                public Object intercepted(Object o, Object[] args, JavaMethod m) {
                    m.invoke(o, args);
                    return m.invoke(o, args);
                }
            } );
            fail( "expected IAE" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "Cannot subclass final class class com.mosaic.lang.reflect.JavaMethodTest$BeanDeclaredFinal", e.getMessage() );
        }
    }

    @Test
    public void testWrapObject_objectWithNoZeroArgConstructor() throws Exception {
        JavaClass<BeanWithTwoArgConstructor> c = JavaClass.toJavaClass(BeanWithTwoArgConstructor.class);
        JavaMethod<BeanWithTwoArgConstructor> m = c.getMethod("getName");

        BeanWithTwoArgConstructor originalBean = new BeanWithTwoArgConstructor("Jim", 1);

        try {
            m.interceptMethodCall( originalBean, new Aspect() {
                @Override
                public Object intercepted(Object o, Object[] args, JavaMethod m) {
                    m.invoke(o, args);
                    return m.invoke(o, args);
                }
            } );
            fail( "expected IAE" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "Superclass has no null constructors but no arguments were given", e.getMessage() );
        }
    }





    public static class SomeBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private static final class BeanDeclaredFinal {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private static class BeanWithTwoArgConstructor {
        private String name;

        public BeanWithTwoArgConstructor( String name, int i ) {
            this.name = name + i;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
