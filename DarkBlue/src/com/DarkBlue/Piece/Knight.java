package com.DarkBlue.Piece;

import com.DarkBlue.Board.*;
import com.DarkBlue.Utilities.*;

public final class Knight extends Piece{

    /*
    NAME
        public Knight(final ChessColor a_color, final int a_currentRow, final int a_currentColumn);
    
    SYNOPSIS
        public Knight(final ChessColor a_color, final int a_currentRow, final int a_currentColumn);
        
        ChessColor a_color --------> The color of the piece, used primarily by the GUI.

        int a_currentRow ----------> The piece's current row.
        
        int a_currentColumn -------> The piece's current column.
    
    DESCRIPTION
        This constructor constructs a new Knight object by calling the Piece 
        superclass constructor and filling in the specific fields.
        
        ArrayLists that contain all and current legal moves are also instantiated, to be
        populated later.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Knight(final ChessColor a_color, final int a_currentRow, final int a_currentColumn){
        super(a_color, PieceType.KNIGHT, Utilities.KNIGHT_ICON, AssignPieceBoardIcon(PieceType.KNIGHT, a_color), a_currentRow, a_currentColumn, AssignPieceValue(PieceType.KNIGHT, a_color));
    }
    
    /*
    NAME
        public Knight(final Piece a_piece);
    
    SYNOPSIS
        public Knight(final Piece a_piece);
        
        Piece a_piece --------> The Piece to be copied.
    
    DESCRIPTION
        This copy constructor constructs a new Knight object by passing in
        a Piece object and cloning its fields.
        
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Knight(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves){
        super(a_piece, a_newRow, a_newColumn, a_moves);
    }
    
    /*
    NAME
        public final void AddCurrentLegalMoves(final Board a_board);
    
    SYNOPSIS
        public final void AddCurrentLegalMoves(final Board a_board);
    
        Board a_board ---> The chessboard which contains the current game state.
    
    DESCRIPTION
        This method populates the current legal move array, taking into account which
        tiles the piece can actually visit on this turn. For example, no tile with a
        friendly piece can be visited. Also, this piece
        may not have any legal moves if the king is in check and the piece can't help him.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    @Override
    public final void AddCurrentLegalMoves(final Board a_board){
        // Clear out the legal moves from the previous turn
        this.m_currentLegalMoves.clear();
        
        // Add the moves that are currently deemed legal
        this.m_currentLegalMoves.addAll(MoveEvaluation.AddCurrentSpectrumMoves(this, a_board, MoveEvaluation.m_allKnightMoves));
    }
}