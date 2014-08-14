package com.mosaic.io.cli;

import com.mosaic.collections.ConsList;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.io.streams.PrettyPrinter;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.system.LiveSystem;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.time.DTM;
import com.mosaic.lang.time.Duration;
import com.mosaic.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *
 */
@SuppressWarnings("unchecked")
public abstract class CLApp {

    protected final SystemX system;

    private String       name;
    private List<String> description;

    private List<CLParameter> allParameters = new ArrayList<>();

    private Set<String>          optionNames    = new HashSet<>();
    private List<CLArgument>     args           = new ArrayList<>();
    private List<CLOption>       options        = new ArrayList<>();

    private DTM                  startedAt;

    private CLOption<Boolean> helpFlag;
    private CLOption<String>  configFile;


    protected CLApp() {
        this( new LiveSystem() );
    }

    protected CLApp( SystemX system ) {
        this.system = system;
        this.name   = this.getClass().getName();
    }


    protected abstract int _run() throws Exception;

    protected void setUpCallback() {}
    protected void tearDownCallback() {}



    private void handleSetUp() {
        setUpCallback();

        configFile = registerOption( "c", "config", "file", "Any command line option may be included within a properties file and specified here." );
        helpFlag   = registerFlag( "?", "help", "Display this usage information." );

        startedAt = system.getCurrentDTM();
    }


    private void handleTearDown() {
        tearDownCallback();

        DTM      nowDTM   = system.getCurrentDTM();
        Duration duration = nowDTM.subtract( startedAt );

        system.opsAudit( "Ran for %s.  Ended at %2d:%02d:%02d UTC on %04d/%02d/%02d.",
            duration.toString(),
            nowDTM.getHour(), nowDTM.getMinutes(), nowDTM.getSeconds(),
            nowDTM.getYear(), nowDTM.getMonth(), nowDTM.getDayOfMonth() );
    }




    private static final int MAX_LINE_LENGTH = 80;

    public final int runApp( String...inputArgs ) {
        handleSetUp();


        ConsList<String> normalisedArgs = normaliseInputArgs( inputArgs );

        auditArgs( normalisedArgs );
        auditEnv();

        boolean successfullySetArgumentsFlag = consumeInputArgs( normalisedArgs );
        if ( !successfullySetArgumentsFlag ) {
            return 1;
        }

        auditParameters();

        if ( !loadConfigIfAvailable() ) {
            return 1;
        }

        if ( helpFlag.getValue() ) {
            printHelp();

            return 0;
        }

        if ( !hasAllMandatoryArguments() ) {
            String missingArgumentName = fetchNameOfFirstMissingArgument();

            system.fatal( "Missing required argument '" + missingArgumentName + "', for more information invoke with --help." );

            return 1;
        }

        try {
            return _run();
        } catch ( Throwable ex ) {
            system.stderr.writeLine( name + " errored unexpectedly and was aborted. The error was 'RuntimeException:whoops'." );
            system.fatal( ex );

            return 1;
        } finally {
            handleTearDown();
        }
    }

    private void auditParameters() {
        if ( !system.isOpsAuditEnabled() ) {
            return;
        }

        system.opsAudit( "Config:" );

        for ( CLParameter param : allParameters ) {
            system.opsAudit( "  %s=%s", param.getLongName(), param.getValue() );
        }
    }

    private boolean loadConfigIfAvailable() {
        String path = this.configFile.getValue();
        if ( path == null ) {
            return true;
        }

        FileX configFile = system.fileSystem.getFile(path);
        if ( configFile == null ) {
            system.fatal( "Unable to find file '"+path+"' specified by --config." );

            return false;
        } else if ( !configFile.isReadable() ) {
            system.fatal( "Unable to load file '"+path+"' specified by --config, permission denied." );

            return false;
        } else {
            Map<String,String> props = configFile.loadProperties();

            for ( Map.Entry<String,String> e : props.entrySet() ) {
                CLParameter p = findParameterByLongName( e.getKey() );

                if ( p == null ) {
                    system.fatal( "Unknown setting '%s' in '%s' specified by --config.", e.getKey(), path );

                    return false;
                } else if ( p.getValue() == null ) {
                    p.setValue( e.getValue() );
                } else {
                    // The value has already been set explicitly via the command line, so ignore the setting in the config file
                }
            }

            return true;
        }
    }

