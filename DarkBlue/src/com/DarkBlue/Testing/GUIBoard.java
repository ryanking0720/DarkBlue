package com.DarkBlue.Testing;

import com.DarkBlue.Board.Board;
//import com.DarkBlue.Utilities.BoardUtilities;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Utilities.Utilities;

//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.Label;
//import javafx.scene.layout.Background;
//import javafx.scene.layout.BackgroundFill;
//import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
//import javafx.scene.layout.Pane;
//import javafx.scene.paint.Color;

/**
 * This is a copy of the GUIBoard class found in DarkBlue.java.
 * It is not functional and is only meant for testing purposes. 
 */
public final class GUIBoard extends GridPane{

	//private final Color BORDER_RED = new Color(100, 25, 5, 1);

	private final GUITile[][] m_tiles;
	private Board m_board;

	/**/
	/*
	 * NAME public GUIBoard(final Board a_board);
	 * 
	 * SYNOPSIS public GUIBoard(final Board a_board);
	 * 
	 * Board a_board -------> The board to translate to the GUI.
	 * 
	 * DESCRIPTION This constructor translates a Board object to the GUI. It assigns
	 * the appropriate GUITiles as final values. Tile properties are taken care of
	 * in the GUITile class. The board is laid out either with white on the bottom
	 * if the human player is white, or with black on the bottom if the human player
	 * is black. This does not change at any time during the game unless a new game
	 * is started with the opposite color.
	 * 
	 * RETURNS Nothing
	 * 
	 * AUTHOR Code written by Ryan King with idea inspired by Amir Afghani
	 * https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/
	 * Table.java
	 */
	public GUIBoard(final Board a_board){
		// Set the layout
		super(/*new GridLayout(Utilities.TEN, Utilities.TEN)*/);

		// Allocate the space for the tiles
		m_tiles = new GUITile[Utilities.TEN][Utilities.TEN];

		// Assign the board
		SetBoard(a_board);

		//DrawBoard();
	}


	/**/
	/*
	 * NAME public final void SetBoard(final Board a_board);
	 * 
	 * SYNOPSIS public final void SetBoard(final Board a_board);
	 * 
	 * Board a_board -------> The board object to be set.
	 * 
	 * DESCRIPTION This method sets the board object in its corresponding field.
	 * 
	 * RETURNS Nothing
	 * 
	 * AUTHOR Code written by Ryan King with idea inspired by Amir Afghani
	 * https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/
	 * Table.java
	 */
	public final void SetBoard(final Board a_board){
		m_board = a_board;
	}

	/**/
	/*
	 * NAME private final int PieceCount();
	 * 
	 * SYNOPSIS private final int PieceCount();
	 * 
	 * No parameters.
	 * 
	 * DESCRIPTION This method returns the number of pieces on the current board.
	 * 
	 * RETURNS int: m_board.PieceCount(): The number of pieces on the current board.
	 * 
	 * AUTHOR Ryan King
	 
	private final int PieceCount(){
		return m_board.PieceCount();
	}
	*/

