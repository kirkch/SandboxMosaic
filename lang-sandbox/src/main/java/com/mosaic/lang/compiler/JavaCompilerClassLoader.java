package com.mosaic.lang.compiler;

import com.mosaic.lang.functional.Pair;
import com.mosaic.lang.functional.Try;


/**
 * A custom class loader that takes raw java code, compiles them in memory and makes them available
 * to the running jvm.
 */
public class JavaCompilerClassLoader extends ClassLoader {

    private final JavaCompiler jc = new JavaCompiler();


    public JavaCompilerClassLoader() {
        this( JavaCompilerClassLoader.class.getClassLoader() );
    }

    public JavaCompilerClassLoader(ClassLoader parentClassLoader) {
        super(parentClassLoader);
    }


    public Class compileClass( String...source ) {
        Try<Pair<String,byte[]>> compiledClassDetails = jc.compileJavaClass( source );

        if ( compiledClassDetails.hasFailure() ) {
            throw new CompilationException( compiledClassDetails.getFailureNoBlock().getMessage() );
        }

        String className = compiledClassDetails.getResultNoBlock().getFirst();
        byte[] bytes     = compiledClassDetails.getResultNoBlock().getSecond();

        return defineClass( className, bytes, 0, bytes.length, null );
    }

}
