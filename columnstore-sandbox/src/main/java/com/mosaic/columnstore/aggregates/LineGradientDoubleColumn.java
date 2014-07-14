package com.mosaic.columnstore.aggregates;

import com.mosaic.columnstore.DoubleColumn;
import com.mosaic.columnstore.columns.DoubleColumnFormula1;
import com.mosaic.lang.QA;


/**
 * The direction of the line is the gradient of the line of best fit for the data points
 * provided.
 *
 *
 * From https://onlinecourses.science.psu.edu/stat501/node/12; formula for calculating the
 * line of best fit for two datasets (x and y) that are linearly correlated.
 *
 * endmemo.com/statistics/lr.php -- includes a php calculator that implements the algorithm
 *
 * Given the formula for of a line as:
 *
 * y = a + b*x
 *
 * The line of best fit can be calculated as:
 *
 * b = covariance/variance
 *
 *  = sum(x*y) - n*meanX*meanY
 *    ------------------------
 *    sum(x^2) - n * meanX^2
 *
 * a = mean(y) - b*mean(x)
 */
public class LineGradientDoubleColumn extends DoubleColumnFormula1 {

    private final long numSamples;

    public LineGradientDoubleColumn( DoubleColumn y, long numSamples ) {
        super( y.getColumnName()+" Gradient", "Gradient of "+y.getColumnName(), "GRADIENT", y, numSamples );

        QA.argIsGT( numSamples, 1, "numSamples" );

        this.numSamples = numSamples;
    }

    public boolean isSet( long row ) {
        return true;
    }

    protected double get( long row, DoubleColumn col ) {
        double meanY = calcMean( row, col );

        double sumCovar = 0;
        double sumVar   = 0;
        double x        = 0;

        long minRow = Math.max( 0, row - numSamples );

        int n = 0;
        for ( long i=minRow; i<row; i++ ) {
            if ( col.isSet(i) ) {
                n++;
            }
        }

        double meanX = sumRange(0,n)/n;
        double mx2   = meanX*meanX;
        double nxy   = n * meanX * meanY;

        for ( long i=minRow; i<row; i++ ) {
            if ( col.isSet(i) ) {
                double y = col.get(i);

                sumCovar += x*y;
                sumVar   += x*x;
            }

            x++;
        }

        sumCovar -= nxy;
        sumVar   -= n * mx2;

        return sumVar == 0 ? 0 : sumCovar / sumVar;
    }


    private double sumRange( long from, long toExc ) {
        long sum = 0;

        for ( long i=from; i<toExc; i++ ) {
            sum += i;
        }

        return sum;
    }

    private double calcMean( long row, DoubleColumn col ) {
        int    n   = 0;
        double sum = 0;

        while ( row >= 0 && n < numSamples ) {
            if ( col.isSet(row) ) {
                n   += 1;
                sum += col.get( row );
            }

            row--;
        }

        return n == 0 ? 0 : sum/n;
    }

}
