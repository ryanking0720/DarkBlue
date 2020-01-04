package com.DarkBlue.Utilities;

import java.awt.Color;

import com.DarkBlue.Move.Move;
import com.DarkBlue.Piece.*;

public interface Utilities{
    // Icons for pieces to be used in algebraic notation
    public static final char PAWN_ICON = 'P';
    public static final char ROOK_ICON = 'R';
    public static final char KNIGHT_ICON = 'N';
    public static final char BISHOP_ICON = 'B';
    public static final char QUEEN_ICON = 'Q';
    public static final char KING_ICON = 'K';
    
    // Icons for pieces to be used on the board
    public static final char WHITE_PAWN_BOARD_ICON = '♙';
    public static final char WHITE_ROOK_BOARD_ICON = '♖';
    public static final char WHITE_KNIGHT_BOARD_ICON = '♘';
    public static final char WHITE_BISHOP_BOARD_ICON = '♗';
    public static final char WHITE_QUEEN_BOARD_ICON = '♕';
    public static final char WHITE_KING_BOARD_ICON = '♔';
    
    public static final char BLACK_PAWN_BOARD_ICON = '♟';
    public static final char BLACK_ROOK_BOARD_ICON = '♜';
    public static final char BLACK_KNIGHT_BOARD_ICON = '♞';
    public static final char BLACK_BISHOP_BOARD_ICON = '♝';
    public static final char BLACK_QUEEN_BOARD_ICON = '♛';
    public static final char BLACK_KING_BOARD_ICON = '♚';
    
    // Letters in algebraic notation
    public static final char A = 'a';
    public static final char B = 'b';
    public static final char C = 'c';
    public static final char D = 'd';
    public static final char E = 'e';
    public static final char F = 'f';
    public static final char G = 'g';
    public static final char H = 'h';
    public static final char NULL = '\0';
    
    // Character versions of single-digit numbers
    public static final char ONE_CHAR = '1';
    public static final char TWO_CHAR = '2';
    public static final char THREE_CHAR = '3';
    public static final char FOUR_CHAR = '4';
    public static final char FIVE_CHAR = '5';
    public static final char SIX_CHAR = '6';
    public static final char SEVEN_CHAR = '7';
    public static final char EIGHT_CHAR = '8';
    
    // Castling move strings
    public static final String KINGSIDE_CASTLE = "0-0";
    public static final String QUEENSIDE_CASTLE = "0-0-0";
    
    // Rules to display in the InstructionFrame
    public static final String INSTRUCTIONS = "How to use this engine:\n\n1. Use your primary mouse button to select the piece you wish to move.\n\n2. Give that piece another primary click to deselect it and choose another piece.\n\n3. Click on any green tile to move the piece to that spot. Play alternates between both players until one loses, resigns, or a draw is reached.";
    
    // Symbolic constants for piece values and initializations for immutability
    public static final int NEGATIVE_SEVEN = -7;
    public static final int NEGATIVE_SIX = -6;
    public static final int NEGATIVE_FIVE = -5;
    public static final int NEGATIVE_FOUR = -4;
    public static final int NEGATIVE_THREE = -3;
    public static final int NEGATIVE_TWO = -2;
    public static final int NEGATIVE_ONE = -1;
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;
    public static final int FIVE = 5;
    public static final int SIX = 6;
    public static final int SEVEN = 7;
    public static final int EIGHT = 8;
    public static final int NINE = 9;
    public static final int TEN = 10;
    public static final int FORTY = 40;
    public static final int FIFTY = 50;
    public static final int FIFTY_ONE = 51;
    public static final int SIXTY = 60;
    public static final int SIXTY_FOUR = 64;
    public static final int ONE_HUNDRED_TWO = 102;
    public static final int ONE_HUNDRED_SEVENTY_EIGHT = 178;
    public static final int TWO_HUNDRED_FOUR = 204;
    public static final int TWO_HUNDRED_FIFTY_FIVE = 255;
    public static final int THREE_HUNDRED_SIXTY = 360;
    public static final int FOUR_HUNDRED_TWENTY = 420;
    public static final int NINE_HUNDRED_NINETY_NINE = 999;
    
    public static final long ONE_LONG = 1;
    
