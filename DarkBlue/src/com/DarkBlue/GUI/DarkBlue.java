package com.DarkBlue.GUI;

import com.DarkBlue.Board.Board;
import com.DarkBlue.Board.Tile;
import com.DarkBlue.Board.Board.BoardBuilder;
import com.DarkBlue.Move.Move;
import com.DarkBlue.Piece.Piece;
import com.DarkBlue.Piece.PieceType;
import com.DarkBlue.Piece.Pawn;
import com.DarkBlue.Piece.King;
import com.DarkBlue.Player.Player;
import com.DarkBlue.Player.Human;
import com.DarkBlue.Player.Computer;
import com.DarkBlue.Player.Minimax;
import com.DarkBlue.Testing.GameTest;
import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Utilities.BoardUtilities;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Utilities.Factory;
import com.DarkBlue.Utilities.GameState;
import com.DarkBlue.Utilities.GameUtilities;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
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

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.junit.Test;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
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
 * and the game state (Check, stalemate, checkmate, any non-stalemate draw conditions, or normal; explained in further detail in its own section).
 * 
 * This class is a singleton.
 * 
 * The main() method initializes one and only one instance of the Dark Blue engine.
 * 
 * Once initialized, the human player will have a choice between starting a new game, loading a game either from a file or a custom FEN string,
 * or viewing the instructions on how to use this engine or the FIDE laws of chess.
 * 
 * When startin a new game, white always goes first, so most players will choose that option over black.
 * That is why I placed it on the left; most people would read the box from left to right.
 * The other side is taken by the computer. This will not change throughout the duration of the game.
 * 
 * Once the game ends, the player will have a chance to play a new game as the other side or continue to play as his/her previous side.
 * 
 * The game will continue indefinitely until one side wins (and the other consequently loses),
 * the game draws due to certain conditions, or if the human player chooses to resign (this counts as a loss).
 * 
 * Loading a game will check to see if the file does in fact have the extension ".fen" and contains a syntactically correct and playable state.
 * The same checks will be performed when loading a custom FEN string.
 * 
 * The human player may ask for help at any time by clicking the "Help Me Move" button in the Help menu.
 * 
 * Undoing a move is performed by clicking the "Undo" button in the File menu.
 * Please note at least 2 or 3 moves must have been made during the current session
 * to perform such an action, even if a game with a large amount of moves is resumed from a file or string.
 * 
 * The Save, Stop, Undo, and Help Me Move buttons are only accessible when a game is in progress.
 * The Undo button is not accessible when a piece is selected.
 * 
 * The New Game button and Load Game submenu are only accessible when a game is not in progress.
 * 
 * Parts of the design of this class and its internal classes were inspired 
 * by the design of the Table class by Amir Afghani in Black Widow Chess,
 * https://github.com/amir650/BlackWidow-Chess
 * but any code not completely identical to that repository was written by Ryan King.
 */
public final class DarkBlue extends JFrame{
    
    /* All static fields (symbolic constants and fields that must be accessible outside the class) */
    
    private static final long serialVersionUID = Utilities.ONE_LONG;
    
    // Headers for dialog boxes
    public static final String TITLE = "Dark Blue";
    public static final String NEW_GAME = "New Game";
    public static final String LOAD_GAME = "Load Game";
    public static final String SAVE_GAME = "Save Game";
    public static final String GAME_OVER = "Game Over";
    public static final String HELP = "Help";
    public static final String ERROR = "Error";
    public static final String QUIT_HEADER = "Quit Game";
    public static final String STOP_HEADER = "Stop Game";
    public static final String PARSER_ERROR = "Parser Error";
    public static final String SUCCESSFUL_SAVE = "Game Saved Successfully";
    public static final String UNPLAYABLE_HEADER = "Unplayable Game";
    
    // Human piece/move selection error statements
    public static final String EMPTY_TILE_ERROR = "That tile is empty.";
    public static final String EMPTY_TILE_HEADER = "Empty Tile";
    public static final String NO_LEGAL_MOVES_ERROR = "That piece has no legal moves.";
    public static final String NO_LEGAL_MOVES_HEADER = "No Legal Moves";
    public static final String WRONG_COLOR_ERROR = "That piece is not your color.";
    public static final String WRONG_COLOR_HEADER = "Wrong Color";
    public static final String ILLEGAL_MOVE_ERROR = "That move is illegal.";
    public static final String ILLEGAL_MOVE_HEADER = "Illegal Move";
    
    // Move history strings for special conditions
    public static final String DRAW = "\n½-½";
    public static final String WHITE_CHECKMATE = "#\n1-0";
    public static final String BLACK_CHECKMATE = "#\n0-1";
    public static final String CHECK = "+\n";
    
    // Other messages
    public static final String THINKING = "Thinking...";
    public static final String PLAY_AS = "Play as:";
    
    // Messages for conditions
    public static final String CHECK_MESSAGE = "Check!";
    public static final String STALEMATE_MESSAGE = "Stalemate!\nIt\'s a draw.";
    public static final String WHITE_CHECKMATE_MESSAGE = "Checkmate!\nWhite wins.";
    public static final String BLACK_CHECKMATE_MESSAGE = "Checkmate!\nBlack wins.";
    public static final String WHITE_RESIGNATION = "White wins by resignation.";
    public static final String BLACK_RESIGNATION = "Black wins by resignation.";
    public static final String FIFTY_MOVE_MESSAGE = "Fifty-move rule!\nIt\'s a draw.";
    public static final String INSUFFICIENT_MATERIAL_MESSAGE = "Insufficient material!\nIt\'s a draw.";
    public static final String THREEFOLD_REPETITION_MESSAGE = "Threefold repetition!\nIt\'s a draw.";
    
    // Serialization message strings
    public static final String FILE_PREFIX = "DarkBlue";
    public static final String SAVED_FIRST = "Game saved as \"";
    public static final String SAVED_LAST = "\".";
    
    // Help strings
    public static final String I_RECOMMEND = "I recommend you ";
    public static final String MOVE_YOUR = "move your ";
    public static final String FROM = " from ";
    public static final String TO = " to ";
    public static final String ON = " on ";
    public static final String PERIOD = ".";
    public static final String PERFORM_A = "perform a ";
    public static final String USE_YOUR = "use your ";
    public static final String TO_CAPTURE = " to capture the ";
    public static final String SIDE_CASTLE = "side castle";
    public static final String PERFORM_EN_PASSANT = "perform an en passant capture with your pawn on ";
    
    // Messages for saving, stopping, etc.
    public static final String SAVE_GAME_MESSAGE = "Would you like to save the game?";
    public static final String STOP_MESSAGE = "Do you really want to stop the game without saving?";
    public static final String FILE_ERROR = "The file you selected could not be parsed.";
    public static final String CUSTOM_FEN = "Please enter a custom FEN string below:";
    public static final String FEN_ERROR = "The custom FEN string you entered could not be parsed.";
    public static final String UNPLAYABLE_ERROR = "This game is not playable.";
    public static final String QUIT_IN_PROGRESS = "Do you really want to quit the game in progress?";
    public static final String QUIT_NO_GAME = "Do you really want to quit the game?";
    
    // Serialization paths
    public static final String FILE_PATH = "src/com/DarkBlue/";
    public static final String SERIAL_PATH = "src/com/DarkBlue/Serial/";
    public static final String UNICODE = "UTF-8";
    public static final String FILE_EXTENSION = ".fen";
    
    // Options for color choice
    public static final Object WHITE = "White";
    public static final Object BLACK = "Black";
    
    // Sizes for the window and some panels
    public static final Dimension WINDOW_SIZE = new Dimension(4120, 4120);
    public static final Dimension PANEL_SIZE = new Dimension(90, 575);
    public static final Dimension SCROLL_SIZE = new Dimension(200, 200);
    
    
    /* Housekeeping fields which may be used in multiple classes */
    
    // The string representation of the en passant tile
    private static String m_enPassantTile = null;
    
    // The original row and column used for determining en passant captures
    private static int m_originalRow;
    private static int m_originalColumn;
        
    // The piece that was moved previously. Useful in determining en passant for pawns
    private static Piece m_previouslyMoved;
    
    // The singleton instance of the engine
    private static DarkBlue m_instance;
    
    
    /* All non-static fields for general housekeeping */
    
    // Search depth for the AI
    private int m_depth = Utilities.THREE;
    
    // The colors of both players and fields used for preserving their values
    private ChessColor m_humanColor, m_computerColor, m_originalHuman, m_originalComputer;  
    
    // Tells if the observer can switch sides
    private boolean m_isPreviouslySavedGame = false;

    // Determines if legal moves should be highlighted on the human's turn
    private boolean m_shouldHighlightLegalMoves = false;
    
    /* Fields for move evaluation and serialization */

    // Tells if a serialized game can allow either king to castle on either side
    private boolean m_canWhiteKingsideCastle = false;
    private boolean m_canWhiteQueensideCastle = false;
    private boolean m_canBlackKingsideCastle = false;
    private boolean m_canBlackQueensideCastle = false;
    
    // This gets appended to a move text area when one player gets promoted
    private String m_promotionString = null;

    // Determines what error message to display when loading from a file
    private boolean m_isParsable = false;
    
    // Represents the state of the game
    private GameState m_gameState = GameState.EMPTY;

    // The visual representation of the board
    private GUIBoard m_board;

    // Contains the observer
    private GameWatcher m_watcher = new GameWatcher();
    
    // The alias for the current player, used to reduce code
    private Player m_currentPlayer;
    
    // The history of the game from move to move in the form of FEN strings
    private Stack<String> m_gameHistory;
    
    // The number of times each board position has occurred during the game
    private HashMap<String, Integer> m_positions;
    
    // The players described by color
    private Player m_white, m_black;
    
    // The players described by type
    private Player m_humanPlayer, m_computerPlayer;

    // The number of single-sided moves made since the last capture or pawn movement
    private int m_currentHalfmoves = Utilities.ZERO;
    
    // The number of moves made by both white then black during the entire game
    // (counts of two halfmoves that gets incremented once black makes its move)
    private int m_fullmoves = Utilities.ONE;
       
    // The integer that keeps track of buttons on certain UI elements
    private int m_buttonInt;
        
    // The piece to be moved
    private Piece m_candidate;
    
    // MouseAdapter fields for the tiles the human player chooses
    private Tile m_sourceTile = null;

    private Tile m_destinationTile = null;
        
    // The victim of the move, if any
    private Piece m_victim;
    
    // The move to be made once the tiles and legality are evaluated
    private Move m_nextMove;
    
    /* My custom extensions of Swing components */
    
    // Gives a classic-looking menu bar on the top of the screen
    private DarkBlueMenuBar m_menuBar = new DarkBlueMenuBar();
    
    // Displays white's move history in algebraic notation
    private MoveTextArea m_whiteMoves;
    
    // Displays black's move history in algebraic notation
    private MoveTextArea m_blackMoves;
    
    // Allows the move history text areas to be scrollable
    private JScrollPane m_whiteScroll;
    private JScrollPane m_blackScroll;
    
    // Contains all captured pieces during a game
    // in order like so: P P P P P P P P R R N N B B Q
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
    // shown when the computer moves or when the human gets help
    private JOptionPane m_optionPane;
    private JDialog m_dialog;

    // Contains the GUIBoard object
    private JPanel m_boardPanel = new JPanel();

    // Header labels for move history panels
    private JLabel m_whiteLabel = new JLabel("White");
    private JLabel m_blackLabel = new JLabel("Black");
    
    // Allows the computer to move or the 
    // human to get help without stopping the EDT
    private SwingWorker<Move, Void> m_worker;
    
    // Bookkeeping field that makes sure an invalid file
    // was chosen before showing the user an error message
    // i.e., an error message will not show if the user did
    // not choose a file to open in the first place
    private String m_filename = Utilities.EMPTY_STRING;
    
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
        this.m_humanColor = ChessColor.WHITE;
        this.m_computerColor = ChessColor.BLACK;
        
        // Initialize the text areas and scroll panes
        this.m_whiteMoves = new MoveTextArea();
        this.m_blackMoves = new MoveTextArea();
        this.m_whiteScroll = new JScrollPane(this.m_whiteMoves);
        this.m_blackScroll = new JScrollPane(this.m_blackMoves);
        
        SetUpScrollPanes();
        
        // Set up the menu
        this.m_menuBar.DisableLiveGameButtons();
        
        // Initialize the board and the players
        this.m_board = new GUIBoard(Board.GetEmptyBoard());
        InitializePlayers(this.m_board.GetBoard());
        
        // Assign the correct player
        this.m_currentPlayer = (this.m_board.WhoseTurnIsIt() == ChessColor.WHITE ? this.m_white : this.m_black);

