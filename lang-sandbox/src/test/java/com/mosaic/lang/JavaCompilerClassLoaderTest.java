package com.mosaic.lang;

import com.mosaic.lang.compiler.CompilationException;
import com.mosaic.lang.compiler.JavaCompilerClassLoader;
import com.mosaic.lang.functional.Function0;
import org.junit.Test;

import static org.junit.Assert.*;


public class JavaCompilerClassLoaderTest {

    @Test
    public void givenClassWithNoPackage_expectSuccess() throws IllegalAccessException, InstantiationException {
        JavaCompilerClassLoader jc = new JavaCompilerClassLoader();

        Class c = jc.compileClass(
            "public class Foo implements com.mosaic.lang.functional.Function0 {",
            "  public Object invoke() {",
            "    return 42;",
            "  }",
            "}"
        );

        assertEquals( "Foo", c.getName() );

        Function0 f  = (Function0) c.newInstance();

        assertEquals( 42, f.invoke() );
    }

    @Test
    public void givenMalformedJava_expectException() throws IllegalAccessException, InstantiationException {
        JavaCompilerClassLoader jc = new JavaCompilerClassLoader();

        try {
            jc.compileClass(
                "package a.b.c;",
                "",
                "pulic class Foo implements com.mosaic.lang.functional.Function0 {",
                "  public Object invoke() {",
                "    return 42;",
                "  }",
                "}"
            );

            fail("expected exception");
        } catch ( CompilationException ex ) {
            assertTrue( ex.getMessage().startsWith("/a/b/c/Foo.java:3: error: class, interface, or enum expected") );
        }
    }

}