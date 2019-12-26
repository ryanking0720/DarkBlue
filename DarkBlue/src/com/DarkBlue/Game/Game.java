package com.DarkBlue.Game;

import com.DarkBlue.Board.*;
import com.DarkBlue.Player.*;
import com.DarkBlue.Utilities.*;
import com.DarkBlue.Move.*;

import javax.swing.JTextField;

public interface Game{
    
    /* ------------------------------------------------------------------
                                Game state evaluation
    ------------------------------------------------------------------ */
    
    /*
    NAME
        public static boolean IsGameOver();
    
    SYNOPSIS
        public static boolean IsGameOver();
    
        No parameters.
    
    DESCRIPTION
        This method determines if the game is over
        by checking to see if white or black is in
        stalemate or checkmate, or if there is 
        insufficient material.
    
    RETURNS
        True if the game is over, and false otherwise.
    
    AUTHOR
        Ryan King
    */
    public static boolean IsGameOver(final Board a_board, final Player a_white, final Player a_black){
        return ((!a_white.IsInCheckmate(a_board) && !a_black.IsInCheckmate(a_board))
                && (!a_white.IsInStalemate(a_board) && !a_black.IsInStalemate(a_board)));
    }
    
    /* ------------------------------------------------------------------
                                Move legality evaluation
    ------------------------------------------------------------------ */
    
    /* ------------------------------------------------------------------
                            Special case evaluation
    ------------------------------------------------------------------ */
        
    /* ------------------------------------------------------------------
                                Initialization utilities
    ------------------------------------------------------------------ */
    
    /* ------------------------------------------------------------------
                            Human and computer modes of play
    ------------------------------------------------------------------ */
    
    /*
    NAME
        public static void ComputerPlay(final Computer a_computer);
    
    SYNOPSIS
        public static void ComputerPlay(final Computer a_computer);
        
        Computer a_computer ---------> The computer player to move.
    
    DESCRIPTION
        This method computes the next move for the computer to make.
        Just like the HumanPlay() method, it assigns all the Game 
        static variables to make the move it selects.
        As of June 3, 2019, it simply selects a random legal move from
        its ArrayList of moves and makes it; a stupid AI that provides no challenge.
        I will improve it with deeper searching and alpha-beta pruning as time goes by.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    /*
    public static void ComputerPlay(final Computer a_computer){
        ArrayList<Move> allPossibleMoves = a_computer.UglyMoves();
        
        // Make a stupid random AI for now
        
        // Get any old move
        Random random = new Random();
        int move = random.nextInt(allPossibleMoves.size());
        
        // Get the moving piece
        m_nextMove = allPossibleMoves.get(move);// For the stupid AI
        //m_nextMove = a_computer.Search(Utilities.ZERO);// For a slightly better AI
        
        // Get the piece's old coordinates
        m_startRow = m_nextMove.GetOldRow();
        m_startColumn = m_nextMove.GetOldColumn();
        
        // Get the piece's new coordinates
        m_destinationRow = m_nextMove.GetNewRow();
        m_destinationColumn = m_nextMove.GetNewColumn();
        
        // Get the victim the piece is capturing, if any
        m_victim = m_nextMove.GetVictim();
    }
    */
    
    /* ------------------------------------------------------------------
          King safety evaluation in all 8 directions and special cases
    ------------------------------------------------------------------ */

    
    /* ------------------------------------------------------------------
                        User input and validation
    ------------------------------------------------------------------ */
    
    /* ------------------------------------------------------------------
                            Move validation and generation
    ------------------------------------------------------------------ */
    
    public static Delta ParseTile(final JTextField a_textField){
        
        // Initialize the variables we'll need
        final String text = a_textField.getText();
        final int row, column;
        
        // Do not proceed if the tile is invalid
        // Return the error delta in this case
        if(!Utilities.IsValidTile(text)){
            return new Delta();
        }
        
        // Parse the row and column into variables
        row = Utilities.ToBoardRow(text);
        column = Utilities.ToBoardColumn(text);
                
        // Return a delta object to signify the new pair of integers
        return new Delta(row, column);
    }
    
    // Test method
}