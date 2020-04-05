package com.DarkBlue.Player;

import com.DarkBlue.Piece.Piece;
import com.DarkBlue.Piece.King;
import com.DarkBlue.Piece.Knight;
import com.DarkBlue.Piece.Rook;
import com.DarkBlue.Piece.Queen;
import com.DarkBlue.Piece.Bishop;
import com.DarkBlue.Piece.Pawn;
import com.DarkBlue.Move.Move;
import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Utilities.MoveEvaluation;
import com.DarkBlue.Board.Board;

import java.util.ArrayList;

/*
 * This class represents a player. It contains a color,
 * a king, a list of active pieces, a list of captured pieces, and a list of all current legal moves.
 */
public abstract class Player{
    
    // The player's side, i.e. white or black, which does not change at all after initialized.
    protected final ChessColor m_color;

    // The one and only king that is placed on the central spot opposite the queen
    protected King m_king;
    
    // How many active pieces the player has on the board, which starts at 16
    protected final ArrayList<Piece> m_activePieces;
    
    // The pieces this player has captured
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
    public Player(final ChessColor a_color, final Board a_board){
        this.m_color = a_color;
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

        // Initialize ArrayLists
        this.m_allCurrentLegalMoves = new ArrayList<>();
        this.m_activePieces = new ArrayList<>();      
        this.m_capturedPieces = new ArrayList<>(a_player.GetCapturedPieces());

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
    	try{
    		this.InitializePieces(a_board);
        
    		// Initialize the legal moves of every piece
    		if(!this.m_activePieces.isEmpty()){
    			this.InitializeCurrentLegalMoves(a_board);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    /**/
    /*
    NAME
        public final int HowManyMoves();
    
    SYNOPSIS
        public final int HowManyMoves();
    
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
    
    /**/
    /*
    NAME
        public final int CapturedPawns();
    
    SYNOPSIS
        public final int CapturedPawns();
    
        No parameters.
    
    DESCRIPTION
        This method returns how many enemy pawns the player has captured.
    
    RETURNS
        int total: The total number of enemy pawns this player has captured.
    
    AUTHOR
        Ryan King
    */
    public final int CapturedPawns(){
        int total = Utilities.ZERO;
        
        for(final Piece piece : this.GetCapturedPieces()){
            if(piece.IsPawn()){
                total++;
            }
        }
        
        return total;
    }
    
    /**/
    /*
    NAME
        public final int CapturedRooks();
    
    SYNOPSIS
        public final int CapturedRooks();
    
        No parameters.
    
    DESCRIPTION
        This method returns how many enemy rooks the player has captured.
    
    RETURNS
        int total: The total number of enemy rooks this player has captured.
    
    AUTHOR
        Ryan King
    */
    public final int CapturedRooks(){
        int total = Utilities.ZERO;
        
        for(final Piece piece : this.GetCapturedPieces()){
            if(piece.IsRook()){
                total++;
            }
        }
        
        return total;
    }
    
    /**/
    /*
    NAME
        public final int CapturedKnights();
    
    SYNOPSIS
        public final int CapturedKnights();
    
        No parameters.
    
    DESCRIPTION
        This method returns how many enemy knights the player has captured.
    
    RETURNS
        int total: The total number of enemy knights this player has captured.
    
    AUTHOR
        Ryan King
    */
    public final int CapturedKnights(){
        int total = Utilities.ZERO;
        
        for(final Piece piece : this.GetCapturedPieces()){
            if(piece.IsKnight()){
                total++;
            }
        }
        
        return total;
    }
    
    /**/
    /*
    NAME
        public final int CapturedBishops();
    
    SYNOPSIS
        public final int CapturedBishops();
    
        No parameters.
    
    DESCRIPTION
        This method returns how many enemy bishops the player has captured.
    
    RETURNS
        int total: The total number of enemy bishops this player has captured.
    
    AUTHOR
        Ryan King
    */
    public final int CapturedBishops(){
        int total = Utilities.ZERO;
        
        for(final Piece piece : this.GetCapturedPieces()){
            if(piece.IsBishop()){
                total++;
            }
        }
        
        return total;
    }
    
    /**/
    /*
    NAME
        public final int CapturedQueens();
    
    SYNOPSIS
        public final int CapturedQueens();
    
        No parameters.
    
    DESCRIPTION
        This method returns how many enemy queens the player has captured.
    
    RETURNS
        int total: The total number of enemy queens this player has captured.
    
    AUTHOR
        Ryan King
    */
    public final int CapturedQueens(){
        int total = Utilities.ZERO;
        
        for(final Piece piece : this.GetCapturedPieces()){
            if(piece.IsQueen()){
                total++;
            }
        }
        
        return total;
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
        // Clear out the pieces to avoid errors
        this.m_activePieces.clear();
        
        // Look through every spot on the board
        for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){
            int row = index / Utilities.EIGHT;
            int column = index % Utilities.EIGHT;
            if(a_board.GetTile(row, column).IsEmpty()){
                // Do not bother collecting a piece if the tile is empty
                continue;
            }else if(a_board.GetTile(row, column).GetPiece().GetColor().IsEnemy(this.GetColor())){
                // Do not bother collecting an enemy piece
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
        public void AddCapturedPiece(final Piece a_piece);
    
    SYNOPSIS
        public void AddCapturedPiece(final Piece a_piece);
    
        Piece a_piece -------> The piece to be added.
    
    DESCRIPTION
        This method adds a new captured Piece to the Player
        by specifying which Piece needs to be added.
    
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
        public void RemoveCapturedPiece(final Piece a_piece);
    
    SYNOPSIS
        public void RemoveCapturedPiece(final Piece a_piece);
    
        Piece a_piece -------> The piece to be removed.
    
    DESCRIPTION
        This method removes a player's captured Piece
        by specifying which Piece needs to be removed.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public final void RemoveCapturedPiece(final Piece a_piece){
        this.m_capturedPieces.remove(a_piece);
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
        public ChessColor GetCapturedPieces();
    
    SYNOPSIS
        public ChessColor GetCapturedPieces();
    
        No parameters
    
    DESCRIPTION
        This method returns the player's captured piece ArrayList.
    
    RETURNS
        ArrayList<Piece> m_capturedPieces.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Piece> GetCapturedPieces(){
        return this.m_capturedPieces;
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
        public abstract PlayerType GetPlayerType();
    
    SYNOPSIS
        public abstract PlayerType GetPlayerType();
    
        No parameters.
    
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
                && ((this.m_activePieces.get(Utilities.ZERO).IsKing() && this.m_activePieces.get(Utilities.ONE).IsKnight())
                || (this.m_activePieces.get(Utilities.ONE).IsKing() && this.m_activePieces.get(Utilities.ZERO).IsKnight()));
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
                && ((this.m_activePieces.get(Utilities.ZERO).IsKing() && this.m_activePieces.get(Utilities.ONE).IsBishop())
                || (this.m_activePieces.get(Utilities.ONE).IsKing() && this.m_activePieces.get(Utilities.ZERO).IsBishop()));
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
        public final boolean IsInStalemate(final Board a_board);
    
    SYNOPSIS
        public final boolean IsInStalemate(final Board a_board);
    
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
        public final Pawn GetPromotedPawn(final Board a_board);
    
    SYNOPSIS
        public final Pawn GetPromotedPawn(final Board a_board);
    
        Board a_board ----------> The board to examine.
    
    DESCRIPTION
        This method searches for a pawn on its last rank (0 for white, 7 for black) and returns it.
        If it does not find such a pawn, it returns null.
        
    RETURNS
        Pawn: A pawn on its last rank or null otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final Pawn GetPromotedPawn(final Board a_board){
    	final int row = (this.IsWhite() ? Utilities.ZERO : Utilities.SEVEN);
    	
    	for(int column = Utilities.ZERO; column < Utilities.EIGHT; column++){
    		if(a_board.GetTile(row, column).IsOccupied() && a_board.GetTile(row, column).GetPiece().IsPawn() && a_board.GetTile(row, column).GetPiece().GetColor() == this.GetColor()){
    			return (Pawn) a_board.GetTile(row, column).GetPiece();
    		}
    	}
    	
    	return null;
    }
    
    /**/
    /*
    NAME
        public final boolean HasCastled();
    
    SYNOPSIS
        public final boolean HasCastled();
    
        No parameters.
    
    DESCRIPTION
        This method determines if a player has castled by looking at the fields of its king.
    
    RETURNS
        boolean: True if the player has castled and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean HasCastled(){
    	return !this.GetKing().CanKingsideCastle() && !this.GetKing().CanQueensideCastle();
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
        // Make a new list to hold the moves
        final ArrayList<Move> uglyMoves = new ArrayList<>();
        
        for(final Piece piece : this.m_activePieces){
            // Get every move
            uglyMoves.addAll(piece.GetCurrentLegalMoves());
        }
        return uglyMoves;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> AttackingMoves();
    
    SYNOPSIS
        public final ArrayList<Move> AttackingMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns all attacking moves
        the player can make with his/her/its pieces.
        This is primarily used to evaluate the optimal move for the AI.
    
    RETURNS
        ArrayList<Move> attackingMoves: All attacking moves for this player on the current turn.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> AttackingMoves(){
        // Make a new list to hold the moves
        final ArrayList<Move> attackingMoves = new ArrayList<>();
        
        for(final Piece piece : this.m_activePieces){
            
            // Find every possible move
            for(final Move move : piece.GetCurrentLegalMoves()){
                if(move.IsAttacking()){
                    attackingMoves.add(move);
                }
            }
        }
        return attackingMoves;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> CastlingMoves();
    
    SYNOPSIS
        public final ArrayList<Move> CastlingMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns all castling moves
        the player can make with his/her/its pieces.
        This is primarily used to evaluate the optimal move for the AI.
    
    RETURNS
        ArrayList<Move> castlingMoves: All castling moves for this player on the current turn.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> CastlingMoves(){
        // Make a new list to hold the moves
        final ArrayList<Move> castlingMoves = new ArrayList<>();

        for(final Piece piece : this.m_activePieces){
            // Skip all pieces except kings
        	if(!piece.IsKing()){
        		continue;
        	}
        	
        	// Find every possible move
        	for(final Move move : piece.GetCurrentLegalMoves()){
        	    if(move.IsCastling()){
        	        castlingMoves.add(move);
        	    }
           }
        }
        return castlingMoves;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> RegularMoves();
    
    SYNOPSIS
        public final ArrayList<Move> RegularMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns all regular moves
        the player can make with his/her/its pieces.
        This is primarily used to evaluate the optimal move for the AI.
    
    RETURNS
        ArrayList<Move> castlingMoves: All castling moves for this player on the current turn.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> RegularMoves(){
        // Make a new list to hold the moves
        final ArrayList<Move> regularMoves = new ArrayList<>();
        for(final Piece piece : this.m_activePieces){
            
            // Find every possible move
            for(final Move move : piece.GetCurrentLegalMoves()){
                if(move.IsRegular()){
                    regularMoves.add(move);
                }
            }
        }
        return regularMoves;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> CheckMoves();
    
    SYNOPSIS
        public final ArrayList<Move> CheckMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns all moves
        that threaten the opponent's king with check.
        This is primarily used to evaluate the optimal move for the AI.
    
    RETURNS
        ArrayList<Move> checkMoves: All moves that put the opponent's king into check.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> CheckMoves(){
        // Make a new list to hold the moves
        final ArrayList<Move> checkMoves = new ArrayList<>();
        
        // Find every possible move
        for(final Piece piece : this.m_activePieces){
            
            // Only add the move if it is of the proper type
            for(final Move move : piece.GetCurrentLegalMoves()){
                if(move.PlacesOpponentIntoCheck()){
                    checkMoves.add(move);
                }
            }
        }
        return checkMoves;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> CheckmateMoves();
    
    SYNOPSIS
        public final ArrayList<Move> CheckmateMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns all moves
        that place the opponent's king into checkmate.
        This is primarily used to evaluate the optimal move for the AI.
    
    RETURNS
        ArrayList<Move> checkmateMoves: All moves that place the opponent's king into checkmate.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> CheckmateMoves(){
        // Make a new list to hold the moves
        final ArrayList<Move> checkmateMoves = new ArrayList<>();
        
        // Find every possible move
        for(final Piece piece : this.m_activePieces){
           
            // Only add the move if it is of the proper type
            for(final Move move : piece.GetCurrentLegalMoves()){
                if(move.PlacesOpponentIntoCheckmate()){
                    checkmateMoves.add(move);
                }
            }
        }
        return checkmateMoves;
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> EnPassantMoves();
    
    SYNOPSIS
        public final ArrayList<Move> EnPassantMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns all en passant moves.
        This is primarily used to evaluate the optimal move for the AI.
    
    RETURNS
        ArrayList<Move> enPassantMoves: All en passant moves.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> EnPassantMoves(){
        // Make a new list to hold the moves
        final ArrayList<Move> enPassantMoves = new ArrayList<>();
        
        // Find every possible move
        for(final Piece piece : this.m_activePieces){
            // Skip all pieces except pawns
        	if(!piece.IsPawn()){
        		continue;
        	}
        	
        	// Find all possible moves
        	for(final Move move : piece.GetCurrentLegalMoves()){
        	    
        	    // Only add the move if it is of the proper type
        		if(move.IsEnPassant()){
        			enPassantMoves.add(move);
        		}
        	}
        }
        return enPassantMoves;
    }
}