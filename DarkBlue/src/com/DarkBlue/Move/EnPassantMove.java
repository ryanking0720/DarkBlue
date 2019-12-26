package com.DarkBlue.Move;

import com.DarkBlue.Piece.*;
import com.DarkBlue.Utilities.*;

public final class EnPassantMove extends Move{
    
    private final int m_capturedPawnRow;
    private final int m_capturedPawnColumn;

    /*
    NAME
        public EnPassantMove(final Pawn a_pawn, final int a_kingNewRow, final int a_kingNewColumn, final Pawn a_victim);
    
    SYNOPSIS
        public EnPassantMove(final Pawn a_pawn, final int a_kingNewRow, final int a_kingNewColumn, final Pawn a_victim);
    
        Pawn a_pawn --------------> The pawn to be moved.
        
        int a_kingNewRow ---------> The new row (1 different from the old one).
        
        int a_kingNewColumn ------> The new column (1 different from the old one).
        
        Pawn a_victim ------------> The pawn to be captured.
    
    DESCRIPTION
        This constructor instantiates a new EnPassantMove object.
        It receives the pawn and its desired coordinates.
        The coordinates where the pawn moves and the coordinates of its victim
        are not the same.
        This is the only type of move where the captured piece is not on the
        same tile the moving piece moves to.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public EnPassantMove(final Pawn a_pawn, final int a_newRow, final int a_newColumn, final Pawn a_victim){
        super(a_pawn, a_newRow, a_newColumn, a_victim, MoveType.EN_PASSANT);
        this.m_capturedPawnRow = a_victim.GetCurrentRow();
        this.m_capturedPawnColumn = a_victim.GetCurrentColumn();
    }

    /*
    NAME
        public final String GetStringMove();
    
    SYNOPSIS
        public final String GetStringMove();
    
        No parameters.
    
    DESCRIPTION
        This method is overridden from the Move superclass.
        It returns a string representation of this move in
        algebraic notation using its fields.
        
        The first character is never the letter P because pawns 
        do not use their icon in algebraic notation.
        
        The suffix e.p. is appended to the end to denote an en passant move.
        
        Example: fxe4 e.p. means that the pawn took the enemy pawn on e4 en passant.
    
    
    RETURNS
        String moveString: A string representation of the move.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final String GetStringMove(){    
        String moveString = Utilities.ToAlgebraicColumn(this.m_oldColumn) + "x" + Utilities.ToAlgebraic(m_newRow, m_newColumn) + " e.p.";
        return moveString;
    }
    
    /*
    NAME
        public final Piece GetVictim();
    
    SYNOPSIS
        public final Piece GetVictim();
    
        No parameters.
    
    DESCRIPTION
        This method is overridden from the Move superclass.
        It returns the victim of this move, which will never be null.
    
    RETURNS
        Piece m_victim: The captured piece.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final Piece GetVictim(){
        return m_victim;
    }
    
    /*
    NAME
        public final int GetCapturedPawnRow();
    
    SYNOPSIS
        public final int GetCapturedPawnRow();
    
        No parameters.
    
    DESCRIPTION
        This method returns the row of the captured pawn object.
    
    RETURNS
         int m_capturedPawnRow: The row of the captured pawn object.
    
    AUTHOR
        Ryan King
    */
    public final int GetCapturedPawnRow(){
        return m_capturedPawnRow;
    }
    
    /*
    NAME
        public final int GetCapturedPawnColumn();
    
    SYNOPSIS
        public final int GetCapturedPawnColumn();
    
        No parameters.
    
    DESCRIPTION
        This method returns the column of the captured pawn object.
    
    RETURNS
         int m_capturedPawnColumn: The column of the captured pawn object.
    
    AUTHOR
        Ryan King
    */
    public final int GetCapturedPawnColumn(){
        return m_capturedPawnColumn;
    }
}