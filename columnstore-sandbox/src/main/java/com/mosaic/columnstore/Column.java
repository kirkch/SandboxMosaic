package com.mosaic.columnstore;

import com.mosaic.io.streams.CharacterStream;


/**
 *
 */
public interface Column<S extends Column> { // S -> self type;  eg IntColumn

    public String getColumnName();
    public String getDescription();

    public boolean isSet( long row );

    public void unset( long row );

    /**
     * How many rows are in this column.  Starts from row zero and goes through to rowCount-1.
     */
    public long size();

    public void resizeIfNecessary( long newSize );

    public CellExplanation explain( long row );

    public void writeValueTo( CharacterStream out, long row );

    /**
     * Reserve this number of characters for displaying a single formatted value of this column.
     */
    public int reserveWidth();

    /**
     * Pre-calculate the value for every cell in this column and store it in the specified
     * destination cell.<p/>
     *
     * By default this is done in parallel, spreading the cells out amongst
     * multiple threads.  Such a parallel approach is extremely effective when there is little
     * to no dependencies between cells.  If however there is a very high level of dependency
     * between the cells, for example if cell N depends on every previous cell before it then
     * calculating each cell separately will be very very inefficient.  For example, consider
     * a column that is the sum of every previous cell.  In such a case override this method
     * and provide a more efficient implementation that knows the specifics of the algorithm at hand.
     */
    public void prePopulateColumn( S destinationColumn );

    public S createAuditor( int expectedCellCount );

}