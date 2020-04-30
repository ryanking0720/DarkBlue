package com.DarkBlue.Move;

import com.DarkBlue.Piece.Piece;
import com.DarkBlue.Piece.King;
import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Utilities.BoardUtilities;
import com.DarkBlue.Board.Board;

/**
 * This class represents a castling move, which is a special type of move
 * that can only occur up to one time per side per game.
 * 
 * In this move, the king and rook move at the same time to spots between
 * their current positions. This is the only type of move where two pieces
 * are allowed to move together during a single turn.
 * 
 * The king always moves two spaces toward the rook and the rook gets placed
 * opposite the king, one tile away from the king's original position.
 * 
 * For white:
 * Kingside castle: K - - R becomes - R K -
 * 
 * Queenside castle: R - - - K becomes - - K R -
 * 
 * For black:
 * Kingside castle: r - - k becomes - k r -
 * 
 * Queenside castle: k - - - r becomes - r k - -
 * 
 * There are special rules that pertain to castling, so a player may not
 * always have the privilege to castle.
 * 
 * 1. The king must not currently be in check.
 * 
 * 2. The tiles between the king and the rook must be empty. That is, free of any friendly or opposing pieces.
 * 
 * 3. The tiles the king will move across must not be threatened by any enemy piece.
 * That is, the king must not castle through check.
 * This does not apply if the rook is being threatened on the turn or passes through threatening tiles
 * on a queenside castle.
 * 
 * 4. The king cannot use castling as a means to escape from check.
 * 
 * 5. Neither the king nor the chosen rook must have moved or castled before.
 * This still applies if the king or rook move back into their original spot after moving out of it.
 * If the king has not moved but one rook has moved 
 * and the player decides to castle with the other one that has not 
 * moved yet, this is legal.
 */
public final class CastlingMove extends Move{
    
    private final int m_rookCurrentRow;
    private final int m_rookCurrentColumn;
    private final int m_rookDestinationRow;
    private final int m_rookDestinationColumn;
    
    /**/
    /*
    NAME
        public CastlingMove(final King a_king, final int a_kingNewRow, final int a_kingNewColumn);
    
    SYNOPSIS
        public CastlingMove(final King a_king, final int a_kingNewRow, final int a_kingNewColumn, final Board a_board);
    
        King a_king --------------> The king to be castled.
        
        int a_kingNewRow ---------> The new row (same as the old one).
        
        int a_kingNewColumn ------> The new column (2 different from the old one).
        
        Board a_board ------------> The board on which this move is made.
    
    DESCRIPTION
        This constructor instantiates a new CastlingMove object.
        It receives the king and his desired coordinates, as well as the board.
        It calculates the source and destination
        positions of the rook based on the coordinates where the king is to move.
        The victim is set to null since castling does not capture any pieces.
        This is the only type of move where two pieces move on the same turn.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public CastlingMove(final King a_king, final int a_kingNewRow, final int a_kingNewColumn, final Board a_board){
        
        super(a_king, a_kingNewRow, a_kingNewColumn, a_board);
        
        this.m_rookCurrentRow = a_king.GetCurrentRow();
        this.m_rookDestinationRow = a_king.GetCurrentRow();

        this.m_rookCurrentColumn = AssignRookCurrentColumn(this.m_newColumn);
        this.m_rookDestinationColumn = AssignRookDestinationColumn(this.m_newColumn);
    }
    
    /**/
    /*
    NAME
        public final String toString();
    
    SYNOPSIS
        public final String toString();
    
        No parameters.
    
    DESCRIPTION
        This method is overridden from the Object superclass.
        It returns a string representation of this move in
        algebraic notation.
        
        Per the rules prescribed by FIDE, a kingside castle by
        either side is returned as "0-0", and a queenside castle
        is returned as "0-0-0".
    
    
    RETURNS
        String moveString: A string representation of the move.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final String toString(){
        if((this.m_newColumn - this.GetOldColumn()) == Utilities.TWO){
            return Utilities.KINGSIDE_CASTLE;
        }else{
            return Utilities.QUEENSIDE_CASTLE;
        }
    }
    
    /**/
    /*
    NAME
        private static final int AssignRookDestinationColumn(final int a_kingDestinationColumn);
    
    SYNOPSIS
        private static final int AssignRookDestinationColumn(final int a_kingDestinationColumn);
    
        int a_kingDestinationColumn --------> The king's destination column.
    
    DESCRIPTION
        This method determines the destination column of the rook using the king's destination column.
    
    RETURNS
         Nothing
    
    AUTHOR
        Ryan King
    */
    private static final int AssignRookDestinationColumn(final int a_kingDestinationColumn){
        if(a_kingDestinationColumn == Utilities.SIX){
            return Utilities.FIVE;
        }else{
            return Utilities.THREE;
        }
    }
    
