package com.mosaic.io.cli;

import com.mosaic.io.streams.PrettyPrinter;
import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.lang.system.LiveSystem;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.StringUtils;

import java.lang.reflect.Method;


/**
 *
 */
public abstract class CLApp {

    public SystemX system = new LiveSystem();

    public final void runApp( String...args ) {
        Method m = ReflectionUtils.findFirstPublicMethodByName( this.getClass(), "run" );

        if ( m == null ) {
            printUsage();
        } else if ( m.getParameterTypes().length > args.length ) {
            printMissingRequiredArgumentsErrorMessage( args );

            printUsage();
        } else {
            try {
                ReflectionUtils.invoke( this, m, (Object[]) args );
            } catch ( IllegalArgumentException ex ) {
                system.error( "Unable to proceed: %s", ex.getMessage() );

                printUsage();
            } catch ( Throwable ex ) {
                system.error( ex );
            }
        }
    }

    private void printMissingRequiredArgumentsErrorMessage( String[] args ) {
        String[] paramNames = getParameterNames();

        system.error.writeString( "Unable to proceed, missing required command line " );
        PrettyPrinter.printPleural( system.error, "argument", paramNames.length-args.length );
        system.error.writeString( " '" );
        PrettyPrinter.englishList( system.error, paramNames, args.length, paramNames.length );
        system.error.writeLine( "'" );
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

        system.info( buf.toString() );
        String[] description = getUsageAppDescription();

        if ( !StringUtils.isBlank(description) ) {
            system.info( "" );

            for ( String line : description ) {
                PrettyPrinter.printWrapped( system.info, line, 80 );
            }
        }

        system.info( "" );
        system.info( "" );

        String[] descriptions = getParameterDescriptions();

        if ( descriptions.length > 0 ) {
            int maxParameterNameLength = PrettyPrinter.longestLength( parameterNames );
            PrettyPrinter p = new PrettyPrinter(system.info, 4, maxParameterNameLength, 3, 120-7-maxParameterNameLength);
            for ( int i=0; i< parameterNames.length; i++ ) {
                p.write( "", parameterNames[i], " - ", descriptions[i] );
            }

            system.info( "" );
            system.info( "" );
        }
    }


}