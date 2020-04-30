package com.DarkBlue.Board;

import com.DarkBlue.Player.Player;
import com.DarkBlue.Piece.Piece;
import com.DarkBlue.Piece.Pawn;
import com.DarkBlue.Piece.Rook;
import com.DarkBlue.Piece.Knight;
import com.DarkBlue.Piece.Bishop;
import com.DarkBlue.Piece.Queen;
import com.DarkBlue.Piece.King;

import com.DarkBlue.GUI.DarkBlue;

import com.DarkBlue.Move.Move;
import com.DarkBlue.Move.RegularMove;
import com.DarkBlue.Move.AttackingMove;
import com.DarkBlue.Move.CastlingMove;
import com.DarkBlue.Move.EnPassantMove;

import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Utilities.BoardUtilities;
import com.DarkBlue.Utilities.Factory;
import com.DarkBlue.Utilities.ChessColor;

/**
 * This class represents a chessboard.
 * It contains 64 Tile objects which are assigned by an internal
 * class called BoardBuilder. The internal class is responsible
 * for assembling a new board after every move.
 * It then assigns its own 8-by-8 array of tiles to the corresponding
 * array in the Board object in its Build() method.
 * 
 * It also contains a flag representing whose turn it is,
 * as well as a lone Tile object representing a possible
 * en passant destination tile, if any.
 */
public final class Board{
    
    // The two-dimensional array of tiles where the chess pieces play
    private final Tile[][] m_boardObject;
    
    // Whose turn it is: White or black
    private final ChessColor m_whoseTurn;
    
    // The en passant tile, used for serialization
    private final Tile m_enPassantTile;
    
    public static final String WHITE_FILES = "  a b c d e f g h\n";
    public static final String BLACK_FILES = "  h g f e d c b a\n";
    
    /**/
    /*
    NAME
        private Board(final BoardBuilder a_builder);
    
    SYNOPSIS
        private Board(final BoardBuilder a_builder);
    
        BoardBuilder a_builder ------> The builder object that initializes the state of the Board.
    
    DESCRIPTION
        This constructor creates a new Board object.
        This is meant to be called at the beginning of the game or a test scenario.
        
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, with help taken from Amir Afghani,
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/board/Board.java
    */
    private Board(final BoardBuilder a_builder){
        // Set the board object
        this.m_boardObject = GetCurrentBoard(a_builder);
        
        // Set whose turn it is
        this.m_whoseTurn = a_builder.WhoseTurnIsIt();
        
        // Set the en passant tile, if any
        if(DarkBlue.GetEnPassantTile() != null){
            this.m_enPassantTile = this.m_boardObject[BoardUtilities.ToBoardRow(DarkBlue.GetEnPassantTile())][BoardUtilities.ToBoardColumn(DarkBlue.GetEnPassantTile())];
        }else{
            this.m_enPassantTile = null;
        }
    }
    
    /**/
    /*
    NAME
        private Board(final Board a_board);
    
    SYNOPSIS
        private Board(final Board a_board);
    
        Board a_board ------> The Board to be copied.
    
    DESCRIPTION
        This copy constructor creates a deep copy of a_board.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private Board(final Board a_board){
        // Initialize the array space
        this.m_boardObject = new Tile[Utilities.EIGHT][Utilities.EIGHT];
        
        // Initialize the turn flag
        this.m_whoseTurn = a_board.m_whoseTurn;
        
        // Make new tiles one at a time
        for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){    
            final int ROW = index / Utilities.EIGHT;
            final int COLUMN = index % Utilities.EIGHT;
            this.m_boardObject[ROW][COLUMN] = new Tile(a_board.m_boardObject[ROW][COLUMN]);
        }
        
        // Set the en passant tile if the engine has one
        if(DarkBlue.GetEnPassantTile() != null){
            this.m_enPassantTile = this.m_boardObject[BoardUtilities.ToBoardRow(DarkBlue.GetEnPassantTile())][BoardUtilities.ToBoardColumn(DarkBlue.GetEnPassantTile())];
        }else{
            this.m_enPassantTile = null;
        }
    }
    
    /**/
    /*
    NAME
        public final ChessColor WhoseTurnIsIt();
    
    SYNOPSIS
        public final ChessColor WhoseTurnIsIt();
    
        No parameters.
    
    DESCRIPTION
        This method returns whose turn it is.
    
    RETURNS
        ChessColor m_whoseTurn: Whose turn it is.
    
    AUTHOR
        Ryan King
    */
    public final ChessColor WhoseTurnIsIt(){
        return this.m_whoseTurn;
    }
    
    /**/
    /*
    NAME
        public final Tile[][] GetBoard();
    
    SYNOPSIS
        public final Tile[][] GetBoard();
    
        No parameters.
    
    DESCRIPTION
        This method returns the two-dimensional Tile array.
    
    RETURNS
        Tile[8][8] m_boardObject: The two-dimensional Tile array.
    
    AUTHOR
        Ryan King
    */
    public final Tile[][] GetBoard(){
        return this.m_boardObject;
    }
    
    /**/
    /*
    NAME
        public final int PieceCount();
    
    SYNOPSIS
        public final int PieceCount();
    
        No parameters.
    
    DESCRIPTION
        This method returns how many pieces are on the board.
    
    RETURNS
        int count: The number of pieces on the board.
    
    AUTHOR
        Ryan King
    */
    public final int PieceCount(){
        int count = Utilities.ZERO;
        
        // Look through every tile on the board for pieces
        for(int i = Utilities.ZERO; i < Utilities.SIXTY_FOUR; i++){
            final int ROW = i / Utilities.EIGHT;
            final int COLUMN = i % Utilities.EIGHT;
            
            if(this.m_boardObject[ROW][COLUMN].IsOccupied()){
                count++;
            }
        }
        
        return count;
    }
    
    /**/
    /*
    NAME
        public final Tile GetEnPassantTile();
    
    SYNOPSIS
        public final Tile GetEnPassantTile();
    
        No parameters.
    
    DESCRIPTION
        This method returns the en passant tile,
        or null if none got set.
    
    RETURNS
        Tile m_enPassantTile: The en passant tile.
    
    AUTHOR
        Ryan King
    */
    public final Tile GetEnPassantTile(){
        return this.m_enPassantTile;
    }
    
    /**/
    /*
    NAME
        private static final Tile[][] GetCurrentBoard(final BoardBuilder a_builder);
    
    SYNOPSIS
        private static final Tile[][] GetCurrentBoard(final BoardBuilder a_builder);
    
        final BoardBuilder a_builder ------> The Board generator.
    
    DESCRIPTION
        This method gives the Board array the current layout
        of the board object as of the most recent move.
    
    RETURNS
        Tile[8][8] of the current Board.
    
    AUTHOR
        Ryan King
    */
    private static final Tile[][] GetCurrentBoard(final BoardBuilder a_builder){
        // Make a new 8 by 8 array
        final Tile[][] BOARD_DUPLICATE = new Tile[Utilities.EIGHT][Utilities.EIGHT];
        
        // Place every tile one at a time
        for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){
            final int ROW = index / Utilities.EIGHT;
            final int COLUMN = index % Utilities.EIGHT;
            BOARD_DUPLICATE[ROW][COLUMN] = new Tile(a_builder.GetBuilderBoard()[ROW][COLUMN]);
        }
        
