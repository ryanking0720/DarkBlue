package com.DarkBlue.Player;

import com.DarkBlue.Piece.*;
import com.DarkBlue.Move.*;
import com.DarkBlue.Utilities.*;
import com.DarkBlue.Board.*;

import java.util.ArrayList;

public abstract class Player{
    
    // The player's side, i.e. white or black, which does not change at all after initialized.
    protected final ChessColor m_color;
    
    // The type of player, i.e. human or computer
    protected final PlayerType m_type;
    
    // The one and only king that is placed on the central spot opposite the queen
    protected King m_king;
    
    // How many active pieces the player has on the board, which starts at 16
    protected final ArrayList<Piece> m_activePieces;
    
    // How many of the opposing player's pieces this player has captured, which starts at 0
    protected final ArrayList<Piece> m_capturedPieces;
    
    // All the legal moves the player can make on the current turn
    protected final ArrayList<Move> m_allCurrentLegalMoves;

    /**/
    /*
    NAME
        public Player(final ChessColor a_color, final Board a_board, final PlayerType a_type);
    
    SYNOPSIS
        public Player(final ChessColor a_color, final Board a_board, final PlayerType a_type);
        
        ChessColor a_color -----> The player's color, i.e. black or white.
    
        Board a_board ----------> The chessboard.
        
        PlayerType a_type ------> The player's type, i.e. human or computer.
    
    DESCRIPTION
        This constructor initializes a player's initial
        pieces, side, and type.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Player(final ChessColor a_color, final Board a_board, final PlayerType a_type){
        this.m_color = a_color;
        this.m_type = a_type;
        this.m_activePieces = new ArrayList<>();
        this.m_capturedPieces = new ArrayList<>();
        this.m_allCurrentLegalMoves = new ArrayList<>();
    }
    /**/
    /*
    NAME
        public Player(final Player a_player, final Board a_board);
    
    SYNOPSIS
        public Player(final Player a_player, final Board a_board);
    
        Player a_player ---------> The player to be copied.
        
        Board a_board -----------> The board the pieces will be taken from.
    
    DESCRIPTION
        This copy constructor creates a new Player object that will
        have duplicate captured pieces from a_player.
        The new Player's pieces depend on which pieces are on a_board.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Player(final Player a_player, final Board a_board){
        // Copy over basic fields
        this.m_color = a_player.GetColor();
        this.m_type = a_player.GetType();
        
        // Initialize ArrayLists
        this.m_allCurrentLegalMoves = new ArrayList<>();
        this.m_activePieces = new ArrayList<>();        
        this.m_capturedPieces = new ArrayList<>();
        
        // Copy the old captured Piece ArrayList
        for(int index = Utilities.ZERO; index < a_player.GetCapturedPieces().size(); index++){
            this.m_capturedPieces.add(a_player.GetCapturedPieces().get(index));
        }
        
        // Initialize the Pieces and Moves according to the Board argument
        this.Refresh(a_board);
    }
    
    /**/
    /*
    NAME
        public void Refresh(final Board a_board);
    
    SYNOPSIS
        public void Refresh(final Board a_board);
    
        Board a_board --------> The board to be evaluated.
    
    DESCRIPTION
        This method calls both the Player.InitializePieces(Board)
        and Player.InitializeCurrentLegalMoves(Board) methods
        in order to refresh the current pieces this player has,
        as well as the legal moves they possess on this new turn.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public final void Refresh(final Board a_board){
        // Find the player's pieces on the board
        this.InitializePieces(a_board);
        
        // Initialize the legal moves of every piece
        this.InitializeCurrentLegalMoves(a_board);
    }
    
    /**/
    /*
    NAME
        public int HowManyMoves();
    
    SYNOPSIS
        public int HowManyMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns how many moves
        this player has by counting up all of the
        sizes of legal move arrays of each piece s/he has.
    
    RETURNS
        int total: The total number of legal moves this player has.
    
    AUTHOR
        Ryan King
    */
    public final int HowManyMoves(){
        // Initialize the total
        int total = Utilities.ZERO;
        
        // Add up the number of moves every piece has
        for(int index = Utilities.ZERO; index < m_activePieces.size(); index++){
            total += this.m_activePieces.get(index).GetCurrentLegalMoves().size();
        }
        
        // Return this grand total
        return total;
    }
    
    public final boolean HasLost(final Board a_board){
    	return this.IsInCheckmate(a_board) || this.IsInStalemate(a_board);
    }
    
