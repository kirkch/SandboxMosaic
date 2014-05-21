package com.mosaic.io.cli;

import com.mosaic.io.streams.PrettyPrinter;
import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.time.Duration;
import com.mosaic.utils.StringUtils;

import java.lang.reflect.Method;


/**
 *
 */
public abstract class CLApp {

    public SystemX system;

    protected CLApp( SystemX system ) {
        this.system = system;
    }

    public final void runApp( String...args ) {
        Method m = ReflectionUtils.findFirstPublicMethodByName( this.getClass(), "run" );

        if ( m == null ) {
            printUsage();
        } else if ( m.getParameterTypes().length > args.length ) {
            printMissingRequiredArgumentsErrorMessage( args );

            printUsage();
        } else {
            invokeRunCommand( m, args );
        }
    }

    private void invokeRunCommand( Method m, String[] args ) {
        final long   startMillis = System.currentTimeMillis();
        final String commandName = this.getClass().getSimpleName();


        Runtime.getRuntime().addShutdownHook( new Thread() {
            public void run() {
                long     endMillis = System.currentTimeMillis();
                Duration duration  = Duration.millis( endMillis-startMillis );

                system.userAudit( "Completed %s in %s", commandName, duration );
            }
        });





        try {
            system.opsLog.newLine();

            system.userLog.writeString( "Running command '" );
            system.userLog.writeString( commandName );
            system.userLog.writeString( "'" );

            for ( Object arg : args ) {
                system.userLog.writeString( " '" );
                system.userLog.writeString( arg.toString() );
                system.userLog.writeString( "'" );
            }

            system.userLog.newLine();
            system.opsLog.newLine();


            ReflectionUtils.invoke( this, m, (Object[]) args );

        } catch ( IllegalArgumentException ex ) {
            system.fatal( "Unable to proceed: %s", ex.getMessage() );
            system.fatal( ex );

            printUsage();
        } catch ( Throwable ex ) {
            system.fatal( ex );
        }
    }

    private void printMissingRequiredArgumentsErrorMessage( String[] args ) {
        String[] paramNames = getParameterNames();

        system.fatalLog.writeString( "Unable to proceed, missing required command line " );
        PrettyPrinter.printPleural( system.fatalLog, "argument", paramNames.length-args.length );
        system.fatalLog.writeString( " '" );
        PrettyPrinter.englishList( system.fatalLog, paramNames, args.length, paramNames.length );
        system.fatalLog.writeLine( "'" );
    }

    protected abstract String[] getUsageAppDescription();
    protected abstract String[] getParameterNames();
    protected abstract String[] getParameterDescriptions();

    private void printUsage() {
        StringBuilder buf = new StringBuilder();
        buf.append( "Usage: java " );
        buf.append( this.getClass().getCanonicalName() );

        String[] parameterNames = getParameterNames();
        for ( String paramName : parameterNames ) {
            buf.append( " <" );
            buf.append( paramName );
            buf.append( ">" );
        }

        system.opsAudit( buf.toString() );
        String[] description = getUsageAppDescription();

        if ( !StringUtils.isBlank(description) ) {
            system.opsAudit( "" );

            for ( String line : description ) {
                PrettyPrinter.printWrapped( system.opsLog, line, 80 );
            }
        }

        system.opsAudit( "" );
        system.opsAudit( "" );

        String[] descriptions = getParameterDescriptions();

        if ( descriptions.length > 0 ) {
            int maxParameterNameLength = PrettyPrinter.longestLength( parameterNames );
            PrettyPrinter p = new PrettyPrinter(system.opsLog, 4, maxParameterNameLength, 3, 120-7-maxParameterNameLength);
            for ( int i=0; i< parameterNames.length; i++ ) {
                p.write( "", parameterNames[i], " - ", descriptions[i] );
            }

            system.opsAudit( "" );
            system.opsAudit( "" );
        }
    }


}