        this.CreateAndShowGUI();
    }
    
    /**/
    /*
    NAME
        private final void SetUpScrollPanes();
    
    SYNOPSIS
        private final void SetUpScrollPanes();
        
        No parameters.
    
    DESCRIPTION
        This method sets up the JScrollPanes so they can
        dynamically scroll on the move history panels.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King with help taken from:
        https://stackoverflow.com/questions/45558095/jscrollpane-not-scrolling-in-jtextarea
    */
    private final void SetUpScrollPanes(){
        // Set sizes for the scroll panes
        this.m_whiteScroll.setPreferredSize(PANEL_SIZE);
        this.m_blackScroll.setPreferredSize(PANEL_SIZE);
        
        // Make sure the panes are dynamically scrollable
        ///////////////////////////////////////////////////////////////
        // Source: https://stackoverflow.com/questions/45558095/jscrollpane-not-scrolling-in-jtextarea
        this.m_whiteScroll.setViewportView(this.m_whiteMoves);
        this.m_whiteScroll.getPreferredSize();       
        this.m_blackScroll.setViewportView(this.m_blackMoves);
        this.m_blackScroll.getPreferredSize();
        ///////////////////////////////////////////////////////////////
        
        // Set scrollbar policies based on an answer from Guillaume Poulet
        // Source: https://stackoverflow.com/questions/10346449/scrolling-a-jpanel
        this.m_whiteScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.m_blackScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.m_whiteScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.m_blackScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

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
        Ryan King, with help taken from:
        https://stackoverflow.com/questions/9474121/i-want-to-get-year-month-day-etc-from-java-date-to-compare-with-gregorian-cal
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
    			+ (MONTH < Utilities.TEN ? Integer.toString(Utilities.ZERO) : Utilities.EMPTY_STRING) + Integer.toString(MONTH)
    			+ (DAY < Utilities.TEN ? Integer.toString(Utilities.ZERO) : Utilities.EMPTY_STRING) + Integer.toString(DAY)
    			+ (HOUR < Utilities.TEN ? Integer.toString(Utilities.ZERO) : Utilities.EMPTY_STRING) + Integer.toString(HOUR) 
    			+ (MINUTE < Utilities.TEN ? Integer.toString(Utilities.ZERO) : Utilities.EMPTY_STRING) + Integer.toString(MINUTE)
    			+ (SECOND < Utilities.TEN ? Integer.toString(Utilities.ZERO) : Utilities.EMPTY_STRING) + Integer.toString(SECOND);
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
        // Idiot proofing
        if(this.m_candidate != null){
            // Highlight the tile the piece is currently resting on
        	if(m_humanPlayer.IsWhite()){
        		this.m_board.GetTile(this.m_candidate.GetCurrentRow() + Utilities.ONE, this.m_candidate.GetCurrentColumn() + Utilities.ONE).LightUp();
        	}else{
        		this.m_board.GetTile(Utilities.SEVEN - (this.m_candidate.GetCurrentRow()), Utilities.SEVEN - (this.m_candidate.GetCurrentColumn())).LightUp();
        	}
        	// Highlight the tiles of every legal move; light up different tiles depending on the perspective of the board
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
            // Change the color of the tile the piece is currently resting on
        	if(m_humanPlayer.IsWhite()){
        		this.m_board.GetTile(this.m_candidate.GetCurrentRow() + Utilities.ONE, this.m_candidate.GetCurrentColumn() + Utilities.ONE).Revert();
        	}else{
        		this.m_board.GetTile(Utilities.SEVEN - (this.m_candidate.GetCurrentRow()), Utilities.SEVEN - (this.m_candidate.GetCurrentColumn())).Revert();
        	}
        	// Change the colors of every legal move; change different tiles depending on the perspective of the board
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
    	this.m_top.add(this.m_menuBar);
    	
    	// Put the menu bar on top
    	a_pane.add(this.m_top, BorderLayout.NORTH);
        
    	// Set layouts for all JPanels
        this.m_left.setLayout(new BoxLayout(this.m_left, BoxLayout.LINE_AXIS));
        this.m_right.setLayout(new BoxLayout(this.m_right, BoxLayout.LINE_AXIS));
        
        this.m_whiteInner.setLayout(new BoxLayout(this.m_whiteInner, BoxLayout.PAGE_AXIS));
        this.m_blackInner.setLayout(new BoxLayout(this.m_blackInner, BoxLayout.PAGE_AXIS));
        
        // Set the alignments for both header labels
        this.m_whiteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.m_whiteLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        this.m_blackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.m_blackLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        // Assemble the white move history panel
        this.m_whiteInner.add(this.m_whiteLabel);
        this.m_whiteInner.add(this.m_whiteScroll);
        
        // Place the move history and the captured pieces side by side
        this.m_left.add(this.m_blackPieces);
        this.m_left.add(this.m_whiteInner);
        
        // Assemble the black move history panel
        m_blackInner.add(this.m_blackLabel);
        this.m_blackInner.add(this.m_blackScroll);
        
        // Place the move history and the captured pieces side by side
        this.m_right.add(this.m_blackInner);
        this.m_right.add(this.m_whitePieces);

        // Add the panes to the content pane
        a_pane.add(this.m_left, BorderLayout.WEST);
        
        a_pane.add(this.m_right, BorderLayout.EAST);
        
        a_pane.add(this.m_board, BorderLayout.CENTER);
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
        this.setSize(WINDOW_SIZE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.AddComponentsToPane(this.getContentPane());
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
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
        One of these two options will always occur.
    
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
    
        Player a_player ------> The player who just moved.
    
    DESCRIPTION
        This method checks the player's final rank to determine if 
        any of his/her pawns can get promoted. It sees if a pawn 
        is found on the last possible rank. If so, this pawn is 
        promoted to a knight, bishop, rook, or queen, which is solely up 
        to the discretion of the player, though 90% of promotions 
        typically end with a queen. A new board gets generated 
        with the newly promoted piece in place of the pawn.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void CheckForPromotions(final Player a_player){
    	// This will hold a copy of all the player's pieces
    	final ArrayList<Piece> ACTIVE_PIECES_COPY = new ArrayList<>();
    	
    	// Make deep copies of the player's pieces
    	for(int index = Utilities.ZERO; index < a_player.GetActivePieces().size(); index++){
            
    		Piece piece = a_player.GetActivePieces().get(index);
            
    		piece = Factory.PieceFactory(piece);
            
    		ACTIVE_PIECES_COPY.add(piece);
    	}
        
    	// Find the index of a pawn on its last rank, if any
    	final int INDEX = GetPromotedPawnIndex(a_player, ACTIVE_PIECES_COPY);
    	
    	// Promote the appropriate pawn if one exists
    	if(INDEX != Utilities.NEGATIVE_ONE){
    		final Pawn PAWN = (Pawn) ACTIVE_PIECES_COPY.get(INDEX);
    		final int ROW = PAWN.GetCurrentRow();
    		final int COLUMN = PAWN.GetCurrentColumn();

    		// Return a new Board object with the new powerful piece replacing the pawn
    		this.m_board.SetBoard(PAWN.Promote(this.m_board.GetBoard(), a_player.IsHuman(), a_player.GetColor()));
    				
    		// Set the promotion string so it will be appended to this pawn's move
    		this.m_promotionString = "=" + Character.toUpperCase(this.m_board.GetBoard().GetTile(ROW, COLUMN).GetPiece().GetIcon());

    		// Redraw the new board
    		this.m_board.DrawBoard();
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
    	
    	// No pawns on their last rank were found
    	return Utilities.NEGATIVE_ONE;
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
        int m_depth: The AI search depth, which is set to 3 by default.
    
    AUTHOR
        Ryan King
    */
    public final int GetMaxSearchDepth(){
        return this.m_depth;
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
        
        This is only based on the existence of an unmoved king and an unmoved rook.
        
        For example, in the starting postition, both white and black can theoretically castle, even though it is not legal for them to do so yet.
        This case would necessitate placing KQkq, which indicates both players can castle on either side.
        If both forms of castling are illegal for both players, a hyphen-minus ("-") is placed instead.
        This legality is determined by seeing if the king and both rooks are in their original positions, disregarding any potential disruptions
        in the form of threats or pieces sitting between the king and the rook. This is evaulated at the beginning of every turn.
        
        4. The space on which an en passant move can be performed if the piece that was most recently moved is a pawn.
        This must be calculated regardless of whether any pawn can actually perform an en passant capture.
        
        For example, after the move 1. e4, which moves the white king's pawn from e2 to e4, this pawn could theoretically be captured en passant
        the next turn because it moved 2 spaces on its first move. Otherwise, a hyphen-minus is placed here instead.
        This would generate "e3". The starting position, on the other hand, will generate "-".
        
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
    	String serial = this.m_board.GetBoard().toString();	
    	    	
    	// Add a space as a delimiter
    	serial += Utilities.SPACE;
    	
    	// Add the current number of halfmoves (times white/black moved individually)
    	serial += Integer.toString(this.m_currentHalfmoves);
    	
    	// Add a space as a delimiter
    	serial += Utilities.SPACE;
    	
    	// Add the current number of fullmoves (times when black finished moving)
    	serial += Integer.toString(this.m_fullmoves);
    	
    	return serial;
    }
    
    /**/
    /*
    NAME
        private final boolean Deserialize();
    
    SYNOPSIS
        private final boolean Deserialize();
    
        No parameters.
    
    DESCRIPTION
        This method takes in a serialization string from
        a text file and parses it into a chessboard.
    
    RETURNS
        True if the parsing of the file was successful, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King, with help about the FileNameExtensionFilter taken from:
        https://stackoverflow.com/questions/15771949/how-do-i-make-jfilechooser-only-accept-txt
        
    */
    private final boolean Deserialize(){
    	try{
    	    this.m_originalHuman = this.m_humanColor;
            this.m_originalComputer = this.m_computerColor;
    	    
    		final JFileChooser CHOOSER = new JFileChooser();

    		CHOOSER.setFileFilter(new FileNameExtensionFilter("FEN files", "fen"));
    	
    		final int CHOICE = CHOOSER.showOpenDialog(null);
    		
    		if(CHOICE == JFileChooser.APPROVE_OPTION){
    		    // A file was chosen.
    		    // Set the flag so that an error message will not show
                this.m_filename = CHOOSER.getSelectedFile().getName();

                // Get the file's extension
                final String EXTENSION = m_filename.substring(m_filename.lastIndexOf(DarkBlue.PERIOD.charAt(Utilities.ZERO)) + Utilities.ONE);
                
                // Check to see if the user tried to sneak a non-FEN file through the chooser
                if(!EXTENSION.equalsIgnoreCase("fen")){
                    return false;
                }
    		    
                // Read the contents of the FEN file
    		    final String FEN = ReadFile(CHOOSER.getSelectedFile());
    	    	
    			// Determine if the file is valid before parsing it
    			if(GameUtilities.IsValidFEN(FEN.trim())){
    			    this.m_isParsable = true;
    			    // Determine if the file contains a playable game before playing it
    			    if(!AttemptToParseFile(FEN)){
    			        return false;
    			    }
    			}else{
    			    this.m_isParsable = false;
    			    return false;
    			}

    			return true;
    		}else{
    		    // The chooser was closed and no file was selected, 
    		    // so don't display an error message
    		    this.m_filename = null;
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
        private final String ReadFile(final File a_file);
    
    SYNOPSIS
        private final String ReadFile(final File a_file);
    
        File a_file ----------> The file to be read.
    
    DESCRIPTION
        This method takes in a File object and attempts
        to read it. It will return the FEN string if successful
        or null on failure.
    
    RETURNS
        String: the FEN string if successful or null on failure.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King, with help about the InputStreamReader taken from:
        https://www.programiz.com/java-programming/inputstreamreader
    */
    private final String ReadFile(final File a_file){
        // Open and read the file
        try{
            final InputStreamReader IN = new InputStreamReader(new FileInputStream(a_file), UNICODE);            
                            
            final Scanner SCANNER = new Scanner(IN);
            
            final String FEN = SCANNER.nextLine();
                
            IN.close();
                
            SCANNER.close();
            
            return FEN;
        }catch(Exception e){
            return null;
        }
    }
    
    /**/
    /*
    NAME
        private final void WriteFile(final String a_file, final String a_FEN);
    
    SYNOPSIS
        private final void WriteFile(final String a_file, final String a_FEN);
    
        String a_file ----------> The filename that will be generated.
        
        String a_FEN -----------> The FEN string to write to the file.
    
    DESCRIPTION
        This method takes in two strings representing the filename
        and the FEN string to write to it and then writes a_FEN into
        a file named a_file.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, with help about the OutputStreamWriter taken from:
        https://docs.oracle.com/javase/7/docs/api/java/io/OutputStreamWriter.html
        
    */
    private final void WriteFile(final String a_file, final String a_FEN) throws Exception{
        try{
            final OutputStreamWriter OUT = new OutputStreamWriter(new FileOutputStream(SERIAL_PATH + a_file), "UTF-8");
        
            OUT.write(a_FEN);
        
            OUT.flush();
        
            OUT.close();
        }catch(Exception e){
            throw e;
        }
    }
    
    /**/
    /*
    NAME
        private final boolean AttemptToParseFile(final String a_FEN);
    
    SYNOPSIS
        private final boolean AttemptToParseFile(final String a_FEN);

        String a_FEN -----------> The FEN string to ne parsed.
    
    DESCRIPTION
        This method takes in the FEN string read from a file
        or taken as custom input from the user and attempts to
        validate and parse it.
    
    RETURNS
        boolean: True if the parsing was successful and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final boolean AttemptToParseFile(final String a_FEN){
        // Discontinue parsing if either player is in checkmate or drawn,
        // or if a player who is in check is not the next player to move
        if(!GameUtilities.IsPlayable(a_FEN, this.m_humanColor)){
            this.m_humanColor = this.m_originalHuman;
            this.m_computerColor = this.m_originalComputer;
            return false;
        }

        // This file is deemed to be playable so parse it
        try{
            ParseFEN(a_FEN, true);
        }catch(Exception e){
            return false;
        }
        
        // This parse attempt has been successful
        return true;
    }
    
    /**/
    /*
    NAME
        private final void CheckStatusAndResume();
    
    SYNOPSIS
        private final void CheckStatusAndResume();
    
        No parameters.
    
    DESCRIPTION
        This method determines if the moving player is in check
        upon resuming a deserialized game. If the moving player
        is in check, the player's castling moves are removed.
        If the mover is a human, a message is displayed to the screen.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void CheckStatusAndResume(){
        // Determine if the next player to move is in check
        final Player MOVER = (m_board.WhoseTurnIsIt().IsWhite() ? this.m_white : this.m_black);
        final Player OPPONENT = (MOVER.IsWhite() ? this.m_black : this.m_white);
        final GameState MOVER_STATE = GameUtilities.EvaluateGameState(MOVER, OPPONENT, this.m_board.GetBoard(), this.m_currentHalfmoves, null);
        
        // If the mover is in check and is a human, show a warning message
        if(MOVER_STATE == GameState.CHECK){
            MOVER.GetKing().RemoveCastlingMoves();
            if(MOVER.IsHuman()){
                JOptionPane.showMessageDialog(this.m_menuBar, CHECK_MESSAGE, TITLE, JOptionPane.WARNING_MESSAGE);
            }
        }
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
        This method parses a string in FEN format
        into the fields in the program.
        It throws an exception if it encounters a problem.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, with help taken from:
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/pgn/FenUtilities.java
    */
    private final void ParseFEN(final String a_FENString, final boolean a_isSerializedGame) throws Exception{
        // PARTS will hold the six parts of the FEN string
    	final String[] PARTS = a_FENString.split(Character.toString(Utilities.SPACE));
		
    	// BUILDER will be a template for building the new board
		final BoardBuilder BUILDER = new BoardBuilder();
		
		// PARTS[0] represents the configuration of the board
		final String[] BOARD = PARTS[Utilities.ZERO].split(Character.toString(Utilities.FORWARD_SLASH));
		
		// PARTS[1] determines whose turn it is
		try{
		    ParseTurn(PARTS[Utilities.ONE], BUILDER);
		}catch(Exception e){
		    throw e;
		}
		
		// PARTS[2] determines which players have a king and rook that have not moved,
		// but castling need not be feasible on the current turn
		ParseCastlingRights(PARTS[Utilities.TWO]);
		
		// Set up the board
		for(int i = Utilities.ZERO; i < BOARD.length; i++){
			GameUtilities.ParseRank(i, BOARD[i], BUILDER);
		}
		
		// PARTS[3] contains the destination tile of an en passant capture
		ParseEnPassantTile(PARTS[Utilities.THREE], BUILDER);
		
		// PARTS[4] contains the number of halfmoves made since the last capture or pawn movement
		// PARTS[5] contains the fullmove clock accumulated throughout the entire game
		try{
		    ParseMoveClocks(PARTS[Utilities.FOUR], PARTS[Utilities.FIVE]);
		}catch(Exception e){
		    throw e;
		}
		
		// Set the board configuration
		this.m_board.SetBoard(BUILDER.Build());
		
		// Initialize the pieces of both players and their legal moves
		if(a_isSerializedGame){
		    InitializePlayers(this.m_board.GetBoard());
		}else{
		    RefreshPlayers();
		}
		
		// Set the current player
		this.m_currentPlayer = (this.m_board.WhoseTurnIsIt().IsWhite() ? this.m_white : this.m_black);
		
		// Add ellipses in the text areas if necessary
		SetUpTextAreas(a_isSerializedGame);
    }
    
    /**/
    /*
    NAME
        private final void ParseTurn(final String a_turn, final BoardBuilder a_builder);
    
    SYNOPSIS
        private final void ParseTurn(final String a_turn, final BoardBuilder a_builder);
    
        String a_turn -----------> The string representing whose turn it is.
        
        BoardBuilder a_builder --> The builder which contains information about the board.
    
    DESCRIPTION
        This method parses a string with the letter "w" or "b"
        into a turn flag for the board.
        It throws an exception if it encounters a problem.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void ParseTurn(final String a_turn, final BoardBuilder a_builder) throws Exception{
        if(a_turn.equalsIgnoreCase("w")){
            a_builder.SetWhoseTurn(ChessColor.WHITE);
        }else if(a_turn.equalsIgnoreCase("b")){
            a_builder.SetWhoseTurn(ChessColor.BLACK);
        }else{
            throw new Exception("Invalid turn");
        }
    }
    
    /**/
    /*
    NAME
        private final void ParseCastlingRights(final String a_rights);
    
    SYNOPSIS
        private final void ParseCastlingRights(final String a_rights);
    
        String a_rights -----------> The string representing castling rights.

    DESCRIPTION
        This method parses a string with the letters "KQkq"
        to determine castling rights for both sides.
        No input validation is performed because the rights
        have already been validated previously in the program.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void ParseCastlingRights(final String a_rights){
        // Idiot proofing
        if(a_rights == null || a_rights.isBlank()){
            return;
        }
        
        // No castling rights
        if(a_rights.equals(Character.toString(Utilities.NO_RIGHTS_OR_TILE))){
            this.m_canWhiteKingsideCastle = false;
            this.m_canWhiteQueensideCastle = false;
            this.m_canBlackKingsideCastle = false;
            this.m_canBlackQueensideCastle = false;
        }else{        
            // White kingside castling rights
            if(a_rights.contains(Character.toString(Utilities.WHITE_KING_BOARD_ICON))){
                this.m_canWhiteKingsideCastle = true;
            }else{
                this.m_canWhiteKingsideCastle = false;
            }
        
            // White queenside castling rights
            if(a_rights.contains(Character.toString(Utilities.WHITE_QUEEN_BOARD_ICON))){
                this.m_canWhiteQueensideCastle = true;
            }else{
                this.m_canWhiteQueensideCastle = false;
            }
        
            // Black kingside castling rights
            if(a_rights.contains(Character.toString(Utilities.BLACK_KING_BOARD_ICON))){
                this.m_canBlackKingsideCastle = true;
            }else{
                this.m_canBlackKingsideCastle = false;
            }
        
            // Black queenside castling rights
            if(a_rights.contains(Character.toString(Utilities.BLACK_QUEEN_BOARD_ICON))){
                this.m_canBlackQueensideCastle = true;
            }else{
                this.m_canBlackQueensideCastle = false;
            }
        }
    }
    
    /**/
    /*
    NAME
        private final void ParseEnPassantTile(final String a_tile, final BoardBuilder a_builder);
    
    SYNOPSIS
        private final void ParseEnPassantTile(final String a_tile, final BoardBuilder a_builder);
    
        String a_tile -----------> The string representing the tile.
        
        BoardBuilder a_builder --> The builder contaning information about the board.

    DESCRIPTION
        This method parses a string with the letter a-h
        followed by 3 or 6 as the en passant tile,
        regardless of whether any pawn can perform
        such a capture. If no such tile exists, this string
        will be a hyphen-minus ("-") and no tile will be assigned.
        No input validation is performed because this
        has already been validated previously in the program.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void ParseEnPassantTile(final String a_tile, final BoardBuilder a_builder){
        // Idiot proofing
        if(a_tile == null || a_tile.isBlank() || a_builder == null){
            return;
        }
        
        if(!a_tile.equals(Character.toString(Utilities.NO_RIGHTS_OR_TILE))){
            // There is a valid en passant tile
            m_enPassantTile = a_tile;
            
            // Determine the color of the pawn to find out where the pawn moved
            final ChessColor PAWN_COLOR = (a_builder.WhoseTurnIsIt().IsBlack() ? ChessColor.WHITE : ChessColor.BLACK);
            m_originalRow = (PAWN_COLOR.IsWhite() ? BoardUtilities.ToBoardRow(m_enPassantTile) + Utilities.ONE : BoardUtilities.ToBoardRow(m_enPassantTile) - Utilities.ONE);
            m_originalColumn = BoardUtilities.ToBoardColumn(m_enPassantTile);
            
            // Instantiate the pawn
            m_previouslyMoved = new Pawn(PAWN_COLOR, m_originalRow, m_originalColumn);
        }else{
            // There is no valid en passant tile
            m_enPassantTile = null;
        }
    }
    
    /**/
    /*
    NAME
        private final void ParseMoveClocks(final String a_halfmoves, final String a_fullmoves);
    
    SYNOPSIS
        private final void ParseMoveClocks(final String a_halfmoves, final String a_fullmoves);
    
        String a_halfmoves -----------> The string representing the number of halfmoves made since the last capture or pawn movement.
        
        String a_fullmoves -----------> The string representing the number of times both white and black have moved during the entire game.

    DESCRIPTION
        This method parses the halfmove and fullmove clocks
        into their respective fields.
        Both numbers have been validated so no range checking is performed.
        However, this can throw an exception if something goes wrong.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void ParseMoveClocks(final String a_halfmoves, final String a_fullmoves) throws Exception{
        try{
            // PARTS[4] contains the number of halfmoves that have occurred since the last capture or pawn movement
            this.m_currentHalfmoves = Integer.parseInt(a_halfmoves);
        
            // PARTS[5] contains the number of fullmoves (a set of two moves made by both white and black) made during the entire game
            this.m_fullmoves = Integer.parseInt(a_fullmoves);
        }catch(Exception e){
            throw e;
        }
    }
    
    /**/
    /*
    NAME
        private final void SetUpTextAreas(final boolean a_isSerializedGame);
    
    SYNOPSIS
        private final void SetUpTextAreas(final boolean a_isSerializedGame);
    
        boolean a_isSerializedGame --> If this game is being taken from a serialization string.

    DESCRIPTION
        This method appends dots to one or both text areas to signify
        that this game is being resumed from the middle.
        This will not occur if the boolean field is set to false.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void SetUpTextAreas(final boolean a_isSerializedGame){
        // Add ellipses if this is being resumed
        if(a_isSerializedGame){
            this.m_whiteMoves.append(Utilities.ELLIPSIS);
        
            if(m_board.GetBoard().WhoseTurnIsIt().IsWhite()){
                this.m_blackMoves.append(Utilities.ELLIPSIS);
            }
        }
    }
    
    /**/
    /*
    NAME
        private final void EvaluatePreviouslyMoved();
    
    SYNOPSIS
        private final void EvaluatePreviouslyMoved();
    
        No parameters.
    
    DESCRIPTION
        This method sets the m_previouslyMoved field
        to a deep copy of the piece that just moved.
        It also sets the original row and column fields
        to the fields in the newly created piece.
        This is useful for determining en passant moves.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void EvaluatePreviouslyMoved(){
        // Make a deep copy of the piece that just moved
        m_previouslyMoved = Factory.PieceFactory(this.m_candidate);
        
        // Copy the piece's coordinates
        m_originalRow = m_previouslyMoved.GetCurrentRow();
        m_originalColumn = m_previouslyMoved.GetCurrentColumn();
    }
    
    /**/
    /*
    NAME
        private final void SpawnThinkingDialog();
    
    SYNOPSIS
        private final void SpawnThinkingDialog();
    
        No parameters.

    DESCRIPTION
        This method spawns a buttonless dialog box that
        says "Thinking..." which is used to indicate the
        computer is thinking about its move or the user 
        has just asked for help.
    
    RETURNS
        Nothing
    
    AUTHOR
        Help taken from:
        https://stackoverflow.com/questions/14126975/joptionpane-without-button
        https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
        https://docs.oracle.com/javase/8/docs/api/javax/swing/JDialog.html
    */
    private final void SpawnThinkingDialog(){
        // Spawn a dialog box to let the user know the computer is thinking
        this.m_optionPane = new JOptionPane(THINKING, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);

        this.m_dialog = new JDialog(m_instance);
        this.m_dialog.setTitle(Utilities.EMPTY_STRING);

        this.m_dialog.setContentPane(this.m_optionPane);

        this.m_dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.m_dialog.pack();
        this.m_dialog.setVisible(true);
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
        Ryan King, with help from Black Widow Chess by Amir Afghani
        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
    */
    private final void ComputerPlay(){
        SpawnThinkingDialog();
		
		// Do not allow the user to interfere with the computer's move
		this.m_menuBar.DisableTabs();

    	this.m_worker = new SwingWorker<Move, Void>(){
    		
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

    				// Get the source tile, destination tile, mover, and victim
    				AssignMovingFields();
    				
    				// Assign other important fields
    				AssignEnPassantTile();
    				
    				EvaluatePreviouslyMoved();

    				// Reset the board for repainting
    				AssignBoard();
    				
    				// Redraw the board
                    m_board.DrawBoard();

    				// Append the new move onto the correct textbox
    				AppendMove(m_computerPlayer.GetColor());
    				
    				// Evaluate the game state
    				m_gameState = GameUtilities.EvaluateGameState(m_humanPlayer, m_computerPlayer, m_board.GetBoard(), m_currentHalfmoves, m_positions);
        
    				// Keep track of the move clocks
    				UpdateMoveClocks();
        
    				// Reset the move fields
    				ResetMoveFields();
    				
    				// Recalculate legal moves
    				RefreshPlayers();
    				
    				// Call the observer
    				m_watcher.Observe();
    			}catch(Exception e){
    				e.printStackTrace();
    			}
    		}   			
    	};
    	
    	// Run the above on a worker thread
    	this.m_worker.execute();
    	
    }
    
    /**/
    /*
    NAME
        private final void AssignMovingFields();
    
    SYNOPSIS
        private final void AssignMovingFields();
    
        No parameters.

    DESCRIPTION
        This method assigns all fields that are necessary
        for the computer to make its move.
        This is not used for the human because some of these
        fields may be assigned and reassigned at different points
        due to the human using mouse clicks and making mistakes.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void AssignMovingFields(){
        // Assign all fields from the given move
        this.m_candidate = this.m_nextMove.GetPiece();
        
        this.m_victim = this.m_nextMove.GetVictim();
        
        UpdatePanel(this.m_computerPlayer, this.m_humanPlayer);
        
        this.m_sourceTile = this.m_board.GetBoard().GetTile(this.m_nextMove.GetOldRow(), this.m_nextMove.GetOldColumn());
    
        this.m_destinationTile = this.m_board.GetBoard().GetTile(this.m_nextMove.GetNewRow(), this.m_nextMove.GetNewColumn());
    }
    
    /**/
    /*
    NAME
        private final void UpdatePanel(final Player a_player, final Player a_opponent);
    
    SYNOPSIS
        private final void UpdatePanel(final Player a_player, final Player a_opponent);
    
        Player a_player --------------> The current player.
        
        Player a_opponent ------------> The opponent.

    DESCRIPTION
        This method updates active/captured pieces on
        both sides when a move needs to be made.
        It will then refresh the current player's captured piece panel.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void UpdatePanel(final Player a_player, final Player a_opponent){
        // Idiot proofing
        if(a_player == null || a_opponent == null){
            return;
        }
        
        // Update the newly captured piece
        if(this.m_victim != null){
            a_player.AddCapturedPiece(this.m_victim);
            a_opponent.RemoveActivePiece(this.m_victim);
        }

        // Refresh the captured pieces panel of the current player
        if(a_player.IsWhite()){
            this.m_blackPieces.Refresh(a_player);
        }else{
            this.m_whitePieces.Refresh(a_player);
        }
    }
    
    /**/
    /*
    NAME
        private final void AppendMove(final ChessColor a_color);
    
    SYNOPSIS
        private final void AppendMove(final ChessColor a_color);
    
        ChessColor a_color -----------> The color whose box this move should be appended to.

    DESCRIPTION
        This method appends the given move string to
        the appropriate move history box.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void AppendMove(final ChessColor a_color){
        // Idiot proofing
        if(a_color == null){
            return;
        }
        
        // Append to the correct textbox
        if(a_color.IsWhite()){
            this.m_whiteMoves.append(this.m_nextMove.toString());
        }else{
            this.m_blackMoves.append(this.m_nextMove.toString());
        }
    }
    
    /**/
    /*
    NAME
        private final void UpdateMoveClocks();
    
    SYNOPSIS
        private final void UpdateMoveClocks();
    
        No parameters.

    DESCRIPTION
        This method updates the halfmove and fullmove clocks.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void UpdateMoveClocks(){
        // Update the halfmove clock
        if(this.m_candidate.IsPawn() || this.m_victim != null){
            this.m_currentHalfmoves = Utilities.ZERO;
        }else{
            this.m_currentHalfmoves++;
        }
        
        // Update the fullmove clock
        if(m_previouslyMoved.IsBlack()){
            this.m_fullmoves++;
        }
    }
    
    /**/
    /*
    NAME
        private final void ResetMoveFields();
    
    SYNOPSIS
        private final void ResetMoveFields();
    
        No parameters.

    DESCRIPTION
        This method resets the source tile,
        destination tile, and next move to null.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void ResetMoveFields(){
        this.m_sourceTile = null;
        this.m_destinationTile = null;
        this.m_nextMove = null;
    }
    
    /**/
    /*
    NAME
        private final void AssignBoard();
    
    SYNOPSIS
        private final void AssignBoard();
    
        No parameters.
    
    DESCRIPTION
        This method assigns to the GUIBoard
        the Board object after the next move 
        has been made on it.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void AssignBoard(){
    	// Reset the board for repainting
        this.m_board.SetBoard(this.m_nextMove.GetTransitionalBoard());
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
			final Tile EN_PASSANT_TILE = (this.m_candidate.IsWhite() ? this.m_board.GetBoard().GetTile(m_nextMove.GetNewRow() + Utilities.ONE, this.m_nextMove.GetOldColumn()) : this.m_board.GetBoard().GetTile(this.m_nextMove.GetNewRow() - Utilities.ONE, this.m_nextMove.GetNewColumn()));			
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
        boolean: true if a pawn moved two tiles and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final boolean EnablesEnPassantMove(){
    	return this.m_nextMove != null && this.m_nextMove.IsRegular() && this.m_nextMove.GetPiece().IsPawn() && !this.m_nextMove.GetPiece().HasMoved() && 
    			((this.m_nextMove.GetPiece().IsWhite() && this.m_nextMove.GetNewRow() == this.m_nextMove.GetOldRow() - Utilities.TWO) || (this.m_nextMove.GetPiece().IsBlack() && this.m_nextMove.GetNewRow() == this.m_nextMove.GetOldRow() + Utilities.TWO)) && this.m_nextMove.GetOldColumn() == this.m_nextMove.GetNewColumn();
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
            
            this.m_buttonInt = JOptionPane.showOptionDialog(this.m_menuBar, PLAY_AS, NEW_GAME, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, COLORS, null);
            
            // Make sure the option selected is a valid one
            switch(m_buttonInt){
                // For a white human player
                case Utilities.ZERO: this.m_humanColor = ChessColor.WHITE;
                break;
                // For a black human player
                case Utilities.ONE:  this.m_humanColor = ChessColor.BLACK;
                break;
                // Do not allow the player to proceed without choosing a color
                default: continue;
            }
            
            // The computer gets the color opposite the human
            this.m_computerColor = BoardUtilities.Reverse(this.m_humanColor);

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
        // Idiot proofing
        if(a_board == null){
            return;
        }
        
        // Determine which subtype is needed
        if(this.m_humanColor.IsWhite()){
            // The human chose white, so the computer becomes black
            this.m_white = new Human(ChessColor.WHITE, a_board);
            this.m_black = new Computer(ChessColor.BLACK, a_board);
            this.m_humanPlayer = this.m_white;
            this.m_computerPlayer = this.m_black;
        }else{
            // The humam chose black, so the computer becomes white
            this.m_white = new Computer(ChessColor.WHITE, a_board);
            this.m_black = new Human(ChessColor.BLACK, a_board);
            this.m_humanPlayer = this.m_black;
            this.m_computerPlayer = this.m_white;
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
        this.m_white.Refresh(this.m_board.GetBoard());
        this.m_black.Refresh(this.m_board.GetBoard());
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
            m_instance = new DarkBlue();
        }
        return m_instance;
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
        // Make a new JFrame, JScrollPane, and JTextArea
        // with a JButton to add text
        JFrame frame = new JFrame();
        JScrollPane scroll = new JScrollPane();
        JTextArea area = new JTextArea();
        JButton button = new JButton("Add More Text");
        
        // Place the text of the Gettysburg Address into the textbox on a button click
        button.addActionListener(new ActionListener(){
            @Override
            public final void actionPerformed(final ActionEvent e){
                area.append("Fourscore and seven years ago our fathers brought forth on this continent a new nation, conceived in liberty and dedicated to the proposition that all men are created equal.\nNow we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure.\nWe are met on a great battlefield of that war.\nWe have come to dedicate a portion of that field as a final resting-place for those who here gave their lives that that nation might live.\nIt is altogether fitting and proper that we should do this.\nBut, in a larger sense, we cannot dedicate — we cannot consecrate — we cannot hallow — this ground.\nThe brave men, living and dead, who struggled here have consecrated it, far above our poor power to add or detract.\nThe world will little note, nor long remember what we say here, but it can never forget what they did here.\nIt is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced.\nIt is rather for us to be here dedicated to the great task remaining before us — that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion — that we here highly resolve that these dead shall not have died in vain — that this nation shall have a new birth of freedom and that government of the people, by the people, for the people, shall not perish from the earth.\n");
            }
        });
        
        // Do not allow the area to be edited by the user
        area.setEditable(false);
        
        // Set the preferred size so the JScrollPane will be forced to adapt with scroll bars
        scroll.setPreferredSize(SCROLL_SIZE);
        scroll.add(area);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Set the viewport area to know where to scroll
        scroll.setViewportView(area);
        scroll.getPreferredSize();
        
        // Add all elements to the frame and pack it
        frame.getContentPane().add(scroll, BorderLayout.CENTER);
        frame.getContentPane().add(button, BorderLayout.SOUTH);
        frame.pack();
        
        // Make the frame end the program when closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Start the program by making the frame visible
        frame.setVisible(true);
    }
    
    /**/
    /*
    NAME
        private final int NthLastIndexOf(int a_nth, final char a_char, String a_string);
    
    SYNOPSIS
        private final int NthLastIndexOf(int a_nth, final char a_char, String a_string);
    
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
        Help inspired by an answer I found on Stack Overflow:
        https://stackoverflow.com/questions/54141716/java-second-last-occurrence-of-char-in-string
        and from Baeldung:
        https://www.baeldung.com/java-count-chars       
    */
    private final int NthLastIndexOf(int a_nth, final char a_char, String a_string){
        ////////////////////////////////////////////////////
        // Source: https://www.baeldung.com/java-count-chars
        final long COUNT = a_string.chars().filter(ch -> ch == a_char).count();
        ////////////////////////////////////////////////////
        
        if(a_nth <= Utilities.ZERO || a_nth > COUNT){
            return a_string.length();
        }
        
        while(a_nth > Utilities.ZERO){
            a_string = a_string.substring(Utilities.ZERO, a_string.lastIndexOf(a_char));
            
            a_nth--;
        }
        
        return a_string.length();
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
        // Idiot proofing
        if(a_string == null || a_string == Utilities.EMPTY_STRING){
            return Utilities.ZERO;
        }
        
        // number will hold the number of \n characters found in a_string
    	int number = Utilities.ZERO;
    	
    	// Make the string iterable
    	final char[] ARRAY = a_string.toCharArray();
    	
    	// Search every character of the string for \n
    	for(final char CHAR : ARRAY){
    		if(CHAR == Utilities.NEWLINE){
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
        undoing a move if necessary. Sorted order is still maintained as described in the class.

    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void AdjustCapturedPieces(final String a_cutoff, final Player a_player, final Player a_opponent){
        // Idiot proofing
        if(a_player == null || a_opponent == null || a_cutoff == null || a_cutoff.isBlank()){
            return;
        }
        
        // Find the last made move, if any
        final String LAST_MOVE = (NumberOfNewlines(a_cutoff) >= Utilities.TWO ? a_cutoff.substring(NthLastIndexOf(Utilities.TWO, Utilities.NEWLINE, a_cutoff)) : a_cutoff);               
        
        // Determine if it was an attacking or en passant move
        if(LAST_MOVE.contains(Utilities.CAPTURE)){
            // Find the piece that was captured on the tile
            final int LIMIT = LAST_MOVE.indexOf(Utilities.CAPTURE_CHAR);
            final String TILE = LAST_MOVE.substring(LIMIT + Utilities.ONE, LIMIT + Utilities.THREE);
                
            final int ROW = BoardUtilities.ToBoardRow(TILE);
            final int COLUMN = BoardUtilities.ToBoardColumn(TILE);
                
            // Find that piece in the player's captured pieces and remove it
            for(int index = Utilities.ZERO; index < a_player.GetCapturedPieces().size(); index++){
                final Piece PIECE = a_player.GetCapturedPieces().get(index);
                if(PIECE.GetCurrentRow() == ROW && PIECE.GetCurrentColumn() == COLUMN){
                    a_player.RemoveCapturedPiece(PIECE);
                    a_opponent.AddActivePiece(PIECE);
                }
            }
        }
    }
    
    /**/
    /*
    NAME
        private final void AdjustMoveHistoryPanels();
    
    SYNOPSIS
        private final void AdjustMoveHistoryPanels();

        No parameters.
        
    DESCRIPTION
        This method removes a captured piece from any player that captured one and adjusts
        the move history panels to delete the move that was undone.

    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void AdjustMoveHistoryPanels(){
        // Manipulate copies of the text in the left and right panels
        final String WHITE_MOVES = m_whiteMoves.getText();
        final String BLACK_MOVES = m_blackMoves.getText();
        
        // Find how many \n's there are in each of the strings
        final int WHITE_NEWLINES = NumberOfNewlines(WHITE_MOVES);
        final int BLACK_NEWLINES = NumberOfNewlines(BLACK_MOVES);
        
        final String NEW_WHITE, NEW_BLACK, WHITE_CUTOFF, BLACK_CUTOFF;
        
        if(WHITE_NEWLINES > Utilities.ONE){
            // Find the cutoff of where the last move got appended (this is the 2nd to last newline)
            final int WHITE_INDEX = NthLastIndexOf(Utilities.TWO, Utilities.NEWLINE, WHITE_MOVES);
            
            // Do not display the move that was just made on either side
            NEW_WHITE = WHITE_MOVES.substring(Utilities.ZERO, WHITE_INDEX) + Character.toString(Utilities.NEWLINE);
            WHITE_CUTOFF = WHITE_MOVES.substring(WHITE_INDEX);
        }else{
            // If only one \n exists, clear the entire history
            NEW_WHITE = Utilities.EMPTY_STRING;
            WHITE_CUTOFF = WHITE_MOVES;
        }
        
        if(BLACK_NEWLINES > Utilities.ONE){
            // Find the cutoff of where the last move got appended (this is the 2nd to last newline)
            final int BLACK_INDEX = NthLastIndexOf(Utilities.TWO, Utilities.NEWLINE, BLACK_MOVES);
            
            // Do not display the move that was just made on either side
            NEW_BLACK = BLACK_MOVES.substring(Utilities.ZERO, BLACK_INDEX) + Character.toString(Utilities.NEWLINE);
            BLACK_CUTOFF = BLACK_MOVES.substring(BLACK_INDEX);
        }else{
            // If only one \n exists, clear the entire history
            NEW_BLACK = Utilities.EMPTY_STRING;
            BLACK_CUTOFF = BLACK_MOVES;
        }
        
        // Update the textboxes
        m_whiteMoves.setText(NEW_WHITE);
        m_blackMoves.setText(NEW_BLACK);
        
        // Remove any captured pieces taken on the previous turn
        String LAST_WHITE = (!BLACK_CUTOFF.equals(BLACK_MOVES) ? BLACK_CUTOFF.substring(NthLastIndexOf(Utilities.TWO, Utilities.NEWLINE, BLACK_CUTOFF)) : BLACK_CUTOFF);

        AdjustCapturedPieces(LAST_WHITE, m_black, m_white);
        
        String LAST_BLACK = (!WHITE_CUTOFF.equals(WHITE_MOVES) ? WHITE_CUTOFF.substring(NthLastIndexOf(Utilities.TWO, Utilities.NEWLINE, WHITE_CUTOFF)) : WHITE_CUTOFF);
            
        AdjustCapturedPieces(LAST_BLACK, m_white, m_black);
    }
    
    /**/
    /*
    NAME
        private final void RemoveCopy(final String a_old);
    
    SYNOPSIS
        private final void RemoveCopy(final String a_old);

        String a_old -------------> The old copy of the board to remove from the hash.
        
    DESCRIPTION
        This method removes an undone board configuration from the hash.
        If there are multiple occurrences of this board already recorded
        in the hash, it will decrement that hash entry by one.
        if there is only one copy of this board in the hash, 
        the entire entry will be removed from the hash.

    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void RemoveCopy(final String a_old){
        if(m_positions.containsKey(a_old)){
            if(m_positions.get(a_old) <= Utilities.ONE){
                m_positions.remove(a_old);                
            }else{
                m_positions.replace(a_old, m_positions.get(a_old) - Utilities.ONE);
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
    			final String OLD = HISTORY.split(Character.toString(Utilities.SPACE))[Utilities.ZERO];
    			
    			RemoveCopy(OLD);
    		}
    		
    		// Record the old board without removing it from the history
    		final String OLD_BOARD = m_gameHistory.peek();
    		
    		try{   		    
    		    // Update the panels and remove any captured pieces
    		    AdjustMoveHistoryPanels();
                
                // Refresh the panels accordingly
                m_blackPieces.Refresh(m_white);
                m_whitePieces.Refresh(m_black);
                
    			// Build the old board
    			ParseFEN(OLD_BOARD, false);
    			
    			// Draw the old board
    			this.m_board.DrawBoard();

    			// Get rid of the old board
    			this.m_gameHistory.pop();
    			
    			// Do not allow the observer to change players
    			this.m_isPreviouslySavedGame = true;
    			
    			// Get only the configuration of the old board
    			final String OLD = OLD_BOARD.split(Character.toString(Utilities.SPACE))[Utilities.ZERO];
    			
    			// Remove one copy of the board from the hash to prevent accidents regarding threefold repetition
    			RemoveCopy(OLD);
    			
    			// Check for any status changes
    			m_watcher.Observe();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    	
    	SwingUtilities.invokeLater(new Runnable(){
    	    /**/
            /*
            NAME
                public final void run();
            
            SYNOPSIS
                public final void run();
            
                No parameters.
            
            DESCRIPTION
                This method draws the board on the Event Dispatch Thread
                once everything else is done executing.
            
            RETURNS
                Nothing
            
            AUTHOR
                Adapted from Black Widow Chess by Amir Afghani,
                https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
            */
    	    @Override
    	    public final void run(){
    	        m_board.DrawBoard();
    	    }
    	});
    }
    
    // End of DarkBlueMenuBar class
    
    /**
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
     * 
     * Help with the usage of the MouseAdapter class taken from:
     * https://www.youtube.com/watch?v=CajXXmhIndI
     */
    private final class GUITile extends JPanel{

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
        
        private final MouseAdapter m_adapter = new MouseAdapter(){           
            /**/
            /*
            NAME
                public final void mouseClicked(final MouseEvent a_event);
            
            SYNOPSIS
                public final void mouseClicked(final MouseEvent a_event);
            
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
                
                SwingUtilities.invokeLater(new Runnable(){
                    /**/
                    /*
                    NAME
                        public final void run();
                    
                    SYNOPSIS
                        public final void run();
                    
                        No parameters.
                    
                    DESCRIPTION
                        This method draws the board on the Event Dispatch Thread
                        once everything else is done executing.
                    
                    RETURNS
                        Nothing
                    
                    AUTHOR
                        Adapted from Black Widow Chess by Amir Afghani,
                        https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
                    */
                    @Override
                    public final void run(){
                        m_board.DrawBoard();
                    }
                });
            }
        };

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
            Ryan King, with help taken from
            https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
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
            this.setSize(BoardUtilities.TILE_DIMENSION);
            this.setPreferredSize(BoardUtilities.TILE_DIMENSION);
            this.setBackground(this.m_originalColor);

            // Add the MouseAdapter
            this.addMouseListener(this.m_adapter);
            
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
            
                // Form the relative path to the proper piece image
                final String PATH = FOLDER + COLOR + PIECE + EXTENSION;
            
                // Attempt to read the image into the JLabel and add it to the tile
                try{   
                	this.m_image = new JLabel(new ImageIcon(ImageIO.read(new File(PATH))));
                    this.add(this.m_image);
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
            private final void Respond(final String a_eventSource);
        
        SYNOPSIS
            private final void Respond(final String a_eventSource);
        
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
            Idea taken from Amir Afghani, Black Widow Chess
            https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
            with changes made specifically for this engine by Ryan King
        */
        private final void Respond(final String a_eventSource){
            
            // The player has not selected a tile
            if(m_sourceTile == null){
            	// Find the tile that was clicked
            	m_sourceTile = m_board.GetBoard().GetTile(BoardUtilities.ToBoardRow(a_eventSource), BoardUtilities.ToBoardColumn(a_eventSource));
            	
            	// Find the piece on the tile
    			m_candidate = m_sourceTile.GetPiece();
            	
    			// Only highlight moves if the user clicked on a piece that is his/her color and has at least one legal move
                if(m_sourceTile.IsOccupied() && m_candidate.GetColor().IsAlly(m_currentPlayer.GetColor()) && m_candidate.CanMove()){
                    m_shouldHighlightLegalMoves = true;
                    m_menuBar.DisableUndo();
                }else{
                    // Show dialog boxes to report specific types of errors
                    ShowSourceErrorMessage();
                    m_sourceTile = null;
                    m_menuBar.EnableUndo();
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
                    ResetHumanFields();
                    m_menuBar.EnableUndo();
                }else{
                	// The player chose a place to move                	 
                    if(Utilities.IsLegal(m_candidate, m_destinationTile.GetRow(), m_destinationTile.GetColumn())){
                        PlayThrough();
                        m_menuBar.EnableUndo();
                    }else{
                        // Report if the move attempted to be made was illegal
                        JOptionPane.showMessageDialog(m_menuBar, ILLEGAL_MOVE_ERROR, ILLEGAL_MOVE_HEADER, JOptionPane.PLAIN_MESSAGE);
                        
                        // Reset the necessary fields
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
            private final void ShowSourceErrorMessage();
        
        SYNOPSIS
            private final void ShowSourceErrorMessage();
        
            No parameters.
        
        DESCRIPTION
            This method shows a popup error message that differs depending on
            what the human player did wrong.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void ShowSourceErrorMessage(){
            if(m_sourceTile.IsEmpty()){
                JOptionPane.showMessageDialog(m_menuBar, EMPTY_TILE_ERROR, EMPTY_TILE_HEADER, JOptionPane.PLAIN_MESSAGE);
            }else if(m_sourceTile.IsOccupied() && m_candidate.GetColor().IsEnemy(m_currentPlayer.GetColor())){
                JOptionPane.showMessageDialog(m_menuBar, WRONG_COLOR_ERROR, WRONG_COLOR_HEADER, JOptionPane.PLAIN_MESSAGE);
            }else if(m_candidate.GetColor().IsAlly(m_currentPlayer.GetColor()) && !m_candidate.CanMove()){
                JOptionPane.showMessageDialog(m_menuBar, NO_LEGAL_MOVES_ERROR, NO_LEGAL_MOVES_HEADER, JOptionPane.PLAIN_MESSAGE);
            }
        }
        
        /**/
        /*
        NAME
            private final void ResetHumanFields();
        
        SYNOPSIS
            private final void ResetHumanFields();
        
            No parameters.
        
        DESCRIPTION
            This method resets the source and destination tiles
            as well as the legal move highlighting flag.
        
        RETURNS
            Nothing
        
        AUTHOR
            Code written by Ryan King, with idea taken from Amir Afghani
            https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
        */
        private final void ResetHumanFields(){
            m_shouldHighlightLegalMoves = false;
            m_sourceTile = null;
            m_destinationTile = null;
        }
        
        /**/
        /*
        NAME
            private final void PlayThrough();
        
        SYNOPSIS
            private final void PlayThrough();
        
            No parameters.
        
        DESCRIPTION
            This method makes the human player's move,
            updates the captured piece panels, and appends
            the move to the appropriate text area.
        
        RETURNS
            Nothing
        
        AUTHOR
            Code written by Ryan King, with idea taken from Amir Afghani
            https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
        */
        private final void PlayThrough(){
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
            
            // Update captured pieces
            UpdatePanel(m_humanPlayer, m_computerPlayer);
            
            // Record the new move in the correct text field
            final MoveTextArea AREA = (m_currentPlayer.IsWhite() ? m_whiteMoves : m_blackMoves);
            
            AREA.append(m_nextMove.toString());
            
            // Determine whether to reset the halfmove clock,
            // increment the fullmove clock if needed
            UpdateMoveClocks();
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
            boolean: True if both tiles are non-null, and false otherwise.
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
            return this.m_tile;
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
            return this.m_tile.toString();
        }
    }// End of GUITile class
    
    /**
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
            Code written by Ryan King with idea inspired by Amir Afghani
            https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
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
            Code written by Ryan King with idea inspired by Amir Afghani
            https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
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
        	int: m_board.PieceCount(): The number of pieces on the current board.
        
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
                	this.add(m_tiles[ROW][COLUMN]);
                }else{
                    AddWhiteBorderTile(ROW, COLUMN);
                }
            }
        }
        
        /**/
        /*
        NAME
            private final void AddWhiteBorderTile(final int a_row, final int a_column);
        
        SYNOPSIS
            private final void AddWhiteBorderTile(final int a_row, final int a_column);
        
            int a_row --------------> The row of the grid.
            
            int a_column -----------> The column of the grid.
        
        DESCRIPTION
            This method inserts a non-functional "border tile"
            on the outside of the chessboard with algebraic notation
            if it is not a corner. This varies depending upon which 
            side the human player chose.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King, with additional help taken from:
            https://stackoverflow.com/questions/8675038/increasing-decreasing-font-size-inside-textarea-using-jbutton
        */
        private final void AddWhiteBorderTile(final int a_row, final int a_column){
            // Set a center-aligned border tile with the proper letter or number
            final JPanel BORDER_TILE = new JPanel();
            BORDER_TILE.setBackground(this.BORDER_RED);
            BORDER_TILE.setSize(BoardUtilities.TILE_DIMENSION);
            BORDER_TILE.setAlignmentX(Component.CENTER_ALIGNMENT);
            BORDER_TILE.setAlignmentY(Component.CENTER_ALIGNMENT);
            
            // Add a letter
            if(m_board.PieceCount() > Utilities.ZERO){
                if(a_row == Utilities.ZERO || a_row == Utilities.NINE){
                    if(a_column != Utilities.ZERO && a_column != Utilities.NINE){
                        final JLabel LETTER = new JLabel();  
                        LETTER.setSize(BoardUtilities.TILE_DIMENSION);
                        LETTER.setText(BoardUtilities.ToAlgebraicColumn(a_column - Utilities.ONE).toUpperCase());
                        LETTER.setForeground(Color.WHITE);
                        Utilities.EnlargeFont(LETTER);
                    
                        LETTER.setAlignmentX(Component.CENTER_ALIGNMENT);
                        LETTER.setAlignmentY(Component.CENTER_ALIGNMENT);
                    
                        BORDER_TILE.add(LETTER);
                    }
                    // Add a number
                }else if(a_column == Utilities.ZERO || a_column == Utilities.NINE){
                    if(a_row != Utilities.ZERO && a_row != Utilities.NINE){
                        final JLabel NUMBER = new JLabel();
                        NUMBER.setSize(BoardUtilities.TILE_DIMENSION);
                        NUMBER.setText(BoardUtilities.ToAlgebraicRow(a_row - Utilities.ONE));
                        NUMBER.setForeground(Color.WHITE);
                        Utilities.EnlargeFont(NUMBER);
                    
                        NUMBER.setAlignmentX(Component.CENTER_ALIGNMENT);
                        NUMBER.setAlignmentY(Component.CENTER_ALIGNMENT);
                    
                        BORDER_TILE.add(NUMBER);
                    }
                }
            }
            this.add(BORDER_TILE);
        }
        
        /**/
        /*
        NAME
            private final void AddBlackBorderTile(final int a_row, final int a_column);
        
        SYNOPSIS
            private final void AddBlackBorderTile(final int a_row, final int a_column);
        
            int a_row --------------> The row of the grid.
            
            int a_column -----------> The column of the grid.
        
        DESCRIPTION
            This method inserts a non-functional "border tile"
            on the outside of the chessboard with algebraic notation
            if it is not a corner. This varies depending upon which 
            side the human player chose.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King, with additional help taken from:
            https://stackoverflow.com/questions/8675038/increasing-decreasing-font-size-inside-textarea-using-jbutton
        */
        private final void AddBlackBorderTile(final int a_row, final int a_column){
            // Set a center-aligned border tile with the proper letter or number
            final JPanel BORDER_TILE = new JPanel();
            BORDER_TILE.setBackground(this.BORDER_RED);
            BORDER_TILE.setSize(BoardUtilities.TILE_DIMENSION);
            BORDER_TILE.setAlignmentX(Component.CENTER_ALIGNMENT);
            BORDER_TILE.setAlignmentY(Component.CENTER_ALIGNMENT);
            
            // Add a letter
            if(m_board.PieceCount() > Utilities.ZERO){
                if(a_row == Utilities.ZERO || a_row == Utilities.NINE){
                    if(a_column != Utilities.ZERO && a_column != Utilities.NINE){
                        final JLabel LETTER = new JLabel();
                        LETTER.setSize(BoardUtilities.TILE_DIMENSION);
                        LETTER.setText(BoardUtilities.ToAlgebraicColumn(a_column - Utilities.ONE).toUpperCase());
                        LETTER.setForeground(Color.WHITE);
                        Utilities.EnlargeFont(LETTER);
                        BORDER_TILE.add(LETTER);
                    }
                    // Add a number
                }else if(a_column == Utilities.ZERO || a_column == Utilities.NINE){
                    if(a_row != Utilities.ZERO && a_row != Utilities.NINE){
                        final JLabel NUMBER = new JLabel();
                        NUMBER.setSize(BoardUtilities.TILE_DIMENSION);
                        NUMBER.setText(Integer.toString(Utilities.EIGHT - (a_row - Utilities.ONE)));
                        NUMBER.setForeground(Color.WHITE);
                        Utilities.EnlargeFont(NUMBER);
                        BORDER_TILE.add(NUMBER);
                    }
                }
            }
            this.add(BORDER_TILE);
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
 
                // Determine which type of tile to add
                if(BoardUtilities.HasValidCoordinates(ROW - Utilities.ONE, COLUMN - Utilities.ONE)){               	
                	this.m_tiles[Utilities.SEVEN - (ROW - Utilities.ONE)][Utilities.SEVEN - (COLUMN - Utilities.ONE)] = new GUITile(m_board.GetTile(ROW - Utilities.ONE, COLUMN - Utilities.ONE));
                	this.add(m_tiles[Utilities.SEVEN - (ROW - Utilities.ONE)][Utilities.SEVEN - (COLUMN - Utilities.ONE)]);
                }else{
                	AddBlackBorderTile(ROW, COLUMN);
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
            Code written by Ryan King with idea inspired by Amir Afghani
            https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
        */
        public final Board GetBoard(){
            return this.m_board;
        }
        
        /**/
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
            Code written by Ryan King with idea inspired by Amir Afghani
            https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
        */
        public final ChessColor WhoseTurnIsIt(){
            return this.m_board.WhoseTurnIsIt();
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
                return this.m_tiles[a_row][a_column];
            }else{
                return null;
            }
        }
    }// End of GUIBoard class
    
    /**
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
     * 
     * Inspired by the Table Menu Bar in Black Widow Chess by Amir Afghani,
     * with original code written by Ryan King.
     * https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
     */
    public final class DarkBlueMenuBar extends JMenuBar implements ActionListener{

    	private static final long serialVersionUID = Utilities.ONE_LONG;
    	
    	// File menu and associated buttons
    	private final JMenu m_file = new JMenu("File");
        private final JMenuItem m_newGame = new JMenuItem("New Game");
        private final JMenu m_loadGame = new JMenu("Load Game...");
        private final JMenuItem m_save = new JMenuItem("Save Game");
        private final JMenuItem m_stop = new JMenuItem("Stop Game");
        private final JMenuItem m_quit = new JMenuItem("Quit Game");
        private final JMenuItem m_undo = new JMenuItem("Undo");
        
        // Load Game submenu
        private final JMenuItem m_fromFile = new JMenuItem("From File");
        private final JMenuItem m_customFEN = new JMenuItem("From Custom FEN");
        
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
            Ryan King, with help taken from:
            https://stackoverflow.com/questions/4434894/java-swing-jmenu-mnemonic
        */
        private final void AddMnemonics(){
        	this.m_newGame.setMnemonic('N');
        	this.m_save.setMnemonic('S');
        	this.m_stop.setMnemonic('G');
        	this.m_quit.setMnemonic('Q');
        	this.m_helpMeMove.setMnemonic('H');
        	this.m_instructions.setMnemonic('I');
        	this.m_rules.setMnemonic('R');
        	this.m_fromFile.setMnemonic('F');
        	this.m_customFEN.setMnemonic('C');
        	this.m_undo.setMnemonic('U');
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
            this.m_newGame.addActionListener(this);
            this.m_save.addActionListener(this);
            this.m_stop.addActionListener(this);
            this.m_quit.addActionListener(this);
            this.m_helpMeMove.addActionListener(this);
            this.m_instructions.addActionListener(this);
            this.m_rules.addActionListener(this);
            this.m_fromFile.addActionListener(this);
            this.m_customFEN.addActionListener(this);
            this.m_undo.addActionListener(this);
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
            this.m_file.add(this.m_newGame);
            this.m_file.add(this.m_loadGame);
            this.m_file.add(this.m_undo);
            this.m_file.add(this.m_save);
            this.m_file.add(this.m_stop);
            this.m_file.add(this.m_quit);
        	this.add(this.m_file);
        	
        	this.m_loadGame.add(this.m_fromFile);
        	this.m_loadGame.add(this.m_customFEN);
        	
        	this.m_help.add(this.m_helpMeMove);
        	this.m_help.add(this.m_instructions);
        	this.m_help.add(this.m_rules);
        	this.add(this.m_help);
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
            This can only work if no game is currently in progress.
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
    		// Reset fields
            m_filename = null;
            m_enPassantTile = null;
                
            ClearGameFields();
                    
            // The game state must be normal at the start of a new game
            m_gameState = GameState.NORMAL;
          
            // Preserve the chosen colors
            m_originalHuman = m_humanColor;
            m_originalComputer = m_computerColor;

    		// Initialize the board and players
            m_board.SetBoard(Board.GetStartingPosition());
            
            // Seralize the board
            final String BOARD = Serialize();
            
            // Stop the game from starting if it is unplayable
            if(!GameUtilities.IsPlayable(BOARD, ChessColor.WHITE) || !GameUtilities.IsPlayable(BOARD, ChessColor.BLACK)){
                ResetAfterUnplayableGame();
                
                // Send the user an error message saying the game is unplayable
                JOptionPane.showMessageDialog(m_menuBar, UNPLAYABLE_ERROR, UNPLAYABLE_HEADER, JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Let the human player choose which side s/he wants to play as
            ChooseColor();
            
            InitializePlayers(m_board.GetBoard());
            m_board.DrawBoard();
    		
    		// Set the current player
    		m_currentPlayer = (m_board.WhoseTurnIsIt().IsWhite() ? m_white : m_black);
    		
    		// Enable these buttons on the menu
    		EnableLiveGameButtons();
    		
    		// Watch for any game-ending conditions (not relevant for starting a new game)
       		m_watcher.Observe();
        }
        
        /**/
        /*
        NAME
            private final void ResetAfterUnplayableGame();
        
        SYNOPSIS
            private final void ResetAfterUnplayableGame();
        
            No parameters.
        
        DESCRIPTION
            This method resets and clears all fields after
            a game is considered to be unplayable.
            Original color choices will be restored.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void ResetAfterUnplayableGame(){
            // Clear the board
            ClearGameFields();
            
            // Reset the chosen colors
            m_humanColor = m_originalHuman;
            m_computerColor = m_originalComputer;
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
            This can only work if no game is currently in progress.
            It resets all UI elements and serialization fields,
            validates the FEN string in the file,
            lets the human choose a color, 
            then validates that the game is playable
            before initializing and drawing the board
            then calling the observer.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void FromFileClicked(){
            
            // Clear the UI elements and reset necessary fields
            ClearGameFields();
            
    		// Parse the board
       		if(!Deserialize() && m_filename != null){ 
       		    if(!m_isParsable){
       		        JOptionPane.showMessageDialog(m_menuBar, FILE_ERROR, PARSER_ERROR, JOptionPane.ERROR_MESSAGE);
       		    }else{
       		        JOptionPane.showMessageDialog(m_menuBar, UNPLAYABLE_ERROR, UNPLAYABLE_HEADER, JOptionPane.ERROR_MESSAGE);
       		    }
       		    m_isParsable = false;
       		    m_filename = null;
       		    return;
       		}else if(m_filename == null){
       		    return;
       		}

            m_filename = null;
    		
        	// Allow the human player to choose his/her color
            ChooseColor();
            
            // Initialize both players
            InitializePlayers(m_board.GetBoard());
            
            // Enable these buttons because they will be needed
            EnableLiveGameButtons();
        	
    		// Assign the current player based on the turn information given
    		m_currentPlayer = (m_board.WhoseTurnIsIt().IsWhite() ? m_white : m_black);
    		
    		m_board.DrawBoard();
    		
    		// Show a "Check!" message if necessary because the observer will not
    		CheckStatusAndResume();
    		
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
            All files are saved in the "Serial" folder located inside this project.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void SaveClicked(){
            // Ask if the user wants to save the game
            final int SAVE = JOptionPane.showConfirmDialog(this, SAVE_GAME_MESSAGE, SAVE_GAME, JOptionPane.YES_NO_OPTION);
            
            if(SAVE != JOptionPane.YES_OPTION){
                return;
            }
            
        	// Turn the game information into a FEN string
        	final String FEN = Serialize();
        	
        	// Get a unique filename with the timestamp
        	final String FILE = FILE_PREFIX + GetDate() + FILE_EXTENSION;
        	
        	try{
        		// Write the FEN string to a file
        		WriteFile(FILE, FEN);
        		
        		// Disable these buttons as they are not usable when not playing a game
        		DisableLiveGameButtons();
        		
        		// Reset all fields
        		ClearGameFields();
        		
        		// Inform the user of the filename to find later
        		JOptionPane.showMessageDialog(m_instance, SAVED_FIRST + FILE + SAVED_LAST, SUCCESSFUL_SAVE, JOptionPane.PLAIN_MESSAGE);
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
        
        /**/
        /*
        NAME
            private final void ClearGameFields();
        
        SYNOPSIS
            private final void ClearGameFields();
        
            No parameters.
        
        DESCRIPTION
            This method clears all the game fields
            including text areas, captured piece panels,
            the non-swapping Boolean field, the board,
            the move clocks, the tile fields,
            the piece fields, and the game state.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void ClearGameFields(){
            // Clear the move history textboxes
            m_whiteMoves.setText(Utilities.EMPTY_STRING);
            m_blackMoves.setText(Utilities.EMPTY_STRING);
            
            // Clear the captured piece panels
            m_whitePieces.Clear();
            m_blackPieces.Clear();
            
            m_isPreviouslySavedGame = true;
            
            // Clear the board
            m_board.SetBoard(Board.GetEmptyBoard());                           
            m_board.DrawBoard();
                
            // Reset the move clocks
            m_currentHalfmoves = Utilities.ZERO;
            m_fullmoves = Utilities.ONE;

            // Reset the game history
            m_positions.clear();
            m_gameHistory.clear();
                
            // Reset mouse-driven fields
            m_sourceTile = null;
            m_destinationTile = null;

            m_candidate = null;
            m_victim = null;
            m_nextMove = null;
            
            // Reset the game state
            m_gameState = GameState.EMPTY;
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
            to allow the user to input a custom FEN string
            after the Load Game... -> From Custom FEN... button is clicked.
            This can only work if no game is currently in progress.
            It resets all UI elements and serialization fields,
            validates the FEN string,
            lets the human choose a color, 
            then validates that the game is playable
            before initializing and drawing the board
            then calling the observer.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void CustomFENClicked(){         
            // Set up a field which will determine what error message to display, if any
            boolean notNull = false;
            
            // Preserve the colors
            m_originalHuman = m_humanColor;
            m_originalComputer = m_computerColor;
            
        	try{
        	    // Grab the FEN string from the user
        		final String FEN = JOptionPane.showInputDialog(this, CUSTOM_FEN, LOAD_GAME, JOptionPane.PLAIN_MESSAGE);
        		
        		if(FEN != null){
        		    notNull = true;
        		}
        		
        		// This was an invalid FEN string
        		if(notNull && !GameUtilities.IsValidFEN(FEN.trim())){
        		    JOptionPane.showMessageDialog(m_menuBar, FEN_ERROR, PARSER_ERROR, JOptionPane.ERROR_MESSAGE);
        		    return;
        		}else if(!notNull){
        		    return;
        		}

                // This FEN string was valid but the game it provided is unplayable
                if(!GameUtilities.IsPlayable(FEN.trim(), m_humanColor)){
                    JOptionPane.showMessageDialog(m_menuBar, UNPLAYABLE_ERROR, UNPLAYABLE_HEADER, JOptionPane.ERROR_MESSAGE);
                    m_humanColor = m_originalHuman;
                    m_computerColor = m_originalComputer;
                    return;
                }
                
                // Allow the human player to choose his/her color
                ChooseColor();
                
                // Parse the string in the real environment
        		ParseCustomFEN(FEN);

        		// Show a check message and remove castling moves if the
        		// next player is in check
        		// Show a message if the player is human
        		CheckStatusAndResume();
        		
        		// Call the observer
                m_watcher.Observe();
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
        
        /**/
        /*
        NAME
            private final void ParseCustomFEN(final String a_FEN);
        
        SYNOPSIS
            private final void ParseCustomFEN(final String a_FEN);
        
            String a_FEN -------------> A custom FEN string typed in by the user.
        
        DESCRIPTION
            This method parses a custom FEN string typed in by the user.
            It will warn the user if the string is invalid or results in
            an unplayable game. Otherwise, a new game will resume and the
            user will choose his/her color like normal.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void ParseCustomFEN(final String a_FEN){ 
            ClearGameFields();
            
            // Parse the string, trimming any trailing whitespace away
            try{
                ParseFEN(a_FEN.trim(), true);
            }catch(Exception e){
                e.printStackTrace();
            }
            
            // Set up the board
            m_board.SetBoard(m_board.GetBoard());
            m_board.DrawBoard();
            
            // Initialize both players
            InitializePlayers(m_board.GetBoard());
            
            // Turn on the buttons that are only accessible during the game
            EnableLiveGameButtons();
            
            // Initialize the current player
            m_currentPlayer = (m_board.WhoseTurnIsIt().IsWhite() ? m_white : m_black);
        }
        
        /**/
        /*
        NAME
            private final void StopClicked();
        
        SYNOPSIS
            private final void StopClicked();
        
            No parameters.
        
        DESCRIPTION
            This method terminates a game
            after the Stop button is clicked.
            It asks the player if they really
            want to quit before clearing the board for a new game.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void StopClicked(){
            // Determine if a game is currently in progress
            if(m_gameState == GameState.NORMAL || m_gameState == GameState.CHECK){
                final int STOP = JOptionPane.showConfirmDialog(this, STOP_MESSAGE, STOP_HEADER, JOptionPane.YES_NO_OPTION);
                
                // Quitting in the middle of a game is resigning and counts as a loss
                if(STOP == JOptionPane.YES_OPTION){
                    ClearGameFields();
                    DisableLiveGameButtons();
                    
                    // This is resignation which counts as a loss
                    if(m_humanColor.IsWhite()){
                        JOptionPane.showMessageDialog(this, BLACK_RESIGNATION, GAME_OVER, JOptionPane.ERROR_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(this, WHITE_RESIGNATION, GAME_OVER, JOptionPane.ERROR_MESSAGE);
                    }
                }else{
                    return;
                }
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
            final int QUIT;
            
        	// Make sure the game is no longer playable, or show a warning message 
            if(CanGameContinue()){
            	QUIT = JOptionPane.showConfirmDialog(this, QUIT_IN_PROGRESS, QUIT_HEADER, JOptionPane.YES_NO_OPTION);
            }else{
                QUIT = JOptionPane.showConfirmDialog(this, QUIT_NO_GAME, QUIT_HEADER, JOptionPane.YES_NO_OPTION);
            }    
            	
            // Quitting in the middle of a game is resigning and counts as a loss
            if(QUIT == JOptionPane.YES_OPTION){
                if(CanGameContinue()){
                    if(m_humanColor.IsWhite()){
            	        JOptionPane.showMessageDialog(this, BLACK_RESIGNATION, GAME_OVER, JOptionPane.ERROR_MESSAGE);
            	    }else{
            		    JOptionPane.showMessageDialog(this, WHITE_RESIGNATION, GAME_OVER, JOptionPane.ERROR_MESSAGE);
            	    }
                }
            }else{
                return;
            }
            
            // Terminate the program
            System.exit(Utilities.ZERO);
        }
        
        /**/
        /*
        NAME
            private final boolean CanGameContinue();
        
        SYNOPSIS
            private final boolean CanGameContinue();
        
            No parameters.
        
        DESCRIPTION
            This method determines if the current game in progress
            can continue with normal play.
        
        RETURNS
            boolean: True if the game can continue or false otherwise.
            One of these two options will always occur.
        
        AUTHOR
            Ryan King
        */
        private final boolean CanGameContinue(){
            return (m_gameState == GameState.NORMAL || m_gameState == GameState.CHECK);
        }
        
        /**/
        /*
        NAME
            private final void RulesOrInstructionsClicked(final boolean a_isRules);
        
        SYNOPSIS
            private final void RulesOrInstructionsClicked(final boolean a_isRules);
        
            boolean a_isRules -------> If the user wants to see the rules of chess.
        
        DESCRIPTION
            This method shows either the chess engine instruction manual
            after the Instructions button is clicked or the FIDE
            laws of chess after the Rules or Instructions button is clicked.
            It searches the project's working directory and
            opens a PDF containing either the FIDE laws of chess            
            or the instructions on how to use the Dark Blue chess engine.    
        
        RETURNS
            Nothing
        
        AUTHOR
            MK Yong, "How to open a PDF File in Java",
            https://mkyong.com/java/how-to-open-a-pdf-file-in-java/
            with slight modifications by Ryan King
        */
        private final void RulesOrInstructionsClicked(final boolean a_isRules){
            try{      
                final File FILE;
                final String WHICH;
                
                // Attempt to find and open the instruction PDF file stored locally in the project
                if(a_isRules){
                    FILE = new File("src/com/DarkBlue/LawsOfChess.pdf");
                    WHICH = "FIDE laws of chess";
                }else{
                    FILE = new File("src/com/DarkBlue/Instructions.pdf");
                    WHICH = "instruction manual";
                }
                
                if(FILE.exists()){
                    // Open the file with the AWT desktop utility if usable
                    if(Desktop.isDesktopSupported()){
                        Desktop.getDesktop().open(FILE);
                    }else{
                        System.err.println("AWT Desktop is not supported.");
                    }

                }else{
                    System.err.println("The document containing the " + WHICH + " does not exist.");
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
        	// Disable the File and Help menus
            DisableTabs();
        	
        	// Construct a new "Thinking..." dialog and set it up
    		SpawnThinkingDialog();

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
        				final String MESSAGE = ConstructHelpMessage(BEST_MOVE);
            		
        				// Display the message
        				JOptionPane.showMessageDialog(m_menuBar, MESSAGE, HELP, JOptionPane.INFORMATION_MESSAGE);
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
            private final String ConstructHelpMessage(final Move a_bestMove);
        
        SYNOPSIS
            private final String ConstructHelpMessage(final Move a_bestMove);
        
            Move a_bestMove ---------> The best move the computer found for the human.
        
        DESCRIPTION
            This method constructs a dynamic string that contains
            a message telling a player what move s/he should make
            given the fields in the move that was passed in.
        
        RETURNS
            String message: A message for the user suggesting an ideal move.
        
        AUTHOR
            Ryan King
        */
        private final String ConstructHelpMessage(final Move a_bestMove){
            String message = I_RECOMMEND;
            
            if(a_bestMove.IsRegular()){
                message += MOVE_YOUR + a_bestMove.GetPiece().GetPieceType().toString().toLowerCase() + FROM + BoardUtilities.ToAlgebraic(a_bestMove.GetOldRow(), a_bestMove.GetOldColumn());                
                message += TO + BoardUtilities.ToAlgebraic(a_bestMove.GetNewRow(), a_bestMove.GetNewColumn()) + PERIOD;
            }else if(a_bestMove.IsAttacking()){
                message += USE_YOUR + a_bestMove.GetPiece().GetPieceType().toString().toLowerCase() + ON + BoardUtilities.ToAlgebraic(a_bestMove.GetOldRow(), a_bestMove.GetOldColumn());
                message += TO_CAPTURE + a_bestMove.GetVictim().GetPieceType().toString().toLowerCase();
                message += ON + BoardUtilities.ToAlgebraic(a_bestMove.GetNewRow(), a_bestMove.GetNewColumn()) + PERIOD;
            }else if(a_bestMove.IsCastling()){
                message += PERFORM_A;
            
                if(a_bestMove.GetNewColumn() < a_bestMove.GetOldColumn()){
                    message += PieceType.QUEEN_STRING.toLowerCase();
                }else{
                    message += PieceType.KING_STRING.toLowerCase();
                }
            
                message += SIDE_CASTLE;
            }else{
                message += PERFORM_EN_PASSANT + BoardUtilities.ToAlgebraic(a_bestMove.GetOldRow(), a_bestMove.GetOldColumn()) + PERIOD;
            }
            
            return message;
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
            }else if(a_event.getSource() == this.m_customFEN){
            	// Enter a custom FEN string that's not from a file
            	CustomFENClicked();
        	}else if(a_event.getSource() == this.m_quit){
        		// Quit a game without saving
            	QuitClicked();
        	}else if(a_event.getSource() == this.m_save){
                // Save a game as a FEN file
                SaveClicked();
            }else if(a_event.getSource() == this.m_helpMeMove){
        		// Ask the computer for help with moving
        		HelpMeMoveClicked();
            }else if(a_event.getSource() == this.m_instructions){
            	// Show instructions on how to use this engine
            	RulesOrInstructionsClicked(false);
            }else if(a_event.getSource() == this.m_rules){
            	// Show the rules of chess as per the regulations set forth by FIDE
                RulesOrInstructionsClicked(true);
            }else if(a_event.getSource() == this.m_stop){
                // Stop the current game in progress without saving or terminating the program
                StopClicked();
            }else if(a_event.getSource() == this.m_undo){
            	// Undo a move if the human player thinks s/he made a mistake
            	UndoClicked();
            }
        	
        	// Draw the board
        	SwingUtilities.invokeLater(new Runnable(){
        	    /**/
                /*
                NAME
                    public final void run();
                
                SYNOPSIS
                    public final void run();
                
                    No parameters.
                
                DESCRIPTION
                    This method draws the board on the Event Dispatch Thread
                    once everything else is done executing.
                
                RETURNS
                    Nothing
                
                AUTHOR
                    Adapted from Black Widow Chess by Amir Afghani,
                    https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/gui/Table.java
                */
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
            m_menuBar.DisableStop();
            
            m_newGame.setEnabled(true);
            m_loadGame.setEnabled(true);
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
            m_menuBar.EnableStop();
            
            m_newGame.setEnabled(false);
            m_loadGame.setEnabled(false);
        }
        
        /**/
        /*
        NAME
            private final void EnableStop();
        
        SYNOPSIS
            private final void EnableStop();
        
            No parameters.
        
        DESCRIPTION
            This method enables the "Stop Game" option
            found on the DarkBlueMenuBar.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void EnableStop(){
            m_stop.setEnabled(true);
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
            private final void DisableStop();
        
        SYNOPSIS
            private final void DisableStop();
        
            No parameters.
        
        DESCRIPTION
            This method disables the "Stop Game" option
            found on the DarkBlueMenuBar.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void DisableStop(){
            m_stop.setEnabled(false);
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
    
    /**
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
     * 
     * Inspired by the MoveHistoryPanel from Black Widow Chess by Amir Afghani,
     * but this code was entirely written by Ryan King unless indicated otherwise.
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
    }// End of MoveTextArea class
	
    /**
     * This class contains methods that check the status of the board
     * and see if the game is over or not.
     * 
     * IsGameOver() will check to see if the game is over by determining
     * if one player is in checkmate or if there is a draw.
     * 
     * Observe() will also search for check, checkmate, stalemate, and
     * other draw conditions, but will notify the player with a JOptionPane message dialog.
     * No dialog will pop up if the human places the computer into check.
     * 
     * Additional methods exist to announce different game conditions such as check, checkmate, and any draw condition.
     * 
     * This class was inspired by the use of the Observer class in Black Widow Chess by Amir Afghani,
     * but this code is was entirely designed and written by Ryan King unless indicated otherwise.
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
			
			// Refresh all moves for both players
            RefreshPlayers();
			
			// Record the position of the board
			RecordBoard();
				
			// Determine who moved last
			final ChessColor PREVIOUS = m_currentPlayer.GetColor();
			final Player OTHER = (PREVIOUS.IsWhite() ? m_black : m_white);
			
			// See if the player moved a pawn to get promoted
			CheckForPromotions(m_currentPlayer);
			
			// Refresh the moves again if any pawns got promoted
			RefreshPlayers();

			// Adjust the castling privileges of both players if necessary
			AdjustCastlingRights(m_currentPlayer);
			AdjustCastlingRights(OTHER);
			
			// Refresh the moves again if castling rights have changed
			RefreshPlayers();
			
			// Determine the right text area based on who moved
			final MoveTextArea AREA = (PREVIOUS.IsWhite() ? m_whiteMoves : m_blackMoves);
			
			// Get a serialized board
			final String SERIAL = Serialize();

			// Save the FEN for undoing the move
			m_gameHistory.push(SERIAL);
			
			// Determine the state of the game
			m_gameState = GameUtilities.EvaluateGameState(OTHER, m_currentPlayer, m_board.GetBoard(), m_currentHalfmoves, m_positions);
			
			// Determine if a =Q/=R/=B/=N needs to be appended
			AppendPromotion(AREA);
						
			// See if the game is over
			if(!CanContinue(OTHER, AREA)){
			    return;
			}
			
			// Swap the players
			SwapPlayers(PREVIOUS);
				
			// Allow the computer to play if it is its turn
			if(m_currentPlayer.IsComputer()){
				ComputerPlay();
			}
			
			// Adjust the undo button dynamically based on the game history
			AdjustUndoButton();
		}
	}
	
	/**/
    /*
    NAME
        private final void AdjustUndoButton();
    
    SYNOPSIS
        private final void AdjustUndoButton();
    
        No parameters.
    
    DESCRIPTION
        This method adjusts if the "Undo" menu button should be
        enabled depending on how many moves there are in the game history.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	private final void AdjustUndoButton(){
	    // Check if the game is still playable
	    if(m_gameState == GameState.NORMAL || m_gameState == GameState.CHECK){
            if(m_gameHistory.size() < Utilities.THREE){
                m_menuBar.DisableUndo();
            }else{
                m_menuBar.EnableUndo();
            }
        }
	}
	
	/**/
    /*
    NAME
        private final boolean CanContinue(final Player a_other, final MoveTextArea a_area);
    
    SYNOPSIS
        private final boolean CanContinue(final Player a_other, final MoveTextArea a_area);
    
        Player a_other -----------> The opponent.
        
        MoveTextArea a_area ------> The opponent's text area.
    
    DESCRIPTION
        This method announces any game conditions
        that may result in the end of the game
        and applies the correct suffixes and newline
        characters to the proper text area.
        The only announced condition that does not lead
        to the game ending is check.
        If the state is anything but check or normal gameplay,
        the method returns false. Otherwise, it returns true.
    
    RETURNS
        boolean: True if the game state is check or normal and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
	private final boolean CanContinue(final Player a_other, final MoveTextArea a_area){
	    if(m_gameState == GameState.CHECKMATE){
            // One side won by checkmate
            AnnounceCheckmate(BoardUtilities.Reverse(a_other.GetColor()));
            return false;
        }else if(m_gameState == GameState.STALEMATE){
            // Draw by stalemate
            AnnounceStalemate(a_area);
            return false;
        }else if(m_gameState == GameState.INSUFFICIENT_MATERIAL){
            // Draw by insufficient material
            AnnounceInsufficientMaterial(a_area);
            return false;
        }else if(m_gameState == GameState.FIFTY_MOVE_RULE){
            // Draw by fifty-move rule
            AnnounceFiftyMoveRule(a_area);
            return false;
        }else if(m_gameState == GameState.THREEFOLD_REPETITION){
            // Draw by threefold repetition
            AnnounceThreefoldRepetition(a_area);
            return false;
        }else if(m_gameState == GameState.CHECK){
            // A player is in check
            AnnounceCheck(a_area, a_other);
            return true;
        }else if(m_gameState == GameState.NORMAL && !m_isPreviouslySavedGame){
            // Nothing significant happened
            a_area.append(Character.toString(Utilities.NEWLINE));
            return true;
        }
	    
	    return true;
	}
	
	/**/
    /*
    NAME
        private final void AnnounceCheckmate(final ChessColor a_winner);
    
    SYNOPSIS
        private final void AnnounceCheckmate(final ChessColor a_winner);
    
        ChessColor a_winner ----------> The winner of the game.
    
    DESCRIPTION
        This method appends a "#\n1-0" to white's text area
        or a "#\n0-1" to black's text area and announces 
        that side has won by checkmate.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	private final void AnnounceCheckmate(final ChessColor a_winner){
	    // Idiot proofing
	    if(a_winner == null){
	        return;
	    }
	    
	    // The winner won by checkmate
        m_menuBar.DisableLiveGameButtons();
        if(a_winner.IsWhite()){
            if(!m_whiteMoves.getText().equals(Utilities.EMPTY_STRING)){
                m_whiteMoves.append(WHITE_CHECKMATE);
            }
            JOptionPane.showMessageDialog(m_menuBar, WHITE_CHECKMATE_MESSAGE, GAME_OVER, JOptionPane.ERROR_MESSAGE);
        }else{
            if(!m_blackMoves.getText().equals(Utilities.EMPTY_STRING)){
                m_blackMoves.append(BLACK_CHECKMATE);
            }
            JOptionPane.showMessageDialog(m_menuBar, BLACK_CHECKMATE_MESSAGE, GAME_OVER, JOptionPane.ERROR_MESSAGE);
        }
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
	    // Idiot proofing
	    if(a_area == null){
            return;
        }
	    
	    // Draw by stalemate
        m_menuBar.DisableLiveGameButtons();
        if(!a_area.getText().equals(Utilities.EMPTY_STRING)){
            a_area.append(DRAW);
        }
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
	    // Idiot proofing
	    if(a_area == null){
            return;
        }
	    
	    // Draw by insufficient material
        m_menuBar.DisableLiveGameButtons();
        if(!a_area.getText().equals(Utilities.EMPTY_STRING)){
            a_area.append(DRAW);
        }
        
        // Announce the end of the game
        JOptionPane.showMessageDialog(m_menuBar, INSUFFICIENT_MATERIAL_MESSAGE, GAME_OVER, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**/
    /*
    NAME
        private final void AnnounceFiftyMoveRule(final MoveTextArea a_area);
    
    SYNOPSIS
        private final void AnnounceFiftyMoveRule(final MoveTextArea a_area);
    
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
	    // Idiot proofing
	    if(a_area == null){
            return;
        }
	    
	    // Draw by the fifty-move rule
        m_menuBar.DisableLiveGameButtons();
        if(!a_area.getText().equals(Utilities.EMPTY_STRING)){
            a_area.append(DRAW);
        }
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
	    // Idiot proofing
	    if(a_area == null){
            return;
        }
	    
	    // Draw by threefold repetition
        m_menuBar.DisableLiveGameButtons();
        if(!a_area.getText().equals(Utilities.EMPTY_STRING)){
            a_area.append(DRAW);
        }
        
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
	    // Idiot proofing
	    if(a_area == null || a_other == null){
            return;
        }
	    
	    // The next player is in check
	    if(!a_area.getText().equals(Utilities.EMPTY_STRING)){
	        a_area.append(CHECK);
	    }
	    
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
	    // Idiot proofing
	    if(a_previous == null){
            return;
        }
	    
	    // Don't swap if the game was just taken from a serialization string
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
	    // Idiot proofing
	    if(a_area == null || m_promotionString == null){
            return;
        }
	    
	    // Append "=Q/R/B/N" if necessary to the proper area
	    if(m_promotionString != null){
            a_area.append(m_promotionString);
            m_promotionString = null;
        }
	}
	
	/**/
    /*
    NAME
        private final void AdjustCastlingRights(final Player a_player);
    
    SYNOPSIS
        private final void AdjustCastlingRights(final Player a_player);
    
        Player a_other --------> The player.
    
    DESCRIPTION
        This method adjusts the castling rights of the player's king.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
	private final void AdjustCastlingRights(final Player a_player){
	    // Idiot proofing
	    if(a_player != null && !a_player.GetKing().HasMoved() && a_player.GetKing().IsInOriginalSpot()){
	        // Gather all the fields of the opponent's king and his associated tile
            final King KING = a_player.GetKing();
            final int KING_ROW = KING.GetCurrentRow();
            final int KING_COLUMN = KING.GetCurrentColumn();
            final ChessColor KING_TILE_COLOR = m_board.GetBoard().GetTile(KING_ROW, KING_COLUMN).GetColor();

            // Find the true status of castling rights
            final boolean KINGSIDE = KING.HasKingsideCastlingRook(m_board.GetBoard());
            final boolean QUEENSIDE = KING.HasQueensideCastlingRook(m_board.GetBoard());
            
            // Make a new king if anything is different
            if(KINGSIDE != KING.CanKingsideCastle() || QUEENSIDE != KING.CanQueensideCastle()){
                m_board.GetBoard().GetBoard()[KING_ROW][KING_COLUMN] = new Tile(KING_TILE_COLOR, KING_ROW, KING_COLUMN, new King(a_player.GetColor(), KING_ROW, KING_COLUMN, KINGSIDE, QUEENSIDE));
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
	    final String BOARD = m_board.GetBoard().toString().split(Character.toString(Utilities.SPACE))[Utilities.ZERO];
	    
	    // Place it into a new entry if one doesn't exist yet or increment an existing entry
        if(!m_positions.containsKey(BOARD)){
            m_positions.put(BOARD, Utilities.ONE);
        }else{
            m_positions.replace(BOARD, m_positions.get(BOARD) + Utilities.ONE);
        }
	}
}