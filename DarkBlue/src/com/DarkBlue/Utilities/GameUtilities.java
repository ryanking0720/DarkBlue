package com.DarkBlue.Utilities;

import java.util.HashMap;
import java.util.Iterator;

import com.DarkBlue.Board.Board;
import com.DarkBlue.Board.Board.BoardBuilder;
import com.DarkBlue.Piece.Bishop;
import com.DarkBlue.Piece.King;
import com.DarkBlue.Piece.Knight;
import com.DarkBlue.Piece.Pawn;
import com.DarkBlue.Piece.PieceType;
import com.DarkBlue.Piece.Queen;
import com.DarkBlue.Piece.Rook;
import com.DarkBlue.Player.Human;
import com.DarkBlue.Player.Player;
/**
 * This interface contains methods that help parse a file in
 * Forsyth-Edwards Notation (FEN).
 * 
 * These include:
 * validating an entire FEN file, 
 * checking to see if both players have a legal distribution of between 1 and 16 pieces, 
 * checking to see if every rank of the board has a sum of exactly eight empty tiles and/or pieces, 
 * checking to see if the side that moves next is a lowercase "w" or "b", 
 * checking to see if castling rights are a valid combination of "KQkq" in that order or "-", 
 * checking to see if the en passant tile is valid or "-", 
 * checking to see if the halfmove clock is at least 0, and
 * checking to see if the fullmove clock is at least 1.
 * 
 * This also contains many variables similar to the Dark Blue chess engine that is meant
 * to parse a FEN file in an isolated environment away from the actual game to determine if the game is playable.
 */
public final class GameUtilities{
    
    public static final String VALID_PIECES = "PpRrNnBbQqKk";
    public static final String[] VALID_CASTLING_RIGHTS_COMBINATIONS = {"KQkq", "KQk", "KQq", "Kkq", "Qkq", "KQ", "Kk", "Kq", "Qk", "Qq", "kq", "K", "Q", "k", "q", "-"};
    
    private static Board m_board = null;
    private static BoardBuilder m_builder = null;
    private static boolean m_canWhiteKingsideCastle = false, m_canWhiteQueensideCastle = false, m_canBlackKingsideCastle = false, m_canBlackQueensideCastle = false;
    private static Player m_white = null, m_black = null, m_currentPlayer = null;
    private static ChessColor m_turn = null;
    private static int m_halfmoves = Utilities.NEGATIVE_ONE;
    private static GameState m_gameState = GameState.EMPTY;
    
    /**/
    /*
    NAME
        public static final boolean IsValidFEN(final String a_string);
    
    SYNOPSIS
        public static final boolean IsValidFEN(final String a_string);
    
        String a_string -----------> The potential FEN string for the engine to read.

    DESCRIPTION
        This method attempts to parse a string that
        could be an FEN string, but it checks for potential
        problems. Any part of the string that is not considered
        a valid part of a FEN string will be found and the method
        will return false. If no problems were found, the method
        will return true.
    
    RETURNS
        boolean: True if this string is a valid FEN string and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King, with help taken from:
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/pgn/FenUtilities.java
    */
    public static final boolean IsValidFEN(final String a_string){
        // Idiot proofing for null or empty arguments
        if(a_string == null || a_string.isBlank()){
            return false;
        }
        
        // Separate all parts
        final String[] PARTS = a_string.split(" ");
        
        // All FEN strings must have six distinct parts
        if(PARTS.length != Utilities.SIX){
            return false;
        }
        
        // The first part contains the board configuration delimited by forward slashes
        final String[] RANKS = PARTS[Utilities.ZERO].split("/");
        
        // All FEN boards must have exactly eight ranks
        if(RANKS == null || RANKS.length != Utilities.EIGHT){
            return false;
        }
        
        // board will hold the board configuration as a single string with no delimiters
        String board = "";

        // Check if each rank is valid
        for(int i = Utilities.ZERO; i < RANKS.length; i++){
            final String RANK = RANKS[i];
            if(HasIncorrectlyPlacedPawns(i, RANK) || !IsValidRank(RANK)){
                return false;
            }
            board += RANK;
        }

        // Make sure both sides have the correct number of pieces
        if(!HasValidPieces(board)){
            return false;
        }
        
        // The second part contains the side to move next
        // Check if the side to move next is either a lowercase "w" or "b"
        if(!(PARTS[Utilities.ONE].equals("w") || PARTS[Utilities.ONE].equals("b"))){
            return false;
        }
        
        // The third part contains the castling rights, if any
        // Check if castling privileges have any of "KQkq" or is only "-"
        if(!HasValidCastlingRights(PARTS[Utilities.TWO])){
            return false;
        }
        
        // The fourth part contains the en passant tile, if any
        // Check if the en passant tile has a lowercase letter from a-h followed by 2 or 7, or is a "-"
        if(!((BoardUtilities.IsValidTile(PARTS[Utilities.THREE]) && (Integer.parseInt(Character.toString(PARTS[Utilities.THREE].charAt(Utilities.ONE))) == Utilities.THREE || Integer.parseInt(Character.toString(PARTS[Utilities.THREE].charAt(Utilities.ONE))) == Utilities.SIX)) || PARTS[Utilities.THREE].equals("-"))){
            return false;
        }
        
        // The fifth part contains the number of halfmoves made since the last capture or pawn movement
        // Check if the halfmove clock is a nonnegative integer (the value must be 0 or above)
        if(!HasValidClock(PARTS[Utilities.FOUR], false)){
            return false;
        }
        
        // The sixth part contains the number of fullmoves made during the entire game, which starts at 1 before the first move is made
        // Check if the fullmove clock is a positive integer (the value must be 1 or above)
        if(!HasValidClock(PARTS[Utilities.FIVE], true)){
            return false;
        }
        
        // Parse the board into a testing environment
        if(!TestParse(a_string)){
            return false;
        }
        
        // This is a valid FEN string
        return true;
    }
    
