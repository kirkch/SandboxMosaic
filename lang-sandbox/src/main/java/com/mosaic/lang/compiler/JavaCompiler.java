package com.mosaic.lang.compiler;


import com.mosaic.lang.Failure;
import com.mosaic.lang.QA;
import com.mosaic.lang.RegExp;
import com.mosaic.lang.functional.Pair;
import com.mosaic.lang.functional.Try;
import com.mosaic.lang.functional.TryNow;
import com.mosaic.utils.ArrayUtils;
import com.mosaic.utils.ListUtils;

import javax.tools.*;
import javax.tools.JavaFileObject.Kind;
import java.io.*;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Programmatically compile java source code.  See JavaCompilerClassLoader for creating new runtime
 * classes directly from source code at runtime.
 */
public class JavaCompiler {

    private final RegExp                   classNameRegExp   = new RegExp("(class|interface)[ \t\n\r]+([a-zA-Z$0-9_-]+)");
    private final RegExp                   packageNameRegExp = new RegExp("package[ \t\n\r]+([a-zA-Z$0-9._-]+)");
    private final javax.tools.JavaCompiler compiler          = ToolProvider.getSystemJavaCompiler();
    private final JavaCompilerFileManager  fileManager       = new JavaCompilerFileManager(compiler.getStandardFileManager(null, null, null));


    public Try<Pair<String,byte[]>> compileJavaClass( String... sourceLines ) {
        return compileFromSource( ArrayUtils.toString(sourceLines, "\n") );
    }

    public synchronized Try<Pair<String,byte[]>> compileFromSource( String source ) {
        String packageNameNbl = packageNameRegExp.extractMatchFrom( source, 1 );
        String name           = classNameRegExp.extractMatchFrom( source, 2 );
        String fqn            = packageNameNbl == null ? name : packageNameNbl + "." + name;

        QA.notNull(name, "Missing class name from java code");

        JavaFileObject                      file        = new JavaSource(fqn, source);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList( file );
        javax.tools.JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);

        boolean success = task.call();
        if ( !success ) {
            List<Diagnostic<? extends JavaFileObject>> errorDiagnostics = ListUtils.filter( diagnostics.getDiagnostics(), d -> d.getKind() == Diagnostic.Kind.ERROR );
            String                                     errorMsg         = ListUtils.toString( ListUtils.map(errorDiagnostics, Diagnostic::toString), "\n" );

            return TryNow.failed( errorMsg );
        }

        return TryNow.successful( new Pair<>(fqn, fileManager.getBytesFor(fqn)) );
    }

}








class JavaCompilerFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private final Map<String, ByteArrayOutputStream> byteArrays = new HashMap<>();

    public JavaCompilerFileManager(JavaFileManager delegate ) {
        super(delegate);
    }


    public byte[] getBytesFor( String name ) {
        ByteArrayOutputStream buf = byteArrays.get(name);
        if ( buf == null ) {
            return null;
        }

        return buf.toByteArray();
    }

    public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
        if (location == StandardLocation.CLASS_OUTPUT && byteArrays.containsKey( className ) && kind == Kind.CLASS) {
            final byte[] bytes = byteArrays.get(className).toByteArray();

            return new SimpleJavaFileObject(URI.create(className), kind) {
                public InputStream openInputStream() {
                    return new ByteArrayInputStream(bytes);
                }
            };
        }

        return super.getJavaFileForInput( location, className, kind );
    }

    public JavaFileObject getJavaFileForOutput(Location location, final String className, Kind kind, FileObject sibling) throws IOException {
        return new SimpleJavaFileObject(URI.create(className), kind) {
            public OutputStream openOutputStream() {
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                byteArrays.put( className, out );

                return out;
            }
        };
    }

}


class JavaSource extends SimpleJavaFileObject {
    final String code;

    JavaSource(String name, String code) {
        super( URI.create("string:///" + name.replace('.','/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE );

        this.code = code;
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}