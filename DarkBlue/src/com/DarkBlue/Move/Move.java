package com.DarkBlue.Move;

import com.DarkBlue.Board.Board;
import com.DarkBlue.Piece.Piece;
import com.DarkBlue.Player.Human;
import com.DarkBlue.Player.Minimax;
import com.DarkBlue.Player.Player;
import com.DarkBlue.Utilities.BoardUtilities;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Utilities.Utilities;

/**
 * This class represents a move, which can take on several different forms.
 * 
 * The fields that all moves have in common are the moving piece,
 * its current row and column (taken from the moving piece), 
 * its destination row and column,
 * the victim (if any), and the type of the move.
 * 
 * All of these fields are final for immutability and protected to be passed down to the subclasses.
 * 
 * If no victim is present, the victim is set to null.
 * 
 * There are 4 different types of moves this extends to:
 * 
 * 1. The regular move. This move represents any piece moving to another tile without capturing any other pieces.
 * Castling has its own class.
 * 
 * 2. The attacking move. This move represents any piece moving to a tile occupied by an enemy piece and capturing it.
 * The captured piece is no longer considered to be in the game and is removed from the board.
 * The special pawn capture of en passant has its own class which is treated differently.
 * 
 * 3. The castling move. This move is a special move performed only by the king as his first move.
 * He switches places with the rook inside.
 * I will include more details about it in its own source file.
 * 
 * 4. The en passant move. This is a special pawn capture that can only occur after an enemy pawn's first move
 * of two squares. I will include more details on its own source file.
 * 
 * The toString() method is abstract, since the string representation of each move is slightly different
 * depending on the rules of algebraic notation, the type of move, as well as the moving piece.
 * 
 * Parts of the design of this class were inspired 
 * by the design of the Move class by Amir Afghani in Black Widow Chess,
 * https://github.com/amir650/BlackWidow-Chess
 * but any code not found in that repository was written by Ryan King.
 */
public abstract class Move{

    // The piece on the old tile
    protected final Piece m_piece;
    
    // The new row the piece wants to move to
    protected final int m_newRow;
    
    // The new column the piece wants to move to
    protected final int m_newColumn;
    
    // The initial configuration of the board before this move is made
    protected final Board m_initialBoard;
    
    /**/
    /*
    NAME
        public Move(final Piece a_piece, final int a_newRow, final int a_newColumn, final Board a_board);
    
    SYNOPSIS
        public Move(final Piece a_piece, final int a_newRow, final int a_newColumn, final Board a_board);
        
        Piece a_piece --------> The piece to be moved.
        
        int a_newRow ---------> The piece's desired row.
        
        int a_newColumn ------> The piece's desired column.

        Board a_board --------> The initial board on which this move is made.

    DESCRIPTION
        This constructor initializes a Move object using the piece,
        its current row and column, the new tile's row and column 
        the victim on the tile (if any), and the board this move is being made on.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, but based off a similar method by Amir Afghani,
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/board/Move.java
    */
    public Move(final Piece a_piece, final int a_newRow, final int a_newColumn, final Board a_board){
        if(a_piece == null || !BoardUtilities.HasValidCoordinates(a_piece.GetCurrentRow(), a_piece.GetCurrentColumn()) || a_board == null || !BoardUtilities.HasValidCoordinates(a_newRow, a_newColumn)){
            System.err.println("Invalid parameters in move");
            System.exit(Utilities.ONE);
        }
        
        this.m_piece = a_piece;
        this.m_newRow = a_newRow;
        this.m_newColumn = a_newColumn;
        this.m_initialBoard = a_board;
    }
    
    /* Concrete methods */
    
    /**/
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
        Ryan King, with inspiration from Amir Afghani, 
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/board/Move.java
    */
    public final Piece GetPiece(){
        return this.m_piece;
    }
    
    /**/
    /*
    NAME
        public final int GetOldRow();
    
    SYNOPSIS
        public final int GetOldRow();
    
        No parameters.
    
    DESCRIPTION
        This method returns the piece's current row.
        
    RETURNS
        int: The piece's current row.
    
    AUTHOR
        Ryan King
    */
    public final int GetOldRow(){
        return this.m_piece.GetCurrentRow();
    }
    
    /**/
    /*
    NAME
        public final int GetOldColumn();
    
    SYNOPSIS
        public final int GetOldColumn();
    
        No parameters.
    
    DESCRIPTION
        This method returns the piece's current column.
        
    RETURNS
        int: The piece's current column.
    
    AUTHOR
        Ryan King
    */
    public final int GetOldColumn(){
        return this.m_piece.GetCurrentColumn();
    }
    
    /**/
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
    
    /**/
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
    
    /**/
    /*
    NAME
        public final Board GetInitialBoard();
    
    SYNOPSIS
        public final Board GetInitialBoard();
    
        No parameters.
    
    DESCRIPTION
        This method returns the initial board field.
    
    RETURNS
        Board m_initialBoard: The initial board field.
    
    AUTHOR
        Based off a similar method by Amir Afghani,
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/board/Move.java
    */
    public final Board GetInitialBoard(){
        return this.m_initialBoard;
    }
    
