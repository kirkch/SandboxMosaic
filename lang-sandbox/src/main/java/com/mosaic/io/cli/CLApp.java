package com.mosaic.io.cli;

import com.mosaic.collections.ConsList;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileContents;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.io.streams.PrettyPrinter;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.Function0;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.LiveSystem;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.time.DTM;
import com.mosaic.lang.time.Duration;
import com.mosaic.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 *
 */
@SuppressWarnings("unchecked")
public abstract class CLApp {

    protected final SystemX system;

    private String                name;
    private List<String>          description;

    private List<CLParameter>     allParameters  = new ArrayList<>();

    private Set<String>           optionNames    = new HashSet<>();
    private List<CLArgument>      args           = new ArrayList<>();
    private List<CLOption>        options        = new ArrayList<>();

    private DTM                   startedAt;

    private CLOption<Boolean>     helpFlag;
    private CLOption<Boolean>     isVerboseFlag;   // NB there is no debug flag; to enable that pass -ea to the JVM on startup..
    private CLOption<String>      configFile;

    private CLOption<String>      clockFile;

    private Function0<DirectoryX> dataDirectoryFetcherNbl;


    protected CLApp( String name ) {
        this( new LiveSystem(name) );
    }

    protected CLApp( SystemX system ) {
        this.system = system;
        this.name   = system.getServiceName();
    }


    protected abstract int run() throws Exception;

    /**
     * Override if you want to invoke code before the app starts to shutdown the services that it
     * depends upon.
     */
    protected void beforeShutdown() {}

    /**
     * Override if you want to invoke code after the app has finished shutting down the services
     * that it depends upon.
     */
    protected void afterShutdown() {
        if ( lockFile != null ) {
            lockFile.delete();

            lockFile = null;
        }
    }


    private void handleSetUp() {
        configFile    = registerOption( "c", "config", "file", "Any command line option may be included within a properties file and specified here." );
        helpFlag      = registerFlag( "?", "help", "Display this usage information." );
        isVerboseFlag = registerFlag( "v", "verbose", "Include operational context in logging suitable for Ops. To enable full developer debugging output then pass -ea to the JVM." );


        clockFile     = registerOption( null, "Xclock", "file", "Share system time via the specified file.  Only used for testing purposes." );
    }


    private void handleTearDown() {
        beforeShutdown();

        DTM      nowDTM   = system.getCurrentDTM();
        Duration duration = nowDTM.subtract( startedAt );

        system.stop();

        afterShutdown();


        system.opsAudit( "Ran for %s.  Ended at %2d:%02d:%02d UTC on %04d/%02d/%02d.",
            duration.toString(),
            nowDTM.getHour(), nowDTM.getMinutes(), nowDTM.getSeconds(),
            nowDTM.getYear(), nowDTM.getMonth(), nowDTM.getDayOfMonth() );
    }




    private static final int MAX_LINE_LENGTH = 80;

    public final int runApp( String...inputArgs ) {
        handleSetUp();


        ConsList<String> normalisedArgs = normaliseInputArgs( inputArgs );

        boolean successfullySetArgumentsFlag = consumeInputArgs( normalisedArgs );
        if ( !successfullySetArgumentsFlag ) {
            return 1;
        }

        system.setDevAuditEnabled( SystemX.isDebugRun() );
        system.setOpsAuditEnabled( isVerboseFlag.getValue() || SystemX.isDebugRun()  );

        startedAt = system.getCurrentDTM();

        if ( clockFile.getValue() != null ) {
            File f = new File(clockFile.getValue());

            system.clock.memoryMapClock( f );

            system.opsAudit( "System time is coming from: " + f );
        }


        auditArgs( normalisedArgs );
        auditEnv();

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

        system.start();

        final AtomicBoolean hasShutdownAlready = new AtomicBoolean( false ); // flag to prevent running the shutdown twice
        Runtime.getRuntime().addShutdownHook( new Thread() {
            public void run() {
                if ( !hasShutdownAlready.get() ) {
                    handleTearDown();
                }
            }
        });


        try {
            if ( dataDirectoryFetcherNbl != null ) {
                return checkLockFileAndPossiblyRun();
            } else {
                return run();
            }


        } catch ( Throwable ex ) {
            system.stderr.writeLine( name + " was aborted unexpectedly.  The error was '"+ex.getMessage()+"'." );
            system.fatal( ex );

            return 1;
        } finally {
            handleTearDown();

            hasShutdownAlready.set( true );
        }
    }

    private FileX lockFile;

    private FileX getLockFile( DirectoryX dir ) {
        if ( lockFile == null  ) {
            lockFile = dir.getFile( "LOCK" );
        }

        return lockFile;
    }

    private FileX getOrCreateLockFile( DirectoryX dir ) {
        if ( lockFile == null  ) {
            lockFile = dir.getOrCreateFile( "LOCK" );
        }

        return lockFile;
    }

