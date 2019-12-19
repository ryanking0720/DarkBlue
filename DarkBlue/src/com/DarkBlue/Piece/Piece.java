package com.DarkBlue.Piece;

import com.DarkBlue.Utilities.*;

import java.util.ArrayList;

import com.DarkBlue.Move.*;
import com.DarkBlue.Board.Board;

public abstract class Piece{
	protected final ChessColor m_color;//White or black
	protected final PieceType m_pieceType;//The type of piece, e.g. pawn, rook, bishop, knight, queen, king
	protected final char m_icon;//The algebraic letter of the piece. Set to 'P' but not used for pawns.
	protected final char m_boardIcon;//The actual piece in Unicode. Much like how a piece looks on a newspaper.
	protected final int m_value;//How important the piece is to the computer
	
	protected final int m_currentRow;//The piece's current row
	protected final int m_currentColumn;//The piece's current column
	protected final int m_moves;//How many times has the piece moved?
	//Current legal moves are stored as Moves because they can only occur on the piece's current spot and on the current turn.
	protected final ArrayList<Move> m_currentLegalMoves;//All the legal moves usable for the current turn only.
	
	/* All abstract methods. */
	
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
	
	/* Constructor and all non-abstract methods. */
	
	// The constructor.
	/*
	NAME
		public Piece(final ChessColor a_color, final PieceType a_descriptor, final int a_currentRow, final int a_currentColumn);
	
	SYNOPSIS
		public Piece(final ChessColor a_color, final PieceType a_descriptor, final int a_currentRow, final int a_currentColumn);
	
		ChessColor a_color -----------> The piece's color, e.g. black or white.
		
		int a_currentRow -------------> The current row of the piece.
		
		int a_currentColumn ----------> The current column of the piece.
	
	DESCRIPTION
		This constructor initializes all of the universal fields for a piece of any type.
		It also initializes the ArrayLists that will contain all legal moves and all current legal moves.
		Value, icon, type, and board icon vary between pieces, so they are set in the proper subclass.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	public Piece(final ChessColor a_color, final PieceType a_type, final char a_icon, final char a_boardIcon, final int a_currentRow, final int a_currentColumn, final int a_value){
		this.m_color = a_color;
		this.m_pieceType = a_type;
		this.m_icon = a_icon;
		this.m_boardIcon = a_boardIcon;
		this.m_value = a_value;
		this.m_currentRow = a_currentRow;
		this.m_currentColumn = a_currentColumn;
		this.m_moves = Utilities.ZERO;
		this.m_currentLegalMoves = new ArrayList<>();
		//The icons and value will be properly set later on
	}
	
	// The copy constructor.
	/*
	NAME
		public Piece(final Piece a_piece);
		
	SYNOPSIS
		public Piece(final Piece a_piece);
		
		Piece a_piece -----------> The Piece to be copied.
		
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
		this.m_color = a_piece.GetColor();
		this.m_pieceType = a_piece.GetPieceType();
		this.m_currentRow = a_newRow;
		this.m_currentColumn = a_newColumn;
		this.m_moves = a_moves;
		this.m_icon = a_piece.GetIcon();
		this.m_boardIcon = a_piece.GetBoardIcon();
		this.m_value = a_piece.GetValue();
		this.m_currentLegalMoves = new ArrayList<>();

		this.m_currentLegalMoves.addAll(MoveEvaluation.CopyCurrentMoves(a_piece.GetCurrentLegalMoves()));
	}
	
	/* Protected assignment methods for final fields */
	
	protected static final int AssignMultiplier(final ChessColor a_color){
		if(a_color.IsWhite()){
			return Utilities.ONE;
		}else{
			return Utilities.NEGATIVE_ONE;
		}
	}
	
	protected static final PieceType AssignPieceType(final int a_buttonInt){
		switch(a_buttonInt){
			case Utilities.THREE: return PieceType.QUEEN;
			case Utilities.TWO: return PieceType.ROOK;
			case Utilities.ONE: return PieceType.BISHOP;
			case Utilities.ZERO: return PieceType.KNIGHT;
			default: return null;
		}
	}
	
	private static final char GetQueenBoardIcon(final ChessColor a_color){
		if(a_color.IsWhite()){
			return Utilities.WHITE_QUEEN_BOARD_ICON;
		}else{
			return Utilities.BLACK_QUEEN_BOARD_ICON;
		}
	}
	
	private static final char GetRookBoardIcon(final ChessColor a_color){
		if(a_color.IsWhite()){
			return Utilities.WHITE_ROOK_BOARD_ICON;
		}else{
			return Utilities.BLACK_ROOK_BOARD_ICON;
		}
		
	}

