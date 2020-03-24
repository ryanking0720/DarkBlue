package com.DarkBlue.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.DarkBlue.Piece.*;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Utilities.Utilities;

/*
 * This represents a panel that contains all captured pieces
 * in a vertical column.
 * 
 * It uses the board icons of any piece passed in and adds it
 * to the existing array of JLabels.
 * 
 * All pieces are sorted in the following order, assuming all of these
 * are on the board and none get promoted:
 * 
 * P P P P P P P P R R N N B B Q
 * 
 * This still allows for fewer pawns and more rooks, bishops,
 * knights, or queens if necessary.
 */
public class CapturedPiecePanel extends JPanel{

	private static final long serialVersionUID = Utilities.ONE_LONG;
	
	// Only 15 pieces can be captured in a normal game
	public static final int MAX = 15;
	
	// This will hold 15 labels
	private final ArrayList<JLabel> m_capturedPieces;
	
	// This keeps track of the number of spots that have been filled
	private int m_spotsFilled;

	/**/
    /*
    NAME
        public CapturedPiecePanel();
    
    SYNOPSIS
        public CapturedPiecePanel();
    
        No parameters.
    
    DESCRIPTION
        This constructor creates a new CapturedPiecePanel object.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, with additional help taken from:
        https://stackoverflow.com/questions/8675038/increasing-decreasing-font-size-inside-textarea-using-jbutton
    */
	public CapturedPiecePanel(){
		super();
		// Configure the layout
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		// Set the size
		this.setPreferredSize(new Dimension(80, 480));
		
		// Set the background color
		this.setBackground(Color.WHITE);
		
		// Instaintiate the JLabel space
		this.m_capturedPieces = new ArrayList<>();
		
		// No spots should be filled
		this.m_spotsFilled = Utilities.ZERO;
		
		for(int i = Utilities.ZERO; i < MAX; i++){
		    // Instantiate each JLabel
		    final JLabel label = new JLabel("");
		    this.m_capturedPieces.add(label);
		    
		    // Add the JLabel to the panel
			this.add(this.m_capturedPieces.get(i));
			this.add(Box.createRigidArea(new Dimension(60, 10)));
			
			// Make the font larger
			final Font font = this.m_capturedPieces.get(i).getFont();
	        final float size = font.getSize() + 15.0f;
	        this.setFont(font.deriveFont(size));
		}

		// Make the font larger
		final Font font = this.getFont();
		final float size = font.getSize() + 15.0f;
		this.setFont(font.deriveFont(size));
	}
	
	/**/
    /*
    NAME
        public final void Clear();
    
    SYNOPSIS
        public final void Clear();
    
        No parameters.
    
    DESCRIPTION
        This method clears the panel by setting all JLabels to empty strings.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, with additional help taken from:
        https://stackoverflow.com/questions/8675038/increasing-decreasing-font-size-inside-textarea-using-jbutton
    */
	public final void Clear(){
		this.m_spotsFilled = Utilities.ZERO;
		for(int i = Utilities.ZERO; i < this.m_capturedPieces.size(); i++){
		    // Change all JLabels to empty strings
			this.m_capturedPieces.get(i).setText("");
		}
	}
	
	/**/
    /*
    NAME
        public final void Insert(final Piece a_piece);
    
    SYNOPSIS
        public final void Insert(final Piece a_piece);
    
        Piece a_piece -----------> The piece to be inserted.
    
    DESCRIPTION
        This method inserts the given piece in sorted order
        as described above in the description for this class.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	public final void Insert(final Piece a_piece){
	    // Mark that a new piece was added
	    m_spotsFilled++;
	    
	    // Get the icon to be placed into the panel
	    final char icon = a_piece.GetBoardIcon();
	    
	    // Count up the existing pieces already inside the panel
	    int pawns = Pawns();
	    int rooks = Rooks();
	    int knights = Knights();
	    int bishops = Bishops();
	    int queens = Queens();
	    
	    // Add a new label for the new piece if necessary
	    if(m_spotsFilled > MAX){
	        final JLabel label = new JLabel("");
	        this.m_capturedPieces.add(label);
	    }
	    
	    // Increment the appropriate bucket
	    switch(a_piece.GetPieceType()){
	        case PAWN: pawns++;
	        break;
	        case ROOK: rooks++;
            break;
	        case KNIGHT: knights++;
            break;
	        case BISHOP: bishops++;
            break;
	        case QUEEN: queens++;
            break;
            default: return;
	    }
	    
	    // i will be the cumulative index,
	    // j will be the index counting each individual piece type
	    int i = Utilities.ZERO, j = Utilities.ZERO;
	    
	    // Adjust all pawns
	    while(i < m_spotsFilled && j < pawns){
	        if(a_piece.IsWhite()){
	            m_capturedPieces.get(i).setText(Character.toString(Utilities.WHITE_PAWN_BOARD_ICON));
	        }else{
                m_capturedPieces.get(i).setText(Character.toString(Utilities.BLACK_PAWN_BOARD_ICON));
            }
	        i++;
	        j++;
	    }
	    
	    // Reset for the next piece
	    j = Utilities.ZERO;
	    
	    // Adjust all rooks
	    while(i < m_spotsFilled && j < rooks){
            if(a_piece.IsWhite()){
                m_capturedPieces.get(i).setText(Character.toString(Utilities.WHITE_ROOK_BOARD_ICON));
            }else{
                m_capturedPieces.get(i).setText(Character.toString(Utilities.BLACK_ROOK_BOARD_ICON));
            }
            i++;
            j++;
        }
        
	    // Reset for the next piece
        j = Utilities.ZERO;
	    
	    // Adjust all knights
        while(i < m_spotsFilled && j < knights){
            if(a_piece.IsWhite()){
                m_capturedPieces.get(i).setText(Character.toString(Utilities.WHITE_KNIGHT_BOARD_ICON));
            }else{
                m_capturedPieces.get(i).setText(Character.toString(Utilities.BLACK_KNIGHT_BOARD_ICON));
            }
            i++;
            j++;
        }
        
        // Reset for the next piece
        j = Utilities.ZERO;
	    
	    // Adjust all bishops
        while(i < m_spotsFilled && j < bishops){
            if(a_piece.IsWhite()){
                m_capturedPieces.get(i).setText(Character.toString(Utilities.WHITE_BISHOP_BOARD_ICON));
            }else{
                m_capturedPieces.get(i).setText(Character.toString(Utilities.BLACK_BISHOP_BOARD_ICON));
            }
            i++;
            j++;
        }
        
        // Reset for the next piece
        j = Utilities.ZERO;
	    
	    // Adjust all queens
        while(i < m_spotsFilled && j < queens){
            if(a_piece.IsWhite()){
                m_capturedPieces.get(i).setText(Character.toString(Utilities.WHITE_QUEEN_BOARD_ICON));
            }else{
                m_capturedPieces.get(i).setText(Character.toString(Utilities.BLACK_QUEEN_BOARD_ICON));
            }
            i++;
            j++;
        }
	}
	
	/**/
    /*
    NAME
        public final int Pawns();
    
    SYNOPSIS
        public final int Pawns();
    
        No parameters.
    
    DESCRIPTION
        This method returns the number of pawns counted
        inside the panel.
    
    RETURNS
        int pawns: The number of pawns in the panel from [0, 8].
    
    AUTHOR
        Ryan King
    */
	public final int Pawns(){
	    // This variable will hold how many of this piece are in the panel
		int pawns = Utilities.ZERO;
		
		for(int i = Utilities.ZERO; i < MAX; i++){
			if(this.m_capturedPieces.get(i).getText().equals(Character.toString(Utilities.WHITE_PAWN_BOARD_ICON)) || this.m_capturedPieces.get(i).getText().equals(Character.toString(Utilities.BLACK_PAWN_BOARD_ICON))){
				pawns++;
			}
		}
		
		return pawns;
	}
	