    /**/
    /*
    NAME
        private void InitializePieces(final Board a_board);
    
    SYNOPSIS
        private void InitializePieces(final Board a_board);
    
        Board a_board ------> The chessboard.
    
    DESCRIPTION
        This method initializes all of one side's Pieces
        at the start of the game and links them to the ones
        that are placed on the Board.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public final void InitializePieces(final Board a_board){
        this.m_activePieces.clear();
        for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){
            int row = index / Utilities.EIGHT;
            int column = index % Utilities.EIGHT;
            if(a_board.GetTile(row, column).IsEmpty()){
                // Do not bother collecting a piece if the tile is empty
                continue;
            }else if(a_board.GetTile(row, column).GetPiece().GetColor().IsEnemy(this.GetColor())){
                continue;
            }else if(a_board.GetTile(row, column).GetPiece().IsKing() && a_board.GetTile(row, column).GetPiece().GetColor() == this.GetColor()){
                // Assign the player's one and only king
                this.m_king = (King) a_board.GetTile(row, column).GetPiece();
                this.AddActivePiece(this.m_king);
            }else if(a_board.GetTile(row, column).GetPiece().IsQueen() && a_board.GetTile(row, column).GetPiece().GetColor() == this.GetColor()){
                // Add a new queen
                Queen next = (Queen) a_board.GetTile(row, column).GetPiece();
                this.AddActivePiece(next);
            }else if(a_board.GetTile(row, column).GetPiece().IsRook() && a_board.GetTile(row, column).GetPiece().GetColor() == this.GetColor()){
                // Add a new rook
                Rook next = (Rook) a_board.GetTile(row, column).GetPiece();
                this.AddActivePiece(next);
            }else if(a_board.GetTile(row, column).GetPiece().IsKnight() && a_board.GetTile(row, column).GetPiece().GetColor() == this.GetColor()){
                // Add a new knight
                Knight next = (Knight) a_board.GetTile(row, column).GetPiece();
                this.AddActivePiece(next);
            }else if(a_board.GetTile(row, column).GetPiece().IsBishop() && a_board.GetTile(row, column).GetPiece().GetColor() == this.GetColor()){
                // Add a new bishop
                Bishop next = (Bishop) a_board.GetTile(row, column).GetPiece();
                this.AddActivePiece(next);
            }else if(a_board.GetTile(row, column).GetPiece().IsPawn() && a_board.GetTile(row, column).GetPiece().GetColor() == this.GetColor()){
                // Add a new pawn
                Pawn next = (Pawn) a_board.GetTile(row, column).GetPiece();
                this.AddActivePiece(next);
            }
        }
    }
    
    /**/
    /*
    NAME
        public void AddActivePiece(final Piece a_piece);
    
    SYNOPSIS
        public void AddActivePiece(final Piece a_piece);
    
        Piece a_piece -------> The piece to be added.
    
    DESCRIPTION
        This method adds a new active Piece to the Player
        by specifying which Piece needs to be added.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public final void AddActivePiece(final Piece a_piece){
        this.m_activePieces.add(a_piece);
    }
    
    /**/
    /*
    NAME
        public void RemoveActivePiece(final Piece a_piece);
    
    SYNOPSIS
        public void RemoveActivePiece(final Piece a_piece);
    
        Piece a_piece -------> The piece to be removed.
    
    DESCRIPTION
        This method removes a player's active Piece
        by specifying which Piece needs to be removed.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public final void RemoveActivePiece(final Piece a_piece){
        this.m_activePieces.remove(a_piece);
    }
    
    /**/
    /*
    NAME
        public void InitializeCurrentLegalMoves(final Board a_board);
    
    SYNOPSIS
        public void InitializeCurrentLegalMoves(final Board a_board);
    
        Board a_board ---------> The current board.
    
    DESCRIPTION
        This method initializes all the currently legal moves
        of every active piece by calling each of their
        AddCurrentLegalMoves(Board) methods in a for loop.    
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public final void InitializeCurrentLegalMoves(final Board a_board){
        this.m_allCurrentLegalMoves.clear();
        for(int index = Utilities.ZERO; index < m_activePieces.size(); index++){
            Piece piece = this.m_activePieces.get(index);
            piece.AddCurrentLegalMoves(a_board);
            this.m_allCurrentLegalMoves.addAll(piece.GetCurrentLegalMoves());
        }
    }
    
    /**/
    /*
    NAME
        public void AddCapturedPiece(final Piece a_piece);
    
    SYNOPSIS
        public void AddCapturedPiece(final Piece a_piece);
    
        final Piece a_piece ------> The piece that just got captured.
    
    DESCRIPTION
        This method adds an opposing piece to the player's
        captured piece ArrayList once it gets captured.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public final void AddCapturedPiece(final Piece a_piece){
        this.m_capturedPieces.add(a_piece);
    }
    
    /**/
    /*
    NAME
        public ChessColor GetColor();
    
    SYNOPSIS
        public ChessColor GetColor();
    
        No parameters
    
    DESCRIPTION
        This method returns which color the player is,
        i.e. white or black.
    
    RETURNS
        ChessColor.WHITE or ChessColor.BLACK.
    
    AUTHOR
        Ryan King
    */
    public final ChessColor GetColor(){
        return this.m_color;
    }
    
