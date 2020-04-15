package com.DarkBlue.Piece;

import com.DarkBlue.Move.Move;
import com.DarkBlue.Move.CastlingMove;
import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Utilities.MoveEvaluation;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Board.Board;

import java.util.ArrayList;
/**
 *  This class represents the king, which is the most important piece in the game of chess.
 *  Each player has one and only one king at all times.
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
 *  * * *               r - -
 *  * K *               - K *
 *  * * *               - * *
 *  
 *  8 moves             4 moves (This rook is assumed to be a lone enemy rook, so the king can capture it)
 *  
 *  The king cannot be captured at any time by the modern rules of chess, but threatening
 *  him with inescapable capture is the key to winning the game.
 *  He also cannot give check on his own, but he can help other pieces 
 *  to deliver checkmate in certain circumstances.
 *  
 *  If both sides are left with singular kings (called "bare kings"), the game ends in a draw.
 *  If one side has a bare king and the other has a king and a knight, the game ends in a draw.
 *  If one side has a bare king and the other has a king and a bishop, the game ends in a draw.
 *  If both sides have a king and a bishop and both bishops move on the same tile color, the game ends in a draw.
 *  The program will notify the user of any of these conditions as "insufficient material" and the game will end.
 *  
 *  The repetition of any board configuration at least three times (this does not need to be consecutive)
 *  is grounds for a draw by threefold repetition, which the program will tell the user.
 *  
 *  If the king is threatened by the opponent but can escape, capture the opposing piece himself,
 *  use another friendly piece to capture the threatening piece, or use a friendly piece to block the threat,
 *  he is said to be in check, and must resolve the threat in one of these four ways.
 *  The player must get his/her king out of check. Any other thing s/he may want to do will be put on hold.
 *  No other piece can move unless it can help the king.
 *  Any move that does not get the king out of check is illegal as per the rules
 *  of chess. A human player placed in check will be shown a warning pop-up.
 *  This does not occur if the human places the computer into check.
 *  
 *  The opponent is the only one allowed to place the other player's king into check.
 * 
 *  No move either player makes is allowed to place their own king into check, whether by accident
 *  or intentionally. This engine gets rid of such moves by making them on a cloned board and
 *  testing for king safety. The king cannot move into check himself, and friendly pieces that may be blocking a threat
 *  directed at the king will not be allowed to move out of the way for any reason until the threat no longer exists.
 *  
 *  The state of stalemate occurs if the player's king is not in check, but is threatened with
 *  any other move he makes and no other friendly piece remaining on the board, if any, has any legal moves.
 *  This condition draws the game by the modern rules of chess and the game is over.
 *  The program will warn of this condition and stop playing.
 *  
 *  The main objective of chess is to bring the opponent's king into the state of checkmate,
 *  which means that their king is in check where he is resting and is also
 *  in check despite using every possible move he has. 
 *  He must also be unable to escape the threat, 
 *  capture the threatening piece himself, 
 *  use a friendly piece to capture the threatening piece, 
 *  or use a friendly piece to block the threat if the threatening piece moves in a linear manner. 
 *  The side delivering checkmate wins and the game is over.
 *  Just like with check, stalemate, or draw conditions, the program will warn either type of player if and when checkmate is found.
 */
public final class King extends Piece{
    
    private final ArrayList<Move> m_currentCastlingMoves;
    private final boolean m_canKingsideCastle;
    private final boolean m_canQueensideCastle;
    
