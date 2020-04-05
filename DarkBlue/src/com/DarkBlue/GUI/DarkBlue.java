package com.DarkBlue.GUI;

import com.DarkBlue.Board.Board;
import com.DarkBlue.Board.Tile;
import com.DarkBlue.Board.Board.BoardBuilder;
import com.DarkBlue.Game.GameState;
import com.DarkBlue.Move.*;
import com.DarkBlue.Piece.*;
import com.DarkBlue.Player.*;
import com.DarkBlue.Testing.GameTest;
import com.DarkBlue.Utilities.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.junit.Test;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/*
 * This class is the driver class for the entire program.
 * 
 * It is the main class for the Dark Blue chess engine.
 * It contains a GUIBoard object on which to play,
 * two Player objects representing both sides,
 * an alias for the current player used to reduce code,
 * an alias for the current move,
 * aliases for the moving piece and the victim (set to null if there is no victim),
 * ArrayLists to store boards and moves made during the game,
 * integers representing source and destination tile coordinates,
 * the number of moves made in the game without pawn movements or captures,
 * Java Swing components with which to build the user interface,
 * immutable Strings used as headings and bodies of JOptionPane dialogue boxes,
 * the color representing which choice the human player initially made,
 * and the game state (Check, stalemate, checkmate, draw, or normal; explained in further detail in its own section).
 * 
 * This class is a singleton.
 * 
 * The main() method initializes one and only one instance of the Dark Blue engine
 * and does not allow the user to instantiate another unless the first one is destroyed.
 * 
 * Once initialized, the human player will have a choice between playing as white or black.
 * 
 * White always goes first, so most players will choose that option over black.
 * That is why I placed it on the left; most people would read the box from left to right.
 * The other side is taken by the computer. This will not change throughout the duration of the game.
 * 
 * Once the game ends, the player will have a chance to play a new game as the other side or continue to play as his/her previous side.
 * 
 * The game will continue indefinitely until one side wins (and the other consequently loses),
 * the game draws due to certain conditions, or if the human player chooses to resign (this counts as a loss).
 */
public final class DarkBlue extends JFrame{
    
    private static final long serialVersionUID = Utilities.ONE_LONG;
    
    // Symbolic constants and messages
    public static final String TITLE = "Dark Blue";
    public static final String GAME_OVER = "Game Over";
    public static final String PLAY_AS = "Play as:";
    public static final String CHECK_MESSAGE = "Check!";
    public static final String STALEMATE_MESSAGE = "Stalemate!\nIt\'s a draw.";
    public static final String FIFTY_MOVE_MESSAGE = "Fifty-move rule!\nIt\'s a draw.";
    public static final String INSUFFICIENT_MATERIAL_MESSAGE = "Insufficient material!\nIt\'s a draw.";
    public static final String THREEFOLD_REPETITION_MESSAGE = "Threefold repetition!\nIt\'s a draw.";
    public static final String WHITE_CHECKMATE_MESSAGE = "Checkmate!\nWhite wins.";
    public static final String BLACK_CHECKMATE_MESSAGE = "Checkmate!\nBlack wins.";
    public static final String RESIGN = "Resign";
    public static final String RESIGN_QUESTION = "Do you really want to resign?";
    public static final String WHITE_RESIGNATION = "White wins by resignation.";
    public static final String BLACK_RESIGNATION = "Black wins by resignation.";
    public static final Object WHITE = "White";
    public static final Object BLACK = "Black";
    
    // Determines if legal moves should be highlighted on the human's turn
    private static boolean m_shouldHighlightLegalMoves = false;
    
    // Static fields for move evaluation and serialization
    private static boolean m_isPreviouslySavedGame = false;
    private static boolean m_canWhiteKingsideCastle = false;
    private static boolean m_canWhiteQueensideCastle = false;
    private static boolean m_canBlackKingsideCastle = false;
    private static boolean m_canBlackQueensideCastle = false;
    private static String m_enPassantTile = null;
    private static String m_promotionString = null;
    private static boolean m_isRestarted = false;
    
    // Represents the state of the game
    private static GameState m_gameState = GameState.EMPTY;

    // The visual representation of the board
    private GUIBoard m_board;

    // Contains the observer
    private GameWatcher m_watcher = new GameWatcher();
    
    // The alias for the current player, used to reduce code
    private Player m_currentPlayer;
        
    // The history of the game from move to move
    private Stack<String> m_gameHistory;
    
    // The number of times each board position has occurred during the game
    private HashMap<String, Integer> m_positions;

    // The colors of both players
    private static ChessColor m_humanColor, m_computerColor;
    
    // The players described by color
    private static Player m_white, m_black;
    
    // The players described by type
    private static Player m_humanPlayer, m_computerPlayer;
    
    // The original row and column used for determining en passant captures
    private static int m_originalRow;
    private static int m_originalColumn;

    // The number of moves made since the last capture or pawn movement
    private int m_currentHalfmoves = Utilities.ZERO;
    
    // The number of moves made by both white then black during the entire game
    private int m_fullmoves = Utilities.ONE;
        
    // Search depth for the AI
    private static int m_depth = Utilities.THREE;
       
    // The integer that keeps track of the button
    private int m_buttonInt;
        
    // The piece to be moved
    private Piece m_candidate;
    
    // MouseListener fields for the tiles the human player chooses
    private Tile m_sourceTile = null;

    private Tile m_destinationTile = null;
        
    // The victim of the move, if any
    private Piece m_victim;
        
    // The piece that was moved previously. Useful in determining en passant for pawns
    private static Piece m_previouslyMoved;
        
    // The move to be made once evaluated
    private Move m_nextMove;
    
    // The singleton instance of the engine
    private static DarkBlue m_instance;   
    
    /* My custom extensions of Swing components */    
    private DarkBlueMenuBar m_menuBar = new DarkBlueMenuBar();
    private MoveTextArea m_whiteMoves;
    private MoveTextArea m_blackMoves;
    private JScrollPane m_whiteScroll;
    private JScrollPane m_blackScroll;
    private CapturedPiecePanel m_whitePieces = new CapturedPiecePanel();
    private CapturedPiecePanel m_blackPieces = new CapturedPiecePanel();
    
    /* Regular Swing components */
    private JPanel m_top = new JPanel();
    private JPanel m_left = new JPanel();
    private JPanel m_right = new JPanel();
    private JPanel m_bottom = new JPanel();
    
    // These will hold the JScrollPanes and their labels
    private JPanel m_whiteInner = new JPanel();
    private JPanel m_blackInner = new JPanel();
    
    // Components for the "Thinking..." dialog box
    private JOptionPane m_optionPane;
	private JDialog m_dialog;

    // Contains the GUIBoard object
    private JPanel m_boardPanel = new JPanel();

    // Header labels for move history panes
    private JLabel m_whiteLabel = new JLabel("White");
    private JLabel m_blackLabel = new JLabel("Black");
    
    // Allows the computer to move without stopping the EDT
    private SwingWorker<Move, Void> m_worker;
    
    // Bookkeeping field that makes sure an invalid file
    // was chosen before showing the user an error message
    // i.e., an error message will not show if the user did
    // not choose a file to open in the first place
    private String m_filename = "";
    
    /**/
    /*
    NAME
        private DarkBlue();
    
    SYNOPSIS
        private DarkBlue();
    
        No parameters.
    
    DESCRIPTION
        This constructor initializes all necessary fields and creates the singleton 
        instance of the Dark Blue chess engine, ready to start a new game.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, with additional help on JScrollPanes taken from:
        https://stackoverflow.com/questions/45558095/jscrollpane-not-scrolling-in-jtextarea
        https://stackoverflow.com/questions/10346449/scrolling-a-jpanel
    */
    private DarkBlue(){        
        super(TITLE);
      
        // Initialize important final fields
        this.m_positions = new HashMap<>();
        this.m_gameHistory = new Stack<>();
        
        // Initialize both colors to defaults
        m_humanColor = ChessColor.WHITE;
        m_computerColor = ChessColor.BLACK;
        
        // Initialize the text areas and scroll panes
        m_whiteMoves = new MoveTextArea();
        m_blackMoves = new MoveTextArea();
        m_whiteScroll = new JScrollPane(m_whiteMoves);
        m_blackScroll = new JScrollPane(m_blackMoves);
        
        // Set sizes for the scroll panes
        m_whiteScroll.setPreferredSize(new Dimension(80, 575));
        m_blackScroll.setPreferredSize(new Dimension(80, 575));
        
        // Make sure the panes are dynamically scrollable
        ///////////////////////////////////////////////////////////////
        // Source: https://stackoverflow.com/questions/45558095/jscrollpane-not-scrolling-in-jtextarea
        m_whiteScroll.setViewportView(m_whiteMoves);
        m_whiteScroll.getPreferredSize();       
        m_blackScroll.setViewportView(m_blackMoves);
        m_blackScroll.getPreferredSize();
        ///////////////////////////////////////////////////////////////
        
        // Set scrollbar policies based on an answer from Guillaume Poulet
        // Source: https://stackoverflow.com/questions/10346449/scrolling-a-jpanel
        m_whiteScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        m_blackScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        m_whiteScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        m_blackScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Set up the menu
        this.m_menuBar.DisableLiveGameButtons();
        
        // Initialize the board and the players
        this.m_board = new GUIBoard(Board.GetEmptyBoard());
        InitializePlayers(m_board.GetBoard());
        
        // Assign the correct player
        this.m_currentPlayer = (this.m_board.WhoseTurnIsIt() == ChessColor.WHITE ? m_white : m_black);

        this.CreateAndShowGUI();
    }
    
    /* Overridden MouseListener and ActionListener events */
    
    /**/
    /*
    NAME
        private final String GetDate();
    
    SYNOPSIS
        private final String GetDate();
        
        No parameters.
    
    DESCRIPTION
        This method gets today's date and time as a string
        by using the Calendar class and returns it.
        The string format is YYYYMMDDHHMMSS.
        YYYY represents the year.
        MM represents the month.
        DD represents the day.
        HH represents the number of hours that have passed since midnight.
        MM represents the number of minutes.
        SS represents the number of seconds.
    
    RETURNS
        A string containing the above mentioned fields.
    
    AUTHOR
        Ryan King
    */
    private final String GetDate(){
        // Get all necessary fields to create a unique timestamp
    	final Calendar NOW = Calendar.getInstance();
    	
    	final int YEAR = NOW.get(Calendar.YEAR);
    	final int MONTH = NOW.get(Calendar.MONTH) + Utilities.ONE;
    	final int DAY = NOW.get(Calendar.DAY_OF_MONTH);
    	final int HOUR = NOW.get(Calendar.HOUR_OF_DAY);
    	final int MINUTE = NOW.get(Calendar.MINUTE);
    	final int SECOND = NOW.get(Calendar.SECOND);
    	
    	// String together in the form YYYYMMDDHHMMSS padded with leading zeros as necessary
    	return Integer.toString(YEAR)
    			+ (MONTH < Utilities.TEN ? Integer.toString(Utilities.ZERO) : "") + Integer.toString(MONTH)
    			+ (DAY < Utilities.TEN ? Integer.toString(Utilities.ZERO) : "") + Integer.toString(DAY)
    			+ (HOUR < Utilities.TEN ? Integer.toString(Utilities.ZERO) : "") + Integer.toString(HOUR) 
    			+ (MINUTE < Utilities.TEN ? Integer.toString(Utilities.ZERO) : "") + Integer.toString(MINUTE)
    			+ (SECOND < Utilities.TEN ? Integer.toString(Utilities.ZERO) : "") + Integer.toString(SECOND);
    }
    
    /**/
    /*
    NAME
        private final void HighlightLegalMoves();
    
    SYNOPSIS
        private final void HighlightLegalMoves();
    
        No parameters.
    
    DESCRIPTION
        This method turns the color of every tile a selected piece
        can move to, as well as its current spot, into green.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void HighlightLegalMoves(){
        if(this.m_candidate != null){
        	if(m_humanPlayer.IsWhite()){
        		this.m_board.GetTile(this.m_candidate.GetCurrentRow() + Utilities.ONE, this.m_candidate.GetCurrentColumn() + Utilities.ONE).LightUp();
        	}else{
        		this.m_board.GetTile(Utilities.SEVEN - (this.m_candidate.GetCurrentRow()), Utilities.SEVEN - (this.m_candidate.GetCurrentColumn())).LightUp();
        	}
            for(final Move MOVE : this.m_candidate.GetCurrentLegalMoves()){
            	if(m_humanPlayer.IsWhite()){
            		this.m_board.GetTile(MOVE.GetNewRow() + Utilities.ONE, MOVE.GetNewColumn() + Utilities.ONE).LightUp();
            	}else{
            		this.m_board.GetTile(Utilities.SEVEN - (MOVE.GetNewRow()), Utilities.SEVEN - (MOVE.GetNewColumn())).LightUp();
            	}
            }
        }
    }
    
    /**/
    /*
    NAME
        private final void UndoHighlighting();
    
    SYNOPSIS
        private final void UndoHighlighting();
    
        No parameters.
    
    DESCRIPTION
        This method turns the color of every tile a previously selected piece
        could move to, as well as its current spot, back to their original colors.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public final void UndoHighlighting(){
        if(this.m_candidate != null){
        	if(m_humanPlayer.IsWhite()){
        		this.m_board.GetTile(this.m_candidate.GetCurrentRow() + Utilities.ONE, this.m_candidate.GetCurrentColumn() + Utilities.ONE).Revert();
        	}else{
        		this.m_board.GetTile(Utilities.SEVEN - (this.m_candidate.GetCurrentRow()), Utilities.SEVEN - (this.m_candidate.GetCurrentColumn())).Revert();
        	}
            for(final Move MOVE : this.m_candidate.GetCurrentLegalMoves()){
            	if(m_humanPlayer.IsWhite()){
            		this.m_board.GetTile(MOVE.GetNewRow() + Utilities.ONE, MOVE.GetNewColumn() + Utilities.ONE).Revert();
            	}else{
            		this.m_board.GetTile(Utilities.SEVEN - (MOVE.GetNewRow()), Utilities.SEVEN - (MOVE.GetNewColumn())).Revert();
            	}
            }
        }
    }
    
    /**/
    /*
    NAME
        private final void AddComponentsToPane(final Container a_pane);
    
    SYNOPSIS
        private final void AddComponentsToPane(final Container a_pane);
    
        Container a_pane ----> The pane where all the components go.
    
    DESCRIPTION
        This method adds all of the Java Swing components to the
        Dark Blue content pane.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void AddComponentsToPane(final Container a_pane){
        // Add the menu bar
    	m_top.add(m_menuBar);
    	
    	// Put the menu bar on top
    	a_pane.add(m_top, BorderLayout.PAGE_START);
        
    	// Set layouts for all JPanels
        m_left.setLayout(new BoxLayout(m_left, BoxLayout.LINE_AXIS));
        m_right.setLayout(new BoxLayout(m_right, BoxLayout.LINE_AXIS));
        
        m_whiteInner.setLayout(new BoxLayout(m_whiteInner, BoxLayout.PAGE_AXIS));
        m_blackInner.setLayout(new BoxLayout(m_blackInner, BoxLayout.PAGE_AXIS));
        
        // Set the alignments for both header labels
        m_whiteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        m_whiteLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        m_blackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        m_blackLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        // Assemble the white move history panel
        m_whiteInner.add(m_whiteLabel);
        m_whiteInner.add(m_whiteScroll);
        
        // Place the move history and the captured pieces side by side
        m_left.add(m_blackPieces);
        m_left.add(m_whiteInner);
        
        // Assemble the black move history panel
        m_blackInner.add(m_blackLabel);
        m_blackInner.add(m_blackScroll);
        
        // Place the move history and the captured pieces side by side
        m_right.add(m_blackInner);
        m_right.add(m_whitePieces);

        a_pane.add(m_left, BorderLayout.LINE_START);
        
        a_pane.add(m_right, BorderLayout.LINE_END);
        
        a_pane.add(m_board, BorderLayout.CENTER);
    }
    
    
    
    
    /**/
    /*
    NAME
        private final void CreateAndShowGUI();
    
    SYNOPSIS
        private final void CreateAndShowGUI();
    
        No parameters.
    
    DESCRIPTION
        This method creates and displays the 
        graphical user interface in the Dark Blue window.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void CreateAndShowGUI(){
        this.setSize(new Dimension(4120, 4120));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.AddComponentsToPane(this.getContentPane());
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
    }
    
    /**/
    /*
    NAME
        public static final ChessColor GetHumanColor();
    
    SYNOPSIS
        public static final ChessColor GetHumanColor();
    
        No parameters.
    
    DESCRIPTION
        This method returns the color chosen
        by the human player at the start of a game.
    
    RETURNS
        ChessColor m_humanColor: The human player's color.
    
    AUTHOR
        Ryan King
    */
    public static final ChessColor GetHumanColor(){
        return m_humanColor;
    }
    
