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
import com.DarkBlue.Player.Player;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Utilities.Utilities;

/**
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
	
	// This will hold 15 labels
	private final ArrayList<JLabel> m_capturedPieces;

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

		for(int i = Utilities.ZERO; i < 15; i++){
		    // Instantiate each JLabel
		    final JLabel label = new JLabel("");
		    this.m_capturedPieces.add(label);
		    
		    // Add the JLabel to the panel
			this.add(this.m_capturedPieces.get(i));
			this.add(Box.createRigidArea(new Dimension(60, 10)));
		}
	}
	
	/**/
    /*
    NAME
        public final void Clear();
    
    SYNOPSIS
        public final void Clear();
    
        No parameters.
    
    DESCRIPTION
        This method clears the panel by getting rid of all JLabels.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	public final void Clear(){
		this.removeAll();
		this.m_capturedPieces.clear();
	}
	
	/**/
    /*
    NAME
        public final void Refresh(final Player a_player);
    
    SYNOPSIS
        public final void Refresh(final Player a_player);
    
        No parameters.
    
    DESCRIPTION
        This method refreshes the captured piece panel by
        looking at the number of pieces a_player has captured.
        All JLabels are removed and then replaced with new copies
        reflecting the current status of the player's captured piece ArrayList.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	public final void Refresh(final Player a_player){
	    final int PAWNS = a_player.CapturedPawns();
	    final int ROOKS = a_player.CapturedRooks();
	    final int KNIGHTS = a_player.CapturedKnights();
	    final int BISHOPS = a_player.CapturedBishops();
	    final int QUEENS = a_player.CapturedQueens();
	    
	    this.removeAll();
	    
	    this.m_capturedPieces.clear();

	    // Make all pawn labels
	    for(int i = Utilities.ZERO; i < PAWNS; i++){
	        JLabel label = new JLabel(Character.toString(a_player.IsWhite() ? Utilities.BLACK_PAWN_BOARD_ICON : Utilities.WHITE_PAWN_BOARD_ICON));
	        this.m_capturedPieces.add(label);
	    }
	    
	    // Make all rook labels
	    for(int i = Utilities.ZERO; i < ROOKS; i++){
	        JLabel label = new JLabel(Character.toString(a_player.IsWhite() ? Utilities.BLACK_ROOK_BOARD_ICON : Utilities.WHITE_ROOK_BOARD_ICON));
            this.m_capturedPieces.add(label);
	    }
	    
	    // Make all knight labels
	    for(int i = Utilities.ZERO; i < KNIGHTS; i++){
	        JLabel label = new JLabel(Character.toString(a_player.IsWhite() ? Utilities.BLACK_KNIGHT_BOARD_ICON : Utilities.WHITE_KNIGHT_BOARD_ICON));
            this.m_capturedPieces.add(label);
	    }
	    
	    // Make all bishop labels
	    for(int i = Utilities.ZERO; i < BISHOPS; i++){
	        JLabel label = new JLabel(Character.toString(a_player.IsWhite() ? Utilities.BLACK_BISHOP_BOARD_ICON : Utilities.WHITE_BISHOP_BOARD_ICON));
            this.m_capturedPieces.add(label);
	    }
	    
	    // Make all queen labels
	    for(int i = Utilities.ZERO; i < QUEENS; i++){
	        JLabel label = new JLabel(Character.toString(a_player.IsWhite() ? Utilities.BLACK_QUEEN_BOARD_ICON : Utilities.WHITE_QUEEN_BOARD_ICON));
            this.m_capturedPieces.add(label);
	    }
	    
	    // Add every label to the panel
	    for(int i = Utilities.ZERO; i < this.m_capturedPieces.size(); i++){
	        this.add(this.m_capturedPieces.get(i));
	    }
	}
}