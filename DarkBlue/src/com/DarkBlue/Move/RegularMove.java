package com.DarkBlue.Move;

import com.DarkBlue.Piece.Piece;
import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Utilities.BoardUtilities;
import com.DarkBlue.Board.Board;

/*
 * This represents any piece moving between two tiles
 * that does not involve any capture of any kind.
 * 
 * Castling moves performed by kings are different,
 * and are handled in a class of their own.
 */
public final class RegularMove extends Move{

    /**/
    /*
    NAME
        public RegularMove(final Piece a_piece, final int a_newRow, final int a_newColumn, final Board a_board);
    
    SYNOPSIS
        public RegularMove(final Piece a_piece, final int a_newRow, final int a_newColumn, final Board a_board);
    
        Piece a_piece --------------> The piece to be moved.
        
        int a_newRow ---------------> The new row.
        
        int a_newColumn ------------> The new column.
        
        Board a_board --------------> The initial board on which this move is made.
    
    DESCRIPTION
        This constructor instantiates a new RegularMove object.
        It receives the piece and its desired coordinates, and the board being played on.
        The victim is automatically set to null in the superclass constructor.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public RegularMove(final Piece a_piece, final int a_newRow, final int a_newColumn, final Board a_board){
        super(a_piece, a_newRow, a_newColumn, null, a_board);
    }

    /**/
    /*
    NAME
        public final String toString();
    
    SYNOPSIS
        public final String toString();
    
        No parameters.
    
    DESCRIPTION
        This method is overridden from the Object superclass.
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
    public final String toString(){
        String moveString = "";
        
        if(!m_piece.IsPawn()){
            moveString += Character.toString(m_piece.GetIcon()).toUpperCase();
        }
        
        moveString += BoardUtilities.ToAlgebraic(m_newRow, m_newColumn);
        
        return moveString;
    }
    
    /**/
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
        Piece m_victim: Always returns null.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final Piece GetVictim(){
        return null;
    }
 
    /**/
    /*
    NAME
        public final MoveType GetMoveType();
    
    SYNOPSIS
        public final MoveType GetMoveType();
    
        No parameters.
    
    DESCRIPTION
        This method returns the type of this move.
    
    RETURNS
        MoveType.REGULAR.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final MoveType GetMoveType(){
        return MoveType.REGULAR;
    }
    
    /**/
    /*
    NAME
        public final boolean HasVictim();
    
    SYNOPSIS
        public final boolean HasVictim();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move has a victim,
        i.e. if the victim field is not set equal to null.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean HasVictim(){
        return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsRegular();
    
    SYNOPSIS
        public final boolean IsRegular();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move type is Regular.
    
    RETURNS
        boolean: Always returns true.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsRegular(){
        return true;
    }
    
    /**/
    /*
    NAME
        public final boolean IsAttacking();
    
    SYNOPSIS
        public final boolean IsAttacking();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move type is Attacking.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsAttacking(){
        return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsCastling();
    
    SYNOPSIS
        public final boolean IsCastling();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move type is Castling.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsCastling(){
        return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsEnPassant();
    
    SYNOPSIS
        public final boolean IsEnPassant();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move type is En Passant.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsEnPassant(){
        return false;
    }
}