    private CLParameter findParameterByLongName( String key ) {
        for ( CLParameter p : allParameters ) {
            if ( key.equals(p.getLongName()) ) {
                return p;
            }
        }

        return null;
    }

    private void auditArgs( ConsList<String> normalisedArgs ) {
        StringBuilder buf = new StringBuilder();

        buf.append( "Command: " );
        buf.append( getName() );

        for( String arg : normalisedArgs ) {
            buf.append( ' ' );

            if ( arg.startsWith("-") ) {
                buf.append( arg );
            } else {
                buf.append( '\'' );
                buf.append( arg );
                buf.append( '\'' );
            }
        }

        system.opsAudit( buf.toString() );
    }

    private void auditEnv() {
        system.opsAudit( "Started at %2d:%02d:%02d UTC on %04d/%02d/%02d",
            startedAt.getHour(), startedAt.getMinutes(), startedAt.getSeconds(),
            startedAt.getYear(), startedAt.getMonth(), startedAt.getDayOfMonth() );

        system.opsAudit( "Ran by: %s", System.getProperty( "user.name" ) );

        system.opsAudit( "Java: %s (%s %s)",
            System.getProperty( "java.runtime.version" ),
            System.getProperty( "java.vm.name" ),
            System.getProperty( "java.vm.vendor" ) );

        system.opsAudit( "OS: %s (%s %s)",
            System.getProperty("os.name"),
            System.getProperty("os.version"),
            System.getProperty("os.arch") );

        system.opsAudit( "Classpath: %s", System.getProperty("java.class.path") );
        system.opsAudit( "Library Path: %s", System.getProperty( "java.library.path" ) );
    }

    protected void setDescription( String...description ) {
        this.description = new ArrayList();

        for ( String line : description ) {
            this.description.add( PrettyPrinter.cleanEnglishSentence( line ) );
        }
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
        this.allParameters.add( arg );

        return arg;
    }

    protected CLArgument<String> registerArgumentOptional( String argumentName, String argumentDescription ) {
        CLArgument<String> arg = CLArgument.stringArgument( argumentName, argumentDescription );
        arg.setOptional( true );


        this.args.add( arg );
        this.allParameters.add( arg );

        return arg;
    }

    protected CLArgument<Iterable<FileX>> scanForFilesArgument( String argumentName, String argumentDescription, final String filePostfix ) {
        return registerArgument(
            argumentName,
            argumentDescription,
            new Function1<String, Iterable<FileX>>() {
                public Iterable<FileX> invoke( String path ) {
                    FileX file = system.fileSystem.getFile( path );
                    if ( file != null ) {
                        return Arrays.asList( file );
                    }

                    DirectoryX inputDirectory = system.getDirectory( path );
                    if ( inputDirectory == null )  {
                        throw new CLException( "Directory '"+path+"' does not exist." );
                    }
                    List<FileX> files = inputDirectory.files( filePostfix );
                    return files;
                }
            }
        );
    }

