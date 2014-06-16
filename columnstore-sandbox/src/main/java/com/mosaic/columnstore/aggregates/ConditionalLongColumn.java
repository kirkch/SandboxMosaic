package com.mosaic.columnstore.aggregates;

import com.mosaic.columnstore.BooleanColumn;
import com.mosaic.columnstore.LongColumn;
import com.mosaic.columnstore.columns.BooleanLong2LongFormula;


/**
 * Returns the value of column X iff column Y is true.
 */
public class ConditionalLongColumn extends BooleanLong2LongFormula {

    private BooleanColumn conditionColumn;
    private LongColumn    sourceColumn;

    public ConditionalLongColumn( String columnName, String description, String opLabel, BooleanColumn condition, LongColumn source ) {
        super( columnName, description, opLabel, condition, source );

        this.conditionColumn = condition;
        this.sourceColumn = source;
    }

    public ConditionalLongColumn( BooleanColumn condition, LongColumn source ) {
        this(
            source.getColumnName() + " iff " + condition.getColumnName(),                           // column name
            "Uses the value of " + source.getColumnName() + " if and only if the value of " + condition.getColumnName() + " is true", // description
            "IF",                                                                                   // op label
            condition,
            source
        );
    }

    public boolean isSet( long row ) {
        return conditionColumn.isSet(row) && sourceColumn.isSet(row);
    }

    protected long get( long row, BooleanColumn condition, LongColumn col ) {
        if ( condition.get(row) ) {
            return col.get(row);
        } else {
            return 0L;
        }
    }

}