	/**/
    /*
    NAME
        public final int Rooks();
    
    SYNOPSIS
        public final int Rooks();
    
        No parameters.
    
    DESCRIPTION
        This method returns the number of rooks counted
        inside the panel.
    
    RETURNS
        int rooks: The number of rooks in the panel from [0, 10].
    
    AUTHOR
        Ryan King
    */
	public final int Rooks(){
	    // This variable will hold how many of this piece are in the panel
		int rooks = Utilities.ZERO;
		
		for(int i = Utilities.ZERO; i < MAX; i++){
			if(this.m_capturedPieces.get(i).getText().equals(Character.toString(Utilities.WHITE_ROOK_BOARD_ICON)) || this.m_capturedPieces.get(i).getText().equals(Character.toString(Utilities.BLACK_ROOK_BOARD_ICON))){
				rooks++;
			}
		}
		
		return rooks;
	}
	
	/**/
    /*
    NAME
        public final int Knights();
    
    SYNOPSIS
        public final int Knights();
    
        No parameters.
    
    DESCRIPTION
        This method returns the number of knights counted
        inside the panel.
    
    RETURNS
        int knights: The number of knights in the panel from [0, 10].
    
    AUTHOR
        Ryan King
    */
	public final int Knights(){
	    // This variable will hold how many of this piece are in the panel
		int knights = Utilities.ZERO;
		
		for(int i = Utilities.ZERO; i < MAX; i++){
			if(this.m_capturedPieces.get(i).getText().equals(Character.toString(Utilities.WHITE_KNIGHT_BOARD_ICON)) || this.m_capturedPieces.get(i).getText().equals(Character.toString(Utilities.BLACK_KNIGHT_BOARD_ICON))){
				knights++;
			}
		}
		
		return knights;
	}
	
	/**/
    /*
    NAME
        public final int Bishops();
    
    SYNOPSIS
        public final int Bishops();
    
        No parameters.
    
    DESCRIPTION
        This method returns the number of bishops counted
        inside the panel.
    
    RETURNS
        int bishops: The number of bishops in the panel from [0, 10].
    
    AUTHOR
        Ryan King
    */
	public final int Bishops(){
	    // This variable will hold how many of this piece are in the panel
		int bishops = Utilities.ZERO;
		
		for(int i = Utilities.ZERO; i < MAX; i++){
			if(this.m_capturedPieces.get(i).getText().equals(Character.toString(Utilities.WHITE_BISHOP_BOARD_ICON)) || this.m_capturedPieces.get(i).getText().equals(Character.toString(Utilities.BLACK_BISHOP_BOARD_ICON))){
				bishops++;
			}
		}
		
		return bishops;
	}
	
	/**/
    /*
    NAME
        public final int Queens();
    
    SYNOPSIS
        public final int Queens();
    
        No parameters.
    
    DESCRIPTION
        This method returns the number of queens counted
        inside the panel.
    
    RETURNS
        int queens: The number of queens in the panel from [0, 9].
    
    AUTHOR
        Ryan King
    */
	public final int Queens(){
	    // This variable will hold how many of this piece are in the panel
		int queens = Utilities.ZERO;
		
		for(int i = Utilities.ZERO; i < MAX; i++){
			if(this.m_capturedPieces.get(i).getText().equals(Character.toString(Utilities.WHITE_QUEEN_BOARD_ICON)) || this.m_capturedPieces.get(i).getText().equals(Character.toString(Utilities.BLACK_QUEEN_BOARD_ICON))){
				queens++;
			}
		}
		
		return queens;
	}
}