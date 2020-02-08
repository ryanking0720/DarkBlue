package com.DarkBlue.Move;

import com.DarkBlue.Utilities.Utilities;

public final class Delta{
    
    // The difference between rows
    private final int m_rowDelta;
    
    // The difference between columns
    private final int m_columnDelta;
    
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
    
    /*
    NAME
        public static final boolean IsErrorDelta(final Delta a_delta);
    
    SYNOPSIS
        public final boolean IsErrorDelta();
    
        No parameters.
    
    DESCRIPTION
        This method returns if this delta object is the error delta,
        i.e. if it has zero as both its row and column values.
    
    RETURNS
        True if this is the error delta, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsErrorDelta(){
        return this.GetRowDelta() == Utilities.ZERO && this.GetColumnDelta() == Utilities.ZERO;
    }
    
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