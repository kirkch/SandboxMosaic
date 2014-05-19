package com.mosaic.io.cli;

import com.mosaic.io.streams.PrettyPrinter;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.system.SystemX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
@SuppressWarnings("unchecked")
public abstract class CLApp2 {

    protected final SystemX system;

    private String description;

    private List<CLArgument>     args           = new ArrayList<>();
    private List<CLOption>       options        = new ArrayList<>();


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

        boolean successfullySetArgumentsFlag = consumeInputArgs( inputArgs );
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

    protected void setDescription( String description ) {
        this.description = PrettyPrinter.cleanEnglishSentence(description);
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

    protected CLOption<Boolean> registerFlag( String shortName, String longName, String description ) {
        CLOption<Boolean> option = CLOption.createBooleanFlag( shortName, longName, description );

        options.add( option );

        // todo error if name clash

        return option;
    }

    protected CLOption<String> registerOption( String shortName, String longName, String argName, String description ) {
        Function1<String,String> argParser = new Function1<String, String>() {
            public String invoke( String arg ) {
                return arg;
            }
        };

        CLOption<String> option = CLOption.createOption( shortName, longName, argName, description, null, argParser );

        options.add( option );

        // todo error if name clash

        return option;
    }

    protected <T> CLOption<T> registerOption( String shortName, String longName, String argName, String description, T initialValue, Function1<String,T> valueParser ) {
        CLOption<T> option = CLOption.createOption( shortName, longName, argName, description, initialValue, valueParser );

        options.add( option );

        // todo error if name clash

        return option;
    }


    private void throwIfAnOptionalArgumentHasBeenDeclared( String newArgumentName ) {
        for ( CLArgument arg : args ) {
            if ( arg.isOptional() ) {
                throw new IllegalStateException( "Mandatory argument '"+newArgumentName+"' must be declared before all optional arguments" );
            }
        }
    }


    private boolean consumeInputArgs( String[] inputArgs ) {
        int numArgsConsumed;

        try {
            numArgsConsumed = consumeFlags( inputArgs );
        } catch ( CLException ex ) {
            system.fatal( ex.getMessage() );
            system.debug( ex, ex.getMessage() );

            return false;
        }

        List<String> remainingArgs = Arrays.asList( inputArgs ).subList( numArgsConsumed, inputArgs.length );
        int          maxIndex      = Math.min( remainingArgs.size(), args.size() );

        for ( int i=0; i< maxIndex; i++ ) {
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



    private int consumeFlags( String[] inputArgs ) {
        int i=0;

        while ( i<inputArgs.length ) {
            final int original = i;

            for ( CLOption option : options ) {
                i = option.consumeCommandLineArgs( inputArgs, i );
            }

            // exit early if a loop through the options did not result in any matches
            if ( i == original ) {
                return i;
            }
        }

        return i;
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
        system.stdout.newLine();

        if ( description != null ) {
            PrettyPrinter.printWrapped( system.stdout, description, MAX_LINE_LENGTH );

            system.stdout.newLine();
        }

        if ( !args.isEmpty() ) {
            int maxArgNameLength = calcLongestArgNameLength();
            PrettyPrinter p = new PrettyPrinter(system.stdout, 3, maxArgNameLength, 1, MAX_LINE_LENGTH-7-maxArgNameLength);
            p.setColumnHandler( 3, PrettyPrinter.WRAP );

            for ( CLArgument arg : args ) {
                p.write( "", arg.getArgumentName(), "-", arg.getArgumentDescription() );
            }

            system.stdout.newLine();
        }

        system.stdout.writeLine( "Options:" );

        for ( CLOption option : options ) {
            system.stdout.writeLine( "" );

            option.printHelpSummary( system.stdout, MAX_LINE_LENGTH );
        }

        system.stdout.newLine();
        system.stdout.writeLine( "    --help" );
        system.stdout.writeLine( "        Display this usage information." ); // todo move to CLOption
        system.stdout.newLine();
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