    /**/
    /*
    NAME
        public static final ChessColor GetComputerColor();
    
    SYNOPSIS
        public static final ChessColor GetComputerColor();
    
        No parameters.
    
    DESCRIPTION
        This method returns the color that wasn't chosen
        by the human player at the start of a game.
    
    RETURNS
        ChessColor m_computerColor: The color not chosen by the human player.
    
    AUTHOR
        Ryan King
    */
    public static final ChessColor GetComputerColor(){
        return m_computerColor;
    }
    
    /**/
    /*
    NAME
        public static final String GetEnPassantTile();
    
    SYNOPSIS
        public static final String GetEnPassantTile();
    
        No parameters.
    
    DESCRIPTION
        This method returns the en passant tile determined
        the previous turn, if any; otherwise null.
    
    RETURNS
        String m_enPassantTile: The en passant tile determined by a two-tile
        pawn move, or null otherwise.
    
    AUTHOR
        Ryan King
    */
    public static final String GetEnPassantTile(){
    	return m_enPassantTile;
    }
    
    /**/
    /*
    NAME
        private final void CheckForPromotions(final Player a_player);
    
    SYNOPSIS
        private final void CheckForPromotions(final Player a_player);
    
        Player a_player ------> The player whose turn it is.
    
    DESCRIPTION
        This method checks the player's final rank to determine if 
        any of his/her pawns can get promoted. If a pawn is found,
        the method determines if it is on the last possible rank.
        If so, this pawn is promoted to a knight, bishop, rook, or queen,
        which is solely up to the discretion of the player, though 90% of
        promotions typically end with a queen. A new board gets generated 
        with the newly promoted piece in place of the pawn.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void CheckForPromotions(final Player a_player){
    	// This will hold a copy of all the player's pieces
    	ArrayList<Piece> activePiecesCopy = new ArrayList<>();
    	
    	// Make deep copies of the player's pieces
    	for(int index = Utilities.ZERO; index < a_player.GetActivePieces().size(); index++){
            
    		Piece piece = a_player.GetActivePieces().get(index);
            
    		piece = Factory.PieceFactory(piece);
            
    		activePiecesCopy.add(piece);
    	}
        
    	final int index = GetPromotedPawnIndex(a_player, activePiecesCopy);
    	
    	if(index != Utilities.NEGATIVE_ONE){
    		final Pawn pawn = (Pawn) activePiecesCopy.get(index);
    		final int row = pawn.GetCurrentRow();
    		final int column = pawn.GetCurrentColumn();

    		// Return a new Board object with the new powerful piece replacing the pawn
    		m_board.SetBoard(pawn.Promote(m_board.GetBoard(), a_player.IsHuman()));
    				
    		m_promotionString = "=" + Character.toUpperCase(m_board.GetBoard().GetTile(row, column).GetPiece().GetIcon());

    		m_board.DrawBoard();
    	}
    }
    
    /**/
    /*
    NAME
        private final int GetPromotedPawnIndex(final Player a_player, final ArrayList<Piece> a_activePiecesCopy);
    
    SYNOPSIS
        private final int GetPromotedPawnIndex(final Player a_player, final ArrayList<Piece> a_activePiecesCopy);
    
        Player a_player -------------------------> The player to check.
        
        ArrayList<Piece> a_activePiecesCopy -----> The copy of the player's active piece roster.
    
    DESCRIPTION
        This method checks the player's final rank to determine if 
        any of his/her pawns can get promoted. If a pawn is found,
        the method returns its index in the active piece roster.
        If none is found, the method returns -1.
    
    RETURNS
        int: An index value in the range [0, a_activePiecesCopy.size() - 1] if a
        pawn to get promoted is found, or -1 if none is found.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final int GetPromotedPawnIndex(final Player a_player, final ArrayList<Piece> a_activePiecesCopy){
    	// Check through all the player's pieces
    	for(int index = Utilities.ZERO; index < a_activePiecesCopy.size(); index++){
    		// Look for a pawn that's on its last rank
    		if(a_activePiecesCopy.get(index).IsPawn()){
    			if((a_player.IsWhite() && a_activePiecesCopy.get(index).GetCurrentRow() == Utilities.ZERO) || (a_player.IsBlack() && a_activePiecesCopy.get(index).GetCurrentRow() == Utilities.SEVEN)){
    				return index;
    			}
    		}else{
    			continue;
   			}
    	}
    	
    	return Utilities.NEGATIVE_ONE;
    }
    
    /**/
    /*
    NAME
        private final GameState EvaluateGameState();
    
    SYNOPSIS
        private final GameState EvaluateGameState();
        
        No parameters.
    
    DESCRIPTION
        This method determines the state of the game.
        It assigns the m_gameState variable to one of the following:
        
        GameState.CHECK: One player's king is in check. He must either move out of it,
        capture the threatening piece himself, use a friendly piece to 
        capture the threatening piece, or use a friendly piece to 
        block the check, assuming the threatening piece moves linearly.
        The game resumes as normal after the player removes the threat.
        
        GameState.STALEMATE: One player's king is not in check but has no legal moves.
        None of the player's other pieces have any legal moves either. 
        The game ends in a draw.
        
        GameState.CHECKMATE: One player's king is in check 
        in his current position and wherever he moves.
        He cannot escape, he cannot capture the piece(s) putting him into check,
        no other friendly piece can capture the piece(s) putting him into check,
        and no friendly piece can block the check if the threatening piece is not a knight or pawn. 
        Any other friendly pieces with moves that would not help the king are not allowed to move.
        The other player wins and the game is over.
        
        GameState.INSUFFICIENT_MATERIAL: The game can end this way due to a number of conditions:
        
            1. White and black have only bare kings left on the board.
        
            2. One player has a king and the other has a king and a bishop.
        
            3. One player has a king and the other has a king and a knight.
        
            4. Both players have a king and a bishop and both bishops 
               move on the same tile color.
                
        GameState.FIFTY_MOVE_RULE: Fifty halfmoves have been made with no capture or pawn movement.
        The game ends in a draw.
        
        GameState.THREEFOLD_REPETITION: The same configuration of the board has been repeated three times.
        However many times this happens need not be consecutive. The game ends in a draw.

        GameState.NORMAL: The game proceeds as normal.
        
        The special GameState.EMPTY status is not assigned here, as it is only used when the 
        engine starts up initially or if the game is stopped when saved.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final GameState EvaluateGameState(final Player a_player){
    	if(a_player.IsInCheckmate(m_board.GetBoard())){
            return GameState.CHECKMATE;
        }else if(a_player.IsInCheck(m_board.GetBoard())){
            return GameState.CHECK;
        }else if(a_player.IsInStalemate(m_board.GetBoard())){
            return GameState.STALEMATE;
        }else if(IsDrawByInsufficientMaterial()){
            return GameState.INSUFFICIENT_MATERIAL;
        }else if(IsDrawByFiftyMoveRule()){
            return GameState.FIFTY_MOVE_RULE;
        }else if(IsDrawByThreefoldRepetition()){
            return GameState.THREEFOLD_REPETITION;
        }else{
            return GameState.NORMAL;
        }
    }
    
    /**/
    /*
    NAME
        private final boolean IsDrawByInsufficientMaterial();
    
    SYNOPSIS
        private final boolean IsDrawByInsufficientMaterial();
    
        No parameters.
    
    DESCRIPTION
        This method checks to see if any draw condition by
        insufficient material exists. 
        This can occur in one of several ways:
        
        1. Both sides have bare kings. Since kings cannot be captured
        nor give check, the game ends in a draw.
        
        2. One side has a bare king and the other has a king and a knight.
        
        3. One side has a bare king and the other has a king and a bishop.
        
        4. Both sides have a king and a bishop and the bishops are on tiles
        of the same color.
        
        If any of these conditions are met, the method returns true.
        If not, it returns false.
    
    RETURNS
        True if the players have insufficient material and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final boolean IsDrawByInsufficientMaterial(){
        if(m_white.HasBareKing() && m_black.HasBareKing()){
            return true;
        }else if(m_white.HasBareKing() && m_black.HasKingAndBishop()){
            return true;
        }else if(m_white.HasBareKing() && m_black.HasKingAndKnight()){
            return true;
        }else if(m_black.HasBareKing() && m_white.HasKingAndBishop()){
            return true;
        }else if(m_black.HasBareKing() && m_white.HasKingAndKnight()){
            return true;
        }else if(m_white.HasKingAndBishop() && m_black.HasKingAndBishop()){
            int whiteBishopRow = Utilities.NEGATIVE_ONE, whiteBishopColumn = Utilities.NEGATIVE_ONE, 
                    blackBishopRow = Utilities.NEGATIVE_ONE, blackBishopColumn = Utilities.NEGATIVE_ONE;
            
            // Find both bishops
            for(int index = Utilities.ZERO; index < Utilities.TWO; index++){
                if(m_white.GetActivePieces().get(index).IsBishop()){
                    whiteBishopRow = m_white.GetActivePieces().get(index).GetCurrentRow();
                    whiteBishopColumn = m_white.GetActivePieces().get(index).GetCurrentColumn();
                }
                if(m_black.GetActivePieces().get(index).IsBishop()){
                    blackBishopRow = m_black.GetActivePieces().get(index).GetCurrentRow();
                    blackBishopColumn = m_black.GetActivePieces().get(index).GetCurrentColumn();
                }
            }
            
            // End the game if the bishops are on the same tile colors
            if(m_board.GetBoard().GetTile(whiteBishopRow, whiteBishopColumn).GetColor() == m_board.GetBoard().GetTile(blackBishopRow, blackBishopColumn).GetColor()){
                return true;
            }
        }
        return false;
    }
    
    /**/
    /*
    NAME
        private final boolean IsDrawByFiftyMoveRule();
    
    SYNOPSIS
        private final boolean IsDrawByFiftyMoveRule();
    
        No parameters.
    
    DESCRIPTION
        This method checks to see if any draw condition by
        the fifty-move rule exists.
        
        This occurs when fifty moves have been made without a 
        single pawn movement or capture of any kind.
        
        If this condition is met, the method returns true.
        If not, it returns false.
    
    RETURNS
        True if the halfmove clock is at least fifty and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final boolean IsDrawByFiftyMoveRule(){
        return m_currentHalfmoves >= Utilities.FIFTY;
    }
    
    /**/
    /*
    NAME
        private final boolean IsDrawByThreefoldRepetition();
    
    SYNOPSIS
        private final boolean IsDrawByThreefoldRepetition();
    
        No parameters.
    
    DESCRIPTION
        This method checks to see if any draw condition by
        threefold repetition exists.
        
        This occurs when the same three moves have occurred
        consecutively; typically this happens when one player's
        king is put into check many times.
        
        If this condition is met, the method returns true.
        If not, it returns false.
    
    RETURNS
        True if the same board configuration has occurred three times
        and false otherwise. One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final boolean IsDrawByThreefoldRepetition(){
        // Make the hashmap iterable
        final Iterator<String> POSITION_ITERATION = m_positions.keySet().iterator();
        
        // Look through every position in the hash
        while(POSITION_ITERATION.hasNext()){
        	final String CURRENT = POSITION_ITERATION.next();
        	if(m_positions.get(CURRENT) == Utilities.THREE){
        		return true;
        	}
        }
        
        return false;
    }
    
    /**/
    /*
    NAME
        public static final Piece GetPreviouslyMoved();
    
    SYNOPSIS
        public static final Piece GetPreviouslyMoved();
    
        No parameters.
    
    DESCRIPTION
        This method returns the piece that was
        previously moved. This is more useful
        outside of this class, especially for the
        evaluation of en passant moves.
    
    RETURNS
        Piece m_previouslyMoved: The previously moved piece.
    
    AUTHOR
        Ryan King
    */
    public static final Piece GetPreviouslyMoved(){
        return m_previouslyMoved;
    }
    
    /**/
    /*
    NAME
        public static final int GetMaxSearchDepth();
    
    SYNOPSIS
        public static final int GetMaxSearchDepth();
    
        No parameters.
    
    DESCRIPTION
        This method returns the search depth of the AI.
    
    RETURNS
        int m_searchDepth: The AI search depth.
    
    AUTHOR
        Ryan King
    */
    public static final int GetMaxSearchDepth(){
        return m_depth;
    }
    
    /**/
    /*
    NAME
        public final String Serialize();
    
    SYNOPSIS
        public final String Serialize();
    
        No parameters.
    
    DESCRIPTION
        This method returns a String representation of the game in Forsyth-Edwards notation (FEN). 
        This notation allows any game to be resumed immediately with all the required information.
        It includes the following, all delimited by one space:
        
        1. The board configuration, expressed by capital letters corresponding to white pieces,
        lowercase letters corresponding to black pieces, and numbers corresponding to consecutive
        empty tiles in a row. All rows are delimited by forward slashes ("/").
        Parsing starts from tile a8-h8, then a7-h7, a6-h6, ... , all the way down to a1-h1.
        This corresponds to row 0, 1, 2, ... , 7 on my internal board.
        
        For example, the board configuration for the starting postition is "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR".
        
        2. A lowercase letter representing the side who will be playing once the game is deserialized and resumed.
        
        For example, the starting position dictates that white should move first, so this will give "w".
        
        3. Capital and/or lowercase letters indicating which sides either king can castle on, if any. 
        
        For example, in the starting postition, both white and black can theoretically castle, even though it is not legal for them to do so yet.
        This case would necessitate placing KQkq, which indicates both players can castle on either side.
        If both forms of castling are illegal for both players, a hyphen-minus ("-") is placed instead.
        This legality is determined by seeing if the king and both rooks are in their original positions, disregarding any potential disruptions
        in the form of threats or pieces sitting between the king and the rook. This is evaulated at the beginning of every turn.
        
        4. The space on which an en passant move can be performed if the piece that was most recently moved is a pawn.
        
        For example, after the move 1. e4, which moves the white king's pawn from e2 to e4, this pawn could theoretically be captured en passant
        the next turn because it moved 2 spaces on its first move. Otherwise, a hyphen-minus is placed here instead.
        This would generate "e4". The starting position, on the other hand, will generate "-".
        
        5. The number of halfmoves (either white or black moving individually, as opposed to an entire turn) since a pawn movement or capture. This starts at 0.
        
        6. The number of turns taken by both players (once both black and white have finished their moves). This starts at 1.
        
        Following these rules, the string returned by the starting position is as follows:
        
        rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
        
    RETURNS
        String serial: The serialization string.
    
    AUTHOR
        Ryan King
    */
    public final String Serialize(){
    	// Initialize the serialization string
    	String serial = m_board.GetBoard().toString();	
    	    	
    	// Add a space as a delimiter
    	serial += " ";
    	
    	// Add the current number of halfmoves (times white/black moved individually)
    	serial += Integer.toString(m_currentHalfmoves);
    	
    	// Add a space as a delimiter
    	serial += " ";
    	
    	// Add the current number of fullmoves (times when black finished moving)
    	serial += Integer.toString(m_fullmoves);
    	
    	return serial;
    }
    