    /**/
    /*
    NAME
        public final boolean PlacesOpponentIntoCheck();
    
    SYNOPSIS
        public final boolean PlacesOpponentIntoCheck();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move ends up producing a state of the board
        where the opponent ends up in check.
    
    RETURNS
        boolean: True if the opponent ends up in check after the move, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Based off isThreatenedBoardImmediate() by Amir Afghani,
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/board/BoardUtils.java
    */
    public final boolean PlacesOpponentIntoCheck(){
        // Make a copy of the transitional board
        final Board CLONE = this.GetTransitionalBoard();
        
        // Initialize dummy players to hold the pieces
        final Player WHITE = new Human(ChessColor.WHITE, CLONE);
        final Player BLACK = new Human(ChessColor.BLACK, CLONE);
        
        // Determine what pieces each side has
        WHITE.InitializePieces(CLONE);
        BLACK.InitializePieces(CLONE);

        // Determine who the opponent is
        final Player OPPONENT = (this.m_piece.IsWhite() ? BLACK : WHITE);
        
        // Return if the opponent is in check
        return OPPONENT.IsInCheck(CLONE);
    }
    
    /**/
    /*
    NAME
        public final boolean PlacesOpponentIntoCheckmate();
    
    SYNOPSIS
        public final boolean PlacesOpponentIntoCheckmate();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move ends up producing a state of the board
        where the opponent ends up in checkmate.
    
    RETURNS
        boolean: True if the opponent ends up in checkmate after the move, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Based off isThreatenedBoardImmediate() by Amir Afghani,
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/board/BoardUtils.java
    */
    public final boolean PlacesOpponentIntoCheckmate(){
        // Make a copy of the transitional board
        final Board CLONE = this.GetTransitionalBoard();
        
        // Initialize dummy players to hold the pieces
        final Player WHITE = new Human(ChessColor.WHITE, CLONE);
        final Player BLACK = new Human(ChessColor.BLACK, CLONE);
        
        // Determine what pieces each side has
        WHITE.InitializePieces(CLONE);
        BLACK.InitializePieces(CLONE);

        // Determine who the opponent is
        final Player OPPONENT = (this.m_piece.IsWhite() ? BLACK : WHITE);
        
        // Return if the opponent is in checkmate
        return OPPONENT.IsInCheckmate(CLONE);
    }
    
    /* Abstract methods */
    
    /**/
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
        Piece: The victim, if any; otherwise null.
    
    AUTHOR
        Ryan King, but based off a similar method by Amir Afghani,
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/board/Move.java
    */
    public abstract Piece GetVictim();
    
    /**/
    /*
    NAME
        public abstract boolean HasVictim();
    
    SYNOPSIS
        public abstract boolean HasVictim();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the destination tile has a victim,
        i.e. if the tile's piece is not set equal to null.
    
    RETURNS
        boolean: true if the tile's piece is non-null, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public abstract boolean HasVictim();
    
    /**/
    /*
    NAME
        public abstract MoveType GetMoveType();
    
    SYNOPSIS
        public abstract MoveType GetMoveType();
    
        No parameters.
    
    DESCRIPTION
        This method returns the type of the move.
        This can be Regular, Attacking, Castling, or En Passant.
    
    RETURNS
        m_moveType: The type of this move.
    
    AUTHOR
        Ryan King
    */
    public abstract MoveType GetMoveType();
    
    /**/
    /*
    NAME
        public abstract boolean IsRegular();
    
    SYNOPSIS
        public abstract boolean IsRegular();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move type is Regular.
    
    RETURNS
        True if the type is Regular, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public abstract boolean IsRegular();
    
    /**/
    /*
    NAME
        public abstract boolean IsAttacking();
    
    SYNOPSIS
        public abstract boolean IsAttacking();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move type is Attacking.
    
    RETURNS
        True if the type is Attacking, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public abstract boolean IsAttacking();
    
    /**/
    /*
    NAME
        public abstract boolean IsCastling();
    
    SYNOPSIS
        public abstract boolean IsCastling();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move type is Castling.
    
    RETURNS
        True if the type is Castling, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public abstract boolean IsCastling();
    
    /**/
    /*
    NAME
        public abstract boolean IsEnPassant();
    
    SYNOPSIS
        public abstract boolean IsEnPassant();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move type is En Passant.
    
    RETURNS
        True if the type is En Passant, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public abstract boolean IsEnPassant();
    
    /**/
    /*
    NAME
        public abstract Board GetTransitionalBoard();
    
    SYNOPSIS
        public abstract Board GetTransitionalBoard();
    
        No parameters.
    
    DESCRIPTION
        This method returns the resulting board that will
        be built once this move has been made.
    
    RETURNS
        Board: The initial board field with this move made on it.
    
    AUTHOR
        Based off the execute() method by Amir Afghani,
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/board/Move.java
    */
    public abstract Board GetTransitionalBoard();
}