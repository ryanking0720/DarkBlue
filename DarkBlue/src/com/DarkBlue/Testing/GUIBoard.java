package com.DarkBlue.Testing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.DarkBlue.Board.Board;
import com.DarkBlue.Utilities.BoardUtilities;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Utilities.Utilities;

/*
 * This is a copy of the GUIBoard class found in DarkBlue.java.
 * It is not functional and is only meant for testing purposes. 
 */
public final class GUIBoard extends JPanel{
    
    private static final long serialVersionUID = Utilities.ONE_LONG;
    private final Color BORDER_RED = new Color(100, 25, 5);
    
    private final GUITile[][] m_tiles;
    private Board m_board;
    private ChessColor m_humanColor;

    /**/
    /*
    NAME
        public GUIBoard(final Board a_board);
    
    SYNOPSIS
        public GUIBoard(final Board a_board);
    
        Board a_board -------> The board to translate to the GUI.

    DESCRIPTION
        This constructor translates a Board object to the GUI.
        It assigns the appropriate GUITiles as final values.
        Tile properties are taken care of in the GUITile class.
        The board is laid out either with white on the bottom if
        the human player is white, or with black on the bottom if
        the human player is black. This does not change at any time
        during the game unless a new game is started with the opposite color.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public GUIBoard(final Board a_board, final ChessColor a_humanColor){
        // Set the layout
        super(new GridLayout(Utilities.TEN, Utilities.TEN));    
        
        // Assign the board
        m_board = a_board;

        // Allocate the space for the tiles
        m_tiles = new GUITile[Utilities.TEN][Utilities.TEN];

        // Remove everything
        this.removeAll();
        
        // Build the type of board the human needs to see
        if(a_humanColor.IsWhite()){
            BuildWhiteBoard();
        }else{
            BuildBlackBoard();
        }
        
        // Revalidate and repaint
        revalidate();
        repaint();
    }       
           
    /**/
    /*
    NAME
        private final void DrawBoard();
    
    SYNOPSIS
        private final void DrawBoard();
    
        No parameters.
    
    DESCRIPTION
        This method redraws the GUIBoard.
        It is usually called after a mouse click.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, based off Black Widow Chess by Amir Afghani
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
    */
    private final void DrawBoard(){
        // Remove everything
        this.removeAll();

        // Build the board in the orientation the human wants to see
        if(m_humanColor.IsWhite()){
            BuildWhiteBoard();
        }else{
            BuildBlackBoard();
        }
        
        revalidate();
        repaint();
    }
    
    /**/
    /*
    NAME
        private final void SetBoard(final Board a_board);
    
    SYNOPSIS
        private final void SetBoard(final Board a_board);
    
        Board a_board -------> The board object to be set.
    
    DESCRIPTION
        This method sets the board object in its
        corresponding field.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void SetBoard(final Board a_board){
        m_board = a_board;
    }
    
    /**/
    /*
    NAME
        private final int PieceCount();
    
    SYNOPSIS
        private final int PieceCount();
    
        No parameters.
    
    DESCRIPTION
        This method returns the number of pieces on the current board.
    
    RETURNS
        m_board.PieceCount(): The number of pieces on the current board.
    
    AUTHOR
        Ryan King
    */
    private final int PieceCount(){
        return m_board.PieceCount();
    }
    
    /**/
    /*
    NAME
        private final void BuildWhiteBoard();
    
    SYNOPSIS
        private final void BuildWhiteBoard();
    
        No parameters.
    
    DESCRIPTION
        This method builds a GUIBoard with white
        oriented at the bottom.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, with additional help taken from:
        https://stackoverflow.com/questions/8675038/increasing-decreasing-font-size-inside-textarea-using-jbutton
    */
    private final void BuildWhiteBoard(){
        for(int index = Utilities.ZERO; index < Utilities.ONE_HUNDRED; index++){
            // Use aliases for the row and column
            final int ROW = index / Utilities.TEN;
            final int COLUMN = index % Utilities.TEN;
            
            // Add a normal GUITile if it is within the boundaries of the board
            if(BoardUtilities.HasValidCoordinates(ROW - Utilities.ONE, COLUMN - Utilities.ONE)){
                this.m_tiles[ROW][COLUMN] = new GUITile(m_board.GetTile(ROW - Utilities.ONE, COLUMN - Utilities.ONE));
                this.m_tiles[ROW][COLUMN].setAlignmentX(Component.CENTER_ALIGNMENT);
                this.m_tiles[ROW][COLUMN].setAlignmentY(Component.CENTER_ALIGNMENT);
                this.add(m_tiles[ROW][COLUMN]);
            }else{
                // Set a center-aligned border tile with the proper letter or number
                final JPanel borderTile = new JPanel();
                borderTile.setBackground(this.BORDER_RED);
                borderTile.setSize(new Dimension(Utilities.SIXTY, Utilities.SIXTY));
                borderTile.setAlignmentX(Component.CENTER_ALIGNMENT);
                borderTile.setAlignmentY(Component.CENTER_ALIGNMENT);
                
                // Add a letter
                if(ROW == Utilities.ZERO || ROW == Utilities.NINE){
                    if(COLUMN != Utilities.ZERO && COLUMN != Utilities.NINE){
                        final JLabel LETTER = new JLabel();                         
                        LETTER.setText(BoardUtilities.ToAlgebraicColumn(COLUMN - Utilities.ONE).toUpperCase());
                        LETTER.setForeground(Color.WHITE);
                        Utilities.EnlargeFont(LETTER);
                        
                        LETTER.setAlignmentX(Component.CENTER_ALIGNMENT);
                        LETTER.setAlignmentY(Component.CENTER_ALIGNMENT);
                        
                        borderTile.add(LETTER);
                    }
                    // Add a number
                }else if(COLUMN == Utilities.ZERO || COLUMN == Utilities.NINE){
                    if(ROW != Utilities.ZERO && ROW != Utilities.NINE){
                        final JLabel NUMBER = new JLabel();
                        NUMBER.setSize(new Dimension(Utilities.SIXTY, Utilities.SIXTY));
                        NUMBER.setText(BoardUtilities.ToAlgebraicRow(ROW - Utilities.ONE));
                        NUMBER.setForeground(Color.WHITE);
                        Utilities.EnlargeFont(NUMBER);
                        
                        NUMBER.setAlignmentX(Component.CENTER_ALIGNMENT);
                        NUMBER.setAlignmentY(Component.CENTER_ALIGNMENT);
                        
                        borderTile.add(NUMBER);
                    }
                }
                this.add(borderTile);
            }
        }
    }
    
