package com.DarkBlue.Piece;

import java.util.ArrayList;

import com.DarkBlue.Move.Move;
import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Utilities.MoveEvaluation;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Board.Board;

/**
 * This represents a chess piece that can move any number of tiles diagonally,
 * stopping either on a tile of a non-king enemy piece or the tile
 * before a friendly piece. The bishop captures the same way it moves.
 * 
 * Inspired by the class of the same name in Black Widow Chess by Amir Afghani,
 * but this code was entirely written by Ryan King unless indicated otherwise.
 */
public class Bishop extends Piece{
    
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
        super(a_color, a_currentRow, a_currentColumn);
        
        this.m_currentDownAndRightMoves = new ArrayList<>();
        this.m_currentUpAndRightMoves = new ArrayList<>();
        this.m_currentUpAndLeftMoves = new ArrayList<>();
        this.m_currentDownAndLeftMoves = new ArrayList<>();
    }
    
    /**/
    /*
    NAME
        public Bishop(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves);
    
    SYNOPSIS
        public Bishop(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves);
        
        Piece a_piece --------> The Piece to be copied.
        
        int a_newRow ---------> The Piece's new row.
        
        int a_newColumn ------> The Piece's new column.
        
        int a_moves ----------> The Piece's new move count.
    
    DESCRIPTION
        This copy constructor constructs a new Bishop object by passing in
        a Piece object, row, column, and move count and cloning all other fields.
        
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Bishop(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves){
        super(a_piece, a_newRow, a_newColumn, a_moves);
        final Bishop CANDIDATE = (Bishop) a_piece;
        
        this.m_currentDownAndRightMoves = new ArrayList<>();        
        this.m_currentUpAndRightMoves = new ArrayList<>();
        this.m_currentUpAndLeftMoves = new ArrayList<>();
        this.m_currentDownAndLeftMoves = new ArrayList<>();
        
        this.m_currentDownAndRightMoves.addAll(CANDIDATE.GetCurrentDownAndRightMoves());
        this.m_currentUpAndRightMoves.addAll(CANDIDATE.GetCurrentUpAndRightMoves());
        this.m_currentUpAndLeftMoves.addAll(CANDIDATE.GetCurrentUpAndLeftMoves());
        this.m_currentDownAndLeftMoves.addAll(CANDIDATE.GetCurrentDownAndLeftMoves());
    }
    
    /**/
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
        // Clear out all legal moves
        this.m_currentLegalMoves.clear();
        
        this.m_currentDownAndRightMoves.clear();
        this.m_currentUpAndRightMoves.clear();
        this.m_currentUpAndLeftMoves.clear();
        this.m_currentDownAndLeftMoves.clear();
        
        // Add all new legal moves in each direction
        this.m_currentDownAndRightMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.DOWN_AND_RIGHT_MOVES));
        this.m_currentUpAndRightMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.UP_AND_RIGHT_MOVES));
        this.m_currentUpAndLeftMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.UP_AND_LEFT_MOVES));
        this.m_currentDownAndLeftMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.DOWN_AND_LEFT_MOVES));
        
        // Compile these moves into the main ArrayList
        this.m_currentLegalMoves.addAll(m_currentDownAndRightMoves);
        this.m_currentLegalMoves.addAll(m_currentUpAndRightMoves);
        this.m_currentLegalMoves.addAll(m_currentUpAndLeftMoves);
        this.m_currentLegalMoves.addAll(m_currentDownAndLeftMoves);
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
        PieceType.BISHOP.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final PieceType GetPieceType(){
        return PieceType.BISHOP;
    }
    
    /**/
    /*
    NAME
        public final char GetIcon();
    
    SYNOPSIS
        public final char GetIcon();
    
        No parameters.
    
    DESCRIPTION
        This method returns this piece's algebraic notation icon.
    
    RETURNS
        char: This piece's algebraic notation icon.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final char GetIcon(){
        if(this.IsWhite()){
            return Utilities.WHITE_BISHOP_ICON;
        }else{
            return Utilities.BLACK_BISHOP_ICON;
        }
    }
    
    /**/
    /*
    NAME
        public final char GetBoardIcon();
    
    SYNOPSIS
        public final char GetBoardIcon();
    
        No parameters.
    
    DESCRIPTION
        This method returns this piece's board icon.
    
    RETURNS
        char: This piece's board icon.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final char GetBoardIcon(){
        if(this.IsWhite()){
            return Utilities.WHITE_BISHOP_BOARD_ICON;
        }else{
            return Utilities.BLACK_BISHOP_BOARD_ICON;
        }
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