package com.DarkBlue.Piece;

import java.util.ArrayList;
import java.util.HashSet;

import com.DarkBlue.Move.Move;
import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Utilities.BoardUtilities;
import com.DarkBlue.Utilities.MoveEvaluation;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Board.Board;
import com.DarkBlue.Board.Tile;

/**
 * This class represents a generic chess piece.
 * 
 * No concrete assumptions are made about this piece.
 * The only fields that every piece needs are a color,
 * a row, a column, the number of moves it has made, and an
 * ArrayList of legal moves for the current turn.
 * 
 * Identity as a certain type of piece, special
 * types of moves, etc. are all handled in each subclass.
 */
public abstract class Piece{
    // White or black
    protected final ChessColor m_color;
    
    // The piece's current row
    protected final int m_currentRow;
    
    // The piece's current column
    protected final int m_currentColumn;
    
    // How many times has the piece moved?
    protected final int m_moves;
    
    // All the legal moves usable for the current turn only
    protected final ArrayList<Move> m_currentLegalMoves;
    
    /* Constructors and all concrete methods. */
    
    // The constructor.
    
    /**/
    /*
    NAME
        public Piece(final ChessColor a_color, final int a_currentRow, final int a_currentColumn);
    
    SYNOPSIS
        public Piece(final ChessColor a_color, final int a_currentRow, final int a_currentColumn);
    
        ChessColor a_color -----------> The piece's color, e.g. black or white.

        int a_currentRow -------------> The current row of the piece.
        
        int a_currentColumn ----------> The current column of the piece.
    
    DESCRIPTION
        This constructor initializes all of the universal fields for a piece of any type.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Piece(final ChessColor a_color, final int a_currentRow, final int a_currentColumn){
        // Validate the color
        if(a_color != null){
            this.m_color = a_color;
        }else{
            this.m_color = null;
            System.err.println("Invalid color parameter: " + a_color);
            System.exit(Utilities.ONE);
        }
        
        // Validate the row and column
        if(BoardUtilities.HasValidCoordinates(a_currentRow, a_currentColumn)){
            this.m_currentRow = a_currentRow;
            this.m_currentColumn = a_currentColumn;
        }else{
            this.m_currentRow = Utilities.NEGATIVE_ONE;
            this.m_currentColumn = Utilities.NEGATIVE_ONE;
            System.err.println("Invalid row and/or column coordinates: Row is " + a_currentRow + " and Column is " + a_currentColumn);
            System.exit(Utilities.ONE);
        }
        
        // Set other final fields
        this.m_moves = Utilities.ZERO;
        this.m_currentLegalMoves = new ArrayList<>();
    }
    
    // The copy constructor.
    /**/
    /*
    NAME
        public Piece(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves);
        
    SYNOPSIS
        public Piece(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves);
        
        Piece a_piece -----------> The Piece to be copied.
        
        int a_newRow ------------> The Piece's new row.
        
        int a_newColumn ---------> The Piece's new column.
        
        int a_moves -------------> The number of times this piece has moved.
        
    DESCRIPTION
        This copy constructor initializes most of the universal fields for a Piece of any type.
        It sets all of the specific fields not set in the regular constructor, as this assumes
        the caller is passing in a fully fleshed-out Piece object.
            
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Piece(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves){
        // Validate the argument piece
        if(a_piece == null || !BoardUtilities.HasValidCoordinates(a_piece.GetCurrentRow(), a_piece.GetCurrentColumn())){
            System.err.println("Invalid piece parameter");
            System.exit(Utilities.ONE);
        }
        
        // Validate the color
        if(a_piece.GetColor() != null){
            this.m_color = a_piece.GetColor();
        }else{
            this.m_color = null;
            System.err.println("Invalid color parameter: " + a_piece.GetColor());
            System.exit(Utilities.ONE);
        }
        
        // Validate the coordinates
        if(BoardUtilities.HasValidCoordinates(a_newRow, a_newColumn)){
            this.m_currentRow = a_newRow;
            this.m_currentColumn = a_newColumn;
        }else{
            this.m_currentRow = Utilities.NEGATIVE_ONE;
            this.m_currentColumn = Utilities.NEGATIVE_ONE;
            System.err.println("Invalid user-defined row and/or column coordinates: Row is " + a_newRow + " and Column is " + a_newColumn);
            System.exit(Utilities.ONE);
        }
        
        this.m_moves = a_moves;
        this.m_currentLegalMoves = new ArrayList<>();
        this.m_currentLegalMoves.addAll(a_piece.GetCurrentLegalMoves());
    }
    
    // Accessors for every field.
    
    /**/
    /*
    NAME
        public final ChessColor GetColor();
    
    SYNOPSIS
        public final ChessColor GetColor();
    
        No parameters.
    
    DESCRIPTION
        This method returns this piece's color.
    
    RETURNS
        ChessColor m_color: This piece's color.
    
    AUTHOR
        Ryan King
    */
    public final ChessColor GetColor(){
        return this.m_color;
    }
    
