package com.DarkBlue.Move;

import com.DarkBlue.Piece.*;
import com.DarkBlue.Utilities.*;

/*
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
 * Kingside castle: R - - K becomes - K R -
 * 
 * Queenside castle: K - - - R becomes - R K - -
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
 * This does not apply if the rook is being threatened on the turn or passes through threatening tiles.
 * 
 * 4. The king cannot use castling as a means to escape from check.
 * 
 * 5. Neither the king nor the chosen rook must have moved or castled before.
 * This still applies if the king or rook move back into their original spot after moving out of it.
 * If the king has not moved but one rook has moved 
 * and the player decides to castle with the other one that has not 
 * moved yet, this is legal.
 * 
 */
public final class CastlingMove extends Move{
    
    private final int m_rookCurrentRow;
    private final int m_rookCurrentColumn;
    private final int m_rookDestinationRow;
    private final int m_rookDestinationColumn;
    
    /*
    NAME
        public CastlingMove(final King a_king, final int a_kingNewRow, final int a_kingNewColumn);
    
    SYNOPSIS
        public CastlingMove(final King a_king, final int a_kingNewRow, final int a_kingNewColumn);
    
        King a_king --------------> The king to be castled.
        
        int a_kingNewRow ---------> The new row (same as the old one).
        
        int a_kingNewColumn ------> The new column (2 different from the old one).
    
    DESCRIPTION
        This constructor instantiates a new CastlingMove object.
        It receives the king and his desired coordinates.
        It calculates the source and destination
         positions of the rook based on the coordinates where the king is to move.
        The victim is set to null since castling does not capture any pieces.
        This is the only type of move where two pieces move on the same turn.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public CastlingMove(final King a_king, final int a_kingNewRow, final int a_kingNewColumn){
        
        super(a_king, a_kingNewRow, a_kingNewColumn, null, MoveType.CASTLING);
        
        this.m_rookCurrentRow = a_king.GetCurrentRow();
        this.m_rookDestinationRow = a_king.GetCurrentRow();

        this.m_rookCurrentColumn = AssignRookCurrentColumn(this.m_newColumn);
        this.m_rookDestinationColumn = AssignRookDestinationColumn(this.m_newColumn);
    }
    
    @Override
    public final String GetStringMove(){
        if((this.m_newColumn - this.m_oldColumn) == Utilities.TWO){
            return Utilities.KINGSIDE_CASTLE;
        }else{
            return Utilities.QUEENSIDE_CASTLE;
        }
    }
    
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
    
    /*
    NAME
        public int GetRookDestinationRow();
    
    SYNOPSIS
        public int GetRookDestinationRow();
    
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
    
    /*
    NAME
        public int GetRookDestinationColumn();
    
    SYNOPSIS
        public int GetRookDestinationColumn();
    
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
}