    /**/
    /*
    NAME
        public King(final ChessColor a_color, final int a_currentRow, final int a_currentColumn, final boolean a_canKingsideCastle, final boolean a_canQueensideCastle);
    
    SYNOPSIS
        public King(final ChessColor a_color, final int a_currentRow, final int a_currentColumn, final boolean a_canKingsideCastle, final boolean a_canQueensideCastle);
        
        ChessColor a_color -------------> The color of the piece, used primarily by the GUI.

        int a_currentRow ---------------> The piece's current row.
        
        int a_currentColumn ------------> The piece's current column.
        
        boolean a_canKingsideCastle ----> If the king can kingside castle.
        
        boolean a_canQueensideCastle ---> If the king can queenside castle.
    
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
    public King(final ChessColor a_color, final int a_currentRow, final int a_currentColumn, final boolean a_canKingsideCastle, final boolean a_canQueensideCastle){
        
        super(a_color, a_currentRow, a_currentColumn);
        
        this.m_currentCastlingMoves = new ArrayList<>();
        this.m_canKingsideCastle = a_canKingsideCastle;
        this.m_canQueensideCastle = a_canQueensideCastle;
    }
    
    /**/
    /*
    NAME
        public King(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves);
    
    SYNOPSIS
        public King(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves);
        
        Piece a_piece --------> The Piece to be copied.
        
        int a_newRow ---------> The piece's new row.
        
        int a_newColumn ------> The piece's new column.
        
        int a_newMoves -------> The piece's new number of moves.
    
    DESCRIPTION
        This copy constructor constructs a new King object by passing in
        a Piece object and cloning most of its fields.
        This is implied to instantiate a king that just moved, which is why both castling fields are set to false.
        
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public King(final Piece a_piece, final int a_newRow, final int a_newColumn, final int a_moves){
        super(a_piece, a_newRow, a_newColumn, a_moves);
        King candidate = (King) a_piece;
        
        this.m_currentCastlingMoves = new ArrayList<>();
        
        this.m_currentCastlingMoves.addAll(candidate.GetCurrentCastlingMoves());
        
        this.m_canKingsideCastle = false;
        this.m_canQueensideCastle = false;
    }
    
    /**/
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
                && (this.m_canKingsideCastle || this.m_canQueensideCastle) && MoveEvaluation.IsKingSafe(a_board, this.GetCurrentRow(), this.GetCurrentColumn(), this.GetColor())){
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
        public final PieceType GetPieceType();
    
    SYNOPSIS
        public final PieceType GetPieceType();
    
        No parameters.
    
    DESCRIPTION
        This method returns this piece's type.
    
    RETURNS
        PieceType.KING.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final PieceType GetPieceType(){
        return PieceType.KING;
    }
    
    /**/
    /*
    NAME
        public final char GetIcon();
    
    SYNOPSIS
        public final char GetIcon();
    
        No parameters.
    
    DESCRIPTION
        This method returns this piece's algebraic notation icon.
    
    RETURNS
        char: This piece's algebraic notation icon.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final char GetIcon(){
        if(this.IsWhite()){
            return Utilities.WHITE_KING_ICON;
        }else{
            return Utilities.BLACK_KING_ICON;
        }
    }
    
    /**/
    /*
    NAME
        public final char GetBoardIcon();
    
    SYNOPSIS
        public final char GetBoardIcon();
    
        No parameters.
    
    DESCRIPTION
        This method returns this piece's board icon.
    
    RETURNS
        char: This piece's board icon.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final char GetBoardIcon(){
        if(this.IsWhite()){
            return Utilities.WHITE_KING_BOARD_ICON;
        }else{
            return Utilities.BLACK_KING_BOARD_ICON;
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
    
    /**/
    /*
    NAME
        public final void AddCurrentCastlingMoves(final Board a_board);
    
    SYNOPSIS
        public final void AddCurrentCastlingMoves(final Board a_board);
    
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
        // Check to see if the king is in his original spot and has not moved before
        if((this.IsWhite() && this.GetCurrentRow() == Utilities.SEVEN && this.GetCurrentColumn() == Utilities.FOUR) 
                || (this.IsBlack() && this.GetCurrentRow() == Utilities.ZERO && this.GetCurrentColumn() == Utilities.FOUR)
                && !this.HasMoved()){
            
            final int KING_DESTINATION_ROW = this.GetCurrentRow();
            int kingDestinationColumn;

            // Do not instantiate the move if the king cannot castle for any reason
            if(this.CanKingsideCastleOnThisTurn(a_board)){                
                kingDestinationColumn = Utilities.SIX;
                
                // Instantiate the move
                final CastlingMove CASTLE = new CastlingMove(this, KING_DESTINATION_ROW, kingDestinationColumn, a_board);    
                
                // Add the move to the list
                m_currentCastlingMoves.add(CASTLE);
            }  
            
            if(this.CanQueensideCastleOnThisTurn(a_board)){                
                kingDestinationColumn = Utilities.TWO;
                
                // Instantiate the move
                final CastlingMove CASTLE = new CastlingMove(this, KING_DESTINATION_ROW, kingDestinationColumn, a_board);    
                
                // Add the move to the list
                m_currentCastlingMoves.add(CASTLE);
            }
        }else{
            return;
        }
    }
    
    /**/
    /*
    NAME
        public final void RemoveCastlingMoves();
    
    SYNOPSIS
        public final void RemoveCastlingMoves();
    
        No parameters.
    
    DESCRIPTION
        This method removes all castling moves from both the
        king's castling move array as well as its current legal move array.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public final void RemoveCastlingMoves(){
        this.m_currentCastlingMoves.clear();
        
        for(final Move MOVE : this.GetCurrentLegalMoves()){
            if(MOVE.IsCastling()){
                this.m_currentLegalMoves.remove(MOVE);
            }
        }
    }
    
    /**/
    /*
    NAME
        public final boolean CanKingsideCastleOnThisTurn(final Board a_board);
    
    SYNOPSIS
        public final boolean CanKingsideCastleOnThisTurn(final Board a_board);
    
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
    public final boolean CanKingsideCastleOnThisTurn(final Board a_board){
        if(!this.CanKingsideCastle() || this.HasMoved() || !this.IsInOriginalSpot()){
            return false;
        }
        
        final int ROW = this.GetCurrentRow();
        int column = this.GetCurrentColumn() + Utilities.ONE, 
        // The king's rook always starts at column 7 of my board.
        rookRow = this.GetCurrentRow(), rookColumn = Utilities.SEVEN;
        
        // See if there's a friendly rook that has not moved
        if(!HasKingsideCastlingRook(a_board)){
            return false;
        }
        
        // Evaluate each tile the king will move across
        while(column < Utilities.SEVEN){
            if(a_board.GetTile(ROW, column).IsOccupied() || !MoveEvaluation.IsKingSafe(a_board, ROW, column, this.GetColor())){
                return false;
            }
            column++;
        }
        
        return true;
    }
    
    /**/
    /*
    NAME
        public final boolean HasKingsideCastlingRook(final Board a_board);
    
    SYNOPSIS
        public final boolean HasKingsideCastlingRook(final Board a_board);
    
        Board a_board ----> The chessboard that contains the current state of the game.
    
    DESCRIPTION
        This method determines if there exists a friendly rook to the king's right (white)
        or left (black) that has not moved yet and could potentially castle with him.

    RETURNS
        True if such a rook is found, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean HasKingsideCastlingRook(final Board a_board){
        final int ROOK_ROW = this.GetCurrentRow(), ROOK_COLUMN = Utilities.SEVEN;
        
        // See if there's a friendly rook that has not moved
        if(a_board.GetTile(ROOK_ROW, ROOK_COLUMN).IsOccupied() 
                && a_board.GetTile(ROOK_ROW, ROOK_COLUMN).GetPiece().IsRook()
                && a_board.GetTile(ROOK_ROW, ROOK_COLUMN).GetPiece().IsAlly(this)
                && !a_board.GetTile(ROOK_ROW, ROOK_COLUMN).GetPiece().HasMoved()){
            return true;
        }
        
        return false;
    }
    
    /**/
    /*
    NAME
        public boolean CanQueensideCastleOnThisTurn(final Board a_board);
    
    SYNOPSIS
        public boolean CanQueensideCastleOnThisTurn(final Board a_board);
    
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
    public final boolean CanQueensideCastleOnThisTurn(final Board a_board){
        if(!this.CanQueensideCastle() || this.HasMoved() || !this.IsInOriginalSpot()){
            return false;
        }
        
        final int ROW = this.GetCurrentRow();
        int column = this.GetCurrentColumn() - Utilities.ONE,
        // The queen's rook always starts at column 0 of my board.
        rookRow = this.GetCurrentRow(), rookColumn = Utilities.ZERO;
        
        // See if there's a friendly rook that has not moved
        if(!HasQueensideCastlingRook(a_board)){
            return false;
        }       
            
        // Evaluate each tile the king will move across
        while(column > Utilities.ONE){
            if(a_board.GetTile(ROW, column).IsOccupied() || !MoveEvaluation.IsKingSafe(a_board, ROW, column, this.GetColor())){
                return false;
            }
            column--;
        }
        
        // Check the tile next to the rook
        if(a_board.GetTile(ROW, column).IsOccupied()){
            return false;
        }
        
        return true;
    }
    
    /**/
    /*
    NAME
        public final boolean HasQueensideCastlingRook(final Board a_board);
    
    SYNOPSIS
        public final boolean HasQueensideCastlingRook(final Board a_board);
    
        Board a_board ----> The chessboard that contains the current state of the game.
    
    DESCRIPTION
        This method determines if there exists a friendly rook to the king's left (white)
        or right (black) that has not moved yet and could potentially castle with him.

    RETURNS
        True if such a rook is found, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean HasQueensideCastlingRook(final Board a_board){
        final int ROOK_ROW = this.GetCurrentRow(), ROOK_COLUMN = Utilities.ZERO;
        
        // See if there's a friendly rook that has not moved
        if(a_board.GetTile(ROOK_ROW, ROOK_COLUMN).IsOccupied() 
                && a_board.GetTile(ROOK_ROW, ROOK_COLUMN).GetPiece().IsRook()
                && a_board.GetTile(ROOK_ROW, ROOK_COLUMN).GetPiece().IsAlly(this)
                && !a_board.GetTile(ROOK_ROW, ROOK_COLUMN).GetPiece().HasMoved()){
            return true;
        }
        
        return false;
    }
    
    /**/
    /*
    NAME
        public final boolean CanKingsideCastle();
    
    SYNOPSIS
        public final boolean CanKingsideCastle();
    
        No parameters.
    
    DESCRIPTION
        This method returns a field that says if the king can kingside castle.

    RETURNS
        boolean m_canKingsideCastle: If the king can perform a kingside castle.
    
    AUTHOR
        Ryan King, but inspired by Black Widow Chess by Amir Afghani,
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/pieces/King.java
    */
    public final boolean CanKingsideCastle(){
        return this.m_canKingsideCastle;
    }
    
    /**/
    /*
    NAME
        public final boolean CanQueensideCastle();
    
    SYNOPSIS
        public final boolean CanQueensideCastle();
    
        No parameters.
    
    DESCRIPTION
        This method returns a field that says if the king can queenside castle.

    RETURNS
        boolean m_canQueensideCastle: If the king can perform a queenside castle.
    
    AUTHOR
        Ryan King, but inspired by Black Widow Chess by Amir Afghani,
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/pieces/King.java
    */
    public final boolean CanQueensideCastle(){
        return this.m_canQueensideCastle;
    }
    
    /**/
    /*
    NAME
        public final boolean IsInOriginalSpot();
    
    SYNOPSIS
        public final boolean IsInOriginalSpot();
    
        No parameters.
    
    DESCRIPTION
        This method returns if a king is in his original spot according to the starting position of chess.

    RETURNS
        boolean: True if the king is in his original spot and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsInOriginalSpot(){
        return this.GetCurrentColumn() == Utilities.FOUR && ((this.IsWhite() && this.GetCurrentRow() == Utilities.SEVEN) || (this.IsBlack() && this.GetCurrentRow() == Utilities.ZERO));
    }
    
    /**/
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