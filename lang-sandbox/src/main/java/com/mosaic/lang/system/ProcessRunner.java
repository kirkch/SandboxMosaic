package com.mosaic.lang.system;

import com.mosaic.collections.concurrent.Future;
import com.mosaic.lang.Cancelable;
import com.mosaic.lang.Failure;
import com.mosaic.lang.functional.TryNow;
import com.mosaic.lang.functional.VoidFunction1;
import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.utils.ListUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public Future<Integer> run() {
        ProcessBuilder b = new ProcessBuilder();

        b.command( this.cmd );

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
                TryNow.tryNow( p::destroy );
                TryNow.tryNow( shutdownHookTicket::cancel );
            } );



            BufferedReader stdout = new BufferedReader( new InputStreamReader(p.getInputStream()) );

            String threadName = "ProcessRunner "+pid+": "+cmd.get(0);
            new Thread(threadName) {
                public void run() {
                    boolean hasReachedEOF = false;
                    while(!hasReachedEOF) {
                        try {
                            String nextLine = stdout.readLine();
                            while ( nextLine != null ) {
                                stdoutCallback.invoke( nextLine );

                                nextLine = stdout.readLine();
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

        } catch ( IOException ex ) {
            promise.completeWithFailure( new Failure(ex) );
        }

        return promise;
    }

}
