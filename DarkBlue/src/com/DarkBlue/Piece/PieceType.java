package com.DarkBlue.Piece;

/*
 * This represents the type of a piece.
 * 
 * Names are self-explanatory and correspond
 * directly to the pieces one would find in chess.
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
    public String toString(){
        switch(this){
            case PAWN: return PAWN_STRING;
            case ROOK: return ROOK_STRING;
            case KNIGHT: return KNIGHT_STRING;
            case BISHOP: return BISHOP_STRING;
            case QUEEN: return QUEEN_STRING;
            case KING: return KING_STRING;
            default: return null;
        }
    }
}