package com.DarkBlue.Piece;

import com.DarkBlue.Move.*;
import com.DarkBlue.Utilities.*;
import com.DarkBlue.Board.*;

import java.util.ArrayList;
/*
 *  This class represents the king, which is the most important piece in the game of chess.
 * 
 *  He is the second-weakest piece in the game, only moving one square in any direction,
 *  as long as said square is not threatened by an enemy piece.
 *  
 *  * = This move is legal
 *  
 *  - = This move is illegal
 *  
 *  (full moves)        (limited moves)
 *  
 *  * * *               R - -
 *  * K *               - K *
 *  * * *               - * *
 *  
 *  8 moves             4 moves (The rook is assumed to be a lone enemy rook, so the king can capture it)
 *  
 *  The king cannot be captured at any time by the modern rules of chess, but threatening
 *  him with inescapable capture is the key to winning the game.
 *  He also cannot give check on his own, but he can help other pieces 
 *  to deliver checkmate in certain circumstances.
 *  
 *  If the king is threatened by the opponent but can escape, capture the opposing piece,
 *  use another piece to capture the threat, or use another piece to block the threat,
 *  he is said to be in check, and must resolve the threat in one of these four ways.
 *  No other piece can move unless it can help the king.
 *  Any move that does not get the king out of check will not be added as per the rules
 *  of this engine. A human player placed in check will be shown a warning message.
 *  
 *  The opponent is the only one allowed to place the other player's king into check.
 * 
 *  No move the player makes is allowed to place their king into check, whether by accident
 *  or intentionally. This engine gets rid of such moves by making them on a cloned board and
 *  testing for king safety.
 *  
 *  The state of stalemate occurs if the player's king is not in check, but is threatened with
 *  any other move he makes and no other friendly piece remaining on the board, if any, has any legal moves.
 *  This condition draws the game by the modern rules of chess and the game is over.
 *  The program will warn of this condition and terminate.
 *  
 *  The main objective of the game is to bring the opponent's king into the state of
 *  checkmate, which means that their king is in check where he is now and is also
 *  threatened in every possible move he has and cannot escape the threat, 
 *  capture the threatening piece himself, use a friendly
 *  piece to capture the threat, or use a friendly piece to block the threat if that piece
 *  moves in a sequential manner. The side declaring the checkmate wins and the game is over.
 *  Again, the program will warn either type of player once a checkmate is declared.
 *  
 *  If both players only have their kings, which cannot give check to each other, the game 
 *  ends in a draw. This can also happen with insufficient material, which I will explain
 *  in a different section.
 * 
 */
public final class King extends Piece{
    
    private final ArrayList<Move> m_currentCastlingMoves;
    
