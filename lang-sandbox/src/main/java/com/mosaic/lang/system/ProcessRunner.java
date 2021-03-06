package com.mosaic.lang.system;

import com.mosaic.collections.concurrent.Future;
import com.mosaic.io.IOUtils;
import com.mosaic.lang.Cancelable;
import com.mosaic.lang.Failure;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.TryNow;
import com.mosaic.lang.functional.VoidFunction1;
import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.utils.ListUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *
 */
public class ProcessRunner {

    private List<String>          cmd;
    private VoidFunction1<String> stdoutCallback;
    private SystemX               system;
    private File                  cwd;

    private String                separator = IOUtils.LINE_SEPARATOR;



    public ProcessRunner( SystemX system, String cmd, String...args ) {
        this( system, cmd, args, line -> {} );
    }

    public ProcessRunner( SystemX system, String cmd, List<String> args ) {
        this( system, cmd, ListUtils.toArray(String.class,args), line -> {} );
    }


    public ProcessRunner( SystemX system, String cmd, List<String> args, VoidFunction1<String> stdoutCallback ) {
        this( system, cmd, ListUtils.toArray(String.class,args), stdoutCallback );
    }

    public ProcessRunner( SystemX system, String cmd, String[] args, VoidFunction1<String> stdoutCallback ) {
        this.system         = system;
        this.cmd            = new ArrayList<>(args.length+1);
        this.stdoutCallback = stdoutCallback;

        this.cmd.add( cmd );

        Collections.addAll(this.cmd, args);
    }


    /**
     * Specified the EOL separator to use when reading lines of text in from the child process.
     */
    public ProcessRunner withLineSeparator( String separator ) {
        this.separator = separator;

        return this;
    }

    public OSProcess run() {
        ProcessBuilder b = new ProcessBuilder();

        b.command( this.cmd );
        b.redirectErrorStream( true );

        if ( cwd != null ) {
            b.directory( cwd );
        }

        Future<Integer> promise = Future.promise();

        try {
            Process p   = b.start();
            int     pid = ReflectionUtils.getPrivateField( p, "pid" );

            system.devAudit( "Invoking command (pid=%s): %s", pid, this.cmd );

            Cancelable shutdownHookTicket = system.addShutdownHook( () -> {
                system.devAudit( "Shutting down child process (pid="+pid+")" );
                promise.completeWithFailure( new Failure(this.getClass(), "JVM shutdown is in progress") );
            });

            promise.onResult( r ->  shutdownHookTicket.cancel() );

            promise.onFailure( f -> {
                TryNow.tryNow( p::destroy );                 // send signal to other process
                TryNow.tryNow( shutdownHookTicket::cancel ); // send signal within local process

                system.opsAudit( "Forced shutdown." );
                stdoutCallback.invoke( "Forced shutdown." );
            } );



            Reader stdout = new InputStreamReader(p.getInputStream()) ;

            String threadName = "ProcessRunner "+pid+": "+cmd.get(0);
            new Thread(threadName) {
                public void run() {
                    StringBuilder buf = new StringBuilder();

                    boolean hasReachedEOF = false;
                    while(!hasReachedEOF) {
                        try {
                            int c = stdout.read();
                            while ( c >= 0 ) {
                                buf.append((char) c);

                                int upToExc = buf.indexOf(separator);
                                if ( upToExc >= 0 ) {
                                    String txt = buf.substring( 0, upToExc );

                                    stdoutCallback.invoke( txt );

                                    buf.replace( 0, upToExc+separator.length(), "" );
                                }

                                c = stdout.read();
                            }

                            hasReachedEOF = true;
                        } catch ( IOException e ) {
                            // an error here just means that the process has been killed/ended.. safe to ignore
                            hasReachedEOF = true;
                        }
                    }

                    while ( p.isAlive() ) {}

                    try {
                        promise.completeWithResult( p.exitValue() );
                    } catch ( Exception ex ) {
                        promise.completeWithFailure( new Failure(ex) );
                    }
                }
            }.start();


            PrintWriter writer = new PrintWriter(new OutputStreamWriter(p.getOutputStream()));

            return new OSProcess(pid, promise, writer);
        } catch ( IOException ex ) {
            promise.completeWithFailure( new Failure(ex) );

            return new OSProcess(-1, promise, null);
        }
    }

    public void setCWD( String cwd ) {
        this.cwd = new File(cwd);
    }
}
