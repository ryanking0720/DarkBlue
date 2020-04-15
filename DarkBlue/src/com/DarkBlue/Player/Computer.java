package com.DarkBlue.Player;

import java.util.ArrayList;

import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Board.Board;
import com.DarkBlue.Move.Move;
import com.DarkBlue.GUI.DarkBlue;

/**
 * This class represents a computer player. The computer player selects its move
 * by using a Minimax algorithm with alpha-beta pruning, which are provided in their
 * own interface.
 */
public final class Computer extends Player{

    /**/
    /*
    NAME
        public Computer(final ChessColor a_color, final Board a_board);
    
    SYNOPSIS
        public Computer(final ChessColor a_color, final Board a_board);
    
        ChessColor a_color -----> The player's color.
        
        Board a_board ----------> The current state of the board.
    
    DESCRIPTION
        This constructor instantiates a new Computer object.
        The type is automatically set the PlayerType.COMPUTER.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Computer(final ChessColor a_color, final Board a_board){
        super(a_color, a_board);
    }
    
    /**/
    /*
    NAME
        public Computer(final Player a_player, final Board a_board);
    
    SYNOPSIS
        public Computer(final Player a_player, final Board a_board);
    
        Player a_player --------> The player to be copied.
        
        Board a_board ----------> The current state of the board.
    
    DESCRIPTION
        This constructor instantiates a new Computer object.
        This is meant to be the copy constructor.
        The type is automatically set the PlayerType.COMPUTER.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Computer(final Player a_player, final Board a_board){
        super(a_player.GetColor(), a_board);
    }
    
    /**/
    /*
    NAME
        public final boolean IsHuman();
    
    SYNOPSIS
        public final boolean IsHuman();
    
        No parameters.
    
    DESCRIPTION
        This method returns if this player's type is human.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsHuman(){
        return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsComputer();
    
    SYNOPSIS
        public final boolean IsComputer();
    
        No parameters.
    
    DESCRIPTION
        This method returns if this player's type is computer.
    
    RETURNS
        boolean: Always returns true.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsComputer(){
        return true;
    }
    
    /**/
    /*
    NAME
        public final PlayerType GetPlayerType();
    
    SYNOPSIS
        public final PlayerType GetPlayerType();
    
        No parameters.
    
    DESCRIPTION
        This method returns this player's type.
    
    RETURNS
        Always returns PlayerType.COMPUTER.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final PlayerType GetPlayerType(){
    	return PlayerType.COMPUTER;
    }
    
    /**/
    /*
    NAME
        public final String toString();
    
    SYNOPSIS
        public final String toString();
    
        No parameters.
    
    DESCRIPTION
        This method returns the player's color and type.
    
    RETURNS
        String: A string description of the player.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final String toString(){
        return this.GetColor().toString().toLowerCase() + " computer player";
    }
}