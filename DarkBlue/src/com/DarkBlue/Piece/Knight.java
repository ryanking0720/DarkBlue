package com.DarkBlue.Piece;

import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Utilities.MoveEvaluation;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Board.Board;

/*
 * This class represents a knight.
 * This type of piece resembles the head of a horse
 * and can move in an L-shape. This can be two tiles
 * up/down and one tile left/right, or one tile up/down
 * and two tiles left/right. It can jump over any pieces
 * and is completely unimpeded in movement unless a friendly
 * piece is occupying all of its destination tiles or its
 * destination tiles go off the board. 
 */
public final class Knight extends Piece{

    /**/
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
        super(a_color, Utilities.WHITE_KNIGHT_ICON, AssignPieceBoardIcon(PieceType.KNIGHT, a_color), a_currentRow, a_currentColumn);
    }
    
    /**/
    /*
    NAME
        public Knight(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves);
    
    SYNOPSIS
        public Knight(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves);
        
        Piece a_piece --------> The Piece to be copied.
        
        int a_newRow ---------> The Piece's new row.
        
        int a_newColumn ------> The Piece's new column.
        
        int a_moves ----------> The Piece's new move count.
    
    DESCRIPTION
        This copy constructor constructs a new Knight object by passing in
        a Piece object and cloning its fields.
        Row, column, and move count are passed in separately.
        
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Knight(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves){
        super(a_piece, a_newRow, a_newColumn, a_moves);
    }
    
    /**/
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
    
    /**/
    /*
    NAME
        public final PieceType GetPieceType();
    
    SYNOPSIS
        public final PieceType GetPieceType();
    
        No parameters.
    
    DESCRIPTION
        This method returns this piece's type.
    
    RETURNS
        PieceType.KNIGHT.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final PieceType GetPieceType(){
        return PieceType.KNIGHT;
    }
    
    /**/
    /*
    NAME
        public final boolean IsPawn();
    
    SYNOPSIS
        public final boolean IsPawn();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a pawn.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsPawn(){
    	return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsKing();
    
    SYNOPSIS
        public final boolean IsKing();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a king.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsKing(){
    	return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsRook();
    
    SYNOPSIS
        public final boolean IsRook();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a rook.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsRook(){
    	return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsBishop();
    
    SYNOPSIS
        public final boolean IsBishop();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a bishop.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsBishop(){
    	return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsQueen();
    
    SYNOPSIS
        public final boolean IsQueen();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a queen.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsQueen(){
    	return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsKnight();
    
    SYNOPSIS
        public final boolean IsKnight();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a knight.
    
    RETURNS
        boolean: Always returns true.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsKnight(){
    	return true;
    }
}