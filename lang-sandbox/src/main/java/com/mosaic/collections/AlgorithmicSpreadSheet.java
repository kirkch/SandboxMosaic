package com.mosaic.collections;


/**
 * Similar in principle to SimpleSpreadSheet but supports on demand calculation of cells.
 */
@SuppressWarnings("unchecked")
public class AlgorithmicSpreadSheet {

    private DynamicList<CellCalculation> columnCalculations = new DynamicList<>();
    private SimpleSpreadSheet<Cell>      table              = new SimpleSpreadSheet<>();


    public <T> void setCell( int row, int col, T value ) {
        Cell cell = new ConstantCell(value);

        table.set( row, col, cell );
    }

    public <T> void setColumnCalculation( int col, CellCalculation<T> calculation ) {
        columnCalculations.set( col, calculation );
    }

    public <T> T get( int row, int col ) {
        Cell cell = getCell( row, col );

        return cell == null ? null : (T) cell.value( this );
    }

    private <T> Cell<T> getCell( int row, int col ) {
        Cell cell = table.get( row, col );

        if ( cell == null ) {
            CellCalculation<T> calc = columnCalculations.get( col );

            if ( calc != null ) {
                cell = new CalculatedCell(calc, row, col);

                table.set( row, col, cell );
            }
        }

        return cell;
    }


    public static interface CellCalculation<T> {
        public T calculateValue( AlgorithmicSpreadSheet spreadSheet, int row, int col );
    }

    private static interface Cell<T> {
        public T value( AlgorithmicSpreadSheet spreadSheet );
    }

    private static class ConstantCell<T> implements Cell<T> {
        private final T value;

        private ConstantCell( T value ) {
            this.value = value;
        }

        public T value( AlgorithmicSpreadSheet spreadSheet ) {
            return value;
        }
    }

    private static class CalculatedCell<T> implements Cell<T> {
        private final CellCalculation<T> func;
        private final int                row;
        private final int                col;

        private CalculatedCell( CellCalculation<T> func, int row, int col ) {
            this.func = func;
            this.row = row;
            this.col = col;
        }

        public T value( AlgorithmicSpreadSheet spreadSheet ) {
            return func.calculateValue(spreadSheet, row,col);
        }
    }
}
