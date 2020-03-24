package com.DarkBlue.Utilities;

import com.DarkBlue.Piece.Bishop;
import com.DarkBlue.Piece.King;
import com.DarkBlue.Piece.Knight;
import com.DarkBlue.Piece.Pawn;
import com.DarkBlue.Piece.Piece;
import com.DarkBlue.Piece.PieceType;
import com.DarkBlue.Piece.Queen;
import com.DarkBlue.Piece.Rook;
import com.DarkBlue.Board.Tile;
import com.DarkBlue.Board.Board;
import com.DarkBlue.Move.AttackingMove;
import com.DarkBlue.Move.CastlingMove;
import com.DarkBlue.Move.EnPassantMove;
import com.DarkBlue.Move.Move;
import com.DarkBlue.Move.RegularMove;

/*
 * This interface contains factory methods that are used in multiple classes
 * to instantiate objects in a controlled way. This is done in order to deny
 * the user control on instantiating an overpowered or otherwise invalid piece
 * or tile. 
 */
public interface Factory{
	
	/**/
    /*
    NAME
        public static Tile TileFactory(final Tile a_tile);
    
    SYNOPSIS
        public static Tile TileFactory(final Tile a_tile);
        
        Tile a_tile ---------------> The tile to be duplicated.
    
    DESCRIPTION
        This method duplicates the given tile and the piece occupying it, if any.
        Returns null if the argument given is null.
    
    RETURNS
        Tile: A deep copy of a_tile complete with another deep copy of its occupant, or null on error.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
	public static Tile TileFactory(final Tile a_tile){
		if(a_tile == null){
			return null;
		}else{
			return new Tile(a_tile);
		}
	}

	/**/
    /*
    NAME
        public static Piece PieceFactory(final Piece a_candidate);
    
    SYNOPSIS
        public static Piece PieceFactory(final Piece a_candidate);
        
        Piece a_candidate ---------------> The piece to be duplicated.
    
    DESCRIPTION
        This method duplicates the given piece and does not change its current number of moves.
    
    RETURNS
        Piece: The new pawn, rook, knight, bishop, queen, or king with its move count increased by 1, or null on error.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static Piece PieceFactory(final Piece a_candidate){
        // Idiot proofing for a null argument
    	if(a_candidate == null){
    		return null;
    	}
    	
    	// Make a deep copy of the piece
        switch(a_candidate.GetPieceType()){
            case PAWN: return new Pawn(a_candidate.GetColor(), a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn());
            case ROOK: return new Rook(a_candidate.GetColor(), a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn());
            case KNIGHT: return new Knight(a_candidate.GetColor(), a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn());
            case BISHOP: return new Bishop(a_candidate.GetColor(), a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn());
            case QUEEN: return new Queen(a_candidate.GetColor(), a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn());
            case KING: return new King(a_candidate.GetColor(), a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn(), ((King)a_candidate).CanKingsideCastle(), ((King)a_candidate).CanQueensideCastle());
            default: return null;
        }
    }
    
    /**/
    /*
    NAME
        public static Piece MovedPieceFactory(final Piece a_candidate);
    
    SYNOPSIS
        public static Piece MovedPieceFactory(final Piece a_candidate);
        
        Piece a_candidate ---------------> The piece to be duplicated.
    
    DESCRIPTION
        This method duplicates the given piece and adds 1 to its current number of moves.
    
    RETURNS
        Piece: The new pawn, rook, knight, bishop, queen, or king with its move count increased by 1, or null on error.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static Piece MovedPieceFactory(final Piece a_candidate){
        // Idiot proofing for a null argument
    	if(a_candidate == null){
    		return null;
    	}
    	
    	// Make a deep copy of the piece that just moved
        switch(a_candidate.GetPieceType()){
            case PAWN: return new Pawn(a_candidate, a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn(), a_candidate.HowManyMoves() + Utilities.ONE);
            case ROOK: return new Rook(a_candidate, a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn(), a_candidate.HowManyMoves() + Utilities.ONE);
            case KNIGHT: return new Knight(a_candidate, a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn(), a_candidate.HowManyMoves() + Utilities.ONE);
            case BISHOP: return new Bishop(a_candidate, a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn(), a_candidate.HowManyMoves() + Utilities.ONE);
            case QUEEN: return new Queen(a_candidate, a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn(), a_candidate.HowManyMoves() + Utilities.ONE);
            case KING: return new King(a_candidate, a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn(), a_candidate.HowManyMoves() + Utilities.ONE);
            default: return null;
        }
    }
    
    /**/
    /*
    NAME
        public static Piece MovedPieceFactory(final Piece a_candidate, final int a_newRow, final int a_newColumn);
    
    SYNOPSIS
        public static Piece MovedPieceFactory(final Piece a_candidate, final int a_newRow, final int a_newColumn);
        
        Piece a_candidate ---------------> The piece to be duplicated.
        
        int a_newRow --------------------> The new row this piece will be moving to.
        
        int a_newColumn -----------------> The new column this piece will be moving to.
    
    DESCRIPTION
        This method duplicates the given piece, places it onto a new tile, and adds 1 to its current number of moves.
    
    RETURNS
        Piece: The new pawn, rook, knight, bishop, queen, or king with its move count increased by 1, or null on error.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static Piece MovedPieceFactory(final Piece a_candidate, final int a_newRow, final int a_newColumn){
        // Idiot proofing for a null argument
    	if(a_candidate == null){
    		return null;
    	}
    	
    	// Make a deep copy of the piece that just moved
        switch(a_candidate.GetPieceType()){
            case PAWN: return new Pawn(a_candidate, a_newRow, a_newColumn, a_candidate.HowManyMoves() + Utilities.ONE);
            case ROOK: return new Rook(a_candidate, a_newRow, a_newColumn, a_candidate.HowManyMoves() + Utilities.ONE);
            case KNIGHT: return new Knight(a_candidate, a_newRow, a_newColumn, a_candidate.HowManyMoves() + Utilities.ONE);
            case BISHOP: return new Bishop(a_candidate, a_newRow, a_newColumn, a_candidate.HowManyMoves() + Utilities.ONE);
            case QUEEN: return new Queen(a_candidate, a_newRow, a_newColumn, a_candidate.HowManyMoves() + Utilities.ONE);
            case KING: return new King(a_candidate, a_newRow, a_newColumn, a_candidate.HowManyMoves() + Utilities.ONE);
            default: return null;
        }
    }
    
    /**/
    /*
    NAME
        public static Piece PromotedPieceFactory(final ChessColor a_color, final int a_row, final int a_column, final int a_buttonInt);
    
    SYNOPSIS
        public static Piece PromotedPieceFactory(final ChessColor a_color, final int a_row, final int a_column, final int a_buttonInt);
        
        ChessColor a_color --------------> The color of the piece to be instantiated.
        
        int a_newRow --------------------> The new row this piece will be moving to.
        
        int a_newColumn -----------------> The new column this piece will be moving to.
        
        int a_buttonInt -----------------> The button chosen by the human or the loop index chosen by the minimax algorithm.
    
    DESCRIPTION
        This method instantiates a new queen, rook, bishop, or knight and places it onto the tile with the row and column specified.
        The button int tells which piece will be instantiated:
        0 returns a queen
        1 returns a rook
        2 returns a bishop
        3 returns a knight
        
        Otherwise, it returns null on error.
    
    RETURNS
        Piece: The new rook, knight, bishop, or queen with its move count set to 0, or null on error.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static Piece PromotedPieceFactory(final ChessColor a_color, final int a_row, final int a_column, final int a_buttonInt){
    	final Piece newPiece;
    	// Instantiate the chosen piece
    	switch(a_buttonInt){
        	case Utilities.ZERO: newPiece = new Queen(a_color, a_row, a_column);
        	break;
        	case Utilities.ONE: newPiece = new Rook(a_color, a_row, a_column);
        	break;
        	case Utilities.TWO: newPiece = new Bishop(a_color, a_row, a_column);
        	break;
        	case Utilities.THREE: newPiece = new Knight(a_color, a_row, a_column);
        	break;
        	default: newPiece = null;
        	break;
    	}
    	
    	return newPiece;
    }
    
    /**/
    /*
    NAME
        public static Move MoveFactory(final Piece a_candidate, final int a_destinationRow, final int a_destinationColumn, final Piece a_victim, final Board a_board);
    
    SYNOPSIS
        public static Move MoveFactory(final Piece a_candidate, final int a_destinationRow, final int a_destinationColumn, final Piece a_victim, final Board a_board);
    
        Piece a_candidate ----------------> The pice to be moved.
        
        int a_destinationRow -------------> The row the piece will move to.
        
        int a_destinationColumn ----------> The column the piece will move to.
        
        Piece a_victim -------------------> The piece that will be captured on this move, if any.
        
        Board a_board --------------------> The board this move will be made on.
    
    DESCRIPTION
        This method creates a new Move object.
        Depending on the type of move this is, it could be:
        
        A regular move 
        (Any piece moving to an empty tile),
        
        An attacking move 
        (Any piece moving to an occupied tile and
        capturing the opposing piece on that tile the way it typically captures),
        
        A castling move 
        (Swapping the king and the rook when there are no other pieces between them,
        the king will not move through check and neither the
        king nor the rook has moved yet), or
        
        An en passant move
        (A special attacking move which can only occur if a pawn is at its fifth rank
        and the previous piece to move was an opposing pawn that advanced 2 tiles on
        its first move and could have been taken by the other pawn had it only moved 1 tile.
        This is the only legal move where the destination tile is not the same as the tile
        of the captured piece).
        
        Null is returned if invalid parameters were passed in.
    
    RETURNS
        Move move: The evaluated move, ready to be made.
        Returns null on error.
    
    AUTHOR
        Ryan King
    */
    public static Move MoveFactory(final Piece a_candidate, final int a_destinationRow, final int a_destinationColumn, final Piece a_victim, final Board a_board){
        if(a_candidate == null || !BoardUtilities.HasValidValue(a_destinationRow) || !BoardUtilities.HasValidValue(a_destinationColumn) || a_board == null){
            return null;
        }
        
        // Get the coordinates of the piece
    	final int sourceRow = a_candidate.GetCurrentRow();
    	final int sourceColumn = a_candidate.GetCurrentColumn();
    	
        if(!a_candidate.IsKing() && !a_candidate.IsPawn()){// This is definitely not a castling or en passant move
            if(a_victim != null){
                return new AttackingMove(a_candidate, a_destinationRow, a_destinationColumn, a_victim, a_board);
            }else{
                return new RegularMove(a_candidate, a_destinationRow, a_destinationColumn, a_board);
            }
        }else{
            if(a_candidate.IsKing()){// This could be a castling move
                if(MoveEvaluation.IsCastlingMove(a_candidate, sourceRow, sourceColumn, a_destinationRow, a_destinationColumn)){
                    // This is a castling move
                    return new CastlingMove((King)a_candidate, a_destinationRow, a_destinationColumn, a_board);               
                }else{// This is a regular or attacking move
                    if(a_victim != null){
                        return new AttackingMove(a_candidate, a_destinationRow, a_destinationColumn, a_victim, a_board);
                    }else{
                        return new RegularMove(a_candidate, a_destinationRow, a_destinationColumn, a_board);
                    }
                }
            }else{// This could be a regular move, an attacking move, or an en passant move
                if(MoveEvaluation.IsEnPassantMove(a_candidate, a_destinationRow, a_destinationColumn, a_board)){
                    // This is an en passant move
                    final Pawn victim;
                    
                    if(BoardUtilities.HasValidCoordinates(a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn() + Utilities.ONE)
                            && a_board.GetTile(a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn() + Utilities.ONE).IsOccupied()){
                        victim = (Pawn) a_board.GetTile(sourceRow, sourceColumn + Utilities.ONE).GetPiece();
                    }else{
                        victim = (Pawn) a_board.GetTile(sourceRow, sourceColumn - Utilities.ONE).GetPiece();
                    }
                    
                    return new EnPassantMove((Pawn)a_candidate, a_destinationRow, a_destinationColumn, victim, a_board);
                }else{// This isn't an en passant move
                    // This is a regular or attacking move
                    if(a_victim != null){
                        return new AttackingMove(a_candidate, a_destinationRow, a_destinationColumn, a_victim, a_board);
                    }else{
                        return new RegularMove(a_candidate, a_destinationRow, a_destinationColumn, a_board);
                    }
                }
            }
        }
    }
}