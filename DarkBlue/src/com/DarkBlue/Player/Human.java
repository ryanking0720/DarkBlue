package com.DarkBlue.Player;

import com.DarkBlue.Utilities.*;
import com.DarkBlue.Board.*;

public final class Human extends Player{

    /**/
    /*
    NAME
        public Human(final ChessColor a_color, final Board a_board);
    
    SYNOPSIS
        public Human(final ChessColor a_color, final Board a_board);
    
        ChessColor a_color -----> The player's color.
        
        Board a_board ----------> The current state of the board.
    
    DESCRIPTION
        This constructor instantiates a new Human object.
        The type is automatically set the PlayerType.HUMAN.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Human(final ChessColor a_color, final Board a_board){
        super(a_color, a_board, PlayerType.HUMAN);
    }
    
    /**/
    /*
    NAME
        public Human(final Player a_player, final Board a_board);
    
    SYNOPSIS
        public Human(final Player a_player, final Board a_board);
    
        Player a_player --------> The player to be copied.
        
        Board a_board ----------> The current state of the board.
    
    DESCRIPTION
        This constructor instantiates a new Human object.
        This is meant to be the copy constructor.
        The type is automatically set the PlayerType.HUMAN.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Human(final Player a_player, final Board a_board){
        super(a_player, a_board);
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
        boolean: Always returns true.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsHuman(){
        return true;
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
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsComputer(){
        return false;
    }
    
    @Override
    public final PlayerType GetPlayerType(){
    	return PlayerType.HUMAN;
    }
}