    /**/
    /*
    NAME
        public PlayerType GetType();
    
    SYNOPSIS
        public PlayerType GetType();
    
        No parameters
    
    DESCRIPTION
        This method returns which type the player is,
        i.e. human or computer.
    
    RETURNS
        PlayerType.HUMAN or PlayerType.COMPUTER.
    
    AUTHOR
        Ryan King
    */
    public final PlayerType GetType(){
        return this.m_type;
    }
    
    /**/
    /*
    NAME
        public abstract boolean IsHuman();
    
    SYNOPSIS
        public abstract boolean IsHuman();
    
        No parameters.
    
    DESCRIPTION
        This method returns if this player's type is human.
    
    RETURNS
        True if the player is human, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public abstract boolean IsHuman();
    
    /**/
    /*
    NAME
        public abstract boolean IsComputer();
    
    SYNOPSIS
        public abstract boolean IsComputer();
    
        No parameters.
    
    DESCRIPTION
        This method returns if this player's type is computer.
    
    RETURNS
        True if the player is a computer, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public abstract boolean IsComputer();
    
    /**/
    /*
    NAME
        public boolean IsWhite();
    
    SYNOPSIS
        public boolean IsWhite();
    
        No parameters.
    
    DESCRIPTION
        This method returns if this player's color is white.
    
    RETURNS
        True if the player is white, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsWhite(){
        return this.m_color == ChessColor.WHITE;
    }
    
    /**/
    /*
    NAME
        public boolean IsBlack();
    
    SYNOPSIS
        public boolean IsBlack();
    
        No parameters.
    
    DESCRIPTION
        This method returns if this player's color is black.
    
    RETURNS
        True if the player is black, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsBlack(){
        return this.m_color == ChessColor.BLACK;
    }
    
    /**/
    /*
    NAME
        public King GetKing();
    
    SYNOPSIS
        public King GetKing();
    
        No parameters.
    
    DESCRIPTION
        This method returns the player's king.
    
    RETURNS
        King m_king: The player's king.
    
    AUTHOR
        Ryan King
    */    
    public final King GetKing(){
        return this.m_king;
    }
    
