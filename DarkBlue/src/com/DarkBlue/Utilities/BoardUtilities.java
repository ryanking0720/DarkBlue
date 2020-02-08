package com.DarkBlue.Utilities;

import java.awt.Color;

public interface BoardUtilities{
	
	// Colors for GUITiles
    public static final Color BLACK = new Color(Utilities.TWO_HUNDRED_FOUR, Utilities.ONE_HUNDRED_TWO, Utilities.ZERO, Utilities.TWO_HUNDRED_FIFTY_FIVE);
    public static final Color WHITE = new Color(Utilities.TWO_HUNDRED_FIFTY_FIVE, Utilities.ONE_HUNDRED_SEVENTY_EIGHT, Utilities.ONE_HUNDRED_TWO, Utilities.TWO_HUNDRED_FIFTY_FIVE);
    public static final Color SELECTED_GREEN = new Color(Utilities.FIFTY_ONE, Utilities.TWO_HUNDRED_FIFTY_FIVE, Utilities.FIFTY_ONE, Utilities.TWO_HUNDRED_FIFTY_FIVE);
	
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
                case Utilities.ZERO: algebraic = Character.toString(Utilities.A);
                break;
                case Utilities.ONE: algebraic = Character.toString(Utilities.B);
                break;
                case Utilities.TWO: algebraic = Character.toString(Utilities.C);
                break;
                case Utilities.THREE: algebraic = Character.toString(Utilities.D);
                break;
                case Utilities.FOUR: algebraic = Character.toString(Utilities.E);
                break;
                case Utilities.FIVE: algebraic = Character.toString(Utilities.F);
                break;
                case Utilities.SIX: algebraic = Character.toString(Utilities.G);
                break;
                case Utilities.SEVEN: algebraic = Character.toString(Utilities.H);
                break;
                default: algebraic = Character.toString(Utilities.NULL);
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
        
        String algebraic = "";
        
        switch(a_column){
            case Utilities.ZERO: algebraic = Character.toString(Utilities.A);
            break;
            case Utilities.ONE: algebraic = Character.toString(Utilities.B);
            break;
            case Utilities.TWO: algebraic = Character.toString(Utilities.C);
            break;
            case Utilities.THREE: algebraic = Character.toString(Utilities.D);
            break;
            case Utilities.FOUR: algebraic = Character.toString(Utilities.E);
            break;
            case Utilities.FIVE: algebraic = Character.toString(Utilities.F);
            break;
            case Utilities.SIX: algebraic = Character.toString(Utilities.G);
            break;
            case Utilities.SEVEN: algebraic = Character.toString(Utilities.H);
            break;
            default: algebraic = Character.toString(Utilities.NULL);
            break;
        }
        
        algebraic += Integer.toString(Utilities.EIGHT - a_row);
        
        return algebraic;
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
        if(a_algebraicSpot.length() != Utilities.TWO){
            return Utilities.NEGATIVE_ONE;
        }
        
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
            if(a_algebraicSpot.length() != Utilities.TWO){
                return Utilities.NEGATIVE_ONE;
            }
            
            final String spot = a_algebraicSpot.toLowerCase();
            
            switch(spot.charAt(Utilities.ZERO)){
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
}