    /**/
    /*
    NAME
        private static final boolean HasIncorrectlyPlacedPawns(final int a_index, final String a_rank);
    
    SYNOPSIS
        private static final boolean HasIncorrectlyPlacedPawns(final int a_index, final String a_rank);
    
        int a_index --------------> The index from 0 to 7 that represents the board rank.
    
        String a_rank ------------> The string representing the rank configuration.

    DESCRIPTION
        This method parses the FEN rank string with its corresponding index
        to determine if any pawns are on the first or last rank of the board.
        Pawns can only be placed between the first and last ranks until they get
        moved onto one of the outer ranks to get promoted.
    
    RETURNS
        boolean: True if is the first or last rank with at least one pawn and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private static final boolean HasIncorrectlyPlacedPawns(final int a_index, final String a_rank){
        return ((a_index == Utilities.ZERO || a_index == Utilities.SEVEN) && (a_rank.contains("P")  || a_rank.contains("p")));
    }
    
    /**/
    /*
    NAME
        public static final boolean HasValidPieces(final String a_board);
    
    SYNOPSIS
        public static final boolean HasValidPieces(final String a_board);
    
        String a_board -----------> The string representing the board configuration.

    DESCRIPTION
        This method parses the FEN board string to determine if
        both sides have the correct number of each piece.
        Though the numbers placed on the rooks, bishops, knights, and
        queens may seem unnaturally high, they are there to determine 
        if any promotions were made. A player can have as many of these
        pieces as possible, which means 9 queens or 10 knights, rooks, and
        bishops if s/he promotes all of his/her pawns.
    
    RETURNS
        boolean: True if this board has the correct amount of each piece for both sides and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static final boolean HasValidPieces(final String a_board){
        if(a_board == null || a_board.isBlank()){
            return false;
        }
        
        // Both players must have between 1 to 16 pieces of their color       
        final int WHITE_PIECES = Pieces(a_board, ChessColor.WHITE);
        final int BLACK_PIECES = Pieces(a_board, ChessColor.BLACK);
        
        if(!((WHITE_PIECES >= Utilities.ONE && WHITE_PIECES <= Utilities.SIXTEEN) && (BLACK_PIECES >= Utilities.ONE && BLACK_PIECES <= Utilities.SIXTEEN))){
            return false;
        }
        
        // Both players must have 0 to 8 pawns
        if(!(HasCorrectNumberOfPieces(a_board, PieceType.PAWN, ChessColor.WHITE) && HasCorrectNumberOfPieces(a_board, PieceType.PAWN, ChessColor.BLACK))){
            return false;
        }
        
        // Both players must have 0 to 10 rooks
        if(!(HasCorrectNumberOfPieces(a_board, PieceType.ROOK, ChessColor.WHITE) && HasCorrectNumberOfPieces(a_board, PieceType.ROOK, ChessColor.BLACK))){
            return false;
        }
        
        // Both players must have 0 to 10 knights
        if(!(HasCorrectNumberOfPieces(a_board, PieceType.KNIGHT, ChessColor.WHITE) && HasCorrectNumberOfPieces(a_board, PieceType.KNIGHT, ChessColor.BLACK))){
            return false;
        }
        
        // Both players must have 0 to 10 bishops
        if(!(HasCorrectNumberOfPieces(a_board, PieceType.BISHOP, ChessColor.WHITE) && HasCorrectNumberOfPieces(a_board, PieceType.BISHOP, ChessColor.BLACK))){
            return false;
        }
        
        // Both players must have 0 to 9 queens
        if(!(HasCorrectNumberOfPieces(a_board, PieceType.QUEEN, ChessColor.WHITE) && HasCorrectNumberOfPieces(a_board, PieceType.QUEEN, ChessColor.BLACK))){
            return false;
        }
        
        // Both players must have exactly one king
        if(!(HasCorrectNumberOfPieces(a_board, PieceType.KING, ChessColor.WHITE) && HasCorrectNumberOfPieces(a_board, PieceType.KING, ChessColor.BLACK))){
            return false;
        }
        
        return true;
    }
    
    /**/
    /*
    NAME
        public static final boolean HasValidCastlingRights(final String a_castlingRights);
    
    SYNOPSIS
        public static final boolean HasValidCastlingRights(final String a_castlingRights);
    
        String a_castlingRights -----------> The string representing the castling rights for both players.

    DESCRIPTION
        This method parses the FEN castling rights string to determine if
        the argument given is valid.
        Notice how all individual rights for both sides of both players are evaluated
        independently of one another.
    
    RETURNS
        boolean: True if this string contains a valid combination of castling rights and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static final boolean HasValidCastlingRights(final String a_castlingRights){
        // Check to see if the argument is valid
        for(final String COMBO : VALID_CASTLING_RIGHTS_COMBINATIONS){
            if(a_castlingRights.equals(COMBO)){
                return true;
            }
        }
        
        // None of the strings were matches; the argument is invalid
        return false;
    }
    
    /**/
    /*
    NAME
        public static final boolean HasValidClock(final String a_clock, final boolean a_isFullmoveClock);
    
    SYNOPSIS
        public static final boolean HasValidClock(final String a_clock, final boolean a_isFullmoveClock);
    
        String a_clock -----------> The string representing the fullmove or halfmove clock.

    DESCRIPTION
        This method parses the clock string to see if it's either a nonnegative integer (halfmove),
        or a positive integer (fullmove). A Boolean flag is passed in to determine which type of move clock
        is being analyzed. 
        
        Passing true as the flag means the clock is the fullmove clock and the number must be positive.
        Passing false as the flag means the clock is the halfmove clock and the number must be positive or zero.
    
    RETURNS
        boolean: True if this clock is a valid integer that is either 0 or positive for a halfmove clock or positive for a fullmove clock and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static final boolean HasValidClock(final String a_clock, final boolean a_isFullmoveClock){
        try{
            // Attempt to parse the clock into an integer
            final int CLOCK = Integer.parseInt(a_clock);
            
            // The halfmove clock must be zero or positive
            // The fullmove clock must only be positive
            final int LIMIT = (a_isFullmoveClock ? Utilities.ONE : Utilities.ZERO);
            
            // Any clock value that lies below the limit is invalid
            // Any halfmove clock above fifty is also invalid
            if(CLOCK < LIMIT || (!a_isFullmoveClock && CLOCK > Utilities.FIFTY)){
                return false;
            }
            
            return true;
        }catch(NumberFormatException e){
            // Non-numeric clocks are invalid
            return false;
        }
    }
    
    /**/
    /*
    NAME
        public static final boolean IsValidRank(final String a_rank);
    
    SYNOPSIS
        public static final boolean IsValidRank(final String a_rank);
    
        String a_rank -----------> The FEN rank for the parser to read.

    DESCRIPTION
        This method attempts to parse a string that
        could be an FEN rank, but it checks for potential
        problems. These problems include invalid letters or
        the number of pieces plus empty tiles not adding to
        exactly 8, a null or empty string, or a string that
        has a length higher than 8.
    
    RETURNS
        boolean: True if this string is a valid FEN rank and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static final boolean IsValidRank(final String a_rank){
        // Null, blank, or inappropriately-sized arguments are not valid ranks
        if(a_rank == null || a_rank.isBlank() || a_rank.length() > Utilities.EIGHT){
            return false;
        }
        
        // emptyTiles will hold the sum of the number of empty tiles found, which when added to occupiedTiles should not exceed 8
        int emptyTiles = Utilities.ZERO;
        
        // occupiedTiles will hold the number of valid pieces found, which when added to emptyTiles should not exceed 8
        int occupiedTiles = Utilities.ZERO;

        // Iterate through every character of the string
        for(int i = Utilities.ZERO; i < a_rank.length(); i++){
            
            // This is a block of empty tiles
            if(Character.isDigit(a_rank.charAt(i))){
                final int DIGIT = Integer.parseInt(Character.toString(a_rank.charAt(i)));
                
                // This digit was not valid
                if(DIGIT >= Utilities.NINE || DIGIT <= Utilities.ZERO){
                    return false;
                }
                
                emptyTiles += DIGIT;
            }else{
                // This could be a piece
                if(!VALID_PIECES.contains(Character.toString(a_rank.charAt(i)))){
                    // This character was not a valid piece; do not continue
                    return false;
                }else{
                    // This character was a valid piece; continue
                    occupiedTiles++;
                }
            }
        }       
        
        // Make sure the number of occupied tiles added to the number of empty tiles is exactly 8
        return ((occupiedTiles + emptyTiles) == Utilities.EIGHT);
    }
    
    /**/
    /*
    NAME
        public static final boolean HasCorrectNumberOfPieces(final String a_board, final PieceType a_type, final ChessColor a_color);
    
    SYNOPSIS
        public static final boolean HasCorrectNumberOfPieces(final String a_board, final PieceType a_type, final ChessColor a_color);
    
        String a_board -----------> The FEN board for the parser to read.
        
        PieceType a_type ---------> The type of piece to check.
        
        ChessColor a_color -------> The color to check.

    DESCRIPTION
        This method attempts to find if there is the correct number of
        occurrences of a piece letter (capital if a_color is white or lowercase
        if a_color is black). If there end up being the wrong amount of occurrences,
        this method will fail and return false.
    
    RETURNS
        boolean: True if there is the correct amount of pieces and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static final boolean HasCorrectNumberOfPieces(final String a_board, final PieceType a_type, final ChessColor a_color){
        // letters will hold the number of pieces found
        int letters = Utilities.ZERO;

        // LIMIT will hold the maximum number of occurrences the letter should have in a valid FEN file
        // Both cases for capital (white) and lowercase (black) letters are evaluated separately
        final int LIMIT = a_type.GetLimit();
        
        // letter will hold the letter of the piece regardless of case differences
        char letter = a_type.ToCharacter();
        
        // ACTUAL will hold the piece character in the correct case
        final char ACTUAL = (a_color.IsWhite() ? Character.toUpperCase(letter) : Character.toLowerCase(letter));
        
        // Find the number of occurrences of this character on the board string
        for(int i = Utilities.ZERO; i < a_board.length(); i++){
            if(ACTUAL == a_board.charAt(i)){
                letters++;
            }
        }
        
        // Only return true if there was exactly one K or k found
        // or if the number of other characters found did not exceed the prescribed limit
        if(a_type == PieceType.KING){
            return letters == LIMIT;
        }else{
            return letters <= LIMIT;
        }
    }
    
    /**/
    /*
    NAME
        public static final int Pieces(final String a_board, final ChessColor a_color);
    
    SYNOPSIS
        public static final int Pieces(final String a_board, final ChessColor a_color);
    
        String a_board -----------> The FEN board for the parser to read.

        ChessColor a_color -------> The color to check.

    DESCRIPTION
        This method finds the number of pieces of a certain color.
        This is used when determining if each side has pieces in the range [1, 16].
    
    RETURNS
        int pieces: The number of black or white pieces, depending on the color passed in.
    
    AUTHOR
        Ryan King
    */
    public static final int Pieces(final String a_board, final ChessColor a_color){
        int pieces = Utilities.ZERO;
        
        // Null or empty arguments do not return anything of value
        if(a_board == null || a_board.isBlank() || a_color == null){
            return pieces;
        }
        
        // Iterate through every character on the board
        for(int i = Utilities.ZERO; i < a_board.length(); i++){
            if(a_color.IsWhite()){
                if(Character.isUpperCase(a_board.charAt(i))){
                    pieces++;
                }
            }else{
                if(Character.isUpperCase(a_board.charAt(i))){
                    pieces++;
                }
            }
        }
        
        return pieces;
    }
    
