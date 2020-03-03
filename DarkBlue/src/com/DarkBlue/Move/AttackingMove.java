package com.DarkBlue.Move;

import com.DarkBlue.Piece.Piece;
import com.DarkBlue.Board.*;
import com.DarkBlue.Utilities.*;

public final class AttackingMove extends Move{

    /*
    NAME
        public AttackingMove(final Piece a_piece, final int a_newRow, final int a_newColumn, final Piece a_victim, final Board a_board);
    
    SYNOPSIS
        public AttackingMove(final Piece a_piece, final int a_newRow, final int a_newColumn, final Piece a_victim, final Board a_board);
    
        Piece a_piece ----------> The piece to move.
        
        int a_newRow -----------> The new row where the piece will move.
        
        int a_newColumn --------> The new column where the piece will move.
        
        Piece a_victim ---------> The piece getting captured.
        
        Board a_board ----------> The board on which this move is made.
    
    DESCRIPTION
        This constructor initializes a new AttackingMove object.
        The old row and column fields are taken directly from a_piece.
        The new row and column fields are passed in here and assigned accordingly.
        The victim is passed in the same manner.
    
    RETURNS
    	Nothing
    
    AUTHOR
        Ryan King
    */
    public AttackingMove(final Piece a_piece, final int a_newRow, final int a_newColumn, final Piece a_victim, final Board a_board){
        super(a_piece, a_newRow, a_newColumn, a_victim, a_board);
    }
    
    /*
    NAME
        public final String toString();
    
    SYNOPSIS
        public final String toString();
    
        No parameters.
    
    DESCRIPTION
        This method is overridden from the Move superclass.
        It returns a string representation of this move in
        algebraic notation using its fields.
        
        Each piece uses its character icon for this except for the pawn.
        The pawn simply uses the letter of the tile it was previously resting on.
        
        Examples:
        
        Nxc6 means that a knight captured the piece on c6.
        
        fxe4 means that a pawn captured the piece on e4.
    
    
    RETURNS
        String moveString: A string representation of the move.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final String toString(){
        String moveString = "";
        
        if(!m_piece.IsPawn()){
            moveString += Character.toString(this.m_piece.GetIcon()).toUpperCase();
        }else{
            moveString += BoardUtilities.ToAlgebraicColumn(this.m_piece.GetCurrentColumn());
        }
        
        moveString += "x" + BoardUtilities.ToAlgebraic(this.m_newRow, this.m_newColumn);
        
        return moveString;
    }
    
    /*
    NAME
        public Piece GetVictim();
    
    SYNOPSIS
        public Piece GetVictim();
    
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
        return this.m_victim;
    }
    
    /*
    NAME
        public final MoveType GetMoveType();
    
    SYNOPSIS
        public final MoveType GetMoveType();
    
        No parameters.
    
    DESCRIPTION
        This method returns the type of this move.
    
    RETURNS
        MoveType.ATTACKING.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final MoveType GetMoveType(){
        return MoveType.ATTACKING;
    }
    
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
        boolean: Always returns true.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean HasVictim(){
        return true;
    }
    
    /*
    NAME
        public final boolean IsRegular();
    
    SYNOPSIS
        public final boolean IsRegular();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move type is Regular.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsRegular(){
        return false;
    }
    
    /*
    NAME
        public final boolean IsAttacking();
    
    SYNOPSIS
        public final boolean IsAttacking();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move type is Attacking.
    
    RETURNS
        boolean: Always returns true.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsAttacking(){
        return true;
    }
    
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