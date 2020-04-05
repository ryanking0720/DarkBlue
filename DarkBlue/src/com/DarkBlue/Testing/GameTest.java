package com.DarkBlue.Testing;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.junit.Test;

import com.DarkBlue.Board.*;
import com.DarkBlue.Game.*;
import com.DarkBlue.GUI.*;
import com.DarkBlue.Move.*;
import com.DarkBlue.Piece.*;
import com.DarkBlue.Player.*;
import com.DarkBlue.Utilities.*;

/*
 * This contains a stripped-down version of the chess game.
 * 
 * It does not include support for threefold repetition, en passant, castling, or promotions, but
 * allows the human to play a game of chess against a computer with a random AI
 * on the command line.
 */
public class GameTest{
    
    private static Board m_board;
    private static Player m_white;
    private static Player m_black;
    private static Player m_currentPlayer;
    private static Player m_computerPlayer;
    
    // The exact coordinates the player chooses when moving
    private static int m_sourceRow;
    private static int m_sourceColumn;
    private static int m_destinationRow;
    private static int m_destinationColumn;
    
    private static Scanner m_keyboard;

    // The number of moves made; useful for determining draws
    private static int m_moves = Utilities.ZERO;
      
    // The integer that keeps track of the button
    private static int m_buttonInt;
            
    // The piece to be moved
    private static Piece m_candidate;
            
    // The victim of the move, if any
    private static Piece m_victim;
            
    // The piece that was moved previously. Useful in determining en passant for pawns
    private static Piece m_previouslyMoved;
            
    // The move to be made once evaluated
    private static Move m_nextMove;
    
    private static GameState m_state;
    
    private static ChessColor m_humanColor, m_computerColor;
    