    /**/
    /*
    NAME
        public static final String ExpandRank(final String a_rank);
    
    SYNOPSIS
        public static final String ExpandRank(final String a_rank);
    
        String a_rank ----------> A rank in FEN notation to expand.
    
    DESCRIPTION
        This method expands a string that represents the rank of a chessboard
        by replacing all of its numbers with that many hyphen-minuses.
        For example, if the string is R5K2, it will expand to R-----K--,
        which will be parsed by the next phase of deserialization.
        All numbers are checked in descending order every time,
        even if the given string does not have every number.
    
    RETURNS
        String: The rank string with all numbers replaced by hyphen-minuses
        as described above.
    
    AUTHOR
        Ryan King, with help taken from:
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/pgn/FenUtilities.java
    */
    public static final String ExpandRank(final String a_rank){
        return a_rank.replaceAll("8", "--------").replaceAll("7", "-------").replaceAll("6", "------").replaceAll("5", "-----").replaceAll("4", "----").replaceAll("3", "---").replaceAll("2", "--").replaceAll("1", "-");
    }
    
    /**/
    /*
    NAME
        private final void TestParse(final String a_FENString, final boolean a_isSerializedGame);
    
    SYNOPSIS
        private final void TestParse(final String a_FENString, final boolean a_isSerializedGame);
    
        String a_FENString -----------> The game to read in Forsyth-Edwards Notation.
        
        boolean a_isSerializedGame ---> If the game is being resumed from a serialization string, as opposed to being the result of an undone move.
    
    DESCRIPTION
        This method parses a string in FEN format in this isolated
        testing environment before anything gets changed in the main chess engine.
        It returns if it encounters a problem.
        Several unnecessary sections of code found in this method's counterpart in the
        actual engine have been removed, namely fullmove parsing and castling rights.
        Lots of this code has been repeated from the "DarkBlue.java" file.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, with help taken from:
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/pgn/FenUtilities.java
    */
    private static final boolean TestParse(final String a_FENString){
        // PARTS will hold the six parts of the FEN string
        final String[] PARTS = a_FENString.split(" ");
        
        // BUILDER will be a template for building the new board
        m_builder = new BoardBuilder();
        
        // PARTS[0] represents the configuration of the board
        final String[] BOARD = PARTS[Utilities.ZERO].split("/");    

        // PARTS[1] determines whose turn it is
        if(PARTS[Utilities.ONE].equalsIgnoreCase("w")){
            m_builder.SetWhoseTurn(ChessColor.WHITE);
            
        }else if(PARTS[Utilities.ONE].equalsIgnoreCase("b")){
            m_builder.SetWhoseTurn(ChessColor.BLACK);
        }else{
            return false;
        }
        
        m_turn = m_builder.WhoseTurnIsIt();
        
        // PARTS[2] determines which sides can castle and where,
        // which need not be feasible on the current turn
        if(PARTS[Utilities.TWO].equals("-")){
            m_canWhiteKingsideCastle = false;
            m_canWhiteQueensideCastle = false;
            m_canBlackKingsideCastle = false;
            m_canBlackQueensideCastle = false;
        }else{
            
            if(PARTS[Utilities.TWO].contains("K")){
                m_canWhiteKingsideCastle = true;
            }else{
                m_canWhiteKingsideCastle = false;
            }
        
            if(PARTS[Utilities.TWO].contains("Q")){
                m_canWhiteQueensideCastle = true;
            }else{
                m_canWhiteQueensideCastle = false;
            }
        
            if(PARTS[Utilities.TWO].contains("k")){
                m_canBlackKingsideCastle = true;
            }else{
                m_canBlackKingsideCastle = false;
            }
        
            if(PARTS[Utilities.TWO].contains("q")){
                m_canBlackQueensideCastle = true;
            }else{
                m_canBlackQueensideCastle = false;
            }
        }
        
        
        // Set up the board
        try{
            for(int i = Utilities.ZERO; i < BOARD.length; i++){
                ParseRank(i, BOARD[i], m_builder);
            }
        }catch(Exception e){
            return false;
        }
        
        // Set the board configuration
        m_board = m_builder.Build();
        
        // Validate castling rights
        if(!IsCastlingRightValid(m_board, ChessColor.WHITE, m_canWhiteKingsideCastle, true)){
            return false;
        }
        
        if(!IsCastlingRightValid(m_board, ChessColor.WHITE, m_canWhiteQueensideCastle, false)){
            return false;
        }
        
        if(!IsCastlingRightValid(m_board, ChessColor.BLACK, m_canBlackKingsideCastle, true)){
            return false;
        }
        
        if(!IsCastlingRightValid(m_board, ChessColor.BLACK, m_canBlackQueensideCastle, false)){
            return false;
        }
             
        // PARTS[4] contains the number of halfmoves that have occurred since the last capture or pawn movement
        m_halfmoves = Integer.parseInt(PARTS[Utilities.FOUR]);

        // Set the current player
        m_currentPlayer = (m_board.WhoseTurnIsIt().IsWhite() ? m_white : m_black);
        
        // The parse was successful
        return true;
    }
    