	/**/
	/*
	 * NAME private final void BuildWhiteBoard();
	 * 
	 * SYNOPSIS private final void BuildWhiteBoard();
	 * 
	 * No parameters.
	 * 
	 * DESCRIPTION This method builds a GUIBoard with white oriented at the bottom.
	 * 
	 * RETURNS Nothing
	 * 
	 * AUTHOR Ryan King, with additional help taken from:
	 * https://stackoverflow.com/questions/8675038/increasing-decreasing-font-size-
	 * inside-textarea-using-jbutton
	 */
	/*
	private final void BuildWhiteBoard(){
		for(int index = Utilities.ZERO; index < Utilities.ONE_HUNDRED; index++){
			// Use aliases for the row and column
			final int ROW = index / Utilities.TEN;
			final int COLUMN = index % Utilities.TEN;

			// Add a normal GUITile if it is within the boundaries of the board
			if(BoardUtilities.HasValidCoordinates(ROW - Utilities.ONE, COLUMN - Utilities.ONE)){
				this.m_tiles[ROW][COLUMN] = new GUITile(
						m_board.GetTile(ROW - Utilities.ONE, COLUMN - Utilities.ONE));
				this.getChildren().add(m_tiles[ROW][COLUMN]);
			}else{
				AddWhiteBorderTile(ROW, COLUMN);
			}
		}
	}
	 */
	/**/
	/*
	 * NAME private final void AddWhiteBorderTile(final int a_row, final int
	 * a_column);
	 * 
	 * SYNOPSIS private final void AddWhiteBorderTile(final int a_row, final int
	 * a_column);
	 * 
	 * int a_row --------------> The row of the grid.
	 * 
	 * int a_column -----------> The column of the grid.
	 * 
	 * DESCRIPTION This method inserts a non-functional "border tile" on the outside
	 * of the chessboard with algebraic notation if it is not a corner. This varies
	 * depending upon which side the human player chose.
	 * 
	 * RETURNS Nothing
	 * 
	 * AUTHOR Ryan King, with additional help taken from:
	 * https://stackoverflow.com/questions/8675038/increasing-decreasing-font-size-
	 * inside-textarea-using-jbutton
	 */
	/*
	private final void AddWhiteBorderTile(final int a_row, final int a_column) {
		// Set a border tile with the proper letter or number
		final Pane BORDER_TILE = new Pane();
		BORDER_TILE.setBackground(new Background(new BackgroundFill(this.BORDER_RED, CornerRadii.EMPTY, Insets.EMPTY)));
		BORDER_TILE.setPrefSize(60, 60);
		//BORDER_TILE.setAlignment(Pos.CENTER);

		// Add a letter
		if(m_board.PieceCount() > Utilities.ZERO){
			if(a_row == Utilities.ZERO || a_row == Utilities.NINE){
				if(a_column != Utilities.ZERO && a_column != Utilities.NINE){
					final Label LETTER = new Label();
					LETTER.setPrefSize(60, 60);
					LETTER.setText(BoardUtilities.ToAlgebraicColumn(a_column - Utilities.ONE).toUpperCase());
					LETTER.setTextFill(Color.WHITE);
					Utilities.EnlargeFont(LETTER);

					LETTER.setAlignment(Pos.CENTER);

					BORDER_TILE.getChildren().add(LETTER);
				}
				// Add a number
			}else if(a_column == Utilities.ZERO || a_column == Utilities.NINE){
				if(a_row != Utilities.ZERO && a_row != Utilities.NINE){
					final Label NUMBER = new Label();
					NUMBER.setPrefSize(60, 60);
					NUMBER.setText(BoardUtilities.ToAlgebraicRow(a_row - Utilities.ONE));
					NUMBER.setTextFill(Color.WHITE);
					Utilities.EnlargeFont(NUMBER);

					NUMBER.setAlignment(Pos.CENTER);

					BORDER_TILE.getChildren().add(NUMBER);
				}
			}
		}
		this.getChildren().add(BORDER_TILE);
	}
	 */
	/**/
	/*
	 * NAME private final void AddBlackBorderTile(final int a_row, final int
	 * a_column);
	 * 
	 * SYNOPSIS private final void AddBlackBorderTile(final int a_row, final int
	 * a_column);
	 * 
	 * int a_row --------------> The row of the grid.
	 * 
	 * int a_column -----------> The column of the grid.
	 * 
	 * DESCRIPTION This method inserts a non-functional "border tile" on the outside
	 * of the chessboard with algebraic notation if it is not a corner. This varies
	 * depending upon which side the human player chose.
	 * 
	 * RETURNS Nothing
	 * 
	 * AUTHOR Ryan King, with additional help taken from:
	 * https://stackoverflow.com/questions/8675038/increasing-decreasing-font-size-
	 * inside-textarea-using-jbutton
	 */
	/*
	private final void AddBlackBorderTile(final int a_row, final int a_column){
		// Set a center-aligned border tile with the proper letter or number
		final Pane BORDER_TILE = new Pane();
		BORDER_TILE.setBackground(new Background(new BackgroundFill(this.BORDER_RED, CornerRadii.EMPTY, Insets.EMPTY)));
		BORDER_TILE.setPrefSize(60, 60);

		// Add a letter
		if(m_board.PieceCount() > Utilities.ZERO){
			if(a_row == Utilities.ZERO || a_row == Utilities.NINE){
				if(a_column != Utilities.ZERO && a_column != Utilities.NINE){
					final Label LETTER = new Label();
					LETTER.setPrefSize(60, 60);
					LETTER.setText(BoardUtilities.ToAlgebraicColumn(a_column - Utilities.ONE).toUpperCase());
					LETTER.setTextFill(Color.WHITE);
					Utilities.EnlargeFont(LETTER);
					BORDER_TILE.getChildren().add(LETTER);
				}
				// Add a number
			} else if(a_column == Utilities.ZERO || a_column == Utilities.NINE){
				if(a_row != Utilities.ZERO && a_row != Utilities.NINE){
					final Label NUMBER = new Label();
					NUMBER.setPrefSize(60, 60);
					NUMBER.setText(Integer.toString(Utilities.EIGHT - (a_row - Utilities.ONE)));
					NUMBER.setTextFill(Color.WHITE);
					Utilities.EnlargeFont(NUMBER);
					BORDER_TILE.getChildren().add(NUMBER);
				}
			}
		}
		this.getChildren().add(BORDER_TILE);
	}
	*/
	/**/
	/*
	 * NAME private final void BuildBlackBoard();
	 * 
	 * SYNOPSIS private final void BuildBlackBoard();
	 * 
	 * No parameters.
	 * 
	 * DESCRIPTION This method builds a GUIBoard with black oriented at the bottom.
	 * 
	 * RETURNS Nothing
	 * 
	 * AUTHOR Ryan King, with additional help taken from:
	 * https://stackoverflow.com/questions/8675038/increasing-decreasing-font-size-
	 * inside-textarea-using-jbutton
	 */
	/*
	private final void BuildBlackBoard(){
		for(int index = Utilities.NINETY_NINE; index >= Utilities.ZERO; index--){
			final int ROW = index / Utilities.TEN;
			final int COLUMN = index % Utilities.TEN;

			// Determine which type of tile to add
			if (BoardUtilities.HasValidCoordinates(ROW - Utilities.ONE, COLUMN - Utilities.ONE)){
				this.m_tiles[Utilities.SEVEN - (ROW - Utilities.ONE)][Utilities.SEVEN - (COLUMN - Utilities.ONE)] = new GUITile(m_board.GetTile(ROW - Utilities.ONE, COLUMN - Utilities.ONE));
				this.getChildren().add(m_tiles[Utilities.SEVEN - (ROW - Utilities.ONE)][Utilities.SEVEN - (COLUMN - Utilities.ONE)]);
			}else{
				AddBlackBorderTile(ROW, COLUMN);
			}
		}
	}
	 */
	/**/
	/*
	 * NAME public final Board GetBoard();
	 * 
	 * SYNOPSIS public final Board GetBoard();
	 * 
	 * No parameters.
	 * 
	 * DESCRIPTION This method returns the board object contained within the class.
	 * 
	 * RETURNS Board m_board: The board object.
	 * 
	 * AUTHOR Code written by Ryan King with idea inspired by Amir Afghani
	 * https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/
	 * Table.java
	 */
	public final Board GetBoard(){
		return this.m_board;
	}

