package com.DarkBlue.Board;

import com.DarkBlue.Piece.*;
import com.DarkBlue.Utilities.*;

public final class Tile{
	// The color of the tile, i.e. black or white
	private final ChessColor m_color;

	//For usage in the Board array (0-7)
	private final int m_row;
	
	//For usage in the Board array (0-7)
	private final int m_column;
	
	// The piece currently on the tile
	private final Piece m_piece;
	
	//Keep in mind that the array spots are reversed from the 
	//traditional notion of the chessboard.
	//For example, tile a1 has row 7, column 0.
	//Tile h8 has row 0, column 7.
	
	//letter(row), |8 - column|
	//letter(0) = a, letter(1) = b, and so on.
	
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
		// Assign final fields
		this.m_color = a_color;
		this.m_row = a_row;
		this.m_column = a_column;
		this.m_piece = a_piece;
	}
	
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
	public Tile(final Tile a_tile){
		
		this.m_color = a_tile.GetColor();
		this.m_row = a_tile.GetRow();
		this.m_column = a_tile.GetColumn();
		
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
		return m_piece;
	}
	
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
		return m_row;
	}
	
	/*
	NAME
		public final int GetColumn();
	
	SYNOPSIS
		public final int GetColumn();
	
		No parameters.
	
	DESCRIPTION
		This method returns the tile's column.
		
	RETURNS
		int m_row: The tile's column.
	
	AUTHOR
		Ryan King
	*/
	public final int GetColumn(){
		return m_column;
	}
	
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
		return m_color;
	}
	
	/*
	NAME
		public final boolean IsOccupied();
	
	SYNOPSIS
		public final boolean IsOccupied();
	
		No parameters.
	
	DESCRIPTION
		This method returns if the tile is occupied.
		
	RETURNS
		True if the tile is occupied, and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	public final boolean IsOccupied(){
		return m_piece != null;
	}
	
	/*
	NAME
		public final boolean IsEmpty();
	
	SYNOPSIS
		public final boolean IsEmpty();
	
		No parameters.
	
	DESCRIPTION
		This method returns if the tile is empty.
		
	RETURNS
		True if the tile is empty, and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	public final boolean IsEmpty(){
		return m_piece == null;
	}
	
	/*
	NAME
		public final String ToAlgebraic();
	
	SYNOPSIS
		public final String ToAlgebraic();
	
		No parameters.
	
	DESCRIPTION
		This method returns a String of the tile's letter and number in algebraic notation.
		
	RETURNS
		String algebraic: The tile's letter and number in algebraic notation.
	
	AUTHOR
		Ryan King
	*/
	public final String ToAlgebraic(){
		return Utilities.ToAlgebraic(this.m_row, this.m_column);
	}
}