package com.DarkBlue.Testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.DarkBlue.Board.Tile;
import com.DarkBlue.GUI.DarkBlue.MoveTextArea;
import com.DarkBlue.Utilities.BoardUtilities;
import com.DarkBlue.Utilities.Factory;
import com.DarkBlue.Utilities.Utilities;

/*
 * This is a copy of the GUITile class found in DarkBlue.java.
 * It is not functional and is only meant for testing purposes. 
 */
public final class GUITile extends JPanel{

    // Final values for bookkeeping
    private static final long serialVersionUID = Utilities.ONE_LONG;
    
    // Important path and extension
    private static final String EXTENSION = ".png";
    private static final String FOLDER = "src/com/DarkBlue/GUI/ChessPieces/";
    
    // Custom final values
    private final Color m_originalColor;
    private final Tile m_tile;
    
    // The image of the chess piece
    private JLabel m_image;

    /**/
    /*
    NAME
        public GUITile(final Tile a_tile);
    
    SYNOPSIS
        public GUITile(final Tile a_tile);
    
        Tile a_tile -------> The tile to translate to the GUI.
    
    DESCRIPTION
        This constructor translates a Tile object to the GUI.
        It takes in the row and column of the tile as final values.
        If the tile has a piece on it, that piece's type and color are
        parsed and the appropriate image is painted onto the tile.
        If the tile has no piece, the image is set to null and the tile 
        remains empty.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public GUITile(final Tile a_tile){
        super();
        this.m_tile = a_tile;
        
        // Set the color of the tile
        if(a_tile.GetColor().IsWhite()){
            this.m_originalColor = BoardUtilities.WHITE;
        }else{
            this.m_originalColor = BoardUtilities.BLACK;
        }
        
        // Set the size and background color
        this.setSize(new Dimension(Utilities.SIXTY, Utilities.SIXTY));
        this.setPreferredSize(new Dimension(Utilities.SIXTY, Utilities.SIXTY));
        this.setBackground(this.m_originalColor);

        // Load the image onto the tile
        DrawTile();
    }
    
    /**/
    /*
    NAME
        private final void SetPiece();
    
    SYNOPSIS
        private final void SetPiece();
    
        No parameters.
    
    DESCRIPTION
        This method sets the image of the tile's piece, if any.
    
    RETURNS
        Nothing
    
    AUTHOR
        Adapted from the official Oracle website,
        https://docs.oracle.com/javase/tutorial/2d/images/loadimage.html
        and Black Widow Chess by Amir Afghani,
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
    */
    private final void SetPiece(){
        // Remove any irrelevant pieces
        this.removeAll();
        
        // Only add an image if the model tile has a piece on it
        if(this.GetTile().IsOccupied()){
            final String COLOR = this.GetTile().GetPiece().GetColor().toString().toLowerCase();
            final String PIECE = this.GetTile().GetPiece().GetPieceType().toString().toLowerCase();
        
            final String PATH = FOLDER + COLOR + PIECE + EXTENSION;
        
            try{   
                this.m_image = new JLabel(new ImageIcon(ImageIO.read(new File(PATH))));
                this.add(m_image);
            }catch(Exception e){        
                e.printStackTrace();
            }
        }
        
        revalidate();
        repaint();
    }
    
    /**/
    /*
    NAME
        private final void DrawTile();
    
    SYNOPSIS
        private final void DrawTile();
    
        No parameters.
    
    DESCRIPTION
        This method assigns the GUI tile's color and piece image if it is nonempty.
    
    RETURNS
        Nothing
    
    AUTHOR
        Amir Afghani, Black Widow Chess
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
    */
    private final void DrawTile(){
        SetPiece();
        
        revalidate();
        repaint();
    }
    
    /**/
    /*
    NAME
        private final Tile GetTile();
    
    SYNOPSIS
        private final Tile GetTile();
    
        No parameters.
    
    DESCRIPTION
        This method returns the Tile object that corresponds with
        this GUI Tile.
        This Tile is set in the constructor and can never change.
    
    RETURNS
        Tile m_tile: The Tile object.
    
    AUTHOR
        Ryan King
    */
    private final Tile GetTile(){
        return m_tile;
    }
    
    /**/
    /*
    NAME
        private final int GetRow();
    
    SYNOPSIS
        private final int GetRow();
    
        No parameters.
    
    DESCRIPTION
        This method returns this tile's row from its model Tile.
    
    RETURNS
        int: The tile's row.
    
    AUTHOR
        Ryan King
    */
    private final int GetRow(){
        return this.m_tile.GetRow();
    }
    
    /**/
    /*
    NAME
        private final int GetColumn();
    
    SYNOPSIS
        private final int GetColumn();
    
        No parameters.
    
    DESCRIPTION
        This method returns this tile's column from its model Tile.
    
    RETURNS
        int: The tile's column.
    
    AUTHOR
        Ryan King
    */
    private final int GetColumn(){
        return this.m_tile.GetColumn();
    }

    /**/
    /*
    NAME
        private final String toString();
    
    SYNOPSIS
        private final String toString();
    
        No parameters.
    
    DESCRIPTION
        This method returns a string representation of this tile,
        which is in algebraic notation.
    
    RETURNS
        String: The tile's representation in algebraic notation.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final String toString(){
        return BoardUtilities.ToAlgebraic(this.GetRow(), this.GetColumn());
    }
}// End of GUITile class