    protected CLArgument<DirectoryX> getOrCreateDirectoryArgument( String argumentName, String argumentDescription ) {
        return registerArgument(
            argumentName,
            argumentDescription,
            new Function1<String, DirectoryX>() {
                public DirectoryX invoke( String path ) {
                    return system.getOrCreateDirectory( path );
                }
            }
        );
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

    protected <T extends Enum<T>> CLOption<T> registerEnum( String shortName, String longName, String description, Class<T> enumClass ) {
        CLOption<T> option = CLOption.createEnumOption( shortName, longName, description, enumClass, null );

        return registerOption( option );
    }

    protected <T extends Enum<T>> CLOption<T> registerEnum( String shortName, String longName, String description, Class<T> enumClass, T defaultValue ) {
        CLOption<T> option = CLOption.createEnumOption( shortName, longName, description, enumClass, defaultValue );

        return registerOption( option );
    }

    private <T> CLOption<T> registerOption( CLOption<T> opt ) {
        String shortName = opt.getShortName();
        String longName  = opt.getLongName();

        throwIfInvalidShortName( shortName );
        throwIfOptionNameHasAlreadyBeenTaken( shortName );
        throwIfOptionNameHasAlreadyBeenTaken( longName );


        options.add( opt );
        allParameters.add( opt );

        if ( shortName.length() > 0 ) {
            optionNames.add( shortName );
        }

        optionNames.add( longName );

        return opt;
    }

    private void throwIfInvalidShortName( String shortName ) {
        if ( shortName.length() > 1 ) {
            throw new IllegalArgumentException( "'"+shortName+"' is not a valid short name, short names can only be one character long" );
        }
    }


    private void throwIfArgumentNameHasAlreadyBeenRegistered( String argumentName ) {
        for ( CLArgument arg : args ) {
            if ( arg.getLongName().equals(argumentName) ) {
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
    private ConsList<String> normaliseInputArgs( String[] inputArgs ) {
        List<String> normalisedArgs = new ArrayList<>();

        for ( String arg : inputArgs ) {
            normalisedInputArg( normalisedArgs, arg );
        }

        return ConsList.newConsList( normalisedArgs );
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


    private boolean consumeInputArgs( ConsList<String> inputArgs ) {
        ConsList<String> unprocessedArgs = inputArgs;

        while ( unprocessedArgs.hasContents() ) {
            ConsList<String> start = unprocessedArgs;

            for ( CLParameter p : allParameters ) {
                if ( unprocessedArgs.isEmpty() ) {
                    return true;
                }

                try {
                    unprocessedArgs = p.tryToConsumeInput( unprocessedArgs );
                } catch ( CLException ex ) {
                    system.fatal( ex, ex.getMessage() );

                    return false;
                }
            }

            if ( start == unprocessedArgs ) { // no progress has been made
                system.fatal( "Unknown flag '"+unprocessedArgs.head()+"'.  Run with --help for more information." );

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
            if ( !arg.hasValidValue() ) {
                return arg.getLongName();
            }
        }

        return null;
    }

    private void printHelp() {
        system.stdout.newLine();

        String name              = this.getClass().getName();
        String formattedArgNames = formattedArgNames();

        system.stdout.writeLine( "Usage: " + name + formattedArgNames );
        system.stdout.newLine();

        if ( description != null ) {
            for ( String line : description ) {
                if ( StringUtils.isBlank(line) ) {
                    system.stdout.newLine();
                } else {
                    PrettyPrinter.printWrapped( system.stdout, line, MAX_LINE_LENGTH, "    " );
                }
            }

            system.stdout.newLine();
        }

        if ( !args.isEmpty() ) {
            system.stdout.writeLine( "Arguments:" );
            system.stdout.writeLine( "" );

            int maxArgNameLength = calcLongestArgNameLength();
            PrettyPrinter p = new PrettyPrinter(system.stdout, 3, maxArgNameLength, 1, MAX_LINE_LENGTH-7-maxArgNameLength);
            p.setColumnHandler( 3, PrettyPrinter.WRAP );

            for ( CLArgument arg : args ) {
                p.write( "", arg.getLongName(), "-", arg.getArgumentDescription() );
            }

            system.stdout.newLine();
        }

        system.stdout.writeLine( "Options:" );

        for ( CLOption option : options ) {
            system.stdout.writeLine( "" );

            option.printHelpSummary( system.stdout, MAX_LINE_LENGTH );
        }

        system.stdout.newLine();
    }

    private String formattedArgNames() {
        StringBuilder buf = new StringBuilder();

        for ( CLArgument arg : args ) {
            buf.append( " " );
            if ( arg.isOptional() ) {
                buf.append( '[' );
                buf.append( arg.getLongName() );
                buf.append( ']' );
            } else {
                buf.append( arg.getLongName() );
            }
        }

        return buf.toString();
    }

    private int calcLongestArgNameLength() {
        int max = 0;

        for ( CLArgument arg : args ) {
            max = Math.max( max, arg.getLongName().length() );
        }

        return max;
    }

}
