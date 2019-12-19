package com.DarkBlue.Move;

import com.DarkBlue.Piece.*;
import com.DarkBlue.Utilities.Utilities;
/*
 * This class represents a move, which can take on several different forms.
 * 
 * The fields that all moves have in common are the moving piece,
 * its current row and column, its destination row and column,
 * the victim (if any), and the type of the move.
 * 
 * All of these fields are final for immutability and protected to be passed down to the subclasses.
 * 
 * If no victim is present, the victim is set to null.
 * 
 * There are 4 different types of moves this extends to:
 * 
 * 1. The regular move. This move represents any piece moving to another tile without capturing any other pieces.
 * 
 * 2. The attacking move. This move represents any piece moving to a tile occupied by an enemy piece and capturing it.
 * The captured piece is no longer considered to be in the game and is removed from the board.
 * The special pawn capture of en passant has its own class which is treated differently.
 * 
 * 3. The castling move. This move is a special move performed only by the king as his first move.
 * I will include more details about it in its own source file.
 * 
 * 4. The en passant move. This is a special pawn capture that can only occur after an enemy pawn's first move
 * of two squares. I will include more details on its own source file.
 * 
 * The GetStringMove() method is abstract, since the string representation of each move is slightly different
 * depending on the rules of algebraic notation, the type of move, as well as the moving piece.
 * 
 */
public abstract class Move{

	protected final Piece m_piece;// The piece on the old tile
	protected final int m_oldRow;// The piece's current row
	protected final int m_oldColumn;// The piece's current column
	protected final int m_newRow;// The new row the piece wants to move to
	protected final int m_newColumn;// The new column the piece wants to move to
	protected final Piece m_victim;// The piece on the new tile. Set to null if empty.
	protected final MoveType m_moveType;
	
	/*
	NAME
		public Move(final Piece a_piece, final int a_oldRow, final int a_oldColumn, final int a_newRow, final int a_newColumn, final Piece a_victim, final MoveType a_type);
	
	SYNOPSIS
		public Move(final Piece a_piece, final int a_oldRow, final int a_oldColumn, final int a_newRow, final int a_newColumn, final Piece a_victim, final MoveType a_type);
		
		Piece a_piece --------> The piece to be moved.
		
		int a_oldRow ---------> The piece's current row.
		
		int a_oldColumn ------> The piece's current column.
		
		int a_newRow ---------> The piece's desired row.
		
		int a_newColumn ------> The piece's desired column.
		
		Piece a_victim -------> The piece on the desired tile; set to null if regular or castling.
		
		MoveType a_type ------> The type of move this is.
		
	DESCRIPTION
		This constructor initializes a Move object using the piece,
		its current row and column, the new tile's row and column 
		and the victim on the tile, if any.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	public Move(final Piece a_piece, final int a_newRow, final int a_newColumn, final Piece a_victim, final MoveType a_type){
		this.m_piece = a_piece;
		this.m_oldRow = a_piece.GetCurrentRow();
		this.m_oldColumn = a_piece.GetCurrentColumn();
		this.m_newRow = a_newRow;
		this.m_newColumn = a_newColumn;
		this.m_victim = a_victim;
		this.m_moveType = a_type;
	}
	
	/*
	NAME
		public abstract String GetStringMove();
	
	SYNOPSIS
		public abstract String GetStringMove();
	
		No parameters.
	
	DESCRIPTION
		This method returns a string representation of the move.
		For example, a regular move to board[4][4] returns ?e4.
		A capture move on the same spots returns ?xe4
		(? does not turn into anything if the Piece is a Pawn).
	
	RETURNS
		String moveString: A string representation of the move.
	
	AUTHOR
		Ryan King
	*/
	public abstract String GetStringMove();
	
	/*
	NAME
		public final Piece GetPiece();
	
	SYNOPSIS
		public final Piece GetPiece();
	
		No parameters.
	
	DESCRIPTION
		This method returns the piece to be moved.
		
	RETURNS
		Piece m_piece: The piece to be moved.
	
	AUTHOR
		Ryan King
	*/
	public final Piece GetPiece(){
		return this.m_piece;
	}
	
	/*
	NAME
		public final int GetOldRow();
	
	SYNOPSIS
		public final int GetOldRow();
	
		No parameters.
	
	DESCRIPTION
		This method returns the piece's current row.
		
	RETURNS
		int m_oldRow: The piece's current row.
	
	AUTHOR
		Ryan King
	*/
	public final int GetOldRow(){
		return this.m_oldRow;
	}
	