    /**/
    /*
    NAME
        private static final boolean IsCastlingRightValid(final Board a_board, final ChessColor a_color, final boolean a_canCastle, final boolean a_isKingside);
    
    SYNOPSIS
        private static final boolean IsCastlingRightValid(final Board a_board, final ChessColor a_color, final boolean a_canCastle, final boolean a_isKingside);
    
        Board a_board ----------> The current board, used to verify castling rights.
        
        ChessColor a_color -----> The side to check.
        
        boolean a_canCastle ----> The castling right to check.
        
        boolean a_isKingside ---> If the right specified above is a kingside or queenside castle.
    
    DESCRIPTION
        This method determines if one castling right
        is valid for a particular side.
        
        If an unmoved rook is not found and the right is true, this castling right is invalid and the game is unplayable.
        
        If a moved rook is found and the right is true, this right is not valid but the game is still playable.
        
        If a moved rook is found and the right is false, this is valid and playable.
        
        If an unmoved rook is found and the right is false, the rook will be changed but this game is still playable.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private static final boolean IsCastlingRightValid(final Board a_board, final ChessColor a_color, final boolean a_canCastle, final boolean a_isKingside){
        final King KING = a_board.GetKing(a_color);
        final int ROOK_ROW = KING.GetCurrentRow();
        final int ROOK_COLUMN = (a_isKingside ? Utilities.SEVEN : Utilities.ZERO);
        
        // Do not allow this to pass if the FEN rights specified say
        // the king has a rook to castle with on that side while he really has none
        if(a_isKingside){
            if(!KING.HasKingsideCastlingRook(a_board) && a_canCastle){
                return false;
            }else{
                return true;
            }
        }else{
            if(!KING.HasQueensideCastlingRook(a_board) && a_canCastle){
                return false;
            }else{
                return true;
            }
        }
    }
     
    /**/
    /*
    NAME
        public static final void ParseRank(final int a_row, final String a_rank, final BoardBuilder a_builder);
    
    SYNOPSIS
        public static final void ParseRank(final int a_row, final String a_rank, final BoardBuilder a_builder);
    
        int a_row ------------------> The row of the rank, 0 to 7.
        
        String a_rank --------------> The actual rank with numbers replaced by hyphen minuses.
        
        BoardBuilder a_builder -----> The BoardBuilder which will contain the placement of all pieces found.
    
    DESCRIPTION
        This method parses a rank string that has been stripped of its numbers
        by checking each character individually. If it is a hyphen-minus, that
        tile is considered empty and the parser moves on. If it is a capital letter,
        it is a white piece and is instantiated as such. If it is a lowercase letter,
        it is a black piece and is instantiated as such. If an invalid character is detected,
        the method will throw an exception.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, with help taken from:
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/pgn/FenUtilities.java
    */
    public static final void ParseRank(final int a_row, final String a_rank, final BoardBuilder a_builder) throws Exception{
        if(!BoardUtilities.HasValidValue(a_row) || a_rank == null || a_rank.isBlank() || a_builder == null){
            throw new Exception("Improper file format");
        }
        
        // Remove the numbers representing empty tiles from the string
        // and replace them with hyphen-minuses.
        final String NO_NUMBERS = GameUtilities.ExpandRank(a_rank);
        
        // Iterate through all 8 spots of the string
        for(int index = Utilities.ZERO; index < NO_NUMBERS.length(); index++){
            
            final char PIECE = NO_NUMBERS.charAt(index);
            if(PIECE == '-'){
                // The parser found an empty tile
                continue;
            }else if(Character.isLetter(PIECE)){
                // The parser found a piece
                switch(PIECE){
                    case Utilities.WHITE_PAWN_ICON: a_builder.SetPiece(new Pawn(ChessColor.WHITE, a_row, index));
                    break;
                    case Utilities.WHITE_QUEEN_ICON: a_builder.SetPiece(new Queen(ChessColor.WHITE, a_row, index));
                    break;
                    case Utilities.WHITE_KING_ICON: a_builder.SetPiece(Factory.KingFactory(ChessColor.WHITE, a_row, index, m_canWhiteKingsideCastle, m_canWhiteQueensideCastle));
                    break;
                    case Utilities.WHITE_BISHOP_ICON: a_builder.SetPiece(new Bishop(ChessColor.WHITE, a_row, index));
                    break;
                    case Utilities.WHITE_KNIGHT_ICON: a_builder.SetPiece(new Knight(ChessColor.WHITE, a_row, index));
                    break;
                    case Utilities.WHITE_ROOK_ICON: a_builder.SetPiece(Factory.RookFactory(ChessColor.WHITE, a_row, index, m_canWhiteKingsideCastle, m_canWhiteQueensideCastle));
                    break;
                    case Utilities.BLACK_PAWN_ICON: a_builder.SetPiece(new Pawn(ChessColor.BLACK, a_row, index));
                    break;
                    case Utilities.BLACK_QUEEN_ICON: a_builder.SetPiece(new Queen(ChessColor.BLACK, a_row, index));
                    break;
                    case Utilities.BLACK_KING_ICON: a_builder.SetPiece(Factory.KingFactory(ChessColor.BLACK, a_row, index, m_canBlackKingsideCastle, m_canBlackQueensideCastle));
                    break;
                    case Utilities.BLACK_BISHOP_ICON: a_builder.SetPiece(new Bishop(ChessColor.BLACK, a_row, index));
                    break;
                    case Utilities.BLACK_KNIGHT_ICON: a_builder.SetPiece(new Knight(ChessColor.BLACK, a_row, index));
                    break;
                    case Utilities.BLACK_ROOK_ICON: a_builder.SetPiece(Factory.RookFactory(ChessColor.BLACK, a_row, index, m_canBlackKingsideCastle, m_canBlackQueensideCastle));
                    break;
                    default: throw new Exception("Improper file format");
                }
            }else{
                // The parser found an invalid character
                throw new Exception("Improper file format");
            }
        }
    }
    
