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
        return (a_move1.GetPiece().GetPieceType() == a_move2.GetPiece().GetPieceType()
        		&& a_move1.GetPiece().GetColor() == a_move2.GetPiece().GetColor()
        		&& a_move1.GetOldRow() == a_move2.GetOldRow()
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
}