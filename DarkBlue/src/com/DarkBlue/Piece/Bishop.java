package com.DarkBlue.Piece;

import java.util.ArrayList;

import com.DarkBlue.Move.*;
import com.DarkBlue.Board.*;
import com.DarkBlue.Utilities.*;

public class Bishop extends Piece{
    
    // The down and right moves usable on this turn only
    private final ArrayList<Move> m_currentDownAndRightMoves;
    
    // The up and right moves usable on this turn only
    private final ArrayList<Move> m_currentUpAndRightMoves;
    
    // The up and left moves usable on this turn only
    private final ArrayList<Move> m_currentUpAndLeftMoves;
    
    // The down and left moves usable on this turn only
    private final ArrayList<Move> m_currentDownAndLeftMoves;
    
    /*
    NAME
        public Bishop(final ChessColor a_color, final char a_descriptor, final int a_currentRow, final int a_currentColumn);
    
    SYNOPSIS
        public Bishop(final ChessColor a_color, final char a_descriptor, final int a_currentRow, final int a_currentColumn);
        
        ChessColor a_color --------> The color of the piece, used primarily by the GUI.

        int a_currentRow ----------> The piece's current row.
        
        int a_currentColumn -------> The piece's current column.
    
    DESCRIPTION
        This constructor constructs a new Bishop object by calling the Piece 
        superclass constructor and filling in the specific fields.
        
        ArrayLists that contain all and current legal moves are also instantiated, to be
        populated later.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Bishop(final ChessColor a_color, final int a_currentRow, final int a_currentColumn){

        super(a_color, PieceType.BISHOP, Utilities.BISHOP_ICON, AssignPieceBoardIcon(PieceType.BISHOP, a_color), a_currentRow, a_currentColumn, AssignPieceValue(PieceType.BISHOP, a_color));
        
        m_currentDownAndRightMoves = new ArrayList<>();
        m_currentUpAndRightMoves = new ArrayList<>();
        m_currentUpAndLeftMoves = new ArrayList<>();
        m_currentDownAndLeftMoves = new ArrayList<>();
    }
    
    /*
    NAME
        public Bishop(final Piece a_piece);
    
    SYNOPSIS
        public Bishop(final Piece a_piece);
        
        Piece a_piece --------> The Piece to be copied.
    
    DESCRIPTION
        This copy constructor constructs a new Bishop object by passing in
        a Piece object and cloning its fields.
        
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */    
    public Bishop(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves){
        super(a_piece, a_newRow, a_newColumn, a_moves);
        Bishop candidate = (Bishop) a_piece;
        
        this.m_currentDownAndRightMoves = new ArrayList<>();        
        this.m_currentUpAndRightMoves = new ArrayList<>();
        this.m_currentUpAndLeftMoves = new ArrayList<>();
        this.m_currentDownAndLeftMoves = new ArrayList<>();
        
        this.m_currentDownAndRightMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentDownAndRightMoves()));
        this.m_currentUpAndRightMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentUpAndRightMoves()));
        this.m_currentUpAndLeftMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentUpAndLeftMoves()));
        this.m_currentDownAndLeftMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentDownAndLeftMoves()));
    }
    
    /*
    NAME
        public final void AddCurrentLegalMoves(final Board a_board);
    
    SYNOPSIS
        public final void AddCurrentLegalMoves(final Board a_board);
    
        Board a_board: The chessboard which contains the current game.
    
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
        this.m_currentLegalMoves.clear();
        
        this.m_currentDownAndRightMoves.clear();
        this.m_currentUpAndRightMoves.clear();
        this.m_currentUpAndLeftMoves.clear();
        this.m_currentDownAndLeftMoves.clear();
        
        this.m_currentDownAndRightMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allDownAndRightMoves));
        this.m_currentUpAndRightMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allUpAndRightMoves));
        this.m_currentUpAndLeftMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allUpAndLeftMoves));
        this.m_currentDownAndLeftMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allDownAndLeftMoves));
        
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
        boolean: Always return false.
    
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
        boolean: Always return false.
    
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
        boolean: Always returns true.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsBishop(){
    	return true;
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
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsKnight(){
    	return false;
    }
    
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
        return m_currentUpAndLeftMoves;
    }
    
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
        return m_currentDownAndLeftMoves;
    }
}