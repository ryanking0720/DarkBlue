package com.DarkBlue.Board;

import com.DarkBlue.Piece.*;
import com.DarkBlue.Utilities.*;

/**
 * This class represents a tile on the chessboard.
 * 
 * A tile has a set row, column, and color.
 * The row and column must be between 0 and 7.
 * It can also contain any type of piece, as indicated by the Piece pointer.
 * Empty tiles have a piece pointer that points to null.
 * All tiles are immutable.
 * 
 * The color is determined through the tile's coordinates.
 * Though the color may look like a waste of space and bookkeeping,
 * it is useful for determining draws when both players have a king and bishop.
 * 
 * Adapted from the Tile class found in an early version of Black Widow Chess by Amir Afghani:
 * https://www.youtube.com/watch?v=h8fSdSUKttk and https://www.youtube.com/watch?v=Ol2pAXgVE7c
 * This class is not used in the current version of Black Widow on his GitHub repository.
 */
public final class Tile{
    // The color of the tile, i.e. black or white
    private final ChessColor m_color;

    //For usage in the Board array (0-7)
    private final int m_row;
    
    //For usage in the Board array (0-7)
    private final int m_column;
    
    // The piece currently on the tile
    private final Piece m_piece;
    
    // Keep in mind that the array spots are reversed from the 
    // traditional notion of the chessboard.
    // For example, tile a1 has row 7, column 0.
    // Tile h8 has row 0, column 7.
    
    // letter(row), |8 - column|
    // letter(0) = a, letter(1) = b, and so on.
    
    /**/
    /*
    NAME
        public Tile(final ChessColor a_color, final int a_row, final int a_column);
    
    SYNOPSIS
        public Tile(final ChessColor a_color, final int a_row, final int a_column);
    
        ChessColor a_color -------> The color of the tile.
        
        int a_row ----------------> The tile's row.
        
        int a_column -------------> The tile's column.
    
    DESCRIPTION
        This constructor initializes a new Tile object
        with its color, row, and column.
        Its piece is set to null and its occupied flag
        is set to false.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Tile(final ChessColor a_color, final int a_row, final int a_column, final Piece a_piece){
        
        // Idiot proofing
        if(a_color == null || !BoardUtilities.HasValidCoordinates(a_row, a_column)){
            System.err.println("Invalid parameter(s) for Tile object. Color: " + a_color + " | Row: " + a_row + " | Column: " + a_column);
            System.exit(Utilities.ONE);
        }
        
        // Assign final fields
        this.m_color = a_color;
        this.m_row = a_row;
        this.m_column = a_column;
        this.m_piece = a_piece;
    }
    
    /**/
    /*
    NAME
        public Tile(final Tile a_tile);
    
    SYNOPSIS
        public Tile(final Tile a_tile);
    
        Tile a_tile -------> The tile to be copied.
        
    DESCRIPTION
        This copy constructor initializes a new Tile object
        based on the fields of the tile being passed in.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Tile(final Tile a_tile){
        
        // Idiot proofing
        if(a_tile == null){
            System.err.println("Null parameter for Tile object.");
            System.exit(Utilities.ONE);
        }
        
        if(a_tile.GetColor() == null || !BoardUtilities.HasValidCoordinates(a_tile.GetRow(), a_tile.GetColumn())){
            System.err.println("Invalid parameter(s) for Tile object. Color: " + a_tile.GetColor() + " | Row: " + a_tile.GetRow() + " | Column: " + a_tile.GetColumn());
            System.exit(Utilities.ONE);
        }
        
        // Assign basic fields
        this.m_color = a_tile.GetColor();
        this.m_row = a_tile.GetRow();
        this.m_column = a_tile.GetColumn();
        
        // Assign the piece
        if(a_tile.IsOccupied()){
            switch(a_tile.GetPiece().GetPieceType()){
                case PAWN: this.m_piece = new Pawn(a_tile.GetPiece(), a_tile.GetPiece().GetCurrentRow(), a_tile.GetPiece().GetCurrentColumn(), a_tile.GetPiece().HowManyMoves());
                break;
                case ROOK: this.m_piece = new Rook(a_tile.GetPiece(), a_tile.GetPiece().GetCurrentRow(), a_tile.GetPiece().GetCurrentColumn(), a_tile.GetPiece().HowManyMoves());
                break;
                case KNIGHT: this.m_piece = new Knight(a_tile.GetPiece(), a_tile.GetPiece().GetCurrentRow(), a_tile.GetPiece().GetCurrentColumn(), a_tile.GetPiece().HowManyMoves());
                break;
                case BISHOP: this.m_piece = new Bishop(a_tile.GetPiece(), a_tile.GetPiece().GetCurrentRow(), a_tile.GetPiece().GetCurrentColumn(), a_tile.GetPiece().HowManyMoves());
                break;
                case QUEEN: this.m_piece = new Queen(a_tile.GetPiece(), a_tile.GetPiece().GetCurrentRow(), a_tile.GetPiece().GetCurrentColumn(), a_tile.GetPiece().HowManyMoves());
                break;
                case KING: this.m_piece = new King(a_tile.GetPiece(), a_tile.GetPiece().GetCurrentRow(), a_tile.GetPiece().GetCurrentColumn(), a_tile.GetPiece().HowManyMoves());
                break;
                default: this.m_piece = null;
                break;
            }
        }else{
            this.m_piece = null;
        }
    }
    
    /**/
    /*
    NAME
        public final Piece GetPiece();
    
    SYNOPSIS
        public final Piece GetPiece();
    
        No parameters.
    
    DESCRIPTION
        This method returns the tile's piece.
        
    RETURNS
        Piece m_piece: The tile's piece.
    
    AUTHOR
        Ryan King
    */
    public final Piece GetPiece(){
        return this.m_piece;
    }
    
