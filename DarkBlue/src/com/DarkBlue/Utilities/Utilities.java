package com.DarkBlue.Utilities;

import java.awt.Font;

import javax.swing.JComponent;

import com.DarkBlue.Move.Move;
import com.DarkBlue.Piece.Piece;

/**
 * This interface contains symbolic constants for
 * commonly used numbers such as 0, 1, 2, etc.
 * as well as letters and numbers used in notation.
 * English-language algebraic, FEN, and Unicode icons
 * for all types and colors of chess pieces exist here as well.
 * 
 * Fields and methods here are determined to be common enough
 * to be used anywhere regardless of the type of class.
 * 
 * It also contains move legality checking.
 */
public interface Utilities{
    // Icons for pieces to be used in algebraic notation
    public static final char WHITE_PAWN_ICON = 'P';
    public static final char WHITE_ROOK_ICON = 'R';
    public static final char WHITE_KNIGHT_ICON = 'N';
    public static final char WHITE_BISHOP_ICON = 'B';
    public static final char WHITE_QUEEN_ICON = 'Q';
    public static final char WHITE_KING_ICON = 'K';
    
    // Icons for pieces to be used in algebraic notation
    public static final char BLACK_PAWN_ICON = 'p';
    public static final char BLACK_ROOK_ICON = 'r';
    public static final char BLACK_KNIGHT_ICON = 'n';
    public static final char BLACK_BISHOP_ICON = 'b';
    public static final char BLACK_QUEEN_ICON = 'q';
    public static final char BLACK_KING_ICON = 'k';
    
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
    
    public static final String EMPTY_STRING = "";
    public static final String CAPTURE = "x";
    public static final String ELLIPSIS = "...\n";
    
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
    public static final char NEWLINE = '\n';
    public static final char CAPTURE_CHAR = 'x';
    public static final char NO_RIGHTS_OR_TILE = '-';
    public static final char SPACE = ' ';
    public static final char FORWARD_SLASH = '/';
    
    // Character versions of single-digit numbers
    public static final char ONE_CHAR = '1';
    public static final char TWO_CHAR = '2';
    public static final char THREE_CHAR = '3';
    public static final char FOUR_CHAR = '4';
    public static final char FIVE_CHAR = '5';
    public static final char SIX_CHAR = '6';
    public static final char SEVEN_CHAR = '7';
    public static final char EIGHT_CHAR = '8';
    
    // Castling move strings for use in descriptive or algebraic notation, which uses the number zero ("0")
    // This is not valid for use with PGN, which uses the capital letter "O" in place of the number zero
    public static final String KINGSIDE_CASTLE = "0-0";
    public static final String QUEENSIDE_CASTLE = "0-0-0";

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
    public static final int FIFTEEN = 15;
    public static final int SIXTEEN = 16;
    public static final int FORTY = 40;
    public static final int FIFTY = 50;
    public static final int FIFTY_ONE = 51;
    public static final int SIXTY = 60;
    public static final int SIXTY_FOUR = 64;
    public static final int NINETY_NINE = 99;
    public static final int ONE_HUNDRED = 100;
    public static final int ONE_HUNDRED_TWO = 102;
    public static final int ONE_HUNDRED_SEVENTY_EIGHT = 178;
    public static final int TWO_HUNDRED_FOUR = 204;
    public static final int TWO_HUNDRED_FIFTY_FIVE = 255;
    public static final int THREE_HUNDRED_SIXTY = 360;

    public static final long ONE_LONG = 1;
    
    public static final float FIVE_FLOAT = 5.0f;
       
    /**/
    /*
    NAME
        public static boolean IsLegal(final Piece a_piece, final int a_destinationRow, final int a_destinationColumn);
    
    SYNOPSIS
        public static boolean IsLegal(final Piece a_piece, final int a_destinationRow, final int a_destinationColumn);
    
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
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static boolean IsLegal(final Piece a_piece, final int a_destinationRow, final int a_destinationColumn){   
        // Idiot proofing
        if(a_piece == null || !BoardUtilities.HasValidCoordinates(a_piece.GetCurrentRow(), a_piece.GetCurrentColumn()) || !BoardUtilities.HasValidCoordinates(a_destinationRow, a_destinationColumn)){
            return false;
        }
        
        // Check for move legality by examining destination coordinates
        for(final Move MOVE : a_piece.GetCurrentLegalMoves()){
            if(MOVE.GetNewRow() == a_destinationRow && MOVE.GetNewColumn() == a_destinationColumn){
                return true;
            }
        }
        return false;
    }
    
    /**/
    /*
    NAME
        public static void EnlargeFont(final JComponent a_object);
    
    SYNOPSIS
        public static void EnlargeFont(final JComponent a_object);
    
        JComponent a_object ------------> The JComponent whose font needs to be resized.
    
    DESCRIPTION
        This method enlarges the font of this JComponent by a size of 5.0f.
    
    RETURNS
        Nothing
    
    AUTHOR
        Help taken from:
        https://stackoverflow.com/questions/8675038/increasing-decreasing-font-size-inside-textarea-using-jbutton
        with additional modifications made by Ryan King
    */
    public static void EnlargeFont(final JComponent a_object){
        // Idiot proofing
        if(a_object == null){
            return;
        }
        
        // Enlarge the component's font
        final Font FONT = a_object.getFont();
        final float SIZE = FONT.getSize() + FIVE_FLOAT;
        a_object.setFont(FONT.deriveFont(SIZE));
    }
}