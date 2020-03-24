package com.DarkBlue.GUI;

import com.DarkBlue.Board.Board;
import com.DarkBlue.Board.Tile;
import com.DarkBlue.Board.Board.BoardBuilder;
import com.DarkBlue.Game.GameState;
import com.DarkBlue.Move.*;
import com.DarkBlue.Piece.*;
import com.DarkBlue.Player.*;
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
    
    public static final String TITLE = "Dark Blue";
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
    private static boolean m_shouldHighlightLegalMoves = false;
    
    private static boolean m_isPreviouslySavedGame = false;
    private static boolean m_canWhiteKingsideCastle = false;
    private static boolean m_canWhiteQueensideCastle = false;
    private static boolean m_canBlackKingsideCastle = false;
    private static boolean m_canBlackQueensideCastle = false;
    private static String m_enPassantTile = null;
    private static String m_promotionString = null;
    private static boolean m_isRestarted = false;
    
    private static GameState m_gameState = GameState.EMPTY;
    
    /* My components */
    
    // The visual representation of the board
    private GUIBoard m_board;

    private GameWatcher m_watcher = new GameWatcher();
    
    // The alias for the current player, used to reduce code
    private Player m_currentPlayer;
        
    // The history of the game from move to move
    private final Stack<String> m_gameHistory;
    
    private final HashMap<String, Integer> m_positions;

    private static ChessColor m_humanColor, m_computerColor;
    
    // The players described by color
    private static Player m_white;
    
    private static Player m_black;
    
    private static Player m_humanPlayer, m_computerPlayer;
    
    private static int m_originalRow;
    private static int m_originalColumn;

    // The number of moves made; useful for determining draws
    private int m_currentHalfmoves = Utilities.ZERO;
    
    private int m_fullmoves = Utilities.ONE;
        
    // Search depth for the AI
    private static int m_depth = Utilities.THREE;
       
    // The integer that keeps track of the button
    private int m_buttonInt;
        
    // The piece to be moved
    private Piece m_candidate;
    
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
    
    
    /* Swing components */    
    private final DarkBlueMenuBar m_menuBar = new DarkBlueMenuBar();
    private static MoveTextArea m_whiteMoves;
    private static MoveTextArea m_blackMoves;
    private static JScrollPane m_whiteScroll;
    private static JScrollPane m_blackScroll;
    private final CapturedPiecePanel m_whitePieces = new CapturedPiecePanel();
    private final CapturedPiecePanel m_blackPieces = new CapturedPiecePanel();
    
    private final JPanel m_top = new JPanel();
    private final JPanel m_left = new JPanel();
    private final JPanel m_right = new JPanel();
    private final JPanel m_bottom = new JPanel();
    
    // These will hold the JScrollPanes and their labels
    private final JPanel m_whiteInner = new JPanel();
    private final JPanel m_blackInner = new JPanel();
    
    private JOptionPane m_optionPane;
	private JDialog m_dialog;

    // Contains the GUIBoard object, as well as two column bumpers on top and bottom
    private final JPanel m_boardPanel = new JPanel();

    private final JLabel m_whiteLabel = new JLabel("White");
    private final JLabel m_blackLabel = new JLabel("Black");
    
    // Allows the computer to move without stopping the EDT
    private SwingWorker<Move, Void> m_worker;
    
    private boolean m_fileNotChosen = true;
    private String m_filename = "";
    
    /**/
    /*
    NAME
        private DarkBlue();
    
    SYNOPSIS
        private DarkBlue();
    
        No parameters.
    
    DESCRIPTION
        This constructor creates a new Dark Blue
        chess engine, ready to play. It calls the
        TryPlay() method to initialize color selection
        and start the game.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King, with additional help on JScrollPanes taken from:
        https://stackoverflow.com/questions/45558095/jscrollpane-not-scrolling-in-jtextarea
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
        
        // Set scrollbar policies
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
    	final Calendar now = Calendar.getInstance();
    	
    	final int year = now.get(Calendar.YEAR);
    	final int month = now.get(Calendar.MONTH) + Utilities.ONE;
    	final int day = now.get(Calendar.DAY_OF_MONTH);
    	final int hour = now.get(Calendar.HOUR_OF_DAY);
    	final int minute = now.get(Calendar.MINUTE);
    	final int second = now.get(Calendar.SECOND);
    	
    	return Integer.toString(year)
    			+ (month < Utilities.TEN ? Integer.toString(Utilities.ZERO) : "") + Integer.toString(month)
    			+ (day < Utilities.TEN ? Integer.toString(Utilities.ZERO) : "") + Integer.toString(day)
    			+ (hour < Utilities.TEN ? Integer.toString(Utilities.ZERO) : "") + Integer.toString(hour) 
    			+ (minute < Utilities.TEN ? Integer.toString(Utilities.ZERO) : "") + Integer.toString(minute)
    			+ (second < Utilities.TEN ? Integer.toString(Utilities.ZERO) : "") + Integer.toString(second);
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
            for(Move move : this.m_candidate.GetCurrentLegalMoves()){
            	if(m_humanPlayer.IsWhite()){
            		this.m_board.GetTile(move.GetNewRow() + Utilities.ONE, move.GetNewColumn() + Utilities.ONE).LightUp();
            	}else{
            		this.m_board.GetTile(Utilities.SEVEN - (move.GetNewRow()), Utilities.SEVEN - (move.GetNewColumn())).LightUp();
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
            for(Move move : this.m_candidate.GetCurrentLegalMoves()){
            	if(m_humanPlayer.IsWhite()){
            		this.m_board.GetTile(move.GetNewRow() + Utilities.ONE, move.GetNewColumn() + Utilities.ONE).Revert();
            	}else{
            		this.m_board.GetTile(Utilities.SEVEN - (move.GetNewRow()), Utilities.SEVEN - (move.GetNewColumn())).Revert();
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
        
        //m_bottom.setLayout(new BoxLayout(m_bottom, BoxLayout.PAGE_AXIS));
        //m_bottom.setSize(new Dimension(100, 120));
        //m_bottom.add(m_blackPieces);
        //m_bottom.add(m_whitePieces);
        
        a_pane.add(m_left, BorderLayout.LINE_START);
        
        a_pane.add(m_right, BorderLayout.LINE_END);
        
        a_pane.add(m_board, BorderLayout.CENTER);
        
        //a_pane.add(m_bottom, BorderLayout.PAGE_END);
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
        ChessColor m_humanColor: The color not chosen by the human player.
    
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
    
    private final int GetPromotedPawnIndex(final Player a_player, final ArrayList<Piece> a_activePiecesCopy){
    	final Pawn pawn;
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
        It sets the m_gameState variable to one of the following:
        
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
        This need not be consecutive. The game ends in a draw.

        GameState.NORMAL: The game proceeds as normal.
    
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
        True if the game is drawn by any of the preceding non-stalemate conditions
        and false otherwise. One of these two options will always occur.
    
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
        True if the game is drawn by any of the preceding non-stalemate conditions
        and false otherwise. One of these two options will always occur.
    
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
        True if the game is drawn by any of the preceding non-stalemate conditions
        and false otherwise. One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final boolean IsDrawByThreefoldRepetition(){
        // Make the hashmap iterable
        final Iterator<String> positionIteration = m_positions.keySet().iterator();
        
        // Look through every position in the hash
        while(positionIteration.hasNext()){
        	String current = positionIteration.next();
        	if(m_positions.get(current) == Utilities.THREE){
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
    		final JFileChooser chooser = new JFileChooser();
    		// path: /home/ryan/git/DarkBlue/DarkBlue/src/com/DarkBlue/Serial/whatever   				
    				
    		chooser.setFileFilter(new FileNameExtensionFilter("FEN files", "fen"));
    	
    		final int choice = chooser.showOpenDialog(null);
    		
    		if(choice == JFileChooser.APPROVE_OPTION){
    		    // A file was chosen.
    		    // Set the flag so that an error message will not show
                m_filename = chooser.getSelectedFile().getName();
    		    
    		    // Open and read the file
    			final InputStreamReader reader = new InputStreamReader(new FileInputStream(chooser.getSelectedFile()), "UTF-8");  			
    			    	    		
				final Scanner scanner = new Scanner(reader);
    	    		
    			final String line = scanner.nextLine();
    	    			
    			reader.close();
    					
    			scanner.close();
    	    	
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
        // parts will hold the six parts of the FEN string
    	final String[] parts = a_FENString.split(" ");
		
    	// builder will be a template for building the new board
		final BoardBuilder builder = new BoardBuilder();
		
		// parts[0] represents the configuration of the board
		final String[] board = parts[Utilities.ZERO].split("/");	
		
		// Determine if both sides have one and only one king
		boolean whiteKingFound = false, blackKingFound = false;
		for(String str : board){
			if(str.contains("K") && !whiteKingFound){
				whiteKingFound = true;
			}else if(whiteKingFound && str.contains("K")){
				m_menuBar.DisableSave();
				m_menuBar.DisableUndo();
				throw new Exception("Duplicate white king found.");
			}
			if(str.contains("k") && !blackKingFound){
				blackKingFound = true;
			}else if(blackKingFound && str.contains("k")){
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
		
		// parts[1] determines whose turn it is
		if(parts[Utilities.ONE].equalsIgnoreCase("w")){
			builder.SetWhoseTurn(ChessColor.WHITE);
		}else if(parts[Utilities.ONE].equalsIgnoreCase("b")){
			builder.SetWhoseTurn(ChessColor.BLACK);
		}else{
			throw new Exception("Improper file format.");
		}
		
		// parts[2] determines which sides can castle and where,
		// which need not be feasible on the current turn
		if(parts[Utilities.TWO].equals("-")){
			m_canWhiteKingsideCastle = false;
			m_canWhiteQueensideCastle = false;
			m_canBlackKingsideCastle = false;
			m_canBlackQueensideCastle = false;
		}else{
			
			if(parts[Utilities.TWO].contains("K")){
				m_canWhiteKingsideCastle = true;
			}else{
				m_canWhiteKingsideCastle = false;
			}
		
			if(parts[Utilities.TWO].contains("Q")){
				m_canWhiteQueensideCastle = true;
			}else{
				m_canWhiteQueensideCastle = false;
			}
		
			if(parts[Utilities.TWO].contains("k")){
				m_canBlackKingsideCastle = true;
			}else{
				m_canBlackKingsideCastle = false;
			}
		
			if(parts[Utilities.TWO].contains("q")){
				m_canBlackQueensideCastle = true;
			}else{
				m_canBlackQueensideCastle = false;
			}
		}
		
		// Set up the board
		for(int i = Utilities.ZERO; i < board.length; i++){
			ParseRank(i, board[i], builder);
		}
		
		// parts[3] contains the destination tile of an en passant capture
		if(!parts[Utilities.THREE].equals("-")){
		    // There is a valid en passant tile
			m_enPassantTile = parts[Utilities.THREE];
			
			// Determine the color of the pawn to find out where the pawn moved
			final ChessColor pawnColor = (builder.WhoseTurnIsIt().IsBlack() ? ChessColor.WHITE : ChessColor.BLACK);
			m_originalRow = (pawnColor.IsWhite() ? BoardUtilities.ToBoardRow(m_enPassantTile) + Utilities.ONE : BoardUtilities.ToBoardRow(m_enPassantTile) - Utilities.ONE);
			m_originalColumn = BoardUtilities.ToBoardColumn(m_enPassantTile);
			
			// Instantiate the pawn
			m_previouslyMoved = new Pawn(pawnColor, m_originalRow, m_originalColumn);
		}else{
		    // There is no valid en passant tile
			m_enPassantTile = null;
		}
		
		// parts[4] contains the number of halfmoves that have occurred since the last pawn movement or capture
		m_currentHalfmoves = Integer.parseInt(parts[Utilities.FOUR]);
		
		// parts[5] contains the number of fullmoves (a set of two moves made by both white and black)
		m_fullmoves = Integer.parseInt(parts[Utilities.FIVE]);
		
		// Set the board configuration
		m_board.SetBoard(builder.Build());
		
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
        if(a_string == null || a_string.isEmpty()){
            return false;
        }
        
        // Separate all parts
        String[] parts = a_string.split(" ");
        
        // All FEN strings must have six distinct parts
        if(parts.length != Utilities.SIX){
            return false;
        }
        
        // The first part contains the board configuration delimited by forward slashes
        String[] ranks = parts[Utilities.ZERO].split("/");
        
        // All FEN boards must have exactly eight ranks
        if(ranks == null || ranks.length != Utilities.EIGHT){
            return false;
        }
        
        // big will hold the board configuration as a single string with
        // no delimiters
        String big = "";

        // Check if each rank is valid
        for(final String rank : ranks){
            if(!IsValidRank(rank)){
                return false;
            }
            big += rank;
        }
        
        // Both players must have one and only one king of their color
        if(!(HasOneAndOnlyOneKing(big, ChessColor.WHITE) && HasOneAndOnlyOneKing(big, ChessColor.BLACK))){
            return false;
        }
        
        // The second part contains the side to move next
        // Check if the side to move next is either a lowercase "w" or "b"
        if(!(parts[Utilities.ONE].equals("w") || parts[Utilities.ONE].equals("b"))){
            return false;
        }
        
        // The third part contains the castling rights, if any
        // Check if castling privileges have any of "KQkq" or is only "-"
        if(!((parts[Utilities.TWO].contains("K") || parts[Utilities.TWO].contains("Q") || parts[Utilities.TWO].contains("k") || parts[Utilities.TWO].contains("q")) || parts[Utilities.TWO].equals("-"))){
            return false;
        }
        
        // The fourth part contains the en passant tile, if any
        // Check if the en passant tile has a lowercase letter from a-h followed by 2 or 7, or is a "-"
        if(!((BoardUtilities.IsValidTile(parts[Utilities.THREE]) && (Integer.parseInt(Character.toString(parts[Utilities.THREE].charAt(Utilities.ONE))) == Utilities.THREE || Integer.parseInt(Character.toString(parts[Utilities.THREE].charAt(Utilities.ONE))) == Utilities.SIX)) || parts[Utilities.THREE].equals("-"))){
            return false;
        }
        
        // The fifth part contains the number of halfmoves made since the last capture or pawn movement
        // Check if the halfmove clock is a positive or zero integer
        try{
            final int halfmoves = Integer.parseInt(parts[Utilities.FOUR]);
            if(halfmoves < Utilities.ZERO){
                return false;
            }
        }catch(NumberFormatException e){
            return false;
        }
        
        // The sixth part contains the number of fullmoves made during the entire game, which starts at 1 before the first move is made
        // Check if the fullmove clock is a positive integer
        try{
            final int fullmoves = Integer.parseInt(parts[Utilities.FIVE]);
            if(fullmoves < Utilities.ONE){
                return false;
            }
        }catch(NumberFormatException e){
            return false;
        }
        
        // This is a valid FEN string
        return true;
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
        if(a_rank == null || a_rank.isEmpty() || a_rank.length() > Utilities.EIGHT){
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
                final int digit = Integer.parseInt(Character.toString(a_rank.charAt(i)));
                
                if(digit >= Utilities.NINE || digit <= Utilities.ZERO){
                    return false;
                }
                
                sum += digit;
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
        private final boolean HasOneAndOnlyOneKing(final String a_board, final ChessColor a_color);
    
    SYNOPSIS
        private final boolean HasOneAndOnlyOneKing(final String a_board, final ChessColor a_color);
    
        String a_board -----------> The FEN board for the parser to read.
        
        ChessColor a_color -------> The color to check.

    DESCRIPTION
        This method attempts to find if there is one and only one
        occurrence of the letter "K" (if a_color is white) or "k"
        (if a_color is black). If there are zero or at least two
        occurrences, this method will fail and return false.
    
    RETURNS
        boolean: True if there is one and only one "K" or "k" and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final boolean HasOneAndOnlyOneKing(final String a_board, final ChessColor a_color){
        // kings will hold the number of K's or k's found
        int kings = Utilities.ZERO;
        
        // kingLetter will hold the actual letter of the king depending on the color
        final char kingLetter = (a_color.IsWhite() ? Utilities.WHITE_KING_ICON : Utilities.BLACK_KING_ICON);
        
        // Check for how many K's or k's there are on the board
        for(int i = Utilities.ZERO; i < a_board.length(); i++){
            if(a_board.charAt(i) == kingLetter){
                kings++;
            }
        }
        
        // Only return true if there was one and only one K or k found
        return kings == Utilities.ONE;
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
    	String noNumbers = a_rank;
    	
    	noNumbers = a_rank.replaceAll("8", "--------");
    	noNumbers = noNumbers.replaceAll("7", "-------");
    	noNumbers = noNumbers.replaceAll("6", "------");
    	noNumbers = noNumbers.replaceAll("5", "-----");
    	noNumbers = noNumbers.replaceAll("4", "----");
    	noNumbers = noNumbers.replaceAll("3", "---");
    	noNumbers = noNumbers.replaceAll("2", "--");
    	noNumbers = noNumbers.replaceAll("1", "-");
    	
    	return noNumbers;
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
    	// Remove the numbers representing empty tiles from the string
    	// and replace them with hyphen-minuses.
    	final String noNumbers = ExpandRank(a_rank);
    	
    	// Iterate through all 8 spots of the string
    	for(int index = Utilities.ZERO; index < noNumbers.length(); index++){
    		
    		final char piece = noNumbers.charAt(index);
    		if(piece == '-'){
    			// The parser found an empty tile
    			continue;
    		}else if(Character.isUpperCase(piece)){
    			// The parser found a white piece
    			switch(piece){
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
    				default: throw new Exception("Improper file format");
    			}
    		}else if(Character.isLowerCase(piece)){
    			// The parser found a black piece
    			switch(piece){
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
        Ryan King,
        with help from https://stackoverflow.com/questions/14126975/joptionpane-without-button
        https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
        https://docs.oracle.com/javase/8/docs/api/javax/swing/JDialog.html
        and Black Widow Chess by Amir Afghani
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
    			return Minimax.MinimaxRoot(m_depth, m_board.GetBoard(), m_white, m_black, true, m_computerPlayer.GetColor());// For a quicker AI
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
    				
    				// Update the panel if necessary
    				UpdateCapturedPiecePanel();
    				
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
        if(m_nextMove.IsEnPassant()){
        	m_board.SetBoard(m_board.GetBoard().EnPassant((EnPassantMove)m_nextMove, m_white, m_black));
        }else if(m_nextMove.IsCastling()){
        	m_board.SetBoard(m_board.GetBoard().Castle((CastlingMove)m_nextMove));
        }else if(m_nextMove.IsAttacking()){
        	m_board.SetBoard(m_board.GetBoard().Attack((AttackingMove)m_nextMove, m_white, m_black));
        }else{
        	m_board.SetBoard(m_board.GetBoard().Move((RegularMove)m_nextMove));
        }
        
        // Get the configuration of the board
        final String raw = Serialize();
        final String[] parts = raw.split(" ");
        final String configuration = parts[Utilities.ZERO];
        
        // Put the configuration into the hash table or increment it if it's already there
        if(m_positions.containsKey(configuration)){
        	m_positions.replace(configuration, m_positions.get(configuration) + Utilities.ONE);
        }else{
        	m_positions.put(configuration, Utilities.ONE);
        }
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
    	if(EnablesEnPassantMove()){
			final Tile enPassantTile = (m_candidate.IsWhite() ? m_board.GetBoard().GetTile(m_nextMove.GetNewRow() + Utilities.ONE, m_nextMove.GetOldColumn()) : m_board.GetBoard().GetTile(m_nextMove.GetNewRow() - Utilities.ONE, m_nextMove.GetNewColumn()));			
			m_enPassantTile = enPassantTile.toString();
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
        final Object[] colors = {WHITE, BLACK};
        
        // Do not allow the user to break out unless s/he chooses an option
        while(true){
            
            m_buttonInt = JOptionPane.showOptionDialog(m_menuBar, PLAY_AS, TITLE, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, colors, null);
            
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
        	final int nextNth = a_nth - Utilities.ONE;
        	return NthLastIndexOf(nextNth, a_char, a_string.substring(Utilities.ZERO, a_string.lastIndexOf(a_char)));
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
    	char[] array = a_string.toCharArray();
    	
    	// Search every character of the string for \n
    	for(char character : array){
    		if(character == '\n'){
    			number++;
    		}
    	}
    	
    	return number;
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
    			final String history = m_gameHistory.pop();
    			
    			if(m_positions.containsKey(history)){
    				// Reduce the board count by one or remove it entirely
    				// if it is sufficiently low
    				if(m_positions.get(history) <= Utilities.ONE){
    					m_positions.remove(history);
    				}else{
    					m_positions.replace(history, m_positions.get(history) - Utilities.ONE);
    				}
    			}
    		}
    		
    		final String oldBoard = m_gameHistory.peek();
    		
    		try{
    			// Build the old board
    			ParseFEN(oldBoard, false);
    			
    			m_board.DrawBoard();

    			// Get rid of the old board
    			m_gameHistory.pop();
    			
    			// Do not allow the observer to change players
    			m_isPreviouslySavedGame = true;
    			
    			// Manipulate copies of the text in the left and right panels
    			final String whiteMoves = m_whiteMoves.getText();
    			final String blackMoves = m_blackMoves.getText();
    			
    			// Find how many \n's there are in each of the strings
    			final int whiteNewlines = NumberOfNewlines(whiteMoves);
    			final int blackNewlines = NumberOfNewlines(blackMoves);
    			
    			final String newWhite, newBlack;
    			
    			if(whiteNewlines > Utilities.ONE){
    				// Find the cutoff of where the last move got appended (this is the 2nd to last newline)
    				final int whiteIndex = NthLastIndexOf(Utilities.TWO, '\n', whiteMoves);
    				
    				// Do not display the move that was just made on either side
    				newWhite = whiteMoves.substring(Utilities.ZERO, whiteIndex);
    			}else{
    				// If only one \n exists, clear the entire history
    				newWhite = "";
    			}
    			
    			if(blackNewlines > Utilities.ONE){
    				// Find the cutoff of where the last move got appended (this is the 2nd to last newline)
    				final int blackIndex = NthLastIndexOf(Utilities.TWO, '\n', whiteMoves);
    				
    				// Do not display the move that was just made on either side
    				newBlack = blackMoves.substring(Utilities.ZERO, blackIndex);
    			}else{
    				// If only one \n exists, clear the entire history
    				newBlack = "";
    			}
    			
    			// Update the textboxes
    			m_whiteMoves.setText(newWhite);
    			m_blackMoves.setText(newBlack);
    			
    			// Remove one copy of the board from the hash to prevent accidents regarding threefold repetition
    			if(m_positions.containsKey(oldBoard)){
    				if(m_positions.get(oldBoard) <= Utilities.ONE){
    					m_positions.remove(oldBoard);   				
    				}else{
    					m_positions.replace(oldBoard, m_positions.get(oldBoard) - Utilities.ONE);
    				}    				
    			}
    			
    			// Check for any status changes
    			m_watcher.Observe();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}else{
    		JOptionPane.showMessageDialog(m_menuBar, "You cannot go back any further.", TITLE, JOptionPane.ERROR_MESSAGE);
    	}
    }
    
    /**/
    /*
    NAME
        private final void UpdateCapturedPiecePanel();
    
    SYNOPSIS
        private final void UpdateCapturedPiecePanel();
    
        No parameters.
    
    DESCRIPTION
        This method updates the appropriate captured piece panel
        with the most recently captured piece. All pieces will be
        sorted as per the rules set forth in the CapturedPiecePanel.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void UpdateCapturedPiecePanel(){
    	if(m_victim != null){
			if(m_victim.IsWhite()){
				m_whitePieces.Insert(m_victim);
			}else{
				m_blackPieces.Insert(m_victim);
			}
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
                final String colorString = this.GetTile().GetPiece().GetColor().toString().toLowerCase();
                final String pieceString = this.GetTile().GetPiece().GetPieceType().toString().toLowerCase();
            
                final String path = FOLDER + colorString + pieceString + EXTENSION;
            
                try{   
                	this.m_image = new JLabel(new ImageIcon(ImageIO.read(new File(path))));
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
            	final int row = BoardUtilities.ToBoardRow(a_eventSource);
            	final int column = BoardUtilities.ToBoardColumn(a_eventSource);
            	m_sourceTile = m_board.GetBoard().GetTile(BoardUtilities.ToBoardRow(a_eventSource), BoardUtilities.ToBoardColumn(a_eventSource));
            	
            	// Find the piece on the tile
    			m_candidate = m_sourceTile.GetPiece();
            	
    			// Only highlight moves if the user clicked on a piece that is his/her color and has at least one legal move
                if(m_sourceTile.IsOccupied() && m_candidate.GetColor().IsAlly(m_currentPlayer.GetColor()) && m_candidate.CanMove()){
                    m_shouldHighlightLegalMoves = true;
                }else{
                    m_sourceTile = null;
                }
            }else{ 
                // The player has selected a destination
                
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
                        
                        // Update the captured piece panel if necessary
                        UpdateCapturedPiecePanel();
                        
                        // Make a copy of the next move
                        m_nextMove = Factory.MoveFactory(m_candidate, m_destinationTile.GetRow(), m_destinationTile.GetColumn(), m_victim, m_board.GetBoard());
                        
                        // Make a copy of the piece that just got moved
                        EvaluatePreviouslyMoved();
                        
                        // Assign the en passant tile, if any
                        AssignEnPassantTile();
                        
                        // Reset the board for repainting
                        AssignBoard();
                        
                        // Record the new move in the correct text field
                        final MoveTextArea area = (m_currentPlayer.IsWhite() ? m_whiteMoves : m_blackMoves);
                        
                        area.append(m_nextMove.toString());
                        
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
            
            // Assign the board
            m_board = a_board;

            // Allocate the space for the tiles
            m_tiles = new GUITile[Utilities.TEN][Utilities.TEN];

            // Remove everything
            this.removeAll();
            
            // Build the type of board the human needs to see
            if(m_humanColor.IsWhite()){
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
                final int row = index / Utilities.TEN;
                final int column = index % Utilities.TEN;
                
                // Add a normal GUITile if it is within the boundaries of the board
                if(BoardUtilities.HasValidCoordinates(row - Utilities.ONE, column - Utilities.ONE)){
                	this.m_tiles[row][column] = new GUITile(m_board.GetTile(row - Utilities.ONE, column - Utilities.ONE));
                	this.m_tiles[row][column].setAlignmentX(Component.CENTER_ALIGNMENT);
                	this.m_tiles[row][column].setAlignmentY(Component.CENTER_ALIGNMENT);
                	this.add(m_tiles[row][column]);
                }else{
                    // Set a center-aligned border tile with the proper letter or number
                	final JPanel borderTile = new JPanel();
                	borderTile.setBackground(this.BORDER_RED);
                	borderTile.setSize(new Dimension(Utilities.SIXTY, Utilities.SIXTY));
                	borderTile.setAlignmentX(Component.CENTER_ALIGNMENT);
            		borderTile.setAlignmentY(Component.CENTER_ALIGNMENT);
                	
                	// Add a letter
                	if(row == Utilities.ZERO || row == Utilities.NINE){
                		if(column != Utilities.ZERO && column != Utilities.NINE){
                			final JLabel letter = new JLabel();             			
                			letter.setText(BoardUtilities.ToAlgebraicColumn(column - Utilities.ONE).toUpperCase());
                			letter.setForeground(Color.WHITE);
                			Utilities.EnlargeFont(letter);
                    		
                    		letter.setAlignmentX(Component.CENTER_ALIGNMENT);
                    		letter.setAlignmentY(Component.CENTER_ALIGNMENT);
                    		
                			borderTile.add(letter);
                		}
                		// Add a number
                	}else if(column == Utilities.ZERO || column == Utilities.NINE){
                		if(row != Utilities.ZERO && row != Utilities.NINE){
                			final JLabel number = new JLabel();
                			number.setSize(new Dimension(Utilities.SIXTY, Utilities.SIXTY));
                			number.setText(BoardUtilities.ToAlgebraicRow(row - Utilities.ONE));
                			number.setForeground(Color.WHITE);
                			Utilities.EnlargeFont(number);
                    		
                    		number.setAlignmentX(Component.CENTER_ALIGNMENT);
                    		number.setAlignmentY(Component.CENTER_ALIGNMENT);
                    		
                			borderTile.add(number);
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
                int row = index / Utilities.TEN;
                int column = index % Utilities.TEN;
                int boardRow = row - Utilities.ONE;
                int boardColumn = column - Utilities.ONE;
                
                if(BoardUtilities.HasValidCoordinates(row - Utilities.ONE, column - Utilities.ONE)){
                	
                	this.m_tiles[Utilities.SEVEN - (row - Utilities.ONE)][Utilities.SEVEN - (column - Utilities.ONE)] = new GUITile(m_board.GetTile(row - Utilities.ONE, column - Utilities.ONE));
                	this.add(m_tiles[Utilities.SEVEN - (row - Utilities.ONE)][Utilities.SEVEN - (column - Utilities.ONE)]);
                }else{
                	final JPanel borderTile = new JPanel();
                	borderTile.setBackground(this.BORDER_RED);
                	
                	// Add a letter
                	if(row == Utilities.ZERO || row == Utilities.NINE){
                		if(column != Utilities.ZERO && column != Utilities.NINE){
                			final JLabel letter = new JLabel();
                			letter.setSize(new Dimension(Utilities.SIXTY, Utilities.SIXTY));
                			letter.setText(BoardUtilities.ToAlgebraicColumn(column - Utilities.ONE).toUpperCase());
                			letter.setForeground(Color.WHITE);
                			Utilities.EnlargeFont(letter);
                			borderTile.add(letter);
                		}
                		// Add a number
                	}else if(column == Utilities.ZERO || column == Utilities.NINE){
                		if(row != Utilities.ZERO && row != Utilities.NINE){
                			final JLabel number = new JLabel();
                			number.setSize(new Dimension(Utilities.SIXTY, Utilities.SIXTY));
                			number.setText(Integer.toString(Utilities.EIGHT - (row - Utilities.ONE)));
                			number.setForeground(Color.WHITE);
                			Utilities.EnlargeFont(number);
                			borderTile.add(number);
                		}
                	}
                	this.add(borderTile);
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
    }
    
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
       		    JOptionPane.showMessageDialog(m_menuBar, "Problem parsing FEN", DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
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
        	final String fen = Serialize();
        	
        	// Get a unique filename with the timestamp
        	final String file = "DarkBlue" + GetDate() + ".fen";
        	
        	try{
        		// Write the FEN string to a file
        		final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("src/com/DarkBlue/Serial/" + file), "UTF-8");
        		
        		writer.write(fen);
        		
        		writer.flush();
        		
        		writer.close();
        		
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
        		
        		// Inform the user of the filename to find later
        		JOptionPane.showMessageDialog(m_instance, "Game saved as \"" + file + "\".", TITLE, JOptionPane.PLAIN_MESSAGE);
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
        		final String fen = JOptionPane.showInputDialog(this, "Please enter a custom FEN string below:", TITLE, JOptionPane.PLAIN_MESSAGE);
        		
        		if(fen != null){
        		    notNull = true;
        		}
        		
        		if(notNull && !IsValidFEN(fen)){
        		    JOptionPane.showMessageDialog(m_menuBar, "Problem parsing FEN", DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
        		    return;
        		}else if(!notNull){
        		    return;
        		}
        		
        		// Parse the string, trimming any trailing whitespace away
        		ParseFEN(fen.trim(), true);
        		
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
            	final int wantToQuit = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit without saving?", TITLE, JOptionPane.YES_NO_OPTION);
                
            	// Quitting in the middle of a game is resigning and counts as a loss
            	if(wantToQuit == JOptionPane.YES_OPTION){
            		if(m_humanColor.IsWhite()){
            			JOptionPane.showMessageDialog(this, BLACK_RESIGNATION, TITLE, JOptionPane.ERROR_MESSAGE);
            		}else{
            			JOptionPane.showMessageDialog(this, WHITE_RESIGNATION, TITLE, JOptionPane.ERROR_MESSAGE);
            		}
            	}
            }
            
            // Terminate the program
            System.exit(Utilities.ZERO);
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
        		final File lawsOfChess = new File("LawsOfChess.pdf");
        		if(lawsOfChess.exists()){

        		    // Open the file with the AWT desktop utility if usable
        			if(Desktop.isDesktopSupported()){
        				Desktop.getDesktop().open(lawsOfChess);
        			}else{
        				System.err.println("AWT Desktop is not supported!");
        			}

        		}else{
        			System.err.println("File does not exist!");
        		}
        	  }catch (Exception e){
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
        */
        private final void HelpMeMoveClicked(){
        	// Show an indicator that the utility is working
        	m_optionPane = new JOptionPane("Thinking...", JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);

        	// Construct a new dialog and set it up
    		m_dialog = new JDialog(m_instance);
    		m_dialog.setTitle("");
    		
    		DisableTabs();

    		m_dialog.setContentPane(m_optionPane);

    		m_dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    		m_dialog.pack();
    		m_dialog.setVisible(true);

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
        				final Move bestMove = get();
        				
        				// Get rid of the dialog box
        				m_dialog.dispose();
        				
        				EnableTabs();
        			
        				// Construct a custom message to send to the user
        				String message = "I recommend you ";
            		
        				if(bestMove.IsRegular()){
        					message += "move your " + bestMove.GetPiece().GetPieceType().toString().toLowerCase() + " from " + BoardUtilities.ToAlgebraic(bestMove.GetOldRow(), bestMove.GetOldColumn());      			
        					message += " to " + BoardUtilities.ToAlgebraic(bestMove.GetNewRow(), bestMove.GetNewColumn()) + ".";
        				}else if(bestMove.IsAttacking()){
        					message += "use your " + bestMove.GetPiece().GetPieceType().toString().toLowerCase() + " on " + BoardUtilities.ToAlgebraic(bestMove.GetOldRow(), bestMove.GetOldColumn());
        					message += " to capture the " + bestMove.GetVictim().GetPieceType().toString().toLowerCase();
        					message += " on " + BoardUtilities.ToAlgebraic(bestMove.GetNewRow(), bestMove.GetNewColumn()) + ".";
        				}else if(bestMove.IsCastling()){
        					message += "perform a ";
            			
        					if(bestMove.GetNewColumn() < bestMove.GetOldColumn()){
        						message += "queen";
        					}else{
        						message += "king";
        					}
            			
        					message += "side castle.";
        				}else{
        					message += "perform an en passant capture with your pawn on " + BoardUtilities.ToAlgebraic(bestMove.GetOldRow(), bestMove.GetOldColumn()) + ".";
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
            	InstructionFrame frame = new InstructionFrame();
            }else if(a_event.getSource() == this.m_rules){
            	// Show the rules of chess as per the regulations set forth by FIDE
            	RulesClicked();
            }else if(a_event.getSource() == this.m_undo){
            	// Undo a move if the human player thinks they made a mistake
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
	    	return m_gameState == GameState.CHECKMATE || m_gameState == GameState.STALEMATE || m_gameState == GameState.INSUFFICIENT_MATERIAL || m_gameState == GameState.FIFTY_MOVE_RULE || m_gameState == GameState.THREEFOLD_REPETITION;
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
			// as long as it is not the result of an undo
			RecordBoard();			
			
			// Refresh all moves for both players
			RefreshPlayers();
				
			// Determine who moved last
			final ChessColor previous = m_currentPlayer.GetColor();
			final Player other = (previous.IsWhite() ? m_black : m_white);
				
			// See if the player moved a pawn to get promoted
			CheckForPromotions(m_currentPlayer);
			
			// Adjust the castling privileges of the other player if necessary
			AdjustCastlingRights(other);
			
			// Refresh the moves again if any pawns got promoted
			RefreshPlayers();
			
			// Determine the right text area based on who moved
			final MoveTextArea area = (previous.IsWhite() ? m_whiteMoves : m_blackMoves);
			
			// Get a serialized board
			final String serial = Serialize();

			// Save the FEN for undoing the move
			m_gameHistory.push(serial);
			
			// Determine the state of the game
			m_gameState = EvaluateGameState(other);
			
			// Determine if a =Q/=R/=B/=N needs to be appended
			AppendPromotion(area);
						
			// See if the game is over
			if(m_gameState == GameState.CHECKMATE && other.IsBlack()){
			    // White won by checkmate
			    AnnounceWhiteCheckmate();
				return;
			}else if(m_gameState == GameState.CHECKMATE && other.IsWhite()){
			    // Black won by checkmate
			    AnnounceBlackCheckmate();
				return;
			}else if(m_gameState == GameState.STALEMATE){
			    // Draw by stalemate
			    AnnounceStalemate(area);
				return;
			}else if(m_gameState == GameState.INSUFFICIENT_MATERIAL){
			    // Draw by insufficient material
			    AnnounceInsufficientMaterial(area);
				return;
			}else if(m_gameState == GameState.FIFTY_MOVE_RULE){
			    // Draw by fifty-move rule
			    AnnounceFiftyMoveRule(area);
				return;
			}else if(m_gameState == GameState.THREEFOLD_REPETITION){
			    // Draw by threefold repetition
			    AnnounceThreefoldRepetition(area);
				return;
			}else if(m_gameState == GameState.CHECK){
			    AnnounceCheck(area, other);
			}else if(m_gameState == GameState.NORMAL && !m_isPreviouslySavedGame){
			    // Nothing significant happened
				area.append("\n");
			}
			
			// Swap the players
			SwapPlayers(previous);
				
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
        JOptionPane.showMessageDialog(m_menuBar, WHITE_CHECKMATE_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);                    
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
        JOptionPane.showMessageDialog(m_menuBar, BLACK_CHECKMATE_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);
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
        JOptionPane.showMessageDialog(m_menuBar, STALEMATE_MESSAGE, TITLE, JOptionPane.INFORMATION_MESSAGE);
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
        JOptionPane.showMessageDialog(m_menuBar, INSUFFICIENT_MATERIAL_MESSAGE, TITLE, JOptionPane.INFORMATION_MESSAGE);
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
        JOptionPane.showMessageDialog(m_menuBar, FIFTY_MOVE_MESSAGE, TITLE, JOptionPane.INFORMATION_MESSAGE);
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
        JOptionPane.showMessageDialog(m_menuBar, THREEFOLD_REPETITION_MESSAGE, TITLE, JOptionPane.INFORMATION_MESSAGE);
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
        private final void AnnounceCheck(final MoveTextArea a_area);
    
    SYNOPSIS
        private final void AnnounceCheck(final MoveTextArea a_area);
    
        MoveTextArea a_area --------> The area to append to.
    
    DESCRIPTION
        This method appends a plus sign after the move in the given text area
        and announces that the human player is in check.
    
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
            final King opposingKing = a_other.GetKing();
            final int kingRow = opposingKing.GetCurrentRow();
            final int kingColumn = opposingKing.GetCurrentColumn();
            final ChessColor kingTileColor = m_board.GetBoard().GetTile(kingRow, kingColumn).GetColor();
            final boolean kingside = opposingKing.HasKingsideCastlingRook(m_board.GetBoard());
            final boolean queenside = opposingKing.HasQueensideCastlingRook(m_board.GetBoard());
            
            // Make a new king if anything is different
            if(kingside != opposingKing.CanKingsideCastle() || queenside != opposingKing.CanQueensideCastle()){
                m_board.GetBoard().GetBoard()[kingRow][kingColumn] = new Tile(kingTileColor, kingRow, kingColumn, new King(a_other.GetColor(), kingRow, kingColumn, kingside, queenside));
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
	    if(!m_isPreviouslySavedGame){
            if(!m_positions.containsKey(m_board.GetBoard().toString())){
                m_positions.put(m_board.GetBoard().toString(), Utilities.ONE);
            }else{
                m_positions.replace(m_board.GetBoard().toString(), m_positions.get(m_board.GetBoard().toString()) + Utilities.ONE);
            }
        }
	}
}