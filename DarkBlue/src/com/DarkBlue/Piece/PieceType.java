package com.DarkBlue.Piece;

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