package com.DarkBlue.Utilities;

import java.awt.Color;
import java.awt.Dimension;

/**
 * This interface contains fields and methods that help with assembling the
 * GUI board and helping with validation of board coordinates, assigning colors,
 * and converting model board coordinates into algebraic notation and vice versa.
 * 
 * Contains some methods and partial inspiration from BoardUtils.java in Black Widow Chess by Amir Afghani:
 * https://github.com/amir650/BlackWidow-Chess
 */
public interface BoardUtilities{
	
	// Colors for GUITiles
    public static final Color BLACK = new Color(Utilities.TWO_HUNDRED_FOUR, Utilities.ONE_HUNDRED_TWO, Utilities.ZERO, Utilities.TWO_HUNDRED_FIFTY_FIVE);
    public static final Color WHITE = new Color(Utilities.TWO_HUNDRED_FIFTY_FIVE, Utilities.ONE_HUNDRED_SEVENTY_EIGHT, Utilities.ONE_HUNDRED_TWO, Utilities.TWO_HUNDRED_FIFTY_FIVE);
    public static final Color SELECTED_GREEN = new Color(Utilities.FIFTY_ONE, Utilities.TWO_HUNDRED_FIFTY_FIVE, Utilities.FIFTY_ONE, Utilities.TWO_HUNDRED_FIFTY_FIVE);
    public static final Dimension TILE_DIMENSION = new Dimension(Utilities.SIXTY, Utilities.SIXTY);
    public static final Color BORDER_BLACK = new Color(154, 152, 0, 1);
    public static final Color BORDER_WHITE = new Color(155, 228, 252, 1);
	
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
        public static boolean IsKingsRook(final ChessColor a_color, final int a_row, final int a_column);
    
    SYNOPSIS
        public static boolean IsKingsRook(final ChessColor a_color, final int a_row, final int a_column);
    
        ChessColor a_color --> The piece's color.
    
        int a_row -----------> The row to validate.
        
        int a_column --------> The column to validate.
    
    DESCRIPTION
        This method checks to see if the coordinates are those of a king's rook of either color.
    
    RETURNS
        True if the coordinates are the ones specified, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    public static boolean IsKingsRook(final ChessColor a_color, final int a_row, final int a_column){
        if(a_color.IsWhite()){
            return a_row == Utilities.SEVEN && a_column == Utilities.SEVEN;
        }else{
            return a_row == Utilities.ZERO && a_column == Utilities.SEVEN;
        }
    }
    
    /**/
    /*
    NAME
        public static boolean IsQueensRook(final ChessColor a_color, final int a_row, final int a_column);
    
    SYNOPSIS
        public static boolean IsQueensRook(final ChessColor a_color, final int a_row, final int a_column);
    
        ChessColor a_color --> The piece's color.
    
        int a_row -----------> The row to validate.
        
        int a_column --------> The column to validate.
    
    DESCRIPTION
        This method checks to see if the coordinates are those of a king's rook of either color.
    
    RETURNS
        True if the coordinates are the ones specified, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    public static boolean IsQueensRook(final ChessColor a_color, final int a_row, final int a_column){
        if(a_color.IsWhite()){
            return a_row == Utilities.SEVEN && a_column == Utilities.ZERO;
        }else{
            return a_row == Utilities.ZERO && a_column == Utilities.ZERO;
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
    
    /**/
    /*
    NAME
        public static String ToAlgebraicColumn(final int a_column);
    
    SYNOPSIS
        public static String ToAlgebraicColumn(final int a_column);
        
        int a_column --------> The column of the array to be converted.
    
    DESCRIPTION
        This method converts the given array index to a column in algebraic notation.
        For example, 0 converts to h, 1 to g, 2 to f, and so on.
        Invalid values return the null character.
    
    RETURNS
        String algebraic: The array spot in algebraic notation, or null if the coordinates are invalid.
    
    AUTHOR
        Ryan King
    */
    public static String ToAlgebraicColumn(final int a_column){
        try{
            switch(a_column){
                case Utilities.ZERO: return Character.toString(Utilities.A);
                case Utilities.ONE: return Character.toString(Utilities.B);
                case Utilities.TWO: return Character.toString(Utilities.C);
                case Utilities.THREE: return Character.toString(Utilities.D);
                case Utilities.FOUR: return Character.toString(Utilities.E);
                case Utilities.FIVE: return Character.toString(Utilities.F);
                case Utilities.SIX: return Character.toString(Utilities.G);
                case Utilities.SEVEN: return Character.toString(Utilities.H);
                default: return Character.toString(Utilities.NULL);
            }
        }catch(Exception e){
            return null;
        }
    }
    