	private static final char GetKnightBoardIcon(final ChessColor a_color){
		if(a_color.IsWhite()){
			return Utilities.WHITE_KNIGHT_BOARD_ICON;
		}else{
			return Utilities.BLACK_KNIGHT_BOARD_ICON;
		}
	}

	private static final char GetBishopBoardIcon(final ChessColor a_color){
		if(a_color.IsWhite()){
			return Utilities.WHITE_BISHOP_BOARD_ICON;
		}else{
			return Utilities.BLACK_BISHOP_BOARD_ICON;
		}		
	}
	
	private static final char GetKingBoardIcon(final ChessColor a_color){
		if(a_color.IsWhite()){
			return Utilities.WHITE_KING_BOARD_ICON;
		}else{
			return Utilities.BLACK_KING_BOARD_ICON;
		}		
	}
	
	private static final char GetPawnBoardIcon(final ChessColor a_color){
		if(a_color.IsWhite()){
			return Utilities.WHITE_PAWN_BOARD_ICON;
		}else{
			return Utilities.BLACK_PAWN_BOARD_ICON;
		}		
	}
	
	protected static final char AssignPieceBoardIcon(final PieceType a_type, final ChessColor a_color){
		switch(a_type){
			case PAWN: return GetPawnBoardIcon(a_color);
			case ROOK: return GetRookBoardIcon(a_color);
			case KNIGHT: return GetKnightBoardIcon(a_color);
			case BISHOP: return GetBishopBoardIcon(a_color);
			case QUEEN: return GetQueenBoardIcon(a_color);
			case KING: return GetKingBoardIcon(a_color);
			default: return Utilities.NULL;
		}
	}
	
	protected static final int AssignPieceValue(final PieceType a_type, final ChessColor a_color){

		switch(a_type){
			case PAWN: return AssignPawnValue(a_color);
			case ROOK: return AssignRookValue(a_color);
			case KNIGHT: 
			case BISHOP: return AssignBishopOrKnightValue(a_color);
			case QUEEN: return AssignQueenValue(a_color);
			case KING: return AssignKingValue(a_color);
			default: return Utilities.ZERO;
		}
	}
	
	protected static final int AssignPawnValue(final ChessColor a_color){
		return Utilities.ONE * AssignMultiplier(a_color);
	}
	
	protected static final int AssignQueenValue(final ChessColor a_color){
		return Utilities.NINE * AssignMultiplier(a_color);
	}
	
	protected static final int AssignRookValue(final ChessColor a_color){
		return Utilities.SEVEN * AssignMultiplier(a_color);
	}
	
	protected static final int AssignBishopOrKnightValue(final ChessColor a_color){
		return Utilities.THREE * AssignMultiplier(a_color);
	}
	
	protected static final int AssignKingValue(final ChessColor a_color){
		return Utilities.NINE_HUNDRED_NINETY_NINE * AssignMultiplier(a_color);
	}

	// Accessors for every field.
	
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
	
	/*
	NAME
		public final PieceType GetPieceType();
	
	SYNOPSIS
		public final PieceType GetPieceType();
	
		No parameters.
	
	DESCRIPTION
		This method returns this piece's type.
	
	RETURNS
		PieceType m_pieceType: This piece's type.
	
	AUTHOR
		Ryan King
	*/
	public final PieceType GetPieceType(){
		return this.m_pieceType;
	}
	
	/*
	NAME
		public final int GetValue();
	
	SYNOPSIS
		public final int GetValue();
	
		No parameters.
	
	DESCRIPTION
		This method returns this piece's value.
	
	RETURNS
		int m_value: This piece's value.
	
	AUTHOR
		Ryan King
	*/
	public final int GetValue(){
		return this.m_value;
	}
	
	/*
	NAME
		public final char GetIcon();
	
	SYNOPSIS
		public final char GetIcon();
	
		No parameters.
	
	DESCRIPTION
		This method returns this piece's algebraic notation icon.
	
	RETURNS
		char m_icon: This piece's algebraic notation icon.
	
	AUTHOR
		Ryan King
	*/
	public final char GetIcon(){
		return this.m_icon;
	}
	
	/*
	NAME
		public final char GetBoardIcon();
	
	SYNOPSIS
		public final char GetIcon();
	
		No parameters.
	
	DESCRIPTION
		This method returns this piece's board icon.
	
	RETURNS
		char m_boardIcon: This piece's board icon.
	
	AUTHOR
		Ryan King
	*/
	public final char GetBoardIcon(){
		return this.m_boardIcon;
	}
	
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
	