    /**/
    /*
    NAME
        public final boolean Deserialize();
    
    SYNOPSIS
        public final boolean Deserialize();
    
        No parameters.
    
    DESCRIPTION
        This method takes in a serialization string from
        a text file and parses it into a valid chessboard.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, with help about the FileNameExtensionFilter taken from:
        https://stackoverflow.com/questions/15771949/how-do-i-make-jfilechooser-only-accept-txt
        
    */
    public final boolean Deserialize(){
    	try{  			
    		final JFileChooser CHOOSER = new JFileChooser();

    		CHOOSER.setFileFilter(new FileNameExtensionFilter("FEN files", "fen"));
    	
    		final int CHOICE = CHOOSER.showOpenDialog(null);
    		
    		if(CHOICE == JFileChooser.APPROVE_OPTION){
    		    // A file was chosen.
    		    // Set the flag so that an error message will not show
                m_filename = CHOOSER.getSelectedFile().getName();

                // Get the file's extension
                final String EXTENSION = m_filename.substring(m_filename.lastIndexOf('.') + Utilities.ONE);
                
                // Check to see if the user tried to sneak a non-FEN file through the chooser
                if(!EXTENSION.equalsIgnoreCase("fen")){
                    return false;
                }
    		    
    		    // Open and read the file
    			final InputStreamReader IN = new InputStreamReader(new FileInputStream(CHOOSER.getSelectedFile()), "UTF-8");  			
    			    	    		
				final Scanner SCANNER = new Scanner(IN);
    	    		
    			final String line = SCANNER.nextLine();
    	    			
    			IN.close();
    					
    			SCANNER.close();
    	    	
    			// Determine if the file is valid before parsing it
    			if(IsValidFEN(line)){
    			    ParseFEN(line, true);
    			}else{
    			    return false;
    			}

    			return true;
    		}else{
    		    // The chooser was closed and no file was selected, 
    		    // so don't display an error message
    		    m_filename = null;
    		    return false;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
        return false;
    }
    
    /**/
    /*
    NAME
        private final void ParseFEN(final String a_FENString, final boolean a_isSerializedGame);
    
    SYNOPSIS
        private final void ParseFEN(final String a_FENString, final boolean a_isSerializedGame);
    
        String a_FENString -----------> The game to read in Forsyth-Edwards Notation.
        
        boolean a_isSerializedGame ---> If the game is being resumed from a serialization string, as opposed to being the result of an undone move.
    
    DESCRIPTION
        This method parses a string in FEN format.
        It throws an exception if it encounters a problem.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, with help taken from:
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/pgn/FenUtilities.java
    */
    private final void ParseFEN(final String a_FENString, final boolean a_isSerializedGame) throws Exception{
        // PARTS will hold the six parts of the FEN string
    	final String[] PARTS = a_FENString.split(" ");
		
    	// BUILDER will be a template for building the new board
		final BoardBuilder BUILDER = new BoardBuilder();
		
		// PARTS[0] represents the configuration of the board
		final String[] BOARD = PARTS[Utilities.ZERO].split("/");	
		
		// Determine if both sides have one and only one king
		boolean whiteKingFound = false, blackKingFound = false;
		for(final String RANK : BOARD){
			if(RANK.contains("K") && !whiteKingFound){
				whiteKingFound = true;
			}else if(whiteKingFound && RANK.contains("K")){
				m_menuBar.DisableSave();
				m_menuBar.DisableUndo();
				throw new Exception("Duplicate white king found.");
			}
			if(RANK.contains("k") && !blackKingFound){
				blackKingFound = true;
			}else if(blackKingFound && RANK.contains("k")){
				m_menuBar.DisableSave();
				m_menuBar.DisableUndo();
				throw new Exception("Duplicate black king found.");
			}
		}
		
		// Do not allow the game to progress if one side does not have a king
		if(!whiteKingFound || !blackKingFound){
			m_board.SetBoard(Board.GetEmptyBoard());
			m_board.DrawBoard();
			m_menuBar.DisableLiveGameButtons();
			throw new Exception("Unplayable game: One or more king(s) not found.");
		}
		
		// PARTS[1] determines whose turn it is
		if(PARTS[Utilities.ONE].equalsIgnoreCase("w")){
			BUILDER.SetWhoseTurn(ChessColor.WHITE);
		}else if(PARTS[Utilities.ONE].equalsIgnoreCase("b")){
			BUILDER.SetWhoseTurn(ChessColor.BLACK);
		}else{
			throw new Exception("Improper file format.");
		}
		
		// PARTS[2] determines which sides can castle and where,
		// which need not be feasible on the current turn
		if(PARTS[Utilities.TWO].equals("-")){
			m_canWhiteKingsideCastle = false;
			m_canWhiteQueensideCastle = false;
			m_canBlackKingsideCastle = false;
			m_canBlackQueensideCastle = false;
		}else{
			
			if(PARTS[Utilities.TWO].contains("K")){
				m_canWhiteKingsideCastle = true;
			}else{
				m_canWhiteKingsideCastle = false;
			}
		
			if(PARTS[Utilities.TWO].contains("Q")){
				m_canWhiteQueensideCastle = true;
			}else{
				m_canWhiteQueensideCastle = false;
			}
		
			if(PARTS[Utilities.TWO].contains("k")){
				m_canBlackKingsideCastle = true;
			}else{
				m_canBlackKingsideCastle = false;
			}
		
			if(PARTS[Utilities.TWO].contains("q")){
				m_canBlackQueensideCastle = true;
			}else{
				m_canBlackQueensideCastle = false;
			}
		}
		
		// Set up the board
		for(int i = Utilities.ZERO; i < BOARD.length; i++){
			ParseRank(i, BOARD[i], BUILDER);
		}
		
		// PARTS[3] contains the destination tile of an en passant capture
		if(!PARTS[Utilities.THREE].equals("-")){
		    // There is a valid en passant tile
			m_enPassantTile = PARTS[Utilities.THREE];
			
			// Determine the color of the pawn to find out where the pawn moved
			final ChessColor pawnColor = (BUILDER.WhoseTurnIsIt().IsBlack() ? ChessColor.WHITE : ChessColor.BLACK);
			m_originalRow = (pawnColor.IsWhite() ? BoardUtilities.ToBoardRow(m_enPassantTile) + Utilities.ONE : BoardUtilities.ToBoardRow(m_enPassantTile) - Utilities.ONE);
			m_originalColumn = BoardUtilities.ToBoardColumn(m_enPassantTile);
			
			// Instantiate the pawn
			m_previouslyMoved = new Pawn(pawnColor, m_originalRow, m_originalColumn);
		}else{
		    // There is no valid en passant tile
			m_enPassantTile = null;
		}
		
		// PARTS[4] contains the number of halfmoves that have occurred since the last capture or pawn movement
		m_currentHalfmoves = Integer.parseInt(PARTS[Utilities.FOUR]);
		
		// PARTS[5] contains the number of fullmoves (a set of two moves made by both white and black) made during the entire game
		m_fullmoves = Integer.parseInt(PARTS[Utilities.FIVE]);
		
		// Set the board configuration
		m_board.SetBoard(BUILDER.Build());
		
		// Initialize both players and their legal moves
		InitializePlayers(m_board.GetBoard());
		
		// Set the current player
		m_currentPlayer = (m_board.WhoseTurnIsIt().IsWhite() ? m_white : m_black);
		
		// Add ellipses if this is being resumed
		if(a_isSerializedGame){
			m_whiteMoves.append("...\n");
		
			if(m_board.GetBoard().WhoseTurnIsIt().IsWhite()){
				m_blackMoves.append("...\n");
			}
		}	
    }
    
    /**/
    /*
    NAME
        private final boolean IsValidFEN(final String a_string);
    
    SYNOPSIS
        private final boolean IsValidFEN(final String a_string);
    
        String a_string -----------> The potential FEN string for the engine to read.

    DESCRIPTION
        This method attempts to parse a string that
        could be an FEN string, but it checks for potential
        problems. Any part of the string that is not considered
        a valid part of a FEN string will be found and the method
        will return false. If no problems were found, the method
        will return true.
    
    RETURNS
        boolean: True if this string is a valid FEN string and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King, with help taken from:
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/pgn/FenUtilities.java
    */
    private final boolean IsValidFEN(final String a_string){
        // Idiot proofing for null or empty arguments
        if(a_string == null || a_string.isBlank()){
            return false;
        }
        
        // Separate all parts
        String[] PARTS = a_string.split(" ");
        
        // All FEN strings must have six distinct parts
        if(PARTS.length != Utilities.SIX){
            return false;
        }
        
        // The first part contains the board configuration delimited by forward slashes
        final String[] RANKS = PARTS[Utilities.ZERO].split("/");
        
        // All FEN boards must have exactly eight ranks
        if(RANKS == null || RANKS.length != Utilities.EIGHT){
            return false;
        }
        
        // board will hold the board configuration as a single string with no delimiters
        String board = "";

        // Check if each rank is valid
        for(final String RANK : RANKS){
            if(!IsValidRank(RANK)){
                return false;
            }
            board += RANK;
        }

        // Make sure both sides have the correct number of pieces
        if(!HasValidPieces(board)){
            return false;
        }
        
        // The second part contains the side to move next
        // Check if the side to move next is either a lowercase "w" or "b"
        if(!(PARTS[Utilities.ONE].equals("w") || PARTS[Utilities.ONE].equals("b"))){
            return false;
        }
        
        // The third part contains the castling rights, if any
        // Check if castling privileges have any of "KQkq" or is only "-"
        if(!((PARTS[Utilities.TWO].contains("K") || PARTS[Utilities.TWO].contains("Q") || PARTS[Utilities.TWO].contains("k") || PARTS[Utilities.TWO].contains("q")) || PARTS[Utilities.TWO].equals("-"))){
            return false;
        }
        
        // The fourth part contains the en passant tile, if any
        // Check if the en passant tile has a lowercase letter from a-h followed by 2 or 7, or is a "-"
        if(!((BoardUtilities.IsValidTile(PARTS[Utilities.THREE]) && (Integer.parseInt(Character.toString(PARTS[Utilities.THREE].charAt(Utilities.ONE))) == Utilities.THREE || Integer.parseInt(Character.toString(PARTS[Utilities.THREE].charAt(Utilities.ONE))) == Utilities.SIX)) || PARTS[Utilities.THREE].equals("-"))){
            return false;
        }
        
        // The fifth part contains the number of halfmoves made since the last capture or pawn movement
        // Check if the halfmove clock is a nonnegative integer (the value must be 0 or above)
        if(!HasValidClock(PARTS[Utilities.FOUR], false)){
            return false;
        }
        
        // The sixth part contains the number of fullmoves made during the entire game, which starts at 1 before the first move is made
        // Check if the fullmove clock is a positive integer (the value must be 1 or above)
        if(!HasValidClock(PARTS[Utilities.FIVE], true)){
            return false;
        }
        
        // This is a valid FEN string
        return true;
    }
    
    /**/
    /*
    NAME
        private final boolean HasValidPieces(final String a_board);
    
    SYNOPSIS
        private final boolean HasValidPieces(final String a_board);
    
        String a_board -----------> The string representing the board configuration.

    DESCRIPTION
        This method parses the FEN board string to determine if
        both sides have the correct number of each piece.
        Though the numbers placed on the rooks, bishops, knights, and
        queens may seem unnaturally high, they are there to determine 
        if any promotions were made. A player can have as many of these
        pieces as possible, which means 9 queens or 10 knights, rooks, and
        bishops if s/he promotes all of his/her pawns.
    
    RETURNS
        boolean: True if this board has the correct amount of each piece for both sides and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final boolean HasValidPieces(final String a_board){
        if(a_board == null || a_board.isBlank()){
            return false;
        }
        
        // Both players must have between 1 to 16 pieces of their color       
        final int WHITE_PIECES = Pieces(a_board, ChessColor.WHITE);
        final int BLACK_PIECES = Pieces(a_board, ChessColor.BLACK);
        
        if(!((WHITE_PIECES >= Utilities.ONE && WHITE_PIECES <= Utilities.SIXTEEN) && (BLACK_PIECES >= Utilities.ONE && BLACK_PIECES <= Utilities.SIXTEEN))){
            return false;
        }
        
        // Both players must have 0 to 8 pawns
        if(!(HasCorrectNumberOfPieces(a_board, PieceType.PAWN, ChessColor.WHITE) && HasCorrectNumberOfPieces(a_board, PieceType.PAWN, ChessColor.BLACK))){
            return false;
        }
        
        // Both players must have 0 to 10 rooks
        if(!(HasCorrectNumberOfPieces(a_board, PieceType.ROOK, ChessColor.WHITE) && HasCorrectNumberOfPieces(a_board, PieceType.ROOK, ChessColor.BLACK))){
            return false;
        }
        
        // Both players must have 0 to 10 knights
        if(!(HasCorrectNumberOfPieces(a_board, PieceType.KNIGHT, ChessColor.WHITE) && HasCorrectNumberOfPieces(a_board, PieceType.KNIGHT, ChessColor.BLACK))){
            return false;
        }
        
        // Both players must have 0 to 10 bishops
        if(!(HasCorrectNumberOfPieces(a_board, PieceType.BISHOP, ChessColor.WHITE) && HasCorrectNumberOfPieces(a_board, PieceType.BISHOP, ChessColor.BLACK))){
            return false;
        }
        
        // Both players must have 0 to 9 queens
        if(!(HasCorrectNumberOfPieces(a_board, PieceType.QUEEN, ChessColor.WHITE) && HasCorrectNumberOfPieces(a_board, PieceType.QUEEN, ChessColor.BLACK))){
            return false;
        }
        
        // Both players must have exactly one king
        if(!(HasCorrectNumberOfPieces(a_board, PieceType.KING, ChessColor.WHITE) && HasCorrectNumberOfPieces(a_board, PieceType.KING, ChessColor.BLACK))){
            return false;
        }
        
        return true;
    }
    
    /**/
    /*
    NAME
        private final boolean HasValidClock(final String a_clock, final boolean a_isFullmoveClock);
    
    SYNOPSIS
        private final boolean HasValidClock(final String a_clock, final boolean a_isFullmoveClock);
    
        String a_clock -----------> The string representing the fullmove or halfmove clock.

    DESCRIPTION
        This method parses the clock string to see if it's either a nonnegative integer (halfmove),
        or a positive integer (fullmove). A Boolean flag is passed in to determine which type of move clock
        is being analyzed. 
        
        Passing true as the flag means the clock is the fullmove clock and the number must be positive.
        Passing false as the flag means the clock is the halfmove clock and the number must be positive or zero.
    
    RETURNS
        boolean: True if this clock is a valid integer that is either 0 or positive for a halfmove clock or positive for a fullmove clock and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final boolean HasValidClock(final String a_clock, final boolean a_isFullmoveClock){
        try{
            // Attempt to parse the clock into an integer
            final int CLOCK = Integer.parseInt(a_clock);
            
            // The halfmove clock must be zero or positive
            // The fullmove clock must only be positive
            final int LIMIT = (a_isFullmoveClock ? Utilities.ONE : Utilities.ZERO);
            
            // Any clock value that lies below the limit is invalid
            if(CLOCK < LIMIT){
                return false;
            }
            
            return true;
        }catch(NumberFormatException e){
            // Non-numeric clocks are invalid
            return false;
        }
    }
    
    /**/
    /*
    NAME
        private final boolean IsValidRank(final String a_rank);
    
    SYNOPSIS
        private final boolean IsValidRank(final String a_rank);
    
        String a_rank -----------> The FEN rank for the parser to read.

    DESCRIPTION
        This method attempts to parse a string that
        could be an FEN rank, but it checks for potential
        problems. These problems include invalid letters or
        the number of pieces plus empty tiles not adding to
        exactly 8, a null or empty string, or a string that
        has a length higher than 8.
    
    RETURNS
        boolean: True if this string is a valid FEN rank and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final boolean IsValidRank(final String a_rank){
        if(a_rank == null || a_rank.isBlank() || a_rank.length() > Utilities.EIGHT){
            return false;
        }
        
        // The only letters this string can contain are the following
        final String PIECES = "PpRrNnBbQqKk";
        
        // sum will hold the sum of the number of empty tiles found, which when added to numPieces should not exceed 8
        int sum = Utilities.ZERO;
        
        // numPieces will hold the number of valid pieces found, which when added to sum should not exceed 8
        int numPieces = Utilities.ZERO;

        // Iterate through every character of the string
        for(int i = Utilities.ZERO; i < a_rank.length(); i++){
            
            // This is a block of empty tiles
            if(Character.isDigit(a_rank.charAt(i))){
                final int DIGIT = Integer.parseInt(Character.toString(a_rank.charAt(i)));
                
                if(DIGIT >= Utilities.NINE || DIGIT <= Utilities.ZERO){
                    return false;
                }
                
                sum += DIGIT;
            }else{
                // This is a piece
                if(!PIECES.contains(Character.toString(a_rank.charAt(i)))){
                    return false;
                }else{
                    numPieces++;
                }
            }
        }       
        
        // Make sure there are eight and only eight pieces plus empty tiles
        return ((sum + numPieces) == Utilities.EIGHT);
    }
    
    /**/
    /*
    NAME
        private final boolean HasCorrectNumberOfPieces(final String a_board, final PieceType a_type, final ChessColor a_color);
    
    SYNOPSIS
        private final boolean HasCorrectNumberOfPieces(final String a_board, final PieceType a_type, final ChessColor a_color);
    
        String a_board -----------> The FEN board for the parser to read.
        
        PieceType a_type ---------> The type of piece to check.
        
        ChessColor a_color -------> The color to check.

    DESCRIPTION
        This method attempts to find if there is the correct number of
        occurrences of a piece letter (capital if a_color is white or lowercase
        if a_color is black). If there end up being the wrong amount of occurrences,
        this method will fail and return false.
    
    RETURNS
        boolean: True if there is the correct amount of pieces and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final boolean HasCorrectNumberOfPieces(final String a_board, final PieceType a_type, final ChessColor a_color){
        // letters will hold the number of pieces found
        int letters = Utilities.ZERO;

        // LIMIT will hold the maximum number of occurrences the letter should have in a valid FEN file
        // Both cases for capital (white) and lowercase (black) letters are evaluated separately
        final int LIMIT = a_type.GetLimit();
        
        // letter will hold the actual letter of the piece depending on the color
        char letter = a_type.ToCharacter();
        
        // ACTUAL will hold the piece character in the correct case
        final char ACTUAL = (a_color.IsWhite() ? Character.toUpperCase(letter) : Character.toLowerCase(letter));
        
        // Find the number of occurrences of this character on the board string
        for(int i = Utilities.ZERO; i < a_board.length(); i++){
            if(ACTUAL == a_board.charAt(i)){
                letters++;
            }
        }
        
        // Only return true if there was exactly one K or k found
        // or if the number of other characters found did not exceed the prescribed limit
        if(a_type == PieceType.KING){
            return letters == LIMIT;
        }else{
            return letters <= LIMIT;
        }
    }
    
    /**/
    /*
    NAME
        private final int Pieces(final String a_board, final ChessColor a_color);
    
    SYNOPSIS
        private final int Pieces(final String a_board, final ChessColor a_color);
    
        String a_board -----------> The FEN board for the parser to read.

        ChessColor a_color -------> The color to check.

    DESCRIPTION
        This method finds the number of pieces of a certain color.
        This is used when determining if each side has pieces in the range [1, 16].
    
    RETURNS
        int pieces: The number of black or white pieces, depending on the color passed in.
    
    AUTHOR
        Ryan King
    */
    private final int Pieces(final String a_board, final ChessColor a_color){
        int pieces = Utilities.ZERO;
        
        // Null or empty arguments do not return anything of value
        if(a_board == null || a_board.isBlank() || a_color == null){
            return pieces;
        }
        
        // Iterate through every character on the board
        for(int i = Utilities.ZERO; i < a_board.length(); i++){
            if(a_color.IsWhite()){
                if(Character.isUpperCase(a_board.charAt(i))){
                    pieces++;
                }
            }else{
                if(Character.isUpperCase(a_board.charAt(i))){
                    pieces++;
                }
            }
        }
        
        return pieces;
    }
    
    /**/
    /*
    NAME
        private final String ExpandRank(final String a_rank);
    
    SYNOPSIS
        private final String ExpandRank(final String a_rank);
    
        String a_rank ----------> A rank in FEN notation to expand.
    
    DESCRIPTION
        This method expands a string that represents the rank of a chessboard
        by replacing all of its numbers with that many hyphen-minuses.
        For example, if the string is R5K2, it will expand to R-----K--,
        which will be parsed by the next phase of deserialization.
        All numbers are checked in descending order every time,
        even if the given string does not have every number.
    
    RETURNS
        String noNumbers: The rank string with all numbers replaced by hyphen-minuses
        as described above.
    
    AUTHOR
        Ryan King, with help taken from:
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/pgn/FenUtilities.java
    */
    private final String ExpandRank(final String a_rank){
    	return a_rank.replaceAll("8", "--------").replaceAll("7", "-------").replaceAll("6", "------").replaceAll("5", "-----").replaceAll("4", "----").replaceAll("3", "---").replaceAll("2", "--").replaceAll("1", "-");
    }
    
    /**/
    /*
    NAME
        private final void ParseRank(final int a_row, final String a_rank, final BoardBuilder a_builder);
    
    SYNOPSIS
        private final void ParseRank(final int a_row, final String a_rank, final BoardBuilder a_builder);
    
        int a_row ------------------> The row of the rank, 0 to 7.
        
        String a_rank --------------> The actual rank with numbers removed, as described above.
        
        BoardBuilder a_builder -----> The BoardBuilder which will contain the placement of all pieces found.
    
    DESCRIPTION
        This method parses a rank string that has been stripped of its numbers
        by checking each character individually. If it is a hyphen-minus, that
        tile is considered empty and the parser moves on. If it is a capital letter,
        it is a white piece and is instantiated as such. If it is a lowercase letter,
        it is a black piece and is instantiated as such. If an invalid character is detected,
        the method will throw an exception.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, with help taken from:
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/pgn/FenUtilities.java
    */
    private final void ParseRank(final int a_row, final String a_rank, final BoardBuilder a_builder) throws Exception{
        if(!BoardUtilities.HasValidValue(a_row) || a_rank == null || a_rank.isBlank() || a_builder == null){
            throw new Exception("Improper file format");
        }
        
    	// Remove the numbers representing empty tiles from the string
    	// and replace them with hyphen-minuses.
    	final String NO_NUMBERS = ExpandRank(a_rank);
    	
    	// Iterate through all 8 spots of the string
    	for(int index = Utilities.ZERO; index < NO_NUMBERS.length(); index++){
    		
    		final char PIECE = NO_NUMBERS.charAt(index);
    		if(PIECE == '-'){
    			// The parser found an empty tile
    			continue;
    		}else if(Character.isLetter(PIECE)){
    			// The parser found a piece
    			switch(PIECE){
    				case Utilities.WHITE_PAWN_ICON: a_builder.SetPiece(new Pawn(ChessColor.WHITE, a_row, index));
    				break;
    				case Utilities.WHITE_QUEEN_ICON: a_builder.SetPiece(new Queen(ChessColor.WHITE, a_row, index));
    				break;
    				case Utilities.WHITE_KING_ICON: a_builder.SetPiece(new King(ChessColor.WHITE, a_row, index, m_canWhiteKingsideCastle, m_canWhiteQueensideCastle));
    				break;
    				case Utilities.WHITE_BISHOP_ICON: a_builder.SetPiece(new Bishop(ChessColor.WHITE, a_row, index));
    				break;
    				case Utilities.WHITE_KNIGHT_ICON: a_builder.SetPiece(new Knight(ChessColor.WHITE, a_row, index));
    				break;
    				case Utilities.WHITE_ROOK_ICON: a_builder.SetPiece(new Rook(ChessColor.WHITE, a_row, index));
    				break;
					case Utilities.BLACK_PAWN_ICON: a_builder.SetPiece(new Pawn(ChessColor.BLACK, a_row, index));
					break;
					case Utilities.BLACK_QUEEN_ICON: a_builder.SetPiece(new Queen(ChessColor.BLACK, a_row, index));
					break;
					case Utilities.BLACK_KING_ICON: a_builder.SetPiece(new King(ChessColor.BLACK, a_row, index, m_canBlackKingsideCastle, m_canBlackQueensideCastle));
					break;
					case Utilities.BLACK_BISHOP_ICON: a_builder.SetPiece(new Bishop(ChessColor.BLACK, a_row, index));
					break;
					case Utilities.BLACK_KNIGHT_ICON: a_builder.SetPiece(new Knight(ChessColor.BLACK, a_row, index));
					break;
					case Utilities.BLACK_ROOK_ICON: a_builder.SetPiece(new Rook(ChessColor.BLACK, a_row, index));
					break;
					default: throw new Exception("Improper file format");
    			}
    		}else{
    			// The parser found an invalid character
    			throw new Exception("Improper file format");
    		}
    	}
    }
    
    /**/
    /*
    NAME
        public final void EvaluatePreviouslyMoved();
    
    SYNOPSIS
        public final void EvaluatePreviouslyMoved();
    
        No parameters.
    
    DESCRIPTION
        This method sets the m_previouslyMoved field
        to a deep copy of the piece that just moved.
        This is useful for determining en passant moves.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public final void EvaluatePreviouslyMoved(){
        // Make a deep copy of the piece that just moved
        m_previouslyMoved = Factory.PieceFactory(m_candidate);
    }
    
    /**/
    /*
    NAME
        private final void ComputerPlay();
    
    SYNOPSIS
        private final void ComputerPlay();
    
        No parameters.
    
    DESCRIPTION
        This method enables the computer player to
        make a move with the minimax algorithm.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, with help from:
        https://stackoverflow.com/questions/14126975/joptionpane-without-button
        https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
        https://docs.oracle.com/javase/8/docs/api/javax/swing/JDialog.html
        and Black Widow Chess by Amir Afghani
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
    */
    private final void ComputerPlay(){
        // Spawn a dialog box to let the user know the computer is thinking
    	m_optionPane = new JOptionPane("Thinking...", JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);

		m_dialog = new JDialog(m_instance);
		m_dialog.setTitle("");

		m_dialog.setContentPane(m_optionPane);

		m_dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		m_dialog.pack();
		m_dialog.setVisible(true);
		
		// Do not allow the user to interfere with the computer's move
		m_menuBar.DisableTabs();

    	m_worker = new SwingWorker<Move, Void>(){
    		
    		@Override
    		protected final Move doInBackground(){    			
    			return Minimax.MinimaxRoot(m_depth, m_board.GetBoard(), m_white, m_black, true, m_computerPlayer.GetColor());
    		}
    		
    		@Override
    		public final void done(){
    			try{
    			    // Wait until the minimax algorithm finishes
    				m_nextMove = get();
    				
    				// Get rid of the "Thinking..." dialog
    				m_dialog.dispose();
    				
    				// Re-enable the File and Help menus
    				m_menuBar.EnableTabs();

    				// Assign all fields from the given move
    				m_candidate = m_nextMove.GetPiece();
    				
    				m_victim = m_nextMove.GetVictim();
    				
    				if(m_victim != null){
    				    m_computerPlayer.AddCapturedPiece(m_victim);
    				    m_humanPlayer.RemoveActivePiece(m_victim);
    				}
    				
    				if(m_computerPlayer.IsWhite()){
    				    m_blackPieces.Refresh(m_computerPlayer);
    				}else{
    				    m_whitePieces.Refresh(m_computerPlayer);
    				}
    				
    				m_sourceTile = m_board.GetBoard().GetTile(m_nextMove.GetOldRow(), m_nextMove.GetOldColumn());
    	        
    				m_destinationTile = m_board.GetBoard().GetTile(m_nextMove.GetNewRow(), m_nextMove.GetNewColumn());
    				
    				// Assign other important fields
    				AssignEnPassantTile();
    				
    				EvaluatePreviouslyMoved();
    				
    				// Reset the board for repainting
    				AssignBoard();

    				// Append the new move onto the correct textbox
    				if(m_computerColor.IsWhite()){
						m_whiteMoves.append(m_nextMove.toString());
					}else{
						m_blackMoves.append(m_nextMove.toString());
					}
    				
    				// Evaluate the game state
    				m_gameState = EvaluateGameState(m_humanPlayer);
        
    				// Keep track of the move clocks
    				if(m_candidate.IsPawn() || m_victim != null){
    					m_currentHalfmoves = Utilities.ZERO;
    				}else{
    					m_currentHalfmoves++;
    				}
        
    				if(m_computerPlayer.IsBlack()){
    					m_fullmoves++;
    				}
    				
    				// Reset the move fields
    				m_sourceTile = null;
    				m_destinationTile = null;
    				m_nextMove = null;
    				
    				// Recalculate legal moves
    				RefreshPlayers();
    				
    				// Redraw the board
    				m_board.DrawBoard();
    				
    				// Call the observer
    				m_watcher.Observe();
    			}catch(Exception e){
    				e.printStackTrace();
    			}
    		}   			
    	};
    	
    	// Run the above on a worker thread
    	m_worker.execute();
    	
    }
    
    /**/
    /*
    NAME
        private final void AssignBoard();
    
    SYNOPSIS
        private final void AssignBoard();
    
        No parameters.
    
    DESCRIPTION
        This method assigns the board after the next move has been made on it.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void AssignBoard(){
    	// Reset the board for repainting
        m_board.SetBoard(m_board.GetBoard().MakeMove(m_nextMove));
    }
    
    /**/
    /*
    NAME
        private final void AssignEnPassantTile();
    
    SYNOPSIS
        private final void AssignEnPassantTile();
    
        No parameters.
    
    DESCRIPTION
        This method assigns the en passant tile if the move was a pawn that moved
        two tiles on its first move.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void AssignEnPassantTile(){
        // Do not assign the en passant tile if the last move was not a pawn that moved two tiles on its first move
    	if(EnablesEnPassantMove()){
			final Tile EN_PASSANT_TILE = (m_candidate.IsWhite() ? m_board.GetBoard().GetTile(m_nextMove.GetNewRow() + Utilities.ONE, m_nextMove.GetOldColumn()) : m_board.GetBoard().GetTile(m_nextMove.GetNewRow() - Utilities.ONE, m_nextMove.GetNewColumn()));			
			m_enPassantTile = EN_PASSANT_TILE.toString();
		}else{
			m_enPassantTile = null;
		}
    }
    
    /**/
    /*
    NAME
        private final boolean EnablesEnPassantMove();
    
    SYNOPSIS
        private final boolean EnablesEnPassantMove();
    
        No parameters.
    
    DESCRIPTION
        This method determines if the previous move was a pawn that moved
        two tiles on its first move.
    
    RETURNS
        True if a pawn moved two squares and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final boolean EnablesEnPassantMove(){
    	return m_nextMove != null && m_nextMove.IsRegular() && m_nextMove.GetPiece().IsPawn() && !m_nextMove.GetPiece().HasMoved() && 
    			((m_nextMove.GetPiece().IsWhite() && m_nextMove.GetNewRow() == m_nextMove.GetOldRow() - Utilities.TWO) || (m_nextMove.GetPiece().IsBlack() && m_nextMove.GetNewRow() == m_nextMove.GetOldRow() + Utilities.TWO)) && m_nextMove.GetOldColumn() == m_nextMove.GetNewColumn();
    }
    
    /**/
    /*
    NAME
        private final void ChooseColor();
    
    SYNOPSIS
        private final void ChooseColor();
    
        No parameters.
    
    DESCRIPTION
        This method allows the human player
        to choose his/her desired color, which
        can either be black or white.
        The computer takes the other color.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void ChooseColor(){
        
        // Initialize the choices
        final Object[] COLORS = {WHITE, BLACK};
        
        // Do not allow the user to break out unless s/he chooses an option
        while(true){
            
            m_buttonInt = JOptionPane.showOptionDialog(m_menuBar, PLAY_AS, TITLE, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, COLORS, null);
            
            // Make sure the option selected is a valid one
            switch(m_buttonInt){
                // For a white human player
                case Utilities.ZERO: m_humanColor = ChessColor.WHITE;
                break;
                // For a black human player
                case Utilities.ONE:  m_humanColor = ChessColor.BLACK;
                break;
                // Do not allow the player to proceed without choosing a color
                default: continue;
            }
            
            // The computer gets the color opposite the human
            m_computerColor = BoardUtilities.Reverse(m_humanColor);

            break;
        }
    }
    
    /**/
    /*
    NAME
        private final void InitializePlayers(final Board a_board);
    
    SYNOPSIS
        private final void InitializePlayers(final Board a_board);
    
        Board a_board --------> The board to refresh on.
    
    DESCRIPTION
        This method initializes one player as a human and the other player
        as a computer, depending on what color the human chose.
        The players' legal moves will be calculated.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void InitializePlayers(final Board a_board){
        // Determine which subtype is needed
        if(m_humanColor.IsWhite()){
            m_white = new Human(ChessColor.WHITE, a_board);// Originally a human
            m_black = new Computer(ChessColor.BLACK, a_board);// Originally a computer
            m_humanPlayer = m_white;
            m_computerPlayer = m_black;
        }else{
            m_white = new Computer(ChessColor.WHITE, a_board);// Originally a computer
            m_black = new Human(ChessColor.BLACK, a_board);// Originally a human
            m_humanPlayer = m_black;
            m_computerPlayer = m_white;
        }

        // Find all pieces on the board and initialize all legal moves
        RefreshPlayers();
    }
    
    /**/
    /*
    NAME
        public final void RefreshPlayers();
    
    SYNOPSIS
        public final void RefreshPlayers();
    
        No parameters.
    
    DESCRIPTION
        This method refreshes the pieces
        and legal moves of both players.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public final void RefreshPlayers(){
        m_white.Refresh(m_board.GetBoard());
        m_black.Refresh(m_board.GetBoard());
    }
    
    /**/
    /*
    NAME
        public static final int GetOriginalRow();
    
    SYNOPSIS
        public static final int GetOriginalRow();
    
        No parameters.
    
    DESCRIPTION
        This method the original row a piece came from.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public static final int GetOriginalRow(){
        return m_originalRow;
    }
    
    /**/
    /*
    NAME
        public static final void GetOriginalColumn();
    
    SYNOPSIS
        public static final void GetOriginalColumn();
    
        No parameters.
    
    DESCRIPTION
        This method the original column a piece came from.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public static final int GetOriginalColumn(){
        return m_originalColumn;
    }
    
    /**/
    /*
    NAME
        public static final DarkBlue GetInstance();
    
    SYNOPSIS
        public static final DarkBlue GetInstance();
    
        No parameters.
    
    DESCRIPTION
        This constructor instantiates one and only one
        Dark Blue chess engine program. If one has already
        been created, the declared alias will point to the
        existing instance.
    
    RETURNS
        DarkBlue m_instance: The singleton instance of this engine.
    
    AUTHOR
        Ryan King
    */
    public static final DarkBlue GetInstance(){
        if(!DarkBlue.HasInstance()){
            return new DarkBlue();
        }else{
            return m_instance;
        }
    }
    
    /**/
    /*
    NAME
        public static final boolean HasInstance();
    
    SYNOPSIS
        public static final boolean HasInstance();
    
        No parameters.
    
    DESCRIPTION
        This method determines if one and only one
        Dark Blue chess engine program exists. If one has already
        been created, the method will return true.
        Otherwise, it will return false.

    RETURNS
        True if an instance of the Dark Blue engine exists, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static final boolean HasInstance(){
        return DarkBlue.m_instance != null;
    }
    
    /**/
    /*
    NAME
        public static final void main(final String[] a_args);
    
    SYNOPSIS
        public static final void main(final String[] a_args);
    
        String[] a_args ------> The array of command line arguments (None are needed for this program).
    
    DESCRIPTION
        This method is the driver of the entire program. It starts by
        showing a human an empty chessboard. Once there, the human can
        click the "File" menu and go down to the "New Game" or "Load Game"
        tabs to start a game.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public static final void main(final String[] a_args){        
    	DarkBlue.GetInstance();
    }
    
    /**/
    /*
    NAME
        public static final void ScrollTest();
    
    SYNOPSIS
        public static final void ScrollTest();
    
        No parameters.
    
    DESCRIPTION
        This method is designed to test the viewport setting on the JScrollPane,
        which is primarily used to determine whether or not to place a scrollbar on
        the move text area.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    @Test
    public static final void ScrollTest(){
        JFrame frame = new JFrame();
        JScrollPane scroll = new JScrollPane();
        JTextArea area = new JTextArea();
        JButton button = new JButton("Add More Text");
        
        button.addActionListener(new ActionListener(){
            @Override
            public final void actionPerformed(final ActionEvent e){
                area.append("Fourscore and seven years ago our fathers brought forth on this continent a new nation, conceived in liberty and dedicated to the proposition that all men are created equal.\nNow we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure.\nWe are met on a great battlefield of that war.\nWe have come to dedicate a portion of that field as a final resting-place for those who here gave their lives that that nation might live.\nIt is altogether fitting and proper that we should do this.\nBut, in a larger sense, we cannot dedicate — we cannot consecrate — we cannot hallow — this ground.\nThe brave men, living and dead, who struggled here have consecrated it, far above our poor power to add or detract.\nThe world will little note, nor long remember what we say here, but it can never forget what they did here.\nIt is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced.\nIt is rather for us to be here dedicated to the great task remaining before us — that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion — that we here highly resolve that these dead shall not have died in vain — that this nation shall have a new birth of freedom and that government of the people, by the people, for the people, shall not perish from the earth.\n");
            }
        });
        
        area.setEditable(false);
        
        scroll.setPreferredSize(new Dimension(200, 200));
        scroll.add(area);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        scroll.setViewportView(area);
        scroll.getPreferredSize();
        
        frame.getContentPane().add(scroll, BorderLayout.CENTER);
        frame.getContentPane().add(button, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    /**/
    /*
    NAME
        private final int NthLastIndexOf(final int a_nth, final char a_char, final String a_string);
    
    SYNOPSIS
        private final int NthLastIndexOf(final int a_nth, final char a_char, final String a_string);
    
        int a_nth -------------> The nth desired index.
        
        char a_char -----------> The desired character to find.
        
        String a_string -------> The string to be searched.
    
    DESCRIPTION
        This method attempts to find the nth to last occurrence of a
        character in a given string. It does so by recursively finding 
        the last character and taking the substring without that last character
        and searching again and again.
    
    RETURNS
        int: The index of the nth-to-last occurrence of a_char.
    
    AUTHOR
        Help taken from Stack Overflow:
        https://stackoverflow.com/questions/54141716/java-second-last-occurrence-of-char-in-string
    */
    private final int NthLastIndexOf(final int a_nth, final char a_char, final String a_string){
        if(a_nth <= Utilities.ZERO){
        	return a_string.length();
        }else{
        	final int NEXT_NTH = a_nth - Utilities.ONE;
        	return NthLastIndexOf(NEXT_NTH, a_char, a_string.substring(Utilities.ZERO, a_string.lastIndexOf(a_char)));
        }
    }
    
    /**/
    /*
    NAME
        private final int NumberOfNewlines(final String a_string);
    
    SYNOPSIS
        private final int NumberOfNewlines(final String a_string);

        String a_string -------> The string to be searched.
    
    DESCRIPTION
        This method attempts to find the total number of newline characters ("\n")
        in the given string a_string. The minimum number this method will return is 0,
        and the maximum is the length of the string.
        This is primarily used to update the textboxes when the user undoes a move.

    RETURNS
        int: The number of newline characters ('\n') found in the given string,
        which can be in the range [0, a_string.length].
    
    AUTHOR
        Ryan King
    */
    private final int NumberOfNewlines(final String a_string){
        // number will hold the number of \n characters found in a_string
    	int number = Utilities.ZERO;
    	
    	// Make the string iterable
    	final char[] ARRAY = a_string.toCharArray();
    	
    	// Search every character of the string for \n
    	for(final char CHAR : ARRAY){
    		if(CHAR == '\n'){
    			number++;
    		}
    	}
    	
    	return number;
    }
    
    /**/
    /*
    NAME
        private final void AdjustCapturedPieces(final String a_cutoff, final Player a_player);
    
    SYNOPSIS
        private final void AdjustCapturedPieces(final String a_cutoff, final Player a_player);

        String a_cutoff -------> The move that just got erased from the game history window.
        
        Player a_player -------> The player whose captured pieces will be viewed.
    
    DESCRIPTION
        This method removes a captured piece icon from a captured piece panel after
        undoing a move. Sorted order is still maintained as described in the class.

    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void AdjustCapturedPieces(final String a_cutoff, final Player a_player){
        final String LAST_MOVE = (NumberOfNewlines(a_cutoff) >= Utilities.TWO ? a_cutoff.substring(NthLastIndexOf(Utilities.TWO, '\n', a_cutoff)) : a_cutoff);               
        
        if(LAST_MOVE.contains("x")){
            final int LIMIT = LAST_MOVE.indexOf('x');
            final String TILE = LAST_MOVE.substring(LIMIT + Utilities.ONE, LIMIT + Utilities.THREE);
                
            final int ROW = BoardUtilities.ToBoardRow(TILE);
            final int COLUMN = BoardUtilities.ToBoardColumn(TILE);
                
            for(final Piece PIECE : a_player.GetCapturedPieces()){
                if(PIECE.GetCurrentRow() == ROW && PIECE.GetCurrentColumn() == COLUMN){
                    a_player.RemoveCapturedPiece(PIECE);
                }
            }
        }
    }
    
    /**/
    /*
    NAME
        private final void UndoClicked();
    
    SYNOPSIS
        private final void UndoClicked();
    
        No parameters.
    
    DESCRIPTION
        This method attempts to undo the previous two halfmoves 
        that were made. If only one halfmove or fewer has been made,
        the interface will warn the user that it cannot go back any further.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void UndoClicked(){
    	if(m_gameHistory.size() >= Utilities.THREE){
    		for(int i = Utilities.ZERO; i < Utilities.TWO; i++){
    			// Remove each intermediate board from the game history
    			final String HISTORY = m_gameHistory.pop();
    			final String OLD = HISTORY.split(" ")[Utilities.ZERO];
    			
    			if(m_positions.containsKey(OLD)){
    				// Reduce the board count by one or remove it entirely
    				// if it is sufficiently low
    				if(m_positions.get(OLD) <= Utilities.ONE){
    					m_positions.remove(OLD);
    				}else{
    					m_positions.replace(OLD, m_positions.get(OLD) - Utilities.ONE);
    				}
    			}
    		}
    		
    		// Record the old board without removing it from the history
    		final String OLD_BOARD = m_gameHistory.peek();
    		
    		try{
    			// Build the old board
    			ParseFEN(OLD_BOARD, false);
    			
    			m_board.DrawBoard();

    			// Get rid of the old board
    			m_gameHistory.pop();
    			
    			// Do not allow the observer to change players
    			m_isPreviouslySavedGame = true;
    			
    			// Manipulate copies of the text in the left and right panels
    			final String WHITE_MOVES = m_whiteMoves.getText();
    			final String BLACK_MOVES = m_blackMoves.getText();
    			
    			// Find how many \n's there are in each of the strings
    			final int WHITE_NEWLINES = NumberOfNewlines(WHITE_MOVES);
    			final int BLACK_NEWLINES = NumberOfNewlines(BLACK_MOVES);
    			
    			final String NEW_WHITE, NEW_BLACK, WHITE_CUTOFF, BLACK_CUTOFF;
    			
    			if(WHITE_NEWLINES > Utilities.ONE){
    				// Find the cutoff of where the last move got appended (this is the 2nd to last newline)
    				final int WHITE_INDEX = NthLastIndexOf(Utilities.TWO, '\n', WHITE_MOVES);
    				
    				// Do not display the move that was just made on either side
    				NEW_WHITE = WHITE_MOVES.substring(Utilities.ZERO, WHITE_INDEX);
    				WHITE_CUTOFF = WHITE_MOVES.substring(WHITE_INDEX);
    			}else{
    				// If only one \n exists, clear the entire history
    				NEW_WHITE = "";
    				WHITE_CUTOFF = WHITE_MOVES;
    			}
    			
    			if(BLACK_NEWLINES > Utilities.ONE){
    				// Find the cutoff of where the last move got appended (this is the 2nd to last newline)
    				final int BLACK_INDEX = NthLastIndexOf(Utilities.TWO, '\n', BLACK_MOVES);
    				
    				// Do not display the move that was just made on either side
    				NEW_BLACK = BLACK_MOVES.substring(Utilities.ZERO, BLACK_INDEX);
    				BLACK_CUTOFF = BLACK_MOVES.substring(BLACK_INDEX);
    			}else{
    				// If only one \n exists, clear the entire history
    				NEW_BLACK = "";
    				BLACK_CUTOFF = BLACK_MOVES;
    			}
    			
    			// Update the textboxes
    			m_whiteMoves.setText(NEW_WHITE);
    			m_blackMoves.setText(NEW_BLACK);
    			
    			String LAST_WHITE = (!BLACK_CUTOFF.equals(BLACK_MOVES) ? BLACK_CUTOFF.substring(NthLastIndexOf(Utilities.TWO, '\n', BLACK_CUTOFF)) : BLACK_CUTOFF);
    			    
    			AdjustCapturedPieces(LAST_WHITE, m_black);
    			
    			String LAST_BLACK = (!WHITE_CUTOFF.equals(WHITE_MOVES) ? WHITE_CUTOFF.substring(NthLastIndexOf(Utilities.TWO, '\n', WHITE_CUTOFF)) : WHITE_CUTOFF);
                    
                AdjustCapturedPieces(LAST_BLACK, m_white);
    			
    			m_blackPieces.Refresh(m_white);
    			m_whitePieces.Refresh(m_black);
    			
    			final String OLD = OLD_BOARD.split(" ")[Utilities.ZERO];
    			// Remove one copy of the board from the hash to prevent accidents regarding threefold repetition
    			if(m_positions.containsKey(OLD)){
    				if(m_positions.get(OLD) <= Utilities.ONE){
    					m_positions.remove(OLD);   				
    				}else{
    					m_positions.replace(OLD, m_positions.get(OLD) - Utilities.ONE);
    				}    				
    			}
    			
    			// Check for any status changes
    			m_watcher.Observe();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}else{
    		JOptionPane.showMessageDialog(m_menuBar, "No previous history available.", TITLE, JOptionPane.ERROR_MESSAGE);
    	}
    }
    
    /*
     * This class represents a GUI depiction of a Tile object.
     * 
     * It contains a model Tile and can also contain a PNG image of a 
     * newspaper-style chess piece if the model's piece field is not null.
     * Otherwise, the tile will be blank.
     * 
     * The tile's color is based off the ChessColor field found in the model.
     * Black tiles are a medium reddish brown and white tiles are a skin-tone peach color.
     * Its original color is a final field which must be used if this tile is turned green
     * as a result of a move that the human decided not to make.
     * 
     * This also contains symbolic constants for the local folder where the piece
     * images are found as well as the PNG extension.
     * 
     * This class uses the MouseListener to allow for an easy and intuitive
     * way to give input. This reduces the likelihood of the human
     * entering invalid input along the way.
     * Most computer movements except for the start of a game are entirely based off mouse events.
     */
    private final class GUITile extends JPanel implements MouseListener{

        // Final values for bookkeeping
        private static final long serialVersionUID = Utilities.ONE_LONG;
        
        // Important path and extension
        public static final String EXTENSION = ".png";
        public static final String FOLDER = "src/com/DarkBlue/GUI/ChessPieces/";
        
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

            // Add the mouse listener
            addMouseListener(this);
            
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
            private final void LightUp();
        
        SYNOPSIS
            private final void LightUp();
        
            No parameters.
        
        DESCRIPTION
            This method turns a tile's color to green if it is a legal move
            or the tile the selected piece is standing on.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void LightUp(){
            this.setBackground(BoardUtilities.SELECTED_GREEN);
            
            revalidate();
            repaint();
        }
        
        /**/
        /*
        NAME
            private final void Revert();
        
        SYNOPSIS
            private final void Revert();
        
            No parameters.
        
        DESCRIPTION
            This method reverts the tile's GUI color to its original color.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void Revert(){
            this.setBackground(this.m_originalColor);
            
            revalidate();
            repaint();
        }
        
        /**/
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
            Adapted from Black Widow Chess by Amir Afghani,
            https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
        */
        @Override
        public final void mouseClicked(final MouseEvent a_event){
            // Do not allow the user to make a move if the game is over
            // or if the AI is playing
        	if(m_watcher.IsGameOver() || m_currentPlayer.IsComputer()){
        		return;
        	}
        	
        	// Only allow the user to use the primary mouse button
            if(SwingUtilities.isLeftMouseButton(a_event)){
            	
            	Respond(a_event.getSource().toString());
            	
            	// Reset the fields and call the observer
            	// once the move has been made
            	if(IsMoveDone()){
            		m_sourceTile = null;
            		m_destinationTile = null;
            		m_board.DrawBoard();           		
            		m_watcher.Observe();
            	}
            }   
            
            // Draw the board
            SwingUtilities.invokeLater(new Runnable(){
            	@Override
                public final void run(){
                	m_board.DrawBoard();
                }
            });
        }
        
        /**/
        /*
        NAME
            public final void Respond(final String a_eventSource);
        
        SYNOPSIS
            public final void Respond(final String a_eventSource);
        
            String a_eventSource -----> The algebraic string representation of the tile the user clicked.
        
        DESCRIPTION
            This method is called on the Event Dispatch Thread to take care of MouseEvents.
            It will determine if the user's click should be processed.
            If so, it will either get the source tile or the destination tile, depending on the state of the turn.
            If selecting a piece initially, the piece's tile and any legal moves it may have will be lit up.
            If deselecting a piece, the tiles will revert back to their original colors and the player can select another piece an unlimited number of times until s/he makes a move.
            Once both tiles have been found, the engine will make the move the player selected and revert the tile colors.
            Play continues after the observer determines if the computer can move.
        
        RETURNS
            Nothing
        
        AUTHOR
            Amir Afghani, Black Widow Chess
            with changes made specifically for this engine by Ryan King
            https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
        */
        public final void Respond(final String a_eventSource){
            // The player has not selected a tile
            if(m_sourceTile == null){
            	// Find the tile that was clicked
            	m_sourceTile = m_board.GetBoard().GetTile(BoardUtilities.ToBoardRow(a_eventSource), BoardUtilities.ToBoardColumn(a_eventSource));
            	
            	// Find the piece on the tile
    			m_candidate = m_sourceTile.GetPiece();
            	
    			// Only highlight moves if the user clicked on a piece that is his/her color and has at least one legal move
                if(m_sourceTile.IsOccupied() && m_candidate.GetColor().IsAlly(m_currentPlayer.GetColor()) && m_candidate.CanMove()){
                    m_shouldHighlightLegalMoves = true;
                }else{
                    if(m_sourceTile.IsEmpty()){
                        JOptionPane.showMessageDialog(m_menuBar, "That tile is empty.", "Empty Tile", JOptionPane.PLAIN_MESSAGE);
                    }else if(m_sourceTile.IsOccupied() && m_candidate.GetColor().IsEnemy(m_currentPlayer.GetColor())){
                        JOptionPane.showMessageDialog(m_menuBar, "That piece is not your color.", "Wrong Color", JOptionPane.PLAIN_MESSAGE);
                    }else if(m_candidate.GetColor().IsAlly(m_currentPlayer.GetColor()) && !m_candidate.CanMove()){
                        JOptionPane.showMessageDialog(m_menuBar, "That piece has no legal moves.", "No Moves", JOptionPane.PLAIN_MESSAGE);
                    }
                    m_sourceTile = null;
                }
            }else{ 
                // The player has selected a destination
                final int ROW = m_originalRow, COLUMN = m_originalColumn;
                
                // Make note of the original coordinates
                m_originalRow = m_sourceTile.GetRow();
                m_originalColumn = m_sourceTile.GetColumn();
                
                // Record the destination tile
                m_destinationTile = m_board.GetBoard().GetTile(BoardUtilities.ToBoardRow(a_eventSource), BoardUtilities.ToBoardColumn(a_eventSource));
              
                // The player deselected his/her current piece
                if(m_sourceTile.GetRow() == m_destinationTile.GetRow() && m_sourceTile.GetColumn() == m_destinationTile.GetColumn()){
                    m_shouldHighlightLegalMoves = false;
                    m_sourceTile = null;
                    m_destinationTile = null;
                }else{
                	// The player chose a place to move                	 
                    if(Utilities.IsLegal(m_candidate, m_destinationTile.GetRow(), m_destinationTile.GetColumn())){
                    	m_shouldHighlightLegalMoves = false;
                    	
                    	// Generate a copy of the victim
                        m_victim = m_destinationTile.GetPiece();                                                                                
                        
                        // Make a copy of the next move
                        m_nextMove = Factory.MoveFactory(m_candidate, m_destinationTile.GetRow(), m_destinationTile.GetColumn(), m_victim, m_board.GetBoard());
                        
                        // Make a copy of the piece that just got moved
                        EvaluatePreviouslyMoved();
                        
                        // Assign the en passant tile, if any
                        AssignEnPassantTile();
                        
                        // Reset the board for repainting
                        AssignBoard();
                        
                        if(m_nextMove.GetVictim() != null){
                            m_humanPlayer.AddCapturedPiece(m_nextMove.GetVictim());
                            m_computerPlayer.RemoveActivePiece(m_nextMove.GetVictim());
                        } 
                        
                        // Update the captured piece panel if necessary
                        if(m_currentPlayer.IsWhite()){
                            m_blackPieces.Refresh(m_humanPlayer);
                        }else{
                            m_whitePieces.Refresh(m_humanPlayer);
                        } 
                        
                        // Record the new move in the correct text field
                        final MoveTextArea AREA = (m_currentPlayer.IsWhite() ? m_whiteMoves : m_blackMoves);
                        
                        AREA.append(m_nextMove.toString());
                        
                        // Determine whether to reset the halfmove clock
                        if(m_candidate.IsPawn() || m_victim != null){
                            m_currentHalfmoves = Utilities.ZERO;
                        }else{
                            m_currentHalfmoves++;
                        }
                        
                        // Increment the fullmove clock if needed
                        if(m_previouslyMoved.IsBlack()){
                        	m_fullmoves++;
                        }                    
                    }else{
                        JOptionPane.showMessageDialog(m_menuBar, "That move is illegal.", "Illegal Move", JOptionPane.PLAIN_MESSAGE);
                        m_destinationTile = null;
                        m_originalRow = ROW;
                        m_originalColumn = COLUMN;
                    }
                }
            }
        }
        
        /**/
        /*
        NAME
            private final boolean IsMoveDone();
        
        SYNOPSIS
            private final boolean IsMoveDone();
        
            No parameters.
        
        DESCRIPTION
            This method checks to see if both the source and destination tiles
            have been assigned and thus if the move to be made has been made.
        
        RETURNS
            True if both tiles are non-null, and false otherwise.
            One of these two options will always occur.
        
        AUTHOR
            Ryan King
        */
        private final boolean IsMoveDone(){
        	return m_sourceTile != null && m_destinationTile != null;
        }
        
        /**/
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
        	return;
        }
        
        /**/
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
        	return;
        }
        
        /**/
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
        	return;
        }
        
