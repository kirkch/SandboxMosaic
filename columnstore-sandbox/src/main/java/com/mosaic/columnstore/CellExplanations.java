package com.mosaic.columnstore;

import com.mosaic.lang.QA;
import com.mosaic.lang.functional.Function1;
import com.mosaic.utils.StringUtils;

import java.util.Arrays;
import java.util.List;


/**
 *
 */
@SuppressWarnings("unchecked")
public class CellExplanations {

    private static final long[] NO_ROW_IDS = new long[] {};

    public static final Function1<Object, String> DEFAULT_FORMATTER = new Function1<Object, String>() {
        public String invoke( Object arg ) {
            return arg == null ? "null" : arg.toString();
        }
    };

    public static final <T> Function1<T, String> defaultFormatter() {
        return (Function1<T, String>) DEFAULT_FORMATTER;
    }


    /**
     * A cell containing a single value that was keyed in.
     */
    public static <T> CellExplanation<T> cellValue( String columnName, long rowId, T constant ) {
        return new CellExplanationValue( columnName, rowId, constant, DEFAULT_FORMATTER, null );
    }

    /**
     * A cell containing a single value that was keyed in.
     */
    public static <T> CellExplanation<T> cellValue( String columnName, long rowId, T constant, Function1<T, String> formatter ) {
        return new CellExplanationValue( columnName, rowId, constant, formatter, null );
    }

    /**
     * A cell containing a single value that was keyed in.
     */
    public static <T> CellExplanation<T> cellValue( String columnName, long rowId, T constant, String descriptiveName ) {
        return new CellExplanationValue( columnName, rowId, constant, DEFAULT_FORMATTER, descriptiveName );
    }

    /**
     * Constant value used within a formula.  Lacks rowId and column name as the constant does
     * not reference any cells on the spread sheet.
     */
    public static <T> CellExplanation<T> constant( T constant, String descriptiveName ) {
        return new CellExplanationConstant(constant, descriptiveName, DEFAULT_FORMATTER);
    }

    public static <T> CellExplanation<T> formula( T result, String op, CellExplanation...operands ) {
        return new CellExplanationFormula( result, op, Arrays.asList(operands), DEFAULT_FORMATTER );
    }

    public static <T> CellExplanation<T> formula( T result, String op, List<CellExplanation> operands ) {
        return new CellExplanationFormula( result, op, operands, DEFAULT_FORMATTER );
    }

//    public static <T> CellExplanation constantValue( String columnName, int rowId, String shortConstantName, T constant ) {
//        return new CellExplanationConstant( columnName, rowId, shortConstantName, constant, new Function1<T,String>() {
//            public String invoke( T arg ) {
//                return arg == null ? "null" : arg.toString();
//            }
//        });
//    }

//    public static <T> CellExplanation constantValue( String columnName, int rowId, String shortConstantName, T constant, Function1<T,String> formatter ) {
//        return new CellExplanationConstant( columnName, rowId, shortConstantName, constant, formatter );
//    }





    private static class CellExplanationValue<T> implements CellExplanation<T> {

        private final String descriptiveName;

        private final T      value;
        private final String columnName;
        private final long[] rowIds;


        private Function1<T, String> formatter;


        private CellExplanationValue( String columnName, long rowId, T value, Function1<T, String> formatter, String descriptiveName ) {
            QA.argNotNull( columnName, "columnName" );

            this.descriptiveName = descriptiveName;
            this.value           = value;
            this.columnName      = columnName;
            this.rowIds          = new long[] {rowId};
            this.formatter       = formatter;
        }


        public String getDescriptiveNameNbl() {
            return descriptiveName;
        }

        public String getColumnNameNbl() {
            return columnName;
        }

        public String getOpNameNbl() {
            return null;
        }

        public List<CellExplanation> getOperandsNbl() {
            return null;
        }

        public T getValue() {
            return value;
        }

        public long[] getRowIds() {
            return rowIds;
        }

        public String toString() {
            if ( descriptiveName == null ) {
                return formatter.invoke( getValue() );
            } else {
                return formatter.invoke( getValue() ) + ":" + descriptiveName;
            }
        }
    }

    private static class CellExplanationConstant<T> implements CellExplanation<T> {
        private T                    constant;
        private String               descriptiveName;
        private Function1<T, String> formatter;

        public CellExplanationConstant( T constant, String descriptiveName, Function1<T, String> formatter ) {
            this.constant        = constant;
            this.descriptiveName = descriptiveName;
            this.formatter = formatter;
        }

        public String getDescriptiveNameNbl() {
            return descriptiveName;
        }

        public String getColumnNameNbl() {
            return null;
        }

        public String getOpNameNbl() {
            return null;
        }

        public long[] getRowIds() {
            return NO_ROW_IDS;
        }

        public List<CellExplanation> getOperandsNbl() {
            return null;
        }

        public T getValue() {
            return constant;
        }

        public String toString() {
            return formatter.invoke( getValue() ) + ":" + descriptiveName;
        }
    }

    private static class CellExplanationFormula<T> implements CellExplanation<T> {

        private final T                     result;
        private final String                op;
        private final List<CellExplanation> operands;
        private final Function1<T, String>  formatter;
        private final String                descriptiveNameNbl;

        public CellExplanationFormula( T result, String op, List<CellExplanation> operands, Function1<T, String> formatter ) {
            this.result             = result;
            this.op                 = op;
            this.operands           = operands;
            this.formatter          = formatter;
            this.descriptiveNameNbl = null;
        }

        public String getDescriptiveNameNbl() {
            return null;
        }

        public String getOpNameNbl() {
            return op;
        }

        public String getColumnNameNbl() {
            return null;
        }

        public long[] getRowIds() {
            return new long[0];
        }

        public List<CellExplanation> getOperandsNbl() {
            return operands;
        }

        public T getValue() {
            return result;
        }

        public String toString() {
            return op + StringUtils.concat(operands, "(", ",", ")");
        }
    }
}
