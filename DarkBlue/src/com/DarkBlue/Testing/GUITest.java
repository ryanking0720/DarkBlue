package com.DarkBlue.Testing;

import com.DarkBlue.Board.Board;
import com.DarkBlue.Board.Tile;
import com.DarkBlue.Piece.Bishop;
import com.DarkBlue.Piece.King;
import com.DarkBlue.Piece.Knight;
import com.DarkBlue.Piece.Pawn;
import com.DarkBlue.Piece.Piece;
import com.DarkBlue.Piece.Queen;
import com.DarkBlue.Piece.Rook;
import com.DarkBlue.Utilities.BoardUtilities;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Utilities.Utilities;

import java.awt.BorderLayout;
import java.util.Scanner;

import javax.swing.JFrame;

/*
 * This file is meant to test the stipped-down GUITile
 * and GUIBoard classes. 
 * 
 * This works by asking the user for a series of strings that
 * will generate a non-working GUITile with their desired type of piece on it.
 * 
 * The user will then be asked what color s/he would want to see positioned
 * on the bottom of a non-working GUIBoard. This color represents the side
 * the human is playing as.
 */
public final class GUITest{
    
    // Fields for validation
    public static final String VALID_COLORS = "WwBb";
    public static final String VALID_PIECES = "PpRrNnBbQqKkEe";
    
    // Scanner for user input
    private static Scanner keyboard;

    /**/
    /*
    NAME
        public static final void main(final String[] a_args);
    
    SYNOPSIS
        public static final void main(final String[] a_args);
    
        String a_args ------> An array of command line arguments.
    
    DESCRIPTION
        This method tests non-functional copies of the GUITile
        and GUIBoard classes. It is the main entry point for this class.
        No command line arguments are needed for this part of the program.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public static final void main(final String[] a_args){
        keyboard = new Scanner(System.in);
        
        //GUITileTest();
        GUIBoardTest();
        
        keyboard.close();
    }
    
    /**/
    /*
    NAME
        private static final void GUITileTest();
    
    SYNOPSIS
        private static final void GUITileTest();
    
        No parameters.
    
    DESCRIPTION
        This method allows a user to enter input to view
        their own sample non-working GUITile with a piece
        that has a color of their choice (or none at all).
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private static final void GUITileTest(){
        // Gather the user input fields
        
        // Get the tile in algebraic notation
        final String TILE_STRING = PromptTile();       

        // Get the piece type character
        final String PIECE_STRING = PromptPiece();       
        
        // Get the color of the piece
        final String PIECE_COLOR_STRING = PromptColor();
        
        // Figure out the color the user wants
        final ChessColor COLOR;
        if(PIECE_COLOR_STRING.equalsIgnoreCase("B")){
            COLOR = ChessColor.BLACK;
        }else{
            COLOR = ChessColor.WHITE;
        }
        
        // Fnd the row and column based on the coordinates the user entered
        final int ROW = BoardUtilities.ToBoardRow(TILE_STRING);
        final int COLUMN = BoardUtilities.ToBoardColumn(TILE_STRING);
        
        // Get the piece given all of the above information
        final Piece PIECE = GetPieceByLetter(PIECE_STRING, COLOR, ROW, COLUMN);

        // Find the tile color based on the coordinates
        final ChessColor tileColor = AssignTileColor(ROW, COLUMN);
        
        // Instantiate the finished model tile
        final Tile TILE = new Tile(tileColor, ROW, COLUMN, PIECE);
        
        // Instantiate the GUITile
        final GUITile GT = new GUITile(TILE);
        
        // Instantiate the frame to hold the tile
        final JFrame FRAME = new JFrame();
        
        // Add the tile to the frame
        FRAME.getContentPane().add(GT, BorderLayout.CENTER);
        
        // Make the frame exit when closed
        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Pack together the contents of the frame
        FRAME.pack();
        
        // Show the frame
        FRAME.setVisible(true);
    }
    
    /**/
    /*
    NAME
        private static final void GUIBoardTest();
    
    SYNOPSIS
        private static final void GUIBoardTest();
    
        No parameters.
    
    DESCRIPTION
        This method allows a user to enter a color
        in order to view their own sample non-working
        GUIBoard with that color oriented toward the bottom.
        This comes complete with pieces in the starting position
        and algebraic notation that gets generated dynamically based
        on the direction the user chose.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private static final void GUIBoardTest(){
        // Find the desired color
        final String COLOR_STRING = PromptColor();
        
        // Determine which color the player wants to show up on the bottom
        final ChessColor PLAYER_COLOR;
        if(COLOR_STRING.equalsIgnoreCase("B")){
            PLAYER_COLOR = ChessColor.BLACK;
        }else{
            PLAYER_COLOR = ChessColor.WHITE;
        }
        
        // Instantiate the GUIBoard
        final GUIBoard GB = new GUIBoard(Board.GetStartingPosition(), PLAYER_COLOR);
        
        // Instantiate the frame to hold the board
        final JFrame FRAME = new JFrame();
        
        // Add the board to the frame
        FRAME.getContentPane().add(GB, BorderLayout.CENTER);
        
        // Make the frame exit when closed
        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Pack together the contents of the frame
        FRAME.pack();
        
        // Show the frame
        FRAME.setVisible(true);
    }
    
    /**/
    /*
    NAME
        private static final String PromptPiece();
    
    SYNOPSIS
        private static final String PromptPiece();
    
        No parameters.
    
    DESCRIPTION
        This method allows a user to enter a string that
        represents a type of piece, or "E/e" if the user prefers an empty tile.
        All input is validated in a do-while loop before being returned.
    
    RETURNS
        String piece: "P/p", "R/r", "B/b", "N/n", "Q/q", "K/k", or "E/e", which either represent pieces or an empty tile.
    
    AUTHOR
        Ryan King
    */
    private static final String PromptPiece(){
        String piece = "";
        
        do{
            System.out.print("Please enter the type of piece by its first letter (P/p, R/r, N/n, B/b, Q/q, K/k, or E/e for empty): ");
            piece = keyboard.nextLine();  
        }while(piece.length() != 1 && !VALID_PIECES.contains(piece));
        
        return piece;
    }
    
