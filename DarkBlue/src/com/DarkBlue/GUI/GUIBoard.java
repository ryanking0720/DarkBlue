package com.DarkBlue.GUI;

import com.DarkBlue.Board.*;
import com.DarkBlue.Move.*;
import com.DarkBlue.Utilities.*;
import com.DarkBlue.Player.*;

import java.awt.GridLayout;
//import java.awt.Dimension;
import java.awt.Graphics;

//import javax.swing.JFrame;
import javax.swing.JPanel;
/*
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import java.io.File;
import javax.imageio.ImageIO;
*/
public final class GUIBoard extends JPanel{
	
	private static final long serialVersionUID = Utilities.ONE_LONG;
	
	private final GUITile[][] m_tiles;
	private Board m_board;

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
		during the game.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	public GUIBoard(final Board a_board){
		super(new GridLayout(Utilities.EIGHT, Utilities.EIGHT));	
		
		m_board = a_board;
		
		m_tiles = new GUITile[Utilities.EIGHT][Utilities.EIGHT];
		
		if(DarkBlue.GetHumanColor().IsWhite()){
			BuildWhiteBoard();
		}else{
			BuildBlackBoard();
		}
		
		revalidate();
		repaint();
	}
	
	public static GUIBoard GetStartingPosition(){
		return new GUIBoard(Board.GetStartingPosition());
	}
	
	public static GUIBoard GetEnPassantTest(){
		return new GUIBoard(Board.GetEnPassantTest());
	}
	
	public static GUIBoard GetStalemateTest(){
		return new GUIBoard(Board.GetStalemateTest());
	}
	
	public static GUIBoard GetCheckmateTest(){
		return new GUIBoard(Board.GetCheckmateTest());
	}
	
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
		Ryan King
	*/
	private final void BuildWhiteBoard(){
		for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){
			int row = index / Utilities.EIGHT;
			int column = index % Utilities.EIGHT;
			this.m_tiles[row][column] = new GUITile(m_board.GetTile(row, column));
			this.add(m_tiles[row][column]);
		}
	}
	
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
		Ryan King
	*/
	private final void BuildBlackBoard(){
		for(int index = (Utilities.SIXTY_FOUR - Utilities.ONE); index >= Utilities.ZERO; index--){	
			int row = index / Utilities.EIGHT;
			int column = index % Utilities.EIGHT;
			this.m_tiles[Utilities.SEVEN - row][Utilities.SEVEN - column] = new GUITile(m_board.GetTile(row, column));
			this.add(m_tiles[Utilities.SEVEN - row][Utilities.SEVEN - column]);
		}
	}
	
	/*
	NAME
		protected final void paintComponent(Graphics a_g);
	
	SYNOPSIS
		protected final void paintComponent(Graphics a_g);
	
		Graphics a_g -------> The Graphics object.
	
	DESCRIPTION
		This method is overridden from Graphics.
		It paints the tiles on the board.
		This method violates the Senior Project naming
		conventions because it is overridden from Java.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	@Override
	protected final void paintComponent(final Graphics a_g){
		super.paintComponent(a_g);
		for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){
			int row = index / Utilities.EIGHT;
			int column = index % Utilities.EIGHT;
			m_tiles[row][column].paintComponent(a_g);
		}
	}
	
	/*
	NAME
		public final void SetBoard(final Board a_board);
	
	SYNOPSIS
		public final void SetBoard(final Board a_board);
	
		Board a_board -------> The board object to be set.
	
	DESCRIPTION
		This method sets the board object in its
		corresponding field.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	public final void SetBoard(final Board a_board){
		m_board = a_board;
	}
	
	/*
	NAME
		public final Board GetBoard();
	
	SYNOPSIS
		public final Board GetBoard();
	
		No parameters.
	
	DESCRIPTION
		This method returns the board object
		contained within the class.
	
	RETURNS
		Board m_board: The board object.
	
	AUTHOR
		Ryan King
	*/
	public final Board GetBoard(){
		return this.m_board;
	}
	
	/*
	NAME
		public final GUIBoard Move(final RegularMove a_candidate);
	
	SYNOPSIS
		public final GUIBoard Move(final RegularMove a_candidate);
	
		RegularMove a_candidate --> The move to be made.
	
	DESCRIPTION
		This method performs the move on the board object
		and reassigns it with its new configuration.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	public final GUIBoard Move(final RegularMove a_candidate){
		m_board = m_board.Move(a_candidate);
		return new GUIBoard(m_board);
	}
	
	/*
	NAME
		public final GUIBoard Attack(final AttackingMove a_candidate);
	
	SYNOPSIS
		public final GUIBoard Attack(final AttackingMove a_candidate);
	
		AttackingMove a_candidate --> The move to be made.
	
	DESCRIPTION
		This method performs the move on the board object
		and reassigns it with its new configuration.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	public final GUIBoard Attack(final AttackingMove a_candidate, final Player a_white, final Player a_black){
		m_board = m_board.Attack(a_candidate, a_white, a_black);
		return new GUIBoard(m_board);
	}
	
	/*
	NAME
		public final GUIBoard Castle(final CastlingMove a_candidate);
	
	SYNOPSIS
		public final GUIBoard Castle(final CastlingMove a_candidate);
	
		CastlingMove a_candidate --> The move to be made.
	
	DESCRIPTION
		This method performs the move on the board object
		and reassigns it with its new configuration.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	public final GUIBoard Castle(final CastlingMove a_candidate){
		m_board = m_board.Castle(a_candidate);
		return new GUIBoard(m_board);
	}
	
	/*
	NAME
		public final GUIBoard EnPassant(final EnPassantMove a_candidate);
	
	SYNOPSIS
		public final GUIBoard EnPassant(final EnPassantMove a_candidate);
	
		EnPassantMove a_candidate --> The move to be made.
	
	DESCRIPTION
		This method performs the move on the board object
		and reassigns it with its new configuration.
	
	RETURNS
		Nothing
	
	AUTHOR
		Ryan King
	*/
	public final GUIBoard EnPassant(final EnPassantMove a_candidate, final Player a_white, final Player a_black){
		m_board = m_board.EnPassant(a_candidate, a_white, a_black);
		return new GUIBoard(m_board);
	}
	
	/*
	NAME
		public final ChessColor WhoseTurnIsIt();
	
	SYNOPSIS
		public final ChessColor WhoseTurnIsIt();
	
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
	public final ChessColor WhoseTurnIsIt(){
		return m_board.WhoseTurnIsIt();
	}
	
	/*
	public static void main(String[] args){
		Board board = Board.GetStartingPosition();
		
		Human player = new Human(ChessColor.BLACK, board);
	
		GUIBoard gBoard = new GUIBoard(board, player.GetColor());
		
		JFrame frame = new JFrame("GUI Board");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(new Dimension(3600, 3600));
		
		frame.add(gBoard);
		
		frame.setVisible(true);
	}
	*/
	
}