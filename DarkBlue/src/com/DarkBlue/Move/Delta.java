package com.DarkBlue.Move;

import com.DarkBlue.Utilities.Utilities;
/*
 * This class represents a vector that can send any piece
 * from anywhere on the board to anywhere on the board.
 * 
 * It is used during move calculation to monitor only the tiles
 * a piece can actually move to instead of checking all sixty-four
 * tiles indiscriminately.
 * 
 * All delta numbers assume black at the top and white at the bottom.
 * 
 * Black initially occupies board rows 0 and 1
 * and white initially occupies rows 6 and 7.
 * 
 * All delta numbers satisfy -7 <= n <= 7, where n is an integer.
 * 
 * The first number represents a change in rows.
 * A positive row moves the piece down in relation to the above description.
 * A negative row moves the piece up.
 * 
 * The second number represents a change in columns.
 * A positive column moves the piece to the right.
 * A negative column moves the piece to the left.
 * 
 * Horizontal deltas have a zero row with a nonzero column.
 * Left deltas have a negative column.
 * Right deltas have a positive column.
 * 
 * Vertical deltas have a nonzero row with a zero column.
 * Up deltas have a negative row.
 * Down deltas have a positive row.
 * 
 * Diagonal and knight's move deltas have a nonzero row with a nonzero column.
 * Upper left deltas have a negative row with a negative column.
 * Upper right deltas have a negative row with a positive column.
 * Lower left deltas have a positive row with a negative column.
 * Lower right deltas have a positive row with a positive column.
 * Knight's move deltas have a row and column whose absolute values have differences of 1 or 2,
 * while diagonal deltas have rows and columns whose absolute values are identical.
 * 
 * An error delta has a default constructor, and it returns (0, 0).
 * It has a zero row and a zero column.
 */
public final class Delta{
    
    // The difference between rows
    private final int m_rowDelta;
    
    // The difference between columns
    private final int m_columnDelta;
    
    /**/
    /*
    NAME
        public Delta(final int a_rowDelta, final int a_columnDelta);
    
    SYNOPSIS
        public Delta(final int a_rowDelta, final int a_columnDelta);
    
        int a_rowDelta -------> The row delta.
        
        int a_columnDelta ----> The column delta.
    
    DESCRIPTION
        This constructor initializes a new Delta object
        with the user-specified row and column arguments.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Delta(final int a_rowDelta, final int a_columnDelta){
        this.m_rowDelta = a_rowDelta;
        this.m_columnDelta = a_columnDelta;
    }
    
    /**/
    /*
    NAME
        public Delta();
    
    SYNOPSIS
        public Delta();
    
        No parameters.
    
    DESCRIPTION
        This no-arg constructor initializes a new Delta object
        with zero as both the row and column arguments.
        This represents an error condition.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Delta(){
        this.m_rowDelta = Utilities.ZERO;
        this.m_columnDelta = Utilities.ZERO;
    }
    
    /**/
    /*
    NAME
        public int GetRowDelta();
    
    SYNOPSIS
        public int GetRowDelta();
    
        No parameters.
    
    DESCRIPTION
        This method returns the row delta.
    
    RETURNS
        int m_rowDelta: The piece's row delta.
    
    AUTHOR
        Ryan King
    */
    public final int GetRowDelta(){
        return m_rowDelta;
    }
    
    /**/
    /*
    NAME
        public int GetColumnDelta();
    
    SYNOPSIS
        public int GetColumnDelta();
    
        No parameters.
    
    DESCRIPTION
        This method returns the column delta.
    
    RETURNS
        int m_columnDelta: The column delta.
    
    AUTHOR
        Ryan King
    */
    public final int GetColumnDelta(){
        return m_columnDelta;
    }
}