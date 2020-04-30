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

/**
 * This interface contains constructor-like methods that are used in multiple classes
 * to instantiate objects in a controlled way. This is done in order to deny
 * the user control of instantiating an overpowered or otherwise invalid piece or move. 
 */
public interface Factory{
	
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
    	if(a_candidate == null || !BoardUtilities.HasValidCoordinates(a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn())){
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
        if(a_candidate == null || !BoardUtilities.HasValidCoordinates(a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn())){
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
        // Idiot proofing for null or invalid arguments
    	if(a_candidate == null || !BoardUtilities.HasValidCoordinates(a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn()) || !BoardUtilities.HasValidCoordinates(a_newRow, a_newColumn)){
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
        // Idiot proofing for null or invalid arguments
        if(a_color == null || !(BoardUtilities.HasValidValue(a_column) && (a_color.IsWhite() && a_row == Utilities.ZERO) || (a_color.IsBlack() && a_row == Utilities.SEVEN)) || !BoardUtilities.HasValidCoordinates(a_row, a_column) || !(a_buttonInt >= Utilities.ZERO && a_buttonInt <= Utilities.THREE)){
            return null;
        }
        
    	// Instantiate the chosen piece
    	switch(a_buttonInt){
        	case Utilities.ZERO: return new Queen(a_color, a_row, a_column);
        	case Utilities.ONE: return new Rook(a_color, a_row, a_column);
        	case Utilities.TWO: return new Bishop(a_color, a_row, a_column);
        	case Utilities.THREE: return new Knight(a_color, a_row, a_column);
        	default: return null;
    	}
    }
    
    /**/
    /*
    NAME
        public static King KingFactory(final ChessColor a_color, final int a_row, final int a_column, final boolean a_kingside, final boolean a_queenside);
    
    SYNOPSIS
        public static King KingFactory(final ChessColor a_color, final int a_row, final int a_column, final boolean a_kingside, final boolean a_queenside);
    
        ChessColor a_color -----------> The king's color.
        
        int a_row --------------------> The king's row.
        
        int a_column -----------------> The king's column.
        
        boolean a_kingside -----------> If the king can perform a kingside castle.
        
        boolean a_queenside ----------> If the king can perform a queenside castle.

    DESCRIPTION
        This method creates a king with the given coordinates.
        A king with the given castling rights and zero moves made is instantiated initially.
        If he can't castle on either side given the input, a new King object
        with one move already made and false castling flags for both sides will be 
        created and returned so that there will be no ambiguities about this king not being able to castle. 
        Otherwise, the initial king with the given rights will be returned instead.
    
    RETURNS
        King: A king with the proper castling rights.
    
    AUTHOR
        Ryan King
    */
    public static King KingFactory(final ChessColor a_color, final int a_row, final int a_column, final boolean a_kingside, final boolean a_queenside){
        // Garbage in, garbage out
        if(!BoardUtilities.HasValidCoordinates(a_row, a_column) || a_color == null){
            return null;
        }
        
        // Make a fresh king with the given rights and 0 moves
        final King KING = new King(a_color, a_row, a_column, a_kingside, a_queenside);
        
        // Make a king that has at least 1 move if he can't castle on either side
        // in order to make it unambiguous when the observer reassigns castling rights
        // The constructor I'm using here automatically sets both castling rights to false
        if(!a_kingside && !a_queenside){           
            return new King(KING, a_row, a_column, Utilities.ONE);
        }else{
            return KING;
        }
    }
    
    /**/
    /*
    NAME
        public static Rook RookFactory(final ChessColor a_color, final int a_row, final int a_column, final boolean a_kingside, final boolean a_queenside);
    
    SYNOPSIS
        public static Rook RookFactory(final ChessColor a_color, final int a_row, final int a_column, final boolean a_kingside, final boolean a_queenside);
    
        ChessColor a_color -----------> The rook's color.
        
        int a_row --------------------> The rook's row.
        
        int a_column -----------------> The rook's column.
        
        boolean a_kingside -----------> If the king can perform a kingside castle.
        
        boolean a_queenside ----------> If the king can perform a queenside castle.

    DESCRIPTION
        This method creates a rook with the given coordinates.
        This is used to instantiate a rook when castling rights may be
        different on either side, say, when rights are represented Kkq
        and white cannot queenside castle.
    
    RETURNS
        Rook: A rook with the proper castling rights.
    
    AUTHOR
        Ryan King
    */
    public static Rook RookFactory(final ChessColor a_color, final int a_row, final int a_column, final boolean a_kingside, final boolean a_queenside){
        // Garbage in, garbage out
        if(!BoardUtilities.HasValidCoordinates(a_row, a_column) || a_color == null){
            return null;
        }
        
        // Make a fresh rook with castling rights
        final Rook ROOK = new Rook(a_color, a_row, a_column);
        
        // Make a rook that has at least 1 move so that it can't castle
        // This makes it unambiguous when the observer reassigns castling rights
        if(BoardUtilities.IsKingsRook(a_color, a_row, a_column) || BoardUtilities.IsQueensRook(a_color, a_row, a_column)){
            if(BoardUtilities.IsKingsRook(a_color, a_row, a_column)){
                if(!a_kingside){
                    return new Rook(ROOK, a_row, a_column, Utilities.ONE);
                }else{
                    return ROOK;
                }
            }else if(BoardUtilities.IsQueensRook(a_color, a_row, a_column) && !a_queenside){
                return new Rook(ROOK, a_row, a_column, Utilities.ONE);
            }else{
                return ROOK;
            }
        }else{
            return ROOK;
        }
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
        // Idiot proofing which is not done for the victim because it can be null
        if(a_candidate == null || !BoardUtilities.HasValidCoordinates(a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn()) || !BoardUtilities.HasValidCoordinates(a_destinationRow, a_destinationColumn) || a_board == null){
            return null;
        }
        
        // Get the coordinates of the piece
    	final int SOURCE_ROW = a_candidate.GetCurrentRow();
    	final int SOURCE_COLUMN = a_candidate.GetCurrentColumn();
    	
        if(!a_candidate.IsKing() && !a_candidate.IsPawn()){// This is definitely not a castling or en passant move
            if(a_victim != null){
                return new AttackingMove(a_candidate, a_destinationRow, a_destinationColumn, a_board);
            }else{
                return new RegularMove(a_candidate, a_destinationRow, a_destinationColumn, a_board);
            }
        }else{
            if(a_candidate.IsKing()){// This could be a castling move
                if(MoveEvaluation.IsCastlingMove(a_candidate, SOURCE_ROW, SOURCE_COLUMN, a_destinationRow, a_destinationColumn)){
                    // This is a castling move
                    return new CastlingMove((King)a_candidate, a_destinationRow, a_destinationColumn, a_board);               
                }else{// This is a regular or attacking move
                    if(a_victim != null){
                        return new AttackingMove(a_candidate, a_destinationRow, a_destinationColumn, a_board);
                    }else{
                        return new RegularMove(a_candidate, a_destinationRow, a_destinationColumn, a_board);
                    }
                }
            }else{// This could be a regular move, an attacking move, or an en passant move
                if(MoveEvaluation.IsEnPassantMove(a_candidate, a_destinationRow, a_destinationColumn, a_board)){
                    // This is an en passant move
                    final Pawn VICTIM;
                    
                    if(BoardUtilities.HasValidCoordinates(a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn() + Utilities.ONE)
                            && a_board.GetTile(a_candidate.GetCurrentRow(), a_candidate.GetCurrentColumn() + Utilities.ONE).IsOccupied()){
                        VICTIM = (Pawn) a_board.GetTile(SOURCE_ROW, SOURCE_COLUMN + Utilities.ONE).GetPiece();
                    }else{
                        VICTIM = (Pawn) a_board.GetTile(SOURCE_ROW, SOURCE_COLUMN - Utilities.ONE).GetPiece();
                    }
                    
                    return new EnPassantMove((Pawn)a_candidate, a_destinationRow, a_destinationColumn, VICTIM, a_board);
                }else{// This isn't an en passant move
                    // This is a regular or attacking move
                    if(a_victim != null){
                        return new AttackingMove(a_candidate, a_destinationRow, a_destinationColumn, a_board);
                    }else{
                        return new RegularMove(a_candidate, a_destinationRow, a_destinationColumn, a_board);
                    }
                }
            }
        }
    }
}