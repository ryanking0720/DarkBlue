package com.DarkBlue.Board;

import com.DarkBlue.Player.*;
import com.DarkBlue.Piece.*;
import com.DarkBlue.Move.*;
import com.DarkBlue.Utilities.*;

/*
 * This class represents a chessboard.
 * It contains 64 Tile objects which are assigned by an internal
 * class called BoardBuilder. The internal class is responsible
 * for determining the spots of all the pieces after every move.
 * It then assigns its own 8-by-8 array of tiles to the corresponding
 * array in the Board object in its Build() method.
 */
public final class Board{
    
    // The two-dimensional array of tiles where the chess pieces play
    private final Tile[][] m_boardObject;
    // Whose turn it is: White or black
    private final ChessColor m_whoseTurn;
    
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
        Ryan King
    */
    private Board(final BoardBuilder a_builder){
        this.m_boardObject = GetCurrentBoard(a_builder);
        this.m_whoseTurn = a_builder.WhoseTurnIsIt();
    }
    
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
        this.m_boardObject = new Tile[Utilities.EIGHT][Utilities.EIGHT];
        this.m_whoseTurn = a_board.m_whoseTurn;
        
        for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){    
            final int row = index / Utilities.EIGHT;
            final int column = index % Utilities.EIGHT;
            this.m_boardObject[row][column] = new Tile(a_board.m_boardObject[row][column]);
        }
    }
    
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
        return m_whoseTurn;
    }
    
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
        Tile[][] boardDuplicate = new Tile[Utilities.EIGHT][Utilities.EIGHT];
        for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){
            final int row = index / Utilities.EIGHT;
            final int column = index % Utilities.EIGHT;
            boardDuplicate[row][column] = new Tile(a_builder.GetBuilderBoard()[row][column]);
        }
        return boardDuplicate;
    }
    
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
        Ryan King
    */
    public static final Board GetStartingPosition(){
        final BoardBuilder builder = new BoardBuilder();
        
        // Set all white Pieces
        builder.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.ZERO));
        builder.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.ONE));
        builder.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.TWO));
        builder.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.THREE));
        builder.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.FOUR));
        builder.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.FIVE));
        builder.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.SIX));
        builder.SetPiece(new Pawn(ChessColor.WHITE, Utilities.SIX, Utilities.SEVEN));
        
        builder.SetPiece(new Rook(ChessColor.WHITE, Utilities.SEVEN, Utilities.ZERO));
        builder.SetPiece(new Knight(ChessColor.WHITE, Utilities.SEVEN, Utilities.ONE));
        builder.SetPiece(new Bishop(ChessColor.WHITE, Utilities.SEVEN, Utilities.TWO));
        builder.SetPiece(new Queen(ChessColor.WHITE, Utilities.SEVEN, Utilities.THREE));
        builder.SetPiece(new King(ChessColor.WHITE, Utilities.SEVEN, Utilities.FOUR));
        builder.SetPiece(new Bishop(ChessColor.WHITE, Utilities.SEVEN, Utilities.FIVE));
        builder.SetPiece(new Knight(ChessColor.WHITE, Utilities.SEVEN, Utilities.SIX));
        builder.SetPiece(new Rook(ChessColor.WHITE, Utilities.SEVEN, Utilities.SEVEN));
        
        // Set all black Pieces
        builder.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.ZERO));
        builder.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.ONE));
        builder.SetPiece(new Pawn(ChessColor.BLACK,Utilities.ONE, Utilities.TWO));
        builder.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.THREE));
        builder.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.FOUR));
        builder.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.FIVE));
        builder.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.SIX));
        builder.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.SEVEN));
        
        builder.SetPiece(new Rook(ChessColor.BLACK, Utilities.ZERO, Utilities.ZERO));
        builder.SetPiece(new Knight(ChessColor.BLACK, Utilities.ZERO, Utilities.ONE));
        builder.SetPiece(new Bishop(ChessColor.BLACK, Utilities.ZERO, Utilities.TWO));
        builder.SetPiece(new Queen(ChessColor.BLACK, Utilities.ZERO, Utilities.THREE));
        builder.SetPiece(new King(ChessColor.BLACK, Utilities.ZERO, Utilities.FOUR));
        builder.SetPiece(new Bishop(ChessColor.BLACK, Utilities.ZERO, Utilities.FIVE));
        builder.SetPiece(new Knight(ChessColor.BLACK, Utilities.ZERO, Utilities.SIX));
        builder.SetPiece(new Rook(ChessColor.BLACK, Utilities.ZERO, Utilities.SEVEN));
        
        // Set the turn to white because white always goes first
        builder.SetWhoseTurn(ChessColor.WHITE);
        
        // Return the newly-built board
        return builder.Build();
    }
    
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
        final BoardBuilder builder = new BoardBuilder();
        
        // Set the pieces
        builder.SetPiece(new King(ChessColor.WHITE, Utilities.TWO, Utilities.THREE));
        builder.SetPiece(new Pawn(ChessColor.WHITE, Utilities.TWO, Utilities.TWO));
        builder.SetPiece(new King(ChessColor.BLACK, Utilities.ZERO, Utilities.THREE));
        builder.SetPiece(new Knight(ChessColor.BLACK, Utilities.ONE, Utilities.THREE));
        
        // Set the turn to white
        builder.SetWhoseTurn(ChessColor.WHITE);
        
        // Return the newly-built board
        return builder.Build();
    }
    
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
        final BoardBuilder builder = new BoardBuilder();
        
        // Set the pieces
        builder.SetPiece(new King(ChessColor.WHITE, Utilities.TWO, Utilities.THREE));
        builder.SetPiece(new Queen(ChessColor.WHITE, Utilities.TWO, Utilities.TWO));
        builder.SetPiece(new King(ChessColor.BLACK, Utilities.ZERO, Utilities.THREE));
        builder.SetPiece(new Knight(ChessColor.BLACK,Utilities.ONE, Utilities.THREE));
        
        // Set the turn to white
        builder.SetWhoseTurn(ChessColor.WHITE);
        
        // Return the newly-built board
        return builder.Build();
    }
    
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
        final BoardBuilder builder = new BoardBuilder();
        
        // Set the white pieces
        builder.SetPiece(new King(ChessColor.WHITE, Utilities.SEVEN, Utilities.FOUR));
        //builder.SetPiece(new Queen(ChessColor.WHITE, Utilities.SEVEN, Utilities.THREE));
        builder.SetPiece(new Rook(ChessColor.WHITE, Utilities.SEVEN, Utilities.SEVEN));
        builder.SetPiece(new Rook(ChessColor.WHITE, Utilities.SEVEN, Utilities.ZERO));
        
        // Set the black pieces
        builder.SetPiece(new King(ChessColor.BLACK, Utilities.ZERO, Utilities.FOUR));
        //builder.SetPiece(new Knight(ChessColor.BLACK, Utilities.ZERO, Utilities.SIX));
        //builder.SetPiece(new Pawn(ChessColor.BLACK, Utilities.SIX, Utilities.SIX));
        builder.SetPiece(new Rook(ChessColor.BLACK, Utilities.ZERO, Utilities.SEVEN));
        builder.SetPiece(new Rook(ChessColor.BLACK, Utilities.ZERO, Utilities.ZERO));
        
        // Set the turn to white
        builder.SetWhoseTurn(ChessColor.WHITE);
        
        // Return the newly-built board
        return builder.Build();
    }
    
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
        final BoardBuilder builder = new BoardBuilder();
        
        // Set the white pieces
        builder.SetPiece(new Pawn(ChessColor.WHITE, Utilities.ONE, Utilities.ONE));
        builder.SetPiece(new King(ChessColor.WHITE, Utilities.TWO, Utilities.THREE));
        
        // Set the black pieces
        builder.SetPiece(new King(ChessColor.BLACK, Utilities.ZERO, Utilities.THREE));
        builder.SetPiece(new Knight(ChessColor.BLACK, Utilities.ZERO, Utilities.ZERO));
        builder.SetPiece(new Pawn(ChessColor.BLACK, Utilities.SIX, Utilities.SIX));
        
        // Set the turn to black
        builder.SetWhoseTurn(ChessColor.BLACK);
        
        // Return the newly-built board
        return builder.Build();
    }
    
    public static final Board GetEnPassantTest(){
        final BoardBuilder builder = new BoardBuilder();
        
        builder.SetPiece(new Pawn(ChessColor.WHITE, Utilities.THREE, Utilities.FOUR));
        builder.SetPiece(new King(ChessColor.WHITE, Utilities.SEVEN, Utilities.FOUR));
        
        
        builder.SetPiece(new Pawn(ChessColor.BLACK, Utilities.ONE, Utilities.THREE));
        builder.SetPiece(new King(ChessColor.BLACK, Utilities.ZERO, Utilities.FOUR));
        
        builder.SetWhoseTurn(ChessColor.BLACK);
        
        return builder.Build();
    }
    
    /*
    NAME
        public final Tile Board.GetTile(final int a_row, final int a_column);
    
    SYNOPSIS
        public final Tile Board.GetTile(final int a_row, final int a_column);
    
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
            if(Utilities.HasValidCoordinates(a_row, a_column)){
                return m_boardObject[a_row][a_column];
            }else{
                return null;
            }
        }catch(Exception e){
            return null;
        }
    }
    
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
        String whiteBoardString = "";
        for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){
            
            int row = index / Utilities.EIGHT;
            int column = index % Utilities.EIGHT;
            
            if(column == Utilities.ZERO){
                whiteBoardString += Integer.toString(Utilities.EIGHT - row) + " ";
            }
            
            if(m_boardObject[row][column].IsOccupied()){
                whiteBoardString += Character.toString(m_boardObject[row][column].GetPiece().GetBoardIcon());
            }else{
                whiteBoardString += "-";
            }
            
            whiteBoardString += " ";
            
            if(column == Utilities.SEVEN){
                whiteBoardString += "\n";
            }
        }
        whiteBoardString += "  a b c d e f g h\n";
        // Return the String
        return whiteBoardString;
    }    
    
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
        String blackBoardString = "";
        for(int index = Utilities.SIXTY_FOUR - Utilities.ONE; index >= Utilities.ZERO; index--){
            int row = index / Utilities.EIGHT;
            int column = index % Utilities.EIGHT;
            
            if(column == Utilities.SEVEN){
                blackBoardString += Integer.toString(Utilities.EIGHT - row) + " ";
            }
                
            if(m_boardObject[row][column].IsOccupied()){
                blackBoardString += Character.toString(m_boardObject[row][column].GetPiece().GetBoardIcon());
            }else{
                blackBoardString += "-";
            }
            
            blackBoardString += " ";
            
            if(column == Utilities.ZERO){
                blackBoardString += "\n";
            }
        }
        blackBoardString += "  h g f e d c b a\n";
        // Return the String
        return blackBoardString;
    }
    
    /*
    NAME
        public final void PrintWhoseTurn();
    
    SYNOPSIS
        public final void PrintWhoseTurn();
    
        No parameters.
    
    DESCRIPTION
        This method prints the current player's turn
        to the console.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public final void PrintWhoseTurn(){
        if(m_whoseTurn == ChessColor.WHITE){
            System.out.println("It’s white’s turn.");
        }else{
            System.out.println("It’s black’s turn.");
        }
    }
    
    public final void PrintBoard(){
        if(this.WhoseTurnIsIt().IsWhite()){
            System.out.println(this.GetWhiteBoard());
        }else{
            System.out.println(this.GetBlackBoard());
        }
    }
    
    /*
    public void SetGameHistory(ArrayList<Move> a_history){
        m_gameHistory = a_history;
    }
    */
    /*
    public ArrayList<Move> GetGameHistory(){
        return m_gameHistory;
    }
    */
    // The BoardBuilder class is responsible for generating any new moves 
    // made on the Board object and passing it into the Board at
    // the end of every turn. This is an internal class residing inside the Board.
    public static class BoardBuilder{
        private final Tile[][] m_builderBoard;
        private ChessColor m_whoseTurn;
        
        /*
        NAME
            public BoardBuilder();
        
        SYNOPSIS
            public BoardBuilder();
        
            No parameters.
        
        DESCRIPTION
            This constructor initializes the space for the BoardBuilder object
            and sets it with empty tiles. It also initializes the m_whoseTurn
            field with white, assuming this is the first board of the game.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        public BoardBuilder(){
            this.m_builderBoard = new Tile[Utilities.EIGHT][Utilities.EIGHT];
            this.InitializeEmptyBoard();
        }
        
        /*
        NAME
            public BoardBuilder(final Tile[][] a_copy, final ChessColor a_color);
        
        SYNOPSIS
            public BoardBuilder();
        
            Tile[][] a_copy ----------> The 8 by 8 Tile array to be copied.
            
            ChessColor a_color -------> The color whose turn it is to move.
        
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
            // Initialize the new array space
            this.m_builderBoard = new Tile[Utilities.EIGHT][Utilities.EIGHT];
            // Set the turn
            this.SetWhoseTurn(a_turn);
            // Iterate through each tile from a_copy
            for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){
                int row = index / Utilities.EIGHT;
                int column = index % Utilities.EIGHT;
                // Instantiate a new Tile with the given color and coordinates
                this.m_builderBoard[row][column] = new Tile(a_copy[row][column]);
            }
        }
        
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
                int row = index / Utilities.EIGHT;
                int column = index % Utilities.EIGHT;
                if(row % Utilities.TWO == Utilities.ONE){
                    if(column % Utilities.TWO == Utilities.ONE){
                        this.m_builderBoard[row][column] = new Tile(ChessColor.WHITE, row, column, null);
                    }else{
                        this.m_builderBoard[row][column] = new Tile(ChessColor.BLACK, row, column, null);
                    }
                }else{
                    if(column % Utilities.TWO == Utilities.ONE){
                        this.m_builderBoard[row][column] = new Tile(ChessColor.BLACK, row, column, null);
                    }else{
                        this.m_builderBoard[row][column] = new Tile(ChessColor.WHITE, row, column, null);
                    }
                }
            }
        }
        
        /*
        NAME
            public final Tile[][] GetBuilderBoard();
        
        SYNOPSIS
            public final Tile[][] GetBuilderBoard();
        
            No parameters.
        
        DESCRIPTION
            This method returns an 8-by-8 Tile array from the BoardBuilder object.
        
        RETURNS
            The BoardBuilder object array.
        
        AUTHOR
            Ryan King
        */
        public final Tile[][] GetBuilderBoard(){
            return this.m_builderBoard;
        }
        
        /*
        NAME
            public final Board Build();
        
        SYNOPSIS
            public final Board Build();
        
            No parameters.
        
        DESCRIPTION
            This method returns the Board object that gets passed to the quasi singleton Board.
        
        RETURNS
            A new Board object.
        
        AUTHOR
            Ryan King
        */
        public final Board Build(){
            return new Board(this);
        }
        
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
            The BoardBuilder object.
        
        AUTHOR
            Ryan King
        */
        public final BoardBuilder SetWhoseTurn(final ChessColor a_color){
            try{
                this.m_whoseTurn = a_color;
                return this;
            }catch(Exception e){
                return this;
            }
        }
        
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
            The BoardBuilder object.
        
        AUTHOR
            Ryan King
        */
        public final BoardBuilder RemovePiece(final int a_row, final int a_column){
            try{
                if(Utilities.HasValidCoordinates(a_row, a_column)){
                    final Tile original = m_builderBoard[a_row][a_column];
                    m_builderBoard[a_row][a_column] = new Tile(original.GetColor(), original.GetRow(), original.GetColumn(), null);
                }
                return this;
            }catch(Exception e){
                return this;
            }
        }
        
        /*
        NAME
            public final BoardBuilder SetPiece(final Piece a_piece, final int a_row, final int a_column);
        
        SYNOPSIS
            public final BoardBuilder SetPiece(final Piece a_piece);
        
            Piece a_piece --------> The piece to be set.
        
        DESCRIPTION
            This method returns the BoardBuilder object
            and sets the piece on its spot on the BoardBuilder array.
        
        RETURNS
            The BoardBuilder object.
        
        AUTHOR
            Ryan King
        */
        public final BoardBuilder SetPiece(final Piece a_piece){
            try{
                if(Utilities.HasValidCoordinates(a_piece.GetCurrentRow(), a_piece.GetCurrentColumn())){
                    final Tile original = m_builderBoard[a_piece.GetCurrentRow()][a_piece.GetCurrentColumn()];
                    m_builderBoard[a_piece.GetCurrentRow()][a_piece.GetCurrentColumn()] = new Tile(original.GetColor(), original.GetRow(), original.GetColumn(), a_piece);
                }
                return this;
            }catch(Exception e){
                return this;
            }
        }
        
        /*
        NAME
            public final void BoardBuilder.SetTile(final Tile a_tile, final int a_row, final int a_column);
        
        SYNOPSIS
            public final void BoardBuilder.SetTile(final Tile a_tile, final int a_row, final int a_column);
            
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
                if(Utilities.HasValidCoordinates(a_row, a_column)){
                    m_builderBoard[a_row][a_column] = a_tile;
                }
            }catch(Exception e){
                return;
            }
        }
        
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
            The desired Tile object if coordinates are valid, or null if they're not.
        
        AUTHOR
            Ryan King
        */
        public final Tile GetTile(final int a_row, final int a_column){
            try{
                if(Utilities.HasValidCoordinates(a_row, a_column)){
                    return m_builderBoard[a_row][a_column];
                }else{
                    return null;
                }
            }catch(Exception e){
                return null;
            }
        }
        
        /*
        NAME
            public final ChessColor BoardBuilder.WhoseTurnIsIt();
        
        SYNOPSIS
            public final ChessColor BoardBuilder.WhoseTurnIsIt();
        
            No parameters.
        
        DESCRIPTION
            This method returns whose turn it is, either white or black.
        
        RETURNS
            The ChessColor representing whose turn it is.
        
        AUTHOR
            Ryan King
        */
        public final ChessColor WhoseTurnIsIt(){
            return m_whoseTurn;
        }
    }//End of BoardBuilder class
    
    /*
    NAME
        public final Board Move(final RegularMove a_candidate, final Player a_white, final Player a_black);
    
    SYNOPSIS
        public final Board Move(final Move a_candidate, final Player a_white, final Player a_black);
    
        RegularMove a_candidate --------> The regular move to be performed.

    DESCRIPTION
        This method returns a new Board object with the specified regular move made.
    
    RETURNS
        The Board once the move has been completed.
    
    AUTHOR
        Ryan King
    */
    public final Board Move(final RegularMove a_candidate){
        final int oldRow = a_candidate.GetPiece().GetCurrentRow();
        final int oldColumn = a_candidate.GetPiece().GetCurrentColumn();
        final int newRow = a_candidate.GetNewRow();
        final int newColumn = a_candidate.GetNewColumn();

        final Tile newTile = m_boardObject[newRow][newColumn];
        final Tile oldTile = m_boardObject[oldRow][oldColumn];
                
        // Set the moved Piece to the new Tile
        m_boardObject[newRow][newColumn] = new Tile(newTile.GetColor(), newTile.GetRow(), newTile.GetColumn(), Utilities.DuplicatePiece(a_candidate.GetPiece(), newRow, newColumn));
        // Remove the moved Piece from the old Tile
        m_boardObject[oldRow][oldColumn] = new Tile(oldTile.GetColor(), oldTile.GetRow(), oldTile.GetColumn(), null);
        
        // Initialize a new BoardBuilder object with the configuration of the new Board
        BoardBuilder builder = new BoardBuilder(m_boardObject, Utilities.Reverse(this.WhoseTurnIsIt()));
        
        return builder.Build();
    }
    
    /*
    NAME
        public final Board Attack(final AttackingMove a_candidate);
    
    SYNOPSIS
        public final Board Attack(final AttackingMove a_candidate);
    
        AttackingMove a_candidate --------> The Move to be performed.

    DESCRIPTION
        This method returns a new Board object with the specified attacking move made.
    
    RETURNS
        The Board once the move has been completed.
    
    AUTHOR
        Ryan King
    */
    public final Board Attack(final AttackingMove a_candidate, final Player a_white, final Player a_black){
        final int oldRow = a_candidate.GetPiece().GetCurrentRow();
        final int oldColumn = a_candidate.GetPiece().GetCurrentColumn();
        final int newRow = a_candidate.GetNewRow();
        final int newColumn = a_candidate.GetNewColumn();
        
        final Piece victim = m_boardObject[newRow][newColumn].GetPiece();

        if(this.WhoseTurnIsIt().IsWhite()){
            a_white.AddCapturedPiece(victim);
            a_black.RemoveActivePiece(victim);
        }else{
            a_black.AddCapturedPiece(victim);
            a_white.RemoveActivePiece(victim);
        }
        
        final Tile newTile = m_boardObject[newRow][newColumn];
        final Tile oldTile = m_boardObject[oldRow][oldColumn];
        
        // Set the moved Piece to the new Tile
        m_boardObject[newRow][newColumn] = new Tile(newTile.GetColor(), newTile.GetRow(), newTile.GetColumn(), Utilities.DuplicatePiece(a_candidate.GetPiece(), newRow, newColumn));
        // Remove the moved Piece from the old Tile
        m_boardObject[oldRow][oldColumn] = new Tile(oldTile.GetColor(), oldTile.GetRow(), oldTile.GetColumn(), null);
        
        // Initialize a new BoardBuilder object with the configuration of the new Board
        final BoardBuilder builder = new BoardBuilder(m_boardObject, Utilities.Reverse(this.WhoseTurnIsIt()));
        
        // Return the newly-moved board
        return builder.Build();
    }
    
    /*
    NAME
        public final Board Castle(final CastlingMove a_candidate);
    
    SYNOPSIS
        public final Board Castle(final CastlingMove a_candidate);
    
        CastlingMove a_candidate --------> The Move to be performed.

    DESCRIPTION
        This method returns a new Board object with the specified castling move made.
    
    RETURNS
        The Board once the move has been completed.
    
    AUTHOR
        Ryan King
    */
    public final Board Castle(final CastlingMove a_candidate){
        // Preserve the king's old coordinates
        final int oldKingRow = a_candidate.GetPiece().GetCurrentRow();
        final int oldKingColumn = a_candidate.GetPiece().GetCurrentColumn();
        
        // Grab the king's new coordinates
        final int newKingRow = a_candidate.GetNewRow();
        final int newKingColumn = a_candidate.GetNewColumn();
        
        // Preserve the rook's old coordinates
        final int oldRookRow = a_candidate.GetRookCurrentRow();
        final int oldRookColumn = a_candidate.GetRookCurrentColumn();
        
        // Grab the rook's new coordinates
        final int newRookRow = a_candidate.GetRookDestinationRow();
        final int newRookColumn = a_candidate.GetRookDestinationColumn();
        
        final Tile oldKingTile = m_boardObject[oldKingRow][oldKingColumn];
        final Tile newKingTile = m_boardObject[newKingRow][newKingColumn];
        
        final Tile oldRookTile = m_boardObject[oldRookRow][oldRookColumn];
        final Tile newRookTile = m_boardObject[newRookRow][newRookColumn];
    
        // Set the moved Piece to the new Tile
        m_boardObject[newKingRow][newKingColumn] = new Tile(newKingTile.GetColor(), newKingTile.GetRow(), newKingTile.GetColumn(), Utilities.DuplicatePiece(a_candidate.GetPiece(), newKingRow, newKingColumn));
        // Remove the moved Piece from the old Tile
        m_boardObject[oldKingRow][oldKingColumn] = new Tile(oldKingTile.GetColor(), oldKingTile.GetRow(), oldKingTile.GetColumn(), null);
        
        final Rook rook = (Rook) m_boardObject[oldRookRow][oldRookColumn].GetPiece();
        
        // Set the moved Piece to the new Tile
        m_boardObject[newRookRow][newRookColumn] = new Tile(newRookTile.GetColor(), newRookTile.GetRow(), newRookTile.GetColumn(), Utilities.DuplicatePiece(rook, newRookRow, newRookColumn));
        // Remove the moved Piece from the old Tile
        m_boardObject[oldRookRow][oldRookColumn] = new Tile(oldRookTile.GetColor(), oldRookTile.GetRow(), oldRookTile.GetColumn(), null);
        
        // Initialize a new BoardBuilder object with the configuration of the new Board
        final BoardBuilder builder = new BoardBuilder(m_boardObject, Utilities.Reverse(this.WhoseTurnIsIt()));            

        // Return the newly-moved board
        return builder.Build();
    }
    
    /*
    NAME
        public final Board EnPassant(final EnPassantMove a_candidate, final Player a_white, final Player a_black);
    
    SYNOPSIS
        public final Board EnPassant(final EnPassantMove a_candidate, final Player a_white, final Player a_black);
    
        EnPassantMove a_candidate --------> The Move to be performed.
        
        Player a_white -------------------> The Player playing as white.
        
        Player a_black -------------------> The Player playing as black.
    
    DESCRIPTION
        This method returns a new Board object with the specified Move made.
    
    RETURNS
        The Board once the move has been completed.
    
    AUTHOR
        Ryan King
    */
    public final Board EnPassant(final EnPassantMove a_candidate, final Player a_white, final Player a_black){
        // Preserve the old coordinates of the moving pawn
        final int oldRow = a_candidate.GetPiece().GetCurrentRow();
        final int oldColumn = a_candidate.GetPiece().GetCurrentColumn();
        
        // Get the coordinates where the moving pawn will move
        final int newRow = a_candidate.GetNewRow();
        final int newColumn = a_candidate.GetNewColumn();
        
        // Get the coordinates of the captured pawn,
        // which are different from the destination
        // coordinates due to the nature of en passant
        final int capturedPawnRow = a_candidate.GetCapturedPawnRow();
        final int capturedPawnColumn = a_candidate.GetCapturedPawnColumn();

        final Tile newTile = m_boardObject[newRow][newColumn];
        final Tile oldTile = m_boardObject[oldRow][oldColumn];
        final Tile pawnTile = m_boardObject[capturedPawnRow][capturedPawnColumn];
        
        // Adjust the pieces on both sides depending on the mover and the victim
        if(this.WhoseTurnIsIt() == ChessColor.WHITE){
            a_white.AddCapturedPiece(m_boardObject[capturedPawnRow][capturedPawnColumn].GetPiece());
            a_black.RemoveActivePiece(m_boardObject[capturedPawnRow][capturedPawnColumn].GetPiece());
        }else{
            a_black.AddCapturedPiece(m_boardObject[capturedPawnRow][capturedPawnColumn].GetPiece());
            a_white.RemoveActivePiece(m_boardObject[capturedPawnRow][capturedPawnColumn].GetPiece());
        }
        
        // Set the moved Piece to the new Tile
        m_boardObject[newRow][newColumn] = new Tile(newTile.GetColor(), newTile.GetRow(), newTile.GetColumn(), Utilities.DuplicatePiece(a_candidate.GetPiece(), newRow, newColumn));
        
        // Remove the moved Piece from the old Tile
        m_boardObject[oldRow][oldColumn] = new Tile(oldTile.GetColor(), oldTile.GetRow(), oldTile.GetColumn(), null);
        
        // Remove the captured Pawn from its Tile
        m_boardObject[capturedPawnRow][capturedPawnColumn] = new Tile(pawnTile.GetColor(), pawnTile.GetRow(), pawnTile.GetColumn(), null);
        
        // Initialize a new BoardBuilder object with the configuration of the new Board
        final BoardBuilder builder = new BoardBuilder(m_boardObject, Utilities.Reverse(this.WhoseTurnIsIt()));
        
        // Return the newly-moved board
        return builder.Build();
    }
    
    /*
    NAME
        public final Board Promote(final Piece a_promotedPiece, final Player a_white, final Player a_black);
    
    SYNOPSIS
        public final Board Promote(final Piece a_promotedPiece, final Player a_white, final Player a_black);
    
        Piece a_promotedPiece ------------> The piece to be promoted.
        
        Player a_white -------------------> The Player playing as white.
        
        Player a_black -------------------> The Player playing as black.
    
    DESCRIPTION
        This method returns a new Board object with the promoted pawn replaced
        by the piece the player chose.
    
    RETURNS
        The Board once the move has been completed.
    
    AUTHOR
        Ryan King
    */
    public final Board Promote(final Piece a_promotedPiece){
        
        final int promotedRow = a_promotedPiece.GetCurrentRow();
        final int promotedColumn = a_promotedPiece.GetCurrentColumn();
        
        final Tile promotedTile = this.m_boardObject[promotedRow][promotedColumn];
        
        // Remove the old pawn and put the promoted piece in its place
        this.m_boardObject[promotedRow][promotedColumn] = new Tile(promotedTile.GetColor(), promotedTile.GetRow(), promotedTile.GetColumn(), Utilities.DuplicatePiece(a_promotedPiece));

        // Initialize a new BoardBuilder object with the configuration of the new Board
        BoardBuilder builder = new BoardBuilder(this.m_boardObject, this.WhoseTurnIsIt());
        
        // Return the newly-moved board
        return builder.Build();
    }

    /*
    NAME
        public final Board InternalUndo();
    
    SYNOPSIS
        public final Board InternalUndo();
    
        No parameters.
    
    DESCRIPTION
        This method returns the Board object that was created 1 move ago.
        If the user is attempting to undo the first move, it will simply
        return itself as is. One of these two options will always occur.
    
    RETURNS
        The board from 1 move ago if enough moves have been made,
        or the current board if this is only the first move.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    /*
    public final Board InternalUndo(){
        // Do not allow the player to undo any moves if this is his/her first move
        if(Game.GetGameHistory().size() <= Utilities.ONE){
            return this;
        }else{
            // Get the old board two moves ago
            Board old = Game.GetGameHistory().get(Game.GetGameHistory().size() - Utilities.ONE); 
            
            // Remove the last two boards in the game history array
            Game.GetGameHistory().remove(Game.GetGameHistory().size() - Utilities.ONE);
            
            // Return the old board
            return old;
        }
    }
    */
    /*
    NAME
        public final Board Undo();
    
    SYNOPSIS
        public final Board Undo();
    
        No parameters.
    
    DESCRIPTION
        This method returns the Board object that was created 2 moves ago.
        If the user is attempting to undo the first move, it will simply
        return itself as is. One of these two options will always occur.
    
    RETURNS
        The board from 2 moves ago if enough moves have been made,
        or the current board if this is only the first move.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    /*
    public final Board Undo(){
        if(Game.GetGameHistory().size() <= Utilities.ONE){
            JOptionPane.showMessageDialog(null, "There are not enough moves to undo.", "Error", JOptionPane.ERROR_MESSAGE);
            return this;
        }else{
            // Get the old board two moves ago
            Board old = Game.GetGameHistory().get(Game.GetGameHistory().size() - Utilities.TWO); 
            
            // Remove the last two boards in the game history array
            Game.GetGameHistory().remove(Game.GetGameHistory().size() - Utilities.ONE);
            Game.GetGameHistory().remove(Game.GetGameHistory().size() - Utilities.ONE);
            
            // Return the old board
            return old;
        }
    }
    */
    /*
    public final void PrintGameHistory(){
        if(!m_gameHistory.isEmpty()){
            for(int index = Utilities.ZERO; index + Utilities.ONE < m_gameHistory.size(); index++){
                System.out.print(index + Utilities.ONE + ". " + m_gameHistory.get(index).GetStringMove());
                if(index + Utilities.ONE < m_gameHistory.size()){
                    System.out.print("\t" + m_gameHistory.get(index + Utilities.ONE).GetStringMove());
                }
                System.out.println();            
            }
        }else{
            return;
        }
    }
    */
}//End of Board class