    // Colors for GUITiles
    public static final Color BLACK = new Color(TWO_HUNDRED_FOUR, ONE_HUNDRED_TWO, ZERO, TWO_HUNDRED_FIFTY_FIVE);
    public static final Color WHITE = new Color(TWO_HUNDRED_FIFTY_FIVE, ONE_HUNDRED_SEVENTY_EIGHT, ONE_HUNDRED_TWO, TWO_HUNDRED_FIFTY_FIVE);
    public static final Color SELECTED_GREEN = new Color(FIFTY_ONE, TWO_HUNDRED_FIFTY_FIVE, FIFTY_ONE, TWO_HUNDRED_FIFTY_FIVE);
    public static final Color MOVABLE_GREEN = new Color(ONE_HUNDRED_TWO, TWO_HUNDRED_FIFTY_FIVE, ONE_HUNDRED_TWO, ONE_HUNDRED_TWO);
    
    /**/
    /*
    NAME
        public static boolean HasValidCoordinates(final int a_row, final int a_column);
    
    SYNOPSIS
        public static boolean HasValidCoordinates(final int a_row, final int a_column);
    
        int a_row -----------> The row to validate.
        
        int a_column --------> The column to validate.
    
    DESCRIPTION
        This method validates a given row and column
        and determines if they are legal coordinates on
        the two-dimensional board array.
    
    RETURNS
        True if the coordinates are valid, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    public static boolean HasValidCoordinates(final int a_row, final int a_column){
        try{
            return HasValidValue(a_row) && HasValidValue(a_column);
        }catch(Exception e){
            return false;
        }
    }
    
    /**/
    /*
    NAME
        public static boolean IsIdentical(final Move a_move1, final Move a_move2);
    
    SYNOPSIS
        public static boolean IsIdentical(final Move a_move1, final Move a_move2);
    
        Move a_move1 -------------> The first move to compare.
        
        Move a_move2 -------------> The second move to compare.
    
    DESCRIPTION
        This method determines if two given moves are identical
        by comparing their deltas.
    
    RETURNS
        True if the deltas of both moves match, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    public static boolean IsIdentical(final Move a_move1, final Move a_move2){
        try{
        return (a_move1.GetOldRow() == a_move2.GetOldRow()
                && a_move1.GetOldColumn() == a_move2.GetOldColumn()
                && a_move1.GetNewRow() == a_move2.GetNewRow()
                && a_move1.GetNewColumn() == a_move2.GetNewColumn());
        }catch(Exception e){
            return false;
        }
    }
    
    /**/
    /*
    NAME
        public static ChessColor Reverse(final ChessColor a_color);
    
    SYNOPSIS
        public static ChessColor Reverse(final ChessColor a_color);
    
        ChessColor a_color ---------> The color of the previous turn.
    
    DESCRIPTION
        This method swaps the color to the opposite one at the end
        of the current turn. For example, if white just moved, this
        method would swap it to black, and vice versa.
    
    RETURNS
        ChessColor: Black if white moved, or white if black moved.
    
    AUTHOR
        Ryan King
    */
    public static ChessColor Reverse(final ChessColor a_color){
        try{
            if(a_color == ChessColor.WHITE){
                return ChessColor.BLACK;
            }else{
                return ChessColor.WHITE;
            }
        }catch(Exception e){
            return null;
        }
    }
    
    public static String ToAlgebraicColumn(final int a_column){
        String algebraic = "";
        try{
            switch(a_column){
                case ZERO: algebraic = Character.toString(A);
                break;
                case ONE: algebraic = Character.toString(B);
                break;
                case TWO: algebraic = Character.toString(C);
                break;
                case THREE: algebraic = Character.toString(D);
                break;
                case FOUR: algebraic = Character.toString(E);
                break;
                case FIVE: algebraic = Character.toString(F);
                break;
                case SIX: algebraic = Character.toString(G);
                break;
                case SEVEN: algebraic = Character.toString(H);
                break;
                default: algebraic = Character.toString(NULL);
                break;
            }
        }catch(Exception e){
            return null;
        }
        
        return algebraic;
    }
    
    public static String ToAlgebraicRow(final int a_row){
        String algebraic = null;
        try{
            if(HasValidValue(a_row)){
                algebraic = Integer.toString(EIGHT - a_row);
            }    
        }catch(Exception e){
            return null;
        }
        return algebraic;
    }
    