    /*
    NAME
        public King(final ChessColor a_color, final int a_currentRow, final int a_currentColumn);
    
    SYNOPSIS
        public King(final ChessColor a_color, final int a_currentRow, final int a_currentColumn);
        
        ChessColor a_color --------> The color of the piece, used primarily by the GUI.

        int a_currentRow ----------> The piece's current row.
        
        int a_currentColumn -------> The piece's current column.
    
    DESCRIPTION
        This constructor constructs a new King object by calling the Piece 
        superclass constructor and filling in the specific fields.
        
        ArrayLists that contain all and current legal moves are also instantiated, to be
        populated later.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public King(final ChessColor a_color, final int a_currentRow, final int a_currentColumn){
        
        super(a_color, PieceType.KING, Utilities.KING_ICON, AssignPieceBoardIcon(PieceType.KING, a_color), a_currentRow, a_currentColumn, AssignPieceValue(PieceType.KING, a_color));
        
        this.m_currentCastlingMoves = new ArrayList<>();
    }
    
    /*
    NAME
        public King(final Piece a_piece);
    
    SYNOPSIS
        public King(final Piece a_piece);
        
        Piece a_piece --------> The Piece to be copied.
    
    DESCRIPTION
        This copy constructor constructs a new King object by passing in
        a Piece object and cloning its fields.
        
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public King(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves){
        super(a_piece, a_newRow, a_newColumn, a_moves);
        King candidate = (King) a_piece;
        
        this.m_currentCastlingMoves = new ArrayList<>();
        
        this.m_currentCastlingMoves.addAll(MoveEvaluation.CopyCurrentMoves(candidate.GetCurrentCastlingMoves()));
    }
    
    /*
    NAME
        public void AddCurrentLegalMoves(final Board a_board);
    
    SYNOPSIS
        public void AddCurrentLegalMoves(final Board a_board);
    
        Board a_board ----> The chessboard which contains the current game.
    
    DESCRIPTION
        This method populates the current legal move array, taking into account which
        tiles the piece can actually visit on this turn. For example, no tile occurring after an
        opposing piece or on and after a friendly piece can be visited. This piece
        cannot give check, but can help give checkmate. No piece can be taken if it is protected
        by another piece. Also, this piece may have limited legal moves 
        if it is in check, or none at all if it is in checkmate or stalemate.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    @Override
    public final void AddCurrentLegalMoves(final Board a_board){
        // Clear out the legal moves to prepare for new evaluation
        m_currentLegalMoves.clear();
        m_currentCastlingMoves.clear();

        // Add the current moves in the king's spectrum
        this.m_currentLegalMoves.addAll(MoveEvaluation.AddCurrentSpectrumMoves(this, a_board, MoveEvaluation.m_allKingMoves));      
        
        // Evaluate castling moves if the king has not moved and is not in check
        if(!this.HasMoved() && this.GetCurrentColumn() == Utilities.FOUR
        		&& (this.IsWhite() && this.GetCurrentRow() == Utilities.SEVEN) || (this.IsBlack() && this.GetCurrentRow() == Utilities.ZERO)
        		&& MoveEvaluation.IsKingSafe(a_board, this.GetCurrentRow(), this.GetCurrentColumn(), this.GetColor())){
            this.AddCurrentCastlingMoves(a_board);
        }
        
        // Add the castling moves if there are any
        if(!this.m_currentCastlingMoves.isEmpty()){
            this.m_currentLegalMoves.addAll(this.m_currentCastlingMoves);
        }
    }
    
    /**/
    /*
    NAME
        public final boolean IsPawn();
    
    SYNOPSIS
        public final boolean IsPawn();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a pawn.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsPawn(){
    	return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsKing();
    
    SYNOPSIS
        public final boolean IsKing();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a king.
    
    RETURNS
        boolean: Always returns true.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsKing(){
    	return true;
    }
    
    /**/
    /*
    NAME
        public final boolean IsRook();
    
    SYNOPSIS
        public final boolean IsRook();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a rook.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsRook(){
    	return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsBishop();
    
    SYNOPSIS
        public final boolean IsBishop();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a bishop.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsBishop(){
    	return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsQueen();
    
    SYNOPSIS
        public final boolean IsQueen();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a queen.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsQueen(){
    	return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsKnight();
    
    SYNOPSIS
        public final boolean IsKnight();
    
        No parameters.
    
    DESCRIPTION
        This method determines if this piece is a knight.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsKnight(){
    	return false;
    }
    
    /*
    NAME
        public void AddCurrentCastlingMoves(final Board a_board);
    
    SYNOPSIS
        public void AddCurrentCastlingMoves(final Board a_board);
    
        Board a_board ----> The chessboard which contains the current game.
    
    DESCRIPTION
        This method populates the current castling move array, taking into account 
        the state of the king on this turn. The rules of castling apply:
        
            1. The king must not have moved or castled before.
            
            2. The rook on the desired side must not have moved or castled before.
            
            3. The two or three squares between the king and the rook must all be empty.
            
            4. The king must not castle into check, through check, or in order to escape check.
            This does not apply if the rook is threatened at any point during the castling.
            
        If all of those statements are true, the move is deemed legal and is added to its ArrayList.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void AddCurrentCastlingMoves(final Board a_board){
        if((this.IsWhite() && this.GetCurrentRow() == Utilities.SEVEN && this.GetCurrentColumn() == Utilities.FOUR) 
                || (this.IsBlack() && this.GetCurrentRow() == Utilities.ZERO && this.GetCurrentColumn() == Utilities.FOUR)
                && !this.HasMoved()){
            int kingDestinationRow = this.GetCurrentRow(), kingDestinationColumn;

            // Do not instantiate the move if the king cannot castle for any reason
            if(this.CanKingsideCastle(a_board)){                
            	kingDestinationColumn = Utilities.SIX;
            	
                // Instantiate the move
                CastlingMove castle = new CastlingMove(this, kingDestinationRow, kingDestinationColumn);    
                
                // Add the move to the list
                m_currentCastlingMoves.add(castle);
            }  
            
            if(this.CanQueensideCastle(a_board)){                
            	kingDestinationColumn = Utilities.TWO;
            	
                // Instantiate the move
                CastlingMove castle = new CastlingMove(this, kingDestinationRow, kingDestinationColumn);    
                
                // Add the move to the list
                m_currentCastlingMoves.add(castle);
            }
        }else{
            return;
        }
    }
    
    /*
    NAME
        public boolean CanKingsideCastle(final Board a_board);
    
    SYNOPSIS
        public boolean CanKingsideCastle(final Board a_board);
    
        Board a_board ----> The chessboard that contains the current state of the game.
    
    DESCRIPTION
        This method determines if a king can kingside castle.
        This is on the right for white and on the left for black.
        There are two spaces between the king and rook.
        The rules of castling apply when determining this.
    
        See the comments for AddCurrentCastlingMoves()
        above for a full description of the rules of castling.
    
    RETURNS
        True if the king can castle, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean CanKingsideCastle(final Board a_board){
        int row = this.GetCurrentRow(), column = this.GetCurrentColumn() + Utilities.ONE, 
                // The king's rook always starts at column 7 of my board.
        rookRow = this.GetCurrentRow(), rookColumn = Utilities.SEVEN;
        
        // Evaluate each tile the king will move across
        while(column < Utilities.SEVEN){
            if(a_board.GetTile(row, column).IsOccupied() || !MoveEvaluation.IsKingSafe(a_board, row, column, this.GetColor())){
                return false;
            }
            column++;
        }
        
        // Only return true if there's a friendly rook that has not moved
        if(a_board.GetTile(rookRow, rookColumn).IsOccupied() 
                && a_board.GetTile(rookRow, rookColumn).GetPiece().IsRook()
                && a_board.GetTile(rookRow, rookColumn).GetPiece().IsAlly(this)
                && !a_board.GetTile(rookRow, rookColumn).GetPiece().HasMoved()){
            return true;
        }else{
            return false;
        }
    }
    
    /*
    NAME
        public boolean CanQueensideCastle(final Board a_board);
    
    SYNOPSIS
        public boolean CanQueensideCastle(final Board a_board);
    
        Board a_board ----> The chessboard that contains the current state of the game.
    
    DESCRIPTION
        This method determines if a king can queenside castle.
        This is on the left for white and on the right for black.
        There are three spaces between the king and rook.
        The rules of castling apply when determining this.
    
        See the comments for AddCurrentCastlingMoves()
        above for a full description of the rules of castling.
    
    RETURNS
        True if the king can castle, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean CanQueensideCastle(final Board a_board){
        int row = this.GetCurrentRow(), column = this.GetCurrentColumn() - Utilities.ONE,
                // The queen's rook always starts at column 0 of my board.
        rookRow = this.GetCurrentRow(), rookColumn = Utilities.ZERO;
            
        // Evaluate each tile the king will move across
        while(column > Utilities.ONE){
            if(a_board.GetTile(row, column).IsOccupied() || !MoveEvaluation.IsKingSafe(a_board, row, column, this.GetColor())){
                return false;
            }
            column--;
        }
        
        // Check the tile next to the rook
        if(a_board.GetTile(row, column).IsOccupied()){
            return false;
        }
        
        // Only return true if there's a friendly rook that has not moved
        if(a_board.GetTile(rookRow, rookColumn).IsOccupied() 
                && a_board.GetTile(rookRow, rookColumn).GetPiece().IsRook()
                && a_board.GetTile(rookRow, rookColumn).GetPiece().IsAlly(this)
                && !a_board.GetTile(rookRow, rookColumn).GetPiece().HasMoved()){
            return true;
        }else{
            return false;
        }
    }
    
    /*
    NAME
        public ArrayList<CastlingMove> GetCurrentCastlingMoves();
    
    SYNOPSIS
        public ArrayList<CastlingMove> GetCurrentCastlingMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns the ArrayList of current castling moves.
    
    RETURNS
        ArrayList<CastlingMove> m_currentCastlingMoves: The array of current castling moves.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> GetCurrentCastlingMoves(){
        return m_currentCastlingMoves;
    }
}