        /**/
        /*
        NAME
            public void mousePressed(final MouseEvent a_event);
        
        SYNOPSIS
            public void mousePressed(final MouseEvent a_event);
        
            MouseEvent a_event -------> The MouseEvent object.
        
        DESCRIPTION
            This method determines if the mouse was pressed and held down on this tile.
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
        	return;
        }
        
        /**/
        /*
        NAME
            public final Tile GetTile();
        
        SYNOPSIS
            public final Tile GetTile();
        
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
        
        /**/
        /*
        NAME
            public final int GetRow();
        
        SYNOPSIS
            public final int GetRow();
        
            No parameters.
        
        DESCRIPTION
            This method returns this tile's row from its model Tile.
        
        RETURNS
            int: The tile's row.
        
        AUTHOR
            Ryan King
        */
        public final int GetRow(){
        	return this.m_tile.GetRow();
        }
        
        /**/
        /*
        NAME
            public final int GetColumn();
        
        SYNOPSIS
            public final int GetColumn();
        
            No parameters.
        
        DESCRIPTION
            This method returns this tile's column from its model Tile.
        
        RETURNS
            int: The tile's column.
        
        AUTHOR
            Ryan King
        */
        public final int GetColumn(){
        	return this.m_tile.GetColumn();
        }

        /**/
        /*
        NAME
            public final String toString();
        
        SYNOPSIS
            public final String toString();
        
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
    
    /*
     * This class represents a GUI depiction of a Board object.
     * 
     * It contains a model Board object and has access to some
     * of its methods.
     * 
     * The reason why this Board object is not immutable is because
     * it will need to be reset once a move is made and the GUI needs to be
     * repainted. This will reduce overhead instead of having to instantiate
     * a new GUIBoard every time.
     * 
     * It contains a 10 by 10 array of GUITiles.
     * 
     * The reason why one extra tile is on each row and column is because the outer
     * tiles contain letters and numbers to help the player use algebraic notation.
     * Despite the fact that these are set to white's view at the start of the program,
     * they will dynamically change to fit whichever perspective the user needs
     * depending on what color s/he chose.
     */
    private final class GUIBoard extends JPanel{
        
