package com.DarkBlue.Utilities;

// Adapted from the "Alliance" enum in JChess by Amir, updated by Garee Blackwood,
// though my color enum is also used for tiles.

public enum ChessColor{
	WHITE, 
	BLACK;
	
	public static final String WHITE_STRING = "WHITE";
	public static final String BLACK_STRING = "BLACK";
	
	public boolean IsAlly(final ChessColor a_color){
		return this == a_color;
	}
	
	public boolean IsEnemy(final ChessColor a_color){
		return this == Utilities.Reverse(a_color);
	}
	
	public boolean IsWhite(){
		return this == ChessColor.WHITE;
	}
	
	public boolean IsBlack(){
		return this == ChessColor.BLACK;
	}
	
	@Override
	public String toString(){
		if(this == ChessColor.WHITE){
			return WHITE_STRING;
		}else{
			return BLACK_STRING;
		}
	}
}