    /**/
    /*
    NAME
        public static String ToAlgebraic(final int a_row, final int a_column);
    
    SYNOPSIS
        public static String ToAlgebraic(final int a_row, final int a_column);
    
        int a_row -----------> The row of the array to be converted.
        
        int a_column --------> The column of the array to be converted.
    
    DESCRIPTION
        This method converts two given array indices to algebraic notation.
        For example, if we were to pass in 6 as the row and 4 as the column,
        it would return a string containing "e2", which is its equivalent in 
        conventional algebraic notation. If the coordinates are invalid, then
        the method returns null.
    
    RETURNS
        String algebraic: The array spot in algebraic notation, or null if the coordinates are invalid.
    
    AUTHOR
        Ryan King
    */
    public static String ToAlgebraic(final int a_row, final int a_column){
        
        if(!HasValidCoordinates(a_row, a_column)){
            return null;
        }
        
        String algebraic = "";
        
        switch(a_column){
            case ZERO: algebraic = Character.toString(A);
            break;
            case ONE: algebraic = Character.toString(B);
            break;
            case TWO: algebraic = Character.toString(C);
            break;
            case THREE: algebraic = Character.toString(D);
            break;
            case FOUR: algebraic = Character.toString(E);
            break;
            case FIVE: algebraic = Character.toString(F);
            break;
            case SIX: algebraic = Character.toString(G);
            break;
            case SEVEN: algebraic = Character.toString(H);
            break;
            default: algebraic = Character.toString(NULL);
            break;
        }
        
        algebraic += Integer.toString(EIGHT - a_row);
        
        return algebraic;
    }
    
    /**/
    /*
    NAME
        public static boolean IsLegal(final Piece a_piece, final int a_destRow, final int a_destCol);
    
    SYNOPSIS
        public static boolean IsLegal(final Piece a_piece, final int a_destRow, final int a_destCol);
    
        Piece a_piece ---------> The candidate piece to move.
        
        int a_destRow ---------> The row where the piece wishes to move.
        
        int a_destCol ---------> The column where the piece wishes to move.
    
    DESCRIPTION
        This method determines if a given move is legal by iterating through
        the piece's current legal moves array and determining if the deltas match.
        If we iterate through the entire list and find no match, the move is
        considered to be illegal.
    
    RETURNS
        boolean: True if the move is legal, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    public static boolean IsLegal(final Piece a_piece, final int a_destinationRow, final int a_destinationColumn){        
        for(Move move : a_piece.GetCurrentLegalMoves()){
            if(move.GetNewRow() == a_destinationRow && move.GetNewColumn() == a_destinationColumn){
                return true;
            }
        }
        return false;
    }
    
    /**/
    /*
    NAME
        public static int ToBoardRow(final String a_algebraicSpot);
    
    SYNOPSIS
        public static int ToBoardRow(final String a_algebraicSpot);
    
        String a_algebraicSpot -----------> The Tile in algebraic notation.
    
    DESCRIPTION
        This method takes in a given String that denotes a Tile in algebraic notation.
        It analyzes the second character of the String to determine the algebraic row number, 
        which it will return if successful. This method returns -1 on error, which happens
        when the String is not of the proper format and/or length.
    
    RETURNS
        int: Any number from 0 to 7 if successful, or -1 if failed.
    
    AUTHOR
        Ryan King
    */
    public static int ToBoardRow(final String a_algebraicSpot){
        if(a_algebraicSpot.length() != TWO){
            return NEGATIVE_ONE;
        }
        
        switch(a_algebraicSpot.charAt(ONE)){
            case ONE_CHAR: return SEVEN;
            case TWO_CHAR: return SIX;
            case THREE_CHAR: return FIVE;
            case FOUR_CHAR: return FOUR;
            case FIVE_CHAR: return THREE;
            case SIX_CHAR: return TWO;
            case SEVEN_CHAR: return ONE;
            case EIGHT_CHAR: return ZERO;
            default:  return NEGATIVE_ONE;
        }
    }
    
    /**/
    /*
    NAME
        public static int ToBoardColumn(final String a_algebraicSpot);
    
    SYNOPSIS
        public static int ToBoardColumn(final String a_algebraicSpot);
    
        String a_algebraicSpot -----------> The Tile in algebraic notation.
    
    DESCRIPTION
        This method takes in a given String that denotes a Tile in algebraic notation.
        It analyzes the first character of the String to determine the letter, which will
        give it the proper number to return. This method returns -1 on error, which happens
        when the String is not of the proper format and/or length.
    
    RETURNS
        int: Any number from 0 to 7 if successful, or -1 if failed.
    
    AUTHOR
        Ryan King
    */
    public static int ToBoardColumn(final String a_algebraicSpot){
        try{
            if(a_algebraicSpot.length() != TWO){
                return NEGATIVE_ONE;
            }
            
            final String spot = a_algebraicSpot.toLowerCase();
            
            switch(spot.charAt(ZERO)){
                case H:  return SEVEN;
                case G:  return SIX;
                case F:  return FIVE;
                case E:  return FOUR;
                case D:  return THREE;
                case C:  return TWO;
                case B:  return ONE;
                case A:  return ZERO;
                default: return NEGATIVE_ONE;
            }
        }catch(Exception e){
            return NEGATIVE_ONE;
        }
    }
    