    /**/
    /*
    NAME
        public final int GetRow();
    
    SYNOPSIS
        public final int GetRow();
    
        No parameters.
    
    DESCRIPTION
        This method returns the tile's row.
        
    RETURNS
        int m_row: The tile's row.
    
    AUTHOR
        Ryan King
    */
    public final int GetRow(){
        return this.m_row;
    }
    
    /**/
    /*
    NAME
        public final int GetColumn();
    
    SYNOPSIS
        public final int GetColumn();
    
        No parameters.
    
    DESCRIPTION
        This method returns the tile's column.
        
    RETURNS
        int m_column: The tile's column.
    
    AUTHOR
        Ryan King
    */
    public final int GetColumn(){
        return this.m_column;
    }
    
    /**/
    /*
    NAME
        public final ChessColor GetColor();
    
    SYNOPSIS
        public final ChessColor GetColor();
    
        No parameters.
    
    DESCRIPTION
        This method returns the tile's color.
        
    RETURNS
        ChessColor m_color: The tile's color.
    
    AUTHOR
        Ryan King
    */
    public final ChessColor GetColor(){
        return this.m_color;
    }
    
    /**/
    /*
    NAME
        public final boolean IsOccupied();
    
    SYNOPSIS
        public final boolean IsOccupied();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the tile is occupied.
        
    RETURNS
        boolean: true if the tile is occupied, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsOccupied(){
        return this.m_piece != null;
    }
    
    /**/
    /*
    NAME
        public final boolean IsEmpty();
    
    SYNOPSIS
        public final boolean IsEmpty();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the tile is empty.
        
    RETURNS
        boolean: true if the tile is empty, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsEmpty(){
        return this.m_piece == null;
    }
    
    /**/
    /*
    NAME
        public final String toString();
    
    SYNOPSIS
        public final String toString();
    
        No parameters.
    
    DESCRIPTION
        This method returns a String of the tile's letter and number in algebraic notation.
        
    RETURNS
        String: The tile's letter and number in algebraic notation.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final String toString(){
        return BoardUtilities.ToAlgebraic(this.m_row, this.m_column);
    }

    /**/
    /*
    NAME
        public final boolean Equals(final Tile a_tile);
    
    SYNOPSIS
        public final boolean Equals(final Tile a_tile);
    
        Tile a_tile ---------> The tile to compare this to.
    
    DESCRIPTION
        This method returns if this tile is equal to the given tile by checking
        piece equivalence, row, column, and color.
        
    RETURNS
        True if the two tiles are considered equal and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean Equals(final Tile a_tile){
        try{
            return this.GetPiece().Equals(a_tile.GetPiece())
                && this.GetColor() == a_tile.GetColor()
                && this.GetRow() == a_tile.GetRow()
                && this.GetColumn() == a_tile.GetColumn();
        }catch(Exception e){
            return false;
        }
    }
}