package com.mosaic.io.cli;

import com.mosaic.collections.ConsList;
import com.mosaic.io.streams.EnglishPrettyPrintUtils;
import com.mosaic.lang.functional.Function1;


/**
 *
 */
@SuppressWarnings("unchecked")
public class CLArgument<T> implements CLParameter<T> {

    public static final Function1<String,String> NO_OP_PARSER = new Function1<String, String>() {
        public String invoke( String arg ) {
            return arg;
        }
    };


    public static CLArgument<String> stringArgument( String argumentName, String argumentDescription ) {
        return new CLArgument( argumentName, argumentDescription, NO_OP_PARSER );
    }


    private final String               argumentName;
    private final String               argumentDescription;
    private final Function1<String, T> valueParser;

    private T       value;
    private boolean isOptional;
    private T       defaultValue;


    public CLArgument( String argumentName, String argumentDescription, Function1<String,T> valueParser ) {
        this.argumentName        = argumentName;
        this.argumentDescription = EnglishPrettyPrintUtils.cleanEnglishSentence( argumentDescription );
        this.valueParser         = valueParser;
    }

    public String getLongName() {
        return argumentName;
    }

    public String getArgumentDescription() {
        String desc = argumentDescription;

        if ( defaultValue != null ) {
            desc = desc + " Defaults to '"+defaultValue+"'.";
        }

        return desc;
    }

    public void setValue( String value ) {
        try {
            this.value = valueParser.invoke(value);
        } catch ( CLException ex ) {
            throw ex;
        } catch ( Exception ex ) {
            String msg = "Invalid value for '" + getLongName() + "', for more information invoke with --help.";

            throw new CLException( msg, ex );
        }
    }

    public T getValue() {
        return value == null ? defaultValue : value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public boolean hasValidValue() {
        return value != null || isOptional;
    }

    public boolean isMandatory() {
        return !isOptional;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public boolean isEmpty() {
        return value == null;
    }

    void setOptional( boolean isOptional ) {
        this.isOptional = isOptional;
    }

    /**
     * Specify a default value for this optional argument.  Will error if the argument has not
     * been marked as optional.
     */
    public CLArgument<T> withDefaultValue( T defaultValue ) {
        if ( !isOptional ) {
            throw new IllegalArgumentException( "Only optional arguments may have default values" );
        }

        this.defaultValue = defaultValue;

        return this;
    }

    public ConsList<String> tryToConsumeInput( ConsList<String> unprocessedInput ) {
        if ( value == null ) {
            if ( unprocessedInput.head().startsWith("-") ) {
                return unprocessedInput;
            } else {
                this.setValue( unprocessedInput.head() );

                return unprocessedInput.tail();
            }
        } else {
            return unprocessedInput;
        }
    }

    public String toString() {
        return getLongName();
    }

}
