package com.DarkBlue.Piece;

import com.DarkBlue.Move.*;
import com.DarkBlue.Utilities.*;
import com.DarkBlue.Board.*;

import java.util.ArrayList;

public final class Rook extends Piece{
	
	// The down moves usable on this turn only
	private final ArrayList<Move> m_currentDownMoves;
	
	// The up moves usable on this turn only
	private final ArrayList<Move> m_currentUpMoves;
	
	// The right moves usable on this turn only
	private final ArrayList<Move> m_currentRightMoves;
	
	// The left moves usable on this turn only
	private final ArrayList<Move> m_currentLeftMoves;
	
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
		
		super(a_color, PieceType.ROOK, Utilities.ROOK_ICON, AssignPieceBoardIcon(PieceType.ROOK, a_color), a_currentRow, a_currentColumn, AssignPieceValue(PieceType.ROOK, a_color));
		
		this.m_currentDownMoves = new ArrayList<>();
		this.m_currentUpMoves = new ArrayList<>();
		this.m_currentRightMoves = new ArrayList<>();
		this.m_currentLeftMoves = new ArrayList<>();
	}
	
	/*
	NAME
		public Rook(final Piece a_piece);
	
	SYNOPSIS
		public Rook(final Piece a_piece);
		
		Piece a_piece --------> The Piece to be copied.
	
	DESCRIPTION
		This copy constructor constructs a new Rook object by passing in
		a Piece object and cloning its fields.
		
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	public Rook(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves){
		super(a_piece, a_newRow, a_newColumn, a_moves);	
		Rook candidate = (Rook) a_piece;
		
		this.m_currentDownMoves = new ArrayList<>();
		this.m_currentUpMoves = new ArrayList<>();
		this.m_currentRightMoves = new ArrayList<>();
		this.m_currentLeftMoves = new ArrayList<>();
		
		this.m_currentDownMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentDownMoves()));
		this.m_currentUpMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentUpMoves()));
		this.m_currentRightMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentRightMoves()));
		this.m_currentLeftMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentLeftMoves()));
	}
	
	/*
	NAME
		public void AddCurrentLegalMoves(final Board a_board);
	
	SYNOPSIS
		public void AddCurrentLegalMoves(final Board a_board);
	
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
	public void AddCurrentLegalMoves(final Board a_board){
		this.m_currentLegalMoves.clear();
		
		this.m_currentDownMoves.clear();
		this.m_currentUpMoves.clear();
		this.m_currentRightMoves.clear();
		this.m_currentLeftMoves.clear();

		this.m_currentDownMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allDownMoves));
		this.m_currentUpMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allUpMoves));
		this.m_currentRightMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allRightMoves));
		this.m_currentLeftMoves.addAll(MoveEvaluation.AddCurrentDirectionalMoves(this, a_board, MoveEvaluation.m_allLeftMoves));
		
		this.m_currentLegalMoves.addAll(this.m_currentDownMoves);
		this.m_currentLegalMoves.addAll(this.m_currentUpMoves);
		this.m_currentLegalMoves.addAll(this.m_currentRightMoves);
		this.m_currentLegalMoves.addAll(this.m_currentLeftMoves);
	}
	
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