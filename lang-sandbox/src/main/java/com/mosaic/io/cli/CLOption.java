package com.mosaic.io.cli;

import com.mosaic.collections.ConsList;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.io.streams.PrettyPrinter;
import com.mosaic.lang.functional.Function1;
import com.mosaic.utils.ListUtils;
import com.mosaic.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 */
public abstract class CLOption<T> implements CLParameter<T> {

    public static CLOption<Boolean> createBooleanFlag( String shortName, String longName, String description ) {
        return new FlagOption( shortName, longName, description );
    }

    public static <T> CLOption<T> createOption( String shortName, String longName, String paramName, String description, T defaultValue, Function1<String,T> valueParser ) {
        return new ValueOption<>( shortName, longName, paramName, description, defaultValue, valueParser );
    }

    public static <T extends Enum<T>> CLOption<T> createEnumOption( String shortName, String longName, String description, Class<T> enumClass, T defaultValue ) {
        return new EnumOption<>( shortName, longName, description, enumClass, defaultValue );
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
            p.write( "", description + "  Defaults to " + formatValue(initialValue) + "." );
        }
    }

    protected abstract List<String> getAliases();


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

    public String formatValue( T v ) {
        if ( v.getClass().isEnum() ) {
            return PrettyPrinter.underscoreCaseToCamelCase( v.toString() );
        } else {
            return v.toString();
        }
    }
}


class FlagOption extends CLOption<Boolean> {
    private static final Function1<String,Boolean> PARSE_BOOLEAN_FUNCTION = new Function1<String, Boolean>() {
        public Boolean invoke( String arg ) {
            return arg.equals("true") ? Boolean.TRUE : Boolean.FALSE;
        }
    };


    public FlagOption( String shortName, String longName, String flagDescription ) {
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

    public ConsList<String> tryToConsumeInput( ConsList<String> unprocessedInput ) {
        String arg = unprocessedInput.head();

        boolean matches = arg.equals( "-"+getShortName() ) || arg.equals( "--"+getLongName() );

        if ( matches ) {
            setValue( "true" );
        }

        return matches ? unprocessedInput.tail() : unprocessedInput;
    }
}


class ValueOption<T> extends CLOption<T> {

    private final String paramName;


    public ValueOption( String shortName, String longName, String paramName, String description, T defaultValue, Function1<String, T> parseValueFunction ) {
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

    public ConsList<String> tryToConsumeInput( ConsList<String> unprocessedInput ) {
        String arg = unprocessedInput.head();

        if ( arg.equals("-" + getShortName()) || arg.equals("--"+getLongName()) ) {
            if ( unprocessedInput.tail().isEmpty() ) {
                throw new IllegalArgumentException( unprocessedInput.head()+" requires a value. See --help for more information." );
            }

            setValue( unprocessedInput.tail().head() );

            return unprocessedInput.tail().tail();
        } else if ( arg.startsWith("--"+getLongName()+"=") ) {
            setValue( arg.substring( getLongName().length()+3 ) );

            return unprocessedInput.tail();
        } else {
            return unprocessedInput;
        }
    }

    public String toString() {
        return getLongName();
    }

}



class EnumOption<T extends Enum<T>> extends ValueOption<T> {

    private static <T extends Enum<T>> String processDescription( String description, final Class<T> enumClass ) {
        StringBuilder buf = new StringBuilder();

        buf.append( PrettyPrinter.cleanEnglishSentence( description ).trim() );
        buf.append( "  The valid values are: " );

        List<T> enumValues = Arrays.asList( enumClass.getEnumConstants() );
        List<String> enumValueNames = ListUtils.map( enumValues, new Function1<T,String>() {
            public String invoke( T v ) {
                return PrettyPrinter.underscoreCaseToCamelCase( v.name() );
            }
        });

        PrettyPrinter.englishList( buf, enumValueNames, "or" );
        buf.append( "." );

        return buf.toString();
    }

    public EnumOption( String shortName, String longName, String description, final Class<T> enumClass, T defaultValue ) {
        super(
            shortName, longName,
            StringUtils.removePostFix( enumClass.getSimpleName(), "Enum" ).toLowerCase(),
            processDescription( description, enumClass ),
            defaultValue,
            new Function1<String, T>() {
                public T invoke( String arg ) {
                    return Enum.valueOf( enumClass, arg.toUpperCase() );
                }
            }
        );
    }

}