    /**/
    /*
    NAME
        private static final int AssignRookCurrentColumn(final int a_kingDestinationColumn);
    
    SYNOPSIS
        private static final int AssignRookCurrentColumn(final int a_kingDestinationColumn);
    
        int a_kingDestinationColumn --------> The king's destination column.
    
    DESCRIPTION
        This method determines the current column of the rook using the king's destination column.
    
    RETURNS
         Nothing
    
    AUTHOR
        Ryan King
    */
    protected static final int AssignRookCurrentColumn(final int a_kingDestinationColumn){
        if(a_kingDestinationColumn == Utilities.SIX){
            return Utilities.SEVEN;
        }else{
            return Utilities.ZERO;
        }
    }
    
    /**/
    /*
    NAME
        public Piece GetVictim();
    
    SYNOPSIS
        public Piece GetVictim();
    
        No parameters.
    
    DESCRIPTION
        This method is overridden from the Move superclass.
        It returns the victim of this move, which will always be null.
    
    RETURNS
        Piece m_victim: The captured piece.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final Piece GetVictim(){
        return null;
    }
    
    /**/
    /*
    NAME
        public final MoveType GetMoveType();
    
    SYNOPSIS
        public final MoveType GetMoveType();
    
        No parameters.
    
    DESCRIPTION
        This method returns the type of this move.
    
    RETURNS
        MoveType.CASTLING.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final MoveType GetMoveType(){
        return MoveType.CASTLING;
    }
    
    /**/
    /*
    NAME
        public final boolean HasVictim();
    
    SYNOPSIS
        public final boolean HasVictim();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move has a victim,
        i.e. if the victim field is not set equal to null.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean HasVictim(){
        return false;
    }
    
    /**/
    /*
    NAME
        public int GetRookCurrentRow();
    
    SYNOPSIS
        public int GetRookCurrentRow();
    
        No parameters.
    
    DESCRIPTION
        This method returns the current row of the rook object.
    
    RETURNS
         int m_rookCurrentRow: The current row of the rook object.
    
    AUTHOR
        Ryan King
    */
    public final int GetRookCurrentRow(){
        return this.m_rookCurrentRow;
    }
    
    /**/
    /*
    NAME
        public int GetRookCurrentColumn();
    
    SYNOPSIS
        public int GetRookCurrentColumn();
    
        No parameters.
    
    DESCRIPTION
        This method returns the current column of the rook object.
    
    RETURNS
         int m_rookCurrentColumn: The current column of the rook object.
    
    AUTHOR
        Ryan King
    */
    public final int GetRookCurrentColumn(){
        return this.m_rookCurrentColumn;
    }
    
    /**/
    /*
    NAME
        public final int GetRookDestinationRow();
    
    SYNOPSIS
        public final int GetRookDestinationRow();
    
        No parameters.
    
    DESCRIPTION
        This method returns the destination row of the rook object.
    
    RETURNS
         int m_rookDestinationRow: The destination row of the rook object.
    
    AUTHOR
        Ryan King
    */
    public final int GetRookDestinationRow(){
        return this.m_rookDestinationRow;
    }
    
    /**/
    /*
    NAME
        public final int GetRookDestinationColumn();
    
    SYNOPSIS
        public final int GetRookDestinationColumn();
    
        No parameters.
    
    DESCRIPTION
        This method returns the destination column of the rook object.
    
    RETURNS
         int m_rookDestinationColumn: The destination column of the rook object.
    
    AUTHOR
        Ryan King
    */
    public final int GetRookDestinationColumn(){
        return this.m_rookDestinationColumn;
    }
    
    /**/
    /*
    NAME
        public final boolean IsRegular();
    
    SYNOPSIS
        public final boolean IsRegular();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move type is Regular.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsRegular(){
        return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsAttacking();
    
    SYNOPSIS
        public final boolean IsAttacking();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move type is Attacking.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsAttacking(){
        return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsCastling();
    
    SYNOPSIS
        public final boolean IsCastling();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move type is Castling.
    
    RETURNS
        boolean: Always returns true.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsCastling(){
        return true;
    }
    
    /**/
    /*
    NAME
        public final boolean IsEnPassant();
    
    SYNOPSIS
        public final boolean IsEnPassant();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the move type is En Passant.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsEnPassant(){
        return false;
    }
    
    /**/
    /*
    NAME
        public final Board GetTransitionalBoard();
    
    SYNOPSIS
        public final Board GetTransitionalBoard();
    
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
    @Override
    public final Board GetTransitionalBoard(){
        // Make a copy of the initial board
        final Board CLONE = Board.GetDeepCopy(this.m_initialBoard);

        // Make the move on the copied board
        return CLONE.Castle(this);
    }
}