    /**/
    /*
    NAME
        public boolean HasBareKing();
    
    SYNOPSIS
        public boolean HasBareKing();
    
        No parameters.
    
    DESCRIPTION
        This method determines if a player
        has a bare king, i.e. this is the only
        piece s/he has left on the board, since
        the king cannot be captured under normal rules.
    
    RETURNS
        true if the player has only his/her king left, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean HasBareKing(){
        return this.m_activePieces.size() == Utilities.ONE && this.m_activePieces.get(Utilities.ZERO).IsKing();
    }
    
    /**/
    /*
    NAME
        public boolean HasKingAndKnight();
    
    SYNOPSIS
        public boolean HasKingAndKnight();
    
        No parameters.
    
    DESCRIPTION
        This method determines if a player
        has only a king and a knight, since this
        combination of pieces will lead to a draw.
    
    RETURNS
        true if the player has only his/her king and a knight left, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean HasKingAndKnight(){
        return this.m_activePieces.size() == Utilities.TWO
                && (this.m_activePieces.get(Utilities.ZERO).IsKing() && this.m_activePieces.get(Utilities.ONE).IsKnight())
                || (this.m_activePieces.get(Utilities.ONE).IsKing() && this.m_activePieces.get(Utilities.ZERO).IsKnight());
    }
    
    /**/
    /*
    NAME
        public boolean HasKingAndBishop();
    
    SYNOPSIS
        public boolean HasKingAndBishop();
    
        No parameters.
    
    DESCRIPTION
        This method determines if a player
        has only a king and a bishop, since this
        combination of pieces will lead to a draw.
    
    RETURNS
        true if the player has only his/her king and a bishop left, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    public final boolean HasKingAndBishop(){
        return this.m_activePieces.size() == Utilities.TWO
                && (this.m_activePieces.get(Utilities.ZERO).IsKing() && this.m_activePieces.get(Utilities.ONE).IsBishop())
                || (this.m_activePieces.get(Utilities.ONE).IsKing() && this.m_activePieces.get(Utilities.ZERO).IsBishop());
    }
    
    /**/
    /*
    NAME
        public final boolean IsInCheck(final Board a_board);
    
    SYNOPSIS
        public final boolean IsInCheck(final Board a_board);
    
        Board a_board ------> The current board.
    
    DESCRIPTION
        This method determines if the player's king is in check,
        i.e. if he is threatened by an enemy piece but has at least
        one legal move he can use to escape, capture the opposing piece,
        or use another piece to capture or block the threatening piece.
        The player who is in check has no choice but to remove the threat.
        The game continues normally after the player does so.
    
    RETURNS
        True if the player is in check, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsInCheck(final Board a_board){
        return !MoveEvaluation.IsKingSafe(a_board, this.GetKing().GetCurrentRow(), this.GetKing().GetCurrentColumn(), this.GetColor()) && this.HowManyMoves() > Utilities.ZERO;
    }
    /**/
    /*
    NAME
        public final boolean CanKingsideCastle(final Board a_board);
    
    SYNOPSIS
        public final boolean CanKingsideCastle(final Board a_board);
    
        Board a_board ------> The current board.
    
    DESCRIPTION
        This method determines if the player's king can castle on his own side,
        i.e., the right side for white, or the left side for black.
    
    RETURNS
        True if the player can perform a kingside castle, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    public final boolean CanKingsideCastle(final Board a_board){
    	final int kingRow = (this.IsWhite() ? Utilities.SEVEN : Utilities.ZERO);
    	final int kingColumn = Utilities.FOUR;
    	
    	final int rookRow = kingRow;
    	final int rookColumn = Utilities.SEVEN;
    	
    	final Piece potentialKing = a_board.GetTile(kingRow, kingColumn).GetPiece();
    	final Piece potentialRook = a_board.GetTile(rookRow, rookColumn).GetPiece();
    	
    	return this.HasPotentialCastlingKing(potentialKing) && this.HasPotentialCastlingRook(potentialRook);
    }
    
    /**/
    /*
    NAME
        public final boolean CanQueensideCastle(final Board a_board);
    
    SYNOPSIS
        public final boolean CanQueensideCastle(final Board a_board);
    
        Board a_board ------> The current board.
    
    DESCRIPTION
        This method determines if the player's king can castle on the side opposite his own,
        i.e., the left side for white, or the right side for black.
    
    RETURNS
        True if the player can perform a queenside castle, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    public final boolean CanQueensideCastle(final Board a_board){
    	final int kingRow = (this.IsWhite() ? Utilities.SEVEN : Utilities.ZERO);
    	final int kingColumn = Utilities.FOUR;
    	
    	final int rookRow = kingRow;
    	final int rookColumn = Utilities.ZERO;
    	
    	final Piece potentialKing = a_board.GetTile(kingRow, kingColumn).GetPiece();
    	final Piece potentialRook = a_board.GetTile(rookRow, rookColumn).GetPiece();
    	
    	return this.HasPotentialCastlingKing(potentialKing) && this.HasPotentialCastlingRook(potentialRook);
    }
    
    /**/
    /*
    NAME
        private final boolean HasPotentialCastlingKing(final Piece a_piece);
    
    SYNOPSIS
        private final boolean HasPotentialCastlingKing(final Piece a_piece);
    
        Board a_board ------> The player whose turn it is.
    
    DESCRIPTION
        This method determines if the player's king can castle,
        which is true if the king is in either spot (7, 4) for white,
        or spot (0, 4) for black and has not moved yet.
    
    RETURNS
        True if the player's king can potentially castle, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    private final boolean HasPotentialCastlingKing(final Piece a_piece){
    	return a_piece != null && a_piece.GetColor().IsAlly(this.GetColor()) && a_piece.IsKing() && !a_piece.HasMoved();
    }
    
    /**/
    /*
    NAME
        private final boolean HasPotentialCastlingRook(final Piece a_piece);
    
    SYNOPSIS
        private final boolean HasPotentialCastlingRook(final Piece a_piece);
    
        Board a_board ------> The player whose turn it is.
    
    DESCRIPTION
        This method determines if the player's rook can castle,
        which is true if the rook is in either spot (7, 7) or (0, 7) for white,
        or spots (0, 0) or (7, 0) for black and has not moved yet.
    
    RETURNS
        True if the player's rook can potentially castle, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    private final boolean HasPotentialCastlingRook(final Piece a_piece){
    	return a_piece != null && a_piece.GetColor().IsAlly(this.GetColor()) && a_piece.IsRook() && !a_piece.HasMoved();
    }
    
    /**/
    /*
    NAME
        public static boolean IsInCheckmate(final Board a_board);
    
    SYNOPSIS
        public static boolean IsInCheckmate(final Board a_board);
    
        Board a_board ------> The player whose turn it is.
    
    DESCRIPTION
        This method determines if the player's king is in checkmate,
        i.e. if he is threatened by one or more enemy pieces and has
        no legal moves he can use to escape, capture the opposing piece,
         or use another piece to capture or block the threatening piece. 
         The game is over and the opposite player wins.
    
    RETURNS
        True if the player is in checkmate, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsInCheckmate(final Board a_board){
        return !MoveEvaluation.IsKingSafe(a_board, this.GetKing().GetCurrentRow(), this.GetKing().GetCurrentColumn(), this.GetColor()) && this.HowManyMoves() == Utilities.ZERO;
    }
    
    /**/
    /*
    NAME
        public static boolean IsInStalemate(final Board a_board);
    
    SYNOPSIS
        public static boolean IsInStalemate(final Board a_board);
    
        Board a_board ------> The player whose turn it is.
    
    DESCRIPTION
        This method determines if the player's king is in stalemate,
        i.e. his/her king is not currently in check but the player has
        no legal move to make with the king nor any other piece. 
        The game is over and declared a draw.
    
    RETURNS
        True if the player is in stalemate, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsInStalemate(final Board a_board){
        return MoveEvaluation.IsKingSafe(a_board, this.GetKing().GetCurrentRow(), this.GetKing().GetCurrentColumn(), this.GetColor()) && this.HowManyMoves() == Utilities.ZERO;
    }
    
    public final int PieceValue(){
    	int total = Utilities.ZERO;
    	
    	for(Piece piece : this.m_activePieces){
    		total += piece.GetValue();
    	}
    	
    	return total;
    }
    
    public final boolean HasCastled(final String a_moveHistory){
    	if(a_moveHistory.contains(Utilities.KINGSIDE_CASTLE) || a_moveHistory.contains(Utilities.QUEENSIDE_CASTLE)){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    /**/
    /*
    NAME
        public PlayerType GetPlayerType();
    
    SYNOPSIS
        public PlayerType GetPlayerType();
    
        No parameters
    
    DESCRIPTION
        This method returns which type of player
        this player is, i.e. a human or computer.
    
    RETURNS
        PlayerType.HUMAN if human, or PlayerType.COMPUTER
        if the player is an AI.
        One of these two options will always return.
    
    AUTHOR
        Ryan King
    */
    public abstract PlayerType GetPlayerType();
    