    /**/
    /*
    NAME
        public static String ToAlgebraicRow(final int a_row);
    
    SYNOPSIS
        public static String ToAlgebraicRow(final int a_row);
    
        int a_row -----------> The row of the array to be converted.

    DESCRIPTION
        This method converts the array index to a row in algebraic notation. 
        For example, row 0 would be converted to 8, 1 to 7, 2 to 6, etc.
        Any invalid number will return -1.
    
    RETURNS
        String algebraic: The row in algebraic notation, or null if the coordinates are invalid.
    
    AUTHOR
        Ryan King
    */
    public static String ToAlgebraicRow(final int a_row){
        String algebraic = null;
        
        try{
            if(HasValidValue(a_row)){
                algebraic = Integer.toString(Utilities.EIGHT - a_row);
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
        
        // Make a string with the letter followed by the number
        return ToAlgebraicColumn(a_column) + ToAlgebraicRow(a_row);
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
        // Idiot proofing for invalid arguments
        if(a_algebraicSpot == null || a_algebraicSpot.isBlank() || a_algebraicSpot.length() != Utilities.TWO){
            return Utilities.NEGATIVE_ONE;
        }
        
        // Usual valid arguments
        switch(a_algebraicSpot.charAt(Utilities.ONE)){
            case Utilities.ONE_CHAR: return Utilities.SEVEN;
            case Utilities.TWO_CHAR: return Utilities.SIX;
            case Utilities.THREE_CHAR: return Utilities.FIVE;
            case Utilities.FOUR_CHAR: return Utilities.FOUR;
            case Utilities.FIVE_CHAR: return Utilities.THREE;
            case Utilities.SIX_CHAR: return Utilities.TWO;
            case Utilities.SEVEN_CHAR: return Utilities.ONE;
            case Utilities.EIGHT_CHAR: return Utilities.ZERO;
            default: return Utilities.NEGATIVE_ONE;
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
            if(a_algebraicSpot == null || a_algebraicSpot.isBlank() || a_algebraicSpot.length() != Utilities.TWO){
                return Utilities.NEGATIVE_ONE;
            }
            
            // Turn the tile to lowercase
            final String SPOT = a_algebraicSpot.toLowerCase();
            
            // Look at the letter
            switch(SPOT.charAt(Utilities.ZERO)){
                case Utilities.H:  return Utilities.SEVEN;
                case Utilities.G:  return Utilities.SIX;
                case Utilities.F:  return Utilities.FIVE;
                case Utilities.E:  return Utilities.FOUR;
                case Utilities.D:  return Utilities.THREE;
                case Utilities.C:  return Utilities.TWO;
                case Utilities.B:  return Utilities.ONE;
                case Utilities.A:  return Utilities.ZERO;
                default: return Utilities.NEGATIVE_ONE;
            }
        }catch(Exception e){
            return Utilities.NEGATIVE_ONE;
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
            return a_value >= Utilities.ZERO && a_value <= Utilities.SEVEN;
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
        boolean: True if the String is valid, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static boolean IsValidTile(final String a_string){
        // Idiot proofing for invalid arguments
        if(a_string == null || a_string.isBlank() || a_string.length() != Utilities.TWO){
            return false;
        }
        
        // The tile must be lowercase
        final String TILE = a_string.toLowerCase();
        
        // Try to parse both the letter and the number of the tile
        try{
            final int TILE_NUMBER = Integer.parseInt(Character.toString(TILE.charAt(Utilities.ONE)));
            return IsValidTileLetter(TILE.charAt(Utilities.ZERO)) && IsValidTileNumber(TILE_NUMBER);
        }catch(Exception e){
            return false;
        }
    }
}