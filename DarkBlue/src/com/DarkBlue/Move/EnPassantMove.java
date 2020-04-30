package com.DarkBlue.Move;

import com.DarkBlue.Piece.Piece;
import com.DarkBlue.Piece.Pawn;
import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Utilities.BoardUtilities;
import com.DarkBlue.Utilities.MoveEvaluation;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Board.Board;

/**
 * This represents a move performed by a pawn on its fifth rank
 * capturing a pawn that just moved two tiles on its first move
 * the previous turn. The capturing pawn moves diagonally forward
 * as it would with a normal pawn capture, but the victim is not
 * on the same tile as the moving piece moves to.
 * 
 * Attempting this any turn after the victim pawn has initially moved is illegal.
 */
public final class EnPassantMove extends Move{
    
    private final int m_capturedPawnRow;
    private final int m_capturedPawnColumn;

    /**/
    /*
    NAME
        public EnPassantMove(final Pawn a_pawn, final int a_kingNewRow, final int a_kingNewColumn, final Pawn a_victim, final Board a_board);
    
    SYNOPSIS
        public EnPassantMove(final Pawn a_pawn, final int a_kingNewRow, final int a_kingNewColumn, final Pawn a_victim, final Board a_board);
    
        Pawn a_pawn --------------> The pawn to be moved.
        
        int a_newRow -------------> The new row (1 different from the old one).
        
        int a_newColumn ----------> The new column (1 different from the old one).
        
        Pawn a_victim ------------> The pawn to be captured.
        
        Board a_board ------------> The board on which this move is made.
    
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
    public EnPassantMove(final Pawn a_pawn, final int a_newRow, final int a_newColumn, final Pawn a_victim, final Board a_board){
        super(a_pawn, a_newRow, a_newColumn, a_board);
        this.m_capturedPawnRow = a_victim.GetCurrentRow();
        this.m_capturedPawnColumn = a_victim.GetCurrentColumn();
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
    public final String toString(){    
        return BoardUtilities.ToAlgebraicColumn(this.GetOldColumn()) + "x" + BoardUtilities.ToAlgebraic(this.m_newRow, this.m_newColumn) + "e.p.";
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
        It returns the victim of this move, which will never be null.
    
    RETURNS
        Piece m_victim: The captured piece.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final Piece GetVictim(){
        return this.m_initialBoard.GetTile(this.m_capturedPawnRow, this.m_capturedPawnColumn).GetPiece();
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
        MoveType.EN_PASSANT.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final MoveType GetMoveType(){
        return MoveType.EN_PASSANT;
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
        boolean: Always returns true.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean HasVictim(){
        return true;
    }
    
    /**/
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
    
    /**/
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
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsRegular(){
        return false;
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
        boolean: Always returns true.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsEnPassant(){
        return true;
    }
    
    /**/
    /*
    NAME
        public final Board GetTransitionalBoard();
    
    SYNOPSIS
        public final Board GetTransitionalBoard();
    
        No parameters.
    
    DESCRIPTION
        This method returns the resulting board that will
        be built once this move has been made.
    
    RETURNS
        Board: The initial board field with this move made on it.
    
    AUTHOR
        Based off the execute() method by Amir Afghani,
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/board/Move.java
    */
    @Override
    public final Board GetTransitionalBoard(){
        // Make a copy of the initial board
        final Board CLONE = Board.GetDeepCopy(this.m_initialBoard);

        // Make the move on the copied board
        return CLONE.EnPassant(this);
    }
}