    private int checkLockFileAndPossiblyRun() throws Exception {
        DirectoryX dataDir          = dataDirectoryFetcherNbl.invoke();
        FileX      existingLockFile = getLockFile( dataDir );

        boolean needsToRunRecoveryMethod;
        if ( existingLockFile != null ) {
            FileContents fc = existingLockFile.openFile( FileModeEnum.READ_WRITE );

            if ( fc.lockFile() ) {
                needsToRunRecoveryMethod = true;

                existingLockFile.delete();
                lockFile = null;

                // NB no need to release fc lock as we deleted the file
            } else {
                system.fatal( "Application is already running, only one instance is allowed at a time." );

                // NB no need to release fc lock as we failed to get it

                return 1;
            }
        } else {
            needsToRunRecoveryMethod = false;
        }


        FileX newLockFile = getOrCreateLockFile( dataDir );

        return newLockFile.rw( fc -> {
            fc.lockFile();

            try {
                if ( needsToRunRecoveryMethod ) {
                    recoverFromCrash();
                }

                String pid = Integer.toString( system.getProcessId() );
                fc.resize( pid.length() );
                fc.writeUTF8StringUndemarcated( 0, pid.length(), pid );

                return run();
            } catch ( Exception e ) {
                Backdoor.throwException( e );
                return 1; // unreachable ;)
            } finally {
                fc.unlockFile();
                newLockFile.delete();
            }
        });
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

        FileX configFile = system.getCurrentWorkingDirectory().getFile(path);
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

    protected <T> CLArgument<T> registerArgumentOptional( String argumentName, String argumentDescription, Function1<String,T> parseFunction ) {
        CLArgument<T> arg = new CLArgument( argumentName, argumentDescription, parseFunction );
        arg.setOptional( true );


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

    /**
     * Returns function that returns the specified service.  The factory method will be guaranteed
     * to run only once, and that single instance will be registered with the CLApp's life cycle.
     * This all happens lazily, thus ensuring that it happens after the CLArguments have all
     * been processed and not at all if the service is not actually used.
     */
    protected <T> Function0<T> registerService( final Function0<T> serviceFactory ) {
        return new Function0<T>() {
            private T service;

            public T invoke() {
                if ( service == null ) {
                    this.service = serviceFactory.invoke();

                    system.registerService( service );
                }

                return service;
            }
        };
    }

    protected CLArgument<Iterable<FileX>> scanForFilesArgument( String argumentName, String argumentDescription, final String filePostfix ) {
        return registerArgument(
            argumentName,
            argumentDescription,
            new Function1<String, Iterable<FileX>>() {
                public Iterable<FileX> invoke( String path ) {
                    DirectoryX cwd  = system.getCurrentWorkingDirectory();
                    FileX      file = cwd.getFile( path );

                    if ( file != null ) {
                        return Arrays.asList( file );
                    }



                    DirectoryX inputDirectory = cwd.getDirectory( path );
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
                    DirectoryX cwd = system.getCurrentWorkingDirectory();

                    return cwd.getOrCreateDirectory( path );
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

    protected CLOption<Long> registerOptionLong( String shortName, String longName, String argName, String description ) {
        return registerOptionLong( shortName, longName, argName, description, null );
    }

    protected CLOption<Long> registerOptionLong( String shortName, String longName, String argName, String description, Long initialValue ) {
        Function1<String,Long> argParser = new Function1<String, Long>() {
            public Long invoke( String arg ) {
                if ( StringUtils.isBlank(arg) ) {
                    return null;
                } else {
                    return Long.parseLong( arg );
                }
            }
        };

        CLOption<Long> option = CLOption.createOption( shortName, longName, argName, description, initialValue, argParser );

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

        if ( !StringUtils.isBlank(shortName) ) {
            optionNames.add( shortName );
        }

        optionNames.add( longName );

        return opt;
    }

    /**
     * Specify that the app is to take out a lock file in the specified directory on startup.
     * If the lock file already exists and owned by a running process, verified by the PID stored
     * as text within the LOCK file, then the app will not start up and will exit with
     * a fatal message reporting that another instance is running. <p/>
     *
     * Once the app has finished, the LOCK file will be removed automatically.  However, if the app
     * crashes and thus does not get a chance to cleanup gracefully then the LOCK file will remain
     * and the PID stored within the LOCK file will not point to a valid process.  In this event,
     * restarting the application will detect that the application did not shutdown cleanly and will
     * take over ownership of the LOCK file before invoking 'recoverFromCrash()'.  Which will
     * allow the application to perform any custom cleanup from the crashed app before restarting
     * the application.
     *
     * @param dataDirectoryFetcher the function that retrieves which directory is to store the lock file
     *
     * @see #recoverFromCrash()
     */
    protected void useLockFile( Function0<DirectoryX> dataDirectoryFetcher ) {
        QA.argNotNull( dataDirectoryFetcher, "dataDirectoryFetcher" );

        this.dataDirectoryFetcherNbl = dataDirectoryFetcher;
    }

    /**
     * Invoked when restarting an application that did not shutdown cleanly previously.  Override
     * to recover from crashes, as detected by a stale LOCK file.  By default this method will
     * throw an exception, which will abort the application.
     *
     * @see #useLockFile(com.mosaic.lang.functional.Function0)
     */
    protected void recoverFromCrash() {
        throw new IllegalStateException( "A previous run of the app did not clean up after itself, manual recovery required. Aborting.." );
    }


    private void throwIfInvalidShortName( String shortName ) {
        if ( shortName != null && shortName.length() > 1 ) {
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

            if ( option.getShortName() != null && option.getShortName().equals(newName) ) {
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
