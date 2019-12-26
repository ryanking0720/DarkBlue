package com.DarkBlue.Move;

import com.DarkBlue.Piece.Piece;
import com.DarkBlue.Utilities.*;

public final class RegularMove extends Move{

    /*
    NAME
        public RegularMove(final Piece a_piece, final int a_newRow, final int a_newColumn);
    
    SYNOPSIS
        public RegularMove(final Pice a_piece, final int a_newRow, final int a_newColumn);
    
        Piece a_piece --------------> The piece to be moved.
        
        int a_newRow ---------> The new row.
        
        int a_newColumn ------> The new column.

    
    DESCRIPTION
        This constructor instantiates a new RegularMove object.
        It receives the piece and its desired coordinates.
        The victim is automatically set to null in the superclass constructor.
        The type is set to MoveType.REGULAR.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public RegularMove(final Piece a_piece, final int a_newRow, final int a_newColumn){
        super(a_piece, a_newRow, a_newColumn, null, MoveType.REGULAR);
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
        
        The first character will be either the piece's icon letter if it is not a pawn.
        
        The next characters will be the tile to which the piece will move in algebraic notation.        
        
        Example: e4 means that a pawn moved to e4.
        Kc6 means that a king moved to c6.
        Ng7 means that a knight moved to g7.
    
    
    RETURNS
        String moveString: A string representation of the move.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final String GetStringMove(){
        String moveString = "";
        
        if(!m_piece.IsPawn()){
            moveString += Character.toString(m_piece.GetIcon());
        }
        
        moveString += Utilities.ToAlgebraic(m_newRow, m_newColumn);
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
        It returns the victim of this move, which will always be null.
    
    RETURNS
        Piece m_victim: The captured piece (none in this subclass).
    
    AUTHOR
        Ryan King
    */
    @Override
    public final Piece GetVictim(){
        return null;
    }
}