    /**/
    /*
    NAME
        public ArrayList<Piece> GetActivePieces();
    
    SYNOPSIS
        public ArrayList<Piece> GetActivePieces();
    
        No parameters.
    
    DESCRIPTION
        This method returns the ArrayList of a player's
        currently active pieces, i.e. the ones that have
        not been captured, even if they have no legal moves.
        
    RETURNS
        An ArrayList<Piece> of a player's active pieces.
    
    AUTHOR
        Ryan King
    */
    public ArrayList<Piece> GetActivePieces(){
        return this.m_activePieces;
    }
    
    /**/
    /*
    NAME
        public ArrayList<Piece> GetCapturedPieces();
    
    SYNOPSIS
        public ArrayList<Piece> GetCapturedPieces();
    
        No parameters.
    
    DESCRIPTION
        This method returns the ArrayList of a player's
        captured pieces, i.e. the ones that s/he has
        captured from the opposing player.
        
    RETURNS
        An ArrayList<Piece> of a player's captured pieces.
    
    AUTHOR
        Ryan King
    */
    public ArrayList<Piece> GetCapturedPieces(){
        return this.m_capturedPieces;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> UglyMoves();
    
    SYNOPSIS
        public final ArrayList<Move> UglyMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns all possible moves
        the player can make with his/her/its pieces.
        This is primarily used to evaluate the optimal move for the AI.
    
    RETURNS
        ArrayList<Move> uglyMoves: All possible moves for this player on the current turn.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> UglyMoves(){
        final ArrayList<Move> uglyMoves = new ArrayList<>();
        for(final Piece piece : this.m_activePieces){
            uglyMoves.addAll(piece.GetCurrentLegalMoves());
        }
        return uglyMoves;
    }
}