    /**/
    /*
    NAME
        public final int GetCurrentRow();
    
    SYNOPSIS
        public final int GetCurrentRow();
    
        No parameters.
    
    DESCRIPTION
        This method returns this piece's current row.
    
    RETURNS
        int m_currentRow: This piece's current row.
    
    AUTHOR
        Ryan King
    */
    public final int GetCurrentRow(){
        return this.m_currentRow;
    }
    
    /**/
    /*
    NAME
        public final int GetCurrentColumn();
    
    SYNOPSIS
        public final int GetCurrentColumn();
    
        No parameters.
    
    DESCRIPTION
        This method returns this piece's current column.
    
    RETURNS
        int m_currentColumn: This piece's current column.
    
    AUTHOR
        Ryan King
    */
    public final int GetCurrentColumn(){
        return this.m_currentColumn;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> GetCurrentLegalMoves();
    
    SYNOPSIS
        public final ArrayList<Move> GetCurrentLegalMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns the ArrayList of all this piece's
        legal moves usable on the current turn.
    
    RETURNS
        ArrayList<Move> m_currentLegalMoves: All of the piece's legal moves for the current turn.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> GetCurrentLegalMoves(){
        return this.m_currentLegalMoves;
    }
    
    /**/
    /*
    NAME
        public final int HowManyMoves();
    
    SYNOPSIS
        public final int HowManyMoves();
        
        No parameters.
    
    DESCRIPTION
        This method returns how many moves this piece has made.
    
    RETURNS
        int m_moves: The number of times this piece has moved.
    
    AUTHOR
        Ryan King
    */
    public final int HowManyMoves(){
        return this.m_moves;
    }
    
    /**/
    /*
    NAME
        public final boolean CanMove();
    
    SYNOPSIS
        public final boolean CanMove();
        
        No parameters.
    
    DESCRIPTION
        This method returns if this piece is able to move
        by checking if its current legal moves ArrayList
        has a size larger than zero.
    
    RETURNS
        boolean: True if the size of the current legal moves ArrayList is nonzero, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean CanMove(){
        return this.m_currentLegalMoves.size() > Utilities.ZERO;    
    }
    
    /**/
    /*
    NAME
        public final boolean HasMoved();
    
    SYNOPSIS
        public final boolean HasMoved();
        
        No parameters.
    
    DESCRIPTION
        This method returns if this piece has moved in the current game.
    
    RETURNS
        boolean: True if this piece has moved at least once this game, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean HasMoved(){
        return this.m_moves > Utilities.ZERO;
    }
    
    /**/
    /*
    NAME
        public final boolean IsWhite();
    
    SYNOPSIS
        public final boolean IsWhite();
    
        No parameters.
    
    DESCRIPTION
        This method returns if this piece is white.   
    
    RETURNS
        boolean: True if the piece is white and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsWhite(){
        return this.m_color == ChessColor.WHITE;
    }
    
    /**/
    /*
    NAME
        public final boolean IsBlack();
    
    SYNOPSIS
        public final boolean IsBlack();
    
        No parameters.
    
    DESCRIPTION
        This method returns if this piece is black.
    
    RETURNS
        boolean: True if this piece is black, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsBlack(){
        return this.m_color == ChessColor.BLACK;
    }
    
    /**/
    /*
    NAME
        public final boolean IsAlly(final Piece a_piece);
    
    SYNOPSIS
        public final boolean IsAlly(final Piece a_piece);
    
        Piece a_piece ------> The piece to check.
    
    DESCRIPTION
        This method returns if the given piece is an ally
        of the argument piece, meaning it has the same color as this piece.
        Null arguments always return false.
    
    RETURNS
        boolean: True if the pieces are identical colors, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsAlly(Piece a_piece){
        try{
            return a_piece.GetColor() == this.GetColor();
        }catch(Exception e){
            return false;
        }
    }
    
    /**/
    /*
    NAME
        public final boolean IsEnemy(final Piece a_piece);
    
    SYNOPSIS
        public final boolean IsEnemy(final Piece a_piece);
    
        Piece a_piece ------> The piece to check.
    
    DESCRIPTION
        This method returns if the given piece is an enemy
        of the argument piece.
        Null arguments always return false.
    
    RETURNS
        boolean: True if the pieces are opposite colors, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsEnemy(final Piece a_piece){
        try{
            return this.GetColor() == BoardUtilities.Reverse(a_piece.GetColor());
        }catch(Exception e){
            return false;
        }
    }
    
    /**/
    /*
    NAME
        public final boolean IsSameType(final Piece a_piece);
    
    SYNOPSIS
        public final boolean IsSameType(final Piece a_piece);
    
        Piece a_piece ------> The piece to check.
    
    DESCRIPTION
        This method returns if the given piece is the same type as this.
        Null arguments always return false.
    
    RETURNS
        boolean: True if the pieces are identical types, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final boolean IsSameType(final Piece a_piece){
        try{
            return this.GetPieceType() == a_piece.GetPieceType();
        }catch(Exception e){
            return false;
        }
    }
    
    /**/
    /*
    NAME
        public final boolean Equals(final Piece a_piece);
    
    SYNOPSIS
        public final boolean Equals(final Piece a_piece);
    
        Piece a_piece ------> The piece to check.
    
    DESCRIPTION
        This method returns if the argued piece is identical to this piece
        in the following ways:
        
        1. Color
        2. Type
        
        If both conditions pass, this method returns true.
        If one of them fails, the method short-circuits and returns false.
        Null arguments always return false.
    
    RETURNS
        boolean: True if the pieces are identical and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean Equals(final Piece a_piece){ 
        try{        
            return this.IsAlly(a_piece) && this.IsSameType(a_piece);
        }catch(Exception e){
            return false;
        }
    }
    
    /**/
    /*
    NAME
        public final String toString();
    
    SYNOPSIS
        public final String toString();
    
        No parameters.
    
    DESCRIPTION
        This method returns the piece's current tile in algebraic notation
        as well as its color and type.
        For example, a white pawn on e2 returns "e2 white pawn".
    
    RETURNS
        String: A string description of the piece.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final String toString(){
        return BoardUtilities.ToAlgebraic(this.m_currentRow, this.m_currentColumn) + Utilities.SPACE + this.m_color.toString().toLowerCase() + Utilities.SPACE + this.GetPieceType().toString().toLowerCase();
    }
    
/* All abstract methods. */
    