    /**/
    /*
    NAME
        public static final GameState EvaluateGameState(final Player a_player, final Player a_opponent, final Board a_board, final int a_halfmoves, final HashMap<String, Integer> a_positions);
    
    SYNOPSIS
        public static final GameState EvaluateGameState(final Player a_player, final Player a_opponent, final Board a_board, final int a_halfmoves, final HashMap<String, Integer> a_positions);
        
        Player a_player --------------------------> The player to evaluate.
        
        Player a_opponent ------------------------> The opponent, used to evaluate insufficient material.
        
        Board a_board ----------------------------> The board to evaluate.
        
        int a_halfmoves --------------------------> The number of one-sided moves since the last capture or pawn movement.
        
        HashMap<String, Integer> a_positions -----> Every unique position of the board that has occurred during the game and how many times each position has occurred.
    
    DESCRIPTION
        This method determines the state of the game given the two players and the board.
        It returns one of the following:
        
        GameState.CHECK: One player's king is in check. He must either move out of it,
        capture the threatening piece himself, use a friendly piece to 
        capture the threatening piece, or use a friendly piece to 
        block the check, assuming the threatening piece moves linearly.
        The game resumes as normal after the player removes the threat.
        
        GameState.STALEMATE: One player's king is not in check but has no legal moves.
        None of the player's other pieces have any legal moves either. 
        The game ends in a draw.
        
        GameState.CHECKMATE: One player's king is in check 
        in his current position and wherever he moves.
        He cannot escape, he cannot capture the piece(s) putting him into check,
        no other friendly piece can capture the piece(s) putting him into check,
        and no friendly piece can block the check if the threatening piece is not a knight or pawn. 
        Any other friendly pieces with moves that would not help the king are not allowed to move.
        The other player wins and the game is over.
        
        GameState.INSUFFICIENT_MATERIAL: The game can end this way due to a number of conditions:
        
            1. White and black have only bare kings left on the board.
        
            2. One player has a king and the other has a king and a bishop.
        
            3. One player has a king and the other has a king and a knight.
        
            4. Both players have a king and a bishop and both bishops 
               move on the same tile color.
                
        GameState.FIFTY_MOVE_RULE: Fifty halfmoves have been made with no capture or pawn movement.
        The game ends in a draw.
        
        GameState.THREEFOLD_REPETITION: The same configuration of the board has been repeated three times.
        However many times this happens need not be consecutive. The game ends in a draw.
        This will not be evaluated if the HashMap is passed in as null.

        GameState.NORMAL: The game proceeds as normal.
        
        The special GameState.EMPTY status is not assigned here, as it is only used when the 
        engine starts up initially or if the game is stopped or saved.
    
    RETURNS
        One of the GameState variables described above.
    
    AUTHOR
        Ryan King
    */
    public static final GameState EvaluateGameState(final Player a_player, final Player a_opponent, final Board a_board, final int a_halfmoves, final HashMap<String, Integer> a_positions){
        // Check for every game state described above except for EMPTY
        if(a_player.IsInCheckmate(a_board)){
            return GameState.CHECKMATE;
        }else if(a_player.IsInStalemate(a_board)){
            return GameState.STALEMATE;
        }else if(GameUtilities.IsDrawByInsufficientMaterial(a_player, a_opponent, a_board)){
            return GameState.INSUFFICIENT_MATERIAL;
        }else if(GameUtilities.IsDrawByFiftyMoveRule(a_halfmoves)){
            return GameState.FIFTY_MOVE_RULE;
        }else if(a_positions != null && GameUtilities.IsDrawByThreefoldRepetition(a_positions)){
            return GameState.THREEFOLD_REPETITION;
        }else if(a_player.IsInCheck(a_board)){
            return GameState.CHECK;
        }else{
            return GameState.NORMAL;
        }
    }
    
