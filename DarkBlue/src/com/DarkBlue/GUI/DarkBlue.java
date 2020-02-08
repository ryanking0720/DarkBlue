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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

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
    public static final String FATAL_ERROR_TITLE = "Fatal Error";
    public static final String RESIGN = "Resign";
    public static final String MOVE = "Move";
    public static final String SOURCE = "Source Tile";
    public static final String DESTINATION = "Destination Tile";
    public static final String INVALID_TILE = "That tile is invalid.";
    public static final String WRONG_COLOR = "That piece is not your color.";
    public static final String EMPTY_TILE = "That tile is empty.";
    public static final String ILLEGAL_MOVE = "That move is illegal.";
    public static final String NO_LEGAL_MOVES = "That piece has no legal moves.";
    public static final String OWN_COLOR = "You cannot capture a piece of your own color.";
    public static final String KING = "You cannot capture the opponent\'s king.";
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
    private static boolean m_isRestarted = false;
    
    private static GameState m_gameState = GameState.EMPTY;
    
    /* My components */
    
    private GUIBoard m_board;
    
    private Scanner m_keyboard;
    
    private GameWatcher m_watcher = new GameWatcher();
    
    // The alias for the current player, used to reduce code
    private Player m_currentPlayer;
        
    // The history of the game from move to move
    private final ArrayList<Board> m_boardHistory;
    
    private final ArrayList<Move> m_moveHistory;
    
    private final HashMap<String, Integer> m_positions;
        
    private GameState m_state = GameState.NORMAL;
    
    private static ChessColor m_humanColor, m_computerColor;
    
    // The players described by color
    private static Player m_white;
    
    private static Player m_black;
    
    private static Player m_humanPlayer, m_computerPlayer;
    

    private static int m_originalRow;
    private static int m_originalColumn;

    // The number of moves made; useful for determining draws
    private int m_allHalfmoves = Utilities.ZERO;
    private int m_currentHalfmoves = Utilities.ZERO;
    
    private int m_fullmoves = Utilities.ONE;
        
    // Search depth for the AI; set to 1 only for testing purposes
    private static int m_depth = Utilities.THREE;// To be used later on
       
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
    private DarkBlueMenuBar m_menuBar = new DarkBlueMenuBar();
    private static MoveTextArea m_whiteMoves;
    private static MoveTextArea m_blackMoves;
    
    //private JPanel m_bottom = new JPanel();
    private JPanel m_top = new JPanel();
    private JPanel m_left = new JPanel();
    private JPanel m_right = new JPanel();

    // Contains the GUIBoard object, as well as two column bumpers on top and bottom
    private JPanel m_boardPanel = new JPanel();

    private JLabel m_whiteLabel = new JLabel("White");
    private JLabel m_blackLabel = new JLabel("Black");
    
    private SwingWorker<Move, Void> m_worker;
    
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
        Ryan King
    */
    private DarkBlue(){        
        super(TITLE);
      
        m_positions = new HashMap<>();
        m_humanColor = ChessColor.WHITE;
        m_computerColor = ChessColor.BLACK;
        m_whiteMoves = new MoveTextArea();
        m_blackMoves = new MoveTextArea();
        this.m_menuBar.DisableSave();
        this.m_board = new GUIBoard(Board.GetEmptyBoard());
        InitializePlayers(m_board.GetBoard());
        this.m_keyboard = new Scanner(System.in);
        this.m_moveHistory = new ArrayList<>();
        this.m_boardHistory = new ArrayList<>();
        
        // Assign the correct player
        m_currentPlayer = (m_board.WhoseTurnIsIt() == ChessColor.WHITE ? m_white : m_black);

        this.CreateAndShowGUI();
        // Test();// Use for testing on the command line
    }
    
    /*
    NAME
        public final void CheckSourceTile(final Delta a_source);
    
    SYNOPSIS
        public final void CheckSourceTile(final Delta a_source);
    
        Delta a_source --------> The pair of integers that represent the source tile.
    
    DESCRIPTION
        This method determines if the source coordinates entered in by the user
        are valid. If they are, they will be officially assigned to the variables
        and the method will return.
        If not, they will not be assigned and the method will repeat.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    /*
    public final void CheckSourceTile(final Delta a_source){
    	do{
    		try{
    			if(!BoardUtilities.HasValidCoordinates(a_source.GetRowDelta(), a_source.GetColumnDelta())){
    				JOptionPane.showMessageDialog(this, INVALID_TILE, TITLE, JOptionPane.ERROR_MESSAGE);
    				continue;
    			}else{
            
    				Piece mover = m_board.GetBoard().GetTile(a_source.GetRowDelta(), a_source.GetColumnDelta()).GetPiece();
            
    				if(m_board.GetBoard().GetTile(a_source.GetRowDelta(), a_source.GetColumnDelta()).IsEmpty()){
    					JOptionPane.showMessageDialog(this, EMPTY_TILE, TITLE, JOptionPane.ERROR_MESSAGE);
    					continue;
    				}else if(mover.GetColor().IsEnemy(m_currentPlayer.GetColor())){
    					JOptionPane.showMessageDialog(this, WRONG_COLOR, TITLE, JOptionPane.ERROR_MESSAGE);
    					continue;
    				}else if(mover.GetColor().IsAlly(m_currentPlayer.GetColor()) && !mover.CanMove()){
    					JOptionPane.showMessageDialog(this, NO_LEGAL_MOVES, TITLE, JOptionPane.ERROR_MESSAGE);
    					continue;
    				}
                
    				m_sourceRow = a_source.GetRowDelta();
    				m_sourceColumn = a_source.GetColumnDelta();
                
    				m_originalRow = m_sourceRow;
    				m_originalColumn = m_sourceColumn;
                
    				return;
    			}
    		}catch(Exception e){
    			JOptionPane.showMessageDialog(this, INVALID_TILE, TITLE, JOptionPane.ERROR_MESSAGE);
    			continue;
    		}
    	}while(true);
    }
    */
    /*
    NAME
        public final boolean CheckDestinationTile(final Delta a_destination);
    
    SYNOPSIS
        public final boolean CheckDestinationTile(final Delta a_destination);
    
        Delta a_destination --------> The pair of integers that represent the destination tile.
    
    DESCRIPTION
        This method determines if the destination coordinates entered in by the user
        are valid. If they are, they will be officially assigned to the variables
        and the method will return.
        If not, they will not be assigned and the method will repeat.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    /*
    public final void CheckDestinationTile(final Delta a_destination){
    	do{
    		try{
    			if(!BoardUtilities.HasValidCoordinates(a_destination.GetRowDelta(), a_destination.GetColumnDelta())){
    				JOptionPane.showMessageDialog(this, INVALID_TILE, TITLE, JOptionPane.ERROR_MESSAGE);
    				continue;
    			}else{
                
    				Piece mover = m_board.GetBoard().GetTile(m_sourceRow, m_sourceColumn).GetPiece();
    				Piece victim = m_board.GetBoard().GetTile(a_destination.GetRowDelta(), a_destination.GetColumnDelta()).GetPiece();
            
    				if(!Utilities.IsLegal(mover, a_destination.GetRowDelta(), a_destination.GetColumnDelta())){
    					JOptionPane.showMessageDialog(this, ILLEGAL_MOVE, TITLE, JOptionPane.ERROR_MESSAGE);
    					continue;
    				}else if(victim != null && victim.IsAlly(mover)){
    					JOptionPane.showMessageDialog(this, OWN_COLOR, TITLE, JOptionPane.ERROR_MESSAGE);
    					continue;
    				}else if(victim != null && victim.IsEnemy(mover) && victim.IsKing()){
    					JOptionPane.showMessageDialog(this, KING, TITLE, JOptionPane.ERROR_MESSAGE);
    					continue;
    				}
                
    				m_destinationRow = a_destination.GetRowDelta();
    				m_destinationColumn = a_destination.GetColumnDelta();
                
    				return;
    			}
    		}catch(Exception e){
    			JOptionPane.showMessageDialog(this, INVALID_TILE, TITLE, JOptionPane.ERROR_MESSAGE);
    			continue;
    		}
    	}while(true);
    }
    */
    
    /* Overridden MouseListener and ActionListener events */
    
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
        if(m_candidate != null){
        	if(m_humanPlayer.IsWhite()){
        		this.m_board.GetTile(m_candidate.GetCurrentRow(), m_candidate.GetCurrentColumn()).LightUp();
        	}else{
        		this.m_board.GetTile(Utilities.SEVEN - m_candidate.GetCurrentRow(), Utilities.SEVEN - m_candidate.GetCurrentColumn()).LightUp();
        	}
            for(Move move : m_candidate.GetCurrentLegalMoves()){
            	if(m_humanPlayer.IsWhite()){
            		this.m_board.GetTile(move.GetNewRow(), move.GetNewColumn()).LightUp();
            	}else{
            		this.m_board.GetTile(Utilities.SEVEN - move.GetNewRow(), Utilities.SEVEN - move.GetNewColumn()).LightUp();
            	}
            }
        }
    }
    
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
        if(m_candidate != null){
        	if(m_humanPlayer.IsWhite()){
        		this.m_board.GetTile(m_candidate.GetCurrentRow(), m_candidate.GetCurrentColumn()).Revert();
        	}else{
        		this.m_board.GetTile(Utilities.SEVEN - m_candidate.GetCurrentRow(), Utilities.SEVEN - m_candidate.GetCurrentColumn()).Revert();
        	}
            for(Move move : m_candidate.GetCurrentLegalMoves()){
            	if(m_humanPlayer.IsWhite()){
            		this.m_board.GetTile(move.GetNewRow(), move.GetNewColumn()).Revert();
            	}else{
            		this.m_board.GetTile(Utilities.SEVEN - move.GetNewRow(), Utilities.SEVEN - move.GetNewColumn()).Revert();
            	}
            }
        }
    }
    
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
    	m_top.add(m_menuBar);
    	
    	a_pane.add(m_top, BorderLayout.PAGE_START);
    	
        JPanel sourcePanel = new JPanel();
        JPanel destinationPanel = new JPanel();
        sourcePanel.setLayout(new BoxLayout(sourcePanel, BoxLayout.PAGE_AXIS));
        destinationPanel.setLayout(new BoxLayout(destinationPanel, BoxLayout.PAGE_AXIS));
        
        ConstructBoardPanel();
        
        m_left.setLayout(new BoxLayout(m_left, BoxLayout.PAGE_AXIS));
        m_right.setLayout(new BoxLayout(m_right, BoxLayout.PAGE_AXIS));
              
        m_left.add(m_whiteLabel);
        m_left.add(m_whiteMoves);
        
        m_whiteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        m_whiteLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        m_blackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        m_blackLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        m_right.add(m_blackLabel);
        m_right.add(m_blackMoves);
        
        a_pane.add(m_left, BorderLayout.LINE_START);
        
        a_pane.add(m_right, BorderLayout.LINE_END);
        
        a_pane.add(m_board, BorderLayout.CENTER);
    }
    
    /*
    NAME
        private final void ConstructBoardPanel();
    
    SYNOPSIS
        private final void ConstructBoardPanel();
    
        No parameters.
    
    DESCRIPTION
        This method constructs the JPanel which will contain the board
        in a vertical BoxLayout.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void ConstructBoardPanel(){
        m_boardPanel.setLayout(new BoxLayout(m_boardPanel, BoxLayout.PAGE_AXIS));
    }
    
    
    
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
        this.pack();
        this.setVisible(true);
    }
    
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
        public static final boolean IsPreviouslySavedGame();
    
    SYNOPSIS
        public static final boolean IsPreviouslySavedGame();
    
        No parameters.
    
    DESCRIPTION
        This method returns if the game being played has been
        deserialized from a file, rather than being started fresh.
    
    RETURNS
        boolean: True if the game was resumed from a file, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public static final boolean IsPreviouslySavedGame(){
    	return m_isPreviouslySavedGame;
    }
    
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

    	/*
    	final Runnable r = new Runnable(){
    		public final void run(){
    	*/
    			ArrayList<Piece> activePiecesCopy = new ArrayList<>();
    			
    			for(int index = Utilities.ZERO; index < a_player.GetActivePieces().size(); index++){
            
    				Piece piece = a_player.GetActivePieces().get(index);
            
    				piece = Factory.PieceFactory(piece);
            
    				activePiecesCopy.add(piece);
    			}
        
    			Pawn pawn;
    			// Check through all the player's pieces
    			for(int index = Utilities.ZERO; index < activePiecesCopy.size(); index++){
    				// Look for a pawn that's on its last rank
    				if(activePiecesCopy.get(index).IsPawn()){
    					if((a_player.IsWhite() && activePiecesCopy.get(index).GetCurrentRow() == Utilities.ZERO) || (a_player.IsBlack() && activePiecesCopy.get(index).GetCurrentRow() == Utilities.SEVEN)){
    						pawn = (Pawn) a_player.GetActivePieces().get(index);
    						// Return a new Board object with the new powerful piece replacing the pawn
    						m_board.SetBoard(pawn.Promote(m_board.GetBoard(), a_player.IsHuman()));
    						m_board.DrawBoard();
    						break;
    					}
    				}else{
    					continue;
    				}
    			}
    	/*
    		}
    	};
    	
    	new Thread(r).start();
    	*/
    }
    
    /*
    NAME
        private final void EvaluateGameState();
    
    SYNOPSIS
        private final void EvaluateGameState();
        
        No parameters.
    
    DESCRIPTION
        This method checks the state of the game.
        It sets the m_state variable to one of the following:
        
        GameState.CHECK: One player's king is in check. He must either move out of it,
        capture the threatening piece himself, use a friendly piece to 
        capture the threatening piece, or use a friendly piece to 
        block the check, assuming the threatening piece moves linearly.
        The player who is in check cannot wait any additional turns to remove the threat.
        Any move by any piece that will not get the king out of check is deemed illegal.
        The game resumes as normal after the player removes the threat.
        
        GameState.STALEMATE: One player's king is not in check but has no legal moves.
        None of the player's other pieces have any legal moves either. 
        The game ends in a draw.
        
        GameState.CHECKMATE: One player's king is in check 
        in his current position and wherever he moves.
        He cannot escape, he cannot capture the piece(s) putting him into check,
        no other friendly piece can capture the piece(s) putting him into check,
        and no friendly piece can block the check. 
        Any other friendly pieces with moves that would not help the king are not allowed to move.
        The other player wins and the game is over.
        
        GameState.DRAW: The game can end with no winner due to a number of conditions:
        
            1. White and black have only bare kings left on the board.
        
            2. One player has a king and the other has a king and a bishop.
        
            3. One player has a king and the other has a king and a knight.
        
            4. Both players have a king and a bishop and both bishops 
                move on the same tile color.
        
            5. The same board configuration has been repeated 3 times. 
                This is typically a symptom of continually putting the opponent's king into check.
            
            6. Fifty moves have been made without a pawn movement or capture.
        
        GameState.NORMAL: The game proceeds as normal.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public final GameState EvaluateGameState(final Player a_player){
    	if(a_player.IsInCheckmate(m_board.GetBoard())){
            return GameState.CHECKMATE;
        }else if(a_player.IsInCheck(m_board.GetBoard())){
            return GameState.CHECK;
        }else if(a_player.IsInStalemate(m_board.GetBoard())){
            return GameState.STALEMATE;
        }else if(IsDrawByInsufficientMaterial() || IsDrawByFiftyMoveRule() || IsDrawByThreefoldRepetition()){
            return GameState.DRAW;
        }else{
            return GameState.NORMAL;
        }
    }
    
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
            if(m_board.GetBoard().GetTile(whiteBishopRow, whiteBishopColumn).GetColor() == m_board.GetBoard().GetTile(blackBishopRow, blackBishopColumn).GetColor()){
                return true;
            }
        }
        return false;
    }
    
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
        Iterator<String> positionIteration = m_positions.keySet().iterator();
        
        while(positionIteration.hasNext()){
        	String current = positionIteration.next();
        	if(m_positions.get(current) == Utilities.THREE){
        		return true;
        	}
        }
        
        return false;
    }
    
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
    
    /*
    NAME
        public static final boolean ShouldHighlightLegalMoves();
    
    SYNOPSIS
        public static final boolean ShouldHighlightLegalMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns if legal moves should be highlighted on the GUI.
    
    RETURNS
        boolean m_shouldHighlightLegalMoves: If legal moves should be highlighted on the GUI.
    
    AUTHOR
        Ryan King
    */
    public static final boolean ShouldHighlightLegalMoves(){
        return m_shouldHighlightLegalMoves;
    }
    
    /*
    NAME
        public static final String Serialize();
    
    SYNOPSIS
        public static final String Serialize();
    
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
    	String serial = "";
    	
    	// Initialize the counter that will hold 
    	// the current number of empty tiles encountered in part of a row
    	int emptyTiles = Utilities.ZERO;
    	
    	// Add the contents of the board
    	for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){
    		int row = index / Utilities.EIGHT;
    		int column = index % Utilities.EIGHT;
    		
    		if(m_board.GetBoard().GetTile(row, column).IsEmpty()){
    			emptyTiles++;
    		}else{
    			if(emptyTiles > Utilities.ZERO){
    				serial += Integer.toString(emptyTiles);
    				emptyTiles = Utilities.ZERO;
    			}
    			serial += m_board.GetBoard().GetTile(row, column).GetPiece().GetIcon();
    		}
    		
    		if(column == Utilities.SEVEN && row < Utilities.SEVEN){
    			serial += "/";
    			emptyTiles = Utilities.ZERO;
    		}
    	}
    	
    	// Add a space as a delimiter
    	serial += " ";
    	
    	// Add the side who will play next when the game resumes
    	serial += Character.toLowerCase(m_board.GetBoard().WhoseTurnIsIt().toString().charAt(Utilities.ZERO));
    	
    	// Add a space as a delimiter
    	serial += " ";
    	
    	// Add which player(s) can castle and on what side
    	if(!(m_white.CanKingsideCastle(m_board.GetBoard()) || m_white.CanQueensideCastle(m_board.GetBoard())) && !(m_black.CanKingsideCastle(m_board.GetBoard()) || m_black.CanQueensideCastle(m_board.GetBoard()))){
    		serial += "-";
    	}else{
    	
    		if(m_white.CanKingsideCastle(m_board.GetBoard())){
    			serial += Utilities.KING_ICON;
    		}
    	
    		if(m_white.CanQueensideCastle(m_board.GetBoard())){
    			serial += Utilities.QUEEN_ICON;
    		}
    	  	
    		if(m_black.CanKingsideCastle(m_board.GetBoard())){
    			serial += Character.toLowerCase(Utilities.KING_ICON);
    		}
    	
    		if(m_black.CanQueensideCastle(m_board.GetBoard())){
    			serial += Character.toLowerCase(Utilities.QUEEN_ICON);
    		}
    	}
    	
    	// Add a space as a delimiter
    	serial += " ";
    	
    	// Add en passant moves, if any
    	if(m_previouslyMoved != null && m_previouslyMoved.IsPawn()){
    		if(m_previouslyMoved.IsWhite() && m_previouslyMoved.GetCurrentRow() == Utilities.FOUR && m_previouslyMoved.HowManyMoves() == Utilities.ONE){
    			serial += BoardUtilities.ToAlgebraic(m_previouslyMoved.GetCurrentRow(), m_previouslyMoved.GetCurrentColumn());
    		}else if(m_previouslyMoved.IsBlack() && m_previouslyMoved.GetCurrentRow() == Utilities.THREE && m_previouslyMoved.HowManyMoves() == Utilities.ONE){
    			serial += BoardUtilities.ToAlgebraic(m_previouslyMoved.GetCurrentRow(), m_previouslyMoved.GetCurrentColumn());
    		}else{
    			serial += "-";
    		}
    	}else{
    		serial += "-";
    	}
    	
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
        public final void Deserialize();
    
    SYNOPSIS
        public final void Deserialize();
    
        No parameters.
    
    DESCRIPTION
        This method takes in a serialization string from
        a text file and parses it into a valid chessboard.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public final void Deserialize(){
    	final Runnable r = new Runnable(){
    		public final void run(){
    			try{  			
    				final JFileChooser chooser = new JFileChooser();
    				// path: /home/ryan/git/DarkBlue/DarkBlue/src/com/DarkBlue/Serial/whatever   				
    				
    				if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){

    					final InputStreamReader reader = new InputStreamReader(new FileInputStream(chooser.getSelectedFile()), "UTF-8");
    	    		
    					@SuppressWarnings("resource")
						final Scanner scanner = new Scanner(reader);
    	    		
    					final String line = scanner.nextLine();
    	    			
    					reader.close();
    	    			
    					ParseFEN(line);
    					
    					m_board.DrawBoard();
    				}  			
    			}catch(Exception e){
    				e.printStackTrace();
    			}
    		}// end of run()
    	};// end of Runnable
    	
    	new Thread(r).start();
    			
    	SwingUtilities.invokeLater(new Runnable(){
    		@Override
    		public final void run(){
    			m_board.DrawBoard();
    		}
    	});
    }
    
    private final void ParseFEN(final String a_FENString) throws Exception{
    	final String[] parts = a_FENString.split(" ");
		
		final BoardBuilder builder = new BoardBuilder();
		
		// parts[0] represents the configuration of the board
		final String[] board = parts[Utilities.ZERO].split("/");
		
		// Set up the board
		for(int i = Utilities.ZERO; i < board.length; i++){
			ParseRank(i, board[i], builder);
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
		
		// parts[3] contains the destination tile of an en passant capture
		if(!parts[Utilities.THREE].equals("-")){
			m_enPassantTile = parts[Utilities.THREE];
		}else{
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
		
		m_currentPlayer = (m_board.WhoseTurnIsIt().IsWhite() ? m_white : m_black);
		//m_isPreviouslySavedGame = true;
		
		m_whiteMoves.append("...\n");
		
		if(m_board.GetBoard().WhoseTurnIsIt() == ChessColor.WHITE){
			m_blackMoves.append("...\n");
		}
    }
    
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
        Ryan King
    */
    private final String ExpandRank(final String a_rank){
    	String noNumbers = a_rank;
    	
    	noNumbers = a_rank.replace("8", "--------");
    	noNumbers = noNumbers.replace("7", "-------");
    	noNumbers = noNumbers.replace("6", "------");
    	noNumbers = noNumbers.replace("5", "-----");
    	noNumbers = noNumbers.replace("4", "----");
    	noNumbers = noNumbers.replace("3", "---");
    	noNumbers = noNumbers.replace("2", "--");
    	noNumbers = noNumbers.replace("1", "-");
    	
    	return noNumbers;
    }
    
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
        Ryan King
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
    				case 'P': a_builder.SetPiece(new Pawn(ChessColor.WHITE, a_row, index));
    				break;
    				case 'Q': a_builder.SetPiece(new Queen(ChessColor.WHITE, a_row, index));
    				break;
    				case 'K': a_builder.SetPiece(new King(ChessColor.WHITE, a_row, index));
    				break;
    				case 'B': a_builder.SetPiece(new Bishop(ChessColor.WHITE, a_row, index));
    				break;
    				case 'N': a_builder.SetPiece(new Knight(ChessColor.WHITE, a_row, index));
    				break;
    				case 'R': a_builder.SetPiece(new Rook(ChessColor.WHITE, a_row, index));
    				break;
    				default: throw new Exception("Improper file format");
    			}
    		}else if(Character.isLowerCase(piece)){
    			// The parser found a black piece
    			switch(piece){
					case 'p': a_builder.SetPiece(new Pawn(ChessColor.BLACK, a_row, index));
					break;
					case 'q': a_builder.SetPiece(new Queen(ChessColor.BLACK, a_row, index));
					break;
					case 'k': a_builder.SetPiece(new King(ChessColor.BLACK, a_row, index));
					break;
					case 'b': a_builder.SetPiece(new Bishop(ChessColor.BLACK, a_row, index));
					break;
					case 'n': a_builder.SetPiece(new Knight(ChessColor.BLACK, a_row, index));
					break;
					case 'r': a_builder.SetPiece(new Rook(ChessColor.BLACK, a_row, index));
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
        Ryan King
    */
    private final void ComputerPlay(){
    	/*
    	m_nextMove = Minimax.Search(m_board.GetBoard(), m_white, m_black, m_depth);
		
		m_candidate = m_nextMove.GetPiece();
    
		// Get the piece's old coordinates
		m_sourceRow = m_nextMove.GetOldRow();
		m_sourceColumn = m_nextMove.GetOldColumn();

		// Get the piece's new coordinates
		m_destinationRow = m_nextMove.GetNewRow();
		m_destinationColumn = m_nextMove.GetNewColumn();
		
		m_sourceTile = m_board.GetBoard().GetTile(m_sourceRow, m_sourceColumn);
		m_destinationTile = m_board.GetBoard().GetTile(m_destinationRow, m_destinationColumn);

		// Get the victim the piece is capturing, if any
		m_victim = m_nextMove.GetVictim();
		
		MakeMove();
    
		m_allHalfmoves++;

		if(m_candidate.IsPawn() || m_victim != null){
			m_currentHalfmoves = Utilities.ZERO;
		}else{
			m_currentHalfmoves++;
		}

		if(m_allHalfmoves % Utilities.TWO == Utilities.ZERO){
			m_fullmoves++;
		}
		
		m_board.DrawBoard();
		*/
    	
    	m_worker = new SwingWorker<Move, Void>(){
    		
    		@Override
    		protected final Move doInBackground(){
    			return Minimax.Search(m_board.GetBoard(), m_white, m_black, m_depth);// For a slightly better AI
    		}
    		
    		@Override
    		public final void done(){
    			try{
    				m_nextMove = get();
    			
    				m_candidate = m_nextMove.GetPiece();
    				
    				m_victim = m_nextMove.GetVictim();
    				
    				m_sourceTile = m_board.GetBoard().GetTile(m_nextMove.GetOldRow(), m_nextMove.GetOldColumn());
    	        
    				m_destinationTile = m_board.GetBoard().GetTile(m_nextMove.GetNewRow(), m_nextMove.GetNewColumn());
    				
    				// Reset the board for repainting
    				AssignBoard();
    				
    				if(m_computerColor.IsWhite()){
						m_whiteMoves.append(m_nextMove.toString() + "\n");
					}else{
						m_blackMoves.append(m_nextMove.toString() + "\n");
					}   				
    	        
    				m_allHalfmoves++;
        
    				if(m_candidate.IsPawn() || m_victim != null){
    					m_currentHalfmoves = Utilities.ZERO;
    				}else{
    					m_currentHalfmoves++;
    				}
        
    				if(m_allHalfmoves % Utilities.TWO == Utilities.ZERO){
    					m_fullmoves++;
    				}
    				
    				m_sourceTile = null;
    				m_destinationTile = null;
    				m_nextMove = null;
    				
    				RefreshPlayers();
    				
    				m_board.DrawBoard();
    				
    				m_watcher.Observe();
    			}catch(Exception e){
    				e.printStackTrace();
    			}
    		}   			
    	};
    	
    	m_worker.execute();
    }
    
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
    }
    
    /*
    NAME
        private final void AssignCurrentPlayer();
    
    SYNOPSIS
        private final void AssignCurrentPlayer();
    
        No parameters.
    
    DESCRIPTION
        This method assigns the current player.
        This is done to reduce code later on and
        give a convenient way to address the player
        whose turn it is.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void AssignCurrentPlayer(){
        if(m_board.WhoseTurnIsIt().IsWhite()){
            m_currentPlayer = m_white;
        }else{
            m_currentPlayer = m_black;
        }
    }
    
    /*
    NAME
        private final void MakeMove();
    
    SYNOPSIS
        private final void MakeMove();
    
        No parameters.
    
    DESCRIPTION
        This method makes the desired
        move on the board using the
        appropriate method in the Board class.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void MakeMove(){
        if(m_nextMove.IsAttacking()){
            m_board = m_board.Attack((AttackingMove)m_nextMove, m_white, m_black);
        }else if(m_nextMove.IsCastling()){
            m_board = m_board.Castle((CastlingMove)m_nextMove);
        }else if(m_nextMove.IsEnPassant()){
            m_board = m_board.EnPassant((EnPassantMove)m_nextMove, m_white, m_black);
        }else{
            m_board = m_board.Move((RegularMove)m_nextMove);
        }

        m_moveHistory.add(m_nextMove);
        String raw = Serialize(), configuration = "";
        String[] parts = raw.split(" ");
        
        for(int i = Utilities.ZERO; i < Utilities.FOUR; i++){
        	configuration += parts[i];
        	
        	if(i <= Utilities.TWO){
        		configuration += " ";
        	}
        }

        if(m_positions.containsKey(configuration)){
        	m_positions.replace(configuration, m_positions.get(configuration) + Utilities.ONE);
        }else{
        	m_positions.put(configuration, Utilities.ONE);
        }
    }
    
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
        
        final Object[] colors = {WHITE, BLACK};
        
        while(true){
            
            m_buttonInt = JOptionPane.showOptionDialog(null, PLAY_AS, TITLE, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, colors, null);
            
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
            
            m_computerColor = BoardUtilities.Reverse(m_humanColor);

            break;
        }
    }
    
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
        if(m_humanColor.IsWhite()){
            m_white = new Human(ChessColor.WHITE, a_board);// Originally a human
            m_black = new Human(ChessColor.BLACK, a_board);// Originally a computer
            m_humanPlayer = m_white;
            m_computerPlayer = m_black;
        }else{
            m_white = new Human(ChessColor.WHITE, a_board);// Originally a computer
            m_black = new Human(ChessColor.BLACK, a_board);// Originally a human
            m_humanPlayer = m_black;
            m_computerPlayer = m_white;
        }

        RefreshPlayers();
    }
    
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
    
    /*
    NAME
        public static final Player GetWhite();
    
    SYNOPSIS
        public static final Player GetWhite();
    
        No parameters.
    
    DESCRIPTION
        This method returns the white player.
    
    RETURNS
        Player m_white: The white player.
    
    AUTHOR
        Ryan King
    */
    public static final Player GetWhite(){
        return m_white;
    }
    
    /*
    NAME
        public static final Player GetBlack();
    
    SYNOPSIS
        public static final Player GetBlack();
    
        No parameters.
    
    DESCRIPTION
        This method returns the black player.
    
    RETURNS
        Player m_black: The black player.
    
    AUTHOR
        Ryan King
    */
    public static final Player GetBlack(){
        return m_black;
    }
    
    /*
    NAME
        public static final void GetOriginalRow();
    
    SYNOPSIS
        public static final void GetOriginalRow();
    
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
    
    /*
    NAME
        public final King GetKing(final ChessColor a_color);
    
    SYNOPSIS
        public final King GetKing(final ChessColor a_color);
    
        ChessColor a_color ----> The color of the king to find.
    
    DESCRIPTION
        This method returns a King object given the color.
    
    RETURNS
        White's king or black's king.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final King GetKing(final ChessColor a_color){
        if(a_color.IsWhite()){
            return m_white.GetKing();
        }else{
            return m_black.GetKing();
        }
    }
    
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
        Nothing
    
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
    
    public static final String GetWhiteHistory(){
    	return m_whiteMoves.getText();
    }
    
    public static final String GetBlackHistory(){
    	return m_blackMoves.getText();
    }
    
    /*
    NAME
        public static final void main(String[] a_args);
    
    SYNOPSIS
        public static final void main(String[] a_args);
    
        String[] a_args ------> The array of command line arguments (None are needed for this program).
    
    DESCRIPTION
        This method is the driver of the entire program. It allows two
        players to play a game of chess. The JOptionPane dialog box on the
        outside asks if the player would like to play. If the player selects Yes,
        it will call the InitializeFields() and PruneIllegalMoves() method to set up a 
        new game of chess. This game executes within another while loop.
        Once the game ends, the checkmate, stalemate, or other draw conditions
        will force this inner while loop to break. 
        Once this happens, another JOptionPane dialog box will spawn,
        asking the player if s/he would like to play again.
        The entire process starts over again
        and continues infinitely as long as the player keeps answering Yes. 
        If the player answers No at any time, the program will terminate.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */     
    public static final void main(String[] a_args){        
        DarkBlue.GetInstance();
    }//End of method main()
    
    /*
    NAME
        private final void Test();
    
    SYNOPSIS
        private final void Test();
    
        No parameters.
    
    DESCRIPTION
        This method plays a loop-driven chess game on the terminal for testing purposes.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void Test(){
        m_keyboard = new Scanner(System.in);
        
        ChooseColor();
        // Only continue for as long as both kings are safe or one player is in check
        while(true){// Beginning of while loop
                    
            // Determine whose turn it is and assign the alias accordingly
            AssignCurrentPlayer();
            
            System.out.println();
            
            this.m_board.GetBoard().PrintBoard();
                    
            // Determine the state of the game after the newly-made move
            m_state = EvaluateGameState(m_currentPlayer);
                    
            // Check to see if either player is in check, stalemate,
            // or checkmate and handle those situations appropriately
            if(m_state == GameState.CHECKMATE && m_currentPlayer.IsWhite()){
                System.err.println(DarkBlue.BLACK_CHECKMATE_MESSAGE);
                return;
            }else if(m_state == GameState.CHECKMATE && m_currentPlayer.IsBlack()){
                System.err.println(DarkBlue.WHITE_CHECKMATE_MESSAGE);
                return;
            }else if(m_state == GameState.CHECK && m_currentPlayer.IsHuman()){
                System.err.println(DarkBlue.CHECK_MESSAGE);
                // Just display this message and do not return; the game is still playable
            }else if(m_state == GameState.STALEMATE){
                System.err.println(DarkBlue.STALEMATE_MESSAGE);
                return;
            }else if(m_state == GameState.DRAW && IsDrawByFiftyMoveRule()){
                System.err.println(DarkBlue.FIFTY_MOVE_MESSAGE);
                return;
            }else if(m_state == GameState.DRAW && IsDrawByInsufficientMaterial()){
                System.err.println(DarkBlue.INSUFFICIENT_MATERIAL_MESSAGE);
                return;
            }else if(m_state == GameState.DRAW && IsDrawByThreefoldRepetition()){
                System.err.println(DarkBlue.THREEFOLD_REPETITION_MESSAGE);
                return;
            }
                    
            this.m_board.GetBoard().PrintWhoseTurn();
                    
            if(this.m_currentPlayer.IsHuman()){
                //TestPlay();                    
            }else{
                System.out.println("Thinking...");
                ComputerPlay();
            }
            
            EvaluatePreviouslyMoved();
            
            RefreshPlayers();
                    
        }// End of while true game loop
    }
    
    /*
    NAME
        private final void CheckSourceTile();
    
    SYNOPSIS
        private final void CheckSourceTile();
    
        No parameters.
    
    DESCRIPTION
        This method determines if the source coordinates entered in by the user
        are valid. If they are, they will be officially assigned to the variables.
        If not, this will repeat until correct values are given.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    /*
    private final void CheckSourceTile(){
        do{
            System.out.print(DarkBlue.SOURCE + ": ");
            String tile = m_keyboard.nextLine();
            m_sourceRow = BoardUtilities.ToBoardRow(tile);
            m_sourceColumn = BoardUtilities.ToBoardColumn(tile);
            try{
            	if(!BoardUtilities.HasValidCoordinates(m_sourceRow, m_sourceColumn)){
            		JOptionPane.showMessageDialog(null, DarkBlue.INVALID_TILE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
            	}else{
            
            		m_candidate = m_board.GetBoard().GetTile(m_sourceRow, m_sourceColumn).GetPiece();
            
            		if(m_board.GetBoard().GetTile(m_sourceRow, m_sourceColumn).IsEmpty()){
            			JOptionPane.showMessageDialog(null, DarkBlue.EMPTY_TILE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
            		}else if(m_candidate.GetColor().IsEnemy(m_currentPlayer.GetColor())){
            			JOptionPane.showMessageDialog(null, DarkBlue.WRONG_COLOR, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
            		}else if(m_candidate.GetColor().IsAlly(m_currentPlayer.GetColor()) && !m_candidate.CanMove()){
            			JOptionPane.showMessageDialog(null, DarkBlue.NO_LEGAL_MOVES, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
            		}
            	}
            	return;
            }catch(Exception e){
            	JOptionPane.showMessageDialog(null, DarkBlue.INVALID_TILE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }while(!BoardUtilities.HasValidCoordinates(m_sourceRow, m_sourceColumn));
    }
    */
    /*
    NAME
        private final void CheckDestinationTile();
    
    SYNOPSIS
        public final void CheckDestinationTile();
    
        No parameters.

    DESCRIPTION
        This method determines if the destination coordinates entered in by the user
        are valid. If they are, they will be officially assigned to the variables.
        If not, this will repeat until correct values are given.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    /*
    private final void CheckDestinationTile(){
        do{
            try{
                System.out.print(DarkBlue.DESTINATION + ": ");
                final String tile = m_keyboard.nextLine();
                m_destinationRow = BoardUtilities.ToBoardRow(tile);
                m_destinationColumn = BoardUtilities.ToBoardColumn(tile);
                if(!BoardUtilities.HasValidCoordinates(m_destinationRow, m_destinationColumn)){
                    JOptionPane.showMessageDialog(null, DarkBlue.INVALID_TILE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                    continue;
                }else{
                
                    final Piece mover = m_board.GetBoard().GetTile(m_sourceRow, m_sourceColumn).GetPiece();
                    m_victim = m_board.GetBoard().GetTile(m_destinationRow, m_destinationColumn).GetPiece();
            
                    if(!Utilities.IsLegal(mover, m_destinationRow, m_destinationColumn)){
                        JOptionPane.showMessageDialog(null, DarkBlue.ILLEGAL_MOVE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                        continue;
                    }else if(m_victim != null && m_victim.IsAlly(mover)){
                        JOptionPane.showMessageDialog(null, DarkBlue.OWN_COLOR, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                        continue;
                    }else if(m_victim != null && m_victim.IsEnemy(mover) && m_victim.IsKing()){
                        JOptionPane.showMessageDialog(null, DarkBlue.KING, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                }
                return;
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, DarkBlue.INVALID_TILE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                continue;
            }
        }while(true);
    }
    */
    /*
    NAME
        private final void TestPlay();
    
    SYNOPSIS
        private final void TestPlay();
    
        No parameters.
    
    DESCRIPTION
        This method allows the user to play both sides of a loop-driven chess game for testing purposes.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    /*
    private final void TestPlay(){
        CheckSourceTile();
        CheckDestinationTile();
        
        m_originalRow = m_sourceRow;
        m_originalColumn = m_sourceColumn;
        
        m_nextMove = Factory.MoveFactory(m_candidate, m_destinationRow, m_destinationColumn, m_victim, m_board.GetBoard());
        
        MakeMove();
    }
    */
    private final class GUITile extends JPanel implements MouseListener{

        // Final values for bookkeeping
        private static final long serialVersionUID = Utilities.ONE_LONG;
        
        public static final String EXTENSION = ".png";
        public static final String FOLDER = "GUI/ChessPieces/";
        
        // Custom final values
        private final Color m_originalColor;
        private final Tile m_tile;

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
            
            this.setSize(new Dimension(Utilities.SIXTY, Utilities.SIXTY));
            this.setPreferredSize(new Dimension(Utilities.SIXTY, Utilities.SIXTY));
            this.setBackground(this.m_originalColor);

            addMouseListener(this);
            
            DrawTile();
        }
        
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
            Ryan King
        */
        private final void SetPiece(){
            this.removeAll();
            
            if(this.GetTile().IsOccupied()){
                final String colorString = this.GetTile().GetPiece().GetColor().toString().toLowerCase();
                final String pieceString = this.GetTile().GetPiece().GetPieceType().toString().toLowerCase();
            
                final String path = FOLDER + colorString + pieceString + EXTENSION;
            
                try{   
                    this.add(new JLabel(new ImageIcon(ImageIO.read(new File(path)))));
                }catch(Exception e){        
                    e.printStackTrace();
                    System.exit(Utilities.ONE);
                }
            }
            
            revalidate();
            repaint();
        }
        
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
            Ryan King
        */
        private final void DrawTile(){
            SetPiece();
            
            revalidate();
            repaint();
        }
        
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
        	if(m_watcher.IsGameOver() || m_currentPlayer.IsComputer()){
        		return;
        	}
        	
            if(SwingUtilities.isLeftMouseButton(a_event)){	
            	
            	Respond(a_event.getSource().toString());
            	
            	if(IsMoveDone()){
            		m_sourceTile = null;
            		m_destinationTile = null;
            		m_board.DrawBoard();           		
            		m_watcher.Observe();
            	}
            }   
            
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
            Ryan King
        */
        public final void Respond(final String a_eventSource){
            if(m_sourceTile == null){
            	// Find the tile that was clicked          	
            	m_sourceTile = m_board.GetBoard().GetTile(BoardUtilities.ToBoardRow(a_eventSource), BoardUtilities.ToBoardColumn(a_eventSource));
    			m_candidate = m_sourceTile.GetPiece();
            	
                if(m_sourceTile.IsOccupied() && m_candidate.GetColor().IsAlly(m_currentPlayer.GetColor()) && m_candidate.CanMove()){
                    m_shouldHighlightLegalMoves = true;
                }else{
                    m_sourceTile = null;
                }
            }else{           	
                m_originalRow = m_sourceTile.GetRow();
                m_originalColumn = m_sourceTile.GetColumn();
                
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
                        
                        m_nextMove = Factory.MoveFactory(m_candidate, m_destinationTile.GetRow(), m_destinationTile.GetColumn(), m_victim, m_board.GetBoard());
                        
                        EvaluatePreviouslyMoved();
                        
                        // Reset the board for repainting
                        AssignBoard();
                        
                        final MoveTextArea area = (m_currentPlayer.IsWhite() ? m_whiteMoves : m_blackMoves);
                        
                        area.append(m_nextMove.toString());
                        
                        m_gameState = EvaluateGameState(m_currentPlayer.IsWhite() ? m_black : m_white);
                        
                        if(!m_watcher.IsGameOver()){
                        	area.append("\n");
                        }
                                              
                        m_allHalfmoves++;
                            
                        if(m_candidate.IsPawn() || m_victim != null){
                            m_currentHalfmoves = Utilities.ZERO;
                        }else{
                            m_currentHalfmoves++;
                        }
                            
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
            Nothing
        
        AUTHOR
            Ryan King
        */
        @Override
        public final String toString(){
            return BoardUtilities.ToAlgebraic(this.GetRow(), this.GetColumn());
        }
    }
    
    private final class GUIBoard extends JPanel{
        
        private static final long serialVersionUID = Utilities.ONE_LONG;
        
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
            super(new GridLayout(Utilities.EIGHT, Utilities.EIGHT));    
            
            m_board = a_board;

            m_tiles = new GUITile[Utilities.EIGHT][Utilities.EIGHT];

            this.removeAll();

            if(DarkBlue.GetHumanColor().IsWhite()){
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
            private final void PurgeBoard();
        
        SYNOPSIS
            private final void PurgeBoard();
        
            No parameters.
        
        DESCRIPTION
            This method clears all the JPanels from the GUIBoard.
            It is usually called when the board needs to be redrawn.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void PurgeBoard(){
        	       	
        	for(int i = Utilities.ZERO; i < Utilities.SIXTY_FOUR; i++){
        		int row = i / Utilities.EIGHT;
        		int column = i % Utilities.EIGHT;
        		this.m_tiles[row][column] = null;
        	}
        	
        	this.removeAll();          
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
            Ryan King
        */
        private final void DrawBoard(){

        	this.removeAll();

            if(m_humanColor.IsWhite()){
                BuildWhiteBoard();
            }else{
                BuildBlackBoard();
            }
            
            if(m_shouldHighlightLegalMoves){
                HighlightLegalMoves();
            }else{
                UndoHighlighting();
            }
            
            revalidate();
            repaint();
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
            return new GUIBoard(m_board.Move(a_candidate));
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
            return new GUIBoard(m_board.Attack(a_candidate, a_white, a_black));
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
            return new GUIBoard(m_board.Castle(a_candidate));
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
            return new GUIBoard(m_board.EnPassant(a_candidate, a_white, a_black));
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
            if(BoardUtilities.HasValidCoordinates(a_row, a_column)){
                return m_tiles[a_row][a_column];
            }else{
                return null;
            }
        }
    }
    
    public final class DarkBlueMenuBar extends JMenuBar implements ActionListener{

    	private static final long serialVersionUID = Utilities.ONE_LONG;
    	
    	private final JMenu m_file = new JMenu("File");
        private final JMenuItem m_newGame = new JMenuItem("New Game");
        private final JMenu m_loadGame = new JMenu("Load Game...");
        private final JMenuItem m_save = new JMenuItem("Save");
        private final JMenuItem m_quit = new JMenuItem("Quit");
        private final JMenuItem m_fromFile = new JMenuItem("From File");
        private final JMenuItem m_customFEN = new JMenuItem("From Custom FEN...");
        
        private final JMenu m_help = new JMenu("Help");
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
            This method adds mnemonics for every menu item.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void AddMnemonics(){
        	m_newGame.setMnemonic('N');
        	m_save.setMnemonic('S');
        	m_quit.setMnemonic('Q');
        	m_instructions.setMnemonic('I');
        	m_rules.setMnemonic('R');
        	m_fromFile.setMnemonic('F');
        	m_customFEN.setMnemonic('C');
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
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private final void AddActionListeners(){
        	m_newGame.addActionListener(this);
        	m_save.addActionListener(this);
        	m_quit.addActionListener(this);
        	m_instructions.addActionListener(this);
        	m_rules.addActionListener(this);
        	m_fromFile.addActionListener(this);
        	m_customFEN.addActionListener(this);
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
        	m_file.add(m_save);
        	m_file.add(m_quit);
        	this.add(m_file);
        	
        	m_loadGame.add(m_fromFile);
        	m_loadGame.add(m_customFEN);
        	
        	m_help.add(m_instructions);
        	m_help.add(m_rules);
        	this.add(m_help);
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
        	if(a_event.getSource() == this.m_newGame){// Works as far as I know...
        		
        		if(m_board.PieceCount() > Utilities.ZERO || m_gameState == GameState.NORMAL){
        			if(JOptionPane.showConfirmDialog(this, "Do you really want to quit this game?", TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
        				return;
        			}
        		}
        				
        		// Clear out static serialization fields  				
        		m_isPreviouslySavedGame = true;
                m_canWhiteKingsideCastle = false;
                m_canWhiteQueensideCastle = false;
                m_canBlackKingsideCastle = false;
                m_canBlackQueensideCastle = false;
                m_enPassantTile = null;
                		
                m_whiteMoves.setText("");
                m_blackMoves.setText("");
                		
                m_gameState = GameState.NORMAL;
        				
                // Set up/clear out non-static fields
                		
        		m_sourceTile = null;
        		m_destinationTile = null;

        		m_candidate = null;
        		m_victim = null;
        		m_nextMove = null;
        				
        		m_allHalfmoves = Utilities.ZERO;
        		m_currentHalfmoves = Utilities.ZERO;
        		m_fullmoves = Utilities.ONE;
        		m_moveHistory.clear();
        		m_boardHistory.clear();
        		m_positions.clear();
        			    
        		ChooseColor();
        		m_board.SetBoard(Board.GetStartingPosition());
        		InitializePlayers(m_board.GetBoard());
        		m_currentPlayer = (m_board.WhoseTurnIsIt().IsWhite() ? m_white : m_black);
        		m_board.DrawBoard();
        		m_save.setEnabled(true);
        		m_watcher.Observe();

        	}else if(a_event.getSource() == this.m_fromFile){
        		m_whiteMoves.setText("");
        		m_blackMoves.setText("");
        		m_isPreviouslySavedGame = true;
           		Deserialize();
           		//m_board.DrawBoard();
            	m_save.setEnabled(true);
            	m_watcher.Observe();
            }else if(a_event.getSource() == this.m_save){
            	final String fen = Serialize();
            	
            	final String file = "DarkBlue" + GetDate() + ".fen";
            	
            	try{
            		final FileWriter writer = new FileWriter("Serial/" + file);
            		
            		writer.write(fen);
            		
            		writer.close();
            		
            		m_save.setEnabled(false);
            	}catch(Exception e){
            		e.printStackTrace();
            	}
            }else if(a_event.getSource() == this.m_customFEN){
            	try{
            		final String fen = JOptionPane.showInputDialog(this, "Please enter a custom FEN string below:", TITLE, JOptionPane.PLAIN_MESSAGE);
            		ParseFEN(fen.trim());
            		m_save.setEnabled(true);
            		m_watcher.Observe();
            	}catch(Exception e){
            		JOptionPane.showMessageDialog(this, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            	}
        	}else if(a_event.getSource() == this.m_quit){
            	final int wantToQuit = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit without saving?", TITLE, JOptionPane.YES_NO_OPTION);
                
                if(wantToQuit == JOptionPane.YES_OPTION){
                	if(!m_watcher.IsGameOver()){
                		if(m_humanColor.IsWhite()){
                			JOptionPane.showMessageDialog(this, BLACK_RESIGNATION, TITLE, JOptionPane.ERROR_MESSAGE);
                		}else{
                			JOptionPane.showMessageDialog(this, WHITE_RESIGNATION, TITLE, JOptionPane.ERROR_MESSAGE);
                		}
                	}
                	
                    System.exit(Utilities.ZERO);
                }
            }else if(a_event.getSource() == m_instructions){
            	InstructionFrame frame = new InstructionFrame();
            }else if(a_event.getSource() == m_rules){
            	//Desktop.browse(new URI("https://www.fide.com/FIDE/handbook/LawsOfChess.pdf"));
            }
        	
        	SwingUtilities.invokeLater(new Runnable(){
    			@Override
    			public final void run(){
    				m_board.DrawBoard();
    				// Clear out move windows, taken pieces, etc.      			       				
    			}
    		});
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
        	private final void EnableLoad();
        
        SYNOPSIS
        	private final void EnableLoad();
        
        	No parameters.
        
        DESCRIPTION
        	This method enables the "Load Game" option
        	found on the DarkBlueMenuBar.
        
        RETURNS
        	Nothing
        
        AUTHOR
            Ryan King
        */
        public final void EnableLoad(){
        	m_loadGame.setEnabled(true);
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
    }
    
    public final class MoveTextArea extends JTextArea{

		private static final long serialVersionUID = Utilities.ONE_LONG;

		public MoveTextArea(){
    		super();  		
    		this.setPreferredSize(new Dimension(60, 480));
    		this.setEditable(false);
    	}
    }
	
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
	    	return m_gameState == GameState.CHECKMATE || m_gameState == GameState.STALEMATE || m_gameState == GameState.DRAW;
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
				
			// Determine who moved last
			final ChessColor previous = m_currentPlayer.GetColor();
				
			// See if the player moved a pawn to get promoted
			CheckForPromotions(m_currentPlayer);
				
			// Refresh the moves again if any pawns got promoted
			RefreshPlayers();				
				
			final Player other = (previous.IsWhite() ? m_black : m_white);
				
			// Determine the state of the game
			m_gameState = EvaluateGameState(other);// bp
								
			// See if the game is over
			if(m_gameState == GameState.CHECKMATE && other.IsBlack()){
				m_menuBar.DisableSave();
				m_whiteMoves.append("#\n1-0");
				JOptionPane.showMessageDialog(m_instance, WHITE_CHECKMATE_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);					
				return;
			}else if(m_gameState == GameState.CHECKMATE && other.IsWhite()){
				m_menuBar.DisableSave();
				m_blackMoves.append("#\n0-1");
				JOptionPane.showMessageDialog(m_instance, BLACK_CHECKMATE_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);
				return;
			}else if(m_gameState == GameState.CHECK){
				if(previous.IsWhite()){
					m_whiteMoves.append("+");
				}else{
					m_blackMoves.append("+");
				}
				if(other.IsHuman()){
					JOptionPane.showMessageDialog(m_instance, CHECK_MESSAGE, TITLE, JOptionPane.WARNING_MESSAGE);
				}
			}else if(m_gameState == GameState.STALEMATE){
				m_menuBar.DisableSave();					
				if(previous.IsWhite()){
					m_whiteMoves.append("\n½-½");
				}else{
					m_blackMoves.append("\n½-½");
				}
				JOptionPane.showMessageDialog(m_instance, STALEMATE_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);
				return;
			}else if(IsDrawByInsufficientMaterial()){
				m_menuBar.DisableSave();
				if(previous.IsWhite()){
					m_whiteMoves.append("\n½-½");
				}else{
					m_blackMoves.append("\n½-½");
				}
				JOptionPane.showMessageDialog(m_instance, INSUFFICIENT_MATERIAL_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);
				return;
			}else if(IsDrawByFiftyMoveRule()){
				m_menuBar.DisableSave();
				if(previous.IsWhite()){
					m_whiteMoves.append("\n½-½");
				}else{
					m_blackMoves.append("\n½-½");
				}
				JOptionPane.showMessageDialog(m_instance, FIFTY_MOVE_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);
				return;
			}else if(IsDrawByThreefoldRepetition()){
				m_menuBar.DisableSave();
				if(previous.IsWhite()){
					m_whiteMoves.append("\n½-½");
				}else{
					m_blackMoves.append("\n½-½");
				}
				JOptionPane.showMessageDialog(m_instance, THREEFOLD_REPETITION_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);
				return;
			}
				
			if(!m_isPreviouslySavedGame){
				m_currentPlayer = (previous.IsWhite() ? m_black : m_white);
			}else{
				m_isPreviouslySavedGame = false;
			}
				
			if(m_currentPlayer.IsComputer()){
				ComputerPlay();
			}
		}
	}
}