    /**/
    /*
    NAME
        private static final String PromptColor();
    
    SYNOPSIS
        private static final String PromptColor();
    
        No parameters.
    
    DESCRIPTION
        This method allows a user to enter a string that
        represents the color black or white.
        All input is validated in a do-while loop before being returned.
    
    RETURNS
        String color: "W", "w", "B", or "b".
    
    AUTHOR
        Ryan King
    */
    private static final String PromptColor(){
        String color = "";
        
        do{
            System.out.print("Please enter the desired color (W/w, B/b): ");
            color = keyboard.nextLine();
        }while(color.length() != 1 && !VALID_COLORS.contains(color));
        
        return color;
    }
    
    /**/
    /*
    NAME
        private static final String PromptTile();
    
    SYNOPSIS
        private static final String PromptTile();
    
        No parameters.
    
    DESCRIPTION
        This method allows a user to enter a string that
        represents a tile in algebraic notation.
        All input is validated in a do-while loop before being returned.
    
    RETURNS
        String tile: A string with a letter a-h followed by a number 1-8.
    
    AUTHOR
        Ryan King
    */
    private static final String PromptTile(){
        String tile = "";
        
        do{
            System.out.print("Please enter the tile coordinates in algebraic notation (a-h followed by 1-8): ");
            tile = keyboard.nextLine();
        }while(!BoardUtilities.IsValidTile(tile));
        
        return tile;
    }
    
    /**/
    /*
    NAME
        private static final Piece GetPieceByLetter(final String a_piece, ChessColor a_color, int a_row, int a_column);
    
    SYNOPSIS
        private static final Piece GetPieceByLetter(final String a_piece, ChessColor a_color, int a_row, int a_column);
    
        String a_piece -----------> A string with that contains one of the following characters: PpRrNnBbQqKkEe, which represent a piece or an empty tile.
        
        ChessColor a_color -------> The color of the piece.
        
        int a_row ----------------> The row the piece is resting on.
        
        int a_column -------------> The column the piece is resting on.
    
    DESCRIPTION
        This method returns a piece with the given type, color, row, and column.
        No validation is performed with any field except the type because they
        have already been validated.
    
    RETURNS
        Piece: A new piece with the specifications provided.
    
    AUTHOR
        Ryan King
    */
    private static final Piece GetPieceByLetter(final String a_piece, ChessColor a_color, int a_row, int a_column){
        if(a_piece.equalsIgnoreCase("P")){
            return new Pawn(a_color, a_row, a_column);
        }else if(a_piece.equalsIgnoreCase("R")){
            return new Rook(a_color, a_row, a_column);
        }else if(a_piece.equalsIgnoreCase("N")){
            return new Knight(a_color, a_row, a_column);
        }else if(a_piece.equalsIgnoreCase("B")){
            return new Bishop(a_color, a_row, a_column);
        }else if(a_piece.equalsIgnoreCase("Q")){
            return new Queen(a_color, a_row, a_column);
        }else if(a_piece.equalsIgnoreCase("K")){
            return new King(a_color, a_row, a_column, false, false);
        }else{
            return null;
        }
    }
    
    /**/
    /*
    NAME
        private static final ChessColor AssignTileColor(final int a_row, final int a_column);
    
    SYNOPSIS
        private static final ChessColor AssignTileColor(final int a_row, final int a_column);
        
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
    private static final ChessColor AssignTileColor(final int a_row, final int a_column){
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
}