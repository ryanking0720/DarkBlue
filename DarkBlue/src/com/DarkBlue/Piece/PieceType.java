package com.DarkBlue.Piece;

import com.DarkBlue.Utilities.Utilities;

/**
 * This represents the type of a piece.
 * 
 * Names are self-explanatory and correspond
 * directly to the pieces one would find in chess.
 * 
 * Partially inspired by Black Widow Chess:
 * https://github.com/amir650/BlackWidow-Chess
 */
public enum PieceType{
    PAWN, 
    ROOK, 
    KNIGHT, 
    BISHOP, 
    QUEEN, 
    KING;
    
    public static final String PAWN_STRING = "PAWN";
    public static final String ROOK_STRING = "ROOK";
    public static final String KNIGHT_STRING = "KNIGHT";
    public static final String BISHOP_STRING = "BISHOP";
    public static final String QUEEN_STRING = "QUEEN";
    public static final String KING_STRING = "KING";
    
    /**/
    /*
    NAME
        public final String toString();
    
    SYNOPSIS
        public final String toString();
    
        No parameters.
    
    DESCRIPTION
        This method returns the piece's type as a human-readable string.
        Pieces with an invalid type return null.
    
    RETURNS
        String: A string description of the piece, or null otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final String toString(){
        switch(this){
            case PAWN:   return PAWN_STRING;
            case ROOK:   return ROOK_STRING;
            case KNIGHT: return KNIGHT_STRING;
            case BISHOP: return BISHOP_STRING;
            case QUEEN:  return QUEEN_STRING;
            case KING:   return KING_STRING;
            default:     return null;
        }
    }
    
    /**/
    /*
    NAME
        public final char ToCharacter();
    
    SYNOPSIS
        public final char ToCharacter();
    
        No parameters.
    
    DESCRIPTION
        This method returns the piece's type as a capital letter.
    
    RETURNS
        char: Either the second character of KNIGHT or the first character
        of any other type.
    
    AUTHOR
        Ryan King
    */
    public final char ToCharacter(){
        if(this == KNIGHT){
            return this.toString().charAt(Utilities.ONE);
        }else{
            return this.toString().charAt(Utilities.ZERO);
        }
    }
    
    /**/
    /*
    NAME
        public final int GetLimit();
    
    SYNOPSIS
        public final int GetLimit();
    
        No parameters.
    
    DESCRIPTION
        This method returns the number of times this type of piece should occur on a FEN string.
        Returns 1 for kings, 8 for pawns, 9 for queens, or 10 for rooks, knights, and bishops.
        This is only done from the perspective of one player, 
        e.g. there should be two kings on the board,
        but each player can have one and only one.
        This also takes the possibility of promotions into account.
    
    RETURNS
        int: the number of times this type of piece
        should occur on a FEN string.
    
    AUTHOR
        Ryan King
    */
    public final int GetLimit(){
        switch(this){            
            case ROOK:   
            case KNIGHT: 
            case BISHOP: return Utilities.TEN;
            case QUEEN:  return Utilities.NINE;
            case PAWN:   return Utilities.EIGHT;
            case KING:   return Utilities.ONE;
            default:     return Utilities.ZERO;
        }
    }
}