        // Return the array
        return BOARD_DUPLICATE;
    }
    
    /**/
    /*
    NAME
        public static final Board GetStartingPosition();
    
    SYNOPSIS
        public static final Board GetStartingPosition();
    
        No parameters.
    
    DESCRIPTION
        This method initializes the BoardBuilder
        and returns a copy to the Board object
        in the starting configuration of a game of chess.
        It also initializes the turn to white, 
        since white always goes first as per the rules of chess.
    
    RETURNS
        A Board object containing the starting position in chess
        with white oriented to what would be the bottom.
    
    AUTHOR
        Help taken from Amir Afghani
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/board/Board.java
    */
    public static final Board GetStartingPosition(){
        // Make a new builder
        final BoardBuilder BUILDER = new BoardBuilder();
        
        // Set all white Pieces
        BUILDER.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.ZERO));
        BUILDER.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.ONE));
        BUILDER.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.TWO));
        BUILDER.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.THREE));
        BUILDER.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.FOUR));
        BUILDER.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.FIVE));
        BUILDER.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.SIX));
        BUILDER.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.SEVEN));
        
        BUILDER.SetPiece(new Rook(ChessColor.WHITE, Utilities.SEVEN, Utilities.ZERO));
        BUILDER.SetPiece(new Knight(ChessColor.WHITE, Utilities.SEVEN, Utilities.ONE));
        BUILDER.SetPiece(new Bishop(ChessColor.WHITE, Utilities.SEVEN, Utilities.TWO));
        BUILDER.SetPiece(new Queen(ChessColor.WHITE, Utilities.SEVEN, Utilities.THREE));
        BUILDER.SetPiece(new King(ChessColor.WHITE, Utilities.SEVEN, Utilities.FOUR, true, true));
        BUILDER.SetPiece(new Bishop(ChessColor.WHITE, Utilities.SEVEN, Utilities.FIVE));
        BUILDER.SetPiece(new Knight(ChessColor.WHITE, Utilities.SEVEN, Utilities.SIX));
        BUILDER.SetPiece(new Rook(ChessColor.WHITE, Utilities.SEVEN, Utilities.SEVEN));
        
        // Set all black Pieces
        BUILDER.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.ZERO));
        BUILDER.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.ONE));
        BUILDER.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.TWO));
        BUILDER.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.THREE));
        BUILDER.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.FOUR));
        BUILDER.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.FIVE));
        BUILDER.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.SIX));
        BUILDER.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.SEVEN));
        
        BUILDER.SetPiece(new Rook(ChessColor.BLACK, Utilities.ZERO, Utilities.ZERO));
        BUILDER.SetPiece(new Knight(ChessColor.BLACK, Utilities.ZERO, Utilities.ONE));
        BUILDER.SetPiece(new Bishop(ChessColor.BLACK, Utilities.ZERO, Utilities.TWO));
        BUILDER.SetPiece(new Queen(ChessColor.BLACK, Utilities.ZERO, Utilities.THREE));
        BUILDER.SetPiece(new King(ChessColor.BLACK, Utilities.ZERO, Utilities.FOUR, true, true));
        BUILDER.SetPiece(new Bishop(ChessColor.BLACK, Utilities.ZERO, Utilities.FIVE));
        BUILDER.SetPiece(new Knight(ChessColor.BLACK, Utilities.ZERO, Utilities.SIX));
        BUILDER.SetPiece(new Rook(ChessColor.BLACK, Utilities.ZERO, Utilities.SEVEN));
        
        // Set the turn to white because white always goes first
        BUILDER.SetWhoseTurn(ChessColor.WHITE);
        
        // Return the newly-built board
        return BUILDER.Build();
    }
    
    /**/
    /*
    NAME
        public static final Board GetEmptyBoard();
    
    SYNOPSIS
        public static final Board GetEmptyBoard();
    
        No parameters.
    
    DESCRIPTION
        This method creates an empty board object with no pieces on it.
    
    RETURNS
        A new Board that is completely empty.
    
    AUTHOR
        Ryan King
    */
    public static final Board GetEmptyBoard(){
        final BoardBuilder BUILDER = new BoardBuilder();
        
        // Empty, because no pieces need to be added
        
        return BUILDER.Build();
    }
    
    /**/
    /*
    NAME
        public static final Board GetDeepCopy(final Board a_board);
    
    SYNOPSIS
        public static final Board GetDeepCopy(final Board a_board);
    
        Board a_board ------> The board to be copied.
    
    DESCRIPTION
        This method creates a deep copy of the argument
        Board object, meaning that all tiles and pieces
        are separate from those of the argument.
    
    RETURNS
        A new Board that is an exact copy of a_board.
        The two are completely separate.
    
    AUTHOR
        Ryan King
    */
    public static final Board GetDeepCopy(final Board a_board){
        return new Board(a_board);
    }
    
    /**/
    /*
    NAME
        public static final Board GetStalemateTest();
    
    SYNOPSIS
        public static final Board GetStalemateTest();
    
        No parameters.
    
    DESCRIPTION
        This method returns a test configuration for
        testing the status of stalemate.
    
    RETURNS
        A board in a predetermined configuration for test purposes.
    
    AUTHOR
        Ryan King
    */
    public static final Board GetStalemateTest(){
        // Initialize a new BoardBuilder
        final BoardBuilder BUILDER = new BoardBuilder();
        
        // Set the pieces
        BUILDER.SetPiece(new King(ChessColor.WHITE, Utilities.TWO, Utilities.THREE, false, false));
        BUILDER.SetPiece(new Pawn(ChessColor.WHITE, Utilities.TWO, Utilities.TWO));
        BUILDER.SetPiece(new King(ChessColor.BLACK, Utilities.ZERO, Utilities.THREE, false, false));
        BUILDER.SetPiece(new Knight(ChessColor.BLACK, Utilities.ONE, Utilities.THREE));
        
        // Set the turn to white
        BUILDER.SetWhoseTurn(ChessColor.WHITE);
        
        // Return the newly-built board
        return BUILDER.Build();
    }
    
    /**/
    /*
    NAME
        public static final Board GetCheckmateTest();
    
    SYNOPSIS
        public static final Board GetCheckmateTest();
    
        No parameters.
    
    DESCRIPTION
        This method returns a test configuration for
        testing the status of checkmate.
    
    RETURNS
        A board in a predetermined configuration for test purposes.
    
    AUTHOR
        Ryan King
    */
    public static final Board GetCheckmateTest(){
        // Initialize a new BoardBuilder
        final BoardBuilder BUILDER = new BoardBuilder();
        
        // Set the pieces
        BUILDER.SetPiece(new King(ChessColor.WHITE, Utilities.TWO, Utilities.THREE, false, false));
        BUILDER.SetPiece(new Queen(ChessColor.WHITE, Utilities.TWO, Utilities.TWO));
        BUILDER.SetPiece(new King(ChessColor.BLACK, Utilities.ZERO, Utilities.THREE, false, false));
        BUILDER.SetPiece(new Knight(ChessColor.BLACK, Utilities.ONE, Utilities.THREE));
        
        // Set the turn to white
        BUILDER.SetWhoseTurn(ChessColor.WHITE);
        
        // Return the newly-built board
        return BUILDER.Build();
    }
    
    /**/
    /*
    NAME
        public static final Board GetCastlingTest();
    
    SYNOPSIS
        public static final Board GetCastlingTest();
    
        No parameters.
    
    DESCRIPTION
        This method returns a test configuration for
        testing the castling of both kings.
    
    RETURNS
        A board in a predetermined configuration for test purposes.
    
    AUTHOR
        Ryan King
    */
    public static final Board GetCastlingTest(){
        // Initialize a new BoardBuilder
        final BoardBuilder BUILDER = new BoardBuilder();
        
        // Set the white pieces
        BUILDER.SetPiece(new King(ChessColor.WHITE, Utilities.SEVEN, Utilities.FOUR, true, true));
        BUILDER.SetPiece(new Rook(ChessColor.WHITE, Utilities.FIVE, Utilities.TWO));
        BUILDER.SetPiece(new Rook(ChessColor.WHITE, Utilities.FIVE, Utilities.FOUR));
       
        // Set the black pieces
        BUILDER.SetPiece(new King(ChessColor.BLACK, Utilities.ZERO, Utilities.FOUR, true, true));
        BUILDER.SetPiece(new Rook(ChessColor.BLACK, Utilities.ZERO, Utilities.SEVEN));
        BUILDER.SetPiece(new Rook(ChessColor.BLACK, Utilities.ZERO, Utilities.ZERO));
        
        // Set the turn to white
        BUILDER.SetWhoseTurn(ChessColor.BLACK);
        
        // Return the newly-built board
        return BUILDER.Build();
    }
    
    /**/
    /*
    NAME
        public static final Board GetPromotionTest();
    
    SYNOPSIS
        public static final Board GetPromotionTest();
    
        No parameters.
    
    DESCRIPTION
        This method returns a test configuration for
        testing pawn promotion.
    
    RETURNS
        A board in a predetermined configuration for test purposes.
    
    AUTHOR
        Ryan King
    */
    public static final Board GetPromotionTest(){
        // Initialize a new BoardBuilder
        final BoardBuilder BUILDER = new BoardBuilder();
        
        // Set the white pieces
        BUILDER.SetPiece(new Pawn(ChessColor.WHITE, Utilities.ONE, Utilities.ONE));
        BUILDER.SetPiece(new King(ChessColor.WHITE, Utilities.TWO, Utilities.THREE, false, false));
        
        // Set the black pieces
        BUILDER.SetPiece(new King(ChessColor.BLACK, Utilities.ZERO, Utilities.THREE, false, false));
        BUILDER.SetPiece(new Knight(ChessColor.BLACK, Utilities.ZERO, Utilities.ZERO));
        BUILDER.SetPiece(new Pawn(ChessColor.BLACK, Utilities.SIX, Utilities.SIX));
        
        // Set the turn to black
        BUILDER.SetWhoseTurn(ChessColor.BLACK);
        
        // Return the newly-built board
        return BUILDER.Build();
    }
    
    /**/
    /*
    NAME
        public static final Board GetEnPassantTest();
    
    SYNOPSIS
        public static final Board GetEnPassantTest();
    
        No parameters.
    
    DESCRIPTION
        This method returns a test configuration for
        testing en passant.
    
    RETURNS
        A board in a predetermined configuration for test purposes.
    
    AUTHOR
        Ryan King
    */
    public static final Board GetEnPassantTest(){
        // Make a new builder
        final BoardBuilder BUILDER = new BoardBuilder();
        
        // Set pieces for white
        BUILDER.SetPiece(new Pawn(ChessColor.WHITE, Utilities.THREE, Utilities.FOUR));
        BUILDER.SetPiece(new King(ChessColor.WHITE, Utilities.SIX, Utilities.FOUR, false, false));
        BUILDER.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.THREE));
        
        // Set pieces for black
        BUILDER.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.THREE));
        BUILDER.SetPiece(new Pawn(ChessColor.BLACK, Utilities.FOUR, Utilities.TWO));
        BUILDER.SetPiece(new King(ChessColor.BLACK, Utilities.ZERO, Utilities.FOUR, false, false));
        
        // Set who will go first in this test
        BUILDER.SetWhoseTurn(ChessColor.WHITE);
        
        return BUILDER.Build();
    }
    
    /**/
    /*
    NAME
        public static final Board GetBareKingTest();
    
    SYNOPSIS
        public static final Board GetBareKingTest();
    
        No parameters.
    
    DESCRIPTION
        This method returns a test configuration for
        testing draw by insufficient material when both
        sides have only bare kings.
    
    RETURNS
        A board in a predetermined configuration for test purposes.
    
    AUTHOR
        Ryan King
    */
    public static final Board GetBareKingTest(){
        // Make a new builder
        final BoardBuilder BUILDER = new BoardBuilder();
        
        // Set pieces for white
        BUILDER.SetPiece(new King(ChessColor.WHITE, Utilities.THREE, Utilities.FIVE, false, false));

        // Set pieces for black
        BUILDER.SetPiece(new King(ChessColor.BLACK, Utilities.TWO, Utilities.ONE, false, false));
        BUILDER.SetPiece(new Pawn(ChessColor.BLACK, Utilities.FOUR, Utilities.FOUR));
        
        // White will go first in this test
        BUILDER.SetWhoseTurn(ChessColor.WHITE);
        
        return BUILDER.Build();
    }
    
    /**/
    /*
    NAME
        public static final Board GetKingAndBishopTest();
    
    SYNOPSIS
        public static final Board GetKingAndBishopTest();
    
        No parameters.
    
    DESCRIPTION
        This method returns a test configuration for
        testing draw by insufficient material when one
        player has a bare king and the other has a king
        and a bishop that can be on either tile color.
    
    RETURNS
        A board in a predetermined configuration for test purposes.
    
    AUTHOR
        Ryan King
    */
    public static final Board GetKingAndBishopTest(){
        final BoardBuilder BUILDER = new BoardBuilder();
        
        // Set pieces for white
        BUILDER.SetPiece(new King(ChessColor.WHITE, Utilities.THREE, Utilities.FIVE, false, false));

        // Set pieces for black
        BUILDER.SetPiece(new King(ChessColor.BLACK, Utilities.TWO, Utilities.ONE, false, false));
        BUILDER.SetPiece(new Pawn(ChessColor.BLACK, Utilities.FOUR, Utilities.FOUR));
        BUILDER.SetPiece(new Bishop(ChessColor.BLACK, Utilities.ZERO, Utilities.FOUR));
        
        // White will go first in this test
        BUILDER.SetWhoseTurn(ChessColor.WHITE);
        
        return BUILDER.Build();
    }
    
    /**/
    /*
    NAME
        public static final Board GetKingAndKnightTest();
    
    SYNOPSIS
        public static final Board GetKingAndKnightTest();
    
        No parameters.
    
    DESCRIPTION
        This method returns a test configuration for
        testing draw by insufficient material when one
        player has a bare king and the other has a king and a knight.
    
    RETURNS
        A board in a predetermined configuration for test purposes.
    
    AUTHOR
        Ryan King
    */
    public static final Board GetKingAndKnightTest(){
        // Make a new builder
        final BoardBuilder BUILDER = new BoardBuilder();
        
        // Set pieces for white
        BUILDER.SetPiece(new King(ChessColor.WHITE, Utilities.THREE, Utilities.FIVE, false, false));

        // Set pieces for black
        BUILDER.SetPiece(new King(ChessColor.BLACK, Utilities.TWO, Utilities.ONE, false, false));
        BUILDER.SetPiece(new Pawn(ChessColor.BLACK, Utilities.FOUR, Utilities.FOUR));
        BUILDER.SetPiece(new Knight(ChessColor.BLACK, Utilities.ZERO, Utilities.FOUR));
        
        // White will go first in this test
        BUILDER.SetWhoseTurn(ChessColor.WHITE);
        
        return BUILDER.Build();
    }
    
    /**/
    /*
    NAME
        public static final Board GetSameColorBishopTest();
    
    SYNOPSIS
        public static final Board GetSameColorBishopTest();
    
        No parameters.
    
    DESCRIPTION
        This method returns a test configuration for
        testing draw by insufficient material when both
        players have a king and a bishop and both bishops
        move on the same tile color.
    
    RETURNS
        A board in a predetermined configuration for test purposes.
    
    AUTHOR
        Ryan King
    */
    public static final Board GetSameColorBishopTest(){
        // Make a new builder
        final BoardBuilder BUILDER = new BoardBuilder();
        
        // Set pieces for white
        BUILDER.SetPiece(new King(ChessColor.WHITE, Utilities.THREE, Utilities.FIVE, false, false));
        BUILDER.SetPiece(new Bishop(ChessColor.WHITE, Utilities.FIVE, Utilities.FIVE));

        // Set pieces for black
        BUILDER.SetPiece(new King(ChessColor.BLACK, Utilities.TWO, Utilities.ONE, false, false));
        BUILDER.SetPiece(new Pawn(ChessColor.BLACK, Utilities.FOUR, Utilities.FOUR));
        BUILDER.SetPiece(new Bishop(ChessColor.BLACK, Utilities.ZERO, Utilities.FOUR));
        
        // White will go first in this test
        BUILDER.SetWhoseTurn(ChessColor.WHITE);
        
        return BUILDER.Build();
    }
    
    /**/
    /*
    NAME
        public final Tile GetTile(final int a_row, final int a_column);
    
    SYNOPSIS
        public final Tile GetTile(final int a_row, final int a_column);
    
        int a_row -----------> The row of the Tile.
        
        int a_column --------> The column of the Tile.
    
    DESCRIPTION
        This method returns a Tile based on that Tile's position on the
        two-dimensional tile array. It will return null if
        invalid arguments are given.
    
    RETURNS
        The desired Tile object if coordinates are valid, or null if they're not.
    
    AUTHOR
        Ryan King
    */
    public final Tile GetTile(final int a_row, final int a_column){
        try{
            if(BoardUtilities.HasValidCoordinates(a_row, a_column)){
                return this.m_boardObject[a_row][a_column];
            }else{
                return null;
            }
        }catch(Exception e){
            return null;
        }
    }  
    
    /**/
    /*
    NAME
        public final King GetKing(final ChessColor a_color);
    
    SYNOPSIS
        public final King GetKing(final ChessColor a_color);
    
        ChessColor a_color ----------> The desired color.
    
    DESCRIPTION
        This method returns the king of the given color.
    
    RETURNS
        King: The king piece of the given color or null on error.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final King GetKing(final ChessColor a_color){
        if(a_color == null){
            return null;
        }
        
        for(int i = Utilities.ZERO; i < Utilities.SIXTY_FOUR; i++){
            // Assign aliases for row and column
            final int ROW = i / Utilities.EIGHT;
            final int COLUMN = i % Utilities.EIGHT;
        
            // Only check occupied tiles
            if(this.m_boardObject[ROW][COLUMN].IsEmpty() || !this.m_boardObject[ROW][COLUMN].GetPiece().IsKing()){
                continue;
            }else{
                if(this.m_boardObject[ROW][COLUMN].GetPiece().IsKing() && this.m_boardObject[ROW][COLUMN].GetPiece().GetColor().IsAlly(a_color)){
                    return (King) this.m_boardObject[ROW][COLUMN].GetPiece();
                }
            }
        }
        
        // Return null on error
        return null;
    }
    
    /**/
    /*
    NAME
        public final String GetWhiteBoard();
    
    SYNOPSIS
        public final String GetWhiteBoard();
    
        No parameters.
    
    DESCRIPTION
        This method sends the information of the Board into a String,
        complete with algebraic notation on the left and bottom sides.
        It returns the current state of the Board from white's perspective.
    
    RETURNS
        String whiteBoardString: A String representation of the Board from white's perspective.
    
    AUTHOR
        Ryan King
    */
    public final String GetWhiteBoard(){
        // Initialize an empty String
        String whiteBoardString = Utilities.EMPTY_STRING;
        for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){
            
            // Assign aliases for row and column
            final int ROW = index / Utilities.EIGHT;
            final int COLUMN = index % Utilities.EIGHT;
            
            // Print the algebraic row
            if(COLUMN == Utilities.ZERO){
                whiteBoardString += Integer.toString(Utilities.EIGHT - ROW) + Utilities.SPACE;
            }
            
            // Get the piece's image if the tile is occupied, otherwise put a hyphen-minus
            if(this.m_boardObject[ROW][COLUMN].IsOccupied()){
                whiteBoardString += Character.toString(this.m_boardObject[ROW][COLUMN].GetPiece().GetBoardIcon());
            }else{
                whiteBoardString += Utilities.NO_RIGHTS_OR_TILE;
            }
            
            // Add spaces between tiles
            whiteBoardString += Utilities.SPACE;
            
            // Start a new line once this has reached the end of the current row
            if(COLUMN == Utilities.SEVEN){
                whiteBoardString += Utilities.NEWLINE;
            }
        }
        
        // Add letters of columns
        whiteBoardString += WHITE_FILES;
        
        // Return the String
        return whiteBoardString;
    }    
    
    /**/
    /*
    NAME
        public final String GetBlackBoard();
    
    SYNOPSIS
        public final String GetBlackBoard();
    
        No parameters.
    
    DESCRIPTION
        This method sends the information of the Board into a String,
        complete with algebraic notation on the left and bottom sides.
        It returns the current state of the Board from black's perspective.
    
    RETURNS
        String blackBoardString: A String representation of the Board from black's perspective.
    
    AUTHOR
        Ryan King
    */
    public final String GetBlackBoard(){
        // Initialize an empty String
        String blackBoardString = Utilities.EMPTY_STRING;
        for(int index = Utilities.SIXTY_FOUR - Utilities.ONE; index >= Utilities.ZERO; index--){
            
            // Assign aliases for row and column
            final int ROW = index / Utilities.EIGHT;
            final int COLUMN = index % Utilities.EIGHT;
            
            // Print the algebraic row
            if(COLUMN == Utilities.SEVEN){
                blackBoardString += Integer.toString(Utilities.EIGHT - ROW) + Utilities.SPACE;
            }
            
            // Get the piece's image if the tile is occupied, otherwise put a hyphen-minus
            if(m_boardObject[ROW][COLUMN].IsOccupied()){
                blackBoardString += Character.toString(this.m_boardObject[ROW][COLUMN].GetPiece().GetBoardIcon());
            }else{
                blackBoardString += Utilities.NO_RIGHTS_OR_TILE;
            }
            
            // Add spaces between tiles
            blackBoardString += Utilities.SPACE;
            
            // Start a new line once this has reached the end of the current row
            if(COLUMN == Utilities.ZERO){
                blackBoardString += Utilities.NEWLINE;
            }
        }
        
        // Add letters of columns
        blackBoardString += BLACK_FILES;
        
        // Return the String
        return blackBoardString;
    }
    
    /**/
    /*
    NAME
        public final String toString();
    
    SYNOPSIS
        public final String toString();
    
        No parameters.
    
    DESCRIPTION
        This method returns an FEN string of the current board,
        excluding the number of halfmoves and fullmoves made during the game.
        The move clocks will be added in later when parsing during the actual game.
    
    RETURNS
        String: The FEN string of this board excluding the move clocks.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final String toString(){
        return SerializeBoardContents() + Utilities.SPACE + SerializeTurn() + Utilities.SPACE + SerializeCastlingRights() + Utilities.SPACE + SerializeEnPassantTile();
    }
    
    /**/
    /*
    NAME
        private final String SerializeBoardContents();
    
    SYNOPSIS
        private final String SerializeBoardContents();
    
        No parameters.
    
    DESCRIPTION
        This method returns an FEN string of the current board configuration.
        White pieces are denoted with capital letters (P, R, N, B, Q, K) and
        black pieces are denoted with lowercase letters (p, r, n, b, q, k).
        The letters used are the standard notational letters used in English.
        Consecutive empty tiles on the same row/rank are compiled into a number between 1 and 8. 
        
        For example, if a rank reads "R (blank) (blank) (blank) K (blank) (blank) R", 
        the FEN serialization of this rank would give "R3K2R". An empty rank would just give "8".
        Ranks are separated by forward slashes ("/").
    
    RETURNS
        String board: The FEN string of this board.
    
    AUTHOR
        Ryan King
    */
    private final String SerializeBoardContents(){
        String board = Utilities.EMPTY_STRING;
        
        // Initialize the counter that will hold 
        // the current number of empty tiles encountered in part of a row
        int emptyTiles = Utilities.ZERO;
        
        // Add the contents of the board
        for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){
            final int ROW = index / Utilities.EIGHT;
            final int COLUMN = index % Utilities.EIGHT;
        
            // Count how many tiles are empty between pieces
            if(this.m_boardObject[ROW][COLUMN].IsEmpty()){
                emptyTiles++;
            }else{
                // Place the empty tile counter into the string once another occupied tile
                // is found on the same row
                if(emptyTiles > Utilities.ZERO){
                    board += Integer.toString(emptyTiles);
                    emptyTiles = Utilities.ZERO;
                }
                board += this.m_boardObject[ROW][COLUMN].GetPiece().GetIcon();
            }
        
            // Add any empty tiles left over at the end
            if(COLUMN == Utilities.SEVEN){
                if(emptyTiles > Utilities.ZERO){
                    board += Integer.toString(emptyTiles);
                }
                
                // Add a row delimiter if necessary
                if(ROW < Utilities.SEVEN){
                    board += Utilities.FORWARD_SLASH;
                }
                
                // Reset the empty tile counter
                emptyTiles = Utilities.ZERO;
            }
        }
        
        return board;
    }
    
    /**/
    /*
    NAME
        private final String SerializeTurn();
    
    SYNOPSIS
        private final String SerializeTurn();
    
        No parameters.
    
    DESCRIPTION
        This method returns an FEN string of whose turn it is on the board,
        either "b" or "w".
    
    RETURNS
        String: "b" if it's black's turn or "w" if it's white's turn.
    
    AUTHOR
        Ryan King
    */
    private final char SerializeTurn(){
        return Character.toLowerCase(this.WhoseTurnIsIt().toString().charAt(Utilities.ZERO));
    }
    
    /**/
    /*
    NAME
        private final String SerializeCastlingRights();
    
    SYNOPSIS
        private final String SerializeCastlingRights();
    
        No parameters.
    
    DESCRIPTION
        This method returns an FEN string of castling rights for both sides.
    
    RETURNS
        String: Some combination of the letters "KQkq" in that order
        if any rights exist, otherwise "-".
    
    AUTHOR
        Ryan King
    */
    private final String SerializeCastlingRights(){
        final String WHITE_CASTLING_RIGHTS = SerializeCastlingRightsForOneSide(this.GetKing(ChessColor.WHITE));
        final String BLACK_CASTLING_RIGHTS = SerializeCastlingRightsForOneSide(this.GetKing(ChessColor.BLACK));
        
        // Add the castling privileges, if any
        if(WHITE_CASTLING_RIGHTS.equals(Character.toString(Utilities.NO_RIGHTS_OR_TILE)) && BLACK_CASTLING_RIGHTS.equals(Character.toString(Utilities.NO_RIGHTS_OR_TILE))){
            return Character.toString(Utilities.NO_RIGHTS_OR_TILE);
        }else{
            String rights = Utilities.EMPTY_STRING;
            
            if(!WHITE_CASTLING_RIGHTS.equals(Character.toString(Utilities.NO_RIGHTS_OR_TILE))){
                rights += WHITE_CASTLING_RIGHTS;
            }
            
            if(!BLACK_CASTLING_RIGHTS.equals(Character.toString(Utilities.NO_RIGHTS_OR_TILE))){
                rights += BLACK_CASTLING_RIGHTS;
            }
            
            return rights;
        }
    }
    
    /**/
    /*
    NAME
        private final String SerializeCastlingRightsForOneSide(final King a_king);
    
    SYNOPSIS
        private final String SerializeCastlingRightsForOneSide(final King a_king);
    
        King a_king --------------> The king whose castling rights must be serialized.
    
    DESCRIPTION
        This method returns an FEN string of castling rights for one side.
        If the king is white, it will return "KQ", "K", "Q", or "-".
        If the king is black, it will return "kq", "k", "q", or "-".
    
    RETURNS
        String: Some combination of the letters "KQ" for white 
        or "kq" for black if any rights exist, otherwise "-".
    
    AUTHOR
        Ryan King
    */
    private final String SerializeCastlingRightsForOneSide(final King a_king){
        String castlingRights = Utilities.EMPTY_STRING;
        
        if(a_king == null){
            return Utilities.EMPTY_STRING;
        }
        
        // Assign helpful aliases to the required characters
        final char KINGSIDE = (a_king.IsWhite() ? Utilities.WHITE_KING_ICON : Utilities.BLACK_KING_ICON);
        final char QUEENSIDE = (a_king.IsWhite() ? Utilities.WHITE_QUEEN_ICON : Utilities.BLACK_QUEEN_ICON);

        // Two checks per side to help get rid of erroneous castling rights
        if(a_king.HasKingsideCastlingRook(this) && !a_king.HasMoved()){
            castlingRights += KINGSIDE;
        }
        
        if(a_king.HasQueensideCastlingRook(this) && !a_king.HasMoved()){
            castlingRights += QUEENSIDE;
        }
        
        // Return the proper string
        if(!castlingRights.isBlank()){
            return castlingRights;
        }else{
            return Character.toString(Utilities.NO_RIGHTS_OR_TILE);
        }
    }
    
    /**/
    /*
    NAME
        private final String SerializeEnPassantTile();
    
    SYNOPSIS
        private final String SerializeEnPassantTile();
    
        No parameters.
    
    DESCRIPTION
        This method returns an FEN string of the en passant tile, if any.
    
    RETURNS
        String: A lowercase letter from a-h followed by 3 or 6 
        if an en passant tile exists, otherwise "-".
    
    AUTHOR
        Ryan King
    */
    private final String SerializeEnPassantTile(){
        if(this.m_enPassantTile != null){
            return this.GetEnPassantTile().toString();
        }else{
            return Character.toString(Utilities.NO_RIGHTS_OR_TILE);
        }
    }
    
    /**/
    /*
    NAME
        private final void AdjustRooks(final Move a_candidate);
    
    SYNOPSIS
        private final void AdjustRooks(final Move a_candidate);
    
        Move a_candidate -------> The move that may change castling privileges.
    
    DESCRIPTION
        This method adjusts the castling rights of the king
        after a move involving a rook has been made. 
        This is meant to be used when a rook moves or 
        is captured and the king hasn't moved and thus 
        may lose castling rights to one side.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void AdjustRooks(final Move a_candidate){
        // Null values do not constitute valid moves
        if(a_candidate == null){
            return;
        }
        
        // Get information about the old piece
        final int OLD_ROW = a_candidate.GetPiece().GetCurrentRow();
        final int OLD_COLUMN = a_candidate.GetPiece().GetCurrentColumn();
        final ChessColor MOVER_COLOR = a_candidate.GetPiece().GetColor();
        
        // Find the affected king
        final King KING = this.GetKing(MOVER_COLOR);
        final int KING_ROW = KING.GetCurrentRow();
        final int KING_COLUMN = KING.GetCurrentColumn();
        final ChessColor TILE_COLOR = m_boardObject[KING_ROW][KING_COLUMN].GetColor();
        
        // Use Boolean fields for determining castling rights
        final boolean KINGSIDE, QUEENSIDE;
        
        // Determine different castling rights if the mover is a rook on its first move and the corresponding king is in his original spot and hasn't moved
        if((a_candidate.GetPiece().IsRook() && !a_candidate.GetPiece().HasMoved()) && (KING.IsInOriginalSpot() && !KING.HasMoved())){
            // Determine if white can kingside castle
            if(a_candidate.GetPiece().IsWhite() && OLD_ROW == Utilities.SEVEN && OLD_COLUMN == Utilities.ZERO){
                KINGSIDE = KING.CanKingsideCastleOnThisTurn(this);
                QUEENSIDE = false;
            // Determine if white can queenside castle
            }else if(a_candidate.GetPiece().IsWhite() && OLD_ROW == Utilities.SEVEN && OLD_COLUMN == Utilities.SEVEN){
                KINGSIDE = false;
                QUEENSIDE = KING.CanQueensideCastleOnThisTurn(this);
            // Determine if black can kingside castle
            }else if(a_candidate.GetPiece().IsBlack() && OLD_ROW == Utilities.ZERO && OLD_COLUMN == Utilities.ZERO){
                KINGSIDE = KING.CanKingsideCastleOnThisTurn(this);
                QUEENSIDE = false;
            // Determine if black can queenside castle
            }else if(a_candidate.GetPiece().IsBlack() && OLD_ROW == Utilities.ZERO && OLD_COLUMN == Utilities.SEVEN){
                KINGSIDE = false;
                QUEENSIDE = KING.CanQueensideCastleOnThisTurn(this);          
            }else{
                KINGSIDE = KING.CanKingsideCastleOnThisTurn(this);
                QUEENSIDE = KING.CanQueensideCastleOnThisTurn(this);
            }
            
            // If the king has not moved, reset the king with adjusted castling privileges and 0 moves made
            // If the king has already moved, it does not matter if the rook has not moved because the king cannot castle
            this.m_boardObject[KING_ROW][KING_COLUMN] = new Tile(TILE_COLOR, KING_ROW, KING_COLUMN, new King(MOVER_COLOR, KING_ROW, KING_COLUMN, KINGSIDE, QUEENSIDE));
        }
    }
    
    /**/
    /*
    NAME
        public final Board Move(final RegularMove a_candidate);
    
    SYNOPSIS
        public final Board Move(final RegularMove a_candidate);
    
        RegularMove a_candidate --------> The regular move to be performed.

    DESCRIPTION
        This method returns a new Board object with the specified regular move made.
    
    RETURNS
        Board: The Board once the move has been completed.
    
    AUTHOR
        Ryan King
    */
    public final Board Move(final RegularMove a_candidate){
        // Null values do not constitute valid moves
        if(a_candidate == null){
            return this;
        }
        
        // Preserve the old row and column
        final int OLD_ROW = a_candidate.GetPiece().GetCurrentRow();
        final int OLD_COLUMN = a_candidate.GetPiece().GetCurrentColumn();
        
        // Make note of the new row and column
        final int NEW_ROW = a_candidate.GetNewRow();
        final int NEW_COLUMN = a_candidate.GetNewColumn();

        // Make note of the new and old tiles
        final Tile NEW_TILE = this.m_boardObject[NEW_ROW][NEW_COLUMN];
        final Tile OLD_TILE = this.m_boardObject[OLD_ROW][OLD_COLUMN];
        
        // Change castling privileges if this scenario eliminates them
        AdjustRooks(a_candidate);
                
        // Set the moved Piece to the new Tile
        this.m_boardObject[NEW_ROW][NEW_COLUMN] = new Tile(NEW_TILE.GetColor(), NEW_TILE.GetRow(), NEW_TILE.GetColumn(), Factory.MovedPieceFactory(a_candidate.GetPiece(), NEW_ROW, NEW_COLUMN));
        // Remove the moved Piece from the old Tile
        this.m_boardObject[OLD_ROW][OLD_COLUMN] = new Tile(OLD_TILE.GetColor(), OLD_TILE.GetRow(), OLD_TILE.GetColumn(), null);
        
        // Initialize a new BoardBuilder object with the configuration of the new Board
        final BoardBuilder BUILDER = new BoardBuilder(this.m_boardObject, BoardUtilities.Reverse(this.WhoseTurnIsIt()));
        
        // Build the board
        return BUILDER.Build();
    }
    
    /**/
    /*
    NAME
        public final Board Attack(final AttackingMove a_candidate);
    
    SYNOPSIS
        public final Board Attack(final AttackingMove a_candidate);
    
        AttackingMove a_candidate --------> The Move to be performed.

    DESCRIPTION
        This method returns a new Board object with the specified attacking move made.
    
    RETURNS
        Board: The Board once the move has been completed.
    
    AUTHOR
        Ryan King
    */
    public final Board Attack(final AttackingMove a_candidate){
        // Null values do not constitute valid moves
        if(a_candidate == null){
            return this;
        }
        
        // Get the rows and columns of the source and destination tiles
        final int OLD_ROW = a_candidate.GetPiece().GetCurrentRow();
        final int OLD_COLUMN = a_candidate.GetPiece().GetCurrentColumn();
        final int NEW_ROW = a_candidate.GetNewRow();
        final int NEW_COLUMN = a_candidate.GetNewColumn();

        // Find both source and destination tiles involved in this move
        final Tile NEW_TILE = this.m_boardObject[NEW_ROW][NEW_COLUMN];
        final Tile OLD_TILE = this.m_boardObject[OLD_ROW][OLD_COLUMN];
        
        // Adjust castling privileges for both sides
        AdjustRooks(a_candidate);
        
        // Set the moved Piece to the new Tile
        this.m_boardObject[NEW_ROW][NEW_COLUMN] = new Tile(NEW_TILE.GetColor(), NEW_TILE.GetRow(), NEW_TILE.GetColumn(), Factory.MovedPieceFactory(a_candidate.GetPiece(), NEW_ROW, NEW_COLUMN));
        
        // Remove the moved Piece from the old Tile
        this.m_boardObject[OLD_ROW][OLD_COLUMN] = new Tile(OLD_TILE.GetColor(), OLD_TILE.GetRow(), OLD_TILE.GetColumn(), null);
        
        // Initialize a new BoardBuilder object with the configuration of the new Board
        final BoardBuilder BUILDER = new BoardBuilder(this.m_boardObject, BoardUtilities.Reverse(this.WhoseTurnIsIt()));
        
        // Return the newly-moved board
        return BUILDER.Build();
    }
    
    /**/
    /*
    NAME
        public final Board Castle(final CastlingMove a_candidate);
    
    SYNOPSIS
        public final Board Castle(final CastlingMove a_candidate);
    
        CastlingMove a_candidate --------> The Move to be performed.

    DESCRIPTION
        This method returns a new Board object with the specified castling move made.
    
    RETURNS
        Board: The Board once the move has been completed.
    
    AUTHOR
        Ryan King
    */
    public final Board Castle(final CastlingMove a_candidate){
        // Null values do not constitute valid moves
        if(a_candidate == null){
            return this;
        }
        
        // Preserve the king's old coordinates
        final int OLD_KING_ROW = a_candidate.GetPiece().GetCurrentRow();
        final int OLD_KING_COLUMN = a_candidate.GetPiece().GetCurrentColumn();
        
        // Grab the king's new coordinates
        final int NEW_KING_ROW = a_candidate.GetNewRow();
        final int NEW_KING_COLUMN = a_candidate.GetNewColumn();
        
        // Preserve the rook's old coordinates
        final int OLD_ROOK_ROW = a_candidate.GetRookCurrentRow();
        final int OLD_ROOK_COLUMN = a_candidate.GetRookCurrentColumn();
        
        // Grab the rook's new coordinates
        final int NEW_ROOK_ROW = a_candidate.GetRookDestinationRow();
        final int NEW_ROOK_COLUMN = a_candidate.GetRookDestinationColumn();
        
        // Find the king's old and new tiles
        final Tile OLD_KING_TILE = this.m_boardObject[OLD_KING_ROW][OLD_KING_COLUMN];
        final Tile NEW_KING_TILE = this.m_boardObject[NEW_KING_ROW][NEW_KING_COLUMN];
        
        // Find the rook's old and new tiles
        final Tile OLD_ROOK_TILE = this.m_boardObject[OLD_ROOK_ROW][OLD_ROOK_COLUMN];
        final Tile NEW_ROOK_TILE = this.m_boardObject[NEW_ROOK_ROW][NEW_ROOK_COLUMN];
    
        // Set the moved king to the new Tile
        this.m_boardObject[NEW_KING_ROW][NEW_KING_COLUMN] = new Tile(NEW_KING_TILE.GetColor(), NEW_KING_TILE.GetRow(), NEW_KING_TILE.GetColumn(), Factory.MovedPieceFactory(a_candidate.GetPiece(), NEW_KING_ROW, NEW_KING_COLUMN));
        
        // Remove the moved king from the old Tile
        this.m_boardObject[OLD_KING_ROW][OLD_KING_COLUMN] = new Tile(OLD_KING_TILE.GetColor(), OLD_KING_TILE.GetRow(), OLD_KING_TILE.GetColumn(), null);
        
        // Set the moved rook to the new Tile
        this.m_boardObject[NEW_ROOK_ROW][NEW_ROOK_COLUMN] = new Tile(NEW_ROOK_TILE.GetColor(), NEW_ROOK_TILE.GetRow(), NEW_ROOK_TILE.GetColumn(), Factory.MovedPieceFactory(this.m_boardObject[OLD_ROOK_ROW][OLD_ROOK_COLUMN].GetPiece(), NEW_ROOK_ROW, NEW_ROOK_COLUMN));
        
        // Remove the moved rook from the old Tile
        this.m_boardObject[OLD_ROOK_ROW][OLD_ROOK_COLUMN] = new Tile(OLD_ROOK_TILE.GetColor(), OLD_ROOK_TILE.GetRow(), OLD_ROOK_TILE.GetColumn(), null);
        
        // Initialize a new BoardBuilder object with the configuration of the new Board
        final BoardBuilder BUILDER = new BoardBuilder(this.m_boardObject, BoardUtilities.Reverse(this.WhoseTurnIsIt()));            

        // Return the newly-moved board
        return BUILDER.Build();
    }
    
    /**/
    /*
    NAME
        public final Board EnPassant(final EnPassantMove a_candidate);
    
    SYNOPSIS
        public final Board EnPassant(final EnPassantMove a_candidate);
    
        EnPassantMove a_candidate --------> The Move to be performed.

    DESCRIPTION
        This method returns a new Board object with the specified Move made.
    
    RETURNS
        Board: The Board once the move has been completed.
    
    AUTHOR
        Ryan King
    */
    public final Board EnPassant(final EnPassantMove a_candidate){
        // Null values do not constitute valid moves
        if(a_candidate == null){
            return this;
        }
        
        // Preserve the old coordinates of the moving pawn
        final int OLD_ROW = a_candidate.GetPiece().GetCurrentRow();
        final int OLD_COLUMN = a_candidate.GetPiece().GetCurrentColumn();
        
        // Get the coordinates where the moving pawn will move
        final int NEW_ROW = a_candidate.GetNewRow();
        final int NEW_COLUMN = a_candidate.GetNewColumn();
        final int OLD_MOVES = a_candidate.GetPiece().HowManyMoves();
        
        // Get the coordinates of the captured pawn,
        // which are different from the destination
        // coordinates due to the nature of en passant
        final int PAWN_ROW = a_candidate.GetCapturedPawnRow();
        final int PAWN_COLUMN = a_candidate.GetCapturedPawnColumn();

        // Find all tiles used in this move
        final Tile NEW_TILE = this.m_boardObject[NEW_ROW][NEW_COLUMN];
        final Tile OLD_TILE = this.m_boardObject[OLD_ROW][OLD_COLUMN];
        final Tile PAWN_TILE = this.m_boardObject[PAWN_ROW][PAWN_COLUMN];
              
        // Set the moved Piece to the new Tile
        this.m_boardObject[NEW_ROW][NEW_COLUMN] = new Tile(NEW_TILE.GetColor(), NEW_TILE.GetRow(), NEW_TILE.GetColumn(), Factory.MovedPieceFactory(a_candidate.GetPiece(), NEW_ROW, NEW_COLUMN));
        
        // Remove the moved Piece from the old Tile
        this.m_boardObject[OLD_ROW][OLD_COLUMN] = new Tile(OLD_TILE.GetColor(), OLD_TILE.GetRow(), OLD_TILE.GetColumn(), null);
        
        // Remove the captured Pawn from its Tile
        this.m_boardObject[PAWN_ROW][PAWN_COLUMN] = new Tile(PAWN_TILE.GetColor(), PAWN_TILE.GetRow(), PAWN_TILE.GetColumn(), null);
        
        // Initialize a new BoardBuilder object with the configuration of the new Board
        final BoardBuilder BUILDER = new BoardBuilder(this.m_boardObject, BoardUtilities.Reverse(this.WhoseTurnIsIt()));
        
        // Return the newly-moved board
        return BUILDER.Build();
    }
    
    /**/
    /*
    NAME
        public final Board Promote(final Piece a_promotedPiece);
    
    SYNOPSIS
        public final Board Promote(final Piece a_promotedPiece);
    
        Piece a_promotedPiece ------------> The piece to be promoted.

    DESCRIPTION
        This method returns a new Board object with the promoted pawn replaced
        by the piece the player chose.
    
    RETURNS
        Board: The Board once the move has been completed.
    
    AUTHOR
        Ryan King
    */
    public final Board Promote(final Piece a_promotedPiece){
        // Null or illegal values do not constitute valid moves
        if(a_promotedPiece == null || a_promotedPiece.IsPawn() || a_promotedPiece.IsKing() || !BoardUtilities.HasValidCoordinates(a_promotedPiece.GetCurrentRow(), a_promotedPiece.GetCurrentColumn()) || ((a_promotedPiece.IsWhite() && a_promotedPiece.GetCurrentRow() != Utilities.ZERO) || (a_promotedPiece.IsBlack() && a_promotedPiece.GetCurrentRow() != Utilities.SEVEN))){
            return this;
        }
        
        // Find the coordinates of the promoted pawn
        final int PROMOTED_ROW = a_promotedPiece.GetCurrentRow();
        final int PROMOTED_COLUMN = a_promotedPiece.GetCurrentColumn();
        
        // Find the tile where the promoted pawn is located
        final Tile PROMOTED_TILE = this.m_boardObject[PROMOTED_ROW][PROMOTED_COLUMN];
        
        // Remove the old pawn and put the promoted piece in its place
        this.m_boardObject[PROMOTED_ROW][PROMOTED_COLUMN] = new Tile(PROMOTED_TILE.GetColor(), PROMOTED_TILE.GetRow(), PROMOTED_TILE.GetColumn(), Factory.MovedPieceFactory(a_promotedPiece, PROMOTED_ROW, PROMOTED_COLUMN));

        // Initialize a new BoardBuilder object with the configuration of the new Board
        final BoardBuilder BUILDER = new BoardBuilder(this.m_boardObject, this.WhoseTurnIsIt());
        
        // Return the newly-moved board
        return BUILDER.Build();
    }
    
    /**
     * The BoardBuilder class is responsible for generating any new moves 
     * made on the Board object and passing it into the Board at
     * the end of every turn. This is an internal class residing inside the Board.
     * A blank BoardBuilder object is also used when deserializing a FEN string.
     * 
     * Adapted from the Builder class in Black Widow Chess by Amir Afghani
     * https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/board/Board.java
     */
    public static class BoardBuilder{
        private final Tile[][] m_builderBoard;
        private ChessColor m_whoseTurn;
        
        /**/
        /*
        NAME
            public BoardBuilder();
        
        SYNOPSIS
            public BoardBuilder();
        
            No parameters.
        
        DESCRIPTION
            This constructor initializes the space for the BoardBuilder object
            and sets it with empty tiles.
            
        RETURNS
            Nothing
        
        AUTHOR
            Amir Afghani,
            https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/board/Board.java
        */
        public BoardBuilder(){
            this.m_builderBoard = new Tile[Utilities.EIGHT][Utilities.EIGHT];
            this.InitializeEmptyBoard();
        }
        
        /**/
        /*
        NAME
            public BoardBuilder(final Tile[][] a_copy, final ChessColor a_turn);
        
        SYNOPSIS
            public BoardBuilder(final Tile[][] a_copy, final ChessColor a_turn);
        
            Tile[][] a_copy ----------> The 8 by 8 Tile array to be copied.
            
            ChessColor a_turn --------> The color whose turn it is to move.
        
        DESCRIPTION
            This constructor initializes the space for the BoardBuilder object
            and sets it with a deep copy of the two-dimensional Tile array that
            gets passed in. It also initializes the whose turn field to the argument provided.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        public BoardBuilder(final Tile[][] a_copy, final ChessColor a_turn){
            if(a_copy == null || a_turn == null || a_copy.length != Utilities.EIGHT || a_copy[Utilities.ZERO].length != Utilities.EIGHT){
                System.err.println("Invalid argument(s) to BoardBuilder constructor");
                System.exit(Utilities.ONE);
            }
            
            // Initialize the new array space
            this.m_builderBoard = new Tile[Utilities.EIGHT][Utilities.EIGHT];
            // Set the turn
            this.SetWhoseTurn(a_turn);
            // Iterate through each tile from a_copy
            for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){
                final int ROW = index / Utilities.EIGHT;
                final int COLUMN = index % Utilities.EIGHT;
                // Instantiate a new Tile with the given color and coordinates
                this.m_builderBoard[ROW][COLUMN] = new Tile(a_copy[ROW][COLUMN]);
            }
        }
        
        /**/
        /*
        NAME
            private final void InitializeEmptyBoard();
        
        SYNOPSIS
            private final void InitializeEmptyBoard();
        
            No parameters.
        
        DESCRIPTION
            This method initializes an empty Board for the BoardBuilder object.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void InitializeEmptyBoard(){
            for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){
                // Assign aliases for the row and column
                final int ROW = index / Utilities.EIGHT;
                final int COLUMN = index % Utilities.EIGHT;
                
                // Determine the proper color for each tile depending on where it is located and assign it accordingly                
                this.m_builderBoard[ROW][COLUMN] = new Tile(AssignTileColor(ROW, COLUMN), ROW, COLUMN, null);
                
            }
        }
        
        /**/
        /*
        NAME
            private final ChessColor AssignTileColor(final int a_row, final int a_column);
        
        SYNOPSIS
            private final ChessColor AssignTileColor(final int a_row, final int a_column);
            
            int a_row ----------------> The row the piece is resting on.
            
            int a_column -------------> The column the piece is resting on.
        
        DESCRIPTION
            This method returns a tile color given the row and column.
            This color is based off the tile's position on a chessboard.
        
        RETURNS
            ChessColor: A new color based on the coordinates provided.
        
        AUTHOR
            Ryan King
        */
        private final ChessColor AssignTileColor(final int a_row, final int a_column){
            // Idiot proofing
            if(!BoardUtilities.HasValidCoordinates(a_row, a_column)){
                return null;
            }
            
            // Find the proper color by examining both coordinates
            if(a_row % Utilities.TWO == Utilities.ONE){
                if(a_column % Utilities.TWO == Utilities.ONE){
                    return ChessColor.WHITE;
                }else{
                    return ChessColor.BLACK;
                }
            }else{
                if(a_column % Utilities.TWO == Utilities.ONE){
                    return ChessColor.BLACK;
                }else{
                    return ChessColor.WHITE;
                }
            }
        }
        
        /**/
        /*
        NAME
            public final Tile[][] GetBuilderBoard();
        
        SYNOPSIS
            public final Tile[][] GetBuilderBoard();
        
            No parameters.
        
        DESCRIPTION
            This method returns an 8-by-8 Tile array from the BoardBuilder object.
        
        RETURNS
            Tile[8][8]: The BoardBuilder object array.
        
        AUTHOR
            Ryan King
        */
        public final Tile[][] GetBuilderBoard(){
            return this.m_builderBoard;
        }
        
        /**/
        /*
        NAME
            public final Board Build();
        
        SYNOPSIS
            public final Board Build();
        
            No parameters.
        
        DESCRIPTION
            This method returns the Board object that gets passed to the quasi singleton Board.
        
        RETURNS
            Board: A new Board object.
        
        AUTHOR
            Amir Afghani,
            https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/board/Board.java
        */
        public final Board Build(){
            return new Board(this);
        }
        
        /**/
        /*
        NAME
            public final BoardBuilder SetWhoseTurn(final ChessColor a_color);
        
        SYNOPSIS
            public final BoardBuilder SetWhoseTurn(final ChessColor a_color);
        
            ChessColor a_color -------> The side whose turn it is.
        
        DESCRIPTION
            This method returns the BoardBuilder object
            and sets the color of the moving player.
        
        RETURNS
            BoardBuilder this: The BoardBuilder object.
        
        AUTHOR
            Amir Afghani,
            https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/board/Board.java
            with exception handling inserted by Ryan King
        */
        public final BoardBuilder SetWhoseTurn(final ChessColor a_color){
            try{
                // Set the turn to the proper color
                this.m_whoseTurn = a_color;
                return this;
            }catch(Exception e){
                return this;
            }
        }
        
        /**/
        /*
        NAME
            public final BoardBuilder RemovePiece(final int a_row, final int a_column);
        
        SYNOPSIS
            public final BoardBuilder RemovePiece(final int a_row, final int a_column);
        
            int a_row ----------> The row of the tile to empty.
            
            int a_column -------> The column of the tile to empty.
        
        DESCRIPTION
            This method removes the piece from the tile coordinates specified.
            If the given coordinates are invalid, the BoardBuilder
            simply returns itself as is.
        
        RETURNS
            BoardBuilder this: The BoardBuilder object.
        
        AUTHOR
            Amir Afghani,
            https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/board/Board.java
            with exception handling inserted by Ryan King
        */
        public final BoardBuilder RemovePiece(final int a_row, final int a_column){
            try{
                // Remove the piece from the tile if coordinates are valid
                if(BoardUtilities.HasValidCoordinates(a_row, a_column)){
                    final Tile ORIGINAL = this.m_builderBoard[a_row][a_column];
                    this.m_builderBoard[a_row][a_column] = new Tile(ORIGINAL.GetColor(), ORIGINAL.GetRow(), ORIGINAL.GetColumn(), null);
                }
                return this;
            }catch(Exception e){
                return this;
            }
        }
        
        /**/
        /*
        NAME
            public final BoardBuilder SetPiece(final Piece a_piece);
        
        SYNOPSIS
            public final BoardBuilder SetPiece(final Piece a_piece);
        
            Piece a_piece --------> The piece to be set.
        
        DESCRIPTION
            This method returns the BoardBuilder object
            and sets the piece on its spot on the BoardBuilder array.
        
        RETURNS
            BoardBuilder this: The BoardBuilder object.
        
        AUTHOR
            Amir Afghani,
            https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/board/Board.java
            with exceptiond handling inserted by Ryan King
        */
        public final BoardBuilder SetPiece(final Piece a_piece){
            try{
                // Set the piece on tile if the piece's coordinates are valid
                if(BoardUtilities.HasValidCoordinates(a_piece.GetCurrentRow(), a_piece.GetCurrentColumn())){
                    final Tile ORIGINAL = this.m_builderBoard[a_piece.GetCurrentRow()][a_piece.GetCurrentColumn()];
                    this.m_builderBoard[a_piece.GetCurrentRow()][a_piece.GetCurrentColumn()] = new Tile(ORIGINAL.GetColor(), ORIGINAL.GetRow(), ORIGINAL.GetColumn(), a_piece);
                }
                return this;
            }catch(Exception e){
                return this;
            }
        }
        
        /**/
        /*
        NAME
            public final void SetTile(final Tile a_tile, final int a_row, final int a_column);
        
        SYNOPSIS
            public final void SetTile(final Tile a_tile, final int a_row, final int a_column);
            
            Tile a_tile -----> The Tile to be set.
        
            int a_row -------> The row of the Tile to be set.
            
            int a_column ----> The column of the Tile to be set.
        
        DESCRIPTION
            This method sets the tile argument into the position on
            the tile array determined by the a_row and a_column arguments.
            It returns if it encounters an exception.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        public final void SetTile(final Tile a_tile, final int a_row, final int a_column){
            try{
                if(BoardUtilities.HasValidCoordinates(a_row, a_column)){
                    this.m_builderBoard[a_row][a_column] = a_tile;
                }
            }catch(Exception e){
                return;
            }
        }
        
        /**/
        /*
        NAME
            public final Tile BoardBuilder.GetTile(final int a_row, final int a_column)
        
        SYNOPSIS
            public final Tile BoardBuilder.GetTile(final int a_row, final int a_column)
        
            int a_row ------> The row of the tile to be retrieved.
            
            int a_column ---> The column of the tile to be retrieved.
        
        DESCRIPTION
            This method returns a tile based on that tile's position on the
            two-dimensional tile array. It will return the error tile if
            invalid arguments are given.
        
        RETURNS
            Tile: The desired Tile object if coordinates are valid, or null if they're not.
        
        AUTHOR
            Ryan King
        */
        public final Tile GetTile(final int a_row, final int a_column){
            try{
                if(BoardUtilities.HasValidCoordinates(a_row, a_column)){
                    return this.m_builderBoard[a_row][a_column];
                }else{
                    return null;
                }
            }catch(Exception e){
                return null;
            }
        }
        
        /**/
        /*
        NAME
            public final ChessColor BoardBuilder.WhoseTurnIsIt();
        
        SYNOPSIS
            public final ChessColor BoardBuilder.WhoseTurnIsIt();
        
            No parameters.
        
        DESCRIPTION
            This method returns whose turn it is, either white or black.
        
        RETURNS
            ChessColor m_whoseTurn: The color representing whose turn it is.
        
        AUTHOR
            Ryan King
        */
        public final ChessColor WhoseTurnIsIt(){
            return this.m_whoseTurn;
        }
    }//End of BoardBuilder class
}//End of Board class