	/**/
	/*
	 * NAME public final ChessColor WhoseTurnIsIt();
	 * 
	 * SYNOPSIS public final ChessColor WhoseTurnIsIt();
	 * 
	 * No parameters.
	 * 
	 * DESCRIPTION This method returns the ChessColor telling whose turn it is from
	 * the board object.
	 * 
	 * RETURNS ChessColor.WHITE or ChessColor.BLACK, depending on whose turn it is.
	 * One of these two options will always occur.
	 * 
	 * AUTHOR Code written by Ryan King with idea inspired by Amir Afghani
	 * https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/
	 * Table.java
	 */
	public final ChessColor WhoseTurnIsIt(){
		return this.m_board.WhoseTurnIsIt();
	}

	/**/
	/*
	 * NAME private final GUITile GetTile(final int a_row, final int a_column);
	 * 
	 * SYNOPSIS private final GUITile GetTile(final int a_row, final int a_column);
	 * 
	 * int a_row --------------> The row of the desired tile.
	 * 
	 * int a_column -----------> The column of the desired tile.
	 * 
	 * DESCRIPTION This method retrieves the specified GUITile object, or null if
	 * the coordinates given are invalid.
	 * 
	 * RETURNS GUITile: The specified GUITile if coordinates are valid, and null
	 * otherwise. One of these two options will always occur.
	 * 
	 * AUTHOR Ryan King
	 */
	public final GUITile GetTile(final int a_row, final int a_column){
		// Only return a tile if the coordinates are valid
		if((a_row >= Utilities.ZERO && a_row < Utilities.TEN) && (a_column >= Utilities.ZERO && a_column < Utilities.TEN)){
			return this.m_tiles[a_row][a_column];
		}else{
			return null;
		}
	}
}