    /**/
    /*
    NAME
        public static boolean HasValidValue(final int a_value);
    
    SYNOPSIS
        public static boolean HasValidValue(final int a_value);
    
        int a_value -------> The value to validate.
    
    DESCRIPTION
        This method returns if a given value
        is a valid value, i.e. 1 through 8.
    
    RETURNS
        True if the value is valid, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    public static boolean HasValidValue(final int a_value){
        try{
            return a_value >= ZERO && a_value <= SEVEN;
        }catch(Exception e){
            return false;
        }
    }
    
    /**/
    /*
    NAME
        public static boolean IsValidTileLetter(final char a_char);
    
    SYNOPSIS
        public static boolean IsValidTileLetter(final char a_char);
    
        char a_char -------> The character to validate.
    
    DESCRIPTION
        This method returns if a given character
        is a valid tile letter, i.e. 'a' through 'h' (lowercase only).
    
    RETURNS
        True if the letter is valid, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    public static boolean IsValidTileLetter(final char a_char){
        return a_char >= Utilities.A && a_char <= Utilities.H;
    }
    
    /**/
    /*
    NAME
        public static boolean IsValidTileNumber(final int a_number);
    
    SYNOPSIS
        public static boolean IsValidTileNumber(final int a_number);
    
        int a_number -------> The character to validate.
    
    DESCRIPTION
        This method returns if a given integer
        is a valid tile number, i.e. 1 through 8.
    
    RETURNS
        True if the number is valid, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    public static boolean IsValidTileNumber(final int a_number){
        return a_number >= Utilities.ONE  && a_number <= Utilities.EIGHT;
    }
    
    /**/
    /*
    NAME
        public static boolean IsValidTile(final String a_string);
    
    SYNOPSIS
        public static boolean IsValidTile(final String a_string);
    
        String a_string -------> The String to validate.
    
    DESCRIPTION
        This method returns if a given String
        is a valid tile, i.e. length is 2, a through h, 1 through 8.
        A valid example would be "a5".
        An invalid example would be "duck".
    
    RETURNS
        True if the String is valid, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    public static boolean IsValidTile(final String a_string){
        if(a_string.length() != Utilities.TWO){
            return false;
        }
        
        final String tile = a_string.toLowerCase();
        
        try{
            final int tileNumber = Integer.parseInt(Character.toString(tile.charAt(Utilities.ONE)));
            return IsValidTileLetter(tile.charAt(Utilities.ZERO)) && IsValidTileNumber(tileNumber);
        }catch(Exception e){
            return false;
        }
    }
        
    /**/
    /*
    NAME
        public static Piece DuplicatePiece(final Piece a_candidate, final int a_newRow, final int a_newColumn);
    
    SYNOPSIS
        public static Piece DuplicatePiece(final Piece a_candidate, final int a_newRow, final int a_newColumn)
        
        Piece a_candidate ---------------> The piece to be duplicated.
        
        int a_row -----------------------> The piece's new row.
        
        int a_column --------------------> The piece's new column.
    
    DESCRIPTION
        This method duplicates the given piece, gives it new coordinates, and adds 1 to its current number of moves.
        This version is used when re-instantiating a piece that has just moved.
    
    RETURNS
        Piece: The new pawn, rook, knight, bishop, queen, or king in its new position
        and with its move count increased by 1, or null on error.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static Piece DuplicatePiece(final Piece a_candidate, final int a_newRow, final int a_newColumn){
        // Make a deep copy of the piece that just moved
        switch(a_candidate.GetPieceType()){
            case PAWN: return new Pawn(a_candidate, a_newRow, a_newColumn, a_candidate.HowManyMoves() + Utilities.ONE);
            case ROOK: return new Rook(a_candidate, a_newRow, a_newColumn, a_candidate.HowManyMoves() + Utilities.ONE);
            case KNIGHT: return new Knight(a_candidate, a_newRow, a_newColumn, a_candidate.HowManyMoves() + Utilities.ONE);
            case BISHOP: return new Bishop(a_candidate, a_newRow, a_newColumn, a_candidate.HowManyMoves() + Utilities.ONE);
            case QUEEN: return new Queen(a_candidate, a_newRow, a_newColumn, a_candidate.HowManyMoves() + Utilities.ONE);
            case KING: return new King(a_candidate, a_newRow, a_newColumn, a_candidate.HowManyMoves() + Utilities.ONE);
            default: return null;
        }
    }
}