    /**/
    /*
    NAME
        private final void BuildBlackBoard();
    
    SYNOPSIS
        private final void BuildBlackBoard();
    
        No parameters.
    
    DESCRIPTION
        This method builds a GUIBoard with black
        oriented at the bottom.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, with additional help taken from:
        https://stackoverflow.com/questions/8675038/increasing-decreasing-font-size-inside-textarea-using-jbutton
    */
    private final void BuildBlackBoard(){
        for(int index = Utilities.NINETY_NINE; index >= Utilities.ZERO; index--){    
            final int ROW = index / Utilities.TEN;
            final int COLUMN = index % Utilities.TEN;

            if(BoardUtilities.HasValidCoordinates(ROW - Utilities.ONE, COLUMN - Utilities.ONE)){                
                this.m_tiles[Utilities.SEVEN - (ROW - Utilities.ONE)][Utilities.SEVEN - (COLUMN - Utilities.ONE)] = new GUITile(m_board.GetTile(ROW - Utilities.ONE, COLUMN - Utilities.ONE));
                this.add(m_tiles[Utilities.SEVEN - (ROW - Utilities.ONE)][Utilities.SEVEN - (COLUMN - Utilities.ONE)]);
            }else{
                final JPanel BORDER_TILE = new JPanel();
                BORDER_TILE.setBackground(this.BORDER_RED);
                
                // Add a letter
                if(ROW == Utilities.ZERO || ROW == Utilities.NINE){
                    if(COLUMN != Utilities.ZERO && COLUMN != Utilities.NINE){
                        final JLabel LETTER = new JLabel();
                        LETTER.setSize(new Dimension(Utilities.SIXTY, Utilities.SIXTY));
                        LETTER.setText(BoardUtilities.ToAlgebraicColumn(COLUMN - Utilities.ONE).toUpperCase());
                        LETTER.setForeground(Color.WHITE);
                        Utilities.EnlargeFont(LETTER);
                        BORDER_TILE.add(LETTER);
                    }
                    // Add a number
                }else if(COLUMN == Utilities.ZERO || COLUMN == Utilities.NINE){
                    if(ROW != Utilities.ZERO && ROW != Utilities.NINE){
                        final JLabel NUMBER = new JLabel();
                        NUMBER.setSize(new Dimension(Utilities.SIXTY, Utilities.SIXTY));
                        NUMBER.setText(Integer.toString(Utilities.EIGHT - (ROW - Utilities.ONE)));
                        NUMBER.setForeground(Color.WHITE);
                        Utilities.EnlargeFont(NUMBER);
                        BORDER_TILE.add(NUMBER);
                    }
                }
                this.add(BORDER_TILE);
            }
        }
    }
    
    /**/
    /*
    NAME
        private final Board GetBoard();
    
    SYNOPSIS
        private final Board GetBoard();
    
        No parameters.
    
    DESCRIPTION
        This method returns the board object
        contained within the class.
    
    RETURNS
        Board m_board: The board object.
    
    AUTHOR
        Ryan King
    */
    private final Board GetBoard(){
        return this.m_board;
    }
    
    /*
    NAME
        private final ChessColor WhoseTurnIsIt();
    
    SYNOPSIS
        private final ChessColor WhoseTurnIsIt();
    
        No parameters.
    
    DESCRIPTION
        This method returns the ChessColor 
        telling whose turn it is from the 
        board object.
    
    RETURNS
        ChessColor.WHITE or ChessColor.BLACK, 
        depending on whose turn it is.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final ChessColor WhoseTurnIsIt(){
        return m_board.WhoseTurnIsIt();
    }
    
    /**/
    /*
    NAME
        private final GUITile GetTile(final int a_row, final int a_column);
    
    SYNOPSIS
        private final GUITile GetTile(final int a_row, final int a_column);
    
        int a_row --------------> The row of the desired tile.
        
        int a_column -----------> The column of the desired tile.
    
    DESCRIPTION
        This method retrieves the specified GUITile object,
        or null if the coordinates given are invalid.
    
    RETURNS
        GUITile: The specified GUITile if coordinates are valid,
        and null otherwise. One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final GUITile GetTile(final int a_row, final int a_column){
        // Only return a tile if the coordinates are valid
        if((a_row >= Utilities.ZERO && a_row < Utilities.TEN) && (a_column >= Utilities.ZERO && a_column < Utilities.TEN)){
            return m_tiles[a_row][a_column];
        }else{
            return null;
        }
    }
}