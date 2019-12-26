package com.DarkBlue.GUI;

import com.DarkBlue.Board.*;
import com.DarkBlue.Game.*;
import com.DarkBlue.Move.*;
import com.DarkBlue.Piece.*;
import com.DarkBlue.Player.*;
import com.DarkBlue.Utilities.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
//import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

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
public final class DarkBlue extends JFrame implements ActionListener{
    
    private static final long serialVersionUID = Utilities.ONE_LONG;
    
    public static final String TITLE = "Dark Blue";
    //private static final String PLAY = "Would you like to play?";
    public static final String PLAY_AS = "Play as:";
    public static final String CHECK_MESSAGE = "Check!";
    public static final String STALEMATE_MESSAGE = "Stalemate!\nIt�s a draw.";
    public static final String FIFTY_MOVE_MESSAGE = "Fifty-move rule!\nIt�s a draw.";
    public static final String INSUFFICIENT_MATERIAL_MESSAGE = "Insufficient material!\nIt�s a draw.";
    public static final String THREEFOLD_REPETITION_MESSAGE = "Threefold repetition!\nIt�s a draw.";
    public static final String WHITE_CHECKMATE_MESSAGE = "Checkmate!\nWhite wins.";
    public static final String BLACK_CHECKMATE_MESSAGE = "Checkmate!\nBlack wins.";
    //private static final String FATAL_ERROR = "Dark Blue has terminated unexpectedly.";
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
    public static final String KING = "You cannot capture the opponent�s king.";
    public static final String RESIGN_QUESTION = "Do you really want to resign?";
    public static final String WHITE_RESIGNATION = "White wins by resignation.";
    public static final String BLACK_RESIGNATION = "Black wins by resignation.";
    public static final Object WHITE = "White";
    public static final Object BLACK = "Black";
    private static boolean m_shouldHighlightLegalMoves = false;
    
    /* My components */
    
    private GUIBoard m_board;
    
    private Scanner m_keyboard;
    
    // The alias for the current player, used to reduce code
    private Player m_currentPlayer;
        
    // The history of the game from move to move
    private final ArrayList<Board> m_boardHistory;
    
    private final ArrayList<Move> m_moveHistory;
        
    private GameState m_state = GameState.NORMAL;
    
    private static ChessColor m_humanColor, m_oppositeColor;
    
    // The players described by color
    private static Player m_white;
    
    private static Player m_black;
    
    private static Player m_computerPlayer;
    
    // The exact coordinates the player chooses when moving
    private int m_sourceRow;
    private int m_sourceColumn;
    private int m_destinationRow;
    private int m_destinationColumn;
    
    private static int m_originalRow;
    private static int m_originalColumn;

    // The number of moves made; useful for determining draws
    private int m_moves = Utilities.ZERO;
        
    // Search depth for the AI
    private static int m_depth = Utilities.FIVE;// To be used later on
        
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
    private JButton m_resign = new JButton(RESIGN);
    private JButton m_move = new JButton(MOVE);
    
    private JPanel m_bottom = new JPanel();
    
    // Contains the board panel and two row bumpers on either side
    private JPanel m_center = new JPanel();
    
    // Contains the GUIBoard object, as well as two column bumpers on top and bottom
    private JPanel m_boardPanel = new JPanel();
    
    // Left and right row indicators with numbers 1-8
    private RowBumper m_leftBumper, m_rightBumper;
    
    // Top and bottom column indicators with letters A-H
    private ColumnBumper m_topBumper, m_bottomBumper;
    
    private JTextField m_source = new JTextField();
    private JTextField m_destination = new JTextField();
    
    private JLabel m_sourceLabel = new JLabel(SOURCE);
    private JLabel m_destinationLabel = new JLabel(DESTINATION);
    
