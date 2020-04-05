package com.DarkBlue.Piece;

import com.DarkBlue.Move.Move;
import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Utilities.MoveEvaluation;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Board.Board;

import java.util.ArrayList;

/*
 * This represents a chess piece that can move any number of tiles horizontally or vertically,
 * stopping either on a tile of a non-king enemy piece or the tile
 * before a friendly piece. The rook captures the same way it moves.
 */
public final class Rook extends Piece{
    
    // The down moves usable on this turn only
    private final ArrayList<Move> m_currentDownMoves;
    
    // The up moves usable on this turn only
    private final ArrayList<Move> m_currentUpMoves;
    
    // The right moves usable on this turn only
    private final ArrayList<Move> m_currentRightMoves;
    
    // The left moves usable on this turn only
    private final ArrayList<Move> m_currentLeftMoves;
    
    /**/
    /*
    NAME
        public Rook(final ChessColor a_color, final int a_currentRow, final int a_currentColumn);
    
    SYNOPSIS
        public Rook(final ChessColor a_color, final int a_currentRow, final int a_currentColumn);
        
        ChessColor a_color --------> The color of the piece, used primarily by the GUI.
        
        int a_currentRow ----------> The piece's current row.
        
        int a_currentColumn -------> The piece's current column.
    
    DESCRIPTION
        This constructor constructs a new Rook object by calling the Piece 
        superclass constructor and filling in the specific fields.
        
        ArrayLists that contain all and current legal moves are also instantiated, to be
        populated later.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Rook(final ChessColor a_color, final int a_currentRow, final int a_currentColumn){
        
        super(a_color, a_currentRow, a_currentColumn);
        
        this.m_currentDownMoves = new ArrayList<>();
        this.m_currentUpMoves = new ArrayList<>();
        this.m_currentRightMoves = new ArrayList<>();
        this.m_currentLeftMoves = new ArrayList<>();
    }
    
    /**/
    /*
    NAME
        public Rook(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves);
    
    SYNOPSIS
        public Rook(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves);
        
        Piece a_piece --------> The Piece to be copied.
        
        int a_newRow ---------> The Piece's new row.
        
        int a_newColumn ------> The Piece's new column.
        
        int a_moves ----------> The Piece's new move count.
    
    DESCRIPTION
        This copy constructor constructs a new Rook object by passing in
        a Piece object and cloning its fields.
        Row, column, and move count are passed in separately.
        
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Rook(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves){
        super(a_piece, a_newRow, a_newColumn, a_moves);    
        final Rook CANDIDATE = (Rook) a_piece;
        
        // Initialize ArrayLists
        this.m_currentDownMoves = new ArrayList<>();
        this.m_currentUpMoves = new ArrayList<>();
        this.m_currentRightMoves = new ArrayList<>();
        this.m_currentLeftMoves = new ArrayList<>();
        
        // Add the moves from the original piece
        this.m_currentDownMoves.addAll(CANDIDATE.GetCurrentDownMoves());
        this.m_currentUpMoves.addAll(CANDIDATE.GetCurrentUpMoves());
        this.m_currentRightMoves.addAll(CANDIDATE.GetCurrentRightMoves());
        this.m_currentLeftMoves.addAll(CANDIDATE.GetCurrentLeftMoves());
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
        // Clear out all old moves
        this.m_currentLegalMoves.clear();
        
        this.m_currentDownMoves.clear();
        this.m_currentUpMoves.clear();
        this.m_currentRightMoves.clear();
        this.m_currentLeftMoves.clear();

        // Calculate new moves and add them to their respective directions
        this.m_currentDownMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allDownMoves));
        this.m_currentUpMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allUpMoves));
        this.m_currentRightMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allRightMoves));
        this.m_currentLeftMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allLeftMoves));
        
        // Add the contents of the directional ArrayLists to the main legal move ArrayList
        this.m_currentLegalMoves.addAll(this.m_currentDownMoves);
        this.m_currentLegalMoves.addAll(this.m_currentUpMoves);
        this.m_currentLegalMoves.addAll(this.m_currentRightMoves);
        this.m_currentLegalMoves.addAll(this.m_currentLeftMoves);
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
        PieceType.ROOK.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final PieceType GetPieceType(){
        return PieceType.ROOK;
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
            return Utilities.WHITE_ROOK_ICON;
        }else{
            return Utilities.BLACK_ROOK_ICON;
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
            return Utilities.WHITE_ROOK_BOARD_ICON;
        }else{
            return Utilities.BLACK_ROOK_BOARD_ICON;
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
        boolean: Always returns true.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsRook(){
        return true;
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
}