    /**/
    /*
    NAME
        public abstract void AddCurrentLegalMoves(final Board a_board);
    
    SYNOPSIS
        public abstract void AddCurrentLegalMoves(final Board a_board);
    
        Board a_board --------> The current state of the game.
    
    DESCRIPTION
        This method adds all the moves that are legal
        on the current turn only. Since every piece, even of the same type and color,
        are in different spots and have different conditions (i.e., blocking a
        king that's in check) as well as methods of moving
        (e.g. a bishop moves diagonally whereas a rook moves horizontally), 
        this method must be declared abstract.
            
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public abstract void AddCurrentLegalMoves(final Board a_board);
    
    /**/
    /*
    NAME
        public abstract PieceType GetPieceType();
    
    SYNOPSIS
        public abstract PieceType GetPieceType();
    
        No parameters.
    
    DESCRIPTION
        This method returns this piece's type.
    
    RETURNS
        PieceType m_pieceType: This piece's type.
    
    AUTHOR
        Ryan King
    */
    public abstract PieceType GetPieceType();
    
    /**/
    /*
    NAME
        public abstract char GetIcon();
    
    SYNOPSIS
        public abstract char GetIcon();
    
        No parameters.
    
    DESCRIPTION
        This method returns this piece's algebraic notation icon.
    
    RETURNS
        char: This piece's algebraic notation icon.
    
    AUTHOR
        Ryan King
    */
    public abstract char GetIcon();
    
    /**/
    /*
    NAME
        public abstract char GetBoardIcon();
    
    SYNOPSIS
        public abstract char GetBoardIcon();
    
        No parameters.
    
    DESCRIPTION
        This method returns this piece's board icon.
    
    RETURNS
        char m_boardIcon: This piece's board icon.
    
    AUTHOR
        Ryan King
    */
    public abstract char GetBoardIcon();
    
    /**/
    /*
    NAME
        public abstract boolean IsPawn();
    
    SYNOPSIS
        public abstract boolean IsPawn();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a pawn.
    
    RETURNS
        boolean: True if the piece is a pawn, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public abstract boolean IsPawn();
    
    /**/
    /*
    NAME
        public abstract boolean IsKing();
    
    SYNOPSIS
        public abstract boolean IsKing();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a king.
    
    RETURNS
        boolean: True if this piece is a king, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public abstract boolean IsKing();
    
    /**/
    /*
    NAME
        public abstract boolean IsRook();
    
    SYNOPSIS
        public abstract boolean IsRook();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a rook.
    
    RETURNS
        boolean: True if this piece is a rook, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public abstract boolean IsRook();
    
    /**/
    /*
    NAME
        public abstract boolean IsBishop();
    
    SYNOPSIS
        public abstract boolean IsBishop();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a bishop.
    
    RETURNS
        boolean: True if this piece is a bishop, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public abstract boolean IsBishop();
    
    /**/
    /*
    NAME
        public abstract boolean IsQueen();
    
    SYNOPSIS
        public abstract boolean IsQueen();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a queen.
    
    RETURNS
        boolean: True if this piece is a queen, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public abstract boolean IsQueen();
    
    /**/
    /*
    NAME
        public abstract boolean IsKnight();
    
    SYNOPSIS
        public abstract boolean IsKnight();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a knight.
    
    RETURNS
        boolean: True if this piece is a knight, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public abstract boolean IsKnight();
}