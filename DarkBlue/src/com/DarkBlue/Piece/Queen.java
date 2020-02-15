package com.DarkBlue.Piece;

import com.DarkBlue.Move.*;
import com.DarkBlue.Utilities.*;
import com.DarkBlue.Board.*;

import java.util.ArrayList;

public final class Queen extends Piece{
    
    // The down moves usable on this turn only
    private final ArrayList<Move> m_currentDownMoves;
        
    // The up moves usable on this turn only
    private final ArrayList<Move> m_currentUpMoves;
        
    // The right moves usable on this turn only
    private final ArrayList<Move> m_currentRightMoves;
        
    // The left moves usable on this turn only
    private final ArrayList<Move> m_currentLeftMoves;
    
    
    
    // The down and right moves usable on this turn only
    private final ArrayList<Move> m_currentDownAndRightMoves;
        
    // The up and right moves usable on this turn only
    private final ArrayList<Move> m_currentUpAndRightMoves;
        
    // The up and left moves usable on this turn only
    private final ArrayList<Move> m_currentUpAndLeftMoves;
        
    // The down and left moves usable on this turn only
    private final ArrayList<Move> m_currentDownAndLeftMoves;
    
    /**/
    /*
    NAME
        public Queen(final ChessColor a_color, final int a_currentRow, final int a_currentColumn);
    
    SYNOPSIS
        public Queen(final ChessColor a_color, final int a_currentRow, final int a_currentColumn);
        
        ChessColor a_color --------> The color of the piece, used primarily by the GUI.

        int a_currentRow ----------> The piece's current row.
        
        int a_currentColumn -------> The piece's current column.
    
    DESCRIPTION
        This constructor constructs a new Queen object by calling the Piece 
        superclass constructor and filling in the specific fields.
        
        ArrayLists that contain all and current legal moves are also instantiated, to be
        populated later.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Queen(final ChessColor a_color, final int a_currentRow, final int a_currentColumn){
        super(a_color, PieceType.QUEEN, Utilities.WHITE_QUEEN_ICON, AssignPieceBoardIcon(PieceType.QUEEN, a_color), a_currentRow, a_currentColumn, AssignPieceValue(PieceType.QUEEN, a_color));
        
        this.m_currentDownMoves = new ArrayList<>();
        this.m_currentUpMoves = new ArrayList<>();
        this.m_currentRightMoves = new ArrayList<>();
        this.m_currentLeftMoves = new ArrayList<>();
        
        this.m_currentDownAndRightMoves = new ArrayList<>();
        this.m_currentUpAndRightMoves = new ArrayList<>();
        this.m_currentUpAndLeftMoves = new ArrayList<>();
        this.m_currentDownAndLeftMoves = new ArrayList<>();
    }
    
    /**/
    /*
    NAME
        public Queen(final Piece a_piece);
    
    SYNOPSIS
        public Queen(final Piece a_piece);
        
        Piece a_piece --------> The Piece to be copied.
    
    DESCRIPTION
        This copy constructor constructs a new Queen object by passing in
        a Piece object and cloning its fields.
        
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Queen(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves){
        super(a_piece, a_newRow, a_newColumn, a_moves);
        Queen candidate = (Queen) a_piece;
        
        this.m_currentDownMoves = new ArrayList<>();
        this.m_currentUpMoves = new ArrayList<>();
        this.m_currentRightMoves = new ArrayList<>();
        this.m_currentLeftMoves = new ArrayList<>();
        
        this.m_currentDownAndRightMoves = new ArrayList<>();
        this.m_currentUpAndRightMoves = new ArrayList<>();
        this.m_currentUpAndLeftMoves = new ArrayList<>();
        this.m_currentDownAndLeftMoves = new ArrayList<>();
        
        this.m_currentDownMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentDownMoves()));
        this.m_currentUpMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentUpMoves()));
        this.m_currentRightMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentRightMoves()));
        this.m_currentLeftMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentLeftMoves()));
        
        this.m_currentDownAndRightMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentDownAndRightMoves()));
        this.m_currentUpAndRightMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentUpAndRightMoves()));
        this.m_currentUpAndLeftMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentUpAndLeftMoves()));
        this.m_currentDownAndLeftMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentDownAndLeftMoves()));
    }
    
    /**/
    /*
    NAME
        public final void AddCurrentLegalMoves(final Board a_board);
    
    SYNOPSIS
        public final void AddCurrentLegalMoves(final Board a_board);
    
        Board a_board ---> The chessboard which contains the current game.
    
    DESCRIPTION
        This method populates the current legal move array, taking into account which
        tiles the piece can actually visit on this turn. For example, no tile occurring after an
        opposing piece or on and after a friendly piece can be visited. Also, this piece
        may not have any legal moves if the king is in check and the piece can't help him.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    @Override
    public final void AddCurrentLegalMoves(final Board a_board){
        // Clear out all the legal moves from the previous turn
        m_currentLegalMoves.clear();
        
        m_currentDownMoves.clear();
        m_currentUpMoves.clear();
        m_currentRightMoves.clear();
        m_currentLeftMoves.clear();
        
        m_currentDownAndRightMoves.clear();
        m_currentUpAndRightMoves.clear();
        m_currentUpAndLeftMoves.clear();
        m_currentDownAndLeftMoves.clear();
        
        this.m_attackedTiles.clear();
        
        // Add the new legal moves for this turn
        this.m_currentDownMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allDownMoves));
        this.m_currentUpMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allUpMoves));
        this.m_currentRightMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allRightMoves));
        this.m_currentLeftMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allLeftMoves));
        
        this.m_currentDownAndRightMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allDownAndRightMoves));
        this.m_currentUpAndRightMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allUpAndRightMoves));
        this.m_currentUpAndLeftMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allUpAndLeftMoves));
        this.m_currentDownAndLeftMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allDownAndLeftMoves));
        
        // Place the new moves into the generic current legal moves ArrayList
        this.m_currentLegalMoves.addAll(m_currentDownMoves);
        this.m_currentLegalMoves.addAll(m_currentUpMoves);
        this.m_currentLegalMoves.addAll(m_currentRightMoves);
        this.m_currentLegalMoves.addAll(m_currentLeftMoves);
        
        this.m_currentLegalMoves.addAll(m_currentDownAndRightMoves);
        this.m_currentLegalMoves.addAll(m_currentUpAndRightMoves);
        this.m_currentLegalMoves.addAll(m_currentUpAndLeftMoves);
        this.m_currentLegalMoves.addAll(m_currentDownAndLeftMoves);
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
        boolean: Always returns true.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsQueen(){
    	return true;
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
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsKnight(){
    	return false;
    }
    
    /**/
    /*
    NAME
    
    
    SYNOPSIS
    
    
    
    
    DESCRIPTION
    
    
    
    RETURNS
    
    
    AUTHOR
        Ryan King
    */