    //private SwingWorker<Void, Void> m_worker;
    
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
        this.m_keyboard = new Scanner(System.in);
        this.m_moveHistory = new ArrayList<>();
        this.m_boardHistory = new ArrayList<>();
        this.CreateAndShowGUI();
        this.TryPlay();
        //Test();
    }
    
    /*
    NAME
        public final boolean CheckSourceTile(final Delta a_source);
    
    SYNOPSIS
        public final boolean CheckSourceTile(final Delta a_source);
    
        Delta a_source --------> The pair of integers that represent the source tile.
    
    DESCRIPTION
        This method determines if the source coordinates entered in by the user
        are valid. If they are, they will be officially assigned to the variables
        and the method will return true.
        If not, they will not be assigned and the method will return false.
    
    RETURNS
        True if the coordinates are valid, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean CheckSourceTile(final Delta a_source){
        try{
            if(!Utilities.HasValidCoordinates(a_source.GetRowDelta(), a_source.GetColumnDelta())){
                JOptionPane.showMessageDialog(this, INVALID_TILE, TITLE, JOptionPane.ERROR_MESSAGE);
                return false;
            }else{
            
                Piece mover = m_board.GetBoard().GetTile(a_source.GetRowDelta(), a_source.GetColumnDelta()).GetPiece();
            
                if(m_board.GetBoard().GetTile(a_source.GetRowDelta(), a_source.GetColumnDelta()).IsEmpty()){
                    JOptionPane.showMessageDialog(this, EMPTY_TILE, TITLE, JOptionPane.ERROR_MESSAGE);
                    return false;
                }else if(mover.GetColor().IsEnemy(m_currentPlayer.GetColor())){
                    JOptionPane.showMessageDialog(this, WRONG_COLOR, TITLE, JOptionPane.ERROR_MESSAGE);
                    return false;
                }else if(mover.GetColor().IsAlly(m_currentPlayer.GetColor()) && !mover.CanMove()){
                    JOptionPane.showMessageDialog(this, NO_LEGAL_MOVES, TITLE, JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                m_sourceRow = a_source.GetRowDelta();
                m_sourceColumn = a_source.GetColumnDelta();
                
                m_originalRow = m_sourceRow;
                m_originalColumn = m_sourceColumn;
                
                return true;
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, INVALID_TILE, TITLE, JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /*
    NAME
        public final boolean CheckDestinationTile(final Delta a_destination);
    
    SYNOPSIS
        public final boolean CheckDestinationTile(final Delta a_destination);
    
        Delta a_destination --------> The pair of integers that represent the destination tile.
    
    DESCRIPTION
        This method determines if the destination coordinates entered in by the user
        are valid. If they are, they will be officially assigned to the variables
        and the method will return true.
        If not, they will not be assigned and the method will return false.
    
    RETURNS
        True if the coordinates are valid, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final boolean CheckDestinationTile(final Delta a_destination){
        try{
            if(!Utilities.HasValidCoordinates(a_destination.GetRowDelta(), a_destination.GetColumnDelta())){
                JOptionPane.showMessageDialog(this, INVALID_TILE, TITLE, JOptionPane.ERROR_MESSAGE);
                return false;
            }else{
                
                Piece mover = m_board.GetBoard().GetTile(m_sourceRow, m_sourceColumn).GetPiece();
                Piece victim = m_board.GetBoard().GetTile(a_destination.GetRowDelta(), a_destination.GetColumnDelta()).GetPiece();
            
                if(!Utilities.IsLegal(mover, a_destination.GetRowDelta(), a_destination.GetColumnDelta())){
                    JOptionPane.showMessageDialog(this, ILLEGAL_MOVE, TITLE, JOptionPane.ERROR_MESSAGE);
                    return false;
                }else if(victim != null && victim.IsAlly(mover)){
                    JOptionPane.showMessageDialog(this, OWN_COLOR, TITLE, JOptionPane.ERROR_MESSAGE);
                    return false;
                }else if(victim != null && victim.IsEnemy(mover) && victim.IsKing()){
                    JOptionPane.showMessageDialog(this, KING, TITLE, JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                m_destinationRow = a_destination.GetRowDelta();
                m_destinationColumn = a_destination.GetColumnDelta();
                
                return true;
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, INVALID_TILE, TITLE, JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /* Overridden MouseListener and ActionListener events */
    
    /*
    NAME
        public final void actionPerformed(final ActionEvent a_event);
    
    SYNOPSIS
        public final void actionPerformed(final ActionEvent a_event);
    
        ActionEvent a_event -------> The ActionEvent object.
    
    DESCRIPTION
        This method determines which button was pressed,
        and then performs the appropriate action.
        This method violates the Senior Project naming
        conventions because it is overridden from Java.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    @Override
    public final void actionPerformed(final ActionEvent a_event){
        if(a_event.getSource() == m_resign){
            int wantToResign = JOptionPane.showConfirmDialog(this, RESIGN_QUESTION, TITLE, JOptionPane.YES_NO_OPTION);
            
            if(wantToResign == JOptionPane.YES_OPTION){
                if(m_humanColor.IsWhite()){
                    JOptionPane.showMessageDialog(this, BLACK_RESIGNATION, TITLE, JOptionPane.ERROR_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(this, WHITE_RESIGNATION, TITLE, JOptionPane.ERROR_MESSAGE);
                }
                System.exit(Utilities.ZERO);
            }
        }else if(a_event.getSource() == m_move){
            if(HumanPlay()){
                Play();
            }
        }
        CreateWorker();
    }
    
    public void CreateWorker(){
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            
            @Override
            protected Boolean doInBackground() throws Exception{
                //MakeMove();
                
                //revalidate();
                //repaint();

                return true;
            }
            
            /*
            @Override
            protected Void process(){
                return null;
            }
            */
            
            @Override
            protected void done(){
                MakeMove();
                m_moveHistory.add(m_nextMove);
                m_boardHistory.add(Board.GetDeepCopy(m_board.GetBoard()));
                
                revalidate();
                repaint();
                
                RefreshPlayers();
            }
            
        };
        worker.execute();
    }
    
    /*
    NAME
        public final void paintComponents(final Graphics a_g);
    
    SYNOPSIS
        public final void paintComponents(final Graphics a_g);
    
        Graphics a_g -------> The Graphics object.
    
    DESCRIPTION
        This method paints every component inside the JFrame.
        This is primarily used to repaint the board once
        a piece moves, but applies to all sections of the program.
        This method violates the Senior Project naming
        conventions because it is overridden from Java.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    /*
    @Override
    public final void paintComponents(final Graphics a_g){
        super.paintComponents(a_g);
        
        m_board.paintComponent(a_g);
    }
    */
    
    public final void HighlightLegalMoves(){
        if(m_candidate != null){
            this.m_board.GetTile(m_candidate.GetCurrentRow(), m_candidate.GetCurrentColumn()).LightUp();
            for(Move move : m_candidate.GetCurrentLegalMoves()){
                this.m_board.GetTile(move.GetNewRow(), move.GetNewColumn()).LightUp();
            }
        }
    }
    
    public final void UndoHighlighting(){
        if(m_candidate != null){
            this.m_board.GetTile(m_candidate.GetCurrentRow(), m_candidate.GetCurrentColumn()).Revert();
            for(Move move : m_candidate.GetCurrentLegalMoves()){
                this.m_board.GetTile(move.GetNewRow(), move.GetNewColumn()).Revert();
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
        Dark Blue content frame.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void AddComponentsToPane(final Container a_pane){
        this.ChooseColor();

        JPanel sourcePanel = new JPanel();
        JPanel destinationPanel = new JPanel();
        sourcePanel.setLayout(new BoxLayout(sourcePanel, BoxLayout.PAGE_AXIS));
        destinationPanel.setLayout(new BoxLayout(destinationPanel, BoxLayout.PAGE_AXIS));
        
        ConstructBoardPanel();
        
        m_resign.addActionListener(this);
        m_move.addActionListener(this);
        
        /*
        m_bottom.setLayout(new FlowLayout());
        
        m_bottom.add(m_resign);
        
        sourcePanel.add(m_sourceLabel);
        sourcePanel.add(m_source);
        
        destinationPanel.add(m_destinationLabel);
        destinationPanel.add(m_destination);
        
        m_bottom.add(sourcePanel);
        m_bottom.add(destinationPanel);
        
        m_resign.setAlignmentX(Component.CENTER_ALIGNMENT);
        m_move.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        m_sourceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        m_source.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        m_destinationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        m_destination.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        m_bottom.add(m_move);
        
        m_bottom.setBackground(Color.WHITE);
        
        a_pane.add(m_bottom, BorderLayout.PAGE_END);
        */
        
        m_center.setLayout(new BoxLayout(m_center, BoxLayout.LINE_AXIS));
        
        m_leftBumper = new RowBumper(m_humanColor);
        
        m_rightBumper = new RowBumper(m_humanColor);
        
        m_center.add(m_leftBumper);
        
        m_center.add(m_boardPanel);
        
        m_center.add(m_rightBumper);
        
        a_pane.add(m_center, BorderLayout.CENTER);
    }
    
    private final void ConstructBoardPanel(){
        m_boardPanel.setLayout(new BoxLayout(m_boardPanel, BoxLayout.PAGE_AXIS));
        
        m_topBumper = new ColumnBumper(m_humanColor);
        m_bottomBumper = new ColumnBumper(m_humanColor);
        
        m_boardPanel.add(m_topBumper);
        
        m_boardPanel.add(m_board);
        
        m_boardPanel.add(m_bottomBumper);
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
    
    
    /*
    NAME
        public static final ChessColor GetOppositeColor();
    
    SYNOPSIS
        public static final ChessColor GetOppositeColor();
    
        No parameters.
    
    DESCRIPTION
        This method returns the color that wasn't chosen
        by the human player at the start of a game.
    
    RETURNS
        ChessColor m_humanColor: The color not chosen by the human player.
    
    AUTHOR
        Ryan King
    */
    public static final ChessColor GetOppositeColor(){
        return m_oppositeColor;
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
        ArrayList<Piece> activePiecesCopy = new ArrayList<>();
        for(int index = Utilities.ZERO; index < a_player.GetActivePieces().size(); index++){
            
            Piece piece = a_player.GetActivePieces().get(index);
            
            piece = Utilities.DuplicatePiece(piece);
            
            activePiecesCopy.add(piece);
        }
        
        Pawn pawn = null;
        // Check through all the player's pieces
        for(int index = Utilities.ZERO; index < activePiecesCopy.size(); index++){
            // Look for a pawn that's on its last rank
            if(activePiecesCopy.get(index).IsPawn()){
                if((a_player.IsWhite() && activePiecesCopy.get(index).GetCurrentRow() == Utilities.ZERO) || (a_player.IsBlack() && activePiecesCopy.get(index).GetCurrentRow() == Utilities.SEVEN)){
                    pawn = (Pawn) a_player.GetActivePieces().get(index);
                    // Return a new Board object with the new powerful piece replacing the pawn
                    m_board = new GUIBoard(pawn.Promote(m_board.GetBoard()));
                    return;
                }
            }else{
                continue;
            }
        }
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
    public final GameState EvaluateGameState(){
        if(m_currentPlayer.IsInCheckmate(m_board.GetBoard())){
            return GameState.CHECKMATE;
        }else if(m_currentPlayer.IsInCheck(m_board.GetBoard())){
            return GameState.CHECK;
        }else if(m_currentPlayer.IsInStalemate(m_board.GetBoard())){
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
        return m_moves >= Utilities.FIFTY;
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
        return false;
    }
    
    /*
    NAME
        private final boolean IsEnPassantMove();
    
    SYNOPSIS
        private final boolean IsEnPassantMove();
    
        No parameters.
    
    DESCRIPTION
        This method determines if the move to be generated is an en passant move,
        which is a special capture involving pawns. If the moving piece is a pawn
        on its fifth rank, and a piece to its left or right is an enemy pawn that
        just advanced 2 tiles on the previous move and would have been captured by
        this pawn had it only moved 1 tile, and the destination tilefor the first 
        pawn is an empty tile diagonally in front of it, then this move is deemed 
        to be an en passant move.
    
    RETURNS
        True if the move is en passant, and false otherwise.
        One of these two options will always occur.
        
    AUTHOR
        Ryan King
    */
    private final boolean IsEnPassantMove(){
        /*
        return (((m_candidate.IsWhite() && m_destinationRow == m_startRow - Utilities.ONE) 
                || (m_candidate.IsBlack() && m_destinationRow == m_startRow + Utilities.ONE)) 
                && (m_destinationColumn == m_startColumn + Utilities.ONE || m_destinationColumn == m_startColumn - Utilities.ONE) 
                && (Utilities.HasValidCoordinates(m_destinationRow, m_startRow - Utilities.ONE)
                        || Utilities.HasValidCoordinates(m_destinationRow, m_startRow + Utilities.ONE))
                && m_board.GetTile(m_destinationRow, m_destinationColumn).IsEmpty()
                && (m_previouslyMoved.IsPawn() 
                && m_previouslyMoved.IsEnemy(m_candidate)
                && m_previouslyMoved.HowManyMoves() == Utilities.ONE
                && Math.abs(m_previouslyMoved.GetCurrentRow() - m_oldRow) == Utilities.TWO
                && m_oldColumn == m_previouslyMoved.GetCurrentColumn()));
        */
        return m_victim != null
                    && m_candidate.IsPawn()
                    && m_victim.IsPawn() 
                    && m_victim.Equals(m_previouslyMoved) 
                    && (m_victim.GetCurrentRow() == m_destinationRow - Utilities.ONE
                            || m_victim.GetCurrentRow() == m_destinationRow + Utilities.ONE)
                    && m_victim.GetCurrentColumn() == m_destinationColumn;
    }
    
    /*
    NAME
        private final boolean IsCastlingMove();
    
    SYNOPSIS
        private final boolean IsCastlingMove();
    
        No parameters.
    
    DESCRIPTION
        This method determines if the given move
        is a castling move or not.
        
    RETURNS
        True if the move is a castling move, 
        or false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    private final boolean IsCastlingMove(){
        return m_candidate.IsKing()
                && m_destinationRow == m_sourceRow 
                && (m_destinationColumn == m_sourceColumn + Utilities.TWO 
                || m_destinationColumn == m_sourceColumn - Utilities.TWO);
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
    
    public static final boolean ShouldHighlightLegalMoves(){
        return m_shouldHighlightLegalMoves;
    }
    
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
        m_previouslyMoved = Utilities.DuplicatePiece(m_candidate);
    }
    
    /*
    NAME
        public final Move EvaluateMove();
    
    SYNOPSIS
        public final Move EvaluateMove();
    
        No parameters.
    
    DESCRIPTION
        This method creates a new Move object.
        Depending on the type of move this is, it could be:
        
        A regular move 
        (Any piece moving to an empty tile),
        
        An attacking move 
        (Any piece moving to an occupied tile and
        capturing the opposing piece on that tile the way it typically captures),
        
        A castling move 
        (Swapping the king and the rook when there are no other pieces between them,
        the king will not move through check and neither the
        king nor the rook has moved yet), or
        
        An en passant move
        (A special attacking move which can only occur if a pawn is at its fifth rank
        and the previous piece to move was an opposing pawn that advanced 2 squares on
        its first move and could have been taken by the other pawn had it only moved 1 square.
        This is the only legal move where the destination tile is not the same as the tile
        of the captured piece).
    
    RETURNS
        Move move: The evaluated move, ready to be made.
        This will never return null, because at this point,
        all possible moves it could generate are deemed to be legal.
    
    AUTHOR
        Ryan King
    */
    private final Move EvaluateMove(){
        final Move move;
        // Instantiate the desired move
        if(!m_candidate.IsKing() && !m_candidate.IsPawn()){// This is definitely not a castling or en passant move
            if(m_victim != null){
                move = new AttackingMove(m_candidate, m_destinationRow, m_destinationColumn, m_victim);
            }else{
                move = new RegularMove(m_candidate, m_destinationRow, m_destinationColumn);
            }
        }else{
            if(m_candidate.IsKing()){// This could be a castling move
                if(IsCastlingMove()){
                    // This is a castling move
                    move = new CastlingMove((King)m_candidate, m_destinationRow, m_destinationColumn);
                    
                    final Rook rook = (Rook) m_board.GetBoard().GetTile(((CastlingMove)move).GetRookCurrentRow(), ((CastlingMove)move).GetRookCurrentColumn()).GetPiece();
                    
                    final Tile rookTile = m_board.GetBoard().GetTile(rook.GetCurrentRow(), rook.GetCurrentColumn());
                    
                    m_board.GetBoard().GetBoard()[rook.GetCurrentRow()][rook.GetCurrentColumn()] = new Tile(rookTile.GetColor(), rookTile.GetRow(), rookTile.GetColumn(), Utilities.DuplicatePiece(rook));                
                }else{// This is a regular or attacking move
                    if(m_victim != null){
                        move = new AttackingMove(m_candidate, m_destinationRow, m_destinationColumn, m_victim);
                    }else{
                        move = new RegularMove(m_candidate, m_destinationRow, m_destinationColumn);
                    }
                }
            }else{// This could be a regular move, an attacking move, or an en passant move
                if(IsEnPassantMove()){
                    // This is an en passant move
                    final Pawn victim;
                        if(Utilities.HasValidCoordinates(m_candidate.GetCurrentRow(), m_candidate.GetCurrentColumn() + Utilities.ONE)
                                && m_board.GetBoard().GetTile(m_candidate.GetCurrentRow(), m_candidate.GetCurrentColumn() + Utilities.ONE).IsOccupied()){
                            victim = (Pawn) m_board.GetBoard().GetTile(m_sourceRow, m_sourceColumn + Utilities.ONE).GetPiece();
                        }else{
                            victim = (Pawn) m_board.GetBoard().GetTile(m_sourceRow, m_sourceColumn - Utilities.ONE).GetPiece();
                        }
                    move = new EnPassantMove((Pawn)m_candidate, m_destinationRow, m_destinationColumn, victim);
                }else{// This isn't an en passant move
                    // This is a regular or attacking move
                    if(m_victim != null){
                        move = new AttackingMove(m_candidate, m_destinationRow, m_destinationColumn, m_victim);
                    }else{
                        move = new RegularMove(m_candidate, m_destinationRow, m_destinationColumn);
                    }
                }
            }
        }
        // Record that the piece moved
        // ?

        EvaluatePreviouslyMoved();
        
        // Return the complete move
        return move;
    }
    
    
    private final boolean HumanPlay(){
        final Delta source = Game.ParseTile(m_source);
        
        if(!this.CheckSourceTile(source)){
            return false;
        }
        
        m_sourceRow = source.GetRowDelta();
        m_sourceColumn = source.GetColumnDelta();
        
        m_candidate = m_board.GetBoard().GetTile(m_sourceRow, m_sourceColumn).GetPiece();
        
        final Delta destination = Game.ParseTile(m_destination);
        
        if(!this.CheckDestinationTile(destination)){
            return false;
        }
        
        m_destinationRow = destination.GetRowDelta();
        m_destinationColumn = destination.GetColumnDelta();
        
        m_victim = m_board.GetBoard().GetTile(m_destinationRow, m_destinationColumn).GetPiece();
        
        m_nextMove = EvaluateMove();
        
        /*
        revalidate();
        repaint();
        */
        
        return true;
    }
    
    /*
    public static GUIBoard GetStartingPosition(){
        return m_instance.new GUIBoard(Board.GetStartingPosition());
    }
    
    public static GUIBoard GetEnPassantTest(){
        return m_instance.new GUIBoard(Board.GetEnPassantTest());
    }
    
    public static GUIBoard GetStalemateTest(){
        return m_instance.new GUIBoard(Board.GetStalemateTest());
    }
    
    public static GUIBoard GetCheckmateTest(){
        return m_instance.new GUIBoard(Board.GetCheckmateTest());
    }
    */
    
    /*
    NAME
        private final void ComputerPlay(final Computer a_computer);
    
    SYNOPSIS
        private final void ComputerPlay(final Computer a_computer);
    
        Computer a_computer ----> The computer player.
    
    DESCRIPTION
        This method enables the computer player to
        make a move with the minimax algorithm.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void ComputerPlay(final Computer a_computer){
        ArrayList<Move> allPossibleMoves = a_computer.UglyMoves();
        
        // Make a stupid random AI for now
        
        // Get any old move
        Random random = new Random();
        final int move = random.nextInt(allPossibleMoves.size());
        
        // Get the moving piece
        m_nextMove = allPossibleMoves.get(move);// For the stupid AI
        //m_nextMove = a_computer.Search(Utilities.ZERO);// For a slightly better AI
        
        // Get the piece's old coordinates
        m_sourceRow = m_nextMove.GetOldRow();
        m_sourceColumn = m_nextMove.GetOldColumn();
        
        // Get the piece's new coordinates
        m_destinationRow = m_nextMove.GetNewRow();
        m_destinationColumn = m_nextMove.GetNewColumn();
        
        // Get the victim the piece is capturing, if any
        m_victim = m_nextMove.GetVictim();
        
        // Make the move
        //MakeMove();
    }
    
    /*
    private final int GetBestMove(final int a_depth, final Board a_board, final int a_value, final Move a_move){
        final ArrayList<Move> allMoves = m_currentPlayer.UglyMoves();
        int value = Utilities.ZERO;
        
        if(a_depth >= m_depth){
            return a_value;
        }
        
        for(final Move move : allMoves){
            Board clone = Board.GetDeepCopy(a_board);
            
            if(move.IsEnPassant()){
                clone = clone.EnPassant((EnPassantMove)move, m_white, m_black);
            }else if(move.IsAttacking()){
                clone = clone.Attack((AttackingMove)move, m_white, m_black);
            }else if(move.IsCastling()){
                clone = clone.Castle((CastlingMove)move);
            }else{
                clone = clone.Move((RegularMove)move);
            }
            
            final int newValue = a_value + move.GetValue();
            
            return GetBestMove(a_depth + Utilities.ONE, clone, newValue, move);

        }
        
        return Utilities.ZERO;
    }
    */
    
    /*
    NAME
        private final void TryPlay();
    
    SYNOPSIS
        private final void TryPlay();
    
        No parameters.
    
    DESCRIPTION
        This method attempts to play a game
        of chess. It has a try-catch block
        on the bottom to catch any exception
        that may occur during gameplay.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void TryPlay(){
        try{
            Play();            
        }catch(RuntimeException e){
            System.err.println("Dark Blue has quit unexpectedly.");
            e.printStackTrace();
            System.exit(Utilities.ONE);
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
        
        Object[] colors = {BLACK, WHITE};
        
        while(true){
            
            m_buttonInt = JOptionPane.showOptionDialog(null, PLAY_AS, TITLE, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, colors, null);
            
            switch(m_buttonInt){
                // For a black human player
                case Utilities.ZERO: m_humanColor = ChessColor.BLACK;
                break;
                // For a white human player
                case Utilities.ONE:  m_humanColor = ChessColor.WHITE;
                break;
                // Do not allow the player to proceed without choosing a color
                default: continue;
            }
            
            m_oppositeColor = Utilities.Reverse(m_humanColor);
            
            m_board = new GUIBoard(Board.GetStartingPosition());// Originally DarkBlue.GetStartingPosition()
            
            InitializePlayers(m_board.GetBoard());

            break;
        }
    }
    
    /*
    NAME
        private final void Play();
    
    SYNOPSIS
        private final void Play();
    
        No parameters.
    
    DESCRIPTION
        This method plays the game.
        It assigns the current player,
        evaluates the game state,
        evaluates the desired move, 
        makes the move, checks for promotions,
        and refreshes the players at the end.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void Play(){
        //int index = Utilities.ZERO;
        
        // Only continue for as long as both kings are safe or one player is in check
        //while(index < Utilities.TWO){// Beginning of while loop
            
            // Determine whose turn it is and assign the alias accordingly
            AssignCurrentPlayer();
            
            // Determine the state of the game after the newly-made move
            m_state = EvaluateGameState();
            
            // Check to see if either player is in check, stalemate,
            // or checkmate and handle those situations appropriately
            if(m_state == GameState.CHECK && m_currentPlayer.IsHuman()){
                JOptionPane.showMessageDialog(this, CHECK_MESSAGE, TITLE, JOptionPane.WARNING_MESSAGE);
                // Just display this message and do not return; the game is still playable
            }else if(m_state == GameState.STALEMATE){
                JOptionPane.showMessageDialog(this, STALEMATE_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }else if(m_state == GameState.DRAW && IsDrawByFiftyMoveRule()){
                JOptionPane.showMessageDialog(this, FIFTY_MOVE_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }else if(m_state == GameState.DRAW && IsDrawByInsufficientMaterial()){
                JOptionPane.showMessageDialog(this, INSUFFICIENT_MATERIAL_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }else if(m_state == GameState.DRAW && IsDrawByThreefoldRepetition()){
                JOptionPane.showMessageDialog(this, THREEFOLD_REPETITION_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }else if(m_state == GameState.CHECKMATE && m_currentPlayer.IsWhite()){
                JOptionPane.showMessageDialog(this, BLACK_CHECKMATE_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }else if(m_state == GameState.CHECKMATE && m_currentPlayer.IsBlack()){
                JOptionPane.showMessageDialog(this, WHITE_CHECKMATE_MESSAGE, TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            RefreshPlayers();
            
            if(m_board.WhoseTurnIsIt().IsWhite()){
                CheckForPromotions(m_black);
            }else{
                CheckForPromotions(m_white);
            }
        //}
            
        if(m_currentPlayer.IsComputer()){
            ComputerPlay((Computer)m_currentPlayer);
            m_moveHistory.add(m_nextMove);
            m_boardHistory.add(Board.GetDeepCopy(m_board.GetBoard()));
            revalidate();
            repaint();
        }
            /*
            // Initialize the move
            m_nextMove = EvaluateMove();
            
            // Reset the move count if a pawn gets moved or a piece gets captured
            if(m_candidate.IsPawn() || m_victim != null){
                m_moves = Utilities.ZERO;
            }else{
                m_moves++;
            }
        
            // Make the move
            MakeMove();
            
            // Take note of the previously moved piece
            EvaluatePreviouslyMoved();
            
            // Check for promotions on the side that's moving next
            if(m_board.WhoseTurnIsIt() == ChessColor.WHITE){
                CheckForPromotions(m_black);
            }else{
                CheckForPromotions(m_white);
            }
                    
            // Refresh pieces and legal moves for both sides
            m_board.GetBoard().RefreshPlayers();
            
            // Add the newly-made move to the game history
            m_boardHistory.add(Board.GetDeepCopy(m_board.GetBoard()));    
            */
        // End of while true game loop
    }
    /*
    private final void InitializePlayers(final Board a_board, final Player a_white, final Player a_black){
        if(DarkBlue.GetHumanColor().IsWhite()){// Timeout exception?
            m_white = new Human(a_white, a_board);
            m_black = new Computer(a_black, a_board);
        }else{
            m_white = new Computer(a_white, a_board);
            m_black = new Human(a_black, a_board);
        }
        RefreshPlayers();
    }
    */
    private final void InitializePlayers(final Board a_board){
        if(DarkBlue.GetHumanColor().IsWhite()){// Timeout exception?
            m_white = new Human(ChessColor.WHITE, a_board);
            m_black = new Human(ChessColor.BLACK, a_board);// Originally a computer
            m_computerPlayer = m_black;
        }else{
            m_white = new Human(ChessColor.WHITE, a_board);// Originally a computer
            m_black = new Human(ChessColor.BLACK, a_board);
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
    
    public static final int GetOriginalRow(){
        return m_originalRow;
    }
    
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
    
    public final void Test(){
        m_keyboard = new Scanner(System.in);
        
        ChooseColor();
        // Only continue for as long as both kings are safe or one player is in check
        while(true){// Beginning of while loop
                    
            // Determine whose turn it is and assign the alias accordingly
            AssignCurrentPlayer();
            
            System.out.println();
            
            this.m_board.GetBoard().PrintBoard();
                    
            // Determine the state of the game after the newly-made move
            m_state = EvaluateGameState();
                    
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
                TestPlay();                    
            }else{
                System.out.println("Thinking...");
                ComputerPlay((Computer)m_computerPlayer);
            }
            
            RefreshPlayers();
                    
        }// End of while true game loop
    }
    
    /*
    NAME
        public final void CheckSourceTile();
    
    SYNOPSIS
        public final void CheckSourceTile();
    
        No parameters.
    
    DESCRIPTION
        This method determines if the source coordinates entered in by the user
        are valid. If they are, they will be officially assigned to the variables
        and the method will return true.
        If not, they will not be assigned and the method will return false.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    private final void CheckSourceTile(){
        do{
            System.out.print(DarkBlue.SOURCE + ": ");
            String tile = m_keyboard.nextLine();
            m_sourceRow = Utilities.ToBoardRow(tile);
            m_sourceColumn = Utilities.ToBoardColumn(tile);
        try{
            if(!Utilities.HasValidCoordinates(m_sourceRow, m_sourceColumn)){
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
        }while(!Utilities.HasValidCoordinates(m_sourceRow, m_sourceColumn));
    }
    
    /*
    NAME
        public final boolean CheckDestinationTile(final Delta a_destination);
    
    SYNOPSIS
        public final boolean CheckDestinationTile(final Delta a_destination);
    
        Delta a_destination --------> The pair of integers that represent the destination tile.
    
    DESCRIPTION
        This method determines if the destination coordinates entered in by the user
        are valid. If they are, they will be officially assigned to the variables
        and the method will return true.
        If not, they will not be assigned and the method will return false.
    
    RETURNS
        True if the coordinates are valid, and false otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
    public final void CheckDestinationTile(){
        do{
            try{
                System.out.print(DarkBlue.DESTINATION + ": ");
                final String tile = m_keyboard.nextLine();
                m_destinationRow = Utilities.ToBoardRow(tile);
                m_destinationColumn = Utilities.ToBoardColumn(tile);
                if(!Utilities.HasValidCoordinates(m_destinationRow, m_destinationColumn)){
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
    
    public final void TestPlay(){
        CheckSourceTile();
        CheckDestinationTile();
        
        m_originalRow = m_sourceRow;
        m_originalColumn = m_sourceColumn;
        
        m_nextMove = EvaluateMove();
        
        MakeMove();
    }
    
    /*
    public static void main(String[] args){
        Board board = Board.GetStartingPosition();
        //Player currentPlayer;
        Human white = new Human(ChessColor.WHITE, board);
        Human black = new Human(ChessColor.BLACK, board);
        
        //currentPlayer = white;
        
        white.Refresh(board);
        black.Refresh(board);
        
        // Cloning test
        Board clone = Board.GetDeepCopy(board);
        
        Pawn kp = (Pawn) clone.GetTile(6, 4).GetPiece();
        
        // Sample move: 1. e4
        Move candidate = new RegularMove(kp, 4, 4);
        
        clone = clone.Move((RegularMove)candidate);
        
        System.out.println("Original board:\n" + board.GetWhiteBoard());
        
        System.out.println("Cloned board with 1. e4:\n" + clone.GetWhiteBoard());
        
        
        // Castling test
        //currentPlayer = m_white;
        
        // Attempt to perform a castle for white
        //CastlingMove whiteQueen = new CastlingMove(m_white.GetKing(), m_white.GetKing().GetCurrentRow(), m_white.GetKing().GetCurrentColumn() - 2, m_board.GetBoard());
        //m_board.Castle(whiteQueen);
        
        //m_currentPlayer = m_black;
        
        
        // Attempt to perform a castle for black
        //CastlingMove blackKing = new CastlingMove(m_black.GetKing(), m_black.GetKing().GetCurrentRow(), m_black.GetKing().GetCurrentColumn() + 2, m_board.GetBoard());
        //m_board.Castle(blackKing);
        
    }
    */
    
    private final class GUITile extends JPanel implements MouseListener{

        // Final values for bookkeeping
        private static final long serialVersionUID = Utilities.ONE_LONG;
        
        public static final String EXTENSION = ".png";
        //public static final String FOLDER = "https://github.com/ryanking0720/DarkBlue/tree/master/DarkBlue/src/com/DarkBlue/GUI/ChessPieces/";
        public static final String FOLDER = "GUI/ChessPieces/";
        
        public final String m_actionCommand;
        
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
                this.m_originalColor = Utilities.WHITE;
            }else{
                this.m_originalColor = Utilities.BLACK;
            }
            
            this.setSize(new Dimension(Utilities.SIXTY, Utilities.SIXTY));
            this.setBackground(this.m_originalColor);
            
            m_actionCommand = Utilities.ToAlgebraic(this.m_tile.GetRow(), this.m_tile.GetColumn());

            addMouseListener(this);
            
            DrawTile();
        }
        
        public final void SetPiece(){
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
        
        public final void DrawTile(){
            SetPiece();
            
            revalidate();
            repaint();
        }
        
        public final void LightUp(){
            this.setBackground(Utilities.SELECTED_GREEN);
            revalidate();
            repaint();
        }
        
        public final void Revert(){
            this.setBackground(this.m_originalColor);
            revalidate();
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
            
            final Runnable r = new Runnable(){
                public void run(){
                    Respond(a_event);
                }
            };
            
            new Thread(r).start();
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    m_board.DrawBoard();
                }
            });
        }
        
        // Responds to mouseevents
        public final void Respond(final MouseEvent a_event){
            if(SwingUtilities.isLeftMouseButton(a_event)){
                
                final String eventSource = this.toString();
                
                if(m_sourceTile == null){
                    m_sourceRow = Utilities.ToBoardRow(eventSource);
                    m_sourceColumn = Utilities.ToBoardColumn(eventSource);
                    m_sourceTile = m_board.GetBoard().GetTile(m_sourceRow, m_sourceColumn);
                    m_candidate = m_sourceTile.GetPiece();
                    if(m_sourceTile.IsOccupied() && m_candidate.GetColor().IsAlly(m_humanColor) && m_candidate.CanMove()){
                        m_shouldHighlightLegalMoves = true;
                    }else{
                        m_sourceTile = null;
                        m_sourceRow = Utilities.NEGATIVE_ONE;
                        m_sourceColumn = Utilities.NEGATIVE_ONE;
                        m_candidate = null;
                    }
                }else{
                    m_destinationRow = Utilities.ToBoardRow(eventSource);
                    m_destinationColumn = Utilities.ToBoardColumn(eventSource);
                    
                    if(m_sourceTile.GetRow() == m_destinationRow && m_sourceTile.GetColumn() == m_destinationColumn){
                        m_shouldHighlightLegalMoves = false;
                        m_sourceTile = null;
                        m_sourceRow = Utilities.NEGATIVE_ONE;
                        m_sourceColumn = Utilities.NEGATIVE_ONE;
                        m_candidate = null;
                    }else{
                        m_destinationTile = m_board.GetBoard().GetTile(m_destinationRow, m_destinationColumn);
                        
                        if(Utilities.IsLegal(m_candidate, m_destinationRow, m_destinationColumn)){
                            m_victim = m_destinationTile.GetPiece();
                            m_shouldHighlightLegalMoves = false;
                        
                            m_nextMove = EvaluateMove();
                        
                            MakeMove();
                        
                            m_nextMove = null;
                            m_sourceTile = null;
                            m_sourceRow = Utilities.NEGATIVE_ONE;
                            m_sourceColumn = Utilities.NEGATIVE_ONE;
                            m_candidate = null;
                        }
                        
                        m_destinationTile = null;
                        m_destinationRow = Utilities.NEGATIVE_ONE;
                        m_destinationColumn = Utilities.NEGATIVE_ONE;
                        m_victim = null;
                    }
                }
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
    
    private final class GUIBoard extends JPanel{
        
        private static final long serialVersionUID = Utilities.ONE_LONG;
        
        private final GUITile[][] m_tiles;
        private final Board m_board;

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
            
            DrawBoard();
        }
        
        public final void DrawBoard(){
            
            PurgeBoard();

            if(DarkBlue.GetHumanColor().IsWhite()){
                BuildWhiteBoard();
            }else{
                BuildBlackBoard();
            }
            
            if(DarkBlue.ShouldHighlightLegalMoves()){
                HighlightLegalMoves();
            }else{
                UndoHighlighting();
            }
            
            revalidate();
            repaint();
        }
        
        /*
        NAME
            private final void PurgeBoard();
        
        SYNOPSIS
            private final void PurgeBoard();
        
            No parameters.
        
        DESCRIPTION
            This method clears the GUIBoard by
            setting all of its tiles to null
            and then removing everything.
        
        RETURNS
            Nothing
        
        AUTHOR
            Ryan King
        */
        private void PurgeBoard(){
            for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){
                int row = index / Utilities.EIGHT;
                int column = index % Utilities.EIGHT;
                this.m_tiles[row][column] = null;
            }
            this.removeAll();
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
        /*
        @Override
        protected final void paintComponent(final Graphics a_g){
            super.paintComponent(a_g);
            for(int index = Utilities.ZERO; index < Utilities.SIXTY_FOUR; index++){
                int row = index / Utilities.EIGHT;
                int column = index % Utilities.EIGHT;
                m_tiles[row][column].paintComponent(a_g);
            }
        }
        */
        
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
        
        public final GUITile GetTile(final int a_row, final int a_column){
            if(Utilities.HasValidCoordinates(a_row, a_column)){
                return m_tiles[a_row][a_column];
            }else{
                return null;
            }
        }
    }
}