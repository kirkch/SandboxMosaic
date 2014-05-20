package com.mosaic.io.cli;

import com.mosaic.io.streams.PrettyPrinter;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.time.DTM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 *
 */
@SuppressWarnings("unchecked")
public abstract class CLApp2 {

    protected final SystemX system;

    private String name;
    private String description;

    private Set<String>          optionNames    = new HashSet<>();
    private List<CLArgument>     args           = new ArrayList<>();
    private List<CLOption>       options        = new ArrayList<>();


    protected CLApp2( SystemX system ) {
        this.system = system;
        this.name   = this.getClass().getName();
    }


    protected abstract int _run();

    protected void setUpCallback() {}
    protected void tearDownCallback() {}



    private final void handleSetUp() {
        setUpCallback();

        DTM nowDTM = system.getCurrentDTM();

        system.audit( "%s started at %2d:%02d:%02d UTC on %04d/%02d/%02d",
            getName(), nowDTM.getHour(), nowDTM.getMinutes(), nowDTM.getSeconds(),
            nowDTM.getYear(), nowDTM.getMonth(), nowDTM.getDayOfMonth() );
    }

    private  final void handleTearDown() {
        tearDownCallback();
    }




    private static final int MAX_LINE_LENGTH = 80;

    public final int runApp( String...inputArgs ) {
        if ( inputArgs.length == 1 && inputArgs[0].equals("--help") ) { // todo
            printHelp();

            return 0;
        }

        String[] normalisedArgs = normaliseInputArgs( inputArgs );

        boolean successfullySetArgumentsFlag = consumeInputArgs( normalisedArgs );
        if ( !successfullySetArgumentsFlag ) {
            return 1;
        }

        if ( !hasAllMandatoryArguments() ) {
            String missingArgumentName = fetchNameOfFirstMissingArgument();

            system.stderr.writeLine( "Missing required argument '"+missingArgumentName+"', for more information invoke with --help." );

            return 1;
        }

        try {
            handleSetUp();

            return _run();
        } catch ( Throwable ex ) {
            system.stderr.writeLine( name + " errored unexpectedly and was aborted. The error was 'RuntimeException:whoops'." );
            system.fatal( ex );

            return 1;
        } finally {
            tearDownCallback();
        }
    }

    protected void setDescription( String description ) {
        this.description = PrettyPrinter.cleanEnglishSentence(description);
    }

    protected void setName( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
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
        throwIfArgumentNameHasAlreadyBeenRegistered( argumentName );

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

        return registerOption( option );
    }

    protected CLOption<String> registerOption( String shortName, String longName, String argName, String description ) {
        Function1<String,String> argParser = new Function1<String, String>() {
            public String invoke( String arg ) {
                return arg;
            }
        };

        CLOption<String> option = CLOption.createOption( shortName, longName, argName, description, null, argParser );

        return registerOption( option );
    }

    protected <T> CLOption<T> registerOption( String shortName, String longName, String argName, String description, T initialValue, Function1<String,T> valueParser ) {
        CLOption<T> option = CLOption.createOption( shortName, longName, argName, description, initialValue, valueParser );

        return registerOption( option );
    }

    private <T> CLOption<T> registerOption( CLOption<T> opt ) {
        String shortName = opt.getShortName();
        String longName  = opt.getLongName();

        throwIfInvalidShortName( shortName );
        throwIfOptionNameHasAlreadyBeenTaken( shortName );
        throwIfOptionNameHasAlreadyBeenTaken( longName );


        options.add( opt );

        optionNames.add( shortName );
        optionNames.add( longName );

        return opt;
    }

    private void throwIfInvalidShortName( String shortName ) {
        if ( shortName.length() != 1 ) {
            throw new IllegalArgumentException( "'"+shortName+"' is not a valid short name, short names can only be one character long" );
        }
    }


    private void throwIfArgumentNameHasAlreadyBeenRegistered( String argumentName ) {
        for ( CLArgument arg : args ) {
            if ( arg.getArgumentName().equals(argumentName) ) {
                throw new IllegalArgumentException( "'"+argumentName+"' has been declared twice" );
            }
        }
    }

    private void throwIfAnOptionalArgumentHasBeenDeclared( String newArgumentName ) {
        for ( CLArgument arg : args ) {
            if ( arg.isOptional() ) {
                throw new IllegalStateException( "Mandatory argument '"+newArgumentName+"' must be declared before all optional arguments" );
            }
        }
    }

    private void throwIfOptionNameHasAlreadyBeenTaken( String newName ) {
        for ( CLOption option : options ) {
            if ( option.getLongName().equals(newName) ) {
                throw new IllegalArgumentException( "'"+newName+"' has been declared twice" );
            }

            if ( option.getShortName().equals(newName) ) {
                throw new IllegalArgumentException( "'"+newName+"' has been declared twice" );
            }
        }
    }

    /**
     * Given the input args supplied from main(args), break up the flags that have been
     * concatenated together.
     */
    private String[] normaliseInputArgs( String[] inputArgs ) {
        List<String> normalisedArgs = new ArrayList<>();

        for ( String arg : inputArgs ) {
            normalisedInputArg( normalisedArgs, arg );
        }

        return normalisedArgs.toArray( new String[normalisedArgs.size()] );
    }



    private void normalisedInputArg( List<String> output, String arg ) {
        if ( arg.length() > 2 && arg.charAt(0) == '-' && arg.charAt(1) != '-' ) {
            for ( int i=1; i<arg.length(); i++ ) {
                char c = arg.charAt(i);

                // handles
                // -abc   -> -a -b -c
                // -ab123 -> -a -b 123

                // todo
                // -fooc   -> -foo -c
                // -abc123 -> -abc 123

                if ( optionNames.contains(Character.toString(c)) ) {
                    output.add( "-"+c );
                } else {
                    output.add( arg.substring(i) );

                    return;
                }
            }
        } else {
            output.add( arg );
        }
    }


    private boolean consumeInputArgs( String[] inputArgs ) {
        int numArgsConsumed;

        try {
            numArgsConsumed = consumeOptions( inputArgs );
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



    private int consumeOptions( String[] inputArgs ) {
        int i=0;

        while ( i<inputArgs.length ) {
            final int original = i;

            for ( CLOption option : options ) {
                i = option.consumeCommandLineArgs( inputArgs, i );

                if ( i >= inputArgs.length ) {
                    return i;
                }
            }

            // exit early if a loop through the options did not result in any matches
            if ( i == original ) {
                String arg = inputArgs[i];

                if ( arg.charAt(0) == '-' ) {
                    String name = arg.substring( arg.startsWith( "--" ) ? 2 : 1 );

                    throw new CLException( "Unknown flag '"+arg+"'.  Run with --help for more information." );
                }
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