    /**/
    /*
    NAME
        public final ArrayList<Move> GetCurrentDownMoves();
    
    SYNOPSIS
        public final ArrayList<Move> GetCurrentDownMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns the current down moves ArrayList.    
    
    RETURNS
        ArrayList<Move> this.m_currentDownMoves: The current down moves.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> GetCurrentDownMoves(){
        return this.m_currentDownMoves;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> GetCurrentUpMoves();
    
    SYNOPSIS
        public final ArrayList<Move> GetCurrentUpMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns the current up moves ArrayList.    
    
    RETURNS
        ArrayList<Move> this.m_currentUpMoves: The current up moves.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> GetCurrentUpMoves(){
        return this.m_currentUpMoves;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> GetCurrentRightMoves();
    
    SYNOPSIS
        public final ArrayList<Move> GetCurrentRightMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns the current right moves ArrayList.    
    
    RETURNS
        ArrayList<Move> this.m_currentRightMoves: The current right moves.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> GetCurrentRightMoves(){
        return this.m_currentRightMoves;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> GetCurrentLeftMoves();
    
    SYNOPSIS
        public final ArrayList<Move> GetCurrentLeftMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns the current left moves ArrayList.    
    
    RETURNS
        ArrayList<Move> this.m_currentLeftMoves: The current left moves.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> GetCurrentLeftMoves(){
        return this.m_currentLeftMoves;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> GetCurrentDownAndRightMoves();
    
    SYNOPSIS
        public final ArrayList<Move> GetCurrentDownAndRightMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns the current down and right moves ArrayList.    
    
    RETURNS
        ArrayList<Move> this.m_currentDownAndRightMoves: The current down and right moves.
    
    AUTHOR
        Ryan King
    */    
    public final ArrayList<Move> GetCurrentDownAndRightMoves(){
        return this.m_currentDownAndRightMoves;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> GetCurrentUpAndRightMoves();
    
    SYNOPSIS
        public final ArrayList<Move> GetCurrentUpAndRightMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns the current up and right moves ArrayList.    
    
    RETURNS
        ArrayList<Move> this.m_currentUpAndRightMoves: The current up and right moves.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> GetCurrentUpAndRightMoves(){
        return this.m_currentUpAndRightMoves;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> GetCurrentUpAndLeftMoves();
    
    SYNOPSIS
        public final ArrayList<Move> GetCurrentUpAndLeftMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns the current up and left moves ArrayList.    
    
    RETURNS
        ArrayList<Move> this.m_currentUpAndLeftMoves: The current up and left moves.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> GetCurrentUpAndLeftMoves(){
        return this.m_currentUpAndLeftMoves;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> GetCurrentDownAndLeftMoves();
    
    SYNOPSIS
        public final ArrayList<Move> GetCurrentDownAndLeftMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns the current down and left moves ArrayList.    
    
    RETURNS
        ArrayList<Move> this.m_currentDownAndLeftMoves: The current down and left moves.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> GetCurrentDownAndLeftMoves(){
        return this.m_currentDownAndLeftMoves;
    }
}