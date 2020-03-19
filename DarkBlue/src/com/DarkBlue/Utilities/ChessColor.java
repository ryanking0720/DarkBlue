package com.DarkBlue.Utilities;

// Adapted from the "Alliance"/"Allegiance" enum in Black Widow Chess by Amir Afghani,
// though my color enum is also used for tiles.
// This is primarily used for readability and was named "ChessColor" to avoid
// confusion with java.awt.Color.

public enum ChessColor{
    WHITE, 
    BLACK;
    
    // Symbolic constants
    public static final String WHITE_STRING = "WHITE";
    public static final String BLACK_STRING = "BLACK";
    
    /**/
    /*
    NAME
        public final boolean IsAlly(final ChessColor a_color);
    
    SYNOPSIS
        public final boolean IsAlly(final ChessColor a_color);
    
        ChessColor a_color ---------> The color to compare.
    
    DESCRIPTION
        This method returns if this color and the argument are the same.
    
    RETURNS
        boolean: True if the colors are the same and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsAlly(final ChessColor a_color){
        return this == a_color;
    }
    
    /**/
    /*
    NAME
        public final boolean IsEnemy(final ChessColor a_color);
    
    SYNOPSIS
        public final boolean IsEnemy(final ChessColor a_color);
    
        ChessColor a_color ---------> The color to compare.
    
    DESCRIPTION
        This method returns if this color and the argument are different.
    
    RETURNS
        boolean: True if the colors are different and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsEnemy(final ChessColor a_color){
        return this == BoardUtilities.Reverse(a_color);
    }
    
    /**/
    /*
    NAME
        public final boolean IsWhite();
    
    SYNOPSIS
        public final boolean IsWhite();
    
        No parameters.
    
    DESCRIPTION
        This method returns if this color is white.
    
    RETURNS
        boolean: True if this color is white and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsWhite(){
        return this == ChessColor.WHITE;
    }
    
    /**/
    /*
    NAME
        public final boolean IsBlack();
    
    SYNOPSIS
        public final boolean IsBlack();
    
        No parameters.
    
    DESCRIPTION
        This method returns if this color is black.
    
    RETURNS
        boolean: True if this color is black and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean IsBlack(){
        return this == ChessColor.BLACK;
    }
    
    /**/
    /*
    NAME
        public final String toString();
    
    SYNOPSIS
        public final String toString();
    
        No parameters.
    
    DESCRIPTION
        This method returns a human-readable string representation
        of this color.
    
    RETURNS
        String: "WHITE" if the color is white and "BLACK" if the color is black.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final String toString(){
        if(this == ChessColor.WHITE){
            return WHITE_STRING;
        }else{
            return BLACK_STRING;
        }
    }
}