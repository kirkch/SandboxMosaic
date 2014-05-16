package com.mosaic.io.cli;

import com.mosaic.io.streams.PrettyPrinter;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.system.SystemX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 *
 */
@SuppressWarnings("unchecked")
public abstract class CLApp2 {

    protected final SystemX system;

    private List<String> description = Collections.EMPTY_LIST;

    private List<CLArgument> args = new ArrayList<>();



    protected CLApp2( SystemX system ) {
        this.system = system;
    }


    protected abstract int _run();


    private static final int MAX_LINE_LENGTH = 80;

    public final int runApp( String...inputArgs ) {
        if ( inputArgs.length == 1 && inputArgs[0].equals("--help") ) {
            printHelp();

            return 0;
        }

        boolean successfullySetArgumentsFlag = setArgumentValues( inputArgs );
        if ( !successfullySetArgumentsFlag ) {
            return 1;
        }

        if ( !hasAllMandatoryArguments() ) {
            String missingArgumentName = fetchNameOfFirstMissingArgument();

            system.stderr.writeLine( "Missing required argument '"+missingArgumentName+"', for more information invoke with --help." );

            return 1;
        }

        return _run();
    }

    protected void setDescription( String...description ) {
        this.description = Arrays.asList(description);
    }

    protected CLArgument<String> registerArgument( String argumentName, String argumentDescription ) {
        return registerArgument( argumentName, argumentDescription, CLArgument.NO_OP_PARSER );
    }

    /**
     * Registers a mandatory argument.  Mandatory arguments must appear before optional arguments.
     *
     * @param parseFunction parse the argument, throws an exception if something went wrong
     */
    protected <T> CLArgument<T> registerArgument( String argumentName, String argumentDescription, Function1<String,T> parseFunction ) {
        throwIfAnOptionalArgumentHasBeenDeclared( argumentName );

        CLArgument<T> arg = new CLArgument( argumentName, argumentDescription, parseFunction );

        this.args.add( arg );

        return arg;
    }

    protected CLArgument<String> registerArgumentOptional( String argumentName, String argumentDescription ) {
        CLArgument<String> arg = CLArgument.stringArgument( argumentName, argumentDescription );
        arg.setOptional( true );


        this.args.add( arg );

        return arg;
    }

    private void throwIfAnOptionalArgumentHasBeenDeclared( String newArgumentName ) {
        for ( CLArgument arg : args ) {
            if ( arg.isOptional() ) {
                throw new IllegalStateException( "Mandatory argument '"+newArgumentName+"' must be declared before all optional arguments" );
            }
        }
    }


    private boolean setArgumentValues( String[] inputArgs ) {
        for ( int i=0; i<Math.min( inputArgs.length, args.size() ); i++ ) {
            CLArgument arg = args.get( i );

            try {
                arg.setValue( inputArgs[i] );
            } catch ( Exception ex ) {
                String msg = "Invalid value for '" + arg.getArgumentName() + "', for more information invoke with --help.";

                system.fatal( ex, msg );

                return false;
            }
        }

        return true;
    }


    private boolean hasAllMandatoryArguments() {
        return fetchNameOfFirstMissingArgument() == null;
    }

    private String fetchNameOfFirstMissingArgument() {
        for ( CLArgument arg : args ) {
            if ( arg.isMandatory() ) {
                if ( arg.isEmpty() ) {
                    return arg.getArgumentName();
                }
            } else {
                // NB mandatory arguments must go before optional ones
                return null;
            }
        }

        return null;
    }

    private void printHelp() {
        String name              = this.getClass().getName();
        String formattedArgNames = formattedArgNames();

        system.stdout.writeLine( "Usage: " + name + formattedArgNames );
        system.stdout.writeLine( "" );

        if ( !description.isEmpty() ) {
            for ( String line : description ) {
                PrettyPrinter.printWrapped( system.stdout, line, MAX_LINE_LENGTH );
            }

            system.stdout.writeLine( "" );
        }

        if ( !args.isEmpty() ) {
            int maxArgNameLength = calcLongestArgNameLength();
            PrettyPrinter p = new PrettyPrinter(system.stdout, 3, maxArgNameLength, 1, MAX_LINE_LENGTH-7-maxArgNameLength);
            p.setColumnHandler( 3, PrettyPrinter.WRAP );

            for ( CLArgument arg : args ) {
                p.write( "", arg.getArgumentName(), "-", arg.getArgumentDescription() );
            }

            system.stdout.writeLine( "" );
        }

        system.stdout.writeLine( "optional flags:" );
        system.stdout.writeLine( "" );
        system.stdout.writeLine( "    --help display this usage information" );
        system.stdout.writeLine( "" );
    }

    private String formattedArgNames() {
        StringBuilder buf = new StringBuilder();

        for ( CLArgument arg : args ) {
            buf.append( " " );
            if ( arg.isOptional() ) {
                buf.append( '[' );
                buf.append( arg.getArgumentName() );
                buf.append( ']' );
            } else {
                buf.append( arg.getArgumentName() );
            }
        }

        return buf.toString();
    }

    private int calcLongestArgNameLength() {
        int max = 0;

        for ( CLArgument arg : args ) {
            max = Math.max( max, arg.getArgumentName().length() );
        }

        return max;
    }

}