    /**/
    /*
    NAME
        public final void TransitionBoardTest();
    
    SYNOPSIS
        public final void TransitionBoardTest();
    
        No parameters.
    
    DESCRIPTION
        This method tests the GetTransitionalBoard() method in the Move class.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public final void TransitionBoardTest(){
        Board sample = Board.GetStartingPosition();
        Move e4 = new RegularMove(sample.GetTile(6, 4).GetPiece(), 4, 4, sample);
        System.out.println("Original board:\n" + sample.toString());
        Board moveMade = e4.GetTransitionalBoard();
        System.out.println("Original board after e4:\n" + sample.toString());
        System.out.println("Copy board:\n" + moveMade.toString());
    }
    
    /**/
    /*
    NAME
        public final void CheckSourceTile();
    
    SYNOPSIS
        public final void CheckSourceTile();
    
        No parameters.
    
    DESCRIPTION
        This method determines if the source coordinates entered in by the user
        are valid. If they are, they will be officially assigned to the variables.
        If not, they will not be assigned and the method will continue to repeat until
        the user gives valid values.
    
    RETURNS
        Nothing
        
    AUTHOR
        Ryan King
    */
    public static final void CheckSourceTile(){
        do{
            System.out.print("Source Tile: ");
            String tile = m_keyboard.nextLine();
            m_sourceRow = BoardUtilities.ToBoardRow(tile);
            m_sourceColumn = BoardUtilities.ToBoardColumn(tile);
        try{
            if(!BoardUtilities.HasValidCoordinates(m_sourceRow, m_sourceColumn)){
                JOptionPane.showMessageDialog(null, "Invalid tile", DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
            }else{
            
                Piece mover = m_board.GetTile(m_sourceRow, m_sourceColumn).GetPiece();
            
                if(m_board.GetTile(m_sourceRow, m_sourceColumn).IsEmpty()){
                    JOptionPane.showMessageDialog(null, "That tile is empty", DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                }else if(mover.GetColor().IsEnemy(m_currentPlayer.GetColor())){
                    JOptionPane.showMessageDialog(null, "That piece is not your color", DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                }else if(mover.GetColor().IsAlly(m_currentPlayer.GetColor()) && !mover.CanMove()){
                    JOptionPane.showMessageDialog(null, "That piece has no legal moves", DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                }
                
                m_candidate = mover;
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Invalid tile", DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
        }
        }while(!BoardUtilities.HasValidCoordinates(m_sourceRow, m_sourceColumn));
    }
    
    /**/
    /*
    NAME
        public final void CheckDestinationTile();
    
    SYNOPSIS
        public final void CheckDestinationTile();
    
        No parameters.
    
    DESCRIPTION
        This method determines if the destination coordinates entered in by the user
        are valid. If they are, they will be officially assigned to the variables.
        If not, they will not be assigned and the method will continue to repeat until
        the user gives valid values.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public static final void CheckDestinationTile(){
        do{
            System.out.print("Destination Tile: ");
            String tile = m_keyboard.nextLine();
            m_destinationRow = BoardUtilities.ToBoardRow(tile);
            m_destinationColumn = BoardUtilities.ToBoardColumn(tile);
            
            try{
                if(!BoardUtilities.HasValidCoordinates(m_destinationRow, m_destinationColumn)){
                    JOptionPane.showMessageDialog(null, "Invalid tile", DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                }else{
                
                    Piece mover = m_board.GetTile(m_sourceRow, m_sourceColumn).GetPiece();
                    Piece victim = m_board.GetTile(m_destinationRow, m_destinationColumn).GetPiece();
            
                    if(!Utilities.IsLegal(mover, m_destinationRow, m_destinationColumn)){
                        JOptionPane.showMessageDialog(null, "That move is illegal", DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                    }else if(victim != null && victim.IsAlly(mover)){
                        JOptionPane.showMessageDialog(null, "You cannot capture a piece of your own color", DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                    }else if(victim != null && victim.IsEnemy(mover) && victim.IsKing()){
                        JOptionPane.showMessageDialog(null, "You cannot capture the opponent\'s king", DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                    
                    m_victim = victim;
                }
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Invalid tile", DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }while(!BoardUtilities.HasValidCoordinates(m_destinationRow, m_destinationColumn));
    }
    
    /**/
    /*
    NAME
        public static final GameState EvaluateGameState();
    
    SYNOPSIS
        public static final GameState EvaluateGameState();
        
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
        
        GameState.INSUFFICIENT_MATERIAL: The game can end with no winner due to a number of conditions:
        
            1. White and black have only bare kings left on the board.
        
            2. One player has a king and the other has a king and a bishop.
        
            3. One player has a king and the other has a king and a knight.
        
            4. Both players have a king and a bishop and both bishops 
               move on the same tile color.
                
        GameState.FIFTY_MOVE_RULE: Fifty halfmoves have been made with no capture or pawn movement.
        
        GameState.THREEFOLD_REPETITION: The same configuration of the board has been repeated three times.
        This need not be consecutive.

        GameState.NORMAL: The game proceeds as normal.
    
    RETURNS
        One of the GameState variables of the given types,
        depending on the situation encountered.
    
    AUTHOR
        Ryan King
    */
    public static final GameState EvaluateGameState(){
        if(m_currentPlayer.IsInCheckmate(m_board)){
            return GameState.CHECKMATE;
        }else if(m_currentPlayer.IsInCheck(m_board)){
            return GameState.CHECK;
        }else if(m_currentPlayer.IsInStalemate(m_board)){
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
    private static final boolean IsDrawByInsufficientMaterial(){
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
            // Initialize the white and black bishop coordinates
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
            // See if the bishops are on the same tile colors
            if(m_board.GetTile(whiteBishopRow, whiteBishopColumn).GetColor() == m_board.GetTile(blackBishopRow, blackBishopColumn).GetColor()){
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
    private static final boolean IsDrawByFiftyMoveRule(){
        return m_moves >= Utilities.FIFTY;
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
    private static final boolean IsDrawByThreefoldRepetition(){
        return false;
    }
    
    /**/
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
    private static final boolean IsEnPassantMove(){
        return m_victim != null
                    && m_victim.IsPawn() 
                    && m_victim.Equals(m_previouslyMoved) 
                    && (m_victim.GetCurrentRow() == m_destinationRow - Utilities.ONE
                            || m_victim.GetCurrentRow() == m_destinationRow + Utilities.ONE)
                    && m_victim.GetCurrentColumn() == m_destinationColumn;
    }
    
    /**/
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
    private static final boolean IsCastlingMove(){
        return m_destinationRow == m_sourceRow 
                && (m_destinationColumn == m_sourceColumn + Utilities.TWO 
                || m_destinationColumn == m_sourceColumn - Utilities.TWO);
    }
    
    
    private static final void InitializePlayers(final Board a_board){
        if(m_humanColor.IsWhite()){
            m_white = new Human(ChessColor.WHITE, a_board);
            m_black = new Computer(ChessColor.BLACK, a_board);
            m_computerPlayer = m_black;
        }else{
            m_white = new Computer(ChessColor.WHITE, a_board);
            m_black = new Human(ChessColor.BLACK, a_board);
            m_computerPlayer = m_white;
        }
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
    public static final void RefreshPlayers(){
        m_white.Refresh(m_board);
        m_black.Refresh(m_board);
    }

    /**/
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
    private static final void ComputerPlay(final Computer a_computer){
        ArrayList<Move> allPossibleMoves = a_computer.UglyMoves();
        
        // Make a stupid random AI for now
        
        // Get any old move
        Random random = new Random();
        int move = random.nextInt(allPossibleMoves.size());
        
        // Get the moving piece
        m_nextMove = allPossibleMoves.get(move);// For the stupid AI

        // Get the piece's old coordinates
        m_sourceRow = m_nextMove.GetOldRow();
        m_sourceColumn = m_nextMove.GetOldColumn();
        
        // Get the piece's new coordinates
        m_destinationRow = m_nextMove.GetNewRow();
        m_destinationColumn = m_nextMove.GetNewColumn();
        
        // Get the victim the piece is capturing, if any
        m_victim = m_nextMove.GetVictim();
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
    private static final void ChooseColor(){
        
        Object[] colors = {DarkBlue.WHITE, DarkBlue.BLACK};
        
        while(true){
            
            m_buttonInt = JOptionPane.showOptionDialog(null, DarkBlue.PLAY_AS, DarkBlue.TITLE, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, colors, null);
            
            switch(m_buttonInt){
                // For a black human player
                case Utilities.ZERO: m_humanColor = ChessColor.WHITE;
                break;
                // For a white human player
                case Utilities.ONE:  m_humanColor = ChessColor.BLACK;
                break;
                // Do not allow the player to proceed without choosing a color
                default: continue;
            }
            m_board = Board.GetStartingPosition();
            
            m_computerColor = BoardUtilities.Reverse(m_humanColor);
            
            InitializePlayers(m_board);

            break;
        }
    }   
    
    @Test
    public final void Play(){
        m_keyboard = new Scanner(System.in);
        
        ChooseColor();
        // Only continue for as long as both kings are safe or one player is in check
        while(true){// Beginning of while loop
                    
            // Determine whose turn it is and assign the alias accordingly
            if(m_board.WhoseTurnIsIt().IsWhite()){
                m_currentPlayer = m_white;
            }else{
                m_currentPlayer = m_black;
            }

            if(m_humanColor.IsWhite()){
                System.out.println(m_board.GetWhiteBoard());
            }else{
                System.out.println(m_board.GetBlackBoard());
            }
            
            RefreshPlayers();
       
            // Determine the state of the game after the newly-made move
            m_state = EvaluateGameState();
                    
            // Check to see if either player is in check, stalemate,
            // or checkmate and handle those situations appropriately
            if(m_state == GameState.CHECK && m_currentPlayer.IsHuman()){
                JOptionPane.showMessageDialog(null, DarkBlue.CHECK_MESSAGE, DarkBlue.TITLE, JOptionPane.WARNING_MESSAGE);
                // Just display this message and do not return; the game is still playable
            }else if(m_state == GameState.STALEMATE){
                JOptionPane.showMessageDialog(null, DarkBlue.STALEMATE_MESSAGE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }else if(m_state == GameState.FIFTY_MOVE_RULE){
                JOptionPane.showMessageDialog(null, DarkBlue.FIFTY_MOVE_MESSAGE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }else if(m_state == GameState.INSUFFICIENT_MATERIAL){
                JOptionPane.showMessageDialog(null, DarkBlue.INSUFFICIENT_MATERIAL_MESSAGE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }else if(m_state == GameState.THREEFOLD_REPETITION){
                JOptionPane.showMessageDialog(null, DarkBlue.THREEFOLD_REPETITION_MESSAGE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }else if(m_state == GameState.CHECKMATE && m_currentPlayer.IsWhite()){
                JOptionPane.showMessageDialog(null, DarkBlue.BLACK_CHECKMATE_MESSAGE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }else if(m_state == GameState.CHECKMATE && m_currentPlayer.IsBlack()){
                JOptionPane.showMessageDialog(null, DarkBlue.WHITE_CHECKMATE_MESSAGE, DarkBlue.TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }
                    
            System.out.println("It\'s " + (m_board.WhoseTurnIsIt().IsWhite() ? "white" : "black") + "\'s turn.");
                    
            if(m_currentPlayer.IsComputer()){
                System.out.println("Thinking...");
                ComputerPlay((Computer)m_computerPlayer);
            }else{
                CheckSourceTile();
                CheckDestinationTile();
                m_nextMove = Factory.MoveFactory(m_candidate, m_destinationRow, m_destinationColumn, m_victim, m_board);
            } 
            
            m_previouslyMoved = Factory.PieceFactory(m_candidate);
            m_board = m_nextMove.GetTransitionalBoard();

           // RefreshPlayers();
        }// End of while true game loop    
    }  
}