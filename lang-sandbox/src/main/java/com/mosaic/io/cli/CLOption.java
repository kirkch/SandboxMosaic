package com.mosaic.io.cli;

import com.mosaic.io.streams.CharacterStream;
import com.mosaic.io.streams.PrettyPrinter;
import com.mosaic.lang.functional.Function1;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public abstract class CLOption<T> {

    public static CLOption<Boolean> createBooleanFlag( String shortName, String longName, String description ) {
        return new FlagImpl( shortName, longName, description );
    }

    public static <T> CLOption<T> createOption( String shortName, String longName, String paramName, String description, T defaultValue, Function1<String,T> valueParser ) {
        return new OptionImpl<>( shortName, longName, paramName, description, defaultValue, valueParser );
    }


    private final String               shortName;
    private final String               longName;
    private final String               description;
    private final Function1<String, T> valueParser;

    private T value;
    private T initialValue;


    public CLOption( String shortName, String longName, String flagDescription, T initialValue, Function1<String, T> valueParser ) {
        this.shortName    = shortName;
        this.longName     = longName;
        this.description  = PrettyPrinter.cleanEnglishSentence( flagDescription );
        this.valueParser  = valueParser;
        this.value        = initialValue;
        this.initialValue = initialValue;
    }


    public void printHelpSummary( CharacterStream out, int maxLineLength ) {
        out.writeString( "    " );

        List<String> aliases = getAliases();
        boolean printComma = false;
        for ( String alias : aliases ) {
            if ( printComma ) {
                out.writeString( ", " );
            } else {
                printComma = true;
            }

            out.writeString( alias );
        }
        out.newLine();

        PrettyPrinter p = new PrettyPrinter( out, 7, maxLineLength-7 );
        p.setColumnHandler( 1, PrettyPrinter.WRAP );

        if ( initialValue == null || initialValue == Boolean.FALSE ) {
            p.write( "", description );
        } else {
            p.write( "", description + " Defaults to " + initialValue + "." );
        }
    }

    protected abstract List<String> getAliases();

    /**
     * Parse the argument at args[i].  Returning the index for the next option to try and consume
     * their argument at.  Thus return i if there was no match, and i+n if n args were matched; typically
     * n will be 1 for boolean flags and 2 for options that take one parameter.
     */
    public abstract int consumeCommandLineArgs( String[] args, int i );

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public T getValue() {
        return value;
    }

    public void setValue( String v ) {
        try {
            this.value = valueParser.invoke( v );
        } catch ( Exception ex ) {
            throw new CLException( "Invalid value '"+v+"' for option '"+longName+"'", ex );
        }
    }

    public String getDescription() {
        return description;
    }

}


class FlagImpl extends CLOption<Boolean> {
    private static final Function1<String,Boolean> PARSE_BOOLEAN_FUNCTION = new Function1<String, Boolean>() {
        public Boolean invoke( String arg ) {
            return arg.equals("true") ? Boolean.TRUE : Boolean.FALSE;
        }
    };


    public FlagImpl( String shortName, String longName, String flagDescription ) {
        super( shortName, longName, flagDescription, Boolean.FALSE, PARSE_BOOLEAN_FUNCTION );
    }

    protected List<String> getAliases() {
        List<String> aliases = new ArrayList<>();

        if ( getShortName() != null ) {
            aliases.add( "-"+getShortName() );
        }

        if ( getLongName() != null ) {
            aliases.add( "--"+getLongName() );
        }

        return aliases;
    }

    public int consumeCommandLineArgs( String[] args, int i ) {
        String arg = args[i];

        boolean matches = arg.equals( "-"+getShortName() ) || arg.equals( "--"+getLongName() );

        if ( matches ) {
            setValue( "true" );
        }

        return matches ? i+1 : i;
    }
}


class OptionImpl<T> extends CLOption<T> {

    private final String paramName;


    public OptionImpl( String shortName, String longName, String paramName, String description, T defaultValue, Function1<String,T> parseValueFunction ) {
        super( shortName, longName, description, defaultValue, parseValueFunction );
        this.paramName = paramName;
    }

    protected List<String> getAliases() {
        List<String> aliases = new ArrayList<>();

        if ( getShortName() != null ) {
            aliases.add( "-"+getShortName() + " <" + paramName + ">" );
        }

        if ( getLongName() != null ) {
            aliases.add( "--"+getLongName() + "=<" + paramName + ">" );
        }

        return aliases;
    }

    public int consumeCommandLineArgs( String[] args, int i ) {
        String arg = args[i];

        String shortFlag = "-" + getShortName();
        if ( arg.equals(shortFlag) ) {
            if ( i+1 == args.length ) {
                throw new IllegalArgumentException( args[i]+" requires a value. See --help for more information." );
            }

            setValue( args[i+1] );

            return i+2;
        } else if ( arg.startsWith(shortFlag) ) {

            setValue( args[i].substring(shortFlag.length()) );

            return i+1;
        } else if ( arg.startsWith("--"+getLongName()+"=") ) {
            setValue( arg.substring( getLongName().length()+3 ) );

            return i+1;
        } else {
            return i;
        }
    }
}