	/*
	NAME
		public final int GetOldColumn();
	
	SYNOPSIS
		public final int GetOldColumn();
	
		No parameters.
	
	DESCRIPTION
		This method returns the piece's current column.
		
	RETURNS
		int m_oldColumn: The piece's current column.
	
	AUTHOR
		Ryan King
	*/
	public final int GetOldColumn(){
		return this.m_oldColumn;
	}
	
	/*
	NAME
		public final int GetNewRow();
	
	SYNOPSIS
		public final int GetNewRow();
	
		No parameters.
	
	DESCRIPTION
		This method returns the piece's destination row.
		
	RETURNS
		int m_newRow: The piece's destination row.
	
	AUTHOR
		Ryan King
	*/
	public final int GetNewRow(){
		return this.m_newRow;
	}
	
	/*
	NAME
		public final int GetNewColumn();
	
	SYNOPSIS
		public final int GetNewColumn();
	
		No parameters.
	
	DESCRIPTION
		This method returns the piece's destination column.
		
	RETURNS
		int m_newColumn: The piece's destination column.
	
	AUTHOR
		Ryan King
	*/
	public final int GetNewColumn(){
		return this.m_newColumn;
	}
	
	/*
	NAME
		public abstract Piece GetVictim();
	
	SYNOPSIS
		public abstract Piece GetVictim();
	
		No parameters.
	
	DESCRIPTION
		This method returns the victim from the 
		move's destination tile.
		Since not all moves have a victim, this method
		is defined separately for each subclass.
	
	RETURNS
		Piece m_victim: The victim on the destination tile, if any.
	
	AUTHOR
		Ryan King
	*/
	public abstract Piece GetVictim();
		
	/*
	NAME
		public final boolean HasVictim();
	
	SYNOPSIS
		public final boolean HasVictim();
	
		No parameters.
	
	DESCRIPTION
		This method returns if the destination tile has a victim,
		i.e. if the tile's piece is not set equal to null.
	
	RETURNS
		true if the tile's piece is non-null, and false otherwise.
	
	AUTHOR
		Ryan King
	*/
	public final boolean HasVictim(){
		return this.m_victim != null;
	}
	
	/*
	NAME
		public final MoveType GetMoveType();
	
	SYNOPSIS
		public final MoveType GetMoveType();
	
		No parameters.
	
	DESCRIPTION
		This method returns the type of the move.
		This can be Regular, Attacking, Castling, or En Passant.
	
	RETURNS
		m_moveType: The type of this move.
	
	AUTHOR
		Ryan King
	*/
	public final MoveType GetMoveType(){
		return this.m_moveType;
	}
	
	/*
	NAME
		public final boolean IsRegular();
	
	SYNOPSIS
		public final boolean IsRegular();
	
		No parameters.
	
	DESCRIPTION
		This method returns if the move type is Regular.
	
	RETURNS
		True if the type is Regular, and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	public final boolean IsRegular(){
		return this.m_moveType == MoveType.REGULAR;
	}
	
	/*
	NAME
		public final boolean IsAttacking();
	
	SYNOPSIS
		public final boolean IsAttacking();
	
		No parameters.
	
	DESCRIPTION
		This method returns if the move type is Attacking.
	
	RETURNS
		True if the type is Attacking, and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	public final boolean IsAttacking(){
		return this.m_moveType == MoveType.ATTACKING;
	}
	
	/*
	NAME
		public final boolean IsCastling();
	
	SYNOPSIS
		public final boolean IsCastling();
	
		No parameters.
	
	DESCRIPTION
		This method returns if the move type is Castling.
	
	RETURNS
		True if the type is Castling, and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	public final boolean IsCastling(){
		return this.m_moveType == MoveType.CASTLING;
	}
	
	/*
	NAME
		public final boolean IsEnPassant();
	
	SYNOPSIS
		public final boolean IsEnPassant();
	
		No parameters.
	
	DESCRIPTION
		This method returns if the move type is En Passant.
	
	RETURNS
		True if the type is En Passant, and false otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	public final boolean IsEnPassant(){
		return this.m_moveType == MoveType.EN_PASSANT;
	}
	
	/*
	NAME
		public final int GetValue();
	
	SYNOPSIS
		public final int GetValue();
	
		No parameters.
	
	DESCRIPTION
		This method returns the value of this move
		to the AI by returning the value of its victim.
		If this move has no victim, e.g. it is a regular
		or castling move, it returns zero.
	
	RETURNS
		The value of the victim of this move if it has one, or zero otherwise.
		One of these two options will always occur.
	
	AUTHOR
		Ryan King
	*/
	public final int GetValue(){
		try{
			return this.m_victim.GetValue();
		}catch(NullPointerException e){
			return Utilities.ZERO;
		}
	}
}