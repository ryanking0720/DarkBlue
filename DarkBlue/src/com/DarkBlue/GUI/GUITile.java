package com.DarkBlue.GUI;

import com.DarkBlue.Board.*;
import com.DarkBlue.Utilities.*;

import javax.swing.JPanel;
//import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import java.net.URL;
import java.net.URLConnection;
//import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

public final class GUITile extends JPanel implements MouseListener{

    // Final values for bookkeeping
    private static final long serialVersionUID = Utilities.ONE_LONG;
    
    public static final String EXTENSION = ".png";
    //public static final String FOLDER = "https://github.com/ryanking0720/DarkBlue/tree/master/DarkBlue/src/com/DarkBlue/GUI/ChessPieces/";
    public static final String FOLDER = "GUI/ChessPieces/";
    
    public final String m_actionCommand;
    
    // Custom final values
    private final Color m_originalColor;
    private final Tile m_tile;
    
    // Variables
    private BufferedImage m_piece;
    //private JLabel m_label = new JLabel();

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
            this.m_originalColor = Utilities.WHITE;
        }else{
            this.m_originalColor = Utilities.BLACK;
        }
        
        this.setSize(new Dimension(Utilities.SIXTY, Utilities.SIXTY));
        this.setBackground(this.m_originalColor);
        
        m_actionCommand = Utilities.ToAlgebraic(this.m_tile.GetRow(), this.m_tile.GetColumn());

        SetPiece();
        
        validate();
    }
    
    public final void SetPiece(){
        this.removeAll();
        
        if(this.GetTile().IsOccupied()){
            final String colorString = this.GetTile().GetPiece().GetColor().toString().toLowerCase();
            final String pieceString = this.GetTile().GetPiece().GetPieceType().toString().toLowerCase();
        
            final String path = FOLDER + colorString + pieceString + EXTENSION;
        
            try{
                //URL url = new URL(path);
                //HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                this.m_piece = ImageIO.read(new File(path));//bp      
                this.add(new JLabel(new ImageIcon(m_piece)));
                validate();
                repaint();
            }catch(Exception e){        
                e.printStackTrace();
                System.exit(Utilities.ONE);
            }
        }
    }
    
    public final void LightUp(){
        this.setBackground(Utilities.SELECTED_GREEN);
        repaint();
    }
    
    public final void Revert(){
        this.setBackground(this.m_originalColor);
        repaint();
    }
    
    /*
    NAME
        protected final void paintComponent(Graphics a_g);
    
    SYNOPSIS
        protected final void paintComponent(Graphics a_g);
    
        Graphics a_g -------> The Graphics object.
    
    DESCRIPTION
        This method is overridden from Graphics.
        It paints the tile with the image provided.
        This method violates the Senior Project naming
        conventions because it is overridden from Java.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    /*
    @Override
    protected final void paintComponent(Graphics a_g){
        
        super.paintComponent(a_g);
        a_g.setColor(this.GetColor());
        
        if(this.IsOccupied()){
            this.SetPiece();
            
            //a_g.drawImage(m_piece, Utilities.ZERO, Utilities.ZERO, this);
            //m_label = new JLabel(new ImageIcon(m_piece));
            //this.add(m_label);
            
        }else{
            m_piece = null;
            if(m_label.getParent() == this){
                this.remove(m_label);
            }
        }
    }
    */

    /*
    NAME
        public void mouseClicked(final MouseEvent a_event);
    
    SYNOPSIS
        public void mouseClicked(final MouseEvent a_event);
    
        MouseEvent a_event -------> The MouseEvent object.
    
    DESCRIPTION
        This method determines if this tile was clicked.
        If it was, it will light up in bright green.
        If it was not clicked, the method will determine if
        this tile is in the fairway of a piece that is moving.
        If so, it will turn to a slightly more transparent green.
        If this tile is not part of the selected piece's legal move
        spectrum, it will not change color at all.
        This method violates the Senior Project naming
        conventions because it is overridden from Java.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    @Override
    public final void mouseClicked(final MouseEvent a_event){
        /*
        if(this.IsSelected() || this.IsMovable()){            
            this.m_state = TileState.UNSELECTED;
            this.m_color = this.m_originalColor;
        }else{
            if(a_event.getSource() == this){
                this.m_state = TileState.SELECTED;
                this.m_color = Utilities.SELECTED_GREEN;
            }else{                
                // Determine if this tile is in the path of any legal move
                // and light it up using the more transparent green
            }
        }
        revalidate();
        repaint();
        */
        if(SwingUtilities.isLeftMouseButton(a_event)){
            
        }
    }
    
    /*
    NAME
        public void mouseExited(final MouseEvent a_event);
    
    SYNOPSIS
        public void mouseExited(final MouseEvent a_event);
    
        MouseEvent a_event -------> The MouseEvent object.
    
    DESCRIPTION
        This method determines if this tile was exited.
        It serves no purpose for my chess engine.
        This method violates the Senior Project naming
        conventions because it is overridden from Java.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    @Override
    public final void mouseExited(final MouseEvent a_event){

    }
    
    /*
    NAME
        public void mouseReleased(final MouseEvent a_event);
    
    SYNOPSIS
        public void mouseReleased(final MouseEvent a_event);
    
        MouseEvent a_event -------> The MouseEvent object.
    
    DESCRIPTION
        This method determines if the mouse was released on this tile.
        It serves no purpose for my chess engine.
        This method violates the Senior Project naming
        conventions because it is overridden from Java.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    @Override
    public final void mouseReleased(final MouseEvent a_event){

    }
    
    /*
    NAME
        public void mouseEntered(final MouseEvent a_event);
    
    SYNOPSIS
        public void mouseEntered(final MouseEvent a_event);
    
        MouseEvent a_event -------> The MouseEvent object.
    
    DESCRIPTION
        This method determines if the mouse was entered on this tile.
        It serves no purpose for my chess engine.
        This method violates the Senior Project naming
        conventions because it is overridden from Java.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    @Override
    public final void mouseEntered(final MouseEvent a_event){

    }
    
    /*
    NAME
        public void mousePressed(final MouseEvent a_event);
    
    SYNOPSIS
        public void mousePressed(final MouseEvent a_event);
    
        MouseEvent a_event -------> The MouseEvent object.
    
    DESCRIPTION
        This method determines if the mouse was entered on this tile.
        It serves no purpose for my chess engine.
        This method violates the Senior Project naming
        conventions because it is overridden from Java.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    @Override
    public final void mousePressed(final MouseEvent a_event){

    }
    
    /*
    NAME
        public int GetRow();
    
    SYNOPSIS
        public int GetRow();
    
        No parameters.
    
    DESCRIPTION
        This method returns the row of this object.
        It is the same as the a_tile object that was
        passed into the constructor and will never change.    
    
    RETURNS
        int m_row: The tile's row.
    
    AUTHOR
        Ryan King
    */
    public final int GetRow(){
        return this.m_tile.GetRow();
    }
    
    /*
    NAME
        public int GetColumn();
    
    SYNOPSIS
        public int GetColumn();
    
        No parameters.
    
    DESCRIPTION
        This method returns the column of this object.
        It is the same as the a_tile object that was
        passed into the constructor and will never change.    
    
    RETURNS
        int m_column: The tile's column.
    
    AUTHOR
        Ryan King
    */
    public final int GetColumn(){
        return this.m_tile.GetColumn();
    }
    
    /*
    NAME
        public Color GetOriginalColor();
    
    SYNOPSIS
        public Color GetOriginalColor();
    
        No parameters.
    
    DESCRIPTION
        This method returns the Color
        of the tile in its normal state only.
        This is used to refer back to when a 
        tile gets unselected or if a move is completed.
        This Color is set in the constructor and can never change.
    
    RETURNS
        Color m_originalColor: The tile's original Color.
    
    AUTHOR
        Ryan King
    */
    public final Color GetOriginalColor(){
        return this.m_originalColor;
    }
    
    /*
    NAME
        public Tile GetTile();
    
    SYNOPSIS
        public Tile GetTile();
    
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
    public final Tile GetTile(){
        return m_tile;
    }
    
    public final boolean IsOccupied(){
        return this.m_tile.IsOccupied();
    }
    
    public final boolean IsEmpty(){
        return this.m_tile.IsEmpty();
    }
    
    /*
    public static void main(String[] args){
        JFrame frame = new JFrame("GUI Tile");
        
        frame.setSize(new Dimension(500, 500));
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Tile t = new Tile(ChessColor.BLACK, Utilities.SEVEN, Utilities.FOUR);
        
        King k = new King(ChessColor.WHITE, Utilities.SEVEN, Utilities.FOUR);
        
        t.SetPiece(k);
        
        GUITile tile = new GUITile(t);
        
        frame.add(tile);
        
        frame.setVisible(true);
    }
    */
    
    public final boolean Equals(final GUITile a_tile){
        return this.GetTile().GetPiece().Equals(a_tile.GetTile().GetPiece());
    }
    
    @Override
    public final String toString(){
        return m_actionCommand;
    }
}