        private static final long serialVersionUID = Utilities.ONE_LONG;
        private final Color BORDER_RED = new Color(100, 25, 5);
        
        private final GUITile[][] m_tiles;
        private Board m_board;

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
        public GUIBoard(final Board a_board){
            // Set the layout
            super(new GridLayout(Utilities.TEN, Utilities.TEN));

            // Allocate the space for the tiles
            m_tiles = new GUITile[Utilities.TEN][Utilities.TEN];
            
            // Assign the board
            SetBoard(a_board);

            DrawBoard();
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
            
            // Highlight legal moves if a piece has been selected
            if(m_shouldHighlightLegalMoves){
                HighlightLegalMoves();
            }else{
                UndoHighlighting();
            }
            
            revalidate();
            repaint();
        }
        
        /**/
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
                	final JPanel BORDER_TILE = new JPanel();
                	BORDER_TILE.setBackground(this.BORDER_RED);
                	BORDER_TILE.setSize(new Dimension(Utilities.SIXTY, Utilities.SIXTY));
                	BORDER_TILE.setAlignmentX(Component.CENTER_ALIGNMENT);
                	BORDER_TILE.setAlignmentY(Component.CENTER_ALIGNMENT);
                	
                	// Add a letter
                	if(ROW == Utilities.ZERO || ROW == Utilities.NINE){
                		if(COLUMN != Utilities.ZERO && COLUMN != Utilities.NINE){
                			final JLabel LETTER = new JLabel();             			
                			LETTER.setText(BoardUtilities.ToAlgebraicColumn(COLUMN - Utilities.ONE).toUpperCase());
                			LETTER.setForeground(Color.WHITE);
                			Utilities.EnlargeFont(LETTER);
                    		
                    		LETTER.setAlignmentX(Component.CENTER_ALIGNMENT);
                    		LETTER.setAlignmentY(Component.CENTER_ALIGNMENT);
                    		
                    		BORDER_TILE.add(LETTER);
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
        public final GUITile GetTile(final int a_row, final int a_column){
            // Only return a tile if the coordinates are valid
            if((a_row >= Utilities.ZERO && a_row < Utilities.TEN) && (a_column >= Utilities.ZERO && a_column < Utilities.TEN)){
                return m_tiles[a_row][a_column];
            }else{
                return null;
            }
        }
    }// End of GUIBoard class
    
    /*
     * This class is my custom extension of the JMenuBar made specifically for this engine.
     * 
     * It contains a File menu. This contains:
     * 
     * New Game: Start a new game
     * 
     * Load Game submenu:
     *      From File: Loads a saved game from a FEN file. Warns the user if the string is invalid.
     *      From Custom FEN: Loads a saved game from a dynamically entered FEN string. Warns the user if the string is invalid.
     * 
     * Save: Saves a game currently in progress and informs the user about the name of the file.
     *      
     * Quit: Exits the program while asking the user if s/he really wants to do this.
     * If the user quits, the game ends with the other side winning by resignation.
     * 
     * Undo: Undoes the previous two halfmoves. This will not work if fewer than two halfmoves have been made since the start of the game.
     * Deserialized games that had more than two halfmoves made will not be able to be undone any further either, since the program only bases this
     * off the number of halfmoves made during the current session.
     * 
     * It also contains a Help menu. This contains:
     * 
     * Instructions: A brief description on how to use this engine.
     * 
     * Rules of Chess: Opens a local PDF on the user's browser showing the official FIDE laws of chess in English. 
     * Rules listed about touching a piece to make a legal move, improperly prepared boards, time controls, etc. do not apply to this engine.
     * 
     * Help Me Move: The AI plays from the human perspective to determine an ideal move. This move is shown to the user with a dynamically-prepared string.
     */
    public final class DarkBlueMenuBar extends JMenuBar implements ActionListener{

    	private static final long serialVersionUID = Utilities.ONE_LONG;
    	
    	// File menu and associated buttons
    	private final JMenu m_file = new JMenu("File");
        private final JMenuItem m_newGame = new JMenuItem("New Game");
        private final JMenu m_loadGame = new JMenu("Load Game...");
        private final JMenuItem m_save = new JMenuItem("Save");
        private final JMenuItem m_quit = new JMenuItem("Quit");
        private final JMenuItem m_undo = new JMenuItem("Undo");
        
        // Load Game submenu
        private final JMenuItem m_fromFile = new JMenuItem("From File");
        private final JMenuItem m_customFEN = new JMenuItem("From Custom FEN...");
        
        // Help menu and associated buttons
        private final JMenu m_help = new JMenu("Help");
        private final JMenuItem m_helpMeMove = new JMenuItem("Help Me Move");
        private final JMenuItem m_instructions = new JMenuItem("Instructions");
        private final JMenuItem m_rules = new JMenuItem("Rules of Chess");
        
        /**/
        /*
        NAME
            public DarkBlueMenuBar();
        
        SYNOPSIS
            public DarkBlueMenuBar();
        
            No parameters.
        
        DESCRIPTION
            This constructor creates a new DarkBlueMenuBar object,
            populates its menus, adds the menus, and adds ActionListeners
            for every item.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        public DarkBlueMenuBar(){
        	super();
        	
        	AddActionListeners();
        	AddMnemonics();
        	AddItemsToMenu();
        }
        
        /**/
        /*
        NAME
            private final void AddMnemonics();
        
        SYNOPSIS
            private final void AddMnemonics();
        
            No parameters.
        
        DESCRIPTION
            This method adds mnemonics for every clickable menu item.
            This will make it easier to select an option quickly.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void AddMnemonics(){
        	m_newGame.setMnemonic('N');
        	m_save.setMnemonic('S');
        	m_quit.setMnemonic('Q');
        	m_helpMeMove.setMnemonic('H');
        	m_instructions.setMnemonic('I');
        	m_rules.setMnemonic('R');
        	m_fromFile.setMnemonic('F');
        	m_customFEN.setMnemonic('C');
        	m_undo.setMnemonic('U');
        }
        
        /**/
        /*
        NAME
            private final void AddActionListeners();
        
        SYNOPSIS
            private final void AddActionListeners();
        
            No parameters.
        
        DESCRIPTION
            This method adds ActionListeners for every menu item.
            The "Load Game" tab does not need an ActionListener because
            it opens up to show two more buttons.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void AddActionListeners(){
        	m_newGame.addActionListener(this);
        	m_save.addActionListener(this);
        	m_quit.addActionListener(this);
        	m_helpMeMove.addActionListener(this);
        	m_instructions.addActionListener(this);
        	m_rules.addActionListener(this);
        	m_fromFile.addActionListener(this);
        	m_customFEN.addActionListener(this);
        	m_undo.addActionListener(this);
        }
        
        /**/
        /*
        NAME
            private final void AddItemsToMenu();
        
        SYNOPSIS
            private final void AddItemsToMenu();
        
            No parameters.
        
        DESCRIPTION
            This method adds all JMenuItems to both JMenus
            and adds both JMenus to the DarkBlueMenuBar.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void AddItemsToMenu(){
        	m_file.add(m_newGame);
        	m_file.add(m_loadGame);
        	m_file.add(m_undo);
        	m_file.add(m_save);
        	m_file.add(m_quit);
        	this.add(m_file);
        	
        	m_loadGame.add(m_fromFile);
        	m_loadGame.add(m_customFEN);
        	
        	m_help.add(m_helpMeMove);
        	m_help.add(m_instructions);
        	m_help.add(m_rules);
        	this.add(m_help);
        }
        
        /**/
        /*
        NAME
            private final void NewGameClicked();
        
        SYNOPSIS
            private final void NewGameClicked();
        
            No parameters.
        
        DESCRIPTION
            This method starts a new game
            after the New Game button is clicked.
            It resets all UI elements and serialization fields,
            lets the human choose a color, then 
            initializes and draws the board
            before calling the observer.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void NewGameClicked(){
        	// Confirm if the user really wants to quit a game in progress if such a game has not ended
    		if(m_gameState == GameState.NORMAL || m_gameState == GameState.CHECK){
    			if(JOptionPane.showConfirmDialog(this, "Do you really want to quit this game?", TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
    				return;
    			}
    		}
    		
    		m_filename = null;
    				
    		// Reset serialization fields  				
    		m_isPreviouslySavedGame = true;
            m_canWhiteKingsideCastle = false;
            m_canWhiteQueensideCastle = false;
            m_canBlackKingsideCastle = false;
            m_canBlackQueensideCastle = false;
            m_enPassantTile = null;
            		
            // Reset UI elements
            m_whiteMoves.setText("");
            m_blackMoves.setText("");
            m_whitePieces.Clear();
            m_blackPieces.Clear();
            		
            // Game state must be normal at the start of a new game
            m_gameState = GameState.NORMAL;

            // Reset mouse-driven fields
    		m_sourceTile = null;
    		m_destinationTile = null;

    		m_candidate = null;
    		m_victim = null;
    		m_nextMove = null;
    		
    		// Reset the move clocks
    		m_currentHalfmoves = Utilities.ZERO;
    		m_fullmoves = Utilities.ONE;

    		// Reset the position history
    		m_positions.clear();
    		m_gameHistory.clear();
    			   
    		// Let the human player choose which side s/he wants to play as
    		ChooseColor();
    		
    		// Initialize and draw the board
    		m_board.SetBoard(Board.GetStartingPosition());
    		InitializePlayers(m_board.GetBoard());
    		m_board.DrawBoard();
    		
    		// White always goes first
    		m_currentPlayer = m_white;
    		
    		// Enable these buttons on the menu
    		EnableLiveGameButtons();
    		
    		// Watch for any game-ending conditions (not relevant for starting a new game)
    		m_watcher.Observe();
        }
        
        /**/
        /*
        NAME
            private final void FromFileClicked();
        
        SYNOPSIS
            private final void FromFileClicked();
        
            No parameters.
        
        DESCRIPTION
            This method opens up a JFileChooser
            to allow the user to open a FEN file
            after the Load Game... -> From File button is clicked.
            It resets all UI elements and serialization fields,
            lets the human choose a color, then 
            initializes and draws the board
            before calling the observer.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void FromFileClicked(){  		
    		// Parse the board
       		if(!Deserialize() && m_filename != null){ 
       		    JOptionPane.showMessageDialog(m_menuBar, "The file could not be parsed.", "Parser Error", JOptionPane.ERROR_MESSAGE);
       		    m_filename = null;
       		    return;
       		}else if(m_filename == null){
       		    return;
       		}
       		
       		// Clear the UI elements and reset necessary fields
            m_whiteMoves.setText("");
            m_blackMoves.setText("");
            m_whitePieces.Clear();
            m_blackPieces.Clear();
            m_isPreviouslySavedGame = true;
            
            m_filename = null;
       		
       		// See which color the human wants to be
       		ChooseColor();
       		
       		// Initialize both players
       		InitializePlayers(m_board.GetBoard());
       		
       		// Enable these buttons because they will be needed
        	EnableLiveGameButtons();

        	// Reset the history
    		m_positions.clear();
    		m_gameHistory.clear();
    		
    		// Assign the current player based on the turn information given
    		m_currentPlayer = (m_board.GetBoard().WhoseTurnIsIt().IsWhite() ? m_white : m_black);
    		
    		// Observe any potential game-ending conditions
        	m_watcher.Observe();
        }
        
        /**/
        /*
        NAME
            private final void SaveClicked();
        
        SYNOPSIS
            private final void SaveClicked();
        
            No parameters.
        
        DESCRIPTION
            This method saves a game into a FEN file
            after the Save button is clicked.
            It resets all UI elements and some serialization fields,
            clears the board, and tells the user what name the file is.
            All files are saved in the "Serial" folder in the working directory
            of this project.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void SaveClicked(){
        	// Turn the game information into a FEN string
        	final String FEN = Serialize();
        	
        	// Get a unique filename with the timestamp
        	final String FILE = "DarkBlue" + GetDate() + ".fen";
        	
        	try{
        		// Write the FEN string to a file
        		final OutputStreamWriter OUT = new OutputStreamWriter(new FileOutputStream("src/com/DarkBlue/Serial/" + FILE), "UTF-8");
        		
        		OUT.write(FEN);
        		
        		OUT.flush();
        		
        		OUT.close();
        		
        		// Disable these buttons as they are not usable when not playing a game
        		DisableLiveGameButtons();
        		
        		// Clear the board
        		m_board.SetBoard(Board.GetEmptyBoard());
        		        		
        		m_board.DrawBoard();
        		
        		// Clear the move history textboxes
        		m_whiteMoves.setText("");
        		m_blackMoves.setText("");
        		
        		// Reset the move clocks
        		m_currentHalfmoves = Utilities.ZERO;
        		m_fullmoves = Utilities.ONE;

        		// Reset the game history
        		m_positions.clear();
        		m_gameHistory.clear();
        		
        		// Reset the game state
        		m_gameState = GameState.EMPTY;
        		
        		// Inform the user of the filename to find later
        		JOptionPane.showMessageDialog(m_instance, "Game saved as \"" + FILE + "\".", TITLE, JOptionPane.PLAIN_MESSAGE);
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
        
        /**/
        /*
        NAME
            private final void CustomFENClicked();
        
        SYNOPSIS
            private final void CustomFENClicked();
        
            No parameters.
        
        DESCRIPTION
            This method opens up a textbox
            to allow the user to input a custom FEN file
            after the Load Game... -> From Custom FEN... button is clicked.
            It resets all UI elements and serialization fields,
            lets the human choose a color, then 
            initializes and draws the board
            before calling the observer.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void CustomFENClicked(){
            boolean notNull = false;
            
        	try{
        	    // Grab the FEN string from the user
        		final String FEN = JOptionPane.showInputDialog(this, "Please enter a custom FEN string below:", TITLE, JOptionPane.PLAIN_MESSAGE);
        		
        		if(FEN != null){
        		    notNull = true;
        		}
        		
        		if(notNull && !IsValidFEN(FEN)){
        		    JOptionPane.showMessageDialog(m_menuBar, "The FEN string could not be parsed.", "Parser Error", JOptionPane.ERROR_MESSAGE);
        		    return;
        		}else if(!notNull){
        		    return;
        		}
        		
        		// Parse the string, trimming any trailing whitespace away
        		ParseFEN(FEN.trim(), true);
        		
        		// Set the move textareas
        		m_whiteMoves.setText("...\n");
        		if(!m_board.GetBoard().WhoseTurnIsIt().IsBlack()){
        			m_blackMoves.setText("...\n");
        		}
        		
        		// Clear the captured piece panels
        		m_whitePieces.Clear();
                m_blackPieces.Clear();
        		
        		// Do not allow the observer to change sides on its first call
        		m_isPreviouslySavedGame = true;
        		
        		// Allow the human player to choose his/her color
        		ChooseColor();
        		
        		// Initialize both players
           		InitializePlayers(m_board.GetBoard());
           		
           		// Draw the board
           		m_board.DrawBoard();
           		
           		// Turn on the buttons that are only accessible during the game
            	EnableLiveGameButtons();
            	
            	// Reset MouseListener fields
            	m_sourceTile = null;
            	m_destinationTile = null;
            	
            	// Reset game history fields
        		m_positions.clear();
        		m_gameHistory.clear();
        		
        		// Initialize the current player
        		m_currentPlayer = (m_board.GetBoard().WhoseTurnIsIt().IsWhite() ? m_white : m_black);
        		
        		// Call the observer
            	m_watcher.Observe();
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
        
        /**/
        /*
        NAME
        	private final void QuitClicked();
        
        SYNOPSIS
            private final void QuitClicked();
        
            No parameters.
        
        DESCRIPTION
            This method quits a game
            after the Quit button is clicked.
            It asks the player if they really
            want to quit before terminating the program.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void QuitClicked(){
        	// Make sure the game is no longer playable, or show a warning message 
            if(m_gameState == GameState.NORMAL || m_gameState == GameState.CHECK){
            	final int QUIT = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit without saving?", "Quit Game", JOptionPane.YES_NO_OPTION);
                
            	// Quitting in the middle of a game is resigning and counts as a loss
            	if(QUIT == JOptionPane.YES_OPTION){
            		if(m_humanColor.IsWhite()){
            			JOptionPane.showMessageDialog(this, BLACK_RESIGNATION, "Game Over", JOptionPane.ERROR_MESSAGE);
            		}else{
            			JOptionPane.showMessageDialog(this, WHITE_RESIGNATION, "Game Over", JOptionPane.ERROR_MESSAGE);
            		}
            	}else{
            	    return;
            	}
            }
            
            // Terminate the program
            System.exit(Utilities.ZERO);
        }
        
        /**/
        /*
        NAME
            private final void InstructionsClicked();
        
        SYNOPSIS
            private final void InstructionsClicked();
        
            No parameters.
        
        DESCRIPTION
            This method shows the chess engine instruction manual
            after the Instructions button is clicked.
            It searches the project's working directory and
            opens a PDF containing the instructions on how to use the
            Dark Blue chess engine.           
        
        RETURNS
            Nothing
        
        AUTHOR
            MK Yong, "How to open a PDF File in Java",
            https://mkyong.com/java/how-to-open-a-pdf-file-in-java/
        */
        private final void InstructionsClicked(){
            try{            
                // Attempt to find and open the instruction PDF file stored locally in the project
                final File INSTRUCTIONS = new File("src/com/DarkBlue/Instructions.pdf");
                if(INSTRUCTIONS.exists()){

                    // Open the file with the AWT desktop utility if usable
                    if(Desktop.isDesktopSupported()){
                        Desktop.getDesktop().open(INSTRUCTIONS);
                    }else{
                        System.err.println("AWT Desktop is not supported!");
                    }

                }else{
                    System.err.println("The instruction manual does not exist!");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        /**/
        /*
        NAME
        	private final void RulesClicked();
        
        SYNOPSIS
            private final void RulesClicked();
        
            No parameters.
        
        DESCRIPTION
            This method shows the official FIDE laws of chess
            after the Rules button is clicked.
            It searches the project's working directory and
            opens a PDF containing the rules of chess.
            The rules about piece selection and draw by agreement 
            do not apply to this engine.
        
        RETURNS
            Nothing
        
        AUTHOR
            MK Yong, "How to open a PDF File in Java",
            https://mkyong.com/java/how-to-open-a-pdf-file-in-java/
        */
        private final void RulesClicked(){
        	try{
        	    // Attempt to find and open the laws of chess PDF file stored locally in the project
        		final File LAWS_OF_CHESS = new File("src/com/DarkBlue/LawsOfChess.pdf");
        		if(LAWS_OF_CHESS.exists()){

        		    // Open the file with the AWT desktop utility if usable
        			if(Desktop.isDesktopSupported()){
        				Desktop.getDesktop().open(LAWS_OF_CHESS);
        			}else{
        				System.err.println("AWT Desktop is not supported!");
        			}

        		}else{
        			System.err.println("The FIDE laws of chess document does not exist!");
        		}
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
        
        /**/
        /*
        NAME
        	private final void HelpMeMoveClicked();
        
        SYNOPSIS
            private final void HelpMeMoveClicked();
        
            No parameters.
        
        DESCRIPTION
            This method suggests a move to the user
            after the Help Me Move button is clicked.
            It uses the computer's own Minimax algorithm
            to determine the best course of action and displays
            the results in a pop-up window.
            Each string is custom-built for each unique move using
            a series of if statements.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King, with additional help taken for opening the "Thinking..." dialog box and
            Amir Afghani's usage of SwingWorker in Black Widow Chess.
            https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
            https://docs.oracle.com/javase/8/docs/api/javax/swing/JDialog.html
            https://stackoverflow.com/questions/28158433/jdialog-without-default-buttons
            https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
        */
        private final void HelpMeMoveClicked(){
        	// Show an indicator that the utility is working
        	m_optionPane = new JOptionPane("Thinking...", JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);

        	// Disable the File and Help menus
            DisableTabs();
        	
        	// Construct a new dialog and set it up
    		m_dialog = new JDialog(m_instance);
    		m_dialog.setTitle("");

    		m_dialog.setContentPane(m_optionPane);

    		m_dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    		m_dialog.pack();
    		m_dialog.setVisible(true);

    		// Execute Minimax from the human player's perspective
        	m_worker = new SwingWorker<Move, Void>(){
        		@Override
        		public final Move doInBackground(){
        		    // Find the best move from the current player's perspective
        			return Minimax.MinimaxRoot(m_depth, m_board.GetBoard(), m_white, m_black, true, m_humanPlayer.GetColor());
        		}
        		
        		@Override
        		public final void done(){
        			try{
        			    // Get the best move when the worker thread is done
        				final Move BEST_MOVE = get();
        				
        				// Get rid of the dialog box
        				m_dialog.dispose();
        				
        				// Re-enable the File and Help menus
        				EnableTabs();
        			
        				// Construct a custom message to send to the user
        				String message = "I recommend you ";
            		
        				if(BEST_MOVE.IsRegular()){
        					message += "move your " + BEST_MOVE.GetPiece().GetPieceType().toString().toLowerCase() + " from " + BoardUtilities.ToAlgebraic(BEST_MOVE.GetOldRow(), BEST_MOVE.GetOldColumn());      			
        					message += " to " + BoardUtilities.ToAlgebraic(BEST_MOVE.GetNewRow(), BEST_MOVE.GetNewColumn()) + ".";
        				}else if(BEST_MOVE.IsAttacking()){
        					message += "use your " + BEST_MOVE.GetPiece().GetPieceType().toString().toLowerCase() + " on " + BoardUtilities.ToAlgebraic(BEST_MOVE.GetOldRow(), BEST_MOVE.GetOldColumn());
        					message += " to capture the " + BEST_MOVE.GetVictim().GetPieceType().toString().toLowerCase();
        					message += " on " + BoardUtilities.ToAlgebraic(BEST_MOVE.GetNewRow(), BEST_MOVE.GetNewColumn()) + ".";
        				}else if(BEST_MOVE.IsCastling()){
        					message += "perform a ";
            			
        					if(BEST_MOVE.GetNewColumn() < BEST_MOVE.GetOldColumn()){
        						message += "queen";
        					}else{
        						message += "king";
        					}
            			
        					message += "side castle.";
        				}else{
        					message += "perform an en passant capture with your pawn on " + BoardUtilities.ToAlgebraic(BEST_MOVE.GetOldRow(), BEST_MOVE.GetOldColumn()) + ".";
        				}
            		
        				// Display the message
        				JOptionPane.showMessageDialog(m_menuBar, message, TITLE, JOptionPane.INFORMATION_MESSAGE);
        			}catch(Exception e){
        				e.printStackTrace();
        			}
        		}
        	};
        	
        	m_worker.execute();
        }
        
        /**/
        /*
        NAME
            public final void DisableTabs();
        
        SYNOPSIS
            public final void DisableTabs();
        
            No parameters.
        
        DESCRIPTION
            This method disables the "File" and "Help" menus
            on the DarkBlueMenuBar.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        public final void DisableTabs(){
            m_file.setEnabled(false);
            m_help.setEnabled(false);
        }
        
        /**/
        /*
        NAME
            public final void EnableTabs();
        
        SYNOPSIS
            public final void EnableTabs();
        
            No parameters.
        
        DESCRIPTION
            This method enables the "File" and "Help" menus
            on the DarkBlueMenuBar.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        public final void EnableTabs(){
            m_file.setEnabled(true);
            m_help.setEnabled(true);
        }
        
        /**/
        /*
        NAME
            public final void actionPerformed(final ActionEvent a_event);
        
        SYNOPSIS
            public final void actionPerformed(final ActionEvent a_event);
        
            ActionEvent a_event ---------> The ActionEvent that was fired.
        
        DESCRIPTION
            This method is overridden from the ActionListener interface.
            It gets called when one of the menu items is pressed.
            This method violates the senior project naming conventions
            because it is directly overridden from Java.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        @Override
        public final void actionPerformed(final ActionEvent a_event){
        	if(a_event.getSource() == this.m_newGame){
        		// Start a new game
        		NewGameClicked();
        	}else if(a_event.getSource() == this.m_fromFile){
        		// Load a game from a file
        		FromFileClicked();
            }else if(a_event.getSource() == this.m_save){
            	// Save a game as a FEN file
            	SaveClicked();
            }else if(a_event.getSource() == this.m_customFEN){
            	// Enter a custom FEN string that's not from a file
            	CustomFENClicked();
        	}else if(a_event.getSource() == this.m_quit){
        		// Quit a game without saving
            	QuitClicked();
        	}else if(a_event.getSource() == this.m_helpMeMove){
        		// Ask the computer for help with moving
        		HelpMeMoveClicked();
            }else if(a_event.getSource() == this.m_instructions){
            	// Show instructions on how to use this engine
            	InstructionsClicked();
            }else if(a_event.getSource() == this.m_rules){
            	// Show the rules of chess as per the regulations set forth by FIDE
            	RulesClicked();
            }else if(a_event.getSource() == this.m_undo){
            	// Undo a move if the human player thinks s/he made a mistake
            	UndoClicked();
            }
        	
        	// Draw the board
        	SwingUtilities.invokeLater(new Runnable(){
    			@Override
    			public final void run(){
    				m_board.DrawBoard();   			       				
    			}
    		});
        }
        
        /**/
        /*
        NAME
            private final void DisableLiveGameButtons();
        
        SYNOPSIS
            private final void DisableLiveGameButtons();
        
            No parameters.
        
        DESCRIPTION
            This method disables the "Save", "Undo", and "Help Me Move"
            buttons on the menu.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        public final void DisableLiveGameButtons(){
            m_menuBar.DisableSave();
            m_menuBar.DisableUndo();
            m_menuBar.DisableHelpMeMove();
        }
        
        /**/
        /*
        NAME
            private final void EnableLiveGameButtons();
        
        SYNOPSIS
            private final void EnableLiveGameButtons();
        
            No parameters.
        
        DESCRIPTION
            This method enables the "Save", "Undo", and "Help Me Move"
            buttons on the menu.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        public final void EnableLiveGameButtons(){
            m_menuBar.EnableSave();
            m_menuBar.EnableUndo();
            m_menuBar.EnableHelpMeMove();
        }
        
        /**/
        /*
        NAME
        	private final void EnableSave();
        
        SYNOPSIS
        	private final void EnableSave();
        
        	No parameters.
        
        DESCRIPTION
        	This method enables the "Save Game" option
        	found on the DarkBlueMenuBar.
        
        RETURNS
        	Nothing
        
        AUTHOR
            Ryan King
        */
        private final void EnableSave(){
        	m_save.setEnabled(true);
        }
        
        /**/
        /*
        NAME
            private final void EnableUndo();
        
        SYNOPSIS
            private final void EnableUndo();
        
            No parameters.
        
        DESCRIPTION
            This method enables the "Undo" option
            found on the DarkBlueMenuBar.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void EnableUndo(){
            m_undo.setEnabled(true);
        }
        
        /**/
        /*
        NAME
        	private final void DisableSave();
        
        SYNOPSIS
        	private final void DisableSave();
        
        	No parameters.
        
        DESCRIPTION
        	This method disables the "Save Game" option
        	found on the DarkBlueMenuBar.
        
        RETURNS
        	Nothing
        
        AUTHOR
            Ryan King
        */
        private final void DisableSave(){
        	m_save.setEnabled(false);
        }
        
        /**/
        /*
        NAME
        	private final void DisableUndo();
        
        SYNOPSIS
        	private final void DisableUndo();
        
        	No parameters.
        
        DESCRIPTION
        	This method disables the "Undo" option
        	found on the DarkBlueMenuBar.
        
        RETURNS
        	Nothing
        
        AUTHOR
            Ryan King
        */
        private final void DisableUndo(){
        	m_undo.setEnabled(false);
        }        
        
        /**/
        /*
        NAME
        	private final void DisableLoad();
        
        SYNOPSIS
        	private final void DisableLoad();
        
        	No parameters.
        
        DESCRIPTION
        	This method disables the "Load Game" option
        	found on the DarkBlueMenuBar.
        
        RETURNS
        	Nothing
        
        AUTHOR
            Ryan King
        */
        public final void DisableLoad(){
        	m_loadGame.setEnabled(false);
        }
        
        /**/
        /*
        NAME
        	private final void EnableHelpMeMove();
        
        SYNOPSIS
        	private final void EnableHelpMeMove();
        
        	No parameters.
        
        DESCRIPTION
        	This method enables the "Help Me Move" option
        	found on the DarkBlueMenuBar.
        
        RETURNS
        	Nothing
        
        AUTHOR
            Ryan King
        */
        public final void EnableHelpMeMove(){
        	m_helpMeMove.setEnabled(true);
        }
        
        /**/
        /*
        NAME
        	private final void DisableHelpMeMove();
        
        SYNOPSIS
        	private final void DisableHelpMeMove();
        
        	No parameters.
        
        DESCRIPTION
        	This method disables the "Help Me Move" option
        	found on the DarkBlueMenuBar.
        
        RETURNS
        	Nothing
        
        AUTHOR
            Ryan King
        */
        public final void DisableHelpMeMove(){
        	m_helpMeMove.setEnabled(false);
        }
    }
    
    /*
     * This class is my custom extension of the JTextArea.
     * 
     * It is built to hold the algebraic notation form of any move
     * made during the game.
     * 
     * One area for white is on the left, and one area for black is on the right.
     * 
     * All moves are displayed on top of one another, given line
     * breaks by the observer.
     * 
     * This also takes into account any special character sequences
     * given after check, checkmate, or draw.
     * 
     * It will be partially cleared when a move is undone,
     * and will be completely cleared at the start of a new game.
     */
    public final class MoveTextArea extends JTextArea{

		private static final long serialVersionUID = Utilities.ONE_LONG;

		/**/
        /*
        NAME
        	public MoveTextArea();
        
        SYNOPSIS
        	public MoveTextArea();
        
        	No parameters.
        
        DESCRIPTION
        	This constructor creates a new MoveTextArea object.
        
        RETURNS
        	Nothing
        
        AUTHOR
            Ryan King, with additional help taken from:
            https://stackoverflow.com/questions/8675038/increasing-decreasing-font-size-inside-textarea-using-jbutton
        */
		public MoveTextArea(){
    		super();

    		// The user must not be allowed to edit this directly
    		this.setEditable(false);
    		    	
    		// Make the font larger
    		Utilities.EnlargeFont(this);
    	}
    }
	
    /*
     * This class contains methods that check the status of the board
     * and see if the game is over or not.
     * 
     * IsGameOver() will check to see if the game is over by determining
     * if one player is in checkmate or if there is a draw.
     * 
     * Observe() will also search for check, checkmate, stalemate, and
     * other draw conditions, but will notify the player with a JOptionPane message dialog.
     * No dialog will pop up if the human places the computer into check.
     */
	public final class GameWatcher{
		
		/**/
        /*
        NAME
        	public final boolean IsGameOver();
        
        SYNOPSIS
        	public final boolean IsGameOver();
        
        	No parameters.
        
        DESCRIPTION
        	This method determines if the game is over
        	by checking its GameState variable.
        
        RETURNS
        	boolean: True if the game is over, and false otherwise.
        	One of these two options will always occur.
        
        AUTHOR
            Ryan King
        */
		public final boolean IsGameOver(){
	    	return m_gameState == GameState.CHECKMATE || m_gameState == GameState.STALEMATE || m_gameState == GameState.INSUFFICIENT_MATERIAL || m_gameState == GameState.FIFTY_MOVE_RULE || m_gameState == GameState.THREEFOLD_REPETITION || m_gameState == GameState.EMPTY;
	    }
		
		/**/
	    /*
	    NAME
	        public final void Observe();
	    
	    SYNOPSIS
	        public final void Observe();
	    
	        No parameters.
	    
	    DESCRIPTION
	        This method determines the state of the game after a player has moved.
	        It automatically reassigns the current player pointer to the correct player.
	        If that player has reached a game-ending condition, it will show up in a JOptionPane
	        dialog box and further moves on the board will not be allowed. 
	        The save option will also become disabled.
	        Otherwise, moves are refreshed and play continues as normal.
	    
	    RETURNS
	        Nothing
	    
	    AUTHOR
	        Ryan King
	    */
		public final void Observe(){
			
			// Do not evaluate an empty board
			if(m_board.PieceCount() == Utilities.ZERO){
				m_gameState = GameState.EMPTY;
				return;
			}
			
			// Record the position of the board
			RecordBoard();
			
			// Refresh all moves for both players
			RefreshPlayers();
				
			// Determine who moved last
			final ChessColor PREVIOUS = m_currentPlayer.GetColor();
			final Player OTHER = (PREVIOUS.IsWhite() ? m_black : m_white);
				
			// See if the player moved a pawn to get promoted
			CheckForPromotions(m_currentPlayer);
			
			// Adjust the castling privileges of the other player if necessary
			AdjustCastlingRights(OTHER);
			
			// Refresh the moves again if any pawns got promoted
			RefreshPlayers();
			
			// Determine the right text area based on who moved
			final MoveTextArea AREA = (PREVIOUS.IsWhite() ? m_whiteMoves : m_blackMoves);
			
			// Get a serialized board
			final String SERIAL = Serialize();

			// Save the FEN for undoing the move
			m_gameHistory.push(SERIAL);
			
			// Determine the state of the game
			m_gameState = EvaluateGameState(OTHER);
			
			// Determine if a =Q/=R/=B/=N needs to be appended
			AppendPromotion(AREA);
						
			// See if the game is over
			if(m_gameState == GameState.CHECKMATE && OTHER.IsBlack()){
			    // White won by checkmate
			    AnnounceWhiteCheckmate();
				return;
			}else if(m_gameState == GameState.CHECKMATE && OTHER.IsWhite()){
			    // Black won by checkmate
			    AnnounceBlackCheckmate();
				return;
			}else if(m_gameState == GameState.STALEMATE){
			    // Draw by stalemate
			    AnnounceStalemate(AREA);
				return;
			}else if(m_gameState == GameState.INSUFFICIENT_MATERIAL){
			    // Draw by insufficient material
			    AnnounceInsufficientMaterial(AREA);
				return;
			}else if(m_gameState == GameState.FIFTY_MOVE_RULE){
			    // Draw by fifty-move rule
			    AnnounceFiftyMoveRule(AREA);
				return;
			}else if(m_gameState == GameState.THREEFOLD_REPETITION){
			    // Draw by threefold repetition
			    AnnounceThreefoldRepetition(AREA);
				return;
			}else if(m_gameState == GameState.CHECK){
			    // A player is in check
			    AnnounceCheck(AREA, OTHER);
			}else if(m_gameState == GameState.NORMAL && !m_isPreviouslySavedGame){
			    // Nothing significant happened
			    AREA.append("\n");
			}
			
			// Swap the players
			SwapPlayers(PREVIOUS);
				
			// Allow the computer to play if it is its turn
			if(m_currentPlayer.IsComputer()){
				ComputerPlay();
			}
		}
	}
	
	/**/
    /*
    NAME
        private final void AnnounceWhiteCheckmate();
    
    SYNOPSIS
        private final void AnnounceWhiteCheckmate();
    
        No parameters.
    
    DESCRIPTION
        This method appends a "#\n1-0" to white's text area
        and announces that white has won by checkmate.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	private final void AnnounceWhiteCheckmate(){
	    // White won by checkmate
        m_menuBar.DisableLiveGameButtons();
        m_whiteMoves.append("#\n1-0");
        
        // Announce the end of the game
        JOptionPane.showMessageDialog(m_menuBar, WHITE_CHECKMATE_MESSAGE, GAME_OVER, JOptionPane.ERROR_MESSAGE);                    
	}
	
	/**/
    /*
    NAME
        private final void AnnounceBlackCheckmate();
    
    SYNOPSIS
        private final void AnnounceBlackCheckmate();
    
        No parameters.
    
    DESCRIPTION
        This method appends a "#\n0-1" to black's text area
        and announces that black has won by checkmate.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	private final void AnnounceBlackCheckmate(){
	    // Black won by checkmate
        m_menuBar.DisableLiveGameButtons();
        m_blackMoves.append("#\n0-1");
        
        // Announce the end of the game
        JOptionPane.showMessageDialog(m_menuBar, BLACK_CHECKMATE_MESSAGE, GAME_OVER, JOptionPane.ERROR_MESSAGE);
	}
	
	/**/
    /*
    NAME
        private final void AnnounceStalemate(final MoveTextArea a_area);
    
    SYNOPSIS
        private final void AnnounceStalemate(final MoveTextArea a_area);
    
        MoveTextArea a_area --------> The area to append to.
    
    DESCRIPTION
        This method appends a draw sign to the given text area
        and announces that the game has ended in stalemate.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	private final void AnnounceStalemate(final MoveTextArea a_area){
	    // Draw by stalemate
        m_menuBar.DisableLiveGameButtons();
        a_area.append("\n½-½");
        // Announce the end of the game
        JOptionPane.showMessageDialog(m_menuBar, STALEMATE_MESSAGE, GAME_OVER, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**/
    /*
    NAME
        private final void AnnounceInsufficientMaterial(final MoveTextArea a_area);
    
    SYNOPSIS
        private final void AnnounceInsufficientMaterial(final MoveTextArea a_area);
    
        MoveTextArea a_area --------> The area to append to.
    
    DESCRIPTION
        This method appends a draw sign to the given text area
        and announces that the game has drawn due to insufficient material.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	private final void AnnounceInsufficientMaterial(final MoveTextArea a_area){
	    // Draw by insufficient material
        m_menuBar.DisableLiveGameButtons();
        a_area.append("\n½-½");
        // Announce the end of the game
        JOptionPane.showMessageDialog(m_menuBar, INSUFFICIENT_MATERIAL_MESSAGE, GAME_OVER, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**/
    /*
    NAME
        private final void AnnounceFiftyMoveRule(final MoveTextArea a_area);
    
    SYNOPSIS
        private final void AnnounceFiftyMoveRul(final MoveTextArea a_area);
    
        MoveTextArea a_area --------> The area to append to.
    
    DESCRIPTION
        This method appends a draw sign to the given text area
        and announces that the game has drawn due to the fifty-move rule.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	private final void AnnounceFiftyMoveRule(final MoveTextArea a_area){
	    // Draw by the fifty-move rule
        m_menuBar.DisableLiveGameButtons();
        a_area.append("\n½-½");
        // Announce the end of the game
        JOptionPane.showMessageDialog(m_menuBar, FIFTY_MOVE_MESSAGE, GAME_OVER, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**/
    /*
    NAME
        private final void AnnounceThreefoldRepetition(final MoveTextArea a_area);
    
    SYNOPSIS
        private final void AnnounceThreefoldRepetition(final MoveTextArea a_area);
    
        MoveTextArea a_area --------> The area to append to.
    
    DESCRIPTION
        This method appends a draw sign to the given text area
        and announces that the game has drawn due to threefold repetition.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	private final void AnnounceThreefoldRepetition(final MoveTextArea a_area){
	    // Draw by threefold repetition
        m_menuBar.DisableLiveGameButtons();
        a_area.append("\n½-½");
        // Announce the end of the game
        JOptionPane.showMessageDialog(m_menuBar, THREEFOLD_REPETITION_MESSAGE, GAME_OVER, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**/
    /*
    NAME
        private final void AnnounceCheck(final MoveTextArea a_area, final Player a_other);
    
    SYNOPSIS
        private final void AnnounceCheck(final MoveTextArea a_area, final Player a_other);
    
        MoveTextArea a_area --------> The area to append to.
    
    DESCRIPTION
        This method appends a plus sign after the move in the given text area
        and announces that the human player is in check.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	private final void AnnounceCheck(final MoveTextArea a_area, final Player a_other){
	    // The next player is in check
        a_area.append("+\n");
        // A king cannot castle when he is in check
        a_other.GetKing().RemoveCastlingMoves();
        // Warn the human player if s/he is in check
        if(a_other.IsHuman()){
            JOptionPane.showMessageDialog(m_menuBar, CHECK_MESSAGE, TITLE, JOptionPane.WARNING_MESSAGE);
        }
	}
	
	/**/
    /*
    NAME
        private final void SwapPlayers(final ChessColor a_previous);
    
    SYNOPSIS
        private final void SwapPlayers(final ChessColor a_previous);
    
        ChessColor a_previous -----> The color of the previous mover.
    
    DESCRIPTION
        This method swaps the black and white players.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	private final void SwapPlayers(final ChessColor a_previous){
	    if(!m_isPreviouslySavedGame){
            m_currentPlayer = (a_previous.IsWhite() ? m_black : m_white);
        }else{
            m_isPreviouslySavedGame = false;
        }
	}
	
	/**/
    /*
    NAME
        private final void AppendPromotion(final MoveTextArea a_area);
    
    SYNOPSIS
        private final void AppendPromotion(final MoveTextArea a_area);
    
        MoveTextArea a_area --------> The area to append to.
    
    DESCRIPTION
        This method appends an equals sign followed by a Q, R, B, or N,
        followed by a newline to the text area of the player who just promoted
        a piece.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	private final void AppendPromotion(final MoveTextArea a_area){
	    if(m_promotionString != null){
            a_area.append(m_promotionString);
            m_promotionString = null;
        }
	}
	
	/**/
    /*
    NAME
        private final void AdjustCastlingRights(final Player a_other);
    
    SYNOPSIS
        private final void AdjustCastlingRights(final Player a_other);
    
        Player a_other --------> The other player.
    
    DESCRIPTION
        This method adjusts the castling rights of the opponent's king.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	private final void AdjustCastlingRights(final Player a_other){
	    if(!a_other.GetKing().HasMoved() && a_other.GetKing().IsInOriginalSpot()){
	        // Gather all the fields of the opponent's king and his associated tile
            final King OPPOSING_KING = a_other.GetKing();
            final int KING_ROW = OPPOSING_KING.GetCurrentRow();
            final int KING_COLUMN = OPPOSING_KING.GetCurrentColumn();
            final ChessColor KING_TILE_COLOR = m_board.GetBoard().GetTile(KING_ROW, KING_COLUMN).GetColor();
            final boolean KINGSIDE = OPPOSING_KING.HasKingsideCastlingRook(m_board.GetBoard());
            final boolean QUEENSIDE = OPPOSING_KING.HasQueensideCastlingRook(m_board.GetBoard());
            
            // Make a new king if anything is different
            if(KINGSIDE != OPPOSING_KING.CanKingsideCastle() || QUEENSIDE != OPPOSING_KING.CanQueensideCastle()){
                m_board.GetBoard().GetBoard()[KING_ROW][KING_COLUMN] = new Tile(KING_TILE_COLOR, KING_ROW, KING_COLUMN, new King(a_other.GetColor(), KING_ROW, KING_COLUMN, KINGSIDE, QUEENSIDE));
            }
        }
	}
	
	/**/
    /*
    NAME
        private final void RecordBoard();
    
    SYNOPSIS
        private final void RecordBoard();
    
        No parameters.
    
    DESCRIPTION
        This method records the FEN string of the current board
        into the game history HashMap if the game was not the result
        of an undo or a new game.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	private final void RecordBoard(){
	    // Get only the board configuration
	    final String BOARD = m_board.GetBoard().toString().split(" ")[Utilities.ZERO];
	    
	    // Place in a new entry or increment an existing entry
        if(!m_positions.containsKey(BOARD)){
            m_positions.put(BOARD, Utilities.ONE);
        }else{
            m_positions.replace(BOARD, m_positions.get(BOARD) + Utilities.ONE);
        }
	}
}