    /**/
    /*
    NAME
        public static final boolean IsDrawByInsufficientMaterial(final Player a_player, final Player a_opponent, final Board a_board);
    
    SYNOPSIS
        public static final boolean IsDrawByInsufficientMaterial(final Player a_player, final Player a_opponent, final Board a_board);
    
        Player a_player ---------> The player to check.
        
        Player a_opponent -------> The opponent.
        
        Board a_board -----------> The board.
    
    DESCRIPTION
        This method checks to see if any draw condition by
        insufficient material exists. 
        This can occur in one of several ways:
        
        1. Both sides have bare kings. Since kings cannot be captured
        nor give check, the game ends in a draw.
        
        2. One side has a bare king and the other has a king and a knight.
        
        3. One side has a bare king and the other has a king and a bishop.
        
        4. Both sides have a king and a bishop and the bishops are on tiles
        of the same color.
        
        If any of these conditions are met, the method returns true.
        If not, it returns false.
    
    RETURNS
        True if the players have insufficient material and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static final boolean IsDrawByInsufficientMaterial(final Player a_player, final Player a_opponent, final Board a_board){
        // Check for all possible draw cases explained above
        if(a_player.HasBareKing() && a_opponent.HasBareKing()){
            return true;
        }else if(a_player.HasBareKing() && a_opponent.HasKingAndBishop()){
            return true;
        }else if(a_player.HasBareKing() && a_opponent.HasKingAndKnight()){
            return true;
        }else if(a_opponent.HasBareKing() && a_player.HasKingAndBishop()){
            return true;
        }else if(a_opponent.HasBareKing() && a_player.HasKingAndKnight()){
            return true;
        }else if(a_player.HasKingAndBishop() && a_opponent.HasKingAndBishop()){
            int playerBishopRow = Utilities.NEGATIVE_ONE, playerBishopColumn = Utilities.NEGATIVE_ONE, 
                    opponentBishopRow = Utilities.NEGATIVE_ONE, opponentBishopColumn = Utilities.NEGATIVE_ONE;
            
            // Find both bishops
            for(int index = Utilities.ZERO; index < Utilities.TWO; index++){
                if(a_player.GetActivePieces().get(index).IsBishop()){
                    playerBishopRow = a_player.GetActivePieces().get(index).GetCurrentRow();
                    playerBishopColumn = a_player.GetActivePieces().get(index).GetCurrentColumn();
                }
                if(a_opponent.GetActivePieces().get(index).IsBishop()){
                    opponentBishopRow = a_opponent.GetActivePieces().get(index).GetCurrentRow();
                    opponentBishopColumn = a_opponent.GetActivePieces().get(index).GetCurrentColumn();
                }
            }
            
            // End the game if the bishops are on tiles of the same color
            if(a_board.GetTile(playerBishopRow, playerBishopColumn).GetColor() == a_board.GetTile(opponentBishopRow, opponentBishopColumn).GetColor()){
                return true;
            }
        }
        return false;
    }
    
    /**/
    /*
    NAME
        public static final boolean IsDrawByThreefoldRepetition(final HashMap<String, Integer> a_positions);
    
    SYNOPSIS
        public static final boolean IsDrawByThreefoldRepetition(final HashMap<String, Integer> a_positions);
    
        HashMap<String, Integer> a_positions ------> The FEN positions of the board and how many times an individual position has occurred.
    
    DESCRIPTION
        This method checks to see if any draw condition by
        threefold repetition exists. If a configuration is found
        to be repeated three times, regardless of whether or not
        this was consecutive, a draw can be claimed.
        
        Typically this happens when one player's
        king is put into check many times.
    
    RETURNS
        True if the same board configuration has occurred three times either consecutively or non-consecutively
        and false otherwise. One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static final boolean IsDrawByThreefoldRepetition(final HashMap<String, Integer> a_positions){
        // Idiot proofing
        if(a_positions == null || a_positions.isEmpty()){
            return false;
        }
        
        // Make the hashmap iterable
        final Iterator<String> POSITION_ITERATION = a_positions.keySet().iterator();
        
        // Look through every position in the hash
        while(POSITION_ITERATION.hasNext()){
            final String CURRENT = POSITION_ITERATION.next();
            if(a_positions.get(CURRENT) == Utilities.THREE){
                return true;
            }
        }
        
        return false;
    }
    
    /**/
    /*
    NAME
        private static final boolean IsDrawByFiftyMoveRule();
    
    SYNOPSIS
        private static final boolean IsDrawByFiftyMoveRule();
    
        No parameters.
    
    DESCRIPTION
        This method checks to see if any draw condition by
        the fifty-move rule exists.
        
        This occurs when fifty moves have been made without a 
        single pawn movement or capture of any kind.
        
        If this condition is met, the method returns true.
        If not, it returns false.
    
    RETURNS
        True if the halfmove clock is at least fifty and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static final boolean IsDrawByFiftyMoveRule(int a_currentHalfmoves){
        return a_currentHalfmoves >= Utilities.FIFTY;
    }
    
    /**/
    /*
    NAME
        public static final GameState GetPlayerGameState(final String a_string, final boolean a_isMover);
    
    SYNOPSIS
        public static final GameState GetPlayerGameState(final String a_string, final boolean a_isMover);
    
        String a_string ------------> The serialization string to parse.
        
        boolean a_isMover ----------> If the player being parsed is the one moving first as per the file.
    
    DESCRIPTION
        This method checks the game state from the perspective of the
        player either given by the "b" or "w" on the given FEN string
        or the opposite if the Boolean flag is false.
        If this state is anything but check or normal, this game is 
        considered to be unplayable and a message is displayed to the user.
        If this is the opponent, anything but normal is considered unplayable.
    
    RETURNS
        GameState: CHECKMATE, STALEMATE, FIFTY_MOVE_RULE, 
        INSUFFICIENT_MATERIAL, CHECK, or NORMAL depending on the game.
        The other game states cannot be evaluated from an FEN string 
        which is why they do not appear.
    
    AUTHOR
        Ryan King
    */
    public static final GameState GetPlayerGameState(final String a_string, final boolean a_isMover){
        // Do not bother if the file is invalid
        if(!TestParse(a_string)){
            return GameState.EMPTY;
        }

        // Instantiate and initialize dummy players
        m_white = new Human(ChessColor.WHITE, m_board);
        m_black = new Human(ChessColor.BLACK, m_board);
        
        m_white.Refresh(m_board);
        m_black.Refresh(m_board);
        
        // Assign aliases for convenience
        if(a_isMover){
            m_currentPlayer = (m_board.WhoseTurnIsIt().IsWhite() ? m_white : m_black);
        }else{
            m_currentPlayer = (m_board.WhoseTurnIsIt().IsWhite() ? m_black : m_white);
        }
        
        final Player OPPONENT = (m_currentPlayer.IsWhite() ? m_black : m_white);
        
        // Return the game state from the current player's perspective
        return EvaluateGameState(m_currentPlayer, OPPONENT, m_board, m_halfmoves, null);
    }
    