	/*
	NAME
		public final boolean IsPawn();
	
	SYNOPSIS
		public final boolean IsPawn();
	
		No parameters.
	
	DESCRIPTION
		This method determines if this piece is a pawn
		by looking at its piece type.
	
	RETURNS
		boolean: True if the piece is a pawn, and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	public final boolean IsPawn(){
		return m_pieceType == PieceType.PAWN;
		
	}
	
	/*
	NAME
		public final boolean IsKing();
	
	SYNOPSIS
		public final boolean IsKing();
	
		No parameters.
	
	DESCRIPTION
		This method determines if this piece is a king
		by looking at its piece type.
	
	RETURNS
		boolean: True if this piece is a king, and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	public final boolean IsKing(){
		return m_pieceType == PieceType.KING;
	}
	
	/*
	NAME
		public final boolean IsRook();
	
	SYNOPSIS
		public final boolean IsRook();
	
		No parameters.
	
	DESCRIPTION
		This method determines if this piece is a rook
		by looking at its piece type.
	
	RETURNS
		boolean: True if this piece is a rook, and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	public final boolean IsRook(){
		return m_pieceType == PieceType.ROOK;
	}
	
	/*
	NAME
		public final boolean IsBishop();
	
	SYNOPSIS
		public final boolean IsBishop();
	
		No parameters.
	
	DESCRIPTION
		This method determines if this piece is a bishop
		by looking at its piece type.
	
	RETURNS
		boolean: True if this piece is a bishop, and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	public final boolean IsBishop(){
		return m_pieceType == PieceType.BISHOP;
	}
	
	/*
	NAME
		public final boolean IsQueen();
	
	SYNOPSIS
		public final boolean IsQueen();
	
		No parameters.
	
	DESCRIPTION
		This method determines if this piece is a queen
		by looking at its piece type.
	
	RETURNS
		boolean: True if this piece is a queen, and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	public final boolean IsQueen(){
		return m_pieceType == PieceType.QUEEN;
	}
	
	/*
	NAME
		public final boolean IsKnight();
	
	SYNOPSIS
		public final boolean IsKnight();
	
		No parameters.
	
	DESCRIPTION
		This method determines if this piece is a knight
		by looking at its piece type.
	
	RETURNS
		boolean: True if this piece is a knight, and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	public final boolean IsKnight(){
		return m_pieceType == PieceType.KNIGHT;
	}
	
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
		return m_color == ChessColor.WHITE;
	}
	
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
		return m_color == ChessColor.BLACK;
	}
	
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
			return this.GetColor() == Utilities.Reverse(a_piece.GetColor());
		}catch(Exception e){
			return false;
		}
	}
	
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
	
	/*
	NAME
		public final boolean IsSameRow(final Piece a_piece);
	
	SYNOPSIS
		public final boolean IsSameRow(final Piece a_piece);
	
		Piece a_piece ------> The piece to check.
	
	DESCRIPTION
		This method returns if the given piece has the same row as this.
		Null arguments always return false.
	
	RETURNS
		boolean: True if the pieces have the same row and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	private final boolean IsSameRow(final Piece a_piece){
		try{
			return this.GetCurrentRow() == a_piece.GetCurrentRow();
		}catch(Exception e){
			return false;
		}
	}
	
	/*
	NAME
		public final boolean IsSameColumn(final Piece a_piece);
	
	SYNOPSIS
		public final boolean IsSameColumn(final Piece a_piece);
	
		Piece a_piece ------> The piece to check.
	
	DESCRIPTION
		This method returns if the given piece has the same column as this.
		Null arguments always return false.
	
	RETURNS
		boolean: True if the pieces have the same column, and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	private final boolean IsSameColumn(final Piece a_piece){
		try{
			return this.GetCurrentColumn() == a_piece.GetCurrentColumn();
		}catch(Exception e){
			return false;
		}
	}
	
	/*
	NAME
		public final boolean IsSameMoves(final Piece a_piece);
	
	SYNOPSIS
		public final boolean IsSameMoves(final Piece a_piece);
	
		Piece a_piece ------> The piece to check.
	
	DESCRIPTION
		This method returns if the given piece has the same number of moves as this.
		Null arguments always return false.
	
	RETURNS
		boolean: True if the pieces have the same number of moves, and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	private final boolean IsSameMoves(final Piece a_piece){
		try{
			return this.HowManyMoves() == a_piece.HowManyMoves();
		}catch(Exception e){
			return false;
		}
	}
	
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
		3. Row
		4. Column
		5. Number of moves
		
		If all five conditions pass, this method returns true.
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
			return this.IsAlly(a_piece)
					&& this.IsSameType(a_piece)
					&& this.IsSameRow(a_piece)
					&& this.IsSameColumn(a_piece)
					&& this.IsSameMoves(a_piece);
		}catch(Exception e){
			return false;
		}
	}
}