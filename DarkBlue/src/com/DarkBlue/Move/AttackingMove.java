package com.DarkBlue.Move;

import com.DarkBlue.Piece.Piece;
import com.DarkBlue.Utilities.*;

public final class AttackingMove extends Move{

	/*
	NAME
		public AttackingMove(final Piece a_piece, final int a_newRow, final int a_newColumn, final Piece a_victim);
	
	SYNOPSIS
		public AttackingMove(final Piece a_piece, final int a_newRow, final int a_newColumn, final Piece a_victim);
	
		Piece a_piece ----------> The piece to move.
		
		int a_newRow -----------> The new row where the piece will move.
		
		int a_newColumn --------> The new column where the piece will move.
		
		Piece a_victim ---------> The piece getting captured.
	
	DESCRIPTION
		This constructor initializes a new AttackingMove object.
		The old row and column fields are taken directly from a_piece.
		The new row and column fields are passed in here and assigned accordingly.
		The victim is passed in the same manner.
		The move type is set to MoveType.ATTACKING.
	
	RETURNS
	
	
	AUTHOR
		Ryan King
	*/
	public AttackingMove(final Piece a_piece, final int a_newRow, final int a_newColumn, final Piece a_victim){
		super(a_piece, a_newRow, a_newColumn, a_victim, MoveType.ATTACKING);
	}
	
	/*
	NAME
		public String GetStringMove();
	
	SYNOPSIS
		public String GetStringMove();
	
		No parameters.
	
	DESCRIPTION
		This method is overridden from the Move superclass.
		It returns a string representation of this move in
		algebraic notation using its fields.
		
		Each piece uses its character icon for this except for the pawn.
		The pawn simply uses the letter of the tile it was previously resting on.
		
		Examples:
		
		Nxc6 means that a knight captured the piece on c6.
		
		fxe4 means that a pawn captured the piece on e4.
	
	
	RETURNS
		String moveString: A string representation of the move.
	
	AUTHOR
		Ryan King
	*/
	@Override
	public final String GetStringMove(){
		String moveString = "";
		
		if(!m_piece.IsPawn()){
			moveString += Character.toString(this.m_piece.GetIcon());
		}else{
			moveString += Utilities.ToAlgebraicColumn(this.m_piece.GetCurrentColumn());
		}
		
		moveString += "x" + Utilities.ToAlgebraic(this.m_newRow, this.m_newColumn);
		return moveString;
	}
	
	/*
	NAME
		public Piece GetVictim();
	
	SYNOPSIS
		public Piece GetVictim();
	
		No parameters.
	
	DESCRIPTION
		This method is overridden from the Move superclass.
		It returns the victim of this move, which will never be null.
	
	RETURNS
		Piece m_victim: The captured piece.
	
	AUTHOR
		Ryan King
	*/
	@Override
	public final Piece GetVictim(){
		return this.m_victim;
	}
}