    /**/
    /*
    NAME
        public static final boolean IsPlayable(final String a_string, final ChessColor a_humanColor);
    
    SYNOPSIS
        public static final boolean IsPlayable(final String a_string, final ChessColor a_humanColor);
    
        String a_string ------------> The serialization string to parse.
        
        ChessColor a_humanColor ----> The human's color.
    
    DESCRIPTION
        This method checks to see if a valid FEN string is playable.
        This means that neither player is in checkmate, the game has not been drawn
        (this excludes threefold repetition), both players are not in check at the same
        time, and if a player is in check, that player is slated to move next by the 
        second field on the FEN string denoting such. If any of these statements are false, 
        the game is considered unplayable and a message will be displayed to the user.
    
    RETURNS
        boolean: True if this game is playable and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static final boolean IsPlayable(final String a_string, final ChessColor a_humanColor){
        // Idiot proofing
        if(!IsValidFEN(a_string) || a_humanColor == null){
            return false;
        }
        
        // Parse this string in an isolated testing environment away from the actual game
        TestParse(a_string);

        // Construct and initialize two dummy players
        m_white = new Human(ChessColor.WHITE, m_board);
        m_black = new Human(ChessColor.BLACK, m_board);
        
        m_white.Refresh(m_board);
        m_black.Refresh(m_board);
        
        String rights = a_string.split(" ")[Utilities.TWO];
        if(!AreCastlingRightsValid(rights)){
            return false;
        }

        // Assign aliases to both players for readability
        final Player HUMAN = (a_humanColor.IsWhite() ? m_white : m_black);
        final Player COMPUTER = (HUMAN.IsWhite() ? m_black : m_white);
        
        // Find the game state from the point of view of both players, which can both be different
        final GameState HUMAN_STATE = EvaluateGameState(HUMAN, COMPUTER, m_board, m_halfmoves, null), COMPUTER_STATE = EvaluateGameState(COMPUTER, HUMAN, m_board, m_halfmoves, null);
        
        // Assign aliases to denote the game states by color
        final GameState WHITE_STATE = (HUMAN.IsWhite() ? HUMAN_STATE : COMPUTER_STATE), BLACK_STATE = (COMPUTER.IsBlack() ? COMPUTER_STATE : HUMAN_STATE);

        // The only game states allowed for a playable game are normal and check
        // Remove any extraneous game states
        if(!(WHITE_STATE == GameState.NORMAL || WHITE_STATE == GameState.CHECK) || !(BLACK_STATE == GameState.NORMAL || BLACK_STATE == GameState.CHECK)){
            return false;
        }
        
        // Both players cannot be in check at the same time
        if(WHITE_STATE == GameState.CHECK && BLACK_STATE == GameState.CHECK){
            return false;
        }
        
        // If only one state is check, see if the player playing next is the one placed in check.
        if(WHITE_STATE == GameState.CHECK){
            if(m_board.WhoseTurnIsIt().IsWhite()){
                return true;
            }else{
                return false;
            }
        }
        
        if(BLACK_STATE == GameState.CHECK){
            if(m_board.WhoseTurnIsIt().IsWhite()){
                return false;
            }else{
                return true;
            }
        }
        
        return true;
    }
    
    /**/
    /*
    NAME
        private static final void ParseCastlingRights(final String a_rights);
    
    SYNOPSIS
        private static final void ParseCastlingRights(final String a_rights);
    
        String a_rights -----------> The string representing castling rights.

    DESCRIPTION
        This method parses a string with the letters "KQkq"
        to determine castling rights for both sides.
        No input validation is performed because the rights
        have already been validated previously in the program.
        This is a copy of the method from "DarkBlue.java".
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private static final void ParseCastlingRights(final String a_rights){
        if(a_rights.equals("-")){
            m_canWhiteKingsideCastle = false;
            m_canWhiteQueensideCastle = false;
            m_canBlackKingsideCastle = false;
            m_canBlackQueensideCastle = false;
        }else{           
            if(a_rights.contains("K")){
                m_canWhiteKingsideCastle = true;
            }else{
                m_canWhiteKingsideCastle = false;
            }
        
            if(a_rights.contains("Q")){
                m_canWhiteQueensideCastle = true;
            }else{
                m_canWhiteQueensideCastle = false;
            }
        
            if(a_rights.contains("k")){
                m_canBlackKingsideCastle = true;
            }else{
                m_canBlackKingsideCastle = false;
            }
        
            if(a_rights.contains("q")){
                m_canBlackQueensideCastle = true;
            }else{
                m_canBlackQueensideCastle = false;
            }
        }
    }
    
    /**/
    /*
    NAME
        public static final boolean AreCastlingRightsValid(final String a_rights);
    
    SYNOPSIS
        public static final boolean AreCastlingRightsValid(final String a_rights);
    
        String a_rights ------------> The castling rights.
    
    DESCRIPTION
        This method checks to see if a string of castling rights
        is valid per the configuration of the board.
    
    RETURNS
        boolean: True if all four rights are valid and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static final boolean AreCastlingRightsValid(final String a_rights){
        // Idiot proofing
        if(a_rights.isBlank()){
            return false;
        }
        
        // Find both kings
        final King WHITE_KING = m_board.GetKing(ChessColor.WHITE);
        final King BLACK_KING = m_board.GetKing(ChessColor.BLACK);
        
        // See if all rights are valid one at a time
        if(!a_rights.equals("-")) {
            if(a_rights.contains("K") && !WHITE_KING.HasKingsideCastlingRook(m_board)){
                return false;
            }
        
            if(a_rights.contains("Q") && !WHITE_KING.HasQueensideCastlingRook(m_board)){
                return false;
            }
        
            if(a_rights.contains("k") && !BLACK_KING.HasKingsideCastlingRook(m_board)){
                return false;
            }
        
            if(a_rights.contains("q") && !BLACK_KING.HasQueensideCastlingRook(m_board)){
                return false;
            }
        }else{
            if(WHITE_KING.HasKingsideCastlingRook(m_board)){
                return false;
            }
            
            if(WHITE_KING.HasQueensideCastlingRook(m_board)){
                return false;
            }
            
            if(BLACK_KING.HasKingsideCastlingRook(m_board)){
                return false;
            }
            
            if(BLACK_KING.HasQueensideCastlingRook(m_board)){
                return false;
            }
        }
        
        return true;
    }
}
